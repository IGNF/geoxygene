package fr.ign.cogit.geoxygene.osm.importexport.metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.contrib.geometrie.IndicesForme;
import fr.ign.cogit.geoxygene.osm.NePasCommit.NumberPointsException;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.OsmRelationMember;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;
import fr.ign.cogit.geoxygene.osm.schema.OSMDefaultFeature;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeometryConversion;
import fr.ign.cogit.geoxygene.osm.schema.OsmSource;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class BuildingAssessment {
	LoadFromPostGIS loader;
	Map<Long, OSMResource> buildings;
	Double[] borders;
	String timestamp;

	// Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";

	public BuildingAssessment(String host, String port, String dbName, String dbUser, String dbPwd) {
		this.loader = new LoadFromPostGIS(host, port, dbName, dbUser, dbPwd);
		this.buildings = new HashMap<Long, OSMResource>();
	}

	public void loadBuildings(String city, String timestamp) throws Exception {
		this.borders = loader.getCityBoundary(city, timestamp);
		this.timestamp = timestamp;
		this.buildings = loader.getSnapshotBuilding(borders, timestamp);
	}

	public Map<Long, IFeature> buildGeometry() throws Exception {
		Map<Long, IFeature> buildingGeom = new HashMap<Long, IFeature>();
		for (OSMResource b : buildings.values()) {
			Set<OSMResource> nodes = new HashSet<OSMResource>();
			if (b.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMWay")) {
				// Récupérer la liste des nodes qui composent le way
				if (((OSMWay) b.getGeom()).isPolygon()) {
					List<Long> vertices = ((OSMWay) b.getGeom()).getVertices();

					nodes = loader.getNodes(vertices, timestamp);

					// if
					// (OsmGeometryConversion.convertOSMPolygonToLambert93((OSMWay)
					// b.getGeom(), nodes).dimension() < 2)
					// throw new NumberPointsException(
					// "Way #" + b.getId() + " est composé de " + nodes.size() +
					// "nodes");

					OSMDefaultFeature feature = new OSMDefaultFeature(b.getContributeur(),
							OsmGeometryConversion.convertOSMPolygonToLambert93((OSMWay) b.getGeom(), nodes), b.getId(),
							b.getChangeSet(), b.getVersion(), b.getUid(), b.getDate(), b.getTags());
					feature.setSource(OsmSource.valueOfTag(b.getSource()));
					feature.setCaptureTool(b.getCaptureTool());
					buildingGeom.put(b.getId(), feature);
					nodes.clear();
				}

			}
			if (b.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMRelation")) {
				// Récupérer les membres de la relation
				OSMRelation primitive = (OSMRelation) b.getGeom();
				OsmRelationMember outer = primitive.getOuterMembers().get(0);
				OSMResource outerWay = loader.getWay(outer.getRef(), timestamp);

				nodes = loader.getNodes(((OSMWay) outerWay.getGeom()).getVertices(), timestamp);

				IPolygon polygon = OsmGeometryConversion.convertOSMPolygonToLambert93((OSMWay) outerWay.getGeom(),
						nodes);
				if (polygon.dimension() < 2)
					throw new NumberPointsException("Way #" + b.getId() + " est composé de " + nodes.size() + "nodes");

				List<OsmRelationMember> inners = primitive.getInnerMembers();
				// add inner rings to polygon
				for (OsmRelationMember inner : inners) {
					OSMResource resource = loader.getWay(inner.getRef(), timestamp);
					nodes.clear();
					nodes = loader.getNodes(((OSMWay) resource.getGeom()).getVertices(), timestamp);
					IRing ring = OsmGeometryConversion.convertOSMPolygonToLambert93((OSMWay) resource.getGeom(), nodes)
							.getExterior();
					if (ring.coord().size() < 4)
						continue;
					polygon.addInterior(ring);
				}
				OSMDefaultFeature feature = new OSMDefaultFeature(b.getContributeur(), polygon, b.getId(),
						b.getChangeSet(), b.getVersion(), b.getUid(), b.getDate(), b.getTags());
				feature.setSource(OsmSource.valueOfTag(b.getSource()));
				feature.setCaptureTool(b.getCaptureTool());

				buildingGeom.put(b.getId(), feature);
				nodes.clear();
			}
		}
		return buildingGeom;
	}

	/**
	 * 
	 * @param feat
	 * @return the following metadata : an "anonymised" user ID, number of tags,
	 *         the building-tag value, the source-tag value
	 */
	public Object[] getMetada(IFeature feat) {
		int nbTags = this.buildings.get(Long.valueOf(feat.getId())).getTags().size();
		String buildingValue = this.buildings.get(Long.valueOf(feat.getId())).getTags().get("building");
		String source = this.buildings.get(Long.valueOf(feat.getId())).getSource();
		int uid = this.buildings.get(Long.valueOf(feat.getId())).getUid() + 111; // Anonymisé
		Object[] metadata = { uid, nbTags, buildingValue, source };
		return metadata;

	}

	/**
	 * 
	 * @param feat
	 * @return the following metrics : perimeter, area, shortest edge length,
	 *         median edge length, elongation value, convexity, compacity,
	 *         smallest surrounding rectangle area
	 */
	public Object[] getGeomMetrics(IFeature feat) {
		Double perimeter = feat.getGeom().length();
		Double area = feat.getGeom().area();

		Double shortestEdge = CommonAlgorithmsFromCartAGen.getShortestEdgeLength(feat.getGeom());
		Double medianEdge = CommonAlgorithmsFromCartAGen.getEdgeLengthMedian(feat.getGeom());

		Double elongation = CommonAlgorithms.elongation(feat.getGeom());
		Double convexity = CommonAlgorithms.convexity(feat.getGeom());
		Double compacite = IndicesForme.indiceCompacite(((IPolygon) feat.getGeom()));
		Double areaSSR = SmallestSurroundingRectangleComputation.getSSR(feat.getGeom()).area();
		Object[] metrics = { perimeter, area, shortestEdge, medianEdge, elongation, convexity, compacite, areaSSR };
		return metrics;
	}

	/**
	 * 
	 * @param features
	 * @param f
	 * @return a table of metric containing : the number of intersected objects,
	 *         the ratio intersected area/area, the number of touched objects,
	 *         the ratio length touched/length
	 */
	public Object[] getTopologyMetric(IFeature f, Collection<IFeature> features) {
		int nbIntersects = 0;
		int nbTouches = 0;
		Double ratioIntersect = 0.0;
		Double ratioTouchLength = 0.0;
		System.out.println("Feature ID : " + Long.valueOf(f.getId()));
		for (IFeature bati : features) {
			if (bati.getId() == f.getId())
				continue;
			if (f.getGeom().intersectsStrictement(bati.getGeom())) {
				nbIntersects++;
				ratioIntersect += f.getGeom().difference(bati.getGeom()).area() / f.getGeom().area();
			}
			if (f.getGeom().touches(bati.getGeom())) {
				nbTouches++;
				ratioTouchLength += f.getGeom().intersection(bati.getGeom()).length() / f.getGeom().length();
			}
		}
		Object[] metrics = { nbIntersects, ratioIntersect, nbTouches, ratioTouchLength };
		return metrics;

	}

	public static void toCSV(Map<Long, Set<Object>> indicatorList, File file) throws IOException {
		FileWriter fileWriter = new FileWriter(file);
		String FILE_HEADER = "id,uid,n_tags,building_value,source,"
				+ "perimeter,area,shortest_length,median_length,elongation,convexity,compacity,area_mbr,"
				+ "n_intersects,r_intersects,n_touches,r_touches";
		fileWriter.append(FILE_HEADER.toString());
		fileWriter.append(NEW_LINE_SEPARATOR);
		try {
			for (Long id : indicatorList.keySet()) {
				fileWriter.append(String.valueOf(id));
				for (int i = 0; i < indicatorList.get(id).size(); i++) {
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(indicatorList.get(id).toArray()[i]));
				}
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
		} catch (Exception e) {
			System.out.println("Erreur d'écriture");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
	}

}
