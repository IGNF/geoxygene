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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author Julien Perret
 *
 */
public class KMeansClusterer {
    static Logger logger=Logger.getLogger(KMeansClusterer.class.getName());
    private int numberOfClusters;
    private List<float[]> data;
    private List<Cluster> clusters = new ArrayList<Cluster>();
    public List<Cluster> getClusters() {return this.clusters;}

    public KMeansClusterer(int theNumberOfClusters, List<float[]> theData) {
	this.numberOfClusters=theNumberOfClusters;
	this.data=theData;
	long t = System.currentTimeMillis();
	// randomly guess the initial cluster locations
	int index = new Double(this.data.size()*Math.random()).intValue();
	this.clusters.add(new Cluster(this.data.get(index)));
	for(int count = 0 ; count < this.numberOfClusters-1 ; count++) {
	    // find the furthest data point
	    float maxDistance = 0f;
	    for(int i = 0 ; i < this.data.size() ; i++) {
		float distance = sqDistanceFromClusters(this.data.get(i));
		if (distance>maxDistance) {
		    maxDistance=distance;
		    index=i;
		}
	    }
	    this.clusters.add(new Cluster(this.data.get(index)));
	}
	boolean hasChanged = assignDataPointsToClusters();
	while (hasChanged) hasChanged = assignDataPointsToClusters();
	t = System.currentTimeMillis()-t;
	logger.info("KMeansClusterer with "+this.numberOfClusters+" clusters and "+this.data.size()+" data points took "+t+" ms to finish");
    }

    /**
     * 
     */
    private boolean  assignDataPointsToClusters() {
	boolean hasChanged = false;
	for(float[] dataPoint:this.data) {
	    float minDistance = Float.MAX_VALUE;
	    Cluster closesCluster=null;
	    for(Cluster cluster:this.clusters) {
		float distance = sqDistance(dataPoint,cluster.location);
		if (distance<minDistance) {
		    minDistance = distance;
		    closesCluster=cluster;
		}
	    }
	    if (closesCluster!=null) {
		if (!closesCluster.dataPoints.contains(dataPoint)) {
		    closesCluster.dataPoints.add(dataPoint);
		    hasChanged=true;
		}
	    }
	}
	// find the centroid of each cluster
	if (hasChanged) for(Cluster cluster:this.clusters) cluster.computeCentroid();
	return hasChanged;
    }

    /**
     * @param fs
     * @return
     */
    private float sqDistanceFromClusters(float[] fs) {
	float minDistance = Float.MAX_VALUE;
	for(Cluster cluster:this.clusters) minDistance = Math.min(minDistance, sqDistance(fs,cluster.location));
	return minDistance;
    }

    /**
     * @param fs
     * @param location
     * @return
     */
    private float sqDistance(float[] fs, float[] location) {
	float distance = 0f;
	for( int i = 0 ; i < location.length ; i++ ) distance+=(location[i]-fs[i])*(location[i]-fs[i]);
	return distance;
    }

    class Cluster {
	float[] location;
	public float[] getLocation() {return this.location;}
	Set<float[]> dataPoints = new HashSet<float[]>();
	public Cluster(float[] theLocation) {this.location=theLocation;}
	/**
	 * 
	 */
	public void computeCentroid() {
	    for(int i = 0 ; i < this.location.length ; i++) this.location[i]=0;
	    for(float[] dataPoint:this.dataPoints) for(int i = 0 ; i < this.location.length ; i++) this.location[i]+=dataPoint[i]/this.dataPoints.size();
	}
    }
}
