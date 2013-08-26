package fr.ign.cogit.geoxygene.contrib.quality.estim.scaledetection;

import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;

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
 *            Abstract class for estimating respresentation scale of a
 *            generalised dataset
 *            
 * @author JFGirres
 * 
 */
public abstract class AbstractScaleDetection {

    private double scale;

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    private double symbolSize;

    public void setSymbolSize(double symbolSize) {
        this.symbolSize = symbolSize;
    }

    public double getSymbolSize() {
        return symbolSize;
    }

    private CarteTopo carteTopoRoads;

    public void setCarteTopoRoads(CarteTopo carteTopoRoads) {
        this.carteTopoRoads = carteTopoRoads;
    }

    public CarteTopo getCarteTopoRoads() {
        return carteTopoRoads;
    }

    public AbstractScaleDetection(CarteTopo carteTopoRoads) {
        this.carteTopoRoads = carteTopoRoads;
    }

}
