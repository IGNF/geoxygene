/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.SymbolShape;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.NoeudRoutierImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

/*
 * ###### IGN / CartAGen ###### Title: RoadNode Description: Noeuds routiers
 * Author: J. Renard Date: 18/09/2009
 */
public class MGCPRoadNode extends GeneObjDefault implements IRoadNode,
    MGCPFeature {
  @Transient
  private NoeudRoutier geoxObj;
  private Collection<INetworkSection> inSections, outSections;

  /**
   * Constructor
   */
  public MGCPRoadNode(Noeud noeud) {

    // Topology links for entering network sections
    this.inSections = new HashSet<INetworkSection>();
    for (Arc arc : noeud.getEntrants()) {
      IFeature feat = arc.getCorrespondant(0);
      if (!(feat instanceof INetworkSection)) {
        continue;
      }
      INetworkSection section = (INetworkSection) feat;
      this.inSections.add(section);
      section.setFinalNode(this);
    }

    // Topology links for exiting network sections
    this.outSections = new HashSet<INetworkSection>();
    for (Arc arc : noeud.getSortants()) {
      IFeature feat = arc.getCorrespondant(0);
      if (!(feat instanceof INetworkSection)) {
        continue;
      }
      INetworkSection section = (INetworkSection) feat;
      this.outSections.add(section);
      section.setInitialNode(this);
    }

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

  public MGCPRoadNode(IPoint point) {
    this(new Noeud(point));
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
  public MGCPRoadNode(NoeudRoutier geoxObj) {
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
  public MGCPRoadNode() {
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
  public Collection<INetworkSection> getInSections() {
    return this.inSections;
  }

  @Override
  public void setInSections(Collection<INetworkSection> inSections) {
    this.inSections = inSections;
  }

  @Override
  public Collection<INetworkSection> getOutSections() {
    return this.outSections;
  }

  @Override
  public void setOutSections(Collection<INetworkSection> outSections) {
    this.outSections = outSections;
  }

  @Override
  @Transient
  public NoeudRoutier getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
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
  @EncodedRelation(targetEntity = MGCPRoadLine.class, invClass = INetworkNode.class, methodName = "InSections", invMethodName = "FinalNode", nToM = false)
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
  @EncodedRelation(targetEntity = MGCPRoadLine.class, invClass = INetworkNode.class, methodName = "OutSections", invMethodName = "InitialNode", nToM = false)
  public List<Integer> getOutSectionsIds() {
    return this.outSectionsIds;
  }

  @Override
  public void restoreGeoxObjects() {
    this.geoxObj = new NoeudRoutierImpl(new ReseauImpl(), this.getGeom());
  }

  @Override
  public int getDegree() {
    int nb = 0;
    for (INetworkSection tr : this.inSections) {
      if (!tr.isDeleted()) {
        nb++;
      }
    }
    for (INetworkSection tr : this.outSections) {
      if (!tr.isDeleted()) {
        nb++;
      }
    }
    return nb;
  }

  @Override
  public SymbolShape getMaxWidthSymbol() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDirectPosition getPosition() {
    return this.getGeom().coord().get(0);
  }

  @Override
  public int getSectionsMaxImportance() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Map<String, Object> getAttributeMap(MGCPFeature feat) {
    // TODO Auto-generated method stub
    return null;
  }

}
