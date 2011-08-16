package fr.ign.cogit.geoxygene.api.spatial.geomprim;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IGenericCurve;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public interface ICurve extends IOrientableCurve, IGenericCurve {
  /** Renvoie la liste des segments. */
  public abstract List<ICurveSegment> getSegment();

  /** Renvoie le segment de rang i */
  public abstract ICurveSegment getSegment(int i);

  /** Affecte un segment au i-ème rang de la liste */
  public abstract void setSegment(int i, ICurveSegment value);

  /** Ajoute un segment en fin de liste sans vérifier la continuité du chaînage. */
  public abstract void addSegment(ICurveSegment value);

  /**
   * A TESTER. Ajoute un segment en fin de liste en vérifiant la continuité du
   * chaînage. Capte une exception en cas de problème. Nécessité de passer une
   * tolérance en paramètre.
   */
  public abstract void addSegment(ICurveSegment value, double tolerance)
      throws Exception;

  /**
   * A TESTER. Ajoute un segment en fin de liste en vérifiant la continuité du
   * chaînage, et en retournant le segment si necessaire. Capte une exception en
   * cas de problème. Nécessité de passer une tolérance en paramètre.
   */
  public abstract void addSegmentTry(ICurveSegment value, double tolerance)
      throws Exception;

  /**
   * Ajoute un segment au i-ème rang de la liste, sans vérifier la continuité du
   * chaînage.
   */
  public abstract void addSegment(int i, ICurveSegment value);

  /** Efface de la liste le (ou les) segment passé en paramètre */
  public abstract void removeSegment(ICurveSegment value);

  /** Efface le i-ème segment de la liste */
  public abstract void removeSegment(int i);

  /** Renvoie le nombre de segment */
  public abstract int sizeSegment();

  /**
   * A TESTER. Vérifie le chaînage des segments. renvoie TRUE s'ils sont
   * chaînés, FALSE sinon. Nécessité de définir une tolérance.
   */
  public abstract boolean validate(double tolerance);

  // ////////////////////////////////////////////////////////////////////////////////
  // Implémentation de GM_GenericCurve
  // /////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////
  /**
   * Retourne le DirectPosition du premier point. Différent de l'opérateur
   * "boundary" car renvoie la valeur du point et non pas l'objet géométrique
   * représentatif. Méthode d'implémentation de l'interface GM_GenericCurve.
   */
  public abstract IDirectPosition startPoint();

  /**
   * Retourne le DirectPosition du dernier point. Différent de l'opérateur
   * "boundary" car renvoie la valeur du point et non pas l'objet géométrique
   * représentatif. Méthode d'implémentation de l'interface GM_GenericCurve.
   */
  public abstract IDirectPosition endPoint();

  /**
   * Approximation linéaire d'une courbe avec les points de contrôle. Elimine
   * les points doublons consécutifs (qui apparaissent quand la courbe est
   * composée de plusieurs segments).
   * <P>
   * Le paramètre spacing indique la distance maximum entre 2 points de contrôle
   * ; le paramètre offset indique la distance maximum entre la polyligne
   * générée et la courbe originale. Si ces 2 paramètres sont à 0, alors aucune
   * contrainte n'est imposée. Dans l'IMPLEMENTATION ACTUELLE : on impose que
   * ces paramètres soient à 0.
   * <P>
   * Le paramètre tolérance est nécessaire pour éliminer les doublons. On peut
   * passer 0.0.
   * <P>
   * Méthode d'implémentation de l'interface GM_GenericCurve.
   */
  // Dans la norme, les paramètres spacing et offset sont de type Distance.
  // Dans la norme, il n'y a pas de paramètre tolérance.
  public abstract ILineString asLineString(double spacing, double offset,
      double tolerance);

  /**
   * Renvoie la liste des coordonnées d'une courbe sous forme d'une liste de
   * DirectPosition .
   */
  public abstract IDirectPositionList coord();
}
