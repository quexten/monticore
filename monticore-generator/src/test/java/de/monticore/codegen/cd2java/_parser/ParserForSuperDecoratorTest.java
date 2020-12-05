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
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserForSuperDecoratorTest extends DecoratorTestCase {

  private ASTCDClass parserClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "parser", "SubAutomaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    ParserForSuperDecorator decorator = new ParserForSuperDecorator(this.glex, new ParserService(decoratedCompilationUnit));
    List<ASTCDClass> parserClassList = decorator.decorate(decoratedCompilationUnit);

    assertEquals(1, parserClassList.size());
    parserClass = parserClassList.get(0);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testMillName() {
    assertEquals("AutomatonParserForSubAutomaton", parserClass.getName());
  }

  @Test
  public void testSuperClass() {
    assertTrue(parserClass.isPresentSuperclass());
    assertDeepEquals("de.monticore.codegen.parser.automaton._parser.AutomatonParser", parserClass.getSuperclass());
  }

  @Test
  public void testNoInterfaces() {
    assertEquals(0, parserClass.sizeInterface());
  }

  @Test
  public void testNoAttributes() {
    assertEquals(0, parserClass.sizeCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(12, parserClass.sizeCDMethods());
  }

  @Test
  public void testParseTransitionMethods(){
    List<ASTCDMethod> methods = getMethodsBy("parseTransition", parserClass);
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException"))
        .build();

    assertEquals(2, methods.size());

    ASTCDMethod parse = methods.get(0);
    assertDeepEquals(CDModifier.PUBLIC, parse.getModifier());
    assertTrue(parse.getMCReturnType().isPresentMCType());
    assertOptionalOf("de.monticore.codegen.parser.automaton._ast.ASTTransition", parse.getMCReturnType().getMCType());
    assertEquals(1, parse.sizeCDParameters());
    assertEquals("fileName", parse.getCDParameter(0).getName());
    assertDeepEquals(String.class, parse.getCDParameter(0).getMCType());
    assertEquals(1, parse.sizeException());
    assertDeepEquals(ioException, parse.getException(0));

    ASTCDMethod parseReader = methods.get(1);
    assertDeepEquals(CDModifier.PUBLIC, parseReader.getModifier());
    assertTrue(parseReader.getMCReturnType().isPresentMCType());
    assertOptionalOf("de.monticore.codegen.parser.automaton._ast.ASTTransition", parseReader.getMCReturnType().getMCType());
    assertEquals(1, parseReader.sizeCDParameters());
    assertEquals("reader", parseReader.getCDParameter(0).getName());
    assertDeepEquals("java.io.Reader", parseReader.getCDParameter(0).getMCType());
    assertEquals(1, parseReader.sizeException());
    assertDeepEquals(ioException, parseReader.getException(0));
  }

  @Test
  public void testParseOverriddenStateMethods(){
    List<ASTCDMethod> methods = getMethodsBy("parseState", parserClass);
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException"))
        .build();

    assertEquals(2, methods.size());

    ASTCDMethod parse = methods.get(0);
    assertDeepEquals(CDModifier.PUBLIC, parse.getModifier());
    assertTrue(parse.getMCReturnType().isPresentMCType());
    assertOptionalOf("de.monticore.codegen.parser.automaton._ast.ASTState", parse.getMCReturnType().getMCType());
    assertEquals(1, parse.sizeCDParameters());
    assertEquals("fileName", parse.getCDParameter(0).getName());
    assertDeepEquals(String.class, parse.getCDParameter(0).getMCType());
    assertEquals(1, parse.sizeException());
    assertDeepEquals(ioException, parse.getException(0));

    ASTCDMethod parseReader = methods.get(1);
    assertDeepEquals(CDModifier.PUBLIC, parseReader.getModifier());
    assertTrue(parseReader.getMCReturnType().isPresentMCType());
    assertOptionalOf("de.monticore.codegen.parser.automaton._ast.ASTState", parseReader.getMCReturnType().getMCType());
    assertEquals(1, parseReader.sizeCDParameters());
    assertEquals("reader", parseReader.getCDParameter(0).getName());
    assertDeepEquals("java.io.Reader", parseReader.getCDParameter(0).getMCType());
    assertEquals(1, parseReader.sizeException());
    assertDeepEquals(ioException, parseReader.getException(0));
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
