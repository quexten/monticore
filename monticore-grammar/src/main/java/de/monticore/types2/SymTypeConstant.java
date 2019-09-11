/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymTypeConstant extends SymTypeExpression {

  /**
   * A typeConstant has a name
   */
  protected String constName;

  public SymTypeConstant(String constName) {
    this.constName = constName;
  }
  
  public String getConstName() {
    return constName;
  }
  
  public String getBoxedConstName() {
    return box(constName);
  }
  
  public String getBaseOfBoxedName() {
    String[] parts = box(constName).split("\\.");
    return parts[parts.length - 1];
  }
  
  public void setConstName(String constName) {
    String c = unbox(constName);
    if (primitiveTypes.contains(name)) {
      this.constName = constName;
    } else {
      Log.error("0xD34B2 Only primitive types allowed (" + primitiveTypes.toString() + "), but was:" + constName);
    }
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    return getConstName();
  }
  
  
  // TODO Future: box, unbox hier rausnehmen und als überschreibbare Dynamische Methoden nach TypeCheck
  /**
   * List of potential constants
   * (on purpose not implemented as enum)
   */
  public static List<String> primitiveTypes = Arrays
          .asList("boolean", "byte", "char", "short", "int", "long", "float", "double","void");
  
  /**
   * Map for unboxing const types (e.g. "java.lang.Boolean" -> "boolean")
   */
  public static Map<String,String> unboxMap;
  
  /**
   * Map for boxing const types (e.g. "boolean" -> "java.lang.Boolean")
   * Results are fully qualified.
   */
  public static Map<String,String> boxMap;
  
  /**
   * initializing the maps
   */
  static {
    unboxMap = new HashMap<String, String>();
    unboxMap.put("java.lang.Boolean", "boolean");
    unboxMap.put("java.lang.Byte", "byte");
    unboxMap.put("java.lang.Character", "char");
    unboxMap.put("java.lang.Short", "short");
    unboxMap.put("java.lang.Integer", "int");
    unboxMap.put("java.lang.Long", "long");
    unboxMap.put("java.lang.Float", "float");
    unboxMap.put("java.lang.Double", "double");
    unboxMap.put("java.lang.String", "String");
    unboxMap.put("Boolean", "boolean");
    unboxMap.put("Byte", "byte");
    unboxMap.put("Character", "char");
    unboxMap.put("Short", "short");
    unboxMap.put("Integer", "int");
    unboxMap.put("Long", "long");
    unboxMap.put("Float", "float");
    unboxMap.put("Double", "double");
    unboxMap.put("String", "String");
    
    boxMap = new HashMap<String,String>();
    boxMap  .put("boolean","java.lang.Boolean");
    boxMap  .put("byte",   "java.lang.Byte");
    boxMap  .put("char",   "java.lang.Character");
    boxMap  .put("double", "java.lang.Double");
    boxMap  .put("float",  "java.lang.Float");
    boxMap  .put("int",    "java.lang.Integer");
    boxMap  .put("long",   "java.lang.Long");
    boxMap  .put("short",  "java.lang.Short");
    boxMap  .put("String", "java.lang.String");
  }
  
  /**
   * unboxing const types (e.g. "java.lang.Boolean" -> "boolean").
   * otherwise return is unchanged
   * @param boxedName
   * @return
   */

  public static String unbox(String boxedName) {
    if (unboxMap.containsKey(boxedName))
      return unboxMap.get(boxedName);
    else
      return boxedName;
  }
  
  /**
   * Boxing const types (e.g. "boolean" -> "java.lang.Boolean")
   * Results are fully qualified.
   * Otherwise return is unchanged
   * @param unboxedName
   * @return
   */
  public static String box(String unboxedName) {
    if (boxMap.containsKey(unboxedName))
      return boxMap.get(unboxedName);
    else
      return unboxedName;
  }
  
  // TODO: 2-3 Tests zB box(unbox("..."))
  
  
  /**
   * Checks whether it is an integer type (incl. byte, long, char)
   * @return true if the given type is an integral type
   */
  public boolean isIntegralType() {
    return  "int"  .equals(getConstName()) ||
            "byte" .equals(getConstName()) ||
            "short".equals(getConstName()) ||
            "long" .equals(getConstName()) ||
            "char" .equals(getConstName()) ;
  }
  
  /**
   * Checks whether it is an integer type (incl. byte, long, char)
   * @return true if the given type is an integral type
   */
  public boolean isNumericType() {
    return  "float" .equals(getConstName()) ||
            "double".equals(getConstName()) ||
            isIntegralType() ;
  }
  
  /**
   * Am I primitive? (such as "int")
   */
  public boolean isPrimitiveType() {
    return true;
  }
  
  
  // --------------------------------------------------------------------------
  
  @Deprecated
  public void setName(String name) {
    this.name = name;
    this.constName = name; // Nur ein Hack um die Tests am laufen zu halten, die setName nutzen
  }
  
    @Override @Deprecated
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    if(!(symTypeExpression instanceof SymTypeConstant)){
      return false;
    }
    if(!this.name.equals(symTypeExpression.name)){
      return false;
    }
    if(!this.typeSymbol.equals(symTypeExpression.typeSymbol)){
      return false;
    }
    for(int i = 0; i<this.superTypes.size();i++){
      if(!this.superTypes.get(i).deepEquals(symTypeExpression.superTypes.get(i))){
        return false;
      }
    }
    return true;
  }

  @Override @Deprecated
  public SymTypeExpression deepClone() {
    SymTypeConstant clone = new SymTypeConstant(getConstName());
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);
    for(SymTypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    clone.typeSymbol = this.typeSymbol;
    return clone;
  }
  //hier enum attr für primitive types
  
  @Deprecated
  public SymTypeConstant() {
  }
  
  
}