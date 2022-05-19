/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class OverridingNTHasNoAnnotationTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4098.A4098";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new OverridingNTHasNoAnnotation());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, OverridingNTHasNoAnnotation.ERROR_CODE,
        String.format(OverridingNTHasNoAnnotation.ERROR_MSG_FORMAT, "Foo", "de.monticore.grammar.cocos.invalid.A4098.A4098Super.Foo" ), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
