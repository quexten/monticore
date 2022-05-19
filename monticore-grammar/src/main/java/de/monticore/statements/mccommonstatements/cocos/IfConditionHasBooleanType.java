/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTIfStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;

public class IfConditionHasBooleanType implements MCCommonStatementsASTIfStatementCoCo {
 
  TypeCalculator typeCheck;
  
  public static final String ERROR_CODE = "0xA0909";
  
  public static final String ERROR_MSG_FORMAT = "Condition in if-statement must be a boolean expression.";
  
  public IfConditionHasBooleanType(TypeCalculator typeCheck){
    this.typeCheck = typeCheck;
  }
  
  //JLS3 14.9-1
  @Override
  public void check(ASTIfStatement node) {
  
    SymTypeExpression result = typeCheck.typeOf(node.getCondition());
    
    if(!TypeCheck.isBoolean(result)){
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  }
}
