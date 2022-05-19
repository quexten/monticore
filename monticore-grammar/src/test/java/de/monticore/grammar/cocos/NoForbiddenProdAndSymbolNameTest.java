/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoForbiddenProdAndSymbolNameTest extends CocoTest{

  private final String MESSAGE = " There must not exist a production with the name ASymbol in the grammar A4122 if " +
      "there already exists a symbol with the name A.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4122.A4122";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new NoForbiddenProdAndSymbolName());
  }

  @Test
  public void testInvalid1(){
    testInvalidGrammar(grammar, NoForbiddenProdAndSymbolName.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testValid1(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs",checker);
  }

}
