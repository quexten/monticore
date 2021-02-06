/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTGrammarMethod;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMethodParameter;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class SymbolRuleMethodTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTSymbolRule, ASTCDClass> link : rootLink.getLinks(ASTSymbolRule.class,
        ASTCDClass.class)) {
      for (ASTGrammarMethod method : link.source().getGrammarMethodList()) {
        link.target().getCDMethodList().add(translateASTMethodToASTCDMethod(method));
      }
    }
    return rootLink;
  }

  private ASTCDMethod createSimpleCDMethod(ASTGrammarMethod method) {
    ASTCDMethod cdMethod = CD4AnalysisNodeFactory.createASTCDMethod();
    cdMethod.setModifier(TransformationHelper.createPublicModifier());
    cdMethod.setName(method.getName());
    String dotSeparatedName = MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(method.getMCReturnType());
    cdMethod.setMCReturnType(TransformationHelper.createReturnType(dotSeparatedName));
    for (ASTMethodParameter param : method.getMethodParameterList()) {
      String typeName = MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(param.getType());
      cdMethod.getCDParameterList().add(TransformationHelper.createParameter(typeName, param.getName()));
    }
    return cdMethod;
  }

  private void addMethodBodyStereotype(ASTModifier modifier, StringBuilder code) {
    // to save the body in the cd
    // todo think of better version
    TransformationHelper.addStereotypeValue(modifier,
        MC2CDStereotypes.METHOD_BODY.toString(),
        code.toString());
  }

  private ASTCDMethod translateASTMethodToASTCDMethod(ASTGrammarMethod method) {
    ASTCDMethod cdMethod = createSimpleCDMethod(method);
    if (method.getBody() instanceof ASTAction) {
      StringBuilder code = new StringBuilder();
      for (ASTMCBlockStatement action : ((ASTAction) method.getBody()).getMCBlockStatementList()) {
        code.append(new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter()).prettyprint(action));
      }
      addMethodBodyStereotype(cdMethod.getModifier(), code);
    }
    return cdMethod;
  }
}
