package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.equation.Grid3D;
import fr.ign.cogit.geoxygene.sig3d.equation.LineEquation;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.index.Tiling;

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
			ILineString ls, IFeatureCollection<IFeature> collBati, IDirectPosition dpActu,
			IFeatureCollection<IFeature> toits) {

		double distMin = Double.POSITIVE_INFINITY;
		IFeature featparce = null;

		if (!collParcelle.hasSpatialIndex()) {
			collParcelle.initSpatialIndex(Tiling.class, false);
		}

		if (!toits.hasSpatialIndex()) {
			toits.initSpatialIndex(Tiling.class, false);
		}

		Collection<IFeature> feats = collParcelle.select(ls);

		for (IFeature parce : feats) {

			List<IOrientableSurface> lSurf = FromGeomToSurface.convertGeom(parce.getGeom());

			for (IOrientableSurface parceSurface : lSurf) {
				if (parceSurface.intersects(ls)) {
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

		IFeatureCollection<IFeature> roofOut = new FT_FeatureCollection<>();

		for (int j = 0; j < toits.size(); j++) {
			IFeature toit = toits.get(j);
			boucleToit: for (IOrientableSurface surf : FromGeomToSurface.convertGeom(toit.getGeom())) {

				List<IOrientableSurface> lSurf = FromGeomToSurface.convertGeom(featparce.getGeom());

				for (IOrientableSurface surfParcelle : lSurf) {

					if (surfParcelle.buffer(0.1).intersects(surf.buffer(0.1))) {
						// On sait que le toit est dans la parcelle
						// featparce = collBati.get(j);
						// on ajoute dans la liste
						roofOut.add(collBati.get(j));
						// on passe à l'entité suivante
						break boucleToit;
					}
				}
			}
		}
		return roofOut;
	}

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
	public static IFeatureCollection<IFeature> batimentPProcheNoParcel(ILineString ls,
			IFeatureCollection<IFeature> collBati, IDirectPosition dpActu, IFeatureCollection<IFeature> toits) {

		if (!toits.hasSpatialIndex()) {
			toits.initSpatialIndex(Tiling.class, false);
		}

		Collection<IFeature> feats = toits.select(ls);

		IFeatureCollection<IFeature> resultsOut = new FT_FeatureCollection<>();

		for (IFeature featTemp : feats) {
			int index = toits.getElements().indexOf(featTemp);

			resultsOut.add(collBati.get(index));

		}

		return resultsOut;

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
			for (IOrientableSurface surf : lOS) {
				// On traite une surface surf

				// On caste la surface en polygone
				IPolygon p = (IPolygon) surf;
				IDirectPosition pointP = Grid3D.intersectionLP(ligneequa, p);
				// On calcule la distance par rapport au sommet initial
				double dist2 = 0.0;
				if (pointP != null) {
					dist2 = pointP.distance(dpActu);
				} else {
					continue;
				}

				if (dist2 < distMin2) {
					distMin2 = dist2;
					ipp = pointP;

					ftpp = (new DefaultFeature(new GM_Point(ipp)));
					Object V = distMin2;
					AttributeManager.addAttribute(ftpp, "Distance", V, "Double");
					AttributeManager.addAttribute(ftpp, BuildingProfileParameters.ID,
							b1.getAttribute(BuildingProfileParameters.ID), "Double");
				}

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
