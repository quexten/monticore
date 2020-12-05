/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive.transhost._symboltable;

import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import mc.embedding.transitive.transhost.TransHostMill;
import mc.embedding.transitive.transhost._ast.ASTTransStart;
import mc.embedding.transitive.transhost._parser.TransHostParser;

import java.io.IOException;
import java.util.Optional;

public class TransHostGlobalScope extends TransHostGlobalScopeTOP {

  public TransHostGlobalScope(ModelPath modelPath,
      String modelFileExtension) {
    super(modelPath, modelFileExtension);
  }

  public TransHostGlobalScope() {
  }

  @Override public TransHostGlobalScope getRealThis() {
    return this;
  }

  @Override public void loadFileForModelName(String modelName, String symbolName) {
    super.loadFileForModelName(modelName, symbolName);
    ModelCoordinate modelCoordinate = ModelCoordinates
        .createQualifiedCoordinate(modelName, getFileExt());
    String filePath = modelCoordinate.getQualifiedPath().toString();
    if (!isFileLoaded(filePath)) {
      addLoadedFile(filePath);
      getModelPath().resolveModel(modelCoordinate);
      if (modelCoordinate.hasLocation()) {
        ASTTransStart parse = parse(modelCoordinate);
        TransHostMill.transHostSymbolTableCreatorDelegator().createFromAST(parse);
      }
    }
    else {
      Log.debug("Already tried to load model for '" + symbolName
              + "'. If model exists, continue with cached version.",
          "CompositeGlobalScope");
    }
  }

  private ASTTransStart parse(ModelCoordinate model) {
    try {
      Optional<ASTTransStart> ast = new TransHostParser().parse(ModelCoordinates.getReader(model));
      if (ast.isPresent()) {
        return ast.get();
      }
    }
    catch (IOException e) {
    }
    return null;
  }

}
