package fr.ign.cogit.geoxygene.sig3d.tetraedrisation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * @author Bonin
 * @version 0.1
 * 
 *          Classe permettant de gérer la décomposition de solides en tétraèdres
 *          ou en triangles. Cette classe fait appel à la bibliothèque TetGen
 *          via JNI http://tetgen.berlios.de/ Pour procéder à une
 *          tetraedrisation, il faut 1) créer un objet de type tétraèdrision 2)
 *          faire appel à la méthode tetraedrise, tetraedriseContrainte ou
 *          tetraedriseNonContrainte Classe for solid decompositions using
 *          TetGen library : http://tetgen.berlios.de/
 * 
 */

public class Tetraedrisation {

	private final static Logger logger = Logger.getLogger(Tetraedrisation.class.getName());

	protected List<GM_Solid> tetraedres = new ArrayList<GM_Solid>();
	protected List<ITriangle> triangles = new ArrayList<ITriangle>();
	protected IGeometry geometry = null;

	/**
	 * @return renvoie les tétraèdres de la décomposition (si ils existent)
	 */
	public List<GM_Solid> getTetraedres() {
		return this.tetraedres;
	}

	/**
	 * @return renvoie les triangles générés par la décomposition (si ils existent)
	 */
	public List<ITriangle> getTriangles() {
		return this.triangles;
	}

	/*
	 * Les classes permettant de faire le lien avec la DLL
	 */
	protected Tetgenio jTetgenioIn = new Tetgenio();
	protected Tetgenio jTetgenioOut = new Tetgenio();

	/**
	 * Constructeur
	 * 
	 * @param feat
	 *            l'entité que l'on souhaite décomposer (dimension 3 obligatoire)
	 */
	public Tetraedrisation(IFeature feat) {
		if (feat.getGeom() instanceof GM_Solid) {

			this.geometry = feat.getGeom();
		} else {

			Tetraedrisation.logger.error(Messages.getString("3DGIS.IsNotSolid"));
		}
	}

	/**
	 * Décomposition d'un solide
	 * 
	 * @param sol
	 *            le solide que l'on souhaite décomposer
	 */
	public Tetraedrisation(GM_Solid sol) {

		this.geometry = sol;

	}

	/**
	 * Permet de convertir dans une format compréhensible par le DLL TODO la
	 * tétraèdrisation contrainte ne fonctionne pas Il faut absolument utiliser des
	 * triangles
	 * 
	 * @param constraint
	 *            indique si l'on fait une décomposition contrainte par les faces du
	 *            solide ou nom (le résultat sera la forme convexe de l'objet
	 *            initial)
	 */
	@SuppressWarnings("unchecked")
	protected void convertJin(boolean constraint) {
		List<IOrientableSurface> listeFacettes = new ArrayList<IOrientableSurface>();

		if (constraint) {

			listeFacettes = ((GM_Solid) this.geometry).getFacesList();
		} else {

			IGeometry geom = this.geometry;

			if (geom instanceof GM_Solid) {

				listeFacettes = ((GM_Solid) geom).getFacesList();

			} else if (geom instanceof GM_MultiSolid<?>) {

				GM_MultiSolid<GM_Solid> gmMultiS = (GM_MultiSolid<GM_Solid>) geom;
				int nbElement = gmMultiS.size();

				for (int i = 0; i < nbElement; i++) {

					listeFacettes.addAll((gmMultiS.get(i)).getFacesList());
				}

			}

		}

		int k = 0;
		int nbFacette = listeFacettes.size();

		// DirectPositionList pointstraites = new DirectPositionList();

		for (int i = 0; i < nbFacette; i++) {

			IOrientableSurface face = listeFacettes.get(i);
			IDirectPositionList lPoints = face.coord();

			int nbPoints = lPoints.size() - 1;

			for (int j = 0; j < nbPoints; j++) {
				IDirectPosition point = lPoints.get(j);

				this.jTetgenioIn.pointlist[3 * k] = point.getX();
				this.jTetgenioIn.pointlist[3 * k + 1] = point.getY();
				this.jTetgenioIn.pointlist[3 * k + 2] = point.getZ();
				k = k + 1;

			}

		}
		this.jTetgenioIn.numberofpoints = k;

		if (!constraint) {
			return;
		}
		// Tétraèdrisation contrainte

		// Nous avons la liste des points, il faut créer les polygones
		// Pour la tétraèdrisation contrainte

		this.jTetgenioIn.numberoffacets = nbFacette;
		int pointActuel = 0;

		this.jTetgenioIn.facetconstraintlist = new double[2 * nbFacette];

		for (int i = 0; i < nbFacette; i++) {
			this.jTetgenioIn.facetconstraintlist[i * 2] = i;
			this.jTetgenioIn.facetconstraintlist[i * 2 + 1] = 1.0;

			IOrientableSurface face = listeFacettes.get(i);
			IDirectPositionList lPonts = face.coord();

			int nbPoints = lPonts.size() - 1;

			// On ne ferme pas le triangle
			this.jTetgenioIn.facetmarkerlist[i] = nbPoints;

			for (int j = 0; j < nbPoints; j++) {
				this.jTetgenioIn.facetvertexlist[pointActuel] = pointActuel;
				pointActuel++;
			}

		}

	}

	/**
	 * Méthode faisant le lien avec la DLL
	 * 
	 * @param options
	 *            les options d'appel à la fonction (voir documentation
	 *            http://tetgen.berlios.de/)
	 * @param jin
	 *            l'objet paramètre décrivant l'objet à décomposer
	 * @param jout
	 *            l'objet en sortie décrivant l'objet décomposé
	 */
	protected native void tetraedriseC(String options, Tetgenio jin, Tetgenio jout);

	static {
		System.loadLibrary("tetrahedrize");

	}

	/**
	 * Tetraedrisation sans contrainte
	 * 
	 * @param constraint
	 *            indique si l'on contraint la tétraèdrisation
	 * @param mesh
	 *            true pour avoir une surface triangulée, false pour avoir des
	 *            tetraèdres
	 */
	public void tetraedrise(boolean constraint, boolean mesh) {

		if (constraint) {
			this.tetraedriseWithConstraint(mesh);
		} else {
			this.tetraedriseWithNoConstraint(mesh);
		}

	}

	/**
	 * la tetraèdrisation contrainte qui ne fonctionne pas l'option mesh permet de
	 * ne récupèrrer que les triangles
	 */
	public void tetraedriseWithConstraint(boolean mesh) {
		try {
			this.convertJin(true);
			this.tetraedriseC("pznQY", this.jTetgenioIn, this.jTetgenioOut);// pq1.2QYzna
			this.convertJout(mesh);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tetraedrisation sans contrainte
	 * 
	 * @param mesh
	 *            true pour avoir une surface triangulée, false pour avoir des
	 *            tetraèdres
	 */
	public void tetraedriseWithNoConstraint(boolean mesh) {

		if (this.geometry == null) {
			Tetraedrisation.logger.error(Messages.getString("3DGIS.GeomEmpty"));
			return;
		}

		this.convertJin(false);
		// tetraedriseC("znY", jin, jout);
		this.tetraedriseC("znY", this.jTetgenioIn, this.jTetgenioOut);

		this.convertJout(mesh);

	}

	/**
	 * Conversion du résultat de la tétraèdrisation en géométrie lisible
	 * 
	 * @param mesh
	 *            true pour avoir une surface triangulée, false pour avoir des
	 *            tetraèdres
	 */
	private void convertJout(boolean mesh) {
		try {

			DirectPositionList lPoints = new DirectPositionList();

			for (int i = 0; i < this.jTetgenioOut.numberofpoints; i++) {
				DirectPosition dp = new DirectPosition(this.jTetgenioOut.pointlist[3 * i],
						this.jTetgenioOut.pointlist[3 * i + 1], this.jTetgenioOut.pointlist[3 * i + 2]);
				lPoints.add(dp);
			}

			for (int i = 0; i < this.jTetgenioOut.numberoftrifaces; i++) {

				IDirectPosition n0 = lPoints.get(this.jTetgenioOut.trifacelist[3 * i]);
				IDirectPosition n1 = lPoints.get(this.jTetgenioOut.trifacelist[3 * i + 1]);
				IDirectPosition n2 = lPoints.get(this.jTetgenioOut.trifacelist[3 * i + 2]);

				this.triangles.add(new GM_Triangle(n0, n1, n2));
			}

			if (!mesh) {
				for (int i = 0; i < this.jTetgenioOut.numberoftetrahedra; i++) {

					// GM_Solid tetra = new Simplex3(
					// lPoints.get(jout.tetrahedronlist[4*i]),

					IDirectPosition n0 = lPoints.get(this.jTetgenioOut.tetrahedronlist[4 * i]);
					IDirectPosition n1 = lPoints.get(this.jTetgenioOut.tetrahedronlist[4 * i + 1]);
					IDirectPosition n2 = lPoints.get(this.jTetgenioOut.tetrahedronlist[4 * i + 2]);
					IDirectPosition n3 = lPoints.get(this.jTetgenioOut.tetrahedronlist[4 * i + 3]);

					DirectPositionList triangle1 = new DirectPositionList();
					triangle1.add(n0);
					triangle1.add(n1);
					triangle1.add(n2);
					triangle1.add(n0);

					DirectPositionList triangle2 = new DirectPositionList();
					triangle2.add(n1);
					triangle2.add(n2);
					triangle2.add(n3);
					triangle2.add(n1);

					DirectPositionList triangle3 = new DirectPositionList();
					triangle3.add(n2);
					triangle3.add(n3);
					triangle3.add(n0);
					triangle3.add(n2);

					DirectPositionList triangle4 = new DirectPositionList();
					triangle4.add(n3);
					triangle4.add(n0);
					triangle4.add(n1);
					triangle4.add(n3);

					GM_LineString ls1 = new GM_LineString(triangle1);
					GM_LineString ls2 = new GM_LineString(triangle2);
					GM_LineString ls3 = new GM_LineString(triangle3);
					GM_LineString ls4 = new GM_LineString(triangle4);

					GM_OrientableSurface tri1 = new GM_Polygon(ls1);
					GM_OrientableSurface tri2 = new GM_Polygon(ls2);
					GM_OrientableSurface tri3 = new GM_Polygon(ls3);
					GM_OrientableSurface tri4 = new GM_Polygon(ls4);

					ArrayList<IOrientableSurface> lTriangles = new ArrayList<IOrientableSurface>(4);
					lTriangles.add(tri1);
					lTriangles.add(tri2);
					lTriangles.add(tri3);
					lTriangles.add(tri4);

					this.tetraedres.add(new GM_Solid(lTriangles));

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
