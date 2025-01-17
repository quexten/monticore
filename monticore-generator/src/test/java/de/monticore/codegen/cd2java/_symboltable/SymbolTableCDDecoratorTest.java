/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.*;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDecorator;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDelegatorDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.ScopeDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.SymbolDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.Symbols2JsonDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.*;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolsurrogatemutator.MandatoryMutatorSymbolSurrogateDecorator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolTableCDDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedASTCompilationUnit;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDCompilationUnit originalASTCompilationUnit;

  private ASTCDCompilationUnit originalSymbolCompilationUnit;

  private ASTCDCompilationUnit originalScopeCompilationUnit;

  private ASTCDCompilationUnit symTabCD;

  private ASTCDCompilationUnit symTabCDWithHC;

  private ASTCDCompilationUnit symTabCDComponent;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    MCPath targetPath = Mockito.mock(MCPath.class);

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    decoratedASTCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalASTCompilationUnit = decoratedASTCompilationUnit.deepClone();
    originalSymbolCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    originalScopeCompilationUnit = decoratedScopeCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new AbstractService(decoratedASTCompilationUnit));

    SymbolTableService symbolTableService = new SymbolTableService(decoratedASTCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedASTCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, symbolTableService);
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex, symbolTableService);

    SymbolDecorator symbolDecorator = new SymbolDecorator(glex, symbolTableService, visitorService, methodDecorator);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, accessorDecorator, symbolTableService);
    SymbolBuilderDecorator symbolBuilderDecorator = new SymbolBuilderDecorator(glex, symbolTableService, builderDecorator);
    ScopeInterfaceDecorator scopeInterfaceDecorator = new ScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopeClassDecorator scopeClassDecorator = new ScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator = new GlobalScopeInterfaceDecorator(glex, symbolTableService, methodDecorator);
    GlobalScopeClassDecorator globalScopeClassDecorator = new GlobalScopeClassDecorator(glex, symbolTableService, methodDecorator);
    ArtifactScopeInterfaceDecorator artifactScopeInterfaceDecorator = new ArtifactScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ArtifactScopeClassDecorator artifactScopeDecorator = new ArtifactScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolSurrogateDecorator symbolReferenceDecorator = new SymbolSurrogateDecorator(glex, symbolTableService, methodDecorator, new MandatoryMutatorSymbolSurrogateDecorator(glex));
    SymbolSurrogateBuilderDecorator symbolReferenceBuilderDecorator = new SymbolSurrogateBuilderDecorator(glex, symbolTableService, accessorDecorator);
    CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator = new CommonSymbolInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolResolverInterfaceDecorator symbolResolverInterfaceDecorator = new SymbolResolverInterfaceDecorator(glex, symbolTableService);
    SymbolDeSerDecorator symbolDeSerDecorator = new SymbolDeSerDecorator(glex, symbolTableService, new MCPath());
    ScopeDeSerDecorator scopeDeSerDecorator = new ScopeDeSerDecorator(glex, symbolTableService, methodDecorator, visitorService, new MCPath());
    Symbols2JsonDecorator symbolTablePrinterDecorator = new Symbols2JsonDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopesGenitorDecorator scopesGenitorDecorator = new ScopesGenitorDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopesGenitorDelegatorDecorator scopesGenitorDelegatorDecorator = new ScopesGenitorDelegatorDecorator(glex, symbolTableService, visitorService);

    SymbolTableCDDecorator symbolTableCDDecorator = new SymbolTableCDDecorator(glex, targetPath, symbolTableService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeInterfaceDecorator, scopeClassDecorator,
        globalScopeInterfaceDecorator, globalScopeClassDecorator,
        artifactScopeInterfaceDecorator, artifactScopeDecorator,
        commonSymbolInterfaceDecorator,
        symbolResolverInterfaceDecorator,
         symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator, scopesGenitorDecorator, scopesGenitorDelegatorDecorator);

    // cd with no handcoded classes
    this.symTabCD = symbolTableCDDecorator.decorate(decoratedASTCompilationUnit, decoratedSymbolCompilationUnit, decoratedScopeCompilationUnit);

    // cd with handcoded classes and component and no start prod
    this.symTabCDWithHC = symbolTableCDDecorator.decorate(decoratedASTCompilationUnit, decoratedSymbolCompilationUnit, decoratedScopeCompilationUnit);

    SymbolTableService mockService = Mockito.spy(new SymbolTableService(decoratedASTCompilationUnit));
    SymbolTableCDDecorator mockDecorator = new SymbolTableCDDecorator(glex, targetPath, mockService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeInterfaceDecorator, scopeClassDecorator,
        globalScopeInterfaceDecorator, globalScopeClassDecorator,
        artifactScopeInterfaceDecorator, artifactScopeDecorator,
        commonSymbolInterfaceDecorator,
        symbolResolverInterfaceDecorator,
        symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator, scopesGenitorDecorator, scopesGenitorDelegatorDecorator);
    Mockito.doReturn(false).when(mockService).hasStartProd(Mockito.any(ASTCDDefinition.class));
    Mockito.doReturn(true).when(mockService).hasComponentStereotype(Mockito.any(ASTModifier.class));
    this.symTabCDComponent = mockDecorator.decorate(decoratedASTCompilationUnit, decoratedSymbolCompilationUnit, decoratedScopeCompilationUnit);
  }


  @Test
  public void testCompilationUnitNotChanged() {
    // TODO NJ: Remove the following loc as soon as stereotype deep equals is fixed
    ASTCDElement clazz = ((ASTCDPackage) originalASTCompilationUnit.getCDDefinition().getCDElement(0)).getCDElement(6);
    ((ASTCDClass) clazz).getModifier().getStereotype().getValues(0).getValue();

    assertDeepEquals(originalASTCompilationUnit, decoratedASTCompilationUnit);
    assertDeepEquals(originalSymbolCompilationUnit, decoratedSymbolCompilationUnit);
    assertDeepEquals(originalScopeCompilationUnit, decoratedScopeCompilationUnit);
  }

  @Test
  public void testCDName() {
    assertEquals("Automaton", symTabCD.getCDDefinition().getName());
  }

  @Test
  public void testClassCount() {
    assertEquals(27, symTabCD.getCDDefinition().getCDClassesList().size());
  }

  @Test
  public void testClassNames() {
    ASTCDClass automatonSymbol = getClassBy("AutomatonSymbol", symTabCD);
    ASTCDClass stateSymbol = getClassBy("StateSymbol", symTabCD);
    ASTCDClass fooSymbol = getClassBy("FooSymbol", symTabCD);
    ASTCDClass automatonSymbolBuilder = getClassBy("AutomatonSymbolBuilder", symTabCD);
    ASTCDClass stateSymbolBuilder = getClassBy("StateSymbolBuilder", symTabCD);
    ASTCDClass fooSymbolBuilder = getClassBy("FooSymbolBuilder", symTabCD);
    ASTCDClass automatonScope = getClassBy("AutomatonScope", symTabCD);
    ASTCDClass automatonSymbolSurrogate = getClassBy("AutomatonSymbolSurrogate", symTabCD);
    ASTCDClass stateSymbolSurrogate = getClassBy("StateSymbolSurrogate", symTabCD);
    ASTCDClass fooSymbolSurrogate = getClassBy("FooSymbolSurrogate", symTabCD);
    ASTCDClass automatonSymbolSurrogateBuilder = getClassBy("AutomatonSymbolSurrogateBuilder", symTabCD);
    ASTCDClass stateSymbolSurrogateBuilder = getClassBy("StateSymbolSurrogateBuilder", symTabCD);
    ASTCDClass fooSymbolSurrogateBuilder = getClassBy("FooSymbolSurrogateBuilder", symTabCD);
    ASTCDClass automatonGlobalScope = getClassBy("AutomatonGlobalScope", symTabCD);
    ASTCDClass automatonArtifactScope = getClassBy("AutomatonArtifactScope", symTabCD);
    ASTCDClass automatonDeSer = getClassBy("AutomatonDeSer", symTabCD);
    ASTCDClass automatonSymbolDeSer = getClassBy("AutomatonSymbolDeSer", symTabCD);
    ASTCDClass stateSymbolDeSer = getClassBy("StateSymbolDeSer", symTabCD);
    ASTCDClass fooSymbolDeSer = getClassBy("FooSymbolDeSer", symTabCD);
    ASTCDClass automatonSymbolTablePrinter = getClassBy("AutomatonSymbols2Json", symTabCD);
    ASTCDClass automatonScopesGenitor = getClassBy("AutomatonScopesGenitor", symTabCD);
    ASTCDClass automatonScopesGenitorDelegator = getClassBy("AutomatonScopesGenitorDelegator", symTabCD);
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(7, symTabCD.getCDDefinition().getCDInterfacesList().size());
  }

  @Test
  public void testInterfaces() {
    ASTCDInterface iAutomatonScope = getInterfaceBy("IAutomatonScope", symTabCD);
    ASTCDInterface iAutomatonGlobalScope = getInterfaceBy("IAutomatonGlobalScope", symTabCD);
    ASTCDInterface iAutomatonArtifactScope = getInterfaceBy("IAutomatonArtifactScope", symTabCD);
    ASTCDInterface iCommonAutomatonSymbol = getInterfaceBy("ICommonAutomatonSymbol", symTabCD);
    ASTCDInterface iAutomatonSymbolResolver = getInterfaceBy("IAutomatonSymbolResolver", symTabCD);
    ASTCDInterface iStateSymbolResolver = getInterfaceBy("IStateSymbolResolver", symTabCD);
    ASTCDInterface symbolInterfaceSymbol = getInterfaceBy("ISymbolInterfaceSymbolResolver", symTabCD);
  }

  @Test
  public void testNoEnum() {
    assertTrue(symTabCD.getCDDefinition().getCDEnumsList().isEmpty());
  }

  @Test
  public void testPackage() {
    assertEquals(6, symTabCD.getCDPackageList().size());
    assertEquals("de", symTabCD.getCDPackageList().get(0));
    assertEquals("monticore", symTabCD.getCDPackageList().get(1));
    assertEquals("codegen", symTabCD.getCDPackageList().get(2));
    assertEquals("symboltable", symTabCD.getCDPackageList().get(3));
    assertEquals("automaton", symTabCD.getCDPackageList().get(4));
    assertEquals("_symboltable", symTabCD.getCDPackageList().get(5));

  }

  @Test
  public void testImports() {
    assertEquals(0, symTabCD.getMCImportStatementList().size());
  }


  @Test
  public void testCDNameWithHC() {
    assertEquals("Automaton", symTabCDWithHC.getCDDefinition().getName());
  }

  @Test
  public void testClassCountWithHC() {
    assertEquals(27, symTabCDWithHC.getCDDefinition().getCDClassesList().size());
  }

  @Test
  public void testClassNamesWithHC() {
    ASTCDClass automatonSymbol = getClassBy("AutomatonSymbol", symTabCDWithHC);
    ASTCDClass stateSymbol = getClassBy("StateSymbol", symTabCDWithHC);
    ASTCDClass fooSymbol = getClassBy("FooSymbol", symTabCDWithHC);
    ASTCDClass automatonSymbolBuilder = getClassBy("AutomatonSymbolBuilder", symTabCDWithHC);
    ASTCDClass stateSymbolBuilder = getClassBy("StateSymbolBuilder", symTabCDWithHC);
    ASTCDClass fooSymbolBuilder = getClassBy("FooSymbolBuilder", symTabCDWithHC);
    ASTCDClass automatonScope = getClassBy("AutomatonScope", symTabCDWithHC);
    ASTCDClass automatonSymbolSurrogate = getClassBy("AutomatonSymbolSurrogate", symTabCDWithHC);
    ASTCDClass stateSymbolSurrogate = getClassBy("StateSymbolSurrogate", symTabCDWithHC);
    ASTCDClass fooSymbolSurrogate = getClassBy("FooSymbolSurrogate", symTabCDWithHC);
    ASTCDClass automatonSymbolSurrogateBuilder = getClassBy("AutomatonSymbolSurrogateBuilder", symTabCDWithHC);
    ASTCDClass stateSymbolSurrogateBuilder = getClassBy("StateSymbolSurrogateBuilder", symTabCDWithHC);
    ASTCDClass fooSymbolSurrogateBuilder = getClassBy("FooSymbolSurrogateBuilder", symTabCDWithHC);
    ASTCDClass automatonGlobalScope = getClassBy("AutomatonGlobalScope", symTabCDWithHC);
    ASTCDClass automatonArtifactScope = getClassBy("AutomatonArtifactScope", symTabCDWithHC);
    ASTCDClass automatonScopesGenitor = getClassBy("AutomatonScopesGenitor", symTabCDWithHC);
    ASTCDClass automatonScopesGenitorDelegator = getClassBy("AutomatonScopesGenitorDelegator", symTabCDWithHC);
  }

  @Test
  public void testInterfaceCountWithHC() {
    assertEquals(7, symTabCDWithHC.getCDDefinition().getCDInterfacesList().size());
  }

  @Test
  public void testInterfacesWithHC() {
    ASTCDInterface iAutomatonScope = getInterfaceBy("IAutomatonScope", symTabCDWithHC);
    ASTCDInterface iAutomatonGlobalScope = getInterfaceBy("IAutomatonGlobalScope", symTabCDWithHC);
    ASTCDInterface iAutomatonArtifactScope = getInterfaceBy("IAutomatonArtifactScope", symTabCDWithHC);
    ASTCDInterface iCommonAutomatonSymbol = getInterfaceBy("ICommonAutomatonSymbol", symTabCDWithHC);
    ASTCDInterface iAutomatonSymbolResolver = getInterfaceBy("IAutomatonSymbolResolver", symTabCDWithHC);
    ASTCDInterface iStateSymbolResolver = getInterfaceBy("IStateSymbolResolver", symTabCDWithHC);
    ASTCDInterface symbolInterfaceSymbol = getInterfaceBy("ISymbolInterfaceSymbolResolver", symTabCDComponent);
  }

  @Test
  public void testNoEnumWithHC() {
    assertTrue(symTabCDWithHC.getCDDefinition().getCDEnumsList().isEmpty());
  }

  @Test
  public void testCDNameComponent() {
    assertEquals("Automaton", symTabCDComponent.getCDDefinition().getName());
  }

  @Test
  public void testClassCountComponent() {
    assertEquals(27, symTabCDComponent.getCDDefinition().getCDClassesList().size());
  }

  @Test
  public void testClassNamesComponent() {
    ASTCDClass automatonSymbol = getClassBy("AutomatonSymbol", symTabCDComponent);
    ASTCDClass stateSymbol = getClassBy("StateSymbol", symTabCDComponent);
    ASTCDClass fooSymbol = getClassBy("FooSymbol", symTabCDComponent);
    ASTCDClass automatonSymbolBuilder = getClassBy("AutomatonSymbolBuilder", symTabCDComponent);
    ASTCDClass stateSymbolBuilder = getClassBy("StateSymbolBuilder", symTabCDComponent);
    ASTCDClass fooSymbolBuilder = getClassBy("FooSymbolBuilder", symTabCDComponent);
    ASTCDClass automatonScope = getClassBy("AutomatonScope", symTabCDComponent);
    ASTCDClass automatonSymbolSurrogate = getClassBy("AutomatonSymbolSurrogate", symTabCDComponent);
    ASTCDClass stateSymbolSurrogate = getClassBy("StateSymbolSurrogate", symTabCDComponent);
    ASTCDClass fooSymbolSurrogate = getClassBy("FooSymbolSurrogate", symTabCDComponent);
    ASTCDClass automatonSymbolSurrogateBuilder = getClassBy("AutomatonSymbolSurrogateBuilder", symTabCDComponent);
    ASTCDClass stateSymbolSurrogateBuilder = getClassBy("StateSymbolSurrogateBuilder", symTabCDComponent);
    ASTCDClass fooSymbolSurrogateBuilder = getClassBy("FooSymbolSurrogateBuilder", symTabCDComponent);
  }

  @Test
  public void testInterfaceCountComponent() {
    assertEquals(7, symTabCDComponent.getCDDefinition().getCDInterfacesList().size());
  }

  @Test
  public void testInterfacesComponent() {
    ASTCDInterface iAutomatonScope = getInterfaceBy("IAutomatonScope", symTabCDComponent);
    ASTCDInterface iAutomatonGlobalScope = getInterfaceBy("IAutomatonGlobalScope", symTabCDComponent);
    ASTCDInterface iAutomatonArtifactScope = getInterfaceBy("IAutomatonArtifactScope", symTabCDComponent);
    ASTCDInterface iCommonAutomatonSymbol = getInterfaceBy("ICommonAutomatonSymbol", symTabCDComponent);
    ASTCDInterface iAutomatonSymbolResolver = getInterfaceBy("IAutomatonSymbolResolver", symTabCDComponent);
    ASTCDInterface iStateSymbolResolver = getInterfaceBy("IStateSymbolResolver", symTabCDComponent);
    ASTCDInterface symbolInterfaceSymbol = getInterfaceBy("ISymbolInterfaceSymbolResolver", symTabCDComponent);
  }

  @Test
  public void testNoEnumComponent() {
    assertTrue(symTabCDComponent.getCDDefinition().getCDEnumsList().isEmpty());
  }
}
