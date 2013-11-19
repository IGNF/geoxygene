/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.ClassificationResult;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.ConclusionIntervals;

/**
 * @author PTaillandier Permet de prendre une décision à l'aide de la méthode
 *         ELECTRE TRI en faisant varier les valeurs des paramètres
 *         Implémentation très moche (presque honteuse... mais bon j'étais un
 *         peu pressé)
 * @author GTouya amélioration de l'implémentation avec un peu plus de
 *         généricité.
 */
public class RobustELECTRETRIMethod extends ELECTRETRIMethod {

  private static Logger logger = Logger.getLogger(RobustELECTRETRIMethod.class
      .getName());

  /**
   * @uml.property name="seuilCoupeSet"
   * @uml.associationEnd multiplicity="(0 -1)" elementType="java.lang.Double"
   */
  private Set<Double> seuilCoupeSet;
  /**
   * @uml.property name="criteriaParams"
   * @uml.associationEnd multiplicity="(0 -1)" ordering="true"
   *                     elementType="java.util.Map"
   *                     qualifier="crits0:java.util.Set java.util.Set"
   */
  private Map<String, List<Map<Set<Criterion>, Set<Double>>>> criteriaParams;

  private Set<Criterion> criteria = new HashSet<Criterion>();

  private boolean harmonisation = false;

  public boolean isHarmonisation() {
    return this.harmonisation;
  }

  public void setHarmonisation(boolean harmonisation) {
    this.harmonisation = harmonisation;
  }

  public Map<String, List<Map<Set<Criterion>, Set<Double>>>> getCriteriaParams() {
    return this.criteriaParams;
  }

  public void setCriteriaParams(
      Map<String, List<Map<Set<Criterion>, Set<Double>>>> paramsCriteres) {
    this.criteriaParams = paramsCriteres;
  }

  public Set<Double> getSeuilCoupeSet() {
    return this.seuilCoupeSet;
  }

  public void setSeuilCoupeSet(Set<Double> seuilCoupeSet) {
    this.seuilCoupeSet = seuilCoupeSet;
  }

  public void setCriteria(Set<Criterion> criteria) {
    this.criteria = criteria;
  }

  public Set<Criterion> getCriteria() {
    return criteria;
  }

  /**
   * variation du seuil de coupe et test de la méthode ELECTRE TRI avec les deux
   * types de procédures (optimiste et pessimiste)
   * @param votes : ensemble des conclusions obtenus après décisions avec
   *          l'ensemble des jeux de paramètres
   * @param criteres : ensemble de Critere : critères utilisés pour la décision
   *          multicritère
   * @param valeursCourantes : vecteur de valeurs courant : Clef : String : nom
   *          critère -> Valeur : Double : valeur du critère
   * @param conclusion : les intervalles auxquels il faut rattaché le vecteur de
   *          valeurs courant
   */
  private void decision(Map<String, Double> votes, Set<Criterion> criteres,
      Map<String, Double> valeursCourantes, ConclusionIntervals conclusion) {
    for (Double valCoupe : this.seuilCoupeSet) {
      this.setCutThreshold(valCoupe.doubleValue());
      this.setProcedureOptimiste(true);
      ClassificationResult decision = this.decisionELECTRETRI(criteres,
          valeursCourantes, conclusion);
      Double val = votes.get(decision);
      if (val == null) {
        val = new Double(0);
      }
      votes.put(decision.getCategory(), new Double(val.doubleValue() + 1));
      this.setProcedureOptimiste(false);
      decision = this
          .decisionELECTRETRI(criteres, valeursCourantes, conclusion);
      Double val2 = votes.get(decision);
      if (val2 == null) {
        val2 = new Double(0);
      }
      votes.put(decision.getCategory(), new Double(val2.doubleValue() + 1));

    }
  }

  /**
   * Modifie l'ensemble la valeur de indiff�rence des critéres donnés en
   * paramètre
   * @param criteres : critères desquels nous cherchons à modifier la valeur de
   *          indifférence
   * @param val : nouvelle valeur de indifférence
   */
  private void setPreference(Set<Criterion> criteres, double val) {
    for (Criterion critere : criteres) {
      ((ELECTRECriterion) critere).setPreference(val);
    }
  }

  /**
   * Modifie l'ensemble la valeur de pr�f�rence des crit�res donn�s en param�tre
   * @param criteres : crit�res desquels nous cherchons � modifier la valeur de
   *          pr�f�rence
   * @param val : nouvelle valeur de pr�f�rence
   */
  private void setIndifference(Set<Criterion> criteres, double val) {
    for (Criterion critere : criteres) {
      ((ELECTRECriterion) critere).setIndifference(val);
    }
  }

  /**
   * Modifie l'ensemble la valeur de poids des crit�res donn�s en param�tre
   * @param criteres : crit�res desquels nous cherchons � modifier la valeur de
   *          poids
   * @param val : nouvelle valeur de poids
   */
  private void setPoids(Set<Criterion> criteres, double val) {
    for (Criterion critere : criteres) {
      ((ELECTRECriterion) critere).setWeight(val);
    }
  }

  /**
   * Modifie l'ensemble la valeur de v�to des crit�res donn�s en param�tre
   * @param criteres : crit�res desquels nous cherchons � modifier la valeur de
   *          v�to
   * @param val : nouvelle valeur de v�to
   */
  private void setVeto(Set<Criterion> criteres, double val) {
    for (Criterion critere : criteres) {
      ((ELECTRECriterion) critere).setVeto(val);
    }
  }

  /**
   * variation des valeurs de pr�f�rence
   * @param votes : ensemble des conclusions obtenus apr�s d�cisions avec
   *          l'ensemble des jeux de param�tres
   * @param criteres : ensemble de Critere : crit�res utilis�s pour la d�cision
   *          multicrit�re
   * @param valeursCourantes : vecteur de valeurs courant : Clef : String : nom
   *          crit�re -> Valeur : Double : valeur du crit�re
   * @param conclusion : les intervalles auxquels il faut rattach� le vecteur de
   *          valeurs courant
   */
  private void decisionPref(Map<String, Double> votes, Set<Criterion> criteres,
      Map<String, Double> valeursCourantes, ConclusionIntervals conclusion) {
    this.setParam("Preference");
    this.decision(votes, criteres, valeursCourantes, conclusion);
  }

  /**
   * variation des valeurs d'indiff�rence
   * @param votes : ensemble des conclusions obtenus apr�s d�cisions avec
   *          l'ensemble des jeux de param�tres
   * @param criteres : ensemble de Critere : crit�res utilis�s pour la d�cision
   *          multicrit�re
   * @param valeursCourantes : vecteur de valeurs courant : Clef : String : nom
   *          crit�re -> Valeur : Double : valeur du crit�re
   * @param conclusion : les intervalles auxquels il faut rattach� le vecteur de
   *          valeurs courant
   */
  private void decisionInd(Map<String, Double> votes, Set<Criterion> criteres,
      Map<String, Double> valeursCourantes, ConclusionIntervals conclusion) {
    this.setParam("Indifference");
    this.decisionPref(votes, criteres, valeursCourantes, conclusion);
  }

  /**
   * variation des valeurs de v�to
   * @param votes : ensemble des conclusions obtenus apr�s d�cisions avec
   *          l'ensemble des jeux de param�tres
   * @param criteres : ensemble de Critere : crit�res utilis�s pour la d�cision
   *          multicrit�re
   * @param valeursCourantes : vecteur de valeurs courant : Clef : String : nom
   *          crit�re -> Valeur : Double : valeur du crit�re
   * @param conclusion : les intervalles auxquels il faut rattach� le vecteur de
   *          valeurs courant
   */
  private void decisionVeto(Map<String, Double> votes, Set<Criterion> criteres,
      Map<String, Double> valeursCourantes, ConclusionIntervals conclusion) {
    this.setParam("Veto");
    this.decisionInd(votes, criteres, valeursCourantes, conclusion);
  }

  private void setParam(String param) {
    List<Map<Set<Criterion>, Set<Double>>> valeurPref = this.criteriaParams
        .get(param);
    Iterator<Map<Set<Criterion>, Set<Double>>> i = valeurPref.iterator();
    while (i.hasNext()) {
      Map<Set<Criterion>, Set<Double>> map = i.next();
      for (Set<Criterion> criteria : map.keySet()) {
        if (param.equals("Poids")) {
          this.setPoids(criteria, map.get(criteria).iterator().next()
              .doubleValue());
        }
        if (param.equals("Veto")) {
          this.setVeto(criteria, map.get(criteria).iterator().next()
              .doubleValue());
        }
        if (param.equals("Preference")) {
          this.setPreference(criteria, map.get(criteria).iterator().next()
              .doubleValue());
        }
        if (param.equals("Indifference")) {
          this.setIndifference(criteria, map.get(criteria).iterator().next()
              .doubleValue());
        }
      }
    }
  }

  /**
   * variation des valeurs des poids
   * @param votes : ensemble des conclusions obtenus après décisions avec
   *          l'ensemble des jeux de paramètres
   * @param criteres : ensemble de Critere : critères utilisés pour la décision
   *          multicritère
   * @param valeursCourantes : vecteur de valeurs courant : Clef : String : nom
   *          critère -> Valeur : Double : valeur du critère
   * @param conclusion : les intervalles auxquels il faut rattaché le vecteur de
   *          valeurs courant
   */
  private void decisionWeight(Map<String, Double> votes,
      Set<Criterion> criteres, Map<String, Double> valeursCourantes,
      ConclusionIntervals conclusion) {
    this.setParam("Poids");
    this.decisionVeto(votes, criteres, valeursCourantes, conclusion);
  }

  @Override
  public ClassificationResult decision(Set<Criterion> criteres,
      Map<String, Double> valeursCourantes, ConclusionIntervals conclusion) {
    Map<String, Double> votes = new Hashtable<String, Double>();

    // Décision multicritère avec différents jeux de paramètres
    this.decisionWeight(votes, criteres, valeursCourantes, conclusion);

    // fonction facultative (pas vraiment encore testée) qui vise à tenir
    // compte
    // de l'éloignement des différentes catégories
    if (this.harmonisation) {
      this.harmonisation(votes);
    }

    // choix d'une catégorie par vote majoritaire
    double robustesse = 0;
    String catChoisie = "";
    double valTot = 0;
    for (String categorie : votes.keySet()) {
      double val = votes.get(categorie).doubleValue();
      if (val > 0) {
        valTot += val;
      }
      // System.out.println("categorie : " + categorie + "  val : " + val);
      if (val > robustesse) {
        catChoisie = categorie;
        robustesse = val;
      }
    }
    // calcul de la robustesse (attention -> si on utilise la fonction
    // d'harmonisation, on ne se retrouve plus avec un résultat compris entre 0
    // et 1)
    robustesse /= valTot;
    logger.info("**** ELECTRE TRI : Categorie choisie : " + catChoisie
        + "  robustesse : " + robustesse + "  ****");
    return new ClassificationResult(catChoisie, robustesse, criteres);
  }

  /**
   * Méthode qui vise à tenir compte de l'éloignement des différentes catégories
   * : typiquement un décision jeu de K = mauvais est plus proche de jeu de K =
   * moyen que de jeu de K = très bon Pour cela on va simplement pour chaque
   * hypothèse d'affectation du jeu de K à une catégorie de qualité soustraire
   * les valeurs des autres hypothèses d'affectation en tenant compte de leur
   * éloignement
   * @param votes : ensemble des conclusions obtenus après décisions avec
   *          l'ensemble des jeux de paramètres
   */
  private void harmonisation(Map<String, Double> votes) {
    // tableau qui va nous servir � sauvegarder temporairement la valeur
    // affecter � chaque cat�gorie
    double[] cat = new double[5];
    for (int i = 0; i < 5; i++) {
      cat[i] = 0;
    }

    // Parcours de l'ensemble des cat�gories
    for (String categorie : votes.keySet()) {

      // nombre de fois o� cette cat�gorie a �t� propos�e par la
      // m�thode de
      // d�cision multicrit�re
      double val = votes.get(categorie).doubleValue();

      // pour chaque type de cat�gorie, on met � jour le tableau cat
      if (categorie.equals("Jeu de connaissances tres mauvais")) {
        cat[0] += val;
        for (int i = 1; i < 5; i++) {
          cat[i] -= val * (0.0 + i) / 5.0;
        }
      } else if (categorie.equals("Jeu de connaissances mauvais")) {
        cat[1] += val;
        for (int i = 0; i < 5; i++) {
          if (i == 1) {
            continue;
          }
          cat[i] -= val * (0.0 + Math.abs(i - 1)) / 5.0;
        }
      } else if (categorie.equals("Jeu de connaissances moyen")) {
        cat[2] += val;
        for (int i = 0; i < 5; i++) {
          if (i == 2) {
            continue;
          }
          cat[i] -= val * (0.0 + Math.abs(i - 2)) / 5.0;
        }
      } else if (categorie.equals("Jeu de connaissances bon")) {
        cat[3] += val;
        for (int i = 0; i < 5; i++) {
          if (i == 3) {
            continue;
          }
          cat[i] -= val * (0.0 + Math.abs(i - 3)) / 5.0;
        }
      } else if (categorie.equals("Jeu de connaissances tres bon")) {
        cat[4] += val;
        for (int i = 0; i < 5; i++) {
          if (i == 4) {
            continue;
          }
          cat[i] -= val * (0.0 + Math.abs(i - 4)) / 5.0;
        }
      }

    }

    // on met � jour le dictionnaire des votes avec le tableau cat
    votes.put("Jeu de connaissances tres mauvais", new Double(cat[0]));
    votes.put("Jeu de connaissances mauvais", new Double(cat[1]));
    votes.put("Jeu de connaissances moyen", new Double(cat[2]));
    votes.put("Jeu de connaissances bon", new Double(cat[3]));
    votes.put("Jeu de connaissances tres bon", new Double(cat[4]));
  }

  /**
   * 
   * 
   * @param criteria
   * @author GTouya
   */
  public void setCriteriaParamsFromCriteria(Set<Criterion> criteria) {
    Set<Double> seuilCSet = new HashSet<Double>();
    // Différentes valeurs du seuil de coupe (lambda)
    seuilCSet.add(new Double(0.6));
    seuilCSet.add(new Double(0.7));
    seuilCSet.add(new Double(0.8));
    this.setSeuilCoupeSet(seuilCSet);

    this.setCriteria(criteria);

    this.criteriaParams = new HashMap<String, List<Map<Set<Criterion>, Set<Double>>>>();
    // set the weight
    List<Map<Set<Criterion>, Set<Double>>> weights = new ArrayList<Map<Set<Criterion>, Set<Double>>>();
    for (Criterion ct : criteria) {
      Map<Set<Criterion>, Set<Double>> map = new HashMap<Set<Criterion>, Set<Double>>();
      Set<Criterion> criterion = new HashSet<Criterion>();
      criterion.add(ct);
      Set<Double> weight = new HashSet<Double>();
      weight.add(new Double(((ELECTRECriterion) ct).getWeight()));
      map.put(criterion, weight);
      weights.add(map);
    }
    this.criteriaParams.put("Poids", weights);

    // set the veto
    List<Map<Set<Criterion>, Set<Double>>> vetos = new ArrayList<Map<Set<Criterion>, Set<Double>>>();
    for (Criterion ct : criteria) {
      Map<Set<Criterion>, Set<Double>> map = new HashMap<Set<Criterion>, Set<Double>>();
      Set<Criterion> criterion = new HashSet<Criterion>();
      criterion.add(ct);
      Set<Double> veto = new HashSet<Double>();
      veto.add(new Double(((ELECTRECriterion) ct).getVeto()));
      map.put(criterion, veto);
      vetos.add(map);
    }
    this.criteriaParams.put("Veto", vetos);

    // set the preference
    List<Map<Set<Criterion>, Set<Double>>> prefs = new ArrayList<Map<Set<Criterion>, Set<Double>>>();
    for (Criterion ct : criteria) {
      Map<Set<Criterion>, Set<Double>> map = new HashMap<Set<Criterion>, Set<Double>>();
      Set<Criterion> criterion = new HashSet<Criterion>();
      criterion.add(ct);
      Set<Double> pref = new HashSet<Double>();
      pref.add(new Double(((ELECTRECriterion) ct).getPreference()));
      map.put(criterion, pref);
      prefs.add(map);
    }
    this.criteriaParams.put("Preference", prefs);

    // set the indifference
    List<Map<Set<Criterion>, Set<Double>>> indifs = new ArrayList<Map<Set<Criterion>, Set<Double>>>();
    for (Criterion ct : criteria) {
      Map<Set<Criterion>, Set<Double>> map = new HashMap<Set<Criterion>, Set<Double>>();
      Set<Criterion> criterion = new HashSet<Criterion>();
      criterion.add(ct);
      Set<Double> indif = new HashSet<Double>();
      indif.add(new Double(((ELECTRECriterion) ct).getIndifference()));
      map.put(criterion, indif);
      indifs.add(map);
    }
    this.criteriaParams.put("Indifference", indifs);
  }
}
