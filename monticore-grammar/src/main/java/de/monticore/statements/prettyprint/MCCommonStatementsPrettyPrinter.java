/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsHandler;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsTraverser;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Iterator;

public class MCCommonStatementsPrettyPrinter implements
    MCCommonStatementsVisitor2, MCCommonStatementsHandler {
  
  protected MCCommonStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCCommonStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  @Override
  public MCCommonStatementsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCCommonStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  protected void printNode(String s) {
    getPrinter().print(s);
  }

  protected void printExpressionsList(Iterator<? extends ASTExpression> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = separator;
    }
  }

  protected void printMCTypeList(Iterator<? extends ASTMCType> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = separator;
    }
  }

  @Override
  public void handle(ASTMCJavaBlock a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("{");
    getPrinter().indent();
    a.getMCBlockStatementList().stream().forEach(m -> m.accept(getTraverser()));
    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTIfStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("if (");
    a.getCondition().accept(getTraverser());
    getPrinter().print(") ");
    a.getThenStatement().accept(getTraverser());
    if (a.isPresentElseStatement()) {
      getPrinter().println("else ");
      a.getElseStatement().accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTForStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("for (");
    a.getForControl().accept(getTraverser());
    getPrinter().print(")");
    a.getMCStatement().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTWhileStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("while (");
    a.getCondition().accept(getTraverser());
    getPrinter().print(")");
    a.getMCStatement().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDoWhileStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("do ");
    a.getMCStatement().accept(getTraverser());
    getPrinter().print("while (");
    a.getCondition().accept(getTraverser());
    getPrinter().println(");");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCommonForControl a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isPresentForInit()) {
      a.getForInit().accept(getTraverser());
    }
    getPrinter().print(";");
    if (a.isPresentCondition()) {
      a.getCondition().accept(getTraverser());
    }
    getPrinter().print(";");
    printExpressionsList(a.getExpressionList().iterator(), ",");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTForInitByExpressions a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(" ");
    printExpressionsList(a.getExpressionList().iterator(), ", ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTEnhancedForControl a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getFormalParameter().accept(getTraverser());
    getPrinter().print(": ");
    a.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTJavaModifier a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(" " + printModifier(a.getModifier()) + " ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTFormalParameter a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printSeparated(a.getJavaModifierList().iterator(), " ");
    a.getMCType().accept(getTraverser());
    getPrinter().print(" ");
    a.getDeclaratorId().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

 @Override
  public void handle(ASTEmptyStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTExpressionStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getExpression().accept(getTraverser());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTSwitchStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("switch (");
    a.getExpression().accept(getTraverser());
    getPrinter().println(") {");
    getPrinter().indent();
    printSeparated(a.getSwitchBlockStatementGroupList().iterator(), "");
    printSeparated(a.getSwitchLabelList().iterator(), "");
    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTConstantExpressionSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("case ");
    a.getConstant().accept(getTraverser());
    getPrinter().println(":");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTEnumConstantSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("case ");
    printNode(a.getEnumConstant());
    getPrinter().println(":");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDefaultSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("default:");
  }

  @Override
  public void handle(ASTBreakStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("break;");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  protected void printSeparated(Iterator<? extends ASTMCCommonStatementsNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = separator;
    }
  }

  protected String printModifier(int constant) {

    switch (constant) {
      case ASTConstantsMCCommonStatements.PRIVATE:
        return "private";
      case ASTConstantsMCCommonStatements.PUBLIC:
        return "public";
      case ASTConstantsMCCommonStatements.PROTECTED:
        return "protected";
      case ASTConstantsMCCommonStatements.STATIC:
        return "static";
      case ASTConstantsMCCommonStatements.TRANSIENT:
        return "transient";
      case ASTConstantsMCCommonStatements.FINAL:
        return "final";
      case ASTConstantsMCCommonStatements.ABSTRACT:
        return "abstract";
      case ASTConstantsMCCommonStatements.NATIVE:
        return "native";
      case ASTConstantsMCCommonStatements.THREADSAFE:
        return "threadsafe";
      case ASTConstantsMCCommonStatements.SYNCHRONIZED:
        return "synchronized";
      case ASTConstantsMCCommonStatements.VOLATILE:
        return "volatile";
      case ASTConstantsMCCommonStatements.STRICTFP:
        return "strictfp";
      default:
        return null;
    }
  }
  
  
}
