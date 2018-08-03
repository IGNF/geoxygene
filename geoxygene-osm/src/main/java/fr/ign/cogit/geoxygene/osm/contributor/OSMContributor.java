package fr.ign.cogit.geoxygene.osm.contributor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.threeten.extra.Interval;

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
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.OSMResourceComparator;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.OSMResourceQualityAssessment;
import fr.ign.cogit.geoxygene.osm.schema.OSMDefaultFeature;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;

public class OSMContributor implements INode {

	private IFeatureCollection<OSMDefaultFeature> contributions;
	private Set<OSMResource> resource = new HashSet<OSMResource>();
	private String name;
	private int id;
	// private int nbContributions = 0;

	private IGeometry activityAreas;
	private List<Interval> changesetDates;
	private Boolean[] temporalActivityPerWeek;

	public OSMContributor(IFeatureCollection<OSMDefaultFeature> contributions, String name, int id) {
		super();
		this.contributions = contributions;
		this.name = name;
		this.id = id;
		// this.setNbTotalChgst(getNbChgsetTotal(this.id));
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

	public Set<OSMResource> getResource() {
		return this.resource;
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

	public IGeometry getActivityAreas() {
		return activityAreas;
	}

	public int getNbContributions() {
		return this.resource.size();
	}

	public void setActivityAreas(IGeometry denseActivityCollection) {
		this.activityAreas = denseActivityCollection;
	}

	public int getNbDayTimeContributions() {
		return this.getDaytimeContributions().size();
	}

	public int getNbNightTimeContributions() {
		return this.getNbContributions() - this.getNbDayTimeContributions();
	}

	public int getNbWeekContributions() {
		return this.getWeekContributions().size();
	}

	public int getNbWeekendContributions() {
		// TODO Auto-generated method stub
		return this.getNbContributions() - this.getNbWeekContributions();
	}

	public List<Interval> getChangesetDates() {
		return changesetDates;
	}

	public void setChangesetDates(List<Interval> changesetDates) {
		this.changesetDates = changesetDates;
	}

	public Boolean[] getTemporalActivityPerWeek() {
		return temporalActivityPerWeek;
	}

	public void setTemporalActivityPerWeek(Boolean[] temporalActivityPerWeek) {
		this.temporalActivityPerWeek = temporalActivityPerWeek;
	}

	@SuppressWarnings("finally")
	public int getNbWeeksActivity() {
		int nbWeeks = 0;
		// Les resources doivent être dans l'ordre chornologiques
		List<OSMResource> resourceList = new ArrayList<OSMResource>(this.resource);
		Collections.sort(resourceList, new OSMResourceComparator());
		Date firstContributionDate = (resourceList.get(0).getDate());
		Date lastContributionDate = (resourceList.get(this.resource.size() - 1)).getDate();
		Calendar c1 = new GregorianCalendar();
		Calendar c2 = new GregorianCalendar();

		c1.setTime(firstContributionDate);
		c2.setTime(lastContributionDate);

		for (int year = c1.get(Calendar.YEAR); year <= c2.get(Calendar.YEAR); year++) {
			Set<OSMResource> resourceByYear = new HashSet<OSMResource>();
			for (OSMResource r : this.resource) {
				Date contributionDate = r.getDate();
				Calendar c = new GregorianCalendar();
				c.setTime(contributionDate);
				if (c.get(Calendar.YEAR) == year)
					resourceByYear.add(r);
			}
			nbWeeks += OSMResourceQualityAssessment.countWeeks(resourceByYear);
		}

		try {
			System.out.println(1 / nbWeeks);
		} catch (ArithmeticException e) {
			System.out.println("Division par zero : " + e.getMessage());
			System.out.println("*** Date de chaque OSMResource ***");
			for (OSMResource r : this.resource) {
				System.out.println(r.getDate().toString());
			}
		} finally {
			return nbWeeks;
		}

	}

}
