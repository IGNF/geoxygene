package fr.ign.cogit.cartagen.spatialanalysis.network.rivers;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.TronconHydrographique;

public class RiverIslandGroup extends AbstractFeature {

  private Set<TronconHydrographique> sections;
  private Set<RiverIsland> simpleIslands;
  private Set<RiverStroke> inStrokes, outStrokes;

  public RiverIslandGroup(Set<RiverIsland> simpleIslands) {
    this.simpleIslands = simpleIslands;
    this.computeGeom();
    this.computeSections();
    this.setInStrokes(new HashSet<RiverStroke>());
    this.setOutStrokes(new HashSet<RiverStroke>());
  }

  private void computeSections() {
    this.sections = new HashSet<TronconHydrographique>();
    for (RiverIsland island : this.simpleIslands) {
      this.sections.addAll(island.getOutline());
    }
  }

  private void computeGeom() {
    IGeometry geom = null;
    for (RiverIsland island : this.simpleIslands) {
      if (geom == null) {
        geom = island.getGeom();
      } else {
        geom = geom.union(island.getGeom());
      }
    }
    this.setGeom(geom);
  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) this.geom;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

  public Set<TronconHydrographique> getSections() {
    return this.sections;
  }

  public void setSections(Set<TronconHydrographique> sections) {
    this.sections = sections;
  }

  public Set<RiverIsland> getSimpleIslands() {
    return this.simpleIslands;
  }

  public void setSimpleIslands(Set<RiverIsland> simpleIslands) {
    this.simpleIslands = simpleIslands;
  }

  public Set<RiverStroke> getInStrokes() {
    return this.inStrokes;
  }

  public void setInStrokes(Set<RiverStroke> inStrokes) {
    this.inStrokes = inStrokes;
  }

  public Set<RiverStroke> getOutStrokes() {
    return this.outStrokes;
  }

  public void setOutStrokes(Set<RiverStroke> outStrokes) {
    this.outStrokes = outStrokes;
  }

}
