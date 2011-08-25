package fr.ign.cogit.geoxygene.api.util.index;

import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 * @param <Feat>
 */
public interface SpatialIndex<Feat extends IFeature> {
  /**
   * Renvoie les paramètres de l'index. Ce que contient exactement cette liste
   * peut être différent pour chaque type d'index.
   * 
   * Pour un dallage: renvoie une ArrayList de 4 éléments - 1er élément : Class
   * égal à Dallage.class - 2ème élément : Boolean indiquant si l'index est en
   * mode MAJ automatique ou non - 3ème élément : GM_Envelope décrivant les
   * limites de la zone couverte - 4ème élément : Integer exprimant le nombre de
   * cases en X et Y.
   * 
   */
  public abstract List<Object> getParametres();

  /** Indique si l'on a demande une mise a jour automatique. */
  public abstract boolean hasAutomaticUpdate();

  /**
   * Demande une mise a jour automatique. NB: Cette méthode ne fait pas les
   * éventuelles MAJ qui auriant ête faites alors que le mode MAJ automatique
   * n'était pas activé.
   */
  public abstract void setAutomaticUpdate(boolean auto);

  /**
   * Met a jour l'index avec le FT_Feature passé en paramètre.
   * <p>
   * <b>ATTENTION : si le nouveau feature est en dehors des dalles existantes,
   * il ne sera jamais inséré dans l'index !</b>
   * @param value FT_Feature provocant la mise à jour de l'index
   * @param cas type de modification de l'index :
   *          <ul>
   *          <li> +1 : on ajoute le feature. <li> -1 : on enleve le feature.
   *          <li> 0 : on modifie le feature.
   *          </ul>
   */
  public abstract void update(Feat value, int cas);

  /**
   * Selection dans le carre dont P est le centre, de cote D. NB: D peut être
   * nul.
   */
  public abstract Collection<Feat> select(IDirectPosition P, double D);

  /** Selection a l'aide d'un rectangle. */
  public abstract Collection<Feat> select(IEnvelope env);

  /** Selection des objets qui intersectent un objet geometrique quelconque. */
  public abstract Collection<Feat> select(IGeometry geometry);

  /**
   * Selection des objets qui croisent ou intersectent un objet geometrique
   * quelconque.
   * 
   * @param strictlyCrosses Si c'est TRUE : ne retient que les objets qui
   *          croisent (CROSS au sens JTS) Si c'est FALSE : ne retient que les
   *          objets qui intersectent (INTERSECT au sens JTS) Exemple : si 1
   *          ligne touche "geometry" juste sur une extrémité, alors avec TRUE
   *          cela ne renvoie pas la ligne, avec FALSE cela la renvoie
   */
  public abstract Collection<Feat> select(IGeometry geometry,
      boolean strictlyCrosses);

  /**
   * Selection a l'aide d'un objet geometrique quelconque et d'une distance. NB:
   * D peut être nul
   */
  public abstract Collection<Feat> select(IGeometry geometry,
      double distance);
}
