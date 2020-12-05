/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;
import mc.testcd4analysis._symboltable.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CD2EAdapter implements IOOTypeSymbolResolver, IMethodSymbolResolver, IFieldSymbolResolver, IFunctionSymbolResolver, IVariableSymbolResolver, ITypeSymbolResolver {

  private ITestCD4AnalysisGlobalScope cd4ascope;

  private CD2EHelper cd2EHelper;

  public CD2EAdapter(ITestCD4AnalysisGlobalScope cd4ascope) {
    this.cd4ascope = cd4ascope;
    this.cd2EHelper = new CD2EHelper();
  }

  @Override
  public List<MethodSymbol> resolveAdaptedMethodSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<MethodSymbol> predicate) {
    List<MethodSymbol> result = Lists.newArrayList();
    Optional<CDMethOrConstrSymbol> methOrConstrSymbolOpt = cd4ascope.resolveCDMethOrConstr(symbolName, modifier);
    if (methOrConstrSymbolOpt.isPresent()) {
      CDMethOrConstrSymbol methOrConstrSymbol = methOrConstrSymbolOpt.get();
      result.add(cd2EHelper.createMethodSymbolFormCDMethOrConstrSymbol(methOrConstrSymbol));
    }
    return result;
  }

  @Override
  public List<OOTypeSymbol> resolveAdaptedOOTypeSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<OOTypeSymbol> predicate) {
    List<OOTypeSymbol> result = Lists.newArrayList();
    Optional<CDTypeSymbol> typeSymbolOpt = cd4ascope.resolveCDType(symbolName, modifier);
    if (typeSymbolOpt.isPresent()) {
      OOTypeSymbol res = cd2EHelper.createOOTypeSymbolFormCDTypeSymbol(typeSymbolOpt.get());
      result.add(res);
    }
    return result;
  }

  @Override
  public List<FieldSymbol> resolveAdaptedFieldSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    List<FieldSymbol> result = Lists.newArrayList();
    Optional<CDFieldSymbol> cdFieldSymbolopt = cd4ascope.resolveCDField(symbolName, modifier);
    if (cdFieldSymbolopt.isPresent()) {
      CDFieldSymbol fieldSymbol = cdFieldSymbolopt.get();
      result.add(cd2EHelper.createFieldSymbolFormCDFieldSymbol(fieldSymbol));
    }
    return result;
  }

  @Override
  public List<FunctionSymbol> resolveAdaptedFunctionSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<FunctionSymbol> predicate) {
    List<FunctionSymbol> result = Lists.newArrayList();
    Optional<CDMethOrConstrSymbol> methOrConstrSymbolOpt = cd4ascope.resolveCDMethOrConstr(symbolName, modifier);
    if (methOrConstrSymbolOpt.isPresent()) {
      CDMethOrConstrSymbol methOrConstrSymbol = methOrConstrSymbolOpt.get();
      result.add(cd2EHelper.createMethodSymbolFormCDMethOrConstrSymbol(methOrConstrSymbol));
    }
    return result;
  }

  @Override
  public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    List<TypeSymbol> result = Lists.newArrayList();
    Optional<CDTypeSymbol> typeSymbolOpt = cd4ascope.resolveCDType(symbolName, modifier);
    if (typeSymbolOpt.isPresent()) {
      OOTypeSymbol res = cd2EHelper.createOOTypeSymbolFormCDTypeSymbol(typeSymbolOpt.get());
      result.add(res);
    }
    return result;
  }

  @Override
  public List<VariableSymbol> resolveAdaptedVariableSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<VariableSymbol> predicate) {
    List<VariableSymbol> result = Lists.newArrayList();
    Optional<CDFieldSymbol> cdFieldSymbolopt = cd4ascope.resolveCDField(symbolName, modifier);
    if (cdFieldSymbolopt.isPresent()) {
      CDFieldSymbol fieldSymbol = cdFieldSymbolopt.get();
      result.add(cd2EHelper.createFieldSymbolFormCDFieldSymbol(fieldSymbol));
    }
    return result;
  }
}
