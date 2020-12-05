/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java._ast.ast_class.ASTDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._cocos.CoCoInterfaceDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.symbol.*;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolsurrogatemutator.MandatoryMutatorSymbolSurrogateDecorator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeprecatedTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  private ASTCDClass clazzA;
  private ASTCDInterface interfaceI;
  private ASTCDEnum enumE;

  private ASTDecorator astDecorator;
  private ASTBuilderDecorator astBuilderDecorator;
  private CoCoInterfaceDecorator coCoInterfaceDecorator;
  private SymbolDecorator symbolDecorator;
  private SymbolBuilderDecorator symbolBuilderDecorator;
  private SymbolSurrogateDecorator symbolSurrogateDecorator;
  private SymbolSurrogateBuilderDecorator symbolSurrogateBuilderDecorator;
  private SymbolResolverInterfaceDecorator symbolResolverInterfaceDecorator;
  private EnumDecorator enumDecorator;
  private ASTInterfaceDecorator astInterfaceDecorator;

  private ASTCDCompilationUnit compilationUnit;

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    compilationUnit = this.parse("de", "monticore", "codegen", "deprecated", "DeprecatedProds");

    this.glex.setGlobalValue("service", new AbstractService(compilationUnit));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    clazzA = getClassBy("A", compilationUnit);
    interfaceI = getInterfaceBy("I", compilationUnit);
    enumE = getEnumBy("E", compilationUnit);

    SymbolTableService symbolTableService = new SymbolTableService(compilationUnit);
    ASTService astService = new ASTService(compilationUnit);
    VisitorService visitorService = new VisitorService(compilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, astService);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    astDecorator = new ASTDecorator(this.glex, astService, visitorService, new NodeFactoryService(compilationUnit),
        astSymbolDecorator, astScopeDecorator, methodDecorator,
        symbolTableService);

    AccessorDecorator accessorDecorator = new AccessorDecorator(glex, astService);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, accessorDecorator, astService);
    astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator, astService);

    coCoInterfaceDecorator = new CoCoInterfaceDecorator(glex, new CoCoService(compilationUnit), astService);

    symbolDecorator = new SymbolDecorator(this.glex, symbolTableService, visitorService, methodDecorator);

    symbolBuilderDecorator = new SymbolBuilderDecorator(glex, symbolTableService, builderDecorator);

    symbolSurrogateDecorator = new SymbolSurrogateDecorator(this.glex, symbolTableService, methodDecorator,
        new MandatoryMutatorSymbolSurrogateDecorator(glex));

    symbolSurrogateBuilderDecorator = new SymbolSurrogateBuilderDecorator(this.glex, symbolTableService,
        new AccessorDecorator(glex, symbolTableService));

    symbolResolverInterfaceDecorator = new SymbolResolverInterfaceDecorator(this.glex, symbolTableService);

    astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService, astSymbolDecorator, astScopeDecorator, methodDecorator);

    enumDecorator = new EnumDecorator(glex, accessorDecorator, astService);
  }

  @Test
  public void testASTADeprecated() {
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(clazzA.getName())
        .setModifier(clazzA.getModifier())
        .build();
    ASTCDClass astA = astDecorator.decorate(clazzA, changedClass);
    assertTrue(astA.isPresentModifier());
    assertTrue(astA.getModifier().isPresentStereotype());

    assertEquals(2, astA.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", astA.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astA.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentA", astA.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testASTABuilderDeprecated() {
    ASTCDClass astABuilder = astBuilderDecorator.decorate(clazzA);

    assertTrue(astABuilder.isPresentModifier());
    assertTrue(astABuilder.getModifier().isPresentStereotype());

    assertEquals(1, astABuilder.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", astABuilder.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astABuilder.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentA", astABuilder.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testACoCoInterfaceDeprecated() {
    List<ASTCDInterface> cocoInterfaceList = coCoInterfaceDecorator.decorate(compilationUnit.getCDDefinition());
    Optional<ASTCDInterface> astaCoCo = cocoInterfaceList.stream().filter(c -> c.getName().equals("DeprecatedProdsACoCo")).findFirst();
    assertTrue(astaCoCo.isPresent());
    assertTrue(astaCoCo.get().isPresentModifier());
    assertTrue(astaCoCo.get().getModifier().isPresentStereotype());

    assertEquals(1, astaCoCo.get().getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", astaCoCo.get().getModifier().getStereotype().getValue(0).getName());
    assertTrue(astaCoCo.get().getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentA", astaCoCo.get().getModifier().getStereotype().getValue(0).getValue());
  }


  @Test
  public void testASymbolDeprecated() {
    ASTCDClass symbolA = symbolDecorator.decorate(clazzA);
    assertTrue(symbolA.isPresentModifier());
    assertTrue(symbolA.getModifier().isPresentStereotype());

    assertEquals(1, symbolA.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", symbolA.getModifier().getStereotype().getValue(0).getName());
    assertTrue(symbolA.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentA", symbolA.getModifier().getStereotype().getValue(0).getValue());
  }


  @Test
  public void testASymbolBuilderDeprecated() {
    ASTCDClass symbolBuilderA = symbolBuilderDecorator.decorate(clazzA);
    assertTrue(symbolBuilderA.isPresentModifier());
    assertTrue(symbolBuilderA.getModifier().isPresentStereotype());

    assertEquals(1, symbolBuilderA.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", symbolBuilderA.getModifier().getStereotype().getValue(0).getName());
    assertTrue(symbolBuilderA.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentA", symbolBuilderA.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testASymbolLoaderDeprecated() {
    ASTCDClass symbolLoaderA = symbolSurrogateDecorator.decorate(clazzA);
    assertTrue(symbolLoaderA.isPresentModifier());
    assertTrue(symbolLoaderA.getModifier().isPresentStereotype());

    assertEquals(1, symbolLoaderA.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", symbolLoaderA.getModifier().getStereotype().getValue(0).getName());
    assertTrue(symbolLoaderA.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentA", symbolLoaderA.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testASymbolLoaderBuilderDeprecated() {
    ASTCDClass symbolLoaderBuilderA = symbolSurrogateBuilderDecorator.decorate(clazzA);
    assertTrue(symbolLoaderBuilderA.isPresentModifier());
    assertTrue(symbolLoaderBuilderA.getModifier().isPresentStereotype());

    assertEquals(1, symbolLoaderBuilderA.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", symbolLoaderBuilderA.getModifier().getStereotype().getValue(0).getName());
    assertTrue(symbolLoaderBuilderA.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentA", symbolLoaderBuilderA.getModifier().getStereotype().getValue(0).getValue());
  }


  @Test
  public void testASymbolResolverInterfaceDeprecated() {
    ASTCDInterface resolverA = symbolResolverInterfaceDecorator.decorate(clazzA);
    assertTrue(resolverA.isPresentModifier());
    assertTrue(resolverA.getModifier().isPresentStereotype());

    assertEquals(1, resolverA.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", resolverA.getModifier().getStereotype().getValue(0).getName());
    assertTrue(resolverA.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentA", resolverA.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testASTIDeprecated() {
    ASTCDInterface changedClass = CD4AnalysisMill.cDInterfaceBuilder().setName(interfaceI.getName())
        .setModifier(interfaceI.getModifier())
        .build();
    ASTCDInterface astA = astInterfaceDecorator.decorate(interfaceI, changedClass);
    assertTrue(astA.isPresentModifier());
    assertTrue(astA.getModifier().isPresentStereotype());

    assertEquals(2, astA.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", astA.getModifier().getStereotype().getValue(0).getName());
    assertTrue(astA.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentI", astA.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testICoCoInterfaceDeprecated() {
    List<ASTCDInterface> cocoInterfaceList = coCoInterfaceDecorator.decorate(compilationUnit.getCDDefinition());
    Optional<ASTCDInterface> astaCoCo = cocoInterfaceList.stream().filter(c -> c.getName().equals("DeprecatedProdsICoCo")).findFirst();
    assertTrue(astaCoCo.isPresent());
    assertTrue(astaCoCo.get().isPresentModifier());
    assertTrue(astaCoCo.get().getModifier().isPresentStereotype());

    assertEquals(1, astaCoCo.get().getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", astaCoCo.get().getModifier().getStereotype().getValue(0).getName());
    assertTrue(astaCoCo.get().getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentI", astaCoCo.get().getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testISymbolLoaderBuilderDeprecated() {
    ASTCDClass symbolLoaderBuilderA = symbolSurrogateBuilderDecorator.decorate(interfaceI);
    assertTrue(symbolLoaderBuilderA.isPresentModifier());
    assertTrue(symbolLoaderBuilderA.getModifier().isPresentStereotype());

    assertEquals(1, symbolLoaderBuilderA.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", symbolLoaderBuilderA.getModifier().getStereotype().getValue(0).getName());
    assertTrue(symbolLoaderBuilderA.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentI", symbolLoaderBuilderA.getModifier().getStereotype().getValue(0).getValue());
  }


  @Test
  public void testISymbolResolverInterfaceDeprecated() {
    ASTCDInterface resolverA = symbolResolverInterfaceDecorator.decorate(interfaceI);
    assertTrue(resolverA.isPresentModifier());
    assertTrue(resolverA.getModifier().isPresentStereotype());

    assertEquals(1, resolverA.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", resolverA.getModifier().getStereotype().getValue(0).getName());
    assertTrue(resolverA.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("CommentI", resolverA.getModifier().getStereotype().getValue(0).getValue());
  }

  @Test
  public void testASTEDeprecated() {
    ASTCDEnum astcdEnum = enumDecorator.decorate(enumE);
    assertTrue(astcdEnum.isPresentModifier());
    assertTrue(astcdEnum.getModifier().isPresentStereotype());

    assertEquals(1, astcdEnum.getModifier().getStereotype().sizeValue());
    assertEquals("deprecated", astcdEnum.getModifier().getStereotype().getValue(0).getName());
  }
}
