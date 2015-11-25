package fr.ign.cogit.mapping.storage;


import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialSerialKeyCreator;

/**
 * A secondary key creator. This generates a key that maps the integer ID
 * field in the <code>NodeData</code> class to the data.
 *
 * @author Dtsatcha
 *
 */
public class NodeIdKeyCreator extends
      SerialSerialKeyCreator<NodeKey, NodeData, Integer> {
   public NodeIdKeyCreator(ClassCatalog catalog) {
      super(catalog, NodeKey.class, NodeData.class, Integer.class);
   }

   @Override
   public Integer createSecondaryKey(NodeKey primaryKey, NodeData data) {
      return data.getId();
   }
}
