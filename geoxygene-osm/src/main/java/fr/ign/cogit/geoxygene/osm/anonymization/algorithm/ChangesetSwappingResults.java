package fr.ign.cogit.geoxygene.osm.anonymization.algorithm;

import java.util.ArrayList;

/**
 * Classe contenant les informations sur la 
 * progression du processus d'échange de 
 * changesets permettant à la fois 
 * de indiquer si le processus est terminé
 * et de pouvoir afficher comment se comporte
 * le processus pour déterminer si on peut 
 * le controler pour obtenir l'anonymisation 
 * souhaitée.
 * @author Matthieu Dufait
 */

public class ChangesetSwappingResults {
  /**
   * Indique le temps de lancement du processus.
   */
  private long startingTime;
  /**
   * Indique le temps de fin du processus.
   */
  private long endingTime;
  
  /**
   * Compte combien d'échanges ont été 
   * acceptés et effectués.
   */
  private long successfulSwapCount;
  
  /**
   * Compte combien d'échanges ont été 
   * effectivement refusés.
   */
  private int unsuccessfulSwapCount;
  /**
   * Compte le nombre de changeset 
   * selectionné pour les échanges.
   */
  private long changesetSelectedCount;
  /**
   * Compte le nombre d'itération de la boucle
   * principale.
   */
  private long iterationCount;
  /**
   * Compte le nombre de couples
   * refusé car déjà échangés.
   */
  private long alreadyTried;
  
  /**
   * Compte le nombre de requêtes
   * ayant échouées.
   */
  private long failedQuery;
  
  /**
   * Compte le nombre de couples
   * ayant le même nombre de ways
   * et de nodes.
   */
  private long tooSimilar;
  
  /**
   * Permet d'enregistrer l'évolution de la différence 
   * des répartitions de contributions.
   */
  private ArrayList<Double> repartitionDifferenceEvolution;
  

  /**
   * Permet d'enregistrer l'évolution de la différence 
   * des contributions des utilisateurs.
   */
  private ArrayList<Double> userContributionDifferenceEvolution;
  
  /**
   * Constructeur intialisant les 
   * attributs à 0.
   */
  public ChangesetSwappingResults() {
    startingTime = 0;
    endingTime = 0;
    
    successfulSwapCount = 0;
    unsuccessfulSwapCount = 0;
    changesetSelectedCount = 0;
    iterationCount = 0;
    alreadyTried = 0;
    failedQuery = 0;
    tooSimilar = 0;
    repartitionDifferenceEvolution = new ArrayList<>();
    userContributionDifferenceEvolution = new ArrayList<>();
  }

  /**
   * Retourne le temps de début.
   * @return startingTime
   */
  public long getStartingTime() {
    return startingTime;
  }
  
  /**
   * Fixe startingTime au temps courant
   * par appel de System.nanoTime().
   */
  public void startTime() {
    this.startingTime = System.nanoTime();
  }

  /**
   * Retourne le temps de fin.
   * @return endingTime
   */
  public long getEndingTime() {
    return endingTime;
  }

  /**
   * Fixe endingTime au temps courant
   * par appel de System.nanoTime().
   */
  public void endTime() {
    this.endingTime = System.nanoTime();
  }

  /**
   * Retourne le nombre d'échanges réussis.
   * @return successfulSwapCount
   */
  public long getSuccessfulSwapCount() {
    return successfulSwapCount;
  }

  /**
   * Incrémente la valeur de successfulSwapCount de 1. 
   */
  public void incSuccessfulSwapCount() {
    this.successfulSwapCount++;
  }

  /**
   * Retourne le nombre de changesets sélectionnés.
   * @return changesetSelectedCount
   */
  public long getChangesetSelectedCount() {
    return changesetSelectedCount;
  }

  /**
   * Incrémente la valeur de changesetSelectedCount de 1. 
   */
  public void incChangesetSelectedCount() {
    this.changesetSelectedCount++;
  }

  /**
   * Retourne le nombre d'itérations.
   * @return iterationCount
   */
  public long getIterationCount() {
    return iterationCount;
  }

  /**
   * Incrémente la valeur de iterationCount de 1. 
   */
  public void incIterationCount() {
    this.iterationCount++;
  }

  /**
   * Retourne le nombre d'échanges refusés.
   * @return unsuccessfulSwapCount
   */
  public int getUnsuccessfulSwapCount() {
    return unsuccessfulSwapCount;
  }

  /**
   * Incrémente la valeur de unsuccessfulSwapCount de 1. 
   */
  public void incUnsuccessfulSwapCount() {
    this.unsuccessfulSwapCount++;
  }
  
  /**
   * Retourne la valeur du nombre de couples 
   * déjà échangés.
   * @return alreadyTried
   */
  public long getAlreadyTried() {
    return alreadyTried;
  }

  /**
   * Incrémente la valeur de alreadyTried de 1. 
   */
  public void incAlreadyTried() {
    this.alreadyTried++;
  }

  /**
   * Retourne le nombre de requêtes 
   * ayant rencontrées un erreur.
   * @return failedQuery
   */
  public long getFailedQuery() {
    return failedQuery;
  }

  /**
   * Incrémente la valeur de failedQuery de 1. 
   */
  public void incFailedQuery() {
    this.failedQuery++;
  }

  /**
   * Retourne la valeur du nombre de 
   * couple ayant le même nombre de
   * nodes et de ways.
   * @return tooSimilar
   */
  public long getTooSimilar() {
    return tooSimilar;
  }

  /**
   * Incrémente la valeur de tooSimilar de 1. 
   */
  public void incTooSimilar() {
    this.tooSimilar++;
  }

  /**
   * Ajoute une nouvelle valeur à l'évolution de la 
   * différence de répartition.
   * @param newVal
   */
  public void addRepartitionDifferenceEvolution(double newVal) {
    repartitionDifferenceEvolution.add(newVal);
  }

  /**
   * Retourne un tableau de l'évolution de la 
   * différence de répartition. 
   * @return this.repartitionDifferenceEvolution.toArray(new Double[0])
   */
  public Double[] getRepartitionDifferenceEvolutionArray() {
    return this.repartitionDifferenceEvolution.toArray(new Double[0]);
  }
  
  /**
   * Ajoute une nouvelle valeur à l'évolution de la 
   * différence de contribution des utilisateurs.
   * @param newVal
   */
  public void addUserContributionDifferenceEvolution(double newVal) {
    userContributionDifferenceEvolution.add(newVal);
  }
  
  /**
   * Retourne un tableau de l'évolution de la 
   * différence de contribution des utilisateurs. 
   * @return this.repartitionDifferenceEvolution.toArray(new Double[0])
   */
  public Double[] getUserContributionDifferenceEvolutionArray() {
    return this.userContributionDifferenceEvolution.toArray(new Double[0]);
  }

  @Override
  public String toString() {
    return "ChangesetSwappingResults [startingTime=" + startingTime
        + ", endingTime=" + endingTime + ", successfulSwapCount="
        + successfulSwapCount + ", unsuccessfulSwapCount="
        + unsuccessfulSwapCount + ", changesetSelectedCount="
        + changesetSelectedCount + ", iterationCount=" + iterationCount
        + ", alreadyTried=" + alreadyTried + ", failedQuery=" + failedQuery
        + ", tooSimilar=" + tooSimilar + "]";
  }
}
