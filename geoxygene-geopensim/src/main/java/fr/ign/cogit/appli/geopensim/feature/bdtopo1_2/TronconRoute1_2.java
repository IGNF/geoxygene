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
package fr.ign.cogit.appli.geopensim.feature.bdtopo1_2;

import fr.ign.cogit.appli.geopensim.feature.micro.TronconRoute;


/**
 * Classe geographique.
 * @author Julien Perret
 * */

public class TronconRoute1_2 extends TronconRoute {
	/**
	 * Identifiant de l'objet issu du shapefile.
	 */
	protected int gid;
	/**
	 * @return identifiant de l'objet issu du shapefile
	 */
	public int getGid() {return this.gid; }
	/**
	 * @param Gid identifiant de l'objet issu du shapefile
	 */
	public void setGid (int Gid) {gid = Gid; }

	protected String classement;
	public String getClassement() {return this.classement; }
	public void setClassement (String Classement) {classement = Classement; }

	protected String dep_gest;
	public String getDep_gest() {return this.dep_gest; }
	public void setDep_gest (String Dep_gest) {dep_gest = Dep_gest; }

	protected int fictif;
	public int getFictif() {return this.fictif; }
	public void setFictif (int Fictif) {fictif = Fictif; }

	protected String franchisst;
	public String getFranchisst() {return this.franchisst; }
	public void setFranchisst (String Franchisst) {franchisst = Franchisst; }

	protected double largeur;
	public double getLargeur() {return this.largeur; }
	public void setLargeur (double Largeur) {largeur = Largeur; }

	protected String nom;
	public String getNom() {return this.nom; }
	public void setNom (String Nom) {nom = Nom; }

	protected int nb_voies;
	public int getNb_voies() {return this.nb_voies; }
	public void setNb_voies (int Nb_voies) {nb_voies = Nb_voies; }

	protected String numero;
	public String getNumero() {return this.numero; }
	public void setNumero (String Numero) {numero = Numero; }

	protected int posit_sol;
	public int getPosit_sol() {return this.posit_sol; }
	public void setPosit_sol (int Posit_sol) {posit_sol = Posit_sol; }

	/* (non-Javadoc)
	 * @see fr.ign.cogit.appli.geopensim.feature.ElementRepresentation#qualifier()
	 */
	@Override
	public void qualifier() {
		super.qualifier();
	}

}
