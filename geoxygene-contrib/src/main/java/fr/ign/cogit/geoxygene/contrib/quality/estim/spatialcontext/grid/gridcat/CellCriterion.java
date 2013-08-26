package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat;


/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author JFGirres
 * 
 * Contient les critères appliqués à une cellule. Chaque élément
 * de la classe est donc lié une cellule et peut déterminer la classe
 * de la cellule selon son propre critère.
 * 
 */
public abstract class CellCriterion {
	private GridCell cellule;
	private int classif;// classification du crit�re
	private Number seuilBas,seuilHaut;// les seuils de la classif
	private Number valeur;// la valeur du critere
	private double poids;// poids du crit�re (compris entre 0 et 1)
	
	public GridCell getCellule() {return cellule;}
	public void setCellule(GridCell cellule) {this.cellule = cellule;}
	public int getClassif() {return classif;}
	public void setClassif(int classif) {this.classif = classif;}
	public Number getSeuilBas() {return seuilBas;}
	public void setSeuilBas(Number seuilBas) {this.seuilBas = seuilBas;}
	public Number getSeuilHaut() {return seuilHaut;}
	public void setSeuilHaut(Number seuilHaut) {this.seuilHaut = seuilHaut;}
	public Number getValeur() {return valeur;}
	public void setValeur(Number valeur) {this.valeur = valeur;}
	public double getPoids() {return poids;}
	public void setPoids(double poids) {this.poids = poids;}

	CellCriterion(GridCell cell,double poids,Number seuilBas,Number seuilHaut){
		cellule = cell;
		this.poids = poids;
		this.seuilHaut = seuilHaut;
		this.seuilBas = seuilBas;
	}
	
	@Override
	public boolean equals(Object obj){
		if (!this.getClass().equals(obj.getClass())) return false;
		CellCriterion crit = (CellCriterion)obj;
		if(!this.cellule.equals(crit.cellule)) return false;
		if(this.poids!=crit.poids) return false;
		if(!this.seuilBas.equals(crit.seuilBas)) return false;
		if(!this.seuilHaut.equals(crit.seuilHaut)) return false;
		return true;
	}
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName()+" "+poids+" "+seuilBas+" "+seuilBas;
	}
	
	public abstract void setValue();
	
	public abstract void setCategory();
	
	public String getNom(){return this.getClass().getSimpleName();}
}
