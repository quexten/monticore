/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportManager.ReportManagerFactory;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.*;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.io.paths.MCPath;

/**
 * Initializes and provides the set of reports desired for MontiCore to the
 * reporting framework.
 *
 */
public class MontiCoreReports implements ReportManagerFactory {

  protected String outputDirectory;

  protected String reportDirectory;

  protected MCPath handwrittenPath;
  
  protected MCPath templatePath;
  

  /**
   * Constructor for de.monticore.MontiCoreReports
   */
  protected MontiCoreReports(
          String outputDirectory,
          String reportDiretory,
          MCPath handwrittenPath,
          MCPath templatePath) {
    this.outputDirectory = outputDirectory;
    this.reportDirectory = reportDiretory;
    this.handwrittenPath = handwrittenPath;
    this.templatePath = templatePath;
  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.ReportManager.ReportManagerFactory#provide(java.lang.String)
   */
  @Override
  public ReportManager provide(String modelName) {
    String lowerCaseName = modelName.toLowerCase();
    MontiCoreNodeIdentifierHelper identifierHelper = new MontiCoreNodeIdentifierHelper();
    ReportingRepository repository = new ReportingRepository(identifierHelper);
    repository.initAllTemplates();
    
    ReportManager reports = new ReportManager(this.outputDirectory);

    Grammar_WithConceptsTraverser traverserSummary = Grammar_WithConceptsMill.inheritanceTraverser();
    SummaryReporter summary = new SummaryReporter(this.reportDirectory, lowerCaseName, repository, traverserSummary);
    GeneratedFilesReporter generated = new GeneratedFilesReporter(this.reportDirectory, lowerCaseName,
        repository);
    HandWrittenCodeReporter handwritten = new HandWrittenCodeReporter(this.reportDirectory,
        lowerCaseName, repository);
    TemplatesReporter templates = new TemplatesReporter(this.reportDirectory, lowerCaseName, repository);
    HookPointReporter hookPoints = new HookPointReporter(this.reportDirectory, lowerCaseName,
        repository);
    InstantiationsReporter instantiations = new InstantiationsReporter(this.reportDirectory,
        lowerCaseName);
    VariablesReporter variables = new VariablesReporter(this.reportDirectory, lowerCaseName);
    DetailedReporter detail = new DetailedReporter(this.reportDirectory, lowerCaseName, repository);
    TemplateTreeReporter templateTree = new TemplateTreeReporter(this.reportDirectory, lowerCaseName);
    InvolvedFilesReporter ioReporter = new InvolvedFilesReporter(this.reportDirectory);
    Grammar_WithConceptsTraverser traverserNodeTree = Grammar_WithConceptsMill.inheritanceTraverser();
    NodeTreeReporter nodeTree = new NodeTreeReporter(this.reportDirectory, lowerCaseName, repository, traverserNodeTree);
    Grammar_WithConceptsTraverser traverserNodeTree2 = Grammar_WithConceptsMill.inheritanceTraverser();
    NodeTreeDecoratedReporter nodeTreeDecorated = new NodeTreeDecoratedReporter(
        this.reportDirectory, lowerCaseName, repository, traverserNodeTree2);
    Grammar_WithConceptsTraverser traverserNodeType = Grammar_WithConceptsMill.inheritanceTraverser();
    NodeTypesReporter nodeTypes = new NodeTypesReporter(this.reportDirectory, lowerCaseName, traverserNodeType);
    TransformationReporter transformations = new TransformationReporter(this.reportDirectory,
        lowerCaseName, repository);
    ArtifactGmlReporter artifactGml = new ArtifactGmlReporter(this.reportDirectory, lowerCaseName);
    ArtifactGVReporter artifactGV = new ArtifactGVReporter(this.reportDirectory, lowerCaseName);
    ODReporter objDiagram = new ODReporter(this.reportDirectory, lowerCaseName, repository);
    SuccessfulReporter finishReporter = new SuccessfulReporter(this.reportDirectory, lowerCaseName);
    IncGenCheckReporter incGenCheck = new IncGenCheckReporter(this.outputDirectory, lowerCaseName);
    IncGenGradleReporter gradleReporter = new IncGenGradleReporter(this.outputDirectory, lowerCaseName);

    reports.addReportEventHandler(summary); // 01_Summary
    reports.addReportEventHandler(generated); // 02_GeneratedFiles
    reports.addReportEventHandler(handwritten); // 03_HandwrittenCodeFiles
    reports.addReportEventHandler(templates); // 04_Templates
    reports.addReportEventHandler(hookPoints); // 05_HookPoint
    reports.addReportEventHandler(instantiations); // 06_Instantiations
    reports.addReportEventHandler(variables); // 07_Variables
    reports.addReportEventHandler(detail); // 08_Detailed
    reports.addReportEventHandler(templateTree); // 09_TemplateTree
    reports.addReportEventHandler(nodeTree); // 10_NodeTree
    reports.addReportEventHandler(nodeTreeDecorated); // 11_NodeTreeDecorated
    reports.addReportEventHandler(nodeTypes); // 12_TypesOfNodes
    reports.addReportEventHandler(transformations); // 14_Transformations
    reports.addReportEventHandler(artifactGml); // 15_ArtifactGml
    reports.addReportEventHandler(artifactGV); // 16_ArtifactGv
    reports.addReportEventHandler(ioReporter); // 18_InvolvedFiles
    reports.addReportEventHandler(finishReporter); // 19_Successful
    reports.addReportEventHandler(objDiagram); // ObjectDiagram
    reports.addReportEventHandler(incGenCheck); // IncGenCheck
    reports.addReportEventHandler(gradleReporter);

    return reports;
  }
  
}
