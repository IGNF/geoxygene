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



//=======================================================================
// Main class for generic least squares estimator
// Date : 28/03/2015
// Contact : yann.meneroux@ign.fr
//=======================================================================
public class Main {


	public static void main(String[] args) {


		// Exemple page 174 livre "Estimation par moindres carrés" (Collection ENSG)

		Constraint c11 = new Constraint("Tx + k*cos(theta)*0.32 + k*sin(theta)*1.50 = 261000");
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




		/*		ArrayList<Double> T = new ArrayList<Double>();
		ArrayList<Double> D = new ArrayList<Double>();

		T.add(0.038); D.add(0.050); 
		T.add(0.194); D.add(0.127);
		T.add(0.425); D.add(0.094);
		T.add(0.626); D.add(0.212);
		T.add(1.253); D.add(0.273);
		T.add(2.500); D.add(0.267);
		T.add(3.740); D.add(0.332);


		Solver solver = new Solver();


		System.out.println("M=[");

		for (int i=0; i<T.size(); i++){

			double t = T.get(i);
			double d = D.get(i);

			System.out.println(t+","+d);

			solver.addConstraint(new Constraint("p1*"+t+"/(p2*"+t+"+1) = "+d+""));

		}

		System.out.println("];");

		solver.addParameter("p1", 1);
		solver.addParameter("p2", 1);

		solver.setIterationsNumber(10);
		solver.setConvergenceCriteria(0.001);

		solver.compute();

		solver.displayResults();

		 */


	}

}
