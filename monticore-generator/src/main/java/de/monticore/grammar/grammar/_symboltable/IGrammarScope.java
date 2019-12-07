/* (c) https://github.com/MontiCore/monticore */
/* generated by template symboltable.ScopeInterface*/

package de.monticore.grammar.grammar._symboltable;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.*;
import java.util.function.Predicate;

import static de.monticore.codegen.GeneratorHelper.isQualified;
import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.util.Optional.empty;

public interface IGrammarScope extends IGrammarScopeTOP {

  // all resolveImported Methods for ProdSymbol
  default public Optional<ProdSymbol> resolveProdImported(String name, AccessModifier modifier) {
    Optional<ProdSymbol> s = this.resolveProdLocally(name);
    if (s.isPresent()) {
      return s;
    }
    return resolveInSuperGrammars(name, modifier);
  }


  default public List<ProdSymbol> resolveProdMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<ProdSymbol> predicate)  {
    if (!isProdSymbolsAlreadyResolved()) {
      setProdSymbolsAlreadyResolved(true);
    } else {
      return new ArrayList<>();
    }

    final List<ProdSymbol> resolvedSymbols = this.resolveProdLocallyMany(foundSymbols, name, modifier, predicate);
    if (!resolvedSymbols.isEmpty()) {
      setProdSymbolsAlreadyResolved(false);
      return resolvedSymbols;
    }

    resolveInSuperGrammars(name, modifier).ifPresent(resolvedSymbols::add);
    if (!resolvedSymbols.isEmpty()) {
      setProdSymbolsAlreadyResolved(false);
      return resolvedSymbols;
    }

    resolvedSymbols.addAll(resolveAdaptedProdLocallyMany(foundSymbols, name, modifier, predicate));
    if (!resolvedSymbols.isEmpty()) {
      setProdSymbolsAlreadyResolved(false);
      return resolvedSymbols;
    }
    final Collection<ProdSymbol> resolvedFromEnclosing = continueProdWithEnclosingScope((foundSymbols | resolvedSymbols.size() > 0), name, modifier, predicate);
    resolvedSymbols.addAll(resolvedFromEnclosing);
    setProdSymbolsAlreadyResolved(false);
    return resolvedSymbols;
  }

  default Optional<ProdSymbol> resolveInSuperGrammars(String name,  AccessModifier modifier) {
    Optional<ProdSymbol> resolvedSymbol = empty();

    // TODO (GV, MB)
    // Die Methode muss überarbeitet werden. GrammarSymbols sollen nicht gefunden werden? Dann braucht man u.U.
    // checkIfContinueWithSuperGrammar gar nicht mehr ...
    Optional<MCGrammarSymbol> spanningSymbol = MCGrammarSymbolTableHelper.getMCGrammarSymbol(this);
    if (spanningSymbol.isPresent()) {
      MCGrammarSymbol grammarSymbol = spanningSymbol.get();
      for (MCGrammarSymbolLoader superGrammarRef : grammarSymbol.getSuperGrammars()) {
        if (checkIfContinueWithSuperGrammar(name, superGrammarRef)
                && (superGrammarRef.isSymbolLoaded())) {
          final MCGrammarSymbol superGrammar = superGrammarRef.getLoadedSymbol();
          resolvedSymbol = resolveInSuperGrammar(name, superGrammar);
          // Stop as soon as symbol is found in a super grammar.
          if (resolvedSymbol.isPresent()) {
            break;
          }
        }
      }
    }
    return resolvedSymbol;
  }

  default boolean checkIfContinueWithSuperGrammar(String name, MCGrammarSymbolLoader superGrammar) {
    // checks cases:
    // 1) A   and A
    // 2) c.A and A
    // 3) A   and p.A
    // 4) p.A and p.A
    // 5) c.A and p.A // <-- only continue with this case, since we can be sure,
    //                       that we are not searching for the super grammar itself.
    String superGrammarName = superGrammar.getName();
    if (getSimpleName(superGrammarName).equals(getSimpleName(name))) {

      // checks cases 1) and 4)
      if (superGrammarName.equals(name) ||
              // checks cases 2) and 3)
              (isQualified(superGrammar) != isQualified(name))) {
        return false;
      } else {
        // case 5)
        return true;
      }
    }
    // names have different simple names and the name isn't qualified (A and p.B)
    return isQualified(superGrammar) && !isQualified(name);
  }

  default Optional<ProdSymbol> resolveInSuperGrammar(String name, MCGrammarSymbol superGrammar) {
    return superGrammar.getSpannedScope().resolveProdImported(name, ALL_INCLUSION);
  }

}

