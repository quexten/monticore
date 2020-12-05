/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder.buildermethods;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.methods.mutator.ListMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

/**
 * changes return type of builder setters for list attributes
 */
public class BuilderListMutatorDecorator extends ListMutatorDecorator {

  protected final ASTMCType builderType;

  public BuilderListMutatorDecorator(GlobalExtensionManagement glex, final ASTMCType builderType) {
    super(glex);
    this.builderType = builderType;
  }

  @Override
  protected List<ASTCDMethod> createSetter(ASTCDAttribute attribute) {
    disableTemplates();
    List<ASTCDMethod> methods = super.createSetter(attribute);
    enableTemplates();
    for (ASTCDMethod m : methods) {
      ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(builderType).build();
      m.setMCReturnType(returnType);
      int attributeIndex;
      if (m.getName().contains(capitalizedAttributeNameWithS)) {
        attributeIndex = m.getName().lastIndexOf(capitalizedAttributeNameWithS);
      } else {
        attributeIndex = m.getName().lastIndexOf(capitalizedAttributeNameWithOutS);
      }
      String methodName = m.getName().substring(0, attributeIndex);
      String parameterCall = m.getCDParameterList().stream()
          .map(ASTCDParameter::getName)
          .collect(Collectors.joining(", "));
      this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_ast.builder.MethodDelegate4ASTBuilder",
          attribute, methodName, parameterCall));
    }
    return methods;
  }

  @Override
  protected ASTCDMethod createSetListMethod(ASTCDAttribute ast) {
    String signature = String.format(SET_LIST, capitalizedAttributeNameWithS, attributeType, ast.getName());
    ASTCDMethod method = this.getCDMethodFacade().createMethodByDefinition(signature);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(builderType).build();
    method.setMCReturnType(returnType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.builder.Set4ASTBuilder", ast));
    return method;
  }
}
