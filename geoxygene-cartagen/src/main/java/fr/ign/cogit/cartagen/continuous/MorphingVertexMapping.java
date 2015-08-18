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

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;

/**
 * A class that maps the vertices of an initial geometry to the vertices of a
 * final geometry in a morphing process.
 * 
 * @author Guillaume Touya
 *
 */
public class MorphingVertexMapping {

    private IDirectPositionList initialCoords, finalCoords;
    private Map<IDirectPosition, IDirectPosition> mapping;

    public MorphingVertexMapping(IDirectPositionList initialCoords,
            IDirectPositionList finalCoords) {
        super();
        this.initialCoords = initialCoords;
        this.finalCoords = finalCoords;
        mapping = new HashMap<>();
        for (int i = 0; i < initialCoords.size(); i++)
            mapping.put(initialCoords.get(i), finalCoords.get(i));
    }

    public IDirectPositionList getInitialCoords() {
        return initialCoords;
    }

    public void setInitialCoords(IDirectPositionList initialCoords) {
        this.initialCoords = initialCoords;
    }

    public IDirectPositionList getFinalCoords() {
        return finalCoords;
    }

    public void setFinalCoords(IDirectPositionList finalCoords) {
        this.finalCoords = finalCoords;
    }

    public IDirectPosition getMapping(IDirectPosition initialVertex) {
        return mapping.get(initialVertex);
    }
}
