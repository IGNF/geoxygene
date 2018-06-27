package fr.ign.cogit.geoxygene.osm.importexport.metrics;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.osm.importexport.OSMObject;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;

public class OSMObjectAssessment {
	/**
	 * 
	 * @param object
	 * @return the number of versions in the contributions list of an OSMObject
	 */
	public static Integer getNbVersions(OSMObject object) {
		return object.getContributions().size();
	}

	public static Set<Integer> getUIDList(OSMObject object) {
		Set<Integer> uidList = new HashSet<Integer>(); // A set does not allow
														// duplication
		for (OSMResource r : object.getContributions())
			uidList.add(r.getUid());
		return uidList;
	}

	public static Integer getNbContributors(OSMObject object) {
		return getUIDList(object).size();
	}

	/**
	 * 
	 * @param object
	 * @return the oldest contribution of the contributions history
	 */
	public static OSMResource getVmin(OSMObject object) {
		return object.getContributions().get(0);
	}

	/**
	 * 
	 * @param object
	 * @return the most recent contribution of the contributions history
	 */
	public static OSMResource getVmax(OSMObject object) {
		int nbVersions = getNbVersions(object);
		return object.getContributions().get(nbVersions - 1);
	}

	/**
	 * 
	 * @param object
	 * @return the date of the oldest contribution of the history
	 */
	public static Date getDateMin(OSMObject object) {
		return getVmin(object).getDate();
	}

	/**
	 * 
	 * @param object
	 * @return the date of the most recent contribution of the history
	 */
	public static Date getDateMax(OSMObject object) {
		return getVmax(object).getDate();
	}

	public static Integer getNbTagEdition(OSMObject object) {
		int nbTagEdition = 0;
		List<OSMResource> contributions = object.getContributions();
		// On ne peut rien dire sur la contribution à la position i = 0
		for (int i = 1; i < object.getContributions().size(); i++) {
			if (contributions.get(i - 1).getVersion() == contributions.get(i).getVersion() - 1)
				// On se place dans le cas où les deux contributions se
				// succèdent au niveau du numéro de version
				if (OSMResourceQualityAssessment.isTagModification(contributions.get(i), contributions.get(i - 1)))
					nbTagEdition++;
		}
		return nbTagEdition;
	}

	public static Integer getNbTagAddition(OSMObject object) {
		int nbTagAddition = 0;
		List<OSMResource> contributions = object.getContributions();
		// On ne peut rien dire sur la contribution à la position i = 0
		for (int i = 1; i < object.getContributions().size(); i++) {
			if (contributions.get(i - 1).getVersion() == contributions.get(i).getVersion() - 1)
				// On se place dans le cas où les deux contributions se
				// succèdent au niveau du numéro de version
				if (OSMResourceQualityAssessment.isTagCreation(contributions.get(i).getTags(),
						contributions.get(i - 1).getTags()))
					nbTagAddition++;
		}
		return nbTagAddition;
	}

	public static Integer getNbTagDelete(OSMObject object) {
		int nbTagDelete = 0;
		List<OSMResource> contributions = object.getContributions();
		// On ne peut rien dire sur la contribution à la position i = 0
		for (int i = 1; i < object.getContributions().size(); i++) {
			if (contributions.get(i - 1).getVersion() == contributions.get(i).getVersion() - 1)
				// On se place dans le cas où les deux contributions se
				// succèdent au niveau du numéro de version
				if (OSMResourceQualityAssessment.isTagDelete(contributions.get(i).getTags(),
						contributions.get(i - 1).getTags()))
					nbTagDelete++;
		}
		return nbTagDelete;
	}

	/**
	 * 
	 * @param object
	 * @return number of geometry editions on the history of the contribution.
	 *         Only for OSMNode and OSMWay type
	 */
	public static Integer getNbGeomEdition(OSMObject object) {
		int nbGeomEdition = 0;
		List<OSMResource> contributions = object.getContributions();
		if (contributions.size() < 2)
			return nbGeomEdition;
		// On ne peut rien dire sur la contribution à la position i = 0
		for (int i = 1; i < object.getContributions().size(); i++) {
			if (!contributions.get(i).isVisible())
				continue;
			if (contributions.get(i - 1).getVersion() == contributions.get(i).getVersion() - 1) {
				// On se place dans le cas où les deux contributions se
				// succèdent au niveau du numéro de version
				if (contributions.get(i).isGeomEquals(contributions.get(i - 1)))
					continue;
				nbGeomEdition++;
			}

		}
		return nbGeomEdition;
	}

	/**
	 * 
	 * @param object
	 * @return OSMNode, OSMWay or OSMRelation
	 */
	public static String getGeomPrimitiveName(OSMObject object) {
		return object.getContributions().get(0).getGeom().getClass().getSimpleName();
		// return object.getPrimitiveGeomOSM().getClass().getSimpleName();
	}

	/**
	 * 
	 * @param object
	 * @return number of reverts in the history of the object
	 */
	public static Integer getNbRevert(OSMObject object) {
		int nbRevert = 0;
		List<OSMResource> contributions = object.getContributions();
		if (contributions.size() < 3)
			return nbRevert;
		for (int i = 2; i < object.getContributions().size(); i++) {
			if (contributions.get(i - 2).getVersion() == contributions.get(i).getVersion() - 2) {
				// Test sur l'égalité géométrique
				if (getGeomPrimitiveName(object).equalsIgnoreCase("OSMNode")) {
					if (!OSMResourceQualityAssessment.isNodeGeomEdition(contributions.get(i), contributions.get(i - 2)))
						continue;
				} else if (getGeomPrimitiveName(object).equalsIgnoreCase("OSMWay")) {
					if (!((OSMWay) contributions.get(i).getGeom()).getVertices()
							.equals(((OSMWay) contributions.get(i - 2).getGeom()).getVertices()))
						continue;
				}
				// Test sur l'égalité des tags
				if (!contributions.get(i).isTagsEquals(contributions.get(i - 2)))
					continue;
				nbRevert++;
			}

		}
		return nbRevert;
	}

	/**
	 * 
	 * @param object
	 * @return number of deletes in the history of the object
	 */
	public static Integer getNbDelete(OSMObject object) {
		int nbDelete = 0;
		for (OSMResource r : object.getContributions())
			if (!r.isVisible())
				nbDelete++;
		return nbDelete;
	}

	/**
	 * 
	 * @param object
	 * @return number of stable tags in the history of the object
	 */
	public static Integer getNbStableTags(OSMObject object) {
		int nbStable = object.getContributions().get(0).getTags().size();
		// TODO
		return nbStable;
	}
}
