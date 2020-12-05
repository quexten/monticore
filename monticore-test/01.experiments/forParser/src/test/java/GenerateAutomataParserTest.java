// (c) https://github.com/MontiCore/monticore

import de.se_rwth.commons.logging.*;
import org.junit.Ignore;
import org.junit.Test;


public class GenerateAutomataParserTest {

  public void setup() {
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }

  // Für die Ausführung dieses Tests muss in der Pom u.U. die Versionsnummer
  // für monticore-grammar und monticore-grammar-grammars auf ${last.mc.release}
  // gesetzt werden
  @Ignore
  @Test
  public void test() {
    String[] args = {"src/test/resources/Automata.mc4", "target/gen"};
    GenerateAutomataParser.main(args);
  }
}
