package de.monticore.codegen.cd2java._visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TraverserInterfaceDecoratorTest extends DecoratorTestCase {

  public static final String AUTOMATONVISITOR2 = "de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor2";

  public static final String AUTOMATONHANDLER = "de.monticore.codegen.ast.automaton._visitor.AutomatonHandler";


  public static final String ASTAUTOMATON = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  public static final String STATESYMBOL = "de.monticore.codegen.ast.automaton._symboltable.StateSymbol";

  public static final String AUTOMATONSCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private MCTypeFacade mcTypeFacade;

  private ASTCDInterface traverserInterface;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();
    this.mcTypeFacade = MCTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);

    TraverserInterfaceDecorator decorator = new TraverserInterfaceDecorator(this.glex, visitorService, symbolTableService);
    this.traverserInterface = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testVisitorName() {
    assertEquals("AutomatonTraverser", traverserInterface.getName());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(0, traverserInterface.sizeCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(42, traverserInterface.sizeCDMethods());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(1, traverserInterface.sizeInterface());
  }

  @Test
  public void testInterface() {
    assertDeepEquals("de.monticore.codegen.ast.lexicals._visitor.LexicalsTraverser", traverserInterface.getInterface(0));
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, traverserInterface, traverserInterface);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

  @Test
  public void testAdd4Automaton() {
    ASTCDMethod astcdMethod = getMethodBy("add4Automaton", traverserInterface);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, astcdMethod.sizeCDParameters());
    ASTCDParameter astcdParameter = astcdMethod.getCDParameter(0);
    assertEquals("automatonVisitor", astcdParameter.getName());
    assertDeepEquals(AUTOMATONVISITOR2, astcdParameter.getMCType());

  }

  @Test
  public void testGetAutomatonVisitorList() {
    ASTCDMethod astcdMethod = getMethodBy("getAutomatonVisitorList", traverserInterface);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCType());
    assertListOf(AUTOMATONVISITOR2, astcdMethod.getMCReturnType().getMCType());
    assertEquals(0, astcdMethod.sizeCDParameters());

  }

  @Test
  public void testGetAutomatonHandler() {
    ASTCDMethod astcdMethod = getMethodBy("getAutomatonHandler", traverserInterface);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCType());
    assertOptionalOf(AUTOMATONHANDLER, astcdMethod.getMCReturnType().getMCType());
    assertEquals(0, astcdMethod.sizeCDParameters());

  }

  @Test
  public void testSetAutomatonHandler() {
    ASTCDMethod astcdMethod = getMethodBy("setAutomatonHandler", traverserInterface);
    assertDeepEquals(PUBLIC, astcdMethod.getModifier());
    assertTrue(astcdMethod.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, astcdMethod.sizeCDParameters());
    ASTCDParameter astcdParameter = astcdMethod.getCDParameter(0);
    assertEquals("automatonHandler", astcdParameter.getName());
    assertDeepEquals(AUTOMATONHANDLER, astcdParameter.getMCType());

  }

  @Test
  public void testVisitASTAutomaton() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(ASTAUTOMATON))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTAutomaton() {
    List<ASTCDMethod> list = getMethodsBy("endVisit", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(ASTAUTOMATON))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testHandleAstautomaton() {
    List<ASTCDMethod> list = getMethodsBy("handle", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(ASTAUTOMATON))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseAstautomaton() {
    List<ASTCDMethod> list = getMethodsBy("traverse", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(ASTAUTOMATON))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testVisitStateSymbol() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(STATESYMBOL))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitStateSymbol() {
    List<ASTCDMethod> list = getMethodsBy("endVisit", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(STATESYMBOL))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testHandleStateSymbol() {
    List<ASTCDMethod> list = getMethodsBy("handle", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(STATESYMBOL))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseStateSymbol() {
    List<ASTCDMethod> list = getMethodsBy("traverse", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(STATESYMBOL))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void testVisitIAutomatonScope() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(AUTOMATONSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitIAutomatonScope() {
    List<ASTCDMethod> list = getMethodsBy("endVisit", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(AUTOMATONSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testHandleIAutomatonScope() {
    List<ASTCDMethod> list = getMethodsBy("handle", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(AUTOMATONSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseIAutomatonScope() {
    List<ASTCDMethod> list = getMethodsBy("traverse", 1, traverserInterface);
    List<ASTCDMethod> methods = list.stream()
        .filter(m -> m.getCDParameter(0).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(AUTOMATONSCOPE))
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals("node", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


}
