/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import com.google.common.collect.Lists;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill;
import de.monticore.statements.testmcvardeclarationstatements._ast.ASTRootVarDeclaration;
import de.monticore.statements.testmcvardeclarationstatements._cocos.TestMCVarDeclarationStatementsCoCoChecker;
import de.monticore.statements.testmcvardeclarationstatements._parser.TestMCVarDeclarationStatementsParser;
import de.monticore.statements.testmcvardeclarationstatements._visitor.TestMCVarDeclarationStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.DeriveSymTypeOfCombineExpressionsDelegator;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class VarDeclarationInitializationHasCorrectTypeTest {

  private static final TestMCVarDeclarationStatementsCoCoChecker checker =
    new TestMCVarDeclarationStatementsCoCoChecker();

  private static final TestMCVarDeclarationStatementsParser parser = new TestMCVarDeclarationStatementsParser();

  @BeforeClass
  public static void setUp(){
    Log.init();
    //LogStub.init();
    Log.enableFailQuick(false);
    TestMCVarDeclarationStatementsMill.reset();
    TestMCVarDeclarationStatementsMill.init();
    checker.setTraverser(TestMCVarDeclarationStatementsMill.traverser());

    checker.addCoCo(new VarDeclarationInitializationHasCorrectType(new DeriveSymTypeOfCombineExpressionsDelegator()));
  }

  @Before
  public void resetSymbolTable() {
    TestMCVarDeclarationStatementsMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    addMyTypeToGlobalScope();
    addStringToGlobalScope();
  }

  protected static void addMyTypeToGlobalScope() {
    OOTypeSymbol type = TestMCVarDeclarationStatementsMill.oOTypeSymbolBuilder()
      .setName("MyType")
      .setSpannedScope(TestMCVarDeclarationStatementsMill.scope())
      .build();
    TestMCVarDeclarationStatementsMill.globalScope().add(type);
    TestMCVarDeclarationStatementsMill.globalScope().addSubScope(type.getSpannedScope());
  }

  protected static void addStringToGlobalScope() {
    OOTypeSymbol type = TestMCVarDeclarationStatementsMill.oOTypeSymbolBuilder()
      .setName("String")
      .setSpannedScope(TestMCVarDeclarationStatementsMill.scope())
      .build();
    TestMCVarDeclarationStatementsMill.globalScope().add(type);
    TestMCVarDeclarationStatementsMill.globalScope().addSubScope(type.getSpannedScope());
  }

  protected void checkExpectedErrors(ASTRootVarDeclaration decl, List<String> expectedErrorCodes) {
    Log.getFindings().clear();
    TestMCVarDeclarationStatementsMill.scopesGenitorDelegator().createFromAST(decl);
    TestMCVarDeclarationStatementsTraverser completerTraverser = TestMCVarDeclarationStatementsMill.traverser();
    completerTraverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());
    decl.accept(completerTraverser);
    // We must manually set a name for the ArtifactScope. Else we get an exception.
    decl.getEnclosingScope().setName("Foo");

    // When
    checker.checkAll(decl);

    // Then
    List<String> actualErrors = Log.getFindings().stream()
      .filter(Finding::isError)
      .map(err -> err.getMsg().split(" ")[0])
      .collect(Collectors.toList());
    assertEquals(expectedErrorCodes, actualErrors);
  }

  @Test
  public void testValidMultiVarDeclaration() throws IOException {
    // Given
    String multiVarDeclaration = "int a = 10, b, c = -12;";
    List<String> expectedErrors = new ArrayList<>();
    ASTRootVarDeclaration astDecl = parser.parse_StringRootVarDeclaration(multiVarDeclaration).get();

    // When & Then
    checkExpectedErrors(astDecl, expectedErrors);
  }

  @Test
  public void testInvalidMultiVarDeclaration() throws IOException {
    // Given
    String multiVarDeclaration = "int a = \"oh no\", b = 10, c, d = \"no no no\";";
    List<String> expectedErrors = Lists.newArrayList(
      VarDeclarationInitializationHasCorrectType.ERROR_CODE,
      VarDeclarationInitializationHasCorrectType.ERROR_CODE
    );
    ASTRootVarDeclaration astDecl = parser.parse_StringRootVarDeclaration(multiVarDeclaration).get();

    // When & Then
    checkExpectedErrors(astDecl, expectedErrors);
  }

  @Test
  public void testInvalidMultiVarDeclarationWithTypeReference() throws IOException {
    // Given
    String multiVarDeclaration = "int a = 3, b, c = MyType, d = \"no no no\";";
    List<String> expectedErrors = Lists.newArrayList(
      VarDeclarationInitializationHasCorrectType.TYPE_REF_ASSIGNMENT_ERROR_CODE,
      VarDeclarationInitializationHasCorrectType.ERROR_CODE
    );
    ASTRootVarDeclaration astDecl = parser.parse_StringRootVarDeclaration(multiVarDeclaration).get();

    // When & Then
    checkExpectedErrors(astDecl, expectedErrors);
  }
}
