package fr.ign.cogit.geoxygene.contrib.quality.estim.terrain;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 *            A class to compute the length error involved by not taking account
 *            of altitudes
 *            
 * @author JFGirres
 * 
 */
public class LengthTerrainError {

    private double lengthError;

    public void setLengthError(double lengthError) {
        this.lengthError = lengthError;
    }

    public double getLengthError() {
        return lengthError;
    }

    private double length2D;

    public double getLength2D() {
        return length2D;
    }

    public void setLength2D(double length2d) {
        length2D = length2d;
    }

    private double length2D5;

    public double getLength2D5() {
        return length2D5;
    }

    public void setLength2D5(double length2d5) {
        length2D5 = length2d5;
    }

    public LengthTerrainError(IFeatureCollection<IFeature> jdd2D5) {
        computeError(jdd2D5);
    }

    @SuppressWarnings("unchecked")
    public void computeError(IFeatureCollection<IFeature> jdd2D5) {
        double absCurv2DFinal = 0;
        double absCurv2D5Final = 0;

        for (IFeature ftFeature : jdd2D5) {

            if (ftFeature.getGeom().isMultiCurve()) {
                GM_MultiCurve<GM_LineString> multiLs = (GM_MultiCurve<GM_LineString>) ftFeature.getGeom();
                for (GM_LineString lineString : multiLs.getList()) {
                    IDirectPositionList listePoints = new DirectPositionList();
                    listePoints = lineString.getControlPoint();
                    double absCurv2DTotal = 0;
                    double absCurv2D5Total = 0;
                    for (int i = 0; i < listePoints.size() - 1; i++) {
                        double absCurv2D;
                        double absCurv2D5;
                        IDirectPosition dp1 = listePoints.get(i);
                        IDirectPosition dp2 = listePoints.get(i + 1);
                        absCurv2D = Math.sqrt((dp2.getX() - dp1.getX()) * (dp2.getX() - dp1.getX())
                                + (dp2.getY() - dp1.getY()) * (dp2.getY() - dp1.getY()));
                        absCurv2D5 = Math.sqrt((dp2.getX() - dp1.getX()) * (dp2.getX() - dp1.getX())
                                + (dp2.getY() - dp1.getY()) * (dp2.getY() - dp1.getY()) + (dp2.getZ() - dp1.getZ())
                                * (dp2.getZ() - dp1.getZ()));
                        absCurv2DTotal = absCurv2DTotal + absCurv2D;
                        absCurv2D5Total = absCurv2D5Total + absCurv2D5;
                    }
                    absCurv2DFinal = absCurv2DFinal + absCurv2DTotal;
                    absCurv2D5Final = absCurv2D5Final + absCurv2D5Total;
                }

            } else {
                if (ftFeature.getGeom().isLineString()) {
                    GM_LineString lineString = (GM_LineString) ftFeature.getGeom();
                    IDirectPositionList listePoints = new DirectPositionList();
                    listePoints = lineString.getControlPoint();
                    double absCurv2DTotal = 0;
                    double absCurv2D5Total = 0;
                    for (int i = 0; i < listePoints.size() - 1; i++) {
                        double absCurv2D;
                        double absCurv2D5;
                        IDirectPosition dp1 = listePoints.get(i);
                        IDirectPosition dp2 = listePoints.get(i + 1);
                        absCurv2D = Math.sqrt((dp2.getX() - dp1.getX()) * (dp2.getX() - dp1.getX())
                                + (dp2.getY() - dp1.getY()) * (dp2.getY() - dp1.getY()));
                        absCurv2D5 = Math.sqrt((dp2.getX() - dp1.getX()) * (dp2.getX() - dp1.getX())
                                + (dp2.getY() - dp1.getY()) * (dp2.getY() - dp1.getY()) + (dp2.getZ() - dp1.getZ())
                                * (dp2.getZ() - dp1.getZ()));
                        absCurv2DTotal = absCurv2DTotal + absCurv2D;
                        absCurv2D5Total = absCurv2D5Total + absCurv2D5;
                    }
                    absCurv2DFinal = absCurv2DFinal + absCurv2DTotal;
                    absCurv2D5Final = absCurv2D5Final + absCurv2D5Total;
                }
            }

            double errorLength = absCurv2D5Final - absCurv2DFinal;
            setLength2D(absCurv2DFinal);
            setLength2D5(absCurv2D5Final);
            setLengthError(errorLength);
        }
    }
}
