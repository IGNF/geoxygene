package fr.ign.cogit.geoxygene.osm.importexport.metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMObject;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;

public class IntrinsicAssessment {
	public static List<OSMResource> myJavaObjects;

	public static void main(String[] args) throws IOException {
		LoadFromPostGIS loaderNepal = new LoadFromPostGIS("localhost", "5432", "nepal", "postgres", "postgres");
		List<Double> bboxNepal = new ArrayList<Double>();
		bboxNepal.add(85.33630);
		bboxNepal.add(27.69640);
		bboxNepal.add(85.34890);
		bboxNepal.add(27.70650);
		List<String> timespanNepal = new ArrayList<String>();
		timespanNepal.add("2013-01-01");
		timespanNepal.add("2014-01-01");
		// loaderNepal.selectWays(bboxNepal, timespanNepal);
		// myJavaObjects = loaderNepal.myJavaObjects;

		List<Double> bbox = new ArrayList<Double>();
		bbox.add(2.3312);
		bbox.add(48.8479);
		bbox.add(2.3644);
		bbox.add(48.8637);
		List<String> timespan = new ArrayList<String>();
		timespan.add("2011-01-01");
		timespan.add("2011-03-01");

		LoadFromPostGIS loader = new LoadFromPostGIS("localhost", "5432", "paris", "postgres", "postgres");
		loader.selectNodes(bbox, timespan);
		loader.selectWays(bbox, timespan);
		myJavaObjects = loader.myJavaObjects;

		// HashMap<Long, OSMObject> myOSMNodeObjects =
		// nodeContributionSummary();
		// HashMap<Long, OSMObject> myOSMWayObjects = wayContributionSummary();
		// writeContributionSummary(myOSMWayObjects, new
		// File("paris_juillet_2014_way.csv"));
		// writeContributionSummary(myOSMNodeObjects, new
		// File("paris_juillet_2014_node.csv"));
		// writeWayContributionDetails(new
		// File("paris_juillet_2014_details_contributions_way.csv"), "OSMWay");
		writeContributionDetails(new File("contributions_paris_janvier-mars_2013.csv"), myJavaObjects);
		OSMResource way = null;
		for (OSMResource resource : myJavaObjects) {
			if (resource.getId() == 55350376)
				if (resource.getVersion() == 2)
					way = resource;
		}
		List<OSMResource> nodeComposingWay = getNodesComposingWay(myJavaObjects, way);
		System.out.println("Nombre de nodes composant way = " + nodeComposingWay.size());
	}

	public static void sortJavaObjects(List<OSMResource> myJavaObjects) {
		Collections.sort(myJavaObjects, new Comparator<OSMResource>() {
			@Override
			public int compare(OSMResource r1, OSMResource r2) {
				return r1.getDate().compareTo(r2.getDate());
			}
		});
	}

	public static List<OSMResource> getNodesComposingWay(List<OSMResource> myJavaObjects, OSMResource myWay) {
		sortJavaObjects(myJavaObjects);
		List<OSMResource> myNodeList = new ArrayList<OSMResource>();
		OSMResource node = null;
		List<Long> vertices = ((OSMWay) myWay.getGeom()).getVertices();
		int wayIndex = myJavaObjects.indexOf(myWay);
		for (Long vertice : vertices) {
			for (int i = wayIndex; i > -1; i--) {
				if (myJavaObjects.get(i).getGeom().getClass().getSimpleName().equals("OSMNode")) {
					if (myJavaObjects.get(i).getId() == vertice) {
						System.out.println("NodeId & vertex match : " + myJavaObjects.get(i).getId());
						node = myJavaObjects.get(i);
						myNodeList.add(node);
						break;
					}
				}
			}
		}
		return myNodeList;
	}

	public static HashMap<Long, OSMObject> wayContributionSummary() {
		HashMap<Long, OSMObject> myOSMWayObjects = osmObjectsInit("OSMWay");
		Iterator<Long> objectIDs = myOSMWayObjects.keySet().iterator();
		while (objectIDs.hasNext()) {
			long currentID = objectIDs.next();
			List<OSMResource> contributionList = fillcontributionList("OSMWay", currentID, myOSMWayObjects);
			intrinsicIndicators(currentID, contributionList, myOSMWayObjects);
			List<String> contributorList = contributorList("OSMWay", currentID);
			myOSMWayObjects.get(currentID).setNbContributors(contributorList.size());
		}
		return myOSMWayObjects;
	}

	public static HashMap<Long, OSMObject> nodeContributionSummary() {
		HashMap<Long, OSMObject> myOSMNodeObjects = osmObjectsInit("OSMNode");
		Iterator<Long> objectIDs = myOSMNodeObjects.keySet().iterator();
		while (objectIDs.hasNext()) {
			long currentID = objectIDs.next();
			List<OSMResource> contributionList = fillcontributionList("OSMNode", currentID, myOSMNodeObjects);
			intrinsicIndicators(currentID, contributionList, myOSMNodeObjects);
			List<String> contributorList = contributorList("OSMNode", currentID);
			myOSMNodeObjects.get(currentID).setNbContributors(contributorList.size());
		}
		return myOSMNodeObjects;
	}

	public static List<String> contributorList(String OSMResourceType, long currentID) {
		List<String> contributorList = new ArrayList<String>();
		Iterator<OSMResource> it = myJavaObjects.iterator();
		while (it.hasNext()) {
			OSMResource contribution = it.next();
			if (currentID == contribution.getId())
				/** Number of unique contributors per OSMObject **/
				if (!contributorList.contains(contribution.getContributeur()))
					contributorList.add(contribution.getContributeur());
		}
		return contributorList;
	}

	public static void intrinsicIndicators(Long currentID, List<OSMResource> contributionList,
			HashMap<Long, OSMObject> myOSMObjects) {
		myOSMObjects.get(currentID).setContributions(contributionList);
		/** dateMin and dateMax **/
		Date datemin = contributionList.get(0).getDate();
		Date datemax = contributionList.get(contributionList.size() - 1).getDate();
		myOSMObjects.get(currentID).setDateMin(datemin);
		myOSMObjects.get(currentID).setDateMax(datemax);
		/** Tag & geom editions **/
		int nbStableTags = 0;
		int nbTagEdition = 0;
		int nbGeomEdition = 0;
		if (contributionList.size() > 1) {
			if (contributionList.get(0).getGeom().getClass().getSimpleName().equals("OSMNode")) {
				int[] indicators = nodeEditionDetails(contributionList, nbStableTags, nbTagEdition, nbGeomEdition);
				nbGeomEdition = indicators[0];
				nbTagEdition = indicators[1];
				nbStableTags = indicators[2];
			}
			if (contributionList.get(0).getGeom().getClass().getSimpleName().equals("OSMWay")) {
				int[] indicators = wayEditionDetails(contributionList, nbStableTags, nbTagEdition, nbGeomEdition);
				nbGeomEdition = indicators[0];
				nbTagEdition = indicators[1];
				nbStableTags = indicators[2];
			}
		} else { // Si l'objet ne compte qu'une contribution, on ne peut rien
					// dire: pas de tag stable, pas d'édition de tag
			// nbStableTags = contributionList.get(0).getTags().size();
			// if (contributionList.get(0).getVersion() == 1) {
			// nbTagEdition = nbStableTags;
			// }
		}
		myOSMObjects.get(currentID).setNbGeomEdition(nbGeomEdition);
		myOSMObjects.get(currentID).setNbTagEdition(nbTagEdition);
		myOSMObjects.get(currentID).setNbStableTags(nbStableTags);
	}

	public static int[] nodeEditionDetails(List<OSMResource> contributionList, int nbStableTags, int nbTagEdition,
			int nbGeomEdition) {
		for (int i = 1; i < contributionList.size(); i++) {
			// Compare two consecutive contributions geometries
			OSMNode currentNode = ((OSMNode) contributionList.get(i).getGeom());
			OSMNode previousNode = ((OSMNode) contributionList.get(i - 1).getGeom());
			nbGeomEdition = countNodeGeomEdition(currentNode, previousNode, nbGeomEdition);
			// Compare two consecutive sets of tags
			HashMap<String, String> currentSetOfTags = (HashMap<String, String>) contributionList.get(i).getTags();
			HashMap<String, String> previousSetOfTags = (HashMap<String, String>) contributionList.get(i - 1).getTags();
			nbStableTags = previousSetOfTags.size();
			int[] tagsIndicator = countTagEdition(currentSetOfTags, previousSetOfTags, nbTagEdition, nbStableTags);
			nbTagEdition = tagsIndicator[0];
			nbStableTags = tagsIndicator[1];
		}
		int[] result = new int[3];
		result[0] = nbGeomEdition;
		result[1] = nbTagEdition;
		result[2] = nbStableTags;
		return result;
	}

	public static int[] wayEditionDetails(List<OSMResource> contributionList, int nbStableTags, int nbTagEdition,
			int nbGeomEdition) {
		for (int i = 1; i < contributionList.size(); i++) {
			// Compare two consecutive contributions geometries (i.e. two
			// consecutive sets of nodes)
			OSMWay currentWay = ((OSMWay) contributionList.get(i).getGeom());
			OSMWay previousWay = ((OSMWay) contributionList.get(i - 1).getGeom());
			nbGeomEdition = countWayGeomEdition(currentWay, previousWay, i);
			// Compare two consecutive sets of tags
			HashMap<String, String> currentSetOfTags = (HashMap<String, String>) contributionList.get(i).getTags();
			HashMap<String, String> previousSetOfTags = (HashMap<String, String>) contributionList.get(i - 1).getTags();
			nbStableTags = previousSetOfTags.size();
			int[] tagsIndicator = countTagEdition(currentSetOfTags, previousSetOfTags, nbTagEdition, nbStableTags);
			nbTagEdition = tagsIndicator[0];
			nbStableTags = tagsIndicator[1];
		}
		int[] result = new int[3];
		result[0] = nbGeomEdition;
		result[1] = nbTagEdition;
		result[2] = nbStableTags;
		return result;
	}

	public static int[] countTagEdition(HashMap<String, String> currentSetOfTags,
			HashMap<String, String> previousSetOfTags, int nbTagEdition, int nbStableTags) {
		// boolean tagAddition = false, tagEdition = false, tagSuppression =
		// false;
		// Count number of tag value editions + tag additions
		if (!currentSetOfTags.isEmpty()) {
			if (!previousSetOfTags.values().containsAll(currentSetOfTags.values())
					|| !currentSetOfTags.values().containsAll(previousSetOfTags.values()))
				nbTagEdition++;
			// for (String currentKey : currentSetOfTags.keySet()) {
			// String previousValue = previousSetOfTags.get(currentKey);
			// if (previousValue == null) { // tag addition
			// // nbTagEdition += 1;
			// tagAddition = true;
			// } else if
			// (!previousValue.equals(currentSetOfTags.get(currentKey))) {
			// // tag value edition
			// // nbTagEdition += 1;
			// tagEdition = true;
			// }
			// }
		}
		// Count number of stable tags
		for (String previousKey : previousSetOfTags.keySet()) {
			String nextValue = currentSetOfTags.get(previousKey);
			if (nextValue == null) { // tag suppression
				// nbTagEdition += 1;
				nbStableTags -= 1;
				// tagSuppression = true;
			}
		}
		// if (tagAddition || tagEdition || tagSuppression)
		// nbTagEdition++;
		int[] result = new int[2];
		result[0] = nbTagEdition;
		result[1] = nbStableTags;
		return result;

	}

	public static int countWayGeomEdition(OSMWay currentWay, OSMWay previousWay, int nbGeomEdition) {
		List<Long> currentComposition = currentWay.getVertices();
		List<Long> previousComposition = previousWay.getVertices();
		// Count number of node additions
		if (!previousComposition.containsAll(currentComposition)
				|| !currentComposition.containsAll(previousComposition))
			nbGeomEdition++;
		return nbGeomEdition;
	}

	public static int countNodeGeomEdition(OSMNode currentNode, OSMNode previousNode, int nbGeomEdition) {
		IDirectPosition currentLocation = currentNode.getPosition();
		IDirectPosition previousLocation = previousNode.getPosition();
		if (!currentLocation.equals(previousLocation)) {
			nbGeomEdition++;
		}
		return nbGeomEdition;
	}

	public static List<OSMResource> fillcontributionList(String OSMResourceType, Long currentID,
			HashMap<Long, OSMObject> myOSMNodeObjects) {
		// Select all the java objects that contain currentID
		List<OSMResource> contributionList = new ArrayList<OSMResource>();
		Iterator<OSMResource> it = myJavaObjects.iterator();
		while (it.hasNext()) {
			OSMResource contribution = it.next();
			if (contribution.getGeom().getClass().getSimpleName().equals(OSMResourceType)) {
				if (currentID == contribution.getId()) {
					contributionList.add(contribution);
				}
			}
		}
		return contributionList;
	}

	/**
	 * Sorts OSMResource contained in myJavaObjects and fills a hashmap that
	 * stores OSMObjects. OSMResource type must be indicated in parameter:
	 * OSMWay, OSMNode or OSMRelation.
	 * 
	 * @param OsmResourceType
	 * @return myOSMObjects
	 **/
	public static HashMap<Long, OSMObject> osmObjectsInit(String OsmResourceType) {
		// Sorting OSMResource list in the chronological order
		Collections.sort(myJavaObjects, new Comparator<OSMResource>() {
			@Override
			public int compare(OSMResource r1, OSMResource r2) {
				return r1.getDate().compareTo(r2.getDate());
			}
		});
		HashMap<Long, OSMObject> myOSMObjects = new HashMap<Long, OSMObject>();
		/** Parsing myJavaObjects to create indicators inside of OSMObjects **/
		Iterator<OSMResource> it = myJavaObjects.iterator();
		while (it.hasNext()) {
			OSMResource contribution = it.next();
			if (contribution.getGeom().getClass().getSimpleName().equals(OsmResourceType)) {
				if (!myOSMObjects.containsKey(contribution.getId())) {
					// If OSMObject doesn't exist yet, create a new object
					OSMObject objet = new OSMObject(contribution.getId());
					objet.nbVersions = 1;
					myOSMObjects.put(contribution.getId(), objet);
				} else {
					// If OSMObject already exists
					myOSMObjects.get(contribution.getId()).nbVersions += 1;
				}
			}
		}
		return myOSMObjects;
	}

	public static void writeContributionSummary(HashMap<Long, OSMObject> myOSMObjects, File file) throws IOException {
		// Create a CSV writer
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		// write header
		String[] line = new String[8];
		line[0] = "id";
		line[1] = "versions";
		line[2] = "contributeurs";
		line[3] = "geom-edition";
		line[4] = "tag-edition";
		line[5] = "stable-tags";
		line[6] = "datemin";
		line[7] = "datemax";
		writer.writeNext(line);
		for (OSMObject myObject : myOSMObjects.values()) {
			line = new String[8];
			line[0] = String.valueOf(myObject.getOsmId());
			line[1] = String.valueOf(myObject.getNbVersions());
			line[2] = String.valueOf(myObject.getNbContributors());
			line[3] = String.valueOf(myObject.getNbGeomEdition());
			line[4] = String.valueOf(myObject.getNbTagEdition());
			line[5] = String.valueOf(myObject.getNbStableTags());
			line[6] = String.valueOf(myObject.getDateMin());
			line[7] = String.valueOf(myObject.getDateMax());
			writer.writeNext(line);
		}
		writer.close();
	}

	public static void writeNodeContributionDetails(File file, String OSMResourceType) throws IOException {
		Collections.sort(myJavaObjects, new Comparator<OSMResource>() {
			@Override
			public int compare(OSMResource r1, OSMResource r2) {
				return r1.getDate().compareTo(r2.getDate());
			}
		});
		// Create a CSV writer
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		// write header
		String[] line = new String[10];
		line[0] = "id";
		line[1] = "version";
		line[2] = "changeset";
		line[3] = "uid";
		line[4] = "contributeur";
		line[5] = "date";
		line[6] = "source";
		line[7] = "nbTags";
		line[8] = "longitude";
		line[9] = "latitude";

		writer.writeNext(line);

		for (OSMResource resource : myJavaObjects) {
			if (resource.getGeom().getClass().getSimpleName().equals(OSMResourceType)) {
				OSMNode node = (OSMNode) resource.getGeom();
				line = new String[10];
				line[0] = Long.toString(resource.getId());
				line[1] = Integer.toString(resource.getVersion());
				line[2] = Integer.toString(resource.getChangeSet());
				line[3] = Integer.toString(resource.getUid());
				line[4] = resource.getContributeur();
				line[5] = resource.getDate().toString();
				line[6] = resource.getSource();
				line[7] = Integer.toString(resource.getNbTags());
				line[8] = Double.toString(node.getLongitude());
				line[9] = Double.toString(node.getLatitude());
				writer.writeNext(line);
			}
		}
		writer.close();
	}

	public static void writeContributionDetails(File file, List<OSMResource> myJavaObjects) throws IOException {
		// Create a CSV writer
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		// write header
		String[] line = new String[9];
		line[0] = "id";
		line[1] = "version";
		line[2] = "changeset";
		line[3] = "uid";
		line[4] = "contributeur";
		line[5] = "date";
		line[6] = "source";
		line[7] = "nbTags";
		line[8] = "OSMResource.getGeom()";

		writer.writeNext(line);

		for (OSMResource resource : myJavaObjects) {

			// OSMNode node = (OSMNode) resource.getGeom();
			// OSMWay way = (OSMWay) resource.getGeom();
			line = new String[9];
			line[0] = Long.toString(resource.getId());
			line[1] = Integer.toString(resource.getVersion());
			line[2] = Integer.toString(resource.getChangeSet());
			line[3] = Integer.toString(resource.getUid());
			line[4] = resource.getContributeur();
			line[5] = resource.getDate().toString();
			line[6] = resource.getSource();
			line[7] = Integer.toString(resource.getNbTags());
			line[8] = resource.getGeom().getClass().getSimpleName();
			writer.writeNext(line);

		}
		writer.close();
	}

	public static void writeWayContributionDetails(File file, String OSMResourceType) throws IOException {
		Collections.sort(myJavaObjects, new Comparator<OSMResource>() {
			@Override
			public int compare(OSMResource r1, OSMResource r2) {
				return r1.getDate().compareTo(r2.getDate());
			}
		});
		// Create a CSV writer
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		// write header
		String[] line = new String[10];
		line[0] = "id";
		line[1] = "version";
		line[2] = "changeset";
		line[3] = "uid";
		line[4] = "contributeur";
		line[5] = "date";
		line[6] = "source";
		line[7] = "nbTags";
		line[8] = "nbNodes";

		writer.writeNext(line);

		for (OSMResource resource : myJavaObjects) {
			if (resource.getGeom().getClass().getSimpleName().equals(OSMResourceType)) {
				// OSMNode node = (OSMNode) resource.getGeom();
				OSMWay way = (OSMWay) resource.getGeom();
				line = new String[9];
				line[0] = Long.toString(resource.getId());
				line[1] = Integer.toString(resource.getVersion());
				line[2] = Integer.toString(resource.getChangeSet());
				line[3] = Integer.toString(resource.getUid());
				line[4] = resource.getContributeur();
				line[5] = resource.getDate().toString();
				line[6] = resource.getSource();
				line[7] = Integer.toString(resource.getNbTags());
				line[8] = Integer.toString(way.getVertices().size());
				writer.writeNext(line);
			}
		}
		writer.close();
	}

}
