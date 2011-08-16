package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.render.RenderUtil;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

public class MovePointMode extends AbstractGeometryEditMode {
  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public MovePointMode(final MainFrame theMainFrame,
      final ModeSelector theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected final JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/move2.png"))); //$NON-NLS-1$
  }

  @Override
  public void mousePressed(final MouseEvent e) {
    ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
    if ((SwingUtilities.isLeftMouseButton(e))) {
      try {
        DirectPosition p = frame.getLayerViewPanel().getViewport()
            .toModelDirectPosition(e.getPoint());
        this.selectPoint(p);
        this.dragCount = 0;
      } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
      }
    }
  }

  @Override
  public void mouseReleased(final MouseEvent e) {
    ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
    if ((SwingUtilities.isLeftMouseButton(e))) {
      try {
        DirectPosition p = frame.getLayerViewPanel().getViewport()
            .toModelDirectPosition(e.getPoint());
        this.movePoint(p);
        frame.getLayerViewPanel().repaint();
      } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
      }
    }
  }

  @Override
  public void mouseMoved(final MouseEvent e) {
  }

  int dragCount = 0;

  @Override
  public void mouseDragged(final MouseEvent e) {
    ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
    if ((SwingUtilities.isLeftMouseButton(e))) {
      try {
        Graphics2D graphics2D = (Graphics2D) frame.getLayerViewPanel()
            .getGraphics();
        graphics2D.setColor(Color.red);
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setXORMode(Color.white);
        DirectPosition p = frame.getLayerViewPanel().getViewport()
            .toModelDirectPosition(e.getPoint());
        if (this.dragCount > 0) {
          RenderUtil.draw(new GM_LineString(new DirectPositionList(
              this.previousPoint, this.currentPoint, this.nextPoint)),
              frame.getLayerViewPanel().getViewport(), graphics2D);
        }
        this.currentPoint = p;
        RenderUtil.draw(new GM_LineString(new DirectPositionList(
            this.previousPoint, this.currentPoint, this.nextPoint)), frame
            .getLayerViewPanel().getViewport(), graphics2D);
        this.dragCount++;
      } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
      }
    }
  }

  /**
   * @param p
   */
  public void selectPoint(IDirectPosition p) {
    IPoint point = new GM_Point(p);
    double minDistance = Double.POSITIVE_INFINITY;
    IFeature closestFeature = null;
    for (IFeature feature : this.mainFrame.getSelectedProjectFrame()
        .getLayerViewPanel().getSelectedFeatures()) {
      double distance = feature.getGeom().distance(point);
      if (distance < minDistance) {
        minDistance = distance;
        closestFeature = feature;
      }
    }
    if (closestFeature == null) {
      return;
    }
    this.currentFeature = closestFeature;
    this.selectPoint(p, closestFeature);
  }

  /**
   * @param point
   * @param feature
   */
  @SuppressWarnings("unchecked")
  private void selectPoint(IDirectPosition point, IFeature feature) {
    if (feature.getGeom().isPolygon()) {
      GM_Polygon polygon = new GM_Polygon((GM_Polygon) feature.getGeom());
      this.selectPoint(point, polygon);
      feature.setGeom(polygon);
    } else {
      if (feature.getGeom().isLineString()) {
        if (!feature.getGeom().coord().contains(point)) {
          GM_LineString line = new GM_LineString(feature.getGeom().coord());
          this.selectPoint(point, line);
          feature.setGeom(line);
        }
      } else {
        if (feature.getGeom().isMultiSurface()) {
          if (!feature.getGeom().coord().contains(point)) {
            GM_MultiSurface<GM_Polygon> multiSurface = new GM_MultiSurface<GM_Polygon>(
                (GM_MultiSurface<GM_Polygon>) feature.getGeom());
            this.selectPoint(point, multiSurface);
            feature.setGeom(multiSurface);
          }
        } else {
          if (feature.getGeom().isMultiCurve()) {
            if (!feature.getGeom().coord().contains(point)) {
              GM_MultiCurve<GM_LineString> multiCurve = new GM_MultiCurve<GM_LineString>(
                  (GM_MultiCurve<GM_LineString>) feature.getGeom());
              this.selectPoint(point, multiCurve);
              feature.setGeom(multiCurve);
            }
          } else {
          }
        }
      }
    }
    this.currentPoint = point;
  }

  /**
   * @param point
   * @param points
   */
  private int closestPointIndex(IDirectPosition point, IDirectPositionList points) {
    int indexPointMin = 0;
    double distanceMin = point.distance(points.get(0));
    for (int index = 1; index < points.size(); index++) {
      IDirectPosition currentPoint = points.get(index);
      double distance = point.distance(currentPoint);
      if (distance < distanceMin) {
        distanceMin = distance;
        indexPointMin = index;
      }
    }
    return indexPointMin;
  }

  private void selectPoint(IDirectPosition point,
      GM_MultiCurve<GM_LineString> multiCurve) {
    double distanceMin = Double.MAX_VALUE;
    for (GM_LineString line : multiCurve) {
      int index = this.closestPointIndex(point, line.getControlPoint());
      double distance = point.distance(line.getControlPoint(index));
      if (distance < distanceMin) {
        this.sourcePoint = line.getControlPoint(index);
        if (index == 0) {
          this.previousPoint = line
              .getControlPoint(line.sizeControlPoint() - 2);
          this.nextPoint = line.getControlPoint(1);
          return;
        }
        this.previousPoint = line.getControlPoint(index - 1);
        this.nextPoint = line.getControlPoint(index + 1);
      }
    }
  }

  private void selectPoint(IDirectPosition point,
      GM_MultiSurface<GM_Polygon> multiSurface) {
    IRing ringMin = null;
    double distanceMin = Double.MAX_VALUE;
    for (GM_Polygon polygon : multiSurface) {
      double distance = Distances.distance(point, polygon.getExterior());
      if (distance < distanceMin) {
        distanceMin = distance;
        ringMin = polygon.getExterior();
      }
      for (int index = 0; index < polygon.sizeInterior(); index++) {
        IRing interiorRing = polygon.getInterior().get(index);
        distance = Distances.distance(point, interiorRing);
        if (distance < distanceMin) {
          distanceMin = distance;
          ringMin = interiorRing;
        }
      }
    }
    if (ringMin != null) {
      IDirectPositionList points = ringMin.coord();
      this.selectPoint(point, points);
    }
  }

  IDirectPosition sourcePoint;
  IDirectPosition previousPoint;
  IDirectPosition nextPoint;
  IFeature currentFeature;

  private void selectPoint(IDirectPosition point, GM_LineString line) {
    int index = this.closestPointIndex(point, line.getControlPoint());
    this.sourcePoint = line.getControlPoint(index);
    if (index == 0) {
      this.previousPoint = line.getControlPoint(line.sizeControlPoint() - 2);
      this.nextPoint = line.getControlPoint(1);
      return;
    }
    this.previousPoint = line.getControlPoint(index - 1);
    this.nextPoint = line.getControlPoint(index + 1);
  }

  private void selectPoint(IDirectPosition point, GM_Polygon polygon) {
    IRing ringMin = polygon.getExterior();
    double distanceMin = Distances.distance(point, ringMin);
    for (int index = 0; index < polygon.sizeInterior(); index++) {
      IRing interiorRing = polygon.getInterior().get(index);
      double distance = Distances.distance(point, interiorRing);
      if (distance < distanceMin) {
        distanceMin = distance;
        ringMin = interiorRing;
      }
    }
    IDirectPositionList points = ringMin.coord();
    this.selectPoint(point, points);
  }

  private void selectPoint(IDirectPosition point, IDirectPositionList points) {
    int index = this.closestPointIndex(point, points);
    this.sourcePoint = points.get(index);
    if (index == 0) {
      this.previousPoint = points.get(points.size() - 1);
      this.nextPoint = points.get(1);
      System.out.println(this.sourcePoint);
      System.out.println(this.previousPoint);
      System.out.println(this.nextPoint);
      return;
    }
    this.previousPoint = points.get(index - 1);
    this.nextPoint = points.get(index + 1);
    System.out.println(this.sourcePoint);
    System.out.println(this.previousPoint);
    System.out.println(this.nextPoint);
  }

  @SuppressWarnings("unchecked")
  public void movePoint(DirectPosition p) {
    if (this.currentFeature.getGeom().isPolygon()) {
      GM_Polygon polygon = new GM_Polygon((GM_Polygon) this.currentFeature
          .getGeom());
      this.movePoint(p, polygon);
      this.currentFeature.setGeom(polygon);
    } else {
      if (this.currentFeature.getGeom().isLineString()) {
        if (!this.currentFeature.getGeom().coord().contains(p)) {
          GM_LineString line = new GM_LineString(this.currentFeature.getGeom()
              .coord());
          this.movePoint(p, line);
          this.currentFeature.setGeom(line);
        }
      } else {
        if (this.currentFeature.getGeom().isMultiSurface()) {
          if (!this.currentFeature.getGeom().coord().contains(p)) {
            GM_MultiSurface<GM_Polygon> multiSurface = new GM_MultiSurface<GM_Polygon>(
                (GM_MultiSurface<GM_Polygon>) this.currentFeature.getGeom());
            this.movePoint(p, multiSurface);
            this.currentFeature.setGeom(multiSurface);
          }
        } else {
          if (this.currentFeature.getGeom().isMultiCurve()) {
            if (!this.currentFeature.getGeom().coord().contains(p)) {
              GM_MultiCurve<GM_LineString> multiCurve = new GM_MultiCurve<GM_LineString>(
                  (GM_MultiCurve<GM_LineString>) this.currentFeature.getGeom());
              this.movePoint(p, multiCurve);
              this.currentFeature.setGeom(multiCurve);
            }
          } else {
          }
        }
      }
    }
  }

  private void movePoint(IDirectPosition p,
      GM_MultiCurve<GM_LineString> multiCurve) {
    for (GM_LineString line : multiCurve) {
      for (IDirectPosition point : line.getControlPoint()) {
        if (point.equals(this.sourcePoint)) {
          point.setCoordinate(p.getCoordinate());
        }
      }
    }
  }

  private void movePoint(IDirectPosition p,
      GM_MultiSurface<GM_Polygon> multiSurface) {
    for (GM_Polygon polygon : multiSurface) {
      IRing ring = polygon.getExterior();
      IDirectPositionList points = ring.coord();
      for (IDirectPosition point : points) {
        if (point.equals(this.sourcePoint)) {
          point.setCoordinate(p.getCoordinate());
          polygon.setExterior(new GM_Ring(new GM_LineString(points)));
          return;
        }
      }
      for (int i = 0; i < polygon.sizeInterior(); i++) {
        ring = polygon.getInterior(i);
        points = ring.coord();
        for (IDirectPosition point : points) {
          if (point.equals(this.sourcePoint)) {
            point.setCoordinate(p.getCoordinate());
            polygon.setInterior(i, new GM_Ring(new GM_LineString(points)));
            return;
          }
        }
      }
    }
  }

  private void movePoint(IDirectPosition p, GM_LineString line) {
    for (IDirectPosition point : line.getControlPoint()) {
      if (point.equals(this.sourcePoint)) {
        point.setCoordinate(p.getCoordinate());
      }
    }
  }

  private void movePoint(IDirectPosition p, GM_Polygon polygon) {
    IRing ring = polygon.getExterior();
    IDirectPositionList points = ring.coord();
    for (IDirectPosition point : points) {
      if (point.equals(this.sourcePoint)) {
        point.setCoordinate(p.getCoordinate());
        polygon.setExterior(new GM_Ring(new GM_LineString(points)));
        return;
      }
    }
    for (int i = 0; i < polygon.sizeInterior(); i++) {
      ring = polygon.getInterior(i);
      points = ring.coord();
      for (IDirectPosition point : points) {
        if (point.equals(this.sourcePoint)) {
          point.setCoordinate(p.getCoordinate());
          polygon.setInterior(i, new GM_Ring(new GM_LineString(points)));
          return;
        }
      }
    }
  }

  @Override
  protected String getToolTipText() {
    return I18N.getString("MovePointMode.ToolTip"); //$NON-NLS-1$
  }
}
