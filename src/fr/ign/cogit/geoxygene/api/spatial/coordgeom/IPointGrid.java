package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IPointGrid {
  /**
   * Get row i.
   * <p>
   * Récupère la ligne i.
   * @param i index of the row
   * @return row with the given index
   */
  public abstract IDirectPositionList getRow(int i);
  /**
   * Number of rows.
   * <p>
   * Nombre de lignes
   * @return Number of rows in the point grid
   */
  public abstract int cardRow();
}
