package fr.ign.cogit.geoxygene.appli.plugin.lenses;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.util.comparators.DistanceFeatureComparator;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class JellyLens extends AbstractLens {

  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(JellyLens.class.getName());

  private double focusArea;

  private double transitionArea;

  private List<IFeature> featuresList;

  public JellyLens(double focusArea, double transitionArea,
      Collection<? extends IFeature> featuresList) {
    this.transitionArea = transitionArea;
    this.focusArea = focusArea;
    this.featuresList = new ArrayList<IFeature>(featuresList);
  }

  @Override
  protected void changeShapes(int x, int y) {
    // logger.debug("Change Shape");

    Point2D newPosition = null;
    try {
      newPosition = ((LayerViewPanel) this.getVisuPanel()).getViewport()
          .toViewPoint(new DirectPosition(x, y));
    } catch (NoninvertibleTransformException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    double convX = newPosition.getX();
    double convY = newPosition.getY();

    IPoint cursor = new GM_Point(new DirectPosition(convX, convY));

    // logger.debug("convX : " + convX);
    // logger.debug("convY : " + convY);

    Comparator<IFeature> c = new DistanceFeatureComparator<IFeature>(cursor);
    // featuresList.

    Collections.sort(featuresList, c);

    this.setFocusRegion(new JellyLensZoneShape(x, y, focusArea, featuresList
        .get(0).getGeom()));
    this.setTransitionRegion(new JellyLensZoneShape(x, y, transitionArea,
        featuresList.get(0).getGeom()));

    ((JellyLensZoneShape) this.getFocusRegion()).setVisuPanel(this
        .getVisuPanel());
    ((JellyLensZoneShape) this.getTransitionRegion()).setVisuPanel(this
        .getVisuPanel());
  }
}
