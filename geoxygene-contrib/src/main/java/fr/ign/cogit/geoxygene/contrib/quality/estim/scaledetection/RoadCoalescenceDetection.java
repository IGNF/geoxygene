package fr.ign.cogit.geoxygene.contrib.quality.estim.scaledetection;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

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
 *            A class to estimate the representation scale of a road network,
 *            based on the detection of symbol coalescence
 * 
 * @author JFGirres
 * 
 */
public class RoadCoalescenceDetection extends AbstractScaleDetection {
    static Logger logger = Logger.getLogger(RoadCoalescenceDetection.class.getName());

    private boolean thickened;

    public void setThickened(boolean thickened) {
        this.thickened = thickened;
    }

    public boolean isThickened() {
        return thickened;
    }

    private IFeatureCollection<IFeature> jddThickeningBuffer;

    public void setJddThickeningBuffer(IFeatureCollection<IFeature> jddThickeningBuffer) {
        this.jddThickeningBuffer = jddThickeningBuffer;
    }

    public IFeatureCollection<IFeature> getJddThickeningBuffer() {
        return jddThickeningBuffer;
    }

    private IFeatureCollection<IFeature> jddThickeningPoint;

    public void setJddThickeningPoint(IFeatureCollection<IFeature> jddThickeningPoint) {
        this.jddThickeningPoint = jddThickeningPoint;
    }

    public IFeatureCollection<IFeature> getJddThickeningPoint() {
        return jddThickeningPoint;
    }

    private double threshold;

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getThreshold() {
        return threshold;
    }

    public RoadCoalescenceDetection(CarteTopo carteTopoRoads) {
        super(carteTopoRoads);
    }

    /**
     * Detection of thickening areas in a road network dataset(especially in
     * curves) according to a specific scale and symbole size
     */
    public IFeatureCollection<IFeature> execute() {

        IPopulation<Arc> popArcs = this.getCarteTopoRoads().getPopArcs();
        double tailleBuffer;
        IFeatureCollection<IFeature> jddThickeningBufferTemp = new FT_FeatureCollection<IFeature>();
        IFeatureCollection<IFeature> jddThickeningPointTemp = new FT_FeatureCollection<IFeature>();

        for (Arc arc : popArcs) {

            tailleBuffer = RoadTypeBuffer.computeSizeTop100(arc, this.getScale());

            GM_LineString lsArc = (GM_LineString) arc.getGeom();
            IMultiCurve<ILineString> lsMultiBufferDroite = JtsAlgorithms.offsetCurve(lsArc, -tailleBuffer);
            IMultiCurve<ILineString> lsMultiBufferGauche = JtsAlgorithms.offsetCurve(lsArc, tailleBuffer);

            jddThickeningBufferTemp.add(new DefaultFeature(lsMultiBufferGauche));
            jddThickeningBufferTemp.add(new DefaultFeature(lsMultiBufferDroite));

            for (IDirectPosition dp : lsArc.coord()) {

                if (!(lsMultiBufferDroite == null)) {
                    double distanceMin = Double.POSITIVE_INFINITY;
                    for (ILineString lsBufferDroite : lsMultiBufferDroite) {
                        double distanceDroite = Distances.distance(dp, lsBufferDroite);
                        if (distanceDroite < distanceMin) {
                            distanceMin = distanceDroite;
                        }
                    }
                    if (distanceMin > this.getThreshold() * tailleBuffer) {
                        jddThickeningPointTemp.add(new DefaultFeature(dp.toGM_Point()));
                    }
                }

                if (!(lsMultiBufferGauche == null)) {
                    double distanceMin = Double.POSITIVE_INFINITY;
                    for (ILineString lsBufferGauche : lsMultiBufferGauche) {
                        double distanceGauche = Distances.distance(dp, lsBufferGauche);
                        if (distanceGauche < distanceMin) {
                            distanceMin = distanceGauche;
                        }
                    }
                    if (distanceMin > this.getThreshold() * tailleBuffer) {
                        jddThickeningPointTemp.add(new DefaultFeature(dp.toGM_Point()));
                    }
                }
            }
        }

        logger.debug("Points = " + jddThickeningPointTemp.size());

        if (jddThickeningPointTemp.size() > 50) {
            setThickened(true);
            setJddThickeningBuffer(jddThickeningBufferTemp);
            setJddThickeningPoint(jddThickeningPointTemp);
        }

        return jddThickeningBufferTemp;
    }
}
