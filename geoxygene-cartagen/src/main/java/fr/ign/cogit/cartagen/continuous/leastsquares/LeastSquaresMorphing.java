/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous.leastsquares;

import fr.ign.cogit.cartagen.continuous.ContinuousGeneralisationMethod;
import fr.ign.cogit.cartagen.continuous.MorphingVertexMapping;
import fr.ign.cogit.cartagen.continuous.optcor.OptCorMorphing;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Implementation of the continuous generalisation method from Peng et al.
 * (2013, ICA Generalisation Workshop). It is a line morphing optimised by a
 * Least Squares adjustment. The equations are not linear, so an iterative least
 * squares adjustment is used. The initial step to match line vertices uses the
 * optcor matching process.
 * @author GTouya
 * 
 */
public class LeastSquaresMorphing implements ContinuousGeneralisationMethod {

  private ILineString geomIni, geomFinal;
  private int k = 20;

  public LeastSquaresMorphing(ILineString geomIni, ILineString geomFinal) {
    super();
    this.geomIni = geomIni;
    this.geomFinal = geomFinal;
  }

  @Override
  public IGeometry getGeomIni() {
    return geomIni;
  }

  @Override
  public IGeometry getGeomFinal() {
    return geomFinal;
  }

  @Override
  public IGeometry continuousGeneralisation(double t) {
    // first, compute the vertex mapping between both polylines
    OptCorMorphing optcor = new OptCorMorphing(geomIni, geomFinal);
    MorphingVertexMapping mapping = optcor.matchLinesVertices();

    // k iterations to time t are used to compute the final morphing
    for (int i = 0; i < k; i++) {
      // compute the time value at iteration i
      double time = t * (i + 1) / k;
      // TODO
    }
    // TODO Auto-generated method stub
    return null;
  }

}
