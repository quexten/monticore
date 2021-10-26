/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.io.FileReaderWriter;
import de.monticore.javalight._ast.ASTJavaLightNode;
import de.monticore.javalight._ast.ASTJavaMethod;
import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.testjavalight._parser.TestJavaLightParser;
import de.monticore.testjavalight._symboltable.TestJavaLightArtifactScope;
import de.monticore.testjavalight._symboltable.TestJavaLightGlobalScope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public abstract class JavaLightCocoTest {

  static protected TestJavaLightGlobalScope globalScope;

  protected TestJavaLightArtifactScope artifactScope;

  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);

    TestJavaLightMill.reset();
    TestJavaLightMill.init();
    BasicSymbolsMill.initializePrimitives();

    globalScope = (TestJavaLightGlobalScope) TestJavaLightMill.globalScope();
    globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources"));
  }

  protected void testValid(String fileName, String methodName, JavaLightCoCoChecker checker) {
    loadFileForModelName(fileName);
    // test method symbol
    final MethodSymbol methodSymbol = artifactScope
            .resolveMethod(methodName)
            .orElse(null);
    assertNotNull(methodSymbol);
    assertTrue(methodSymbol.isPresentAstNode());

    Log.getFindings().clear();
    checker.checkAll((ASTJavaLightNode) methodSymbol.getAstNode());

    assertTrue(Log.getFindings().isEmpty());
  }

  protected void testInvalid(String fileName, String methodName, String code, String message,
                             JavaLightCoCoChecker checker) {
    testInvalid(fileName, methodName, code, message, checker, 1);
  }

  protected void testInvalid(String fileName, String methodName, String code, String message,
                             JavaLightCoCoChecker checker, int numberOfFindings) {
    loadFileForModelName(fileName);
    // test method symbol
    final MethodSymbol methodSymbol = artifactScope
            .resolveMethod(methodName)
            .orElse(null);
    assertNotNull(methodSymbol);
    assertTrue(methodSymbol.isPresentAstNode());

    Log.getFindings().clear();
    checker.checkAll((ASTJavaLightNode) methodSymbol.getAstNode());

    assertFalse(Log.getFindings().isEmpty());
    assertEquals(numberOfFindings, Log.getFindings().size());
    for (Finding f : Log.getFindings()) {
      assertEquals(code + message, f.getMsg());
    }
  }

  protected void loadFileForModelName(String modelName) {
    // 1. calculate potential location of model file and try to find it in model path
    Optional<URL> url = globalScope.getSymbolPath().find(Names.getPathFromPackage(modelName) + ".java");

    // 2. if the file was found, parse the model and create its symtab
    if (url.isPresent()) {
      Reader reader = FileReaderWriter.getReader(url.get());
      Optional<ASTJavaMethod> optAST;
      try {
        optAST = new TestJavaLightParser().parse(reader);
        if (optAST.isPresent()) {
          artifactScope = (TestJavaLightArtifactScope) new JavaLightPhasedSymbolTableCreatorDelegator().createFromAST(optAST.get());
          globalScope.addSubScope(artifactScope);
        }
      } catch (IOException e) {
        fail();
      }
    }
  }

}
