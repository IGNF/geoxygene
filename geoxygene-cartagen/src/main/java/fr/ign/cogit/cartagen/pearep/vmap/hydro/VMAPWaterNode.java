/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.hydro;

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
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.NoeudHydrographique;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.NoeudHydrographiqueImpl;

/*
 * ###### IGN / CartAGen ###### Title: WaterNode Description: Noeuds
 * hydrographiques Author: J. Renard Date: 18/09/2009
 */
@Access(AccessType.PROPERTY)
public class VMAPWaterNode extends GeneObjDefault implements IWaterNode,
    VMAPFeature {

  private NoeudHydrographique geoxObj;
  private Collection<INetworkSection> inSections, outSections;

  /**
   * Empty constructor used by EJB to load features from PostGIS
   */
  public VMAPWaterNode() {
    super();
  }

  /**
   * Constructor
   */
  public VMAPWaterNode(Noeud noeud) {
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
    this.geoxObj = new NoeudHydrographiqueImpl();
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

  public VMAPWaterNode(IPoint point) {
    this(new Noeud(point));
    this.geoxObj = new NoeudHydrographiqueImpl();
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

  @Override
  @Transient
  public double getWidth() {
    return GeneralisationLegend.RES_EAU_LARGEUR;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

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

  @Override
  @Transient
  public Collection<INetworkSection> getInSections() {
    return this.inSections;
  }

  @Override
  @Transient
  public Collection<INetworkSection> getOutSections() {
    return this.outSections;
  }

  private List<Integer> inSectionsIds = new ArrayList<Integer>();

  public void setInSectionsIds(List<Integer> inSectionsIds) {
    this.inSectionsIds = inSectionsIds;
  }

  @ElementCollection
  @CollectionTable(name = "WaterInSectionIds", joinColumns = @JoinColumn(name = "finalNode"))
  @Column(name = "inSectionsIds")
  @Access(AccessType.FIELD)
  @EncodedRelation(targetEntity = VMAPWaterLine.class, invClass = INetworkNode.class, methodName = "InSections", invMethodName = "FinalNode", nToM = false)
  public List<Integer> getInSectionsIds() {
    return this.inSectionsIds;
  }

  private List<Integer> outSectionsIds = new ArrayList<Integer>();

  public void setOutSectionsIds(List<Integer> outSectionsIds) {
    this.outSectionsIds = outSectionsIds;
  }

  @ElementCollection
  @CollectionTable(name = "WaterOutSectionIds", joinColumns = @JoinColumn(name = "initialNode"))
  @Column(name = "outSectionsIds")
  @EncodedRelation(targetEntity = VMAPWaterLine.class, invClass = INetworkNode.class, methodName = "OutSections", invMethodName = "InitialNode", nToM = false)
  public List<Integer> getOutSectionsIds() {
    return this.outSectionsIds;
  }

  @Override
  public Map<String, Object> getAttributeMap(VMAPFeature feat) {
    // TODO
    return null;
  }

  @Override
  public int getDegree() {
    return this.getInSections().size() + this.getOutSections().size();
  }

  @Override
  public SymbolShape getMaxWidthSymbol() {
    return null;
  }

  @Override
  public IDirectPosition getPosition() {
    return this.getGeom().getPosition();
  }

  @Override
  public int getSectionsMaxImportance() {
    return 0;
  }

  @Override
  public void setInSections(Collection<INetworkSection> inSections) {
    this.inSections = inSections;
  }

  @Override
  public void setOutSections(Collection<INetworkSection> outSections) {
    this.outSections = outSections;
  }

}
