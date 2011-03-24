package fr.ign.cogit.geoxygene.contrib.delaunay;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class TriangulationJTS extends AbstractTriangulation {

    public TriangulationJTS(String nom_logique) {
        super(nom_logique);
    }

    @Override
    public void triangule() throws Exception {
        ConformingDelaunayTriangulationBuilder tb = new ConformingDelaunayTriangulationBuilder();
        GM_MultiPoint sites = new GM_MultiPoint();
        for (Noeud n : this.getPopNoeuds()) {
            sites.add(n.getGeometrie());
        }
        GeometryFactory geomFact = new GeometryFactory();
        Geometry geomSites = AdapterFactory.toGeometry(geomFact, sites);
        tb.setTolerance(1.0);
        tb.setSites(geomSites);
        GeometryCollection triangles = (GeometryCollection) tb.getTriangles(geomFact);
        for (int i = 0; i < triangles.getNumGeometries(); i++) {
            Polygon triangle = (Polygon) triangles.getGeometryN(i);
            this.getPopFaces().nouvelElement(AdapterFactory.toGM_Object(triangle));
        }
        logger.info(this.getPopFaces().size() + " triangles créés");
        /*
        GeometryCollection edges = (GeometryCollection) tb.getEdges(geomFact);
        for (int i = 0; i < edges.getNumGeometries(); i++) {
            LineString edge = (LineString) edges.getGeometryN(i);
            this.getPopArcs().nouvelElement(AdapterFactory.toGM_Object(edge));
        }
        logger.info(this.getPopArcs().size() + " arcs créés");
        this.creeTopologieArcsNoeuds(1.0);
        this.creeTopologieFaces();
        */
        this.ajouteArcsEtNoeudsAuxFaces(true);
        //this.filtreArcsDoublons();
    }
}
