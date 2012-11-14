package fr.ign.cogit.geoxygene.datatools.hibernate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;

import javax.persistence.InheritanceType;

import org.apache.log4j.Logger;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import fr.ign.cogit.geoxygene.datatools.hibernate.GeodatabaseHibernate;
import fr.ign.cogit.geoxygene.datatools.hibernate.inheritance.ParisBarcelone;
import fr.ign.cogit.geoxygene.datatools.hibernate.inheritance.ParisNewYork;
import fr.ign.cogit.geoxygene.datatools.hibernate.inheritance.Flight;
import fr.ign.cogit.geoxygene.datatools.hibernate.inheritance.A320;
import fr.ign.cogit.geoxygene.datatools.hibernate.inheritance.Boeing747;

/**
 * @author Julien Perret
 * 
 */
public class TestInheritance extends TestCase {
  
  /** Logger. */
  private static Logger logger = Logger.getLogger(TestInheritance.class);
  
  /** Connexion à la base de test. */
  private Connection jdbcConnection = null;
  
  /**
   * Initialisation de la connexion à la base avant les tests.
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    try {
      // Création de la connexion
      Class.forName("org.postgresql.Driver");
      jdbcConnection = DriverManager.getConnection("jdbc:postgresql://del1109s019:5432/dbunit", "dbunit", "dbunit");
      logger.info("Création de la connexion à la base de données.");
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail("Exception thrown on Init : " + e.getMessage());
    }
  }
  
  /**
   * Nettoyage de fin de test.
   */
  @After
  public void tearDown() throws Exception {
      try {
          // Close la connexion
          jdbcConnection.close();
          logger.info("Fermeture de la connexion à la base de données.");
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

  /**
   * Test sur une hiérarchie de classe simple.
   */
  @Test
  public void testInheritance() {
    
    logger.info("==================================================================");
    logger.info("Début du test TestInheritance.");
    
    GeodatabaseHibernate db = new GeodatabaseHibernate();

    // ------------------------------------------------------------------------------------
    // On ajoute 2 vols 
    db.begin();
    ParisBarcelone parisBarcelone = new ParisBarcelone();
    parisBarcelone.setName("ParisBarcelone");
    ParisNewYork parisNewYork = new ParisNewYork();
    parisNewYork.setName("ParisNewYork");
    db.makePersistent(parisBarcelone);
    db.makePersistent(parisNewYork);
    db.commit();
    
    // ------------------------------------------------------------------------------------
    // On vérifie qu'ils sont bien ajoutés 
    // ParisBarcelone
    List<ParisBarcelone> resultParisBarcelone = db.loadAll(ParisBarcelone.class);
    int nbResultParisBarcelone = resultParisBarcelone.size();
    Assert.assertTrue("Nombre de vol Paris-Barcelone est 0. ", nbResultParisBarcelone > 0);
    // ParisNewYork
    List<ParisNewYork> resultParisNewYork = db.loadAll(ParisNewYork.class);
    int nbResultParisNewYork = resultParisNewYork.size();
    Assert.assertTrue("Nombre de vol Paris-NewYork est 0. ", nbResultParisNewYork > 0);
    // Aucun Flight
    // TODO : voir le problème
    // Assert.assertNull(db.loadAll(Flight.class));
    
    Statement st = null;
    ResultSet rs = null;
    try { 
      // On vérifie que la table ParisBarcelone contient tous les objets
      int nbParisBarcelone = -1;
      st = jdbcConnection.createStatement();
      rs = st.executeQuery("Select count(*) as nb From database.ParisBarcelone;");
      if (rs.next()) {
        nbParisBarcelone = rs.getInt("nb");
        Assert.assertEquals("Le nombre d'objets de ParisBarcelone est différent de celui en base.", nbParisBarcelone, nbResultParisBarcelone);
      }
      st.close();
      rs.close();
      
      // On vérifie que la table ParisNewYork contient tous les objets
      int nbParisNewYork = -1;
      st = jdbcConnection.createStatement();
      rs = st.executeQuery("Select count(*) as nb From database.ParisNewYork;");
      if (rs.next()) {
        nbParisNewYork = rs.getInt("nb");
        Assert.assertEquals("Le nombre d'objets de ParisNewYork est différent de celui en base.", nbParisNewYork, nbResultParisNewYork);
      }
      st.close();
      rs.close();
      
      // On vérifie que la table Flight contient l'ensemble des objets ParisNewYork + ParisBarcelone
      int nbFlight = -1;
      st = jdbcConnection.createStatement();
      rs = st.executeQuery("Select count(*) as nb From database.Flight;");
      if (rs.next()) {
        nbFlight = rs.getInt("nb");
        Assert.assertEquals("Le nombre d'objets de Flight est différent de celui en base.", nbFlight, nbResultParisNewYork + nbResultParisBarcelone);
      }
      st.close();
      rs.close();
      
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail("Exception thrown on count Plane : " + e.getMessage());
    } finally {
      try {
        if (rs != null) {
            rs.close();
        }
      } catch (Exception e) {
          logger.error("Error while closing statement : " + e.getMessage());
      }
      try {
        if (st != null) {
          st.close();
        }
      } catch (Exception e) {
        logger.error("Error while closing statement : " + e.getMessage());
      }
    }
    
    logger.info("Fin du test TestInheritance.");
    logger.info("==================================================================");
  }

  /**
   * Test Hiérarchie de classe avec un Discriminator.
   */
  @Test
  public void testDiscriminator() {
    
    logger.info("==================================================================");
    logger.info("Début du test TestDiscriminator.");
    
    GeodatabaseHibernate db = new GeodatabaseHibernate();

    // ------------------------------------------------------------------------------
    // On ajoute 1 avion Airbus et 1 avion Boeing 
    db.begin();
    A320 a320 = new A320();
    a320.setName("Airbus 320");
    Boeing747 boeing747 = new Boeing747();
    boeing747.setName("Boeing 747");
    db.makePersistent(a320);
    db.makePersistent(boeing747);
    db.commit();
    
    // ------------------------------------------------------------------------------
    // On vérifie qu'ils sont bien ajoutés
    // On doit avoir 0 Airbus
    List<A320> resultA320 = db.loadAll(A320.class);
    int nbResultA320 = resultA320.size();
    Assert.assertTrue("A320 : ", nbResultA320 > 0);
    // On doit avoir 0 Boeing
    List<Boeing747> resultBoeing747 = db.loadAll(Boeing747.class);
    int nbResultBoeing747 = resultBoeing747.size();
    Assert.assertTrue("Boeing747 : ", nbResultBoeing747 > 0);
    
    // On vérifie que les tables A320 et Boeing747 sont vides
    Statement st = null;
    ResultSet rs = null;
    try { 
      // On vérifie que la table Boeing747 est vide
      int nbA320 = -1;
      st = jdbcConnection.createStatement();
      rs = st.executeQuery("Select count(*) as nb from database.A320;");
      if (rs.next()) {
        nbA320 = rs.getInt("nb");
        Assert.assertEquals("Le nombre d'A320 n'est pas égal à 0.", nbA320, 0);
      }
      st.close();
      rs.close();
      
      // On vérifie que la table Boeing747 est vide
      int nbBoeing747 = -1;
      st = jdbcConnection.createStatement();
      rs = st.executeQuery("Select count(*) as nb from database.Boeing747;");
      if (rs.next()) {
        nbBoeing747 = rs.getInt("nb");
        Assert.assertEquals("Le nombre de Boeing747 n'est pas égal à 0.", nbBoeing747, 0);
      }
      st.close();
      rs.close();
      
      // On vérifie que la table Plane contient l'ensemble des lignes 
      int nbPlane = -1;
      st = jdbcConnection.createStatement();
      rs = st.executeQuery("Select count(*) as nb from database.Plane;");
      if (rs.next()) {
        nbPlane = rs.getInt("nb");
        Assert.assertEquals("Le nombre d'avion est différent de Boeing747 + A320.", nbPlane, nbResultBoeing747 + nbResultA320);
      }
      st.close();
      rs.close();
      
      // On vérifie que la table Plane contient l'ensemble des lignes avec l'attribut "planetype" qui vaut A320
      int nbPlaneA320 = -1;
      st = jdbcConnection.createStatement();
      rs = st.executeQuery("Select count(*) as nb from database.Plane Where planetype = 'A320';");
      if (rs.next()) {
        nbPlaneA320 = rs.getInt("nb");
        Assert.assertEquals("Le nombre d'avion de type A320 est différent du nombre d'objets instanciés persistants A320.", nbPlaneA320, nbResultA320);
      }
      st.close();
      rs.close();
      
      // On vérifie que la table Plane contient l'ensemble des lignes avec l'attribut "planetype" qui vaut Boeing747
      int nbPlaneBoeing747 = -1;
      st = jdbcConnection.createStatement();
      rs = st.executeQuery("Select count(*) as nb from database.Plane Where planetype = 'Boeing747';");
      if (rs.next()) {
        nbPlaneBoeing747 = rs.getInt("nb");
        Assert.assertEquals("Le nombre d'avion de type Boeing747 est différent du nombre d'objets instanciés persistants Boeing747.", nbPlaneBoeing747, nbResultBoeing747);
      }
      st.close();
      rs.close();
      
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail("Exception thrown on count Plane : " + e.getMessage());
    } finally {
      try {
        if (rs != null) {
            rs.close();
        }
      } catch (Exception e) {
          logger.error("Error while closing statement : " + e.getMessage());
      }
      try {
        if (st != null) {
          st.close();
        }
      } catch (Exception e) {
        logger.error("Error while closing statement : " + e.getMessage());
      }
    }
      
    logger.info("Fin du test TestDiscriminator.");
    logger.info("==================================================================");
  }
  
  /**
   * Test Hiérarchie avec "strategy = InheritanceType.JOINED".
   */
  @Test
  public void testJoined() {
    
    logger.info("==================================================================");
    logger.info("Début du test TestJoined.");
    
    /*
    db.begin();
    Boat boat = new Boat();
    boat.setName("machin");
    Ferry ferry = new Ferry();
    ferry.setName("truc");
    db.makePersistent(boat);
    db.makePersistent(ferry);
    db.commit();
    
    AmericaCupClass americaCupClass = new AmericaCupClass();
    americaCupClass.setName("america");
    AmericaCupClass americaCupClass2 = new AmericaCupClass();
    americaCupClass2.setName("america2");
  
    db.begin();
    db.makePersistent(americaCupClass);
    db.makePersistent(americaCupClass2);
    db.commit();
    
    */
    
    logger.info("Fin du test TestDiscriminator.");
    logger.info("==================================================================");
  }
  
  
  /**
   * Test Hiérarchie avec "strategy = InheritanceType.TABLE_PER_CLASS".
   */
  @Test
  public void testTablePerClass() {
    
    logger.info("==================================================================");
    logger.info("Début du test TestJoined.");
    
    /*
    Pigeon pigeon = new Pigeon();
    pigeon.setName("pigeon");
    Goose goose = new Goose();
    goose.setName("tom the goose");
    Hunter hunter = new Hunter();
    hunter.setName("Claude the hunter");
    hunter.getKills().add(pigeon);
    hunter.getKills().add(goose);
    BirdInterface birdy = new BirdImpl();
    birdy.setName("Birdy");
    db.begin();
    db.makePersistent(pigeon);
    db.makePersistent(goose);
    db.makePersistent(hunter);
    db.makePersistent(BirdProxy.newInstance(birdy,
        new Class[] { BirdInterface.class }));
    db.commit();

    Canine canine = new Canine();
    canine.setName("dog");
    Feline feline = new Feline();
    feline.setName("cat");
    Rodent rodent = new Rodent();
    rodent.setName("Mouse");
    rodent.getPredators().add(canine);
    rodent.getPredators().add(feline);
    db.begin();
    db.makePersistent(canine);
    db.makePersistent(feline);
    db.makePersistent(rodent);
    db.commit();

    List<BirdInterface> list = db.loadAll(BirdInterface.class);
    for (BirdInterface b : list) {
      System.out.println(b.getId() + " : " + b.getName());
    }
    List<Mammal> listMammals = db.loadAll(Mammal.class);
    for (Mammal m : listMammals) {
      System.out.println(m.getId() + " : " + m.getName());
      for (Mammal p : m.getPredators()) {
        System.out.println("prédator : " + p.getId() + " : " + p.getName());
      }
    }
    */
    
    logger.info("Fin du test TestDiscriminator.");
    logger.info("==================================================================");
  }
  
  

}
