package fr.ign.cogit.geoxygene.api.spatial.geomcomp;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;

/**
 * @author julien Gaffuri 2 juil. 2009
 * 
 */
public interface ICompositeSolid extends IComposite, ISolid {
  /** Renvoie la liste des GM_Solid */
  public abstract List<ISolid> getGenerator();

  /** Nombre de GM_Solid constituant self */
  public abstract int sizeGenerator();

  /**
   * NON IMPLEMETE (renvoie 0.0). Aire.
   */
  @Override
  public abstract double area();

  /**
   * NON IMPLEMETE (renvoie 0.0). Volume.
   */
  // Dans la norme, le résultat est de type Volume.
  @Override
  public abstract double volume();
}
