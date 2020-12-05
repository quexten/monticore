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

import static de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter.MISSING;

public class IncGenCheckReporter extends IncGenReporter {

  final static String SIMPLE_FILE_NAME = "IncGenCheck";

  public IncGenCheckReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + modelName.replaceAll("\\.", "/"), SIMPLE_FILE_NAME, "sh");
    this.outputDir = outputDir;
  }

  @Override
  public void flush(ASTNode node) {
    openFile();
    //create check: grammar changed?
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
        //test if file was removed
        String file = inputFile.replaceAll("\\\\", "/");
        writeLine("[ -e " + file + " ] || (touch $1; echo " + file + " removed!; exit 0;)");
        //test if file was changed by comparing its hash to its previous hash
        writeLine("md5sum -c <<<\"" +checkSum +" *" + file + "\" || (touch $1; echo " + file + " changed!; exit 0;)");
      }
      for (String s : grammarFiles) {
        String digest;
        if (!s.contains(".jar")) {
          File inputFile = new File(s);
          if (inputFile.exists()) {
            digest = IncrementalChecker.getChecksum(inputFile.toString());
          } else {
            digest = MISSING;
          }
        } else {
          //only the local files are important
          continue;
        }
        //test if file was removed
        String file = s.replaceAll("\\\\", "/");
        writeLine("[ -e " + file + " ] || (touch $1; echo " + file + " removed!; exit 0;)");
        //test if file was changed by comparing its hash to its previous hash
        writeLine("md5sum -c <<<\"" +digest +" *" + file + "\" || (touch $1; echo " + file + " changed!; exit 0;)");
      }
    }
    //create check: user templates changed or deleted?
    for (String s : userTemplates) {
      String digest;
      if (!s.contains(".jar")) {
        File inputFile = new File(s);
        if (inputFile.exists()) {
          digest = IncrementalChecker.getChecksum(inputFile.toString());
        }else {
          digest = MISSING;
        }
      }else{
        //only the local files are important
        continue;
      }
      String file = s.replaceAll("\\\\", "/");
      //test if file was removed
      writeLine("[ -e " + file + " ] || (touch $1; echo " + file + " removed!; exit 0;)");
      //test if file was changed by comparing its hash to its previous hash
      writeLine("md5sum -c <<<\"" +digest +" *" + file + "\" || (touch $1; echo " + file + " changed!; exit 0;)");
    }
    // create check: used file deleted?
    for (String p : usedHWCFiles) {
      writeLine("[ -e " + p + " ] || (touch $1; echo " + p + " removed!; exit 0;)");
    }
    // create check: relevant file added?
    for (String p : notExistentHWCFiles) {
      writeLine("[ -e " + p + " ] && (touch $1; echo " + p + " added!; exit 0;)");
    }

    super.flush(node);
  }
}
