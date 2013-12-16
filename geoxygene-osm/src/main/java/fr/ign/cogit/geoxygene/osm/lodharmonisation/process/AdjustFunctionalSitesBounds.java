package fr.ign.cogit.geoxygene.osm.lodharmonisation.process;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.core.genericschema.FunctionalSite;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.osm.lodharmonisation.operation.AdjustBoundsToComponents;

public class AdjustFunctionalSitesBounds {

  private static Logger logger = Logger
      .getLogger(AdjustFunctionalSitesBounds.class.getName());

  private Set<LoDSpatialRelation> toInclude, toExclude;
  private Set<FunctionalSiteGroup> groups;

  public AdjustFunctionalSitesBounds() {
    super();
  }

  public AdjustFunctionalSitesBounds(Set<LoDSpatialRelation> toInclude,
      Set<LoDSpatialRelation> toExclude) {
    super();
    this.toInclude = toInclude;
    this.toExclude = toExclude;
  }

  /**
   * 
   * @return a set of the modified features.
   */
  public Set<IGeneObj> harmonise() {
    Set<IGeneObj> modifiedFeats = new HashSet<IGeneObj>();
    findGroups();
    for (FunctionalSiteGroup group : groups) {
      Set<IPolygon> componentsToInclude = new HashSet<IPolygon>();
      Set<IPolygon> componentsToExclude = new HashSet<IPolygon>();
      for (LoDSpatialRelation relation : group.getToInclude())
        componentsToInclude.add((IPolygon) relation.getFeature2()
            .getSymbolGeom());
      for (LoDSpatialRelation relation : group.getToExclude())
        componentsToExclude.add((IPolygon) relation.getFeature2()
            .getSymbolGeom());
      AdjustBoundsToComponents ope = new AdjustBoundsToComponents(
          group.getBounds(), componentsToInclude, componentsToExclude);
      IPolygon newGeom = ope.adjustBoundsTight();
      ((IGeneObj) group.getSite()).setGeom(newGeom);
      modifiedFeats.add((IGeneObj) group.getSite());
    }
    return modifiedFeats;
  }

  private void findGroups() {
    groups = new HashSet<AdjustFunctionalSitesBounds.FunctionalSiteGroup>();
    HashSet<FunctionalSite> treated = new HashSet<FunctionalSite>();
    for (LoDSpatialRelation instance : toInclude) {
      FunctionalSite site = (FunctionalSite) instance.getFeature1();
      IPolygon bounds = (IPolygon) instance.getFeature1().getGeom();
      if (treated.contains(site)) {
        FunctionalSiteGroup group = getGroupFromSite(site);
        group.getToInclude().add(instance);
        continue;
      }
      FunctionalSiteGroup group = new FunctionalSiteGroup(site, bounds);
      group.getToInclude().add(instance);
      groups.add(group);
      treated.add(site);
    }
    for (LoDSpatialRelation instance : toExclude) {
      FunctionalSite site = (FunctionalSite) instance.getFeature1();
      IPolygon bounds = (IPolygon) instance.getFeature1().getGeom();
      if (treated.contains(site)) {
        FunctionalSiteGroup group = getGroupFromSite(site);
        group.getToExclude().add(instance);
        continue;
      }
      FunctionalSiteGroup group = new FunctionalSiteGroup(site, bounds);
      group.getToExclude().add(instance);
      groups.add(group);
      treated.add(site);
    }
    logger.finest(groups.size() + " groups found");
  }

  private FunctionalSiteGroup getGroupFromSite(FunctionalSite site) {
    for (FunctionalSiteGroup group : groups) {
      if (group.getSite().equals(site))
        return group;
    }
    return null;
  }

  static class FunctionalSiteGroup {
    private FunctionalSite site;
    private Set<LoDSpatialRelation> toInclude, toExclude;
    private IPolygon bounds;

    FunctionalSiteGroup(FunctionalSite site, IPolygon bounds) {
      this.site = site;
      this.bounds = bounds;
      this.toInclude = new HashSet<LoDSpatialRelation>();
      this.toExclude = new HashSet<LoDSpatialRelation>();
    }

    public FunctionalSite getSite() {
      return site;
    }

    public void setSite(FunctionalSite site) {
      this.site = site;
    }

    public Set<LoDSpatialRelation> getToInclude() {
      return toInclude;
    }

    public void setToInclude(Set<LoDSpatialRelation> toInclude) {
      this.toInclude = toInclude;
    }

    public Set<LoDSpatialRelation> getToExclude() {
      return toExclude;
    }

    public void setToExclude(Set<LoDSpatialRelation> toExclude) {
      this.toExclude = toExclude;
    }

    public IPolygon getBounds() {
      return bounds;
    }

    public void setBounds(IPolygon bounds) {
      this.bounds = bounds;
    }

    @Override
    public int hashCode() {
      return site.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null)
        return false;
      if (!(obj instanceof FunctionalSiteGroup))
        return false;
      return site.equals(((FunctionalSiteGroup) obj).getSite());
    }

  }
}
