package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public class ScopeRuleToCDScopeClass implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    createLinksForMatchedASTRules(rootLink);
    return rootLink;
  }

  private void createLinksForMatchedASTRules(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    // creates Links from the ScopeRule for the Scope class
    Set<Link<ASTMCGrammar, ASTCDClass>> linkList = rootLink.getLinks(ASTMCGrammar.class, ASTCDClass.class);
    List<ASTScopeRule> scopeRuleList = ASTNodes.getSuccessors(rootLink.source(), ASTScopeRule.class);
    // only allowed to be one scope class and one scope rule for one cd compilationUnit
    if (linkList.size() == 1 && scopeRuleList.size() == 1) {
      ASTScopeRule scopeRule = scopeRuleList.get(0);
      Link<ASTMCGrammar, ASTCDClass> link = linkList.stream().findFirst().get();
      new Link<>(scopeRule, link.target(), link.parent());
    }
  }
}