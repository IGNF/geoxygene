package fr.ign.cogit.geoxygene.distance;


import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;

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
   * @param l
   * @return
   */
  public static double[][] turningFunction(ILineString l) {
    double[][] tfunc = new double[2][];
    double[] xs = new double[l.coord().size()-1];
    double[] ys = new double[l.coord().size()-1];
    double len  = l.length();

    if (l.coord().size() >= 2) {
      IDirectPosition p1 = l.coord().get(0);
      IDirectPosition p2 = l.coord().get(1);
      Vecteur v = new Vecteur(p1, p2);
      xs[0] =0.0;
      v.normalise();
      ys[0] = Math.atan2(v.getY(), v.getX());
      ys[0] = (ys[0] < 0 )? ys[0] + 2*Math.PI : ys[0]; 
    }
    for (int i = 1; i < l.coord().size()-1; i++) {
      IDirectPosition p1 = l.coord().get(i - 1);
      IDirectPosition p2 = l.coord().get(i);
      IDirectPosition p3 = l.coord().get(i + 1);
      Vecteur v1 = new Vecteur(p1, p2);
      Vecteur v2 = new Vecteur(p2, p3);
      xs[i] = v2.norme()/len + xs[i-1];
      v1.normalise();
      v2.normalise();
      double angle = Math.atan2(v1.getX() * v2.getY() - v1.getY() * v2.getX(),
          v1.getX() * v2.getX() + v1.getY() * v2.getY());
      ys[i] = ys[i-1] + angle;
    }
    tfunc[0] = xs;
    tfunc[1] = ys;
    return tfunc;
  }

  
  /**
   * Compute the turning angle distance between 2 closed linestrings.<br/>
   * This method is NOT 
   * 
   *  See <a
   * href="http://www.cim.mcgill.ca/~sveta/pr/algorithm.htm"
   * >http://www.cim.mcgill.ca/~sveta/pr/algorithm.htm</a>
   * @param l1
   * @param l2
   * @return
   */
  public static double turningDistance(ILineString l1, ILineString l2){
    double distance = Double.POSITIVE_INFINITY;
    double[][] tfunc1 = turningFunction(l1);
    double[][] tfunc2 = turningFunction(l2);
  
    // For convenience reasons, we first compute the function tfunc2 from -1 to
    // 2. In order to do so, we only have to shift the function horizontaly from
    // [0;1] to [-1;0]/[1;2] and verticaly of -2PI/2PI.
    double[] xs = new double[tfunc2[0].length*3];
    double[] ys = new double[tfunc2[1].length*3];
    for(int i = 0; i < tfunc2[0].length; i++){
      xs[i] = tfunc2[0][i]-1.0;
      ys[i] = tfunc2[1][i]-(2.0*Math.PI);
    }
    for(int i =tfunc2[1].length; i < tfunc2[1].length*2; i++ ){
      xs[i] = tfunc2[0][i-tfunc2[1].length];
      ys[i] = tfunc2[1][i-tfunc2[1].length];
    }
    for(int i = tfunc2[1].length*2; i < tfunc2[0].length*3; i++){
      xs[i] = tfunc2[0][i-tfunc2[1].length*2]+1.0;
      ys[i] = tfunc2[1][i-tfunc2[1].length*2]+(2.0*Math.PI);
    }
  
    tfunc2[0] = xs;
    tfunc2[1] = ys;
    return distance;
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
  public static void partialTurningMatching(ILineString l1, ILineString l2, double sampling_step, double delta){
    double[][] tfunc1 = turningFunction(l1);
    double[][] tfunc2 = turningFunction(l2);
    int n = (int) (1.0/sampling_step) ;
    double[] smpld1 = new double[n];
    double[] smpld2 = new double[n];   
    double step = 0.0d;
    
    //sampling.
    for(int i = 0; i < n ; i++){
      double x = 0.0;
      double value = Double.MAX_VALUE;
      for(int j = 0; j < tfunc1[0].length; j++){
        x += tfunc1[0][j];
        if(step <= x){
          value = tfunc1[1][j];
          break;
        }
      }
      smpld1[i] = value;
      x = 0.0;
      for(int j = 0; j < tfunc2[0].length; j++){
        x += tfunc2[0][j];
        if(step <= x){
          value = tfunc2[1][j];
          break;
        }
      }
      smpld2[i] = value;
      step += sampling_step;
    }
    //shifting
    System.out.println("Shifting step");
    
  }
}

