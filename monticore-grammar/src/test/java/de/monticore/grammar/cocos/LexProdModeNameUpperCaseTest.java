/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class LexProdModeNameUpperCaseTest extends CocoTest{
  private final String MESSAGE = " The lexical production %s must use Upper-case mode names.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4038.A4038";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new LexProdModeNameUpperCase());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, LexProdModeNameUpperCase.ERROR_CODE,
                         String.format(LexProdModeNameUpperCase.ERROR_MSG_FORMAT,
                              "EndTag"), checker);
  }



}
