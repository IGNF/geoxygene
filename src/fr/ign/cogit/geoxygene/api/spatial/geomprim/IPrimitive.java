package fr.ign.cogit.geoxygene.api.spatial.geomprim;

import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.geomcomp.IComplex;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public interface IPrimitive extends IGeometry {
  /** Renvoie le set des complexes auxquels appartient this */
  public abstract Set<IComplex> getComplex();

  /** Nombre de complexes auxquels appartient this */
  public abstract int sizeComplex();
}
