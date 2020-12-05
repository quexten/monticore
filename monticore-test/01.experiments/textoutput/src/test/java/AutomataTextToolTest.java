/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataTextTool;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class AutomataTextToolTest {
  
  @BeforeClass
  public static void init() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.clearFindings();
    LogStub.clearPrints();
  }
  
  @Test
  public void executePingPong() {
    AutomataTextTool.main(new String[]{"src/test/resources/example/PingPong.aut"});
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
  
    List<String> p = LogStub.getPrints();
    assertEquals(5, p.size());
  
    // Check some "[INFO]" outputs
    assertTrue(p.get(0), p.get(0).matches(".*.INFO.  AutomataTool Automaton DSL Tool.*\n"));
    assertTrue(p.get(3), p.get(3).matches(".*.INFO.  AutomataTool Printing the parsed automaton into textual form:.*\n"));
  
    // Check resulting pretty print:
    String res = p.get(p.size() - 1).replaceAll("[\r\n]", " ");
    // Please note: the resulting string is NONDETERMINISTIC, i.e. the text output is chosen among
    // three alternatives
    assertTrue(res.length() > 500);
    assertTrue(res, res.matches(".*automaton is called 'PingPong'.*"));
    assertTrue(res,
            res.matches(".*State Pong changes to NoGame when receiving input stopGame.*")
                    || res.matches(".*With input stopGame the state of the described object changes its state from Pong to NoGame.*")
                    || res.matches(".*State Pong and input stopGame map to new state NoGame.*")
                    || res.matches(".*Processing stopGame the object transitions from state Pong to NoGame.*")
            );
  }
  
  @Test
  public void executeSimple12() {
    AutomataTextTool.main(new String[] { "src/test/resources/example/Simple12.aut" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(5, p.size());
  }
  
  @Test
  public void executeHierarchyPingPong() {
    AutomataTextTool.main(new String[] { "src/test/resources/example/HierarchyPingPong.aut" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(5, p.size());
  }
  
}
