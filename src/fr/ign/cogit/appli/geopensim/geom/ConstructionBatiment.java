/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 *
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 *
 * See: http://oxygene-project.sourceforge.net
 *
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.geom;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.algo.GenerateurValeur;
import fr.ign.cogit.appli.geopensim.feature.micro.FormeBatiment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * @author Florence Curie
 *
 */
public class ConstructionBatiment {

	private static Logger logger=Logger.getLogger(ConstructionBatiment.class.getName());
	private static double dimensionMinimum = 3.0;
	
	public static IGeometry construire(FormeBatiment formeBatiment,IDirectPosition centre,double aire,double elongation,double epaisseur,double dimensionMax){
		double dimensionMaximum = Double.MAX_VALUE; 
		if (dimensionMax!=-1)dimensionMaximum = dimensionMax * 2;
		// L'aire est indispensable on adapte ensuite la forme en fonction des valeurs d'élongation et d'épaisseur
		// On vérifie que les contraintes sont respectées
		if ((epaisseur!=-1.0)&&(epaisseur<dimensionMinimum)){epaisseur = dimensionMinimum;}
		if ((epaisseur!=-1.0)&&(epaisseur>dimensionMaximum)){epaisseur = dimensionMaximum;}
		if ((elongation!=-1.0)&&((elongation<0)||(elongation>1))){elongation = GenerateurValeur.genererValeurAleatoire(0.1,1);}
		// On construit la forme en fonction des informations disponibles
		IGeometry forme = null;
		if (formeBatiment==FormeBatiment.Carre){
			double cote = Math.sqrt(aire);
			forme = ShapeFactory.createCarre(centre, cote);
			// Si les contraintes de dimension ne sont pas respectées : l'aire est modifiée
			if (cote<dimensionMinimum)cote = dimensionMinimum;
			else if (cote>dimensionMaximum)cote = dimensionMaximum;
		}else if (formeBatiment==FormeBatiment.Rectangle){
			double largeur = 0;
			double hauteur = 0;
			if(epaisseur!=-1){// épaisseur imposée
				hauteur = epaisseur;
				largeur = aire/hauteur;
				// Si les contraintes de dimension ne sont pas respectées : l'aire est modifiée
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
			}else if (elongation!=-1){ // élongation imposée
				hauteur = Math.sqrt(aire*elongation);
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				largeur = aire/hauteur;
				// Si les contraintes de dimension ne sont pas respectées : l'aire est modifiée
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
			}else{// ni épaisseur ni élongation imposées on tire au sort une valeur d'élongation
				double borneInferieure = Math.max(Math.pow(dimensionMinimum,2)/aire,0.01);
				double borneSuperieure = Math.min(Math.pow(dimensionMaximum,2)/aire,1);
				elongation = GenerateurValeur.genererValeurAleatoire(borneInferieure,borneSuperieure);
				largeur = Math.sqrt(aire/elongation);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = aire/largeur;
				// Si les contraintes de dimension ne sont pas respectées : l'aire est modifiée
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
			}
			logger.info("hauteur : "+ hauteur+ " et largeur : "+largeur);
			forme = ShapeFactory.createRectangle(centre, largeur, hauteur);
		}else if (formeBatiment==FormeBatiment.FormeL){
			double largeur = 0;
			double hauteur = 0;
			double epaisseur1 = 0;
			double epaisseur2 = 0;
			// On vérifie que la contrainte sur l'élongation est possible
			if ((elongation!=-1)&&(elongation<Math.pow(dimensionMinimum,2)/aire)){elongation = Math.pow(dimensionMinimum,2)/aire;}
			// On vérifie que la contrainte sur l'épaisseur est possible
			if ((epaisseur!=-1)&&(epaisseur>Math.sqrt(aire))){epaisseur = Math.sqrt(aire);}
			// On fait les calculs pour les différents cas possibles
			if((elongation!=-1.0)&&(epaisseur!=-1.0)){
				// On vérifie que les contraintes sur l'élongation et sur l'épaisseur sont compatibles (la contrainte sur l'élongation saute si ce n'est pas le cas)
				if (elongation<Math.pow(epaisseur,2)/aire){elongation=Math.pow(epaisseur,2)/aire;}
				largeur = (aire+Math.pow(epaisseur,2))/(epaisseur*(elongation+1));
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = elongation * largeur;
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = epaisseur;
				epaisseur2 = epaisseur;
			}else if (epaisseur!=-1.0){
				// On tire au hasard la valeur de l'élongation
				elongation = GenerateurValeur.genererValeurAleatoire(Math.pow(epaisseur,2)/aire,1);
				largeur = (aire+Math.pow(epaisseur,2))/(epaisseur*(elongation+1));
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = elongation * largeur;
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = epaisseur;
				epaisseur2 = epaisseur;
			}else if (elongation!=-1.0){
				// On tire au hasard les valeurs des épaisseurs
				epaisseur1 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt(aire/elongation),dimensionMaximum));
				epaisseur2 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt(aire/(1/elongation)),dimensionMaximum));
				largeur = (aire+epaisseur1*epaisseur2)/(elongation*epaisseur1+epaisseur2);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = elongation * largeur;
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
			}else{ // Ni élongation ni épaisseur imposée on tire au sort ces valeurs
				elongation = GenerateurValeur.genererValeurAleatoire(Math.pow(dimensionMinimum,2)/aire,1);
				epaisseur1 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt(aire/elongation),dimensionMaximum));
				epaisseur2 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt(aire/(1/elongation)),dimensionMaximum));
				largeur = (aire+epaisseur1*epaisseur2)/(elongation*epaisseur1+epaisseur2);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = elongation * largeur;
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
			}
			logger.info("hauteur : "+ hauteur+ " ; largeur : "+largeur+ " ; epaisseur1 : "+epaisseur1+ " ; epaisseur2 : "+epaisseur2);
			forme = ShapeFactory.createL(centre, largeur, hauteur, epaisseur1, epaisseur2);
		}else if (formeBatiment==FormeBatiment.FormeU){
			double largeur = 0;
			double hauteur = 0;
			double epaisseur1 = 0;
			double epaisseur2 = 0;
			// On vérifie que la contrainte sur l'élongation est possible
			if (elongation!=-1){
				double contrainteElongation1 = Math.pow(dimensionMinimum, 2)/aire;
				double contrainteElongation2 = aire/(4*Math.pow(dimensionMinimum, 2));
				if(elongation<contrainteElongation1){elongation = contrainteElongation1;}
				if(elongation>contrainteElongation2){elongation = contrainteElongation2;}
			}
			// On vérifie que la contrainte sur l'épaisseur est possible
			if (epaisseur!=-1){
				double contrainteEpaisseur = Math.sqrt(aire*0.5);
				if(epaisseur>contrainteEpaisseur){epaisseur = contrainteEpaisseur;}
			}
			// On fait les calculs pour les différents cas possibles
			if((elongation!=-1.0)&&(epaisseur!=-1.0)){
				// On vérifie que les contraintes sur l'élongation et sur l'épaisseur sont compatibles (la contrainte sur l'élongation saute si ce n'est pas le cas)
				if (elongation<Math.pow(epaisseur,2)/aire){elongation = Math.pow(epaisseur,2)/aire;}
				if (elongation>aire/(4*Math.pow(epaisseur,2))){elongation = aire/(4*Math.pow(epaisseur,2));}
				largeur = (aire+2*epaisseur*epaisseur)/(2*elongation*epaisseur+epaisseur);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+2*epaisseur*epaisseur)/(2*epaisseur+epaisseur*(1/elongation));
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = epaisseur;
				epaisseur2 = epaisseur;
			}else if (epaisseur!=-1.0){
				// On tire au hasard la valeur de l'élongation
				double borneInf = Math.pow(epaisseur,2)/aire>0 ? Math.pow(epaisseur,2)/aire : 0.01;
				double borneSup = aire/(4*Math.pow(epaisseur,2))<1 ? aire/(4*Math.pow(epaisseur,2)) : 1;
				elongation = GenerateurValeur.genererValeurAleatoire(borneInf,borneSup);
				largeur = (aire+2*epaisseur*epaisseur)/(2*elongation*epaisseur+epaisseur);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+2*epaisseur*epaisseur)/(2*epaisseur+epaisseur*(1/elongation));
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				if (epaisseur>hauteur){epaisseur1=hauteur;} else{epaisseur1 = epaisseur;}
				if (largeur<2*epaisseur){epaisseur2=hauteur;}else{epaisseur2 = epaisseur;}
			}else if (elongation!=-1.0){
				// On tire au hasard les valeurs des épaisseurs
				epaisseur1 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt((aire/4)/elongation),dimensionMaximum/2));
				epaisseur2 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt(aire*elongation),dimensionMaximum));
				largeur = (aire+2*epaisseur1*epaisseur2)/(2*elongation*epaisseur1+epaisseur2);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+2*epaisseur1*epaisseur2)/(2*epaisseur1+epaisseur2*(1/elongation));
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
			}else{ // Ni élongation ni épaisseur imposée on tire au sort ces valeurs
				double borneInf = Math.pow(dimensionMinimum,2)/aire>0 ? Math.pow(dimensionMinimum,2)/aire : 0.01;
				double borneSup = aire/(4*Math.pow(dimensionMinimum,2))<1 ? aire/(4*Math.pow(dimensionMinimum,2)) : 1;
				elongation = GenerateurValeur.genererValeurAleatoire(borneInf,borneSup);
				epaisseur1 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt((aire/4)/elongation),dimensionMaximum/2));
				epaisseur2 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt(aire*elongation),dimensionMaximum));
				largeur = (aire+2*epaisseur1*epaisseur2)/(2*elongation*epaisseur1+epaisseur2);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+2*epaisseur1*epaisseur2)/(2*epaisseur1+epaisseur2*(1/elongation));
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
			}
			logger.info("hauteur : "+ hauteur+ " ; largeur : "+largeur+ " ; epaisseur1 : "+epaisseur1+ " ; epaisseur2 : "+epaisseur2);
			forme = ShapeFactory.createU(centre, largeur, hauteur, epaisseur1, epaisseur2);
		}else if (formeBatiment==FormeBatiment.FormeT){
			double largeur = 0;
			double hauteur = 0;
			double epaisseur1 = 0;
			double epaisseur2 = 0;
			// On vérifie que la contrainte sur l'élongation est possible
			if (elongation!=-1){
				double contrainteElongation1 = Math.pow(dimensionMinimum, 2)/aire;
				double contrainteElongation2 = aire/(Math.pow(dimensionMinimum, 2));
				if(elongation<contrainteElongation1){elongation = contrainteElongation1;}
				if(elongation>contrainteElongation2){elongation = contrainteElongation2;}
			}
			// On vérifie que la contrainte sur l'épaisseur est possible
			if (epaisseur!=-1){
				double contrainteEpaisseur = Math.sqrt(aire);
				if(epaisseur>contrainteEpaisseur){epaisseur = contrainteEpaisseur;}
			}
			// On fait les calculs pour les différents cas possibles
			if((elongation!=-1.0)&&(epaisseur!=-1.0)){
				// On vérifie que les contraintes sur l'élongation et sur l'épaisseur sont compatibles (la contrainte sur l'élongation saute si ce n'est pas le cas)
				if (elongation<Math.pow(epaisseur,2)/aire){elongation = Math.pow(epaisseur,2)/aire;}
				if (elongation>aire/(Math.pow(epaisseur,2))){elongation = aire/(Math.pow(epaisseur,2));}
				largeur = (aire+epaisseur*epaisseur)/(epaisseur+elongation*epaisseur);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+epaisseur*epaisseur)/((epaisseur/elongation)+epaisseur);
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = epaisseur;
				epaisseur2 = epaisseur;
			}else if (epaisseur!=-1.0){
				// On tire au hasard la valeur de l'élongation
				double borneInf = Math.pow(epaisseur,2)/aire>0 ? Math.pow(epaisseur,2)/aire : 0.01;
				double borneSup = aire/(Math.pow(epaisseur,2))<1 ? aire/(Math.pow(epaisseur,2)) : 1;
				elongation = GenerateurValeur.genererValeurAleatoire(borneInf,borneSup);
				logger.debug("elongation : "+elongation);
				largeur = (aire+epaisseur*epaisseur)/(epaisseur+elongation*epaisseur);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+epaisseur*epaisseur)/((epaisseur/elongation)+epaisseur);
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				if (epaisseur>hauteur){epaisseur1=hauteur;} else{epaisseur1 = epaisseur;}
				if (largeur<epaisseur){epaisseur2=hauteur;}else{epaisseur2 = epaisseur;}
			}else if (elongation!=-1.0){
				// On tire au hasard les valeurs des épaisseurs
				epaisseur1 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt(aire/elongation),dimensionMaximum));
				epaisseur2 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt(aire*elongation),dimensionMaximum));
				largeur = (aire+epaisseur2*epaisseur1)/(epaisseur2+elongation*epaisseur1);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+epaisseur2*epaisseur1)/((epaisseur2/elongation)+epaisseur1);
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
			}else{ // Ni élongation ni épaisseur imposée on tire au sort ces valeurs
				double borneInf = Math.pow(dimensionMinimum,2)/aire>0 ? Math.pow(dimensionMinimum,2)/aire : 0.01;
				double borneSup = aire/(Math.pow(dimensionMinimum,2))<1 ? aire/(Math.pow(dimensionMinimum,2)) : 1;
				elongation = GenerateurValeur.genererValeurAleatoire(borneInf,borneSup);
				epaisseur1 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt(aire/elongation),dimensionMaximum));
				epaisseur2 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.min(Math.sqrt(aire*elongation),dimensionMaximum));
				largeur = (aire+epaisseur2*epaisseur1)/(epaisseur2+elongation*epaisseur1);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+epaisseur2*epaisseur1)/((epaisseur2/elongation)+epaisseur1);
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
			}
			logger.info("hauteur : "+ hauteur+ " ; largeur : "+largeur+ " ; epaisseur1 : "+epaisseur1+ " ; epaisseur2 : "+epaisseur2);
			forme = ShapeFactory.createT(centre, largeur, hauteur, epaisseur1, epaisseur2);
		}else if (formeBatiment==FormeBatiment.Barre){
			double largeur = 0;
			double hauteur = 0;
			double epaisseur1 = 0;
			double epaisseur2 = 0;
			// On fixe dès le départ epaisseur0
			double epaisseur0 = 5;
			// On vérifie que la contrainte sur l'élongation est possible
			if (elongation!=-1){
				double contrainteElongation1 = Math.pow(dimensionMinimum, 2)/aire;
				double contrainteElongation2 = aire/(Math.pow(dimensionMinimum, 2));
				if(elongation<contrainteElongation1){elongation = contrainteElongation1;}
				if(elongation>contrainteElongation2){elongation = contrainteElongation2;}
			}
			// On vérifie que la contrainte sur l'épaisseur est possible
			if (epaisseur!=-1){
				double contrainteEpaisseur = Math.sqrt(aire);
				if(epaisseur>contrainteEpaisseur){epaisseur = contrainteEpaisseur;}
			}
			// On fait les calculs pour les différents cas possibles
			if((elongation!=-1.0)&&(epaisseur!=-1.0)){
				epaisseur2 = epaisseur;
				// On vérifie que les contraintes sur l'élongation et sur l'épaisseur sont compatibles (la contrainte sur l'élongation saute si ce n'est pas le cas)
				if (elongation<Math.pow(epaisseur2,2)/aire){elongation = Math.pow(epaisseur2,2)/aire;}
				double delta = (epaisseur2*epaisseur2)-(4*elongation*(-2*aire));
				if (delta>0){
					double L1 = (-epaisseur2-Math.sqrt(delta))/(2*elongation);
					double L2 = (-epaisseur2+Math.sqrt(delta))/(2*elongation);
					largeur = Math.max(L1,L2);
				}else if (delta==0){
					largeur = -(epaisseur2/(2*elongation));
				}
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = elongation * largeur;
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = largeur/2;
				// C'est très moche ça
				int val = (int) Math.rint(epaisseur1/epaisseur0);
				epaisseur0 = epaisseur1/val;
			}else if (epaisseur!=-1.0){
				double hauteurIndentation = 4;
				hauteur = epaisseur;
				largeur = aire/(hauteur-hauteurIndentation+(hauteurIndentation/2));
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				epaisseur2 = hauteur-hauteurIndentation;	
				epaisseur1 = largeur/2;
				// C'est très moche ça
				int val = (int) Math.rint(epaisseur1/epaisseur0);
				epaisseur0 = epaisseur1/val;
			}else if (elongation!=-1.0){
				// On tire au hasard la valeur de l'épaisseur
				epaisseur2 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.sqrt(aire*elongation));
				double delta = (epaisseur2*epaisseur2)-(4*elongation*(-2*aire));
				if (delta>0){
					double L1 = (-epaisseur2-Math.sqrt(delta))/(2*elongation);
					double L2 = (-epaisseur2+Math.sqrt(delta))/(2*elongation);
					largeur = Math.max(L1,L2);
				}else if (delta==0){
					largeur = -(epaisseur2/(2*elongation));
				}
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = elongation * largeur;
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = largeur/2;
				// C'est très moche ça
				int val = (int) Math.rint(epaisseur1/epaisseur0);
				epaisseur0 = epaisseur1/val;
			}else{ // Ni élongation ni épaisseur imposée on tire au sort ces valeurs
				double borneInf = Math.pow(epaisseur,2)/aire>0 ? Math.pow(epaisseur,2)/aire : 0.01;
				elongation = GenerateurValeur.genererValeurAleatoire(borneInf,1);
				epaisseur2 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.sqrt(aire*elongation));
				double delta = (epaisseur2*epaisseur2)-(4*elongation*(-2*aire));
				if (delta>0){
					double L1 = (-epaisseur2-Math.sqrt(delta))/(2*elongation);
					double L2 = (-epaisseur2+Math.sqrt(delta))/(2*elongation);
					largeur = Math.max(L1,L2);
				}else if (delta==0){
					largeur = -(epaisseur2/(2*elongation));
				}
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				logger.info(dimensionMaximum);
				hauteur = elongation * largeur;
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = largeur/2;
				// C'est très moche ça
				int val = (int) Math.rint(epaisseur1/epaisseur0);
				epaisseur0 = epaisseur1/val;
			}
			logger.info("hauteur : "+ hauteur+ " ; largeur : "+largeur+ " ; epaisseur1 : "+epaisseur1+ " ; epaisseur2 : "+epaisseur2);
			forme = ShapeFactory.createBarre(centre, largeur, hauteur, epaisseur0, epaisseur2);
		}else if (formeBatiment==FormeBatiment.Escalier){
			double largeur = 0;
			double hauteur = 0;
			double epaisseur1 = 0;
			double epaisseur2 = 0;
			// On vérifie que la contrainte sur l'élongation est possible
			if (elongation!=-1){
				double contrainteElongation1 = Math.pow(dimensionMinimum, 2)/aire;
				double contrainteElongation2 = aire/(Math.pow(dimensionMinimum, 2));
				if(elongation<contrainteElongation1){elongation = contrainteElongation1;}
				if(elongation>contrainteElongation2){elongation = contrainteElongation2;}
			}
			// On vérifie que la contrainte sur l'épaisseur est possible
			if (epaisseur!=-1){
				double contrainteEpaisseur = Math.sqrt(aire);
				if(epaisseur>contrainteEpaisseur){epaisseur = contrainteEpaisseur;}
			}
			// On fait les calculs pour les différents cas possibles
			if((elongation!=-1.0)&&(epaisseur!=-1.0)){
				epaisseur2 = epaisseur;
				// On vérifie que les contraintes sur l'élongation et sur l'épaisseur sont compatibles (la contrainte sur l'élongation saute si ce n'est pas le cas)
				if (elongation<Math.pow(epaisseur2,2)/aire){elongation = Math.pow(epaisseur2,2)/aire;}
				// On tire au sort l'épaisseur de la barre verticale 
				//(on pourrait aussi considérer que cette épaisseur est la même que l'épaisseur de la barre horizontale)
				double epaisseurBarreV = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.sqrt(aire/elongation));
				largeur = (aire+epaisseur2*epaisseurBarreV)/(epaisseur2+elongation*epaisseurBarreV);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+epaisseur2*epaisseurBarreV)/((epaisseur2/elongation)+epaisseurBarreV);
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = (largeur-epaisseurBarreV)/2;
			}else if (epaisseur!=-1.0){
				epaisseur2 = epaisseur;
				// On tire au hasard la valeur de l'élongation
				double borneInf = Math.pow(epaisseur2,2)/aire>0 ? Math.pow(epaisseur2,2)/aire : 0.01;
				elongation = GenerateurValeur.genererValeurAleatoire(borneInf,1);
				// On tire au sort l'épaisseur de la barre verticale 
				//(on pourrait aussi considérer que cette épaisseur est la même que l'épaisseur de la barre horizontale)
				double epaisseurBarreV = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.sqrt(aire/elongation));
				largeur = (aire+epaisseur2*epaisseurBarreV)/(epaisseur2+elongation*epaisseurBarreV);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+epaisseur2*epaisseurBarreV)/((epaisseur2/elongation)+epaisseurBarreV);
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = (largeur-epaisseurBarreV)/2;
			}else if (elongation!=-1.0){
				// On tire au hasard la valeur de l'épaisseur des barres horizontales
				epaisseur2 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.sqrt(aire*elongation));
				// On tire au sort l'épaisseur de la barre verticale 
				//(on pourrait aussi considérer que cette épaisseur est la même que l'épaisseur de la barre horizontale)
				double epaisseurBarreV = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.sqrt(aire/elongation));
				largeur = (aire+epaisseur2*epaisseurBarreV)/(epaisseur2+elongation*epaisseurBarreV);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+epaisseur2*epaisseurBarreV)/((epaisseur2/elongation)+epaisseurBarreV);
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = (largeur-epaisseurBarreV)/2;
			}else{ // Ni élongation ni épaisseur imposée on tire au sort ces valeurs
				// On tire au hasard la valeur de l'élongation
				double borneInf = Math.pow(epaisseur2,2)/aire>0 ? Math.pow(epaisseur2,2)/aire : 0.01;
				elongation = GenerateurValeur.genererValeurAleatoire(borneInf,1);
				// On tire au hasard la valeur de l'épaisseur des barres horizontales et verticale
				epaisseur2 = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.sqrt(aire*elongation));
				double epaisseurBarreV = GenerateurValeur.genererValeurAleatoire(dimensionMinimum,Math.sqrt(aire/elongation));
				largeur = (aire+epaisseur2*epaisseurBarreV)/(epaisseur2+elongation*epaisseurBarreV);
				if (largeur<dimensionMinimum)largeur = dimensionMinimum;
				else if(largeur>dimensionMaximum)largeur = dimensionMaximum;
				hauteur = (aire+epaisseur2*epaisseurBarreV)/((epaisseur2/elongation)+epaisseurBarreV);
				if (hauteur<dimensionMinimum)hauteur = dimensionMinimum;
				else if(hauteur>dimensionMaximum)hauteur = dimensionMaximum;
				epaisseur1 = (largeur-epaisseurBarreV)/2;
			}
	
			logger.info("hauteur : "+ hauteur+ " ; largeur : "+largeur+ " ; epaisseur1 : "+epaisseur1+ " ; epaisseur2 : "+epaisseur2);
			forme = ShapeFactory.createEscalier2(centre, largeur, hauteur, epaisseur1, epaisseur2, epaisseur1, epaisseur2);
		}else if (formeBatiment==FormeBatiment.Cercle){
            double rayon = Math.sqrt(aire/Math.PI);
            if(2*rayon>dimensionMax)rayon = dimensionMax/2;
            else if(2*rayon<dimensionMinimum)rayon = dimensionMinimum/2;
            logger.info("diamètre : "+ 2*rayon);
			forme = ShapeFactory.createCercle(centre, rayon, 5);
		}
		
		return forme;
	}
	
    public static IGeometry construire(FormeBatiment formeBatiment,IDirectPosition centre, double width, double length) {
        IGeometry forme = null;
        if (formeBatiment == FormeBatiment.Barre){
            forme = createBarre(centre, width, length);
        }else if (formeBatiment == FormeBatiment.Carre){
            forme = ShapeFactory.createCarre(centre, length);
        }else if (formeBatiment == FormeBatiment.Rectangle){
            forme = ShapeFactory.createRectangle(centre, length, width);
        }else {
            double longueurBarre = length*GenerateurValeur.genererValeurAleatoire(0.3,1);
            double largeurBarre = length*GenerateurValeur.genererValeurAleatoire(0.3,1);
            double longueurBarre2 = 10;
            double largeurBarre2 = 10;
            if (formeBatiment == FormeBatiment.FormeL){
                forme = ShapeFactory.createL(centre, length, width,
                        longueurBarre, largeurBarre);
            }else if (formeBatiment == FormeBatiment.FormeU){
                forme = ShapeFactory.createU(centre, length, width,
                        longueurBarre / 2, largeurBarre);
            }else if (formeBatiment == FormeBatiment.FormeT){
                forme = ShapeFactory.createT(centre, length, width,
                        longueurBarre, largeurBarre);
            }else if (formeBatiment == FormeBatiment.Escalier){
                longueurBarre2 = (length - longueurBarre)
                * GenerateurValeur.genererValeurAleatoire(0,0.7);
                largeurBarre2 = (width - largeurBarre)
                * GenerateurValeur.genererValeurAleatoire(0.3,1);
                forme = ShapeFactory.createEscalier2(centre, length, width,
                        longueurBarre, largeurBarre, longueurBarre2,
                        largeurBarre2);
            }else {
                forme = ShapeFactory.createCercle(centre, length, 5);
            }
        }
        return forme;
    }

	public static IGeometry construire(FormeBatiment formeBatiment,IDirectPosition centre) {
		double longueur = 10;
		double largeur = longueur * GenerateurValeur.genererValeurAleatoire(0.3,1);
		return construire(formeBatiment, centre, largeur, longueur);
	}

	public static boolean verifier(FormeBatiment typeFormeBatiment,IGeometry forme) {
		boolean ok = true;
		if (typeFormeBatiment==FormeBatiment.FormeL){
			double longueurBarre1 = Distances.distance(forme.coord().get(1),forme.coord().get(2));
			double longueurBarre2 = Distances.distance(forme.coord().get(4),forme.coord().get(5));
			if ((longueurBarre1<3)||(longueurBarre2<3)){
				ok = false;
			}
		}else if (typeFormeBatiment==FormeBatiment.FormeU){
			double longueurBarre1 = Distances.distance(forme.coord().get(1),forme.coord().get(2));
			double longueurBarre2 = (Distances.distance(forme.coord().get(2),forme.coord().get(3)))-(Distances.distance(forme.coord().get(0),forme.coord().get(1)));
			if ((longueurBarre1<3)||(longueurBarre2<3)){
				ok = false;
			}
		}else if (typeFormeBatiment==FormeBatiment.FormeT){
			double longueurBarre1 = Distances.distance(forme.coord().get(0),forme.coord().get(1));
			double longueurBarre2 = Distances.distance(forme.coord().get(3),forme.coord().get(4));
			if ((longueurBarre1<3)||(longueurBarre2<3)){
				ok = false;
			}
		}else if (typeFormeBatiment==FormeBatiment.Escalier){
			double longueurBarre1 = Distances.distance(forme.coord().get(3),forme.coord().get(4));
			double longueurBarre2 = Distances.distance(forme.coord().get(7),forme.coord().get(8));
			double longueurBarre3 = Distances.distance(forme.coord().get(0),forme.coord().get(1))-
									Distances.distance(forme.coord().get(6),forme.coord().get(7));
			if ((longueurBarre1<3)||(longueurBarre2<3)||(longueurBarre3<3)){
				ok = false;
			}
		}
		return ok;
	}

	// vérification moins dépendante de la forme du bâtiment mais ne repère
	// pas les rétrécissements (pour les U, les escaliers et les H)
	public static boolean verifierV2(int typeFormeBatiment,IGeometry forme) {
		boolean ok = true;

		// la face est elle dans le sens trigonométrique ?
		IDirectPositionList listePoints = forme.coord();
		listePoints.remove(0);
		double somme = 0;
		for (int i = 0;i<listePoints.size();i++){
			IDirectPosition pointPrec = new DirectPosition();
            if (i == 0) {
                pointPrec = listePoints.get(listePoints.size() - 1);
            } else {
                pointPrec = listePoints.get(i - 1);
            }
			IDirectPosition pointEncours = listePoints.get(i);
			IDirectPosition pointSuiv = new DirectPosition();
			if (i==listePoints.size()-1){pointSuiv = listePoints.get(0);}
			else{pointSuiv = listePoints.get(i+1);}
			Vecteur vect1 = (new Vecteur(pointPrec,pointEncours)).vectNorme();
			Vecteur vect2 = (new Vecteur(pointEncours,pointSuiv)).vectNorme();
			Vecteur vect3 = vect1.prodVectoriel(vect2);
			somme +=vect3.getZ();
		}

		// On retourne les faces qui ne sont pas dans le sens trigonométrique
		IDirectPositionList listePoints2 = forme.coord();
		if (somme<0){
			listePoints2.inverseOrdre();
			listePoints = listePoints2;
			listePoints.remove(0);
		}

		// On recherche les angles sortants successifs
		Angle anglePrec = null;
		Vecteur vect3Prec = null;
		for (int i = 0;i<listePoints.size()+1;i++){
			IDirectPosition pointPrec = new DirectPosition();
			int index = i;
			if (i>listePoints.size()-1){index = i-listePoints.size();}
			if (index==0){pointPrec = listePoints.get(listePoints.size()-1);}
			else{pointPrec = listePoints.get(index-1);}
			IDirectPosition pointEncours = listePoints.get(index);
			IDirectPosition pointSuiv = new DirectPosition();
			if (index==listePoints.size()-1){pointSuiv = listePoints.get(0);}
			else{pointSuiv = listePoints.get(index+1);}
			Vecteur vect1 = (new Vecteur(pointPrec,pointEncours)).vectNorme();
			Vecteur vect2 = (new Vecteur(pointEncours,pointSuiv)).vectNorme();
			Vecteur vect3 = vect1.prodVectoriel(vect2);
			Angle angleEnCours = vect1.angleVecteur(vect2);
			if ((vect3Prec!=null)&&(anglePrec!=null)){
				if ((vect3.getZ()>0)&&(vect3Prec.getZ()>0)){
					DirectPositionList listeP = new DirectPositionList();
					listeP.add(pointEncours);
					listeP.add(pointPrec);
					GM_LineString ligne = new GM_LineString(listeP);
					if ((ligne.length()<3)&&((anglePrec.getValeur()+angleEnCours.getValeur())*180/Math.PI <220)){
						System.out.println("Arrête sortante problématique : "+ligne);
						ok = false;
					}
				}
			}
			anglePrec = vect1.angleVecteur(vect2);
			vect3Prec = vect3;
		}
		return ok;
	}

	/**
	 * crée une barre
	 * @param centre centre de la barre
	 */
	private static IGeometry createBarre(IDirectPosition centre, double width, double length) {
		int nb = Math.min(1, (int) GenerateurValeur.genererValeurAleatoire(length / 5, length / 10));
		GM_LineString exterior = new GM_LineString();
        double x = centre.getX() - length / 2;
        double y = centre.getY();
        double aggrLength = 0;
		for (int i = 0; i < nb && aggrLength < length; i++) {
			double rectLength = GenerateurValeur.genererValeurAleatoire(5, 10);
			if ((aggrLength + rectLength > length)
			            || (length - aggrLength - rectLength < 5)) {
			    rectLength = length - aggrLength;
			}
            double rectWidth = GenerateurValeur.genererValeurAleatoire(5, width);
            double deviation = (width - rectWidth) / 2;
            deviation = GenerateurValeur.genererValeurAleatoire(-deviation, deviation);
            exterior.addControlPoint(2 * i, new DirectPosition(x, y + deviation - rectWidth / 2));
            exterior.addControlPoint(2 * i, new DirectPosition(x + rectLength, y + deviation - rectWidth / 2));
            exterior.addControlPoint(2 * i, new DirectPosition(x + rectLength, y + deviation + rectWidth / 2));
            exterior.addControlPoint(2 * i, new DirectPosition(x, y + deviation + rectWidth / 2));
			aggrLength += rectLength;
			x += rectLength;
		}
        exterior = (GM_LineString) Filtering.DouglasPeucker(exterior, 1.0);
        if (exterior.sizeControlPoint() == 0) {
            System.out.println("Error " + width + " - " + length);
            return ShapeFactory.createRectangle(centre, length, width);
        }
        exterior.addControlPoint(exterior.getControlPoint(0));
		return new GM_Polygon(exterior);
	}

//	/**
//	 * @return une valeur aléatoire comprise entre a et b
//	 */
//	private static double getValAleatoire(double a, double b){
//		double valAlea = a+Math.random()*(b-a);
//		return valAlea;
//	}
}
