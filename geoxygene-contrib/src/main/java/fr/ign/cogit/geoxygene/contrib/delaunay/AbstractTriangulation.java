package fr.ign.cogit.geoxygene.contrib.delaunay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public abstract class AbstractTriangulation extends CarteTopo {
  public AbstractTriangulation(String nom_logique) {
    this.setNom(nom_logique);
    this.setPersistant(false);
    // nécessaire pour ojb
    this.ojbConcreteClass = this.getClass().getName();
    Population<ArcDelaunay> arcs = new Population<ArcDelaunay>(false, "Edge", ArcDelaunay.class, true);
    this.addPopulation(arcs);
    Population<NoeudDelaunay> noeuds = new Population<NoeudDelaunay>(false, "Node", NoeudDelaunay.class, true);
    this.addPopulation(noeuds);
    Population<TriangleDelaunay> faces = new Population<TriangleDelaunay>(false, "Face", TriangleDelaunay.class, true);
    this.addPopulation(faces);
    this.voronoiDiagram = new CarteTopo(this.getNom() + "_voronoiDiagram");
  }

  protected CarteTopo voronoiDiagram = null;
  private String options = null;

  /**
   * Set the triangulation options
   * 
   * @param options triangulation options.
   * @see #triangule(String)
   */
  public void setOptions(String options) {
    this.options = options;
  }

  /**
   * @return the options
   */
  public String getOptions() {
    return this.options;
  }

  /** Population des arcs de voronoi de la triangulation. */
  public IPopulation<Arc> getPopVoronoiEdges() {
    return this.voronoiDiagram.getPopArcs();
  }

  /** Population des noeuds de voronoi de la triangulation. */
  public IPopulation<Noeud> getPopVoronoiVertices() {
    return this.voronoiDiagram.getPopNoeuds();
  }

  /** Population des cellules de voronoi de la triangulation. */
  public IPopulation<Face> getPopVoronoiFaces() {
    return this.voronoiDiagram.getPopFaces();
  }

  /** Le diagramme de Voronoi sous la forme d'une carte Topo */
  public CarteTopo getVoronoiDiagram() {
    return this.voronoiDiagram;
  }

  /**
   * Run the triangulation with default parameters.
   * @throws Exception
   */
  public void triangule() throws Exception {
    this.triangule("czeBQ"); //$NON-NLS-1$
  }

  /**
   * Run the triangulation with the given parameters. Lance la triangulation
   * avec les paramètres donnés.
   * 
   * @param trianguleOptions paramètres de la triangulation :
   *          <ul>
   *          <li><b>z Zero:</b> points are numbered from zero
   *          <li><b>e Edges:</b> export edges
   *          <li><b>c Convex Hull:</b> Creates segments on the convex hull of
   *          the triangulation. If you are triangulating a vertex set, this
   *          switch causes the creation of all edges in the convex hull. If you
   *          are triangulating a PSLG, this switch specifies that the whole
   *          convex hull of the PSLG should be triangulated, regardless of what
   *          segments the PSLG has. If you do not use this switch when
   *          triangulating a PSLG, it is assumed that you have identified the
   *          region to be triangulated by surrounding it with segments of the
   *          input PSLG. Beware: if you are not careful, this switch can cause
   *          the introduction of an extremely thin angle between a PSLG segment
   *          and a convex hull segment, which can cause overrefinement (and
   *          possibly failure if Triangle runs out of precision). If you are
   *          refining a mesh, the -c switch works differently; it generates the
   *          set of boundary edges of the mesh.
   *          <li><b>B Boundary:</b> No boundary markers in the output.
   *          <li><b>Q Quiet:</b> Suppresses all explanation of what Triangle is
   *          doing, unless an error occurs.
   *          <li>v for exporting a Voronoi diagram. This implementation does
   *          not use exact arithmetic to compute the Voronoi vertices, and does
   *          not check whether neighboring vertices are identical. Be
   *          forewarned that if the Delaunay triangulation is degenerate or
   *          near-degenerate, the Voronoi diagram may have duplicate vertices,
   *          crossing edges, or infinite rays whose direction vector is zero.
   *          The result is a valid Voronoi diagram only if Triangle's output is
   *          a true Delaunay triangulation. The Voronoi output is usually
   *          meaningless (and may contain crossing edges and other pathology)
   *          if the output is a CDT or CCDT, or if it has holes or concavities.
   *          If the triangulation is convex and has no holes, this can be fixed
   *          by using the -L switch to ensure a conforming Delaunay
   *          triangulation is constructed.
   *          <li>p for reading a Planar Straight Line Graph
   *          <li>r for refining a previously generated mesh
   *          <li>q for Quality mesh generation by my variant of Jim Ruppert's
   *          Delaunay refinement algorithm. Adds vertices to the mesh to ensure
   *          that no angles smaller than 20 degrees occur. An alternative
   *          minimum angle may be specified after the `q'. If the minimum angle
   *          is 20.7 degrees or smaller, the triangulation algorithm is
   *          mathematically guaranteed to terminate (assuming infinite
   *          precision arithmetic-- Triangle may fail to terminate if you run
   *          out of precision). In practice, the algorithm often succeeds for
   *          minimum angles up to 33.8 degrees. For some meshes, however, it
   *          may be necessary to reduce the minimum angle to avoid problems
   *          associated with insufficient floating-point precision. The
   *          specified angle may include a decimal point.
   *          <li>V Verbose: Gives detailed information about what Triangle is
   *          doing. Add more `V's for increasing amount of detail. `-V' gives
   *          information on algorithmic progress and more detailed statistics.
   *          `-VV' gives vertex-by-vertex details, and prints so much that
   *          Triangle runs much more slowly. `-VVVV' gives information only a
   *          debugger could love.
   *          </ul>
   * @throws Exception
   */
  public void triangule(String trianguleOptions) throws Exception {
    if (this.getPopNoeuds().size() < 3) {
      CarteTopo.logger.error(I18N.getString("Triangulation.Cancelled") //$NON-NLS-1$
          + this.getPopNoeuds().size()
          + I18N.getString("Triangulation.NeedsAtLeast3Points")); //$NON-NLS-1$
      return;
    }
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug(I18N.getString("Triangulation.StartedWithOptions") //$NON-NLS-1$
          + trianguleOptions);
    }
    this.setOptions(trianguleOptions);
    this.create();
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug(I18N.getString("Triangulation.Finished")); //$NON-NLS-1$
    }
  }

  abstract void create() throws Exception;

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
    // logger.info("removing edge " +edge);
    // remove it from the set
    orderedEdgeSet.remove(edge);
    double length = edge.getGeometrie().length();
    if (length <= alpha || !this.regular(edge)) {
      return false;
    }
    // get the face we have to remove
    Face face = (edge.getFaceDroite() == null) ? edge.getFaceGauche() : edge
        .getFaceDroite();
    // logger.info("associated with " + face);
    if (face != null) {
      // logger.info(face.arcs().size() + " edges");
      // for (Arc arc : face.arcs()) {
      // logger.info(arc);
      // }
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
      // logger.info(revealedEdges.size() + " new revealed edges");
      // for (Arc arc : revealedEdges) {
      // logger.info(arc);
      // }
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
    CarteTopo.logger.info(this.getPopArcs().size() + " edges");
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
      // for (Arc edge : orderedEdgeSet) {
      // logger.info("length = " + edge.getGeometrie().length());
      // }
    }
    // while the list of boundary edges is not empty, try to remove the
    // longest edge
    while (!orderedEdgeSet.isEmpty()) {
      /*
       * List<GM_LineString> list = new
       * ArrayList<GM_LineString>(orderedEdgeSet.size()); for (Arc a :
       * orderedEdgeSet) {list.add(a.getGeometrie());} logger.info("boundary = "
       * + Operateurs.union(list));
       */
      // GM_MultiCurve<GM_OrientableCurve> boundary = new
      // GM_MultiCurve<GM_OrientableCurve>();
      // for (Arc a : orderedEdgeSet) {
      // boundary.add(a.getGeometrie());
      // }
      // logger.info("boundary = " + boundary);
      this.removeLongestEdge(orderedEdgeSet, alpha);
    }
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug("Alpha shape finished");
    }
    List<ILineString> list = new ArrayList<ILineString>(0);
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
    // for (GM_LineString l : list) {
    // logger.info(" l = " + l);
    // }
    ILineString union = Operateurs.union(list);
    CarteTopo.logger.info("union " + union);
    return new GM_Polygon(union);
  }
}
