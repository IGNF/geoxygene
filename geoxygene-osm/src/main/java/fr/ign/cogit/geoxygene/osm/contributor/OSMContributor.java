package fr.ign.cogit.geoxygene.osm.contributor;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.graphe.IEdge;
import fr.ign.cogit.geoxygene.contrib.graphe.IGraph;
import fr.ign.cogit.geoxygene.contrib.graphe.IGraphLinkableFeature;
import fr.ign.cogit.geoxygene.contrib.graphe.INode;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.anonymization.db.SQLDBPreAnonymization;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.schema.OSMDefaultFeature;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;

public class OSMContributor implements INode {

	private IFeatureCollection<OSMDefaultFeature> contributions;
	private Set<OSMResource> resource;
	private String name;
	private int id;
	private int nbContributions = 0;
	private int nbWeekendContributions = 0;
	private int nbWeekContributions = 0;
	private int nbDayTimeContributions = 0;
	private int nbNightTimeContributions = 0;
	/**
	 * Nombre d'objets créés, modifiés, supprimés ou rétablis dans une fenêtre
	 * spatio-temporelle
	 */
	private int nbCreation = 0;
	private int nbModification = 0;
	private int nbDelete = 0;
	private int nbRevert = 0;
	/**
	 * Nombre de nodes créés, modifiés, supprimés dans une fenêtre
	 * spatio-temporelle
	 */
	private int nbNodeCreation = 0;
	private int nbNodeModification = 0;
	private int nbNodeDelete = 0;
	/**
	 * Nombre de nodes dont la version était la plus récente dans le fenêtre
	 */
	private int nbNodeUpToDate = 0;
	/**
	 * Nombre de nodes qui ont été édités par la suite par un autre contributeur
	 */
	private int nbNodeCorrected = 0;
	/**
	 * Nombre de nodes qui ont été édités par la suite par le même contributeur
	 */
	private int nbNodeAutoCorrected = 0;
	/**
	 * Nombre de ways créés, modifiés, supprimés dans une fenêtre
	 * spatio-temporelle
	 */
	private int nbWayCreation = 0, nbWayModification = 0, nbWayDelete = 0;
	/**
	 * Nombre de Ways dont la version était la plus récente dans le fenêtre
	 */
	private int nbWayUpToDate = 0;
	/**
	 * Nombre de Ways qui ont été édités par la suite par un autre contributeur
	 */
	private int nbWayCorrected = 0;
	/**
	 * Nombre de Ways qui ont été édités par la suite par le même contributeur
	 */
	private int nbWayAutoCorrected = 0;

	/**
	 * Nombre de relations créées, modifiées, supprimées dans une fenêtre
	 * spatio-temporelle
	 */
	private int nbRelationCreation = 0, nbRelationModification = 0, nbRelationDelete = 0;
	/**
	 * Nombre de relations dont la version était la plus récente dans le fenêtre
	 */
	private int nbRelationUpToDate = 0;
	/**
	 * Nombre de relations qui ont été édités par la suite par un autre
	 * contributeur
	 */
	private int nbRelationCorrected = 0;
	/**
	 * Nombre de relations qui ont été édités par la suite par le même
	 * contributeur
	 */
	private int nbRelationAutoCorrected = 0;
	/**
	 * Nombre de changesets produits par le contributeur sur la fenêtre, et la
	 * part que cela représente par rapport au nombre total de changesets
	 * produits sur la fenêtre
	 */
	private int nbChangeset = 0;
	private double pChangeset = 0;
	/**
	 * Durée moyenne (en minutes) d'un changeset calculée à partir de
	 * nbChangeset
	 */
	private double chgstMeanDuration = 0;
	/**
	 * Nombre de changesets produits par le contributeur dans la fenêtre en
	 * utilisant les éditeurs suivants: iD, JOSM, Maps.me Android, Maps.me IOS,
	 * Potlatch, sur un autre éditeur ou sur un éditeur inconnu
	 * 
	 */
	private int nbChgsetID = 0, nbChgsetJosm = 0, nbChgsetMapsMeAndroid = 0, nbChgsetMapsMeIOS = 0,
			nbChgsetPotlatch = 0, nbChgsetOther = 0, nbChgsetUnknown = 0;
	/**
	 * Nombre de changesets produits par le contributeur depuis le début de son
	 * inscription
	 */
	private int nbTotalChgst = 0;

	private IFeatureCollection<DefaultFeature> activityAreas;

	public OSMContributor(IFeatureCollection<OSMDefaultFeature> contributions, String name, int id) {
		super();
		this.contributions = contributions;
		this.name = name;
		this.id = id;
		this.setNbTotalChgst(getNbChgsetTotal(this.id));
	}

	public IFeatureCollection<OSMDefaultFeature> getContributions() {
		return contributions;
	}

	public void setContributions(IFeatureCollection<OSMDefaultFeature> contributions) {
		this.contributions = contributions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addContribution(OSMDefaultFeature contribution) {
		this.getContributions().add(contribution);
	}

	/**
	 * Ajoute la contribution à la liste des resources du contributeur et met à
	 * jour les attributs de création, modification, suppression, d'édition de
	 * node/way/relation. Les attributs portant sur les changesets du
	 * contributeur ne sont pas calculés
	 * 
	 * @param contribution
	 *            de type OSMResource
	 */
	public void addContribution(OSMResource contribution) {
		this.resource.add(contribution);
		this.nbContributions++;
		// Met à jours les indicateurs sur le contributeur
		if (contribution.getEditionType().equals("creation")) {
			this.nbCreation++;
			// Attributs portant sur les types node, way et relation
			this.nbNodeCreation = (contribution.getGeom().getClass().equals("OSMNode") ? this.nbCreation++
					: this.nbNodeCreation);
			this.nbWayCreation = (contribution.getGeom().getClass().equals("OSMWay") ? this.nbWayCreation++
					: this.nbWayCreation);
			this.nbRelationCreation = (contribution.getGeom().getClass().equals("OSMRelation")
					? this.nbRelationCreation++ : this.nbRelationCreation);
		} else if (contribution.getEditionType().equals("modification")) {
			this.nbModification++;
			// Attributs portant sur les types node, way et relation
			this.nbNodeModification = (contribution.getGeom().getClass().equals("OSMNode") ? this.nbCreation++
					: this.nbNodeCreation);
			this.nbWayModification = (contribution.getGeom().getClass().equals("OSMWay") ? this.nbWayCreation++
					: this.nbWayCreation);
			this.nbRelationModification = (contribution.getGeom().getClass().equals("OSMRelation")
					? this.nbRelationCreation++ : this.nbRelationCreation);
		} else if (contribution.getEditionType().equals("delete")) {
			this.nbDelete++;
			// Attributs portant sur les types node, way et relation
			this.nbNodeDelete = (contribution.getGeom().getClass().equals("OSMNode") ? this.nbCreation++
					: this.nbNodeCreation);
			this.nbWayDelete = (contribution.getGeom().getClass().equals("OSMWay") ? this.nbWayCreation++
					: this.nbWayCreation);
			this.nbRelationDelete = (contribution.getGeom().getClass().equals("OSMRelation") ? this.nbRelationCreation++
					: this.nbRelationCreation);
		} else {
			this.nbRevert++;
		}
		if (contribution.isUpToDate()) {
			this.nbNodeUpToDate = (contribution.getGeom().getClass().equals("OSMNode") ? this.nbNodeUpToDate++
					: this.nbNodeUpToDate);
			this.nbWayUpToDate = (contribution.getGeom().getClass().equals("OSMWay") ? this.nbWayUpToDate++
					: this.nbWayUpToDate);
			this.nbRelationUpToDate = (contribution.getGeom().getClass().equals("OSMRelation")
					? this.nbRelationUpToDate++ : this.nbRelationUpToDate);
		}
		if (contribution.willbeCorrected()) {
			this.nbNodeCorrected = (contribution.getGeom().getClass().equals("OSMNode") ? this.nbNodeCorrected++
					: this.nbNodeCorrected);
			this.nbWayCorrected = (contribution.getGeom().getClass().equals("OSMWay") ? this.nbWayCorrected++
					: this.nbWayCorrected);
			this.nbRelationCorrected = (contribution.getGeom().getClass().equals("OSMRelation")
					? this.nbRelationCorrected++ : this.nbRelationCorrected);
		}
		if (contribution.willbeAutoCorrected()) {
			this.nbNodeAutoCorrected = (contribution.getGeom().getClass().equals("OSMNode") ? this.nbNodeAutoCorrected++
					: this.nbNodeAutoCorrected);
			this.nbWayAutoCorrected = (contribution.getGeom().getClass().equals("OSMWay") ? this.nbWayAutoCorrected++
					: this.nbWayCorrected);
			this.nbRelationAutoCorrected = (contribution.getGeom().getClass().equals("OSMRelation")
					? this.nbRelationAutoCorrected++ : this.nbRelationAutoCorrected);
		}
		if (contribution.isNightTimeContribution())
			this.nbNightTimeContributions++;
		else
			this.nbDayTimeContributions++;
		if (contribution.isWeekendContribution())
			this.nbWeekendContributions++;
		else
			this.nbWeekContributions++;
	}

	/**
	 * Parcourt les contributions de la fenêtre et calcule les indicateurs sur
	 * les changesets que le contributeur a produits sur la fenêtre
	 * 
	 * @param myJavaObjects
	 * @throws SQLException
	 */
	public void changesetRelatedAttributes(Set<OSMResource> myJavaObjects) throws SQLException {

		// Récupère les changesets produits dans la zone et compte ceux qui
		// appartiennent au contributeur
		Set<Integer> contributorChgst = new HashSet<Integer>();
		Set<Integer> totalChgst = new HashSet<Integer>();
		for (OSMResource r : myJavaObjects) {
			totalChgst.add(r.getChangeSet());
			if (r.getUid() == this.id) {
				contributorChgst.add(r.getChangeSet());
				// Durée du changeset
				Double d = changesetDuration(r.getChangeSet());
				this.chgstMeanDuration = (this.chgstMeanDuration + d) / 2;

				// Editeur utilisé
				String editor = getChgestEditor(r.getChangeSet());
				if (editor.startsWith("iD"))
					this.setNbChgsetID(this.getNbChgsetID() + 1);
				else if (editor.startsWith("JOSM"))
					this.nbChgsetJosm++;
				else if (editor.startsWith("Maps.me.ios"))
					this.nbChgsetMapsMeIOS++; // préfixe à vérifier
				else if (editor.startsWith("Maps.me.android"))
					this.nbChgsetMapsMeAndroid++; // préfixe à vérifier
				else if (editor.startsWith("Potlatch"))
					this.nbChgsetPotlatch++;
				else if (editor.equals("null"))
					this.nbChgsetUnknown++;
				else
					this.nbChgsetOther++;
			}

		}
		this.nbChangeset = contributorChgst.size();
		this.pChangeset = Double.valueOf(this.nbChangeset) / Double.valueOf(totalChgst.size());

	}

	public static Double changesetDuration(int chgstID) throws SQLException {
		// Connnexion to database
		java.sql.Connection conn;
		String url = "jdbc:postgresql://localhost:5432/paris"; // A changer si
																// besoin
		conn = DriverManager.getConnection(url, "postgres", "postgres");
		Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		String query = "SELECT closed_at - created_at FROM changeset WHERE id = " + chgstID;
		ResultSet r = s.executeQuery(query);
		String[] duration = r.getString(1).split(":");
		return Double.valueOf(duration[0]) * 60 + Double.valueOf(duration[1]) + Double.valueOf(duration[2]) / 60;

	}

	public static String getChgestEditor(int chgstID) throws SQLException {
		// Connnexion to database
		java.sql.Connection conn;
		String url = "jdbc:postgresql://localhost:5432/paris"; // Nom BDD à
																// changer si
																// besoin
		conn = DriverManager.getConnection(url, "postgres", "postgres");
		Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		String query = "SELECT tags -> \'created_by\' FROM changeset WHERE id = " + chgstID;
		ResultSet r = s.executeQuery(query);
		return r.getString(1);

	}

	public static int getNbChgsetTotal(int chgsetID) {
		int nbChgsetTot = 0;
		String urlAPI = "http://www.openstreetmap.org/api/0.6/user/" + chgsetID;
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
		return nbChgsetTot;
	}

	public int nbGPScontribution() {
		int nbGPScontrib = 0;
		for (OSMFeature obj : contributions) {
			if (obj.getTags().containsKey("source"))
				if (obj.getAttribute("source").toString().equalsIgnoreCase("gps"))
					nbGPScontrib++;
		}
		return nbGPScontrib;
	}

	public int getNbOfCreatedObjects() {
		int nbOfCreatedObjects = 0;
		for (OSMFeature obj : contributions) {
			if (obj.getVersion() == 1)
				nbOfCreatedObjects++;
		}
		return nbOfCreatedObjects;
	}

	public Collection<OSMFeature> getWeekEndContributions() {
		Set<OSMFeature> weekEndContributions = new HashSet<>();

		for (OSMFeature obj : contributions) {
			Date contributionDate = obj.getDate();
			Calendar c = new GregorianCalendar();
			c.setTime(contributionDate);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SATURDAY) {
				weekEndContributions.add(obj);
			} else if (dayOfWeek == Calendar.SUNDAY) {
				weekEndContributions.add(obj);
			}
		}

		return weekEndContributions;
	}

	public Collection<OSMFeature> getWeekContributions() {
		Set<OSMFeature> weekContributions = new HashSet<>();

		for (OSMFeature obj : contributions) {
			Date contributionDate = obj.getDate();
			Calendar c = new GregorianCalendar();
			c.setTime(contributionDate);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SATURDAY) {
				continue;
			} else if (dayOfWeek == Calendar.SUNDAY) {
				continue;
			} else
				weekContributions.add(obj);
		}

		return weekContributions;
	}

	/**
	 * Get the contributions this contributor made during daytime (between 9.00
	 * and 18.00).
	 * 
	 * @return
	 */
	public Collection<OSMFeature> getDaytimeContributions() {
		Set<OSMFeature> daytimeContributions = new HashSet<>();

		for (OSMFeature obj : contributions) {
			Date contributionDate = obj.getDate();
			Calendar c = new GregorianCalendar();
			c.setTime(contributionDate);
			Calendar nineOClock = (Calendar) c.clone();
			nineOClock.set(Calendar.HOUR_OF_DAY, 9);
			nineOClock.set(Calendar.MINUTE, 0);
			Calendar sixOClock = (Calendar) c.clone();
			sixOClock.set(Calendar.HOUR_OF_DAY, 18);
			sixOClock.set(Calendar.MINUTE, 0);
			if (c.before(nineOClock))
				continue;
			if (c.after(sixOClock))
				continue;

			daytimeContributions.add(obj);
		}

		return daytimeContributions;
	}

	/**
	 * Get the contributions this contributor made during night time (before
	 * 9.00 and after 18.00).
	 * 
	 * @return
	 */
	public Collection<OSMFeature> getNighttimeContributions() {
		Set<OSMFeature> nighttimeContributions = new HashSet<>();

		for (OSMFeature obj : contributions) {
			Date contributionDate = obj.getDate();
			Calendar c = new GregorianCalendar();
			c.setTime(contributionDate);
			Calendar nineOClock = (Calendar) c.clone();
			nineOClock.set(Calendar.HOUR_OF_DAY, 9);
			nineOClock.set(Calendar.MINUTE, 0);
			Calendar sixOClock = (Calendar) c.clone();
			sixOClock.set(Calendar.HOUR_OF_DAY, 18);
			sixOClock.set(Calendar.MINUTE, 0);
			if (c.before(nineOClock))
				nighttimeContributions.add(obj);
			else if (c.after(sixOClock))
				nighttimeContributions.add(obj);
		}

		return nighttimeContributions;
	}

	/**
	 * Group the given OSM contributions, i.e. OsmGeneObj instances by OSM user,
	 * instanciating {@link OSMContributor}.
	 * 
	 * @param contributions
	 * @return
	 */
	public static Collection<OSMContributor> findContributors(Collection<OSMDefaultFeature> contributions) {
		Map<Integer, OSMContributor> contributors = new HashMap<>();

		for (OSMDefaultFeature obj : contributions) {
			Integer userId = obj.getUid();
			if (contributors.keySet().contains(userId)) {
				OSMContributor contributor = contributors.get(userId);
				contributor.addContribution(obj);
			} else {
				// create a new contributor
				IFeatureCollection<OSMDefaultFeature> objs = new FT_FeatureCollection<>();
				objs.add(obj);
				OSMContributor newUser = new OSMContributor(objs, obj.getContributor(), obj.getUid());
				contributors.put(userId, newUser);
			}
		}

		return contributors.values();
	}

	/**
	 * Compute the centre of the contributions of the contributor.
	 * 
	 * @return
	 */
	public IPoint getContributionsCentre() {
		return this.getContributions().getCenter().toGM_Point();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OSMContributor other = (OSMContributor) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public Set<IFeature> getGeoObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGeoObjects(Set<IFeature> geoObjects) {
		// TODO Auto-generated method stub

	}

	@Override
	public IGraphLinkableFeature getGraphLinkableFeature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGraphLinkableFeature(IGraphLinkableFeature feature) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<IEdge> getEdgesIn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEdgesIn(Set<IEdge> edgesIn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addEdgeIn(IEdge edgeIn) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<IEdge> getEdgesOut() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEdgesOut(Set<IEdge> edgesOut) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addEdgeOut(IEdge edgeOut) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<IEdge> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDegree() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IPoint getGeom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGeom(IPoint geom) {
		// TODO Auto-generated method stub

	}

	@Override
	public IDirectPosition getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDirectPosition getPositionIni() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGraph getGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGraph(IGraph graph) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getProximityCentrality() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getBetweenCentrality() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<INode> getNextNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<INode, IEdge> getNeighbourEdgeNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public IFeatureCollection<DefaultFeature> getActivityAreas() {
		return activityAreas;
	}

	public int getNbContributions() {
		return nbContributions;
	}

	public int getNbWeekendContributions() {
		return nbWeekendContributions;
	}

	public int getNbWeekContributions() {
		return nbWeekContributions;
	}

	public int getNbDayTimeContributions() {
		return nbDayTimeContributions;
	}

	public int getNbNightTimeContributions() {
		return nbNightTimeContributions;
	}

	public int getNbCreation() {
		return nbCreation;
	}

	public int getNbModification() {
		return nbModification;
	}

	public int getNbDelete() {
		return nbDelete;
	}

	public int getNbRevert() {
		return nbRevert;
	}

	public int getNbNodeCreation() {
		return nbNodeCreation;
	}

	public int getNbNodeModification() {
		return nbNodeModification;
	}

	public int getNbNodeDelete() {
		return nbNodeDelete;
	}

	public int getNbNodeUpToDate() {
		return nbNodeUpToDate;
	}

	public int getNbNodeCorrected() {
		return nbNodeCorrected;
	}

	public int getNbNodeAutoCorrected() {
		return nbNodeAutoCorrected;
	}

	public int getNbWayCreation() {
		return nbWayCreation;
	}

	public int getNbWayDelete() {
		return nbWayDelete;
	}

	public int getNbWayModification() {
		return nbWayModification;
	}

	public int getNbWayUpToDate() {
		return nbWayUpToDate;
	}

	public int getNbWayCorrected() {
		return nbWayCorrected;
	}

	public int getNbWayAutoCorrected() {
		return nbWayAutoCorrected;
	}

	public int getNbRelationCreation() {
		return nbRelationCreation;
	}

	public int getNbRelationModification() {
		return nbRelationModification;
	}

	public int getNbRelationDelete() {
		return nbRelationDelete;
	}

	public int getNbRelationUpToDate() {
		return nbRelationUpToDate;
	}

	public int getNbRelationCorrected() {
		return nbRelationCorrected;
	}

	public int getNbRelationAutocorrected() {
		return nbRelationAutoCorrected;
	}

	public int getNbChangeset() {
		return nbChangeset;
	}

	public double getpChangeset() {
		return pChangeset;
	}

	public int getNbChgsetMapsMeAndroid() {
		return nbChgsetMapsMeAndroid;
	}

	public int getNbChgsetOther() {
		return nbChgsetOther;
	}

	public int getNbChgsetMapsMeIOS() {
		return nbChgsetMapsMeIOS;
	}

	public int getNbChgsetJosm() {
		return nbChgsetJosm;
	}

	public int getNbChgsetPotlatch() {
		return nbChgsetPotlatch;
	}

	public int getNbChgsetUnknown() {
		return nbChgsetUnknown;
	}

	public int getNbTotalChgst() {
		return nbTotalChgst;
	}

	public void setNbTotalChgst(int nbTotalChgst) {
		this.nbTotalChgst = nbTotalChgst;
	}

	public int getNbChgsetID() {
		return nbChgsetID;
	}

	public void setNbChgsetID(int nbChgsetID) {
		this.nbChgsetID = nbChgsetID;
	}

	public void setActivityAreas(IFeatureCollection<DefaultFeature> denseActivityCollection) {
		this.activityAreas = denseActivityCollection;
	}

}
