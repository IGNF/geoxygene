package fr.ign.cogit.mapping.webentity.spatial;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.mapping.generalindex.RecordFactory;



public interface GeometryRecordFactory extends RecordFactory<Geometry> {
   /**
    * Create a record from a triple.
    *
    * @param triple
    *           a triple.
    * @return a record based on information in the triple, or <code>null</code>
    *         if no record could be made.
    */
   @Override
   public GeometryRecord createRecord(Graph graph);
}
