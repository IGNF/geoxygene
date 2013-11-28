package fr.ign.cogit.process.geoxygene.netmatching;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LinearProgram;
import fr.ign.cogit.process.geoxygene.GeoxygeneProcess;


@DescribeProcess(title = "LinearFeatureMatcher", description = "Implementation of Li and Goodchild's approach for linear feature matching presented in Linna Li & Michael Goodchild (2011): An optimisation model for linear feature matching in geographical data conflation, International Journal of Image and Data Fusion, 2:4, 309-328.")
public class LinearFeatureMatcherProcess implements GeoxygeneProcess {
  
  @DescribeResult(name = "OK", description = "OK")
  public String execute() {
    
    System.out.println("---");
    
    LinearProgram lp = new LinearProgram(new double[]{5.0, 10.0});
    lp.addConstraint(new LinearBiggerThanEqualsConstraint(new double[]{3.0, 1.0}, 8.0, "c1"));
    lp.addConstraint(new LinearBiggerThanEqualsConstraint(new double[]{0.0, 4.0}, 4.0, "c2"));
    lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{2.0, 0.0}, 2.0, "c3"));
    lp.setMinProblem(true);
    
    LinearProgramSolver solver  = SolverFactory.newDefault();
    double[] sol = solver.solve(lp);
    for (int i = 0; i < sol.length; i++) {
      System.out.println(sol[i]);
    }
    System.out.println("---");
    
    return "OK";
    
  }

}
