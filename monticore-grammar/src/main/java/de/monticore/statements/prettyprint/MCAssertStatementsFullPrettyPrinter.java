package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mcassertstatements.MCAssertStatementsMill;
import de.monticore.statements.mcassertstatements._ast.ASTMCAssertStatementsNode;
import de.monticore.statements.mcassertstatements._visitor.MCAssertStatementsTraverser;

public class MCAssertStatementsFullPrettyPrinter extends ExpressionsBasisFullPrettyPrinter {

  private MCAssertStatementsTraverser traverser;

  public MCAssertStatementsFullPrettyPrinter(IndentPrinter printer){
    super(printer);
    this.traverser = MCAssertStatementsMill.traverser();

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.addExpressionsBasisVisitor(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCAssertStatementsPrettyPrinter assertStatements = new MCAssertStatementsPrettyPrinter(printer);
    traverser.addMCAssertStatementsVisitor(assertStatements);
    traverser.setMCAssertStatementsHandler(assertStatements);

    traverser.addMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
  }

  public MCAssertStatementsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCAssertStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTMCAssertStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }




}