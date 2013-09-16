package fr.ign.cogit.geoxygene.api.feature;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 * @param <Feat>
 */
public interface IPopulation<Feat extends IFeature> extends
    IFeatureCollection<Feat> {
  /**
   * Renvoie l'identifiant. NB: l'ID n'est remplit automatiquement que si la
   * population est persistante
   */
  @Id
  public abstract int getId();

  /** Affecte une valeur a l'identifiant */
  public abstract void setId(int I);

  /**
   * Chargement des éléments persistants d'une population. Tous les éléments de
   * la table correspondante sont chargés.
   */
  public abstract void chargeElements();

  /**
   * Chargement des éléments persistants d'une population qui intersectent une
   * géométrie donnée. ATTENTION: la table qui stocke les éléments doit avoir
   * été indexée dans le SGBD. ATTENTION AGAIN: seules les populations avec une
   * géométrie sont chargées.
   */
  public abstract void chargeElementsPartie(IGeometry geom);

  /**
   * Chargement des éléments persistants d'une population. Tous les éléments de
   * la table correspondante sont chargés. Les données doivent d'abord avoir été
   * indexées. PB: TRES LENT !!!!!!!
   */
  public abstract void chargeElementsProches(IPopulation<Feat> pop, double dist);

  /**
   * Renvoie une population avec tous les éléments de this situés à moins de
   * "dist" des éléments de la population Travail sur un index en mémoire (pas
   * celui du SGBD). Rmq : Fonctionne avec des objets de géométrie quelconque
   */
  public abstract IPopulation<Feat> selectionElementsProchesGenerale(
      IPopulation<Feat> pop, double dist);

  /**
   * Renvoie une population avec tous les éléments de this situés à moins de
   * "dist" des éléments de la population pop.
   */
  public abstract IPopulation<Feat> selectionLargeElementsProches(
      IPopulation<Feat> pop, double dist);

  /**
   * Chargement des éléments persistants d'une population qui intersectent une
   * zone d'extraction donnée. ATTENTION: la table qui stocke les éléments doit
   * avoir été indexée dans le SGBD. ATTENTION AGAIN: seules les populations
   * avec une géométrie sont chargées.
   */
  public abstract void chargeElementsPartie(IExtraction zoneExtraction);

  /**
   * Detruit la population si elle est persistante, MAIS ne détruit pas les
   * éléments de cette population (pour cela vider la table correspondante dans
   * le SGBD).
   */
  public abstract void detruitPopulation();

  public abstract String getNom();

  public abstract void setNom(String S);

  /**
   * Booléen spécifiant si la population est persistente ou non (vrai par
   * défaut).
   */
  public abstract boolean getPersistant();

  /**
   * Booléen spécifiant si la population est persistente ou non (vrai par
   * défaut).
   */
  public abstract void setPersistant(boolean b);

  /** Récupère le DataSet de la population. */
  @ManyToOne
  public abstract IDataSet<?> getDataSet();

  /** Définit le DataSet de la population, et met à jour la relation inverse. */
  public abstract void setDataSet(IDataSet<?> O);

  /** Ne pas utiliser, necessaire au mapping OJB */
  public abstract void setDataSetID(int I);

  /** Ne pas utiliser, necessaire au mapping OJB */
  @Transient
  public abstract int getDataSetID();

  /**
   * Enlève, ET DETRUIT si il est persistant, un élément de la liste des
   * elements de la population, met également à jour la relation inverse, et
   * eventuellement l'index.
   * <p>
   * <b>NB :</b> différent de remove (hérité de FT_FeatureCollection) qui ne
   * détruit pas l'élément.
   */
  public abstract void enleveElement(Feat O);

  /**
   * Crée un nouvel élément de la population, instance de sa classe par défaut,
   * et l'ajoute à la population.
   * <p>
   * Si la population est persistante, alors le nouvel élément est rendu
   * persistant dans cette méthode <b>NB :</b> différent de add (hérité de
   * FT_FeatureCollection) qui ajoute un élément déjà existant.
   */
  public abstract Feat nouvelElement();

  /**
   * Crée un nouvel élément de la population (avec la géoémtrie geom), instance
   * de sa classe par défaut, et l'ajoute à la population.
   * <p>
   * Si la population est persistante, alors le nouvel élément est rendu
   * persistant dans cette méthode <b>NB :</b> différent de add (hérité de
   * FT_FeatureCollection) qui ajoute un élément déjà existant.
   */
  public abstract Feat nouvelElement(IGeometry geom);

  /**
   * Crée un nouvel élément de la population, instance de sa classe par défaut,
   * et l'ajoute à la population. La création est effectuée à l'aide du
   * constructeur spécifié par les tableaux signature(classe des objets du
   * constructeur), et param (objets eux-mêmes).
   * <p>
   * Si la population est persistante, alors le nouvel élément est rendu
   * persistant dans cette méthode
   * <p>
   * <b>NB :</b> différent de add (hérité de FT_FeatureCollection) qui ajoute un
   * élément déjà existant.
   * @param signature
   * @param param
   * @return Feature
   */
  public abstract Feat nouvelElement(Class<?>[] signature, Object[] param);

  // ////////////////////////////////////////////////
  // Copie de population
  /**
   * Copie la population passée en argument dans la population traitée (this).
   * <p>
   * <b>NB :<b>
   * <ul>
   * <li>1/ ne copie pas l'eventuelle indexation spatiale,
   * <li>2/ n'affecte pas la population au DataSet de la population à copier.
   * <li>3/ mais recopie les autres infos: élements, classe, FlagGeom, Nom et
   * NomClasse
   * </ul>
   * @param populationACopier
   */
  public abstract void copiePopulation(IPopulation<Feat> populationACopier);

  // //////////////////////////////////////////////////////////////////////////////
  /**
   * Complète Population.chargeElements(). - On vérifie que la population
   * correspond à une classe du schéma conceptuel du DataSet. Si non, on initie
   * les populations du DataSet en y incluant celle-ci. - Chaque FT_Feature
   * chargé est renseigné avec sa population (donc son featureType).
   */
  public abstract void chargeElementsAvecMetadonnees();
}
