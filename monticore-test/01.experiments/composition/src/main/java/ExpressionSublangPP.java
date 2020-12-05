/* (c) https://github.com/MontiCore/monticore */
import de.monticore.prettyprint.IndentPrinter;
import expression._ast.*;
import expression._visitor.*;

/**
 * Pretty prints automatons. Use {@link #print(ASTExpression)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *
 */
public class ExpressionSublangPP implements ExpressionVisitor2, ExpressionHandler {

  protected IndentPrinter out;
  protected ExpressionTraverser traverser;
  
  public ExpressionSublangPP(IndentPrinter o) {
    out = o;
  }
  
  public ExpressionTraverser getTraverser() {
    return traverser;
  }
  
  public void setTraverser(ExpressionTraverser traverser) {
    this.traverser = traverser;
  }

  // ----------------------------------------------------------
  // Typical visit/endvist methods:

  @Override
  public void visit(ASTTruth node) {
    if(node.isTt()) {
      out.print("true ");
    } else {
      out.print("false ");
    }
  }
  
  @Override
  public void visit(ASTNot node) {
    out.print("!");
  }
  
  @Override
  public void visit(ASTVariable node) {
    out.print(node.getName() +" ");
  }
  
  /* 
   * for InfixOps the traversal strategy has to be adapted:
   * The whole node is handled here 
   * (classic traverse, visit, endvisit are out of business) 
   */ 
  public void handle(ASTAnd node) {
    // out.print("/*(*/ ");
    node.getLeft().accept(getTraverser());
    out.print("&& ");
    node.getRight().accept(getTraverser());
    // out.print("/*)*/ ");
  }

}
