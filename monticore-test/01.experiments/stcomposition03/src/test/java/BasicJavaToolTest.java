/* (c) https://github.com/MontiCore/monticore */

import automata7._symboltable.Automata7ArtifactScope;
import automata7._symboltable.IAutomata7ArtifactScope;
import automata7._symboltable.StimulusSymbol;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import javaandaut.Class2StimulusAdapter;
import javaandaut.JavaAndAutTool;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Optional;

import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class BasicJavaToolTest {

  @BeforeClass
  public static void setUpLogger(){
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }

  @Test
  public void testPingPong(){
    ModelPath mp = new ModelPath(Paths.get("src/test/resources/example"));
    IAutomata7ArtifactScope symTab = JavaAndAutTool
        .createJavaAndAutSymTab("src/test/resources/example/PingPong.aut", mp);
    symTab.setName("PingPong");
    Optional<StimulusSymbol> hit = symTab.resolveStimulus("Hit");
    assertTrue(hit.isPresent());
    assertEquals("Hit", hit.get().getName());
    assertTrue(hit.get() instanceof Class2StimulusAdapter); //assure that an adapter was found
  }


}
