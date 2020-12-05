/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.JSON_PRINTER;
import static org.junit.Assert.*;

public class ScopeDeSerDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String JSON_OBJECT = "de.monticore.symboltable.serialization.json.JsonObject";

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_DELEGATOR_VISITOR = "de.monticore.codegen.symboltable.automaton._visitor.AutomatonDelegatorVisitor";

  private static final String AUTOMATON_SYMBOL = "AutomatonSymbol";

  private static final String STATE_SYMBOL = "StateSymbol";

  private static final String FOO_SYMBOL = "FooSymbol";

  private static final String DESER = "DeSer";

  @Before
  public void setUp(){
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    originalCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));

    ScopeDeSerDecorator decorator = new ScopeDeSerDecorator(glex, new SymbolTableService(astcdCompilationUnit), new MethodDecorator(glex, new SymbolTableService(decoratedScopeCompilationUnit)), new VisitorService(astcdCompilationUnit));

    this.scopeClass = decorator.decorate(decoratedScopeCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testScopeDeSerClassName(){
    assertEquals("AutomatonScopeDeSer", scopeClass.getName());
  }

  @Test
  public void testSuperInterfaceCount(){
    assertEquals(1, scopeClass.sizeInterface());
  }

  @Test
  public void testConstructorCount(){
    assertEquals(1, scopeClass.sizeCDConstructors());
  }

  @Test
  public void testConstructors(){
    ASTCDConstructor constructor = scopeClass.getCDConstructor(0);
    assertDeepEquals(CDModifier.PUBLIC, constructor.getModifier());
    assertTrue(constructor.isEmptyCDParameters());
  }

  @Test
  public void testAttributeCount(){
    assertEquals(6, scopeClass.sizeCDAttributes());
  }

  @Test
  public void testAttributes(){
    List<ASTCDAttribute> attributeList = scopeClass.getCDAttributeList();
    assertDeepEquals(CDModifier.PACKAGE_PRIVATE, attributeList.get(0).getModifier());
    assertEquals("automatonSymbolDeSer", attributeList.get(0).getName());
    assertDeepEquals(AUTOMATON_SYMBOL+DESER, attributeList.get(0).getMCType());
    assertDeepEquals(CDModifier.PACKAGE_PRIVATE, attributeList.get(1).getModifier());
    assertEquals("stateSymbolDeSer", attributeList.get(1).getName());
    assertDeepEquals(STATE_SYMBOL+DESER, attributeList.get(1).getMCType());
    assertDeepEquals(CDModifier.PACKAGE_PRIVATE, attributeList.get(2).getModifier());
    assertEquals("fooSymbolDeSer", attributeList.get(2).getName());
    assertDeepEquals(FOO_SYMBOL+DESER, attributeList.get(2).getMCType());
    assertDeepEquals(CDModifier.PACKAGE_PRIVATE, attributeList.get(3).getModifier());
    assertEquals("qualifiedNameSymbolDeSer", attributeList.get(3).getName());
    assertDeepEquals("de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbolDeSer",
        attributeList.get(3).getMCType());
    assertDeepEquals(CDModifier.PROTECTED, attributeList.get(4).getModifier());
    assertEquals("printer", attributeList.get(4).getName());
    assertDeepEquals(JSON_PRINTER, attributeList.get(4).getMCType());
    assertDeepEquals(CDModifier.PROTECTED, attributeList.get(5).getModifier());
    assertEquals("symbolTablePrinter", attributeList.get(5).getName());
    assertDeepEquals(AUTOMATON_DELEGATOR_VISITOR, attributeList.get(5).getMCType());
  }

  @Test
  public void testMethodCount(){
    assertEquals(15, scopeClass.sizeCDMethods());
  }

  @Test
  public void testSerializeMethod(){
    ASTCDMethod method = getMethodBy("serialize", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("toSerialize", parameter.getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeMethod(){
    ASTCDMethod method = getMethodBy("deserialize", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("serialized", parameter.getName());
    assertDeepEquals(String.class, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeAutomatonScopeMethod(){
    ASTCDMethod method = getMethodBy("deserializeAutomatonScope", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("scopeJson", parameter.getName());
    assertDeepEquals(JSON_OBJECT, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_SCOPE, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeAutomatonArtifactScopeMethod(){
    ASTCDMethod method = getMethodBy("deserializeAutomatonArtifactScope", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("scopeJson", parameter.getName());
    assertDeepEquals(JSON_OBJECT, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeAddonsMethods(){
    List<ASTCDMethod> methodList = getMethodsBy("deserializeAddons", scopeClass);
    assertEquals(2, methodList.size());
    for (ASTCDMethod method: methodList) {
      assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
      assertEquals(0, method.sizeException());
      List<ASTCDParameter> parameters = method.getCDParameterList();
      assertEquals("scope", parameters.get(0).getName());
      //assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(0).getMCType());
      //assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, parameters.get(0).getMCType());
      assertEquals("scopeJson", parameters.get(1).getName());
      assertDeepEquals(JSON_OBJECT, parameters.get(1).getMCType());
      assertTrue(method.getMCReturnType().isPresentMCVoidType());
    }
  }

  @Test
  public void testAddSymbolsMethod(){
    ASTCDMethod method = getMethodBy("addSymbols", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("scope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void testDeserializeAutomatonSymbol(){
    ASTCDMethod method = getMethodBy("deserializeAutomatonSymbol", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("symbolJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("scope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testDeserializeStateSymbol(){
    ASTCDMethod method = getMethodBy("deserializeStateSymbol", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("symbolJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("scope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testDeserializeFooSymbol(){
    ASTCDMethod method = getMethodBy("deserializeFooSymbol", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("symbolJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("scope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testDeserializeQualifiedNameSymbol(){
    ASTCDMethod method = getMethodBy("deserializeQualifiedNameSymbol", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(2, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("symbolJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertEquals("scope", parameters.get(1).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, parameters.get(1).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testDeserializeExtraAttributeMethod(){
    ASTCDMethod method = getMethodBy("deserializeExtraAttribute", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertBoolean(method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeFooMethod(){
    ASTCDMethod method = getMethodBy("deserializeFoo", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertListOf(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testDeserializeBlaMethod(){
    ASTCDMethod method = getMethodBy("deserializeBla", scopeClass);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals(0, method.sizeException());
    assertEquals(1, method.sizeCDParameters());
    List<ASTCDParameter> parameters = method.getCDParameterList();
    assertEquals("scopeJson", parameters.get(0).getName());
    assertDeepEquals(JSON_OBJECT, parameters.get(0).getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertOptionalOf(Integer.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testGeneratedCode(){
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, scopeClass, scopeClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
