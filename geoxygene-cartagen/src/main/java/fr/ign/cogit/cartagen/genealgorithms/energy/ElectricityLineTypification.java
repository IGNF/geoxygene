package fr.ign.cogit.cartagen.genealgorithms.energy;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class ElectricityLineTypification {

  private INetwork electricityNet;
  private double bufferSize;

  public ElectricityLineTypification(INetwork electricityNet, double bufferSize) {
    super();
    this.setElectricityNet(electricityNet);
    this.setBufferSize(bufferSize);
  }

  public double getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(double bufferSize) {
    this.bufferSize = bufferSize;
  }

  public INetwork getElectricityNet() {
    return electricityNet;
  }

  public void setElectricityNet(INetwork electricityNet) {
    this.electricityNet = electricityNet;
  }

  public void typify() {
    // first enrich the network if necessary (i.e. build topology)
    enrichNetwork();

    // find the clusters of parallel sections in the network
    Set<ParallelSectionsCluster> clusters = findClusters();
    for (ParallelSectionsCluster cluster : clusters)
      cluster.collapse();

    // update topology
    this.electricityNet.removeAllNodes();
    enrichNetwork();
  }

  private Set<ParallelSectionsCluster> findClusters() {
    // TODO Auto-generated method stub
    return null;
  }

  private void enrichNetwork() {
    // TODO
  }

  class ParallelSectionsCluster {
    private Set<IElectricityLine> components;
    private IPolygon geom;

    public Set<IElectricityLine> getComponents() {
      return components;
    }

    public void setComponents(Set<IElectricityLine> components) {
      this.components = components;
    }

    public IPolygon getGeom() {
      return geom;
    }

    public void setGeom(IPolygon geom) {
      this.geom = geom;
    }

    public ParallelSectionsCluster(Set<IElectricityLine> components,
        IPolygon geom) {
      super();
      this.components = components;
      this.geom = geom;
    }

    public void collapse() {
      // TODO
    }
  }
}
