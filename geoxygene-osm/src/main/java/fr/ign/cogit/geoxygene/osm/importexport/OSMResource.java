package fr.ign.cogit.geoxygene.osm.importexport;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.ign.cogit.geoxygene.osm.schema.OsmCaptureTool;

public class OSMResource {

	private String contributeur;
	private String source;
	private OsmCaptureTool captureTool = OsmCaptureTool.UNKNOWN;
	private PrimitiveGeomOSM geom;
	private int changeSet, version;
	private long id;
	private int uid;
	private HashMap<String, String> tags;
	private Date date;
	private int nbTags;
	private boolean visible;

	/**
	 * Attributs qui concernent les autres versions de l'objet dans une fenêtre
	 * spatio-temporelle donnée
	 * 
	 */
	private int vmin, vmax;
	/**
	 * vrai si la version de l'objet est la plus récente par rapport à la
	 * fenêtre temporelle étudiée
	 */
	private boolean upToDate = true; // par défaut
	/**
	 * vrai s'il existe une version plus récente de l'objet dans la fenêtre,
	 * corrigée par un autre contributeur
	 */
	private boolean willbeCorrected = false; // par défaut
	/**
	 * vrai s'il existe une version plus récente de l'objet dans la fenêtre,
	 * corrigée par le même contributeur
	 */
	private boolean willbeAutoCorrected = false; // par défaut
	/***
	 * le type d'édition : creation, modification, delete, revert
	 */
	private String editionType;
	/**
	 * géométrie précédente de l'objet
	 */
	private PrimitiveGeomOSM formerGeom;
	/**
	 * tags précédents de l'objet
	 */
	private HashMap<String, String> formerTags;
	/**
	 * Version précédente
	 */
	private int formerVersion;
	/**
	 * Date d'édition de la version précédente
	 */
	private Date formerEditionDate;
	/**
	 * Durée écoulée avant le revert. S'il n'y a pas eu de revert l'attribut
	 * reste null
	 */
	private Long timeframeBeforeRevert = null;

	public static final String TAG_SOURCE = "source";
	public static final String TAG_OUTIL = "created_by";
	public static final String TAG_NODE = "node";
	public static final String TAG_WAY = "way";
	public static final String TAG_REL = "relation";
	public static final String ATTR_USER = "user";
	public static final String ATTR_DATE = "timestamp";
	public static final String ATTR_SET = "changeset";
	public static final String ATTR_ID = "id";
	public static final String ATTR_UID = "uid";
	public static final String ATTR_VERSION = "version";
	public static final String ATTR_LAT = "lat";
	public static final String ATTR_LON = "lon";

	/***
	 * Met à jour les attributs qui nécessitent la comparaison aux autres
	 * versions de la fenêtre spatio-temporelle
	 * 
	 * @param myJavaObjects
	 *            en supposant que les objets soient rangés dans l'ordre
	 *            chronologiques
	 */
	public void setHistoryRelatedAtrributes(Set<OSMResource> myJavaObjects) {
		OSMResource[] array = (OSMResource[]) myJavaObjects.toArray();
		this.vmin = array[0].getVersion();
		this.vmax = array[myJavaObjects.size() - 1].getVersion();
		// Récupère la position du OSMResource courant
		boolean positionFound = false;
		int pos = 0;
		int positionMinus1 = 0;
		int positionMinus2 = 0;
		this.formerVersion = this.version - 1;
		while (!positionFound) {
			if (array[pos].id == this.id) {
				if (array[pos].version == this.formerVersion - 1)
					positionMinus2 = pos;
				else if (array[pos].version == this.formerVersion)
					positionMinus1 = pos;
				else if (array[pos].version == this.version)
					positionFound = true;
				else
					pos++;
			}
		}
		this.formerEditionDate = array[positionMinus1].date;
		this.formerGeom = array[positionMinus1].geom;
		this.formerTags = array[positionMinus1].tags;
		this.editionType = (this.version == 1 ? "creation" : "modification");
		this.editionType = (this.visible == false ? "delete" : "modification");
		// Comparaison avec la version précédente (n-2)
		if (array[positionMinus2].visible == this.visible)
			if (array[positionMinus2].geom.equals(this.geom))
				if (array[positionMinus2].tags.size() == this.tags.size())
					if (array[positionMinus2].tags.equals(this.tags)) {
						this.editionType = "revert";
						// Calcule la différence de date en millisecondes
						long diff = array[positionMinus2].date.getTime() - this.date.getTime();
						this.setTimeframeBeforeRevert(TimeUnit.MILLISECONDS.toMinutes(diff));

					}
		// Recherche des versions suivantes de la fenêtre
		int nextpos = pos + 1;
		while (nextpos != myJavaObjects.size()) {
			if (array[nextpos].id == this.id) {
				this.upToDate = false;
				if (array[nextpos].uid == this.uid)
					this.willbeAutoCorrected = true;
				else
					this.willbeCorrected = true;
				break;
			} else
				nextpos++;
		}

	}

	public String getContributeur() {
		return contributeur;
	}

	public void setContributeur(String contributeur) {
		this.contributeur = contributeur;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public OsmCaptureTool getCaptureTool() {
		return captureTool;
	}

	public void setCaptureTool(OsmCaptureTool outil) {
		this.captureTool = outil;
	}

	public PrimitiveGeomOSM getGeom() {
		return geom;
	}

	public void setGeom(PrimitiveGeomOSM geom) {
		this.geom = geom;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getChangeSet() {
		return changeSet;
	}

	public void setChangeSet(int changeSet) {
		this.changeSet = changeSet;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(HashMap<String, String> tags) {
		this.tags = tags;
		nbTags = tags.size();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getNbTags() {
		return nbTags;
	}

	public void setNbTags(int nbtags) {
		this.nbTags = nbtags;
	}

	@Override
	public boolean equals(Object obj) {
		OSMResource autre = (OSMResource) obj;
		if (autre.id != this.id)
			return false;
		if (autre.version != this.version)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	@Override
	public String toString() {
		return "id: " + id + " v." + version;
	}

	public OSMResource(String contributeur, PrimitiveGeomOSM geom, long id, int changeSet, int version, int uid,
			Date date) {
		this.contributeur = contributeur;
		this.geom = geom;
		this.id = id;
		this.uid = uid;
		this.changeSet = changeSet;
		this.version = version;
		this.date = date;
		tags = new HashMap<String, String>();
		nbTags = tags.size();
	}

	public void addTag(String cle, String valeur) {
		tags.put(cle, valeur);
		nbTags += 1;
	}

	/**
	 * True if the resource relates to a feature, false if it only relates to a
	 * part of a geometry.
	 * 
	 * @return
	 */
	public boolean isFeature() {
		if (!(this.geom instanceof OSMNode))
			return true;
		if (this.tags.size() == 0)
			return false;
		return true;
	}

	public void writeToPostGIS(Connection connection, String queryOSM) {
		Statement stat;
		try {
			stat = connection.createStatement();
			stat.executeQuery(queryOSM);

		} catch (SQLException e) {
			// do nothing
			// e.printStackTrace();
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getVmin() {
		return vmin;
	}

	public void setVmin(int vmin) {
		this.vmin = vmin;
	}

	public int getVmax() {
		return vmax;
	}

	public void setVmax(int vmax) {
		this.vmax = vmax;
	}

	public boolean isUpToDate() {
		return upToDate;
	}

	public void setUpToDate(boolean upToDate) {
		this.upToDate = upToDate;
	}

	public boolean willbeCorrected() {
		return willbeCorrected;
	}

	public void setWillbeCorrected(boolean willbeCorrected) {
		this.willbeCorrected = willbeCorrected;
	}

	public boolean willbeAutoCorrected() {
		return willbeAutoCorrected;
	}

	public void setWillbeAutocorrected(boolean willbeAutocorrected) {
		this.willbeAutoCorrected = willbeAutocorrected;
	}

	public String getEditionType() {
		return editionType;
	}

	public void setEditionType(String editionType) {
		this.editionType = editionType;
	}

	public PrimitiveGeomOSM getFormerGeom() {
		return formerGeom;
	}

	public void setFormerGeom(PrimitiveGeomOSM formerGeom) {
		this.formerGeom = formerGeom;
	}

	public HashMap<String, String> getFormerTags() {
		return formerTags;
	}

	public void setFormerTags(HashMap<String, String> formerTags) {
		this.formerTags = formerTags;
	}

	public int getFormerVersion() {
		return formerVersion;
	}

	public void setFormerVersion(int formerVersion) {
		this.formerVersion = formerVersion;
	}

	public Date getFormerEditionDate() {
		return formerEditionDate;
	}

	public void setFormerEditionDate(Date formerEditionDate) {
		this.formerEditionDate = formerEditionDate;
	}

	/**
	 * If contribution is made by night
	 * 
	 * @return true if contribution hour is between 6 PM and 6 AM
	 */
	public boolean isNightTimeContribution() {
		Date contributionDate = this.getDate();
		Calendar c = new GregorianCalendar();
		c.setTime(contributionDate);
		Calendar nineOClock = (Calendar) c.clone();
		nineOClock.set(Calendar.HOUR_OF_DAY, 9);
		nineOClock.set(Calendar.MINUTE, 0);
		Calendar sixOClock = (Calendar) c.clone();
		sixOClock.set(Calendar.HOUR_OF_DAY, 18);
		sixOClock.set(Calendar.MINUTE, 0);
		if (c.before(nineOClock) || c.after(sixOClock))
			return true;
		else
			return false;
	}

	/**
	 * If contribution is made during the weekend
	 * 
	 * @return true if contribution day is Saturday or Sunday
	 */
	public boolean isWeekendContribution() {
		Date contributionDate = this.getDate();
		Calendar c = new GregorianCalendar();
		c.setTime(contributionDate);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
			return true;
		else
			return false;
	}

	public Long getTimeframeBeforeRevert() {
		return timeframeBeforeRevert;
	}

	public void setTimeframeBeforeRevert(Long timeframeBeforeRevert) {
		this.timeframeBeforeRevert = timeframeBeforeRevert;
	}

}
