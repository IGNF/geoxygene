package fr.ign.cogit.geoxygene.contrib.quality.estim.digitizing;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

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
 *            An abstract class to simulate digitizing error on vector objects
 * @author JFGirres
 * 
 */
public abstract class AbstractErrorSimulation<Geom extends GM_Object> {

    private CarteTopo carteTopoIn;

    public CarteTopo getCarteTopoIn() {
        return carteTopoIn;
    }

    public void setCarteTopoIn(CarteTopo carteTopoIn) {
        this.carteTopoIn = carteTopoIn;
    }

    private IFeatureCollection<IFeature> jddIn;

    public IFeatureCollection<IFeature> getJddIn() {
        return jddIn;
    }

    public void setJddIn(IFeatureCollection<IFeature> jddIn) {
        this.jddIn = jddIn;
    }

    private double ecartType;

    public void setEcartType(double ecartType) {
        this.ecartType = ecartType;
    }

    public double getEcartType() {
        return ecartType;
    }

    private double moyenne;

    public void setMoyenne(double moyenne) {
        this.moyenne = moyenne;
    }

    public double getMoyenne() {
        return moyenne;
    }

    private IFeatureCollection<IFeature> jddOut = new FT_FeatureCollection<IFeature>();

    public void setJddOut(IFeatureCollection<IFeature> jddOut) {
        this.jddOut = jddOut;
    }

    public IFeatureCollection<IFeature> getJddOut() {
        return jddOut;
    }

    public AbstractErrorSimulation() {
    }

    /**
     * Execute the simulation of errors in a polyline
     */
    public abstract void executeSimulation();

}
