package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.feature.FT_Feature;

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
 *            A class for Contour lines used in the methods to delineate
 *            mountain areas
 *            
 * @author JFGirres
 * 
 */
public class Contour extends FT_Feature {

    private double valeur;

    public Contour(ILineString lineString, double valeur) {
        super(lineString);
        this.valeur = valeur;
    }

    public void setValeur(double valeur) {
        this.valeur = valeur;
    }

    public double getValeur() {
        return valeur;
    }

}
