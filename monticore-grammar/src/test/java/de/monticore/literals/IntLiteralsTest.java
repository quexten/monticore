/* (c) https://github.com/MontiCore/monticore */

package de.monticore.literals;

import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTLiteral;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class IntLiteralsTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  
  private void checkIntLiteral(int i, String s) throws IOException {
    ASTLiteral lit = LiteralsTestHelper.getInstance().parseLiteral(s);
    assertTrue(lit instanceof ASTIntLiteral);
    assertEquals(i, ((ASTIntLiteral) lit).getValue());
  }
  
  @Test
  public void testIntLiterals() {
    try {
      // decimal number
      checkIntLiteral(0, "0");
      checkIntLiteral(123, "123");
      checkIntLiteral(10, "10");
      checkIntLiteral(5, "5");
      
      // hexadezimal number
      checkIntLiteral(0x12, "0x12");
      checkIntLiteral(0Xeff, "0Xeff");
      checkIntLiteral(0x34567890, "0x34567890");
      checkIntLiteral(0xabcdef, "0xabcdef");
      checkIntLiteral(0x0, "0x0");
      checkIntLiteral(0xa, "0xa");
      checkIntLiteral(0xC0FFEE, "0xC0FFEE");
      checkIntLiteral(0x005f, "0x005f");
      
      // octal number
      checkIntLiteral(02, "02");
      checkIntLiteral(07, "07");
      checkIntLiteral(00, "00");
      checkIntLiteral(076543210, "076543210");
      checkIntLiteral(00017, "00017");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
