package fr.ign.cogit.geoxygene.contrib.leastsquares;


import java.util.ArrayList;

import org.junit.Test;

import fr.ign.cogit.geoxygene.contrib.leastsquaresgeneric.Constraint;
import fr.ign.cogit.geoxygene.contrib.leastsquaresgeneric.Solver;
import junit.framework.Assert;

public class LeastSquaresGenericTest {


	@Test
	public void test1() {

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

		double TxChapeau = solver.getParameter("Tx");
		double TyChapeau = solver.getParameter("Ty");
		double kChapeau = solver.getParameter("k");
		double thetaChapeau = solver.getParameter("theta"); 

		double Tx = 248716.4310955899;
		double Ty = 574847.5915941315;
		double k = 23436.514058778277;
		double theta = 0.13771673494441908; 

		Assert.assertEquals(Tx, TxChapeau, Math.pow(10, -6));
		Assert.assertEquals(Ty, TyChapeau, Math.pow(10, -6));
		Assert.assertEquals(k, kChapeau, Math.pow(10, -6));
		Assert.assertEquals(theta, thetaChapeau, Math.pow(10, -6));

	}

	@Test
	public void test2() {

		ArrayList<Double> T = new ArrayList<Double>();
		ArrayList<Double> D = new ArrayList<Double>();

		T.add(0.038); D.add(0.050); 
		T.add(0.194); D.add(0.127);
		T.add(0.425); D.add(0.094);
		T.add(0.626); D.add(0.212);
		T.add(1.253); D.add(0.273);
		T.add(2.500); D.add(0.267);
		T.add(3.740); D.add(0.332);


		Solver solver = new Solver();


		for (int i=0; i<T.size(); i++){

			double t = T.get(i);
			double d = D.get(i);

			solver.addConstraint(new Constraint("p1*"+t+"/(p2*"+t+"+1) = "+d+""));

		}


		solver.addParameter("p1", 1);
		solver.addParameter("p2", 1);

		solver.setIterationsNumber(10);
		solver.setConvergenceCriteria(0.001);


		solver.compute();


		double p1 = solver.getParameter("p1");
		double p2 = solver.getParameter("p2");

		double p1Chapeau = 0.6488940374077881;
		double p2Chapeau = 1.7899669235296043;

		Assert.assertEquals(p1, p1Chapeau, Math.pow(10, -6));
		Assert.assertEquals(p2, p2Chapeau, Math.pow(10, -6));

	}

	@Test
	public void test3() {

		// Calcul de la moyenne

		Solver solver = new Solver();

		double controle = 0;

		for (int i=0; i<100; i++){

			double x = Math.random()*20;
			controle += x;

			solver.addConstraint(new Constraint("moyenne = "+x));

		}

		controle /= solver.getConstraints().size();

		solver.addParameter("moyenne", 10);

		solver.setIterationsNumber(10);

		solver.compute();

		Assert.assertEquals(solver.getParameter("moyenne"), controle, Math.pow(10, -4));

	}

	@Test
	public void test4(){

		// Contraintes indicatives
		Constraint c1 = new Constraint("a = 60.27");
		Constraint c2 = new Constraint("b = 40.54");
		Constraint c3 = new Constraint("c = 83.12");
		
		// Contrainte impérative
		Constraint c4 = new Constraint("a+b+c := 180");

		Solver solver = new Solver();

		solver.addConstraint(c1);
		solver.addConstraint(c2);
		solver.addConstraint(c3);
		solver.addConstraint(c4);

		solver.addParameter("a", 1);
		solver.addParameter("b", 1);
		solver.addParameter("c", 1);

		solver.setIterationsNumber(5);

		solver.compute();



		double somme = solver.getParameter("a")+solver.getParameter("b")+solver.getParameter("c");

		Assert.assertEquals(somme, 180, Math.pow(10, -4));	

	}

	@Test
	public void test5(){

		// ------------------------------------------------------------
		// Cercle solution
		// ------------------------------------------------------------
		double Xc = 120;
		double Yc = 50;
		double Rc = 80;
		// ------------------------------------------------------------


		// ------------------------------------------------------------
		// Simulation de la mesure des points 
		// ------------------------------------------------------------

		int N = 200;

		ArrayList<Double> X = new ArrayList<Double>();
		ArrayList<Double> Y = new ArrayList<Double>();

		for (int i=0; i<N; i++){

			double r = Rc + 3*(Math.random()-0.5)*Math.sqrt(12);
			double t = Math.random()*2*Math.PI;

			X.add(Xc+r*Math.cos(t));
			Y.add(Yc+r*Math.sin(t));
			
		}


		// ------------------------------------------------------------
		// Estimation par moindres carrés
		// ------------------------------------------------------------

		Solver solver = new Solver();

		for (int i=0; i<N; i++){

			double x = X.get(i);
			double y = Y.get(i);

			Constraint c = new Constraint("sqrt(("+x+"-Xc)^2 + ("+y+"-Yc)^2) - Rc = 0 +/- 1.0");

			solver.addConstraint(c);

		}

		solver.addParameter("Xc", 100);
		solver.addParameter("Yc", 100);
		solver.addParameter("Rc", 100);

		solver.setIterationsNumber(10);

		solver.compute();

		double XcChap = solver.getParameter("Xc");
		double YcChap = solver.getParameter("Yc");
		double RcChap = solver.getParameter("Rc");
		
		Assert.assertEquals(XcChap, Xc, 1);	
		Assert.assertEquals(YcChap, Yc, 1);	
		Assert.assertEquals(RcChap, Rc, 1);	

	}

}
