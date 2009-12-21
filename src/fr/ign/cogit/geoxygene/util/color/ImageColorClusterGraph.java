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
			list.add(i);
			clusterMap.put(i, list);
		}
		logger.info(clusterMap.size()+" clusters");
		// merge clusters with identical clusters in its neighborhood
		for(int y = 0 ; y < clusterImage.getHeight() ; y++) {
			for(int x = 0 ; x < clusterImage.getWidth() ; x++) {
				List<Integer> neighbors = identicalNeighbors(clusterImage,x,y,1);
				int index = x+y*clusterImage.getWidth();
				for(Integer neighbor:neighbors) merge(clusterIndices,index,neighbor,clusterMap);
			}
		}
		logger.info(clusterMap.size()+" clusters left");

		Map<Integer,Integer> newColorMap = new HashMap<Integer,Integer>();
		// find cluster colors and build a graph
		for(Entry<Integer,List<Integer>> entry : clusterMap.entrySet()) {
			// select the first pixel in the cluster
			int index = entry.getValue().get(0);
			int x = index%clusterImage.getWidth();
			int y = index/clusterImage.getWidth();
			newColorMap.put(entry.getKey(), clusterImage.getRGB(x, y));
			Vertex v = new Vertex(x,y,new Color(clusterImage.getRGB(x, y)));
			v.count=entry.getValue().size();
			vertices.put(entry.getKey(),v);
		}
		// create edges between clusters
		// find cluster colors and build a graph
		for(Entry<Integer,List<Integer>> entry : clusterMap.entrySet()) {
			Set<Integer> neighborhood = new HashSet<Integer>();
			for (Integer index:entry.getValue()) {
				int x = index%clusterImage.getWidth();
				int y = index/clusterImage.getWidth();
				neighborhood.addAll(neighborhood(clusterImage,x,y,1));
			}
			neighborhood.removeAll(entry.getValue());
			for (Integer index:neighborhood) {
				Edge edge = new Edge(vertices.get(entry.getKey()),vertices.get(clusterIndices[index]));
				edges.add(edge);
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
					neighbors.add(x+i+(y+j)*clusterImage.getWidth());
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
					neighbors.add(x+i+(y+j)*clusterImage.getWidth());
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
		List<Integer> pixels = clusterMap.get(clusterIndices[i2]);
		clusterMap.remove(clusterIndices[i2]);
		for(Integer i : pixels) clusterIndices[i]=clusterIndices[i1];
		/*
	    logger.info(clusterIndices[i1]);
	    logger.info(clusterMap.get(clusterIndices[i1]));
	    logger.info(pixels);
		 */
		clusterMap.get(clusterIndices[i1]).addAll(pixels);
		clusterIndices[i2]=clusterIndices[i1];
	}

	/**
	 * @param colorGraph
	 */
	@SuppressWarnings("unchecked")
	private void stats() {
		int nbPixels = image.getWidth()*image.getHeight();
		colorVertices = new HashMap<Color,List<Vertex>>();
		Map<Color, Integer> occurrenceMap = ColorUtil.occurrenceMap(image);
		List<float[]> sizes = new ArrayList<float[]>();
		for(Color color:occurrenceMap.keySet()) {
			List<Vertex> newColorVertices = new ArrayList<Vertex>();
			int countSum = 0;
			for(Vertex vertex:vertices.values()) if (vertex.color.equals(color)) {
				newColorVertices.add(vertex);
				countSum+=vertex.count;
				clusterSize.add(vertex.count);
				if (vertex.count>10) sizes.add(new float[]{vertex.count});
			}
			colorVertices.put(color,newColorVertices);
			colorNumberOfClusters.put(color,newColorVertices.size());
			colorClusterSizeSum.add(countSum);
			double clusterAverageSize = countSum/(double)newColorVertices.size();
			colorClusterAverageSize.put(color,clusterAverageSize);
			colorProportion.put(color,100*occurrenceMap.get(color)/(double)nbPixels);
			totalCountSum+=countSum;
			totalNumberOfClusters+=newColorVertices.size();
		}
		clusterSizeAverage = totalCountSum/(double)totalNumberOfClusters;
		clusterSizeStandardDeviation=MathUtil.ecartType(clusterSize, clusterSizeAverage);
		logger.info("The average cluster is "+clusterSizeAverage+" pixels large");
		logger.info("The standard deviation in the cluster size is "+clusterSizeStandardDeviation +" pixels");
		
		KMeansClusterer clusterer = new KMeansClusterer(numberOfClusters,sizes);
		clusterSizes = new float[numberOfClusters];
		int i = 0;
		for(KMeansClusterer.Cluster cluster:clusterer.getClusters()) {
			clusterSizes[i++]=cluster.getLocation()[0];
		}
		Arrays.sort(clusterSizes);
		for (float size : clusterSizes) logger.info("size "+size);
		
		clusterColors = new List[clusterSizes.length];
		
		for(Color color:occurrenceMap.keySet()) {
			logger.info(colorNumberOfClusters.get(color)+" clusters for color "+Integer.toHexString(color.getRGB())+" with an average of "+colorClusterAverageSize.get(color)+" pixels per cluster    ---   "+occurrenceMap.get(color)+" pixels   --- "+colorProportion.get(color)+" %");
			int[] listNumbers = getNumbers(colorVertices.get(color));
			int[] listSurfaces = getSurfaces(colorVertices.get(color));
			logger.info("It is made of:");
			for (int index = 0 ; index < clusterSizes.length ; index++) {
				logger.info(listNumbers[index]+" clusters with a total area of "+listSurfaces[index]);
				if (listNumbers[index]>0) {
					if (clusterColors[index]==null) clusterColors[index]=new ArrayList<Color>();
					clusterColors[index].add(color);
				}
			}
		}
		Map<Integer,Integer> sizeMap = new HashMap<Integer,Integer>();
		for (Vertex vertex:vertices.values()) {
			int count = vertex.count/10;
			if (sizeMap.get(count)==null) sizeMap.put(count, 1);
			else sizeMap.put(count, sizeMap.get(count)+1);
		}
		Integer[] sizeList = sizeMap.keySet().toArray(new Integer[0]);
		Arrays.sort(sizeList);
		int maxOccurence = 0;
		for(Integer occurence:sizeMap.values()) maxOccurence=Math.max(maxOccurence, occurence);
		
		logger.info(sizeList[sizeList.length-1]+" x "+maxOccurence);
		BufferedImage graphImage = new BufferedImage(sizeList[sizeList.length-1], maxOccurence/10,BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = graphImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		graphics.setStroke(new BasicStroke(1.0f));
		graphics.setColor(Color.white);
		for(Entry<Integer,Integer> entry:sizeMap.entrySet()) graphics.drawLine(entry.getKey(), maxOccurence/10-1, entry.getKey(), maxOccurence/10-1-entry.getValue()/10);
		//ColorUtil.writeImage(graphImage, "/home/julien/Desktop/occurenceMap.png");
	}
	/**
	 * @param list
	 * @param clusterSizes
	 * @return
	 */
	private int[] getSurfaces(List<Vertex> list) {
		int[] surfaces = new int[clusterSizes.length];
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
		int[] numbers = new int[clusterSizes.length];
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
		for(int index = 0 ; index < clusterSizes.length ; index++) {
			float size=clusterSizes[index];
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
	 * @return
	 */
	public BufferedImage buildGraphImage() {
		int vertexSize = 1;
		BufferedImage graphImage = new BufferedImage(image.getWidth()*vertexSize, image.getHeight()*vertexSize,BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = graphImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		graphics.setStroke(new BasicStroke(1.0f));
		for(int y = 0 ; y < image.getHeight() ; y++) {
			for(int x = 0 ; x < image.getWidth() ; x++) {
				Color color = new Color(image.getRGB(x, y));
				graphics.setColor(color);
				graphics.fillRect(x*vertexSize, y*vertexSize, vertexSize, vertexSize);
			}
		}
		graphics.setColor(Color.white);
		logger.info("drawing "+edges.size()+" edges");
		for(Edge e:edges) {
			graphics.drawLine(e.initialVertex.x*vertexSize+vertexSize/2, e.initialVertex.y*vertexSize+vertexSize/2, e.finalVertex.x*vertexSize+vertexSize/2, e.finalVertex.y*vertexSize+vertexSize/2);
		}
		logger.info("drawing "+vertices.values().size()+" vertices");
		for(Vertex v:vertices.values()) {
			graphics.setColor(v.color);
			//int countSize = 2*(v.count-1);
			graphics.fillOval(v.x*vertexSize, v.y*vertexSize, vertexSize-1, vertexSize-1);
			graphics.setColor(Color.black);
			graphics.drawOval(v.x*vertexSize, v.y*vertexSize, vertexSize-1, vertexSize-1);
			graphics.drawString(""+v.count, v.x*vertexSize, v.y*vertexSize+vertexSize-1);

		}
		logger.info("drawing finished");
		return graphImage;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public BufferedImage buildGraphClusterImage() {
		int maxNumberOfColorsPerCluster = 0;
		float sizeOfMaxCluster = 0f;
		Map<Color,float[]> averageSizePerColor = new HashMap<Color,float[]>();
		for (Color color:colorVertices.keySet()) {
			List<Vertex> theVertices = colorVertices.get(color);
			List<Vertex>[] clusterVertices = new List[numberOfClusters];
			for(int index = 0 ; index<numberOfClusters ; index++) clusterVertices[index]=new ArrayList<Vertex>();
			for(Vertex v:theVertices) {
				int index = getIndexOfClosestCluster(v.count);
				clusterVertices[index].add(v);
			}
			float[] averageSize = new float[numberOfClusters];
			for(int i = 0 ; i < numberOfClusters ; i++) {
				float totalSize = 0f;
				for(Vertex v:clusterVertices[i]) totalSize+=v.count;
				if (totalSize==0f) continue;
				totalSize/=clusterVertices[i].size();
				averageSize[i]=totalSize;
				sizeOfMaxCluster = Math.max(sizeOfMaxCluster, totalSize);
			}
			averageSizePerColor.put(color, averageSize);
		}
		for(List<Color> list:clusterColors) maxNumberOfColorsPerCluster=Math.max(maxNumberOfColorsPerCluster, list.size());
		
		logger.info("sizeOfMaxCluster = "+sizeOfMaxCluster);
		logger.info("maxNumberOfColorsPerCluster = "+maxNumberOfColorsPerCluster);
		
		int[][][] positionAndSizeArray = new int[numberOfClusters][][];
		int maxIndex = 0;
		for(int i = 0 ; i < numberOfClusters ; i++) {
			int index = 0;
			List<Color> list = clusterColors[i];
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
		
		logger.info("Creating image of "+maxIndex*factor+" x "+(int)sizeOfMaxCluster*numberOfClusters*factor);
		
		BufferedImage graphImage = new BufferedImage((int)(maxIndex*factor), (int)(sizeOfMaxCluster*numberOfClusters*factor),BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = graphImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		graphics.setStroke(new BasicStroke(1.0f));

		for (int i = 0 ; i < positionAndSizeArray.length ; i++)
			for (int j = 0 ; j < positionAndSizeArray[i].length ; j++) {
				graphics.setColor(clusterColors[i].get(j));
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
