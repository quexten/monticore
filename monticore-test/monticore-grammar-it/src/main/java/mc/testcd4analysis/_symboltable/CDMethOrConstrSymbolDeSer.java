/* generated from model TestCD4Analysis */
/* generated by template core.Class*/

/* (c) https://github.com/MontiCore/monticore */
package mc.testcd4analysis._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.List;

public class CDMethOrConstrSymbolDeSer extends CDMethOrConstrSymbolDeSerTOP {

  @Override
  protected void serializeReturnType(CDTypeSymbolSurrogate returnType, TestCD4AnalysisSymbols2Json s2j) {

  }

  @Override
  protected void serializeExceptions(List<CDTypeSymbolSurrogate> exceptions, TestCD4AnalysisSymbols2Json s2j) {

  }

  @Override
  protected CDTypeSymbolSurrogate deserializeReturnType(JsonObject symbolJson) {
    return null;
  }

  @Override
  protected List<CDTypeSymbolSurrogate> deserializeExceptions(JsonObject symbolJson) {
    return null;
  }
}