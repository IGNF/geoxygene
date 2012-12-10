package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.Column;
import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;

import fr.ign.cogit.geoxygene.datatools.hibernate.GeodatabaseHibernate;
import fr.ign.cogit.geoxygene.datatools.hibernate.HibernateUtil;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.index.Tiling;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.cartetopo.data.Roads;

/**
 * Carte Topo dataset. <br/>
 * <ul>
 * <li>ct1 : schema construit à la main</li>
 * <li>ct2 : schema construit à partir d'un jeu de données en base.</li>
 * </ul>
 */
public class CompareCarteTopoTest extends DBTestCase {

  // Logger
  private static Logger logger = Logger.getLogger(CompareCarteTopoTest.class);

  // public static final String TABLE_TRONCON_ROUTE = "contrib.troncon_route";
  public static final String TABLE_ROADS = "contrib.roads";

  private FlatXmlDataSet loadedDataSet;

  /**
   * Initialisation des paramètres pour accéder à la base de données.
   */
  public CompareCarteTopoTest() {
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS,
        "org.postgresql.Driver");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
        // "jdbc:postgresql://del1109s019:5432/dbunit");
        "jdbc:postgresql://localhost:5433/dbunit");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME,
        "dbunit");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD,
        "dbunit");
  }

  /**
   * Load the data which will be inserted for the test.
   */
  protected IDataSet getDataSet() throws Exception {
    // logger.info("Chargement des données en base");
    loadedDataSet = new FlatXmlDataSet(this.getClass().getClassLoader()
        .getResourceAsStream("dbunit/contrib.xml"));
    return loadedDataSet;
  }

  /**
   * On charge une carte topo en base de données.
   * @throws Exception
   */
  /*public void testCheckTronconDataLoaded() throws Exception {

    // On vérifie que le dataset n'est pas null.
    assertNotNull(loadedDataSet);

    // On vérifie que les 61 lignes sont bien ajoutées
    int rowCount = loadedDataSet.getTable(TABLE_TRONCON_ROUTE).getRowCount();
    assertEquals(61, rowCount);

  }*/

  /**
   * Comparaison du calcul du chemin le plus court 26-39 : en base et dans GeOxygene.
   * Chargement des données en base se fait via DBUnit
   * Chargement des données dans Geoxygene se fait par Hibernate.
   * @throws Exception
   */
  public void testCheckRoad() throws Exception {

    // On vérifie que le dataset n'est pas null.
    assertNotNull(loadedDataSet);

    // On vérifie que les 61 lignes sont bien ajoutées en base
    int rowCount = loadedDataSet.getTable(TABLE_ROADS).getRowCount();
    assertEquals(61, rowCount);
    
    // -----------------------------------------------------------------------------------------
    // Chargement dans Geoxygene
    // 
    // On commence par charger le reseau dans Geoxygene
    GeodatabaseHibernate geoDB = new GeodatabaseHibernate();
    FT_FeatureCollection<Roads> roads = geoDB.loadAllFeatures(Roads.class);
    
    // On vérifie que les 61 lignes sont bien ajoutées dans la FT_FeatureCollection
    assertEquals(rowCount, roads.size());
    
    // Chargement de la topologie du reseau
    // utiliser la classe Chargeur : Chargeur.importAsNodes(roads, carteTopo);
    // Noeud noeudSource = new Noeud();
    CarteTopo carteTopo = new CarteTopo("Network Map Test");
    double tolerance = 0.1;

    // A la main
    // Arcs
    for (IFeature element : roads) {
        
        Arc arc = carteTopo.getPopArcs().nouvelElement();
        ILineString ligne = new GM_LineString((IDirectPositionList) element.getGeom().coord().clone());
        arc.setGeometrie(ligne);
        
        String oneway = element.getAttribute("sens").toString();
        if (oneway.equals("Y")) {
          arc.setOrientation(2);
        } else {
          arc.setOrientation(1);
        }
        
        // double length = Double.parseDouble(element.getAttribute("length").toString());
        // arc.setPoids(length);
        
        // Noeuds
        IDirectPosition p1 = arc.getGeometrie().getControlPoint(0);
        IDirectPosition p2 = arc.getGeometrie().getControlPoint(arc.getGeometrie().sizeControlPoint() - 1);
       
        //
        int source = Integer.parseInt(element.getAttribute("source").toString());
        int target = Integer.parseInt(element.getAttribute("target").toString());
        
        // ???
        Collection<Noeud> candidates = carteTopo.getPopNoeuds().select(p1, tolerance);
        if (candidates.isEmpty()) {
          Noeud n1 = carteTopo.getPopNoeuds().nouvelElement(p1.toGM_Point());
          n1.setId(source);
          arc.setNoeudIni(n1);
        } 
        candidates = carteTopo.getPopNoeuds().select(p2, tolerance);
        if (candidates.isEmpty()) {
          Noeud n2 = carteTopo.getPopNoeuds().nouvelElement(p2.toGM_Point());
          n2.setId(target);
          arc.setNoeudFin(n2);
        } 
    }
    
    List<Noeud> toRemove = new ArrayList<Noeud>(0);
    // connect the single nodes
    for (Noeud n : carteTopo.getPopNoeuds()) {
      if (n.arcs().size() == 1) {
        Collection<Noeud> candidates = carteTopo.getPopNoeuds().select(n.getCoord(),
            tolerance);
        candidates.remove(n);
        candidates.removeAll(toRemove);
        if (candidates.size() == 1) {
          Noeud candidate = candidates.iterator().next();
          // LOGGER.info("connecting node " + n + " (" + n.getDistance() +
          // ") to node " + candidate + " (" + candidate.getDistance() + ")");
          for (Arc a : new ArrayList<Arc>(n.getEntrants())) {
            candidate.addEntrant(a);
          }
          for (Arc a : new ArrayList<Arc>(n.getSortants())) {
            candidate.addSortant(a);
          }
          toRemove.add(n);
        }
      }
    }
    carteTopo.enleveNoeuds(toRemove);
    
    logger.info("Nb d'arcs = " + carteTopo.getListeArcs().size());
    logger.info("Nb de noeuds = " + carteTopo.getListeNoeuds().size());
    
    // -----------------------------------------------------------------------------------------
    // Jeu de données pour le calcul du plus court chemin 
    Groupe gpResult = null;
    Noeud nDepart = carteTopo.getPopNoeuds().get(4);
    Noeud nArrivee = carteTopo.getPopNoeuds().get(14);
    logger.info("Noeud départ = " + nDepart.getId());
    logger.info("Noeud arrivée = " + nArrivee.getId());
    
    // logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    // for (int i = 0; i < carteTopo.getListeNoeuds().size(); i++) {
      // logger.info("Noeud " + i + " = " + carteTopo.getListeNoeuds().get(i).getId());
    // }
    // logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    
    // Calcul de la topologie arc/noeuds (relations noeud initial/noeud final
    // pour chaque arete) a l'aide de la géometrie.
    // carteTopo.creeTopologieArcsNoeuds(0.1);
    
    
    // Calcul de la topologie de carte topologique (relations face gauche /
    // face droite pour chaque arete) avec les faces définies comme des
    // cycles du graphe.
    // carteTopo.creeTopologieFaces();
    // logger.info("Nombre de faces de la carte : " + carteTopo.getListeFaces().size());
    
    
    logger.info("------------------------------------------------------------------------");
    // On calcule le plus court chemin
    gpResult = nDepart.plusCourtChemin(nArrivee, 100);
    logger.info(gpResult.toString());
    
    
    
    // Calcul du chemin le plus court 26-39 en base
    /*String sqlGetShortestPath = " SELECT * "
        + " FROM shortest_path('SELECT id, source, target, x1, y1, x2, y2, cost, reverse_cost FROM contrib.roads', 26, 39, true, true); ";
    // vertex_id, edge_id, cost
    
    try {
      
      Class.forName("org.postgresql.Driver");
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://del1109s019:5432/dbunit", "dbunit", "dbunit");
      
      Statement st = connection.createStatement();
      ResultSet rs = st.executeQuery(sqlGetShortestPath);
      
      // On charge le resultat dans GeOxygene
      while (rs.next()) {
        System.out.println("vertex_id = " + rs.getString("vertex_id"));
      }
      
      rs.close();
      st.close();
      connection.close();
      
    } catch (Exception e) {
      fail("Erreur de chargement " + e.toString());
    }*/
    
    // On compare les 2 résultats

  }

}
