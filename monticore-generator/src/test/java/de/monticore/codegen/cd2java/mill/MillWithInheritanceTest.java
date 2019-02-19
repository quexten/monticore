package de.monticore.codegen.cd2java.mill;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MillWithInheritanceTest {

  private CDTypeFactory cdTypeFacade;


  private ASTCDCompilationUnit cdCompilationUnit;

  private ASTCDClass millClass;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFactory.getInstance();

    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/codegen/factory/BGrammar.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    GlobalScope globalScope = TestHelper.createGlobalScope(modelPath);
    script.createSymbolsFromAST(globalScope, grammar.get());
    cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        globalScope);
    ASTCDDefinition astcdDefinition = cdCompilationUnit.getCDDefinition().deepClone();

    cdCompilationUnit.setEnclosingScope(globalScope);
    MillDecorator millDecorator = new MillDecorator(glex);
    this.millClass = millDecorator.decorate(cdCompilationUnit);
    //test if not changed the original Definition
    assertTrue(astcdDefinition.deepEquals(cdCompilationUnit.getCDDefinition()));
  }

  @Test
  public void testAttributeName() {
    assertEquals("mill", millClass.getCDAttribute(0).getName());
    assertEquals("millASTBlub", millClass.getCDAttribute(1).getName());
    assertEquals("millASTBli", millClass.getCDAttribute(2).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : millClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, millClass.sizeCDConstructors());
    assertTrue(PROTECTED.deepEquals(millClass.getCDConstructor(0).getModifier()));
    assertEquals("BGrammarMill", millClass.getCDConstructor(0).getName());
  }

  @Test
  public void testGetMillMethod() {
    ASTCDMethod getMill = millClass.getCDMethod(0);
    //test Method Name
    assertEquals("getMill", getMill.getName());
    //test Parameters
    assertTrue(getMill.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("BGrammarMill");
    assertTrue(returnType.deepEquals(getMill.getReturnType()));
    //test Modifier
    assertTrue(PROTECTED_STATIC.deepEquals(getMill.getModifier()));
  }

  @Test
  public void testInitMeMethod() {
    ASTCDMethod initMe = millClass.getCDMethod(1);
    //test Method Name
    assertEquals("initMe", initMe.getName());
    //test Parameters
    assertEquals(1, initMe.sizeCDParameters());
    ASTType type = cdTypeFacade.createSimpleReferenceType("BGrammarMill");
    assertTrue(type.deepEquals(initMe.getCDParameter(0).getType()));
    assertEquals("a", initMe.getCDParameter(0).getName());
    //test ReturnType
    assertTrue(cdTypeFacade.createVoidType().deepEquals(initMe.getReturnType()));
    //test Modifier
    assertTrue(PUBLIC_STATIC.deepEquals(initMe.getModifier()));
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod init = millClass.getCDMethod(2);
    //test Method Name
    assertEquals("init", init.getName());
    //test Parameters
    assertTrue(init.isEmptyCDParameters());
    //test ReturnType
    assertTrue(cdTypeFacade.createVoidType().deepEquals(init.getReturnType()));
    //test Modifier
    assertTrue(PUBLIC_STATIC.deepEquals(init.getModifier()));
  }

  @Test
  public void testResetMethod() {
    ASTCDMethod init = millClass.getCDMethod(3);
    //test Method Name
    assertEquals("reset", init.getName());
    //test Parameters
    assertTrue(init.isEmptyCDParameters());
    //test ReturnType
    assertTrue(cdTypeFacade.createVoidType().deepEquals(init.getReturnType()));
    //test Modifier
    assertTrue(PUBLIC_STATIC.deepEquals(init.getModifier()));
  }

  @Test
  public void testCBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(8);
    //test Method Name
    assertEquals("aSTCBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.cgrammar._ast.ASTC");
    assertTrue(returnType.deepEquals(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(PUBLIC_STATIC.deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testFooBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(9);
    //test Method Name
    assertEquals("aSTFooBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTFoo");
    assertTrue(returnType.deepEquals(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(PUBLIC_STATIC.deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void tesBarBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(10);
    //test Method Name
    assertEquals("aSTBarBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTBar");
    assertTrue(returnType.deepEquals(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(PUBLIC_STATIC.deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, millClass, millClass);
    System.out.println(sb.toString());
  }

  @Test
  public void testGeneratedCodeInFile() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(Paths.get("target/generated-test-sources/generatortest/mill").toFile());
    Path generatedFiles = Paths.get("BGrammarMill.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, millClass, millClass);
  }
}
