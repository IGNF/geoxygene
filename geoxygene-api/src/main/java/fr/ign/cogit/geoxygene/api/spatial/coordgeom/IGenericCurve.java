package fr.ign.cogit.geoxygene.api.spatial.coordgeom;


/**
 * NON UTILISE. Cette interface de la norme n'a plus de sens depuis qu'on a fait
 * hériter GM_CurveSegment de GM_Curve.
 * 
 * <P>
 * Définition de la norme : les classes GM_Curve et GM_CurveSegment représentent
 * toutes deux des géométries à une dimension, et partagent donc plusieurs
 * signatures d'opération. Celles-ci sont définies dans l'interface
 * GM_GenericCurve. La paramétrisation employée dans les méthodes se fait par la
 * longueur de l'arc (absisse curviligne) ou par une autre paramétrisation.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */
public interface IGenericCurve {
  /**
   * Retourne le DirectPosition du premier point. Différent de l'opérateur
   * "boundary" car renvoie la valeur du point et non pas l'objet géométrique
   * représentatif.
   */
  public abstract IDirectPosition startPoint();

  /**
   * Retourne le DirectPosition du dernier point. Différent de l'opérateur
   * "boundary" car renvoie la valeur du point et non pas l'objet géométrique
   * représentatif.
   */
  public abstract IDirectPosition endPoint();

  /**
   * NON IMPLEMENTE. Renvoie un point à l'abcsisse curviligne s.
   */
  public abstract// NORME : le paramètre en entree est de type Distance.
  IDirectPosition param(double s);

  /**
   * Renvoie O pour une GM_Curve. Pour un GM_CurveSegment, égal au endParam du
   * précedent segment dans la segmentation (0 pour le premier segment).
   */
  public abstract// NORME : le résultat est de type Distance.
  double startParam();

  /**
   * Longueur de la courbe pour une GM_Curve. Pour un GM_CurveSegment, égale à
   * startParam plus la longueur du segment.
   */
  public abstract// NORME : le résultat est de type Distance.
  double endParam();

  /**
   * NON IMPLEMENTE. Renvoie le paramètre au point P (le paramètre étant a
   * priori la distance). Si P n'est pas sur la courbe, on cherche alors pour le
   * calcul le point le plus proche de P sur la courbe (qui est aussi renvoyé en
   * résultat). On renvoie en général une seule distance, sauf si la courbe
   * n'est pas simple.
   */
  public abstract// NORME : le résultat est de type Distance.
  double[] paramForPoint(IDirectPosition P);

  /**
   * NON IMPLEMENTE. Représentation alternative d'une courbe comme l'image
   * continue d'un intervalle de réels, sans imposer que cette paramétrisation
   * représente la longueur de la courbe, et sans imposer de restrictions entre
   * la courbe et ses segments. Utilité : pour les courbes paramétrées, pour
   * construire une surface paramétrée.
   */
  public abstract IDirectPosition constrParam(double cp);

  /**
   * NON IMPLEMENTE. Paramètre au startPoint pour une courbe paramétrée,
   * c'est-à-dire : constrParam(startConstrParam())=startPoint().
   */
  public abstract double startConstrParam();

  /**
   * NON IMPLEMENTE. Paramètre au endPoint pour une courbe paramétrée,
   * c'est-à-dire : constrParam(endConstrParam())=endPoint().
   */
  public abstract double endConstrParam();

  /**
   * NON IMPLEMENTE. Longueur entre 2 points.
   */
  public abstract// NORME : le résultat est de type Length.
  double length(IDirectPosition p1, IDirectPosition p2);

  /**
   * NON IMPLEMENTE. Longueur d'une courbe paramétrée "entre 2 réels".
   */
  public abstract// NORME : le résultat est de type Length.
  double length(double cparam1, double cparam2);

  /**
   * Approximation linéaire d'une courbe avec les points de contrôle. Le
   * paramètre spacing indique la distance maximum entre 2 points de contrôle;
   * le paramètre offset indique la distance maximum entre la polyligne générée
   * et la courbe originale. Si ces 2 paramètres sont à 0, alors aucune
   * contrainte n'est imposée. Le paramètre tolérance permet d'éliminer les
   * points consécutifs doublons qui peuvent apparaître quand la courbe est
   * composée de plusieurs segments.
   */
  public abstract// NORME : spacing et offset sont de type Distance. tolerance
  // n'est pas en paramètre.
  ILineString asLineString(double spacing, double offset, double tolerance);
  ILineString asLineString(double spacing, double offset);
}
