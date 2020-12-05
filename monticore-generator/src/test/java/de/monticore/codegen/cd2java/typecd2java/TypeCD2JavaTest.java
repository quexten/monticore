/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typecd2java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de.monticore.MontiCoreScript;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;

public class TypeCD2JavaTest {

  private ASTCDCompilationUnit cdCompilationUnit;

  @Before
  public void setUp() {
    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/Automaton.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    Grammar_WithConceptsGlobalScope grammar_withConceptsGlobalScope = new Grammar_WithConceptsGlobalScope(modelPath, "mc4");

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    script.createSymbolsFromAST(grammar_withConceptsGlobalScope, grammar.get());
    cdCompilationUnit = script.deriveASTCD(grammar.get(), new GlobalExtensionManagement(),
        grammar_withConceptsGlobalScope);

    cdCompilationUnit.setEnclosingScope(grammar_withConceptsGlobalScope);
    //make types java compatible
    TypeCD2JavaDecorator decorator = new TypeCD2JavaDecorator(grammar_withConceptsGlobalScope);
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
    assertEquals(3, typeArgument.getNameList().size());
    assertEquals("automaton", typeArgument.getNameList().get(0));
    assertEquals("_ast", typeArgument.getNameList().get(1));
    assertEquals("ASTState", typeArgument.getNameList().get(2));
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
