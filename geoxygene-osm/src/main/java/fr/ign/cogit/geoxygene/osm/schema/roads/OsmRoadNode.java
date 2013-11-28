package fr.ign.cogit.geoxygene.osm.schema.roads;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.road.RoadLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.osm.schema.network.OsmNetworkNode;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.NoeudRoutierImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

/*
 * ###### IGN / CartAGen ###### Title: RoadNode Description: Noeuds routiers
 * Author: J. Renard Date: 18/09/2009
 */
public class OsmRoadNode extends OsmNetworkNode implements IRoadNode {
  @Transient
  private NoeudRoutier geoxObj;

  /**
   * Constructor
   */
  public OsmRoadNode(Noeud noeud) {
    super(noeud);
    this.setGeom(noeud.getGeom());
    this.geoxObj = new NoeudRoutierImpl();
    this.geoxObj.setGeom(noeud.getGeom());
    for (INetworkSection section : this.getInSections()) {
      // links creation for GeOx objects
      this.geoxObj.getArcsEntrants().add((ArcReseau) section.getGeoxObj());
      ((ArcReseau) section.getGeoxObj()).setNoeudFinal(this.geoxObj);
    }
    for (INetworkSection section : this.getOutSections()) {
      // links creation for GeOx objects
      this.geoxObj.getArcsSortants().add((ArcReseau) section.getGeoxObj());
      ((ArcReseau) section.getGeoxObj()).setNoeudInitial(this.geoxObj);
    }
  }

  public OsmRoadNode(IPoint point) {
    super(new Noeud(point));
    this.geoxObj = new NoeudRoutierImpl();
    this.geoxObj.setGeom(point);
    for (INetworkSection section : this.getInSections()) {
      // links creation for GeOx objects
      this.geoxObj.getArcsEntrants().add((ArcReseau) section.getGeoxObj());
      ((ArcReseau) section.getGeoxObj()).setNoeudFinal(this.geoxObj);
    }
    for (INetworkSection section : this.getOutSections()) {
      // links creation for GeOx objects
      this.geoxObj.getArcsSortants().add((ArcReseau) section.getGeoxObj());
      ((ArcReseau) section.getGeoxObj()).setNoeudInitial(this.geoxObj);
    }
  }

  /**
   * Constructor
   */
  public OsmRoadNode(NoeudRoutier geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.setInSections(new HashSet<INetworkSection>());
    this.setOutSections(new HashSet<INetworkSection>());
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public OsmRoadNode() {
    super();
  }

  @Override
  @Transient
  public double getWidth() {
    if (this == null) {
      return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0;
    }
    int maxImportanceTroncons = this.getSectionsMaxImportance();
    if (maxImportanceTroncons == 0) {
      return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0;
    }
    if (maxImportanceTroncons == 1) {
      return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_1;
    }
    if (maxImportanceTroncons == 2) {
      return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_2;
    }
    if (maxImportanceTroncons == 3) {
      return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_3;
    }
    if (maxImportanceTroncons == 4) {
      return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_4;
    }
    return 0.0;
  }

  @Override
  @Transient
  public NoeudRoutier getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPoint getGeom() {
    return super.getGeom();
  }

  /**
   * 
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  @Column(name = "CartAGenDB_name")
  public String getDbName() {
    return super.getDbName();
  }

  @Override
  @Id
  public int getId() {
    return super.getId();
  }

  @Override
  public int getSymbolId() {
    return super.getSymbolId();
  }

  @Override
  public boolean isEliminated() {
    return super.isEliminated();
  }

  // ///////////////////////////////////////
  // HIBERNATE RELATED FIELDS & METHODS //
  // ///////////////////////////////////////

  private List<Integer> inSectionsIds = new ArrayList<Integer>();

  public void setInSectionsIds(List<Integer> inSectionsIds) {
    this.inSectionsIds = inSectionsIds;
  }

  @ElementCollection
  @CollectionTable(name = "RoadInSectionIds", joinColumns = @JoinColumn(name = "finalNode"))
  @Column(name = "inSectionsIds")
  @Access(AccessType.FIELD)
  @EncodedRelation(targetEntity = RoadLine.class, invClass = INetworkNode.class, methodName = "InSections", invMethodName = "FinalNode", nToM = false)
  public List<Integer> getInSectionsIds() {
    return this.inSectionsIds;
  }

  private List<Integer> outSectionsIds = new ArrayList<Integer>();

  public void setOutSectionsIds(List<Integer> outSectionsIds) {
    this.outSectionsIds = outSectionsIds;
  }

  @ElementCollection
  @CollectionTable(name = "RoadOutSectionIds", joinColumns = @JoinColumn(name = "initialNode"))
  @Column(name = "outSectionsIds")
  @EncodedRelation(targetEntity = RoadLine.class, invClass = INetworkNode.class, methodName = "OutSections", invMethodName = "InitialNode", nToM = false)
  public List<Integer> getOutSectionsIds() {
    return this.outSectionsIds;
  }

  @Override
  public void restoreGeoxObjects() {
    this.geoxObj = new NoeudRoutierImpl(new ReseauImpl(), this.getGeom());
  }

}
