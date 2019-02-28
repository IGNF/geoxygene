package fr.ign.cogit.geoxygene.contrib.delaunay;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
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

		IMultiCurve<ICurve> linesConstraints = new GM_MultiCurve<ICurve>();
		for (Arc a : this.getPopArcs()) {
			linesConstraints.add(a.getGeometrie());
		}

		GeometryFactory geomFact = new GeometryFactory();
		Geometry geomSites = AdapterFactory.toGeometry(geomFact, sites);
		Geometry lineConstraints = AdapterFactory.toGeometry(geomFact, linesConstraints);
		tb.setTolerance(0.01);
		tb.setSites(geomSites);
		tb.setConstraints(lineConstraints);

		GeometryCollection triangles = (GeometryCollection) tb.getTriangles(geomFact);
		for (int i = 0; i < triangles.getNumGeometries(); i++) {
			Polygon triangle = (Polygon) triangles.getGeometryN(i);
			this.getPopFaces().nouvelElement(AdapterFactory.toGM_Object(triangle));
		}
		// logger.info(this.getPopFaces().size() + " triangles créés");
		if (this.getOptions().indexOf('e') != -1) {
			// GeometryCollection edges = (GeometryCollection) tb
			// .getEdges(geomFact);
			// for (int i = 0; i < edges.getNumGeometries(); i++) {
			// LineString edge = (LineString) edges.getGeometryN(i);
			// this.getPopArcs().nouvelElement(AdapterFactory.toGM_Object(edge));
			// }
			// this.creeNoeudsManquants(0.1);
			// this.creeTopologieArcsNoeuds(0.1);
			// this.ajouteArcsEtNoeudsAuxFaces(true);
			this.addMissingEdges(0.1);
		}

		if (this.getOptions().indexOf('v') != -1) {
			this.ajouteArcsEtNoeudsAuxFaces(true);
			try {
				GeometryCollection diagram = (GeometryCollection) tb.getSubdivision().getVoronoiDiagram(geomFact);
				for (int i = 0; i < diagram.getNumGeometries(); i++) {
					Polygon cell = (Polygon) diagram.getGeometryN(i);
					this.getPopVoronoiFaces().nouvelElement(AdapterFactory.toGM_Object(cell));
				}
				// logger.info(this.getPopVoronoiFaces().size() + " cellules
				// créés");
				this.voronoiDiagram.ajouteArcsEtNoeudsAuxFaces(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void nettoyer() {
		this.voronoiDiagram.nettoyer();
		super.nettoyer();
	}
}
