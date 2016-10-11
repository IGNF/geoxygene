package fr.ign.cogit.geoxygene.sig3d.sample;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Edge;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Vertex;
import fr.ign.cogit.geoxygene.sig3d.topology.CarteTopo3D;

public class CarteTopo3DExec {

  public static void main(String[] args) {

    ISolid s = DisplayData.createCube();

    IFeature feat = new DefaultFeature(s);
    CarteTopo3D ct3D = new CarteTopo3D(feat);

    System.out.println("Test");
    System.out.println("NB Faces : " + ct3D.getlTrianglesTopo().size());
    System.out.println("NB Arrêtes : " + ct3D.getlEdgeTopo().size());
    System.out.println("NB Sommets : " + ct3D.getlVertexTopo().size());

    for (Triangle tri : ct3D.getlTrianglesTopo()) {

      System.out.println("Nombres arrêtes pour une face : "
          + tri.calculEdge().size());

    }

    for (Edge e : ct3D.getlEdgeTopo()) {

      System.out.println("Nombre de triangles voisins pour une arrête : "
          + e.getNeighbourTriangles().size());

    }

    for (Vertex v : ct3D.getlVertexTopo()) {

      System.out.println("Nombre de triangles voisins pour un sommet : "
          + v.getLTRiRel().size());

    }

    System.out.println("Nombre de groupes : " + ct3D.getGroupes().size());

  }
}
