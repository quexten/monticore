/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisSymbolTableCreatorDelegator;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.io.paths.ModelPath;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.fail;

public abstract class DecoratorTestCase {

  private static final String MODEL_PATH = "src/test/resources/";

  @BeforeClass
  public static void setUpDecoratorTestCase() {
    ICD4AnalysisGlobalScope globalScope = CD4AnalysisMill.cD4AnalysisGlobalScope();
    globalScope.clear();
    globalScope.setModelFileExtension("cd");
    globalScope.setModelPath(new ModelPath(Paths.get(MODEL_PATH)));
  }

  public ASTCDCompilationUnit parse(String... names) {
    String qualifiedName = String.join("/", names);

    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> ast = null;
    try {
      ast = parser.parse(MODEL_PATH + qualifiedName + ".cd");
    } catch (IOException e) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
    if (!ast.isPresent()) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
    CD4AnalysisSymbolTableCreatorDelegator creator = CD4AnalysisMill.cD4AnalysisSymbolTableCreatorDelegator();
    creator.createFromAST(ast.get());
    return ast.get();
  }
}
