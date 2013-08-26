package fr.ign.cogit.geoxygene.contrib.quality.comparison;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.quality.comparison.cutting.Cutting;
import fr.ign.cogit.geoxygene.contrib.quality.comparison.measure.Measure;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

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
 *            An abstract class to perform comparisons bet
 * 
 * @author JFGirres
 * 
 */
public abstract class AbstractComparison<Geom extends IGeometry> implements Comparison<Geom> {

    private IFeatureCollection<IFeature> jddRef;
    private IFeatureCollection<IFeature> jddComp;

    @Override
    public IFeatureCollection<IFeature> getJddRef() {
        return jddRef;
    }

    public void setJddRef(IFeatureCollection<IFeature> jddRef) {
        this.jddRef = jddRef;
    }

    @Override
    public IFeatureCollection<IFeature> getJddComp() {
        return jddComp;
    }

    public void setJddComp(IFeatureCollection<IFeature> jddComp) {
        this.jddComp = jddComp;
    }

    private IFeatureCollection<IFeature> jddRefOut = new FT_FeatureCollection<IFeature>();
    private IFeatureCollection<IFeature> jddCompOut = new FT_FeatureCollection<IFeature>();

    private boolean automaticMatching = true;

    /**
     * true = Automatique false = Manuel
     * @param automaticMatching
     */
    public void setAutomaticMatching(boolean automaticMatching) {
        this.automaticMatching = automaticMatching;
    }

    public boolean isAutomaticMatching() {
        return automaticMatching;
    }

    private Cutting<Geom> cuttingMethod = new Cutting<Geom>();

    public Cutting<Geom> getCuttingMethod() {
        return cuttingMethod;
    }

    public void setCuttingMethod(Cutting<Geom> cuttingMethod) {
        this.cuttingMethod = cuttingMethod;
    }

    private List<Class<? extends Measure>> measures = null;
    private Map<Class<? extends Measure>, List<Double>> measurements = new HashMap<Class<? extends Measure>, List<Double>>();

    @Override
    public List<Double> getMeasurements(Class<? extends Measure> measureClass) {
        return this.measurements.get(measureClass);
    }

    public void setJddRefOut(IFeatureCollection<IFeature> jddRefOut) {
        this.jddRefOut = jddRefOut;
    }

    public IFeatureCollection<IFeature> getJddRefOut() {
        return jddRefOut;
    }

    public void setJddCompOut(IFeatureCollection<IFeature> jddCompOut) {
        this.jddCompOut = jddCompOut;
    }

    public IFeatureCollection<IFeature> getJddCompOut() {
        return jddCompOut;
    }

    public AbstractComparison(IFeatureCollection<IFeature> jddRef, IFeatureCollection<IFeature> jddComp,
            List<Class<? extends Measure>> measures) {
        this.jddRef = jddRef;
        this.jddComp = jddComp;
        this.measures = measures;
    }

    /**
     * Execute the comparison between two datasets
     */
    @Override
    public abstract void executeComparison();

    /**
     * Compute comparison indicators
     * @param geomRef
     * @param geomComp
     */
    @Override
    public void computeIndicators(Geom geomRef, Geom geomComp) {
        for (Class<? extends Measure> measureClass : this.measures) {
            try {
                Constructor<? extends Measure> constructor = measureClass.getConstructor(geomRef.getClass(),
                        geomComp.getClass());
                Measure m = constructor.newInstance(geomRef, geomComp);
                double measurement = m.getMeasure();
                List<Double> list = this.measurements.get(measureClass);
                if (list == null) {
                    list = new ArrayList<Double>();
                    this.measurements.put(measureClass, list);
                }
                list.add(new Double(measurement));
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A VALIDER !!!! Fill the FeatureCollections containing the outputs
     * geometry used for comparisons
     */
    @Override
    public void fillDatasetsOutputs(Geom geomRef, Geom geomComp) {
        IFeature ftRef = new DefaultFeature(geomRef);
        IFeature ftComp = new DefaultFeature(geomComp);
        jddRefOut.add(ftRef);
        jddCompOut.add(ftComp);
    }
}
