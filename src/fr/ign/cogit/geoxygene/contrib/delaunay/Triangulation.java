/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.delaunay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Classe mère de la triangulation construite sur la bibliothèque Triangle de
 * Jonathan Richard Shewchuk. Triangulation class used on top of Jonathan
 * Richard Shewchuk's Triangle library.
 * 
 * @author Bonin
 * @author Julien Perret
 * @version 1.1
 */

public class Triangulation extends CarteTopo {
  /**
   * Logger.
   */
  static Logger logger = Logger.getLogger(Triangulation.class.getName());

  /**
   * Constructor.
   */
  public Triangulation() {
    // nécessaire pour ojb
    this.ojbConcreteClass = this.getClass().getName();
    this.setPersistant(false);
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

  /**
   * @param nom_logique
   */
  public Triangulation(String nom_logique) {
    // nécessaire pour ojb
    this.ojbConcreteClass = this.getClass().getName();
    this.setNom(nom_logique);
    this.setPersistant(false);
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

  private Triangulateio jin = new Triangulateio();
  private Triangulateio jout = new Triangulateio();
  private Triangulateio jvorout = new Triangulateio();
  private String options = null;

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
   * Convert the node collection into an array
   */
  private void convertJin() {
    NoeudDelaunay node;
    DirectPosition coord;
    List<Noeud> noeuds = new ArrayList<Noeud>(this.getListeNoeuds());
    this.jin.numberofpoints = noeuds.size();
    this.jin.pointlist = new double[2 * this.jin.numberofpoints];
    for (int i = 0; i < noeuds.size(); i++) {
      node = (NoeudDelaunay) noeuds.get(i);
      coord = node.getGeometrie().getPosition();
      this.jin.pointlist[2 * i] = coord.getX();
      this.jin.pointlist[2 * i + 1] = coord.getY();
    }
  }

  /**
   * Convert the edges into an array.
   */
  private void convertJinSegments() {
    ArrayList<FT_Feature> noeuds = new ArrayList<FT_Feature>(this
        .getListeNoeuds());
    ArrayList<FT_Feature> aretes = new ArrayList<FT_Feature>(this
        .getListeArcs());
    this.jin.numberofsegments = aretes.size();
    this.jin.segmentlist = new int[2 * this.jin.numberofsegments];
    for (int i = 0; i < this.jin.numberofsegments; i++) {
      this.jin.segmentlist[2 * i] = noeuds
          .indexOf(((ArcDelaunay) aretes.get(i)).getNoeudIni());
      this.jin.segmentlist[2 * i + 1] = noeuds.indexOf(((ArcDelaunay) aretes
          .get(i)).getNoeudFin());
    }
  }

  /**
   * Convert back the result into vertices, edges and triangles.
   */
  private void convertJout() {
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug(I18N.getString("Triangulation.ExportStart")); //$NON-NLS-1$
    }
    try {
      if (Triangulation.logger.isDebugEnabled()) {
        Triangulation.logger.debug(I18N
            .getString("Triangulation.NodeExportStart")); //$NON-NLS-1$
      }
      for (int i = this.jin.numberofpoints; i < this.jout.numberofpoints; i++) {
        this.getPopNoeuds().nouvelElement().setCoord(
            new DirectPosition(this.jout.pointlist[2 * i],
                this.jout.pointlist[2 * i + 1]));
      }
      ArrayList<FT_Feature> noeuds = new ArrayList<FT_Feature>(this
          .getListeNoeuds());
      Class<?>[] signaturea = { this.getPopNoeuds().getClasse(),
          this.getPopNoeuds().getClasse() };
      Object[] parama = new Object[2];
      if (Triangulation.logger.isDebugEnabled()) {
        Triangulation.logger.debug(I18N
            .getString("Triangulation.EdgeExportStart")); //$NON-NLS-1$
      }
      for (int i = 0; i < this.jout.numberofedges; i++) {
        parama[0] = noeuds.get(this.jout.edgelist[2 * i]);
        parama[1] = noeuds.get(this.jout.edgelist[2 * i + 1]);
        this.getPopArcs().nouvelElement(signaturea, parama);
      }
      Class<?>[] signaturef = { this.getPopNoeuds().getClasse(),
          this.getPopNoeuds().getClasse(), this.getPopNoeuds().getClasse() };
      Object[] paramf = new Object[3];
      if (Triangulation.logger.isDebugEnabled()) {
        Triangulation.logger.debug(I18N
            .getString("Triangulation.TriangleExportStart")); //$NON-NLS-1$
      }
      for (int i = 0; i < this.jout.numberoftriangles; i++) {
        paramf[0] = noeuds.get(this.jout.trianglelist[3 * i]);
        paramf[1] = noeuds.get(this.jout.trianglelist[3 * i + 1]);
        paramf[2] = noeuds.get(this.jout.trianglelist[3 * i + 2]);
        this.getPopFaces().nouvelElement(signaturef, paramf).setId(i);
      }
      if (this.getOptions().indexOf('v') != -1) {
        if (Triangulation.logger.isDebugEnabled()) {
          Triangulation.logger.debug(I18N
              .getString("Triangulation.VoronoiDiagramExportStart")); //$NON-NLS-1$
        }
        GM_Envelope envelope = this.getPopNoeuds().envelope();
        envelope.expandBy(100);
        this.voronoiVertices.initSpatialIndex(Tiling.class, true, envelope, 10);
        // l'export du diagramme de voronoi
        for (int i = 0; i < this.jvorout.numberofpoints; i++) {
          this.voronoiVertices.add(new Noeud(
              new GM_Point(new DirectPosition(this.jvorout.pointlist[2 * i],
                  this.jvorout.pointlist[2 * i + 1]))));
        }
        for (int i = 0; i < this.jvorout.numberofedges; i++) {
          int indexIni = this.jvorout.edgelist[2 * i];
          int indexFin = this.jvorout.edgelist[2 * i + 1];
          if (indexFin == -1) {
            // infinite edge
            double vx = this.jvorout.normlist[2 * i];
            double vy = this.jvorout.normlist[2 * i + 1];
            Noeud c1 = this.voronoiVertices.getElements().get(indexIni);
            Noeud c2 = new Noeud();
            double vectorSize = 10000000;
            c2.setGeometrie(new GM_Point(new DirectPosition(c1.getGeometrie()
                .getPosition().getX()
                + vectorSize * vx, c1.getGeometrie().getPosition().getY()
                + vectorSize * vy)));
            GM_LineString line = new GM_LineString(new DirectPositionList(
                Arrays.asList(c1.getGeometrie().getPosition(), c2
                    .getGeometrie().getPosition())));
            GM_Object intersection = line.intersection(envelope.getGeom());
            DirectPositionList list = intersection.coord();
            if (list.size() > 1) {
              c2.setGeometrie(list.get(1).toGM_Point());
            }
            indexFin = this.voronoiVertices.size();
            this.voronoiVertices.add(c2);
          }
          this.voronoiEdges.add(new Arc(this.voronoiVertices.getElements().get(
              indexIni), this.voronoiVertices.getElements().get(indexFin)));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug(I18N.getString("Triangulation.ExportEnd")); //$NON-NLS-1$
    }
  }

  /**
   * Méthode de triangulation proprment dite en C - va chercher la bibliothèque
   * C (dll/so).
   * 
   * @param trianguleOptions
   * @param trianguleJin
   * @param trianguleJout
   * @param trianguleJvorout
   */
  private native void trianguleC(String trianguleOptions,
      Triangulateio trianguleJin, Triangulateio trianguleJout,
      Triangulateio trianguleJvorout);

  static {
    System.loadLibrary("trianguledll");} //$NON-NLS-1$

  /**
   * Run the triangulation with the given parameters. Lance la triangulation
   * avec les paramètres donnés
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
      Triangulation.logger.error(I18N.getString("Triangulation.Cancelled") //$NON-NLS-1$
          + this.getPopNoeuds().size()
          + I18N.getString("Triangulation.NeedsAtLeast3Points")); //$NON-NLS-1$
      return;
    }
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug(I18N
          .getString("Triangulation.StartedWithOptions") //$NON-NLS-1$
          + trianguleOptions);
    }
    this.setOptions(trianguleOptions);
    this.convertJin();
    if (trianguleOptions.indexOf('p') != -1) {
      this.convertJinSegments();
      this.getPopArcs().setElements(new ArrayList<Arc>());
    }
    if (this.getOptions().indexOf('v') != -1) {
      this.trianguleC(trianguleOptions, this.jin, this.jout, this.jvorout);
    } else {
      this.trianguleC(trianguleOptions, this.jin, this.jout, null);
    }
    this.convertJout();
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug(I18N.getString("Triangulation.Finished")); //$NON-NLS-1$
    }
  }

  /**
   * Run the triangulation with default parameters:
   * <ul>
   * <li><b>z Zero:</b> points are numbered from zero
   * <li><b>e Edges:</b> export edges
   * <li><b>c Convex Hull:</b> Creates segments on the convex hull of the
   * triangulation. If you are triangulating a vertex set, this switch causes
   * the creation of all edges in the convex hull. If you are triangulating a
   * PSLG, this switch specifies that the whole convex hull of the PSLG should
   * be triangulated, regardless of what segments the PSLG has. If you do not
   * use this switch when triangulating a PSLG, it is assumed that you have
   * identified the region to be triangulated by surrounding it with segments of
   * the input PSLG. Beware: if you are not careful, this switch can cause the
   * introduction of an extremely thin angle between a PSLG segment and a convex
   * hull segment, which can cause overrefinement (and possibly failure if
   * Triangle runs out of precision). If you are refining a mesh, the -c switch
   * works differently; it generates the set of boundary edges of the mesh.
   * <li><b>B Boundary:</b> No boundary markers in the output.
   * <li><b>Q Quiet:</b> Suppresses all explanation of what Triangle is doing,
   * unless an error occurs.
   * </ul>
   * 
   * @throws Exception
   */
  public void triangule() throws Exception {
    this.triangule("czeBQ"); //$NON-NLS-1$
  }

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
    // remove it from the set
    orderedEdgeSet.remove(edge);
    double length = edge.getGeometrie().length();
    if (length > alpha && this.regular(edge)) {
      // get the face we have to remove
      Face face = (edge.getFaceDroite() == null) ? edge.getFaceGauche() : edge
          .getFaceDroite();
      if (face != null) {
        // remove the edge from the face
        face.enleveArcDirect(edge);
        face.enleveArcIndirect(edge);
        // get the revealed node
        List<Noeud> nodes = face.noeuds();
        nodes.remove(edge.getNoeudIni());
        nodes.remove(edge.getNoeudFin());
        Noeud revealedNode = nodes.get(0);
        // mark it as belonging to the boundary
        this.boundaryNodes.put(revealedNode, Boolean.TRUE);
        // get the revealed edges,
        // i.e. the remaing edges of the triangle
        List<Arc> revealedEdges = face.arcs();
        // add them to the boundary
        orderedEdgeSet.addAll(revealedEdges);
        // mark the as belonging to the boundary
        for (Arc arc : revealedEdges) {
          this.boundaryEdges.put(arc, Boolean.TRUE);
        }
        // remove the triangle from the revealed edges
        for (Arc arc : face.getArcsDirects()) {
          arc.setFaceGauche(null);
        }
        for (Arc arc : face.getArcsIndirects()) {
          arc.setFaceDroite(null);
        }
        // remove the triangle from the triangulation
        this.getPopFaces().remove(face);
      } else {
        Triangulation.logger.error("NULL FACE");
      }
      // remove the edge from the triangulation
      edge.setNoeudIni(null);
      edge.setNoeudFin(null);
      this.getPopArcs().remove(edge);
      return true;
    }
    return false;
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
    for (Noeud n : this.getPopNoeuds()) {
      this.boundaryNodes.put(n, Boolean.FALSE);
    }
    // create a comparator to sort the boundary edges
    Comparator<Arc> comparator = new Comparator<Arc>() {
      public int compare(Arc o1, Arc o2) {
        if (o1 == null || o2 == null) {
          return 0;
        }
        double l1 = o1.getGeometrie().length();
        double l2 = o2.getGeometrie().length();
        return l1 > l2 ? 1 : l1 < l2 ? -1 : 0;
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
        Triangulation.logger.error("Edge without face " + edge);
      }
    }
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Running the alpha shape");
    }
    // while the list of boundary edges is not empty, try to remove the
    // longest edge
    while (!orderedEdgeSet.isEmpty()) {
      this.removeLongestEdge(orderedEdgeSet, alpha);
    }
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Alpha shape finished");
    }
    List<GM_LineString> list = new ArrayList<GM_LineString>();
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
    return new GM_Polygon(Operateurs.union(list));
  }

  /**
   * Computes the characteristic shape of the triangulation created using the
   * points of the input feature collection.
   * <p>
   * This algorithm implements the method described in: "Efficient generation of
   * simple polygons for characterizing the shape of a set of points in the
   * plane", Matt Duckham, Lars Kulik, Mike Worboys, Antony Galton, 2008.
   * 
   * @param featureCollection a feature collection
   * @param alpha the length threshold for the characteristic shape algorithm
   * @return the characteristic shape of the input feature collection
   */
  public static GM_Polygon getCharacteristicShape(
      Collection<? extends FT_Feature> featureCollection, double alpha) {
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Creating the triangulation");
    }
    Triangulation t = new Triangulation("Triangulation");
    t.importAsNodes(featureCollection);
    try {
      t.triangule();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Creation of the triangulation finished");
    }
    GM_Polygon shape = t.getCharacteristicShape(alpha);
    // cleaning up
    t.nettoyer();
    return shape;
  }

  /**
   * Computes the characteristic shape of the triangulation created using the
   * points of the input feature collection.
   * <p>
   * This algorithm implements the method described in: "Efficient generation of
   * simple polygons for characterizing the shape of a set of points in the
   * plane", Matt Duckham, Lars Kulik, Mike Worboys, Antony Galton, 2008.
   * 
   * @param featureCollection a feature collection
   * @param alpha the length threshold for the characteristic shape algorithm
   * @return the characteristic shape of the input feature collection
   */
  public static GM_Polygon getCharacteristicShape(
      FT_FeatureCollection<? extends FT_Feature> featureCollection, double alpha) {
    return Triangulation.getCharacteristicShape(
        featureCollection.getElements(), alpha);
  }
}
