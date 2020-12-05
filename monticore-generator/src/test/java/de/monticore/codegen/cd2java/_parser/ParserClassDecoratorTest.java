package de.monticore.codegen.cd2java._parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserClassDecoratorTest extends DecoratorTestCase {

  private ASTCDClass parserClass;

  private GlobalExtensionManagement glex;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private final String AST_AUTOMATON = "de.monticore.codegen.symboltable.automaton._ast.ASTAutomaton";

  private final String AST_STATE = "de.monticore.codegen.symboltable.automaton._ast.ASTState";

  private final String AST_NAME = "de.monticore.codegen.ast.lexicals._ast.ASTName";

  private final String AUTOMATON_ANTLR_PARSER = "AutomatonAntlrParser";

  @Before
  public void setUp() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    ParserService parserService = new ParserService(decoratedCompilationUnit);

    ParserClassDecorator parserClassDecorator = new ParserClassDecorator(glex, parserService);

    Optional<ASTCDClass> parserClassOpt = parserClassDecorator.decorate(decoratedCompilationUnit);
    assertTrue(parserClassOpt.isPresent());
    this.parserClass = parserClassOpt.get();
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName(){
    assertEquals("AutomatonParser", parserClass.getName());
  }

  @Test
  public void testNoSuperInterfaces(){
    assertTrue(parserClass.isEmptyInterface());
  }

  @Test
  public void testSuperclass(){
    assertTrue(parserClass.isPresentSuperclass());
    assertDeepEquals("de.monticore.antlr4.MCConcreteParser", parserClass.getSuperclass());
  }

  @Test
  public void testNoAttributes(){
    assertTrue(parserClass.isEmptyCDAttributes());
  }

  @Test
  public void testNoConstructors(){
    assertTrue(parserClass.isEmptyCDConstructors());
  }

  @Test
  public void testMethodCount(){
    assertEquals(29, parserClass.sizeCDMethods());
  }

  @Test
  public void testParseMethods(){
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException"))
        .build();
    List<ASTCDMethod> methods = getMethodsBy("parse", parserClass);
    assertEquals(2, methods.size());
    //parse with filename
    ASTCDMethod parseFileName = methods.get(0);
    assertTrue(parseFileName.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_AUTOMATON, parseFileName.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PUBLIC, parseFileName.getModifier());
    assertEquals(1, parseFileName.sizeException());
    assertDeepEquals(ioException, parseFileName.getException(0));
    assertEquals(1, parseFileName.sizeCDParameters());
    assertDeepEquals(String.class, parseFileName.getCDParameter(0).getMCType());
    assertEquals("fileName", parseFileName.getCDParameter(0).getName());

    //parse with reader
    ASTCDMethod parseReader = methods.get(1);
    assertTrue(parseReader.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_AUTOMATON, parseReader.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PUBLIC, parseReader.getModifier());
    assertEquals(1, parseReader.sizeException());
    assertDeepEquals(ioException, parseReader.getException(0));
    assertEquals(1, parseReader.sizeCDParameters());
    assertDeepEquals("java.io.Reader", parseReader.getCDParameter(0).getMCType());
    assertEquals("reader", parseReader.getCDParameter(0).getName());

    //parse_string
    ASTCDMethod parseString = getMethodBy("parse_String", parserClass);
    assertTrue(parseString.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_AUTOMATON, parseString.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PUBLIC, parseString.getModifier());
    assertEquals(1, parseString.sizeException());
    assertDeepEquals(ioException, parseString.getException(0));
    assertEquals(1, parseString.sizeCDParameters());
    assertDeepEquals(String.class, parseString.getCDParameter(0).getMCType());
    assertEquals("str", parseString.getCDParameter(0).getName());
  }

  @Test
  public void testParseStateMethods(){
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException"))
        .build();
    List<ASTCDMethod> methods = getMethodsBy("parseState", parserClass);
    assertEquals(2, methods.size());
    //parse with filename
    ASTCDMethod parseFileName = methods.get(0);
    assertTrue(parseFileName.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_STATE, parseFileName.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PUBLIC, parseFileName.getModifier());
    assertEquals(1, parseFileName.sizeException());
    assertDeepEquals(ioException, parseFileName.getException(0));
    assertEquals(1, parseFileName.sizeCDParameters());
    assertDeepEquals(String.class, parseFileName.getCDParameter(0).getMCType());
    assertEquals("fileName", parseFileName.getCDParameter(0).getName());

    //parse with reader
    ASTCDMethod parseReader = methods.get(1);
    assertTrue(parseReader.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_STATE, parseReader.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PUBLIC, parseReader.getModifier());
    assertEquals(1, parseReader.sizeException());
    assertDeepEquals(ioException, parseReader.getException(0));
    assertEquals(1, parseReader.sizeCDParameters());
    assertDeepEquals("java.io.Reader", parseReader.getCDParameter(0).getMCType());
    assertEquals("reader", parseReader.getCDParameter(0).getName());

    //parse_string
    ASTCDMethod parseString = getMethodBy("parse_StringState", parserClass);
    assertTrue(parseString.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_STATE, parseString.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PUBLIC, parseString.getModifier());
    assertEquals(1, parseString.sizeException());
    assertDeepEquals(ioException, parseString.getException(0));
    assertEquals(1, parseString.sizeCDParameters());
    assertDeepEquals(String.class, parseString.getCDParameter(0).getMCType());
    assertEquals("str", parseString.getCDParameter(0).getName());
  }

  @Test
  public void testParseSuperProdMethods(){
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException"))
        .build();
    List<ASTCDMethod> methods = getMethodsBy("parseName", parserClass);
    assertEquals(2, methods.size());
    //parse with filename
    ASTCDMethod parseFileName = methods.get(0);
    assertTrue(parseFileName.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_NAME, parseFileName.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PUBLIC, parseFileName.getModifier());
    assertEquals(1, parseFileName.sizeException());
    assertDeepEquals(ioException, parseFileName.getException(0));
    assertEquals(1, parseFileName.sizeCDParameters());
    assertDeepEquals(String.class, parseFileName.getCDParameter(0).getMCType());
    assertEquals("fileName", parseFileName.getCDParameter(0).getName());

    //parse with reader
    ASTCDMethod parseReader = methods.get(1);
    assertTrue(parseReader.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_NAME, parseReader.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PUBLIC, parseReader.getModifier());
    assertEquals(1, parseReader.sizeException());
    assertDeepEquals(ioException, parseReader.getException(0));
    assertEquals(1, parseReader.sizeCDParameters());
    assertDeepEquals("java.io.Reader", parseReader.getCDParameter(0).getMCType());
    assertEquals("reader", parseReader.getCDParameter(0).getName());

    //parse_string
    ASTCDMethod parseString = getMethodBy("parse_StringName", parserClass);
    assertTrue(parseString.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_NAME, parseString.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PUBLIC, parseString.getModifier());
    assertEquals(1, parseString.sizeException());
    assertDeepEquals(ioException, parseString.getException(0));
    assertEquals(1, parseString.sizeCDParameters());
    assertDeepEquals(String.class, parseString.getCDParameter(0).getMCType());
    assertEquals("str", parseString.getCDParameter(0).getName());
  }

  @Test
  public void testCreateMethods(){
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException"))
        .build();
    List<ASTCDMethod> methods = getMethodsBy("create", parserClass);
    assertEquals(2, methods.size());
    //create with filename
    ASTCDMethod createFileName = methods.get(0);
    assertTrue(createFileName.getMCReturnType().isPresentMCType());
    assertDeepEquals(AUTOMATON_ANTLR_PARSER, createFileName.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PROTECTED, createFileName.getModifier());
    assertEquals(1, createFileName.sizeException());
    assertDeepEquals(ioException, createFileName.getException(0));
    assertEquals(1, createFileName.sizeCDParameters());
    assertDeepEquals(String.class, createFileName.getCDParameter(0).getMCType());
    assertEquals("fileName", createFileName.getCDParameter(0).getName());

    //create with reader
    ASTCDMethod createReader = methods.get(1);
    assertTrue(createReader.getMCReturnType().isPresentMCType());
    assertDeepEquals(AUTOMATON_ANTLR_PARSER, createReader.getMCReturnType().getMCType());
    assertDeepEquals(CDModifier.PROTECTED, createReader.getModifier());
    assertEquals(1, createReader.sizeException());
    assertDeepEquals(ioException, createReader.getException(0));
    assertEquals(1, createReader.sizeCDParameters());
    assertDeepEquals("java.io.Reader", createReader.getCDParameter(0).getMCType());
    assertEquals("reader", createReader.getCDParameter(0).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, parserClass, parserClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

}
