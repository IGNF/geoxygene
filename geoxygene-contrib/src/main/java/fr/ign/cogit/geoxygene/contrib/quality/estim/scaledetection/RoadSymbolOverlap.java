package fr.ign.cogit.geoxygene.contrib.quality.estim.scaledetection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

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
 *            A class to estimate the representation scale of a road network,
 *            based on symbol overlap
 * 
 * @author JFGirres
 * 
 */
public class RoadSymbolOverlap extends AbstractScaleDetection {
    
    static Logger logger = Logger.getLogger(RoadSymbolOverlap.class.getName());

    private double neighbourhoodDistance;

    public void setNeighbourhoodDistance(double neighbourhoodDistance) {
        this.neighbourhoodDistance = neighbourhoodDistance;
    }

    public double getNeighbourhoodDistance() {
        return neighbourhoodDistance;
    }

    private boolean thickened;

    public void setThickened(boolean thickened) {
        this.thickened = thickened;
    }

    public boolean isThickened() {
        return thickened;
    }

    private IFeatureCollection<IFeature> jddThickeningAreas;

    public void setJddThickeningAreas(IFeatureCollection<IFeature> jddThickeningAreas) {
        this.jddThickeningAreas = jddThickeningAreas;
    }

    public IFeatureCollection<IFeature> getJddThickeningAreas() {
        return jddThickeningAreas;
    }

    private IFeatureCollection<IFeature> jddBufferArc;

    public IFeatureCollection<IFeature> getJddBufferArc() {
        return jddBufferArc;
    }

    public void setJddBufferArc(IFeatureCollection<IFeature> jddBufferArc) {
        this.jddBufferArc = jddBufferArc;
    }

    public RoadSymbolOverlap(CarteTopo carteTopoRoads, double neighbourhoodDistance) {
        super(carteTopoRoads);
        this.neighbourhoodDistance = neighbourhoodDistance;
    }

    /**
     * Code permettent de détécter les couples d'intersections les plus proches
     * les unes des autres dans un réseau routier afin d'évaluer l'empatement
     * maximum possible du jeu de données à partir des arcs entrants ou sortants
     * de ces intersections proches.
     */
    @SuppressWarnings("unchecked")
    public void execute() {

        // double tailleBuffer = this.getSymbolSize()*this.getScale()/1000;
        // tailleBuffer = tailleBuffer/2; //car on se base sur la demi-largeur
        // du
        // symbole

        setThickened(false);

        IPopulation<Noeud> popNoeuds = this.getCarteTopoRoads().getPopNoeuds();
        IPopulation<Arc> popArcIntersection12 = new Population<Arc>();
        IPopulation<Arc> popArcSelectionBuffer = new Population<Arc>();
        IPopulation<Noeud> popNoeudIntersection1 = new Population<Noeud>();
        IPopulation<Noeud> popNoeudIntersection2 = new Population<Noeud>();
        List<Double> listeDistanceIntersection = new ArrayList<Double>();

        IFeatureCollection<IFeature> jddBufferArc = new FT_FeatureCollection<IFeature>();
        IFeatureCollection<IFeature> jddBufferNoeud = new FT_FeatureCollection<IFeature>();
        IFeatureCollection<IFeature> jddBufferIntersect = new FT_FeatureCollection<IFeature>();

        // on récupère tous les noeuds de la carte topo
        for (Noeud noeud : popNoeuds) {
            Noeud noeudIntersection1 = new Noeud();

            // Il faut au moins 3 arcs entrants ou sortant du noeud pour être
            // une
            // intersection
            if (!(noeud.arcs().size() > 2))
                continue;
            else {
                noeudIntersection1 = noeud;
                List<Arc> popArc1 = noeudIntersection1.arcs();
                double distanceIntersection = Double.MAX_VALUE;
                Arc ArcDistanceMin = new Arc();
                Noeud noeudMin = new Noeud();

                // on regarde ensuite chacun des arcs connectés aux noeuds
                for (Arc arc : popArc1) {
                    Noeud noeud2 = new Noeud();

                    // si notre intersection est le neoud de départ, on cherche
                    // le noeud
                    // d'arrivée
                    if (noeudIntersection1.equals(arc.getNoeudIni())) {
                        noeud2 = arc.getNoeudFin();
                    }
                    // et le cas inverse
                    if (noeudIntersection1.equals(arc.getNoeudFin())) {
                        noeud2 = arc.getNoeudIni();
                    }
                    // Pour chaque noeud opposé à l'intersection par rapport à
                    // l'arc,
                    // il faut aussi que ce soit une intersection (au moins 3
                    // arcs entrants ou sortants)
                    if (!(noeud2.arcs().size() > 2))
                        continue;

                    else {
                        Noeud noeudIntersection2 = noeud2;

                        // et on calcul la distance euclidienne entre les deux
                        double distanceIntersection12 = Math
                                .sqrt(((noeudIntersection1.getCoord().getX() - noeudIntersection2.getCoord().getX()) * (noeudIntersection1
                                        .getCoord().getX() - noeudIntersection2.getCoord().getX()))
                                        + ((noeudIntersection1.getCoord().getY() - noeudIntersection2.getCoord().getY()) * (noeudIntersection1
                                                .getCoord().getY() - noeudIntersection2.getCoord().getY())));

                        // Puis on sélectionne la distance min
                        if (distanceIntersection12 < distanceIntersection) {
                            ArcDistanceMin = arc;
                            noeudMin = noeudIntersection2;
                            distanceIntersection = distanceIntersection12;
                        }
                    }
                }

                // distance complétement arbitraire mais suffisante pour l'étude
                if (distanceIntersection < neighbourhoodDistance) {
                    // et on sauve le tout
                    popArcIntersection12.add(ArcDistanceMin);
                    popNoeudIntersection1.add(noeudIntersection1);
                    popNoeudIntersection2.add(noeudMin);
                    listeDistanceIntersection.add(distanceIntersection);
                }
            }
        }

        // Selection de tous les arcs (entrants ou sortant)
        // par les noeuds sélectionnés pour générer un buffer,
        // en éliminant les doublons (un arc est entrant ou sortant de deux
        // noeuds)
        for (Noeud noeudSelect : popNoeudIntersection1) {
            for (Noeud noeudAChoper : popNoeuds) {
                if (noeudSelect.equals(noeudAChoper)) {
                    for (Arc arcAChopper : noeudAChoper.arcs()) {
                        boolean ajoutArc = true;
                        for (Arc arcBuffer : popArcSelectionBuffer) {
                            if (arcAChopper.getGeom().equals(arcBuffer.getGeom())) {
                                ajoutArc = false;
                            }
                        }
                        if (ajoutArc == true) {
                            popArcSelectionBuffer.add(arcAChopper);
                        }
                    }
                }
            }
        }

        // Génaration du buffer autour des arcs (largeur équivalente à la taille
        // supposée du symbole)
        for (Arc arc : popArcSelectionBuffer) {

            double tailleBuffer = RoadTypeBuffer.computeSizeTop100(arc, this.getScale());

            ILineString lineString = (ILineString) arc.getGeom();
            jddBufferArc.add(new DefaultFeature((IPolygon) lineString.buffer(tailleBuffer)));

            Noeud noeudStart = arc.getNoeudIni();
            Noeud noeudEnd = arc.getNoeudFin();
            IPoint pointStart = (IPoint) noeudStart.getGeom();
            IPoint pointEnd = (IPoint) noeudEnd.getGeom();
            jddBufferNoeud.add(new DefaultFeature((IPolygon) pointStart.buffer(tailleBuffer, 20)));
            jddBufferNoeud.add(new DefaultFeature((IPolygon) pointEnd.buffer(tailleBuffer, 20)));

        }

        setJddBufferArc(jddBufferArc);

        // Recupération uniquement de la partie intersectée qui nous intéresse
        for (IFeature ftFeature : jddBufferArc) {
            IGeometry polyBuffer = (IPolygon) ftFeature.getGeom();
            for (IFeature ftFeature2 : jddBufferArc) {
                IPolygon polyBuffer2 = (IPolygon) ftFeature2.getGeom();

                IGeometry polyIntersect = polyBuffer.intersection(polyBuffer2);

                // Dans le cas de polygones
                if (polyIntersect instanceof GM_Polygon) {
                    IPolygon polyObject = (IPolygon) polyIntersect;
                    if (!polyObject.isEmpty()) {
                        Collection<Noeud> intersectionNoeud = this.getCarteTopoRoads().getPopNoeuds()
                                .select(polyObject);
                        if (intersectionNoeud.isEmpty()) {
                            Collection<IFeature> intersectionBufferNoeud = jddBufferNoeud.select(polyObject);
                            if (intersectionBufferNoeud.isEmpty()) {
                                jddBufferIntersect.add(new DefaultFeature(polyObject));
                            }
                        }
                    }
                }

                // Dans le cas de multi-polygone
                if (polyIntersect instanceof GM_MultiSurface) {
                    for (IPolygon polyMulti : ((IMultiSurface<IPolygon>) polyIntersect).getList()) {
                        if (!polyMulti.isEmpty()) {
                            Collection<Noeud> intersectionNoeud = this.getCarteTopoRoads().getPopNoeuds()
                                    .select(polyMulti);
                            if (intersectionNoeud.isEmpty()) {
                                Collection<IFeature> intersectionBufferNoeud = jddBufferNoeud.select(polyMulti);
                                if (intersectionBufferNoeud.isEmpty()) {
                                    jddBufferIntersect.add(new DefaultFeature(polyMulti));
                                }
                            }
                        }
                    }
                }
            }
        }

        logger.info("Nombre = " + jddBufferIntersect.size());

        if (jddBufferIntersect.size() > 0) {
            setThickened(true);
            setJddThickeningAreas(jddBufferIntersect);
        }
    }
}
