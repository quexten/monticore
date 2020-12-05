/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class OverridingAbstractNTsHaveNoSuperRulesTest extends CocoTest{

  private final String MESSAGE =  " The abstract production ArrayType overriding a production of " +
          "a sub grammar must not extend the production Name.\n" +
          "Hint: Overriding productions can only implement interfaces.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4002.A4002";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new OverridingAbstractNTsHaveNoSuperRules());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, OverridingAbstractNTsHaveNoSuperRules.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Overriding", checker);
  }

}
