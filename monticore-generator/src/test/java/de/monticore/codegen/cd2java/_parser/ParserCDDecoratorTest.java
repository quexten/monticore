package de.monticore.codegen.cd2java._parser;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserCDDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedASTCompilationUnit;

  private ASTCDCompilationUnit originalASTCompilationUnit;

  private ASTCDCompilationUnit parserCD;

  private ASTCDCompilationUnit parserCDComponent;

  @Before
  public void setUp() {
    // to be issued (the warnings are not checked)
    LogStub.init();         // replace log by a sideffect free variant
//     LogStub.initPlusLog();  // for manual testing purpose only
//    Log.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();
    IterablePath targetPath = Mockito.mock(IterablePath.class);

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedASTCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalASTCompilationUnit = decoratedASTCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new AbstractService(decoratedASTCompilationUnit));

    ParserService service = new ParserService(decoratedASTCompilationUnit);

    ParserClassDecorator parserClassDecorator = new ParserClassDecorator(glex, service);
    ParserForSuperDecorator parserForSuperDecorator = new ParserForSuperDecorator(glex, service);
    ParserCDDecorator parserCDDecorator = new ParserCDDecorator(glex, parserClassDecorator, parserForSuperDecorator, service);

    this.parserCD = parserCDDecorator.decorate(decoratedASTCompilationUnit);

    ParserService mockService = Mockito.spy(new ParserService(decoratedASTCompilationUnit));
    parserClassDecorator = new ParserClassDecorator(glex, mockService);
    parserForSuperDecorator = new ParserForSuperDecorator(glex, mockService);
    ParserCDDecorator mockDecorator = new ParserCDDecorator(glex, parserClassDecorator, parserForSuperDecorator, mockService);
    Mockito.doReturn(false).when(mockService).hasStartProd(Mockito.any(ASTCDDefinition.class));
    Mockito.doReturn(true).when(mockService).hasComponentStereotype(Mockito.any(ASTModifier.class));
    this.parserCDComponent = mockDecorator.decorate(decoratedASTCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalASTCompilationUnit, decoratedASTCompilationUnit);
  }

  @Test
  public void testCDName() {
    assertEquals("Automaton", parserCD.getCDDefinition().getName());
  }

  @Test
  public void testClassCount() {
    assertEquals(1, parserCD.getCDDefinition().sizeCDClasss());
  }

  @Test
  public void testClasses(){
    ASTCDClass automatonParser = getClassBy("AutomatonParser", parserCD);
  }

  @Test
  public void testNoEnum() {
    assertTrue(parserCD.getCDDefinition().isEmptyCDEnums());
  }

  @Test
  public void testNoInterface(){
    assertTrue(parserCD.getCDDefinition().isEmptyCDInterfaces());
  }

  @Test
  public void testPackage() {
    assertEquals(6, parserCD.getPackageList().size());
    assertEquals("de", parserCD.getPackageList().get(0));
    assertEquals("monticore", parserCD.getPackageList().get(1));
    assertEquals("codegen", parserCD.getPackageList().get(2));
    assertEquals("symboltable", parserCD.getPackageList().get(3));
    assertEquals("automaton", parserCD.getPackageList().get(4));
    assertEquals("_parser", parserCD.getPackageList().get(5));
  }

  @Test
  public void testImports() {
    assertEquals(0, parserCD.getMCImportStatementList().size());
  }

  @Test
  public void testCDNameComponent() {
    assertEquals("Automaton", parserCDComponent.getCDDefinition().getName());
  }

  @Test
  public void testNoClassesComponent() {
    assertTrue(parserCDComponent.getCDDefinition().isEmptyCDClasss());
  }

  @Test
  public void testNoEnumComponent(){
    assertTrue(parserCDComponent.getCDDefinition().isEmptyCDEnums());
  }

  @Test
  public void testNoInterfaceComponent(){
    assertTrue(parserCDComponent.getCDDefinition().isEmptyCDInterfaces());
  }
}
