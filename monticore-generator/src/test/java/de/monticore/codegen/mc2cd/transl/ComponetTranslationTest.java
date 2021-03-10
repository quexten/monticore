/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ComponetTranslationTest {

  private ASTCDCompilationUnit componentCD;

  private ASTCDCompilationUnit nonComponentCD;

  @BeforeClass
  public static void setup(){
    GrammarFamilyMill.init();
  }

  @Before
  public void setUp() {
    componentCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AbstractProd.mc4")).get();
    nonComponentCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRuleInheritance.mc4")).get();
  }

  @Test
  public void testIsComponent() {
    assertTrue(componentCD.getCDDefinition().isPresentModifier());
    assertTrue(componentCD.getCDDefinition().getModifier().isPresentStereotype());
    assertEquals(1, componentCD.getCDDefinition().getModifier().getStereotype().sizeValues());
    assertEquals("component",componentCD.getCDDefinition().getModifier().getStereotype().getValues(0).getName());
    assertFalse(componentCD.getCDDefinition().getModifier().getStereotype().getValues(0).isPresentText());
  }

  @Test
  public void testIsNotComponent() {
    assertTrue(nonComponentCD.getCDDefinition().isPresentModifier());
    assertFalse(nonComponentCD.getCDDefinition().getModifier().isPresentStereotype());
  }

}
