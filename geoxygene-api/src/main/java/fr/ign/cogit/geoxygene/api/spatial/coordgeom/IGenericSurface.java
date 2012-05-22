package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

/**
 * NON UTILISE. Cette interface de la norme n'a plus de sens depuis qu'on a fait
 * hériter GM_SurfacePatch de GM_Surface.
 * 
 * <P>
 * Définition de la norme : les classes GM_Surface et GM_SurfacePatch
 * représentent toutes deux des géométries à deux dimensions, et partagent donc
 * plusieurs signatures d'opération. Celles-ci sont définies dans l'interface
 * GM_GenericSurface.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */
public interface IGenericSurface {
  /** Périmètre. */
  public abstract// NORME : le résultat est de type Length.
  double perimeter();

  /** Aire. */
  public abstract// NORME : le résultat est de type Area.
  double area();
}
