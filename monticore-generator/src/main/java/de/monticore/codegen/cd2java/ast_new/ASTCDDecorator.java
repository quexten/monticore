package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java.mill.MillDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;

public class ASTCDDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  private final ASTFullDecorator astFullDecorator;

  private final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator;

  private final ASTBuilderDecorator astBuilderDecorator;

  private final NodeFactoryDecorator nodeFactoryDecorator;

  private final MillDecorator millDecorator;

  public ASTCDDecorator(final GlobalExtensionManagement glex,
      final ASTFullDecorator astFullDecorator, final ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator,
      final ASTBuilderDecorator astBuilderDecorator,
      final NodeFactoryDecorator nodeFactoryDecorator, final MillDecorator millDecorator) {
    super(glex);
    this.astFullDecorator = astFullDecorator;
    this.astLanguageInterfaceDecorator = astLanguageInterfaceDecorator;
    this.astBuilderDecorator = astBuilderDecorator;
    this.nodeFactoryDecorator = nodeFactoryDecorator;
    this.millDecorator = millDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit ast) {
    List<String> astPackage = new ArrayList<>(ast.getPackageList());
    astPackage.addAll(Arrays.asList(ast.getCDDefinition().getName().toLowerCase(), ASTConstants.AST_PACKAGE));

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(ast.getCDDefinition().getName())
        .addAllCDClasss(createASTClasses(ast))
        .addCDInterface(createASTInterface(ast))
        .addAllCDClasss(createASTBuilderClasses(ast))
        .addCDClass(createNodeFactoryClass(ast))
        .addCDClass(createMillClass(ast))
        .build();

    for (ASTCDClass cdClass : astCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(astPackage));
    }

    for (ASTCDInterface cdInterface : astCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(astPackage));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(astPackage)
        .setCDDefinition(astCD)
        .build();
  }

  private List<ASTCDClass> createASTClasses(final ASTCDCompilationUnit ast) {
    return ast.getCDDefinition().getCDClassList().stream()
        .map(astFullDecorator::decorate)
        .collect(Collectors.toList());
  }

  private ASTCDInterface createASTInterface(final ASTCDCompilationUnit ast) {
    return astLanguageInterfaceDecorator.decorate(ast);
  }

  private List<ASTCDClass> createASTBuilderClasses(final ASTCDCompilationUnit ast) {
    return ast.getCDDefinition().getCDClassList().stream()
        .map(astBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  private ASTCDClass createNodeFactoryClass(final ASTCDCompilationUnit ast) {
    return nodeFactoryDecorator.decorate(ast);
  }

  private ASTCDClass createMillClass(final ASTCDCompilationUnit ast) {
    return millDecorator.decorate(ast);
  }
}
