<#-- (c) https://github.com/MontiCore/monticore -->
  de.monticore.symboltable.serialization.json.JsonObject symbol =
          de.monticore.symboltable.serialization.JsonParser.parseJsonObject(serialized);
  de.monticore.symboltable.serialization.JsonDeSers.checkCorrectDeSerForKind(getSerializedKind(), symbol);
  return deserialize(symbol);