/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTWhileStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTWhileStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;

public class WhileConditionHasBooleanType implements MCCommonStatementsASTWhileStatementCoCo {
 
  TypeCalculator typeCheck;
  
  public static final String ERROR_CODE = "0xA0919";
  
  public static final String ERROR_MSG_FORMAT = "Condition in while-statement must be a boolean expression.";
  
  public WhileConditionHasBooleanType(TypeCalculator typeCheck){
    this.typeCheck = typeCheck;
  }
  
  //JLS3 14.12-1
  @Override
  public void check(ASTWhileStatement node) {
    SymTypeExpression result = typeCheck.typeOf(node.getCondition());
  
    if(!TypeCheck.isBoolean(result)){
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  }
}