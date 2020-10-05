/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting.reporter;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportCreator;
import de.monticore.incremental.IncrementalChecker;
import de.monticore.io.paths.IterablePath;
import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter.GEN_ERROR;
import static de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter.MISSING;

public class IncGenGradleReporter extends IncGenReporter {

  final static String SIMPLE_FILE_NAME = "IncGenGradleCheck";

  public IncGenGradleReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + modelName.replaceAll("\\.", "/"), SIMPLE_FILE_NAME, "txt");
    this.outputDir = outputDir;
  }

  @Override
  public void flush(ASTNode node) {
    openFile();

    for (Path lateOne : filesThatMatterButAreNotThereInTime) {
      if (modelToArtifactMap.keySet().contains(lateOne)) {
        String toAdd = Paths.get(modelToArtifactMap.get(lateOne).toString(), lateOne.toString()).toString();
        if (!grammarFiles.contains(toAdd)) {
          grammarFiles.add(toAdd);
        }
      }
    }

    Collections.sort(grammarFiles);

    if (inputFile != null && !inputFile.isEmpty()) {
      String checkSum;
      if (node != null) {
        checkSum = IncrementalChecker.getChecksum(inputFile);
      } else {
        checkSum = GEN_ERROR;
      }
      writeLine("mc4:"+ inputFile.replaceAll("\\\\","/" ) + " "+checkSum);
      for (String s : grammarFiles) {
        //only local files are important
        if (!s.contains(".jar")) {
          File inputFile = new File(s);
          if (inputFile.exists()) {
            checkSum = IncrementalChecker.getChecksum(inputFile.toString());
          } else {
            checkSum = MISSING;
          }
          writeLine("mc4:" + s.replaceAll("\\\\", "/") + " " + checkSum);
        }
      }
    }
    // create check: used file deleted?
    for (String p : usedHWCFiles) {
//      writeLine("if (-not (Test-Path " + p + ")) { echo \"" + p + " removed!\"; exit}");
      writeLine("hwc:" + p);
    }
    // create check: relevant file added?
    for (String p : notExistentHWCFiles) {
//        writeLine("if (Test-Path " + p + ") { echo \"" + p + " added!\"; exit}");
      writeLine("gen:" + p);
    }

    super.flush(node);
  }
}
