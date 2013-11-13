package fr.ign.cogit.cartagen.spatialanalysis.network.flexibilitygraph;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

public abstract class NetworkConflict {

  private double conflictCost;
  private Set<INetworkSection> sections;

  public Set<INetworkSection> getSections() {
    return sections;
  }

  public void setSections(Set<INetworkSection> sections) {
    this.sections = sections;
  }

  public NetworkConflict(Set<INetworkSection> sections) {
    super();
    this.sections = sections;
  }

  public double getConflictCost() {
    return conflictCost;
  }

  public void setConflictCost(double conflictCost) {
    this.conflictCost = conflictCost;
  }

  public Set<IFeature> getSectionsColn() {
    return new HashSet<IFeature>(sections);
  }

}
