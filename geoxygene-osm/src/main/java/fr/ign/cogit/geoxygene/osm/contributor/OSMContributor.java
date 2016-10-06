package fr.ign.cogit.geoxygene.osm.contributor;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;

public class OSMContributor {

  private IFeatureCollection<OsmGeneObj> contributions;
  private String name;
  private int id;

  public OSMContributor(IFeatureCollection<OsmGeneObj> contributions,
      String name, int id) {
    super();
    this.contributions = contributions;
    this.name = name;
    this.id = id;
  }

  public IFeatureCollection<OsmGeneObj> getContributions() {
    return contributions;
  }

  public void setContributions(IFeatureCollection<OsmGeneObj> contributions) {
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

  public void addContribution(OsmGeneObj contribution) {
    this.getContributions().add(contribution);
  }

  public Collection<OsmGeneObj> getWeekEndContributions() {
    Set<OsmGeneObj> weekEndContributions = new HashSet<>();

    for (OsmGeneObj obj : contributions) {
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

  public Collection<OsmGeneObj> getWeekContributions() {
    Set<OsmGeneObj> weekContributions = new HashSet<>();

    for (OsmGeneObj obj : contributions) {
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
   * @return
   */
  public Collection<OsmGeneObj> getDaytimeContributions() {
    Set<OsmGeneObj> daytimeContributions = new HashSet<>();

    for (OsmGeneObj obj : contributions) {
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
   * Get the contributions this contributor made during night time (before 9.00
   * and after 18.00).
   * @return
   */
  public Collection<OsmGeneObj> getNighttimeContributions() {
    Set<OsmGeneObj> nighttimeContributions = new HashSet<>();

    for (OsmGeneObj obj : contributions) {
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
   * @param contributions
   * @return
   */
  public static Collection<OSMContributor> findContributors(
      Collection<OsmGeneObj> contributions) {
    Map<Integer, OSMContributor> contributors = new HashMap<>();

    for (OsmGeneObj obj : contributions) {
      Integer userId = obj.getUid();
      if (contributors.keySet().contains(userId)) {
        OSMContributor contributor = contributors.get(userId);
        contributor.addContribution(obj);
      } else {
        // create a new contributor
        IFeatureCollection<OsmGeneObj> objs = new FT_FeatureCollection<>();
        objs.add(obj);
        OSMContributor newUser = new OSMContributor(objs, obj.getContributor(),
            obj.getUid());
        contributors.put(userId, newUser);
      }
    }

    return contributors.values();
  }

  /**
   * Compute the centre of the contributions of the contributor.
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

}
