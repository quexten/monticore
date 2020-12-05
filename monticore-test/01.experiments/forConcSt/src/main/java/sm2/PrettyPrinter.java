/* (c) https://github.com/MontiCore/monticore */
package sm2;

import sm2._ast.ASTAutomaton;
import sm2._ast.ASTState;
import sm2._ast.ASTTransition;
import sm2._visitor.SM2Handler;
import sm2._visitor.SM2Traverser;
import sm2._visitor.SM2Visitor2;

/**
 * Pretty prints sm2. Use {@link #print(ASTAutomaton)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 */
public class PrettyPrinter implements SM2Visitor2 , SM2Handler {
  private String result = "";
  
  private int indention = 0;
  
  private String indent = "";

  protected SM2Traverser traverser;

  /**
   * Prints the sm2
   * 
   * @param automaton
   */
  public void print(ASTAutomaton automaton) {
    handle(automaton);
  }
  
  /**
   * Gets the printed result.
   * 
   * @return the result of the pretty print.
   */
  public String getResult() {
    return this.result;
  }

  @Override
  public void setTraverser(SM2Traverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public SM2Traverser getTraverser() {
    return traverser;
  }

  @Override
  public void visit(ASTAutomaton node) {
    println("automaton " + node.getName() + " {");
    indent();
  }
  
  @Override
  public void endVisit(ASTAutomaton node) {
    unindent();
    println("}");
  }
  
  @Override
  public void traverse(ASTAutomaton node) {
    // guarantee ordering: states before transitions
    node.getStateList().stream().forEach(s -> s.accept(getTraverser()));
    node.getTransitionList().stream().forEach(t -> t.accept(getTraverser()));
  }
  
  @Override
  public void visit(ASTState node) {
    print("state " + node.getName());
    if (node.isInitial()) {
      print(" <<initial>>");
    }
    if (node.isFinal()) {
      print(" <<final>>");
    }
    println(";");
  }
  
  @Override
  public void visit(ASTTransition node) {
    print(node.getFrom());
    print(" - " + node.getInput() + " > ");
    print(node.getTo());
    println(";");
  }
  
  private void print(String s) {
    result += (indent + s);
    indent = "";
  }
  
  private void println(String s) {
    result += (indent + s + "\n");
    indent = "";
    calcIndention();
  }
  
  private void calcIndention() {
    indent = "";
    for (int i = 0; i < indention; i++) {
      indent += "  ";
    }
  }
  
  private void indent() {
    indention++;
    calcIndention();
  }
  
  private void unindent() {
    indention--;
    calcIndention();
  }
}
