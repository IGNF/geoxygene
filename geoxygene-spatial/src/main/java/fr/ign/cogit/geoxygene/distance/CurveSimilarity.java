package fr.ign.cogit.geoxygene.distance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class CurveSimilarity {

  /**
   * Similarity measure between curves based on the dynamic time warping method.
   * @param l1 a polyline
   * @param l2 another polyline
   * @return the dtw distance between the curves.
   */
  public static double dtwDistance(ILineString l1, ILineString l2) {

    double[][] matrix = new double[l1.coord().size()][l2.coord().size()];

    for (int i = 1; i < l2.coord().size(); i++) {
      matrix[0][i] = Double.POSITIVE_INFINITY;
    }
    for (int i = 1; i < l1.coord().size(); i++) {
      matrix[i][0] = Double.POSITIVE_INFINITY;
    }
    matrix[0][0] = 0.0;
    for (int i = 1; i < l1.coord().size(); i++) {
      for (int j = 1; j < l2.coord().size(); j++) {
        double cost = l1.coord().get(i).distance(l2.coord().get(j));
        matrix[i][j] = cost
            + Math.min(matrix[i - 1][j],
                Math.min(matrix[i][j - 1], matrix[i - 1][j - 1]));
      }
    }
    return matrix[l1.coord().size() - 1][l2.coord().size() - 1];
  }


  /**
   * Compute the turning angle function for a polygonal curve.
   * @param l linestring
   * @return
   */
  public static double[][] turningFunction(ILineString l) {
    IDirectPositionList coordinates = l.coord();
    if (l.isClosed() && coordinates.size() > 1) {
      coordinates.add(coordinates.get(1));
    }
    double[][] tfunc = new double[2][];
    double[] xs = new double[coordinates.size()];
    double[] ys = new double[coordinates.size()];
    double len = l.length();

    if (coordinates.size() >= 2) {
      IDirectPosition p1 = l.coord().get(0);
      IDirectPosition p2 = l.coord().get(1);
      Vecteur v = new Vecteur(p1, p2);
      xs[0] = 0.0;
      v.normalise();
      ys[0] = Math.atan2(v.getY(), v.getX());
      ys[0] = (ys[0] < 0) ? ys[0] + 2 * Math.PI : ys[0];
    }
    for (int i = 1; i < coordinates.size() - 1; i++) {
      IDirectPosition p1 = coordinates.get(i - 1);
      IDirectPosition p2 = coordinates.get(i);
      IDirectPosition p3 = coordinates.get(i + 1);
      Vecteur v1 = new Vecteur(p1, p2);
      Vecteur v2 = new Vecteur(p2, p3);
      xs[i] = v2.norme() / len + xs[i - 1];
      v1.normalise();
      v2.normalise();
      double angle = Math.atan2(v1.getX() * v2.getY() - v1.getY() * v2.getX(),
          v1.getX() * v2.getX() + v1.getY() * v2.getY());
      ys[i] = ys[i - 1] + angle;
    }
    xs[coordinates.size() - 1] = 1.0;
    ys[coordinates.size() - 1] = ys[coordinates.size() - 2];
    tfunc[0] = xs;
    tfunc[1] = ys;
    return tfunc;
  }

  /**
   * TODO terminer 
   * <p>
   * Compute the turning angle distance between 2 closed linestrings.<br/>
   * This method works ONLY with closed linestrings since we assume that the
   * function is shifted by 2PI every unit.
   * </p>
   * See Artkin & al., An efficiently computable metric for comparing polygonal
   * shapes, IEEE vol 13 n°3, 1991
   * @param l1 the first closed linestring
   * @param l2 the second closed linestring
   * @return
   */
  public static double turningDistance(ILineString l1, ILineString l2) {
    System.out.println("ENTER");
    if (!l1.isClosed() || !l2.isClosed()) {
      return Double.POSITIVE_INFINITY;
    }
    double distance = Double.POSITIVE_INFINITY;
    double[][] f = turningFunction(l1);
    double[][] g = turningFunction(l2);
    assert (f[1][0] + 2 * Math.PI == f[1][f[1].length - 1]);
    assert (g[1][0] + 2 * Math.PI == g[1][g[1].length - 1]);
    
    
    GM_LineString fline = new GM_LineString();
    GM_LineString gline = new GM_LineString();
    // For convenience reasons we convert the turning functions into polylines (without the 0 and 1 values).
    for(int i = 1; i < f[0].length-1; i++){
      fline.addControlPoint(new DirectPosition(f[0][i], f[1][i]));
    }
    for(int i = 1; i < g[0].length-1; i++){
      gline.addControlPoint(new DirectPosition(g[0][i], g[1][i]));
    }
    //now we find all the m*n critical points on [0;1]
    List<Double> criticalValues = new ArrayList<>();
    for(IDirectPosition pf : fline.coord()){
      for(IDirectPosition pg : gline.coord()){
        if(pf.getX() >= pg.getX()){
          criticalValues.add(pf.getX()-pg.getX());
          System.out.println(pg.getX()-pf.getX());
        }else{
          criticalValues.add(pf.getX()+1-pg.getX());
          System.out.println(pf.getX()+1-pg.getX());
        }
      }
    }
    Collections.sort(criticalValues);
      
    
    
    double[][] exttf = new double[2][g[0].length * 3];
    // For convenience reasons, we first compute the function tfunc2 from -1 to
    // 2. In order to do so, we only have to shift the function horizontaly from
    // [0;1] to [-1;0]/[1;2] and verticaly of -2PI/2PI.
    double[] xs = new double[f[0].length * 3 - 4];
    double[] ys = new double[f[1].length * 3 - 4];
    int id = 0;
    for (int i = 0; i < f[0].length - 1; i++) {
      xs[id] = f[0][i] - 1.0;
      ys[id] = f[1][i] - (2.0 * Math.PI);
      id++;
    }
    for (int i = 1; i < f[1].length - 1; i++) {
      xs[id] = f[0][i];
      ys[id] = f[1][i];
      id++;
    }
    for (int i = 1; i < f[1].length; i++) {
      xs[id] = f[0][i] + 1.0;
      ys[id] = f[1][i] + (2.0 * Math.PI);
      id++;
    }
    exttf[0] = xs;
    exttf[1] = ys;

    // Find the shifts
    ArrayList<Double> shifts = new ArrayList<Double>();
    for (int i = 1; i < g[0].length; i++) {
      for (int j = 1; j < f[0].length; j++) {
        Double shift = f[0][j] - g[0][i];
        shifts.add(shift);
      }
    }
    shifts.sort(Comparator.comparingDouble(o -> Math.abs(o.doubleValue())));
    List<ArrayList<Double>> H = CurveSimilarity.turningSimilarityStrips(f, g);

    double hci = 0.0;
    double ci = 0.0;
    double hfg = 0.0;
    double hgf = 0.0;
    //initial values
    for (ArrayList<Double> l : H) {
      for (Double d : H.get(0)) {
        hci += d;
      }
    }
    for (Double d : H.get(3)) {
      hfg += d;
    }
    for (Double d : H.get(2)) {
      hfg +=d;
    }
    
    
    for(Double shift : shifts){
      //compute h(shift, 0)
      hci  = (hfg-hgf)*(shift - ci) + hci;
      ci  =  shift;
      //update 
    }
    
 
    // read the function from t to t+1.
    /*
     * double[][] newf; try { newf = CurveSimilarity.subFunction(exttf, shift,
     * shift + 1); } catch (Exception e) { e.printStackTrace(); return
     * Double.POSITIVE_INFINITY; } // shift the function to the bounds [0;1] so
     * it can be compared. double d = newf[0][0]; for (int k = 0; k <
     * newf[0].length; k++) { newf[0][k] = newf[0][k] - d; } } }
     */

    return distance;
  }

  /**
   * <p>
   * Compute the n+m stripes between the tunring functions f and g. <br/>
   * We assume that the given turning functions are well formed (accordingly to
   * {@link #turningFunction(ILineString)}})
   * </p>
   *
   * See Artkin & al., An efficiently computable metric for comparing polygonal
   * shapes, IEEE vol 13 n&deg;3, 1991
   * @param f
   * @param g
   * @return an array containing, in this order, Rgg, Rff, Rgf, Rfg
   */
  private static List<ArrayList<Double>> turningSimilarityStrips(double[][] f,
      double[][] g) {
    GM_LineString fl1 = new GM_LineString();
    GM_LineString fl2 = new GM_LineString();
    ArrayList<DirectPosition> merged = new ArrayList<DirectPosition>();
    for (int i = 0; i < f[0].length - 1; i++) {
      DirectPosition pt = new DirectPosition(f[0][i], f[1][i]);
      fl1.addControlPoint(pt);
      if (pt.getX() != 0.0)
        merged.add(pt);
      fl1.addControlPoint(new DirectPosition(f[0][i + 1], f[1][i]));
    }
    for (int i = 0; i < g[0].length - 1; i++) {
      DirectPosition pt = new DirectPosition(g[0][i], g[1][i]);
      fl2.addControlPoint(pt);
      if (pt.getX() != 0.0)
        merged.add(pt);
      fl2.addControlPoint(new DirectPosition(g[0][i + 1], g[1][i]));
    }
    merged.sort(Comparator.comparingDouble(DirectPosition::getX));
    ArrayList<Double> rgg = new ArrayList<>();
    ArrayList<Double> rff = new ArrayList<>();
    ArrayList<Double> rgf = new ArrayList<>();
    ArrayList<Double> rfg = new ArrayList<>();

    for (int i = 0; i < merged.size() - 1; i++) {
      int pt1InF = fl1.coord().getList().indexOf(merged.get(i));
      int pt2InF = fl1.coord().getList().indexOf(merged.get(i + 1));
      GM_LineString d1 = new GM_LineString(new DirectPosition(merged.get(i)
          .getX(), 0), new DirectPosition(merged.get(i).getX(), 100));
      GM_Point intersection = null;
      // case Rgg
      if (pt1InF == -1 && pt2InF == -1) {
        intersection = (GM_Point) d1.intersection(fl1);
        rgg.add(intersection.getPosition().distance(merged.get(i)));
        System.out.println("RGG ");
      }
      // case Rff
      if (pt1InF != -1 && pt2InF != -1) {
        intersection = (GM_Point) d1.intersection(fl2);
        rff.add(intersection.getPosition().distance(merged.get(i)));
        System.out.println("RFF ");
      }
      // case Rgf
      if (pt1InF == -1 && pt2InF != -1) {
        intersection = (GM_Point) d1.intersection(fl1);
        rgf.add(intersection.getPosition().distance(merged.get(i)));
        System.out.println("RGF ");
      }
      // /case Rfg
      if (pt1InF != -1 && pt2InF == -1) {
        intersection = (GM_Point) d1.intersection(fl2);
        rfg.add(intersection.getPosition().distance(merged.get(i)));
        System.out.println(intersection);
        System.out.println("RFG ");
      }
    }
    List<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
    result.add(rff);
    result.add(rgg);
    result.add(rgf);
    result.add(rfg);
    return result;
  }

  /**
   * Read the given turning function from x to x'.<br/>
   * FIXME boxing
   * @param f the function
   * @param x the lower bound.
   * @param xp the upper bound.
   * @return the portion of the turning function read from x to x'
   * @throws Exception
   */
  @SuppressWarnings("boxing")
  public static double[][] subFunction(double[][] f, double x, double xp)
      throws Exception {
    if (x < f[0][0] || xp > f[0][f[0].length - 1]) {
      throw new Exception("Trying to read a turning function from " + x
          + " to " + xp + ", but the function is defined only from " + f[0][0]
          + " to " + f[0][f[0].length - 1] + "");
    }
    int startId = 0;
    int endId = 0;
    ArrayList<Double> xs = new ArrayList<Double>();
    ArrayList<Double> ys = new ArrayList<Double>();
    for (int k = 0; k < f[0].length; k++) {
      if (x <= f[0][k]) {
        startId = k - 1;
        break;
      }
    }
    for (int k = startId; k < f[0].length; k++) {
      if (f[0][k] >= xp) {
        endId = k;
        break;
      }
    }
    xs.add(x);
    ys.add(f[1][startId]);
    for (int k = startId + 1; k < endId; k++) {
      xs.add(f[0][k]);
      ys.add(f[1][k]);
    }
    xs.add(xp);
    ys.add(f[1][endId]);
    double[][] fp = new double[2][xs.size()];
    for (int k = 0; k < xs.size(); k++) {
      fp[0][k] = xs.get(k);
    }
    for (int k = 0; k < ys.size(); k++) {
      fp[1][k] = ys.get(k);
    }
    return fp;

  }

  /**
   * 
   * Partial curve matching with turning functions. <br/>
   * TODO A finir.
   * @param l1
   * @param l2
   * @param sampling_step
   * @param delta
   */
  public static void partialTurningMatching(ILineString l1, ILineString l2,
      double sampling_step, double delta) {
    double[][] tfunc1 = turningFunction(l1);
    double[][] tfunc2 = turningFunction(l2);
    int n = (int) (1.0 / sampling_step);
    double[] smpld1 = new double[n];
    double[] smpld2 = new double[n];
    double step = 0.0d;

    // sampling.
    for (int i = 0; i < n; i++) {
      double x = 0.0;
      double value = Double.MAX_VALUE;
      for (int j = 0; j < tfunc1[0].length; j++) {
        x += tfunc1[0][j];
        if (step <= x) {
          value = tfunc1[1][j];
          break;
        }
      }
      smpld1[i] = value;
      x = 0.0;
      for (int j = 0; j < tfunc2[0].length; j++) {
        x += tfunc2[0][j];
        if (step <= x) {
          value = tfunc2[1][j];
          break;
        }
      }
      smpld2[i] = value;
      step += sampling_step;
    }
    // shifting
    System.out.println("Shifting step"); //$NON-NLS-1$

  }
}
