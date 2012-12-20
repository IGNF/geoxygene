/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.urban;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.CityAxis;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.CityPartition;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public interface IUrbanBlock extends IGeneObjSurf {

  /**
   * Get the town the block is part of.
   * 
   * @return
   * @author GTouya
   */
  public ITown getTown();

  public void setTown(ITown town);

  /**
   * Get the network sections (e.g. roads, rivers) surrounding the block.
   * @return
   */
  public IFeatureCollection<INetworkSection> getSurroundingNetwork();

  public void setSurroundingNetwork(
      IFeatureCollection<INetworkSection> surroundingNetwork);

  /**
   * Gets the urban elements composing the block
   * @return
   */
  public IFeatureCollection<IUrbanElement> getUrbanElements();

  public void setUrbanElements(IFeatureCollection<IUrbanElement> urbanElements);

  /**
   * Gets the inner alignments of the block
   * @return
   */
  public IFeatureCollection<IUrbanAlignment> getAlignments();

  public void setAlignments(IFeatureCollection<IUrbanAlignment> alignments);

  /**
   * Get the empty spaces of the block, i.e. the parts of the block where there
   * is no urban element.
   * @return
   */
  public Collection<IEmptySpace> getEmptySpaces();

  /**
   * Determines if the block is fully colored as a meso
   */
  public boolean isColored();

  public void setColored(boolean bool);

  // //////////////////////////
  // Addtional spatial analysis méthods
  // //////////////////////////

  public void setPartition(CityPartition nearest);

  public CityPartition getPartition();

  public Set<IUrbanBlock> getNeighbours();

  public double getDensity();

  public boolean isStandard();

  public void updateGeom(IPolygon cutGeom);

  public IPolygon getCityBlockGeom();

  public boolean isHoleBlock();

  public Set<CityAxis> getAxes();

  public Set<IUrbanBlock> getInitialGeoxBlocks();

  public double getSimulatedDensity();

  public IUrbanBlock aggregateWithBlock(IUrbanBlock neigh);

  public boolean isEdge();

  public void setEdge(boolean b);

  public int getAggregLevel();

  public void setAggregLevel(int i);

  public HashSet<IUrbanBlock> getInsideBlocks();

  public void setStemmingFromN1Transfo(boolean b);

  /**
   * Feat type name
   */
  public static final String FEAT_TYPE_NAME = "UrbanBlock";

}
