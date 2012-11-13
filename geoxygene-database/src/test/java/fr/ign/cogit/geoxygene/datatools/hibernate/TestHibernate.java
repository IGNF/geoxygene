package fr.ign.cogit.geoxygene.datatools.hibernate;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.datatools.hibernate.GeodatabaseHibernate;
import fr.ign.cogit.geoxygene.datatools.hibernate.data.Point_eau;
import fr.ign.cogit.geoxygene.datatools.hibernate.data.Troncon_cours_eau;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Classe de test pour la GeodatabaseHibernate. <br/>
 * Afin de pouvoir utiliser ce test, il faut activer : le mapping des classes
 * Point_eau et Troncon_cours_eau dans le fichier de configuration d'Hibernate :
 * hibernate.cfg.xml qui se trouve dans la racine du répertoire src/. Pour ce
 * faire, ajouter les lignes suivantes : &lt;mapping
 * class="donnees.julien.Point_eau"/&gt; &lt;mapping
 * class="donnees.julien.Troncon_cours_eau"/&gt;
 * <br/>
 * @author Julien Perret
 */
public class TestHibernate {

  // Logger
  private static Logger logger = Logger.getLogger(TestHibernate.class);

  /**
   * Chargement POINT_EAU avec Hibernate. Test le chargement et l'ajout.
   * @param args arguments d'exécution.
   */
  @Test
  public void testPointEau() {
    
    logger.info("Test chargement POINT_EAU.");

    // Création de la base de données hibernate
    GeodatabaseHibernate db = new GeodatabaseHibernate();

    // -----------------------------------------------------------------------------
    // Test : chargement des objets déjà dans la base
    List<Point_eau> resultPoint_eau = db.loadAll(Point_eau.class);
    int result1 = resultPoint_eau.size();
    // Test : il y en a au moins 0.
    logger.info("Nombre de lignes = " + result1);
    Assert.assertTrue("Nb d'occurence pour POINT_EAU : ", result1 >= 0);

    // -----------------------------------------------------------------------------
    // Test : ajout d'une ligne
    
    // On commence une nouvelle transaction
    db.begin();
    
    // On crée un objet POINT_EAU
    Point_eau p = new Point_eau();
    
    // On définit les attributs
    p.setNature("lac" + result1);
    p.setSource("" + System.currentTimeMillis());
    p.setGeom(new GM_Point(new DirectPosition(10000, 10000, 100)));
    
    // On rend les objets créés persistants
    db.makePersistent(p);
    
    // On termine la transaction
    db.commit();
    
    // On vérifie qu'on a une ligne de plus que tout à l'heure
    resultPoint_eau = db.loadAll(Point_eau.class);
    int result2 = resultPoint_eau.size();
    logger.info("Nombre de lignes apres l'ajout = " + result2);
    Assert.assertEquals("Nb d'occurence pour POINT_EAU : ", result1 + 1, result2);
    
    // -----------------------------------------------------------------------------
    // On ferme la connexion
    db.close();
    
    logger.info("fin du test.");
    logger.info("==================================================================");
  }

  /**
   * Chargement TRONCON_COURS_EAU avec Hibernate. <br/>
   * Test le chargement et l'ajout.
   * @param args arguments d'exécution.
   */
  @Test
  public void testTronconCoursEau() {

    // Création de la base de données hibernate
    GeodatabaseHibernate db = new GeodatabaseHibernate();

    // -----------------------------------------------------------------------------
    // Test : chargement des objets déjà dans la base
    List<Troncon_cours_eau> resultTroncon_cours_eau = db
        .loadAll(Troncon_cours_eau.class);
    int result1 = resultTroncon_cours_eau.size();
    // Test : il y en a au moins 0.
    logger.info("Nombre de lignes = " + result1);
    Assert.assertTrue("Nb d'occurence pour TRONCON_COURS_EAU : ", result1 >= 0);
    
    
    // -----------------------------------------------------------------------------
    // Test : ajout d'une ligne
    
    // On commence une nouvelle transaction
    db.begin();

    // On crée un nouveau troncon de cours d'eau
    Troncon_cours_eau t = new Troncon_cours_eau();

    // On définit les attributs
    t.setNom("rivière" + result1);
    t.setSource("juju");
    DirectPositionList liste = new DirectPositionList();
    liste.add(new DirectPosition(10000, 10000, 100));
    liste.add(new DirectPosition(20000, 10000, 10));
    t.setGeom(new GM_LineString(liste));

    // On rend les objets créés persistants
    db.makePersistent(t);

    // termine la transaction
    db.commit();
    
    // On vérifie qu'on a une ligne de plus que tout à l'heure
    resultTroncon_cours_eau = db.loadAll(Troncon_cours_eau.class);
    int result2 = resultTroncon_cours_eau.size();
    logger.info("Nombre de lignes apres l'ajout = " + result2);
    Assert.assertEquals("Nb d'occurence pour TRONCON_COURS_EAU : ", result1 + 1, result2);

    // -----------------------------------------------------------------------------
    // On ferme la connexion
    db.close();

    logger.info("fin du test.");
    logger.info("==================================================================");

  }

}
