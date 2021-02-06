/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class NTOnlyExtendsOrAstextendsNTOrClassTest extends CocoTest{

  private final String MESSAGE = " The nonterminal B must not extend and astextend a type.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4029.A4029";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new NTOnlyExtendOrAstextendNTOrClass());
  }

  @Test
  public void testASTExtendMultiple() {
    testInvalidGrammar(grammar, NTOnlyExtendOrAstextendNTOrClass.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testExtendNT(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs", checker);
  }

}
