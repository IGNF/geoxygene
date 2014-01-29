package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

/**
 * Collection of tools dealing with IGeometry objects
 * 
 * @author JeT
 * 
 */
public class IGeometryUtil {

    private IGeometryUtil() {
        // tools library class
    }

    /**
     * generate the envelope of a list of geometries by combining all envelopes
     * 
     * @param geometries
     *            list of geometries to compute envelope
     * @return the overall envelope
     */
    public static IEnvelope getEnvelope(List<? extends IGeometry> geometries) {
        IEnvelope envelope = new GM_Envelope();

        for (int nGeometry = 0; nGeometry < geometries.size(); nGeometry++) {
            IGeometry geometry = geometries.get(nGeometry);
            if (nGeometry == 0) {
                envelope.setLowerCorner(geometry.getEnvelope().getLowerCorner());
                envelope.setUpperCorner(geometry.getEnvelope().getUpperCorner());
            } else {
                envelope.expand(geometry.getEnvelope());
            }
        }
        return envelope;
    }

}
