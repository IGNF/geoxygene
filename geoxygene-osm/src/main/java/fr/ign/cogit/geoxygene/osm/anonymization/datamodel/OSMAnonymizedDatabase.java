package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.ObjectPrimaryKey;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.ElementDbAccess;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.RoleMembre;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.TypeRelation;

/**
 * Classe servant à représenter l'ensemble 
 * d'une base de données OpenStreetMap 
 * anonymisée.
 * 
 * Mise à jour au 2 août 2017: 
 * Le modèle de données a de multiples 
 * problèmes issus du manque de recul et 
 * à des besoins liés à ces classes trop
 * flous lors de leurs développement. 
 * En particulier la classe ElementDbAccess
 * ne suit pas la même architecture que les
 * autres classes nécessitant un accès à la
 * base de données en séparant ses fonctionnalités
 * avec OSMAnonymizedDatabase.
 * Certaines classes représentant les objets 
 * OSM manquent de fonctionnalités ou ne 
 * sont plus à jour avec les modifications 
 * apportées par la pré-anonymisation de 
 * la base. Les classes anonymisées n'ont 
 * pas d'interface pour être lues. 
 * @author Matthieu Dufait
 */
public class OSMAnonymizedDatabase {
  private Map<Long, OSMAnonymizedChangeSet> changesetSet;
  private Map<Long, OSMAnonymizedContributor> contributorSet;
  private Map<ObjectPrimaryKey, OSMAnonymizedObject> elementSet;
  
  public OSMAnonymizedDatabase() {
    changesetSet = new HashMap<Long, OSMAnonymizedChangeSet>();
    contributorSet = new HashMap<Long, OSMAnonymizedContributor>();
    elementSet = new HashMap<ObjectPrimaryKey, OSMAnonymizedObject>();
  }
  
  // TODO relocaliser le remplissage de la base hors de la classe
  
  /**
   * Fonction qui rempli la base de donnée courante
   * à partir d'une base de données PostGIS type népal dans 
   * laquelle les données sont enregistrées sous forme
   * d'éléments OSM: node, way, relation et relatiomemember.
   */
  public void readFromElementPostGISDB(ElementDbAccess access) {
    try {
      System.out.println("Lecture des nodes sur la base");
      ResultSet results = access.readNodes();
    
      // création des nodes
      System.out.println("Création des objets nodes");
      this.createNodes(results);

      System.out.println("Lecture des ways sur la base");
      results = access.readWays();

      System.out.println("Création des objets ways");
      this.createWays(results);

      System.out.println("Lecture des relations sur la base");
      results = access.readRelations();

      System.out.println("Création des objets relations");
      this.createRelations(results);

      System.out.println("Lecture des membres sur la base");
      results = access.readMembers();

      System.out.println("Création des objets membres");
      this.createMembers(results);
      
      // fermeture des resource précédement ouverte
      ElementDbAccess.instance.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    System.out.println("Chargement des données terminé");
  }
  
  private OSMAnonymizedObject searchElementFromIdAndVersion(
      long idAndVersion, OSMPrimitiveType type) {
    OSMAnonymizedObject obj;
    String stringIdVersion = ""+idAndVersion;
    ObjectPrimaryKey pk;
    int len = stringIdVersion.length();
    
    for(int versionSize = len-1; versionSize > 1; versionSize--) {
      pk = new ObjectPrimaryKey(type, 
          Long.parseLong(stringIdVersion.substring(0, versionSize)));
      obj = elementSet.get(pk);
      if(obj != null)
        if(obj.hasPossibleVersion(idAndVersion))
          return obj;
    }
    return null;
  }
  
  private void createMembers(ResultSet results) throws SQLException {
    long idrel, idmb; 
    OSMAnonymizedRelationMember member;
    RoleMembre role;
    OSMPrimitiveType type;
    OSMAnonymizedObject ref, relationObj;
    OSMAnonymizedRelation relation;
    ObjectPrimaryKey pkRef;
    
    while(results.next()) {
      // récupération des valeurs
      idrel = results.getLong("idrel");
      idmb = results.getLong("idmb");
      type = OSMPrimitiveType.getTypeFromString(results.getString("typemb"));
      role = RoleMembre.valueOfTexte(results.getString("rolemb"));
      
      // récupartion des références 
      pkRef = new ObjectPrimaryKey(type, idmb);
      
      ref = elementSet.get(pkRef);
      
      // création de l'objet member
      member = new OSMAnonymizedRelationMember(role, ref, type);
      
      relationObj = searchElementFromIdAndVersion(idrel, OSMPrimitiveType.relation);
      relation = (OSMAnonymizedRelation) 
          relationObj.getPossibleVersion(idrel).getGeom();
      relation.getMembers().add(member);
      
    }
  }
  
  private void createRelations(ResultSet results) throws SQLException {
    long id, uid, changeset; 
    int version;
    String typeS;
    HashMap<String, String> tags;
    OSMAnonymizedRelation relation;
    OSMAnonymizedResource resource;
    OSMAnonymizedChangeSet changesetObject;
    ObjectPrimaryKey pk;
    OSMAnonymizedObject osmObject;
    TypeRelation type;
    
    while(results.next()) {
      // récupération des valeurs
      id = results.getLong("id");
      version = results.getInt("vrel");
      uid = results.getLong("uid");
      changeset = results.getLong("changeset");
      
      // initialisation des tags
      tags = initTagsMap(results);
      
      // création et récupération des objets
      changesetObject = changesetAndContributorCreation(changeset, uid);
      
      typeS = tags.get(OSMRelation.TAG_TYPE);
      if(typeS != null)
        type = TypeRelation.valueOfTexte(typeS);
      else
        type = TypeRelation.NON_DEF;
      
      relation = new OSMAnonymizedRelation(type);
      
      // création de la resource 
      resource = new OSMAnonymizedResource(relation, id, version, null, 
          changesetObject, tags, null, null, null);
      
      // récupération du OSMAnonymizedObject
      pk = new ObjectPrimaryKey(relation.getOSMPrimitiveType(), id);      
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
   * Fonction qui à partir d'un ResultSet devant 
   * faire une requête de selection sur les 
   * ways crée les Objets en base et les ajoute
   * aux Maps.
   * @param results
   * @throws SQLException
   */
  private void createWays(ResultSet results) throws SQLException {
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
  private void createNodes(ResultSet results) throws SQLException {
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
      
      lat = results.getDouble("lat");
      lon = results.getDouble("lon");
      tags = initTagsMap(results);
      
      // création et récupération des objets
      // objet du changeset
      changesetObject = changesetAndContributorCreation(changeset, uid);
      
      // objet du node
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
      // sinon création
      else {
        contributorObject = new OSMAnonymizedContributor(uid);
        contributorSet.put(uid, contributorObject);
      }      
      
      //création de l'objet changeset
      changesetObject = new OSMAnonymizedChangeSet(changeset, contributorObject, 
          null, null, null, null);
      changesetSet.put(changeset, changesetObject);
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
    
    System.out.println("nb changeset: "+changesetSet.size());
    System.out.println("nb contibutors: "+contributorSet.size());
    System.out.println("nb nodes: "+nbElement(OSMPrimitiveType.node));
    System.out.println("nb ways: "+nbElement(OSMPrimitiveType.way));
    System.out.println("nb relations: "+nbElement(OSMPrimitiveType.relation));
   // for(ObjectPrimaryKey key: elementSet.keySet())
   //   System.out.println(elementSet.get(key));
    
   /* ArrayList<Long> al = new ArrayList<>();
    for(Long l: contributorSet.keySet())
      al.add(contributorSet.get(l).getId());
    
    Collections.sort(al);
    for(Long l: al)
      System.out.println(l);*/
  }
}
