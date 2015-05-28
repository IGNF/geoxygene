/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema;

import java.util.Set;

import fr.ign.cogit.cartagen.graph.IGraphLinkableFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Root interface describing the requirements for geographic objects handled by
 * the different generalisation modules of CartAGen. Other interfaces extend
 * this one, more specific to given types of objects (depending on their
 * geometry type and thematic).The objects handled within CartAGen belong to
 * classes that implement these more specific interfaces.
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */
public interface IGeneObj extends IGraphLinkableFeature, IFeature,
    IPersistentObject {

  /**
   * @return TRUE if the object has to be considered eliminated by the
   *         generalisation process, FALSE otherwise
   */
  boolean isEliminated();

  /**
   * @return FALSE if the object is an object of the original data, TRUE if it
   *         has been created during the generalisation process
   */
  boolean hasBeenCreated();

  void setBeenCreated(boolean created);

  /**
   * In the case where isEliminated = FALSE
   * @return True if <b>this</b> has been created by a n-1 transformation
   *         (aggregation), False otherwise
   */
  boolean isStemmingFromN1Transfo();

  /**
   * In the case where isEliminated = FALSE
   * @return True if <b>this</b> has been created by a m-n transformation
   *         (typification), False otherwise
   */
  boolean isStemmingFromMNTransfo();

  /**
   * @return the geometry that <b>this</b> is associated to
   */
  @Override
  IGeometry getGeom();

  /**
   * @return the geometry of the symbol that <b>this</b> is associated to
   */
  @Override
  IGeometry getSymbolGeom();

  /**
   * @return the area of the symbol geometry that <b>this</b> is associated to
   */
  @Override
  double getSymbolArea();

  /**
   * Modifies the geometry of that <b>this</b> is associated to.
   */
  @Override
  void setGeom(IGeometry geom);

  /**
   * @return the initial geometry of <b>this</b>
   */
  IGeometry getInitialGeom();

  /**
   * Modifies the initial geometry of <b>this</b>
   */
  void setInitialGeom(IGeometry geom);

  /**
   * Eliminate <b>this</b> in a proper manner.
   */
  void eliminate();

  /**
   * Cancel a previous elimination of <b>this</b> in a proper manner.
   */
  void cancelElimination();

  /**
   * The possible graphn linkable feature attached to the geneobj
   */
  public IGraphLinkableFeature getLinkableFeature();

  public void setLinkableFeature(IGraphLinkableFeature linkableFeature);

  /**
   * Getter for antecedents.
   * @return the antecedents. It can be empty but not {@code null}.
   * 
   */
  public Set<IGeneObj> getAntecedents();

  /**
   * 
   * {@code this.setAntecedents(new HashSet<IGeneObj>())}
   * @param antecedents the set of antecedents to set
   */
  public void setAntecedents(Set<IGeneObj> antecedents);

  /**
   * Adds a IGeneObj to antecedents IGeneObj to {@code this}.
   * @param antecedent the antecedent to remove
   */
  public void addAntecedent(IGeneObj antecedent);

  /**
   * Removes a IGeneObj from antecedents
   * @param antecedent the antecedent to remove
   */
  public void removeAntecedent(IGeneObj antecedent);

  // /////////////////////////////////////////////
  // Attribute resultingObjects and getter/setter
  // /////////////////////////////////////////////

  /**
   * Getter for resultingObjects.
   * @return the resultingObjects. It can be empty but not {@code null}.
   */
  public Set<IGeneObj> getResultingObjects();

  /*
   * 
   * In the case where isEliminated = TRUE: if this elimination is because
   * <b>this</b> has been aggregated with another CartAGen object (or has been
   * part of a typification), this method returns the resulting object(s). The
   * returned set contains 1 object if the performed operation is an aggregation
   * (n-1), several objects if the performed operation is a typification (m-n).
   * 
   * @return HashSet of CartAGen objects having been generated from <b>this</b>
   * by generalisation
   */
  /**
   * Setter for resultingObjects. Also updates the reverse reference from each
   * element of resultingObjects to {@code this}. To break the reference use
   * {@code this.setResultingObjects(new HashSet<IGeneObj>())}
   * @param resultingObjects the set of resultingObjects to set
   */
  public void setResultingObjects(Set<IGeneObj> resultingObjects);

  /**
   * Adds a IGeneObj to resultingObjects, and updates the reverse reference from
   * the added IGeneObj to {@code this}.
   * @param resultingObject the resultingObject to add
   */
  public void addResultingObject(IGeneObj resultingObject);

  /**
   * Removes a IGeneObj from resultingObjects, and updates the reverse reference
   * from the removed IGeneObj by removing {@code this}.
   * @param resultingObject the resultingObject to remove
   */
  public void removeResultingObject(IGeneObj resultingObject);

  /**
   * @return the GeoXygene object that <b>this</b> is associated to
   */
  IFeature getGeoxObj();

  /**
   * @return the artifacts of the Generalisation object into the agent world
   */
  Set<Object> getGeneArtifacts();

  /***
   * adds an artifact from the agent world on the generalisation object
   * @param artifact
   */
  void addToGeneArtifacts(Object artifact);

  /***
   * removes an artifact from the agent world on the generalisation object
   * @param artifact
   */
  void removeFromGeneArtifacts(Object artifact);

  /**
   * @return the Id of the symbol associated to this object
   */
  int getSymbolId();

  /**
   * Set the Id of the symbol associated to this object
   * @param symbolId the symbol Id to set
   */
  void setSymbolId(int symbolId);

  /**
   * Displaces the GeneObj and register the displacement
   */
  public void displaceAndRegister(double dx, double dy);

  /**
   * registers the displacement of the GeneObj if persistence is necessary
   */
  public void registerDisplacement();

  public static final String FEAT_TYPE_NAME = "";

  /**
   * Useful for storing a persistent identifier on the objects with shapefiles.
   * Do not use for other purposes.
   */
  public int getShapeId();

  public void setShapeId(int id);

  /**
   * Get the name of the CartAGenDB this IGeneObj is part of. Useful to import
   * from PostGIS into the appropriate DB but DO NOT use it to get the DB
   * {@code this} belongs.
   */
  public String getDbName();

  /**
   * Copy all the attributes of obj in {@code this} if they belong to the same
   * class.
   * @param obj
   */
  public void copyAttributes(IGeneObj obj);

  /**
   * get the SupportObj which is assciated
   */
  // public BDSupportObj getBDSupportObj();

  // public void setBDSupportObj(BDSupportObj bdSupportObj);

}
