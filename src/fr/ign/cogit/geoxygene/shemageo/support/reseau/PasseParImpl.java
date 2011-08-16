/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.reseau;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Franchissement;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.PassePar;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * (peut-etre inutile sous forme de classe; a voir a l'usage)
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class PasseParImpl extends ElementDuReseauImpl implements PassePar {

  @Override
  public IPoint getGeom() {
    return this.getFranchissement().getGeom();
  }

  /**
   * l'arc concerne pas la relation passe par
   */
  private ArcReseau arc = null;

  public ArcReseau getArc() {
    return this.arc;
  }

  public void setArc(ArcReseau arc) {
    this.arc = arc;
  }

  /**
   * le franchissement concerne pas la relation passe par
   */
  private Franchissement franchissement = null;

  public Franchissement getFranchissement() {
    return this.franchissement;
  }

  public void setFranchissement(Franchissement franchissement) {
    this.franchissement = franchissement;
  }

  /**
   * le niveau
   */
  private int niveau = 0;

  public int getNiveau() {
    return this.niveau;
  }

  public void setNiveau(int niveau) {
    this.niveau = niveau;
  }

}
