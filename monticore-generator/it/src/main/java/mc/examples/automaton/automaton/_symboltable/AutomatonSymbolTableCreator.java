/* (c)  https://github.com/MontiCore/monticore */
package mc.examples.automaton.automaton._symboltable;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Optional;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import mc.examples.automaton.automaton._ast.ASTAutomaton;
import mc.examples.automaton.automaton._ast.ASTState;
import mc.examples.automaton.automaton._ast.ASTTransition;

public class AutomatonSymbolTableCreator extends AutomatonSymbolTableCreatorTOP {
  
  public AutomatonSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final Scope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  @Override
  public Scope createFromAST(ASTAutomaton rootNode) {
      requireNonNull(rootNode);

      final ArtifactScope artifactScope = new ArtifactScope(Optional.empty(), "", new ArrayList<>());
      putOnStack(artifactScope);

      rootNode.accept(this);

      return artifactScope;
  }

  @Override
  public void visit(final ASTAutomaton automatonNode) {
    final AutomatonSymbol automaton = new AutomatonSymbol(automatonNode.getName());
    addToScopeAndLinkWithNode(automaton, automatonNode);
  }
  
  @Override
  public void endVisit(final ASTAutomaton automatonNode) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(final ASTState stateNode) {
    final StateSymbol stateSymbol = new StateSymbol(stateNode.getName());
    
    addToScopeAndLinkWithNode(stateSymbol, stateNode);
  }

  @Override
  public void endVisit(final ASTState node) {
    removeCurrentScope();
  }

  @Override
  public void visit(final ASTTransition transitionNode) {
    transitionNode.setEnclosingScope(currentScope().get());
  }
}
