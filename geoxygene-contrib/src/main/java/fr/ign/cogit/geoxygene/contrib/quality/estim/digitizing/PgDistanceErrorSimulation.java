package fr.ign.cogit.geoxygene.contrib.quality.estim.digitizing;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.quality.util.RandomGenerator;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

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
 * @author JFGirres
 */
public class PgDistanceErrorSimulation extends AbstractErrorSimulation<GM_Polygon> {

    public PgDistanceErrorSimulation() {
    }

    /**
     * A basic method to simulate digitizing error on polygons (by determining
     * the offset distance based a gaussian distribution).
     */
    @Override
    public void executeSimulation() {

        IFeatureCollection<IFeature> jddPolyInitial = this.getJddIn();
        IFeatureCollection<IFeature> jddPolySimule = new FT_FeatureCollection<IFeature>();

        double ecartType = this.getEcartType();
        double moyenne = this.getMoyenne();

        for (IFeature feature : jddPolyInitial) {
            IDirectPositionList dplIntital = feature.getGeom().coord();
            IDirectPositionList dplSimule = new DirectPositionList();

            for (int j = 0; j < dplIntital.size(); j++) {
                IDirectPosition dp = dplIntital.get(j);
                double angleErreurSommet = Math.abs(RandomGenerator.genereAngle());
                double distAleatoireSommet = Math.abs((RandomGenerator.genereNumLoiNormale() * ecartType) + moyenne);
                double offsetX = Math.cos(angleErreurSommet) * distAleatoireSommet * RandomGenerator.genereSigne(), offsetY = Math
                        .sin(angleErreurSommet) * distAleatoireSommet * RandomGenerator.genereSigne();

                dplSimule.add(new DirectPosition(dp.getX() + offsetX, dp.getY() + offsetY));
            }

            IDirectPosition dpCloture = dplSimule.get(0);
            dplSimule.remove(dplIntital.size() - 1);
            dplSimule.add(dpCloture);

            IPolygon polygone = new GM_Polygon(new GM_LineString(dplSimule));

            jddPolySimule.add(new DefaultFeature(polygone));
        }
        this.setJddOut(jddPolySimule);
    }
}
