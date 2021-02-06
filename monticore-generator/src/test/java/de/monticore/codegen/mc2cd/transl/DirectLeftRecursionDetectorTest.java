/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.grammar.DirectLeftRecursionDetector;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import parser.MCGrammarParser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Tests the helper class on a concrete grammar containing left recursive and normal rules.
 *
 */
public class DirectLeftRecursionDetectorTest {

  private Optional<ASTMCGrammar> astMCGrammarOptional;
  
  private DirectLeftRecursionDetector directLeftRecursionDetector = new DirectLeftRecursionDetector();

  @BeforeClass
  public static void setUp(){
    Grammar_WithConceptsMill.init();
  }

  @Before
  public void setup() {
    final Path modelPath = Paths.get("src/test/resources/mc2cdtransformation/DirectLeftRecursionDetector.mc4");
    astMCGrammarOptional = MCGrammarParser.parse(modelPath);;
    assertTrue(astMCGrammarOptional.isPresent());
  }

  @Test
  public void testRecursiveRule() {
    // firs
    final List<ASTClassProd> productions = astMCGrammarOptional.get().getClassProdList();

    // TODO: add ASTAlt parameter to isAlternativeLeftRecursive-method
    final ASTClassProd exprProduction = productions.get(0);
    /*
    boolean isLeftRecursive = directLeftRecursionDetector.isAlternativeLeftRecursive(exprProduction);
    Assert.assertTrue(isLeftRecursive);

    final ASTClassProd nonRecursiveProudction1 = productions.get(1);
    isLeftRecursive = directLeftRecursionDetector.isAlternativeLeftRecursive(nonRecursiveProudction1);
    Assert.assertFalse(isLeftRecursive);

    final ASTClassProd nonRecursiveProudction2 = productions.get(2);
    isLeftRecursive = directLeftRecursionDetector.isAlternativeLeftRecursive(nonRecursiveProudction2);
    Assert.assertFalse(isLeftRecursive);
    */
  }

}
