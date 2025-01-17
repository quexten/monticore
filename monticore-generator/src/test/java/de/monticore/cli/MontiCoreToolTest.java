/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cli;

import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.monticore.MontiCoreConfiguration.*;
import static org.junit.Assert.assertTrue;

/**
 * A collection of exemplary use cases for the CLI arguments. These unit tests
 * do not really test something but are written to try out certain argument
 * combinations and hence designed to not fail.
 *
 */
public class MontiCoreToolTest {

  /**
   * Pretty default arguments.
   */
  static String[] simpleArgs = {
      "-" + GRAMMAR,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + HANDCODEDPATH, "src/test/java" };
  
  /**
   * Arguments activating the detailed developer logging.
   */
  static String[] devLogArgs = {
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + GRAMMAR,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + HANDCODEDPATH, "src/test/java",
      "-" + DEV };
  
  /**
   * Arguments specifying a custom log configuration file to use.
   */
  static String[] customLogArgs = {
      "-" + GRAMMAR,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + HANDCODEDPATH, "src/test/java",
      "-" + CUSTOMLOG, "src/test/resources/test.logging.xml" };
  
  /**
   * Arguments for using a custom Groovy script.
   */
  static String[] customScriptArgs = {
      "-" + GRAMMAR,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + HANDCODEDPATH, "src/test/java",
      "-" + SCRIPT, "src/test/resources/my_noemf.groovy",
      };
  
  /**
   * Arguments for using a custom Groovy script.
   */
  static String[] customEmfScriptArgs = {
      "-" + GRAMMAR,
      "src/test/resources/de/monticore/AutomatonEmf.mc4",
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + HANDCODEDPATH, "src/test/java",
      "-" + SCRIPT, "src/test/resources/my_emf.groovy",
      };
  
  /**
   * These arguments specify inputs where there are no ".mc4" files. This will
   * be reported to the user instead of silently doing nothing.
   */
  static String[] argsWithNoGrammars = {
      "-" + GRAMMAR,
      "src/test/resources/monticore",
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + HANDCODEDPATH, "src/test/java" };

  static String[] help = {
      "-" + HELP
  };

  public MontiCoreToolTest() throws IOException {
  }

  @BeforeClass
  public static void deactivateFailQuick() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setup() {
    GrammarFamilyMill.reset();
    GrammarFamilyMill.init();
  }
  
  @Test
  public void testMontiCoreCLI() {
    new MontiCoreTool().run(simpleArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testMontiCoreDevLogCLI() {
    new MontiCoreTool().run(devLogArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testMontiCoreCustomLogCLI() {
    new MontiCoreTool().run(customLogArgs);
    
    assertTrue(!false);
  }

  @Test
  public void testMontiCoreCustomScriptCLI() {
    new MontiCoreTool().run(customScriptArgs);
    
    assertTrue(!false);
  }

  @Test
  public void testMontiCoreCustomEmfScriptCLI() {
    new MontiCoreTool().run(customEmfScriptArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testHelp() {
    new MontiCoreTool().run(help);

    assertTrue(!false);
}
  @Ignore // It's not possible to switch off fail quick (Logger in CLI)
  @Test
  public void testArgsWithNoGrammars() {
    new MontiCoreTool().run(argsWithNoGrammars);
    
    assertTrue(!false);
  }

  /**
   * Test that there is no randomness / non-determinism in the generator.
   * Running twice on the same input (with only different output directories) must lead to
   * *exactly* the same output.
   * This is used for Gradle-Caching!
   */
  @Test
  public void testReproducibility() throws IOException {
    File reproOutDir1 = Paths.get("target/test-run-repo/repo1/").toFile();
    File reproOutDir2 = Paths.get("target/test-run-repo/repo2/").toFile();

    String[] reproducableArgs1 = {
        "-" + GRAMMAR,
        "src/test/resources/de/monticore/Automaton.mc4",
        "-" + MODELPATH, "src/test/resources",
        "-" + OUT, reproOutDir1.toString(),
        "-" + HANDCODEDPATH, "src/test/java",
        "-" + DSTLGEN_LONG, "true"};

    new MontiCoreTool().run(reproducableArgs1);
    FileUtils.deleteDirectory(reproOutDir2);
    FileUtils.moveDirectory(reproOutDir1, reproOutDir2);

    new MontiCoreTool().run(reproducableArgs1);


    List<String> diff = new ArrayList<>();
    for(File f1: FileUtils.listFiles(reproOutDir1, null, true)) {
      if (f1.isFile()) {
        String relPath1 = reproOutDir1.toPath().relativize(f1.toPath()).toString();
        File f2 = Paths.get(reproOutDir2.toString(), relPath1).toFile();

        if(!FileUtils.contentEquals(f1, f2)) {
          diff.add(relPath1);
        }

        assertTrue("File does not exist \n\t" + f2.getAbsolutePath(), f2.isFile());
        /*assertTrue("Different output generating twice! \n" +
              "\t" + f1.getAbsolutePath() + "\n" +
              "\t" + f2.getAbsolutePath() + "\n",
            FileUtils.contentEquals(f1, f2));
         */
      }
    }
    diff.forEach(s -> System.out.println("\t " + s));
    assertTrue(diff.isEmpty());
  }
  
  @After
  public void tearDown() throws Exception {
    Reporting.off();
  }
}
