package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.zone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopoFactory;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.geometrie.IndicesForme;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

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
 *            A class to delineate urban areas using a road network
 * @author JFGirres
 * 
 */
public class UrbanAreaZone {

    private double tailleBlocks;

    public void setTailleBlocks(double tailleBlocks) {
        this.tailleBlocks = tailleBlocks;
    }

    private double seuilCompacite;

    public void setSeuilCompacite(double seuilCompacite) {
        this.seuilCompacite = seuilCompacite;
    }

    private double seuilBuffer;

    public void setSeuilBuffer(double seuilBuffer) {
        this.seuilBuffer = seuilBuffer;
    }

    private double tailleZone;

    public void setTailleZone(double tailleZone) {
        this.tailleZone = tailleZone;
    }

    private IFeatureCollection<IFeature> jddUrbanArea;

    public void setJddUrbanArea(IFeatureCollection<IFeature> jddUrbanArea) {
        this.jddUrbanArea = jddUrbanArea;
    }

    public IFeatureCollection<IFeature> getJddUrbanArea() {
        return jddUrbanArea;
    }

    public UrbanAreaZone() {
    }

    /**
     * Create urban areas from a road network
     * @param jddRoads
     */
    public void createAreasFromRoads(IFeatureCollection<IFeature> jddRoads) {

        IFeatureCollection<IFeature> jddLsRoads = new FT_FeatureCollection<IFeature>();
        // On vire les GM_MultiCurve
        for (IFeature feature : jddRoads) {
            if (feature.getGeom().isLineString()) {
                jddLsRoads.add(feature);
            }
        }

        // on convertit en carte topologique (si c'est pas du MultiCurve)
        CarteTopo carteTopoRoads = CarteTopoFactory.newCarteTopo(jddLsRoads);
        carteTopoRoads.filtreNoeudsSimples();
        IPopulation<Face> popFaces = carteTopoRoads.getPopFaces();
        Population<Face> popFacesSelect = new Population<Face>();

        // Filtre les faces les plus petites et pas trop allongées
        List<IGeometry> list = new ArrayList<IGeometry>();
        for (Face face : popFaces) {
            if (face.getGeom().area() < tailleBlocks
                    && IndicesForme.indiceCompacite(face.getGeometrie()) > seuilCompacite) {
                list.add(face.getGeometrie().buffer(seuilBuffer));
            }
        }
        IGeometry union = JtsAlgorithms.union(list);

        // retirer les trous
        union = union.buffer(-seuilBuffer);
        union = union.buffer(2 * seuilBuffer);
        union = union.buffer(-2 * seuilBuffer);

        if (union.isMultiSurface()) {
            GM_MultiSurface<GM_Polygon> multiUnion = (GM_MultiSurface<GM_Polygon>) union;
            for (GM_Polygon polygon : multiUnion.getList()) {
                Face newFace = new Face();
                // vire les trous
                polygon.getInterior().clear();
                newFace.setGeom(polygon);
                popFacesSelect.add(newFace);
            }
        } else {
            if (union.isPolygon()) {
                Face newFace = new Face();
                // vire les trous
                ((GM_Polygon) union).getInterior().clear();
                newFace.setGeom(union);
                popFacesSelect.add(newFace);
            }
        }

        IFeatureCollection<IFeature> jddUrban = new FT_FeatureCollection<IFeature>();
        for (Iterator<Face> it = popFacesSelect.iterator(); it.hasNext();) {
            Face face = it.next();
            if (face.getGeom().area() > tailleZone) {
                jddUrban.add(new DefaultFeature(face.getGeom()));
            }
        }
        setJddUrbanArea(jddUrban);
    }
}
