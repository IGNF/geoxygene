/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.road;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.road.IDualCarriageWay;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.routier.CarrefourComplexe;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.CarrefourComplexeImpl;

/*
 * ###### IGN / CartAGen ###### Title: Interchange Description: Echangeurs
 * Author: J. Renard Date: 21/10/2009
 */

public class DualCarriageway extends GeneObjSurfDefault implements
    IDualCarriageWay {

  /**
   * Associated Geoxygene schema object
   */
  private CarrefourComplexe geoxObj;
  private Set<IRoadLine> innerRoads, outerRoads;
  private int importance = 0;

  /**
   * Constructor
   */
  public DualCarriageway(CarrefourComplexe geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.innerRoads = new HashSet<>();
    this.outerRoads = new HashSet<>();
  }

  public DualCarriageway(IPolygon poly) {
    super();
    this.geoxObj = new CarrefourComplexeImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.innerRoads = new HashSet<>();
    this.outerRoads = new HashSet<>();
  }

  public DualCarriageway(IPolygon poly, int importance) {
    super();
    this.geoxObj = new CarrefourComplexeImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setImportance(importance);
    this.innerRoads = new HashSet<>();
    this.outerRoads = new HashSet<>();
  }

  public DualCarriageway(IPolygon poly, int importance,
      Collection<IRoadLine> innerRoads) {
    super();
    this.geoxObj = new CarrefourComplexeImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setImportance(importance);
    this.innerRoads = new HashSet<>();
    this.innerRoads.addAll(innerRoads);
    this.outerRoads = new HashSet<>();
  }

  public DualCarriageway(IPolygon poly, int importance,
      Collection<IRoadLine> innerRoads, Collection<IRoadLine> outerRoads) {
    super();
    this.geoxObj = new CarrefourComplexeImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setImportance(importance);
    this.innerRoads = new HashSet<>();
    this.innerRoads.addAll(innerRoads);
    this.outerRoads = new HashSet<>();
    this.outerRoads.addAll(outerRoads);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  public int getImportance() {
    return importance;
  }

  @Override
  public void setImportance(int importance) {
    this.importance = importance;
  }

  @Override
  public Set<IRoadLine> getInnerRoads() {
    return innerRoads;
  }

  @Override
  public Set<IRoadLine> getOuterRoads() {
    return outerRoads;
  }

  public void setInnerRoads(Set<IRoadLine> innerRoads) {
    this.innerRoads = innerRoads;
  }

  public void setOuterRoads(Set<IRoadLine> outerRoads) {
    this.outerRoads = outerRoads;
  }

  /**
   * Get the dual carriageway instance from the urban block sharing the same
   * geometry.
   * @param block
   * @param branchs
   * @return
   */
  public static IDualCarriageWay getDualCarriageWay(IUrbanBlock block,
      Collection<IDualCarriageWay> duals) {
    for (IDualCarriageWay d : duals) {
      if (block.getGeom().equals(d.getGeom())) {
        return d;
      }
    }
    return null;
  }
}
