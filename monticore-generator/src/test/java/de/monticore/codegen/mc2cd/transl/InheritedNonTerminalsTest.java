/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InheritedNonTerminalsTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;
  
  public InheritedNonTerminalsTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/InheritedNonTerminalsGrammar.mc4"));
    assertTrue(cdCompilationUnit.isPresent());
    List<ASTCDClass> classList = cdCompilationUnit.get().getCDDefinition().getCDClassList();
    astA = classList.get(0);
    astB = classList.get(1);
  }
  
  @Test
  public void testName() {
    assertEquals("ASTA", astA.getName());
    assertEquals("ASTB", astB.getName());
  }
  
  @Test
  public void testSuperGrammarResolving() {
    String name = typeToString(astA.getCDAttributeList().get(0).getMCType());
    assertEquals("mc2cdtransformation.Supergrammar.ASTX", name);
  }
}
