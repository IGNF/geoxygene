package fr.ign.cogit.geoxygene.osm.contributor;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.datatools.CRSConversion;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class ActivityArea {
	public static String host = "localhost";
	public static String port = "5432";
	public static String dbName = "paris";
	public static String dbUser = "postgres";
	public static String dbPwd = "postgres";

	public static void main(String[] args) throws Exception {
		List<Double> bbox = new ArrayList<Double>();
		bbox.add(2.3250);
		bbox.add(48.8350);
		bbox.add(2.3700);
		bbox.add(48.8800);

		List<String> timespan = new ArrayList<String>();
		timespan.add("2013-01-01");
		timespan.add("2015-01-01");
		List<OSMResource> osmNodeList1556219 = selectNodesByUid((long) 1556219, bbox, timespan);
		List<OSMResource> osmNodeList17286 = selectNodesByUid((long) 138059, bbox, timespan);
		IGeometry aggregatedAreas1556219 = getActivityAreas(osmNodeList1556219, 1000);
		IGeometry aggregatedAreas17286 = getActivityAreas(osmNodeList17286, 1000);
		IFeatureCollection<DefaultFeature> denseActivityAreas17286 = getDenseActivityAreas(aggregatedAreas17286,
				osmNodeList17286, 4);
		IFeatureCollection<DefaultFeature> denseActivityAreas1556219 = getDenseActivityAreas(aggregatedAreas1556219,
				osmNodeList1556219, 4);
		System.out.println("intersection ? "
				+ denseActivityAreas17286.get(0).getGeom().intersects(denseActivityAreas1556219.get(0).getGeom()));
		System.out.println("aire intersectée ? " + denseActivityAreas17286.get(0).getGeom()
				.intersection(denseActivityAreas1556219.get(0).getGeom()).area());
		double intersection = denseActivityAreas17286.get(0).getGeom()
				.intersection(denseActivityAreas1556219.get(0).getGeom()).area();
		double union = denseActivityAreas17286.get(0).getGeom().union(denseActivityAreas1556219.get(0).getGeom())
				.area();
		System.out.println("aire 1 =" + denseActivityAreas17286.get(0).getGeom().area());
		System.out.println("aire 2 =" + denseActivityAreas1556219.get(0).getGeom().area());
		System.out.println("union =" + union);
		System.out.println("intersection =" + intersection);
		double distSurfacique = 1 - intersection / union;
		System.out.println("distance surfacique ? " + distSurfacique);
	}

	/**
	 * @param nodeList
	 *            corresponds to the list of nodes contributed by a contributor
	 * @return hull is a multipolygon of the contributor's activity areas
	 **/
	public static IGeometry getActivityAreas(List<OSMResource> nodeList, double threshold) throws Exception {
		IFeatureCollection<IFeature> ftcolPoints = new FT_FeatureCollection<IFeature>();
		// On parcourt la liste des nodes (d'un contributeur donné) et on
		// l'ajoute à la collection de IFeature
		int i = 0;
		for (OSMResource resource : nodeList) {
			double latitude = ((OSMNode) resource.getGeom()).getLatitude();
			double longitude = ((OSMNode) resource.getGeom()).getLongitude();

			IPoint ipoint = new GM_Point(CRSConversion.wgs84ToLambert93(latitude, longitude));
			DefaultFeature point = new DefaultFeature(ipoint);

			System.out.println(i + ") " + point.getGeom().coord());
			ftcolPoints.add(point);
			i++;
		}
		System.out.println("ftcolPoints.size() =" + ftcolPoints.size());
		// On crée la triangulation
		TriangulationJTS triangule = new TriangulationJTS("TriangulationJTS");
		triangule.importAsNodes(ftcolPoints);
		// On triangule
		triangule.triangule("v");

		System.out.println("triangule.getListeFaces().size() = " + triangule.getListeFaces().size());

		// On récupère les arcs qui ont une longueur < threshold
		IPopulation<Face> popTriangles = triangule.getPopFaces();
		System.out.println("nb Triangles = " + popTriangles.size());
		IGeometry hull = null;
		for (Face face : popTriangles) {
			boolean remove = false;
			for (Arc arc : face.arcs()) {
				System.out.println("arc.longueur() :" + arc.longueur() + " m");
				if (arc.longueur() > threshold) {
					remove = true;
					System.out.println("arc.longueur() :" + arc.longueur() + " m" + " remove = " + remove);
					break;

				}
			}
			if (!remove) {
				if (hull == null)
					hull = face.getGeom();
				else
					hull = hull.union(face.getGeom());
			}
		}
		// System.out.println(hull.area());
		return hull;
	}

	public static IFeatureCollection<DefaultFeature> getDenseActivityAreas(IGeometry hull, List<OSMResource> nodeList,
			int nbPoints) throws Exception {
		IFeatureCollection<DefaultFeature> denseActivityAreas = new FT_FeatureCollection<DefaultFeature>();
		FeatureType ftArea = new FeatureType();
		ftArea.setGeometryType(IPolygon.class);
		denseActivityAreas.setFeatureType(ftArea);
		// IGeometry nodes = null;
		IMultiPoint nodes = new GM_MultiPoint();

		for (OSMResource resource : nodeList) {
			double latitude = ((OSMNode) resource.getGeom()).getLatitude();
			double longitude = ((OSMNode) resource.getGeom()).getLongitude();

			IPoint ipoint = new GM_Point(CRSConversion.wgs84ToLambert93(latitude, longitude));

			nodes.add(new GM_Point(ipoint.getPosition()));
		}
		System.out.println("(hull instanceof IMultiSurface<?>) " + (hull instanceof IMultiSurface<?>));
		System.out.println("(hull instanceof IPolygon) " + (hull instanceof IPolygon));
		System.out.println("(hull instanceof ISurface) " + (hull instanceof ISurface));

		if (hull instanceof IMultiSurface<?>)
			for (IPolygon simple : ((IMultiSurface<IPolygon>) hull)) {
				System.out.println("Nodes intersects simple " + nodes.intersects(simple));
				System.out.println("Nodes touches simple " + nodes.touches(simple));
				System.out.println("Nodes relates simple " + nodes.relate(simple));
				System.out.println("Nodes intersection simple " + nodes.intersection(simple).numPoints());
				boolean remove = false;
				if (simple == null)
					continue;
				System.out.println("aire de la zone =" + simple.area() + "m carrés");
				if (nodes.intersects(simple))
					if (nodes.intersection(simple).numPoints() < nbPoints)
						remove = true;
				if (!remove)
					denseActivityAreas.add(new DefaultFeature(simple));
			}
		else if (hull instanceof ISurface)
			denseActivityAreas.add(new DefaultFeature(hull));

		return denseActivityAreas;
	}

	/**
	 * Retrieves OSM nodes from a PostGIS database according to spatiotemporal
	 * and uid parameters
	 * 
	 * @param bbox
	 *            contains the bounding box coordinates in the following order :
	 *            [minLon, minLat, maxLon, maxLat]
	 * 
	 * @param timespan
	 *            is composed of the begin date and end date written in
	 *            timestamp format
	 * 
	 * @param uid
	 *            contributor's ID
	 * @throws Exception
	 */
	public static List<OSMResource> selectNodesByUid(Long uid, List<Double> bbox, List<String> timespan)
			throws Exception {
		List<OSMResource> uidNodeList = new ArrayList<OSMResource>();
		String query = "SELECT idnode, id, uid, vnode, changeset, username, datemodif, hstore_to_json(tags), lat, lon FROM node WHERE uid = "
				+ uid + " AND node.geom && ST_MakeEnvelope(" + bbox.get(0).toString() + "," + bbox.get(1).toString()
				+ "," + bbox.get(2).toString() + "," + bbox.get(3).toString() + ", 4326) AND datemodif >= \'"
				+ timespan.get(0).toString() + "\' AND datemodif <= \'" + timespan.get(1).toString() + "\';";

		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
			while (r.next()) {
				System.out.println("Writing node...");
				DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
				Date date = null;
				try {
					date = formatDate.parse(r.getString("datemodif"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				OSMResource myOsmResource = new OSMResource(r.getString("username"),
						new OSMNode(r.getDouble("lat"), r.getDouble("lon")), r.getLong("id"), r.getInt("changeset"),
						r.getInt("vnode"), r.getInt("uid"), date);
				// Add tags if exist
				if (!r.getString("hstore_to_json").toString().equalsIgnoreCase("{}")) {
					try {
						JSONObject obj = new JSONObject(r.getString("hstore_to_json"));
						for (int i = 0; i < obj.names().length(); i++) {
							String key = obj.names().getString(i);
							String value = obj.getString(key);
							System.out.println(" Ajout du tag {" + key + ", " + value + "}");
							myOsmResource.addTag(key, value);
							if (key.equalsIgnoreCase("source")) {
								myOsmResource.setSource(value);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				uidNodeList.add(myOsmResource);
				System.out.println("Java object created ! " + "\nid = " + myOsmResource.getId() + "\nusername = "
						+ myOsmResource.getContributeur() + "     uid = " + myOsmResource.getUid() + "\nvnode = "
						+ myOsmResource.getVersion() + "\ndate = " + myOsmResource.getDate() + "   Changeset = "
						+ myOsmResource.getChangeSet());
				System.out.println("-------------------------------------------");
			}
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}
		return uidNodeList;
	}

}
