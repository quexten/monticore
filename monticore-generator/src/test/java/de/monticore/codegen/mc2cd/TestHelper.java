/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import de.monticore.MontiCoreScript;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.scopeTransl.MC2CDScopeTranslation;
import de.monticore.codegen.mc2cd.symbolTransl.MC2CDSymbolTranslation;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsSymbolTableCreatorDelegator;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import parser.MCGrammarParser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class TestHelper {

  /**
   * Convenience bundling of parsing and transformation
   *
   * @param model the .mc4 file that is to be parsed and transformed
   * @return the root node of the resulting CD AST
   */

  public static Optional<ASTCDCompilationUnit> parseAndTransformForSymbol(Path model) {
    Optional<ASTMCGrammar> grammar = MCGrammarParser.parse(model);
    if (!grammar.isPresent()) {
      return Optional.empty();
    }
    Grammar_WithConceptsGlobalScope symbolTable = createGlobalScope(new ModelPath(Paths.get("src/test/resources")));
    Grammar_WithConceptsSymbolTableCreatorDelegator stc = Grammar_WithConceptsMill.grammar_WithConceptsSymbolTableCreatorDelegator();
    stc.createFromAST(grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDSymbolTranslation().apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }

  public static Optional<ASTCDCompilationUnit> parseAndTransformForScope(Path model) {
    Optional<ASTMCGrammar> grammar = MCGrammarParser.parse(model);
    if (!grammar.isPresent()) {
      return Optional.empty();
    }
    MontiCoreScript mc = new MontiCoreScript();
    Grammar_WithConceptsGlobalScope symbolTable = createGlobalScope(new ModelPath(Paths.get("src/test/resources")));
    mc.createSymbolsFromAST(symbolTable, grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDScopeTranslation().apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }

  public static Optional<ASTCDCompilationUnit> parseAndTransform(Path model) {
    Optional<ASTMCGrammar> grammar = MCGrammarParser.parse(model);
    if (!grammar.isPresent()) {
      return Optional.empty();
    }
    MontiCoreScript mc = new MontiCoreScript();
    Grammar_WithConceptsGlobalScope symbolTable = createGlobalScope(new ModelPath(Paths.get("src/test/resources")));
    mc.createSymbolsFromAST(symbolTable, grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDTransformation(
        new GlobalExtensionManagement()).apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }
  
  public static Grammar_WithConceptsGlobalScope createGlobalScope(ModelPath modelPath) {
    IGrammar_WithConceptsGlobalScope scope = Grammar_WithConceptsMill.grammar_WithConceptsGlobalScope();
    // reset global scope
    scope.clear();

    // Set Fileextension and ModelPath
    scope.setModelFileExtension("mc4");
    scope.setModelPath(modelPath);
    return (Grammar_WithConceptsGlobalScope) scope;
  }

  public static Optional<ASTCDClass> getCDClass(ASTCDCompilationUnit cdCompilationUnit, String cdClassName) {
    return cdCompilationUnit.getCDDefinition().getCDClassList().stream()
        .filter(cdClass -> cdClass.getName().equals(cdClassName))
        .findAny();
  }

  public static Optional<ASTCDInterface> getCDInterface(ASTCDCompilationUnit cdCompilationUnit, String cdInterfaceName) {
    return cdCompilationUnit.getCDDefinition().getCDInterfaceList().stream()
        .filter(cdClass -> cdClass.getName().equals(cdInterfaceName))
        .findAny();
  }

  public static boolean isListOfType(ASTMCType typeRef, String typeArg) {
    if (!TransformationHelper.typeToString(typeRef).equals("java.util.List")) {
      return false;
    }
    if (!(typeRef instanceof ASTMCGenericType)) {
      return false;
    }
    ASTMCGenericType type = (ASTMCGenericType) typeRef;
    if (type.getMCTypeArgumentList().size() != 1) {
      return false;
    }
    if (!type.getMCTypeArgumentList().get(0).getMCTypeOpt().get()
            .printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(typeArg)) {
      return false;
    }
    return true;
  }

}
