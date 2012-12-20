/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.streets;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;

public class CityAxis extends AbstractFeature {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static AtomicInteger counter = new AtomicInteger();
  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private int id;
  private StreetNetwork net;
  private ILineString geomAxis;
  private IGeneObjLin objLin;
  private HashSet<IUrbanBlock> alongBlocks;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public CityAxis(StreetNetwork net, IGeneObjLin objLin) {
    this.net = net;
    this.setObjLin(objLin);
    this.id = CityAxis.counter.getAndIncrement();
    this.geomAxis = objLin.getGeom();
    this.geom = objLin.getGeom();
  }

  // Getters and setters //
  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public StreetNetwork getNet() {
    return this.net;
  }

  public void setNet(StreetNetwork net) {
    this.net = net;
  }

  @Override
  public ILineString getGeom() {
    return this.geomAxis;
  }

  public void setGeomAxis(ILineString geom) {
    this.geomAxis = geom;
  }

  public void setObjLin(IGeneObjLin objLin) {
    this.objLin = objLin;
  }

  public IGeneObjLin getObjLin() {
    return this.objLin;
  }

  public void setAlongBlocks(HashSet<IUrbanBlock> alongBlocks) {
    this.alongBlocks = alongBlocks;
  }

  public HashSet<IUrbanBlock> getAlongBlocks() {
    return this.alongBlocks;
  }

  // Other public methods //
  @Override
  public int hashCode() {
    return this.id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    CityAxis other = (CityAxis) obj;
    if (this.id != other.id) {
      return false;
    }
    if (this.net == null) {
      if (other.net != null) {
        return false;
      }
    } else if (!this.net.equals(other.net)) {
      return false;
    }
    if (this.objLin == null) {
      if (other.objLin != null) {
        return false;
      }
    } else if (!this.objLin.equals(other.objLin)) {
      return false;
    }
    return true;
  }

  @Override
  public void setGeom(IGeometry geom) {
    super.setGeom(geom);
    this.geomAxis = (ILineString) geom;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
