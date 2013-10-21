/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.selection;

import java.util.Collection;
import java.util.HashSet;

import javax.swing.JOptionPane;

import org.w3c.dom.Element;

import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

public class ObjectSelection {
  private String name;
  private String cartagenDataset;
  private Collection<IFeature> objs;

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

  public ObjectSelection(Element root) {
    CartAGenDocOld document = CartAGenDocOld.getInstance();
    Element nomElem = (Element) root.getElementsByTagName("name").item(0);
    this.name = nomElem.getChildNodes().item(0).getNodeValue();
    Element versionElem = (Element) root.getElementsByTagName(
        "cartagen-dataset").item(0);
    this.cartagenDataset = versionElem.getChildNodes().item(0).getNodeValue();
    this.objs = new HashSet<IFeature>();
    // si les versions ne correspondent pas, on ne charge pas les objets
    if (!document.getDatabases().containsKey(cartagenDataset))
      return;
    // set cartagen dataset as the current dataset
    CartAGenDataSet dataset = document.getDatabases().get(cartagenDataset)
        .getDataSet();
    document.setCurrentDataset(dataset);
    // on fait maintenant une boucle sur les objets de cette selection
    Element objsElem = (Element) root.getElementsByTagName("objects").item(0);
    for (int j = 0; j < objsElem.getElementsByTagName("object").getLength(); j++) {
      Element objElem = (Element) objsElem.getElementsByTagName("object").item(
          j);
      int id = Integer.valueOf(objElem.getChildNodes().item(0).getNodeValue());
      String popName = objElem.getAttribute("population-name");
      IFeature feat = null;
      for (IFeature f : dataset.getPopulation(popName)) {
        if (f.getId() == id) {
          feat = f;
          break;
        }
      }
      if (feat == null) {
        JOptionPane.showMessageDialog(CartagenApplication.getInstance()
            .getFrame(), "Error, this object does not exist !!!");
        return;
      }
      objs.add(feat);
    }
  }

  public void addToSelection() {
    CartagenApplication.getInstance().getFrame().getVisuPanel().selectedObjects
        .addAll(objs);
  }
}
