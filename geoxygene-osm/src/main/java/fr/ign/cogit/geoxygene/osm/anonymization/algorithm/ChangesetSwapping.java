package fr.ign.cogit.geoxygene.osm.anonymization.algorithm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.osm.anonymization.analysis.DatabaseStatistics;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.PostgresAccess;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.ChangesetSwappingQueries;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.ChangesetSwappingQueriesType;

/**
 * Classe contenant les fonction permettant
 * de effectuer le processus d'échange de 
 * changeset pour anonymiser la base de 
 * contributions d'OpenStreetMap.
 * 
 * Elle utilise comme d'autre classe du package 
 * anonymization un accès à la base de données
 * avec la classe PostgresAccess ainsi qu'une 
 * table de requête avec la classe ChangesetSwappingQueries.
 * Elle a aussi besoin d'une instance de la classe
 * DatabaseStatistics qui indique les caractéristiques
 * de la base de données avant l'anonymisation et
 * qui sert de comparaison avec l'état de la base au 
 * cours du processus. La fonction permettant de 
 * lancer l'anonymisation est la fonction swapping().
 * 
 * @author Matthieu Dufait
 */

// TODO ajouter une incrémentation pour éviter les traitements infinis
public class ChangesetSwapping {
  private Logger logger = Logger.getLogger(ChangesetSwapping.class.getName());

  // XXX Option possiblement à changer 
  //private static final double SWAPPING_DIFFERENCE_LIMIT = 1;
  //private static final double SWAPPING_DIFFERENCE_LIMIT = 0.1;
  private static final double SWAPPING_DIFFERENCE_LIMIT = 0.01;
  
  /**
   * La classe PostgresAccess gère la connexion à la base de donnée.
   */
  private PostgresAccess dbAccess;
  
  /**
   * Contient les statistiques de la base de données 
   * avant le traitement d'échange des contributions.
   */
  private DatabaseStatistics originalDbStats;
  /**
   * Contient les statistiques de la base de données
   * mises à jour à chaque échange de contributions,
   * permet d'être comparé aux statistiques d'origine
   * pour s'assurer de conserver des bases proches.
   */
  private DatabaseStatistics currentDbStats;
  
  /**
   * Classe contenant les données sur les requêtes qui 
   * sont utilisées dans les fonctions de cette classe.
   * La gestion de cet objet est à la totale discrétion 
   * de son utilisateur et peut provoquer de graves problèmes
   * si elle est mal utilisée. En particulier risque de pertes 
   * ou de fuites de données de la base de données.
   */
  private ChangesetSwappingQueries queries;
  
  /**
   * Permet d'enregistrer les résultats de l'alogortihme
   * d'anonymisation pour étudier son impact sur la base.
   */
  private ChangesetSwappingResults swappingResults;
  
  /**
   * Donne le nombre d'échanges souhaités 
   * pendant le traitement.
   */
  private int swappingGoal; 
  
  /**
   * Indique si on refuse les changeset ayant 
   * le même nombre d'éléments.
   */
  private boolean refusSimilaire = true;

  /**
   * Constructeur permettant d'initialiser les attributs
   * avec les paramètres.
   * Initilialise les résultats par défaut et
   * le nombre d'échanges souhaité pour un nombre 
   * de changeset n dans la base à (n / log10 n).
   * @param dbAccess
   * @param dbStats
   * @param queries
   */
  public ChangesetSwapping(PostgresAccess dbAccess, DatabaseStatistics dbStats,
      ChangesetSwappingQueries queries) {
    super();
    this.dbAccess = dbAccess;
    this.originalDbStats = dbStats;
    this.currentDbStats = new DatabaseStatistics(dbStats);
    this.queries = queries;
    // XXX Option possiblement à changer 
    this.swappingGoal = (int) (dbStats.getNbContributions()/Math.log10(dbStats.getNbContributions()));
    //this.swappingGoal = (int) (dbStats.getNbContributions());
    this.swappingResults = new ChangesetSwappingResults();
  }
  
  /**
   * Fonction principale de l'algorithme d'échange de progression.
   * Cette fonction permet de sélectionner les changesets à échanger
   * et les envoie à la fonction changesetSwap(ChangesetData, ChangesetData)
   * qui retourne un booléen indiquant si l'échange est réussit. 
   * La sélection se fait dans une boucle qui s'arrête lorsque le 
   * nombre d'échanges voulus est atteint. La fonction enregistre aussi
   * les données sur le comportement de l'algorithme dans le
   * ChangesetSwappingResults de l'instance et affiche la progression
   * tous les 10% du but.
   */
  public void swapping() {
    logger.log(Level.INFO, "Démarrage du processus d'échange de contributions");
    // réinitialisation des résultats.
    this.swappingResults = new ChangesetSwappingResults();
    this.swappingResults.startTime();
    Set<ChangesetPair> triedPairs = new HashSet<>();
    ChangesetData changeset1, changeset2;
    ArrayDeque<ChangesetData> pileChangeset = new ArrayDeque<>();
    
    while(swappingResults.getSuccessfulSwapCount() < this.swappingGoal) {
      // mise à jour du compte d'itérations
      swappingResults.incIterationCount();
     
      // sélection des changesets
      do {
        // récupération des changesets si on en a plus assez
        if(pileChangeset.size() < 2)
          pileChangeset = getRandomChangeset();
        // sélection du premier changeset
        changeset1 = pileChangeset.removeFirst();
        swappingResults.incChangesetSelectedCount();
        // sélection du second changeset
        changeset2 = pileChangeset.removeFirst();
        swappingResults.incChangesetSelectedCount();
      } while(changesetRefuse(changeset1, changeset2, pileChangeset, triedPairs));
      
      // si le swap est réussit on incrémente la valeur
      if(changesetSwap(changeset1, changeset2)) {
        swappingResults.incSuccessfulSwapCount();
        triedPairs.add(new ChangesetPair(changeset1.getId(),changeset2.getId()));
      }
      else 
        swappingResults.incUnsuccessfulSwapCount();
      
      // affichage de la progression
      if(swappingResults.getSuccessfulSwapCount() % (this.swappingGoal/10) == 0)
        logger.log(Level.INFO, "Traitement "+swappingResults.getSuccessfulSwapCount()+
            " sur "+this.swappingGoal);
    }
    // affichage des résultats
    this.swappingResults.endTime();
    logger.log(Level.INFO, "Processus d'échange terminé");
  }

  /**
   * Retourne true si
   *    (les changesets ont le même contributeur
   *    ou
   *    les nombres de node et de way sont égaux)
   *    et il reste des changesets dans la pile.
   * @param changeset1
   * @param changeset2
   * @param pileChangeset
   * @param triedPairs 
   * @param triedPairs 
   * @return
   */
  private boolean changesetRefuse(ChangesetData changeset1,
      ChangesetData changeset2, ArrayDeque<ChangesetData> pileChangeset, 
      Set<ChangesetPair> triedPairs) {
    // on refuse si les changesets sont issus du même contributeur
    boolean retour = changeset1.getuId() == changeset2.getuId();
    boolean temp;
    
    // on refuse si les changesets ont le même nombre de way et de node
    if(refusSimilaire) {
      temp = changeset1.getNbNode() == changeset2.getNbNode() && 
          changeset1.getNbWay() == changeset2.getNbWay();
      if(temp)
        swappingResults.incTooSimilar();
      retour = retour || temp;
    }
    
    // on refuse si la pair a déjà été échangée
    temp = triedPairs.contains(
        new ChangesetPair(changeset1.getId(),changeset2.getId()));
    if(temp)
      swappingResults.incAlreadyTried();
    retour = retour || temp;
    
    return retour;
  }
  
  /**
   * Fonction qui lit la base de données pour obtenir 
   * une pile de changeset sous forme de ChangesetData.
   * La requête appelée est 
   * ChangesetSwappingQueriesType.GET_RANDOM_CHANGESET 
   * elle doit retourner 5 colonnes permettant d'initialiser
   * le ChangesetData. Une nouvelle ArrayDeque est 
   * initialisée et remplie pendant le déroulement de
   * cette fonction, elle est ensuite retournée.
   * Attention si la requête retourne moins de 2
   * changesets, la fonction swapping() entrera en boucle
   * infinie.
   * @return une nouvelle ArrayDeque<ChangesetData>
   *    remplie avec les données de la base.
   */
  private ArrayDeque<ChangesetData> getRandomChangeset() {
    Connection conn = null;
    Statement stmt = null;
    ResultSet results = null;
    ArrayDeque<ChangesetData> retour = new ArrayDeque<ChangesetData>();
    ChangesetData data;
    try {
      conn = dbAccess.getConnection();
      stmt = conn.createStatement();
      // exécution de la commande de récupération des changesets.
      results = stmt.executeQuery(
          queries.get(ChangesetSwappingQueriesType.GET_RANDOM_CHANGESET));
      while(results.next())
      {
        // création de l'objet ChangesetDate représentant 
        // un changeset et initialisation des attributs.
        data = new ChangesetData();
        data.setId(results.getLong(1));
        data.setuId(results.getLong(2));
        data.setNbNode(results.getLong(3));
        data.setNbWay(results.getLong(4));
        data.setNbRelation(results.getLong(5));
        data.setHasTagComment(results.getBoolean(6));
        // ajout à la file de retour
        retour.add(data);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      if(results != null) 
        try{ results.close(); } catch (Exception e) { }
      if(stmt != null) 
        try{ stmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
    return retour;
  }
  
  /**
   * Fonction qui après génération de statistiques incluant l'échange
   * entre les changesets donnés en paramètre détermine si
   * l'échange est acceptable et appelle la fonction 
   * actualSwap(ChangesetData, ChangesetData) qui effectue
   * l'échange dans la base de données. 
   * Retourne un booléen indiquant si l'échange est réussi,
   * il est false si l'échange n'est pas accepté ou  
   * si la requête SQL n'a pas aboutie.
   * @param changeset1 
   * @param changeset2
   * @return un booléen indiquant si l'échange est réussi.
   */
  private boolean changesetSwap(ChangesetData changeset1, ChangesetData changeset2) {
    DatabaseStatistics newStats = currentDbStats.computeSwap(changeset1, changeset2);
    
    double repartitionDifference = newStats.computeRepartitionDifference(originalDbStats);
    double contributionDifference = newStats.computeUserContributionDifference();
    if(repartitionDifference < SWAPPING_DIFFERENCE_LIMIT) {
      if(this.actualSwap(changeset1, changeset2)) {
        swappingResults.addRepartitionDifferenceEvolution(repartitionDifference);
        swappingResults.addUserContributionDifferenceEvolution(contributionDifference);
        currentDbStats = newStats;
        return true;
      }
      // si on arrive ici la requête à échouée.
      swappingResults.incFailedQuery();
    }    
    
    return false;
  }
  
  /**
   * Fonction appelant la requête SQL permettant 
   * d'échanger les contributeurs de deux changesets.
   * Revoie un booléen indiquant si une exception
   * est levée durant cette fonction.
   * @param changeset1
   * @param changeset2
   * @return un booléen indiquant si l'opération est réussie.
   */
  private boolean actualSwap(ChangesetData changeset1, ChangesetData changeset2) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    try {
      conn = dbAccess.getConnection();
      pstmt = conn.prepareStatement(
          queries.get(ChangesetSwappingQueriesType.SWAP_CHANGESET));
      
      pstmt.setLong(1, changeset1.getId());
      pstmt.setLong(2, changeset2.getuId());
      pstmt.setLong(3, changeset2.getId());
      pstmt.setLong(4, changeset1.getuId());
      pstmt.setLong(5, changeset1.getId());
      pstmt.setLong(6, changeset2.getId());
      
      pstmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    } finally {
      if(pstmt != null) 
        try{ pstmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
    return true;
  }

  /**
   * Retourne l'instance de ChangesetSwappingResults qui
   * indique le comportement du processus d'échange 
   * de contributions.
   * @return swappingResults
   */
  public ChangesetSwappingResults getSwappingResults() {
    return swappingResults;
  }
  
  /**
   * Retourne la valeur indiquant si on 
   * refuse les changesets ayant le même
   * nombre de ways et de nodes.
   * @return refusSimilaire
   */
  public boolean isRefusSimilaire() {
    return refusSimilaire;
  }

  /**
   * Moifie la valeur indiquant si on 
   * refuse les changesets ayant le même
   * nombre de ways et de nodes avec
   * la valeur donnée en paramètre. 
   * @param refusSimilaire
   */
  public void setRefusSimilaire(boolean refusSimilaire) {
    this.refusSimilaire = refusSimilaire;
  }



  /**
   * Classe permettant de 
   * stocker une paire de long
   * représentant les id de deux 
   * changeset pour être utilisé
   * dans la collection indiquant
   * les paires précédemment essayées.
   * @author Matthieu Dufait
   */
  class ChangesetPair {
    final long id1,id2;
    
    public ChangesetPair(long id1, long id2) {
      this.id1 = id1;
      this.id2 = id2;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id1 ^ (id1 >>> 32));
      result = prime * result + (int) (id2 ^ (id2 >>> 32));
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ChangesetPair other = (ChangesetPair) obj;
      if (id1+id2 != other.id1+other.id2)
        return false;
      if (id1*id2 != other.id1*other.id2)
        return false;
      return true;
    }
  }
}
