/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection;

import java.util.Collection;
import java.util.HashSet;

import javax.swing.JOptionPane;

import org.w3c.dom.Element;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;

public class ObjectSelection {
  private String name;
  private String cartagenDataset;
  private Collection<IFeature> objs;
  private GeOxygeneApplication appli;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCartagenDataset() {
    return cartagenDataset;
  }

  public void setVersion(String cartagenDataset) {
    this.cartagenDataset = cartagenDataset;
  }

  public Collection<IFeature> getObjs() {
    return objs;
  }

  public void setObjs(Collection<IFeature> objs) {
    this.objs = objs;
  }

  @Override
  public String toString() {
    return name + " (" + objs.size() + " objects)";
  }

  public ObjectSelection(GeOxygeneApplication appli, Element root) {
    this.appli = appli;
    CartAGenDoc document = CartAGenDoc.getInstance();
    Element nomElem = (Element) root.getElementsByTagName("name").item(0);
    this.name = nomElem.getChildNodes().item(0).getNodeValue();
    Element versionElem = (Element) root
        .getElementsByTagName("cartagen-dataset").item(0);
    this.cartagenDataset = versionElem.getChildNodes().item(0).getNodeValue();
    this.objs = new HashSet<IFeature>();
    // si les versions ne correspondent pas, on ne charge pas les objets
    if (!cartagenDataset.equals("default")
        && !document.getDatabases().containsKey(cartagenDataset))
      return;
    if (!cartagenDataset.equals("default")) {
      // set cartagen dataset as the current dataset
      CartAGenDataSet dataset = document.getDatabases().get(cartagenDataset)
          .getDataSet();
      document.setCurrentDataset(dataset);
    }
    // on fait maintenant une boucle sur les objets de cette selection
    Element objsElem = (Element) root.getElementsByTagName("objects").item(0);
    for (int j = 0; j < objsElem.getElementsByTagName("object")
        .getLength(); j++) {
      Element objElem = (Element) objsElem.getElementsByTagName("object")
          .item(j);
      int id = Integer.valueOf(objElem.getChildNodes().item(0).getNodeValue());
      String popName = objElem.getAttribute("population-name");
      IFeature feat = null;
      if (appli.getMainFrame().getSelectedProjectFrame()
          .getLayer(popName) == null)
        continue;
      for (IFeature f : appli.getMainFrame().getSelectedProjectFrame()
          .getLayer(popName).getFeatureCollection()) {
        if (f.getId() == id) {
          feat = f;
          break;
        }
      }
      if (feat == null) {
        JOptionPane.showMessageDialog(appli.getMainFrame().getGui(),
            "Error, this object does not exist !!!");
        return;
      }
      objs.add(feat);
    }
  }

  public void addToSelection() {
    appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
        .getSelectedFeatures().addAll(objs);
  }
}
