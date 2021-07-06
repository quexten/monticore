/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mojo;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import de.monticore.cli.MontiCoreStandardCLI;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static de.monticore.MontiCoreConfiguration.*;

/**
 * Invokes {@link MontiCore} using the given configuration parameters.
 *
 */
@Mojo(name = "generate",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public final class GenerateMojo extends AbstractMojo {
  
  /**
   * The current Maven project.
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject mavenProject;
  
  /**
   * @return mavenProject
   */
  protected MavenProject getMavenProject() {
    return this.mavenProject;
  }
  
  /**
   * The set of grammars/directories to be passed to MontiCore. This is the
   * "grammar" option of MontiCore and defaults to "src/main/grammars".
   */
  @Parameter
  private List<File> grammars;
  
  /**
   * @return the value of the "grammars" configuration parameter.
   */
  protected Set<File> getGrammars() {
    ImmutableSet.Builder<File> grammarFilesBuilder = ImmutableSet.builder();
    
    if (this.grammars != null) {
      this.grammars.forEach(g -> grammarFilesBuilder.add(fromBasePath(g)));
    }
    
    ImmutableSet<File> grammarFiles = grammarFilesBuilder.build();
    return grammarFiles.isEmpty()
        ? ImmutableSet.of(getDefaultGrammarDirectory())
        : grammarFiles;
  }
  
  /**
   * @return the default directory for MontiCore grammars.
   */
  public File getDefaultGrammarDirectory() {
    return fromBasePath("src/main/grammars");
  }
  
  /**
   * The output directory for source code generated by MontiCore. This is the
   * "outputDirectory" option of MontiCore.
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/${plugin.goalPrefix}/sourcecode")
  private File outputDirectory;
  
  /**
   * @return the value of the "outputDirectory" configuration parameter.
   */
  protected File getOutputDirectory() {
    return fromBasePath(this.outputDirectory);
  }

  /**
   * The report directory for reports generated by MontiCore. This is the
   * "reportDirectory" option of MontiCore.
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/${plugin.goalPrefix}/sourcecode/reports")
  private File reportDirectory;

  /**
   * @return the value of the "outputDirectory" configuration parameter.
   */
  protected File getReportDirectory() {
    return fromBasePath(this.reportDirectory);
  }
  
  /**
   * The set of directories containing handwritten code to be integrated into
   * generated code. This is the "handcodedPath" option of MontiCore and
   * defaults to "src/main/java".
   */
  @Parameter
  private List<File> handcodedPaths;
  
  /**
   * @return the value of the "targetPaths" configuration parameter.
   */
  protected Set<File> getHandcodedPaths() {
    ImmutableSet.Builder<File> handcodedPathsBuilder = ImmutableSet.builder();
    
    if (this.handcodedPaths != null) {
      this.handcodedPaths.forEach(t -> handcodedPathsBuilder.add(fromBasePath(t)));
    }
    
    ImmutableSet<File> handcodedPathFiles = handcodedPathsBuilder.build();
    return handcodedPathFiles.isEmpty()
        ? ImmutableSet.of(getDefaultHandcodedPath())
        : handcodedPathFiles;
  }
  
  /**
   * @return the default path for handwritten code.
   */
  public File getDefaultHandcodedPath() {
    return fromBasePath("src/main/java");
  }
  
  /**
   * The set of directories containing templates to be integrated into the code
   * generation process. This is the "templatePath" option of MontiCore and
   * defaults to "src/main/resources".
   */
  @Parameter
  private List<File> templatePaths;
  
  /**
   * @return the value of the "templatePaths" configuration parameter.
   */
  protected Set<File> getTemplatePaths() {
    ImmutableSet.Builder<File> templatePathsBuilder = ImmutableSet.builder();
    
    if (this.templatePaths != null) {
      this.templatePaths.forEach(t -> templatePathsBuilder.add(fromBasePath(t)));
    }
    
    ImmutableSet<File> templatePathFiles = templatePathsBuilder.build();
    return templatePathFiles.isEmpty()
        ? getDefaultTemplatePath().map(ImmutableSet::of).orElse(ImmutableSet.of())
        : templatePathFiles;
  }
  
  /**
   * @return the default path for templates. Returns empty if the directory doesn't exist.
   */
  public Optional<File> getDefaultTemplatePath() {
    return Optional.of(fromBasePath("src/main/resources")).filter(File::exists);
  }
  
  /**
   * The set of models/directories to be passed to MontiCore as part of the
   * model path. By default all grammar directories as specified in the
   * "grammars" parameter as well as any matching project dependency artifacts
   * as specified by the parameters "modelPathDependencies", "scopes", and
   * "classifiers" are added to the modelpath. This is the "modelPath" option of
   * MontiCore.
   */
  @Parameter
  private List<File> modelPaths;
  
  /**
   * @return the value of the "modelPaths" configuration parameter.
   */
  protected Set<File> getModelPaths() {
    ImmutableSet.Builder<File> modelPaths = ImmutableSet.builder();
    
    // 1st: we take all modelpaths directly from the configuration
    if (this.modelPaths != null) {
      this.modelPaths.forEach(mP -> modelPaths.add(fromBasePath(mP)));
    }
    
    // 2nd: if specified we add any grammar directories (default)
    if (addGrammarDirectoriesToModelPath()) {
      for (File grammarFile : getGrammars()) {
        if (grammarFile.isDirectory()) {
          modelPaths.add(grammarFile);
        }
      }
    }
    
    // 3rd: if specified we add the project source directories (non default)
    if (addSourceDirectoriesToModelPath()) {
      getMavenProject().getCompileSourceRoots().forEach(csR -> modelPaths.add(new File(csR)));
    }
    
    // 4th: if specified we add the entire project "compile" classpath (non
    // default)
    if (addClassPathToModelPath()) {
      try {
        getMavenProject().getCompileClasspathElements().forEach(
            cpDir -> modelPaths.add(new File(cpDir)));
      }
      catch (DependencyResolutionRequiredException e) {
        Throwables.propagate(e);
      }
    }
    
    for (Artifact artifact : getMavenProject().getArtifacts()) {
      // 5th: we add any explicitly specified project dependencies
      if (getModelPathDependencies().contains(
          getArtifactDescription(artifact.getGroupId(), artifact.getArtifactId()))) {
        modelPaths.add(artifact.getFile());
      }
      // 6th: we add any project dependencies matching the classifier/scope
      // filter (default: all "grammars", "grammar", and "symbols" dependencies
      // of any scope)
      // FIXME (minor): this does not work in reactor builds which do not invoke
      // the install phase (e.g., mvn dependency:analyze)
      else if (getClassifiers().contains(artifact.getClassifier())
          && (getScopes().isEmpty() || getScopes().contains(artifact.getScope()))) {
        modelPaths.add(artifact.getFile());
      }
    }
    
    return modelPaths.build();
  }
  
  /**
   * A list of dependencies to be added to the model path. The dependencies must
   * be stated as "groupID:artifactID" and must be declared in the project's
   * dependencies.
   */
  @Parameter
  private List<String> modelPathDependencies;
  
  /**
   * @return modelDependencies
   */
  protected List<String> getModelPathDependencies() {
    return firstNonNull(this.modelPathDependencies, ImmutableList.<String> of());
  }
  
  /**
   * Indicates whether any grammar directories should be added to the modelpath
   * (true by default).
   */
  @Parameter(defaultValue = "true")
  private boolean addGrammarDirectoriesToModelPath = true;
  
  /**
   * @return the value of the "addGrammarDirectoriesToModelPath" configuration
   * parameter.
   */
  protected boolean addGrammarDirectoriesToModelPath() {
    return this.addGrammarDirectoriesToModelPath;
  }
  
  /**
   * Indicates whether the project source directories should be added to the
   * modelpath (false by default).
   */
  @Parameter(defaultValue = "false")
  private boolean addSourceDirectoriesToModelPath = false;
  
  /**
   * @return the value of the "addSourceDirectoriesToModelPath" configuration
   * parameter.
   */
  protected boolean addSourceDirectoriesToModelPath() {
    return this.addSourceDirectoriesToModelPath;
  }
  
  /**
   * Indicates whether the (compile) classpath (entries) of the Maven project
   * should be added to the modelpath (false by default).
   */
  @Parameter(defaultValue = "false")
  private boolean addClassPathToModelPath = false;
  
  /**
   * @return the value of the "addClassPathToModelPath" configuration parameter.
   */
  protected boolean addClassPathToModelPath() {
    return this.addClassPathToModelPath;
  }
  
  /**
   * Indicates whether the output directory should be added to the modelpath
   * (true by default).
   */
  @Parameter(defaultValue = "true")
  private boolean addOutputDirectoryToModelPath = false;
  
  /**
   * @return the value of the "addOutputDirectoryToModelPath" configuration
   * parameter.
   */
  protected boolean addOutputDirectoryToModelPath() {
    return this.addOutputDirectoryToModelPath;
  }
  
  /**
   * The scopes to be considered for dependencies to add to the modelpath (any
   * scope by default).
   */
  @Parameter
  private List<String> scopes = ImmutableList.of();
  
  /**
   * @return the value of the "scopes" configuration parameter.
   */
  protected List<String> getScopes() {
    return this.scopes;
  }
  
  /**
   * The classifiers to be considered for dependencies to add to the modelpath
   * (defaults to "grammars", "grammar", "symbols").
   */
  @Parameter(defaultValue = "grammars, grammar, symbols")
  private List<String> classifiers = ImmutableList.of();
  
  /**
   * @return the value of the "classifiers" configuration parameter.
   */
  protected List<String> getClassifiers() {
    return firstNonNull(this.classifiers, ImmutableList.<String> builder()
        .add("grammars")
        .add("grammar")
        .add("symbols")
        .build());
  }
  
  /**
   * An optional alternative Groovy script to execute. This may either be an
   * absolute or relative path in the current project classpath.
   */
  @Parameter
  private String script = null;
  
  /**
   * @return the value of the "script" configuration parameter.
   */
  protected String getScript() {
    return this.script;
  }
  
  /**
   * A set of custom key value arguments to be passed to the Groovy script. The
   * value of an argument may be a space separated list of values. This has no
   * impact on the default script. It is solely passed to custom scripts
   * provided by the "script" parameter.
   */
  @Parameter
  private Map<String, String> arguments = Collections.emptyMap();
  
  /**
   * @return the value of the "arguments" configuration parameter.
   */
  protected Map<String, String> getArguments() {
    return this.arguments;
  }
  
  /**
   * Switch to control the "force" configuration parameter to force generation
   * bypassing the incremental check (defaults to false).
   */
  @Parameter(defaultValue = "false")
  private boolean force = false;
  
  /**
   * @return the value of the "force" configuration parameter.
   */
  protected boolean getForce() {
    return this.force;
  }
  
  /**
   * Switch to skip the plugin execution. Defaults to false.
   */
  @Parameter(defaultValue = "false")
  private boolean skip = false;
  
  /**
   * @return whether this plugin should be skipped.
   */
  protected boolean skip() {
    return this.skip;
  }
  
  /**
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    if (skip()) {
      getLog().info("Plugin execution skipped per configuration.");
      return;
    }
    
    // create all necessary directories
    getOutputDirectory().mkdirs();
    
    // setup configuration
    List<String> argList = new ArrayList<>();
    argList.add("-" + GRAMMAR);
    Set<File> grammars = getGrammars();
    argList.addAll(findGrammars(grammars));
    argList.add("-" + MODELPATH);
    argList.addAll(toStringSet(getModelPaths()));
    argList.add("-" + HANDCODEDPATH);
    argList.addAll(toStringSet(getHandcodedPaths()));
    if (!getTemplatePaths().isEmpty()) {
      argList.add("-" + TEMPLATEPATH);
      argList.addAll(toStringSet(getTemplatePaths()));
    }
    argList.add("-" + OUT);
    argList.addAll(Arrays.asList(getOutputDirectory().getAbsolutePath()));
    argList.add("-" + REPORT);
    argList.addAll(Arrays.asList(getReportDirectory().getAbsolutePath()));
    if (getScript() != null) {
      argList.add("-" + SCRIPT);
      argList.add(getScript());
    }

    // run MontiCore via CLI
    MontiCoreStandardCLI.main(argList.toArray(new String[0]));

    // if everything went well we also need to add the generated output to the
    // Maven project compile roots
    getMavenProject().addCompileSourceRoot(getOutputDirectory().getPath());
    getLog().debug("Adding compile source root: " + getOutputDirectory().getPath());
  }
  
  /**
   * @return a new {@link File} that is absolutized based on the current Maven
   * project's base directory.
   */
  protected File fromBasePath(File file) {
    return fromBasePath(file.getPath());
  }
  
  /**
   * @return a new {@link File} that is absolutized based on the current Maven
   * project's base directory.
   */
  protected File fromBasePath(String filePath) {
    File file = new File(filePath);
    return !file.isAbsolute()
        ? new File(getMavenProject().getBasedir(), filePath)
        : file;
  }
  
  /**
   * @param groupId
   * @param artifactId
   * @return groupId + ":" + artifactId
   */
  protected String getArtifactDescription(String groupId, String artifactId) {
    return groupId.trim() + ":" + artifactId.trim();
  }
  
  /**
   * @param files
   * @return a set of all the file names of the given collection of files
   */
  protected Set<String> toStringSet(Collection<File> files) {
    return ImmutableSet.<String> builder()
        .addAll(Iterables.transform(files, file -> file.getPath())).build();
  }

  /**
   * @param directories the directories potentially containing grammars
   * @return a set of all the absolute paths that lead to grammars
   */
  protected Set<String> findGrammars(Set<File> directories){
    Set<String> grammarFiles = new HashSet<>();
    for(File directory: directories){
      if(directory.exists() && directory.isDirectory()){
        String[] files = directory.list();
        if(files!=null) {
          grammarFiles.addAll(Arrays.stream(files)
            .filter(file -> file.endsWith(".mc4"))
            .map(file -> directory.getAbsolutePath() + "/" + file)
            .filter(file -> new File(file).exists())
            .collect(Collectors.toSet())
          );
        }
      }
    }
    return grammarFiles;
  }
}
