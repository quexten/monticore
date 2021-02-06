/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class DuplicatedSymbolDefinitionInProdTest extends CocoTest {

  private final String MESSAGE = " Symbol or scope is mentioned more than once in the declaration 'A'.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4041.A4041";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new DuplicatedSymbolDefinitionInProd());
  }

  @Test
  public void testDuplicatedSymbolDefinition() {
    testInvalidGrammar(grammar, DuplicatedSymbolDefinitionInProd.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
