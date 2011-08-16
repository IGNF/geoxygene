/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.relief;

import javax.persistence.Entity;
import javax.persistence.Table;

import fr.ign.cogit.geoxygene.api.schemageo.relief.CourbeDeNiveau;
import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.shemageo.support.champContinu.IsoligneImpl;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
@Entity
@Table(name = "courbe_de_niveau")
public class CourbeDeNiveauImpl extends IsoligneImpl implements CourbeDeNiveau {

  public CourbeDeNiveauImpl(ChampContinu champContinu, double valeur,
      ICurve geom) {
    super(champContinu, valeur, geom);
  }

  public CourbeDeNiveauImpl() {
    super();
  }
}
