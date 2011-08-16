/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.reseau;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * (peut-etre inutile sous forme de classe; a voir a l'usage)
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface PassePar extends ElementDuReseau {

  /**
   * @return niveau du passage
   */
  public int getNiveau();

  public void setNiveau(int niveau);

  @Override
  public IPoint getGeom();

  /**
   * @return l'arc concerne
   */
  public ArcReseau getArc();

  public void setArc(ArcReseau arc);

  /**
   * @return le franchissement concerne
   */
  public Franchissement getFranchissement();

  public void setFranchissement(Franchissement franchissement);

}
