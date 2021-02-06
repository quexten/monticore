/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoTokenDefinedTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();

  private final String grammarInvalid = "de.monticore.grammar.cocos.invalid.A4101.A4101";
  private final String grammarValid = "de.monticore.grammar.cocos.valid.Attributes";


  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new NoTokenDefined());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammarInvalid, NoTokenDefined.ERROR_CODE,
        String.format(NoTokenDefined.ERROR_MSG_FORMAT, "A4101"), checker);
  }

  @Test
  public void testValid() {
    testValidGrammar(grammarValid, checker);
  }
}
