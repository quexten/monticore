/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StarImportSuperGrammarTest {
  
  private ASTCDCompilationUnit cdCompilationUnit;

  @BeforeClass
  public static void setup(){
    Grammar_WithConceptsMill.init();
  }
  
  public StarImportSuperGrammarTest() {
    cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/OverridingClassProdGrammar.mc4")).get();
  }
  
  @Test
  public void testStarImport() {
    ASTMCImportStatement importStatement = cdCompilationUnit.getMCImportStatementList().get(0);
    assertTrue(importStatement.isStar());
    assertEquals("mc2cdtransformation.Supergrammar", importStatement.getQName());
  }
}
