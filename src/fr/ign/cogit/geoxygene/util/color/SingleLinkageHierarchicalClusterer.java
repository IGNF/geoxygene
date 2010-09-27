/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.util.color;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * @author Julien Perret
 *
 */
public class SingleLinkageHierarchicalClusterer {
    static Logger logger=Logger.getLogger(SingleLinkageHierarchicalClusterer.class.getName());

    List<Vertex> vertices = new ArrayList<Vertex>();
    List<Edge> edges = new ArrayList<Edge>();
    private Color[] colors;
    Cluster root;
    List<Cluster> leafClusterList;

    /**
     * @param thecolors
     * @param numberOfClusters
     */
    public SingleLinkageHierarchicalClusterer(Color[] thecolors, int numberOfClusters) {
	long t = System.currentTimeMillis();
	setColors(thecolors);
	for(Color color:this.getColors()) insertColor(color);
	for(int i = 0 ; i < this.vertices.size() ; i++) {
	    if (logger.isTraceEnabled()) logger.trace(i+" "+this.edges.size()); //$NON-NLS-1$
	    for(int j = i+1 ; j < this.vertices.size() ; j++) {
		Vertex v1= this.vertices.get(i);
		Vertex v2=this.vertices.get(j);
		//float weight = weight(v1,v2);
		//if (weight<limit)
		this.edges.add(new Edge(v1,v2));
	    }
	}
	Edge[] edgeArray = this.edges.toArray(new Edge[0]);
	Arrays.sort(edgeArray,new Comparator<Edge>() {
	    @Override
		public int compare(Edge e1, Edge e2) {
		float w =e1.weight-e2.weight;
		if (w<0) return -1;
		if (w>0) return 1;
		return 0;
	    }});
	this.edges=new ArrayList<Edge>(Arrays.asList(edgeArray));

	t = System.currentTimeMillis()-t;
	logger.info("The initial construction of the graph took "+t+" ms");
	//logger.info("There are "+vertices.size()+" vertices and "+edges.size()+" edges");
	singleLinkageHierarchicalClustering(numberOfClusters);
    }

    /**
     * Sets the color array
     * @param colors to use as the color array
     */
    public void setColors(Color[] colors) {this.colors = colors;}

    /**
     * Returns the color array
     * @return the color array
     */
    public Color[] getColors() {return this.colors;}

    /**
     * @param color
     * @param type
     */
    private void insertColor(Color color) {if (color!=null) this.vertices.add(new Vertex(color));}

    /**
     * @param cluster1
     * @param cluster2
     * @return
     */
    public float sqDistance(Cluster cluster1, Cluster cluster2) {
	if (cluster1.leaf) return sqDistance(cluster1.getColor(), cluster2);
	return distanceClusterCluster(sqDistance(cluster1.leftCluster, cluster2), sqDistance(cluster1.rightCluster, cluster2));
    }

    static final int MIN = 0;
    static final int MAX = 1;
    static final int AVG = 2;

    static int distanceClusterCluster = MIN;
    public static void setDistanceClusterCluster(int theDistanceClusterCluster) {SingleLinkageHierarchicalClusterer.distanceClusterCluster = theDistanceClusterCluster;}
    static int distanceColorCluster = MIN;
    public static void setDistanceColorCluster(int theDistanceColorCluster) {SingleLinkageHierarchicalClusterer.distanceColorCluster = theDistanceColorCluster;}

    /**
     * @param sqDistance1
     * @param sqDistance2
     * @return
     */
    private float distanceClusterCluster(float sqDistance1, float sqDistance2) {
	//if (logger.isDebugEnabled()) logger.debug((distanceClusterCluster == MIN)?"MIN":(distanceClusterCluster == MAX)?"MAX":"AVG");
	if (distanceClusterCluster == MIN) return Math.min(sqDistance1, sqDistance2);
	if (distanceClusterCluster == MAX) return Math.max(sqDistance1, sqDistance2);
        return (sqDistance1+sqDistance2)/2;
    }

    public float sqDistance(Color color, Cluster cluster) {
	if (cluster.leaf) {
	    logger.debug("color = "+color+"  -  cluster = "+cluster);
	    return ColorUtil.sqDistance(color, cluster.getColor());
	}
	return distanceColorCluster(sqDistance(color, cluster.leftCluster), sqDistance(color, cluster.rightCluster));
    }
    /**
     * @param sqDistance1
     * @param sqDistance2
     * @return
     */
    private float distanceColorCluster(float sqDistance1, float sqDistance2) {
	//if (logger.isDebugEnabled()) logger.debug((distanceColorCluster == MIN)?"MIN":(distanceColorCluster == MAX)?"MAX":"AVG");
	if (distanceColorCluster == MIN) return Math.min(sqDistance1, sqDistance2);
	if (distanceColorCluster == MAX) return Math.max(sqDistance1, sqDistance2);
	return (sqDistance1+sqDistance2)/2;
    }

    class Vertex {
	Color color;
	List<Edge> vertexEdges = new ArrayList<Edge>();
	public Vertex(Color c) {this.color=c;}
	@Override
	public boolean equals(Object obj) {return (obj instanceof Vertex)?this.equals((Vertex) obj):false;}
	public boolean equals(Vertex vertex) {return this.color.equals(vertex.color);}
    }

    class Edge {
	Vertex initialVertex;
	Vertex finalVertex;
	float weight;
	public Edge(Vertex v1, Vertex v2) {
	    this.initialVertex=v1;
	    this.finalVertex=v2;
	    this.weight = ColorUtil.sqDistance(v1.color, v2.color);
	    v1.vertexEdges.add(this);
	    v2.vertexEdges.add(this);
	}
	@Override
	public boolean equals(Object obj) {return (obj instanceof Edge)?this.equals((Edge) obj):false;}
	public boolean equals(Edge edge) {return this.initialVertex.equals(edge.initialVertex)&&this.finalVertex.equals(edge.finalVertex);}
    }

    public float weight(Vertex v1, Vertex v2) {return ColorUtil.sqDistance(v1.color, v2.color);}
    public float weight(Edge e) {return weight(e.initialVertex, e.finalVertex);}

    public static final int KRUSKAL = 0;
    public static final int PRIM = 1;
    private static int minimumSpanningTreeAlgorithm = PRIM;
    public static int getMinimumSpanningTreeAlgorithm() {return minimumSpanningTreeAlgorithm;}
    public static void setMinimumSpanningTreeAlgorithm(int theMinimumSpanningTreeAlgorithm) {minimumSpanningTreeAlgorithm = theMinimumSpanningTreeAlgorithm;}

    public void singleLinkageHierarchicalClustering(int numberOfClusters) {
	if (minimumSpanningTreeAlgorithm==PRIM) singleLinkageHierarchicalClusteringPrim(numberOfClusters);
	else singleLinkageHierarchicalClusteringKruskal(numberOfClusters);
    }

    /**
     * @param numberOfClusters
     */
    public void singleLinkageHierarchicalClusteringPrim(int numberOfClusters) {
	long t = System.currentTimeMillis();
	List<Cluster> clusterPool = new ArrayList<Cluster>();
	Map<Color,Cluster> map = new HashMap<Color,Cluster>();
	// every color is its own cluster
	for (Color color:this.colors) if (color!=null) {
	    Cluster c = new Cluster(color);
	    clusterPool.add(c);
	    map.put(color, c);
	}
	List<Vertex> newVertices = new ArrayList<Vertex>();
	newVertices.add(this.vertices.get(0));

	while (clusterPool.size()!=1) {
	    Edge e = getShortestEdge(newVertices);
	    this.edges.remove(e);
	    newVertices.add(newVertices.contains(e.initialVertex)?e.finalVertex:e.initialVertex);
	    Color c1 = e.initialVertex.color;
	    Color c2 = e.finalVertex.color;
	    Cluster cluster1 = map.get(c1);
	    while (cluster1.parentCluster!=null) cluster1=cluster1.parentCluster;
	    Cluster cluster2 = map.get(c2);
	    while (cluster2.parentCluster!=null) cluster2=cluster2.parentCluster;
	    while(cluster1.equals(cluster2)) {
		e = getShortestEdge();
		this.edges.remove(e);
		c1 = e.initialVertex.color;
		c2 = e.finalVertex.color;
		cluster1 = map.get(c1);
		while (cluster1.parentCluster!=null) cluster1=cluster1.parentCluster;
		cluster2 = map.get(c2);
		while (cluster2.parentCluster!=null) cluster2=cluster2.parentCluster;
	    }
	    Cluster cluster = new Cluster(cluster1,cluster2);
	    clusterPool.remove(cluster1);
	    clusterPool.remove(cluster2);
	    clusterPool.add(cluster);
	    if (logger.isDebugEnabled()) logger.debug(clusterPool.size()+" clusters");
	}	

	t = System.currentTimeMillis()-t;
	logger.info("Hierarchy building took "+t+" ms");

	this.root = clusterPool.get(0);
	// marking depth
	markDepth(this.root,0);
	logger.info("root cluster with level "+this.root.level);

	reduceClusters(numberOfClusters);

	Color[] newColors = new Color[numberOfClusters];
	int i = 0;
	for(Cluster cluster:this.leafClusterList) newColors[i++]=cluster.getColor();
	// changing the colors
	this.setColors(newColors);
    }

    /**
     * @param newVertices
     * @return
     */
    private Edge getShortestEdge(List<Vertex> newVertices) {
	for(Edge e:this.edges) {
	    if ( (newVertices.contains(e.initialVertex)&&!newVertices.contains(e.finalVertex)) || (!newVertices.contains(e.initialVertex)&&newVertices.contains(e.finalVertex)) )
		return e;
	}
	return null;
    }

    public Edge getShortestEdge() {
	/*
	float minWeight = Float.MAX_VALUE;
	Edge minEdge = null;
	for(Edge e:edges) {
	    float weight = weight(e);
	    if (weight<minWeight) {
		minWeight = weight;
		minEdge = e;
	    }
	}
	return minEdge;
	 */
	return this.edges.get(0);
    }

    /**
     * @param numberOfClusters
     */
    public void singleLinkageHierarchicalClusteringKruskal(int numberOfClusters) {
	long t = System.currentTimeMillis();
	List<Cluster> clusterPool = new ArrayList<Cluster>();
	Map<Color,Cluster> map = new HashMap<Color,Cluster>();
	// every color is its own cluster
	for (Color color:this.colors) if (color!=null) {
	    Cluster c = new Cluster(color);
	    clusterPool.add(c);
	    map.put(color, c);
	}

	if (logger.isDebugEnabled()) writeDebugImage("kruskal_",clusterPool,clusterPool.size());

	// TODO revoir la fusion des clusters
	while (clusterPool.size()!=1) {
	    Edge e = getShortestEdge();
	    this.edges.remove(e);
	    Color c1 = e.initialVertex.color;
	    Color c2 = e.finalVertex.color;
	    Cluster cluster1 = topCluster(map.get(c1));
	    Cluster cluster2 = topCluster(map.get(c2));
	    while(cluster1.equals(cluster2)) {
		e = getShortestEdge();
		this.edges.remove(e);
		c1 = e.initialVertex.color;
		c2 = e.finalVertex.color;
		cluster1 = topCluster(map.get(c1));
		cluster2 = topCluster(map.get(c2));		
	    }
	    Cluster cluster = mergeClusters(cluster1,cluster2);
	    clusterPool.remove(cluster1);
	    clusterPool.remove(cluster2);
	    clusterPool.add(cluster);
	    if (logger.isDebugEnabled()) logger.debug(clusterPool.size()+" clusters");
	    if (logger.isDebugEnabled()) writeDebugImage("kruskal_",clusterPool, clusterPool.size());
	}

	logger.info("Hierarchy building took "+(System.currentTimeMillis()-t)+" ms");
	t = System.currentTimeMillis();

	this.root = clusterPool.get(0);
	// marking depth
	markDepth(this.root,0);

	reduceClusters(numberOfClusters);
	t = System.currentTimeMillis()-t;
	logger.info("Cluster reduction took "+t+" ms");

	Color[] newColors = new Color[numberOfClusters];
	int i = 0;
	for(Cluster cluster:this.leafClusterList) newColors[i++]=cluster.getColor();
	// changing the colors
	this.setColors(newColors);
    }

    public BufferedImage buildImage() {
	int ringSize = 10;
	int width = (this.root.level+1)*2*ringSize;
	BufferedImage clusterImage = new BufferedImage(width, width,BufferedImage.TYPE_INT_RGB);
	Graphics2D graphics = clusterImage.createGraphics();
	graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
	graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
	graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	graphics.setStroke(new BasicStroke(1.0f));
	drawCluster(graphics, this.root, new BigDecimal(0), new BigDecimal(360), width, ringSize);
	return clusterImage;
    }


    /**
     * @param clusterPool
     */
    private void writeDebugImage(String prefix,List<Cluster> clusterPool, int n) {
	BufferedImage clusterImage = new BufferedImage(512, 512,BufferedImage.TYPE_INT_RGB);
	Graphics2D graphics = clusterImage.createGraphics();
	graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
	graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
	graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	graphics.setStroke(new BasicStroke(1.0f));
	for(Cluster cluster:clusterPool) drawClusterLab(graphics, cluster, 512, 8);
	try {ImageIO.write(clusterImage, "PNG", new File("/tmp/"+prefix+clusterPool.size()+"_"+n));} catch (IOException e1) {e1.printStackTrace();} //$NON-NLS-1$
    }

    /**
     * @param cluster
     * @return
     */
    private Cluster topCluster(Cluster cluster) {return (cluster.parentCluster==null)?cluster:topCluster(cluster.parentCluster);}

    /**
     * @param cluster1
     * @param cluster2
     */
    private Cluster mergeClusters(Cluster cluster1, Cluster cluster2) {return new Cluster(cluster1,cluster2);}

    public void reduceClusters(int numberOfClusters) {
	Cluster[] leaves = leaves(this.root).toArray(new Cluster[0]);

	// cutting the links to obtain k groups (numberOfClusters)
	//TODO revoir cette distance entre clusters !!!
	Arrays.sort(leaves, new Comparator<Cluster>() {
	    @Override
		public int compare(Cluster c1, Cluster c2) {
	    //if (c1.depth==c2.depth) {
	    Cluster parent1 = c1.parentCluster;
	    Cluster parent2 = c2.parentCluster;
	    Color color1 = parent1.getColor();
	    float d1Left = sqDistance(color1,parent1.leftCluster);
	    float d1Right = sqDistance(color1, parent1.rightCluster);
	    //float d1Avg = (d1Left+d1Right)/2;
	    float d1 = Math.max(d1Left,d1Right);//(float) (Math.pow(d1Avg-d1Left,2)+Math.pow(d1Avg-d1Right,2));
	    Color color2 = parent2.getColor();
	    float d2Left = sqDistance(color2,parent2.leftCluster);
	    float d2Right = sqDistance(color2, parent2.rightCluster);
	    //float d2Avg = (d2Left+d2Right)/2;
	    float d2 = Math.max(d2Left,d2Right);//(float) (Math.pow(d2Avg-d2Left,2)+Math.pow(d2Avg-d2Right,2));
	    float d = d2-d1;
	    if (d<0) return -1;
	    if (d>0) return 1;
	    return 0;
	    //}
	    //return c1.depth-c2.depth;
	    }});

	this.leafClusterList = new ArrayList<Cluster>(Arrays.asList(leaves));
	//logger.info("There are "+clusterList.size()+" leaves");

	while(this.leafClusterList.size()>numberOfClusters) {
	    Cluster lastCluster = this.leafClusterList.get(this.leafClusterList.size()-1);
	    Cluster lastClusterParent = lastCluster.parentCluster;
	    Cluster brotherCluster = (lastClusterParent.rightCluster==lastCluster)?lastClusterParent.leftCluster:lastClusterParent.rightCluster;
	    lastCluster.parentCluster=null;
	    //logger.info("    "+clusterList.size()+" leaves before removal ( "+clusterList.indexOf(lastClusterParent.rightCluster)+"     "+clusterList.indexOf(lastClusterParent.leftCluster));
	    this.leafClusterList.remove(lastCluster);
	    this.leafClusterList.remove(brotherCluster);
	    //logger.info("    "+clusterList.size()+" leaves after removal");
	    if (brotherCluster.leaf) {
		lastClusterParent.leaf=true;
		lastClusterParent.leftCluster = null;
		lastClusterParent.rightCluster = null;
		boolean inserted = false;
		for (int i = 0 ; (i < this.leafClusterList.size()) && !inserted ; i++ ) {
		    Cluster parent1 = lastClusterParent.parentCluster;
		    Cluster parent2 = this.leafClusterList.get(i).parentCluster;
		    if (parent1==null) {
			this.leafClusterList.add(i,lastClusterParent);
			inserted = true;
		    } else if (parent2==null) {
			continue;
		    } else {
			Color color1 = parent1.getColor();
			float d1Left = sqDistance(color1,parent1.leftCluster);
			float d1Right = sqDistance(color1, parent1.rightCluster);
			//float d1Avg = (d1Left+d1Right)/2;
			float d1 = Math.max(d1Left,d1Right);//(float) (Math.pow(d1Avg-d1Left,2)+Math.pow(d1Avg-d1Right,2));
			Color color2 = parent2.getColor();
			float d2Left = sqDistance(color2,parent2.leftCluster);
			float d2Right = sqDistance(color2, parent2.rightCluster);
			//float d2Avg = (d2Left+d2Right)/2;
			float d2 = Math.max(d2Left,d2Right);//(float) (Math.pow(d2Avg-d2Left,2)+Math.pow(d2Avg-d2Right,2));
			if (d1>d2) {
			    this.leafClusterList.add(i,lastClusterParent);
			    inserted = true;
			}
		    }
		    //}
		    logger.debug(this.leafClusterList.size()+" leaves");
		}
		if (!inserted) this.leafClusterList.add(lastClusterParent);
	    } else {
		if (lastClusterParent.parentCluster!=null) {
		    // shift everything one level up
		    if (lastClusterParent.parentCluster.rightCluster==lastClusterParent.parentCluster) {
			lastClusterParent.parentCluster.rightCluster=brotherCluster;
		    } else {
			lastClusterParent.parentCluster.leftCluster=brotherCluster;
		    }
		} else {} 
		brotherCluster.parentCluster=lastClusterParent.parentCluster;
		markDepth(brotherCluster, lastClusterParent.depth);
	    }
	    if (logger.isDebugEnabled()) writeDebugImage("reduceCluster_",Arrays.asList(new Cluster[]{this.root}),this.leafClusterList.size());
	}

	markDepth(this.root,0);
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
	result.addAll(leaves(cluster.rightCluster));
	result.addAll(leaves(cluster.leftCluster));
	return result;
    }
    /**
     * @param root
     */
    private void markDepth(Cluster cluster,int newDepth) {
	cluster.depth=newDepth;
	if (!cluster.leaf) {
	    markDepth(cluster.leftCluster, newDepth+1);
	    markDepth(cluster.rightCluster, newDepth+1);
	    cluster.level=Math.max(cluster.leftCluster.level, cluster.rightCluster.level)+1;
	} else {
	    cluster.level=0;
	}
    }

    public static void drawCluster(Graphics2D graphics, Cluster cluster, BigDecimal startAngle, BigDecimal angle, int width, int size) {
	if (cluster==null) return;
	// radius of the current arc
	int r = (cluster.depth+1)*size;
	// shift the the beginning of the current arc
	int x = width/2-r;
	// angle for each child element
	if (logger.isDebugEnabled()) logger.debug(cluster.depth+"-"+angle); //$NON-NLS-1$
	// draw the children
	//double angleChildren = angle/2.0;
	if (!cluster.leaf) {
	    /*
	    BigDecimal leftSize = new BigDecimal(cluster.leftCluster.size);
	    BigDecimal rightSize = new BigDecimal(cluster.rightCluster.size);
	    BigDecimal sum = leftSize.add(rightSize);
	    BigDecimal rightAngle = angle.multiply(rightSize).divide(sum, BigDecimal.ROUND_UP);
	    BigDecimal leftAngle = angle.add(rightAngle.negate());
	     */
	    BigDecimal angleChildren = angle.divide(new BigDecimal(2), BigDecimal.ROUND_UP);
	    drawCluster(graphics,cluster.leftCluster,startAngle,angleChildren,width,size);
	    drawCluster(graphics,cluster.rightCluster,startAngle.add(angleChildren),angle.subtract(angleChildren), width,size);
	}
	// build the average color for the children of the node
	graphics.setColor(cluster.getColor());
	if(angle.intValue()!=0) graphics.fillArc(x,x,2*r,2*r,startAngle.intValue(),angle.intValue());
	else {
	    graphics.drawLine(x+r, x+r, x+r+(int)(r*Math.cos(Math.toRadians(startAngle.doubleValue()))), x+r-(int)(r*Math.sin(Math.toRadians(startAngle.doubleValue()))));
	    graphics.drawLine(x+r, x+r, x+r+(int)(r*Math.cos(Math.toRadians(startAngle.doubleValue()+angle.doubleValue()))), x+r-(int)(r*Math.sin(Math.toRadians(startAngle.doubleValue()+angle.doubleValue()))));
	}
    }

    public static void writeClusterImage(SingleLinkageHierarchicalClusterer graph, int imageSize, int clusterSize, String imageName) {
	BufferedImage image = graph.buildClusterImage(imageSize, clusterSize);
	ColorUtil.writeImage(image, imageName);
    }

    public BufferedImage buildClusterImage(int imageSize, int clusterSize) {
	return buildClusterImage(this.root, imageSize, clusterSize);
    }

    public BufferedImage buildClusterImage(Cluster rootCluster,int imageSize, int clusterSize) {
	BufferedImage clusterImage = new BufferedImage(imageSize, imageSize,BufferedImage.TYPE_INT_RGB);
	Graphics2D graphics = clusterImage.createGraphics();
	graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
	graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
	graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	graphics.setStroke(new BasicStroke(1.0f));
	drawClusterLab(graphics, rootCluster, imageSize, clusterSize);
	return clusterImage;
    }

    /**
     * @param graphics
     * @param rootCluster
     */
    private void drawClusterLab(Graphics2D graphics, Cluster cluster, int imageSize, int clusterSize) {
	int factor = imageSize/256;
	float[] lab = ColorUtil.toLab(cluster.getColor());
	int a = factor*new Float(lab[1]).intValue()+imageSize/2;
	int b = factor*new Float(lab[2]).intValue()+imageSize/2;
	if(!cluster.leaf) {
	    float[] lab1 = ColorUtil.toLab(cluster.leftCluster.getColor());
	    float[] lab2 = ColorUtil.toLab(cluster.rightCluster.getColor());
	    int a1 = factor*new Float(lab1[1]).intValue()+imageSize/2;
	    int b1 = factor*new Float(lab1[2]).intValue()+imageSize/2;
	    int a2 = factor*new Float(lab2[1]).intValue()+imageSize/2;
	    int b2 = factor*new Float(lab2[2]).intValue()+imageSize/2;
	    graphics.setColor(Color.white);
	    graphics.drawLine(a, b, a2, b2);
	    graphics.drawLine(a, b, a1, b1);
	}
	graphics.setColor(cluster.getColor());
	graphics.fillOval(a-clusterSize/2, b-clusterSize/2, clusterSize, clusterSize);
	if(!cluster.leaf) {
	    drawClusterLab(graphics, cluster.leftCluster, imageSize, clusterSize);
	    drawClusterLab(graphics, cluster.rightCluster, imageSize, clusterSize);
	}
    }

    //private ColorGraph graph;
    //public ColorGraph getGraph() {return this.graph;}

    public static void writeSingleLinkageHierarchicalClustering(SingleLinkageHierarchicalClusterer graph, String imageName) {
	ColorUtil.writeImage(graph.buildImage(), imageName);
    }     
}

// TODO inclure les différences de colorspace dans le cluster et la fonction inverse RGB -> LAB
class Cluster {
    public boolean leaf = true;
    public Cluster leftCluster = null;
    public Cluster rightCluster = null;
    public Cluster parentCluster = null;
    public long red;
    public long green;
    public long blue;
    public float l;
    public float a;
    public float b;
    public float x;
    public float y;
    public float z;
    public int level;
    public int size;
    public int depth=0;
    public Cluster(Color c) {
	this.leaf = true;
	this.red = c.getRed();
	this.green = c.getGreen();
	this.blue = c.getBlue();
	float[] xyz = ColorUtil.toXyz(c);
	this.x = xyz[0];
	this.y = xyz[1];
	this.z = xyz[2];
	float[] lab = ColorUtil.toLab(c);
	this.l = lab[0];
	this.a = lab[1];
	this.b = lab[2];
	this.level = 0;
	this.size = 1;
    }
    public Cluster(Cluster cluster1, Cluster cluster2) {
	this.leaf = false;
	this.leftCluster = cluster1;
	this.rightCluster = cluster2;
	this.leftCluster.parentCluster = this;
	this.rightCluster.parentCluster = this;
	this.red = cluster1.red+cluster2.red;
	this.green = cluster1.green+cluster2.green;
	this.blue = cluster1.blue+cluster2.blue;
	this.l = cluster1.l+cluster2.l;
	this.a = cluster1.a+cluster2.a;
	this.b = cluster1.b+cluster2.b;
	this.level = Math.max(cluster1.level, cluster2.level)+1;
	this.size = cluster1.size+cluster2.size;
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
	boolean leafValues = 
	    (ColorUtil.getColorSpace()==ColorUtil.RGB)?
		    (this.red==cluster.red)&&(this.green==cluster.green)&&(this.blue==cluster.blue)&&(this.level==cluster.level)&&(this.size==cluster.size)://&&(this.depth==cluster.depth);
			(this.l==cluster.l)&&(this.a==cluster.a)&&(this.b==cluster.b)&&(this.level==cluster.level)&&(this.size==cluster.size);
		    if (this.leaf) return cluster.leaf&&leafValues;
		    return (!cluster.leaf)&&cluster.leftCluster.equals(this.leftCluster)&&cluster.rightCluster.equals(this.rightCluster)&&leafValues;
    }
    public Color getColor() {
	if (ColorUtil.getColorSpace()==ColorUtil.RGB)
	    return new Color((int)(this.red/(double)this.size),(int)(this.green/(double)this.size),(int)(this.blue/(double)this.size));
	else if (ColorUtil.getColorSpace()==ColorUtil.LAB)
	    return ColorUtil.toColor(new float[]{this.l/this.size,this.a/this.size,this.b/this.size});
	else {
	    return new Color(ColorSpace.getInstance(ColorSpace.CS_CIEXYZ), new float[]{this.x/this.size,this.y/this.size,this.z/this.size}, 1f);
	}
    }
}
