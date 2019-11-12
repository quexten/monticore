// (c) https://github.com/MontiCore/monticore

package de.monticore.types.typesymbols._symboltable;


import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;


public  class TypeSymbolBuilder extends TypeSymbolBuilderTOP {


protected TypeSymbolBuilder()  {
   this.realBuilder = (TypeSymbolBuilder) this;
}



  public TypeSymbolBuilder setTypeParameterList(List<TypeVarSymbol> typeVariableList) {
    for(TypeVarSymbol t : typeVariableList) {
      spannedScope.add(t);
    }
    return this.realBuilder;
  }

   public TypeSymbolBuilder setMethodList(List<MethodSymbol> methodList) {
      for(MethodSymbol m : methodList) {
        spannedScope.add(m);
      }
      return this.realBuilder;
   }

   public TypeSymbolBuilder setFieldList(List<FieldSymbol> fieldList) {
      for(FieldSymbol f : fieldList) {
        spannedScope.add(f);
      }
      return this.realBuilder;
   }
}
