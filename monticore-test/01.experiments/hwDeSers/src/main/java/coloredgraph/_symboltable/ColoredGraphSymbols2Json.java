/* (c) https://github.com/MontiCore/monticore */

package coloredgraph._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;

import java.awt.*;

/**
 * This class extends the generated symbol table printer to add serialization of the "color"
 * symbolrule attribute of the VertexSymbol. As the type of "color" is not a built-in data type,
 * for which default serialization exists, the serialization strategy has to be realized manually.
 */
public class ColoredGraphSymbols2Json extends ColoredGraphSymbols2JsonTOP {

  public ColoredGraphSymbols2Json() {
   }

  public ColoredGraphSymbols2Json(JsonPrinter printer) {
    super(printer);
  }

  /**
   * This method serializes the color of a vertex in form of an instance of java.awt.Color as a
   * JSON array with numeric values for each red, green, and blue.
   * TODO AB: Move these methods to SymbolDeSer in MC generator
   * @param color
   */
  @Override public void serializeVertexColor(Color color) {
    printer.beginArray("color");     // Serialize color as arrays,
    printer.value(color.getRed());   // add red value first
    printer.value(color.getGreen()); // ... followed by green
    printer.value(color.getBlue());  // ... and blue.
    printer.endArray();              // Print the array end.
  }
}

