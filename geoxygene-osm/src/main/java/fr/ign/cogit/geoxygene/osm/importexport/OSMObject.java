package fr.ign.cogit.geoxygene.osm.importexport;

import java.util.Date;
import java.util.List;

public class OSMObject {

	// private List<OSMFeature> contributions;
	private List<OSMResource> contributions;
	// IFeatureCollection<IFeature> test = new FT_FeatureCollection<IFeature>();
	private long osmId;
	public int nbVersions;
	public int nbContributors;
	private Date dateMin;
	private Date dateMax;
	private int nbStableTags;
	private int nbTagEdition;
	private int nbGeomEdition;

	public long getOsmId() {
		return osmId;
	}

	// public void setNbVersions(int version){
	// this.nbVersions = version;
	// }
	public int getNbVersions() {
		return nbVersions;
	}

	public int getNbContributors() {
		return nbContributors;
	}

	public void setNbContributors(int nbContributors) {
		this.nbContributors = nbContributors;
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
	}

	public OSMObject(long id, int nbVersions, int nbContributors, Date creationDate, Date suppressionDate,
			int nbStableTags, int nbTagEdition, int nbGeomEdition) {
		this.osmId = id;
		this.nbVersions = nbVersions;
		this.nbContributors = nbContributors;
		this.dateMin = creationDate;
		this.dateMax = suppressionDate;
		this.nbStableTags = nbStableTags;
		this.nbTagEdition = nbTagEdition;
		this.nbGeomEdition = nbGeomEdition;
	}

}