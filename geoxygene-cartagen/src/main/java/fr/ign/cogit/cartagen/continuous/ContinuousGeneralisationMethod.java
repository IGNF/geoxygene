/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * An interface for continuous generalisation methods (or morphing) between an
 * initial detailed geometry and a final less detailed geometry.
 * 
 * @author Guillaume Touya
 *
 */
public interface ContinuousGeneralisationMethod {

    /**
     * The initial (i.e. most detailed) geometry being continuously generalised.
     * 
     * @return
     */
    public IGeometry getGeomIni();

    /**
     * The final (i.e. least detailed) geometry that is the target of continuous
     * generalisation.
     * 
     * @return
     */
    public IGeometry getGeomFinal();

    /**
     * The continuous generalisation function for a default geometry and a
     * parameter t between 0 and 1. If t is 0, returns the initial geometry, if
     * t is 1, returns the final geometry.
     * 
     * @param t
     * @return
     */
    public IGeometry continuousGeneralisation(double t);
}
