package fr.ign.cogit.geoxygene.matching.optimisation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleWeightedGraph;

import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LinearProgram;
import Jama.Matrix;
import fr.ign.cogit.evidence.configuration.Configuration;
import fr.ign.cogit.evidence.configuration.ConfigurationSet;
import fr.ign.cogit.evidence.variable.Variable;
import fr.ign.cogit.evidence.variable.VariableFactory;
import fr.ign.cogit.evidence.variable.VariableSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.matching.Matchings;
import fr.ign.cogit.geoxygene.matching.Matchings.Matching;
import fr.ign.cogit.geoxygene.matching.Matchings.Matching.Pattern;
import fr.ign.cogit.geoxygene.matching.Parameters;
import fr.ign.cogit.geoxygene.matching.dst.graph.Link;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Implementation of Li and Goodchild's approach for linear feature matching presented in
 * Linna Li & Michael Goodchild (2011): An optimisation model for linear feature matching in
 * geographical data conflation, International Journal of Image and Data Fusion, 2:4, 309-328.
 * <p>
 * TODO Use polynomialtransformation class and test withe rubbersheeting too as we're at it
 * TODO Cleanup the mess
 * @author Julien Perret
 */
public class LinearFeatureMatcher {
  double a;
  double gamma;
  double selection;
  double alpha;
  double beta;
  int k;
  String nameDB1;
  String nameDB2;
  IPopulation<IFeature> db1;
  IPopulation<IFeature> db2;
  Map<IFeature, Integer> map1;
  Map<IFeature, Integer> map2;
  double[] s1;
  double[] s2;
  double[] length1;
  double[] length2;
  int[] delta1;
  int[] delta2;
  double[] affineTransform;
  Set<SimpleMatch> subModel1Links;
  Set<SimpleMatch> subModel2Links;

  public Set<SimpleMatch> getSubModel1Links() {
    return this.subModel1Links;
  }

  public Set<SimpleMatch> getSubModel2Links() {
    return this.subModel2Links;
  }

  public LinearFeatureMatcher(IPopulation<IFeature> db1, IPopulation<IFeature> db2, Parameters param) {
    this.a = param.getDouble("a");
    this.alpha = a;
    this.gamma = param.getDouble("gamma");
    this.k = param.getInteger("k");
    this.selection = param.getDouble("selection");
    this.beta = param.getDouble("beta");
    this.nameDB1 = param.getString("name_db1");
    this.nameDB2 = param.getString("name_db2");
    System.out.println("a = " + a + " beta = " + beta + " gamma = " + gamma + " selection = "
        + selection + " nameDB1 = " + this.nameDB1 + " nameDB2 = " + this.nameDB2);
    this.db1 = db1;
    this.db2 = db2;
    this.map1 = buildMap(this.db1);
    this.map2 = buildMap(this.db2);
    this.s1 = this.computeSimilarityMatrix(this.db1, this.db2);
    /*
    for (int i = 0; i < this.s1.length; i++) {
      System.out.println("similarity1 " + i +" = " + this.s1[i]);
    }
    */
    this.s2 = this.computeSimilarityMatrix(this.db2, this.db1);
    /*
    for (int i = 0; i < this.s2.length; i++) {
      System.out.println("similarity2 " + i +" = " + this.s2[i]);
    }
    */
    this.length1 = this.computeLengthArray(this.db1);
    this.length2 = this.computeLengthArray(this.db2);
    this.delta1 = this.computeBinarySlackArray(this.db1.size(), this.db2.size(), this.s2);
    System.out.println("delta 1 = ");
    for (int i = 0; i < this.delta1.length; i++) {
      if (this.delta1[i] > 0d) {
        System.out.println(this.db1.get(i).getGeom());
      }
    }
    this.delta2 = this.computeBinarySlackArray(this.db2.size(), this.db1.size(), this.s1);
    System.out.println("delta 2 = ");
    for (int i = 0; i < this.delta2.length; i++) {
      if (this.delta2[i] > 0d) {
        System.out.println(this.db2.get(i).getGeom());
      }
    }
    if (param.getBoolean("affine_transformation")) {
      this.affineTransform = this.computeAffineTransform();
    }
    // this.subModel1Links = this.computeSubModel1();
    // this.subModel2Links = this.computeSubModel2();
    this.subModel1Links = this.computeSubModel(this.db1, this.db2, this.s1, this.delta2,
        this.length1, this.length2);
    this.subModel2Links = this.computeSubModel(this.db2, this.db1, this.s2, this.delta1,
        this.length2, this.length1);
  }

  private Map<IFeature, Integer> buildMap(IPopulation<IFeature> db) {
    Map<IFeature, Integer> map = new HashMap<IFeature, Integer>(db.size());
    for (int i = 0; i < db.size(); i++) {
      map.put(db.get(i), i);
    }
    return map;
  }

  private double[] computeSimilarityMatrix(IPopulation<IFeature> db1, IPopulation<IFeature> db2) {
    int p = db1.size();
    int q = db2.size();
    double[] s = new double[p * q];
    for (int i = 0; i < s.length; i++) {
      s[i] = 0.d;
    }
    // init distances
    for (int i = 0; i < p; i++) {
      IFeature f1 = db1.get(i);
      ILineString l1 = getLineString(f1);
      for (int j = 0; j < q; j++) {
        IFeature f2 = db2.get(j);
        ILineString l2 = getLineString(f2);
        double d = Distances.premiereComposanteHausdorff(l1, l2);
        if (d > this.a) {
          s[j + i * q] = 0.d;
        } else {
          double nameDissimilarity = computeNameDissimilarity(f1.getAttribute(this.nameDB1)
              .toString(), f2.getAttribute(this.nameDB2).toString());
          if (nameDissimilarity < 0) {
            s[j + i * q] = this.a - d;
          } else {
            s[j + i * q] = this.a - (d + nameDissimilarity) / 2.;
          }
        }
      }
    }
    return s;
  }

  private double computeNameDissimilarity(String a, String b) {
    if (a == null || b == null) {
      return -1;
    }
    int minLength = Math.min(a.length(), b.length());
    int difference = Math.abs(a.length() - b.length());
    int d = computeHammingDistance(a.substring(0, minLength), b.substring(0, minLength))
        + difference;
    return 2. * d / (a.length() + b.length()) * alpha;
  }

  private int computeHammingDistance(String a, String b) {
    assert (a.length() == b.length());
    int result = 0;
    for (int i = 0; i < a.length(); i++) {
      if (a.charAt(i) != b.charAt(i)) {
        result++;
      }
    }
    return result;
  }

  private double[] computeLengthArray(IPopulation<IFeature> db) {
    double[] length = new double[db.size()];
    for (int i = 0; i < db.size(); i++) {
      length[i] = getLineString(db.get(i)).length();
    }
    return length;
  }

  private int[] computeBinarySlackArray(int p, int q, double[] similarity) {
    int[] delta = new int[p];
    for (int i = 0; i < p; i++) {
      delta[i] = 1;
      for (int j = 0; j < q; j++) {
//        double distance = similarity[j + i * q];
        double distance = similarity[i + j * p];
        if (distance > this.gamma) {
          delta[i] = 0;
          break;
        }
      }
    }
    return delta;
  }

  private Set<SimpleMatch> computeSubModel(IPopulation<IFeature> db1, IPopulation<IFeature> db2,
      double[] similarities, int[] delta, double[] length1, double[] length2) {
    System.out.println("computeSubModel");
    int p = db1.size();
    int q = db2.size();
    LinearProgram lp = new LinearProgram(similarities);
    lp.setMinProblem(false);
    for (int i = 0; i < lp.getIsboolean().length; i++) {
      lp.getIsboolean()[i] = true;
    }
    // for all i: (sum of zij for all j <= 1)
    for (int i = 0; i < p; i++) {
      double[] weights = new double[p * q];
      for (int k = 0; k < weights.length; k++) {
        weights[k] = 0.d;
      }
      for (int j = 0; j < q; j++) {
        weights[j + i * q] = 1.0d;
      }
      lp.addConstraint(new LinearSmallerThanEqualsConstraint(weights, 1.0d, "c1_" + i));
    }
    // for all j: (sum of zij for all i + delta_j >= 1)
    for (int j = 0; j < q; j++) {
      double[] weights = new double[p * q];
      for (int k = 0; k < weights.length; k++) {
        weights[k] = 0.d;
      }
      for (int i = 0; i < p; i++) {
        weights[j + i * q] = 1.d;
      }
      /*
      if (1 == delta[j])
        System.out.println("feature j " + j + " has delta = 1");
        */
      lp.addConstraint(new LinearBiggerThanEqualsConstraint(weights, (1 - delta[j]), "c2_" + j));
    }
    // for all j: (sum of zij * li for all i <= kj*beta)
    for (int j = 0; j < q; j++) {
      double[] weights = new double[p * q];
      for (int k = 0; k < weights.length; k++) {
        weights[k] = 0.d;
      }
      for (int i = 0; i < p; i++) {
        weights[j + i * q] = length1[i];
      }
      lp.addConstraint(new LinearSmallerThanEqualsConstraint(weights, this.beta * length2[j], "c3_"
          + j));
    }
    LinearProgramSolver solver = SolverFactory.newDefault();
    double[] solution = solver.solve(lp);
    System.out.println("solution " + solution.length);
    Set<SimpleMatch> result = new HashSet<SimpleMatch>();
    for (int i = 0; i < solution.length; i++) {
      if (solution[i] == 1.0) {
        int rj = i % q;
        int ri = i / q;
        // System.out.println(ri + ", " + rj + " = " + solution[i]);
        IFeature f1 = db1.get(ri);
        IFeature f2 = db2.get(rj);
        result.add(new SimpleMatch(f1, f2));
      }
    }
    return result;
  }

  private Set<SimpleMatch> computeSubModel1() {
    System.out.println("computeSubModel1");
    int p = this.db1.size();
    int q = this.db2.size();
    LinearProgram lp = new LinearProgram(this.s1);
    for (int i = 0; i < lp.getIsboolean().length; i++) {
      lp.getIsboolean()[i] = true;
    }
    // for all i: (sum of zij for all j <= 1)
    for (int i = 0; i < p; i++) {
      double[] weights = new double[p * q];
      for (int k = 0; k < weights.length; k++) {
        weights[k] = 0.d;
      }
      for (int j = 0; j < q; j++) {
        weights[j + i * q] = 1.0d;
      }
      lp.addConstraint(new LinearSmallerThanEqualsConstraint(weights, 1.0d, "c1_" + i));
    }
    // for all j: (sum of zij for all i + delta_j >= 1)
    for (int j = 0; j < q; j++) {
      double[] weights = new double[p * q];
      for (int k = 0; k < weights.length; k++) {
        weights[k] = 0.d;
      }
      for (int i = 0; i < p; i++) {
        weights[j + i * q] = 1.d;
      }
      lp.addConstraint(new LinearBiggerThanEqualsConstraint(weights, 1.d - this.delta2[j], "c2_"
          + j));
    }
    // for all j: (sum of zij * li for all i <= kj*beta)
    for (int j = 0; j < q; j++) {
      double[] weights = new double[p * q];
      for (int k = 0; k < weights.length; k++) {
        weights[k] = 0.d;
      }
      for (int i = 0; i < p; i++) {
        weights[j + i * q] = this.length1[i];
      }
      lp.addConstraint(new LinearSmallerThanEqualsConstraint(weights, this.beta * this.length2[j],
          "c3_" + j));
    }
    lp.setMinProblem(false);
    LinearProgramSolver solver = SolverFactory.newDefault();
    double[] solution = solver.solve(lp);
    System.out.println("solution " + solution.length);
    Set<SimpleMatch> result = new HashSet<SimpleMatch>();
    for (int i = 0; i < solution.length; i++) {
      if (solution[i] > 0) {
        int rj = i % q;
        int ri = i / q;
        // System.out.println(ri + ", " + rj + " = " + solution[i]);
        IFeature f1 = this.db1.get(ri);
        IFeature f2 = this.db2.get(rj);
        result.add(new SimpleMatch(f1, f2));
      }
    }
    return result;
  }

  private Set<SimpleMatch> computeSubModel2() {
    System.out.println("computeSubModel2");
    int p = this.db1.size();
    int q = this.db2.size();
    LinearProgram lp = new LinearProgram(this.s2);
    for (int i = 0; i < lp.getIsboolean().length; i++) {
      lp.getIsboolean()[i] = true;
    }
    // for all j: (sum of zji for all i <= 1)
    for (int j = 0; j < q; j++) {
      double[] weights = new double[p * q];
      for (int k = 0; k < weights.length; k++) {
        weights[k] = 0.d;
      }
      for (int i = 0; i < p; i++) {
        weights[i + j * p] = 1.d;
      }
      lp.addConstraint(new LinearSmallerThanEqualsConstraint(weights, 1.d, "c1_" + j));
    }
    // for all i: (sum of zji for all j + delta_i >= 1)
    for (int i = 0; i < p; i++) {
      double[] weights = new double[p * q];
      for (int k = 0; k < weights.length; k++) {
        weights[k] = 0.d;
      }
      for (int j = 0; j < q; j++) {
        weights[i + j * p] = 1.d;
      }
      lp.addConstraint(new LinearBiggerThanEqualsConstraint(weights, 1.d - this.delta1[i], "c2_"
          + i));
    }
    // for all i: (sum of zji * kj for all j <= li*beta)
    for (int i = 0; i < p; i++) {
      double[] weights = new double[p * q];
      for (int k = 0; k < weights.length; k++) {
        weights[k] = 0.d;
      }
      for (int j = 0; j < q; j++) {
        weights[i + j * p] = this.length2[j];
      }
      lp.addConstraint(new LinearSmallerThanEqualsConstraint(weights, this.beta * this.length1[i],
          "c3_" + i));
    }
    lp.setMinProblem(false);
    LinearProgramSolver solver = SolverFactory.newDefault();
    double[] solution = solver.solve(lp);
    System.out.println("solutions: " + solution.length);
    Set<SimpleMatch> result = new HashSet<SimpleMatch>();
    for (int i = 0; i < solution.length; i++) {
      if (solution[i] > 0) {
        int ri = i % p;
        int rj = i / p;
        // System.out.println(ri + ", " + rj + " = " + solution[i]);
        IFeature f1 = this.db1.get(ri);
        IFeature f2 = this.db2.get(rj);
        result.add(new SimpleMatch(f1, f2));
      }
    }
    return result;
  }

  public Matchings getMatchings(String idAttributeName1, String idAttributeName2) {
    UndirectedGraph<IFeature, Link> graph = new SimpleWeightedGraph<IFeature, Link>(Link.class);
    for (IFeature f : this.db1) {
      graph.addVertex(f);
    }
    for (IFeature f : this.db2) {
      graph.addVertex(f);
    }
    for (SimpleMatch l : this.subModel1Links) {
      graph.addEdge(l.f1, l.f2);
    }
    for (SimpleMatch l : this.subModel2Links) {
      graph.addEdge(l.f1, l.f2);
    }
    ConnectivityInspector<IFeature, Link> inspector = new ConnectivityInspector<IFeature, Link>(
        graph);
    List<Set<IFeature>> links = inspector.connectedSets();

    Matchings res = new Matchings();
    Matching match = new Matching();
    List<Pattern> patterns = match.getPattern();
    int linkId = 0;
    GF_AttributeType at1 = db1.getFeatureType().getFeatureAttributeByName(idAttributeName1);
    GF_AttributeType at2 = db2.getFeatureType().getFeatureAttributeByName(idAttributeName2);
    for (Set<IFeature> link : links) {
      String in = "";
      String out = "";
      // System.out.println(link);
      Pattern pattern = new Pattern();
      for (IFeature f : link) {
        if (db1.contains(f)) {
          pattern.getIn().add("0." + f.getAttribute(idAttributeName1));
          in += "\t" + f.getGeom() + "\n";
          f.setAttribute(at1, linkId);
        } else {
          pattern.getOut().add("1." + f.getAttribute(idAttributeName2));
          out += "\t" + f.getGeom() + "\n";
          f.setAttribute(at2, linkId);
        }
      }
      if (!pattern.getIn().isEmpty() && !pattern.getOut().isEmpty()) {
        patterns.add(pattern);
        System.out.println("LINK " + linkId);
        System.out.println("\tIN");
        System.out.println(in);
        System.out.println("\tOUT");
        System.out.println(out);
      }
      linkId++;
    }
    res.getMatching().add(match);
    return res;
  }

  private ILineString getLineString(IFeature f) {
    IGeometry geom = f.getGeom();
    if (geom instanceof ILineString) {
      return (ILineString) f.getGeom();
    }
    if (geom instanceof IMultiCurve<?>) {
      IMultiCurve<?> mc = (IMultiCurve<?>) geom;
      if (mc.size() == 1 && mc.get(0) instanceof ILineString) {
        return (ILineString) mc.get(0);
      }
    }
    return null;
  }

  public class SimpleMatch {
    IFeature f1;
    IFeature f2;

    public IFeature getF1() {
      return this.f1;
    }

    public IFeature getF2() {
      return this.f2;
    }

    public SimpleMatch(IFeature f1, IFeature f2) {
      this.f1 = f1;
      this.f2 = f2;
    }
  }

  private double[] computeAffineTransform() {
    // build subregions
    IEnvelope envelope = this.db1.getEnvelope();
    double minx = envelope.minX();
    double miny = envelope.minY();
    double maxx = envelope.maxX();
    double maxy = envelope.maxY();
    int n = (int) Math.ceil(Math.sqrt(this.k));
    double width = (maxx - minx) / n;
    double height = (maxy - miny) / n;
    IDirectPosition[] points = new IDirectPosition[n * n];
    double[][] values = new double[2 * n * n][6];

    VariableFactory<IDirectPosition> vf = new VariableFactory<IDirectPosition>();
    VariableSet<IDirectPosition> vs = new VariableSet<IDirectPosition>(vf);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        double x = minx + i * width;
        double y = miny + j * height;
        IPolygon polygon = new GM_Envelope(x, x + width, y, y + height).getGeom();
        Collection<IFeature> features = this.db1.select(polygon);
        Iterator<IFeature> it = features.iterator();
        while (it.hasNext()) {
          IFeature f = it.next();
          double delta = this.delta1[this.map1.get(f)];
          if (delta == 0) {
            it.remove();
          }
        }
        // TODO check delta1
        if (!features.isEmpty()) {
          ILineString geom = getLineString(features.iterator().next());
          IDirectPosition point = null;
          for (IDirectPosition p : geom.getControlPoint()) {
            if (p.toGM_Point().intersects(polygon)) {
              point = p;
              break;
            }
          }
          points[i * n + j] = point;
          values[(i * n + j) * 2][0] = 1.;
          values[(i * n + j) * 2][1] = point.getX();
          values[(i * n + j) * 2][2] = point.getY();
          values[(i * n + j) * 2 + 1][3] = 1.;
          values[(i * n + j) * 2 + 1][4] = point.getX();
          values[(i * n + j) * 2 + 1][5] = point.getY();
          Variable<IDirectPosition> var = vf.newVariable();
          Collection<IFeature> selected2 = this.db2.select(point.toGM_Point()
              .buffer(this.selection));
          Iterator<IFeature> it2 = selected2.iterator();
          // TODO check delta2
          while (it2.hasNext()) {
            IFeature f = it2.next();
            double delta = this.delta2[this.map2.get(f)];
            if (delta == 0) {
              it2.remove();
            }
          }
          for (IFeature f : selected2) {
            ILineString g = getLineString(f);
            for (IDirectPosition dp : g.getControlPoint()) {
              if (dp.distance(point) < this.selection) {
                var.add(dp);
              }
            }
          }
          vs.add(var);
          System.out.println(var.size() + " candidate points");
          if (var.size() == 0) {
            System.out.println(geom);
          }
        } else {
          System.out.println("CRAP " + polygon);
          System.exit(1);
        }
      }
    }
    System.out.println("VS " + vs.getNumberOfVariables() + " variables "
        + vs.getNumberOfConfigurations() + " configurations");
    // System.exit(1);

    Matrix matrixA = new Matrix(values);
    ConfigurationSet<IDirectPosition> cs = new ConfigurationSet<IDirectPosition>(vs);
    cs.addAllConfigurations();
    double minResidual = Double.POSITIVE_INFINITY;
    Matrix minTransform = null;
    Configuration<IDirectPosition> minConf = null;
    long ts = System.currentTimeMillis();
    for (int i = 0; i < cs.getNumberOfConfiguration(); i++) {
      // System.out.println("configuration " + i);
      Configuration<IDirectPosition> conf = cs.getConfiguration(i);
      List<IDirectPosition> list = conf.getAsList();
      double[][] vals = new double[2 * n * n][1];
      for (int j = 0; j < list.size(); j++) {
        vals[j * 2][0] = list.get(j).getX();
        vals[j * 2 + 1][0] = list.get(j).getY();
      }
      Matrix matrixB = new Matrix(vals);
      Matrix result = matrixA.solve(matrixB);
      Matrix Residual = matrixA.times(result).minus(matrixB);
      double rnorm = Residual.normInf();
      if (rnorm < minResidual) {
        minResidual = rnorm;
        minConf = conf;
        minTransform = result;
      }
    }
    long te = System.currentTimeMillis();
    System.out.println("computation took " + (te - ts) + " ms");
    System.out.println("minresidual = " + minTransform.get(0, 0) + " " + minTransform.get(1, 0)
        + " " + minTransform.get(2, 0) + " " + minTransform.get(3, 0) + " "
        + minTransform.get(4, 0) + " " + minTransform.get(5, 0));

    // best conf
    List<IDirectPosition> list = minConf.getAsList();
    for (int i = 0; i < list.size(); i++) {
      IDirectPosition p1 = points[i];
      IDirectPosition p2 = list.get(i);
      ILineString ll = new GM_LineString(p1, p2);
      System.out.println(ll);
    }
    // apply transform
    return new double[] { minTransform.get(0, 0), minTransform.get(1, 0), minTransform.get(2, 0),
        minTransform.get(3, 0), minTransform.get(4, 0), minTransform.get(5, 0),
        minTransform.get(6, 0) };
  }
}
