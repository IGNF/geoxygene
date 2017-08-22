package fr.ign.cogit.geoxygene.osm.anonymization;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;

import fr.ign.cogit.geoxygene.osm.anonymization.algorithm.ChangesetSwapping;
import fr.ign.cogit.geoxygene.osm.anonymization.analysis.ChangesetDBAnalysis;
import fr.ign.cogit.geoxygene.osm.anonymization.analysis.DatabaseAnalysis;
import fr.ign.cogit.geoxygene.osm.anonymization.analysis.DatabaseStatistics;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.OSMAnonymizedDatabase;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.AnonymizedDate;
import fr.ign.cogit.geoxygene.osm.anonymization.db.SQLDBPreAnonymization;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.ElementDbAccess;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.PostgresAccess;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.ChangesetSwappingQueries;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.DatabaseAnalysisQueries;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.PreAnonymizationQueries;

/**
 * Classe qui permet de rassembler différentes 
 * fonction ayant servit de main à des classes 
 * particulières. 
 * @author Matthieu Dufait
 */
public class MainAnonymization {

  /**
   * Permet de tester les classes DatabaseAnalysis
   * et DatabaseStatistics.
   */
  public static void databaseStatisticsCreationAndSave() {
    DatabaseAnalysis sql = new DatabaseAnalysis(
        new PostgresAccess("localhost", "5432", "nepal3", 
            "postgres", "postgres"), DatabaseAnalysisQueries.ANALYSIS_QUERIES);
    
    DatabaseStatistics stats = sql.getStats();
    
    stats.setStatsSaveFile("data/nepalStatsSaveFile.oas");
    long start = System.nanoTime();
    System.out.println(stats.computeRepartitionDifference(stats));
    System.out.println((System.nanoTime()-start)+"ns");
    stats.save();
  }

  /**
   * Permet de tester la fonction load
   * de DatabaseStatistics, nécessite un 
   * appel à databaseStatisticsCreationAndSave()
   * pour que ça fonctionne correctement.
   */
  public static void databaseStatisticsLoad() {
    DatabaseStatistics stats = DatabaseStatistics.load();
    
    System.out.println(stats);
  }
  
  /**
   * Permet de tester les fonctions 
   * de la classe AnonymizedDate.
   */
  @SuppressWarnings("deprecation")
  public static void anonymizedDateTest()
  {
    //AnonymizedDate.setAnonymizationMode(SIMPLE_GENERALISATION);
    //AnonymizedDate.setAnonymizationMode(RANDOM_PERTURBATION);
    AnonymizedDate.setAnonymizationMode(AnonymizedDate.ROUNDED_GENERALISATION);
    DateFormat df = new SimpleDateFormat();
    // ne rentre pas
    /*System.out.println(df.format(
    new AnonymizedDate("23/04/2015 13:02:10", 
        new Date(8*3600*1000))));
    // ne rentre pas
    System.out.println(df.format(
    new AnonymizedDate("23/04/2015 14:59:10", 
        new Date(20*1000))));
    // ne rentre pas
    System.out.println(df.format(
    new AnonymizedDate("23/04/2015 14:55:10", 
        new Date(12*60*1000))));
    // ne rentre pas
    System.out.println(df.format(
    new AnonymizedDate("23/04/2015 14:30:10", 
        new Date(8*3600*1000))));
    // rentre
    System.out.println(df.format(
    new AnonymizedDate("23/04/2015 14:35:10", 
        new Date(8*3600*1000))));
    // rentre
    System.out.println(df.format(
    new AnonymizedDate("23/04/2015 14:59:10", 
        new Date(4*60*1000))));*/
    /*System.out.println(df.format(d1));
    System.out.println(d1.getHours());*/
    
    long l = new Timestamp((01*60*60+37*60+44)*1000).getTime();
    System.out.println(new Date("2007/12/12 13:12:55").getTime());
    System.out.println(l);
    System.out.println(df.format(
        new AnonymizedDate("2007/12/12 13:12:55", 
            new Date(l))));
  }
  
  /**
   * Permet de tester DatabaseAnalysis.
   */
  public static void databaseAnalysisTest() {

    DatabaseAnalysis sql = new DatabaseAnalysis(
        new PostgresAccess("localhost", "5432", "nepal3", 
            "postgres", "postgres"), DatabaseAnalysisQueries.ANALYSIS_QUERIES);
    
    DatabaseStatistics stats = sql.getStats();
    System.out.println(stats);
    System.out.println("Repartition node contributions:");
    System.out.println(stats.getRepartitionNodeContributions());
    System.out.println("Repartition way contributions:");
    System.out.println(stats.getRepartitionWayContributions());
    System.out.println("Repartition relation contributions:");
    System.out.println(stats.getRepartitionRelationContributions());
    System.out.println("Repartition tags comment:");
    System.out.println(stats.getRepartitionTagComment());
  }
  
  /**
   * Fonction permettant de faire l'analyse 
   * d'une base de données de changeset avec 
   * la classe la ChangesetDBAnalysis.
   */
  public static void changesetDBAnalysis() {
    int[] tab = ChangesetDBAnalysis.instance.centileNumChanges();

    for(Double d : ChangesetDBAnalysis.generateCentiles(0.05, 0.0, 1.0))
      System.out.print(String.format("%.02f ", d));
    System.out.println();
    System.out.println(ArrayUtils.toString(tab));
    
    double[] tabD = ChangesetDBAnalysis.instance.centileTimeOpened();
    for(Double d : ChangesetDBAnalysis.generateCentiles(0.05, 0.0, 1.0))
      System.out.print(String.format("%.02f ", d));
    System.out.println();
    System.out.println(ArrayUtils.toString(tabD));
  }

  /**
   * Fonction utiliser pour tester la 
   * classe OSMAnonymizedDatabase, parcours la base
   * de données PostGIS pour créer une instance 
   * et affiche les résultats.
   */
  public static void osmAnonymizedDatabaseTest() {
    OSMAnonymizedDatabase db = new OSMAnonymizedDatabase();
    ElementDbAccess.instance.setReadTags(false);
    db.readFromElementPostGISDB(ElementDbAccess.instance);
    db.printTest();
  }
  
  
  /**
   * Fonction main de ChangesetSwapping permettant 
   * d'intialiser une instance de ChangesetSwapping,
   * d'appliquer le processus d'anoymisation, affiche
   * les résultats du traitement et enregistre dans 
   * des fichiers les valeurs de répartitions.
   */
  public static void changesetSwappingMainFunction() {
    PostgresAccess paNepal3 =  new PostgresAccess("localhost", "5432", "nepalt1", 
        "postgres", "postgres");
    DatabaseAnalysis dbAnalysis = new DatabaseAnalysis(paNepal3, 
        DatabaseAnalysisQueries.ANALYSIS_QUERIES);
    DatabaseStatistics stats = new DatabaseStatistics();
    stats = dbAnalysis.getStats();

    // XXX Option possiblement à changer 
    // par défaut {true, true, true, true}
    //stats.setDiffOptions(new boolean[]{true, true, true, false});
    //stats.setDiffOptions(new boolean[]{true, true, false, false});
    //stats.setDiffOptions(new boolean[]{true, true, false, true});
    //stats.setDiffOptions(new boolean[]{false, false, false, true});
    
    ChangesetSwapping swapping = new ChangesetSwapping(paNepal3, 
        stats, ChangesetSwappingQueries.SWAPPING_QUERIES);
    
    // XXX Option possiblement à changer 
    //swapping.setRefusSimilaire(false);
    
    swapping.swapping();

    System.out.println(swapping.getSwappingResults());
    
    PrintWriter writer;
    try {
      writer = new PrintWriter("data/repartition.csv", "UTF-8");
      for(Double d : swapping.getSwappingResults().getRepartitionDifferenceEvolutionArray())
        writer.println(d);
      writer.close();
    } catch (IOException e) { 
      e.printStackTrace();
    }
    try {
      writer = new PrintWriter("data/contribution.csv", "UTF-8");
      for(Double d : swapping.getSwappingResults().getUserContributionDifferenceEvolutionArray())
        writer.println(d);
      writer.close();
    } catch (IOException e) { 
      e.printStackTrace();
    }
  }
  
  /**
   * Fonction principale de la classe SQLDBPreAnonymization, 
   * permet d'instancier la classe et lance le processus
   * de pré-anonymisation complet de la base définie dans 
   * l'instance. 
   */
  public static void sqlDBPreAnonymizationMainFunctions() {
    SQLDBPreAnonymization sql = new SQLDBPreAnonymization(
        new PostgresAccess("localhost", "5432", "comores4", 
            "postgres", "postgres"), PreAnonymizationQueries.NEPAL_QUERIES);
    
    sql.setDisplayOn(true);
    
    sql.fullProcess();
  }
  
  public static void main(String[] args) {
    changesetSwappingMainFunction();
    //databaseAnalysisTest();
  }
}
