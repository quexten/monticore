/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class DummyAdapter implements IETypeSymbolResolvingDelegate, IEMethodSymbolResolvingDelegate, IEVariableSymbolResolvingDelegate {

  private IExpressionsBasisScope scope;

  private TypeExpression a;
  private TypeExpression b;

  public DummyAdapter(IExpressionsBasisScope scope){
    this.scope = scope;
  }

  @Override
  public Collection<EMethodSymbol> resolveAdaptedEMethodSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<EMethodSymbol> predicate) {
    ArrayList<EMethodSymbol> list = new ArrayList<>();
    if(symbolName.equals("call")||symbolName.equals("A.B.C.call")) {
      symbolName = "int";
    }
    EMethodSymbol sym = ExpressionsBasisSymTabMill.eMethodSymbolBuilder().setAccessModifier(modifier).setName(symbolName).setEnclosingScope(scope).build();
    TypeExpression returnType =TypesCalculatorHelper.fromEMethodSymbol(sym);
    returnType.setName(symbolName);
    sym.setReturnType(returnType);
    list.add(sym);
    return list;
  }

  @Override
  public Collection<ETypeSymbol> resolveAdaptedETypeSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<ETypeSymbol> predicate) {
    ArrayList<ETypeSymbol> list = new ArrayList<>();
    ETypeSymbol sym = ExpressionsBasisSymTabMill.eTypeSymbolBuilder().setAccessModifier(modifier).setName(symbolName).setEnclosingScope(scope).build();
    list.add(sym);
    return list;
  }

  @Override
  public Collection<EVariableSymbol> resolveAdaptedEVariableSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<EVariableSymbol> predicate) {
    ArrayList<EVariableSymbol> list = new ArrayList<>();
    if(symbolName.contains("var")){
      symbolName=symbolName.substring(3);
    }
    EVariableSymbol sym = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setAccessModifier(modifier).setName(symbolName).setEnclosingScope(scope).build();

    TypeExpression type = TypesCalculatorHelper.fromEVariableSymbol(sym);
    type.setName(symbolName);
    if(symbolName.equals("A")){
      a = new ObjectType();
      a.setName("A");
      b = new ObjectType();
      b.setName("B");

      List<TypeExpression> superTypes = new ArrayList<>();
      superTypes.add(a);

      b.setSuperTypes(superTypes);

    }
    if(symbolName.equals("B")){
      ArrayList<TypeExpression> superTypes = new ArrayList<>();
      superTypes.add(a);
      type.setSuperTypes(superTypes);
    }
    sym.setType(type);
    list.add(sym);
    return list;
  }

}
