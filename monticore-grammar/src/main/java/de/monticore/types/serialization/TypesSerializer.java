/* generated by template symboltable.serialization.ArtifactScopeSerializer*/

package de.monticore.types.serialization;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.monticore.symboltable.serializing.DelegatingSerializer;
import de.monticore.symboltable.serializing.ISerialization;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

public class TypesSerializer  {
  protected GsonBuilder gson;
  
  public TypesSerializer() {
    gson = new GsonBuilder();
    gson.serializeSpecialFloatingPointValues();
    
    List<ISerialization<?>> serializers = ImmutableList.of(
        new ASTMCQualifiedTypeSerializer()
        //TODO: Hier andere Serialisierer für TypesKlassen registrieren
        );
    
    DelegatingSerializer delegatingSerializer = new DelegatingSerializer(serializers);
    
    gson.registerTypeAdapter(ASTMCType.class, delegatingSerializer);
    
    for (ISerialization<?> serializer : serializers) {
      gson.registerTypeAdapter(serializer.getSerializedClass(), serializer);
    }
    
  }  

  public Optional<String> serialize(ASTMCType as) {
    String serialize;
    try {
      serialize = getGson().toJson(as);
    }
    catch (Exception e) {
      Log.info("Serialization of TypesSerializer in \"" + as.toString() + "\" failed.", e,
          "JsonArtifactScopeSerializer");
      return Optional.empty();
    }
    
    return Optional.ofNullable(serialize);
  }
  

  public Optional<ASTMCType> deserialize(String content) {
    ASTMCType fromJson;
    try {
      fromJson = getGson().fromJson(content, ASTMCType.class);
    }
    catch (Exception e) {
      Log.info("Deserialization of ASTMCType from \"" + content + "\" failed.", e,
          "JsonArtifactScopeSerializer");
      return Optional.empty();
    }
    
    return Optional.ofNullable(fromJson);
  }
  
  protected Gson getGson() {
    return gson.create();
  }
}
