package fr.ign.cogit.geoxygene.sig3d.semantic;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleStripArray;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.TextureManager;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

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
 * 
 * @version 0.1
 * 
 *          Classe permettant d'afficher un MNT et de plaquer une photo Cette
 *          classe sert avant tout pour l'affichage, il n'y a donc pas de modèle
 *          de données derrière On peut toujours récupèrer les points des
 *          différents triangles à l'aide du triangle strip et des différents
 *          paramètres On peut cependant récupérer les géométries correspondant
 *          à une zone (zone rectangle entre 2 coordonnées) Class used to render
 *          DTM from a .asc file. The DTM is displayed with an image mapped on
 *          it or with a ColorShade
 * 
 */
public class DTM extends AbstractDTMLayer {

	public int echantillonage = 1;

	public static double CONSTANT_OFFSET = 0;

	protected DTM() {
		super();
	}

	/*
	 * Les paramètres du MNT xIni : il s'agit du X du coin supérieur gauche du MNT
	 * yIni : il s'agit du Y du coin supérieur gauche du MNT pasX : il s'agit du pas
	 * en X du MNT (echantillonnage inclus) pasY : il s'agit du pas en Y du MNT
	 * (echantillonnage inclus) nX : il s'agit du nombre de mailles en X à afficher
	 * nY : il s'agit du nombre de mailles en Y à afficher strip : il s'agit de la
	 * liste de triangles formant le MNT exageration : il s'agi du coefficient en Z
	 * que l'on applique bgeo : il s'agit de la BranchGroup représentant le MNT
	 */
	protected double xIni;
	protected double yIni;
	protected double zMax;
	protected double zMin;
	protected double stepX;
	protected double stepY;
	protected int nX;
	protected int nY;
	protected TriangleStripArray strip;
	protected double noDataValue;

	// Echantillonnage
	protected int sampling;

	protected Color4f[] color4fShade = null;

	/**
	 * Ajoute à la carte un MNT avec orthophoto. L'orthophoto doit être orientée
	 * nord, sud
	 * 
	 * @param file          Nom du fichier du MNT, il doit être au format
	 * @param layerName     Nom de couche du MNT
	 * @param fill          Indique si l'on fait une représentation maille ou une
	 *                      représentation continue
	 * @param exager        L'exaggération du MNT
	 * @param imageFileName Nom du fichier de l'image à plaquer
	 * @param imageEnvelope coordonnées min et max de l'image
	 */
	public DTM(String file, String layerName, boolean fill, int exager, String imageFileName, IEnvelope imageEnvelope) {
		super(file, layerName, fill, exager, imageFileName, imageEnvelope);

		this.bgLayer.addChild(this.representationProcess(file, layerName, fill, exager, imageFileName, imageEnvelope));

	}

	/**
	 * Permet de créer un MNT en utilisant un InputStream
	 * 
	 * @param is
	 * @param layerName
	 * @param fill
	 * @param exager
	 * @param imageFileName
	 * @param imageEnvelope
	 */
	public DTM(InputStream is, String layerName, boolean fill, int exager, String imageFileName,
			IEnvelope imageEnvelope) {
		super("", layerName, fill, exager, imageFileName, imageEnvelope);

		this.bgLayer.addChild(this.representationProcess(is, layerName, fill, exager, imageFileName, imageEnvelope));

	}

	/**
	 * Permet de rafraichir un MNT en appliquant les paramètres nécessaires pour
	 * l'utilisation d'un MNT
	 * 
	 * @param file
	 * @param layerName
	 * @param fill
	 * @param exager
	 * @param imageFileName
	 * @param imageEnvelope
	 */
	public void refresh(String file, String layerName, boolean fill, int exager, String imageFileName,
			IEnvelope imageEnvelope) {

		BranchGroup parent = null;

		if (this.isVisible()) {
			parent = (BranchGroup) this.bgLayer.getParent();
			this.bgLayer.detach();
		}

		this.bgLayer.removeAllChildren();
		this.bgLayer.addChild(this.representationProcess(file, layerName, fill, exager, imageFileName, imageEnvelope));

		if (parent != null) {
			parent.addChild(this.bgLayer);
		}

	}

	private Shape3D representationProcess(String file, String layerName, boolean fill, int exager, String imageFilePath,
			IEnvelope imageEnvelope) {
		try {
			return representationProcess(new FileInputStream(file), layerName, fill, exager, imageFilePath,
					imageEnvelope);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Calcul l'objet Java3D associé à un MNT avec une image plaquée
	 * 
	 * @param file
	 * @param layerName
	 * @param fill
	 * @param exager
	 * @param imageFilePath
	 * @param imageEnvelope
	 * @return un objet Java3D représentant la forme du MNT
	 */
	private Shape3D representationProcess(InputStream is, String layerName, boolean fill, int exager,
			String imageFilePath, IEnvelope imageEnvelope) {

		this.imagePath = imageFilePath;
		this.imageEnvelope = imageEnvelope;
		this.colorShade = null;
		this.isFilled = fill;

		// On initialize les paramètres génèraux concernant le MNT
		this.layerName = layerName;
		this.exageration = exager;
		// Les informations concernant le coin supérieur gauche du MNT
		double shiftX = 0;
		double shiftY = 0;

		// Le nombre d'éléments présents dans le MNT
		int nbpoints = 0;
		int numligne = 0;
		int numcol = 0;

		// Il s'agit d'une chaine contenant les informations en train d'être lu
		// dans le fichier
		String ligne;

		double denomX = this.imageEnvelope.getUpperCorner().getX() - this.imageEnvelope.getLowerCorner().getX();
		double denomY = this.imageEnvelope.getUpperCorner().getY() - this.imageEnvelope.getLowerCorner().getY();

		try {

			// Lecture du fichier et récupèration des différentes valeurs citées
			// précédemment

			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			ligne = br.readLine();

			String[] result = ligne.split(" ");

			int ncols = Integer.parseInt(result[result.length - 1]);

			// System.out.println("ncols" + result[result.length - 1]);

			ligne = br.readLine();
			result = ligne.split("\\s");

			// System.out.println("nrows" + result[result.length - 1]);

			int nrows = Integer.parseInt(result[result.length - 1]);

			ligne = br.readLine();
			result = ligne.split("\\s");

			shiftX = Double.parseDouble(result[result.length - 1]);

			// System.out.println("xllcorner" + result[result.length - 1]);

			ligne = br.readLine();
			result = ligne.split("\\s");

			shiftY = Double.parseDouble(result[result.length - 1]);

			// System.out.println("yllcorner" + shiftY);

			ligne = br.readLine();
			result = ligne.split("\\s");

			// System.out.println("cellsize" + result[result.length - 1]);

			double cellsize = Double.parseDouble(result[result.length - 1]);

			ligne = br.readLine();

			this.noDataValue = Double.parseDouble(result[result.length - 1]);

			ligne = br.readLine();

			double maxy = ((nrows - 1) * cellsize + shiftY);

			// On prépare le nombre de lignes
			nrows = nrows / this.echantillonage;
			ncols = ncols / this.echantillonage;

			// Tab contiendra les Z de la ligne précédente
			int[] tab = new int[nrows - 1];

			for (int i = 0; i < nrows - 1; i++) {

				tab[i] = 2 * ncols;
			}

			// Création de la géométrie Java3D qui accueillera le MNT
			TriangleStripArray strp = null;

			// La construction se fait en TriangleStripArray
			// C'est un mode de construction efficace et rapide
			// Toutefois chaque point se retrouve chargé 2 fois en mémoire
			// (un test avec un autre mode de représentation indexé n'a pas été
			// concluant)

			strp = new TriangleStripArray(2 * ncols * (nrows - 1),
					GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2, tab);

			strp.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
			strp.setCapability(IndexedGeometryArray.ALLOW_COORDINATE_INDEX_READ);
			strp.setCapability(Geometry.ALLOW_INTERSECT);

			// On renseigne les différentes valeurs donnant des informations sur
			// le MNT
			this.xIni = shiftX;
			this.yIni = shiftY;
			// on initialize la ligne précédente
			double[] lignePred = new double[ncols];

			result = ligne.split("\\s");

			// On remplit la premiere ligne : initialisation
			for (int i = 0; i < ncols; i++) {

				lignePred[i] = Double.parseDouble(result[i * this.echantillonage]);
				this.zMin = Math.min(lignePred[i], this.zMin);
				this.zMax = Math.max(lignePred[i], this.zMax);
			}

			// On passe des lignes pour sous échantillonner
			for (int i = 0; i < this.echantillonage; i++) {
				ligne = br.readLine();

			}

			numligne = 0;
			// On traite le fichier ligne par ligne
			for (int j = 0; j < nrows; j++) {

				if (ligne == null) {
					break;
				}

				numcol = 0;

				// On charge la ligne en mémoire
				result = ligne.split("\\s");

				double qx = 0;
				double qy = 0;

				// Pour chaque colonne que l'on souhaite récupèrer
				for (int i = 0; i < ncols; i++) {

					// On récupère le Z du point que l'on traite actuellement
					// Le Z du point en dessous
					double nouvZ = Double.parseDouble(result[i * this.echantillonage]);
					double oldZ = lignePred[i];

					this.zMin = Math.min(nouvZ, this.zMin);
					this.zMax = Math.max(nouvZ, this.zMax);

					// On crée le nouveau point

					// On crée le nouveau point
					Point3d pointAncien = null;
					if (oldZ == this.noDataValue) {

						pointAncien = new Point3d((cellsize * numcol + shiftX), (maxy - cellsize * numligne),
								this.noDataValue * exager);
					} else {

						pointAncien = new Point3d((cellsize * numcol + shiftX), (maxy - cellsize * numligne),
								oldZ * exager);
					}

					Point3d nouvePoint = null;

					if (nouvZ == this.noDataValue) {
						nouvePoint = new Point3d((cellsize * numcol + shiftX),
								(maxy - cellsize * (numligne + this.echantillonage)), this.noDataValue * exager);

					} else {
						nouvePoint = new Point3d((cellsize * numcol + shiftX),
								(maxy - cellsize * (numligne + this.echantillonage)), nouvZ * exager);

					}

					TexCoord2f q = new TexCoord2f();

					TexCoord2f q2 = new TexCoord2f();

					qx = pointAncien.x - this.getImageEnvelope().getLowerCorner().getX();
					qy = pointAncien.y - this.getImageEnvelope().getLowerCorner().getY();

					double q2x = nouvePoint.x - this.getImageEnvelope().getLowerCorner().getX();
					double q2y = nouvePoint.y - this.getImageEnvelope().getLowerCorner().getY();

					q.set((float) (qx / denomX), (float) (qy / denomY));
					q2.set((float) (q2x / denomX), (float) (q2y / denomY));

					strp.setCoordinate(nbpoints, pointAncien);

					strp.setTextureCoordinate(0, nbpoints, q);

					nbpoints++;

					strp.setCoordinate(nbpoints, nouvePoint);

					strp.setTextureCoordinate(0, nbpoints, q2);

					nbpoints++;
					lignePred[i] = nouvZ;

					numcol = numcol + this.echantillonage;

				}
				numligne = numligne + this.echantillonage;
				// On passe des lignes pour sous àchantillonner
				for (int l = 0; l < this.echantillonage; l++) {
					ligne = br.readLine();

				}

			}

			br.close();

			this.stepX = this.echantillonage * cellsize;
			this.stepY = this.echantillonage * cellsize;
			this.nX = ncols;
			this.nY = nrows;
			this.sampling = this.echantillonage;
			this.strip = strp;

			Appearance app = new Appearance();
			TransparencyAttributes tra = new TransparencyAttributes();
			tra.setTransparencyMode(TransparencyAttributes.NONE);
			app.setTransparencyAttributes(tra);

			URL url = new URL(this.imagePath);

			Texture t = TextureManager.textureNoReapetLoading(url.getPath());

			app.setTexture(t);

			// Style normal
			// Création des attributs du polygone
			PolygonAttributes pa = new PolygonAttributes();

			// a modifier pour changer de mode
			// Le mode permet de représenter le MNT de différentes manières
			if (fill) {
				pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
			} else {
				pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
			}

			pa.setCullFace(PolygonAttributes.CULL_BACK);
			pa.setBackFaceNormalFlip(true);

			// Association à l'apparence des attributs de géométrie et de
			// material

			app.setPolygonAttributes(pa);

			Shape3D shapepleine = new Shape3D(this.strip, app);

			return shapepleine;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Permet de créer un MNT en appliquant un dégradé de couleur.
	 * 
	 * @param file           Fichier MNT au format .asc
	 * @param layerName      Nom de la couche
	 * @param fill           Si l'on veut une représentation sous forme remplie ou
	 *                       sous forme de maille
	 * @param exager         exaggeration à appliquer
	 * @param colorGradation dégradé appliqué sur le MNT. Il s'agit d'un Color[].
	 *                       Les triangles seront classés en fonction du nombre de
	 *                       couleur dans le tableau de couleur. La classe Degrade
	 *                       propose une série de tableau de couleurs.
	 */
	public DTM(String file, String layerName, boolean fill, int exager, Color[] colorGradation) {

		super(file, layerName, fill, exager, colorGradation);

		this.bgLayer.addChild(this.representationProcess(file, layerName, fill, exager, colorGradation));

	}

	/**
	 * Permet de créer un MNT en utilisant un InputStream
	 * 
	 * @param is
	 * @param layerName
	 * @param fill
	 * @param exager
	 * @param colorGradation
	 */
	public DTM(InputStream is, String layerName, boolean fill, int exager, Color[] colorGradation) {

		super("", layerName, fill, exager, colorGradation);

		this.bgLayer.addChild(this.representationProcess(is, layerName, fill, exager, colorGradation));

	}

	/**
	 * Rafraichit la représentation d'un MNT en appliquer un dégradé
	 * 
	 * @param file
	 * @param layerName
	 * @param fill
	 * @param exager
	 * @param colorGradation
	 */
	public void refresh(String file, String layerName, boolean fill, int exager, Color[] colorGradation) {

		BranchGroup parent = null;

		if (this.isVisible()) {
			parent = (BranchGroup) this.bgLayer.getParent();
			this.bgLayer.detach();
		}

		this.bgLayer.removeAllChildren();
		this.bgLayer.addChild(this.representationProcess(file, layerName, fill, exager, colorGradation));

		if (parent != null) {
			parent.addChild(this.bgLayer);
		}

	}

	private Shape3D representationProcess(String file, String layerName, boolean fill, int exager,
			Color[] colorGradation) {
		try {
			return this.representationProcess(new FileInputStream(file), layerName, fill, exager, colorGradation);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Calcul l'objet 3D permettant de représenter un MNT en appliquant un dégradé.
	 * 
	 * @param file
	 * @param layerName
	 * @param fill
	 * @param exager
	 * @param colorGradation
	 * @return un objet Java3D représentant la forme du MNT
	 */
	private Shape3D representationProcess(InputStream is, String layerName, boolean fill, int exager,
			Color[] colorGradation) {

		int nbElemCouleur = colorGradation.length;
		this.color4fShade = new Color4f[nbElemCouleur];

		for (int i = 0; i < nbElemCouleur; i++) {
			this.color4fShade[i] = new Color4f(colorGradation[i]);
		}

		// Les informations concernant le coin supérieur gauche du MNT
		double shiftX = 0;
		double shiftY = 0;

		// Le nombre d'éléments présents dans le MNT
		int nbpoints = 0;
		int numligne = 0;
		int numcol = 0;

		// Valeur indiquant l'absence de données
		this.noDataValue = Double.NaN;

		this.zMin = Double.POSITIVE_INFINITY;
		this.zMax = Double.NEGATIVE_INFINITY;

		// Il s'agit d'une chaine contenant les informations en train d'être lu
		// dans le fichier
		String ligne;

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			// Lecture du fichier et récupèration des différentes valeurs citées
			// précédemment

			ligne = br.readLine();

			String[] result = ligne.split(" ");

			int ncols = Integer.parseInt(result[result.length - 1]);

			// System.out.println("ncols" + result[result.length - 1]);

			ligne = br.readLine();
			result = ligne.split("\\s");

			// System.out.println("nrows" + result[result.length - 1]);

			int nrows = Integer.parseInt(result[result.length - 1]);

			ligne = br.readLine();
			result = ligne.split("\\s");

			shiftX = Double.parseDouble(result[result.length - 1]);

			// System.out.println("xllcorner" + result[result.length - 1]);

			ligne = br.readLine();
			result = ligne.split("\\s");

			shiftY = Double.parseDouble(result[result.length - 1]);

			// System.out.println("yllcorner" + shiftY);

			ligne = br.readLine();
			result = ligne.split("\\s");

			stepX = Double.parseDouble(result[result.length - 1]);

			if (result[0].equalsIgnoreCase("CELLSIZE")) {
				// There is not CELLSIZEX and CELLSIZEY
				stepY = stepX;
			} else {
				ligne = br.readLine();
				result = ligne.split("\\s");
				stepY = Double.parseDouble(result[result.length - 1]);
			}

			ligne = br.readLine();
			result = ligne.split("\\s");
			this.noDataValue = Double.parseDouble(result[result.length - 1]);

			ligne = br.readLine();

			double maxy = ((nrows - 1) * stepY + shiftY);

			// On prépare le nombre de lignes
			nrows = nrows / this.echantillonage;
			ncols = ncols / this.echantillonage;

			// Tab contiendra les Z de la ligne précédente
			int[] tab = new int[nrows - 1];

			for (int i = 0; i < nrows - 1; i++) {

				tab[i] = 2 * ncols;
			}

			// Création de la géométrie Java3D qui accueillera le MNT
			TriangleStripArray strp = null;

			// La construction se fait en TriangleStripArray
			// C'est un mode de construction efficace et rapide
			// Toutefois chaque point se retrouve chargé 2 fois en mémoire
			// (un test avec un autre mode de représentation indexé n'a pas été
			// concluant)

			strp = new TriangleStripArray(2 * ncols * (nrows - 1),
					GeometryArray.COORDINATES | GeometryArray.COLOR_4 | GeometryArray.NORMALS, tab);

			strp.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
			strp.setCapability(Geometry.ALLOW_INTERSECT);
			// On renseigne les différentes valeurs donnant des informations sur
			// le MNT
			this.xIni = shiftX;
			this.yIni = shiftY;
			// on initialize la ligne précédente
			double[] lignePred = new double[ncols];

			result = ligne.split("\\s");

			String s = result[0];
			int shift = 0;
			if (s.isEmpty()) {
				shift = 1;
			}

			// On remplit la premiere ligne : initialisation
			for (int i = 0 + shift; i < ncols + shift; i++) {

				lignePred[i - shift] = Double.parseDouble(result[i * this.echantillonage]);

				if (lignePred[i - shift] == noDataValue) {
					continue;
				}

				this.zMin = Math.min(lignePred[i - shift], this.zMin);
				this.zMax = Math.max(lignePred[i - shift], this.zMax);
			}

			// On passe des lignes pour sous échantillonner
			for (int i = 0; i < this.echantillonage; i++) {
				ligne = br.readLine();

			}

			numligne = 0;
			// On traite le fichier ligne par ligne
			for (int j = 1; j < nrows; j++) {

				if (ligne == null) {
					break;
				}

				numcol = 0;

				// On charge la ligne en mémoire
				result = ligne.split("\\s");

				// Pour chaque colonne que l'on souhaite récupèrer
				for (int i = 0 + shift; i < ncols + shift; i++) {

					// System.out.println("row : " + j + " col : " + i );
					// On récupère le Z du point que l'on traite actuellement
					// Le Z du point en dessous
					double nouvZ = Double.parseDouble(result[i * this.echantillonage]);
					double oldZ = lignePred[i - shift];

					if (nouvZ != noDataValue) {
						this.zMin = Math.min(nouvZ, this.zMin);
						this.zMax = Math.max(nouvZ, this.zMax);
					}

					// On crée le nouveau point
					Point3d pointAncien = null;
					if (oldZ == this.noDataValue) {

						pointAncien = new Point3d((stepX * numcol + shiftX), (maxy - stepY * numligne),
								this.noDataValue * exager);
					} else {

						pointAncien = new Point3d((stepX * numcol + shiftX), (maxy - stepY * numligne), oldZ * exager);
					}

					Point3d nouvePoint = null;

					if (nouvZ == this.noDataValue) {
						nouvePoint = new Point3d((stepX * numcol + shiftX),
								(maxy - stepY * (numligne + this.echantillonage)), this.noDataValue * exager);

					} else {
						nouvePoint = new Point3d((stepX * numcol + shiftX),
								(maxy - stepY * (numligne + this.echantillonage)), nouvZ * exager);

					}

					strp.setCoordinate(nbpoints, pointAncien);

					nbpoints++;

					strp.setCoordinate(nbpoints, nouvePoint);

					nbpoints++;
					lignePred[i - shift] = nouvZ;

					numcol = numcol + this.echantillonage;

				}
				numligne = numligne + this.echantillonage;
				// On passe des lignes pour sous àchantillonner
				for (int l = 0; l < this.echantillonage; l++) {
					ligne = br.readLine();

				}

			}

			// Assignation des couleurs.
			for (int i = 0; i < nbpoints; i++) {
				Point3d p = new Point3d();
				strp.getCoordinate(i, p);

				strp.setColor(i, this.getColor4f(p.getZ()));
			}

			br.close();

			this.stepX = this.echantillonage * stepX;
			this.stepY = this.echantillonage * stepY;
			this.nX = ncols;
			this.nY = nrows;
			this.sampling = this.echantillonage;
			this.strip = strp;

			Appearance app = new Appearance();
			TransparencyAttributes tra = new TransparencyAttributes();
			tra.setTransparencyMode(TransparencyAttributes.NONE);
			app.setTransparencyAttributes(tra);

			// Style normal
			// Création des attributs du polygone
			PolygonAttributes pa = new PolygonAttributes();

			// a modifier pour changer de mode
			// Le mode permet de représenter le MNT de différentes manières
			// pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
			if (fill) {
				pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
			} else {
				pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
			}
			// pa.setPolygonMode(PolygonAttributes.POLYGON_POINT);

			pa.setCullFace(PolygonAttributes.CULL_BACK);
			pa.setBackFaceNormalFlip(true);

			// Création du material (gestion des couleurs et de l'affichage)
			Material material = new Material();

			material.setSpecularColor(new Color3f(1f, 1f, 1f));
			material.setShininess(128f);
			material.setDiffuseColor(new Color3f(0.8f, 0.8f, 0.8f));

			// Autorisations pour le material
			app.setCapability(Appearance.ALLOW_MATERIAL_READ);
			app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
			/*
			 * ColoringAttributes cA = new ColoringAttributes();
			 * cA.setShadeModel(ColoringAttributes.SHADE_GOURAUD); cA.setColor(new
			 * Color3f(0.0f, 0.8f, 0.0f));
			 */

			// Association à l'apparence des attributs de géométrie et de
			// material
			/*
			 * RenderingAttributes rendering = new RenderingAttributes();
			 * rendering.setAlphaTestFunction(RenderingAttributes.EQUAL);
			 * rendering.setAlphaTestValue(1.0f); app.setRenderingAttributes(rendering);
			 */

			app.setPolygonAttributes(pa);

			app.setMaterial(material);

			// app.setColoringAttributes(cA);

			Shape3D shapepleine;

			GeometryInfo geomInfo = new GeometryInfo(strp);

			NormalGenerator ng = new NormalGenerator();
			ng.generateNormals(geomInfo);

			shapepleine = new Shape3D(geomInfo.getGeometryArray(), app);

			return shapepleine;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Met à jour la représentation de la carte dans la vue
	 */
	@Override
	public void refresh() {
		if (this.colorShade == null) {

			this.refresh(this.path, this.layerName, this.isFilled, this.exageration, this.imagePath,
					this.getImageEnvelope());
		} else {

			this.refresh(this.path, this.layerName, this.isFilled, this.exageration, this.colorShade);
		}

	}

	/**
	 * Permet de faire une petite classification sur les altitudes des points Il
	 * faut modifier le code pour influer sur cette classification, pour l'instant
	 * Cette classification utilise les véritables altitudes et non les altitudes
	 * exaggarees On utilise une classification linéaire en fonction du tableau de
	 * dégradé utilisé
	 * 
	 * @param z l'altitude dont on veut obtenir la couleur
	 * @return la couleur au format Color3f
	 */
	public Color4f getColor4f(double z) {

		// System.out.println("z ="+z+";"+"nodata"+noDataValue);

		if (z == this.noDataValue) {

			return new Color4f(0, 0, 0, 1.0f);
		}
		// Il s'agit du nombre de couleur que l'on va interprêter comme un
		// nombre de classes
		int nbColor = this.color4fShade.length;

		// la largeur d'une classe
		double largeur = (this.zMax - this.zMin) / nbColor;

		int numClass = (int) ((z - this.zMin) / largeur);

		numClass = Math.max(Math.min(nbColor - 1, numClass), 0);

		return this.color4fShade[numClass];

	}

	/**
	 * @return Les coordonnées inférieures gauche de l'image plaquée (renvoie 0,0 si
	 *         il n'y a pas d'image définie
	 */
	public IDirectPosition getPMinImage() {
		if (this.getImageEnvelope() == null) {
			return new DirectPosition(0, 0);
		}

		return this.getImageEnvelope().getLowerCorner();
	}

	/**
	 * @return Les coordonnées supérieures droite de l'image plaquée (renvoie 0,0 si
	 *         il n'y a pas d'image définie
	 */
	public IDirectPosition getPMaxImage() {
		if (this.getImageEnvelope() == null) {
			return new DirectPosition(0, 0);
		}
		return this.getImageEnvelope().getUpperCorner();
	}

	/**
	 * Cette fonction permet d'extraire en géométrie géoxygene les triangles du MNT
	 * compris dans le rectangle formé par dpMin et dpMax en 2D
	 * 
	 * @param dpMin point inférieur gauche du rectangle
	 * @param dpMax point supérieur droit du rectangle
	 * @return une liste de polygones décrivant les géométries du MNT compris dans
	 *         le rectangle formé par les 2 points en 2D
	 */
	@Override
	public MultiPolygon processSurfacicGrid(double xmin, double xmax, double ymin, double ymax) {

		GeometryFactory fac = new GeometryFactory();

		// On récupère dans quels triangles se trouvent dpMin et dpMax
		int posxMin = (int) ((xmin - this.xIni) / (this.stepX * this.sampling));
		int posyMin = (int) ((ymin - this.yIni) / (this.stepY * this.sampling));

		int posxMax = 1 + (int) ((xmax - this.xIni) / (this.stepX * this.sampling));
		int posyMax = 1 + (int) ((ymax - this.yIni) / (this.stepY * this.sampling));

		// On récupère les sommets extérieurs de ces triangles (ceux qui
		// permettent d'englober totalement le rectangle dpMin, dpMax
		Coordinate dpOrigin = new Coordinate(posxMin * this.stepX + this.xIni, posyMin * this.stepY + this.yIni);
		Coordinate dpFin = new Coordinate(posxMax * this.stepX + this.xIni, posyMax * this.stepY + this.yIni);

		// On évalue le nombre de mailles à couvrir
		int nbInterX = Math.max(1, (int) ((dpFin.x - dpOrigin.x) / this.stepX));
		int nbInterY = Math.max(1, (int) ((int) ((dpFin.y - dpOrigin.y) / this.stepY)));

		Polygon[] lPolys = new Polygon[2 * nbInterX * nbInterY];
		int indPoly = 0;
		// On crée une géométrie géoxygne pour chacune de ces mailles
		// (2 triangles par maille)
		for (int i = 0; i < nbInterX; i++) {
			for (int j = 0; j < nbInterY; j++) {

				Coordinate dp1 = new Coordinate(dpOrigin.x + i * this.stepX, dpOrigin.y + j * this.stepY);
				Coordinate dp2 = new Coordinate(dpOrigin.x + (i + 1) * this.stepX, dpOrigin.y + j * this.stepY);
				Coordinate dp3 = new Coordinate(dpOrigin.x + i * this.stepX, dpOrigin.y + (j + 1) * this.stepY);

				Coordinate dp4 = new Coordinate(dpOrigin.x + (i + 1) * this.stepX, dpOrigin.y + (j + 1) * this.stepY);

				Coordinate[] coord = new Coordinate[4];
				coord[0] = dp1;
				coord[1] = dp2;
				coord[2] = dp4;
				coord[3] = dp1;

				LinearRing l1 = fac.createLinearRing(coord);

				Coordinate[] coord2 = new Coordinate[4];
				coord2[0] = dp1;
				coord2[1] = dp4;
				coord2[2] = dp3;
				coord2[3] = dp1;

				LinearRing l2 = fac.createLinearRing(coord2);
				lPolys[indPoly] = fac.createPolygon(l1, null);
				indPoly++;
				lPolys[indPoly] = fac.createPolygon(l2, null);
				indPoly++;

			}

		}
		// On renvoie la liste des triangles
		return fac.createMultiPolygon(lPolys);
	}

	/**
	 * Cette fonction renvoie les lignes comprises dans le rectangles ayant dpMin et
	 * dpMax comme points extrémités
	 * 
	 * @param dpMin point inférieur gauche
	 * @param dpMax point supérieur droit
	 * @return une liste de points correspondant aux lignes des mailles comprises
	 *         entre ces 2 points
	 */
	@Override
	public MultiLineString processLinearGrid(double xmin, double ymin, double xmax, double ymax) {
		GeometryFactory fact = new GeometryFactory();
		// On récupère l'indice des triangles contenant ces points
		int posxMin = (int) ((xmin - this.xIni) / (this.stepX * this.sampling));
		int posyMin = (int) ((ymin - this.yIni) / (this.stepY * this.sampling));

		int posxMax = 1 + (int) ((xmax - this.xIni) / (this.stepX * this.sampling));
		int posyMax = 1 + (int) ((ymax - this.yIni) / (this.stepY * this.sampling));

		// On récupère les points extrêmes appartenant à ces triangles
		Coordinate dpOrigin = new Coordinate(posxMin * this.stepX + this.xIni, posyMin * this.stepY + this.yIni);
		Coordinate dpFin = new Coordinate(posxMax * this.stepX + this.xIni, posyMax * this.stepY + this.yIni);

		// On calcule le nombre de géométries à générer
		int nbInterX = (int) ((dpFin.x - dpOrigin.x) / this.stepX);
		int nbInterY = (int) ((dpFin.y - dpOrigin.y) / this.stepY);

		nbInterX = Math.max(1, nbInterX);
		nbInterY = Math.max(1, nbInterY);

		LineString[] lineStrings = new LineString[(nbInterX - 1) * (nbInterY - 1) * 3 + 2];
		int indL = 0;

		// On crée 3 lignes par maill du MNT
		for (int i = 0; i < nbInterX - 1; i++) {

			for (int j = 0; j < nbInterY - 1; j++) {

				Coordinate dp1 = new Coordinate(dpOrigin.x + i * this.stepX, dpOrigin.y + j * this.stepY);
				Coordinate dp2 = new Coordinate(dpOrigin.x + (i + 1) * this.stepX, dpOrigin.y + j * this.stepY);
				Coordinate dp3 = new Coordinate(dpOrigin.x + i * this.stepX, dpOrigin.y + (j + 1) * this.stepY);
				Coordinate dp4 = new Coordinate(dpOrigin.x + (i + 1) * this.stepX, dpOrigin.y + (j + 1) * this.stepY);

				Coordinate[] c1 = new Coordinate[2];
				Coordinate[] c2 = new Coordinate[2];
				Coordinate[] c3 = new Coordinate[2];

				c1[0] = dp1;
				c1[1] = dp2;

				LineString s1 = fact.createLineString(c1);

				c2[0] = dp1;
				c2[1] = dp3;

				LineString s2 = fact.createLineString(c2);

				c3[0] = dp1;
				c3[1] = dp4;

				LineString s3 = fact.createLineString(c3);

				lineStrings[indL] = s1;
				indL++;
				lineStrings[indL] = s2;
				indL++;
				lineStrings[indL] = s3;
				indL++;

			}

		}
		// On complète en ajoutant les bordures supérieures et inférieures (qui
		// ne sont pas parcourus par le boucle précédente
		Coordinate dpInterX = new Coordinate(dpFin.x, dpOrigin.y);
		Coordinate dpInterY = new Coordinate(dpOrigin.x, dpFin.y);

		Coordinate[] c1 = new Coordinate[2];
		c1[0] = dpInterX;
		c1[1] = dpFin;

		lineStrings[indL] = fact.createLineString(c1);
		indL++;

		Coordinate[] c2 = new Coordinate[2];
		c2[0] = dpInterY;
		c2[1] = dpFin;

		lineStrings[indL] = fact.createLineString(c2);

		// On renvoie le tout
		return fact.createMultiLineString(lineStrings);
	}

	/**
	 * Renvoie le triangle qui se trouve dans la maille indexX, indexY et dans le
	 * bas de la maille si inferior est true et dans le haut de celle-ci sinon
	 * Renvoie null si les indices ne correspondent pas à un triangle existant
	 * 
	 * @param indexX   l'indice de la maille en X
	 * @param indexY   l'indice de la maille en Y
	 * @param inferior la position du triangle par rapport à la maille
	 * @return une liste de points correspondants au sommet du triangle concerné
	 */
	public DirectPositionList getTriangle(int indexX, int indexY, boolean inferior) {

		// Si les coordonnées ne sont pas dans le MNT, on renvoie null
		if (indexX >= (this.nX - 1) || indexX < 0 || indexY >= (this.nY - 1) || indexY < 0) {

			return null;

		}
		// On récupère les 4 coordonnées potentielle
		int lignInit2 = 2 * (indexX) + 2 * (this.nY - 1 - (indexY + 1)) * this.nX;

		Point3d pointemp1 = new Point3d();
		this.strip.getCoordinate(lignInit2, pointemp1);

		Point3d pointemp2 = new Point3d();
		this.strip.getCoordinate(lignInit2 + 1, pointemp2);

		Point3d pointemp3 = new Point3d();
		this.strip.getCoordinate(lignInit2 + 2, pointemp3);

		Point3d pointemp4 = new Point3d();
		this.strip.getCoordinate(lignInit2 + 3, pointemp4);

		DirectPosition pt12D = new DirectPosition(pointemp1.x + this.xIni, pointemp1.y + this.yIni, pointemp1.getZ());
		DirectPosition pt22D = new DirectPosition(pointemp2.x + this.xIni, pointemp2.y + this.yIni, pointemp2.getZ());
		DirectPosition pt32D = new DirectPosition(pointemp3.x + this.xIni, pointemp3.y + this.yIni, pointemp3.getZ());
		DirectPosition pt42D = new DirectPosition(pointemp4.x + this.xIni, pointemp4.y + this.yIni, pointemp4.getZ());

		DirectPositionList dpl = new DirectPositionList();

		if (inferior) {

			dpl.add(pt12D);
			dpl.add(pt22D);
			dpl.add(pt32D);

		} else {

			dpl.add(pt42D);
			dpl.add(pt22D);
			dpl.add(pt32D);
		}

		return dpl;
	}

	/**
	 * Cette fonction permet de projeter un point sur un MNT en lui ajoutant un
	 * altitude offsetting
	 * 
	 * @param x          ,y le point à projeter
	 * @param offsetting l'altitude que l'on rajoute au point final
	 * @return un point 3D ayant comme altitude Z du MNT + offesting
	 */
	@Override
	public Coordinate castCoordinate(double x, double y) {

		// Etant donne que l'on a translaté le MNT
		// On fait de meme avec le vecteur

		// On recupere la maille dans laquelle se trouve le point
		int posx = (int) ((x - this.xIni) / (this.stepX * this.sampling));
		int posy = (int) ((y - this.yIni) / (this.stepY * this.sampling));

		if (posx >= (this.nX) || posx < 0 || posy >= (this.nY) || posy < 0) {

			return new Coordinate(x, y, 0);

		} else if (posx == this.nX - 1) {

			/*
			 * int lignInit2 = 2 * (posx-1) + 2 * (this.nY - 1 - (posy + 1)) this.nX;
			 * Point3d pointemp1 = new Point3d(); this.strip.getCoordinate(lignInit2 + 2,
			 * pointemp1); Point3d pointemp2 = new Point3d();
			 * this.strip.getCoordinate(lignInit2 + 3, pointemp2); DirectPosition ptloc2D =
			 * new DirectPosition(x, y); DirectPosition pt12D = new
			 * DirectPosition(pointemp1.x, pointemp1.y); DirectPosition pt22D = new
			 * DirectPosition(pointemp2.x, pointemp2.y); double d1 =
			 * ptloc2D.distance(pt12D); double d2 = ptloc2D.distance(pt22D); double zMoy; if
			 * (d1 < 0.5) { zMoy = pointemp1.z; return new Coordinate(x, y, zMoy +
			 * offsetting); } else if (d2 < 0.5) { zMoy = pointemp2.z; return new
			 * Coordinate(x, y, zMoy + offsetting); } pt12D.setZ(pointemp1.z);
			 * pt22D.setZ(pointemp2.z); double invDist1 = 1/d1; double invDist2 = 1/d2; //Un
			 * peu bancal, mais on fait une moyenne pondérée par l'inverse des distances ...
			 * zMoy = (invDist1 *pt12D.getZ() + invDist1*pt22D.getZ())/(invDist1+invDist2 );
			 */
			return new Coordinate(x, y, 0);

		} else if (posy == this.nY - 1) {

			/*
			 * int lignInit2 = 2 * (posx) + 2 * (this.nY - 1 - posy ) this.nX; Point3d
			 * pointemp1 = new Point3d(); this.strip.getCoordinate(lignInit2+1, pointemp1);
			 * Point3d pointemp2 = new Point3d(); this.strip.getCoordinate(lignInit2 + 2,
			 * pointemp2); DirectPosition ptloc2D = new DirectPosition(x, y); DirectPosition
			 * pt12D = new DirectPosition(pointemp1.x, pointemp1.y); DirectPosition pt22D =
			 * new DirectPosition(pointemp2.x, pointemp2.y); double d1 =
			 * ptloc2D.distance(pt12D); double d2 = ptloc2D.distance(pt22D); double zMoy; if
			 * (d1 < 0.5) { zMoy = pointemp1.z; return new Coordinate(x, y, zMoy +
			 * offsetting); } else if (d2 < 0.5) { zMoy = pointemp2.z; return new
			 * Coordinate(x, y, zMoy + offsetting); } double invDist1 = 1/d1; double
			 * invDist2 = 1/d2; pt12D.setZ(pointemp1.z); pt22D.setZ(pointemp2.z); //Un peu
			 * bancal, mais on fait une moyenne pondérée par l'inverse des distances ...
			 * zMoy = (invDist1 *pt12D.getZ() + invDist1*pt22D.getZ())/(invDist1+invDist2 );
			 */

			return new Coordinate(x, y, 0);
		} else {
			int lignInit2 = 2 * (posx) + 2 * (this.nY - 1 - (posy + 1)) * this.nX;

			// int lignInit2 = 2 * (posx) + 2 * (this.nY - 1 - (posy + 1)) * this.nX;

			Point3d pointemp1 = new Point3d();
			this.strip.getCoordinate(lignInit2, pointemp1);

			Point3d pointemp2 = new Point3d();
			this.strip.getCoordinate(lignInit2 + 1, pointemp2);

			Point3d pointemp3 = new Point3d();
			this.strip.getCoordinate(lignInit2 + 2, pointemp3);

			Point3d pointemp4 = new Point3d();
			this.strip.getCoordinate(lignInit2 + 3, pointemp4);

			DirectPosition pt12D = new DirectPosition(pointemp1.x, pointemp1.y);
			DirectPosition pt22D = new DirectPosition(pointemp2.x, pointemp2.y);
			DirectPosition pt32D = new DirectPosition(pointemp3.x, pointemp3.y);
			DirectPosition pt42D = new DirectPosition(pointemp4.x, pointemp4.y);

			DirectPosition ptloc2D = new DirectPosition(x, y);

			double d1 = ptloc2D.distance(pt12D);
			double d2 = ptloc2D.distance(pt22D);

			double d3 = ptloc2D.distance(pt32D);

			double d4 = ptloc2D.distance(pt42D);

			double zMoy = 0;
			if (d1 < 5) {

				zMoy = pointemp1.z;
				return new Coordinate(x, y, zMoy + DTM.CONSTANT_OFFSET);
			} else if (d2 < 5) {
				zMoy = pointemp2.z;
				return new Coordinate(x, y, zMoy + DTM.CONSTANT_OFFSET);
			} else if (d3 < 5) {
				zMoy = pointemp3.z;
				return new Coordinate(x, y, zMoy + DTM.CONSTANT_OFFSET);
			} else if (d4 < 5) {
				zMoy = pointemp4.z;
				return new Coordinate(x, y, zMoy + DTM.CONSTANT_OFFSET);
			}

			Vecteur v1 = new Vecteur(pt22D, pt32D);
			Vecteur v2 = new Vecteur(pt22D, ptloc2D);

			double d = v1.prodVectoriel(v2).prodScalaire(new Vecteur(0, 0, 1));

			if (d > 0) {// Triangle 1 2 3

				double xn = (pointemp2.y - pointemp1.y) * (pointemp3.z - pointemp1.z)
						- (pointemp3.y - pointemp1.y) * (pointemp2.z - pointemp1.z);
				double yn = (pointemp3.x - pointemp1.x) * (pointemp2.z - pointemp1.z)
						- (pointemp2.x - pointemp1.x) * (pointemp3.z - pointemp1.z);
				double zn = (pointemp2.x - pointemp1.x) * (pointemp3.y - pointemp1.y)
						- (pointemp3.x - pointemp1.x) * (pointemp2.y - pointemp1.y);

				zMoy = (pointemp1.x * xn + pointemp1.y * yn + pointemp1.z * zn - x * xn - y * yn) / zn;

				// zMoy = ((1 / d1) * pointemp1.z + (1 / d2) * pointemp2.z + (1 / d3)
				// * pointemp3.z)
				// / (1 / d1 + 1 / d2 + 1 / d3);
				// zMoy = (pointemp1.z + pointemp2.z + pointemp3.z)/3;
				// PlanEquation eq = new PlanEquation(pt12D, pt22D, pt32D);

				// zMoy = eq.getZ(ptloc2D);

			} else {// Il se trouve dans le triangle 2 3 4

				double xn = (pointemp2.y - pointemp4.y) * (pointemp3.z - pointemp4.z)
						- (pointemp3.y - pointemp4.y) * (pointemp2.z - pointemp4.z);
				double yn = (pointemp3.x - pointemp4.x) * (pointemp2.z - pointemp4.z)
						- (pointemp2.x - pointemp4.x) * (pointemp3.z - pointemp4.z);
				double zn = (pointemp2.x - pointemp4.x) * (pointemp3.y - pointemp4.y)
						- (pointemp3.x - pointemp4.x) * (pointemp2.y - pointemp4.y);

				zMoy = (pointemp4.x * xn + pointemp4.y * yn + pointemp4.z * zn - x * xn - y * yn) / zn;

				// pt42D.setZ(pointemp4.getZ());
				// pt22D.setZ(pointemp2.getZ());
				// pt32D.setZ(pointemp3.getZ());

				// PlanEquation eq = new PlanEquation(pt42D, pt22D, pt32D);

				// zMoy = eq.getZ(ptloc2D);

				// zMoy = ((1 / d4) * pointemp4.z + (1 / d2) * pointemp2.z + (1 / d3)
				// * pointemp3.z)
				// / (1 / d4 + 1 / d2 + 1 / d3);
				// zMoy = (pointemp4.z + pointemp2.z + pointemp3.z)/3;
			}

			return new Coordinate(x, y, zMoy + DTM.CONSTANT_OFFSET);

		}

	}

	@Override
	public Box3D get3DEnvelope() {
		if (this.emprise == null) {

			return new Box3D(this.xIni, this.yIni, this.zMin, this.xIni + this.nX * this.stepX,
					this.yIni + this.nY * this.stepY, this.zMax);
		}
		return this.emprise;
	}

	/**
	 * @return Objet Java3D servant a representer le MNT
	 */
	public TriangleStripArray getRepresentation() {

		return this.strip;
	}

	/**
	 * @return Nombre de mailes en X
	 */
	public int getNX() {
		return this.nX;
	}

	/**
	 * @return Nombre de mailles en Y
	 */
	public int getNY() {
		return this.nY;
	}

	/**
	 * @return Origine en X du MNT
	 */
	public double getXIni() {

		return this.xIni;
	}

	/**
	 * @return Origine en Y du MNT
	 */
	public double getYIni() {

		return this.yIni;
	}

	/**
	 * @return Pas des mailles en X
	 */
	public double getStepX() {

		return this.stepX;
	}

	/**
	 * @return Pas des mailles en Y
	 */
	public double getStepY() {

		return this.stepY;
	}

	@Override
	public Component getRepresentationComponent() {
		JButton jb = new JButton(Messages.getString("3DGIS.DTM"));
		jb.setHorizontalAlignment(SwingConstants.HORIZONTAL);
		return jb;
	}

	@Override
	public GM_Object getGeometryAt(double x, double y) {
		int posx = (int) ((x - this.xIni) / (this.stepX * this.sampling));
		int posy = (int) ((y - this.yIni) / (this.stepY * this.sampling));

		DirectPositionList lTri = this.getTriangle(posx, posy, true);
		GM_Triangle tri = new GM_Triangle(new GM_LineString(lTri));

		if (tri.contains(new GM_Point(new DirectPosition(x, y)))) {
			return tri;
		}
		lTri = this.getTriangle(posx, posy, false);
		return new GM_Triangle(new GM_LineString(lTri));

	}

	/**
	 * Method to get lowest position Input : none Output : DirectPosition
	 */

	public DirectPosition getLowestPoint() {

		// Getting resolutions
		double xr = stepX;
		double yr = stepY;

		// Getting minimal coordinates
		double xmin = xIni + xr;
		double ymin = yIni + yr;

		// Getting DTM sizes
		double nx = nX;
		double ny = nY;

		// Getting maximal cooridnates
		double xmax = xIni + xr * (nx - 2);
		double ymax = yIni + yr * (ny - 2);

		// Temporary optimum
		double ztemp;

		// Temporary solution
		double xSitemin = 0;
		double ySitemin = 0;
		double zSitemin = Double.MAX_VALUE;

		// Running through grid
		for (double x = xmin; x <= xmax; x += xr) {

			for (double y = ymin; y <= ymax; y += yr) {

				// Altitude projection
				ztemp = castCoordinate(x, y).getOrdinate(2);

				if ((ztemp < zSitemin) && (ztemp != -9999)) {

					xSitemin = x;
					ySitemin = y;
					zSitemin = ztemp;

				}

			}

		}

		return new DirectPosition(xSitemin, ySitemin, zSitemin);

	}

	/**
	 * Method to get highest position Input : none Output : DirectPosition
	 */

	public DirectPosition getHighestPoint() {

		// Getting resolutions
		double xr = stepX;
		double yr = stepY;

		// Getting minimal coordinates
		double xmin = xIni + xr;
		double ymin = yIni + yr;

		// Getting DTM sizes
		double nx = nX;
		double ny = nY;

		// Getting maximal cooridnates
		double xmax = xIni + xr * (nx - 2);
		double ymax = yIni + yr * (ny - 2);

		// Temporary optimum
		double ztemp;

		// Temporary solution
		double xSitemax = 0;
		double ySitemax = 0;
		double zSitemax = Double.MIN_VALUE;

		// Running through grid
		for (double x = xmin; x <= xmax; x += xr) {

			for (double y = ymin; y <= ymax; y += yr) {

				// Altitude projection
				ztemp = castCoordinate(x, y).getOrdinate(2);

				if (ztemp > zSitemax) {

					xSitemax = x;
					ySitemax = y;
					zSitemax = ztemp;

				}

			}

		}

		return new DirectPosition(xSitemax, ySitemax, zSitemax);

	}

	/**
	 * Method to get average value Input : none Output : double
	 */

	public double getAverageAltitude() {

		// Getting resolutions
		double xr = stepX;
		double yr = stepY;

		// Getting minimal coordinates
		double xmin = xIni + xr;
		double ymin = yIni + yr;

		// Getting DTM sizes
		double nx = nX;
		double ny = nY;

		// Getting maximal cooridnates
		double xmax = xIni + xr * (nx - 2);
		double ymax = yIni + yr * (ny - 2);

		// Temporary optimum
		double ztemp;

		// Temporary solution
		double mean = 0;
		double count = 0;

		// Running through grid
		for (double x = xmin; x <= xmax; x += xr) {

			for (double y = ymin; y <= ymax; y += yr) {

				ztemp = castCoordinate(x, y).getOrdinate(2);

				if (ztemp != -9999) {

					mean += ztemp;
					count++;

				}

			}

		}

		return mean / (count);

	}

	/**
	 * Method to get standard deviation value Input : none Output : double
	 */

	public double getStdAltitude() {

		// Getting resolutions
		double xr = stepX;
		double yr = stepY;

		// Getting minimal coordinates
		double xmin = xIni + xr;
		double ymin = yIni + yr;

		// Getting DTM sizes
		double nx = nX;
		double ny = nY;

		// Getting maximal cooridnates
		double xmax = xIni + xr * (nx - 2);
		double ymax = yIni + yr * (ny - 2);

		// Temporary optimum
		double ztemp;

		// Temporary solution
		double mean = 0;
		double meanSquare = 0;
		double count = 0;

		// Running through grid
		for (double x = xmin; x <= xmax; x += xr) {

			for (double y = ymin; y <= ymax; y += yr) {

				ztemp = castCoordinate(x, y).getOrdinate(2);

				if (ztemp != -9999) {

					mean += ztemp;
					meanSquare += ztemp * ztemp;
					count++;

				}

			}

		}

		return Math.sqrt((meanSquare / count - (mean / (count)) * (mean / (count))));

	}

	/**
	 * Method to get surface ratio under threshold altitude Input : none Output :
	 * double
	 */

	public double getRatioUnder(double threshold) {

		// Getting resolutions
		double xr = stepX;
		double yr = stepY;

		// Getting minimal coordinates
		double xmin = xIni + xr;
		double ymin = yIni + yr;

		// Getting DTM sizes
		double nx = nX;
		double ny = nY;

		// Getting maximal cooridnates
		double xmax = xIni + xr * (nx - 2);
		double ymax = yIni + yr * (ny - 2);

		// Temporary optimum
		double ratio = 0;
		double counter = 0;
		double z;

		// Running through grid
		for (double x = xmin; x <= xmax; x += xr) {

			for (double y = ymin; y <= ymax; y += yr) {

				// Altitude projection
				z = castCoordinate(x, y).getOrdinate(2);

				if (z < threshold) {

					ratio++;

				}

				counter++;

			}

		}

		return ratio / counter;

	}

}
