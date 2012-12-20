/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.relief;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.ChampContinu;

/*
 * ###### IGN / CartAGen ###### Title: ReliefField Description: Champ relief
 * Author: J. Renard Date: 04/01/2012
 */

public interface IReliefField extends IGeneObj {

  /**
   * @return
   */
  public ChampContinu getChampContinu();

  /**
   * @return le nom de l'objet
   */
  public String getName();

  public void setName(String nom);

  /**
   * @return les isolignes de l'objet
   */
  public IFeatureCollection<IContourLine> getContourLines();

  public void setContourLines(IFeatureCollection<IContourLine> contourLines);

  public void addContourLine(IContourLine line);

  public void removeContourLine(IContourLine line);

  /**
   * @return les points cotes de l'objet
   */
  public IFeatureCollection<ISpotHeight> getSpotHeights();

  public void setSpotHeights(IFeatureCollection<ISpotHeight> spotHeights);

  public void addSpotHeight(ISpotHeight spot);

  public void removeSpotHeight(ISpotHeight spot);

  /**
   * @return les elements caracteristiques de l'objet
   */
  public IFeatureCollection<IReliefElementLine> getReliefElementLines();

  public void setReliefElementLines(
      IFeatureCollection<IReliefElementLine> reliefElementLines);

  public void addReliefElementLine(IReliefElementLine line);

  public void removeReliefElementLine(IReliefElementLine line);

  /**
   * @return les pixels MNT de l'objet
   */
  public IFeatureCollection<IDEMPixel> getDEMPixels();

  public void setDEMPixels(IFeatureCollection<IDEMPixel> pixels);

  public void addDEMPixel(IDEMPixel pix);

  public void removeDEMPixel(IDEMPixel pix);

  /**
   * @return les triangles de relief issus de la triangulation du champ relief
   */
  public IFeatureCollection<IReliefTriangle> getTriangles();

  public void setTriangles(IFeatureCollection<IReliefTriangle> triangles);

  /**
   * @return the minimal Z value of the relief field
   */
  public double getZMin();

  /**
   * @return the maximal Z value of the relief field
   */
  public double getZMax();

  public static final String FEAT_TYPE_NAME = "ReliefField"; //$NON-NLS-1$

}
