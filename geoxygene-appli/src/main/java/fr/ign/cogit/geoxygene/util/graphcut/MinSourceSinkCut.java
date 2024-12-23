package fr.ign.cogit.geoxygene.util.graphcut;

/*
 * ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 * 
 * Project Info: http://jgrapht.sourceforge.net/
 * Project Creator: Barak Naveh (http://sourceforge.net/users/barak_naveh)
 * 
 * (C) Copyright 2003-2012, by Barak Naveh and Contributors.
 * 
 * This program and the accompanying materials are dual-licensed under
 * either
 * 
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 * 
 * or (per the licensee's choosing)
 * 
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
/*
 * -------------------------
 * MinSourceSinkCut.java
 * -------------------------
 * (C) Copyright 2012-2012, by Joris Kinable and Contributors.
 * 
 * Original Author: Joris Kinable
 * Contributor(s):
 * 
 * Changes
 * -------
 * 26-Nov-2012 : Initial revision (JK);
 */
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;

/**
 * Given a directed, weighted graph G(V,E). This class computes a minimum s-t
 * cut. For this, it relies on the EdmondsKarpMaximumFlow implementation. Note:
 * it is not recommended to use this class to calculate the overall minimum cut
 * in a graph by iteratively invoking this class for all source-sink pairs. This
 * is computationally expensive. Instead, use the StoerWagnerMinimumCut
 * implementation.
 * 
 * @author Joris Kinable
 */
public class MinSourceSinkCut<V, E> {

    // FIXME: public/package
    public EdmondsKarpMaximumFlow<V, E> ekMaxFlow;
    Set<V> minCut = null;
    DirectedGraph<V, E> graph;
    double cutWeight;
    V source = null;
    V sink = null;
    double epsilon = EdmondsKarpMaximumFlow.DEFAULT_EPSILON;

    public MinSourceSinkCut(DirectedGraph<V, E> graph) {
        this.ekMaxFlow = new EdmondsKarpMaximumFlow<V, E>(graph);
        this.graph = graph;
    }

    public MinSourceSinkCut(DirectedGraph<V, E> graph, double epsilon) {
        this.ekMaxFlow = new EdmondsKarpMaximumFlow<V, E>(graph);
        this.graph = graph;
        this.epsilon = epsilon;
    }

    /**
     * Compute a minimum s-t cut
     * 
     * @param source
     * @param sink
     */
    public void computeMinCut(V source, V sink) {
        this.source = source;
        this.sink = sink;
        this.minCut = new HashSet<V>();

        //First compute a maxFlow from source to sink
        this.ekMaxFlow.calculateMaximumFlow(source, sink);
        this.cutWeight = this.ekMaxFlow.getMaximumFlowValue();
        Map<E, Double> maxFlow = this.ekMaxFlow.getMaximumFlow();

        Queue<V> processQueue = new LinkedList<V>();
        processQueue.add(source);

        while (!processQueue.isEmpty()) {
            V vertex = processQueue.remove();
            if (this.minCut.contains(vertex)) {
                continue;
            } else {
                this.minCut.add(vertex);
            }

            //1. Get the forward edges with residual capacity
            Set<E> outEdges = new HashSet<E>(this.graph.outgoingEdgesOf(vertex));
            for (Iterator<E> it = outEdges.iterator(); it.hasNext();) {
                E edge = it.next();
                double edgeCapacity = this.graph.getEdgeWeight(edge);
                double flowValue = maxFlow.get(edge);
                if (Math.abs(edgeCapacity - flowValue) <= this.epsilon) { //No residual capacity on the edge
                    it.remove();
                }
            }
            for (E edge : outEdges) {
                processQueue.add(Graphs.getOppositeVertex(this.graph, edge, vertex));
            }

            //2. Get the backward edges with non-zero flow
            Set<E> inEdges = new HashSet<E>(this.graph.incomingEdgesOf(vertex));
            for (Iterator<E> it = inEdges.iterator(); it.hasNext();) {
                E edge = it.next();

                //double edgeCapacity=graph.getEdgeWeight(edge);
                double flowValue = maxFlow.get(edge);
                if (flowValue <= this.epsilon) { //There is no flow on this edge
                    it.remove();
                }
            }
            for (E edge : inEdges) {
                processQueue.add(Graphs.getOppositeVertex(this.graph, edge, vertex));
            }
        }
    }

    /**
     * @return Returns the min cut partition containing the source, or null if
     *         there was no call to computeMinCut(V source, V sink)
     */
    public Set<V> getSourcePartition() {
        return Collections.unmodifiableSet(this.minCut);
    }

    /**
     * Returns the min cut partition containing the sink
     * 
     * @return returns the min cut partition containing the sink
     */
    public Set<V> getSinkPartition() {
        if (this.minCut == null) {
            return null;
        }
        Set<V> set = new HashSet<V>(this.graph.vertexSet());
        set.removeAll(this.minCut);
        return Collections.unmodifiableSet(set);
    }

    /**
     * Get the cut weight. This is equal to the max s-t flow
     * 
     * @return cut weight
     */
    public double getCutWeight() {
        return this.cutWeight;
    }

    /**
     * Let S be the set containing the source, and T be the set containing the
     * sink, i.e. T=V\S. This method returns the edges which have their tail in
     * S, and their head in T
     * 
     * @return all edges which have their tail in S, and their head in T. If
     *         computeMinCut(V source, V sink) has not been invoked, this method
     *         returns
     *         null.
     */
    public Set<E> getCutEdges() {
        if (this.minCut == null) {
            return null;
        }
        Set<E> cutEdges = new HashSet<E>();
        for (V vertex : this.minCut) {
            for (E edge : this.graph.outgoingEdgesOf(vertex)) {
                if (!this.minCut.contains(Graphs.getOppositeVertex(this.graph, edge, vertex))) {
                    cutEdges.add(edge);
                }
            }
        }
        return Collections.unmodifiableSet(cutEdges);
    }

    /**
     * Returns the source of the last call
     * 
     * @return source of last minCut call, null if there was no call
     */
    public V getCurrentSource() {
        return this.source;
    }

    /**
     * Returns the sink of the last call
     * 
     * @return sink of last minCut call, null if there was no call
     */
    public V getCurrentSink() {
        return this.sink;
    }
}

// End MinSourceSinkCut.java