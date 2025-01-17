/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDCLIDecoratorTest extends DecoratorTestCase {
  private ASTCDCompilationUnit cliCD;

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDCompilationUnit decoratedCD;
 
  private ASTCDCompilationUnit originalCD;
  
 
  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    decoratedCD = parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCD = decoratedCD.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCD));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCD);
    ParserService parserService = new ParserService(decoratedCD);
    CLIDecorator cliDecorator = new CLIDecorator(glex, parserService,symbolTableService);
    CDCLIDecorator cdcliDecorator = new CDCLIDecorator(glex ,cliDecorator,parserService);
    this.cliCD = cdcliDecorator.decorate(decoratedCD);
  }
  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(decoratedCD,originalCD);
  }
  @Test
  public void testCDName() {
    assertEquals("Automaton", cliCD.getCDDefinition().getName());
  }
  @Test
  public void testClassCount() {
    assertEquals(1, cliCD.getCDDefinition().getCDClassesList().size());
  }
  @Test
  public void testClassNames() {
    ASTCDClass automatonCli = getClassBy("AutomatonTool", cliCD);
  }
  @Test
  public void testNoInterface() {
    assertTrue( cliCD.getCDDefinition().getCDInterfacesList().isEmpty());
  }

  @Test
  public void testNoEnum() {
    assertTrue(cliCD.getCDDefinition().getCDEnumsList().isEmpty());
  }
  @Test
  public void testPackage() {
    assertEquals(5, cliCD.getCDPackageList().size());
    assertEquals("de", cliCD.getCDPackageList().get(0));
    assertEquals("monticore", cliCD.getCDPackageList().get(1));
    assertEquals("codegen", cliCD.getCDPackageList().get(2));
    assertEquals("ast", cliCD.getCDPackageList().get(3));
    assertEquals("automaton", cliCD.getCDPackageList().get(4));
  }
  @Test
  public void testImports() {
    assertEquals(0, cliCD.getMCImportStatementList().size());
  }
  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    for (ASTCDClass clazz : cliCD.getCDDefinition().getCDClassesList()) {
      StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, clazz, clazz);
      // test parsing
      ParserConfiguration configuration = new ParserConfiguration();
      JavaParser parser = new JavaParser(configuration);
      ParseResult parseResult = parser.parse(sb.toString());
      assertTrue(parseResult.isSuccessful());
    }
  }


}
