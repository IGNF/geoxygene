/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.color;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.util.algo.MathUtil;

/**
 * @author Julien Perret
 *
 */
public class ImageColorClusterGraph {
	static Logger logger=Logger.getLogger(ImageColorClusterGraph.class.getName());

	Map<Integer,Vertex> vertices = new HashMap<Integer,Vertex>();
	List<Edge> edges  = new ArrayList<Edge>();
	int totalCountSum = 0;
	int totalNumberOfClusters = 0;
	List<Integer> clusterSize = new ArrayList<Integer>();
	List<Integer> colorClusterSizeSum = new ArrayList<Integer>();
	Map<Color,Double> colorClusterAverageSize = new HashMap<Color,Double>();
	Map<Color,Double> colorProportion = new HashMap<Color,Double>();
	Map<Color,Integer> colorNumberOfClusters = new HashMap<Color,Integer>();
	double clusterSizeAverage = 0;
	double clusterSizeStandardDeviation=0;
	float[] clusterSizes;
	Map<Color,List<Vertex>> colorVertices;
	List<Color>[] clusterColors;
	int numberOfClusters = 4;

	//private Color colorLookUpTable[];
	BufferedImage image;

	public ImageColorClusterGraph(BufferedImage clusterImage) {
		long t = System.currentTimeMillis();
		this.image=clusterImage;
		int nbPixels = clusterImage.getWidth()*clusterImage.getHeight();
		int[] clusterIndices = new int[nbPixels];
		Map<Integer,List<Integer>> clusterMap = new HashMap<Integer,List<Integer>>();
		// create one cluster per pixel
		for(int i = 0 ; i < nbPixels ; i++) {
			clusterIndices[i] = i;
			List<Integer> list = new ArrayList<Integer>();
			list.add(new Integer(i));
			clusterMap.put(new Integer(i), list);
		}
		logger.info(clusterMap.size()+" clusters");
		// merge clusters with identical clusters in its neighborhood
		for(int y = 0 ; y < clusterImage.getHeight() ; y++) {
			for(int x = 0 ; x < clusterImage.getWidth() ; x++) {
				List<Integer> neighbors = identicalNeighbors(clusterImage,x,y,1);
				int index = x+y*clusterImage.getWidth();
				for(Integer neighbor:neighbors) merge(clusterIndices,index,neighbor.intValue(),clusterMap);
			}
		}
		logger.info(clusterMap.size()+" clusters left");

		Map<Integer,Integer> newColorMap = new HashMap<Integer,Integer>();
		// find cluster colors and build a graph
		for(Entry<Integer,List<Integer>> entry : clusterMap.entrySet()) {
			// select the first pixel in the cluster
			int index = entry.getValue().get(0).intValue();
			int x = index%clusterImage.getWidth();
			int y = index/clusterImage.getWidth();
			newColorMap.put(entry.getKey(), new Integer(clusterImage.getRGB(x, y)));
			Vertex v = new Vertex(x,y,new Color(clusterImage.getRGB(x, y)));
			v.count=entry.getValue().size();
			this.vertices.put(entry.getKey(),v);
		}
		// create edges between clusters
		// find cluster colors and build a graph
		for(Entry<Integer,List<Integer>> entry : clusterMap.entrySet()) {
			Set<Integer> neighborhood = new HashSet<Integer>();
			for (Integer index:entry.getValue()) {
				int x = index.intValue()%clusterImage.getWidth();
				int y = index.intValue()/clusterImage.getWidth();
				neighborhood.addAll(neighborhood(clusterImage,x,y,1));
			}
			neighborhood.removeAll(entry.getValue());
			for (Integer index:neighborhood) {
				Edge edge = new Edge(this.vertices.get(entry.getKey()),this.vertices.get(new Integer(clusterIndices[index.intValue()])));
				this.edges.add(edge);
			}
		}
		logger.info("Clustering finished in "+(System.currentTimeMillis()-t)+" ms");
		t = System.currentTimeMillis();
		stats();
		logger.info("End of Stats in "+(System.currentTimeMillis()-t)+" ms");
	}

	/**
	 * @param clusterImage
	 * @param x
	 * @param y
	 * @return
	 */
	private List<Integer> identicalNeighbors(BufferedImage clusterImage, int x, int y, int neighborhoodSize) {
		List<Integer> neighbors = new ArrayList<Integer>();
		for (int i = -neighborhoodSize ; i <= neighborhoodSize ; i++) {
			for (int j = -neighborhoodSize ; j <= neighborhoodSize ; j++) {
				if ((i==0)&&(j==0)) continue;
				if (
						(x+i>=0) && (x+i<clusterImage.getWidth()) &&
						(y+j>=0) && (y+j<clusterImage.getHeight()) &&
						(clusterImage.getRGB(x, y) == clusterImage.getRGB(x+i, y+j)) )
					neighbors.add(new Integer(x+i+(y+j)*clusterImage.getWidth()));
			}
		}
		return neighbors;
	}

	private List<Integer> neighborhood(BufferedImage clusterImage, int x, int y, int neighborhoodSize) {
		List<Integer> neighbors = new ArrayList<Integer>();
		for (int i = -neighborhoodSize ; i <= neighborhoodSize ; i++) {
			for (int j = -neighborhoodSize ; j <= neighborhoodSize ; j++) {
				if ((i==0)&&(j==0)) continue;
				if (
						(x+i>=0) && (x+i<clusterImage.getWidth()) &&
						(y+j>=0) && (y+j<clusterImage.getHeight()) )
					neighbors.add(new Integer(x+i+(y+j)*clusterImage.getWidth()));
			}
		}
		return neighbors;
	}

	/*
    private Integer bestNeighbor(BufferedImage clusterImage, int x, int y, int neighborhoodSize) {
	Map<Integer,Integer> neighborArity = new HashMap<Integer,Integer>();
	for (int i = -neighborhoodSize ; i <= neighborhoodSize ; i++) {
	    for (int j = -neighborhoodSize ; j <= neighborhoodSize ; j++) {
		if ((i==0)&&(j==0)) continue;
		if (
			(x+i>=0) && (x+i<clusterImage.getWidth()) &&
			(y+j>=0) && (y+j<clusterImage.getHeight()) &&
			(clusterImage.getRGB(x, y) != clusterImage.getRGB(x+i, y+j)) ) {
		    int neighborIndex = x+i+(y+j)*clusterImage.getWidth();
		    if (neighborArity.get(neighborIndex)==null) neighborArity.put(neighborIndex,1);
		    else neighborArity.put(neighborIndex, neighborArity.get(neighborIndex)+1);
		}
	    }
	}
	int maxValue = 0;
	Integer max=0;
	for (Entry<Integer,Integer> neighborEntry:neighborArity.entrySet()) {
	    if(neighborEntry.getValue()>maxValue) {
		maxValue = neighborEntry.getValue();
		max = neighborEntry.getKey();
	    }
	}
	return max;
    }
	 */

	private boolean merge(int[] clusterIndices, int i1, int i2, Map<Integer,List<Integer>> clusterMap) {
		if (clusterIndices[i1]<clusterIndices[i2]) replaceIndices(clusterIndices, i1, i2, clusterMap);
		else if (clusterIndices[i1]>clusterIndices[i2]) replaceIndices(clusterIndices, i2, i1, clusterMap);
		else return false;
		return true;
	}

	/**
	 * @param clusterIndices
	 * @param i1
	 * @param i2
	 * @param clusterMap
	 */
	private void replaceIndices(int[] clusterIndices, int i1, int i2, Map<Integer, List<Integer>> clusterMap) {
		List<Integer> pixels = clusterMap.get(new Integer(clusterIndices[i2]));
		clusterMap.remove(new Integer(clusterIndices[i2]));
		for(Integer i : pixels) clusterIndices[i.intValue()]=clusterIndices[i1];
		/*
	    logger.info(clusterIndices[i1]);
	    logger.info(clusterMap.get(clusterIndices[i1]));
	    logger.info(pixels);
		 */
		clusterMap.get(new Integer(clusterIndices[i1])).addAll(pixels);
		clusterIndices[i2]=clusterIndices[i1];
	}

	/**
	 * @param colorGraph
	 */
	@SuppressWarnings("unchecked")
	private void stats() {
		int nbPixels = this.image.getWidth()*this.image.getHeight();
		this.colorVertices = new HashMap<Color,List<Vertex>>();
		Map<Color, Integer> occurrenceMap = ColorUtil.occurrenceMap(this.image);
		List<float[]> sizes = new ArrayList<float[]>();
		for(Color color:occurrenceMap.keySet()) {
			List<Vertex> newColorVertices = new ArrayList<Vertex>();
			int countSum = 0;
			for(Vertex vertex:this.vertices.values()) if (vertex.color.equals(color)) {
				newColorVertices.add(vertex);
				countSum+=vertex.count;
				this.clusterSize.add(new Integer(vertex.count));
				if (vertex.count>10) sizes.add(new float[]{vertex.count});
			}
			this.colorVertices.put(color,newColorVertices);
			this.colorNumberOfClusters.put(color,new Integer(newColorVertices.size()));
			this.colorClusterSizeSum.add(new Integer(countSum));
			double clusterAverageSize = countSum/(double)newColorVertices.size();
			this.colorClusterAverageSize.put(color,new Double(clusterAverageSize));
			this.colorProportion.put(color,new Double(100*occurrenceMap.get(color).intValue()/(double)nbPixels));
			this.totalCountSum+=countSum;
			this.totalNumberOfClusters+=newColorVertices.size();
		}
		this.clusterSizeAverage = this.totalCountSum/(double)this.totalNumberOfClusters;
		this.clusterSizeStandardDeviation=MathUtil.ecartType(this.clusterSize, this.clusterSizeAverage);
		logger.info("The average cluster is "+this.clusterSizeAverage+" pixels large");
		logger.info("The standard deviation in the cluster size is "+this.clusterSizeStandardDeviation +" pixels"); //$NON-NLS-2$

		KMeansClusterer clusterer = new KMeansClusterer(this.numberOfClusters,sizes);
		this.clusterSizes = new float[this.numberOfClusters];
		int i = 0;
		for(KMeansClusterer.Cluster cluster:clusterer.getClusters()) {
			this.clusterSizes[i++]=cluster.getLocation()[0];
		}
		Arrays.sort(this.clusterSizes);
		for (float size : this.clusterSizes) logger.info("size "+size);

		this.clusterColors = new List[this.clusterSizes.length];

		for(Color color:occurrenceMap.keySet()) {
			logger.info(this.colorNumberOfClusters.get(color)+" clusters for color "+Integer.toHexString(color.getRGB())+" with an average of "+this.colorClusterAverageSize.get(color)+" pixels per cluster    ---   "+occurrenceMap.get(color)+" pixels   --- "+this.colorProportion.get(color)+" %");
			int[] listNumbers = getNumbers(this.colorVertices.get(color));
			int[] listSurfaces = getSurfaces(this.colorVertices.get(color));
			logger.info("It is made of:");
			for (int index = 0 ; index < this.clusterSizes.length ; index++) {
				logger.info(listNumbers[index]+" clusters with a total area of "+listSurfaces[index]);
				if (listNumbers[index]>0) {
					if (this.clusterColors[index]==null) this.clusterColors[index]=new ArrayList<Color>();
					this.clusterColors[index].add(color);
				}
			}
		}
		Map<Integer,Integer> sizeMap = new HashMap<Integer,Integer>();
		for (Vertex vertex:this.vertices.values()) {
			int count = vertex.count/10;
			if (sizeMap.get(new Integer(count))==null) sizeMap.put(new Integer(count), new Integer(1));
			else sizeMap.put(new Integer(count), new Integer(sizeMap.get(new Integer(count)).intValue()+1));
		}
		Integer[] sizeList = sizeMap.keySet().toArray(new Integer[0]);
		Arrays.sort(sizeList);
		int maxOccurence = 0;
		for(Integer occurence:sizeMap.values()) maxOccurence=Math.max(maxOccurence, occurence.intValue());

		logger.info(sizeList[sizeList.length-1]+" x "+maxOccurence);
		BufferedImage graphImage = new BufferedImage(sizeList[sizeList.length-1].intValue(), maxOccurence/10,BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = graphImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		graphics.setStroke(new BasicStroke(1.0f));
		graphics.setColor(Color.white);
		for(Entry<Integer,Integer> entry:sizeMap.entrySet()) graphics.drawLine(entry.getKey().intValue(), maxOccurence/10-1, entry.getKey().intValue(), maxOccurence/10-1-entry.getValue().intValue()/10);
		//ColorUtil.writeImage(graphImage, "/home/julien/Desktop/occurenceMap.png");
	}
	/**
	 * @param list
	 * @param clusterSizes
	 * @return
	 */
	private int[] getSurfaces(List<Vertex> list) {
		int[] surfaces = new int[this.clusterSizes.length];
		for(Vertex vertex:list) {
			int index = getIndexOfClosestCluster(vertex.count);
			surfaces[index]+=vertex.count;
		}
		return surfaces;
	}

	/**
	 * @param list
	 * @param clusterSizes
	 * @return
	 */
	private int[] getNumbers(List<Vertex> list) {
		int[] numbers = new int[this.clusterSizes.length];
		for(Vertex vertex:list) {
			int index = getIndexOfClosestCluster(vertex.count);
			numbers[index]++;
		}
		return numbers;
	}

	/**
	 * @param count
	 * @param clusterSizes
	 * @return
	 */
	private int getIndexOfClosestCluster(int count) {
	    float minDistance = Float.MAX_VALUE;
	    int closestClusterIndex=0;
		for(int index = 0 ; index < this.clusterSizes.length ; index++) {
			float size=this.clusterSizes[index];
			float distance = size-count;
			distance*=distance;
			if (distance<minDistance) {
			    minDistance = distance;
			    closestClusterIndex=index;
			}
		}
		return closestClusterIndex;
	}

	/**
	 * @return the graph image
	 */
	public BufferedImage buildGraphImage() {
		int vertexSize = 1;
		BufferedImage graphImage = new BufferedImage(this.image.getWidth()*vertexSize, this.image.getHeight()*vertexSize,BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = graphImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		graphics.setStroke(new BasicStroke(1.0f));
		for(int y = 0 ; y < this.image.getHeight() ; y++) {
			for(int x = 0 ; x < this.image.getWidth() ; x++) {
				Color color = new Color(this.image.getRGB(x, y));
				graphics.setColor(color);
				graphics.fillRect(x*vertexSize, y*vertexSize, vertexSize, vertexSize);
			}
		}
		graphics.setColor(Color.white);
		logger.info("drawing "+this.edges.size()+" edges");
		for(Edge e:this.edges) {
			graphics.drawLine(e.initialVertex.x*vertexSize+vertexSize/2, e.initialVertex.y*vertexSize+vertexSize/2, e.finalVertex.x*vertexSize+vertexSize/2, e.finalVertex.y*vertexSize+vertexSize/2);
		}
		logger.info("drawing "+this.vertices.values().size()+" vertices");
		for(Vertex v:this.vertices.values()) {
			graphics.setColor(v.color);
			//int countSize = 2*(v.count-1);
			graphics.fillOval(v.x*vertexSize, v.y*vertexSize, vertexSize-1, vertexSize-1);
			graphics.setColor(Color.black);
			graphics.drawOval(v.x*vertexSize, v.y*vertexSize, vertexSize-1, vertexSize-1);
			graphics.drawString(""+v.count, v.x*vertexSize, v.y*vertexSize+vertexSize-1); //$NON-NLS-1$

		}
		logger.info("drawing finished");
		return graphImage;
	}

	/**
	 * @return the cluster image
	 */
	@SuppressWarnings("unchecked")
	public BufferedImage buildGraphClusterImage() {
		int maxNumberOfColorsPerCluster = 0;
		float sizeOfMaxCluster = 0f;
		Map<Color,float[]> averageSizePerColor = new HashMap<Color,float[]>();
		for (Color color:this.colorVertices.keySet()) {
			List<Vertex> theVertices = this.colorVertices.get(color);
			List<Vertex>[] clusterVertices = new List[this.numberOfClusters];
			for(int index = 0 ; index<this.numberOfClusters ; index++) clusterVertices[index]=new ArrayList<Vertex>();
			for(Vertex v:theVertices) {
				int index = getIndexOfClosestCluster(v.count);
				clusterVertices[index].add(v);
			}
			float[] averageSize = new float[this.numberOfClusters];
			for(int i = 0 ; i < this.numberOfClusters ; i++) {
				float totalSize = 0f;
				for(Vertex v:clusterVertices[i]) totalSize+=v.count;
				if (totalSize==0f) continue;
				totalSize/=clusterVertices[i].size();
				averageSize[i]=totalSize;
				sizeOfMaxCluster = Math.max(sizeOfMaxCluster, totalSize);
			}
			averageSizePerColor.put(color, averageSize);
		}
		for(List<Color> list:this.clusterColors) maxNumberOfColorsPerCluster=Math.max(maxNumberOfColorsPerCluster, list.size());

		logger.info("sizeOfMaxCluster = "+sizeOfMaxCluster);
		logger.info("maxNumberOfColorsPerCluster = "+maxNumberOfColorsPerCluster);

		int[][][] positionAndSizeArray = new int[this.numberOfClusters][][];
		int maxIndex = 0;
		for(int i = 0 ; i < this.numberOfClusters ; i++) {
			int index = 0;
			List<Color> list = this.clusterColors[i];
			logger.info(list.size()+" colors for "+i);
			positionAndSizeArray[i]=new int[list.size()][];
			for(int j = 0 ; j < list.size() ; j++) {
				Color color = list.get(j);
				float averageSize = averageSizePerColor.get(color)[i];
				logger.info("averageSize =  "+averageSize);
				positionAndSizeArray[i][j] = new int[3];
				positionAndSizeArray[i][j][0]=index;
				positionAndSizeArray[i][j][1]=(int)(i*sizeOfMaxCluster);
				positionAndSizeArray[i][j][2]=(int)averageSize-1;
				index+=averageSize;
			}
			maxIndex=Math.max(maxIndex, index);
		}

		float factor = 1024f/maxIndex;

		logger.info("Creating image of "+maxIndex*factor+" x "+(int)sizeOfMaxCluster*this.numberOfClusters*factor);

		BufferedImage graphImage = new BufferedImage((int)(maxIndex*factor), (int)(sizeOfMaxCluster*this.numberOfClusters*factor),BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = graphImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		graphics.setStroke(new BasicStroke(1.0f));

		for (int i = 0 ; i < positionAndSizeArray.length ; i++)
			for (int j = 0 ; j < positionAndSizeArray[i].length ; j++) {
				graphics.setColor(this.clusterColors[i].get(j));
				graphics.fillOval((int)(positionAndSizeArray[i][j][0]*factor), (int)(positionAndSizeArray[i][j][1]*factor), (int)(positionAndSizeArray[i][j][2]*factor), (int)(positionAndSizeArray[i][j][2]*factor));
				graphics.setColor(Color.black);
				graphics.drawOval((int)(positionAndSizeArray[i][j][0]*factor), (int)(positionAndSizeArray[i][j][1]*factor), (int)(positionAndSizeArray[i][j][2]*factor), (int)(positionAndSizeArray[i][j][2]*factor));
			}
		return graphImage;
	}


	class Vertex {
		int x = 0;
		int y = 0;
		Color color = null;
		int count = 0;
		public List<Edge> vertexEdges = new ArrayList<Edge>();
		public Vertex(int newx, int newy, Color c) {
			this.x=newx;
			this.y=newy;
			this.color=c;
			this.count=1;
		}
	}

	class Edge {
		public Vertex initialVertex = null;
		public Vertex finalVertex = null;
		public Edge(Vertex ini, Vertex fin) {
			this.initialVertex=ini;
			this.finalVertex=fin;
			this.initialVertex.vertexEdges.add(this);
			this.finalVertex.vertexEdges.add(this);
		}
	}
}
