/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MoveTransitionsTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testDeleteTransitions() throws IOException {
    String inputFile = "src/main/models/automaton/Testautomat.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    // execute tested code and store result
    MoveTransitions mtr = new MoveTransitions(aut.get());

    // definition of test input
    mtr.doAll();

    // assertions
    assertEquals(2, aut.get().getStateList().size());

    ASTState state_a = aut.get().getState(0);
    ASTState state_b = aut.get().getState(1);
    assertEquals(4, state_a.getTransitionList().size());
    assertEquals(0, state_b.getTransitionList().size());

    // and undo
    mtr.undoReplacement();

    // assertions
    assertEquals(0, state_a.getTransitionList().size());
    assertEquals(4, state_b.getTransitionList().size());

  }



}
