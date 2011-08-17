package fr.ign.cogit.geoxygene.api.spatial.geomprim;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;

public interface ISolid extends IPrimitive {
  /**
   * NON IMPLEMENTE (renvoie 0.0). Aire. Dans la norme, le résultat est de type
   * Area.
   * */
  public abstract double area();

  public abstract IDirectPositionList coord();

  /**
   * NON IMPLEMETE (renvoie 0.0). Volume. Dans la norme, le résultat est de type
   * Volume.
   */
  public abstract double volume();

  /**
   * Redéfinition de l'opérateur "boundary" sur GM_Object. Renvoie une
   * GM_SolidBoundary, c'est-à-dire un shell extérieur et éventuellement un
   * (des) shell(s) intérieur(s).
   */
  public abstract ISolidBoundary boundary();

  /**
   * Renvoie la liste des faces extérieures d'un solide
   * 
   * @return la liste des faces extérieures d'un solide
   */
  public abstract List<IOrientableSurface> getFacesList();

  /**
   * Permet de renvoyer une chaine de caractère décrivant un solide
   */
  public abstract String toString();
}
