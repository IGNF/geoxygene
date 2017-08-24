package fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util;

/**
 * Enumération de différents types d'édition
 * qui nous permettent de définir le contenu 
 * des modifications apportées à la base que 
 * l'on applique à un élément. 
 * d'OpenStreetMap.
 * 
 * Pour chaque type on va avoir de multiple
 * types d'édition décrivant comment l'objet
 * a été modifié avec des types de simples
 * modifications (ADD_X_TAGS par exemple) 
 * et des types plus composites en cas de 
 * modification de différents types 
 * (UPDATE_X_TAGS_AND_GEOM).
 * @author Matthieu Dufait
 */
public enum OSMEditType {
  CREATE_SIMPLE_NODE,
  CREATE_TAGGED_NODE,
  MODIFY_NODE_GEOM,
  ADD_NODE_TAGS,
  UPDATE_NODE_TAGS,
  DELETE_NODE_TAGS,
  UPDATE_NODE_TAGS_AND_GEOM,
  DELETE_NODE,
  
  CREATE_SIMPLE_WAY,
  CREATE_TAGGED_WAY,
  MODIFY_WAY_NODES,
  ADD_WAY_TAGS,
  UPDATE_WAY_TAGS,
  DELETE_WAY_TAGS,
  UPDATE_WAY_TAGS_AND_GEOM,
  DELETE_WAY,
  
  CREATE_SIMPLE_RELATION,
  CREATE_TAGGED_RELATION,
  ADD_RELATION_MEMBERS,
  DELETE_RELATION_MEMBERS,
  UPDATE_RELATION_MEMBERS,
  ADD_RELATION_TAGS,
  UPDATE_RELATION_TAGS,
  DELETE_RELATION_TAGS,
  UPDATE_RELATION_TAGS_AND_MEMBERS,
  DELETE_RELATION,
  
  UNCLASSIFIED,;
}
