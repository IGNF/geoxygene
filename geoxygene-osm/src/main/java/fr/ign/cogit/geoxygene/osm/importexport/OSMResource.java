package fr.ign.cogit.geoxygene.osm.importexport;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

import java.util.Comparator;

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

    public HashMap<String, String> getTags() {
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

    public OSMResource(String contributeur, PrimitiveGeomOSM geom, long id,
            int changeSet, int version, int uid, Date date) {
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
}
