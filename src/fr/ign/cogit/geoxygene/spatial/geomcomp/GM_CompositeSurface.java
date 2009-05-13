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

package fr.ign.cogit.geoxygene.spatial.geomcomp;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_SurfaceBoundary;


/** NON UTILISE POUR LE MOMENT. A TERMINER ET TESTER.
 * Complexe ayant toutes les propriétés géométriques d'une surface.
 * C'est une liste de surfaces orientées (GM_OrientableSurfaces) contigues.
 * Hérite de GM_OrientableCurve, mais le lien n'apparaît pas explicitement (problème de double héritage en java). Les méthodes et attributs ont été reportés.
 *
 *  <P> ATTENTION : normalement, il faudrait remplir le set "element" (contrainte : toutes les primitives du generateur
 * sont dans le complexe). Ceci n'est pas implémenté pour le moment.
 *  <P> A FAIRE AUSSI : iterateur sur "generator"
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 *
 */

public class GM_CompositeSurface extends GM_Composite {

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Attribut "generator" et méthodes pour le traiter ////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	/** Les GM_OrientableSurface constituant self. */
	protected List<GM_OrientableSurface> generator;

	/** Renvoie la liste des GM_OrientableSurface */
	public List<GM_OrientableSurface> getGenerator() {return generator;}

	/** Renvoie la GM_OrientableSurface de rang i */
	public GM_OrientableSurface getGenerator (int i) {return this.generator.get(i);}

	/** Affecte une GM_OrientableSurface au rang i. Attention : aucun contrôle de continuité n'est effectué. */
	public void setGenerator (int i, GM_OrientableSurface value) {this.generator.set(i, value);}

	/** Ajoute une GM_OrientableSurface en fin de liste. Attention : aucun contrôle de continuité n'est effectué. */
	public void addGenerator (GM_OrientableSurface value) {this.generator.add(value);}

	/** A FAIRE.
	 * Ajoute une GM_OrientableSurface en fin de liste avec un contrôle de continuité avec la tolérance passée en paramètre.
	 * Envoie une exception en cas de problème.
	 * @param value
	 * @param tolerance
	 * @throws Exception
	 */
	public void addGenerator (GM_OrientableSurface value, double tolerance) throws Exception {
	}

	/** A FAIRE.
	 * Ajoute une GM_OrientableSurface en fin de liste avec un contrôle de continuité avec la tolérance passée en paramètre.
	 * Eventuellement change le sens d'orientation de la surface pour assurer la continuite.
	 * Envoie une exception en cas de problème.
	 * @param value
	 * @param tolerance
	 * @throws Exception
	 */
	public void addGeneratorTry (GM_OrientableSurface value, double tolerance) throws Exception {
	}

	/** Ajoute une GM_OrientableSurface au rang i. Attention : aucun contrôle de continuité n'est effectué. */
	public void addGenerator (int i, GM_OrientableSurface value) {this.generator.add(i, value);}

	/** Efface la (ou les) GM_OrientableSurface passé en paramètre. Attention : aucun contrôle de continuité n'est effectué. */
	public void removeGenerator (GM_OrientableSurface value) throws Exception {
		if (this.generator.size() == 1) throw new Exception ( "Il n'y a qu'un objet dans l'association." );
		this.generator.remove(value);
	}

	/** Efface la GM_OrientableSurface de rang i. Attention : aucun contrôle de continuité n'est effectué. */
	public void removeGenerator (int i) throws Exception {
		if (this.generator.size() == 1) throw new Exception ( "Il n'y a qu'un objet dans l'association." );
		this.generator.remove(i);
	}

	/** Nombre de GM_OrientableSurface constituant self */
	public int sizeGenerator () {return this.generator.size();}




	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Constructeurs ///////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// les constructeurs sont calques sur ceux de GM_Surface
	/** Constructeur par défaut */
	public GM_CompositeSurface() {
		generator = new ArrayList<GM_OrientableSurface>();
		primitive = new GM_Surface();
		proxy[0] = primitive;
		GM_OrientableSurface proxy1 = new GM_OrientableSurface();
		proxy1.orientation = -1;
		proxy1.proxy[0] = primitive;
		proxy1.proxy[1] = proxy1;
		proxy1.primitive = new GM_Surface(primitive);
		proxy[1] = proxy1;
	}

	/** Constructeur à partir d'une et d'une seule GM_OrientableSurface.
	 *  L'orientation vaut +1. */
	public GM_CompositeSurface(GM_OrientableSurface oCurve) {
		generator = new ArrayList<GM_OrientableSurface>();
		generator.add(oCurve);
		primitive = new GM_Surface();
		//        this.simplifyPrimitive(); -> creer la primitive
		proxy[0] = primitive;
		GM_OrientableSurface proxy1 = new GM_OrientableSurface();
		proxy1.orientation = -1;
		proxy1.proxy[0] = primitive;
		proxy1.proxy[1] = proxy1;
		proxy1.primitive = new GM_Surface(primitive);
		proxy[1] = proxy1;
	}




	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Attributs et méthodes héritées de GM_OrientableSurface ////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// On simule l'heritage du modele en reportant les attributs et methodes
	// de GM_OrientableSurface
	// On n'a pas repris l'attribut "orientation" qui ne sert a rien ici.

	/** Primitive. Elle doit etre recalculée à chaque modification de self : fait dans getPrimitive(). */
	protected GM_Surface primitive;

	/** Renvoie la primitive de self. */
	// le calcul est fait en dynamique dans la methode privee simplifyPrimitve.
	public GM_Surface getPrimitive ( Geodatabase data)  {
		this.simplifyPrimitive(data);
		return this.primitive;
	}

	/**
	 * Attribut stockant les primitives orientées de cette primitive.
	 * Proxy[0] est celle orientée positivement.
	 * Proxy[1] est celle orientée négativement.
	 * On accède aux primitives orientées par getPositive() et getNegative().
	 */
	protected GM_OrientableSurface[] proxy = new GM_OrientableSurface[2];

	/** Renvoie la primitive orientée positivement. */
	public GM_OrientableSurface getPositive(Geodatabase data) {
		this.simplifyPrimitive(data);
		return this.primitive;       // equivaut a return this.proxy[0]
	}

	/** Renvoie la primitive orientée négativement. */
	public GM_OrientableSurface getNegative(Geodatabase data)  {
		this.simplifyPrimitive(data);
		return this.primitive.getNegative();
	}

	/** Redéfinition de l'opérateur "boundary" sur GM_OrientableSurface. Renvoie une GM_SurfaceBoundary.  */
	public GM_SurfaceBoundary boundary(Geodatabase data)  {
		this.simplifyPrimitive(data);
		return this.primitive.boundary();
	}




	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Méthodes "validate" /////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	/** 
	 * A FAIRE - renvoie toujours true pour le moment.
	 * Vérifie la continuité des composants. Renvoie TRUE s'ils sont contigus, FALSE sinon.
	 * Cette méthode n'est pas dans la norme.
	 * @param tolerance
	 * @return
	 */
	public boolean validate(double tolerance) {
		/*    for (int i=0; i<generator.size()-1; i++) {
            GM_OrientableCurve oCurve1 = (GM_OrientableCurve)generator.get(i);
            GM_Curve prim1 = (GM_Curve)oCurve1.getPrimitive();
            GM_OrientableCurve oCurve2 = (GM_OrientableCurve)generator.get(i+1);
            GM_Curve prim2 = (GM_Curve)oCurve2.getPrimitive();
            DirectPosition pt1 = prim1.endPoint();
            DirectPosition pt2 = prim2.startPoint();
            if (!pt1.equals(pt2,tolerance))
                return false;
        }*/
		return true;
	}




	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Méthodes privées pour usage interne /////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	/** 
	 * Calcule la primitive se self. MARCHE PAS
	 * @param data
	 */
	private void simplifyPrimitive(Geodatabase data)  {
		/*       int n = generator.size();
        if (n > 1) {
            GM_Surface prim = (GM_Surface)this.primitive;
            prim = this.getGenerator(0);
            // clonage de la primitive
            GM_Surface union = new GM_Surface();
            for (int i=0; i<prim.cardPatch(); i++)
                union.appendPatch(prim.getPatch(i));

            union = prim;
            for (int i=1; i<n; i++) {
                GM_Surface surf = new GM_Surface(((GM_Surface)this.getGenerator(i)).getPrimitive());
                union = (GM_Surface)union.union(data,0.0000000001,surf);
            }
        }*/
	}





}