package fr.ign.cogit.geoxygene.api.spatial.geomprim;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ISurfacePatch;

public interface ISurface extends IOrientableSurface {
  /** Renvoie la liste des patch. */
  public abstract List<ISurfacePatch> getPatch();

  /** Renvoie le patch de rang i. */
  public abstract ISurfacePatch getPatch(int i);

  /** Affecte un patch au rang i. */
  public abstract void setPatch(int i, ISurfacePatch value);

  /** Ajoute un patch en fin de liste. */
  public abstract void addPatch(ISurfacePatch value);

  /** Ajoute un patch au rang i. */
  public abstract void addPatch(int i, ISurfacePatch value);

  /** Efface le patch de valeur value. */
  public abstract void removePatch(ISurfacePatch value);

  /** Efface le patch de rang i. */
  public abstract void removePatch(int i);

  /** Renvoie le nombre de patch. */
  public abstract int sizePatch();

  /** Périmètre. */
  public abstract double perimeter();

  // ////////////////////////////////////////////////////////////////////////////////
  // Méthodes d'accès aux coordonnées
  // //////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie la frontière extérieure sous forme d'une polyligne (on a
   * linéarisé). Ne fonctionne que si la surface est composée d'un et d'un seul
   * patch, qui est un polygone. (sinon renvoie null).
   */
  public abstract ILineString exteriorLineString();

  /**
   * Renvoie la frontière extérieure sous forme d'une GM_Curve. Ne fonctionne
   * que si la surface est composée d'un et d'un seul patch, qui est un
   * polygone. (sinon renvoie null).
   */
  public abstract ICurve exteriorCurve();

  /**
   * Renvoie la liste des coordonnées de la frontière EXTERIEURE d'une surface,
   * sous forme d'une DirectPositionList.
   */
  public abstract IDirectPositionList exteriorCoord();

  /**
   * Renvoie la frontière intérieure de rang i sous forme d'une polyligne (on a
   * linéarisé). Ne fonctionne que si la surface est composée d'un et d'un seul
   * patch, qui est un polygone (sinon renvoie null).
   */
  public abstract ILineString interiorLineString(int i);

  /**
   * Renvoie la frontière intérieure de rang i sous forme d'une GM_Curve. Ne
   * fonctionne que si la surface est composée d'un et d'un seul patch, qui est
   * un polygone (sinon renvoie null).
   */
  public abstract ICurve interiorCurve(int i);

  /**
   * Renvoie la liste des coordonnées de la frontière intérieure de rang i d'une
   * surface, sous forme d'un GM_PointArray.
   */
  public abstract IDirectPositionList interiorCoord(int i);

  /**
   * Renvoie la liste des coordonnées d'une surface (exterieure et interieur)
   * sous forme d'une DirectPositionList. Toutes les coordonnees sont
   * concatenees.
   */
  @Override
  public abstract IDirectPositionList coord();
}
