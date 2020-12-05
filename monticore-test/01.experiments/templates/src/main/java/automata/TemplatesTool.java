/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._ast.ASTTransition;
import automata._parser.AutomataParser;
import automata._symboltable.AutomataSymbolTableCreator;
import automata._symboltable.AutomataSymbols2Json;
import automata._symboltable.IAutomataArtifactScope;
import automata._symboltable.IAutomataGlobalScope;
import com.google.common.collect.Lists;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Main class for the Automaton DSL tool.
 */
public class TemplatesTool {

  /** Configurational values:
   */
  public static final Path SYMBOL_LOCATION = Paths.get("target");
  public static final String TOP_NAME_EXTENSION = "TOP";

  /**
   * The tool calculates and uses the following
   * values along it's generation process.
   *
   * This implementation follows the general approach to store all
   * derived data in attributes of the tool-object.
   * This PREVENTS re-entrant reuse of the AutomationTool.
   * But it allows to share calculated results in the attributes below.
   *
   * Furthermore, in this approach we pre-calculate the needed information
   * (such as stimuli lists) allowing the templates to be relatively free
   * of calculations, iterations and callbacks to the java code.
   * This MAY lead to inefficient and not needed pre-calculations and
   * missing information -- in case the templates are adapted.
   */

  // Filename of the model: args[0]
  static protected String modelfilename = "";

  //  handcodedPath: args[1]
  static protected IterablePath handcodedPath;

  // output directory: args[2]
  static protected File outputDir;
  
  // output directory: args[2]
  static protected Optional<File> templatePath = Optional.empty();
  
  // The AST of the model to be handled (will result from parsing)
  protected ASTAutomaton ast;

  // The Global Scope of the symbol table
  protected IAutomataGlobalScope globalScope;

  // the symbol table of the model (after parsing and SymTab creation)
  IAutomataArtifactScope modelTopScope;

  // The generator engine used (reentrant, so only one instance needed)
  protected GeneratorEngine generatorEngine;
  
  // The global extension management used
  protected GlobalExtensionManagement glex;
  
  // Two dimensional map: SourceState x Stimulus -> Transition
  Map<ASTState, Map<String,ASTTransition>> deltaMap = new HashMap<>();
  
  // Map: Name -> State (== State-Symbols)
  Map<String, ASTState> stateMap = new HashMap<>();
  
  // List of stimuli
  Set<String> stimuli = new HashSet<>();
  
  /**
   * Use three arguments to specify the automata model,
   * the path containing handwritten extensions of
   * the generated code and the output directory.
   *
   * @param args requires 3 arguments:
   *     1. automata modelfile,
   *     2. handcodedPath,
   *     3. output directory
   */
  public static void main(String[] args) {
    new TemplatesTool(args);
  }

  /**
   * Entry method of the AutomataTool:
   * It extracts the relevant three arguments from the command line argumemnts
   * and calls the tool execution workflow
   */
  public TemplatesTool(String[] args) {
    if (args.length < 3) {
      Log.error("0xEE631 Please specify at least 3 arguments: \n"
          + "1. automata modelfile,\n"
          + "2. handcodedPath,\n"
          + "3. output directory.\n"
          + "4. (optional) templatePath"
          );
      return;
    }
    // get the model from args
    modelfilename = args[0];

    // get handcodedPath from arg[1]
    handcodedPath = IterablePath.from(new File(args[1]), "java");

    // get output directory from arg[2]
    outputDir = new File(args[2]);
    
    // if present get templatePath
    if(args.length == 4) {
      templatePath = Optional.of(new File(args[3]));
    }
    
    executeWorkflow();
  }
  
  /**
   * Second entry method of the AutomataTool:
   * it stores the input parameters and calls the execution workflow
   */
  public TemplatesTool(String modelfilename, IterablePath handcodedPath, File outputDir) {
    this.modelfilename = modelfilename;
    this.handcodedPath = handcodedPath;
    this.outputDir = outputDir;
    
    executeWorkflow();
  }
  
  
  /**
   * The execution workflow:
   * a single larger method calling all the individual steps needed
   */
  public void executeWorkflow() {

    // Part 1: Frontend
    // parse the model and create the AST representation
    ast = parse(modelfilename);
    Log.info(modelfilename + " parsed successfully", this.getClass().getName());

    // setup the symbol table
    globalScope =  AutomataMill
        .globalScope();
    globalScope.setModelPath(new ModelPath());
    globalScope.setFileExt("aut");
    modelTopScope = createSymbolTable(ast);
  
    // Part 2: CoCos
    // deliberately omitted
  
    // Part 3: Store Symboltable
    // store artifact scope and its symbols
    AutomataSymbols2Json deser = new AutomataSymbols2Json();
    deser.store(modelTopScope, SYMBOL_LOCATION+"/"+ Paths.get(modelfilename).getFileName() + "sym");
    Log.info(modelfilename + " symboltable stored successfully", this.getClass().getName());
    
    // Part 4: Transformation and Data Calculation
    deriveStateMap_DeltaMap();
    
    // Part 5: Backend for Generation: Setup Engine
    initGeneratorEngine();
    
    // Part 6: Generate.....
    //generate the class for the whole statechart
    generateStatechart();
  
    //generate the factory class for the states
    generateFactory();
  
    //generate the abstract class for the states
    generateAbstractState();
  
    // generate the class for each state
    for(ASTState state : ast.getStateList()) {
      generateState(state);
    }
  
    Log.info(modelfilename + " code generated successfully", this.getClass().getName());
  }

  /************************************************************************/
  /***          The Generator Functions: each creates a java file       ***/
  /************************************************************************/
  
  /**
   * Initializes the generator engine
   */
  protected void initGeneratorEngine() {
    initGlex();
    GeneratorSetup s = new GeneratorSetup();
  
    s.setGlex(glex);
    s.setOutputDirectory(outputDir);
    templatePath.ifPresent(e -> s.setAdditionalTemplatePaths(Lists.newArrayList(e)));
    generatorEngine = new GeneratorEngine(s);
  }
  
  /**
   * Initializes the global extension management
   */
  protected void initGlex() {
    glex = new GlobalExtensionManagement();
    
    // The modelName is used veryeher and does not change during generation
    glex.setGlobalValue("modelName", ast.getName());
  }
  
  /**
   * Generates the class for the statechart itself
   */
  protected void generateStatechart() {
    String className = ast.getName();

    // we assume there is at least one state (--> CoCo)
    // if there are more: one will arbitrarily be choosen (may be the last one)  (---> CoCo?)
    ASTState initialState = ast.getStateList().stream().filter(ASTState::isInitial).findAny().get();
    
    // handle TOP extension
    boolean isHW = existsHandwrittenClass(handcodedPath,className);
    if(isHW){
      className = ast.getName() + TOP_NAME_EXTENSION;
    }
    
    // call generator
    generatorEngine.generate("Statechart.ftl",
            Paths.get(className + ".java"), ast,
            initialState,
            stimuli,
            className, isHW);
  }
  
  /**
   * Generates the abstract super class for all state classes
   */
  protected void generateAbstractState() {
    String className = "Abstract" + ast.getName() + "State";
  
    // handle TOP extension
    if(existsHandwrittenClass(handcodedPath,className)){
      className += TOP_NAME_EXTENSION;
    }

    generatorEngine.generate("AbstractState.ftl",
            Paths.get(className+ ".java"), ast,
            stimuli, className);
  }
  
  
  /**
   * Generates a class for the given state
   *
   * @param state the state the code is generated for.
   */
  protected void generateState(ASTState state) {
    String className = state.getName() + "State";
  
    // handle TOP extension
    boolean isHW = existsHandwrittenClass(handcodedPath,className);
    if(isHW){
      className += TOP_NAME_EXTENSION;
    }
  
    // sub Map of delta: contains all transitions starting in this state
    Map<String,ASTTransition> outgoing = deltaMap.get(state);
    
    generatorEngine.generate("ConcreteState.ftl",
            Paths.get(className+ ".java"), ast,
            outgoing, className, isHW);
  }
  
  /**
   * Generates the class for the state factory
   * (although the factory isn't used in this example)
   */
  protected void generateFactory() {
    String className = ast.getName() + "Factory";
  
    // handle TOP extension
    boolean isHW = existsHandwrittenClass(handcodedPath,className);
    if(isHW){
      className = className+ TOP_NAME_EXTENSION;
    }
    
    generatorEngine.generate("StatechartFactory.ftl",
            Paths.get(className+ ".java"), ast,
            className, isHW);
  }
  
  /**
   * Calculates a list of transitions that act as representatives for all occuring stimuli
   * (each stumulis is represented exactly once in that list)
   *
   * @param allTransitions list o all transitions in the automaton
   * @param inputsToBeExcluded inputs that should be excluded
   * @return a list of transitions that act as representatives for not accepted inputs
   */
  protected void deriveStateMap_DeltaMap() {
    
    // We might also extend and use the symbol table for this extra infos
    // For demonstration we use the direct approach
  
    // initialize delta: transition map of maps, and state name2node
    for(ASTState s: ast.getStateList()) {
      stateMap.put(s.getName(),s);
      deltaMap.put(s,new HashMap<>());
    }
    
    // Add the transitions to the table
    for(ASTTransition t: ast.getTransitionList()) {
      String input = t.getInput();
      stimuli.add(input);
      ASTState from = stateMap.get(t.getFrom());
      // we assume that the automaton is deterministic --> CoCo
      // if it isn't one transition will arbitrarily choosen (may be the last one)
      // However, it may be incomplete!
      deltaMap.get(from).put(input, t);
    }
  }
  
  /**
   * Parse the model contained in the specified file.
   *
   * @param file - file to parse
   * @return
   */
  public ASTAutomaton parse(String file) {
    try {
      AutomataParser parser = new AutomataParser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(file);

      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0x238F1 Model could not be parsed: " + file + ".");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0x238F2 Failed to parse " + file, e);
    }
    return null;
  }
  
  /**
   * Create the symbol table from the parsed AST.
   *
   * @param ast the model
   * @return
   */
  public IAutomataArtifactScope createSymbolTable(ASTAutomaton ast) {
    AutomataSymbolTableCreator stc = AutomataMill.automataSymbolTableCreator();
    stc.putOnStack(globalScope);
    return stc.createFromAST(ast);
  }
  
  /**
   * Check whewther an handwritten version of that class already exits and shall be integrated
   * (e.g. to apply TOP mechanism)
   */
  public static boolean existsHandwrittenClass(IterablePath targetPath, String qualifiedName) {
    Path hwFile = Paths.get(Names.getPathFromPackage(qualifiedName)+ ".java");
    Optional<Path> hwFilePath = targetPath.getResolvedPath(hwFile);
    boolean result = hwFilePath.isPresent();
    if (result) {
      Reporting.reportUseHandwrittenCodeFile(hwFilePath.get(),hwFile);
    }
    Reporting.reportHWCExistenceCheck(targetPath, hwFile, hwFilePath);
    return result;
  }
}
