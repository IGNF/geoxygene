package fr.ign.cogit.geoxygene.contrib.leastsquaresgeneric;

import java.util.Stack;

//=======================================================================
// Class to build and eval expressions through reverse polish notation
// Date : 28/03/2015
// Contact : yann.meneroux@ign.fr
//=======================================================================
public class ReversePolishNotation {


	private String stdForm;  // Standard formulation	
	private String rpnForm;  // RPN formulation

	int NSTACK = 1000;

	private String[] CARSPE = {
			")",
			"(",
			"]",
			"[",
			"+",
			"-",
			"/",
			"*",
			"^",
			"%",
			"~"
	};

	private String[] CARSPE_PRIO = {
			"-2",
			"-1",
			"-4",
			"-3",
			"3",
			"3",
			"2",
			"2",
			"1",
			"2",
			"2"
	};

	private String[] FM = Functions.getNamesTable();

	// Pile initiale
	String[][] PILE_INI;

	// Pile RPN
	Stack<String> PILE;

	// Pile annexe
	Stack<String> PILEAN;

	public String getStdForm(){return stdForm;}
	public String getRpnForm(){return rpnForm;}

	public void displayPileIni(){
		for (int i=0; i<PILE_INI.length; i++){System.out.print(PILE_INI[i][0]+" ");} System.out.println();
		for (int i=0; i<PILE_INI.length; i++){System.out.print(PILE_INI[i][1]+" ");} System.out.println();

	}

	public void displayPile(){for (int i=0; i<PILE.size(); i++){System.out.print(PILE.get(i)+" ");}System.out.println();}
	public void displayPilean(){for (int i=0; i<PILEAN.size(); i++){System.out.print(PILEAN.get(i)+" ");}System.out.println();}


	// -------------------------------------------------------------------
	// General method to build RPN expression
	// Input : mathematical expression (string)
	// -------------------------------------------------------------------
	public ReversePolishNotation(String expression){

		this.stdForm = expression;
		this.rpnForm = "";

		// Marqueurs de positions
		int pos1 = 0;
		int pos2 = 0;

		// Pile initiale 
		PILE_INI = new String[NSTACK][2];

		// Pile RPN
		PILE = new Stack<String>();

		// Pile annexe
		PILEAN = new Stack<String>();

		// Element de pile
		String item;

		// Priorités
		int prio = 0;
		int prio_som = 0;

		// -------------------------------------------------------------------
		//Gestion des fonctions mathématiques
		// -------------------------------------------------------------------
		for (int i=0; i<FM.length; i++){
			if ((expression.indexOf(FM[i]+"(")<5000) && (expression.indexOf(FM[i]+"(")!=-1)){ 
				int comptParO = 1;
				int comptParF = 0;
				int posF = expression.indexOf(FM[i]+"(")+FM[i].length();
				int posP1 = posF;
				expression = expression.substring(0,posP1)+"$["+expression.substring(posP1+1,expression.length());
				while(comptParF!=comptParO){
					if (posF<expression.length()-1){
						posF++;
						if(expression.substring(posF, posF+1).equals("(")){
							comptParO++;
						}
						if(expression.substring(posF, posF+1).equals(")")){
							comptParF++;
						}
					}
					else{

						System.out.println("Error : parenthesis");
						System.exit(0);
					}
				}
				int posP2 = posF;
				expression = expression.substring(0,posP2)+"]"+expression.substring(posP2+1,expression.length());
				i--; // S'il y a plusieurs fois la même fonction
			}
		}

		// -------------------------------------------------------------------
		//Gestion des signes moins monadiques
		// -------------------------------------------------------------------
		for (int i=0; i<expression.length()-1; i++){
			if (expression.substring(i, i+1).equals("-")){                  
				if (i==0){
					expression = "0"+expression;
				}
				else{
					if ((expression.substring(i-1, i).equals("("))||(expression.substring(i-1, i).equals("["))){
						expression  = expression.substring(0,i)+"0"+expression.substring(i,expression.length());
					}
				}
				i=i+2;
			}
		}

		this.stdForm = expression;

		// -------------------------------------------------------------------
		// Création de la pile initiale
		// -------------------------------------------------------------------

		int N = 0;

		for (int i=0; i<expression.length(); i++){
			for (int j=0; j<CARSPE.length; j++){
				if (expression.substring(i, i+1).equals(CARSPE[j])){

					pos2 = i;

					if (pos2>pos1){
						PILE_INI[N][0] = expression.substring(pos1,pos2);
						PILE_INI[N][1] = "0"; //Ce n'est pas un caractère spécial
						N++; 
					}

					PILE_INI[N][0] = expression.substring(pos2,pos2+1);
					PILE_INI[N][1] = CARSPE_PRIO[j]; //C'est un caractère spécial
					N++;
					pos1 = pos2+1;

				}
			}
		}

		if (pos1 != expression.length()){ // Correction si on est pas à la fin
			PILE_INI[N][0] = expression.substring(pos1,expression.length());
			PILE_INI[N][1] = "0";
			N++;
		}

		N--; //Correction de la taille de la pile initiale

		// -------------------------------------------------------------------
		// Formulation de la polonaise inverse
		// -------------------------------------------------------------------
		for (int i=0; i<=N; i++){

			item = PILE_INI[i][0];
			prio = Integer.parseInt(PILE_INI[i][1]);

			if (prio==0){ // Opérande
				PILE.push(item);
			}

			if (prio==-1){ //Parenthèse ouvrante
				PILEAN.push(item);
			}

			if (prio==-3){ //Crochet
				PILEAN.push(item);
			}

			if (prio==-4){ //Crochet fermant
				while(!PILEAN.lastElement().equals("[")){
					PILE.push(PILEAN.pop());
				}
				PILE.push("!f"); //Marqueur de fonction
				PILEAN.pop();
			}


			if (prio > 0){ // Opérateur

				if (PILEAN.empty()){
					PILEAN.push(item);
				}
				else{ 

					while((PILEAN.lastElement().equals("+"))||(PILEAN.lastElement().equals("-"))||(PILEAN.lastElement().equals("*"))||(PILEAN.lastElement().equals("/"))||(PILEAN.lastElement().equals("^"))||(PILEAN.lastElement().equals("%"))||(PILEAN.lastElement().equals("~"))){
						if (PILEAN.lastElement().equals("+")){prio_som = 3;}
						if (PILEAN.lastElement().equals("-")){prio_som = 3;}
						if (PILEAN.lastElement().equals("%")){prio_som = 2;}
						if (PILEAN.lastElement().equals("~")){prio_som = 2;}
						if (PILEAN.lastElement().equals("*")){prio_som = 2;}
						if (PILEAN.lastElement().equals("/")){prio_som = 2;}
						if (PILEAN.lastElement().equals("^")){prio_som = 1;}


						if (prio_som<=prio){
							PILE.push(PILEAN.pop());
						}
						else{
							break;
						}

						if(PILEAN.empty()){break;}

					}

					PILEAN.push(item);

				}

			}

			if (prio==-2){ //Parenthèse fermante

				if (PILEAN.empty()){
					System.out.println("Error on expression : "+expression +" parenthesis");
					System.exit(0);
				}

				while(!PILEAN.lastElement().equals("(")){
					if (!PILEAN.empty()){
						PILE.push(PILEAN.pop());
					}
					else{
						System.out.println("Error on expression : "+expression +" parenthesis");
						System.exit(0);
					}

				}

				PILEAN.pop();

			}

		}

		//On vide la pile annexe
		while(!PILEAN.empty()){
			if (PILEAN.lastElement().equals("(")){
				System.out.println("Error on expression : "+expression +" parenthesis");
				System.exit(0);
			}
			PILE.push(PILEAN.pop());
		}

		// Remplissage de rpnform
		for (int i=0; i<PILE.size(); i++){

			rpnForm += PILE.get(i);

			if (i != PILE.size()-1){rpnForm += " ";}

		}


	}

	// -------------------------------------------------------------------
	// ToString method redefinition
	// Output : constraint in rpn form
	// -------------------------------------------------------------------
	public String toString(){

		return this.rpnForm;

	}

}