/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.misc.MiscMill;
import mc.testcases.misc._ast.ASTDef;
import mc.testcases.misc._ast.ASTSub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeleteSubListTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  ASTDef def;
  ASTSub sub;

  @Before
  public void setUp()  {
    def = MiscMill.defBuilder().uncheckedBuild();
    sub = MiscMill.subBuilder().uncheckedBuild();
    def.setSub(sub);
  }

  @Test
  public void testDoReplacement() {
    DeleteSubList testee = new DeleteSubList(def);
    assertTrue(testee.doPatternMatching());
    testee.doReplacement();

    assertFalse(def.isPresentSub());
  }

  @Test
  public void testUndoReplacement() {
    ASTDef def_before = def.deepClone();
    DeleteSubList testee = new DeleteSubList(def);
    testee.doAll();
    testee.undoReplacement();

    assertSame(sub, def.getSub());
    assertTrue(def_before.deepEquals(def));
  }

}
