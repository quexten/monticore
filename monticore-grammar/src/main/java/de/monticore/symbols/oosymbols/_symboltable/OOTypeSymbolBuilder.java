// (c) https://github.com/MontiCore/monticore

// (c) https://github.com/MontiCore/monticore

package de.monticore.symbols.oosymbols._symboltable;

public  class OOTypeSymbolBuilder extends OOTypeSymbolBuilderTOP {

public OOTypeSymbolBuilder()  {
   this.realBuilder = (OOTypeSymbolBuilder) this;
}

@Override
  public OOTypeSymbol build ()  {
    OOTypeSymbol symbol = super.build();
    symbol.setSpannedScope(this.spannedScope);
    return symbol;
  }

}
