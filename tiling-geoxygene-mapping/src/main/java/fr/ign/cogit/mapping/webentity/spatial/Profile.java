package fr.ign.cogit.mapping.webentity.spatial;


/*
 * Dr Tsatcha D
 */
import com.vividsolutions.jts.geom.Geometry;

public interface Profile {
   public GeometryRecordFactory getRecordFactory();

    public String getIndexDir();
}
