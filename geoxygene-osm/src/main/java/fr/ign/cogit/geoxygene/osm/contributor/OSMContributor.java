package fr.ign.cogit.geoxygene.osm.contributor;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.graphe.IEdge;
import fr.ign.cogit.geoxygene.contrib.graphe.IGraph;
import fr.ign.cogit.geoxygene.contrib.graphe.IGraphLinkableFeature;
import fr.ign.cogit.geoxygene.contrib.graphe.INode;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;

public class OSMContributor implements INode {

	private IFeatureCollection<OSMFeature> contributions;
	private String name;
	private int id;
	private int nbOfContributions;
	private int nbOfweekendContributions;
	private int nbOfweekContributions;
	private int nbOfDayTimeContributions;
	private int nbOfNightTimeContributions;
	private int nbOfCreatedObjects;
	private int nbOfDayRecord;
	private IGeometry activityAreas;

	public OSMContributor(IFeatureCollection<OSMFeature> contributions, String name, int id) {
		super();
		this.contributions = contributions;
		this.name = name;
		this.id = id;
	}

	public IFeatureCollection<OSMFeature> getContributions() {
		return contributions;
	}

	public void setContributions(IFeatureCollection<OSMFeature> contributions) {
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

	public void addContribution(OSMFeature contribution) {
		this.getContributions().add(contribution);
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

	public int getNbOfDayRecord() {
		return nbOfDayRecord;
	}

	public void setNbOfDayRecord(int nbOfDayRecord) {
		this.nbOfDayRecord = nbOfDayRecord;
	}

	public int getNbOfCreatedObjects() {
		int nbOfCreatedObjects = 0;
		for (OSMFeature obj : contributions) {
			if (obj.getVersion() == 1)
				nbOfCreatedObjects++;
		}
		return nbOfCreatedObjects;
	}

	public void setNbOfCreatedObjects(int nbOfCreatedObjects) {
		this.nbOfCreatedObjects = nbOfCreatedObjects;
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
	public static Collection<OSMContributor> findContributors(Collection<OSMFeature> contributions) {
		Map<Integer, OSMContributor> contributors = new HashMap<>();

		for (OSMFeature obj : contributions) {
			Integer userId = obj.getUid();
			if (contributors.keySet().contains(userId)) {
				OSMContributor contributor = contributors.get(userId);
				contributor.addContribution(obj);
			} else {
				// create a new contributor
				IFeatureCollection<OSMFeature> objs = new FT_FeatureCollection<>();
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

	public int getNbOfContributions() {
		return nbOfContributions;
	}

	public void setNbOfContributions(int nbOfContributions) {
		this.nbOfContributions = nbOfContributions;
	}

	public int getNbOfweekendContributions() {
		return nbOfweekendContributions;
	}

	public void setNbOfweekendContributions(int nbOfweekendContributions) {
		this.nbOfweekendContributions = nbOfweekendContributions;
	}

	public int getNbOfweekContributions() {
		return nbOfweekContributions;
	}

	public void setNbOfweekContributions(int nbOfweekContributions) {
		this.nbOfweekContributions = nbOfweekContributions;
	}

	public int getNbOfDayTimeContributions() {
		return nbOfDayTimeContributions;
	}

	public void setNbOfDayTimeContributions(int nbOfDayTimeContributions) {
		this.nbOfDayTimeContributions = nbOfDayTimeContributions;
	}

	public int getNbOfNightTimeContributions() {
		return nbOfNightTimeContributions;
	}

	public void setNbOfNightTimeContributions(int nbOfNightTimeContributions) {
		this.nbOfNightTimeContributions = nbOfNightTimeContributions;
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

	public IGeometry getActivityAreas() {
		return activityAreas;
	}

	public void setActivityAreas(IGeometry activityAreas) {
		this.activityAreas = activityAreas;
	}

}
