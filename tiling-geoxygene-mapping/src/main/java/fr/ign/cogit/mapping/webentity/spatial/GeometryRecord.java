package fr.ign.cogit.mapping.webentity.spatial;

/**
 *Dr Tsatcha D.
 */

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.generalindex.Record;

public class GeometryRecord extends Record<Geometry> {
   /**
    * Create a new instance.
    *
    * @param key
    *           a key
    * @param value
    *           a value
    * @return a new record.
    */
   public static GeometryRecord create(Node key, Geometry value) {
      return new GeometryRecord(key, value);
   }


   /**
    * Construct a new instance.
    *
    * @param key
    *           a key
    * @param value
    *           a value
    */
   protected GeometryRecord(Node key, Geometry value) {
      super(key, value);
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return String.format("%s: %s", getKey(), getValue().toText());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      GeometryRecord other = (GeometryRecord) obj;
      if (key == null) {
         if (other.key != null)
            return false;
      } else if (!key.equals(other.key))
         return false;
      if (value == null) {
         if (other.value != null)
            return false;
      } else if (!value.equals(other.value))
         return false;
      return true;
   }

}
