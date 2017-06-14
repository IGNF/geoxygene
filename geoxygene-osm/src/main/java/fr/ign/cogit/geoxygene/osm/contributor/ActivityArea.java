package fr.ign.cogit.geoxygene.osm.contributor;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.delaunay.NoeudDelaunay;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class ActivityArea {
	public String host;
	public String port;
	public String dbName;
	public String dbUser;
	public String dbPwd;
	private Collection<IFeature> polygons;

	/**
	 * @param nodeList
	 *            corresponds to the list of nodes contributed by a contributor
	 * @return hull is a multipolygon of the contributor's activity areas
	 **/
	public IGeometry getActivityAreas(List<OSMResource> nodeList) throws Exception {
		IFeatureCollection<IFeature> ftcolPoints = new FT_FeatureCollection<IFeature>();
		List<NoeudDelaunay> delaunayNodeList = new ArrayList<NoeudDelaunay>();
		// On parcourt la liste des nodes (d'un contributeur donné) et on
		// l'ajoute à la collection de IFeature
		for (OSMResource resource : nodeList) {
			ftcolPoints.add(new DefaultFeature(((OSMNode) resource.getGeom()).getPosition().toGM_Point()));
		}
		// On crée la triangulation
		TriangulationJTS triangule = new TriangulationJTS("TriangulationJTS");
		triangule.importAsNodes(ftcolPoints);
		// On triangule
		triangule.triangule();

		// On récupère les arcs qui ont une longueur < 500 m
		IPopulation<Face> popTriangles = triangule.getPopFaces();
		IGeometry hull = null;
		for (Face face : popTriangles) {
			boolean remove = false;
			for (Arc arc : face.arcs()) {
				if (arc.longueur() > 500) {
					remove = true;
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

		return hull;

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
	public List<OSMResource> selectNodesByUid(Long uid, List<Double> bbox, List<String> timespan) throws Exception {
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
						myOsmResource.setNbTags(obj.names().length());
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
