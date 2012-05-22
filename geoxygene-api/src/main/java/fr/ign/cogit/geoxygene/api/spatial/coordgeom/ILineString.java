package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import java.util.List;

public interface ILineString extends ICurveSegment {
  /**
   * Renvoie la liste conbtrolPoint. Equivalent de samplePoint() et de coord().
   * A laisser ?
   */
  public abstract IDirectPositionList getControlPoint();

  /** Renvoie le DirectPosition de rang i. */
  public abstract IDirectPosition getControlPoint(int i);

  /** Affecte un DirectPosition au i-ème rang de la liste. */
  public abstract void setControlPoint(int i, IDirectPosition value);

  /** Ajoute un DirectPosition en fin de liste */
  public abstract void addControlPoint(IDirectPosition value);

  /** Ajoute un DirectPosition au i-ème rang de la liste. */
  public abstract void addControlPoint(int i, IDirectPosition value);

  /** Efface de la liste le DirectPosition passé en paramètre. */
  public abstract void removeControlPoint(IDirectPosition value);

  /** Efface le i-ème DirectPosition de la liste. */
  public abstract void removeControlPoint(int i);

  /** Renvoie le nombre de DirectPosition. */
  public abstract int sizeControlPoint();

  // ////////////////////////////////////////////////////////////////////////
  // Méthode de la norme ///////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  /**
   * TODO Renvoie null. Décompose une polyligne en une séquence de segments.
   */
  public abstract List<ILineSegment> asGM_LineSegment();

  // ////////////////////////////////////////////////////////////////////////
  // Implémentation de méthodes abstraites /////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  @Override
  public abstract IDirectPositionList coord();

  @Override
  public abstract ICurveSegment reverse();

  /**
   * Verifie si la ligne est fermee ou non. La ligne est fermee lorsque les deux
   * points extremes ont la meme position
   * @return
   */
  public abstract boolean isClosed(double tolerance);

  /**
   * @return <code>true</code> if this LineString is closed (start point=end
   *         point); <code>false</code> otherwise.
   */
  public abstract boolean isClosed();

  @Override
  public abstract Object clone();

  @Override
  public abstract boolean isLineString();
}
