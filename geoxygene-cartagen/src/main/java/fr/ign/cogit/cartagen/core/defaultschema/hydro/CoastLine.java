/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/

package fr.ign.cogit.cartagen.core.defaultschema.hydro;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

@Entity
@Access(AccessType.PROPERTY)
public class CoastLine extends GeneObjLinDefault implements ICoastLine {

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
    return super.getGeom();
  }

  @Override
  public boolean isEliminated() {
    return super.isEliminated();
  }

  @Override
  @Id
  public int getId() {
    return super.getId();
  }

  public CoastLine(ILineString line) {
    super();
    this.setInitialGeom(line);
    this.setGeom(line);
  }

}
