package fr.ign.cogit.geoxygene.sig3d.gui.table.featurestable;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 *  
 * @version 0.1
 * 
 * Le modèle de la table contenant la liste des entités. Les colonnes seront les
 * attributs contenant dans le feature Type du premier élement The table model
 * of the table containing features
 */
public class FeaturesListTableModel extends AbstractTableModel {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  // Les entités contenues
  private FT_FeatureCollection<IFeature> featColl;

  /**
   * Constructeur à partir d'un liste d'entités. Il s'agit des entités
   * s'affichange dans la JTable
   * 
   * @param featColl
   */
  public FeaturesListTableModel(FT_FeatureCollection<IFeature> featColl) {
    this.featColl = featColl;
  }

  @Override
  /**
   * Le nombre de colonnes àquivaut au nombre d'attributs +1 (la géométrie)
   */
  public int getColumnCount() {

    if (this.getRowCount() == 0) {
      return 1;
    }

    GF_FeatureType featType = this.featColl.get(0).getFeatureType();
    if (featType == null) {
      return 1;
    }

    List<GF_AttributeType> attributs = featType.getFeatureAttributes();
    return attributs.size() + 1;
  }

  @Override
  /**
   * Le nombre de lignes est le nombre d'entités
   */
  public int getRowCount() {

    return this.featColl.size();
  }

  @Override
  /**
   * On renvoie la valeur de l'attribut correspondant. si c'est la dernière colonne, on renvoie la géométrie
   */
  public Object getValueAt(int rowIndex, int columnIndex) {

    if (columnIndex >= this.getColumnCount()) {
      return null;
    }

    if (rowIndex >= this.getRowCount()) {
      return null;
    }

    if (this.getRowCount() == 0) {
      return null;
    }

    IFeature feat = this.featColl.get(rowIndex);
    // C'est la dernière colonne dans laquelle la geometrie est affichée
    if (columnIndex == this.getColumnCount() - 1) {
      return feat.getGeom();
    }

    GF_FeatureType featType = this.featColl.get(0).getFeatureType();
    if (featType == null) {
      return null;
    }

    List<GF_AttributeType> attributs = featType.getFeatureAttributes();
    GF_AttributeType att = attributs.get(columnIndex);

    return feat.getAttribute(att.getMemberName());
  }

  // Permet de renvoyer l'entité correspondant à une ligne
  public IFeature getFeature(int rowIndex) {
    if (this.getRowCount() > rowIndex) {
      return this.featColl.get(rowIndex);
    }
    return null;
  }

  public int getIndex(IFeature feat) {
    return this.featColl.getElements().indexOf(feat);
  }

  @Override
  /**
   * L'édition est bloquée
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  @Override
  public String getColumnName(int column) {

    if (column >= this.getColumnCount()) {
      return null;
    }

    if (this.getRowCount() == 0) {
      return null;
    }
    // Dernière colonne : c'est la géoémtrie
    if (column == this.getColumnCount() - 1) {
      return "Geometry";
    }

    GF_FeatureType featType = this.featColl.get(0).getFeatureType();
    if (featType == null) {
      return null;
    }

    List<GF_AttributeType> attributs = featType.getFeatureAttributes();
    GF_AttributeType att = attributs.get(column);

    return att.getMemberName();
  }

}
