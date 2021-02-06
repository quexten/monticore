/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typecd2java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import org.junit.Before;
import org.junit.Test;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;

public class TypeCD2JavaTest extends DecoratorTestCase {

  private ASTCDCompilationUnit cdCompilationUnit;

  @Before
  public void setUp() {
    ICD4AnalysisGlobalScope globalScope = CD4AnalysisMill.globalScope();
    cdCompilationUnit = parse("de", "monticore", "codegen", "ast", "Automaton");
    cdCompilationUnit.setEnclosingScope(globalScope);

    //make types java compatible
    TypeCD2JavaDecorator decorator = new TypeCD2JavaDecorator(globalScope);
    decorator.decorate(cdCompilationUnit);
  }

  @Test
  public void testTypeJavaConformList() {
    assertTrue(cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(1).getMCType() instanceof ASTMCGenericType);
    ASTMCGenericType simpleReferenceType = (ASTMCGenericType) cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(1).getMCType();
    assertFalse(simpleReferenceType.getNameList().isEmpty());
    assertEquals(3, simpleReferenceType.getNameList().size());
    assertEquals("java", simpleReferenceType.getNameList().get(0));
    assertEquals("util", simpleReferenceType.getNameList().get(1));
    assertEquals("List", simpleReferenceType.getNameList().get(2));
  }

  @Test
  public void testTypeJavaConformASTPackage() {
    //test that for AST classes the package is now java conform
    assertTrue(cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(1).getMCType() instanceof ASTMCGenericType);
    ASTMCGenericType listType = (ASTMCGenericType) cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(1).getMCType();
    assertEquals(1, listType.getMCTypeArgumentList().size());
    assertTrue(listType.getMCTypeArgumentList().get(0).getMCTypeOpt().isPresent());
    assertTrue(listType.getMCTypeArgumentList().get(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    ASTMCQualifiedType typeArgument = (ASTMCQualifiedType) listType.getMCTypeArgumentList().get(0).getMCTypeOpt().get();
    assertEquals(7, typeArgument.getNameList().size());
    assertEquals("de", typeArgument.getNameList().get(0));
    assertEquals("monticore", typeArgument.getNameList().get(1));
    assertEquals("codegen", typeArgument.getNameList().get(2));
    assertEquals("ast", typeArgument.getNameList().get(3));
    assertEquals("automaton", typeArgument.getNameList().get(4));
    assertEquals("_ast", typeArgument.getNameList().get(5));
    assertEquals("ASTState", typeArgument.getNameList().get(6));
  }

  @Test
  public void testStringType() {
    //test that types like String are not changed
    assertTrue(cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(0).getMCType() instanceof ASTMCQualifiedType);
    ASTMCQualifiedType simpleReferenceType = (ASTMCQualifiedType) cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(0).getMCType();
    assertFalse(simpleReferenceType.getNameList().isEmpty());
    assertEquals(1, simpleReferenceType.getNameList().size());
    assertEquals("String", simpleReferenceType.getNameList().get(0));
  }
}
