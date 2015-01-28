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

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

/**
 * A grid tessellation to put on top of the data. Value from class T can be
 * associated to the cells.
 * 
 * @author Guillaume
 * 
 * @param <T>
 */
public class GridTessellation<T> {

	private IDirectPosition corner;
	private double width;
	private IEnvelope envelope;
	private int rowNb, colNb;
	private List<GridCell<T>> cells;

	public GridTessellation(IEnvelope envelope, double width) {
		this.envelope = envelope;
		this.width = width;
		this.corner = new DirectPosition(envelope.center().getX()
				- (envelope.width() / 2), envelope.center().getY()
				+ (envelope.length() / 2));
		computeRowColNb();
		computeCells();
	}

	public GridTessellation(IEnvelope envelope, double width, T defaultValue) {
		this.envelope = envelope;
		this.width = width;
		this.corner = new DirectPosition(envelope.center().getX()
				- (envelope.width() / 2), envelope.center().getY()
				+ (envelope.length() / 2));
		computeRowColNb();
		computeCells(defaultValue);
	}

	public IDirectPosition getCorner() {
		return corner;
	}

	public void setCorner(IDirectPosition corner) {
		this.corner = corner;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public IEnvelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(IEnvelope envelope) {
		this.envelope = envelope;
	}

	public int getRowNb() {
		return rowNb;
	}

	public void setRowNb(int rowNb) {
		this.rowNb = rowNb;
	}

	public int getColNb() {
		return colNb;
	}

	public void setColNb(int colNb) {
		this.colNb = colNb;
	}

	public List<GridCell<T>> getCells() {
		return cells;
	}

	public void setCells(List<GridCell<T>> cells) {
		this.cells = cells;
	}

	/**
	 * Compute the cells of the tessellation.
	 */
	private void computeCells() {
		this.cells = new ArrayList<GridCell<T>>();
		for (int i = 1; i <= rowNb; i++) {
			for (int j = 1; j <= colNb; j++) {
				if (isEven(i) && (!isEven(j)))
					continue;
				if (!isEven(i) && isEven(j))
					continue;
				// now compute the center for cell (i,j)
				double xCenter = corner.getX() + width / 2 + (width * (j - 1));
				double yCenter = corner.getY() - (width / 2)
						- (width * (i - 1));

				this.cells.add(new GridCell<T>(this, i, j, new DirectPosition(
						xCenter, yCenter), width));
			}
		}
	}

	/**
	 * Compute the cells of the tessellation and put a default value to all
	 * cells.
	 */
	private void computeCells(T defaultValue) {
		this.cells = new ArrayList<GridCell<T>>();
		for (int i = 1; i <= rowNb; i++) {
			for (int j = 1; j <= colNb; j++) {
				if (isEven(i) && (!isEven(j)))
					continue;
				if (!isEven(i) && isEven(j))
					continue;
				// now compute the center for cell (i,j)
				double xCenter = corner.getX() + width / 2 + (width * (j - 1));
				double yCenter = corner.getY() - (width / 2)
						- (width * (i - 1));

				this.cells.add(new GridCell<T>(this, i, j, new DirectPosition(
						xCenter, yCenter), width, defaultValue));
			}
		}
	}

	/**
	 * Returns true if the number is even.
	 * 
	 * @param number
	 * @return
	 */
	private boolean isEven(int number) {
		int rest = number % 2;
		if (rest == 0)
			return true;
		return false;
	}

	/**
	 * Compute the number of rows and columns, given the envelope of the grid
	 * tessellation and the width of a cell.
	 */
	private void computeRowColNb() {
		this.colNb = Math.round((float) this.envelope.width() / (float) width) + 1;
		this.rowNb = Math.round((float) this.envelope.length() / (float) width) * 2 + 2;
	}

	/**
	 * Get the cell at a given (row,column) coordinate.
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public GridCell<T> getCell(int row, int column) {
		if (row < 0)
			return null;
		if (row >= rowNb)
			return null;
		if (column < 0)
			return null;
		if (column >= colNb)
			return null;
		return this.cells.get(row * colNb + column);
	}
}
