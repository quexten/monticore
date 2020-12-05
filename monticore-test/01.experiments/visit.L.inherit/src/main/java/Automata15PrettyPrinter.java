/* (c) https://github.com/MontiCore/monticore */

import automata15._ast.*;
import automata15._visitor.*;

/**
 * Pretty prints automatons. Use {@link #print(ASTAutomata15)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *

 */
public class Automata15PrettyPrinter implements Automata15Visitor2 {

  protected String result = "";
  
  protected int indention = 0;
  
  protected String indent = "";

  /**
   * Gets the printed result.
   * 
   * @return the result of the pretty print.
   */
  public String getResult() {
    return this.result;
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
  public void visit(ASTState node) {
    println("state " + node.getName() +";");
  }
  
  @Override
  public void visit(ASTTransition node) {
    print(node.getFrom());
    print(" - " + node.getInput() + " > ");
    print(node.getTo());
    println(";");
  }
  

  // the following part manages indentation -----------------
  
  protected void print(String s) {
    result += (indent + s);
    indent = "";
  }
  
  protected void println(String s) {
    result += (indent + s + "\n");
    indent = "";
    calcIndention();
  }
  
  protected void calcIndention() {
    indent = "";
    for (int i = 0; i < indention; i++) {
      indent += "  ";
    }
  }
  
  protected void indent() {
    indention++;
    calcIndention();
  }
  
  protected void unindent() {
    indention--;
    calcIndention();
  }
}
