package fr.ign.cogit.geoxygene.osm.anonymization.junk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.OSMAnonymizedChangeSet;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.OSMAnonymizedContributor;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.OSMAnonymizedNode;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.OSMAnonymizedObject;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.OSMAnonymizedResource;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.OSMAnonymizedWay;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.ObjectPrimaryKey;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.ElementDbAccess;

/**
 * Classe servant à représenter l'ensemble 
 * d'une base de données OpenStreetMap 
 * anonymisée avec les dates
 * 
 * Deprecated on 12th of June 2017
 * 
 * @author Matthieu Dufait
 */
@Deprecated
public class OSMAnonymizedDatabaseWithDates {
  private Map<Long, OSMAnonymizedChangeSet> changesetSet;
  private Map<Long, OSMAnonymizedContributor> contributorSet;
  private Map<ObjectPrimaryKey, OSMAnonymizedObject> elementSet;
  
  public OSMAnonymizedDatabaseWithDates() {
    changesetSet = new HashMap<Long, OSMAnonymizedChangeSet>();
    contributorSet = new HashMap<Long, OSMAnonymizedContributor>();
    elementSet = new HashMap<ObjectPrimaryKey, OSMAnonymizedObject>();
  }
  
  /**
   * Fonction qui rempli la base de donnée courante
   * à partir d'une base de données PostGIS dans 
   * laquelle les données sont enregistrées sous forme
   * d'éléments OSM: node, way, relation et relatiomemember.
   */
  public void readFromElementPostGISDB(ElementDbAccess access) {
    // map servant à enregistrer les dates des éléments d'un changeset 
    // pour le calcul de la durée et de la date initiale
    HashMap<Long, ArrayList<Date>> changesetDates = new HashMap<Long, ArrayList<Date>>();
    
    try {
      System.out.println("Lecture des nodes sur la base");
      ResultSet results = access.readNodes();
    
      // création des nodes
      System.out.println("Création des objets nodes");
      this.createNodes(changesetDates, results);      

      System.out.println("Lecture des ways sur la base");
      results = access.readWays();

      System.out.println("Création des objets ways");
      this.createWays(changesetDates, results);
      
      // fermeture des resource précédement ouverte
      ElementDbAccess.instance.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    System.out.println("Chargement des données terminé");
  }
  
  private void createWays(HashMap<Long, ArrayList<Date>> changesetDates,
      ResultSet results) throws SQLException {
    long id, uid, changeset; 
    Long[] composedOf;
    int version;
    HashMap<String, String> tags;
    List<OSMAnonymizedObject> wayNodes;
    OSMAnonymizedWay way;
    OSMAnonymizedResource resource;
    OSMAnonymizedChangeSet changesetObject;
    ObjectPrimaryKey pk;
    OSMAnonymizedObject osmObject;
    
    while(results.next()) {
      // récupération des valeurs
      id = results.getLong("id");
      version = results.getInt("vway");
      uid = results.getLong("uid");
      changeset = results.getLong("changeset");
      composedOf = (Long[]) results.getArray("composedof").getArray();
      
      // ajout des dates liées au changeset
      updateChangesetDates(changesetDates, changeset, results);
      // initialisation des tags
      tags = initTagsMap(results);
      
      // création et récupération des objets
      changesetObject = changesetAndContributorCreation(changeset, uid);
      
      wayNodes = new ArrayList<OSMAnonymizedObject>();
      
      for(Long idNode : composedOf)
        wayNodes.add(elementSet.get(
            new ObjectPrimaryKey(OSMPrimitiveType.node, idNode)));
      way = new OSMAnonymizedWay(wayNodes);
      
      // création de la resource 
      resource = new OSMAnonymizedResource(way, id, version, null, 
          changesetObject, tags, null, null, null);
      
      // récupération du OSMAnonymizedObject
      pk = new ObjectPrimaryKey(way.getOSMPrimitiveType(), id);      
      osmObject = elementSet.get(pk);
      
      // cas où on crée un nouvel Objet
      if(osmObject == null) {
        osmObject = new OSMAnonymizedObject(resource);
        elementSet.put(pk, osmObject);
        // cas où il existe
      } else {
        osmObject.addNewVersion(resource);
      }
    }
  }
  
  /**
   * Fonction qui gère la création des nodes
   * @param changesetDates
   * @param results
   * @throws SQLException
   */
  private void createNodes(HashMap<Long, ArrayList<Date>> changesetDates,
      ResultSet results) throws SQLException {
    long id, uid, changeset;
    double lon, lat;
    int version;
    HashMap<String, String> tags;
    OSMAnonymizedNode node;
    OSMAnonymizedResource resource;
    OSMAnonymizedChangeSet changesetObject;
    ObjectPrimaryKey pk;
    OSMAnonymizedObject osmObject;
    
    while(results.next()) {
      // récupération des valeurs
      id = results.getLong("id");
      version = results.getInt("vnode");
      uid = results.getLong("uid");
      changeset = results.getLong("changeset");
      // ajout des dates liées au changeset
      updateChangesetDates(changesetDates, changeset, results);
      lat = results.getDouble("lat");
      lon = results.getDouble("lon");
      tags = initTagsMap(results);
      
      // création et récupération des objets
      changesetObject = changesetAndContributorCreation(changeset, uid);
      
      node = new OSMAnonymizedNode(lat, lon);
      
      // récupération du OSMAnonymizedObject
      pk = new ObjectPrimaryKey(node.getOSMPrimitiveType(), id);      
      osmObject = elementSet.get(pk);
      
      // création de la resource 
      resource = new OSMAnonymizedResource(node, id, version, null, 
          changesetObject, tags, null, null, null);
      
      // cas où on crée un nouvel Objet
      if(osmObject == null) {
        osmObject = new OSMAnonymizedObject(resource);
        elementSet.put(pk, osmObject);
        // cas où il existe
      } else {
        osmObject.addNewVersion(resource);
      }
    }
  }
  
  private void updateChangesetDates(HashMap<Long, ArrayList<Date>> changesetDates,
      long changeset, ResultSet results) throws SQLException {
    if(!changesetDates.containsKey(changeset))
      changesetDates.put(changeset, new ArrayList<Date>());
    changesetDates.get(changeset).add(results.getDate("datemodif"));    
  }
  
  private HashMap<String, String> initTagsMap(ResultSet results) throws SQLException {
    String[] tempArray = (String[]) results.getArray("hstore_to_array").getArray();
    HashMap<String, String> tags = new HashMap<String, String>();
    for(int cpt = 0; cpt < tempArray.length; cpt+=2)
      tags.put(tempArray[cpt], tempArray[cpt+1]);
    return tags;
  }
  
  /*private OSMAnonymizedResource previousFinder(int version, OSMPrimitiveType type, long id) {
    // on recherche s'il existe un élément précédent
    // en considérant que les éléments sont lus dans 
    // l'ordre grace à l'ORDER BY dans la requete
    OSMAnonymizedResource precedent;
    int cpt = version - 1;
    do {
      precedent = elementSet.get(new ElementPrimaryKey(type, id, cpt));
      cpt--;
    } while (precedent == null && cpt > 1);
    return precedent;
  }*/
  
  private OSMAnonymizedChangeSet changesetAndContributorCreation(long changeset, long uid) {
    OSMAnonymizedChangeSet changesetObject;
    OSMAnonymizedContributor contributorObject;
    // récupération du changeset
    if(changesetSet.containsKey(changeset))
      changesetObject = changesetSet.get(changeset);
    else {
      // récupération du contributeur
      if(contributorSet.containsKey(uid))
        contributorObject = contributorSet.get(uid);
      else
        contributorObject = new OSMAnonymizedContributor(uid);
      
      //création de l'objet changeset
      changesetObject = new OSMAnonymizedChangeSet(changeset, contributorObject, 
          null, null, null, null);
    }
    return changesetObject;
  }
  
  public long nbElement(OSMPrimitiveType type) {
    int retour = 0;
    for(ObjectPrimaryKey pk : elementSet.keySet())
      if(pk.getElementType() == type)
        retour += elementSet.get(pk).getVersionsCount();
    return retour;
  }
  
  public void printTest() {
    //for(ObjectPrimaryKey l : elementSet.keySet())
    //  System.out.println(elementSet.get(l));
    
    System.out.println(changesetSet.size());
    System.out.println(contributorSet.size());
    System.out.println("nb ways: "+nbElement(OSMPrimitiveType.way));
    System.out.println("nb nodes: "+nbElement(OSMPrimitiveType.way));
    for(ObjectPrimaryKey key: elementSet.keySet())
      System.out.println(elementSet.get(key));
  }
}
