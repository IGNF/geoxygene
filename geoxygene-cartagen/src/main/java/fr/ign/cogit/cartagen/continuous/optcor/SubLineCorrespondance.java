/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous.optcor;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;

/**
 * Interface for correspondances between subline parts in the OptCor continuous
 * generalisation method. Correspondances can be between vertices, between
 * segments, or between a vertex and segments.
 * 
 * @author Guillaume Touya
 *
 */
public interface SubLineCorrespondance {

    public List<Object> getMatchedFeaturesInitial();

    public List<Object> getMatchedFeaturesFinal();

    public enum CorrespondanceType {
        C1, C2, C3, C1_, C2_, C3_
    }

    public CorrespondanceType getType();

    public IDirectPositionList morphCorrespondance(double t);
}
