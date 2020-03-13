package fr.ign.cogit.geoxygene.osm.importexport.metrics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMObject;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.OsmRelationMember;
import fr.ign.cogit.geoxygene.osm.schema.OSMDefaultFeature;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeometryConversion;

public class OSMResourceQualityAssessment {
    /**
     * Contributions de la fenêtre chargées depuis PostGIS
     */
    Set<OSMResource> myJavaObjects;
    /**
     * Contributions rangées par OSMObject : map référencée par le type de
     * primitive OSM.
     * 
     */
    // HashMap<String, HashMap<Long, OSMObject>> myOSMObjects;
    private HashMap<Long, OSMObject> myOSMObjects;
    private HashMap<Long, OSMObject> myNodeOSMObjects;
    private HashMap<Long, OSMObject> myWayOSMObjects;
    private HashMap<Long, OSMObject> myRelOSMObjects;

    public OSMResourceQualityAssessment(Set<OSMResource> myJavaObjects) {
        this.myJavaObjects = myJavaObjects;
        this.myOSMObjects = groupByOSMObject(myJavaObjects);
        this.myNodeOSMObjects = groupObjectByType(myJavaObjects, "OSMNode");
        System.out.println("myNodeObjects : " + this.myNodeOSMObjects.size());
        this.myWayOSMObjects = groupObjectByType(myJavaObjects, "OSMWay");
        System.out.println("myWayOSMObjects : " + this.myWayOSMObjects.size());

    }

    public static int countWeeks(Set<OSMResource> oneYearContributions) {
        Set<Integer> weekNumbers = new HashSet<Integer>();
        for (OSMResource r : oneYearContributions) {
            Date contributionDate = r.getDate();
            Calendar c = new GregorianCalendar();
            c.setTime(contributionDate);
            int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);
            weekNumbers.add(weekOfYear);
        }
        return weekNumbers.size();
    }

    /**
     * Group OSM contributions by object
     * 
     * @param myJavaObjects
     * @param nameGeomOSM
     *            equals "OSMNode", "OSMWay" or "OSMRelation"
     * @return a map of OSMobjects identified by their OSM ID
     */
    public static HashMap<Long, OSMObject> groupObjectByType(
            Set<OSMResource> myJavaObjects, String nameGeomOSM) {
        HashMap<Long, OSMObject> myOSMObjects = new HashMap<Long, OSMObject>();
        Iterator<OSMResource> it = myJavaObjects.iterator();
        while (it.hasNext()) {
            OSMResource contribution = it.next();
            if (!contribution.getGeom().getClass().getSimpleName()
                    .equalsIgnoreCase(nameGeomOSM))
                continue;
            OSMObject objet = new OSMObject(contribution.getId());
            myOSMObjects.put(contribution.getId(), objet);
            objet.addcontribution(contribution);
            // if (!myOSMObjects.containsKey(contribution.getId())) {
            // // If OSMObject doesn't exist yet, create a new object
            // OSMObject objet = new OSMObject(contribution.getId());
            //
            // // objet.nbVersions = 1;
            // // objet.addContributor((long) contribution.getUid());
            // myOSMObjects.put(contribution.getId(), objet);
            // objet.addcontribution(contribution);
            //
            // // Indicate if OSM primitive is an OSMNode, OSMWay or
            // // OSMRelation
            // objet.setPrimitiveGeomOSM(nameGeomOSM);
            //
            // System.out.println("Ajout objet");
            // } else {
            // // If OSMObject already exists : increments the number of
            // // version and stores the contribution
            // /*
            // * myOSMObjects.get(contribution.getId()).nbVersions += 1;
            // * myOSMObjects.get(contribution.getId()).addcontribution(
            // * contribution);
            // */
            // // Refresh the list of unique contributors of the OSMobject
            // /*
            // * if
            // * (!myOSMObjects.get(contribution.getId()).getContributorList()
            // * .contains(contribution.getUid()))
            // * myOSMObjects.get(contribution.getId()).addContributor((long)
            // * contribution.getUid());
            // */
            // }
        }
        System.out.println("size of the list " + myOSMObjects.size());
        // Chronologically order every list of contributions in each OSMObject
        for (OSMObject o : myOSMObjects.values()) {
            Collections.sort(o.getContributions(), new OSMResourceComparator());
        }
        return myOSMObjects;
    }

    public static HashMap<Long, OSMObject> groupByOSMObject(
            Set<OSMResource> myJavaObjects) {
        HashMap<Long, OSMObject> myOSMObjects = new HashMap<Long, OSMObject>();
        Iterator<OSMResource> it = myJavaObjects.iterator();
        while (it.hasNext()) {
            OSMResource contribution = it.next();

            if (!myOSMObjects.containsKey(contribution.getId())) {
                // If OSMObject doesn't exist yet, create a new object
                OSMObject objet = new OSMObject(contribution.getId());
                myOSMObjects.put(contribution.getId(), objet);
                objet.addcontribution(contribution);
                objet.setPrimitiveGeomOSM(
                        contribution.getGeom().getClass().getSimpleName());
            } else {
                myOSMObjects.get(contribution.getId())
                        .addcontribution(contribution);

            }
        }
        // Chronologically order every list of contributions in each OSMObject
        for (OSMObject o : myOSMObjects.values()) {
            Collections.sort(o.getContributions(), new OSMResourceComparator());
        }
        return myOSMObjects;
    }

    /**
     * Count the number of tags (key/value pair) in an OSMResource
     * 
     * @param resource
     * @return number tags
     */
    public static Integer countTags(OSMResource resource) {
        return resource.getTags().size();
    }

    /**
     * Get the previous contribution version of an OSMResource
     * 
     * @param resource
     * @return the former version of the resource if exists
     * @throws Exception
     */
    public OSMResource getFormerVersion(OSMResource resource) throws Exception {
        System.out.println(
                "Get former version of resource id : " + resource.getId()
                        + " current version : " + resource.getVersion());
        if (resource.getVersion() == 1)
            return null;
        else {
            // Get the set of OSMObject which primitive is the same as resource
            // HashMap<Long, OSMObject> osmObj =
            // this.myOSMObjects.get(resource.getGeom().getClass().getSimpleName());
            OSMObject osmObj = this.myOSMObjects.get(resource.getId());
            // List of all the versions of the current contribution
            // List<OSMResource> allVersions =
            // osmObj.get(resource.getId()).getContributions();
            List<OSMResource> allVersions = osmObj.getContributions();
            System.out.println("Nombre de versions " + allVersions.size());
            Iterator<OSMResource> it = allVersions.iterator();
            OSMResource former = null;
            int i = 0;
            while (it.hasNext()) {
                OSMResource r = it.next();
                if (r.getVersion() == resource.getVersion())
                    break;
                i++;
            }
            if (i > 1)
                former = allVersions.get(i - 1);
            if (former == null && i > 1) {
                // Define object primitive type
                System.out.println(osmObj.getPrimitiveGeomOSM());
                String osmDataType = "relation";
                if (osmObj.getPrimitiveGeomOSM().equals("OSMNode"))
                    osmDataType = "node";
                else if (osmObj.getPrimitiveGeomOSM().equals("OSMWay"))
                    osmDataType = "way";

                OSMObject histo = OSMObject.makeFromHistory(osmObj.getOsmId(),
                        i - 1, osmDataType);
                former = histo.getContributions().get(i - 1);
            }

            return former;
        }
    }

    public OSMResource getFormerVersion2(OSMResource resource)
            throws Exception {
        System.out.println(
                "Get former version of resource id : " + resource.getId()
                        + " current version : " + resource.getVersion());
        if (resource.getVersion() == 1)
            return null;
        else {

            OSMResource former = null;

            // Define object primitive type
            System.out.println(resource.getGeom().getClass().getSimpleName());
            String osmDataType = "relation";
            if (resource.getGeom().getClass().getSimpleName().equals("OSMNode"))
                osmDataType = "node";
            else if (resource.getGeom().getClass().getSimpleName()
                    .equals("OSMWay"))
                osmDataType = "way";

            OSMObject histo = OSMObject.makeFromHistory(resource.getId(),
                    resource.getVersion(), osmDataType);
            try {
                former = histo.getContributions()
                        .get(resource.getVersion() - 2);
            } catch (IndexOutOfBoundsException e) {
                System.out.println(
                        "Versions manquantes dans la base de données pour l'objet "
                                + osmDataType + " (ID" + resource.getVersion()
                                + ")");
            }

            return former;
        }
    }

    /**
     * Get the set of tags of a contribution's former version
     * 
     * @param resource
     * @return former set of tags
     * @throws Exception
     */
    public Map<String, String> getFormerTags(OSMResource resource)
            throws Exception {
        OSMResource former = getFormerVersion2(resource);
        if (former != null)
            return former.getTags();
        else {
            System.out.println("Former is null : ");
            return null;
        }

    }

    /**
     * Get the geometry of a contribution's former version
     * 
     * @param resource
     * @return
     * @throws Exception
     */
    public OSMDefaultFeature getFormerGeom(OSMResource resource)
            throws Exception {
        if (resource.getVersion() == 1)
            return null;
        return getOSMFeature(getFormerVersion(resource));
    }

    /**
     * Identify the edition type relatively to previous version
     * 
     * @param resource
     * @return creation, modification, delete or revert
     * @throws Exception
     */
    public EditionType getEditionType(OSMResource resource) throws Exception {
        if (resource.getVersion() == 1)
            return EditionType.creation;

        if (!resource.isVisible())
            return EditionType.delete;
        // Get former version v-1
        OSMResource versionMinus1 = this.getFormerVersion(resource);
        if (versionMinus1 != null) {
            if (!versionMinus1.isVisible()) {
                // Get former version v-2
                OSMResource versionMinus2 = this
                        .getFormerVersion(versionMinus1);
                if (versionMinus2 != null) {
                    if (versionMinus2.equals(resource))
                        return EditionType.revert;
                }
            } else
                return EditionType.modification;
        }
        return null;
    }

    public static EditionType getEditionType(OSMResource resource,
            OSMResource former) {
        if (resource.getVersion() == 1)
            return EditionType.creation;
        if (!resource.isVisible())
            return EditionType.delete;
        else if (former.isVisible())
            return EditionType.revert;
        else
            return EditionType.modification;
    }

    public Date getFormerEditionDate(OSMResource resource) throws Exception {
        return this.getFormerVersion(resource).getDate();
    }

    /**
     * If the contribution in parameter is a revert, this method returns the
     * elapsed time between former version.
     * 
     * @param resource
     * @return elapsed time in seconds or null if resource given in parameter is
     *         not a revert
     * @throws Exception
     */
    public Double getTimeFrameBeforeRevert(OSMResource resource)
            throws Exception {
        if (getEditionType(resource).equals(EditionType.revert)) {
            Double diff = (double) (resource.getDate().getTime()
                    - this.getFormerVersion(resource).getDate().getTime())
                    / 1000;
            return diff;
        }
        return null;
    }

    /**
     * If contribution is made by night
     * 
     * @return true if contribution hour is between 6 PM and 6 AM
     */
    public boolean isNightTimeContribution(OSMResource resource) {
        Date contributionDate = resource.getDate();
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
    public boolean isWeekendContribution(OSMResource resource) {
        Date contributionDate = resource.getDate();
        Calendar c = new GregorianCalendar();
        c.setTime(contributionDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
            return true;
        else
            return false;
    }

    /**
     * Compare with the tags of the previous version and determines whether a
     * new tag is created in the current tag set.
     * 
     * @param resource
     * @return true if there is a new tag key in the new set of tags
     * @throws Exception
     */
    public boolean isTagCreation(OSMResource resource) throws Exception {
        System.out.println("Edition type " + getEditionType(resource));
        if (resource.getVersion() == 1)
            return false;
        if (getEditionType(resource) == null)
            return false;
        // if (getEditionType(resource).equals(EditionType.modification)) {
        System.out.println("Id : " + resource.getId() + " - version : "
                + resource.getVersion() + " - type : "
                + resource.getGeom().getClass().getSimpleName());
        // Compares the two key sets: if the former key set does not contain
        // the key set of the current version then there is some tag
        // creation in the current version
        if (this.getFormerTags(resource).keySet() == null)
            if (resource.getTags().keySet() != null)
                return true;
        if (!this.getFormerTags(resource).keySet()
                .containsAll(resource.getTags().keySet()))
            return true;
        // }
        return false;
    }

    public static boolean isTagCreation(Map<String, String> tags,
            Map<String, String> formerTags) {
        if (formerTags.keySet().containsAll(tags.keySet()))
            return false;
        return true;
    }

    /**
     * Determines whether the is tag modification in the current tag set,
     * relatively to the previous set of tags.
     * 
     * @param resource
     * @return true if the values of the former tags have been changed
     * @throws Exception
     */
    public boolean isTagModification(OSMResource resource) throws Exception {
        if (resource.getVersion() == 1)
            return false;
        // if (getEditionType(resource).equals(EditionType.modification)) {
        Iterator<String> it = this.getFormerTags(resource).keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String formerValue = this.getFormerTags(resource).get(key);
            String currentValue = resource.getTags().get(key);
            if (formerValue == null || currentValue == null)
                return false;
            if (!formerValue.equals(currentValue))
                return true;
            // }
        }
        return false;
    }

    /**
     * 
     * @param resource
     * @param former
     * @return true if the values of the former tags have been changed
     */
    public static boolean isTagModification(OSMResource resource,
            OSMResource former) {
        try {
            List<String> currentTagKeys = new ArrayList<String>(
                    resource.getTags().keySet());
            List<String> previousTagKeys = new ArrayList<String>(
                    former.getTags().keySet());
            currentTagKeys.retainAll(previousTagKeys);
            for (String key : currentTagKeys)
                if (!resource.getTags().get(key)
                        .equals(former.getTags().get(key)))
                    return true;

            // Iterator<String> it = resource.getTags().keySet().iterator();
            // while (it.hasNext()) {
            // String key = it.next();
            // if
            // (!former.getTags().get(key).equals(resource.getTags().get(key)))
            // return true;
            // }

        } catch (NullPointerException e) {
            return false;

        }
        return false;
    }

    /**
     * Compare with the tags of the previous version and determines whether a
     * tag is deleted in the current tag set.
     * 
     * @param resource
     * @return true if a tag key is missing in the new set of tags
     * @throws Exception
     */
    public boolean isTagDelete(OSMResource resource) throws Exception {
        if (resource.getVersion() == 1)
            return false;
        if (getEditionType(resource) == null)
            return false;
        // if (getEditionType(resource).equals(EditionType.modification)) {
        if (!resource.getTags().keySet()
                .containsAll(this.getFormerTags(resource).keySet()))
            return true;
        // }
        return false;
    }

    public static boolean isTagDelete(Map<String, String> tags,
            Map<String, String> formerTags) {
        if (tags.keySet().containsAll(formerTags.keySet()))
            return false;
        return true;
    }

    public OSMDefaultFeature getOSMFeature(OSMResource resource)
            throws Exception {
        OsmGeometryConversion convertor = new OsmGeometryConversion("4326");
        IGeometry igeom = null;
        if (resource.getGeom().getClass().getSimpleName().equals("OSMNode")) {
            igeom = convertor.convertOsmPoint((OSMNode) resource.getGeom(),
                    true);
        }
        if (resource.getGeom().getClass().getSimpleName().equals("OSMWay")) {
            List<OSMResource> myNodes = this.getWayComposition(resource);
            igeom = convertor.convertOSMLine((OSMWay) resource.getGeom(),
                    myNodes, true);
        }
        if (resource.getGeom().getClass().getSimpleName()
                .equals("OSMRelation")) {
            // TODO: voir comment fabriquer des géométries complexes avec
            // Geoxygene
        }
        OSMDefaultFeature feature = new OSMDefaultFeature(
                resource.getContributeur(), igeom, (int) resource.getId(),
                resource.getChangeSet(), resource.getVersion(),
                resource.getUid(), resource.getDate(), resource.getTags());
        return feature;
    }

    public List<OSMResource> getWayComposition(OSMResource way) {
        List<OSMResource> myNodeList = new ArrayList<OSMResource>();
        // A way is composed of a list of nodes
        List<Long> composition = ((OSMWay) way.getGeom()).getVertices();
        for (Long nodeID : composition) {
            myNodeList.add(
                    this.getLatestElement("OSMNode", nodeID, way.getDate()));
        }
        return myNodeList;
    }

    /**
     * 
     * @param way
     * @param myNodeObjects
     * @return most recent nodes that compose the way
     */
    public static List<OSMResource> getWayComposition(OSMResource way,
            HashMap<Long, OSMObject> myNodeObjects) {
        List<OSMResource> myNodeList = new ArrayList<OSMResource>();
        for (Long nodeId : ((OSMWay) way.getGeom()).getVertices()) {
            List<OSMResource> nodeHistory = myNodeObjects.get(nodeId)
                    .getContributions();
            OSMResource r = getLatestElement(nodeHistory, nodeId,
                    way.getDate());
            myNodeList.add(r);
        }
        return myNodeList;
    }

    /**
     * 
     * @param node
     * @param former
     * @return true if the geometries are different
     */
    public static boolean isNodeGeomEdition(OSMResource node,
            OSMResource former) {
        return node.isGeomEquals(former);
    }

    /**
     * Compare la composition de deux ways et s'il y a eu ajout de node retourne
     * les nodes ajoutés
     * 
     * @param composition
     * @param former
     *            composition
     * @return set of added nodes
     */
    public static Set<OSMResource> getAddedNodes(List<OSMResource> composition,
            List<OSMResource> former) {
        Set<OSMResource> addedNodes = new HashSet<OSMResource>();
        for (OSMResource r : composition) {
            Iterator<OSMResource> it = former.iterator();
            boolean isInFormer = false;
            OSMResource f = null;
            while (it.hasNext()) {
                f = it.next();
                if (r.getId() == f.getId()) {
                    isInFormer = true;
                    break;
                }
            }
            if (!isInFormer)
                addedNodes.add(f);
        }
        return addedNodes;
    }

    public static boolean isCompositionsEquals(List<OSMResource> nodes,
            List<OSMResource> formerNodes) {
        if (nodes.size() != formerNodes.size())
            return true;
        for (int i = 0; i < nodes.size(); i++)
            if (!isNodeGeomEdition(nodes.get(i), formerNodes.get(i)))
                return true;
        return false;
    }

    public static boolean isGeomEdition(OSMResource resource,
            OSMResource former) {
        return !resource.isGeomEquals(former);

    }

    public boolean isGeomEdition(OSMResource resource) throws Exception {
        if (resource.getVersion() == 1)
            return false;
        return !resource.isGeomEquals(this.getFormerVersion2(resource));
    }

    public List<OSMResource> getRelationComposition(OSMResource relation) {
        List<OSMResource> myMemberList = new ArrayList<OSMResource>();
        List<OsmRelationMember> members = ((OSMRelation) relation.getGeom())
                .getMembers();
        for (OsmRelationMember m : members) {
            if (m.isNode())
                myMemberList.add(this.getLatestElement("OSMNode", m.getRef(),
                        relation.getDate()));
            else if (m.isWay())
                myMemberList.add(this.getLatestElement("OSMWay", m.getRef(),
                        relation.getDate()));
            else if (m.isRelation())
                myMemberList.add(this.getLatestElement("OSMRelation",
                        m.getRef(), relation.getDate()));
        }
        return myMemberList;
    }

    /**
     * Get the latest OSMResource that composes a complex contribution (Way or
     * Relation)
     * 
     * @param eltID
     *            ID of the OSMResource to fetch
     * @param r
     *            a complex OSMResource (way or relation)
     * @return the latest contribution regarding the date r
     */
    public OSMResource getLatestElement(String primitive, Long eltID, Date d) {
        // Get the set of OSMObject which primitive is the same as resource
        // HashMap<Long, OSMObject> osmObj = this.myOSMObjects.get(primitive);
        OSMObject osmObj = this.myOSMObjects.get(primitive);
        Iterator<OSMResource> it = osmObj.getContributions().iterator();
        OSMResource toDate = null;
        while (it.hasNext()) {
            OSMResource next = it.next();
            // Break the loop as soon as node date is after way date
            if (next.getDate().after(d))
                break;
            toDate = next;
        }
        return toDate;
    }

    public static OSMResource getLatestElement(List<OSMResource> history,
            Long eltID, Date d) {
        Iterator<OSMResource> it = history.iterator();
        OSMResource toDate = null;
        while (it.hasNext()) {
            OSMResource next = it.next();
            // Break the loop as soon as node date is after way date
            if (next.getDate().after(d))
                break;
            toDate = next;
        }
        return toDate;
    }

    /**
     * 
     * @param resource
     * @return the number of key tags which are different from the previous
     *         version
     * @throws Exception
     */
    public Integer getNbChangedTags(OSMResource resource) throws Exception {
        int nbChanges = 0;
        if (isTagModification(resource)) {
            OSMResource former = this.getFormerVersion(resource);
            // Compares the values of the tags
            for (String k : resource.getTags().keySet())
                if (!resource.getTags().get(k).equals(former.getTags().get(k)))
                    nbChanges++;
        }
        return nbChanges;
    }

    /**
     * 
     * @param resource
     * @return the number of key tags that are missing in the current version
     * @throws Exception
     */
    public Integer getNbDeletedTags(OSMResource resource) throws Exception {
        int nbDeletes = 0;
        if (this.isTagDelete(resource)) {
            OSMResource former = this.getFormerVersion(resource);
            for (String k : former.getTags().keySet())
                if (!resource.getTags().containsKey(k))
                    nbDeletes++;
        }
        return nbDeletes;
    }

    /**
     * 
     * @param resource
     * @return The number of new key tags in the current version
     * @throws Exception
     */
    public Integer getNbAddedTags(OSMResource resource) throws Exception {
        int nbAdditions = 0;
        if (this.isTagCreation(resource)) {
            OSMResource former = this.getFormerVersion(resource);
            for (String k : resource.getTags().keySet())
                if (!former.getTags().containsKey(k))
                    nbAdditions++;
        }
        return nbAdditions;
    }

    public HashMap<Long, OSMObject> getMyNodeOSMObjects() {
        return myNodeOSMObjects;
    }

    public void setMyNodeOSMObjects(HashMap<Long, OSMObject> myNodeOSMObjects) {
        this.myNodeOSMObjects = myNodeOSMObjects;
    }

    public HashMap<Long, OSMObject> getMyWayOSMObjects() {
        return myWayOSMObjects;
    }

    public void setMyWayOSMObjects(HashMap<Long, OSMObject> myWayOSMObjects) {
        this.myWayOSMObjects = myWayOSMObjects;
    }

    public HashMap<Long, OSMObject> getMyRelOSMObjects() {
        return myRelOSMObjects;
    }

    public void setMyRelOSMObjects(HashMap<Long, OSMObject> myRelOSMObjects) {
        this.myRelOSMObjects = myRelOSMObjects;
    }

    public void writeOSMObjectCSV(String filename) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(filename), ';');
        // System.out.println("écriture du fichier csv");
        // write header
        String[] line = { "id", "version", "changeset", "uid", "user", "date",
                "visible", "primitive", "source" };
        writer.writeNext(line);

        for (OSMObject obj : this.myOSMObjects.values()) {
            for (OSMResource r : obj.getContributions()) {
                String[] row = { String.valueOf(r.getId()),
                        String.valueOf(r.getVersion()),
                        String.valueOf(r.getChangeSet()),
                        String.valueOf(r.getUid()),
                        String.valueOf(r.getContributeur()),
                        String.valueOf(r.getDate()),
                        String.valueOf(r.isVisible()),
                        String.valueOf(r.getGeom().getClass().getSimpleName()),
                        String.valueOf(r.getSource()) };
                writer.writeNext(row);
            }

        }
        writer.close();
    }

    public void writeOSMObjectsDetails2CSV(String filename) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(filename), ';');
        // System.out.println("écriture du fichier csv");
        // write header
        String[] line = { "id", "version", "changeset", "uid", "user", "date",
                "visible", "primitive", "source", "isTagCreation",
                "isTagDelete", "isTagModification", "isGeomEdition" };
        writer.writeNext(line);

        for (OSMObject obj : this.myOSMObjects.values()) {
            for (OSMResource r : obj.getContributions()) {
                if (r != null) {
                    String[] row = { String.valueOf(r.getId()),
                            String.valueOf(r.getVersion()),
                            String.valueOf(r.getChangeSet()),
                            String.valueOf(r.getUid()),
                            String.valueOf(r.getContributeur()),
                            String.valueOf(r.getDate()),
                            String.valueOf(r.isVisible()),
                            String.valueOf(
                                    r.getGeom().getClass().getSimpleName()),
                            String.valueOf(r.getSource()),
                            String.valueOf(this.isTagCreation(r)),
                            String.valueOf(this.isTagDelete(r)),
                            String.valueOf(this.isTagModification(r)),
                            String.valueOf(this.isGeomEdition(r)) };
                    writer.writeNext(row);
                }
            }

        }
        writer.close();
    }

}
