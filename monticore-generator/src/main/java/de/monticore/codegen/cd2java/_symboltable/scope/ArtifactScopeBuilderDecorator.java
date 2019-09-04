package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class ArtifactScopeBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected BuilderDecorator builderDecorator;

  protected final SymbolTableService symbolTableService;

  protected final AccessorDecorator accessorDecorator;

  public ArtifactScopeBuilderDecorator(final GlobalExtensionManagement glex,
                                       final SymbolTableService symbolTableService,
                                       final BuilderDecorator builderDecorator,
                                       final AccessorDecorator accessorDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.builderDecorator = builderDecorator;
    this.accessorDecorator = accessorDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass scopeClass) {
    ASTCDClass decoratedScopClass = scopeClass.deepClone();
    String scopeBuilderName = scopeClass.getName() + BUILDER_SUFFIX;

    decoratedScopClass.getCDMethodList().clear();

    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass scopeBuilder = builderDecorator.decorate(decoratedScopClass);
    builderDecorator.setPrintBuildMethodTemplate(true);

    scopeBuilder.getCDAttributeList().forEach(a -> a.setModifier(PROTECTED.build()));
    scopeBuilder.setName(scopeBuilderName);

    // new build method template
    Optional<ASTCDMethod> buildMethod = scopeBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    buildMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new TemplateHookPoint("_symboltable.artifactscope.Build", scopeClass.getName())));

    BuilderMutatorMethodDecorator builderMutatorMethodDecorator = new BuilderMutatorMethodDecorator(glex,
        getCDTypeFacade().createQualifiedType(scopeBuilderName));

    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute();
    List<ASTCDMethod> enclosingScopeMethods = builderMutatorMethodDecorator.decorate(enclosingScopeAttribute);
    enclosingScopeMethods.addAll(accessorDecorator.decorate(enclosingScopeAttribute));

    scopeBuilder.addCDAttribute(enclosingScopeAttribute);
    scopeBuilder.addAllCDMethods(enclosingScopeMethods);

    return scopeBuilder;
  }


  protected ASTCDAttribute createEnclosingScopeAttribute() {
    ASTCDAttribute enclosingScope = this.getCDAttributeFacade().createAttribute(PROTECTED,
        getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeInterfaceType()), "enclosingScope");
    this.replaceTemplate(VALUE, enclosingScope, new StringHookPoint("= Optional.empty()"));
    return enclosingScope;
  }

}