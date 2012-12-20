/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.relief;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IDEMPixel;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefField;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefTriangle;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.ChampContinu;

/*
 * ###### IGN / CartAGen ###### Title: WaterPoint Description: Noeuds de réseau
 * Author: J. Renard Date: 18/09/2009
 */

public class ReliefField extends GeneObjDefault implements IReliefField {

  private ChampContinu champContinu;

  @Override
  public ChampContinu getChampContinu() {
    return this.champContinu;
  }

  private IFeatureCollection<IContourLine> contourLines;
  private IFeatureCollection<ISpotHeight> spotHeights;
  private IFeatureCollection<IReliefElementLine> reliefElementLines;
  private IFeatureCollection<IDEMPixel> demPixels;
  private IFeatureCollection<IReliefTriangle> triangles;

  public ReliefField(ChampContinu champ) {
    super();
    this.champContinu = champ;
    this.setEliminated(false);
    this.contourLines = new FT_FeatureCollection<IContourLine>();
    this.spotHeights = new FT_FeatureCollection<ISpotHeight>();
    this.reliefElementLines = new FT_FeatureCollection<IReliefElementLine>();
    this.demPixels = new FT_FeatureCollection<IDEMPixel>();
    this.triangles = new FT_FeatureCollection<IReliefTriangle>();
  }

  /**
   * @return le nom de l'objet
   */
  @Override
  public String getName() {
    return this.champContinu.getNom();
  }

  @Override
  public void setName(String nom) {
    this.champContinu.setNom(nom);
  }

  /**
   * @return les isolignes de l'objet
   */

  @Override
  public IFeatureCollection<IContourLine> getContourLines() {
    return this.contourLines;
  }

  @Override
  public void setContourLines(IFeatureCollection<IContourLine> contourLines) {
    this.contourLines = contourLines;
  }

  @Override
  public void addContourLine(IContourLine line) {
    this.contourLines.add(line);
  }

  @Override
  public void removeContourLine(IContourLine line) {
    this.contourLines.remove(line);
  }

  /**
   * @return les points cotes de l'objet
   */

  @Override
  public IFeatureCollection<ISpotHeight> getSpotHeights() {
    return this.spotHeights;
  }

  @Override
  public void setSpotHeights(IFeatureCollection<ISpotHeight> spotHeights) {
    this.spotHeights = spotHeights;
  }

  @Override
  public void addSpotHeight(ISpotHeight spot) {
    this.spotHeights.add(spot);
  }

  @Override
  public void removeSpotHeight(ISpotHeight spot) {
    this.spotHeights.remove(spot);
  }

  /**
   * @return les elements caracteristiques de l'objet
   */

  @Override
  public IFeatureCollection<IReliefElementLine> getReliefElementLines() {
    return this.reliefElementLines;
  }

  @Override
  public void setReliefElementLines(
      IFeatureCollection<IReliefElementLine> reliefElementLines) {
    this.reliefElementLines = reliefElementLines;
  }

  @Override
  public void addReliefElementLine(IReliefElementLine line) {
    this.reliefElementLines.add(line);
  }

  @Override
  public void removeReliefElementLine(IReliefElementLine line) {
    this.reliefElementLines.remove(line);
  }

  /**
   * @return les pixels MNT de l'objet
   */

  @Override
  public IFeatureCollection<IDEMPixel> getDEMPixels() {
    return this.demPixels;
  }

  @Override
  public void setDEMPixels(IFeatureCollection<IDEMPixel> pixels) {
    this.demPixels = pixels;
  }

  @Override
  public void addDEMPixel(IDEMPixel pix) {
    this.demPixels.add(pix);
  }

  @Override
  public void removeDEMPixel(IDEMPixel pix) {
    this.demPixels.remove(pix);
  }

  /**
   * @return les triangles issus de la triangulation de l'objet
   */

  @Override
  public IFeatureCollection<IReliefTriangle> getTriangles() {
    return this.triangles;
  }

  @Override
  public void setTriangles(IFeatureCollection<IReliefTriangle> triangles) {
    this.triangles = triangles;
  }

  /**
   * Minimum Z value of the relief field
   */
  private double zMin = Double.MAX_VALUE;

  @Override
  public double getZMin() {
    if (this.zMin == Double.MAX_VALUE) {
      // System.out.println("min");
      for (IContourLine cn : this.getContourLines()) {
        if (cn.getWidth() < this.zMin) {
          this.zMin = cn.getWidth();
        }
      }
      for (IDEMPixel pix : this.getDEMPixels()) {
        if (pix.getZ() < this.zMin) {
          this.zMin = pix.getZ();
        }
      }
    }
    return this.zMin;
  }

  /**
   * Maximum Z value of the relief field
   */
  private double zMax = Double.MIN_VALUE;

  @Override
  public double getZMax() {
    if (this.zMax == Double.MIN_VALUE) {
      // System.out.println("max");
      for (IContourLine cn : this.getContourLines()) {
        if (cn.getWidth() > this.zMax) {
          this.zMax = cn.getWidth();
        }
      }
      for (IDEMPixel pix : this.getDEMPixels()) {
        if (pix.getZ() > this.zMax) {
          this.zMax = pix.getZ();
        }
      }
    }
    return this.zMax;
  }

}
