package fr.ign.cogit.geoxygene.contrib.delaunay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;

public abstract class AbstractTriangulation extends CarteTopo {
    public AbstractTriangulation(String nom_logique) {
        this.setNom(nom_logique);
        this.setPersistant(false);
        // nécessaire pour ojb
        this.ojbConcreteClass = this.getClass().getName();
        Population<ArcDelaunay> arcs = new Population<ArcDelaunay>(false, I18N
                    .getString("CarteTopo.Edge"), //$NON-NLS-1$
                    ArcDelaunay.class, true);
        this.addPopulation(arcs);
        Population<NoeudDelaunay> noeuds = new Population<NoeudDelaunay>(false,
                    I18N.getString("CarteTopo.Node"), //$NON-NLS-1$
                    NoeudDelaunay.class, true);
        this.addPopulation(noeuds);
        Population<TriangleDelaunay> faces = new Population<TriangleDelaunay>(
                    false, I18N.getString("CarteTopo.Face"), //$NON-NLS-1$
                    TriangleDelaunay.class, true);
        this.addPopulation(faces);
    }
    Population<Arc> voronoiEdges = new Population<Arc>();

    /** Population des arcs de voronoi de la triangulation. */
    public Population<Arc> getPopVoronoiEdges() {
        return this.voronoiEdges;
    }

    Population<Noeud> voronoiVertices = new Population<Noeud>();

    /** Population des noeuds de voronoi de la triangulation. */
    public Population<Noeud> getPopVoronoiVertices() {
        return this.voronoiVertices;
    }

    /**
     * Run the triangulation with default parameters:
     * @throws Exception 
     */
    public abstract void triangule() throws Exception;


    /**
     * Map used to mark nodes as belonging to the boundary or not.
     */
    private HashMap<Noeud, Boolean> boundaryNodes = new HashMap<Noeud, Boolean>();
    /**
     * Map used to mark edges as belonging to the boundary or not.
     */
    private HashMap<Arc, Boolean> boundaryEdges = new HashMap<Arc, Boolean>();

    /**
     * Regularity Algorithm.
     * 
     * @param edge An edge of the triangulation
     * @return True is the triangulation with edge removed is regular. False
     *         otherwise
     */
    private boolean regular(Arc edge) {
      // if longestArc is a boundary edge
      if (this.boundaryEdges.get(edge)) {
        Face face = (edge.getFaceDroite() == null) ? edge.getFaceGauche() : edge
            .getFaceDroite();
        if (face == null) {
          System.out.println("ERROR : not regular anymore");
          return false;
        }
        List<Noeud> nodes = face.noeuds();
        nodes.remove(edge.getNoeudIni());
        nodes.remove(edge.getNoeudFin());
        Noeud n = nodes.get(0);
        return (!this.boundaryNodes.get(n));
      }
      return false;
    }
    /**
     * Remove the longest edge in the set if it is longer than the threshold and
     * that the resulting triangulation is regular.
     * 
     * @param orderedEdgeSet an order set of edges.
     * @param alpha the threshold
     * @return true if the edge was removed, false otherwise
     */
    private boolean removeLongestEdge(TreeSet<Arc> orderedEdgeSet, double alpha) {
      // the set is ordered so the longest is the first
      Arc edge = orderedEdgeSet.iterator().next();
//      logger.info("removing edge " +edge);
      // remove it from the set
      orderedEdgeSet.remove(edge);
      double length = edge.getGeometrie().length();
      if (length <= alpha || !this.regular(edge)) {
        return false;
      }
      // get the face we have to remove
      Face face = (edge.getFaceDroite() == null) ? edge.getFaceGauche() : edge
          .getFaceDroite();
      //logger.info("associated with " + face);
      if (face != null) {
//        logger.info(face.arcs().size() + " edges");
//        for (Arc arc : face.arcs()) {
//          logger.info(arc);
//        }
        // remove the edge from the face
        face.enleveArcDirect(edge);
        face.enleveArcIndirect(edge);
        // get the revealed node
        List<Noeud> nodes = face.noeuds();
        nodes.remove(edge.getNoeudIni());
        nodes.remove(edge.getNoeudFin());
        // there should only be one node left
        Noeud revealedNode = nodes.get(0);
        // mark it as belonging to the boundary
        this.boundaryNodes.put(revealedNode, Boolean.TRUE);
        // get the revealed edges,
        // i.e. the remaing edges of the triangle
        List<Arc> revealedEdges = face.arcs();
        revealedEdges.remove(edge);
//        logger.info(revealedEdges.size() + " new revealed edges");
//        for (Arc arc : revealedEdges) {
//          logger.info(arc);
//        }
        // add them to the boundary
        orderedEdgeSet.addAll(revealedEdges);
        // mark the as belonging to the boundary
        for (Arc arc : revealedEdges) {
          this.boundaryEdges.put(arc, Boolean.TRUE);
        }
        this.boundaryEdges.remove(edge);
        // remove the triangle from the revealed edges
        List<Arc> edges = new ArrayList<Arc>(face.getArcsDirects());
        for (Arc arc : edges) {
          arc.setFaceGauche(null);
        }
        edges.clear();
        edges.addAll(face.getArcsIndirects());
        for (Arc arc : edges) {
          arc.setFaceDroite(null);
        }
        // remove the triangle from the triangulation
        this.getPopFaces().remove(face);
      } else {
        CarteTopo.logger.error("NULL FACE");
      }
      // remove the edge from the triangulation
      edge.setNoeudIni(null);
      edge.setNoeudFin(null);
      this.getPopArcs().remove(edge);
      return true;
    }

    /**
     * Computes the characteristic shape of the triangulation. Warning. This does
     * actually modify the triangulation by removing triangles and edges.
     * <p>
     * This algorithm implements the method described in "Efficient generation of
     * simple polygons for characterizing the shape of a set of points in the
     * plane", Matt Duckham, Lars Kulik, Mike Worboys, Antony Galton, 2008.
     * 
     * @param alpha the length threshold
     * @return the characteristic shape of the triangulation
     */
  public GM_Polygon getCharacteristicShape(double alpha) {
      // initilize the nodes as not belonging to the boundary
      logger.info(this.getPopArcs().size() + " edges");
      for (Noeud n : this.getPopNoeuds()) {
        this.boundaryNodes.put(n, Boolean.FALSE);
      }
      // create a comparator to sort the boundary edges
      Comparator<Arc> comparator = new Comparator<Arc>() {
        @Override
      public int compare(Arc o1, Arc o2) {
          if (o1 == null || o2 == null) {
            return 0;
          }
          double l1 = o1.getGeometrie().length();
          double l2 = o2.getGeometrie().length();
          return Double.compare(l2, l1);
        }

        @Override
        public boolean equals(Object obj) {
          return this.equals(obj);
        }
      };
      TreeSet<Arc> orderedEdgeSet = new TreeSet<Arc>(comparator);
      // initialize the boundary edges and adding them to the set
      for (Arc edge : this.getPopArcs()) {
        // if the edge has no face either on the right or the left, it
        // belongs to the boundary
        if (edge.getFaceDroite() == null || edge.getFaceGauche() == null) {
          orderedEdgeSet.add(edge);
          this.boundaryEdges.put(edge, Boolean.TRUE);
          this.boundaryNodes.put(edge.getNoeudIni(), Boolean.TRUE);
          this.boundaryNodes.put(edge.getNoeudFin(), Boolean.TRUE);
        } else {
          this.boundaryEdges.put(edge, Boolean.FALSE);
        }
        if (edge.getFaceDroite() == null && edge.getFaceGauche() == null) {
            CarteTopo.logger.error("Edge without face " + edge);
        }
      }
      if (CarteTopo.logger.isDebugEnabled()) {
          CarteTopo.logger.debug("Running the alpha shape");
//          for (Arc edge : orderedEdgeSet) {
//              logger.info("length = " + edge.getGeometrie().length());
//          }
      }
      // while the list of boundary edges is not empty, try to remove the
      // longest edge
      while (!orderedEdgeSet.isEmpty()) {
          /*
          List<GM_LineString> list = new ArrayList<GM_LineString>(orderedEdgeSet.size());
          for (Arc a : orderedEdgeSet) {list.add(a.getGeometrie());}
        logger.info("boundary = " + Operateurs.union(list));
        */
//        GM_MultiCurve<GM_OrientableCurve> boundary = new GM_MultiCurve<GM_OrientableCurve>();
//        for (Arc a : orderedEdgeSet) {
//          boundary.add(a.getGeometrie());
//        }
//        logger.info("boundary = " + boundary);
        this.removeLongestEdge(orderedEdgeSet, alpha);
      }
      if (CarteTopo.logger.isDebugEnabled()) {
          CarteTopo.logger.debug("Alpha shape finished");
      }
      List<GM_LineString> list = new ArrayList<GM_LineString>(0);
      // built the list of all boundary edges at the end of the process
      for (Arc arc : this.getPopArcs()) {
        if (arc.getFaceDroite() == null || arc.getFaceGauche() == null) {
          list.add(arc.getGeometrie());
        }
      }
      // cleaning up
      this.boundaryEdges.clear();
      this.boundaryNodes.clear();
      // return the simple polygon formed by the union of boundary edges
//      for (GM_LineString l : list) {
//          logger.info(" l = " + l);
//      }
      GM_LineString union = Operateurs.union(list);
      logger.info("union " + union);
      return new GM_Polygon(union);
    }
}
