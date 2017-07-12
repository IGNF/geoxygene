/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.electretri;

import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.Criterion;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.ClassificationResult;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.ConclusionIntervals.Interval;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.MultiCriteriaDecisionClassifMethod;

/**
 * @author PTaillandier ELECTRE TRI multiple criteria decision method. Méthode
 *         ELECTRE TRI de décision multicritère (voir thèse de P. Taillandier
 *         Chap. E)
 */
public class ELECTRETRIMethod implements MultiCriteriaDecisionClassifMethod {

  // proc�dure optimiste (true) ou pessimiste (false)
  /**
   * @uml.property name="optimisticProcess"
   */
  private boolean optimisticProcess;

  // valeur du seuil de coupe
  /**
   * @uml.property name="cutThreshold"
   */
  private double cutThreshold;

  /**
   * @return
   * @uml.property name="cutThreshold"
   */
  public double getCutThreshold() {
    return this.cutThreshold;
  }

  /**
   * @param cutThreshold
   * @uml.property name="cutThreshold"
   */
  public void setCutThreshold(double cutThreshold) {
    this.cutThreshold = cutThreshold;
  }

  /**
   * Détermination de la relation entre deux vecteurs de valeurs
   * @param criteria : les crit�res pour �tablir la relation
   * @param currentValues : le vecteur de valeurs courant
   * @param refValues : le vecteur de valeur de r�f�rence
   * @return le nom de la relation unissant les deux vecteurs : soit A R Ref =>
   *         les deux vecteurs sont incomparables, soit A S Ref => le vecteur
   *         courant est sup�rieur au vecteur ref soit Ref S A => le vecteur ref
   *         est sup�rieur au vecteur courant soit A I Ref => les deux vecteurs
   *         sont incompatibles
   */
  public String relation(Set<Criterion> criteria,
      Map<String, Double> currentValues, Map<String, Double> refValues) {
    // On commence par calculer pour chaque crit�re les concordances et les
    // discordances entre
    // le vecteur courant et le vecteur de ref
    double concordGARef = 0;
    double concordGRefA = 0;
    double poidsTot = 0;
    for (Criterion crit : criteria) {
      ELECTRECriterion critere = (ELECTRECriterion) crit;
      Double valeurCouranteD = currentValues.get(critere.getName());
      if (valeurCouranteD == null) {
        continue;
      }
      double valeurCourante = valeurCouranteD.doubleValue();

      double valeurRef = refValues.get(critere.getName()).doubleValue();

      concordGARef += critere.getWeight()
          * critere.computeConcordance(valeurCourante, valeurRef);
      concordGRefA += critere.getWeight()
          * critere.computeConcordance(valeurRef, valeurCourante);
      poidsTot += critere.getWeight();
    }

    // on calcul ensuite les concordances globales
    concordGARef /= poidsTot;
    concordGRefA /= poidsTot;

    // on calcul ensuite les valeurs TARef et TRefA (taux d'affaiblissement de
    // la concordance du aux effets des v�tos)
    double TARef = 1;
    double TRefA = 1;
    for (Criterion crit : criteria) {
      ELECTRECriterion critere = (ELECTRECriterion) crit;
      Double valeurCouranteD = currentValues.get(critere.getName());
      if (valeurCouranteD == null) {
        continue;
      }
      double valeurCourante = valeurCouranteD.doubleValue();
      double valeurRef = refValues.get(critere.getName()).doubleValue();

      double discorARef = critere.computeDiscordance(valeurCourante, valeurRef);
      double discorRefA = critere.computeDiscordance(valeurRef, valeurCourante);

      if (discorARef > concordGARef) {
        TARef *= (1 - discorARef) / (1 - concordGARef);
      }
      if (discorRefA > concordGRefA) {
        TRefA *= (1 - discorRefA) / (1 - concordGRefA);
      }
    }
    double credibiliteGlobaleARef = concordGARef * TARef;
    double credibiliteGlobaleRefA = concordGRefA * TRefA;

    // on d�duit enfin de ces valeurs la relation existante entre les deux
    // vecteurs de valeurs
    if (credibiliteGlobaleARef < this.cutThreshold) {
      if (credibiliteGlobaleRefA < this.cutThreshold) {
        return "A R Ref";
      }
      return "Ref S A";
    }
    if (credibiliteGlobaleRefA < this.cutThreshold) {
      return "A S Ref";
    }
    return "A I Ref";
  }

  public ClassificationResult decision(Set<Criterion> criteres,
      Map<String, Double> valeursCourantes, ConclusionIntervals conclusion) {
    // A noter que la fonction decisionFC existe uniquement pour �tre
    // appel�e
    // par la m�thode "decision" de la classe MethodeFonctionsCroyancesRobuste
    return this.decisionELECTRETRI(criteres, valeursCourantes, conclusion);

  }

  /**
   * Fonction qui permet de d�finir la cat�gorie � affecter par rapport � un jeu
   * de K courant
   * @param criteres : Ensemble de CritereELECTRETRI (les crit�res pour cette
   *          m�thode)
   * @param valeursCourantes : dictionnaire des valeurs des crit�res pour le jeu
   *          de K courant : Clef : Strign : nom du crit�re -> Valeur : Double :
   *          sa valeur
   * @param conclusion : objets qui regroupent les Intervalles possibles pour
   *          les affectations de conclusion
   * @return le nom de la cat�gorie affect�e
   */
  public ClassificationResult decisionELECTRETRI(Set<Criterion> criteres,
      Map<String, Double> valeursCourantes, ConclusionIntervals conclusion) {
    // Cas de la proc�dure optimiste
    if (this.optimisticProcess) {
      // on parcours tous les intervalles (donc conclusion) en partant du
      // premier (jeu de K = mauvais)
      for (int i = conclusion.getIntervals().size() - 1; i > 0; i--) {
        Interval interval = conclusion.getIntervals().get(i);

        // on d�finit la relation entre le vecteur de valeurs courant le
        // vecteur
        // de ref courant (celui marquant la borne inf de l'intervalle courant)
        Map<String, Double> ref = interval.getStartPoint();
        String relation = this.relation(criteres, valeursCourantes, ref);

        // System.out.println("Optimiste : relation avec "+
        // intervalle.getConclusion() + " -> " + relation);

        // si le vecteur ref ne surclasse pas (n'est pas meilleur que) le
        // vecteur courant -> conclusion de l'intervalle courant
        if (!relation.equals("Ref S A")) {
          return new ClassificationResult(interval.getConclusion(), -1,
              criteres);
        }
      }
      return new ClassificationResult(
          conclusion.getIntervals().get(0).getConclusion(), -1, criteres);
    }
    // cas de la proc�dure pessimiste
    for (int i = conclusion.getIntervals().size() - 1; i > 0; i--) {
      Interval interval = conclusion.getIntervals().get(i);

      // on d�finit la relation entre le vecteur de valeurs courant le vecteur
      // de ref courant (celui marquant la borne inf de l'intervalle courant)
      Map<String, Double> ref = interval.getStartPoint();
      String relation = this.relation(criteres, valeursCourantes, ref);

      // System.out.println("Pessimiste : relation avec "+
      // intervalle.getConclusion() + " -> " + relation);

      // si le vecteur courant surclasse (est meilleur que) le vecteur ref ->
      // conclusion de l'intervalle courant
      if (relation.equals("A S Ref")) {
        return new ClassificationResult(interval.getConclusion(), -1, criteres);
      }
    }
    return new ClassificationResult(
        conclusion.getIntervals().get(0).getConclusion(), -1, criteres);
  }

  /**
   * @return
   * @uml.property name="procedureOptimiste"
   */
  public boolean isProcedureOptimiste() {
    return this.optimisticProcess;
  }

  /**
   * @param procedureOptimiste
   * @uml.property name="procedureOptimiste"
   */
  public void setProcedureOptimiste(boolean procedureOptimiste) {
    this.optimisticProcess = procedureOptimiste;
  }

}
