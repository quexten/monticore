/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.*;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertListOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class ArtifactScopeClassDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private de.monticore.types.MCTypeFacade MCTypeFacade;

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.AutomatonScope";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private static final String IMPORT_STATEMENT = "de.monticore.symboltable.ImportStatement";

  @Before
  public void setUp() {
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    this.glex = new GlobalExtensionManagement();
    this.MCTypeFacade = MCTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    ArtifactScopeClassDecorator decorator = new ArtifactScopeClassDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit),new VisitorService(decoratedCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedCompilationUnit)));

    //creates normal Symbol
    this.scopeClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonArtifactScope", scopeClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(1, scopeClass.getInterfaceList().size());
  }

  @Test
  public void testSuperClassCount() {
    assertTrue(scopeClass.isPresentCDExtendUsage());
  }

  @Test
  public void testSuperClass() {
    assertDeepEquals(AUTOMATON_SCOPE, scopeClass.getCDExtendUsage().getSuperclass(0));
  }

  @Test
  public void testConstructorCount() {
    assertEquals(3, scopeClass.getCDConstructorList().size());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonArtifactScope", cdConstructor.getName());

    assertEquals(2, cdConstructor.sizeCDParameters());
    assertDeepEquals(String.class, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("packageName", cdConstructor.getCDParameter(0).getName());

    assertDeepEquals("List<de.monticore.symboltable.ImportStatement>", cdConstructor.getCDParameter(1).getMCType());
    assertEquals("imports", cdConstructor.getCDParameter(1).getName());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  }


  @Test
  public void testConstructorWithEnclosingScope() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(1);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonArtifactScope", cdConstructor.getName());

    assertEquals(3, cdConstructor.sizeCDParameters());

    assertDeepEquals("Optional<" + I_AUTOMATON_SCOPE + ">", cdConstructor.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(0).getName());

    assertDeepEquals(String.class, cdConstructor.getCDParameter(1).getMCType());
    assertEquals("packageName", cdConstructor.getCDParameter(1).getName());

    assertDeepEquals("List<de.monticore.symboltable.ImportStatement>", cdConstructor.getCDParameter(2).getMCType());
    assertEquals("imports", cdConstructor.getCDParameter(2).getName());


    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  }

  @Test
  public void testZeroArgsConstructor(){
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(2);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonArtifactScope", cdConstructor.getName());
    assertTrue(cdConstructor.isEmptyCDParameters());
    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(2, scopeClass.getCDAttributeList().size());
  }

  @Test
  public void testPackageNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("packageName", scopeClass);
    assertDeepEquals(CDModifier.PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testImportsAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("imports", scopeClass);
    assertListOf(IMPORT_STATEMENT, astcdAttribute.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(8, scopeClass.getCDMethodList().size());
  }

  @Test
  public void testGetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("getPackageName", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("setPackageName", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("packageName", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetImportListMethod() {
    ASTCDMethod method = getMethodBy("getImportsList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(IMPORT_STATEMENT), method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetImportsListMethod() {
    ASTCDMethod method = getMethodBy("setImportsList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(MCTypeFacade.createListTypeOf(IMPORT_STATEMENT), method.getCDParameter(0).getMCType());
    assertEquals("imports", method.getCDParameter(0).getName());
  }

  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodsBy("accept", scopeClass).get(0);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonTraverser", method.getCDParameter(0).getMCType());
    assertEquals("visitor", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetNameOptMethod() {
    ASTCDMethod method = getMethodBy("getName", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetEnclosingScopeMethod(){
    ASTCDMethod method = getMethodBy("setEnclosingScope", scopeClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("enclosingScope", method.getCDParameter(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void testGeneratedCode() {
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
