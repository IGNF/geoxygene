package fr.ign.cogit.geoxygene.osm.anonymization.junk;

import java.util.HashMap;

/**
 * Classe permettant d'enregistrer la structure
 * d'une base de données d'éléments OSM afin
 * de pouvoir connaitre les noms de tables et 
 * de colonnes pour construire les requêtes permettant
 * de lire les données.
 * 
 * Il s'agit d'un tableau associatif 
 * 
 * 
 * A la date du 6 juin 2017 cette classe 
 * est inutile (une autre méthode a été choisie) 
 * ainsi que mal placée (devrait être dans db 
 * au lieu de datamodel/util) ainsi il faut soit 
 * la placer dans junk soit la supprimer.
 * 
 * Ce n'est pas fait car il ne faut pas oublier
 * le role d'origine de la classe avant de la déplacer.
 * 
 * 
 * @author Matthieu Dufait
 */
@Deprecated
public class PostGISElementDBStructure {
  public static final HashMap<String, String> nepalStructure;
  static {
    nepalStructure = new HashMap<String, String>();
    nepalStructure.put("TABLE_NODE", "node");
    nepalStructure.put("TABLE_WAY", "way");
    nepalStructure.put("TABLE_RELATION", "relation");
    nepalStructure.put("TABLE_MEMBER", "relationmember");
    //nepalStructure.put("TABLE_TAG", "*UNDEFINED");              //
    //nepalStructure.put("TABLE_WAYNODES", "*UNDEFINED");         //
    
    nepalStructure.put("NODE_OSMID", "id");
    nepalStructure.put("NODE_VERSION", "vnode");
    //nepalStructure.put("NODE_VISIBLE", "*UNDEFINED");           //
    nepalStructure.put("NODE_USERID", "uid");
    nepalStructure.put("NODE_CHANGESET", "changeset");
    nepalStructure.put("NODE_TIMESTAMP", "datemodif");
    nepalStructure.put("NODE_LAT", "lat");
    nepalStructure.put("NODE_LON", "lon");
    nepalStructure.put("NODE_TAGS", "tags");
    
    nepalStructure.put("WAY_OSMID", "id");
    nepalStructure.put("WAY_VERSION", "vway");
    //nepalStructure.put("WAY_VISIBLE", "*UNDEFINED");            //
    nepalStructure.put("WAY_USERID", "uid");
    nepalStructure.put("WAY_CHANGESET", "changeset");
    nepalStructure.put("WAY_TIMESTAMP", "datemodif");
    nepalStructure.put("WAY_NODELIST", "composedof");
    nepalStructure.put("WAY_TAGS", "tags");
    
    nepalStructure.put("RELATION_OSMID", "id");
    nepalStructure.put("RELATION_VERSION", "vrel");
    //nepalStructure.put("RELATION_VISIBLE", "*UNDEFINED");       //
    nepalStructure.put("RELATION_USERID", "uid");
    nepalStructure.put("RELATION_CHANGESET", "changeset");
    nepalStructure.put("RELATION_TIMESTAMP", "datemodif");
    nepalStructure.put("RELATION_TAGS", "tags");    

    nepalStructure.put("MEMBER_REFID", "idmb");
    //nepalStructure.put("MEMBER_RELATIONID", "*UNDEFINED");      //
    nepalStructure.put("MEMBER_RELATIONIDVERSION", "idrel");
    nepalStructure.put("MEMBER_TYPE", "typemb");
    nepalStructure.put("MEMBER_ROLE", "rolemb");
    
    //nepalStructure.put("TAG_KEY", "*UNDEFINED");
    //nepalStructure.put("TAG_VALUE", "*UNDEFINED");
    
  }
  
}
