/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.urban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.operation.buffer.BufferParameters;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.core.persistence.Encoded1To1Relation;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.AutreConstructionImpl;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/*
 * ###### IGN / CartAGen ###### Title: BuildingAlignment Description: Autres
 * alignements de batiments Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class UrbanAlignment extends GeneObjSurfDefault implements
    IUrbanAlignment {

  /**
   * Associated Geoxygene schema object
   */
  @Transient
  private AbstractFeature geoxObj;

  /**
   * The urban elements of the block
   */
  @Transient
  private List<IUrbanElement> urbanElements;

  /**
   * The geometric curve drawing the inner shape of the alignment
   */
  private ILineString shapeLine;

  /**
   * The initial geometric curve drawing the inner shape of the alignment
   */
  private ILineString initialShapeLine;

  /**
   * The initial and final elements of the alignment
   */
  @Transient
  private IUrbanElement initialElement, finalElement;

  /**
   * Constructor
   */

  public UrbanAlignment() {
    super();
    this.geoxObj = new AutreConstructionImpl(new GM_Polygon());
    this.setInitialGeom(new GM_Polygon());
    this.urbanElements = new ArrayList<IUrbanElement>();
    this.setEliminated(false);
  }

  public UrbanAlignment(List<IUrbanElement> urbanElements,
      ILineString shapeLine, IUrbanElement initialElement,
      IUrbanElement finalElement) {
    super();
    this.urbanElements = urbanElements;
    this.geoxObj = new AutreConstructionImpl(new GM_Polygon());
    this.shapeLine = shapeLine;
    this.initialShapeLine = (ILineString) shapeLine.clone();
    this.initialElement = initialElement;
    this.finalElement = finalElement;
    this.setEliminated(false);
  }

  public UrbanAlignment(List<IUrbanElement> urbanElements) {

    // liaison avec les micros
    this.setUrbanElements(urbanElements);

    // Computation of the characteristics
    this.computeInitialAndFinalElements();
    this.computeShapeLine();
    this.setInitialShapeLine((ILineString) this.getShapeLine().clone());

    // Ajout à la couche de données
    CartAGenDocOld.getInstance().getCurrentDataset().getUrbanAlignments()
        .add(this);

  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Transient
  public IPolygon getGeom() {
    if (this.shapeLine == null) {
      return new GM_Polygon();
    }
    double distBuffer = Math
        .sqrt(GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT)
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    return (IPolygon) this.shapeLine.buffer(distBuffer / 2.0, 2,
        BufferParameters.CAP_SQUARE, BufferParameters.CAP_SQUARE);
  }

  @Override
  @Transient
  public IPolygon getInitialGeom() {
    if (this.initialShapeLine == null) {
      return new GM_Polygon();
    }
    double distBuffer = Math
        .sqrt(GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT)
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    return (IPolygon) this.initialShapeLine.buffer(distBuffer / 2.0, 2,
        BufferParameters.CAP_SQUARE, BufferParameters.CAP_SQUARE);
  }

  @Override
  @Transient
  public List<IUrbanElement> getUrbanElements() {
    return this.urbanElements;
  }

  @Override
  public void setUrbanElements(List<IUrbanElement> urbanElements) {
    this.urbanElements = urbanElements;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public ILineString getShapeLine() {
    return this.shapeLine;
  }

  @Override
  public void setShapeLine(ILineString shapeLine) {
    this.shapeLine = shapeLine;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public ILineString getInitialShapeLine() {
    return this.initialShapeLine;
  }

  @Override
  public void setInitialShapeLine(ILineString shapeLine) {
    this.initialShapeLine = shapeLine;
  }

  @Override
  @Transient
  public IUrbanElement getInitialElement() {
    return this.initialElement;
  }

  @Override
  public void setInitialElement(IUrbanElement initialElement) {
    this.initialElement = initialElement;
  }

  @Override
  @Transient
  public IUrbanElement getFinalElement() {
    return this.finalElement;
  }

  @Override
  public void setFinalElement(IUrbanElement finalElement) {
    this.finalElement = finalElement;
  }

  /**
   * determines the initial and final urban elements of the alignment
   */
  @Override
  public void computeInitialAndFinalElements() {

    double distMaxTot1 = 0.0;
    double distMaxTot2 = 0.0;
    IUrbanElement build1 = this.urbanElements.get(0);
    IUrbanElement build2 = this.urbanElements
        .get(this.urbanElements.size() - 1);

    for (IUrbanElement currentAgent : this.urbanElements) {

      double distMin1 = Double.MAX_VALUE;
      double distMin2 = Double.MAX_VALUE;
      for (IUrbanElement building : this.urbanElements) {
        if (building.equals(currentAgent)) {
          continue;
        }
        double dist = currentAgent.getGeom().distance(building.getGeom());
        if (dist < distMin1) {
          distMin2 = distMin1;
          distMin1 = dist;
          continue;
        } else if (dist < distMin2) {
          distMin2 = dist;
        }
      }

      if (distMin2 > distMaxTot1) {
        distMaxTot2 = distMaxTot1;
        build2 = build1;
        distMaxTot1 = distMin2;
        build1 = currentAgent;
        continue;
      } else if (distMin2 > distMaxTot2) {
        distMaxTot2 = distMin2;
        build2 = currentAgent;
      }

    }

    this.setInitialElement(build1);
    this.setFinalElement(build2);

  }

  /**
   * computes the shape line of the alignment based on its buildings
   */
  @Override
  public void computeShapeLine() {

    HashMap<IUrbanElement, Boolean> treatedUrbanElements = new HashMap<IUrbanElement, Boolean>();
    treatedUrbanElements.put(this.initialElement, Boolean.valueOf(true));
    treatedUrbanElements.put(this.finalElement, Boolean.valueOf(true));
    for (IUrbanElement build : this.urbanElements) {
      if (build.isDeleted()) {
        continue;
      }
      treatedUrbanElements.put(build, Boolean.valueOf(true));
    }
    List<IUrbanElement> orderedComponents = new ArrayList<IUrbanElement>();

    // Initialisation of the shape line
    ILineString line = new GM_LineString();
    line.coord().add(this.initialElement.getGeom().centroid());
    treatedUrbanElements.put(this.initialElement, Boolean.valueOf(false));
    orderedComponents.add(this.initialElement);
    IUrbanElement currentUrbanElement = this.initialElement;

    // Addition of the centroid coordinates one by one
    for (int i = 0; i < this.urbanElements.size() - 2; i++) {
      double distMin = Double.MAX_VALUE;
      IUrbanElement nearestBuilding = currentUrbanElement;
      for (IUrbanElement build : treatedUrbanElements.keySet()) {
        if (treatedUrbanElements.get(build) == Boolean.valueOf(false)) {
          continue;
        }
        double dist = build.getGeom().distance(currentUrbanElement.getGeom());
        if (dist < distMin) {
          distMin = dist;
          nearestBuilding = build;
        }
      }
      line.coord().add(nearestBuilding.getGeom().centroid());
      treatedUrbanElements.put(nearestBuilding, Boolean.valueOf(false));
      orderedComponents.add(nearestBuilding);
      currentUrbanElement = nearestBuilding;
      if (nearestBuilding.equals(this.finalElement)) {
        break;
      }
    }

    // Smoothing and affectation of the shape line
    line.coord().add(this.finalElement.getGeom().centroid());
    orderedComponents.add(this.finalElement);
    ILineString filteredLine = GaussianFilter.gaussianFilter(line, 500.0, 1.0);
    this.setShapeLine(filteredLine);
    this.setUrbanElements(orderedComponents);

    // Test if it is a straight alignment in order to maket the shape line
    // straight
    ILineSegment seg = new GM_LineSegment(this.getInitialElement().getGeom()
        .centroid(), this.getFinalElement().getGeom().centroid());
    if (this.getGeom().contains(seg)) {
      this.setShapeLine(seg);
    }

  }

  /**
   * completely destroys the alignment, deleting all its inner buildings
   */
  @Override
  public void destroy() {
    this.eliminate();
    for (IUrbanElement build : this.getUrbanElements()) {
      build.eliminate();
    }
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

  private int initialElementId;

  public void setInitialElementId(int initialElementId) {
    this.initialElementId = initialElementId;
  }

  @Encoded1To1Relation(targetEntity = Building.class, inverse = false, methodName = "InitialElement", invClass = IUrbanElement.class)
  public int getInitialElementId() {
    return this.initialElementId;
  }

  private int finalElementId;

  public void setFinalElementId(int finalElementId) {
    this.finalElementId = finalElementId;
  }

  @Encoded1To1Relation(targetEntity = Building.class, inverse = false, methodName = "FinalElement", invClass = IUrbanElement.class)
  public int getFinalElementId() {
    return this.finalElementId;
  }

}
