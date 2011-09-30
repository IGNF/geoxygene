package fr.ign.cogit.geoxygene.api.feature;

import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.api.spatial.toporoot.ITopology;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationRole;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface IFeature {
  /**
   * Renvoie l'identifiant. NB: l'identifiant n'est rempli automatiquement que
   * pour les objets persistants
   * @return l'identifiant
   */
  public abstract int getId();

  /**
   * Affecte un identifiant (ne pas utiliser si l'objet est persistant car cela
   * est automatique)
   * @param Id l'identifiant
   */
  public abstract void setId(int Id);

  /**
   * Renvoie une geometrie.
   * @return la géométrie de l'objet
   */
  public abstract IGeometry getGeom();

  /**
   * Affecte une geometrie et met à jour les éventuels index concernés.
   * @param g nouvelle géométrie de l'objet
   */
  public abstract void setGeom(IGeometry g);

  /**
   * Renvoie true si une geometrie existe, false sinon.
   * @return vrai si une geometrie existe, faux sinon.
   */
  public abstract boolean hasGeom();

  /**
   * Renvoie la topologie de l'objet.
   * @return la topologie de l'objet
   */
  public abstract ITopology getTopo();

  /**
   * Affecte la topologie de l'objet.
   * @param t la topologie de l'objet
   */
  public abstract void setTopo(ITopology t);

  /**
   * Renvoie true si une topologie existe, false sinon.
   * @return vrai si l'objet possède une topologie, faux sinon
   */
  public abstract boolean hasTopo();

  /**
   * Clonage avec clonage de la geometrie.
   * @throws CloneNotSupportedException
   */
  public abstract IFeature cloneGeom() throws CloneNotSupportedException;

  /** Renvoie toutes les FT_FeatureCollection auquelles appartient this. */
  public abstract List<IFeatureCollection<IFeature>> getFeatureCollections();

  /** Renvoie la i-eme FT_FeatureCollection a laquelle appartient this. */
  public abstract IFeatureCollection<IFeature> getFeatureCollection(int i);

  /** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
  public abstract List<IFeature> getCorrespondants();

  /** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
  public abstract void setCorrespondants(List<IFeature> L);

  /** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
  public abstract IFeature getCorrespondant(int i);

  /** Lien bidirectionnel n-m des éléments vers eux même. */
  public abstract void addCorrespondant(IFeature O);

  /** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
  public abstract void removeCorrespondant(IFeature O);

  /** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
  public abstract void clearCorrespondants();

  /** Lien bidirectionnel n-m des éléments vers eux même. */
  public abstract void addAllCorrespondants(Collection<IFeature> c);

  /**
   * Renvoie les correspondants appartenant a la FT_FeatureCollection passee en
   * parametre.
   */
  public abstract Collection<IFeature> getCorrespondants(
      IFeatureCollection<? extends IFeature> pop);

  /**
   * @return the population
   */
  public abstract IPopulation<IFeature> getPopulation();

  /**
   * @param population the population to set
   */
  public abstract void setPopulation(IPopulation<IFeature> population);

  /**
   * Affecte le feature type de l'objet
   * @param featureType le feature type de l'objet
   */
  public abstract void setFeatureType(FeatureType featureType);

  /**
   * Utilitaire pour retrouver le type d'un objet (passe par la population)
   * 
   * @return le featureType de ce feature
   */
  public abstract FeatureType getFeatureType();

  /**
   * Methode reflexive pour recupérer la valeur d'un attribut donné en paramètre
   * 
   * @param attribute
   * @return la valeur de l'attribut sous forme d'Object
   */
  public abstract Object getAttribute(AttributeType attribute);

  /**
   * Méthode reflexive pour affecter à un feature une valeur d'attribut pour
   * l'attributeType donné en paramètre. Il est inutile de connaître la classe
   * d'implémentation du feature ni le nom de la méthode setter à invoquer.
   * 
   * @param attribute
   * @param valeur
   */
  public abstract void setAttribute(AttributeType attribute, Object valeur);

  /**
   * Methode reflexive pour recupérer les features en relation par
   * l'intermédiaire du role donné en paramètre. Attention, cette méthode
   * suppose que tous les éléments en relation ont été chargés en mémoire. Ce
   * n'est pas toujours le cas avec OJB : pour des raisons de performances, le
   * concepteur du fichier de mapping y représente parfois les relations de
   * façon unidirectionnelle. Si la méthode renvoie une liste vide, vérifiez
   * votre fichier de mapping. Si vous ne souhaitez pas le modifier, explorez la
   * relation dans l'autre sens (par exemple, avec un fichier de mapping donné,
   * le role "troncon route a pour noeud initial" sera explorable mais pas le
   * role "noeud routier à pour arcs sortants".
   * 
   * @param ftt le type d'objets dont on veut la liste
   * @param role le rôle que l'on souhaite explorer
   * @return la liste des features en relation
   */
  public abstract List<? extends IFeature> getRelatedFeatures(
      GF_FeatureType ftt, AssociationRole role);

  // //////////////////////////////////////////////////////////////////////////
  // /
  // ///////////////// Ajout Nathalie
  // //////////////////////////////////////////////////////////////////////////
  // /
  /**
   * Methode pour recupérer la valeur d'un attribut dont le nom est donné en
   * paramètre
   * 
   * @param nomAttribut
   * @return la valeur de l'attribut sous forme d'Object
   */
  public abstract Object getAttribute(String nomAttribut);

  /**
   * Methode pour recupérer les features en relation par l'intermédiaire du role
   * donné en paramètre. Attention, cette méthode suppose que tous les éléments
   * en relation ont été chargés en mémoire. Ce n'est pas toujours le cas avec
   * OJB : pour des raisons de performances, le concepteur du fichier de mapping
   * y représente parfois les relations de façon unidirectionnelle. Si la
   * méthode renvoie une liste vide, vérifiez votre fichier de mapping. Si vous
   * ne souhaitez pas le modifier, explorez la relation dans l'autre sens (par
   * exemple, avec un fichier de mapping donné, le role "troncon route a pour
   * noeud initial" sera explorable mais pas le role "noeud routier à pour arcs
   * sortants".
   * 
   * @param nomFeatureType
   * @param nomRole
   * @return la liste des features en relation avec nomFeatureType via nomRole
   */
  public abstract List<? extends IFeature> getRelatedFeatures(
      String nomFeatureType, String nomRole);

  /**
   * Renvoie la représentation liée à l'objet - Renvoie null si non définie
   * @return la représentation liée à l'objet - Renvoie null si non définie
   */
  public abstract Representation getRepresentation();

  /**
   * Affecte une représentation à un objet
   * @param rep représentation à affecter au FT_Feature
   */
  public abstract void setRepresentation(Representation rep);

  /**
   * Marqueur de suppression d'un objet (utilisé par exemple en généralisation).
   * @return vrai si l'objet a été supprimé, faux sinon
   */
  public abstract boolean isDeleted();

  /**
   * Affecte le marqueur de suppression d'un objet (utilisé par exemple en
   * généralisation).
   * @param deleted vrai si l'objet a été supprimé, faux sinon
   */
  public abstract void setDeleted(boolean deleted);

  /**
   * Renvoie vrai si l'objet intersecte l'envelope, faux sinon
   * @param env envelope
   * @return vrai si l'objet intersecte l'envelope, faux sinon
   */
  public abstract boolean intersecte(IEnvelope env);

  @Override
  public abstract String toString();
}
