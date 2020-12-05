/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scopes;

import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.*;
import mc.feature.scopes.scopeattributes.ScopeAttributesMill;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesGlobalScope;
import mc.feature.scopes.supautomaton.SupAutomatonMill;
import mc.feature.scopes.supautomaton._ast.ASTSup;
import mc.feature.scopes.supautomaton._parser.SupAutomatonParser;
import mc.feature.scopes.supautomaton._symboltable.ISupAutomatonGlobalScope;
import mc.feature.scopes.supautomaton._symboltable.SupAutomatonGlobalScope;
import mc.feature.scopes.supautomaton._symboltable.SupAutomatonScope;
import mc.feature.scopes.supautomaton._symboltable.SupAutomatonSymbolTableCreatorDelegator;
import mc.feature.scopes.superautomaton._symboltable.AutomatonSymbol;
import mc.feature.scopes.superautomaton._symboltable.StateSymbol;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScopesTest {

  private ASTSup astSup;
  private SupAutomatonSymbolTableCreatorDelegator symbolTableCreator;
  private ISupAutomatonGlobalScope globalScope;


  @Before
  public void setUp() throws IOException {
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    SupAutomatonParser supAutomatonParser = new SupAutomatonParser();
    Optional<ASTSup> astSup = supAutomatonParser.parse("src/test/resources/mc/feature/scopes/SupAutomatonModel.aut");
    assertFalse(supAutomatonParser.hasErrors());
    assertTrue(astSup.isPresent());

    ISupAutomatonGlobalScope globalScope = SupAutomatonMill.globalScope();
    globalScope.setFileExt("aut");
    globalScope.getModelPath().addEntry(Paths.get("src/test/resources/mc/feature/scopes"));
    this.symbolTableCreator = SupAutomatonMill.supAutomatonSymbolTableCreatorDelegator();

    this.astSup = astSup.get();
    this.globalScope = globalScope;
    this.symbolTableCreator = symbolTableCreator;
  }

  @Test
  public void testResolvingFromGrammarScope(){

    SupAutomatonScope fromAST = (SupAutomatonScope) symbolTableCreator.createFromAST(astSup);
    fromAST.setName("TopPingPong");

    //findet, denn liegt im ersten Scope
    Optional<AutomatonSymbol> pingPongAutomatonSymbolLokal = fromAST.getSubScopes().stream().findAny().get().resolveAutomaton("PingPong");
    //findet, denn sucht dann von oben aus
    Optional<AutomatonSymbol> pingPongAutomatonSymbolGlobal = fromAST.resolveAutomaton("TopPingPong.PingPong");
    //mit resolveDown kann der SupScope aus dem SupAutomatonScope heraus gefunden werden
    Optional<StateSymbol> pingStateSymbol = fromAST.getSubScopes().stream().findAny().get().resolveStateDown("PingPong.Ping");
    //Muss hier mit resolve vollqualifiziert angegeben werden, denn sonst findet der SupAutomatonScope nichts in SupScopes
    //resolved hier also einmal nach ganz oben und von da aus wieder nach unten
    Optional<StateSymbol> pongStateSymbol = fromAST.resolveState("TopPingPong.PingPong.Pong");
    Optional<StateSymbol> noGameStateSymbol = fromAST.resolveState("TopPingPong.PingPong.NoGame");
    //findet also voll qualifiziert auch vom global aus
    Optional<StateSymbol> pingStateSymbolGlobal = globalScope.resolveState("TopPingPong.PingPong.Ping");

    assertTrue(pingPongAutomatonSymbolLokal.isPresent());
    assertTrue(pingPongAutomatonSymbolGlobal.isPresent());
    assertTrue(pingStateSymbol.isPresent());
    assertTrue(pongStateSymbol.isPresent());
    assertTrue(noGameStateSymbol.isPresent());
    assertTrue(pingStateSymbolGlobal.isPresent());

  }
}
