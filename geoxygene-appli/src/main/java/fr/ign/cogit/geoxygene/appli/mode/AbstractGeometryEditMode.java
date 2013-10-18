package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.Cursor;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeometryToolBar;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

public abstract class AbstractGeometryEditMode extends AbstractMode {
  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public AbstractGeometryEditMode(final MainFrame theMainFrame,
      final ModeSelector theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  public Cursor getCursor() {
    return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
  }

  private GeometryToolBar geometryToolBar = null;

  public GeometryToolBar getGeometryToolBar() {
    return this.geometryToolBar;
  }

  public void setGeometryToolBar(GeometryToolBar theGeometryToolBar) {
    this.geometryToolBar = theGeometryToolBar;
  }

  private IDirectPositionList points = new DirectPositionList();

  public IDirectPositionList getPoints() {
    return this.points;
  }

  protected IDirectPosition currentPoint = null;

  public IDirectPosition getCurrentPoint() {
    return this.currentPoint;
  }
}
