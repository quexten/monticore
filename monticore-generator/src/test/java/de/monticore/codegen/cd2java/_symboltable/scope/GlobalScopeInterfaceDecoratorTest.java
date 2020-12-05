/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

<<<<<<< HEAD
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
=======
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
<<<<<<< HEAD
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableConstants;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
=======
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.*;
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
import org.junit.Before;
import org.junit.Test;

<<<<<<< HEAD
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
<<<<<<< HEAD
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
=======
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
=======
import java.util.List;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
>>>>>>> edb5034b36c1599c0df1472c44f6c8c344790379
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GlobalScopeInterfaceDecoratorTest extends DecoratorTestCase {

  private ASTCDInterface scopeInterface;

  private GlobalExtensionManagement glex;

<<<<<<< HEAD
  private MCTypeFacade mcTypeFacade;

=======
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

<<<<<<< HEAD
  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  private static final String QUALIFIED_NAME_SYMBOL = "de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol";

  private static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String PREDICATE_AUTOMATON = "java.util.function.Predicate<de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol>";

  private static final String PREDICATE_QUALIFIED_NAME = "java.util.function.Predicate<de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol>";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  @Before
  public void setUp() {
    Log.init();
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
=======
  private MCTypeFacade mcTypeFacade;

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  private static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String PREDICATE_AUTOMATON = "java.util.function.Predicate<de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol>";

  private static final String QUALIFIED_NAME_SYMBOL = "de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol";

  private static final String PREDICATE_QUALIFIED_NAME = "java.util.function.Predicate<de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol>";

  @Before
  public void setUp() {
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    this.glex = new GlobalExtensionManagement();
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    this.mcTypeFacade = MCTypeFacade.getInstance();
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

<<<<<<< HEAD
    GlobalScopeInterfaceDecorator decorator = new GlobalScopeInterfaceDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit));
=======
    GlobalScopeInterfaceDecorator decorator = new GlobalScopeInterfaceDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedCompilationUnit)));
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e

    this.scopeInterface = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
<<<<<<< HEAD
  public void testClassName() {
=======
  public void testInterfaceName() {
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
    assertEquals("IAutomatonGlobalScope", scopeInterface.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, scopeInterface.sizeInterface());
  }

  @Test
  public void testSuperInterfaces() {
<<<<<<< HEAD
<<<<<<< HEAD
    assertDeepEquals(I_AUTOMATON_SCOPE, scopeInterface.getInterface(0));
    assertDeepEquals(SymbolTableConstants.I_GLOBAL_SCOPE_TYPE, scopeInterface.getInterface(1));
  }

  @Test
  public void testNoAttributes() {
    assertTrue(scopeInterface.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(14, scopeInterface.getCDMethodList().size());
=======
    assertDeepEquals("de.monticore.codegen.ast.lexicals._symboltable.ILexicalsGlobalScope",
=======
    assertDeepEquals("de.monticore.symboltable.IGlobalScope",
>>>>>>> edb5034b36c1599c0df1472c44f6c8c344790379
        scopeInterface.getInterface(0));
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope",
        scopeInterface.getInterface(1));
  }

  @Test
  public void testCalculateModelNamesForAutomatonMethod() {
    ASTCDMethod method = getMethodBy("calculateModelNamesForAutomaton", scopeInterface);
    assertDeepEquals("Set<String>", method.getMCReturnType().getMCType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testCalculateModelNamesForStateMethod() {
    ASTCDMethod method = getMethodBy("calculateModelNamesForState", scopeInterface);
    assertDeepEquals("Set<String>", method.getMCReturnType().getMCType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
  }


  @Test
<<<<<<< HEAD
<<<<<<< HEAD
  public void testGetAutomatonLanguageMethod() {
    ASTCDMethod method = getMethodBy("getAutomatonLanguage", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals("AutomatonLanguage", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
=======
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
  public void testCacheMethod() {
    ASTCDMethod method = getMethodBy("cache", scopeInterface);
=======
  public void testAddLoadedFileMethod() {
    ASTCDMethod method = getMethodBy("addLoadedFile", scopeInterface);
>>>>>>> edb5034b36c1599c0df1472c44f6c8c344790379

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("setEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", method.getCDParameter(0).getName());
  }

  @Test
  public void testResolveAdaptedAutomatonMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedAutomaton", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(AUTOMATON_SYMBOL, method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_AUTOMATON, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testResolveAdaptedSuperProdMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedQualifiedName", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(QUALIFIED_NAME_SYMBOL, method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }


  @Test
  public void testAddAdaptedAutomatonSymbolResolverMethod() {
    List<ASTCDMethod> methods = getMethodsBy("addAdaptedAutomatonSymbolResolver", 1,
        scopeInterface);

    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(
        "de.monticore.codegen.ast.automaton._symboltable.IAutomatonSymbolResolver",
        method.getCDParameter(0).getMCType());
    assertEquals("element", method.getCDParameter(0).getName());
  }


  @Test
  public void testResolveAutomatonManyMethod() {
    ASTCDMethod method = getMethodBy("resolveAutomatonMany", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(AUTOMATON_SYMBOL),
        method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_AUTOMATON, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testResolveAdaptedMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedAutomaton", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(AUTOMATON_SYMBOL),
        method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_AUTOMATON, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testResolveAutomatonManySuperProdMethod() {
    ASTCDMethod method = getMethodBy("resolveQualifiedNameMany", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(QUALIFIED_NAME_SYMBOL),
        method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

<<<<<<< HEAD

  @Test
  public void testContinueWithModelLoaderMethod() {
    ASTCDMethod method = getMethodBy("continueWithModelLoader", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("calculatedModelName", method.getCDParameter(0).getName());
    assertDeepEquals("AutomatonModelLoader", method.getCDParameter(1).getMCType());
    assertEquals("modelLoader", method.getCDParameter(1).getName());
  }

  @Test
  public void testResolveAutomatonManyMethod() {
    ASTCDMethod method = getMethodBy("resolveAutomatonMany", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_AUTOMATON, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testResolveAdaptedMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedAutomaton", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_AUTOMATON, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testLoadModelsForMethod() {
    ASTCDMethod method = getMethodBy("loadModelsForAutomaton", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testResolveAutomatonManySuperProdMethod() {
    ASTCDMethod method = getMethodBy("resolveQualifiedNameMany", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(QUALIFIED_NAME_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testResolveAdaptedSuperProdMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedQualifiedName", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(QUALIFIED_NAME_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }


  @Test
  public void testLoadModelsForSuperProdMethod() {
    ASTCDMethod method = getMethodBy("loadModelsForQualifiedName", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, scopeInterface, scopeInterface);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
=======
  @Test
  public void testMethodCount() {
    assertEquals(97, scopeInterface.getCDMethodList().size());
  }

>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
}
