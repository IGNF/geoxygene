/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.elev;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.CourbeDeNiveauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/*
 * ###### IGN / CartAGen ###### Title: ContourLine Description: Courbes de
 * niveau Author: J. Renard Date: 18/09/2009
 */

public class VMAPContourLine extends GeneObjDefault implements IContourLine,
    VMAPFeature {

  /**
   * Associated Geoxygene schema object
   */
  private CourbeDeNiveau geoxObj;

  private String fCode;
  private int acc, hqc, mcc, tileId, zv2;
  private long edgeId;

  /**
   * Constructor
   */
  public VMAPContourLine(CourbeDeNiveau geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }
  }

  public VMAPContourLine(ILineString line, double value) {
    super();
    this.geoxObj = new CourbeDeNiveauImpl(new ChampContinuImpl(), value, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }
  }

  public VMAPContourLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new CourbeDeNiveauImpl(new ChampContinuImpl(),
        (Integer) attributes.get("zv2"), line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }
    this.fCode = (String) attributes.get("f_code");
    this.setAltitude((Integer) attributes.get("zv2"));

    if (type.equals(PeaRepDbType.VMAP2i)) {
      this.acc = (Integer) attributes.get("acc");
      this.hqc = (Integer) attributes.get("hqc");
      this.mcc = (Integer) attributes.get("mcc");
      this.zv2 = (Integer) attributes.get("zv2");
    }
    if (type.equals(PeaRepDbType.VMAP1)) {
      this.tileId = (Integer) attributes.get("tile_id");
      this.edgeId = (Long) attributes.get("edg_id");
    }
    if (type.equals(PeaRepDbType.VMAP0)) {
      this.hqc = (Integer) attributes.get("hqc");
      this.tileId = (Integer) attributes.get("tile_id");
      this.edgeId = (Long) attributes.get("edg_id");
    }
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  private boolean isMaster = false;

  @Override
  public boolean isMaster() {
    return this.isMaster;
  }

  @Override
  public double getWidth() {
    if (this.isMaster()) {
      return GeneralisationLegend.CN_LARGEUR_MAITRESSE;
    }
    return GeneralisationLegend.CN_LARGEUR_NORMALE;
  }

  @Override
  public IPolygon getSymbolExtent() {
    return (IPolygon) CommonAlgorithms.buffer(this.getGeom(), this.getWidth()
        * 0.5 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
  }

  @Override
  public double getAltitude() {
    return (this.geoxObj).getValeur();
  }

  @Override
  public void setAltitude(double z) {
    (this.geoxObj).setValeur(z);
  }

  public String getfCode() {
    return fCode;
  }

  public void setfCode(String fCode) {
    this.fCode = fCode;
  }

  public int getAcc() {
    return acc;
  }

  public void setAcc(int acc) {
    this.acc = acc;
  }

  public int getHqc() {
    return hqc;
  }

  public void setHqc(int hqc) {
    this.hqc = hqc;
  }

  public int getMcc() {
    return mcc;
  }

  public void setMcc(int mcc) {
    this.mcc = mcc;
  }

  @Override
  public Map<String, Object> getAttributeMap(VMAPFeature feat) {
    // TODO Auto-generated method stub
    return null;
  }

  public int getTileId() {
    return tileId;
  }

  public void setTileId(int tileId) {
    this.tileId = tileId;
  }

  public long getEdgeId() {
    return edgeId;
  }

  public void setEdgeId(long edgeId) {
    this.edgeId = edgeId;
  }

  public void setZv2(int zv2) {
    this.zv2 = zv2;
  }

  public int getZv2() {
    return zv2;
  }

}
