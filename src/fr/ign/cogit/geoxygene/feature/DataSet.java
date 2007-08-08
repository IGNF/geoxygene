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

package fr.ign.cogit.geoxygene.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;


/** Classe mère pour tout jeu de données.
 *  Un DataSet peut par exemple correspondre à une zone d'une BD, ou seulement un thème.
 *  Un DataSet est constitué de manière récursive d'un ensemble de jeux de données, 
 *  et d'un ensemble de populations, elles mêmes constituées d'un ensemble d'éléments.
 * 
 * @author Sébastien Mustière
 * @version 1.1
 *  
 * 9.02.2006 : extension de la méthode chargeExtractionThematiqueEtSpatiale (grosso)
 *  
 */

 public class DataSet  {

 	
	protected int id;
	/** Renvoie l'identifiant */
	public int getId() {return id;}
	/** Affecte un identifiant. */
	public void setId (int Id) {id = Id;}
	
    /** Paramètre statique de connexion à la BD */
    public static Geodatabase db;



///////////////////////////////////////////////////////
//      Constructeurs / Chargement / persistance     
///////////////////////////////////////////////////////

    /** Constructeur par défaut. */
    public DataSet() {this.ojbConcreteClass = this.getClass().getName();}

    /** Constructeur par défaut, recopiant les champs de métadonnées du DataSet en paramètre sur le nouveau */
    public DataSet(DataSet DS) {
        this.ojbConcreteClass = this.getClass().getName();
        if (DS == null) return;
        this.setNom(DS.getNom());
        this.setTypeBD(DS.getTypeBD());
        this.setModele(DS.getModele());
        this.setZone(DS.getZone());
        this.setDate(DS.getDate());
        this.setCommentaire(DS.getCommentaire());
    }

    /** Chargement des instances des populations persistantes d'un jeu de données. */
    public void chargeElements() {
        if (!this.getPersistant()) {
            System.out.println("----- ATTENTION : Probleme au chargement du jeu de donnees "+this.getNom()); 
            System.out.println("----- Impossible de charger les elements d'un jeu de donnees non persistant"); 
            return;
        }

        // chargement recursif des dataset composants this
        Iterator itDS = this.getComposants().iterator();
        while (itDS.hasNext() ) {
            DataSet DS = (DataSet)itDS.next();
            if ( DS.getPersistant() ) DS.chargeElements() ;
        }

       
        // chargement recursif des populations de this
        System.out.println("");
        System.out.println("###### Chargement des elements du DataSet "+this.getNom());
        Iterator itPop = this.getPopulations().iterator();
        while ( itPop.hasNext() ) {
            Population pop = (Population)itPop.next();
            if ( pop.getPersistant() ) pop.chargeElements();
        }
    }

    /** Chargement des instances des populations persistantes d'un jeu de données qui
     *  intersectent une géométrie donnée (extraction géométrique). */
    public void chargeElementsPartie(GM_Object geom) {
        if (!this.getPersistant()) {
            System.out.println("----- ATTENTION : Probleme au chargement du jeu de donnees "+this.getNom()); 
            System.out.println("----- Impossible de charger les elements d'un jeu de donnees non persistant"); 
            return;
        }
        // chargement recursif des dataset composants this
        Iterator itDS = this.getComposants().iterator();
        while (itDS.hasNext() ) {
            DataSet DS = (DataSet)itDS.next();
            if ( DS.getPersistant() ) DS.chargeElementsPartie(geom) ;
        }
        
        // chargement recursif des populations de this
        System.out.println("");
        System.out.println("###### Chargement des elements du DataSet "+this.getNom());
        Iterator itPop = this.getPopulations().iterator();
        while ( itPop.hasNext() ) {
            Population pop = (Population)itPop.next();
            if ( pop.getPersistant() ) pop.chargeElementsPartie(geom);
        }
    }
    
	/** Chargement des instances des populations persistantes d'un jeu de données qui
	 *  intersectent une géométrie donnée. 
	 *  ATTENTION: les tables qui stockent les éléments doivent avoir été indexées dans Oracle.
	 *  ATTENTION AGAIN: seules les populations avec une géométrie sont chargées.
	 */
	public void chargeElementsPartie(Extraction zoneExtraction) {
		chargeElementsPartie(zoneExtraction.getGeom());
	} 
    
	/**Méthode de chargement pour les test. Elle est un peu tordue
	 * dans le paramétrage mais permet de ne charger que ce qu'on veut.
	 * Elle permet de charger les instances des populations persistantes 
	 * d'un jeu de données qui :
	 * - intersectent une géométrie donnée (extraction géométrique),
	 * - ET qui appartiennent à certains thèmes et populations précisés en entrée.
	 *
	 * @param geom : Définit la zone d'extraction.
	 * 
	 * @param themes : Définit les sous-DS du DS à charger. Pour le DS lui-même,
	 * et pour chaque sous-DS, on précise également quelles populations 
	 * sont chargées. Ce paramètre est une liste de liste de String 
	 * composée comme suit (si la liste est nulle on charge tout) :
	 * 1/ Le premier élément est soit null (on charge alors toutes les populations 
	 * directement sous le DS), soit une liste contenant les noms des populations
	 * directement sous le DS que l'on charge (si la liste est vide, on ne charge rien).
	 * 
	 * 2/ Tous les autres éléments sont des listes (une pour chaque sous-DS) qui 
	 * contiennent chacune d'abord le nom d'un sous-DS que l'on veut charger,
	 * puis soit rien d'autre si on charge toutes les populations du sous-DS,
	 * soit le nom des populations du sous-DS que l'on veut charger.
	 * 
	 * NB: Attention aux majuscules et aux accents.
	 * 
	 * EXEMPLE de parametre themes pour un DS repréentant la BDCarto, et
	 * spécifiant qu'on ne veut charger que les troncon et les noeud du thème
	 * routier, et les troncons du thème hydro, mais tout le thème ferré.
	 * theme = {null, liste1, liste2, liste3}, avec :  
	 * - null car il n'y a pas de population directement sous le DS BDCarto,
	 * - liste1 = {"Routier", "Tronçons de route", "Noeuds routier"},
	 * - liste2 = {"Hydrographie", "Tronçons de cours d'eau"},
	 * - liste3 = {"Ferré"}.
	 *  
	 */
	public void chargeExtractionThematiqueEtSpatiale(GM_Object geom, List themes) {
		if (!this.getPersistant()) {
			System.out.println("----- ATTENTION : Probleme au chargement du jeu de donnees "+this.getNom()); 
			System.out.println("----- Impossible de charger les elements d'un jeu de donnees non persistant"); 
			return;
		}
		
		List populationsACharger, themeACharger, extraitThemes ;
		Iterator itThemes, itPopulationsACharger;
		String nom;
		boolean aCharger;
		

		// chargement recursif des dataset composants this
		Iterator itDS = this.getComposants().iterator();
		while (itDS.hasNext() ) {
			DataSet DS = (DataSet)itDS.next();
			populationsACharger = null;
			if (themes == null) aCharger = true;
			else {
				itThemes = themes.iterator();
				themeACharger = (List)itThemes.next();
				if (!itThemes.hasNext() ) aCharger = true;
				else {
					aCharger = false;
					while (itThemes.hasNext()) {
						themeACharger = (List)itThemes.next();
						if ( DS.getNom().equals(themeACharger.get(0)) ) {
							aCharger = true;
							if (themeACharger.size() == 1) {
								populationsACharger = null;
								break;
							} 
							extraitThemes = new ArrayList(themeACharger);
							extraitThemes.remove(0);
							populationsACharger = new ArrayList();
							populationsACharger.add(extraitThemes);
							break;
						} 
					}
				}
			}
			if ( aCharger && DS.getPersistant() ) DS.chargeExtractionThematiqueEtSpatiale(geom,populationsACharger) ;
		}
        
        
		// chargement des populations de this (directement sous this)
		if (themes == null) populationsACharger = null;
		else {
			itThemes = themes.iterator(); 
			populationsACharger = (List)itThemes.next();
		}
		System.out.println("");
		System.out.println("###### Chargement des elements du DataSet "+this.getNom());
		Iterator itPop = this.getPopulations().iterator();
		while ( itPop.hasNext() ) {
			Population pop = (Population)itPop.next();
			if ( populationsACharger == null ) aCharger = true;
			else {
				aCharger = false;
				itPopulationsACharger = populationsACharger.iterator();
				while(itPopulationsACharger.hasNext()){
					nom = (String)itPopulationsACharger.next();
					if (pop.getNom().equals(nom)) {
						aCharger = true;
						break;
					}
				}
			}
			if ( aCharger && pop.getPersistant() ){
				if (geom!=null)pop.chargeElementsPartie(geom);
				else pop.chargeElements();
			}
		}

	}
    
    /** Pour un jeu de données persistant, détruit le jeu de données, ses thèmes et ses objets populations -
     * ATTENTION : ne détruit pas les éléments des populations (pour cela vider les tables Oracle)
     */
    public void detruitJeu() {
        if (!this.getPersistant()) {
            System.out.println("----- ATTENTION : Probleme à la destruction du jeu de donnees "+this.getNom()); 
            System.out.println("----- Le jeu de données n'est pas persistant"); 
            return;
        }
        // destruction des populations de this
        Iterator itPop = this.getPopulations().iterator();
        while ( itPop.hasNext() ) {
            Population pop = (Population)itPop.next();
            if ( pop.getPersistant() ) pop.detruitPopulation();
        }

        // destruction recursive des dataset composants this
        System.out.println(" ");
        Iterator itDS = this.getComposants().iterator();
        while (itDS.hasNext() ) {
            DataSet DS = (DataSet)itDS.next();
            if ( DS.getPersistant() ) DS.detruitJeu() ;
        }
        
		// destruction des zones d'extraction associées à this
		System.out.println(" ");
		Iterator itExt = this.getExtractions().iterator();
		while (itExt.hasNext() ) {
			Extraction ex = (Extraction)itExt.next();
			System.out.println("###### Destruction de la zone d'extraction "+ex.getNom());
			db.deletePersistent(ex);
		}

		//destruction de this
        System.out.println("###### Destruction du DataSet "+this.getNom());
        db.deletePersistent(this);
    }
    
    /** Booléen spécifiant si le thème est persistant ou non (vrai par défaut).  
     *  NB : si un jeu de données est non persistant, tous ses thèmes sont non persistants.
     *  Mais si un jeu de données est persistant, certains de ses thèmes peuvent ne pas l'être.
     * 
	 * ATTENTION: pour des raisons propres à OJB, même si la classe DataSet est concrète,
 	 * il n'est pas possible de créer un objet PERSISTANT de cette classe, 
 	 * il faut utiliser les sous-classes.
     */
    // NB pour codeurs : laisser 'true' par défaut. Sinon, comme cet attribut n'est pas persistant, 
    // cela pose des problèmes au chargement (un thème persistant chargé a son attribut persistant à false.
    protected boolean persistant = true;
    public boolean getPersistant() {return persistant;}
    public void setPersistant(boolean b) {persistant = b;}
    
///////////////////////////////////////////////////////
//          Metadonnées     
///////////////////////////////////////////////////////
     /** Nom de la classe concrète de this : pour OJB, ne pas manipuler directement */
     protected String ojbConcreteClass;
     public String getOjbConcreteClass() {return ojbConcreteClass;}
     public void setOjbConcreteClass(String S) {ojbConcreteClass = S;}
     
     /** Nom du jeu de données */
     protected String nom;
     public String getNom() {return nom; }
     public void setNom (String S) {nom = S; }

     /** Type de BD (BDcarto, BDTopo...). */
     protected String typeBD;
     public String getTypeBD() {return typeBD; }
     public void setTypeBD (String S) {typeBD = S; }
     
     /** Modèle utilisé (format shape, structuré...). */
     protected String modele;
     public String getModele() {return modele; }
     public void setModele(String S) {modele = S; }

     /** Zone géographique couverte. */
     protected String zone;
     public String getZone() {return zone; }
     public void setZone(String S) {zone = S; }

     /** Date des données. */
     protected String date;
     public String getDate() {return date; }
     public void setDate(String S) {date = S; }

     /** Commentaire quelconque. */
     protected String commentaire;
     public String getCommentaire() {return commentaire; }
     public void setCommentaire(String S) {commentaire = S; }

     
///////////////////////////////////////////////////////
//          Thèmes du jeu de données
///////////////////////////////////////////////////////
    /** Un DataSet se décompose récursivement en un ensemble de DataSet. 
     *  Le lien de DataSet vers lui-même est un lien 1-n.
     *  Les méthodes get (sans indice) et set sont nécessaires au mapping. 
     *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
     *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes. 
     *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
     *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
     */
    protected List composants = new ArrayList();   

    /** Récupère la liste des DataSet composant this. */
    public List getComposants() {return composants ; } 
    /** Définit la liste des DataSet composant le DataSet, et met à jour la relation inverse. */
    public void setComposants(List L) {
        List old = new ArrayList(composants);
        Iterator it1 = old.iterator();
        while ( it1.hasNext() ) {
            DataSet O = (DataSet)it1.next();
            O.setAppartientA(null);
        }
        Iterator it2 = L.iterator();
        while ( it2.hasNext() ) {
            DataSet O = (DataSet)it2.next();
            O.setAppartientA(this);
        }
    }
    /** Récupère le ième élément de la liste des DataSet composant this. */
    public DataSet getComposant(int i) {return (DataSet)composants.get(i) ; }  
    /** Ajoute un objet à la liste des DataSet composant le DataSet, et met à jour la relation inverse. */
    public void addComposant(DataSet O) {
        if ( O == null ) return;
        composants.add(O) ;
        O.setAppartientA(this) ;
    }
    /** Enlève un élément de la liste DataSet composant this, et met à jour la relation inverse. */
    public void removeComposant(DataSet O) {
        if ( O == null ) return;
        composants.remove(O) ; 
        O.setAppartientA(null);
    }
    /** Vide la liste des DataSet composant this, et met à jour la relation inverse. */
    public void emptyComposants() {
        List old = new ArrayList(composants);
        Iterator it = old.iterator(); 
        while ( it.hasNext() ) {
            DataSet O = (DataSet)it.next();
            O.setAppartientA(null);
        }
    }
    /** Recupère le DataSet composant de this avec le nom donné. */
    public DataSet getComposant(String nom) {
        DataSet th;
        Iterator it = this.getComposants().iterator();
        while ( it.hasNext() ) {
            th = (DataSet)it.next();
            if ( th.getNom().equals(nom) ) return th;
        }
        System.out.println("----- ATTENTION : DataSet composant #"+nom+"# introuvable dans le DataSet "+this.getNom());
        return null;
    }

    
    /** Relation inverse à Composants */
    private DataSet appartientA;
    /** Récupère le DataSet dont this est composant. */
    public DataSet getAppartientA() {return appartientA;  }
    /** Définit le DataSet dont this est composant., et met à jour la relation inverse. */
    public void setAppartientA(DataSet O) {
        DataSet old = appartientA;
        appartientA = O;  
        if ( old  != null ) old.getComposants().remove(this);
        if ( O != null ) {
            appartientAID = O.getId();
            if ( !(O.getComposants().contains(this)) ) O.getComposants().add(this);            
        } else appartientAID = 0;
    }
    private int appartientAID;
    /** Ne pas utiliser, necessaire au mapping OJB */
    public void setAppartientAID(int I) {appartientAID = I;}
    /** Ne pas utiliser, necessaire au mapping OJB */
    public int getAppartientAID() {return appartientAID;}
    

    /** Liste des population du DataSet. 
     *  Les méthodes get (sans indice) et set sont nécessaires au mapping. 
     *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
     *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes. 
     *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
     *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
     */
    protected List populations = new ArrayList();   

    /** Récupère la liste des populations en relation. */
    public List getPopulations() {return populations ; } 
    /** Définit la liste des populations en relation, et met à jour la relation inverse. */
    public void setPopulations (List L) {
        List old = new ArrayList(populations);
        Iterator it1 = old.iterator();
        while ( it1.hasNext() ) {
            Population O = (Population)it1.next();
            O.setDataSet(null);
        }
        Iterator it2 = L.iterator();
        while ( it2.hasNext() ) {
            Population O = (Population)it2.next();
            O.setDataSet(this);
        }
    }
    /** Récupère le ième élément de la liste des populations en relation. */
    public Population getPopulation(int i) {return (Population)populations.get(i) ; }  
    /** Ajoute un objet à la liste des populations en relation, et met à jour la relation inverse. */
    public void addPopulation(Population O) {
        if ( O == null ) return;
        populations.add(O) ;
        O.setDataSet(this) ;
    }
    /** Enlève un élément de la liste des populations en relation, et met à jour la relation inverse. */
    public void removePopulation(Population O) {
        if ( O == null ) return;
        populations.remove(O) ; 
        O.setDataSet(null);
    }
    /** Vide la liste des populations en relation, et met à jour la relation inverse. */
    public void emptyPopulations() {
        List old = new ArrayList(populations);
        Iterator it = old.iterator(); 
        while ( it.hasNext() ) {
            Population O = (Population)it.next();
            O.setDataSet(null);
        }
    }
    /** Recupère la population avec le nom donné. */
    public Population getPopulation(String nom) {
        Population th;
        Iterator it = this.getPopulations().iterator();
        while ( it.hasNext() ) {
            th = (Population)it.next();
            if ( th.getNom().equals(nom) ) return th;
        }
        System.out.println("=============== ATTENTION : population '"+nom+"' introuvable ==============");
        return null;
    }
    
    
    
	/** Liste des zones d'extraction définies pour ce DataSt */
	protected List extractions = new ArrayList();   

	/** Récupère la liste des extractions en relation. */
	public List getExtractions() {return extractions; } 
	/** Définit la liste des extractions en relation. */
	public void setExtractions(List L) {
		extractions = L;
	}
	/** Ajoute un élément de la liste des extractions en relation. */
	public void addExtraction(Extraction O) {
		extractions.add(O) ;
	}
    
    
}
