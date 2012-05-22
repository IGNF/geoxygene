package fr.ign.cogit.geoxygene.api.feature;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import fr.ign.cogit.geoxygene.api.feature.type.GF_Constraint;
import fr.ign.cogit.geoxygene.api.schema.IProduct;
import fr.ign.cogit.geoxygene.api.schema.dataset.DatasetConceptualSchema;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public interface IDataSet<CS extends DatasetConceptualSchema<?, ?, ?, ?, ?, ?>> {
  /**
   * Renvoie l'identifiant
   * @return l'identifiant
   */
  @Id
  public abstract int getId();

  /**
   * Affecte un identifiant.
   * @param Id un identifiant
   */
  public abstract void setId(int Id);

  /**
   * Chargement des instances des populations persistantes d'un jeu de données.
   */
  public abstract void chargeElements();

  /**
   * Chargement des instances des populations persistantes d'un jeu de données
   * qui intersectent une géométrie donnée (extraction géométrique).
   * @param geom géométrie utilisée pour l'extraction géométrique.
   */
  public abstract void chargeElementsPartie(IGeometry geom);

  /**
   * Chargement des instances des populations persistantes d'un jeu de données
   * qui intersectent une géométrie donnée. ATTENTION: les tables qui stockent
   * les éléments doivent avoir été indexées dans Oracle. ATTENTION AGAIN:
   * seules les populations avec une géométrie sont chargées.
   * @param zoneExtraction zone utilisée pour l'extraction géométrique
   */
  public abstract void chargeElementsPartie(IExtraction zoneExtraction);

  /**
   * Méthode de chargement pour les test. Elle est un peu tordue dans le
   * paramétrage mais permet de ne charger que ce qu'on veut. Elle permet de
   * charger les instances des populations persistantes d'un jeu de données qui
   * : - intersectent une géométrie donnée (extraction géométrique), - ET qui
   * appartiennent à certains thèmes et populations précisés en entrée.
   * 
   * @param geom Définit la zone d'extraction.
   * 
   * @param themes Définit les sous-DS du DS à charger. Pour le DS lui-même, et
   *          pour chaque sous-DS, on précise également quelles populations sont
   *          chargées. Ce paramètre est une liste de liste de String composée
   *          comme suit (si la liste est nulle on charge tout) :
   *          <ul>
   *          <li>1/ Le premier élément est soit null (on charge alors toutes
   *          les populations directement sous le DS), soit une liste contenant
   *          les noms des populations directement sous le DS que l'on charge
   *          (si la liste est vide, on ne charge rien).
   *          <li>2/ Tous les autres éléments sont des listes (une pour chaque
   *          sous-DS) qui contiennent chacune d'abord le nom d'un sous-DS que
   *          l'on veut charger, puis soit rien d'autre si on charge toutes les
   *          populations du sous-DS, soit le nom des populations du sous-DS que
   *          l'on veut charger.
   *          </ul>
   * 
   *          <b>NB :</b> Attention aux majuscules et aux accents.
   *          <p>
   *          <b>EXEMPLE</b> de parametre themes pour un DS repréentant la
   *          BDCarto, et spécifiant qu'on ne veut charger que les troncon et
   *          les noeud du thème routier, et les troncons du thème hydro, mais
   *          tout le thème ferré.
   *          <p>
   *          <b>theme = {null, liste1, liste2, liste3}</b>, avec :
   *          <ul>
   *          <li>null car il n'y a pas de population directement sous le DS
   *          BDCarto,
   *          <li>liste1 = {"Routier","Tronçons de route", "Noeuds routier"},
   *          <li>liste2 = {"Hydrographie","Tronçons de cours d'eau"},
   *          <li>liste3 = {"Ferré"}.
   *          </ul
   */
  public abstract void chargeExtractionThematiqueEtSpatiale(IGeometry geom,
      List<List<String>> themes);

  /**
   * Pour un jeu de données persistant, détruit le jeu de données, ses thèmes et
   * ses objets populations.
   * <p>
   * <b>ATTENTION :</b> ne détruit pas les éléments des populations (pour cela
   * vider les tables Oracle).
   */
  public abstract void detruitJeu();

  /**
   * Booléen spécifiant si le thème est persistant ou non (vrai par défaut).
   * <p>
   * <b>NB :</b> si un jeu de données est non persistant, tous ses thèmes sont
   * non persistants. Mais si un jeu de données est persistant, certains de ses
   * thèmes peuvent ne pas l'être.
   * <p>
   * <b>ATTENTION :</b> pour des raisons propres à OJB, même si la classe
   * DataSet est concrète, il n'est pas possible de créer un objet PERSISTANT de
   * cette classe, il faut utiliser les sous-classes.
   * @return vrai si le jeu de donné est persistant, faux sinon
   */
  public abstract boolean getPersistant();

  /**
   * Booléen spécifiant si le thème est persistant ou non (vrai par défaut).
   * <p>
   * <b>NB :</b> si un jeu de données est non persistant, tous ses thèmes sont
   * non persistants. Mais si un jeu de données est persistant, certains de ses
   * thèmes peuvent ne pas l'être.
   * <p>
   * <b>ATTENTION :</b> pour des raisons propres à OJB, même si la classe
   * DataSet est concrète, il n'est pas possible de créer un objet PERSISTANT de
   * cette classe, il faut utiliser les sous-classes.
   * @param b vrai si le jeu de donné est persistant, faux sinon
   */
  public abstract void setPersistant(boolean b);

  public abstract String getOjbConcreteClass();

  public abstract void setOjbConcreteClass(String S);

  public abstract String getNom();

  public abstract void setNom(String S);

  public abstract String getTypeBD();

  public abstract void setTypeBD(String S);

  public abstract String getModele();

  public abstract void setModele(String S);

  public abstract String getZone();

  public abstract void setZone(String S);

  public abstract String getDate();

  public abstract void setDate(String S);

  public abstract String getCommentaire();

  public abstract void setCommentaire(String S);

  /** Récupère la liste des DataSet composant this. */
  @OneToMany
  public abstract List<IDataSet<?>> getComposants();

  /**
   * Définit la liste des DataSet composant le DataSet, et met à jour la
   * relation inverse.
   */
  public abstract void setComposants(List<IDataSet<?>> L);

  /** Récupère le ième élément de la liste des DataSet composant this. */
  public abstract IDataSet<?> getComposant(int i);

  /**
   * Ajoute un objet à la liste des DataSet composant le DataSet, et met à jour
   * la relation inverse.
   */
  public abstract void addComposant(IDataSet<?> O);

  /**
   * Enlève un élément de la liste DataSet composant this, et met à jour la
   * relation inverse.
   */
  public abstract void removeComposant(IDataSet<?> O);

  /**
   * Vide la liste des DataSet composant this, et met à jour la relation
   * inverse.
   */
  public abstract void emptyComposants();

  /**
   * Recupère le DataSet composant de this avec le nom donné.
   * @param nom nom du dataset à récupérer
   * @return le DataSet composant de this avec le nom donné.
   */
  public abstract IDataSet<?> getComposant(String nom);

  /** Récupère le DataSet dont this est composant. */
  @ManyToOne
  public abstract IDataSet<?> getAppartientA();

  /**
   * Définit le DataSet dont this est composant., et met à jour la relation
   * inverse.
   */
  public abstract void setAppartientA(IDataSet<?> O);

  /** Ne pas utiliser, necessaire au mapping OJB */
  public abstract void setAppartientAID(int I);

  /** Ne pas utiliser, necessaire au mapping OJB */
  @Transient
  public abstract int getAppartientAID();

  /** Récupère la liste des populations en relation. */
  @OneToMany
  public abstract List<IPopulation<? extends IFeature>> getPopulations();

  /**
   * Définit la liste des populations en relation, et met à jour la relation
   * inverse.
   */
  public abstract void setPopulations(List<IPopulation<? extends IFeature>> L);

  /** Récupère le ième élément de la liste des populations en relation. */
  public abstract IPopulation<? extends IFeature> getPopulation(int i);

  /**
   * Ajoute un objet à la liste des populations en relation, et met à jour la
   * relation inverse.
   */
  public abstract void addPopulation(IPopulation<? extends IFeature> O);

  /**
   * Enlève un élément de la liste des populations en relation, et met à jour la
   * relation inverse.
   * @param O élément à enlever
   */
  public abstract void removePopulation(IPopulation<? extends IFeature> O);

  /**
   * Vide la liste des populations en relation, et met à jour la relation
   * inverse.
   */
  public abstract void emptyPopulations();

  /**
   * Recupère la population avec le nom donné.
   * @param nom nom de la population à récupérer
   * @return la population avec le nom donné.
   */
  public abstract IPopulation<? extends IFeature> getPopulation(String nom);

  /** Récupère la liste des extractions en relation. */
  // @OneToMany
  @Transient
  public abstract List<IExtraction> getExtractions();

  /** Définit la liste des extractions en relation. */
  public abstract void setExtractions(List<IExtraction> L);

  /** Ajoute un élément de la liste des extractions en relation. */
  public abstract void addExtraction(IExtraction O);

  /**
   * @return the produit
   */
  // @OneToOne
  @Transient
  public abstract IProduct<?> getProduit();

  /**
   * @param produit the produit to set
   */
  public abstract void setProduit(IProduct<?> produit);

  /**
   * Affecte le schema conceptuel correspondant au jeu de donnees
   * @param schema le schema conceptuel correspondant au jeu de donnees
   */
  public abstract void setSchemaConceptuel(CS schema);

  /**
   * Renvoie le schema conceptuel correspondant au jeu de donnees
   * @return le schema conceptuel correspondant au jeu de donnees
   */
  // @OneToOne
  @Transient
  public abstract CS getSchemaConceptuel();

  /**
   * Renvoie la liste des contraintes d'integrite s'appliquant a ce jeu de
   * donnees
   * @return liste des contraintes d'integrite s'appliquant a ce jeu de donnees
   */
  // @OneToMany
  @Transient
  public abstract List<GF_Constraint> getContraintes();

  /**
   * Affecte la liste des contraintes d'integrite s'appliquant a ce jeu de
   * donnees
   * @param contraintes la liste des contraintes d'integrite s'appliquant a ce
   *          jeu de donnees
   */
  public abstract void setContraintes(List<GF_Constraint> contraintes);

  /***************************************************************************
   * Partie Utilisation du DataSet : les données peuvent être accedées via des
   * FT_Collection et via des Populations.
   **************************************************************************/
  /**
   * initialise la liste des populations du jeu en fonction du schéma
   * conceptuel. Les données ne sont pas chargées.
   */
  public abstract void initPopulations();

  /**
   * @param nom nom du featuretype
   * @return population dont le featuretype correspond au nom donné
   */
  public abstract IPopulation<? extends IFeature> getPopulationByFeatureTypeName(
      String nom);
}
