package fr.ign.cogit.geoxygene.matching.dst.geoAppariement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * @author Julien Perret
 *
 */
public class SingleLinkageAHC {

  static Logger logger = Logger
      .getLogger(SingleLinkageAHC.class.getName());

  List<Vertex> vertices = new ArrayList<Vertex>();
  List<Edge> edges = new ArrayList<Edge>();
  Collection<IFeature> elements;
  Cluster root;
  List<Cluster> leafClusterList;

  
  public SingleLinkageAHC(Collection<IFeature> elements,
      int numberOfClusters) {
    long t = System.currentTimeMillis();
    setElements(elements);
    for (IFeature element : this.getElements()) {
      this.insertElement(element);
    }
    for (int i = 0; i < this.vertices.size(); i++) {
      if (SingleLinkageAHC.logger.isTraceEnabled()) {
        SingleLinkageAHC.logger.trace(i + " "
            + this.edges.size());
      }
      for (int j = i + 1; j < this.vertices.size(); j++) {
        Vertex v1 = this.vertices.get(i);
        Vertex v2 = this.vertices.get(j);
        // float weight = weight(v1,v2);
        // if (weight<limit)
        this.edges.add(new Edge(v1, v2));
      }
    }
    Edge[] edgeArray = this.edges.toArray(new Edge[0]);
    Arrays.sort(edgeArray, new Comparator<Edge>() {
      @Override
      public int compare(Edge e1, Edge e2) {
        double w = e1.weight - e2.weight;
        if (w < 0) {
          return -1;
        }
        if (w > 0) {
          return 1;
        }
        return 0;
      }
    });
    this.edges = new ArrayList<Edge>(Arrays.asList(edgeArray));

    t = System.currentTimeMillis() - t;
    SingleLinkageAHC.logger
        .info("The initial construction of the graph took " + t + " ms");
    // logger.info("There are "+vertices.size()+" vertices and "+edges.size()+" edges");
    SingleLinkageAHC(numberOfClusters);
  }


  public void setElements(Collection<IFeature> elements) {
    this.elements = elements;
  }

  public Collection<IFeature> getElements() {
    return this.elements;
  }

  private void insertElement(IFeature element) {
    if (element != null) {
      this.vertices.add(new Vertex(element));
    }
  }

  /**
   * @param cluster1
   * @param cluster2
   * @return the squared distance between clusters
   */
  public double sqDistance(Cluster cluster1, Cluster cluster2) {
    if (cluster1.leaf) {
      return this.sqDistance(cluster1.getComponent().get(0), cluster2.getComponent().get(0));
    }
    return sqDistance(cluster1.getComponent(), cluster2.getComponent());
  }
  
  public double sqDistance(IFeature f1, IFeature f2){
    double dist = f1.getGeom().distance(f2.getGeom());
    return dist*dist;
  }
  
  public double sqDistance(Collection<IFeature> c1, Collection<IFeature> c2){
    List<IGeometry> geo1 = new ArrayList<IGeometry>();
    for(IFeature f : c1){
      geo1.add(f.getGeom());
    }
    List<IGeometry> geo2 = new ArrayList<IGeometry>();
    for(IFeature f : c2){
      geo2.add(f.getGeom());
    }
    double dist = JtsAlgorithms.union(geo1).distance(JtsAlgorithms.union(geo2));
    return dist*dist;
  }

  static final int MIN = 0;
  static final int MAX = 1;
  static final int AVG = 2;

  static int distanceClusterCluster = AVG;


  class Vertex {
    IFeature element;
    List<Edge> vertexEdges = new ArrayList<Edge>();
    public Vertex(IFeature e) {
      this.element = e;
    }

    @Override
    public boolean equals(Object obj) {
      return (obj instanceof Vertex) ? this.equals((Vertex) obj) : false;
    }

    public boolean equals(Vertex vertex) {
      return this.element.equals(vertex.element);
    }
  }

  class Edge {
    Vertex initialVertex;
    Vertex finalVertex;
    double weight;

    public Edge(Vertex v1, Vertex v2) {
      this.initialVertex = v1;
      this.finalVertex = v2;
      this.weight =  v1.element.getGeom().distance(v2.element.getGeom());
      this.weight *=this.weight; 
      v1.vertexEdges.add(this);
      v2.vertexEdges.add(this);
    }

    @Override
    public boolean equals(Object obj) {
      return (obj instanceof Edge) ? this.equals((Edge) obj) : false;
    }

    public boolean equals(Edge edge) {
      return this.initialVertex.equals(edge.initialVertex)
          && this.finalVertex.equals(edge.finalVertex);
    }
  }


  public static final int KRUSKAL = 0;
  public static final int PRIM = 1;
  private static int minimumSpanningTreeAlgorithm = KRUSKAL;

  public static int getMinimumSpanningTreeAlgorithm() {
    return SingleLinkageAHC.minimumSpanningTreeAlgorithm;
  }

  public static void setMinimumSpanningTreeAlgorithm(
      int theMinimumSpanningTreeAlgorithm) {
    SingleLinkageAHC.minimumSpanningTreeAlgorithm = theMinimumSpanningTreeAlgorithm;
  }

  public void SingleLinkageAHC(int numberOfClusters) {
    if (SingleLinkageAHC.minimumSpanningTreeAlgorithm == SingleLinkageAHC.PRIM) {
      //SingleLinkageAHCPrim(numberOfClusters);
    } else {
      SingleLinkageAHCKruskal(numberOfClusters);
    }
  }

  /**
   * @param numberOfClusters
   */
  /*
  public void SingleLinkageAHCPrim(int numberOfClusters) {
    long t = System.currentTimeMillis();
    List<Cluster> clusterPool = new ArrayList<Cluster>();
    Map<IFeature, Cluster> map = new HashMap<IFeature, Cluster>();
    // every color is its own cluster
    for (IFeature element : this.elements) {
      if (element != null) {
        Cluster c = new Cluster(element);
        clusterPool.add(c);
        map.put(element, c);
      }
    }
    List<Vertex> newVertices = new ArrayList<Vertex>();
    newVertices.add(this.vertices.get(0));

    while (clusterPool.size() != 1) {
      Edge e = getShortestEdge(newVertices);
      this.edges.remove(e);
      newVertices.add(newVertices.contains(e.initialVertex) ? e.finalVertex
          : e.initialVertex);
      Color c1 = e.initialVertex.color;
      Color c2 = e.finalVertex.color;
      Cluster cluster1 = map.get(c1);
      while (cluster1.parentCluster != null) {
        cluster1 = cluster1.parentCluster;
      }
      Cluster cluster2 = map.get(c2);
      while (cluster2.parentCluster != null) {
        cluster2 = cluster2.parentCluster;
      }
      while (cluster1.equals(cluster2)) {
        e = getShortestEdge();
        this.edges.remove(e);
        c1 = e.initialVertex.color;
        c2 = e.finalVertex.color;
        cluster1 = map.get(c1);
        while (cluster1.parentCluster != null) {
          cluster1 = cluster1.parentCluster;
        }
        cluster2 = map.get(c2);
        while (cluster2.parentCluster != null) {
          cluster2 = cluster2.parentCluster;
        }
      }
      Cluster cluster = new Cluster(cluster1, cluster2);
      clusterPool.remove(cluster1);
      clusterPool.remove(cluster2);
      clusterPool.add(cluster);
      if (SingleLinkageAHC.logger.isDebugEnabled()) {
        SingleLinkageAHC.logger.debug(clusterPool.size()
            + " clusters");
      }
    }

    t = System.currentTimeMillis() - t;
    SingleLinkageAHC.logger.info("Hierarchy building took "
        + t + " ms");

    this.root = clusterPool.get(0);
    // marking depth
    this.markDepth(this.root, 0);
    SingleLinkageAHC.logger.info("root cluster with level "
        + this.root.level);

    this.reduceClusters(numberOfClusters);

    Color[] newColors = new Color[numberOfClusters];
    int i = 0;
    for (Cluster cluster : leafClusterList) {
      newColors[i++] = cluster.getColor();
    }
    // changing the colors
    this.setColors(newColors);
  }
*/
  /**
   * @param newVertices
   * @return
   */
  private Edge getShortestEdge(List<Vertex> newVertices) {
    for (Edge e : this.edges) {
      if ((newVertices.contains(e.initialVertex) && !newVertices
          .contains(e.finalVertex))
          || (!newVertices.contains(e.initialVertex) && newVertices
              .contains(e.finalVertex))) {
        return e;
      }
    }
    return null;
  }

  public Edge getShortestEdge() {
    /*
     * float minWeight = Float.MAX_VALUE; Edge minEdge = null; for(Edge e:edges)
     * { float weight = weight(e); if (weight<minWeight) { minWeight = weight;
     * minEdge = e; } } return minEdge;
     */
    return this.edges.get(0);
  }

  /**
   * @param numberOfClusters
   */
  public void SingleLinkageAHCKruskal(int numberOfClusters) {
    long t = System.currentTimeMillis();
    List<Cluster> clusterPool = new ArrayList<Cluster>();
    Map<IFeature, Cluster> map = new HashMap<IFeature, Cluster>();
    // every color is its own cluster
    for (IFeature element : this.elements) {
      if (element != null) {
        Cluster c = new Cluster(element);
        clusterPool.add(c);
        map.put(element, c);
      }
    }
    SingleLinkageAHC.logger.info(this.elements.size()
        + " elements");

    // TODO revoir la fusion des clusters
    int i =0;
    while (clusterPool.size() != 1) {
      Edge e = getShortestEdge();
      this.edges.remove(e);
      IFeature c1 = e.initialVertex.element;
      IFeature c2 = e.finalVertex.element;
      Cluster cluster1 = this.topCluster(map.get(c1));
      Cluster cluster2 = this.topCluster(map.get(c2));
      while (cluster1.equals(cluster2)) {
        e = getShortestEdge();
        this.edges.remove(e);
        c1 = e.initialVertex.element;
        c2 = e.finalVertex.element;
        cluster1 = this.topCluster(map.get(c1));
        cluster2 = this.topCluster(map.get(c2));
      }
      Cluster cluster = mergeClusters(cluster1, cluster2);
      cluster.weight = e.weight;
      clusterPool.remove(cluster1);
      clusterPool.remove(cluster2);
      clusterPool.add(cluster);
      if (SingleLinkageAHC.logger.isDebugEnabled()) {
        SingleLinkageAHC.logger.debug(clusterPool.size()
            + " clusters");
      }
      
      if (SingleLinkageAHC.logger.isDebugEnabled()) {
       // writeDebugShape("kruskal_"+(++i), clusterPool);
      }
      SingleLinkageAHC.logger.info(clusterPool.size()
          + " clusters");
    }

    SingleLinkageAHC.logger.info("Hierarchy building took "
        + (System.currentTimeMillis() - t) + " ms");
    t = System.currentTimeMillis();

    this.root = clusterPool.get(0);
    // marking depth
    this.markDepth(this.root, 0);

    this.reduceClusters(1.0d);
    t = System.currentTimeMillis() - t;
    SingleLinkageAHC.logger.info("Cluster reduction took "
        + t + " ms");

    i = 0;
    for (Cluster cluster : this.leafClusterList) {
      if(cluster.getComponent().size() == 1){
        i++;
      }
    }
    this.writeDebugShape("result_simplelinkage",this.leafClusterList);
    System.out.println("NUMBER OF LEAVES : "+i);
  }


  /**
   * @param clusterPool
   */
  private void writeDebugShape(String prefix, List<Cluster> clusterPool) {
    Population<IFeature> clusters = new Population<IFeature>();
    for(Cluster c : clusterPool){
      List<IGeometry> geoms = new ArrayList<IGeometry>();
      for(IFeature f : c.component){
        geoms.add(f.getGeom());
      }
      IGeometry geom = JtsAlgorithms.union(geoms);
      IFeature feat = new DefaultFeature(geom.convexHull());
      clusters.add(feat);
    }
//    ShapefileWriter.write(clusters, "/home/BDumenieu/Bureau/temp/"+prefix+".shp");
  }
  
  public List<List<IFeature>> getClustersAsLists(){
    List<List<IFeature>> clusters = new ArrayList<List<IFeature>>();
    for(Cluster c  : this.leafClusterList){
      clusters.add(c.getComponent());
    }
    return clusters;
  }
  
  

  /**
   * @param cluster
   * @return
   */
  private Cluster topCluster(Cluster cluster) {
    return (cluster.parentCluster == null) ? cluster : this
        .topCluster(cluster.parentCluster);
  }

  /**
   * @param cluster1
   * @param cluster2
   */
  private Cluster mergeClusters(Cluster cluster1, Cluster cluster2) {
    return new Cluster(cluster1, cluster2);
  }

  public void reduceClusters(double maximum) {
    List<Cluster> result = new ArrayList<SingleLinkageAHC.Cluster>();
    result.add(this.root);
    Stack<Cluster> stack = new Stack<Cluster>();
    stack.add(this.root);
    while (!stack.empty()) {
      Cluster c = stack.pop();
      if (!c.leaf) {
        Cluster left = c.leftCluster;
        Cluster right = c.rightCluster;
        result.remove(c);
        if(c.weight > (maximum*maximum)){
          stack.add(left);
          stack.add(right);
        }else{
          result.add(c);
        }
      }else{
        result.add(c);
      }
    }
    this.leafClusterList = result;
  }
  
  
  public void reduceClusters(int numberOfClusters) {
    Cluster[] leaves = leaves(this.root).toArray(new Cluster[0]);

    // cutting the links to obtain k groups (numberOfClusters)
    // TODO revoir cette distance entre clusters !!!
    Arrays.sort(leaves, new Comparator<Cluster>() {
      @Override
      public int compare(Cluster c1, Cluster c2) {
        // if (c1.depth==c2.depth) {
        Cluster parent1 = c1.parentCluster;
        Cluster parent2 = c2.parentCluster;
        double d1Left = SingleLinkageAHC.this.sqDistance(
            parent1, parent1.leftCluster);
        double d1Right = SingleLinkageAHC.this.sqDistance(
            parent1, parent1.rightCluster);
        // float d1Avg = (d1Left+d1Right)/2;
        double d1 = Math.max(d1Left, d1Right);// (float)
                                             // (Math.pow(d1Avg-d1Left,2)+Math.pow(d1Avg-d1Right,2));
        double d2Left = SingleLinkageAHC.this.sqDistance(
            parent2, parent2.leftCluster);
        double d2Right = SingleLinkageAHC.this.sqDistance(
            parent2, parent2.rightCluster);
        // float d2Avg = (d2Left+d2Right)/2;
        double d2 = Math.max(d2Left, d2Right);// (float)
                                             // (Math.pow(d2Avg-d2Left,2)+Math.pow(d2Avg-d2Right,2));
        double d = d2 - d1;
        if (   d < 0) {
          return -1;
        }
        if (d > 0) {
          return 1;
        }
        return 0;
        // }
        // return c1.depth-c2.depth;
      }
    });

    this.leafClusterList = new ArrayList<Cluster>(Arrays.asList(leaves));
    // logger.info("There are "+clusterList.size()+" leaves");

    while (this.leafClusterList.size() > numberOfClusters) {
      Cluster lastCluster = this.leafClusterList.get(this.leafClusterList
          .size() - 1);
      Cluster lastClusterParent = lastCluster.parentCluster;
      Cluster brotherCluster = (lastClusterParent.rightCluster == lastCluster) ? lastClusterParent.leftCluster
          : lastClusterParent.rightCluster;
      lastCluster.parentCluster = null;
      // logger.info("    "+clusterList.size()+" leaves before removal ( "+clusterList.indexOf(lastClusterParent.rightCluster)+"     "+clusterList.indexOf(lastClusterParent.leftCluster));
      this.leafClusterList.remove(lastCluster);
      this.leafClusterList.remove(brotherCluster);
      // logger.info("    "+clusterList.size()+" leaves after removal");
      if (brotherCluster.leaf) {
        lastClusterParent.leaf = true;
        lastClusterParent.leftCluster = null;
        lastClusterParent.rightCluster = null;
        boolean inserted = false;
        for (int i = 0; (i < this.leafClusterList.size()) && !inserted; i++) {
          Cluster parent1 = lastClusterParent.parentCluster;
          Cluster parent2 = this.leafClusterList.get(i).parentCluster;
          if (parent1 == null) {
            this.leafClusterList.add(i, lastClusterParent);
            inserted = true;
          } else if (parent2 == null) {
            continue;
          } else {
            double d1Left = this.sqDistance(parent1, parent1.leftCluster);
            double d1Right = this.sqDistance(parent1, parent1.rightCluster);
            // float d1Avg = (d1Left+d1Right)/2;
            double d1 = Math.max(d1Left, d1Right);// (float)
                                                 // (Math.pow(d1Avg-d1Left,2)+Math.pow(d1Avg-d1Right,2));
            double d2Left = this.sqDistance(parent2, parent2.leftCluster);
            double d2Right = this.sqDistance(parent2, parent2.rightCluster);
            // float d2Avg = (d2Left+d2Right)/2;
            double d2 = Math.max(d2Left, d2Right);// (float)
                                                 // (Math.pow(d2Avg-d2Left,2)+Math.pow(d2Avg-d2Right,2));
            if (d1 > d2) {
              this.leafClusterList.add(i, lastClusterParent);
              inserted = true;
            }
          }
          // }
          SingleLinkageAHC.logger.debug(this.leafClusterList
              .size()
              + " leaves");
        }
        if (!inserted) {
          this.leafClusterList.add(lastClusterParent);
        }
      } else {
        if (lastClusterParent.parentCluster != null) {
          // shift everything one level up
          if (lastClusterParent.parentCluster.rightCluster == lastClusterParent.parentCluster) {
            lastClusterParent.parentCluster.rightCluster = brotherCluster;
          } else {
            lastClusterParent.parentCluster.leftCluster = brotherCluster;
          }
        } else {
        }
        brotherCluster.parentCluster = lastClusterParent.parentCluster;
        this.markDepth(brotherCluster, lastClusterParent.depth);
      }
//      if (SingleLinkageHierarchicalClusterer.logger.isDebugEnabled()) {
//        this.writeDebugImage("reduceCluster_", Arrays
//            .asList(new Cluster[] { this.root }), this.leafClusterList.size());
//      }
    }

    this.markDepth(this.root, 0);
  }

  /**
   * @param root
   * @return
   */
  private List<Cluster> leaves(Cluster cluster) {
    List<Cluster> result = new ArrayList<Cluster>();
    if (cluster.leaf) {
      result.add(cluster);
      return result;
    }
    result.addAll(this.leaves(cluster.rightCluster));
    result.addAll(this.leaves(cluster.leftCluster));
    return result;
  }

  /**
   * @param root
   */
  private void markDepth(Cluster cluster, int newDepth) {
    cluster.depth = newDepth;
    if (!cluster.leaf) {
      this.markDepth(cluster.leftCluster, newDepth + 1);
      this.markDepth(cluster.rightCluster, newDepth + 1);
      cluster.level = Math.max(cluster.leftCluster.level,
          cluster.rightCluster.level) + 1;
    } else {
      cluster.level = 0;
    }
  }

  public static void drawCluster(Graphics2D graphics, Cluster cluster,
      BigDecimal startAngle, BigDecimal angle, int width, int size) {
    if (cluster == null) {
      return;
    }
    // radius of the current arc
    int r = (cluster.depth + 1) * size;
    // shift the the beginning of the current arc
    int x = width / 2 - r;
    // angle for each child element
    if (SingleLinkageAHC.logger.isDebugEnabled()) {
      SingleLinkageAHC.logger.debug(cluster.depth
          + "-" + angle);
    }
    // draw the children
    // double angleChildren = angle/2.0;
    if (!cluster.leaf) {
      /*
       * BigDecimal leftSize = new BigDecimal(cluster.leftCluster.size);
       * BigDecimal rightSize = new BigDecimal(cluster.rightCluster.size);
       * BigDecimal sum = leftSize.add(rightSize); BigDecimal rightAngle =
       * angle.multiply(rightSize).divide(sum, BigDecimal.ROUND_UP); BigDecimal
       * leftAngle = angle.add(rightAngle.negate());
       */
      BigDecimal angleChildren = angle.divide(new BigDecimal(2),
          BigDecimal.ROUND_UP);
      drawCluster(graphics, cluster.leftCluster, startAngle, angleChildren,
          width, size);
      drawCluster(graphics, cluster.rightCluster,
          startAngle.add(angleChildren), angle.subtract(angleChildren), width,
          size);
    }
    // build the average color for the children of the node
    graphics.setColor(Color.BLUE);
    if (angle.intValue() != 0) {
      graphics.fillArc(x, x, 2 * r, 2 * r, startAngle.intValue(), angle
          .intValue());
    } else {
      graphics.drawLine(x + r, x + r, x + r
          + (int) (r * Math.cos(Math.toRadians(startAngle.doubleValue()))), x
          + r - (int) (r * Math.sin(Math.toRadians(startAngle.doubleValue()))));
      graphics.drawLine(x + r, x + r, x
          + r
          + (int) (r * Math.cos(Math.toRadians(startAngle.doubleValue()
              + angle.doubleValue()))), x
          + r
          - (int) (r * Math.sin(Math.toRadians(startAngle.doubleValue()
              + angle.doubleValue()))));
    }
  }


//TODO inclure les différences de colorspace dans le cluster et la fonction
//inverse RGB -> LAB
class Cluster {
 public boolean leaf = true;
 public Cluster leftCluster = null;
 public Cluster rightCluster = null;
 public Cluster parentCluster = null;
 public int level;
 public int size;
 public int depth = 0;
 public List<IFeature> component;
 public double weight;

 public Cluster(IFeature e) {
   this.leaf = true;
   this.level = 0;
   this.size = 1;
   this.weight = -1;
   this.component = new ArrayList<IFeature>();
   this.component.add(e);
 }

 public Cluster(Cluster cluster1, Cluster cluster2) {
   this.leaf = false;
   this.leftCluster = cluster1;
   this.rightCluster = cluster2;
   this.leftCluster.parentCluster = this;
   this.rightCluster.parentCluster = this;
   this.component = new ArrayList<IFeature>();
   
   for(IFeature e : cluster1.component){
     this.component.add(e);
   }
   for(IFeature e : cluster2.component){
     this.component.add(e);
   }
   this.level = Math.max(cluster1.level, cluster2.level) + 1;
   this.size = cluster1.size + cluster2.size;
 }
 
    public IFeature getClusterAsFeature() {
      if (this.leaf) {
        return this.component.get(0);
      } else {
        List<IGeometry> geoms = new ArrayList<IGeometry>();
        for (IFeature f : this.component) {
          geoms.add(f.getGeom());
        }
        IGeometry g = JtsAlgorithms.union(geoms);
        DefaultFeature df = new DefaultFeature(g);
        return df;
      }
    }

    public List<IFeature> getClusterComponents() {
      List<IFeature> feats = new ArrayList<IFeature>();
      if (this.leaf) {
        feats.add(this.component.get(0));
      } else {
        for (IFeature f : this.component) {
          feats.add(f);
        }
      }
      return feats;
    }

 @Override
 public boolean equals(Object obj) {
   if (obj instanceof Cluster) {
     Cluster cluster = (Cluster) obj;
     return this.equals(cluster);
   }
   return false;
 }

 public boolean equals(Cluster cluster) {
   boolean ok = true;
   for(IFeature e : this.component){
     if(!cluster.component.contains(e)){
       ok = false;
       break;
     }
   }
   boolean leafValues =ok && (this.level == cluster.level) && (this.size == cluster.size);
   if (this.leaf) {
     return cluster.leaf && leafValues;
   }
   return (!cluster.leaf) && cluster.leftCluster.equals(this.leftCluster)
       && cluster.rightCluster.equals(this.rightCluster) && leafValues;
 }

 public List<IFeature> getComponent() {
   return this.component;
 }
}

  /**
   * @return
   * 
   */
  public List<Cluster> getClusters() {
    return this.leafClusterList;

  }
}
