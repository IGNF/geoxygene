package fr.ign.cogit.mapping.util;

import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.datastructure.hexaTree.TransmittedNode;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;



public class NodeUtil {

   /**
    * Get the string representation for a node.
    *
    * Parliament prepends {@link KbGraph#MAGICAL_BNODE_PREFIX} to blank nodes.
    *
    * @param n
    *           a node
    * @return the string representation of that node.
    *La definition du  noeud doit etre compactible avec celui de cartagen...
    */
   public static final String getStringRepresentation(Node n) {
      String stringRep = n.toString();
      return stringRep;
   }



   /**
    * Get the node representation of a string. This only works for URI and blank
    * nodes.
    *
    * This is the reverse of {@link NodeUtil#getStringRepresentation(Node)}.
    *
    * @param representation
    *           a representation of a node.
    * @return the node.
    * A Refaire pour respecter les stratégies de cartagen...
    */
   public static final Node getNodeRepresentation(String representation) {
      Node result = null;
//      if (representation.startsWith(KbGraph.MAGICAL_BNODE_PREFIX)) {
//         result = Node.createAnon(AnonId.create(representation
//               .substring(KbGraph.MAGICAL_BNODE_PREFIX.length())));
//      } else {
//         result = Node.createURI(representation);
//      }
      return result;
   }

    public static final Node buildNode(String str) {
        int minv = -1;
        int maxv = -1;
        String srid = null;
        String signature = null;
        String tableName = null;
        String tableId = null;
        String geoType = null;
        // on realise jamais une generation
        // supérieure à  celle choisit au cadrage
        // de l'ecran..; dans ce cas aucun decoupage
        // a été réalisé...
        int geoOrder = 1;
        int hexaIndiceI = 0;
        int hexaIndiceJ = 0;
        int cadreNumber=-1;
        int hexNumber =-1;
       

        String use = null;
        Node node = null;
        if (str == null) {
            throw new RuntimeException(str + "  " + "est vide");
        } else {
            String[] containt = str.split(" ");

            if (containt.length != 14) {
                throw new RuntimeException(str + " a une taille de "
                        + containt.length + " " + "au lieu de 14");
            } else {

                for (int i = 0; i < containt.length; i++) {
                    String[] term = containt[i].split(":");
                    int taille = term[1].length();
                    // on exploite le troisieme element du noeud
                    if (i == 2) {
                        use = term[1].substring(0, taille - 1);
                        minv = Integer.parseInt(use);
                    }

                    if (i == 3) {
                        use = term[1].substring(0, taille - 1);
                        maxv = Integer.parseInt(use);
                    }

                    if (i == 4) {
                        use = term[1].substring(0, taille - 1);
                        srid = use;
                    }

                    if (i == 5) {
                        use = term[1].substring(0, taille - 1);
                        signature = use;
                    }

                    if (i == 6) {
                        use = term[1].substring(0, taille - 1);
                        tableName = use;
                    }

                    if (i == 7) {
                        use = term[1].substring(0, taille - 1);
                        tableId = use;
                    }

                    if (i == 8) {
                        use = term[1].substring(0, taille - 1);
                        hexaIndiceI = Integer.parseInt(use);
                    }

                    if (i == 9) {
                        use = term[1].substring(0, taille - 1);
                        hexaIndiceJ = Integer.parseInt(use);
                        
                    }

                    if (i == 10) {
                        use = term[1].substring(0, taille - 1);
                        geoOrder = Integer.parseInt(use);
                        
                    }
                    
                    
                    if (i == 11) {
                        use = term[1].substring(0, taille - 1);
                        geoType = use;
                        
                    }
                    
                    if (i == 12) {
                        use = term[1].substring(0, taille - 1);
                        cadreNumber = Integer.parseInt(use);
                        
                        
                    }
                    
                    
                    if (i == 13) {
                        use = term[1].substring(0, taille - 1);
                        hexNumber = Integer.parseInt(use);
                        
                        
                    }
                    
                    
                
                }

                ScaleInfo scale = new ScaleInfo(minv, maxv);
                TransmittedNode transmitted=  new TransmittedNode(hexaIndiceI, hexaIndiceJ, geoOrder,geoType);
                node = new Node(srid, scale, signature, tableName,
                        Integer.parseInt(tableId),transmitted);

            }

        }

        return node;
    }

}
