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

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Structure permettant le stockage des résultats de la réalisation
 * d'un appariement au sein du SGBD
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

@SuppressWarnings("unchecked")
public class EnsembleDeLiensSGBD extends Population {

	public EnsembleDeLiensSGBD() {
		super(false, "Ensemble de liens SGBD", LienSGBD.class, true);
	}

	public EnsembleDeLiensSGBD(boolean persistant) {
		super(persistant, "Ensemble de liens SGBD", LienSGBD.class, true);
	}

	/** Nom du l'ensemble des liens d'appariement  (ex: "Appariement des routes par la méthode XX")*/
	private String nom ;
	@Override
	public String getNom() {return this.nom;}
	@Override
	public void setNom(String nom) {this.nom = nom;}

	/** Description textuelle des paramètres utilisés pour l'appariement */
	private String parametrage ;
	public String getParametrage() {return this.parametrage;}
	public void setParametrage(String parametrage) {this.parametrage = parametrage;}

	/** Description textuelle du résultat de l'auto-évaluation des liens */
	private String evaluationInterne ;
	public String getEvaluationInterne() {return this.evaluationInterne;}
	public void setEvaluationInterne(String evaluation) {this.evaluationInterne = evaluation;}

	/** Description textuelle du résultat de l'évaluation globale des liens */
	private String evaluationGlobale ;
	public String getEvaluationGlobale() {return this.evaluationGlobale;}
	public void setEvaluationGlobale(String evaluation) {this.evaluationGlobale = evaluation;}


	/** Liste des populations auxquelles les objets ref et comp des liens sont attach�s
	 * sous forme de string*/
	private String populations ;
	public String getPopulations() {return this.populations;}
	public void setPopulations(String populations) {this.populations = populations;}

	/** Liste aidant à instancier la variable populations*/
	private HashSet<String> listePop;
	public HashSet<String> getListePop() {return this.listePop;}
	public void setListePop(HashSet<String> listePop) {this.listePop = listePop;}

	/** Liste des populations r�elles*/
	private List<Population> listePopulations;
	public List<Population> getListePopulations() {return this.listePopulations;}
	public void setListePopulations(List<Population> listePopulations) {this.listePopulations = listePopulations;}

	/** Date de l'enregistrement*/
	private String date;
	public String getDate() {return this.date;}
	public void setDate(String date) {this.date = date;}

	/**Couleur du lien : rouge */
	private int rouge;
	public int getRouge() { return this.rouge; }
	public void setRouge(int rouge) { this.rouge = rouge; }

	/**Couleur du lien : vert */
	private int vert;
	public int getVert() { return this.vert; }
	public void setVert(int vert) { this.vert = vert; }

	/**Couleur du lien : bleu */
	private int bleu;
	public int getBleu() { return this.bleu; }
	public void setBleu(int bleu) { this.bleu = bleu; }

	//////////////////////////////////////////////////////////////////////////
	//  relation     BIDIRECTIONNELLE     1-n     ////////////////////////
	//////////////////////////////////////////////////////////////////////////

	/** Lien bidirectionnel 1-n vers Lien.
	 *  1 objet EnsembleDeLiens est en relation avec n objets LienSGBD (n pouvant etre nul).
	 *  1 objet LienSGBD est en relation avec 1 objet EnsembleDeLiens au plus.
	 *
	 *  NB: un objet EnsembleDeLiens ne doit pas être en relation plusieurs fois avec le même objet LienSGBD :
	 *  il est impossible de bien gérer des relations 1-n bidirectionnelles avec doublons.
	 *
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec
	 *  les methodes fournies.
	 * 
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List<LienSGBD> liensSGBD = this.getElements();

	/** Récupère la liste des objets en relation. */
	public List<LienSGBD> getLiensSGBD() {return this.liensSGBD ; }

	/** définit la liste des objets en relation, et met à jour la relation inverse. */
	public void setLiensSGBD (List<LienSGBD> L) {
		List old = new ArrayList(this.liensSGBD);
		Iterator it1 = old.iterator();
		while ( it1.hasNext() ) {
			LienSGBD lien = (LienSGBD)it1.next();
			lien.setEnsembleLiensSGBD(null);
		}
		Iterator it2 = L.iterator();
		while ( it2.hasNext() ) {
			LienSGBD lien = (LienSGBD)it2.next();
			lien.setEnsembleLiensSGBD(this);
		}
	}

	/** Récupère le ième élément de la liste des objets en relation. */
	public LienSGBD getLienSGBD(int i) {return this.liensSGBD.get(i) ; }

	/** Ajoute un objet à la liste des objets en relation, et met à jour la relation inverse. */
	public void addLienSGBD (LienSGBD lien) {
		if ( lien == null ) return;
		this.liensSGBD.add(lien) ;
		lien.setEnsembleLiensSGBD(this) ;
	}

	/** enlève un élément de la liste des objets en relation, et met à jour la relation inverse. */
	public void removeLienSGBD(LienSGBD lien) {
		if ( lien == null ) return;
		this.liensSGBD.remove(lien) ;
		lien.setEnsembleLiensSGBD(null);
	}

	/** Vide la liste des objets en relation, et met à jour la relation inverse. */
	public void emptyLiensSGBD () {
		List old = new ArrayList(this.liensSGBD);
		Iterator it = old.iterator();
		while ( it.hasNext() ) {
			LienSGBD lien = (LienSGBD)it.next();
			lien.setEnsembleLiensSGBD(null);
		}
	}

	/** détruit dans le SGBD l'ensemble de liens SGBD et les liens d'appariement SGBD
	 * en correspondance (attention: il faut que les objets soient persistants) */
	public void detruitEnsembleDeLiensSGBD(Geodatabase geodatabase){
		Iterator<LienSGBD> it = this.getLiensSGBD().iterator();
		while(it.hasNext()){
			LienSGBD lien = it.next();
			geodatabase.deletePersistent(lien);
		}
		geodatabase.deletePersistent(this);
	}

	////////////////////////////////////////////////////////////////////////////////////
	//////////  CONVERSION ENTRE ENSEMBLE DE LIENS ET ENSEMBLE DE LIENS SGBD  //////////
	////////////////////////////////////////////////////////////////////////////////////

	/**Methode de conversion entre les ensembles de liens d'appariement vers les ensemebles
	 * de liens SGBD */
	public EnsembleDeLiensSGBD conversionEnsembleLiensVersSGBD(EnsembleDeLiens ensemble, int rouge, int vert, int bleu){
		//nom
		if (ensemble.getNom()==null || ensemble.getNom().length()==0)this.setNom("Non renseigné");
		else this.setNom(ensemble.getNom());

		//parametrage
		if (ensemble.getParametrage()==null || ensemble.getParametrage().length()==0){
			this.setParametrage("Non renseigné");
		}
		else this.setParametrage(ensemble.getParametrage());

		//evaluationInterne
		if (ensemble.getEvaluationInterne()==null || ensemble.getEvaluationInterne().length()==0)this.setEvaluationInterne("Non renseigné");
		else this.setEvaluationInterne(ensemble.getEvaluationInterne());

		//evaluationGlobale
		if (ensemble.getEvaluationGlobale()==null || ensemble.getEvaluationGlobale().length()==0)this.setEvaluationGlobale("Non renseigné");
		else this.setEvaluationGlobale(ensemble.getEvaluationGlobale());

		//date
		this.setDate(new GregorianCalendar().getTime().toString());

		//couleur
		this.setRouge(rouge);
		this.setVert(vert);
		this.setBleu(bleu);

		//liensSGBD
		this.setListePop(new HashSet<String>());
		Iterator it = ensemble.getElements().iterator();
		while(it.hasNext()){
			LienSGBD lienSGBD = (LienSGBD)this.nouvelElement();
			this.addLienSGBD(lienSGBD);
			lienSGBD.conversionLiensVersSGBD((Lien)it.next());
		}

		Iterator<String> itPop = this.listePop.iterator();
		String pop = "";
		while(itPop.hasNext()){
			pop = pop.concat(itPop.next()+"|");
		}
		this.setPopulations(pop.substring(0, pop.length()-1));

		return this;
	}

	/**Methode de conversion entre les ensembles de liens SGBD vers les ensembles de
	 * liens d'appariement */
	public void conversionSGBDVersEnsembleLiens(EnsembleDeLiens ensemble){
		//nom
		ensemble.setNom(this.getNom());

		//parametrage
		ensemble.setParametrage(this.getParametrage());

		//evaluationInterne
		ensemble.setEvaluationInterne(this.getEvaluationInterne());

		//evaluationGlobale
		ensemble.setEvaluationGlobale(this.getEvaluationGlobale());

		//chargement des populations concernées par les liens d'appariement en m�moire
		StringTokenizer token = new StringTokenizer(this.getPopulations(),"|");
		String pop;
		Geodatabase geodb = GeodatabaseOjbFactory.newInstance();
		this.setListePopulations(new ArrayList());
		while(token.hasMoreElements()){
			pop = token.nextToken();
			try {
				Class classe = Class.forName(pop);
				Population population = new Population(false,"Population",classe,true);
				population.addCollection(geodb.loadAllFeatures(classe));
				this.getListePopulations().add(population);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("ATTENTION: la classe "+pop+" stockée dans LIENS ne semble plus exist�e");
				System.out.println("RESOLUTION: recréer cette classe ou refaire un appariement pour un stockage adapté de vos liens");
			}
		}

		//liensSGBD
		Iterator<LienSGBD> it = this.getLiensSGBD().iterator();
		while(it.hasNext()){
			LienSGBD lienSGBD = it.next();
			//ensemble.getElements().add(lienSGBD.conversionSGBDVersLiens());
			//meilleure solution dans le sens où précise la population du lien
			//mais plantage au niveau du stockage au moment de la création de la population
			//de l'ensemble de liens SGBD
			ensemble.add(lienSGBD.conversionSGBDVersLiens());
		}
	}

}