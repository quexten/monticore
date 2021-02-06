package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class LeftRecursiveTranslationTest {

  private ASTCDCompilationUnit leftRecursive;

  @BeforeClass
  public static void setup(){
    Grammar_WithConceptsMill.init();
  }

  @Before
  public void setUp() {
    leftRecursive = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/LeftRecursiveProd.mc4")).get();
  }

  @Test
  public void testLeftRecursiveProd(){
    ASTCDClass a = getClassBy("ASTA", leftRecursive);
    assertTrue(a.isPresentModifier());
    assertTrue(a.getModifier().isPresentStereotype());
    assertEquals(1, a.getModifier().getStereotype().sizeValue());
    assertEquals("left_recursive", a.getModifier().getStereotype().getValue(0).getName());
    assertFalse(a.getModifier().getStereotype().getValue(0).isPresentValue());
  }

}
