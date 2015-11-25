package fr.ign.cogit.mapping.webentity.spatial;

import com.vividsolutions.jts.geom.Geometry;


/*
 * @author Dr Tsatcha
 * Cette classe permet de stocker un géometrique
 * primitive (son type et sa geometrie)
 * @see 
 */

public class TilingGeo {
    
    public String geoType;
    
    public Geometry geom;

    /**
     * @param geoType
     * @param geom
     */
    public TilingGeo(String geoTypeParam, Geometry geomParam) {
        super();
        this.geoType = geoTypeParam;
        this.geom = geomParam;
    }
    

    public String getGeoType() {
        return geoType;
    }

    public void setGeoType(String geoType) {
        this.geoType = geoType;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }
    
    
    
    

}
