/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.common.triangulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.cartagen.graph.IEdge;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.triangulation.ITriangulation;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegmentFactory;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangle;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangleFactory;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Class for triangulation computation
 * 
 * @author Bonin, Gaffuri
 */

public class Triangulation extends Graph implements ITriangulation {
  private static Logger logger = Logger
      .getLogger(Triangulation.class.getName());

  /**
   * @return The points of the triangulation
   */
  @Override
  public List<TriangulationPoint> getPoints() {
    return this.points;
  }

  /**
	 */
  private List<TriangulationPoint> points;

  /**
   * @return The segments of the triangulation
   */
  @Override
  public Collection<TriangulationSegment> getSegments() {
    return this.segments;
  }

  /**
	 */
  private Collection<TriangulationSegment> segments;

  /**
   * @return The triangles of the triangulation
   */
  @Override
  public Collection<TriangulationTriangle> getTriangles() {
    return this.triangles;
  }

  /**
	 */
  private Collection<TriangulationTriangle> triangles;

  /**
   * The segment factory used to create the segments
   */
  private TriangulationSegmentFactory segFac;

  /**
   * The triangle factory used to create the triangles
   */
  private TriangulationTriangleFactory triFac;

  /**
   * The input triangulation structure of the C function
   */
  private Triangulateio jin;

  /**
   * The output triangulation structure of the C function
   */
  private Triangulateio jout;

  /**
   * The output voronoï structure of the C function
   */
  private Triangulateio jvorout;

  /**
   * The output voronoi structure of the C function
   */
  // private static Triangulateio jvorout = new Triangulateio();

  /**
   * The triangulation points array
   */
  private static TriangulationPoint[] pts = null;

  /**
   * The shewchuk triangulation options. Default options: pczeBQ p: constrained
   * triangulation c: Encloses the convex hull with segments z: index from 0 to
   * n-1 and not from 0 to n e: give the segments v: computes the voronoï
   * diagram Q, V,VV,VVV: comments (Q for quit, V for verbose)
   */
  private String options = "pczeBQ";

  /**
   * Constructor of a triangulation from points
   * 
   * @param points
   * @param segFac
   * @param triFac
   */
  public Triangulation(List<TriangulationPoint> points,
      TriangulationSegmentFactory segFac, TriangulationTriangleFactory triFac) {
    this(points, new ArrayList<TriangulationSegment>(), segFac, triFac);
  }

  /**
   * Constructor of a triangulation from points and segments between some of the
   * points to be constrained
   * 
   * @param points
   * @param segments
   * @param segFac
   * @param triFac
   */
  public Triangulation(List<TriangulationPoint> points,
      Collection<TriangulationSegment> segments,
      TriangulationSegmentFactory segFac, TriangulationTriangleFactory triFac) {
    super("triangulationGraph", false);
    this.points = points;
    this.segments = segments;
    this.triangles = new ArrayList<TriangulationTriangle>();
    this.segFac = segFac;
    this.triFac = triFac;
    Set<INode> nodes = new HashSet<INode>();
    for (INode node : points) {
      node.setGraph(this);
      nodes.add(node);
    }
    super.setNodes(nodes);
    Set<IEdge> edges = new HashSet<IEdge>();
    for (IEdge edge : edges) {
      edge.setGraph(this);
      edges.add(edge);
    }
    super.setEdges(edges);
  }

  /**
   * Compute the triangulation
   */
  public void compute() {
    this.compute(false);
  }

  /**
   * Computes the triangulation
   * @param triPoints the points of the triangulation
   */
  public void compute(List<TriangulationPoint> triPoints) {
    this.points = triPoints;
    this.compute();
  }

  /**
   * Computes the triangulation
   * @param triPoints the points of the triangulation
   */
  public void compute(List<TriangulationPoint> triPoints,
      Collection<TriangulationSegment> triSegs) {
    this.points = triPoints;
    this.segments = triSegs;
    this.compute();
  }

  /**
   * Compute the triangulation
   * 
   * @param createTriangles True is the triangles are needed
   */
  public void compute(boolean createTriangles) {
    this.compute(createTriangles, null);
  }

  /**
   * Compute the triangulation
   * 
   * @param createTriangles True is the triangles are needed
   * @param geom An input geometry to consider if we want the triangulation to
   *          belong to it. Null in other case.
   */
  public void compute(boolean createTriangles, IGeometry geom) {
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Triangulation computation");
    }

    // clear the triangles list
    this.triangles.clear();

    // trivial cases
    if (this.points.size() < 2) {
      return;
    }
    if (this.points.size() == 2) {
      // build the single segment
      this.segments.add(this.segFac.create(this.points.get(0),
          this.points.get(1)));
      return;
    }

    // input data conversion
    this.convertJin();

    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Call to the C triangulation fonction");
    }
    if (this.getOptions().indexOf('v') != -1) {
      Triangulation.trianguleC(this.getOptions(), this.jin, this.jout,
          this.jvorout);
    } else {
      Triangulation.trianguleC(this.options, this.jin, this.jout, null);
    }
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Triangulation finished ("
          + this.jout.numberofedges + " segments, "
          + this.jout.numberoftriangles + " triangles)");
    }

    // cleaning
    this.jin = null;

    // output data conversion
    this.convertJout(createTriangles, geom);

    // cleaning
    this.jout = null;

    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("End of triangulation computation");
    }
  }

  /**
   * Compute the triangulation
   * 
   * @param createTriangles True is the triangles are needed
   * @param geom An input geometry to consider if we want the triangulation to
   *          belong to it. Null in other case.
   */
  public void compute(boolean createTriangles, IGeometry geom, String options) {
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Triangulation computation");
    }
    this.options = options;
    // clear the triangles list
    this.triangles.clear();

    // trivial cases
    if (this.points.size() < 2) {
      return;
    }
    if (this.points.size() == 2) {
      // build the single segment
      this.segments.add(this.segFac.create(this.points.get(0),
          this.points.get(1)));
      return;
    }

    // input data conversion
    this.convertJin();

    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Call to the C triangulation fonction");
    }
    if (this.getOptions().indexOf('v') != -1) {
      Triangulation.trianguleC(this.getOptions(), this.jin, this.jout,
          this.jvorout);
    } else {
      Triangulation.trianguleC(options, this.jin, this.jout, null);
    }
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Triangulation finished ("
          + this.jout.numberofedges + " segments, "
          + this.jout.numberoftriangles + " triangles)");
    }

    // cleaning
    this.jin = null;

    // output data conversion
    this.convertJout(createTriangles, geom);

    // cleaning
    this.jout = null;

    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("End of triangulation computation");
    }
  }

  /**
   * Call to the C function
   * 
   * @param options les options
   * @param jin
   * @param jout
   * @param jvorout voronoi
   */
  private static native void trianguleC(String options, Triangulateio jin_,
      Triangulateio jout_, Triangulateio jvorout);

  static {
    System.loadLibrary("triangulation");
  }

  /**
   * Input data conversion
   */
  private void convertJin() {
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Input data triangulation conversion");
    }

    // build the input and output structures
    this.jin = new Triangulateio();
    this.jout = new Triangulateio();
    this.jvorout = new Triangulateio();

    // fill the points list
    this.jin.numberofpoints = this.points.size();
    this.jin.pointlist = new double[2 * this.jin.numberofpoints];
    Triangulation.pts = new TriangulationPoint[this.jin.numberofpoints];

    int i = 0;
    for (TriangulationPoint point : this.points) {
      this.jin.pointlist[2 * i] = point.getPosition().getX();
      this.jin.pointlist[2 * i + 1] = point.getPosition().getY();
      Triangulation.pts[i] = point;
      point.setIndex(i);
      i++;
    }

    // if there are no input segments to constraint the triangulation, return
    if (this.segments.size() == 0) {
      return;
    }

    // initialise the segments list
    this.jin.numberofsegments = this.segments.size();
    this.jin.segmentlist = new int[2 * this.jin.numberofsegments];
    i = 0;
    for (TriangulationSegment s : this.segments) {
      this.jin.segmentlist[2 * i] = s.getPoint1().getIndex();
      this.jin.segmentlist[2 * i + 1] = s.getPoint2().getIndex();
      i++;
    }

  }

  /**
   * Output data conversion
   * 
   * @param createTriangles True is the triangles are needed
   * @param geom An input geometry to consider if we want the triangulation to
   *          belong to it. Null in other case.
   */
  private void convertJout(boolean createTriangles, IGeometry geom) {
    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Output data triangulation conversion - geom="
          + geom);
    }

    int i;
    TriangulationPoint p1 = null, p2 = null, p3 = null;

    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Construction of " + this.jout.numberofedges
          + " segments");
    }
    for (i = 0; i < this.jout.numberofedges; i++) {
      // create segment between points number jout.edgelist[2*i] and point
      // number jout.edgelist[2*i+1]
      if (Triangulation.logger.isTraceEnabled()) {
        Triangulation.logger.trace("Construction of segment " + i);
      }
      try {
        // get the points
        p1 = Triangulation.pts[this.jout.edgelist[2 * i]];
        p2 = Triangulation.pts[this.jout.edgelist[2 * i + 1]];

        // if the segment already exists, continue
        if (p1.isLinkedBySegment(p2)) {
          continue;
        }

        // there is a geometry to consider and the segment center point does not
        // belong to it: continue
        if (geom != null
            && !geom.contains(new DirectPosition((p1.getPosition().getX() + p2
                .getPosition().getX()) / 2, (p1.getPosition().getY() + p2
                .getPosition().getY()) / 2).toGM_Point())) {
          continue;
        }

        // create the segment
        TriangulationSegment seg = this.segFac.create(p1, p2);
        this.segments.add(seg);

      } catch (Exception e) {
        Triangulation.logger
            .error("ERROR: impossible to create triangulation segment");
        e.printStackTrace();
        continue;
      }
    }

    // return if no triangle construction is demanded
    if (!createTriangles) {
      return;
    }

    if (Triangulation.logger.isDebugEnabled()) {
      Triangulation.logger.debug("Construction of "
          + this.jout.numberoftriangles + " triangles");
    }
    for (i = 0; i < this.jout.numberoftriangles; i++) {
      // create triangle between points number jout.trianglelist[3*i],
      // jout.trianglelist[3*i+1] and jout.trianglelist[3*i+2]
      if (Triangulation.logger.isTraceEnabled()) {
        Triangulation.logger.trace("Construction of triangle " + i);
      }
      try {
        // get the points
        p1 = Triangulation.pts[this.jout.trianglelist[3 * i]];
        p2 = Triangulation.pts[this.jout.trianglelist[3 * i + 1]];
        p3 = Triangulation.pts[this.jout.trianglelist[3 * i + 2]];

        // there is a geometry to consider and the triangle center point does
        // not belong to it: continue
        if (geom != null
            && !geom.contains(new DirectPosition((p1.getPosition().getX()
                + p2.getPosition().getX() + p3.getPosition().getX()) / 3, (p1
                .getPosition().getY() + p2.getPosition().getY() + p3
                .getPosition().getY()) / 3).toGM_Point())) {
          continue;
        }

        // create the triangle
        TriangulationTriangle tri = this.triFac.create(p1, p2, p3);
        p1.setGeom(new GM_Point(tri.getGeom().coord().get(0)));
        p2.setGeom(new GM_Point(tri.getGeom().coord().get(1)));
        p3.setGeom(new GM_Point(tri.getGeom().coord().get(2)));
        this.triangles.add(tri);

      } catch (Exception e) {
        Triangulation.logger
            .error("ERROR: impossible to create triangulation triangle");
        Triangulation.logger.error(e.getStackTrace());
        continue;
      }
    }

    if (this.getOptions().indexOf('v') != -1) {
      if (Triangulation.logger.isDebugEnabled()) {
        Triangulation.logger.debug(I18N
            .getString("Triangulation.VoronoiDiagramExportStart")); //$NON-NLS-1$
      }
      IEnvelope envelope = this.getPointsEnvelope();
      envelope.expandBy(100);
      this.getPopVoronoiVertices().initSpatialIndex(Tiling.class, true,
          envelope, 10);
      // l'export du diagramme de voronoi
      for (int j = 0; j < this.jvorout.numberofpoints; j++) {
        this.getPopVoronoiVertices().add(
            new Noeud(new GM_Point(new DirectPosition(
                this.jvorout.pointlist[2 * j],
                this.jvorout.pointlist[2 * j + 1]))));
      }
      for (int j = 0; j < this.jvorout.numberofedges; j++) {
        int indexIni = this.jvorout.edgelist[2 * j];
        int indexFin = this.jvorout.edgelist[2 * j + 1];
        if (indexFin == -1) {
          // infinite edge
          double vx = this.jvorout.normelist[2 * j];
          double vy = this.jvorout.normelist[2 * j + 1];
          Noeud c1 = this.getPopVoronoiVertices().getElements().get(indexIni);
          Noeud c2 = new Noeud();
          double vectorSize = 10000000;
          c2.setGeometrie(new GM_Point(new DirectPosition(c1.getGeometrie()
              .getPosition().getX()
              + vectorSize * vx, c1.getGeometrie().getPosition().getY()
              + vectorSize * vy)));
          GM_LineString line = new GM_LineString(new DirectPositionList(
              Arrays.asList(c1.getGeometrie().getPosition(), c2.getGeometrie()
                  .getPosition())));
          IGeometry intersection = line.intersection(envelope.getGeom());
          IDirectPositionList list = intersection.coord();
          if (list.size() > 1) {
            c2.setGeometrie(list.get(1).toGM_Point());
          }
          indexFin = this.getPopVoronoiVertices().size();
          this.getPopVoronoiVertices().add(c2);
        }
        this.getPopVoronoiEdges().add(
            new Arc(this.getPopVoronoiVertices().getElements().get(indexIni),
                this.getPopVoronoiVertices().getElements().get(indexFin)));
      }
    }
  }

  // *****************************************
  // VORONOI DIAGRAMS
  protected CarteTopo voronoiDiagram = null;

  /** Population des arcs de voronoi de la triangulation. */
  @Override
  public IPopulation<Arc> getPopVoronoiEdges() {
    return this.voronoiDiagram.getPopArcs();
  }

  /** Population des noeuds de voronoi de la triangulation. */
  @Override
  public IPopulation<Noeud> getPopVoronoiVertices() {
    return this.voronoiDiagram.getPopNoeuds();
  }

  /** Population des cellules de voronoi de la triangulation. */
  @Override
  public IPopulation<Face> getPopVoronoiFaces() {
    return this.voronoiDiagram.getPopFaces();
  }

  /** Le diagramme de Voronoi sous la forme d'une carte Topo */
  @Override
  public CarteTopo getVoronoiDiagram() {
    return this.voronoiDiagram;
  }

  public String getOptions() {
    return this.options;
  }

  public void setOptions(String options) {
    this.options = options;
  }

  private IEnvelope getPointsEnvelope() {
    IPopulation<DefaultFeature> pop = new Population<DefaultFeature>();
    for (TriangulationPoint pt : this.getPoints()) {
      pop.add(new DefaultFeature(pt.getGeom()));
    }
    return pop.envelope();
  }
}
