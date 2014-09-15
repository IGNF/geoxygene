package fr.ign.cogit.geoxygene.appli.plugin.lenses;


public class FishEyeLens extends AbstractLens {

  public static double FOCUS_RADIUS = 50.0;
  public static double TRANSITION_RADIUS = 100.0;

  public FishEyeLens(double focusRadius, double transitionRadius) {
    super();
    // this.focusRadius = focusRadius;
    // this.transitionRadius = transitionRadius;

    this.setFocusRegion(new FishEyeLensZoneShape(1, 1, focusRadius));
    this.setTransitionRegion(new FishEyeLensZoneShape(1, 1, transitionRadius));

  }

  public FishEyeLens() {
    super();
    this.setFocusRegion(new FishEyeLensZoneShape(1, 1, FOCUS_RADIUS));
    this.setTransitionRegion(new FishEyeLensZoneShape(1, 1, TRANSITION_RADIUS));
  }

  @Override
  protected void changeShapes(int x, int y) {
    this.getFocusRegion().setCenter(x, y);
    this.getTransitionRegion().setCenter(x, y);

    // this.setFocusRegion(new Ellipse2D.Double(x - focusRadius, y -
    // focusRadius,
    // 2 * focusRadius, 2 * focusRadius));
    // this.setTransitionRegion(new Ellipse2D.Double(x - transitionRadius, y
    // - transitionRadius, 2 * transitionRadius, 2 * transitionRadius));

  }
}
