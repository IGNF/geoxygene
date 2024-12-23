package fr.ign.cogit.geoxygene.util.graphcut;

/*
 * ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 * 
 * Project Info: http://jgrapht.sourceforge.net/
 * Project Creator: Barak Naveh (http://sourceforge.net/users/barak_naveh)
 * 
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
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
 * -----------------
 * EdmondsKarpMaximumFlow.java
 * -----------------
 * (C) Copyright 2008-2008, by Ilya Razenshteyn and Contributors.
 * 
 * Original Author: Ilya Razenshteyn
 * Contributor(s): -
 * 
 * $Id$
 * 
 * Changes
 * -------
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.jgrapht.DirectedGraph;

/**
 * A <a href = "http://en.wikipedia.org/wiki/Flow_network">flow network</a> is a
 * directed graph where each edge has a capacity and each edge receives a flow.
 * The amount of flow on an edge can not exceed the capacity of the edge (note,
 * that all capacities must be non-negative). A flow must satisfy the
 * restriction that the amount of flow into a vertex equals the amount of flow
 * out of it, except when it is a source, which "produces" flow, or sink, which
 * "consumes" flow.
 * 
 * <p>
 * This class computes maximum flow in a network using <a href =
 * "http://en.wikipedia.org/wiki/Edmonds-Karp_algorithm">Edmonds-Karp
 * algorithm</a>. Be careful: for large networks this algorithm may consume
 * significant amount of time (its upper-bound complexity is O(VE^2), where V -
 * amount of vertices, E - amount of edges in the network).
 * 
 * <p>
 * For more details see Andrew V. Goldberg's <i>Combinatorial Optimization
 * (Lecture Notes)</i>.
 */
public final class EdmondsKarpMaximumFlow<V, E> {

    /**
     * Default tolerance.
     */
    public static final double DEFAULT_EPSILON = 0.000000001;

    private DirectedGraph<V, E> network; // our network
    private double epsilon; // tolerance (DEFAULT_EPSILON or user-defined)
    private int currentSource; // current source vertex
    private int currentSink; // current sink vertex
    private Map<E, Double> maximumFlow; // current maximum flow
    private Double maximumFlowValue; // current maximum flow value
    private int numNodes; // number of nodes in the network
    private Map<V, Integer> indexer; // mapping from vertices to their indexes in the internal representation
    private List<Node> nodes; // internal representation of the network

    /**
     * Added by JeT
     */
    public Node getNode(V v) {
        return this.nodes.get(this.indexer.get(v));
    }

    /**
     * Added by JeT
     */
    public double getFlow(E e) {
        return this.maximumFlow.get(e);
    }

    /**
     * Constructs <tt>MaximumFlow</tt> instance to work with <i>a copy of</i>
     * <tt>network</tt>. Current source and sink are set to <tt>null</tt>. If
     * <tt>network</tt> is weighted, then capacities are weights, otherwise all
     * capacities are equal to one. Doubles are compared using <tt>
     * DEFAULT_EPSILON</tt> tolerance.
     * 
     * @param network
     *            network, where maximum flow will be calculated
     */
    public EdmondsKarpMaximumFlow(DirectedGraph<V, E> network) {
        this(network, DEFAULT_EPSILON);
    }

    /**
     * Constructs <tt>MaximumFlow</tt> instance to work with <i>a copy of</i>
     * <tt>network</tt>. Current source and sink are set to <tt>null</tt>. If
     * <tt>network</tt> is weighted, then capacities are weights, otherwise all
     * capacities are equal to one.
     * 
     * @param network
     *            network, where maximum flow will be calculated
     * @param epsilon
     *            tolerance for comparing doubles
     */
    public EdmondsKarpMaximumFlow(DirectedGraph<V, E> network, double epsilon) {
        if (network == null) {
            throw new NullPointerException("network is null");
        }
        if (epsilon <= 0) {
            throw new IllegalArgumentException("invalid epsilon (must be positive)");
        }
        for (E e : network.edgeSet()) {
            if (network.getEdgeWeight(e) < -epsilon) {
                throw new IllegalArgumentException("invalid capacity (must be non-negative)");
            }
        }

        this.network = network;
        this.epsilon = epsilon;

        this.currentSource = -1;
        this.currentSink = -1;
        this.maximumFlow = null;
        this.maximumFlowValue = null;

        this.buildInternalNetwork();
    }

    // converting the original network into internal more convenient format
    private void buildInternalNetwork() {
        this.numNodes = this.network.vertexSet().size();
        this.nodes = new ArrayList<Node>();
        Iterator<V> it = this.network.vertexSet().iterator();
        this.indexer = new HashMap<V, Integer>();
        for (int i = 0; i < this.numNodes; i++) {
            V currentNode = it.next();
            this.nodes.add(new Node(currentNode));
            this.indexer.put(currentNode, i);
        }
        for (int i = 0; i < this.numNodes; i++) {
            V we = this.nodes.get(i).prototype;
            for (E e : this.network.outgoingEdgesOf(we)) {
                V he = this.network.getEdgeTarget(e);
                int j = this.indexer.get(he);
                Arc e1 = new Arc(i, j, this.network.getEdgeWeight(e), e);
                Arc e2 = new Arc(j, i, 0.0, null);
                e1.reversed = e2;
                e2.reversed = e1;
                this.nodes.get(i).outgoingArcs.add(e1);
                this.nodes.get(j).outgoingArcs.add(e2);
            }
        }
    }

    /**
     * Sets current source to <tt>source</tt>, current sink to <tt>sink</tt>,
     * then calculates maximum flow from <tt>source</tt> to <tt>sink</tt>. Note,
     * that <tt>source</tt> and <tt>sink</tt> must be vertices of the <tt>
     * network</tt> passed to the constructor, and they must be different.
     * 
     * @param source
     *            source vertex
     * @param sink
     *            sink vertex
     */
    public void calculateMaximumFlow(V source, V sink) {
        if (!this.network.containsVertex(source)) {
            throw new IllegalArgumentException("invalid source (null or not from this network)");
        }
        if (!this.network.containsVertex(sink)) {
            throw new IllegalArgumentException("invalid sink (null or not from this network)");
        }

        if (source.equals(sink)) {
            throw new IllegalArgumentException("source is equal to sink");
        }

        this.currentSource = this.indexer.get(source);
        this.currentSink = this.indexer.get(sink);

        for (int i = 0; i < this.numNodes; i++) {
            for (Arc currentArc : this.nodes.get(i).outgoingArcs) {
                currentArc.flow = 0.0;
            }
        }
        this.maximumFlowValue = 0.0;
        for (;;) {
            this.breadthFirstSearch();
            if (!this.nodes.get(this.currentSink).visited) {
                this.maximumFlow = new HashMap<E, Double>();
                for (int i = 0; i < this.numNodes; i++) {
                    for (Arc currentArc : this.nodes.get(i).outgoingArcs) {
                        if (currentArc.prototype != null) {
                            this.maximumFlow.put(currentArc.prototype, currentArc.flow);
                        }
                    }
                }
                return;
            }
            this.augmentFlow();
        }
    }

    private void breadthFirstSearch() {
        for (int i = 0; i < this.numNodes; i++) {
            this.nodes.get(i).visited = false;
        }
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.offer(this.currentSource);
        this.nodes.get(this.currentSource).visited = true;
        this.nodes.get(this.currentSource).flowAmount = Double.POSITIVE_INFINITY;
        while (queue.size() != 0) {
            int currentNode = queue.poll();
            for (Arc currentArc : this.nodes.get(currentNode).outgoingArcs) {
                if ((currentArc.flow + this.epsilon) < currentArc.capacity) {
                    if (!this.nodes.get(currentArc.head).visited) {
                        this.nodes.get(currentArc.head).visited = true;
                        this.nodes.get(currentArc.head).flowAmount = Math.min(this.nodes.get(currentNode).flowAmount, currentArc.capacity - currentArc.flow);
                        this.nodes.get(currentArc.head).lastArc = currentArc;
                        queue.add(currentArc.head);
                    }
                }
            }
        }
    }

    private void augmentFlow() {
        double deltaFlow = this.nodes.get(this.currentSink).flowAmount;
        this.maximumFlowValue += deltaFlow;
        int currentNode = this.currentSink;
        while (currentNode != this.currentSource) {
            this.nodes.get(currentNode).lastArc.flow += deltaFlow;
            this.nodes.get(currentNode).lastArc.reversed.flow -= deltaFlow;
            currentNode = this.nodes.get(currentNode).lastArc.tail;
        }
    }

    /**
     * Returns maximum flow value, that was calculated during last <tt>
     * calculateMaximumFlow</tt> call, or <tt>null</tt>, if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     * 
     * @return maximum flow value
     */
    public Double getMaximumFlowValue() {
        return this.maximumFlowValue;
    }

    /**
     * Returns maximum flow, that was calculated during last <tt>
     * calculateMaximumFlow</tt> call, or <tt>null</tt>, if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     * 
     * @return <i>read-only</i> mapping from edges to doubles - flow values
     */
    public Map<E, Double> getMaximumFlow() {
        if (this.maximumFlow == null) {
            return null;
        }
        return Collections.unmodifiableMap(this.maximumFlow);
    }

    /**
     * Returns current source vertex, or <tt>null</tt> if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     * 
     * @return current source
     */
    public V getCurrentSource() {
        if (this.currentSource == -1) {
            return null;
        }
        return this.nodes.get(this.currentSource).prototype;
    }

    /**
     * Returns current sink vertex, or <tt>null</tt> if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     * 
     * @return current sink
     */
    public V getCurrentSink() {
        if (this.currentSink == -1) {
            return null;
        }
        return this.nodes.get(this.currentSink).prototype;
    }

    // class used for internal representation of network
    // FIXME: class & members package -> public 
    public class Node {
        public V prototype; // corresponding node in the original network
        public List<Arc> outgoingArcs = new ArrayList<Arc>(); // list of outgoing arcs
        // in the residual
        // network
        public boolean visited; // this mark is used during BFS to mark visited nodes
        public Arc lastArc; // last arc in the shortest path
        public double flowAmount; // amount of flow, we are able to push here

        Node(V prototype) {
            this.prototype = prototype;
        }
    }

    // class used for internal representation of network
    class Arc {
        int tail; // "from"
        int head; // "to"
        double capacity; // capacity (can be zero)
        double flow; // current flow (can be negative)
        Arc reversed; // for each arc in the original network we are to create
                      // reversed arc
        E prototype; // corresponding edge in the original network, can be null,
                     // if it is reversed arc

        Arc(int tail, int head, double capacity, E prototype) {
            this.tail = tail;
            this.head = head;
            this.capacity = capacity;
            this.prototype = prototype;
        }
    }
}

// End EdmondsKarpMaximumFlow.java