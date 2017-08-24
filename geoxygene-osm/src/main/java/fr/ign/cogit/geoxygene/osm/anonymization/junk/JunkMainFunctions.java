package fr.ign.cogit.geoxygene.osm.anonymization.junk;

import fr.ign.cogit.geoxygene.osm.anonymization.db.access.ElementDbAccess;

@SuppressWarnings("deprecation")
public class JunkMainFunctions {
  
  /**
   * main de ChangesetDBTests.
   */
  public static void mainChangesetDBTests() {
    ChangesetDBTests.getInstance().execTest(new DBTestNumChanges());
  }
  
  /**
   * main de OSMAnonymizedDatabaseWithDates.
   */
  public static void mainOSMAnonymizedDatabaseWithDates() {
    OSMAnonymizedDatabaseWithDates db = new OSMAnonymizedDatabaseWithDates();
    db.readFromElementPostGISDB(ElementDbAccess.instance);
    db.printTest();
  }
}
