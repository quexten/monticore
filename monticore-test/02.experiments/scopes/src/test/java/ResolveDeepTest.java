/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;
import automata._symboltable.AutomataGlobalScope;
import automata._symboltable.IAutomataArtifactScope;
import automata._symboltable.IAutomataScope;
import automata._symboltable.StateSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class ResolveDeepTest {
  
  @Test
  public void testDeepResolve() throws IOException {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Path model = Paths.get("src/test/resources/example/HierarchyPingPong.aut");
    AutomataParser parser = new AutomataParser();
    
    // parse model
    Optional<ASTAutomaton> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    // build symbol table
    IAutomataArtifactScope scope = createSymbolTable(jsonDoc.get());
    
    // test resolving
    // we only test the resolveDown methods as the other methods are not
    // modified (i.e., resolve and resolve locally methods are not affected)
    
    // test if default qualified resolveDown behavior is preserved
    assertTrue(scope.resolveStateDown("PingPong.InGame.Ping").isPresent());
    assertTrue(scope.resolveStateDown("PingPong.very.deep.substate").isPresent());
    
    // test deep resolving with unqualified symbol name
    assertTrue(scope.resolveStateDown("PingPong.substate").isPresent());
    
    // test unqualified resolving with multiple occurrences: 2 Ping symbols
    assertEquals(scope.resolveStateDownMany("PingPong.Ping").size(), 2, 0);
    
    // test negative case, where we try to resolve one Ping state
    boolean success = true;
    try {
      scope.resolveStateDown("PingPong.Ping");
    }
    catch (ResolvedSeveralEntriesForSymbolException e) {
      success = false;
    }
    assertFalse(success);
    
    // test "half"-qualified down resolving. We pass an incomplete qualification
    // for symbol Ping. Expected behavior: we handle the name as fully qualified
    // until there is only one part left and continue with deep resolving in all
    // substates. In this test case, we navigate to the scope spanning symbol
    // "very". From here, the symbol Ping lies several scopes beneath. However,
    // since Ping is uniquely accessible from this point, no error occurs and we
    // find exactly one symbol.
    assertTrue(scope.resolveStateDown("PingPong.very.Ping").isPresent());
    
    // test down resolving with in-between steps
    Optional<StateSymbol> deep_sym = scope.resolveState("PingPong.deep");
    IAutomataScope deep_scope = deep_sym.get().getSpannedScope();
    assertTrue(deep_scope.resolveStateDown("substate").isPresent());
  }
  
  /**
   * Creates the symbol table from the parsed AST.
   *
   * @param ast The top AST node.
   * @return The artifact scope derived from the parsed AST
   */
  public static IAutomataArtifactScope createSymbolTable(ASTAutomaton ast) {
    AutomataMill.globalScope().setFileExt("aut");
    return AutomataMill.automataSymbolTableCreator().createFromAST(ast);
  }
  
}
