package fr.ign.cogit.geoxygene.osm.importexport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OSMObject {

	// private List<OSMFeature> contributions;
	private List<OSMResource> contributions;
	// IFeatureCollection<IFeature> test = new FT_FeatureCollection<IFeature>();
	private long osmId;
	public int nbVersions;
	public int nbContributors;
	private Set<Long> contributorList;
	private Date dateMin;
	private Date dateMax;
	private int nbStableTags;
	private int nbTagEdition;
	private int nbGeomEdition;
	public List<List<Long>> wayComposition;

	public long getOsmId() {
		return osmId;
	}

	// public void setNbVersions(int version){
	// this.nbVersions = version;
	// }
	public int getNbVersions() {
		return nbVersions;
	}

	public void addContributor(Long uid) {
		this.contributorList.add(uid);
	}

	public Set<Long> getContributorList() {
		return contributorList;
	}

	public void setContributorList(Set<Long> contributorList) {
		this.contributorList = contributorList;
	}

	public int getNbContributors() {
		return nbContributors;
	}

	public void setNbContributors(int nbContributors) {
		this.nbContributors = nbContributors;
	}

	public void addcontribution(OSMResource resource) {
		this.getContributions().add(resource);

	}

	public void setDateMin(Date datemin) {
		this.dateMin = datemin;
	}

	public Date getDateMin() {
		return dateMin;
	}

	public void setDateMax(Date datemax) {
		this.dateMax = datemax;
	}

	public Date getDateMax() {
		return dateMax;
	}

	public void setNbStableTags(int nbStableTags) {
		this.nbStableTags = nbStableTags;
	}

	public int getNbStableTags() {
		return nbStableTags;
	}

	public void setNbTagEdition(int nbTagEdition) {
		this.nbTagEdition = nbTagEdition;
	}

	public int getNbTagEdition() {
		return nbTagEdition;
	}

	public void setNbGeomEdition(int nbEdition) {
		this.nbGeomEdition = nbEdition;
	}

	public int getNbGeomEdition() {
		return nbGeomEdition;
	}

	public void setContributions(List<OSMResource> contribution) {
		this.contributions = contribution;
	}

	public List<OSMResource> getContributions() {
		return contributions;
	}

	public OSMObject(long id) {
		this.osmId = id;
		this.contributorList = new HashSet<Long>();
		this.contributions = new ArrayList<OSMResource>();
	}

	public OSMObject(long id, int nbVersions, int nbContributors, Date creationDate, Date suppressionDate,
			int nbStableTags, int nbTagEdition, int nbGeomEdition) {
		this.osmId = id;
		this.nbVersions = nbVersions;
		this.contributorList = new HashSet<Long>();
		this.nbContributors = nbContributors;
		this.contributions = new ArrayList<OSMResource>();
		this.dateMin = creationDate;
		this.dateMax = suppressionDate;
		this.nbStableTags = nbStableTags;
		this.nbTagEdition = nbTagEdition;
		this.nbGeomEdition = nbGeomEdition;
	}

}