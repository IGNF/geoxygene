package fr.ign.cogit.mapping.storage;


import java.io.Serializable;

/**
 * Representation des données géometriques à stocker.
 *
 * @author Dr Tsatcha D.
 *
 */
public class NodeData implements Serializable {

   private static final long serialVersionUID = -9036265713376562086L;
   private String node;
   private byte[] extent;
   private int id;
   private String crsCode;

   /**
    * Crèe une nouvelle instance.
    *
    * @param id
    *           l'id.
    * @param node
    *           le noeud.
    * @param extent
    *           l'extensio,.
    * @param crsCode
    *           le système de coordonnées de reference utilisé.
    */
   public NodeData(int id, String node, byte[] extent, String crsCode) {
      this.id = id;
      this.node = node;
      this.extent = extent;
      this.crsCode = crsCode;
   }

   /**
    * Recupère le système de reference.
    *
    * @return le code.
    */
   public String getCRSCode() {
      return crsCode;
   }

   /**
    * Obtient l'id.
    *
    * @return retourne id.
    */
   public int getId() {
      return id;
   }

   /**
    * Obtient le noeud.
    *
    * @return le noeud.
    */
   public String getNode() {
      return node;
   }

   /**
    * Obtient l'extension.
    *
    * @return l'extension.
    */
   public byte[] getExtent() {
      return extent;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return "NodeData [node=" + node + ", id=" + id + ", crsCode=" + crsCode + "]";
   }
}