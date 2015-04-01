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
// Static class to gather mathematical standard functions
// Date : 29/03/2015
// Contact : yann.meneroux@ign.fr
//=======================================================================

public class Functions {

	// 32 functions
	private static String[] FM = {
		"cos",
		"sin",
		"tan",
		"acos",
		"asin",
		"atan",
		"cosh",
		"sinh",
		"tanh",
		"acosh",
		"asinh",
		"atanh",
		"sinc",
		"E",
		"exp",
		"ln",
		"log",
		"sqrt",
		"factorial",
		"abs",
		"sgn",
		"log2",
		"frac",
		"Lgv",    
		"logit",
		"sgm",    
		"sec",    
		"cotan",
		"cotanh",
		"rand",     
		"randn",    
		"round"    
	};


	// -------------------------------------------------------------------
	// Method to get functions table
	// -------------------------------------------------------------------
	public static String[] getNamesTable(){return FM;}


	// -------------------------------------------------------------------
	// Method to compute a function in a value
	// Input : function code and argument
	// -------------------------------------------------------------------
	public static double eval(double code, double arg){

		double calcul = 0;

		// ---------------------------------------------------------------
		// Cosinus 
		// ---------------------------------------------------------------
		if (code==0){calcul = Math.cos(arg);}

		// ---------------------------------------------------------------
		// Sinus
		// ---------------------------------------------------------------
		if (code==1){calcul = Math.sin(arg);}

		// ---------------------------------------------------------------
		// Tangente
		// ---------------------------------------------------------------
		if (code==2){calcul = Math.tan(arg);}

		// ---------------------------------------------------------------
		// ArcCosinus
		// ---------------------------------------------------------------
		if (code==3){
			if ((arg>1)||(arg<-1)){
				System.out.println("Error : arccosinus invalid argument : "+arg);
				System.exit(0);
			}
			calcul = Math.acos(arg);
		}

		// ---------------------------------------------------------------
		// ArcSinus
		// ---------------------------------------------------------------
		if (code==4){
			if ((arg>1)||(arg<-1)){
				System.out.println("Error : arcsinus invalid argument : "+arg);
				System.exit(0);
			}
			calcul = Math.acos(arg);
		}

		// ---------------------------------------------------------------
		// ArcTangente 
		// ---------------------------------------------------------------
		if (code==5){calcul = Math.atan(arg);}

		// ---------------------------------------------------------------
		// Cosinus Hyperbolique
		// ---------------------------------------------------------------
		if (code==6){calcul = Math.cosh(arg);}

		// ---------------------------------------------------------------
		// Sinus Hyperboilique
		// ---------------------------------------------------------------
		if (code==7){calcul = Math.sinh(arg);}

		// ---------------------------------------------------------------
		// Tangente Hyperbolique
		// ---------------------------------------------------------------
		if (code==8){calcul = Math.tanh(arg);}

		// ---------------------------------------------------------------
		// ArgCosinus Hyperbolique
		// ---------------------------------------------------------------
		if (code==9){calcul = Math.log(arg+Math.sqrt(Math.pow(arg,2)-1));}

		// ---------------------------------------------------------------
		// ArgSinus Hyperbolique
		// ---------------------------------------------------------------
		if (code==10){calcul = Math.log(arg+Math.sqrt(Math.pow(arg,2)+1));}

		// ArgTangente Hyperbolique
		if (code==11){calcul = 0.5*Math.log((1-arg)/(1+arg));}

		// ---------------------------------------------------------------
		// Sinus Cardinal
		// ---------------------------------------------------------------
		if (code==12){calcul = Math.sin(arg)/arg;}

		// ---------------------------------------------------------------
		// Partie Entière
		// ---------------------------------------------------------------
		if (code==13){calcul = Math.floor(arg);}

		// ---------------------------------------------------------------
		// Exponentielle
		// ---------------------------------------------------------------
		if (code==14){calcul = Math.exp(arg);}

		// ---------------------------------------------------------------
		// Logarithme en base e
		// ---------------------------------------------------------------
		if (code==15){calcul = Math.log(arg);
		if (arg < 0){
			System.out.println("Error : logartithm invalid argument : "+arg);
			System.exit(0);
		}}
		
		// ---------------------------------------------------------------
		// Logarithme en base 10
		// ---------------------------------------------------------------
		if (code==16){calcul = Math.log10(arg);
		if (arg < 0){
			System.out.println("Error : logartithm invalid argument : "+arg);
			System.exit(0);
		}}
		
		// ---------------------------------------------------------------
		// Racine carrée
		// ---------------------------------------------------------------
		if (code==17){
			if (arg < 0){
				System.out.println("Error : square root invalid argument : "+arg);
				System.exit(0);
			}
			calcul = Math.sqrt(arg);
		}
		
		// ---------------------------------------------------------------
		// Factorielle
		// ---------------------------------------------------------------
		if (code==18){
			if ((Math.floor(arg)!=arg)||(arg<0)){
				System.out.println("Error : Factorial invalid argument : "+arg);
				System.exit(0);
			}
			calcul = 1;
			while(arg>1){
				calcul = arg*calcul;
				arg--;
			} 
		}
		
		// ---------------------------------------------------------------
		// Valeur Absolue
		// ---------------------------------------------------------------
		if (code==19){
			if (arg>0){calcul = arg;}
			if (arg<0){calcul = -arg;}
			if (arg==0){calcul = 0;}
		}
		
		// ---------------------------------------------------------------
		// Signe
		// ---------------------------------------------------------------
		if (code==20){
			if (arg>0){calcul = +1;}
			if (arg<0){calcul = -1;}
			if (arg==0){calcul = 0;}
		}
		
		// ---------------------------------------------------------------
		// Logarithme en base 2
		// ---------------------------------------------------------------
		if (code==21){
			if (arg < 0){
				System.out.println("Error : logartithm invalid argument : "+arg);
				System.exit(0);
			}
			calcul = Math.log(arg)/Math.log(2);
		}
		
		// ---------------------------------------------------------------
		// Partie Fractionnaire
		// ---------------------------------------------------------------
		if (code==22){calcul = arg - Math.floor(arg);}
		
		// ---------------------------------------------------------------
		// Fonction de Langevin
		// ---------------------------------------------------------------
		if (code==23){calcul = 1/Math.tanh(arg)-1/arg;}
		
		// ---------------------------------------------------------------
		// Logit
		// ---------------------------------------------------------------
		if (code==24){
			if ((arg <= 0)||(arg>=1)){
				System.out.println("Error : logit invalid argument : "+arg);
				System.exit(0);
			}
			calcul = Math.log(arg/(1-arg));
		}
		
		// ---------------------------------------------------------------
		// Sigmoïde
		// ---------------------------------------------------------------
		if (code==25){calcul = 1/(1+Math.exp(-arg));}
		
		// ---------------------------------------------------------------
		// Sécante
		// ---------------------------------------------------------------
		if (code==26){calcul = 1/Math.cos(arg);}
		
		// ---------------------------------------------------------------
		// Cotangente
		// ---------------------------------------------------------------
		if (code==27){calcul = 1/Math.tan(arg);}
		
		// ---------------------------------------------------------------
		// Cotangente Hyperbolique
		// ---------------------------------------------------------------
		if (code==28){calcul = 1/Math.tanh(arg); }
		
		// ---------------------------------------------------------------
		// Fonction Aléatoire Continue
		// ---------------------------------------------------------------
		if (code==29){
			calcul = Math.random()/(double)Math.random();
			calcul -= Math.floor(calcul);
			calcul *= arg;
		}
		
		// ---------------------------------------------------------------
		// Fonction Aléatoire Discrète
		// ---------------------------------------------------------------
		if (code==30){
			calcul = Math.random() % (int)(arg+1);
		}
		
		// ---------------------------------------------------------------
		// Arrondi
		// ---------------------------------------------------------------
		if (code==31){
			calcul = Math.floor(arg);
			if ((arg-calcul)>=0.5){calcul = calcul+1;}
		}


		return calcul;
		
	}

}