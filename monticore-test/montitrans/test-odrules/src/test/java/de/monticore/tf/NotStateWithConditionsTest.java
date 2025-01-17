/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class NotStateWithConditionsTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  ASTAutomaton aut;

  @Test
  public void testAutomatWith1InitialAnd2OtherStates() throws IOException {
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse_StringAutomaton("automaton Automaton {state a; state b <<initial>>; state c;}");

    assertTrue(aut.isPresent());
    assertEquals(3, aut.get().getStateList().size());

    // execute tested code and store result
    NotStateWithConditions rule = new NotStateWithConditions(aut.get());

    // assertions
    assertFalse(rule.doPatternMatching());
  }

  @Test
  public void testAutomatWith3OtherStates() throws IOException {
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse_StringAutomaton("automaton Automaton {state a; state b; state c;}");

    assertTrue(aut.isPresent());
    assertEquals(3, aut.get().getStateList().size());

    // execute tested code and store result
    NotStateWithConditions rule = new NotStateWithConditions(aut.get());

    // assertions
    rule.doPatternMatching();
  }
}
