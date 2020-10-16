package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Classe permettant de placer sur un cercle les points générés par un profil
 * 
 * @author MFund
 * @author MBrasebin
 * @author JPerret
 * @author YMeneroux
 * 
 */
public class TransformToCircle {

  private static Logger logger = LogManager.getLogger(TransformToCircle.class);

  public static IFeatureCollection<IFeature> transform(
      IFeatureCollection<IFeature> pPoint, double nbX, int nbY, double rayon) {

    double pas = Math.PI * 2 / nbX;

    IFeatureCollection<IFeature> featOut = new FT_FeatureCollection<IFeature>();

    for (IFeature point : pPoint) {

      try {
        IFeature pOut = point.cloneGeom();

        double x = Double.parseDouble(pOut.getAttribute(BuildingProfileParameters.NAM_ATT_X).toString());

        double y = Double.parseDouble(pOut.getAttribute(BuildingProfileParameters.NAM_ATT_Y).toString());

        double valAlpha = x * pas;

        double r = rayon * (1 + y / nbY);

        IDirectPosition dpP = new DirectPosition(r * Math.cos(valAlpha), r
            * Math.sin(valAlpha));

        pOut.setGeom(new GM_Point(dpP));
        featOut.add(pOut);

      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    return featOut;
  }

  @SuppressWarnings("unchecked")
  public static IFeatureCollection<IFeature> transformLine(
      IFeatureCollection<IFeature> lFeatLine, double rayon, double echantAng) {
    IFeatureCollection<IFeature> featsOut = new FT_FeatureCollection<IFeature>();

    double totalLength = 0;
    double pourcInit = 0;

    IFeature featTemp = lFeatLine.get(0);
    lFeatLine.remove(0);
    lFeatLine.add(featTemp);

    for (IFeature feat : lFeatLine) {

      totalLength = totalLength + feat.getGeom().length();

    }

    for (IFeature feat : lFeatLine) {

      try {
        IFeature featOut = feat.cloneGeom();
        IGeometry geom = feat.getGeom();

        double pourArc = geom.length() / totalLength;

        ILineString ls = null;

        if (geom instanceof ILineString) {
          ls = (ILineString) geom;
        } else if (geom instanceof IMultiCurve<?>) {

          ls = (ILineString) ((IMultiCurve<ICurve>) geom).get(0);

        } else {
          logger.error("Error : geom not linear");
        }

        ILineString lsEch = Operateurs.echantillone(ls, pourArc * echantAng
            * Math.PI / 180);

        IDirectPositionList dpl = lsEch.coord();

        int nbP = dpl.size();

        for (int i = 0; i < nbP; i++) {

          IDirectPosition dpTemp = dpl.get(i);

          dpTemp.setX(rayon
              * Math.cos(Math.PI * 2 * (pourcInit + pourArc * i / (nbP - 1))));
          dpTemp.setY(rayon
              * Math.sin(Math.PI * 2 * (pourcInit + pourArc * i / (nbP - 1))));

        }

        pourcInit = pourcInit + pourArc;

        featOut.setGeom(lsEch);
        featsOut.add(featOut);

      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    return featsOut;
  }

}
