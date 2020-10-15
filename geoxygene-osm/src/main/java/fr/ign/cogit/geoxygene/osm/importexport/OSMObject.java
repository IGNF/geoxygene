package fr.ign.cogit.geoxygene.osm.importexport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;

public class OSMObject {
	public static String host = "localhost";
	public static String port = "5432";
	public static String dbName = ""; // A définir avant de faire appel à la BD
	public static String dbUser = "postgres";
	public static String dbPwd = "postgres";

	// private List<OSMFeature> contributions;
	private List<OSMResource> contributions;
	private long osmId;
	public List<List<Long>> wayComposition = new ArrayList<List<Long>>();
	private String primitiveGeomOSM;

	public long getOsmId() {
		return osmId;
	}

	public void addcontribution(OSMResource resource) {
		this.getContributions().add(resource);

	}

	public void setContributions(List<OSMResource> contribution) {
		this.contributions = contribution;
	}

	public List<OSMResource> getContributions() {
		return contributions;
	}

	public OSMObject(long id) {
		this.osmId = id;
		this.contributions = new ArrayList<OSMResource>();
	}

	public String getPrimitiveGeomOSM() {
		return primitiveGeomOSM;
	}

	public void setPrimitiveGeomOSM(String primitiveGeomOSM) {
		this.primitiveGeomOSM = primitiveGeomOSM;
	}

	public int getNbGeomEdit() {
		int nbGeomEdit = 0;
		if (this.wayComposition.size() > 1)
			for (int i = wayComposition.size() - 1; i > 1; i--) {
				Collection listOne = this.wayComposition.get(i);
				Collection listTwo = this.wayComposition.get(i - 1);
				Collection<String> similar = new HashSet<String>(listOne);
				Collection<String> different = new HashSet<String>();
				different.addAll(listOne);
				different.addAll(listTwo);

				similar.retainAll(listTwo);
				different.removeAll(similar);
				nbGeomEdit = different.size();
			}
		return nbGeomEdit;
	}

	/**
	 * Produit un OSMObject à partir de toutes les versions précédentes d'une
	 * contribution, y compris la version donnée en entrée
	 * 
	 * @param id
	 *            : contribution's OSM id
	 * @param version
	 *            : contribution's version of the object
	 * @return
	 * @throws Exception
	 */
	public static OSMObject makeFromHistory(Long id, Integer version, String osmDataType) throws Exception {
		OSMObject history = new OSMObject(id);

		LoadFromPostGIS ld = new LoadFromPostGIS(host, port, dbName, dbUser, dbPwd);

		String query = "SELECT *, hstore_to_json(tags) FROM " + osmDataType + " WHERE id = " + id;
		if (osmDataType.equals("node"))
			query += " AND vnode <= " + version + " ORDER BY vnode;";
		else if (osmDataType.equals("way"))
			query += " AND vway <= " + version + " ORDER BY vway;";
		else if (osmDataType.equals("relation"))
			query += " AND vrel <= " + version + " ORDER BY vrel;";
		System.out.println(query);
		ld.selectFromDB(query, osmDataType);
		try {
			for (OSMResource r : ld.myJavaObjects)
				history.addcontribution(r);
		} catch (NullPointerException e) {
			System.out.println("Size of myJavaObjects : " + ld.myJavaObjects.size());
			System.out.println(e);
		}
		return history;
	}

}