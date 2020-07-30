/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTOverrideAnnotation;
import de.monticore.grammar.grammar._cocos.GrammarASTClassProdCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks if nonterminals with an override annotation really overrides a class
 *

 */
public class NoOverridingNTHasAnnotation implements GrammarASTClassProdCoCo {

  public static final String ERROR_CODE = "0xA4094";

  public static final String ERROR_MSG_FORMAT = " The production %s does not override any production.";

  @Override
  public void check(ASTClassProd a) {
    if (a.getGrammarAnnotationsList().stream().anyMatch(s -> s instanceof ASTOverrideAnnotation)) {
      Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
              .getMCGrammarSymbol(a.getEnclosingScope());

      if (!grammarSymbol.get().getInheritedProd(a.getName()).isPresent()) {
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
                a.get_SourcePositionStart());
      }
    }
  }

}
