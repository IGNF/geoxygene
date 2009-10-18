/*
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
 * 
 */

package fr.ign.cogit.geoxygene.contrib.appariement.stockageLiens;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Liens stockables dans le SGBD
 * Resultat de l'appariement : lien entre objets de BDref et objets de BDcomp.
 * Un lien a aussi une géométrie qui est sa représentation graphique.
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class LienSGBD extends FT_Feature {

	/** Les objets de Reference pointés par le lien */
	protected String objetsRef;
	public String getObjetsRef() {return objetsRef;}
	public void setObjetsRef(String liste) { objetsRef = liste; }

	/** Les objets de Comparaison pointés par le lien */
	protected String objetsComp;
	public String getObjetsComp() {return objetsComp;}
	public void setObjetsComp(String liste) { objetsComp = liste; }

	/** Estimation de la qualité du lien d'appariement
	 * (mapping fait avec la table Representation_Lien au besoin)*/
	private double evaluation ;
	public double getEvaluation() {return evaluation;}
	public void setEvaluation(double evaluation) {this.evaluation = evaluation;}

	/** Liste d'indicateurs temporaires utilisés pendant les calculs d'appariement */
	protected String indicateurs;
	public String getIndicateurs() {return indicateurs;}
	public void setIndicateurs(String indicateurs) { this.indicateurs = indicateurs; }

	/** Texte libre
	 * (mapping fait avec la table Representation_Lien au besoin)*/
	protected String commentaire = new String();
	public String getCommentaire() {return commentaire;}
	public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

	/** Texte libre pour décrire le nom de l'appariement.
	 * (mapping fait avec la table Representation_Lien au besoin)*/
	protected String nom = new String();
	public String getNom() {return nom;}
	public void setNom(String nom) { this.nom = nom; }

	/** Texte libre pour décrire le type d'appariement (ex. "Noeud-Noeud").
	 * (mapping fait avec la table Representation_Lien au besoin)*/
	protected String type = new String();
	public String getType() {return type;}
	public void setType(String type) { this.type = type; }

	/** Texte libre pour décrire les objets de référence pointés.
	 * (mapping fait avec la table Representation_Lien au besoin)*/
	protected String reference = new String();
	public String getReference() {return reference;}
	public void setReference(String reference) { this.reference = reference; }

	/** Texte libre pour décrire les objets de comparaison pointés.
	 * (mapping fait avec la table Representation_Lien au besoin)*/
	protected String comparaison = new String();
	public String getComparaison() {return comparaison;}
	public void setComparaison(String comparaison) { this.comparaison = comparaison; }


	/**Methode de conversion entre les liens d'appariement vers les liens SGBD */
	public LienSGBD conversionLiensVersSGBD(Lien lien){
		List<?> listeObjetsRef = lien.getObjetsRef(), listeObjetsComp = lien.getObjetsComp(),
		indic = lien.getIndicateurs();
		Iterator<?> itRef = listeObjetsRef.iterator(), itComp = listeObjetsComp.iterator(),
		itIndic = indic.iterator();

		FT_Feature feature;
		String formatRef="", formatComp="", formatIndic="", classe="";

		//Reference
		while(itRef.hasNext()){
			feature = (FT_Feature)itRef.next();
			classe = feature.getClass().getName();
			if (formatRef.contains(classe)&&formatRef.length()!=0) formatRef = formatRef.replaceAll(classe,classe+" "+feature.getId());
			else{
				formatRef = formatRef+classe+" "+feature.getId()+"|";
				this.getEnsembleLiensSGBD().getListePop().add(classe);
			}
		}
		formatRef = formatRef.substring(0,formatRef.length()-1);
		this.setObjetsRef(formatRef);

		//Comparaison
		while(itComp.hasNext()){
			feature = (FT_Feature)itComp.next();
			classe = feature.getClass().getName();
			if (formatComp.contains(classe)&&formatComp.length()!=0) formatComp = formatComp.replaceAll(classe,classe+" "+feature.getId());
			else {
				formatComp = formatComp+classe+" "+feature.getId()+"|";
				this.getEnsembleLiensSGBD().getListePop().add(classe);
			}
		}
		formatComp = formatComp.substring(0,formatComp.length()-1);
		this.setObjetsComp(formatComp);

		//Indicateurs
		while(itIndic.hasNext()){
			formatIndic = formatIndic+(String)itIndic.next()+"|";
		}
		if (formatIndic.length()>0)this.setIndicateurs(formatIndic.substring(0,formatIndic.length()-1));
		else this.setIndicateurs("Non renseigné");

		//evaluation
		this.setEvaluation(lien.getEvaluation());

		//commentaire
		if (lien.getCommentaire().length()==0)this.setCommentaire("Non renseigné");
		else this.setCommentaire(lien.getCommentaire());

		//nom
		if (lien .getNom().length()==0)this.setNom("Non renseigné");
		else this.setNom(lien.getNom());

		//type
		if (lien.getType().length()==0)this.setType("Non renseigné");
		else this.setType(lien.getType());

		//reference
		if (lien.getReference().length()==0)this.setReference("Non renseigné");
		else this.setReference(lien.getReference());

		//comparaison
		if (lien.getComparaison().length()==0)this.setComparaison("Non renseigné");
		else this.setComparaison(lien.getComparaison());

		//geometrie
		this.setGeom(lien.getGeom());

		return this;
	}

	/**Methode de conversion entre les liens SGBD vers les liens d'appariement  */
	@SuppressWarnings("unchecked")
	public Lien conversionSGBDVersLiens(){
		Lien lien = new Lien();

		//reference
		String formatRef = this.getObjetsRef(), valeurRef, valeurRefClass, valeurRefIds,valeurRefId;
		StringTokenizer tokenRef = new StringTokenizer(formatRef,"|");StringTokenizer tokenRefId;
		Population populationCourante;
		FT_Feature feature;
		List<Population> liste = this.getEnsembleLiensSGBD().getListePopulations();
		while(tokenRef.hasMoreElements()){
			valeurRef = tokenRef.nextToken();
			valeurRefClass = valeurRef.substring(0,valeurRef.indexOf(" "));
			valeurRefIds = valeurRef.replaceFirst(valeurRefClass,"");
			tokenRefId = new StringTokenizer(valeurRefIds," ");
			Iterator<Population> it = liste.iterator();
			while(it.hasNext()){
				populationCourante = it.next();
				if(valeurRefClass.equals(populationCourante.getNomClasse())){
					while(tokenRefId.hasMoreElements()){
						valeurRefId = tokenRefId.nextToken();
						int refId = new Integer(valeurRefId).intValue();
						Iterator<FT_Feature> itPop = populationCourante.getElements().iterator();
						while(itPop.hasNext()){
							feature = itPop.next();
							if(refId==feature.getId()){
								lien.addObjetRef(feature);
								break;
							}
						}
					}
					break;
				}
			}
		}

		//comparaison
		String formatComp = this.getObjetsComp(), valeurComp, valeurCompClass, valeurCompIds,valeurCompId;
		StringTokenizer tokenComp = new StringTokenizer(formatComp,"|");StringTokenizer tokenCompId;
		while(tokenComp.hasMoreElements()){
			valeurComp = tokenComp.nextToken();
			valeurCompClass = valeurComp.substring(0,valeurComp.indexOf(" "));
			valeurCompIds = valeurComp.replaceFirst(valeurCompClass,"");
			tokenCompId = new StringTokenizer(valeurCompIds," ");
			Iterator<Population> it = liste.iterator();
			while(it.hasNext()){
				populationCourante = it.next();
				if(valeurCompClass.equals(populationCourante.getNomClasse())){
					while(tokenCompId.hasMoreElements()){
						valeurCompId = tokenCompId.nextToken();
						int compId = new Integer(valeurCompId).intValue();
						Iterator<FT_Feature> itPop = populationCourante.getElements().iterator();
						while(itPop.hasNext()){
							feature = itPop.next();
							if(compId==feature.getId()){
								lien.addObjetComp(feature);
								break;
							}
						}
					}
					break;
				}
			}
		}

		//Indicateurs
		String formatIndic = this.getIndicateurs(), valeurIndic;
		StringTokenizer tokenIndic = new StringTokenizer(formatIndic,"|");
		while(tokenIndic.hasMoreElements()){
			valeurIndic = tokenIndic.nextToken();
			lien.addIndicateur(valeurIndic);
		}

		//evaluation
		lien.setEvaluation(this.getEvaluation());

		//commentaire
		lien.setCommentaire(this.getCommentaire());

		//nom
		lien.setNom(this.getNom());

		//type
		lien.setType(this.getType());

		//reference
		lien.setReference(this.getReference());

		//comparaison
		lien.setComparaison(this.getComparaison());

		//geometrie
		lien.setGeom(this.getGeom());

		return lien;
	}

	//////////////////////////////////////
	private EnsembleDeLiensSGBD ensembleLiensSGBD;

	/** Récupère l'objet en relation. */
	public EnsembleDeLiensSGBD getEnsembleLiensSGBD() {return ensembleLiensSGBD;  }

	/** Définit l'objet en relation, et met à jour la relation inverse. */
	public void setEnsembleLiensSGBD(EnsembleDeLiensSGBD ensemble) {
		EnsembleDeLiensSGBD old = ensembleLiensSGBD;
		ensembleLiensSGBD = ensemble;
		if ( old != null ) old.getLiensSGBD().remove(this);
		if ( ensemble != null) {
			if ( ! ensemble.getLiensSGBD().contains(this) ) ensemble.getLiensSGBD().add(this);
		}
	}

}