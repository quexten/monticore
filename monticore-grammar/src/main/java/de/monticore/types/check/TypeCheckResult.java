/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.Optional;

/**
 * Wrapper class for the last result in a type check
 */
public class TypeCheckResult {

  protected Optional<SymTypeExpression> currentResult;

  protected boolean type;

  protected boolean method;

  protected boolean field;

  public TypeCheckResult(){
    this.currentResult = Optional.empty();
    this.field = false;
    this.method = false;
    this.type = false;
  }

  public SymTypeExpression getCurrentResult() {
    return currentResult.get();
  }

  public boolean isPresentCurrentResult() {
    return currentResult.isPresent();
  }

  public void setCurrentResult(SymTypeExpression currentResult){
    this.currentResult = Optional.ofNullable(currentResult);
  }
  
  public void setCurrentResultAbsent() {
    this.currentResult = Optional.empty();
  }

  public void reset(){
    setCurrentResultAbsent();
    field = false;
    method = false;
    type = false;
  }

  public void setType() {
    this.type = true;
    this.field = false;
    this.method = false;
  }

  public void setMethod() {
    this.method = true;
    this.type=false;
    this.field=false;
  }

  public void setField() {
    this.field = true;
    this.type=false;
    this.method=false;
  }

  public void unsetType(){
    this.type=false;
  }

  public boolean isField() {
    return field;
  }

  public boolean isMethod() {
    return method;
  }

  public boolean isType() {
    return type;
  }

  public TypeCheckResult copy(){
    TypeCheckResult result = new TypeCheckResult();
    result.currentResult = currentResult;
    result.type = type;
    result.field = field;
    result.method = method;
    return result;
  }
}
