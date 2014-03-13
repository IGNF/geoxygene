/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.railway;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.network.NetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayNode;
import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.NoeudFerre;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.ferre.NoeudFerreImpl;

/*
 * ###### IGN / CartAGen ###### Title: RailwayNode Description: Noeuds du réseau
 * ferré Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class RailwayNode extends NetworkNode implements IRailwayNode {

  private NoeudFerre geoxObj;

  /**
   * Constructor
   */
  public RailwayNode(Noeud noeud) {
    super(noeud);
    this.setGeom(noeud.getGeom());
    this.geoxObj = new NoeudFerreImpl();
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

  public RailwayNode(IPoint point) {
    super(new Noeud(point));
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public RailwayNode() {
    super();
  }

  @Override
  @Transient
  public double getWidth() {
    return GeneralisationLegend.RES_FER_LARGEUR;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
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
  @CollectionTable(name = "RailInSectionIds", joinColumns = @JoinColumn(name = "finalNode"))
  @Column(name = "inSectionsIds")
  @Access(AccessType.FIELD)
  @EncodedRelation(targetEntity = RailwayLine.class, invClass = INetworkNode.class, methodName = "InSections", invMethodName = "FinalNode", nToM = false)
  public List<Integer> getInSectionsIds() {
    return this.inSectionsIds;
  }

  private List<Integer> outSectionsIds = new ArrayList<Integer>();

  public void setOutSectionsIds(List<Integer> outSectionsIds) {
    this.outSectionsIds = outSectionsIds;
  }

  @ElementCollection
  @CollectionTable(name = "RailOutSectionIds", joinColumns = @JoinColumn(name = "initialNode"))
  @Column(name = "outSectionsIds")
  @EncodedRelation(targetEntity = RailwayLine.class, invClass = INetworkNode.class, methodName = "OutSections", invMethodName = "InitialNode", nToM = false)
  public List<Integer> getOutSectionsIds() {
    return this.outSectionsIds;
  }

}
