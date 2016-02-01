package fr.ign.cogit.geoxygene.contrib.randomforests;

import interpolation.core.gui.ColorMap;
import interpolation.core.gui.Graphics;
import interpolation.core.interp.OrdinaryKriging;
import interpolation.core.objects.Map;
import interpolation.core.objects.Observation;




public class Main {

	public static void main(String[] args) {


		TrainingDataSet TDS = new TrainingDataSet();

		for (int i=0; i<200; i++){

			String z = "c2";
	
			String t = "homme";
			if (Math.random() < 0.0){t = "femme";}
	
			double x = Math.random()*200;
			double y = Math.random()*200;
			double r = (Math.random()-0.5)*30;
			

			if (t == "homme"){
				if (x+2+r < y){z = "c1";}
			}
			
			if (t == "femme"){
				if (200-x+r > y){z = "c1";}
			}
			
			InputData X = new InputData();

			X.addFeature("x", x);
			X.addFeature("y", y);
			X.addFeature("type", t);


			OutputData Y = new OutputData("z", z);

			TDS.addData(new TrainingData(X, Y));

		}
		

		DecisionTree tree = new DecisionTree(DecisionTree.MODE_CLASSIFICATION);
		tree.setMaxDepth(5);
		tree.setVerbose(true);
		tree.setRandomness(0.5);

		tree.build(TDS);


		/*
		
		RandomForest forest = new RandomForest(RandomForest.MODE_CLASSIFICATION, 150);
		forest.setVerbose(true);
		forest.setBagging(100);
		forest.setRandomness(0);
	

		forest.build(TDS);

	*/
		// Représentation
		Map map = new Map(0,0,200,200,0.5);
		OrdinaryKriging ok = new OrdinaryKriging();

		for (int i=0; i<500; i++){

			double x = Math.random()*200;
			double y = Math.random()*200;

			InputData X = new InputData();
			X.addFeature("x", x);
			X.addFeature("y", y);

			double z = tree.posterior("c1", X);

			Observation obs = new Observation(x, y, z);

			ok.add(obs);

		}

		ok.estimate(map);

		Graphics g = new Graphics(map, ColorMap.TYPE_GRAVITY);
		g.setVisible(true);


	}

}
