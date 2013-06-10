package fr.ign.cogit.cartagen.spatialanalysis.network.rivers;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.TronconHydrographique;

public class RiverIsland extends AbstractFeature {

  private Set<TronconHydrographique> outline;
  private Set<RiverStroke> inStrokes, outStrokes;

  public RiverIsland(IPolygon geom, Set<TronconHydrographique> outline) {
    this.setGeom(geom);
    this.outline = outline;
    this.setInStrokes(new HashSet<RiverStroke>());
    this.setOutStrokes(new HashSet<RiverStroke>());
  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) this.geom;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

  public Set<TronconHydrographique> getOutline() {
    return outline;
  }

  public void setOutline(Set<TronconHydrographique> outline) {
    this.outline = outline;
  }

  public Set<RiverStroke> getInStrokes() {
    return inStrokes;
  }

  public void setInStrokes(Set<RiverStroke> inStrokes) {
    this.inStrokes = inStrokes;
  }

  public Set<RiverStroke> getOutStrokes() {
    return outStrokes;
  }

  public void setOutStrokes(Set<RiverStroke> outStrokes) {
    this.outStrokes = outStrokes;
  }

}
