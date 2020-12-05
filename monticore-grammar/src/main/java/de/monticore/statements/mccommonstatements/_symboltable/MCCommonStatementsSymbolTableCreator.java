/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements._symboltable;

import de.monticore.statements.mccommonstatements._ast.ASTFormalParameter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;

import java.util.Deque;

@Deprecated
public   class MCCommonStatementsSymbolTableCreator extends MCCommonStatementsSymbolTableCreatorTOP {

  public MCCommonStatementsSymbolTableCreator() {
  }

  public MCCommonStatementsSymbolTableCreator(
      IMCCommonStatementsScope enclosingScope) {
    super(enclosingScope);
  }

  public MCCommonStatementsSymbolTableCreator(
      Deque<? extends IMCCommonStatementsScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public void endVisit(ASTFormalParameter ast) {
    FieldSymbol symbol = ast.getDeclaratorId().getSymbol();
    symbol.setType(createTypeLoader(ast.getMCType()));
  }

  private SymTypeExpression createTypeLoader(ASTMCType ast) {
    FullSynthesizeFromMCFullGenericTypes synFromFull = new FullSynthesizeFromMCFullGenericTypes();
    // Start visitor
    ast.accept(synFromFull.getTraverser());
    return synFromFull.getResult().orElse(new SymTypeOfNull());
  }

}
