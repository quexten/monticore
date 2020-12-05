/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol.symbolsurrogatemutator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

@Deprecated
public class MandatoryMutatorSymbolSurrogateDecorator extends MandatoryMutatorDecorator {

  public MandatoryMutatorSymbolSurrogateDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    String name = String.format(SET, StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(ast.getName())));
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, name, this.getCDParameterFacade().createParameters(ast));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.symbolsurrogate.Set4SymbolSurrogate", ast));
    return method;
  }

}
