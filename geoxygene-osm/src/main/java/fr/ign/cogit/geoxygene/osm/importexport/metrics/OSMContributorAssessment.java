package fr.ign.cogit.geoxygene.osm.importexport.metrics;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.ign.cogit.geoxygene.osm.anonymization.db.SQLDBPreAnonymization;
import fr.ign.cogit.geoxygene.osm.contributor.OSMContributor;
import fr.ign.cogit.geoxygene.osm.importexport.OSMObject;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.ChangesetRetriever;

public class OSMContributorAssessment {
	/**
	 * Parse all the contributions and get the contributions authored by a user
	 * 
	 * @param user
	 * @param myJavaObjects
	 */
	public static void addAllContributions(OSMContributor user, Set<OSMResource> myJavaObjects) {
		for (OSMResource r : myJavaObjects)
			if (r.getUid() == user.getId())
				user.addContribution(r);
	}

	/**
	 * 
	 * @param user
	 * @return number of contributions
	 */
	public static Integer getNbContributions(Set<OSMResource> contributions) {
		return contributions.size();
	}

	/**
	 * 
	 * @param user
	 * @return number of contributions produced on Saturdays and Sundays
	 */
	public static Integer getNbWeekendContributions(Collection<OSMResource> contributions) {
		int nbWeekendContrib = 0;
		for (OSMResource obj : contributions) {
			Date contributionDate = obj.getDate();
			Calendar c = new GregorianCalendar();
			c.setTime(contributionDate);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
				nbWeekendContrib++;
			}
		}
		return nbWeekendContrib;
	}

	/**
	 * 
	 * @param contributions
	 * @return
	 */
	public static Integer getNbNightContributions(Collection<OSMResource> contributions) {
		int nbNightContrib = 0;
		for (OSMResource obj : contributions) {
			Date contributionDate = obj.getDate();
			Calendar c = new GregorianCalendar();
			c.setTime(contributionDate);
			Calendar nineOClock = (Calendar) c.clone();
			nineOClock.set(Calendar.HOUR_OF_DAY, 9);
			nineOClock.set(Calendar.MINUTE, 0);
			Calendar sixOClock = (Calendar) c.clone();
			sixOClock.set(Calendar.HOUR_OF_DAY, 18);
			sixOClock.set(Calendar.MINUTE, 0);
			if (c.before(nineOClock) || c.after(sixOClock))
				nbNightContrib++;
		}
		return nbNightContrib;
	}

	public static Integer getNbCreations(Collection<OSMResource> contributions) {
		int nbCrea = 0;
		for (OSMResource r : contributions) {
			if (r.getVersion() == 1)
				nbCrea++;
		}
		return nbCrea;
	}

	public static Integer getNbModification(Collection<OSMResource> contributions) {
		int k = 0;
		for (OSMResource r : contributions)
			if (r.getVersion() > 1)
				k++;
		return k;
	}

	public static Integer getNbDeletes(Collection<OSMResource> contributions) {
		int k = 0;
		for (OSMResource r : contributions)
			if (!r.isVisible())
				k++;
		return k;
	}
	// getNbReverts(Set<OSMResource> contributions)

	public static Integer getNbCreatedNodes(Collection<OSMResource> contributions) {
		int k = 0;
		for (OSMResource r : contributions)
			if (r.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMNode"))
				if (r.getVersion() == 1)
					k++;
		return k;
	}

	public static Integer getNbCreatedWays(Collection<OSMResource> contributions) {
		int k = 0;
		for (OSMResource r : contributions)
			if (r.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMWay"))
				if (r.getVersion() == 1)
					k++;
		return k;
	}

	public static Integer getNbCreatedRelations(Collection<OSMResource> contributions) {
		int k = 0;
		for (OSMResource r : contributions)
			if (r.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMRelation"))
				if (r.getVersion() == 1)
					k++;
		return k;
	}

	public static Integer getNbModifiedNodes(Collection<OSMResource> contributions) {
		int k = 0;
		for (OSMResource r : contributions)
			if (r.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMNode"))
				if (r.getVersion() > 1)
					k++;

		return k;
	}

	public static Integer getNbDeletedNodes(Collection<OSMResource> contributions) {
		int k = 0;
		for (OSMResource r : contributions)
			if (r.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMNode"))
				if (!r.isVisible())
					k++;

		return k;
	}

	public static Integer getNbDeletedWays(Collection<OSMResource> contributions) {
		int k = 0;
		for (OSMResource r : contributions)
			if (r.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMWay"))
				if (!r.isVisible())
					k++;

		return k;
	}

	public static Integer getNbDeletedRelations(Collection<OSMResource> contributions) {
		int k = 0;
		for (OSMResource r : contributions)
			if (r.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMRelation"))
				if (!r.isVisible())
					k++;

		return k;
	}

	/**
	 * @param uid
	 * @param myOSMObjects
	 * @return number of node which latest version is authored by user uid
	 */
	public static Integer getNbToDate(Integer uid, HashMap<Long, OSMObject> myOSMObjects) {
		int nb = 0;
		for (OSMObject obj : myOSMObjects.values()) {
			int last = obj.getContributions().size() - 1;
			if (obj.getContributions().get(last).getUid() == uid)
				nb++;
		}
		return nb;
	}

	/**
	 * 
	 * @param uid
	 * @return the number of changesets a user produced since his/her
	 *         registration on OSM
	 */
	public static Integer getTotalChgsetCount(Integer uid) throws NullPointerException {
		int nbChgsetTot = 0;
		try {
			String urlAPI = "http://api.openstreetmap.org/api/0.6/user/" + uid;
			Document xml = SQLDBPreAnonymization.getDataFromAPI(urlAPI);

			Node osm = xml.getFirstChild();
			Element user = (Element) osm.getChildNodes().item(1);
			NodeList properties = user.getChildNodes();
			for (int i = 0; i < properties.getLength(); i++) {
				if (properties.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element elt = (Element) properties.item(i);
					if (elt.getNodeName().equals("changesets"))
						nbChgsetTot = Integer.valueOf(elt.getAttribute("count"));
				}
			}
		} catch (NullPointerException e) {
			return null;
		}
		return nbChgsetTot;
	}

	/**
	 * 
	 * @param uid
	 * @return number of received block for a OSM user
	 */
	public static Integer getNbBlockReceived(Integer uid) throws NullPointerException {
		try {
			String urlAPI = "http://api.openstreetmap.org/api/0.6/user/" + uid;
			Document xml = SQLDBPreAnonymization.getDataFromAPI(urlAPI);
			Node osm = xml.getFirstChild();
			Element user = (Element) osm.getChildNodes().item(1);
			NodeList properties = user.getChildNodes();
			for (int i = 0; i < properties.getLength(); i++) {
				if (properties.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element elt = (Element) properties.item(i);
					// System.out.println(elt.getNodeName());
					if (elt.getNodeName().equals("blocks")) {
						Element blocks = (Element) elt.getChildNodes().item(1);
						// System.out.println(blocks.getNodeName());
						// System.out.println(Integer.valueOf(blocks.getAttribute("count")));
						return Integer.valueOf(blocks.getAttribute("count"));
					}

				}
			}
		} catch (NullPointerException e) {
			return null;
		}
		return 0;

	}

	/**
	 * Calcule l'utilisation des éditeurs OSM à partir des changesets
	 * 
	 * @param changesetIDs
	 * @param retriever
	 * @return une map dont les clés sont les changeset IDs et les valeurs sont
	 *         le pourcentage d'utilisation de l'éditeur (par rapport au nombre
	 *         total de changesets)
	 * @throws Exception
	 */
	public static HashMap<String, Double> getChangesetEditorUse(Set<Integer> changesetIDs, ChangesetRetriever retriever)
			throws Exception {
		HashMap<String, Double> chsgtEditors = new HashMap<String, Double>();
		for (Integer chgstID : changesetIDs) {
			if (!retriever.isInChangesetTable(Long.valueOf(chgstID))) {
				String value = retriever.getChangesetValues(Long.valueOf(chgstID));
				retriever.insertOneRow(value);
			}
			String editor = retriever.getChgestEditor(chgstID);
			if (!chsgtEditors.containsKey(editor))
				chsgtEditors.put(editor, Double.valueOf(1 / changesetIDs.size()));
			else
				chsgtEditors.put(editor, chsgtEditors.get(editor) + Double.valueOf(1 / changesetIDs.size()));
		}

		return chsgtEditors;
	}
}
