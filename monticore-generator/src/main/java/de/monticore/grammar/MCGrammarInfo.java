/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTConceptAntlr;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTJavaCodeExt;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar_withconcepts._ast.ASTMCConcept;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Contains information about a grammar which is required for the parser
 * generation
 */
public class MCGrammarInfo {
  
  /**
   * Keywords of the processed grammar and its super grammars
   */
  private Set<String> keywords = Sets.newLinkedHashSet();
  
  /**
   * Lexer patterns
   */
  private Map<MCGrammarSymbol, List<Pattern>> lexerPatterns = new HashMap<>();
  
  private Collection<String> leftRecursiveRules = new HashSet<>();
  
  /**
   * Additional java code for parser defined in antlr concepts of the processed
   * grammar and its super grammars
   */
  private List<String> additionalParserJavaCode = new ArrayList<String>();
  
  /**
   * Additional java code for lexer defined in antlr concepts of the processed
   * grammar and its super grammars
   */
  private List<String> additionalLexerJavaCode = new ArrayList<String>();
  
  /**
   * Predicates
   */
  private ArrayListMultimap<String, PredicatePair> predicats = ArrayListMultimap.create();
  
  /**
   * Internal: LexNamer for naming lexer symbols in the antlr source code
   */
  private LexNamer lexNamer = new LexNamer();

  private Map<String, String> splitRules = Maps.newHashMap();

  private List<String> keywordRules = Lists.newArrayList();

  /**
   * The symbol of the processed grammar
   */
  private MCGrammarSymbol grammarSymbol;
  
  public MCGrammarInfo(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
    buildLexPatterns();
    findAllKeywords();
    grammarSymbol.getTokenRulesWithInherited().forEach(t -> addSplitRule(t));
    grammarSymbol.getKeywordRulesWithInherited().forEach(k -> keywordRules.add(k));

    addSubRules();
    addHWAntlrCode();
    addLeftRecursiveRules();
  }

  private void addSplitRule(String s) {
    String name = "";
    for (char c:s.toCharArray()) {
      name += getLexNamer().getConstantName(String.valueOf(c));
    }
    splitRules.put(s, name.toLowerCase());
  }

  public Map<String, String> getSplitRules() {
    return splitRules;
  }

  public List<String> getKeywordRules() {
    return keywordRules;
  }

  // ------------- Handling of the antlr concept -----------------------------
  
  /**
   * Add all sub/superule-relations to the symbol table form the perspective of
   * the super rule by using addSubrule
   *
   */
  private void addSubRules() {
    Set<MCGrammarSymbol> grammarsToHandle = Sets
        .newLinkedHashSet(Arrays.asList(grammarSymbol));
    grammarsToHandle.addAll(MCGrammarSymbolTableHelper.getAllSuperGrammars(grammarSymbol));
    for (MCGrammarSymbol grammar : grammarsToHandle) {
      HashMap<String, List<ASTRuleReference>> ruleMap = Maps.newLinkedHashMap();
      // Collect superclasses and superinterfaces for classes
      for (ASTClassProd classProd : ((ASTMCGrammar) grammar.getAstNode())
          .getClassProdsList()) {
        List<ASTRuleReference> ruleRefs = Lists.newArrayList();
        ruleRefs.addAll(classProd.getSuperRuleList());
        ruleRefs.addAll(classProd.getSuperInterfaceRuleList());
        ruleMap.put(classProd.getName(), ruleRefs);
      }
      
      // Collect superclasses and superinterfaces for abstract classes
      for (ASTAbstractProd classProd : ((ASTMCGrammar) grammar.getAstNode())
          .getAbstractProdsList()) {
        List<ASTRuleReference> ruleRefs = Lists.newArrayList();
        ruleRefs.addAll(classProd.getSuperRuleList());
        ruleRefs.addAll(classProd.getSuperInterfaceRuleList());
        ruleMap.put(classProd.getName(), ruleRefs);
      }
      
      // Collect superinterfaces for interfaces
      for (ASTInterfaceProd classProd : ((ASTMCGrammar) grammar.getAstNode())
          .getInterfaceProdsList()) {
        List<ASTRuleReference> ruleRefs = Lists.newArrayList();
        ruleRefs.addAll(classProd.getSuperInterfaceRuleList());
        ruleMap.put(classProd.getName(), ruleRefs);
      }

      // Add relation to predicats
      for (Entry<String, List<ASTRuleReference>> entry: ruleMap.entrySet()) {
        for (ASTRuleReference ref: entry.getValue()) {
          Optional<ProdSymbol> prodByName = grammarSymbol
              .getProdWithInherited(ref.getTypeName());
          if (prodByName.isPresent()) {
            addSubrule(prodByName.get().getName(), entry.getKey(), ref);
          }
          else {
            Log.error("0xA2110 Undefined rule: " + ref.getTypeName(),
                ref.get_SourcePositionStart());
          }
        }
      }
    }
  }
  
  
  private void addSubrule(String superrule, String subrule, ASTRuleReference ruleReference) {
    PredicatePair subclassPredicatePair = new PredicatePair(subrule, ruleReference);
    predicats.put(superrule, subclassPredicatePair);
  }
  

  private Collection<String> addLeftRecursiveRuleForProd(ASTClassProd ast) {
    List<ASTProd> superProds = TransformationHelper.getAllSuperProds(ast);
    Collection<String> names = new ArrayList<>();
    superProds.forEach(s -> names.add(s.getName()));
    DirectLeftRecursionDetector detector = new DirectLeftRecursionDetector();
    for (ASTAlt alt : ast.getAltsList()) {
      if (detector.isAlternativeLeftRecursive(alt, names)) {
        names.add(ast.getName());
        return names;
      }
    }
    return Lists.newArrayList();
  }
  
  private void addLeftRecursiveRules() {
    Set<MCGrammarSymbol> grammarsToHandle = Sets
        .newLinkedHashSet(Arrays.asList(grammarSymbol));
    grammarsToHandle.addAll(MCGrammarSymbolTableHelper.getAllSuperGrammars(grammarSymbol));
    for (MCGrammarSymbol grammar : grammarsToHandle) {
      for (ASTClassProd classProd : ((ASTMCGrammar) grammar.getAstNode()).getClassProdsList()) {
        leftRecursiveRules.addAll(addLeftRecursiveRuleForProd(classProd));
      }
    }
  }
  
  /**
   * @return grammarSymbol
   */
  public MCGrammarSymbol getGrammarSymbol() {
    return this.grammarSymbol;
  }
  
  /**
   * @param grammarSymbol the grammarSymbol to set
   */
  public void setGrammarSymbol(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
  }
  
  /**
   * @return java code
   */
  public List<String> getAdditionalParserJavaCode() {
    return this.additionalParserJavaCode;
  }
  
  /**
   * @return java code
   */
  public List<String> getAdditionalLexerJavaCode() {
    return this.additionalLexerJavaCode;
  }
  
  private void addHWAntlrCode() {
    // Get Antlr hwc
    Set<MCGrammarSymbol> grammarsToHandle = Sets
        .newLinkedHashSet(Arrays.asList(grammarSymbol));
    grammarsToHandle.addAll(MCGrammarSymbolTableHelper.getAllSuperGrammars(grammarSymbol));
    for (MCGrammarSymbol grammar : grammarsToHandle) {
      if (grammar.isPresentAstNode()) {
        // Add additional java code for lexer and parser
        for (ASTConcept concept : grammar.getAstNode().getConceptsList()) {
          if (concept.getConcept() instanceof ASTMCConcept) {
            ASTConceptAntlr conceptAntlr = ((ASTMCConcept) concept.getConcept()).getConceptAntlr();
            conceptAntlr.getAntlrParserActionsList().forEach(a -> addAdditionalParserJavaCode(a.getText()));
            conceptAntlr.getAntlrLexerActionsList().forEach(a -> addAdditionalLexerJavaCode(a.getText()));
          }
        }
      }
    }
  }
  
  /**
   * @param action the java code to add
   */
  private void addAdditionalParserJavaCode(ASTJavaCodeExt action) {
    additionalParserJavaCode.add(ParserGeneratorHelper.getText(action));
  }
  
  /**
   * @param action the java code to add
   */
  private void addAdditionalLexerJavaCode(ASTJavaCodeExt action) {
    additionalLexerJavaCode.add(ParserGeneratorHelper.getText(action));
  }
  
  // ------------- Handling of keywords -----------------------------
  
  public Set<String> getKeywords() {
    return Collections.unmodifiableSet(keywords);
  }
  
  /**
   * Checks if the terminal or constant <code>name</code> is a and has to be
   * defined in the parser.
   * 
   * @param name - rule to check
   * @return true, if the terminal or constant <code>name</code> is a and has to
   * be defined in the parser.
   */
  public boolean isKeyword(String name, MCGrammarSymbol grammar) {
    boolean matches = false;
    boolean found = false;
    
    // Check with options
    if (mustBeKeyword(name)) {
      matches = true;
      found = true;
    }
    
    // Automatically detect if not specified
    if (!found && lexerPatterns.containsKey(grammar)) {
      for (Pattern p : lexerPatterns.get(grammar)) {
        
        if (p.matcher(name).matches()) {
          matches = true;
          Log.debug(name + " is considered as a keyword because it matches " + p + " "
              + "(grammarsymtab)", MCGrammarSymbol.class.getSimpleName());
          break;
        }
        
      }
    }
    
    return matches;
  }
  
  public boolean isProdLeftRecursive(String name) {
    return leftRecursiveRules.contains(name);
  }
  
  public List<PredicatePair> getSubRulesForParsing(String ruleName) {
    // Consider superclass
    Optional<ProdSymbol> ruleByName = grammarSymbol.getProdWithInherited(ruleName);
    List<PredicatePair> predicateList = Lists.newArrayList();
    if (!ruleByName.isPresent()) {
      return predicateList;
    }
    
    if (predicats.containsKey(ruleName)) {
      predicateList.addAll(predicats.get(ruleName));
    }

    return predicateList;
  }
  
  /**
   * @return lexNamer
   */
  public LexNamer getLexNamer() {
    return this.lexNamer;
  }

  
  /**
   * Iterates over all Rules to find all keywords
   */
  private void findAllKeywords() {
    for (ProdSymbol ruleSymbol : grammarSymbol.getProdsWithInherited().values()) {
      if (ruleSymbol.isParserProd()) {
        if (ruleSymbol.isPresentAstNode() && ruleSymbol.getAstNode() instanceof ASTClassProd) {
          ASTProd astProd = ruleSymbol.getAstNode();
          Optional<MCGrammarSymbol> refGrammarSymbol = MCGrammarSymbolTableHelper
              .getMCGrammarSymbol(astProd.getEnclosingScope());
          astProd.accept(new TerminalVisitor(refGrammarSymbol));
        }
      }
    }
    
  }
  
  private void buildLexPatterns() {
    buildLexPatterns(grammarSymbol);
    grammarSymbol.getSuperGrammarSymbols().forEach(g -> buildLexPatterns(g));
  }
  
  private void buildLexPatterns(MCGrammarSymbol grammar) {
    List<Pattern> patterns = lexerPatterns.get(grammar);
    if (patterns == null) {
      patterns = new ArrayList<>();
      lexerPatterns.put(grammar, patterns);
    }
    
    for (ProdSymbol rule : grammar.getProdsWithInherited().values()) {
      if (rule.isPresentAstNode() && rule.isIsLexerProd()) {
        if (!MCGrammarSymbolTableHelper.isFragment(rule.getAstNode())) {
          Optional<Pattern> lexPattern = MCGrammarSymbolTableHelper.calculateLexPattern(
              grammar,
                  (ASTLexProd) rule.getAstNode());
          
          if (lexPattern.isPresent()) {
            patterns.add(lexPattern.get());
          }
        }
      }
    }
  }
  
  private boolean mustBeKeyword(String rule) {
    return keywords.contains(rule);
  }

  private class TerminalVisitor implements Grammar_WithConceptsVisitor {

    TerminalVisitor(Optional<MCGrammarSymbol> refGrammarSymbol) {
      this.refGrammarSymbol = refGrammarSymbol;
    }

    Optional<MCGrammarSymbol> refGrammarSymbol;

    Grammar_WithConceptsVisitor realThis = this;

    @Override
    public Grammar_WithConceptsVisitor getRealThis() {
      return realThis;
    }

    @Override
    public void setRealThis(Grammar_WithConceptsVisitor realThis) {
      this.realThis = realThis;
    }

    @Override
    public void visit(ASTTerminal keyword) {
      if (isKeyword(keyword.getName(), grammarSymbol)
              || (refGrammarSymbol.isPresent() && isKeyword(keyword.getName(), refGrammarSymbol.get()))) {
        keywords.add(keyword.getName());
      }
    }

    @Override
    public void visit(ASTConstant keyword) {
      if (isKeyword(keyword.getName(), grammarSymbol)
              || (refGrammarSymbol.isPresent() && isKeyword(keyword.getName(), refGrammarSymbol.get()))) {
        keywords.add(keyword.getName());
      }
    }
  }
  
}
