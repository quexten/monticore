/* (c) https://github.com/MontiCore/monticore */

import de.monticore.generating.templateengine.reporting.*;
import de.se_rwth.commons.logging.Log;

/**
 * Main class 
 *
 */
public class SomeCode {
  
  /**
   * 
   */
  public static void main(String[] args) {

    // use normal logging (no DEBUG, TRACE)
    Log.ensureInitalization();

    // do nothing
  }
  
  // Used in 14.errorhandling as fictive example
  void aMethod() {
    Reporting.reportToDetailed("Additional info");
  }

  // Used in 14.errorhandling as fictive example
  public void processModels() {
    // disable fail quick
    Log.enableFailQuick(false);
    // iterate and process some models, but completely
    // ...
  
    // re-enable fail quick
    Log.enableFailQuick(true);
  }

}
