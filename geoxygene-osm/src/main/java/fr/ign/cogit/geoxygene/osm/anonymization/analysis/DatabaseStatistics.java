package fr.ign.cogit.geoxygene.osm.anonymization.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import fr.ign.cogit.geoxygene.osm.anonymization.algorithm.ChangesetData;
import fr.ign.cogit.geoxygene.osm.anonymization.algorithm.ContributorData;
import fr.ign.cogit.geoxygene.osm.anonymization.analysis.collections.DiscreetRangeMap;

/**
 * Cette classe est utilisée pour contenir 
 * des statistiques sur une base de données
 * issues d'OpenStreetMap afin qu'elle puissent
 * être comparées lors de l'anonymisation.
 * 
 * Les statistiques contiennent les informations 
 * sur les nombres des différents types d'éléments 
 * contenus dans la base ainsi que le nombre de 
 * changesets et de contributeurs.
 * 
 * Elle contient aussi des tables permettant de
 * représenter la répartition du nombre de contributions
 * de chaque type et de changesets par utilisateurs 
 * sous forme de DiscreetRangeMap<Integer>.
 * 
 * Enfin une HashMap<Long, ContributorData> permet de 
 * donner les informations sur l'ensembles des 
 * utilisateurs de la base comme les nombres de 
 * contributions de chaque types effectués.
 * 
 * @author Matthieu Dufait
 */
public class DatabaseStatistics implements Serializable {
  
  private static final long serialVersionUID = -6099829639908433675L;

  /**
   * Donne le nom du fichier par défaut pour enregistrer les statistiques.
   */
  public static final String staticStatsSaveFile = "data/statsSaveFile.oas";
  
  /**
   * Permet de stocker la répartition du nombre de changesets 
   * par utilisateur indiquant sur des intervalles de nombre de 
   * changesets, le nombre d'utilisateur ayant contribué d'autant 
   * de changesets.
   */
  private DiscreetRangeMap<Integer> repartitionChangesetContributions;
  /**
   * Permet de stocker la répartition du nombre de nodes 
   * par utilisateur indiquant sur des intervalles de nombre de 
   * nodes, le nombre d'utilisateur ayant contribué d'autant 
   * de nodes.
   */
  private DiscreetRangeMap<Integer> repartitionNodeContributions;
  /**
   * Permet de stocker la répartition du nombre de ways 
   * par utilisateur indiquant sur des intervalles de nombre de 
   * ways, le nombre d'utilisateur ayant contribué d'autant 
   * de ways.
   */
  private DiscreetRangeMap<Integer> repartitionWayContributions;
  /**
   * Permet de stocker la répartition du nombre de relations 
   * par utilisateur indiquant sur des intervalles de nombre de 
   * relations, le nombre d'utilisateur ayant contribué d'autant 
   * de relations.
   */
  private DiscreetRangeMap<Integer> repartitionRelationContributions;
  /**
   * Permet de stocker la répartition du nombre de tags comment 
   * par utilisateur indiquant sur des intervalles de nombre de 
   * tags comment, le nombre d'utilisateur ayant ajouté autant 
   * de tags comment.
   */
  private DiscreetRangeMap<Integer> repartitionTagComment;
  
  /**
   * Stocke le nombre d'utilisateurs dans la base.
   */
  private long nbUsers;
  /**
   * Stocke le nombre de changesets dans la base.
   */
  private long nbContributions;
  /**
   * Stocke le nombre de nodes dans la base.
   */
  private long nbNodes;
  /**
   * Stocke le nombre de ways dans la base.
   */
  private long nbWays;
  /**
   * Stocke le nombre de relations dans la base.
   */
  private long nbRelations;
  /**
   * Stocke le nombre de tags comment 
   * ajoutés aux changeset dans la base.
   */
  private long nbTagsComment;
  /**
   * Stocke les inforations sur les utilisateurs sous 
   * forme de ContributorData, la clé étant l'identifiant
   * numérique de l'utilisateur.
   */
  private HashMap<Long, ContributorData> contributorMap;
  
  // ajout de la répartition de comment
  
  /**
   * Permet de stocker le nom du fichier utiliser pour 
   * enregistrer les statistiques. 
   */
  private transient String statsSaveFile = staticStatsSaveFile;
  
  /**
   * Donne le nombre d'indiquateurs de différence implémentés.
   */
  private static final int DIFFERENCE_INDICATORS_COUNT = 4;
  /**
   * Détermine les options utilisées lors du calcule de différence
   */
  private transient boolean[] diffOptions;
  
  /**
   * Constructeur par recopie d'une autre DatabaseStatistics,
   * demande que toutes les classes en attributs aient elles-mêmes
   * un contructeur par recopie afin d'être copiées à leur tour. 
   * @param other la DatabaseStatistics à copier.
   */
  public DatabaseStatistics(DatabaseStatistics other) {
    repartitionChangesetContributions = new DiscreetRangeMap<>(other.repartitionChangesetContributions);
    repartitionNodeContributions = new DiscreetRangeMap<>(other.repartitionNodeContributions);
    repartitionWayContributions = new DiscreetRangeMap<>(other.repartitionWayContributions);
    repartitionRelationContributions = new DiscreetRangeMap<>(other.repartitionRelationContributions);
    repartitionTagComment = new DiscreetRangeMap<>(other.repartitionTagComment);
    this.nbUsers = other.nbUsers;
    this.nbContributions = other.nbContributions;
    this.nbNodes = other.nbNodes;
    this.nbWays = other.nbWays;
    this.nbRelations = other.nbRelations;
    this.nbTagsComment = other.nbTagsComment;
    this.contributorMap = new HashMap<>();
    diffOptions = new boolean[DIFFERENCE_INDICATORS_COUNT];
    for(int i = 0; i < DIFFERENCE_INDICATORS_COUNT; i++)
      diffOptions[i] = other.diffOptions[i];
    for(Long key : other.contributorMap.keySet()) {
      this.contributorMap.put(key, 
          new ContributorData(other.contributorMap.get(key)));
    }
  }
  
  /**
   * Constructeur par défaut initialisant les collections
   * vides mais n'initialise pas les autres valeurs à une valeur
   * par défaut. Il faut initialiser correctement les valeurs 
   * à l'aide des accesseurs à chacun des attributs. 
   */
  public DatabaseStatistics() {
    repartitionChangesetContributions = new DiscreetRangeMap<>();
    repartitionNodeContributions = new DiscreetRangeMap<>();
    repartitionWayContributions = new DiscreetRangeMap<>();
    repartitionRelationContributions = new DiscreetRangeMap<>();
    repartitionTagComment = new DiscreetRangeMap<>();
    this.contributorMap = new HashMap<>();
    diffOptions = new boolean[DIFFERENCE_INDICATORS_COUNT];
    for(int i = 0; i < DIFFERENCE_INDICATORS_COUNT; i++)
      diffOptions[i] = true;
  }
  
  /**
   * Fonction qui sauvegarde les statistiques courantes 
   * dans le fichier dont le nom est donné par l'attribut
   * statsSaveFile. Utilise pour cela les fonctions de 
   * serialization de Java. Le fichier est écrasé s'il 
   * existe déjà et le dossier parent donné par le 
   * nom du fichier est créé si nécessaire.
   */
  public void save() {
    File saveFile = new File(statsSaveFile);
    FileOutputStream fos;
    ObjectOutputStream oos;
    try {
      // création du fichier si nécessaire
      if(!saveFile.exists()) {
        // création du dossier parent si nécessaire
        if(!saveFile.getParentFile().exists())
          if (!saveFile.getParentFile().mkdir()) 
            throw new IOException("Failed to create directory " + saveFile.getParent());
        saveFile.createNewFile();
      }
      // sauvegarde dans le fichier
      fos = new FileOutputStream(saveFile);
      oos = new ObjectOutputStream(fos);
      oos.writeObject(this);
      
      oos.close();
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Charge le fichier donné par le nom de fichier
   * par défaut et renvoi une nouvelle DatabaseStatistics
   * définie par lecture de ce fichier.
   * @return une nouvelle DatabaseStatistics
   */
  public static DatabaseStatistics load() {
    return DatabaseStatistics.load(staticStatsSaveFile);
  }
  
  /**
   * Charge le fichier donné par le nom de fichier
   * donné en paramètre et renvoi une nouvelle 
   * DatabaseStatistics définie par lecture de ce fichier.
   * Renvoi une exception si le fichier n'existe pas 
   * ou si le fichier ne peut être chargé. 
   *
   * @param filename le nom du fichier à charger
   * @return une nouvelle DatabaseStatistics
   */
  public static DatabaseStatistics load(String filename) {
    File saveFile = new File(filename);
    FileInputStream fis;
    ObjectInputStream ois;
    DatabaseStatistics dbStatsReturn = null;
    try {
      fis = new FileInputStream(saveFile);
      ois = new ObjectInputStream(fis);
      // lecture du fichier
      dbStatsReturn = (DatabaseStatistics) ois.readObject();
      
      ois.close();
      fis.close();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
    return dbStatsReturn;
  }

  /**
   * Retourne le nom du fichier de 
   * sauvegarde courant.
   * @return statsSaveFiles
   */
  public String getStatsSaveFile() {
    return statsSaveFile;
  }

  /**
   * Change le nom du fichier de sauvegarde courant
   * par celui donné en paramètre, ne vérifie pas que
   * le nom est correct. 
   * @param statsSaveFile le nouveau chemin vers le fichier
   * de sauvegarde.
   */
  public void setStatsSaveFile(String statsSaveFile) {
    this.statsSaveFile = statsSaveFile;
  }

  /**
   * Retourne la table contenant les informations sur la
   * répartition des contributions. 
   * @return repartitionContributions
   */
  public DiscreetRangeMap<Integer> getRepartitionChangesetContributions() {
    return repartitionChangesetContributions;
  }

  /**
   * Modifie la table de la répartition des contributions avec
   * celle donnée en paramètre.
   * @param rep
   */
  public void setRepartitionChangesetContributions(DiscreetRangeMap<Integer> rep) {
    this.repartitionChangesetContributions = rep;
  }

  /**
   * Retourne la valeur du nombre de contributeur
   * @return nbContributeurs
   */
  public long getNbUsers() {
    return nbUsers;
  }

  /**
   * Modifie la valeur du nombre de contributeurs
   * avec la valeur donnée en paramètre. 
   * @param nbContributeurs nouveau nombre de contributeurs.
   */
  public void setNbUsers(long nbContributeurs) {
    this.nbUsers = nbContributeurs;
  }

  /**
   * Retourne la valeur dun nombre de changesets.
   * @return nbContributions
   */
  public long getNbContributions() {
    return nbContributions;
  }
  
  /**
   * Modifie la valeur du nombre de changesets 
   * avec la valeur donnée en paramètre.
   * @param nbContributions nouveau nombre de changesets.
   */
  public void setNbContributions(long nbContributions) {
    this.nbContributions = nbContributions;
  }
  
  /**
   * Retourne la valeur du nombre de nodes.
   * @return nbNodes.
   */
  public long getNbNodes() {
    return nbNodes;
  }

  /**
   * Modifie la valeur du nombre de nodes
   * avec la valeur donnée en paramètre.
   * @param nbNodes nouveau nombre de nodes.
   */
  public void setNbNodes(long nbNodes) {
    this.nbNodes = nbNodes;
  }

  /**
   * Retourne la valeur du nombre de ways.
   * @return nbWays.
   */
  public long getNbWays() {
    return nbWays;
  }

  /**
   * Modifie la valeur du nombre de ways 
   * avec la valeur donnée en paramètre.
   * @param nbWays nouveau nombre de ways.
   */
  public void setNbWays(long nbWays) {
    this.nbWays = nbWays;
  }

  /**
   * Retourne la valeur du nombre de relations.
   * @return nbRelations.
   */
  public long getNbRelations() {
    return nbRelations;
  }

  /**
   * Modifie la valeur du nombre de relations
   * avec la valeur donnée en paramètre.
   * @param nbRelations nouveau nombre de relations.
   */
  public void setNbRelations(long nbRelations) {
    this.nbRelations = nbRelations;
  }

  /**
   * Retourne la valeur du nombre de tags comment.
   * @return nbTagsComment.
   */
  public long getNbTagsComment() {
    return nbTagsComment;
  }

  /**
   * Modifie la valeur du nombre de tags comment
   * avec la valeur donnée en paramètre.
   * @param nbTagsComment nouveau nombre de tags comment.
   */
  public void setNbTagsComment(long nbTagsComment) {
    this.nbTagsComment = nbTagsComment;
  }

  /**
   * Modifie la référence de la table de répartition
   * des contributions de nodes avec la valeur donnée
   * en paramètre.
   * @param repartitionNode nouvelle table assignée à l'instance.
   */
  public void setRepartitionNodeContributions(DiscreetRangeMap<Integer> repartitionNode) {
    this.repartitionNodeContributions = repartitionNode;
    
  }
  
  /**
   * Retourne une référence vers la table de répartition
   * des contributions de nodes.
   * @return repartitionNodeContributions.
   */
  public DiscreetRangeMap<Integer> getRepartitionNodeContributions() {
    return repartitionNodeContributions;
  }

  /**
   * Modifie la référence de la table de répartition
   * des contributions de ways avec la valeur donnée
   * en paramètre.
   * @param repartitionWay nouvelle table assignée à l'instance.
   */
  public void setRepartitionWayContributions(DiscreetRangeMap<Integer> repartitionWay) {
    this.repartitionWayContributions = repartitionWay;
    
  }
  
  /**
   * Retourne une référence vers la table de répartition
   * des contributions de ways.
   * @return repartitionWayContributions.
   */
  public DiscreetRangeMap<Integer> getRepartitionWayContributions() {
    return repartitionWayContributions;
  }
  
  /**
   * Modifie la référence de la table de répartition
   * des contributions de relations avec la valeur donnée
   * en paramètre.
   * @param repartitionRelation nouvelle table assignée à l'instance.
   */
  public void setRepartitionRelationContributions(DiscreetRangeMap<Integer> repartitionRelation) {
    this.repartitionRelationContributions = repartitionRelation;
  }
  
  /**
   * Retourne une référence vers la table de répartition
   * des contributions de relations.
   * @return repartitionRelationContributions.
   */
  public DiscreetRangeMap<Integer> getRepartitionRelationContributions() {
    return repartitionRelationContributions;
  }
  
  /**
   * Retourne une référence vers la table de répartition
   * des tags comment sur les changesets.
   * @return repartitionTagComment
   */
  public DiscreetRangeMap<Integer> getRepartitionTagComment() {
    return repartitionTagComment;
  }

  /**
   * Modifie la référence de la table de répartition
   * des tags comment avec la valeur donnée en paramètre.
   * @param repartitionTagComment
   */
  public void setRepartitionTagComment(
      DiscreetRangeMap<Integer> repartitionTagComment) {
    this.repartitionTagComment = repartitionTagComment;
  }

  /**
   * Retourne la référene vers la table des 
   * données sur les contributeurs de la base. 
   * @return contributorMap
   */
  public HashMap<Long, ContributorData> getContributorMap() {
    return contributorMap;
  }

  /**
   * Modifie la valeur de la référence vers la 
   * table des contributeurs.
   * @param contributorMap nouvelle référence.
   */
  public void setContributorMap(HashMap<Long, ContributorData> contributorMap) {
    this.contributorMap = contributorMap;
  }
  
  /**
   * Retourne le tableau indiquant les 
   * options choisies pour calculer la différence
   * entre deux statistiques.
   * @return diffOptions
   */
  public boolean[] getDiffOptions() {
    return diffOptions;
  }

  /**
   * Modifie le tableau indiquant les 
   * options choisies pour calculer la différence
   * entre deux statistiques avec le tableau
   * donné en paramètre.
   * @param diffOptions
   */
  public void setDiffOptions(boolean[] diffOptions) {
    this.diffOptions = diffOptions;
  }

  /**
   * Retourne un double entre 0 et 1 qui calcule la différence
   * entre les données en calculant la différence entre
   * les données d'origine et les données actuelles pour 
   * chaque intervalles de répartitions.
   * A 0 les données sont identiques, pas de différence,
   * A 1 les données sont complètement différentes.
   * @param otherStats
   * @return
   */
  public double computeRepartitionDifference(DatabaseStatistics otherStats) {
    
    // initialisation de la moyenne
    double average = 0.0;
    int nbOptionsSelected = 0;
    int currentOption = 0;
    
    for(boolean b : diffOptions)
      if(b) nbOptionsSelected++;
    
    // difference sur la répartition des changesets
    // déprécié ici, les changesets ne change pas
    // avec l'échange de changeset.
    /*average += computeRepartitionDifference(
        this.repartitionChangesetContributions, 
        this.nbContributions,
        otherStats.repartitionChangesetContributions,
        otherStats.nbContributions);*/

    // difference sur la répartition des nodes
    if(diffOptions[currentOption++])
      average += computeRepartitionDifference(
          this.repartitionNodeContributions, 
          this.nbUsers,
          otherStats.repartitionNodeContributions,
          otherStats.nbUsers);

    // difference sur la répartition des relations
    if(diffOptions[currentOption++])
      average += computeRepartitionDifference(
          this.repartitionRelationContributions, 
          this.nbUsers,
          otherStats.repartitionRelationContributions,
          otherStats.nbUsers);

    // difference sur la répartition des ways
    if(diffOptions[currentOption++])
      average += computeRepartitionDifference(
          this.repartitionWayContributions, 
          this.nbUsers,
          otherStats.repartitionWayContributions,
          otherStats.nbUsers);

    // difference sur la répartition des tags comment
    if(diffOptions[currentOption++])
      average += computeRepartitionDifference(
          this.repartitionTagComment, 
          this.nbUsers,
          otherStats.repartitionTagComment,
          otherStats.nbUsers);
    
    return average/nbOptionsSelected;
  }
  
  /**
   * Calcule la différence entre deux tables de 
   * répartition en comptant pour chaque intervalle
   * de la table la différence absolue entre les deux
   * tables puis en divisant cette valeur par le 
   * nombre d'éléments des tables (on vérifie que les
   * tables ont le même nombre d'éléments et ne sont 
   * pas vides).  Le nombre retournée est donc
   * un réel entre 0.0 et 1.0 où 
   * 0.0 indique des tables identiques et
   * 1.0 indique des tables compètement différentes.
   * @throws IllegalArgumentException si les tables sont
   *    vide ou si les tables ont un monbre différent 
   *    d'éléments. 
   * @param ownMap la première table de répatition
   * @param ownNb le nombre d'élément de la table
   * @param otherMap l'autre table de répartition
   * @param otherNb le nombre d'élément de la table
   * @return un réel ente 0 et 1 indiquant la 
   *    différence entre les deux tables.
   */
  private static double computeRepartitionDifference(
      DiscreetRangeMap<Integer> ownMap, long ownNbContrib,
      DiscreetRangeMap<Integer> otherMap, long otherNbContrib) {
    if(ownNbContrib <= 0)
      throw new IllegalArgumentException("Divisor ownNbContrib equals 0.");
    if(ownNbContrib != otherNbContrib)
      throw new IllegalArgumentException("Sizes differ.");
    
    // pour chaque intervalle
    // on compte le nombre de différence
    // et on divise nbDifference/nbContributions
    double totalDiff = 0.0;
    Integer firstVal;
    
    for(Long range : ownMap.keySet()) {
      firstVal = ownMap.getValue(range);
      if(firstVal != null) {
        if(otherMap.getValue(range) == null)
          totalDiff += firstVal;
        else
          totalDiff += Math.abs(firstVal - otherMap.getValue(range));
      }
    }
    return totalDiff/ownNbContrib;
  }

  /**
   * Cettre fonction calcule la différence 
   * entre les valeurs d'origine et les valeurs
   * courantes dans la table des contributeurs.
   * Pour cela la fonction parcours l'ensemble
   * de la table et pour chaque contributeur
   * on calcule la différence entre ses valeurs
   * d'orine est ses valeur courante. On calcule
   * ensuite le ratio de valeurs modifiées
   * et on en fait la moyenne.
   * @return valeur entre 0.0 et 1.0
   */
  public double computeUserContributionDifference() {
    
    double diffNode, diffWay, diffRelation, diffTagComment;
    diffNode = diffWay = diffRelation = diffTagComment = 0.0;
    int nbOptionsSelected = 0;
    int currentOption = 0;
    
    for(boolean b : diffOptions)
      if(b) nbOptionsSelected++;
    
    for(Long key : contributorMap.keySet()) {
      ContributorData cd = contributorMap.get(key);
      diffNode += Math.abs(cd.getOriginalUserNbNode()-cd.getUserNbNode());
      diffWay += Math.abs(cd.getOriginalUserNbWay()-cd.getUserNbWay());
      diffRelation += Math.abs(cd.getOriginalUserNbRelation()-cd.getUserNbRelation());
      diffTagComment += Math.abs(cd.getOriginalUserNbTagComment()-cd.getUserNbTagComment());
    }
    
    double diffRetour = 0.0;
    // sélection des différences à calculer en fonction des options
    if(diffOptions[currentOption++])
      diffRetour += diffNode/nbNodes;
    if(diffOptions[currentOption++])
      diffRetour += diffWay/nbWays;
    if(diffOptions[currentOption++])
      diffRetour += diffRelation/nbRelations;
    if(diffOptions[currentOption++])
      diffRetour += diffTagComment/nbTagsComment;
    diffRetour = diffRetour / 2 / nbOptionsSelected;
    return diffRetour;
  }
  
  /**
   * Crée une copie des statistiques courantes 
   * et calcule l'effet de l'échange entre 
   * changeset1 et changeset2 dans cette statistiques.
   * Retorune ensuite la DatabaseStatistics nouvellement
   * créée. Le recalcule est fait sur les tables de 
   * répartition avec la fonction recalculateMapElement 
   * et sur la table des contributeur avec la fonction
   * updateContributorStats.
   * @param changeset1
   * @param changeset2
   * @return une DatabaseStatistics qui calcule les 
   *    effets de l'échange.
   */
  public DatabaseStatistics computeSwap(ChangesetData changeset1, ChangesetData changeset2) {
    DatabaseStatistics swapStats = new DatabaseStatistics(this);
    
    // récupération des contributors à modifier
    ContributorData contributor1 = swapStats.getContributorMap().get(changeset1.getuId());
    ContributorData contributor2 = swapStats.getContributorMap().get(changeset2.getuId());
    // recalcul des nodes
    recalculateMapElement(swapStats.repartitionNodeContributions,
        changeset1.getNbNode(), contributor1.getUserNbNode(),
        changeset2.getNbNode(), contributor2.getUserNbNode());
    // recalcul des ways
    recalculateMapElement(swapStats.repartitionWayContributions,
        changeset1.getNbWay(), contributor1.getUserNbWay(),
        changeset2.getNbWay(), contributor2.getUserNbWay());
    // recalcul des relations
    recalculateMapElement(swapStats.repartitionRelationContributions,
        changeset1.getNbRelation(), contributor1.getUserNbRelation(),
        changeset2.getNbRelation(), contributor2.getUserNbRelation());
    // recalcul des tags comment
    recalculateMapElement(swapStats.repartitionTagComment,
        changeset1.hasTagComment() ? 1:0, contributor1.getUserNbTagComment(),
        changeset2.hasTagComment() ? 1:0, contributor2.getUserNbTagComment());
    
    // mise à jour des statistiques sur les contributeurs
    swapStats.updateContributorStats(changeset1, contributor1, changeset2, contributor2);
    
    return swapStats;
  }
  
  /**
   * Calcule l'effet d'un échange de chageset 
   * sur les données des contributeur par simple
   * recalcul des atttribut des ContributorData en
   * paramètre. La fonction modifie les attributs
   * subissant un changement dans les paramètres.
   * @param changeset1
   * @param user1
   * @param changeset2
   * @param user2
   */
  private void updateContributorStats(ChangesetData changeset1, 
      ContributorData user1, ChangesetData changeset2, ContributorData user2) {
      // mise à jour du nombre de nodes contribués 
      user1.setUserNbNode(user1.getUserNbNode() 
          - changeset1.getNbNode() + changeset2.getNbNode());
      user2.setUserNbNode(user2.getUserNbNode() 
          - changeset2.getNbNode() + changeset1.getNbNode());
      // mise à jour du nombre de ways contribués 
      user1.setUserNbWay(user1.getUserNbWay() 
          - changeset1.getNbWay() + changeset2.getNbWay());
      user2.setUserNbWay(user2.getUserNbWay() 
          - changeset2.getNbWay() + changeset1.getNbWay());
      // mise à jour du nombre de relations contribuées 
      user1.setUserNbRelation(user1.getUserNbRelation() 
          - changeset1.getNbRelation() + changeset2.getNbRelation());
      user2.setUserNbRelation(user2.getUserNbRelation() 
          - changeset2.getNbRelation() + changeset1.getNbRelation());
      // mise à jour du nombre de tags comment 
      user1.setUserNbTagComment(user1.getUserNbTagComment() 
          - (changeset1.hasTagComment() ? 1:0) + (changeset2.hasTagComment() ? 1:0));
      user2.setUserNbTagComment(user2.getUserNbTagComment() 
          - (changeset2.hasTagComment() ? 1:0) + (changeset1.hasTagComment() ? 1:0));
  }
  
  /**
   * Recalcule les tables de répartition
   * de contributions d'éléments après 
   * l'échange des valeur de changeset 
   * données en paramètre. Pour cela
   * la fonction repère les intervalles des
   * contributeurs et mette à jour les valeurs
   * des intervalles si les contributeurs 
   * changent de d'intervalles.
   * @param mapElement table à mettre à jour
   * @param changesetVal1 
   *    nombre de contribution du type de la 
   *    table dans le changeset 1
   * @param userVal1
   *    nombre de contributions du type 
   *    de la table pour le contributeur 1
   * @param changesetVal2
   *    nombre de contributions du type 
   *    de la table dans le changeset 2
   * @param userVal2
   *    nombre de contributions du type 
   *    de la table pour le contributeur 2
   */
  private void recalculateMapElement(DiscreetRangeMap<Integer> mapElement,
      long changesetVal1, long userVal1, long changesetVal2, long userVal2) {
    // pour chaque changeset
    // decremente l'ancien intervalle donné par userVal
    mapElement.setValue(userVal1, mapElement.getValue(userVal1)-1);
    mapElement.setValue(userVal2, mapElement.getValue(userVal2)-1);
    // increment le nouvel intervalle donné par userValX + changesetValY - changesetValX
    long intervalle = userVal1 + changesetVal2 - changesetVal1;
    // TODO remove if not needed - permet d'afficher en cas de pb
    if(mapElement.getValue(intervalle) ==  null)
      System.out.println("userVal1 : "+ userVal1 + " changesetVal2 : " + changesetVal2 + " changesetVal1 : "+ changesetVal1);
    mapElement.setValue(intervalle, mapElement.getValue(intervalle)+1);
    intervalle = userVal2 + changesetVal1 - changesetVal2;
    // TODO remove if not needed - permet d'afficher en cas de pb
    if(mapElement.getValue(intervalle) ==  null)
      System.out.println("userVal2 : "+ userVal2 + " changesetVal2 : " + changesetVal2 + " changesetVal1 : "+ changesetVal1);
    mapElement.setValue(intervalle, mapElement.getValue(intervalle)+1);
  }
  
  
  @Override
  public String toString() {
    return "DatabaseStatistics [repartitionChangesetContributions="
        + repartitionChangesetContributions + ", \nrepartitionNodeContributions="
        + repartitionNodeContributions + ", \nrepartitionWayContributions="
        + repartitionWayContributions + ", \nrepartitionRelationContributions="
        + repartitionRelationContributions + ", \nrepartitionTagComment="
        + repartitionTagComment + ", \nnbUsers=" + nbUsers + ", nbContributions="
        + nbContributions + ", nbNodes=" + nbNodes + ", nbWays=" + nbWays
        + ", nbRelations=" + nbRelations + ", nbTagsComment=" + nbTagsComment
        + "]";
  }
}
