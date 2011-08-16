package fr.ign.cogit.geoxygene.api.spatial.geomcomp;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurveBoundary;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;

public interface ICompositeCurve extends IComposite, IOrientableCurve {
  /** Renvoie la liste des GM_OrientableCurve */
  public abstract List<IOrientableCurve> getGenerator();

  /** Renvoie la GM_OrientableCurve de rang i */
  public abstract IOrientableCurve getGenerator(int i);

  /**
   * Affecte une GM_OrientableCurve au rang i. Attention : aucun contrôle de
   * continuité n'est effectué.
   */
  public abstract void setGenerator(int i, IOrientableCurve value);

  /**
   * Ajoute une GM_OrientableCurve en fin de liste. Attention : aucun contrôle
   * de continuité n'est effectué.
   */
  public abstract void addGenerator(IOrientableCurve value);

  /**
   * Ajoute une GM_OrientableCurve en fin de liste avec un contrôle de
   * continuité avec la tolérance passée en paramètre. Envoie une exception en
   * cas de problème.
   */
  public abstract void addGenerator(IOrientableCurve value, double tolerance)
      throws Exception;

  /**
   * Ajoute une GM_OrientableCurve en fin de liste avec un contrôle de
   * continuité avec la tolérance passée en paramètre. Eventuellement change le
   * sens d'orientation de la courbe pour assurer la continuite. Envoie une
   * exception en cas de problème.
   */
  public abstract void addGeneratorTry(IOrientableCurve value, double tolerance)
      throws Exception;

  /**
   * Ajoute une GM_OrientableCurve au rang i. Attention : aucun contrôle de
   * continuité n'est effectué.
   */
  public abstract void addGenerator(int i, IOrientableCurve value);

  /**
   * Efface la (ou les) GM_OrientableCurve passé en paramètre. Attention : aucun
   * contrôle de continuité n'est effectué.
   */
  public abstract void removeGenerator(IOrientableCurve value) throws Exception;

  /**
   * Efface la GM_OrientableCurve de rang i. Attention : aucun contrôle de
   * continuité n'est effectué.
   */
  public abstract void removeGenerator(int i) throws Exception;

  /** Nombre de GM_OrientableCurve constituant self */
  public abstract int sizeGenerator();

  /** Renvoie la primitive de self. */
  // le calcul est fait en dynamique dans la methode privee simplifyPrimitve.
  public abstract ICurve getPrimitive();

  /** Renvoie la primitive orientée positivement. */
  public abstract IOrientableCurve getPositive();

  /** Renvoie la primitive orientée négativement. */
  public abstract IOrientableCurve getNegative();

  /**
   * Redéfinition de l'opérateur "boundary" sur GM_OrientableCurve. Renvoie une
   * GM_CurveBoundary, c'est-à-dire deux GM_Point.
   */
  public abstract ICurveBoundary boundary();

  // //////////////////////////////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////
  // Méthodes "validate" /////////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////
  // cette méthode n'est pas dans la norme.
  /**
   * Vérifie le chaînage des composants. Renvoie TRUE s'ils sont chaînés, FALSE
   * sinon.
   */
  public abstract boolean validate(double tolerance);

  /** Renvoie les coordonnees de la primitive. */
  public abstract IDirectPositionList coord();
}
