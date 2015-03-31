/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/

package fr.ign.cogit.geoxygene.contrib.leastsquaresgeneric;

import java.util.ArrayList;





//=======================================================================
// Main class for generic least squares estimator
// Date : 28/03/2015
// Contact : yann.meneroux@ign.fr
//=======================================================================
public class Main {


	public static void main(String[] args) {
		
		
		
		ArrayList<Double> X = new ArrayList<Double>();
		ArrayList<Double> Y = new ArrayList<Double>();
	
		System.out.println("M = [");
		
		for (double x=0; x<4000; x+=3){
			
			double err = (Math.random()-0.5)*Math.sqrt(12)/50;
			double y = 0.01*x+10+20*Math.sin(0.01*x)+10*Math.sin(0.1*x)+2*Math.sin(x)+err;
			
			System.out.println(x+","+y);
			
			X.add(x);
			Y.add(y);
			
		}
		
		System.out.println("];");
		
		
		Solver solver = new Solver();
		
		for (int i=0; i<X.size(); i++){
			
			double x = X.get(i);
			double y = Y.get(i);
			
			solver.addConstraint(new Constraint("a*"+x+" + b + k1*sin(theta1*"+x+") + k2*sin(theta2*"+x+") = "+y));
			
		}
		
		solver.addParameter("a", 1);
		solver.addParameter("b", 1);
		solver.addParameter("k1", 1);
		solver.addParameter("k2", 1);
		solver.addParameter("theta1", 1);
		solver.addParameter("theta2", 1);
		
		
		solver.setIterationsNumber(15);
		
		solver.compute();
		
		solver.displayResults();
		
		
		// Exemple page 174
	
	/*	Constraint c11 = new Constraint("Tx + k*cos(theta)*0.32 + k*sin(theta)*1.50 = 261000");
		Constraint c12 = new Constraint("Ty - k*sin(theta)*0.32 + k*cos(theta)*1.50 = 608000");
		
		Constraint c21 = new Constraint("Tx + k*cos(theta)*0.15 + k*sin(theta)*1.25 = 256000");
		Constraint c22 = new Constraint("Ty - k*sin(theta)*0.15 + k*cos(theta)*1.25 = 604000");
		
		Constraint c31 = new Constraint("Tx + k*cos(theta)*1.02 + k*sin(theta)*0.75 = 275000");
		Constraint c32 = new Constraint("Ty - k*sin(theta)*1.02 + k*cos(theta)*0.75 = 589000");
		
		Solver solver = new Solver();
		
		solver.addConstraint(c11);
		solver.addConstraint(c12);
		solver.addConstraint(c21);
		solver.addConstraint(c22);
		solver.addConstraint(c31);
		solver.addConstraint(c32);
		
		solver.addParameter("Tx", 20000);
		solver.addParameter("Ty", 50000);
		solver.addParameter("k", 20000);
		solver.addParameter("theta", 0.1);
		
		solver.setIterationsNumber(100);
		solver.setConvergenceCriteria(0.001);
		
		solver.compute();
		
		
		System.out.println("Après "+solver.getEffectiveIterationsNumber()+" itérations :");
		System.out.println("Tx  = "+solver.getParameter("Tx")+" m");
		System.out.println("Ty  = "+solver.getParameter("Ty")+" m");
		System.out.println("k  = "+solver.getParameter("k"));
		System.out.println("theta  = "+solver.getParameter("theta")*180/Math.PI+" °");
		
	*/
		
	}

}
