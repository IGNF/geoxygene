package fr.ign.cogit.cartagen.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Undirected graphs in which any two vertices are connected by exactly one
 * simple path.
 * @author GTouya
 * 
 */
public class TreeGraph extends Graph {

  public TreeGraph(String name) {
    super(name, false);
  }

  /**
   * Adds an edge to the graph, only if it is possible, i.e. there is no edge
   * already added between both nodes.
   * @param node1
   * @param node2
   */
  public void addEdge(INode node1, INode node2) {
    if (this.getEdgesConnectingNodes(node1, node2).size() != 0)
      return;
    getEdges().add(new Edge(node1, node2));
  }

  public List<INode> getLeaves() {
    List<INode> leafs = new ArrayList<INode>();
    for (INode node : getNodes()) {
      if (node.getDegree() == 1)
        leafs.add(node);
    }
    return leafs;
  }

  /**
   * Compute the longest path between any two leaves of the tree.
   * @return
   */
  public GraphPath getLongestPathBetweenLeaves() {
    List<INode> leaves = getLeaves();
    GraphPath longest = null;
    double longestLength = 0.0;
    for (int i = 0; i < leaves.size(); i++) {
      for (int j = i + 1; j < leaves.size(); j++) {
        GraphPath path = this.computeDijkstraShortestPath(leaves.get(i),
            leaves.get(j));
        if (longest == null) {
          longest = path;
          longestLength = path.getLength();
          continue;
        }
        if (path.getLength() > longestLength) {
          longest = path;
          longestLength = path.getLength();
          continue;
        }
      }
    }

    return longest;
  }
}
