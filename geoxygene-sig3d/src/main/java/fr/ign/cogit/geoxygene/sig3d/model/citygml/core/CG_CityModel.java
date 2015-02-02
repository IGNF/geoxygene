package fr.ign.cogit.geoxygene.sig3d.model.citygml.core;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.citygml.core.CityModelImpl;
import org.citygml4j.model.citygml.appearance.AbstractSurfaceData;
import org.citygml4j.model.citygml.appearance.Appearance;
import org.citygml4j.model.gml.Envelope;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class CG_CityModel extends FT_FeatureCollection<CG_CityObject> {

  public void setDpLL(IDirectPosition dpLL) {
    this.dpLL = dpLL;
  }

  public void setDpUR(IDirectPosition dpUR) {
    this.dpUR = dpUR;
  }

  private IDirectPosition dpLL;
  private IDirectPosition dpUR;

  private List<CG_AbstractSurfaceData> lCGA;

  public List<CG_AbstractSurfaceData> getlCGA() {

    if (this.lCGA == null) {

      this.lCGA = new ArrayList<CG_AbstractSurfaceData>();
    }
    return this.lCGA;
  }

  public CG_CityModel() {
    super();
  }

  public CG_CityModel(CityModelImpl impl) {

    super();

    int nbElem = impl.getCityObjectMember().size();

    System.out.println(nbElem);

    if (impl.isSetBoundedBy()) {

      Envelope bS = impl.getBoundedBy().getEnvelope();

      if (bS.getLowerCorner() != null) {

        this.dpLL = new DirectPosition(bS.getLowerCorner().getValue().get(0),
            bS.getLowerCorner().getValue().get(1), bS.getLowerCorner()
                .getValue().get(2));

        this.dpUR = new DirectPosition(bS.getUpperCorner().getValue().get(0),
            bS.getUpperCorner().getValue().get(1), bS.getUpperCorner()
                .getValue().get(2));
      } else {

        List<org.citygml4j.model.gml.DirectPosition> lPos = bS.getPos();

        this.dpLL = new DirectPosition(lPos.get(0).getValue().get(0), lPos
            .get(0).getValue().get(1), lPos.get(0).getValue().get(2));

        this.dpUR = new DirectPosition(lPos.get(1).getValue().get(0), lPos
            .get(1).getValue().get(1), lPos.get(1).getValue().get(2));
      }

    }

    if (impl.isSetAppearanceMember()) {

      int nbApp = impl.getAppearanceMember().size();

      for (int i = 0; i < nbApp; i++) {

        Appearance ap = impl.getAppearanceMember().get(i).getFeature();
        if (ap.isSetSurfaceDataMember()) {

          int nbDataMember = ap.getSurfaceDataMember().size();

          for (int j = 0; j < nbDataMember; j++) {

            AbstractSurfaceData abs = ap.getSurfaceDataMember().get(j)
                .getSurfaceData();

            this.getlCGA().add(
                CG_AbstractSurfaceData.generateAbstractSurfaceData(abs));

          }

        }

      }
    }

  }

  public IDirectPosition getDpLL() {
    return this.dpLL;
  }

  public IDirectPosition getDpUR() {
    return this.dpUR;
  }

  /*
   * protected List<Object> genericApplicationPropertyOfCityModel;
   * 
   * public List<Object> get_GenericApplicationPropertyOfCityModel() { if
   * (genericApplicationPropertyOfCityModel == null) {
   * genericApplicationPropertyOfCityModel = new ArrayList<Object>(); } return
   * this.genericApplicationPropertyOfCityModel; }
   * 
   * public boolean isSet_GenericApplicationPropertyOfCityModel() { return
   * ((this.genericApplicationPropertyOfCityModel != null) &&
   * (!this.genericApplicationPropertyOfCityModel .isEmpty())); }
   * 
   * public void unset_GenericApplicationPropertyOfCityModel() {
   * this.genericApplicationPropertyOfCityModel = null; }
   */

}
