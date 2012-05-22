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

  @Override
  public abstract IDirectPosition startPoint();

  @Override
  public abstract IDirectPosition endPoint();

  // Dans la norme, les paramètres spacing et offset sont de type Distance.
  // Dans la norme, il n'y a pas de paramètre tolérance.
  @Override
  public abstract ILineString asLineString(double spacing, double offset,
      double tolerance);

  @Override
  public abstract IDirectPositionList coord();

  public abstract void clearSegments();
}
