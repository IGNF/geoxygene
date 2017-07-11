package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile;

import java.awt.Color;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.analysis.roof.RoofDetection;
import fr.ign.cogit.geoxygene.sig3d.equation.Grid3D;
import fr.ign.cogit.geoxygene.sig3d.equation.LineEquation;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;

/**
 * Classe d'utilitaires en lien avec le calcul de profil à partir de bâtiments
 * 
 * @author MFund
 * @author MBrasebin
 * @author JPerret
 * @author YMeneroux
 * 
 */
public class BuildingProfileTools {

	/**
	 * Fonction récupérant le bâtiment le plus proche (de collBati) sur une des
	 * parcelles (de collParcelle) dans sur la ligne définie par ls à partir du
	 * point dpActu
	 * 
	 * @param collParcelle
	 * @param ls
	 * @param collBati
	 * @param dpActu
	 * @return le bâtiment le plus proche
	 */
	@SuppressWarnings("unchecked")
	public static IFeatureCollection<IFeature> batimentPProche(IFeatureCollection<IFeature> collParcelle,
			ILineString ls, IFeatureCollection<IFeature> collBati, IDirectPosition dpActu) {
		IFeatureCollection<IFeature> toits = new FT_FeatureCollection<IFeature>();

		boolean facesOrientation = true;
		double seuil = 0.2;
		IFeatureCollection<IFeature> toits_de_bati = RoofDetection.detectRoof(collBati, seuil, facesOrientation);
		double distMin = Double.POSITIVE_INFINITY;
		IFeature featparce = null;

		for (int i = 0; i < collParcelle.size(); i++) {
			IFeature parce = collParcelle.get(i);

			List<IOrientableSurface> lSurf = FromGeomToSurface.convertGeom(parce.getGeom());

			for (IOrientableSurface line : lSurf) {
				if (line.intersects(ls)) {
					double dist = parce.getGeom().distance(new GM_Point(dpActu));
					if (dist < distMin) {
						distMin = dist;
						featparce = parce;
						// jusque là je garde les parcelles les plus proches
					}
				}
			}

		}
		if (featparce == null) {
			return null;
		}

		for (int j = 0; j < toits_de_bati.size(); j++) {
			IFeature toit = toits_de_bati.get(j);
			boucleToit: for (IOrientableSurface surf : ((GM_MultiSurface<IOrientableSurface>) toit.getGeom())
					.getList()) {

				List<IOrientableSurface> lSurf = FromGeomToSurface.convertGeom(featparce.getGeom());

				for (IOrientableSurface surfParcelle : lSurf) {

					if (surfParcelle.buffer(0.1).intersects(surf.buffer(0.1))) {
						// On sait que le toit est dans la parcelle
						// featparce = collBati.get(j);
						// on ajoute dans la liste
						toits.add(collBati.get(j));
						// on passe à l'entité suivante
						break boucleToit;
					}
				}
			}
		}
		return toits;
	}

	/**
	 * Fonction de récupération du point le plus proche sur un batiment
	 * (batiment) le long d'une ligne définie par l'équation (ligneequa) à
	 * partir du sommet (dpActu)
	 * 
	 * @param ligneequa
	 * @param batiment
	 * @param dpActu
	 * @return
	 */
	public static IFeature intersectionPProche(LineEquation ligneequa, IFeatureCollection<IFeature> batiment,
			IDirectPosition dpActu) {
		IFeature ftpp = null;
		// System.out.println(batiment.size());
		double distMin2 = Double.POSITIVE_INFINITY;
		IDirectPosition ipp = null;

		for (IFeature b1 : batiment) {
			// transformer la géométrie en une liste polygone
			List<IOrientableSurface> lOS = FromGeomToSurface.convertGeom(b1.getGeom());
			// On va parcourir les surfaces
			for (int j = 0; j < lOS.size(); j++) {
				// On traite une surface surf
				ISurface surf = (ISurface) lOS.get(j);
				// On caste la surface en polygone
				IPolygon p = (IPolygon) surf;
				IDirectPosition pointP = Grid3D.intersectionLP(ligneequa, p);
				// On calcule la distance par rapport au sommet initial
				double dist2 = 0.0;
				if (pointP != null) {
					dist2 = pointP.distance(dpActu);
				}
				if (dist2 == 0) {
					continue;
				}
				if (dist2 < distMin2) {
					distMin2 = dist2;
					ipp = pointP;
				}
				ftpp = (new DefaultFeature(new GM_Point(ipp)));
				Object V = distMin2;
				AttributeManager.addAttribute(ftpp, "Distance", V, "Double");
				AttributeManager.addAttribute(ftpp, BuildingProfileParameters.ID,
						b1.getAttribute(BuildingProfileParameters.ID), "Double");
			}
		}
		return ftpp;
	}

	/**
	 * Permet d'obtenir les couleurs d'un dégradé pour la valeurx comprise entre
	 * valeurmin et valeurmax
	 * 
	 * @param valeurmin
	 * @param valeurmax
	 * @param valeurx
	 * @return
	 */
	public static Color degrade(double valeurmin, double valeurmax, double valeurx) {
		int r = 230;
		int g = 255 - ((int) (255 * (valeurx - valeurmin) / (valeurmax - valeurmin)));
		int b = 255;
		return new Color(r, g, b);
	}
}
