package fr.ign.cogit.geoxygene.matching.dst.graph;

import org.jgrapht.graph.DefaultEdge;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

public class Link extends DefaultEdge {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  double belief;
  double plausibility;
  double doubtt;
  double ignorance;
  double communality;

  @Override
  public String toString() {
    String result = "Link\n";
    result += "\tFrom:\n";
    result += "\t\t" + ((IFeature) this.getSource()).getAttribute("gid") + " " + this.getSource() + "\n";
    result += "\tTo:\n";
    result += "\t\t" + ((IFeature) this.getTarget()).getAttribute("gid") + " " + this.getTarget() + "\n";
    return result;
  }
}
