/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import com.google.common.collect.Iterables;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class UsageNameTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;

  @BeforeClass
  public static void setup(){
    Grammar_WithConceptsMill.init();
  }
  
  public UsageNameTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/UsageNameGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
  }
  
  @Test
  public void testNonTerminal() {
    ASTCDAttribute cdAttribute = Iterables.getOnlyElement(astA.getCDAttributeList());
    assertEquals("nonTerminalUsageName", cdAttribute.getName());
  }
  
  @Test
  public void testConstant() {
    ASTCDAttribute cdAttribute = Iterables.getOnlyElement(astB.getCDAttributeList());
    assertEquals("constantUsageName", cdAttribute.getName());
  }
}
