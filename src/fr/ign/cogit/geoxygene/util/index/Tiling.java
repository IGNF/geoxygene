/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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
 
package fr.ign.cogit.geoxygene.util.index;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/**
 * Index spatial par simple dallage.
 * 
 * @author Thierry Badard, Arnaud Braun & Sébastien Mustière
 * @version 1.0
 */

public class Tiling implements SpatialIndex {

	// ===============================================
	/** Renvoie les paramètres du dallage.
	 * 
	 * ArrayList de 4 éléments:
	 * - 1er  élément : Class égal à Dallage.class  
	 * - 2ème élément : Boolean indiquant si l'index est en mode MAJ automatique ou non
	 * - 3ème élément : GM_Envelope décrivant les limites de la zone couverte
	 * - 4ème élément : Integer exprimant le nombre de cases en X et Y.
	 *  
	 *  
	 */
	public List getParametres() {
		List param = new ArrayList();
		param.add(Tiling.class);
		param.add(new Boolean(automaticUpdate));
		param.add(new GM_Envelope(xmin,xmax,ymin,ymax));
		param.add(new Integer(size));
		return param;
	}

	// ===============================================
	/** Tableau de collections de features appartenant a chaque dalle.
	 * Un feature peut appartenir a plusieurs dalles. */
	private Set[][] index;


	// ===============================================	
	/** Taille du dallage (nombre de rectangles par cote). */
	private int size;
	
	/** Taille du dallage (nombre de rectangles par cote). */
	public int getSize() {
		return size;
	}  


	// xmin, xmax, ymin, ymax
	/** paramètre interne du dallage */ 
	private double xmin ;
	/** paramètre interne du dallage */ 
	private double xmax ;
	/** paramètre interne du dallage */ 
	private double ymin ;
	/** paramètre interne du dallage */ 
	private double ymax ;
	            
	// calcul de dX et dY
	/** paramètre interne du dallage */ 
	private double dX ;
	/** paramètre interne du dallage */ 
	private double dY ;

	/** Tableau à deux dimensions des dalles sous forme de Polygones. */
	private GM_Polygon[][] dallesPolygones;
	 
	// ===============================================
	/** Indique si l'on a demande une mise a jour automatique. */
	private boolean automaticUpdate;
	
	/** Indique si l'on a demande une mise a jour automatique. */
	public boolean hasAutomaticUpdate() {
		return automaticUpdate;
	}

	/** Demande une mise a jour automatique.
	 * NB: Cette méthode ne fait pas les éventuelles MAJ qui
	 * auriant ête faites alors que le mode MAJ automatique n'était
	 * pas activé.
	 */
	public void setAutomaticUpdate(boolean auto) {
		automaticUpdate = auto;
	}

     
	// ===============================================    
	/** Tableau à deux dimensions des dalles. */
	private GM_Envelope[][] dallage;
	
    /** Renvoie le tableau à 2 dimensions des dalles. */
	public GM_Envelope[][] getDallage() {
		return dallage;
	}
	
    /** renvoie la dalle d'indice i,j. */
	public GM_Envelope getDallage(int i, int j) {
		return dallage[i][j];
	}
    
	/** Tableau des dalles contenant le feature. */
	public GM_Envelope[] getDallage(FT_Feature feat) {
		List result = new ArrayList();
		for (int i=0; i < size; i++)
			 for (int j=0; j < size; j++)
				if (index[i][j].contains(feat))
					result.add(dallage[i][j]);           
		GM_Envelope[] array = new GM_Envelope[result.size()];
		for (int k=0; k < result.size(); k++) 
			array[k] = (GM_Envelope)result.get(k);
		return array;
	}
	
	/** Tableau des numéros des dalles contenant le feature. */
	public List getNumDallage(FT_Feature feat) {
		List result = new ArrayList();
		for (int i=0; i < size; i++) {
			for (int j=0; j < size; j++) {
			   if (index[i][j].contains(feat)) {
					List couple = new ArrayList();
					couple.add(new Integer(i)); 
					couple.add(new Integer(j));
					result.add(couple); 
			   }
			}
		}
		return result;		
	}
	

	/** Dalle couvrant le point passe en parametre. 
	 * Renvoie NULL si aucune dalle ne couvre ce point.*/
	public GM_Envelope getDallage(DirectPosition dp) {
		for (int i=0; i < size; i++)
			 for (int j=0; j < size; j++)
				if (dallage[i][j].contains(dp))
					return dallage[i][j];
		return null;
	}
	

	/** Etant donné une enveloppe, renvoie les indices min et max des
	 *  dalles qui intersectent cette enveloppe 
	 *  (dans l'ordre: imin, imax, jmin, jmax)
	 */
    private int[] dallesIntersectees(GM_Envelope env) {
		int i, imin = 0, imax = size-1, jmin= 0, jmax=size-1;
    	boolean min=true;
		for(i=0;i<size;i++){
			if (min) {
				 if (env.getLowerCorner().getX()<=dallage[i][0].getUpperCorner().getX()) {
					imin = i;
					min = false;
				 }
			}
			if (!min) {
				if (env.getUpperCorner().getX()<=dallage[i][0].getUpperCorner().getX()) {
				   imax = i;
				   break;
				}    			
			}
		}
		min = true;
		for(i=0;i<size;i++){
			if (min) {
				 if (env.getLowerCorner().getY()<=dallage[0][i].getUpperCorner().getY()) {
					jmin = i;
					min = false;
				 }
			}
			if (!min) {
				if (env.getUpperCorner().getY()<=dallage[0][i].getUpperCorner().getY()) {
				   jmax = i;
				   break;
				}    			
			}
		}
		int tab[] = {imin,imax,jmin,jmax};
		return tab; 
    	
/* AUTRE METHODE POSSIBLE POUR FAIRE LA MEME CHOSE MAIS BIZARREMENT CA A L'AIR MOINS RAPIDE    	
    	int imin, imax, jmin, jmax;
		double imind, jmind, imindf, imaxdf,jmindf,jmaxdf;

		imind = (env.getLowerCorner().getX()-xmin)/dX;
		imindf = Math.floor(imind);
		if (imindf == imind ) {if (imindf != 0) imindf = imindf -1;} 
		imin = (new Double(imindf)).intValue(); 

		imaxdf = Math.floor((env.getUpperCorner().getX()-xmin)/dX);
		if ( imaxdf == size ) imaxdf = size-1;
		imax = (new Double(imaxdf)).intValue(); 

		jmind = (env.getLowerCorner().getY()-ymin)/dY;
		jmindf = Math.floor(jmind);
		if (jmindf == jmind ) {if (jmindf != 0)  jmindf = jmindf -1;}
		jmin = (new Double(jmindf)).intValue(); 

		jmaxdf = Math.floor((env.getUpperCorner().getY()-ymin)/dY);
		if ( jmaxdf == size ) jmaxdf = size-1;
		jmax = (new Double(jmaxdf)).intValue(); 
*/
    }

	// ===============================================
	/** Features appartenant a la dalle d'indice i,j.*/
	public FT_FeatureCollection select (int i, int j) {
		return new FT_FeatureCollection(index[i][j]);
	}



	// ===============================================
	/** Selection a l'aide d'un rectangle. */
	public FT_FeatureCollection select(GM_Envelope env) {
	   int tab[];
	   Set result = new HashSet();
	   GM_Object geometry = new GM_Polygon(env);
	   if (env.getUpperCorner().getX() == env.getLowerCorner().getX() )
	   		if (env.getUpperCorner().getY() == env.getLowerCorner().getY() )
	   			geometry = new GM_Point(env.getUpperCorner());

	   tab = this.dallesIntersectees(env);
	   for (int i=tab[0]; i <= tab[1]; i++) {
		   for (int j=tab[2]; j <= tab[3]; j++) {
				Iterator it = index[i][j].iterator();
			 	while (it.hasNext()) {
				   FT_Feature feature = (FT_Feature) it.next();
				   GM_Object geom = feature.getGeom();
				   GM_Envelope envCourante = geom.envelope();
				   if (env.overlaps(envCourante))
				   		if (geometry.intersects(geom))					   
							result.add(feature);
			   }
		   }
	   }
	   FT_FeatureCollection collectionresult = new FT_FeatureCollection();
	   collectionresult.setElements(new ArrayList(result));
	   return collectionresult;                                       
	}



	// ===============================================
	/** Selection dans le carre dont P est le centre, de cote D. 
	 * NB: D peut être nul.*/
	public FT_FeatureCollection select (DirectPosition P, double D) {
		return select (new GM_Envelope(P,D));
	}
		
	
	
	// ===============================================
	/** Selection des objets qui intersectent un objet geometrique quelconque. */
	public FT_FeatureCollection select(GM_Object geometry)  {
	int tab[];
	   Set result = new HashSet();
	   GM_Envelope envGeometry = geometry.envelope();
	   tab = this.dallesIntersectees(envGeometry);
	   for (int i=tab[0]; i <= tab[1]; i++) {
		   for (int j=tab[2]; j <= tab[3]; j++) {
			   if (geometry.intersects(dallesPolygones[i][j])) {
					Iterator it = index[i][j].iterator();
					while (it.hasNext()) {
					   FT_Feature feature = (FT_Feature) it.next();
					   GM_Object geom = feature.getGeom();
					   GM_Envelope envCourante = geom.envelope();
					   if (envGeometry.overlaps(envCourante))
						   if (geometry.intersects(geom))					   
								result.add(feature);		
					}
			   }
		   }
	   }
	   FT_FeatureCollection collectionresult = new FT_FeatureCollection();
	   collectionresult.setElements(new ArrayList(result));	   	   
	   return collectionresult;                    
	}

	/** Selection des objets qui croisent ou intersectent un objet geometrique quelconque.
	 * 
	 * @param strictlyCrosses
	 * Si c'est TRUE : ne retient que les  objets qui croisent (CROSS au sens JTS)
	 * Si c'est FALSE : ne retient que les  objets qui intersectent (INTERSECT au sens JTS)
	 * Exemple : si 1 ligne touche "geometry" juste sur une extrémité, 
	 * alors avec TRUE cela ne renvoie pas la ligne, avec FALSE cela la renvoie
	 */
	public FT_FeatureCollection select(GM_Object geometry, boolean strictlyCrosses)  {
	   int tab[];
	   Set result = new HashSet();
	   GM_Envelope envGeometry = geometry.envelope();
	   tab = this.dallesIntersectees(envGeometry);
	   for (int i=tab[0]; i <= tab[1]; i++) {
		   for (int j=tab[2]; j <= tab[3]; j++) {
				if (!geometry.intersects(dallesPolygones[i][j])) continue;
				Iterator it = index[i][j].iterator();
				while (it.hasNext()) {
				   FT_Feature feature = (FT_Feature) it.next();
				   GM_Object geom = feature.getGeom();
				   GM_Envelope envCourante = geom.envelope();
				   if (envGeometry.overlaps(envCourante))
				   		if (strictlyCrosses) {  
							if (geometry.crosses(geom)) result.add(feature);
				   		}
					   	else 		
							if (geometry.intersects(geom)) result.add(feature);
				}
		   }
	   }
	   FT_FeatureCollection collectionresult = new FT_FeatureCollection();
	   collectionresult.setElements(new ArrayList(result));
	   return collectionresult;                    
	}


	// ===============================================
	/** Selection a l'aide d'un objet geometrique quelconque et d'une distance. 
	 * NB: D peut être nul.*/
	public FT_FeatureCollection select(GM_Object geometry, double distance) {
		if ( distance == 0 ) return select(geometry);
		return select(geometry.buffer(distance));
	}
	
	



	// ===============================================
	//                CONSTRUCTEURS 	
	// ===============================================

	/** Crée et instancie un dallage d'une collection de FT_Feature, 
	 * en fonction des limites de la zone 
	 * et du nombre de cases souhaitées sur la zone.
	 * 
	 * @param fc
	 * La liste de Features à indexer
	 * 
	 * @param automaticUpd
	 * Spéciifie si l'index doit être mis à jour automatiquement 
	 * quand on modifie les objets de fc
	 * 
	 * @param envelope
	 * Enveloppe décrivant les limites de l'index spatial.
	 * NB: Tout objet hors de ces limites ne sera pas traité lors des requêtes spatiales !!!!!
	 * 
	 * @param n
	 * Nombre de dalles en X et en Y, du dallage.
	 */		
	public Tiling (FT_FeatureCollection fc, Boolean automaticUpd, GM_Envelope envelope, Integer n) {
	    int tab[];
		// initialisation des variables
		size = n.intValue();
		dallage = new GM_Envelope[size][size];
		automaticUpdate = automaticUpd.booleanValue();
		index = new Set[size][size];
		for (int i=0; i < size; i++)
			for (int j=0; j < size; j++)
				index[i][j] = new  HashSet();
	            
		// xmin, xmax, ymin, ymax
		xmin = envelope.minX();
		xmax = envelope.maxX();
		ymin = envelope.minY();
		ymax = envelope.maxY();
	            
		// calcul de dX et dY
		dX = (xmax-xmin) / size;
		dY = (ymax-ymin) / size;
	    
		// ecriture des dalles   
		for (int i=0; i < size; i++) {
			for (int j=0; j < size; j++) {
				GM_Envelope env = new GM_Envelope();
				env.setLowerCorner( new DirectPosition(xmin + (i * dX), ymin + (j * dY)) );
				env.setUpperCorner( new DirectPosition(xmin + ((i+1) * dX), ymin + ((j+1) * dY)) );
				dallage[i][j] = env;
			}
		}
	            
		// initialisation d'un tableau de polygones
		dallesPolygones = new GM_Polygon[size][size];
		for (int i=0; i < size; i++) {
		   for (int j=0; j < size; j++) {					 			 
			dallesPolygones[i][j] = new GM_Polygon(dallage[i][j]);		
		   }
		}								 				
					            
		// calcul de l'index			 	 
		fc.initIterator();
		while (fc.hasNext()) {				
			 FT_Feature feature = fc.next();
			 GM_Object geom = feature.getGeom();
			 GM_Envelope envObjet = geom.envelope();
			 tab = this.dallesIntersectees(envObjet);
			 for (int i=tab[0]; i <= tab[1]; i++) {
				for (int j=tab[2]; j <= tab[3]; j++) {
					if (geom.intersects(dallesPolygones[i][j])) {
						index[i][j].add(feature);	
					}
				}
			 }
		}
	 }


	/** Crée et instancie un dallage d'une collection de FT_Feature, 
	 * en fonction du nombre de cases souhaitées sur la zone.
	 * NB: les limites de la zone de l'index sont celles de la collection traitée.
	 * Il est donc impossible de rajouter ensuite dans la collection un objet 
	 * en dehors de cette zone. 
	 * 
	 * @param fc
	 * La liste de Features à indexer
	 * 
	 * @param automaticUpd
	 * Spéciifie si l'index doit être mis à jour automatiquement 
	 * quand on modifie les objets de fc
	 * 
	 * @param n
	 * Nombre de dalles en X et en Y, du dallage.
	 */		
	public Tiling (FT_FeatureCollection fc, Boolean automaticUpd, Integer n) {
		this(fc, automaticUpd, fc.envelope(), n);
	}
		
	/** Crée et instancie un dallage d'une collection de FT_Feature.
	 * Les paramètres sont définis par la collection en entrée:  
	 * 1/ Les limites de la zone de l'index sont celles de la collection traitée.
	 * Il est donc impossible de rajouter ensuite dans la collection un objet 
	 * en dehors de cette zone.
	 * 2/ Le nombre de cases est défini automatiquement pour qu'il y ait
	 * de l'ordre de 50 objets par dalle en moyennne (approximatif) 
	 * 
	 * @param fc
	 * La liste de Features à indexer
	 * 
	 * @param automaticUpd
	 * Spéciifie si l'index doit être mis à jour automatiquement 
	 * quand on modifie les objets de fc
	 * 
	 */		
	public Tiling (FT_FeatureCollection fc, Boolean automaticUpd) {
		this(fc,automaticUpd,new Integer(nbDallesXY(fc)));
	}

	private static int nbDallesXY(FT_FeatureCollection fc) {
		int nb = (int)Math.sqrt(fc.size()/50);
		if (nb == 0) nb=1;
		return nb;
	}
	/** Crée et instancie un dallage en reprenant les paramètres d'un autre dallage.
	 */		
	public Tiling (FT_FeatureCollection fc, Tiling spIdx) {
		this(fc,(Boolean)spIdx.getParametres().get(1),(GM_Envelope)spIdx.getParametres().get(2),(Integer)spIdx.getParametres().get(3));
	}



	// ===============================================
	//                MISE A JOUR 	
	// ===============================================

	/** Met a jour l'index avec le FT_Feature. 
	 * Si cas vaut +1 : on ajoute le feature.
	 * Si cas vaut -1 : on enleve le feature.
	 * Si cas vaut 0 : on modifie le feature.*/
	public void update(FT_Feature value, int cas) {
		int tab[];
		if (cas==1) {
			if (value == null) return;
			GM_Object geom = value.getGeom();
			if (geom == null) return;
			GM_Envelope envObjet = geom.envelope();
			tab = this.dallesIntersectees(envObjet);
			for (int i=tab[0]; i <= tab[1]; i++) {
			   for (int j=tab[2]; j <= tab[3]; j++) {
				   if (geom.intersects(dallesPolygones[i][j])) {
					   index[i][j].add(value);	
				   }
			   }
			}
		}		
		else if (cas ==-1) {
			GM_Envelope[] envs = getDallage(value);
			for (int k=0; k<envs.length; k++) {
				Iterator itDallesConcernees = this.getNumDallage(value).iterator();
				while (itDallesConcernees.hasNext()) {
					List num = (List) itDallesConcernees.next();
					index[((Integer)num.get(0)).intValue()][((Integer)num.get(1)).intValue()].remove(value);
				}
			}
		}									
		else if (cas == 0) {
			this.update(value,-1);
			this.update(value,+1);
		}
		else {
			System.out.println("spatialIndex.update(value, cas) : \"cas\" doit valoir +1, -1 ou 0.");
		}		
	}
	        

}
