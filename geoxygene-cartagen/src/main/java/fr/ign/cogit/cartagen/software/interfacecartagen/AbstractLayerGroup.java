/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;

import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.LayerManager;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.LoadedLayer;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;

/**
 * for storing the the data en layers then we can use it to fill the interface
 * by passing the layer manager
 * @author kjaara
 * 
 */

public abstract class AbstractLayerGroup {

  // boolean to determine if the layers are symbolised or not
  public boolean symbolisationDisplay = true;

  // booleans to determine which layers are visible or not
  public AbstractButton cVoirOccSol = new JCheckBox();
  public AbstractButton cVoirBati = new JCheckBox();
  public AbstractButton cVoirVille = new JCheckBox();
  public AbstractButton cVoirIlot = new JCheckBox();
  public AbstractButton cVoirAlign = new JCheckBox();
  public AbstractButton cVoirRH = new JCheckBox();
  public AbstractButton cVoirRR = new JCheckBox();
  public AbstractButton cVoirRF = new JCheckBox();
  public AbstractButton cVoirRE = new JCheckBox();
  public AbstractButton cVoirCN = new JCheckBox();
  public AbstractButton cVoirPointCote = new JCheckBox();
  public AbstractButton cVoirAdmin = new JCheckBox();
  public AbstractButton cVoirMNTDegrade = new JCheckBox();
  public AbstractButton cVoirHypsometrie = new JCheckBox();
  public AbstractButton cVoirOmbrageOpaque = new JCheckBox();
  public AbstractButton cVoirOmbrageTransparent = new JCheckBox();
  public AbstractButton cVoirReliefElem = new JCheckBox();
  public AbstractButton cVoirAirport = new JCheckBox();
  public AbstractButton cVoirMasque = new JCheckBox();
  public AbstractButton cVoirPOI = new JCheckBox();

  /**
   * Getter for the interface layers
   */

  public abstract LoadedLayer getLayer(String layer);

  /**
   * load one layer
   * @param pop
   */
  public abstract LoadedLayer replaceOneLayer(LayerManager layerManager,
      CartAGenDataSet dataSet, String layerName);

  /**
   * 
   * fill the LayerGroupe from the dataSet
   */
  public abstract void loadLayers(CartAGenDataSet dataSet,
      boolean areLayersSymbolised);

  /**
   * add a layer into the interface
   */
  public abstract void loadInterfaceWithOneLayer(LayerManager layerManager,
      String layerName, AbstractButton b);

  /**
   * add the layers and symbolised layers into the interface
   */
  public abstract void loadInterfaceWithLayers(LayerManager layerManager);

  public abstract void loadInterfaceWithLayers(LayerManager layerManager,
      SymbolList symbols);

  public abstract void removeLayer(LayerManager layerManager,
      String lAYER_SPECIALPOINT);

}
