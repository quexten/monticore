/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._ast.ASTBasicSymbolsNode;
import de.monticore.symbols.oosymbols._ast.ASTMethod;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.testjavalight._parser.TestJavaLightParser;
import de.monticore.types.check.DeriveSymTypeOfCombineExpressionsDelegator;
import de.monticore.types.check.SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReturnTypeAssignmentIsValidTest extends JavaLightCocoTest {

  private static final JavaLightCoCoChecker checker = new JavaLightCoCoChecker();
  
  @BeforeClass
  public static void disableFailQuick(){
    
    LogStub.enableFailQuick(false);
    TestJavaLightMill.reset();
    TestJavaLightMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker.addCoCo(new ReturnTypeAssignmentIsValid(new TypeCalculator(new SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator(), new DeriveSymTypeOfCombineExpressionsDelegator())));
    
  }
  
  public void checkValid(String expressionString) throws IOException {
  
    TestJavaLightParser parser = new TestJavaLightParser();
    Optional<ASTMethod> optAST = parser.parse_StringMethod(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll((ASTBasicSymbolsNode) optAST.get());
    assertTrue(Log.getFindings().isEmpty());
    
  }
  
  public void checkInvalid(String expressionString) throws IOException {
    
    TestJavaLightParser parser = new TestJavaLightParser();
    Optional<ASTMethod> optAST = parser.parse_StringMethod(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll((ASTBasicSymbolsNode) optAST.get());
    assertFalse(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void testValid() throws IOException {
    
    checkValid("public void test(){return;}");
    checkValid("public int test(){return 3;}");
    checkValid("public char test(){return 'c';}");
    checkValid("public double test(){return 1.2;}");
    checkValid("public boolean test(){return false;}");
    
  }
  
  @Test
  public void testInvalid() throws IOException {
    
    checkInvalid("public void test(){return 3;}");
    checkInvalid("public int test(){return 3.0;}");
    checkInvalid("public char test(){return;}");
    checkInvalid("public double test(){return true;}");
    checkInvalid("public boolean test(){return 'f';}");
    
  }
  
}