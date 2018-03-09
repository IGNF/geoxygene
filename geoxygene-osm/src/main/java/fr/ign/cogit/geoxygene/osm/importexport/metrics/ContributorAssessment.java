package fr.ign.cogit.geoxygene.osm.importexport.metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.contributor.OSMContributor;
import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.ChangesetRetriever;
import fr.ign.cogit.geoxygene.osm.schema.OSMDefaultFeature;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeometryConversion;

public class ContributorAssessment {
	/**
	 * Makes a summary of the contributors of a set of OSM contributions
	 * 
	 * @param myJavaObjects
	 *            set of OSMResource
	 * @return a hashmap containing all OSM contributors indexed by their uid
	 * @throws Exception
	 */
	public static HashMap<Long, OSMContributor> contributorSummary(Set<OSMResource> myJavaObjects) throws Exception {
		// Fills only the keys of myContributors hashmap
		// HashMap<Long, OSMContributor> myContributors =
		// osmContributorsInit(myJavaObjects);
		HashMap<Long, OSMContributor> myContributors = new HashMap<Long, OSMContributor>();
		// Parcourt myJavaObjects et associe à chaque contributeur ses
		// contributions
		Iterator<OSMResource> it = myJavaObjects.iterator();
		while (it.hasNext()) {
			OSMResource contribution = it.next();
			// Pour l'instant: remplit on ne considère que les contributions de
			// type node
			if (contribution.getGeom().getClass().getSimpleName().equals("OSMNode")) {
				Long uid = (long) contribution.getUid();
				String username = contribution.getContributeur();
				if (!myContributors.containsKey(uid)) {
					// Ajout d'un nouveau contributeur dans la liste et
					// initialise
					// avec l'objet parcouru
					myContributors.put(uid, new OSMContributor(new FT_FeatureCollection<OSMDefaultFeature>(), username,
							uid.intValue()));
					myContributors.get(uid).addContribution(contribution);
				} else {
					// Ajoute l'objet parcouru dans les contributions du
					// contributeur existant
					OSMDefaultFeature feature = OSMResource2OSMFeature(contribution, myJavaObjects);
					myContributors.get(uid).addContribution(feature);
					myContributors.get(uid).addContribution(contribution);

				}
			}

		}

		// Iterator<Long> contributorsIDs = myContributors.keySet().iterator();
		// while (contributorsIDs.hasNext()) {
		// long currentUID = contributorsIDs.next();
		// List<OSMFeature> contributionList =
		// fillcontributionList(currentUID, myJavaObjects);
		// for (OSMFeature contribution : contributionList)
		// myContributors.get(currentUID).addContribution(contribution);

		/** Calcul des indicateurs **/
		// int nbOfContributions =
		// myContributors.get(currentUID).getContributions().size();
		// int nbOfDayTimeContributions =
		// myContributors.get(currentUID).getDaytimeContributions().size();
		// int nbOfNightTimeContributions =
		// myContributors.get(currentUID).getNighttimeContributions().size();
		// int nbOfweekContributions =
		// myContributors.get(currentUID).getWeekContributions().size();
		// int nbOfweekendContributions =
		// myContributors.get(currentUID).getWeekEndContributions().size();
		// myContributors.get(currentUID).setNbOfContributions(nbOfContributions);
		// myContributors.get(currentUID).setNbOfDayTimeContributions(nbOfDayTimeContributions);
		// myContributors.get(currentUID).setNbOfNightTimeContributions(nbOfNightTimeContributions);
		// myContributors.get(currentUID).setNbOfweekContributions(nbOfweekContributions);
		// myContributors.get(currentUID).setNbOfweekendContributions(nbOfweekendContributions);
		// }
		return myContributors;
	}

	public static List<OSMFeature> fillcontributionList(Long currentUID, Set<OSMResource> myJavaObjects)
			throws Exception {
		// Select all the java objects that contain currentID
		List<OSMFeature> contributionList = new ArrayList<OSMFeature>();
		for (OSMResource resource : myJavaObjects) {
			if (currentUID == resource.getUid()) {
				OSMFeature feature = OSMResource2OSMFeature(resource, myJavaObjects);
				contributionList.add(feature);
			}
		}
		Collections.sort(contributionList);
		return contributionList;
	}

	public static HashMap<Long, OSMContributor> osmContributorsInit(Set<OSMResource> myJavaObjects) {
		// Sorting OSMResource list in the chronological order
		// IntrinsicAssessment.sortJavaObjects(myJavaObjects);
		HashMap<Long, OSMContributor> myContributors = new HashMap<Long, OSMContributor>();
		/** Parsing myJavaObjects to create indicators inside of OSMObjects **/
		Iterator<OSMResource> it = myJavaObjects.iterator();
		while (it.hasNext()) {
			OSMResource contribution = it.next();
			Long uid = (long) contribution.getUid();
			String username = contribution.getContributeur();
			if (!myContributors.containsKey(uid)) {
				myContributors.put(uid,
						new OSMContributor(new FT_FeatureCollection<OSMDefaultFeature>(), username, uid.intValue()));
			}

		}
		return myContributors;
	}

	public static OSMDefaultFeature OSMResource2OSMFeature(OSMResource resource, Set<OSMResource> myJavaObjects)
			throws Exception {
		OsmGeometryConversion convertor = new OsmGeometryConversion("4326");
		IGeometry igeom = null;
		if (resource.getGeom().getClass().getSimpleName().equals("OSMNode")) {
			igeom = convertor.convertOsmPoint((OSMNode) resource.getGeom());
		}
		if (resource.getGeom().getClass().getSimpleName().equals("OSMWay")) {
			List<OSMResource> myNodes = IntrinsicAssessment.getNodesComposingWay(myJavaObjects, resource);
			igeom = convertor.convertOSMLine((OSMWay) resource.getGeom(), myNodes);
		}
		if (resource.getGeom().getClass().getSimpleName().equals("OSMRelation")) {
			// Plus tard
		}
		OSMDefaultFeature feature = new OSMDefaultFeature(resource.getContributeur(), igeom, (int) resource.getId(),
				resource.getChangeSet(), resource.getVersion(), resource.getUid(), resource.getDate(),
				resource.getTags());
		return feature;
	}

	public static void writeContributorSummary(HashMap<Long, OSMContributor> myContributors, File file)
			throws IOException {
		// Create a CSV writer
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		// write header
		String[] line = new String[20];
		line[0] = "uid";
		line[1] = "name";
		line[2] = "nbContributions";
		line[3] = "nbDayContributions";
		line[4] = "nbNightContributions";
		line[5] = "nbOfweekContributions";
		line[6] = "nbOfweekendContributions";
		line[7] = "nbCreations";
		line[8] = "nbModifications";
		line[9] = "nbDeletes";
		line[10] = "nbBlockReceived";
		line[11] = "totalChangesetCount";
		line[12] = "p_DayContributions";
		line[13] = "p_NightContributions";
		line[14] = "p_OfweekContributions";
		line[15] = "p_OfweekendContributions";
		line[16] = "p_Creations";
		line[17] = "p_Modifications";
		line[18] = "p_Deletes";
		line[19] = "nbChangesetFenetre";

		writer.writeNext(line);
		for (OSMContributor contributor : myContributors.values()) {
			line = new String[20];
			line[0] = String.valueOf(contributor.getId());
			line[1] = String.valueOf(contributor.getName());
			line[2] = String.valueOf(contributor.getNbContributions());

			line[3] = String.valueOf(contributor.getNbDayTimeContributions());
			line[4] = String.valueOf(contributor.getNbNightTimeContributions());
			line[5] = String.valueOf(contributor.getNbWeekContributions());
			line[6] = String.valueOf(contributor.getNbWeekendContributions());
			line[7] = String.valueOf(OSMContributorAssessment.getNbCreations(contributor.getResource()));
			line[8] = String.valueOf(OSMContributorAssessment.getNbModification(contributor.getResource()));
			line[9] = String.valueOf(OSMContributorAssessment.getNbDeletes(contributor.getResource()));

			line[10] = String.valueOf(OSMContributorAssessment.getNbBlockReceived(contributor.getId()));
			line[11] = String.valueOf(OSMContributorAssessment.getTotalChgsetCount(contributor.getId()));

			line[12] = String.valueOf(Double.valueOf(contributor.getNbDayTimeContributions())
					/ Double.valueOf(contributor.getNbContributions()));
			line[13] = String.valueOf(Double.valueOf(contributor.getNbNightTimeContributions())
					/ Double.valueOf(contributor.getNbContributions()));
			line[14] = String.valueOf(Double.valueOf(contributor.getNbWeekContributions())
					/ Double.valueOf(contributor.getNbContributions()));
			line[15] = String.valueOf(Double.valueOf(contributor.getNbWeekendContributions())
					/ Double.valueOf(contributor.getNbContributions()));
			line[16] = String.valueOf(Double.valueOf(OSMContributorAssessment.getNbCreations(contributor.getResource()))
					/ Double.valueOf(contributor.getNbContributions()));
			line[17] = String
					.valueOf(Double.valueOf(OSMContributorAssessment.getNbModification(contributor.getResource()))
							/ Double.valueOf(contributor.getNbContributions()));
			line[18] = String.valueOf(Double.valueOf(OSMContributorAssessment.getNbDeletes(contributor.getResource()))
					/ Double.valueOf(contributor.getNbContributions()));
			line[19] = String.valueOf(ChangesetRetriever.getNbChangesets(contributor.getResource()));

			writer.writeNext(line);
		}
		writer.close();
	}
}
