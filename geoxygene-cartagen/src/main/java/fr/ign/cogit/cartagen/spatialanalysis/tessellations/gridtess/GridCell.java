/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.tessellations.gridtess;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class GridCell<T> extends DefaultFeature {

	private GridTessellation<T> tessellation;
	private int row, column;
	private IDirectPosition center;
	private double size;
	private T value;

	public GridCell(GridTessellation<T> tessellation, int row, int column,
			IDirectPosition center, double size) {
		this.tessellation = tessellation;
		this.row = row;
		this.column = column;
		this.center = center;
		this.size = size;
	}

	public GridCell(GridTessellation<T> tessellation, int row, int column,
			IDirectPosition center, double size, T value) {
		this.tessellation = tessellation;
		this.row = row;
		this.column = column;
		this.center = center;
		this.size = size;
		this.value = value;
	}

	public GridTessellation<T> getTessellation() {
		return tessellation;
	}

	public void setTessellation(GridTessellation<T> tessellation) {
		this.tessellation = tessellation;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public IDirectPosition getCenter() {
		return center;
	}

	public void setCenter(IDirectPosition center) {
		this.center = center;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	/**
	 * Computes the geometry of the cell.
	 * 
	 * @return
	 */
	@Override
	public IPolygon getGeom() {
		IDirectPositionList coord = new DirectPositionList();
		coord.add(new DirectPosition(center.getX() - size / 2, center.getY()
				- size / 2));
		coord.add(new DirectPosition(center.getX() + size / 2, center.getY()
				- size / 2));
		coord.add(new DirectPosition(center.getX() + size / 2, center.getY()
				+ size / 2));
		coord.add(new DirectPosition(center.getX() - size / 2, center.getY()
				+ size / 2));
		coord.add(new DirectPosition(center.getX() - size / 2, center.getY()
				- size / 2));

		return new GM_Polygon(new GM_LineString(coord));
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * Get the neighbouring cells in 4-kind of neighbourhood (cell left, right,
	 * up and down).
	 * 
	 * @return
	 */
	public Set<GridCell<T>> getNeighbours4() {
		Set<GridCell<T>> neighbours = new HashSet<>();
		if (row > 0)
			neighbours.add(tessellation.getCell(row - 1, column));
		if (row < tessellation.getRowNb() - 1)
			neighbours.add(tessellation.getCell(row + 1, column));
		if (column > 0)
			neighbours.add(tessellation.getCell(row, column - 1));
		if (column < tessellation.getColNb() - 1)
			neighbours.add(tessellation.getCell(row, column + 1));

		return neighbours;
	}

	/**
	 * Get the neighbouring cells in 8-kind of neighbourhood (cell left, right,
	 * up and down).
	 * 
	 * @return
	 */
	public Set<GridCell<T>> getNeighbours8() {
		Set<GridCell<T>> neighbours = new HashSet<>();
		if (row > 0)
			neighbours.add(tessellation.getCell(row - 1, column));
		if (row < tessellation.getRowNb() - 1)
			neighbours.add(tessellation.getCell(row + 1, column));
		if (column > 0)
			neighbours.add(tessellation.getCell(row, column - 1));
		if (column < tessellation.getColNb() - 1)
			neighbours.add(tessellation.getCell(row, column + 1));
		if (row > 0 && column > 0)
			neighbours.add(tessellation.getCell(row - 1, column - 1));
		if ((row < tessellation.getRowNb() - 1) && column > 0)
			neighbours.add(tessellation.getCell(row + 1, column - 1));
		if (row > 0 && (column < tessellation.getColNb() - 1))
			neighbours.add(tessellation.getCell(row - 1, column + 1));
		if ((row < tessellation.getRowNb() - 1)
				&& (column < tessellation.getColNb() - 1))
			neighbours.add(tessellation.getCell(row + 1, column + 1));

		return neighbours;
	}

	/**
	 * Returns the grid cell as an envelope object.
	 * 
	 * @return
	 */
	public IEnvelope getEnvelope() {
		return new GM_Envelope(center.getX() - size / 2, center.getX() + size
				/ 2, center.getY() - size / 2, center.getY() + size / 2);
	}
}
