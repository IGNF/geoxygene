package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IGriddedSurface extends IParametricCurveSurface {
  public abstract IPointGrid getControlPoint();
}
