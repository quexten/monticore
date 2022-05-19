/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractNTOnlyExtendsOrAstextendsNTOrClassTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4030.A4030";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new AbstractNTOnlyExtendOrAstextendNTOrClass());
  }

  @Test
  public void testASTExtendMultiple() {
    testInvalidGrammar(grammar, AbstractNTOnlyExtendOrAstextendNTOrClass.ERROR_CODE,
        String.format(AbstractNTOnlyExtendOrAstextendNTOrClass.ERROR_MSG_FORMAT, "B"),
        checker);
  }

  @Test
  public void testExtendNT(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs", checker);
  }

}
