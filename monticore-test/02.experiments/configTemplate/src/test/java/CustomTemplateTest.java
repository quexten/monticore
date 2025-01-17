/* (c) https://github.com/MontiCore/monticore */

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

/**
 * Main class for the some Demonstration to Parse
 */
public class CustomTemplateTest {

  private static final String MSG = "Empty method body detected!";

  /**
   * @throws IOException
   */
  @Test
  public void testParseMethods() throws IOException {
    // Internally calls the default constructor new ASTAutomaton, which is
    // empty by default. As this experiment overrides the empty method template
    // by an alternative logging a corresponding warning instead, we can test
    // this behavior by implicitly invoking the constructor and checking for
    // the expected warning in the log.
    Log.clearFindings();
    ASTAutomaton aut = AutomataMill.automatonBuilder().setName("test").build();

    // expecting two warnings of the same type, once for instantiating the mill
    // and once for instantiating the ASTAutomaton node
    List<Finding> findings = Log.getFindings();
    assertEquals(2, findings.size());
    assertEquals(MSG, findings.get(0).getMsg());
    assertEquals(MSG, findings.get(1).getMsg());

    // the config template additionally configures overriding the specific
    // "sizeStates" method of the automaton to always return 10.
    // Thus, we expect the corresponding method to return 10, while the
    // actual state list is empty
    assertEquals(0, aut.getStateList().size()); // actual state list size
    assertEquals(10, aut.sizeStates());         // modified state list size

    assertEquals(0, aut.count());
  }

}
