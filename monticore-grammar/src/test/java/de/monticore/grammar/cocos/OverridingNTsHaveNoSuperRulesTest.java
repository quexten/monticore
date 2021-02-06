/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class OverridingNTsHaveNoSuperRulesTest extends CocoTest{

  private final String MESSAGE =  " The production QualifiedName overriding a production of " +
          "a sub grammar must not extend the production Name.\n" +
          "Hint: Overriding productions can only implement interfaces.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4001.A4001";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new OverridingNTsHaveNoSuperRules());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, OverridingNTsHaveNoSuperRules.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
