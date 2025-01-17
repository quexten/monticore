/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.WhileConditionHasBooleanType;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.DeriveSymTypeOfCombineExpressionsDelegator;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WhileConditionHasBooleanTypeTest {
  
  private static final TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
  
  @BeforeClass
  public static void disableFailQuick(){
  
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker.addCoCo(new WhileConditionHasBooleanType(new TypeCalculator(null,new DeriveSymTypeOfCombineExpressionsDelegator())));
    
  }
  
  public void checkValid(String expressionString) throws IOException {
    TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
    Optional<ASTMCBlockStatement> optAST = parser.parse_StringMCBlockStatement(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  public void checkInvalid(String expressionString) throws IOException {
    TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
    Optional<ASTMCBlockStatement> optAST = parser.parse_StringMCBlockStatement(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertFalse(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testValid() throws IOException{
    checkValid("while(true){}");
    checkValid("while(1<2){}");
    checkValid("while(!true&&(5==6)){}");
    checkValid("while((1<2)||(5%2==1)){}");
  }
  
  @Test
  public void testInvalid()throws IOException{
    checkInvalid("while(1+1){}");
    checkInvalid("while('c'+10){}");
    checkInvalid("while(1.2-5.5){}");
  }
  
}