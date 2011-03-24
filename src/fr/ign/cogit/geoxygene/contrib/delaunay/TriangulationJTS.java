package fr.ign.cogit.geoxygene.contrib.delaunay;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class TriangulationJTS extends AbstractTriangulation {

    public TriangulationJTS(String nom_logique) {
        super(nom_logique);
    }

    @Override
    public void create() throws Exception {
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
        this.ajouteArcsEtNoeudsAuxFaces(true);
        if (this.getOptions().indexOf('v') != -1) {
          //this.filtreArcsDoublons();
          VoronoiDiagramBuilder vb = new VoronoiDiagramBuilder();
          vb.setTolerance(1.0);
          vb.setSites(geomSites);
          GeometryCollection cells = (GeometryCollection) vb.getDiagram(geomFact);;
          for (int i = 0; i < cells.getNumGeometries(); i++) {
            Polygon cell = (Polygon) cells.getGeometryN(i);
            this.getPopVoronoiFaces().nouvelElement(AdapterFactory.toGM_Object(cell));
          }
          logger.info(this.getPopVoronoiFaces().size() + " cellules créés");
          this.voronoiDiagram.ajouteArcsEtNoeudsAuxFaces(true);
        }
    }
}
