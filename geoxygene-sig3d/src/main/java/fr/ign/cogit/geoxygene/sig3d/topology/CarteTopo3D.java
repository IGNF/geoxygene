package fr.ign.cogit.geoxygene.sig3d.topology;

import java.util.ArrayList;
import java.util.List;


import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Edge;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Vertex;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.7
 * */
public class CarteTopo3D {

  private List<Triangle> lTrianglesTopo = new ArrayList<Triangle>();
  private List<Edge> lEdgeTopo = new ArrayList<Edge>();
  private List<Vertex> lVertexTopo = new ArrayList<Vertex>();
  private List<List<Triangle>> lGroupes = null;

  public static final double AREA_EPSILON = 0.0001;

  public CarteTopo3D(IGeometry geom) {
  
    List<IOrientableSurface> lIOS = FromGeomToSurface.convertGeom(geom);
    
    
    if (lIOS != null && ! lIOS.isEmpty()) {
      
      List<ITriangle> lTri = FromPolygonToTriangle.convertAndTriangle(lIOS);
      if (lTri != null && ! lTri.isEmpty()) {

      process(lTri);
      }

    }

  }

  public CarteTopo3D(IFeature feat) {

    this(feat.getGeom());

  }

  private void process(List<ITriangle> lTri) {

    for (ITriangle tri : lTri) {

      // System.out.println(tri.area());

      if (tri.area() < AREA_EPSILON) {

        continue;
      }

      Vertex v1 = new Vertex(tri.coord().get(0));
      Vertex v2 = new Vertex(tri.coord().get(1));
      Vertex v3 = new Vertex(tri.coord().get(2));

      int index1 = lVertexTopo.indexOf(v1);
      if (index1 != -1) {
        v1 = lVertexTopo.get(index1);
      } else {
        lVertexTopo.add(v1);
      }

      int index2 = lVertexTopo.indexOf(v2);
      if (index2 != -1) {
        v2 = this.lVertexTopo.get(index2);
      } else {
        lVertexTopo.add(v2);
      }

      int index3 = lVertexTopo.indexOf(v3);
      if (index3 != -1) {
        v3 = this.lVertexTopo.get(index3);
      } else {
        lVertexTopo.add(v3);
      }

      Triangle triangle = new Triangle(v1, v2, v3);

      if (!triangle.equals((Object) tri)) {
        System.out.println("Different");
        triangle.equals((Object) tri);
      }

      if (this.lTrianglesTopo.contains(triangle)) {
        continue;
      }

      v1.ajouteTriangle(triangle);
      v2.ajouteTriangle(triangle);
      v3.ajouteTriangle(triangle);

      this.lTrianglesTopo.add(triangle);

      List<Edge> lE = new ArrayList<Edge>();
      lE.addAll(triangle.calculEdge());

      for (Edge eTemp : lE) {

        int indexE = lEdgeTopo.indexOf(eTemp);

        if (indexE != -1) {

          triangle.calculEdge().remove(eTemp);

          Edge eToAddTemp = lEdgeTopo.get(indexE);

          triangle.calculEdge().add(eToAddTemp);
          eToAddTemp.getNeighbourTriangles().add(triangle);

        } else {

          lEdgeTopo.add(eTemp);

        }

      }

    }

  }

  private List<List<Triangle>> processGroupes() {
    List<List<Triangle>> lLTri = new ArrayList<List<Triangle>>();
    // On initialise
    List<Triangle> triIni = new ArrayList<Triangle>();
    triIni.addAll(this.getlTrianglesTopo());

    // On parcourt chaque triangle
    while (true) {

      if (triIni.size() == 0) {
        break;
      }
      // On enlève le premier de la liste
      Triangle triAct = triIni.remove(0);

      // On entame une nouveau groupes
      List<Triangle> lTriTemp = new ArrayList<Triangle>();
      // On ajoute le triangle actuel au groupe
      lTriTemp.add(triAct);

      // On ajoute le nouveau groupe à la liste des groupes
      lLTri.add(lTriTemp);

      // On liste les triangles suivants à traiter
      List<Triangle> lTriToTreat = new ArrayList<Triangle>();
      // On met tous les voisines

      List<Triangle> tNeighTemp = triAct.getNeighBourTriangles();

      for (Triangle t : tNeighTemp) {
        // patch ...

        if (!triIni.remove(t)) {

          System.out.println("Error");
        } else {
          lTriToTreat.add(t);
        }

      }

      // On traite chaque voisin
      while (true) {

        if (lTriToTreat.size() == 0) {
          break;
        }

        // On recupère le premier triangle à enlever
        Triangle triToTriTreat = lTriToTreat.remove(0);

        // On regarde si il est déjà dans la liste
        int index = lTriTemp.indexOf(lTriTemp);

        // Il n'est pas dans la liste, on l'ajoute et on rajoute ses voisins
        if (index == -1) {
          triIni.remove(triToTriTreat);

          lTriTemp.add(triToTriTreat);

          List<Triangle> newLTriangle = triToTriTreat.getNeighBourTriangles();
          for (Triangle triNew : newLTriangle) {
            if (!lTriTemp.contains(triNew) && !lTriToTreat.contains(triNew)) {
              lTriToTreat.add(triNew);
            }

          }

        }

      }

    }

    return lLTri;

  }

  public List<List<Triangle>> processGroupByPlaneEquation(double epsilon) {

    List<List<Triangle>> lLTri = new ArrayList<List<Triangle>>();
    // On initialise
    List<Triangle> triIni = new ArrayList<Triangle>();
    triIni.addAll(this.getlTrianglesTopo());

    // On parcourt chaque triangle
    while (true) {

      if (triIni.size() == 0) {
        break;
      }
      // On enlève le premier de la liste
      Triangle triAct = triIni.remove(0);
      Vecteur vAct = (new PlanEquation(triAct)).getNormale();
      vAct.normalise();
      // On entame une nouveau groupes
      List<Triangle> lTriTemp = new ArrayList<Triangle>();
      // On ajoute le triangle actuel au groupe
      lTriTemp.add(triAct);

      // On ajoute le nouveau groupe à la liste des groupes
      lLTri.add(lTriTemp);

      // On liste les triangles suivants à traiter
      List<Triangle> lTriToTreat = new ArrayList<Triangle>();
      // On met tous les voisines

      List<Triangle> tNeighTemp = triAct.getNeighBourTriangles();

      for (Triangle t : tNeighTemp) {
        // patch ...

        lTriToTreat.add(t);

      }

      // On traite chaque voisin
      while (true) {

        if (lTriToTreat.size() == 0) {
          break;
        }

        // On recupère le premier triangle à enlever
        Triangle triToTriTreat = lTriToTreat.remove(0);

        // On regarde si il est déjà dans la liste
        int index = lTriTemp.indexOf(lTriTemp);

        Vecteur vTemp = (new PlanEquation(triToTriTreat)).getNormale();
        vTemp.normalise();

        // on vérifie qu'ils sont dans le même plan
        boolean samePlan = Math.abs(vTemp.prodScalaire(vAct)) > 1 - epsilon;

        // Il n'est pas dans la liste, on l'ajoute et on rajoute ses voisins
        if (index == -1 && samePlan) {
          triIni.remove(triToTriTreat);

          lTriTemp.add(triToTriTreat);

          List<Triangle> newLTriangle = triToTriTreat.getNeighBourTriangles();
          for (Triangle triNew : newLTriangle) {
            if (!lTriTemp.contains(triNew) && !lTriToTreat.contains(triNew)) {
              lTriToTreat.add(triNew);
            }

          }

        }

      }

    }

    return lLTri;

  }

  public List<Triangle> getlTrianglesTopo() {
    return lTrianglesTopo;
  }

  public List<Edge> getlEdgeTopo() {
    return lEdgeTopo;
  }

  public List<Vertex> getlVertexTopo() {
    return lVertexTopo;
  }

  public List<List<Triangle>> getGroupes() {
    if (lGroupes == null) {
      lGroupes = processGroupes();
    }
    return lGroupes;
  }

}
