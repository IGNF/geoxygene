package fr.ign.cogit.geoxygene.sig3d.util.correction;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Edge;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Vertex;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

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
 * 
 * 
 **/ 
public class Checker {

  private List<Triangle> lT = new ArrayList<Triangle>();
  private List<Vertex> lV = new ArrayList<Vertex>();

  private IFeatureCollection<IFeature> featCollDeb = new FT_FeatureCollection<IFeature>();

  public List<Triangle> getlT() {
    return lT;
  }

  public List<Vertex> getlV() {
    return lV;
  }

  public IFeatureCollection<IFeature> getFeatCollDeb() {
    return featCollDeb;
  }

  public Checker(IFeature feat) {
    if (feat.getGeom() instanceof GM_MultiSurface<?>) {

      List<ITriangle> lI = new ArrayList<ITriangle>();

      IMultiSurface<?> gMSO = (IMultiSurface<?>) feat
          .getGeom();
      int nbSurf = gMSO.size();

      for (int j = 0; j < nbSurf; j++) {

        lI.add(new GM_Triangle(gMSO.get(j).coord().get(0), gMSO.get(j).coord()
            .get(1), gMSO.get(j).coord().get(2)));

      }

      init(lI);

    } else {
      System.out.println("Mauvaise classe géométrie");
    }

  }

  public Checker(List<ITriangle> lT) {

    init(lT);

  }

  public void init(List<ITriangle> lT) {

    int nbFaces = lT.size();

    for (int i = 0; i < nbFaces; i++) {
      ITriangle tri = lT.get(i);

      Vertex v1 = new Vertex(tri.coord().get(0));
      Vertex v2 = new Vertex(tri.coord().get(1));
      Vertex v3 = new Vertex(tri.coord().get(2));

      int index1 = lV.indexOf(v1);
      if (index1 != -1) {
        v1 = this.lV.get(index1);
      } else {
        lV.add(v1);
      }

      int index2 = lV.indexOf(v2);
      if (index2 != -1) {
        v2 = this.lV.get(index2);
      } else {
        lV.add(v2);
      }

      int index3 = lV.indexOf(v3);
      if (index3 != -1) {
        v3 = this.lV.get(index3);
      } else {
        lV.add(v3);
      }

      Triangle triangle = new Triangle(v1, v2, v3);
      v1.ajouteTriangle(triangle);
      v2.ajouteTriangle(triangle);
      v3.ajouteTriangle(triangle);

      this.lT.add(triangle);

    }

  }

  /**
   * Vérifie que chaque vertex est rattaché à plus de 2 triangles
   * @return
   */
  public boolean checkConnectivity() {

    for (int i = 0; i < this.lV.size(); i++) {

      if (this.lV.get(i).getLTRiRel().size() < 3) {

        if (this.lV.get(i).getLTRiRel().size() == 2) {

          Triangle tri1 = this.lV.get(i).getLTRiRel().get(0);
          Triangle tri2 = this.lV.get(i).getLTRiRel().get(1);

          if (healConnectivity(tri1, tri2, this.lV.get(i))) {
            i--;
          } else {
            return false;
          }

        }

      }

    }

    return true;
  }

  private boolean healConnectivity(Triangle t1, Triangle t2, Vertex badVertex) {

    PlanEquation pE = t1.getPlanEquation();
    PlanEquation pE2 = t2.getPlanEquation();

    if (pE.getNormale().getNormalised()
        .prodScalaire(pE2.getNormale().getNormalised()) > 0.9) {
      // /Triangles dans le même plan on peut soigner
      List<Vertex> nouvTri = new ArrayList<Vertex>(3);

      for (int i = 0; i < 3; i++) {

        Vertex vT1 = t1.getLVertices()[i];
        Vertex vT2 = t2.getLVertices()[i];

        if (!(vT1.equals(badVertex)) && !(nouvTri.contains(vT1))) {

          nouvTri.add(vT1);

        }

        if (!(vT2.equals(badVertex)) && !(nouvTri.contains(vT2))) {

          nouvTri.add(vT2);

        }

      }

      if (nouvTri.size() != 3) {
        System.out.println("Can not repair");
        return false;
      }

      Triangle triF = new Triangle(nouvTri.get(0), nouvTri.get(1),
          nouvTri.get(2));

      if (triF.getPlanEquation().getNormale().prodScalaire(pE.getNormale()) < 0) {

        triF = new Triangle(nouvTri.get(0), nouvTri.get(2), nouvTri.get(1));

      }

      this.lV.remove(badVertex);
      this.lT.remove(t1);
      this.lT.remove(t2);
      this.lT.add(triF);

      return true;

    } else {
      System.out.println("Can not repair 2");
    }

    return false;

  }

  /**
   * Vérifie que chaque vertex est rattaché à plus de 2 triangles
   * 
   * 
   * 
   * 
   * @return
   */
  public boolean checkFullConnectivity() {

    if (!this.checkConnectivity()) {

      return false;
    }

    int nbFaces = this.lT.size();

    for (int i = 0; i < nbFaces; i++) {

      List<Edge> lE = this.lT.get(i).calculEdge();

      int nbEdge = lE.size();

      for (int k = 0; k < nbEdge; k++) {
        Edge e = lE.get(k);
        int nbE = 0;

        for (int j = 0; j < nbFaces; j++) {

          if (this.lT.get(j).contientEdge(e)
              || this.lT.get(j).contientEdge(e.inverse())) {

            nbE++;

            if (nbE > 2) {
              return false;
            }

          }

        }

        if (nbE != 2) {
          return false;
        }

      }

    }

    return true;

  }

  /**
   * Vérifie si les normales sont bien orientées
   * @return
   */
  public boolean checkNormales() {

    Vertex vIni = this.lV.get(0);

    List<Vertex> exploratedVertex = new ArrayList<Vertex>();
    List<Vertex> vertexToExplore = new ArrayList<Vertex>();
    vertexToExplore.add(vIni);

    while (!vertexToExplore.isEmpty()) {
      Vertex v = vertexToExplore.remove(0);
      exploratedVertex.add(v);

      List<Triangle> lTri = v.getLTRiRel();

      int nbTri = lTri.size();

      for (int i = 0; i < nbTri; i++) {
        Triangle triA = lTri.get(i);

        List<Edge> lE = triA.calculEdge();

        int nbEdge = lE.size();

        for (int k = 0; k < nbEdge; k++) {

          Edge e = lE.get(k);

          for (int l = 1; l < nbTri; l++) {

            if (lTri.get(l).contientEdge(e)) {

              // 2 triangles contiennent des vertex dans le même sens, il y a un
              // bug
              return false;
            }

          }

        }

        Vertex[] tabV = triA.getLVertices();

        for (int m = 0; m < 3; m++) {

          if ((!vertexToExplore.contains(tabV[m]))
              && (!exploratedVertex.contains(tabV[m]))) {

            vertexToExplore.add(tabV[m]);

          }

        }

      }

    }

    System.out.println("Explorated Vertex : " + exploratedVertex
        + " NB Vertex " + this.lV.size());

    return true;

  }

  public boolean checkNoAutoIntersection() {

    int nbFaces = lT.size();

    for (int i = 0; i < nbFaces; i++) {
      Triangle t = lT.get(i);

      for (int j = i; j < nbFaces; j++) {

        Triangle t2 = lT.get(j);

        if (t.intersects(t2)) {
          return false;
        }

      }

    }

    return true;

  }

}
