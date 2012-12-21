/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.menus.themes;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class BlockMenu extends JMenu {

  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unused")
  private Logger logger = Logger.getLogger(BlockMenu.class.getName());

  private JMenuItem mIlotSelectionnerTous = new JMenuItem(new SelectAction());

  public JCheckBoxMenuItem mIdIlotVoir = new JCheckBoxMenuItem("Display id");

  public JCheckBoxMenuItem mVoirCoutSuppressionBatiments = new JCheckBoxMenuItem(
      "Voir cout suppression batiments");

  public JCheckBoxMenuItem mVoirDensiteInitiale = new JCheckBoxMenuItem(
      "Voir densite initiale");
  public JCheckBoxMenuItem mVoirDensiteSimulee = new JCheckBoxMenuItem(
      "Voir densite simulee");
  public JCheckBoxMenuItem mVoirSatisfactionDensite = new JCheckBoxMenuItem(
      "Voir satisfaction densite");
  public JCheckBoxMenuItem mVoirTauxSuperpositionBatiments = new JCheckBoxMenuItem(
      "Voir moyenne taux superposition batiments");
  public JCheckBoxMenuItem mVoirSatisfactionProximite = new JCheckBoxMenuItem(
      "Voir satisfaction proximite");

  public BlockMenu(String title) {
    super(title);

    this.add(this.mIlotSelectionnerTous);

    this.addSeparator();

    this.add(this.mIdIlotVoir);

    this.addSeparator();

    this.add(this.mVoirCoutSuppressionBatiments);

    this.addSeparator();

    this.add(this.mVoirDensiteInitiale);
    this.add(this.mVoirDensiteSimulee);

    this.addSeparator();

    this.add(this.mVoirTauxSuperpositionBatiments);
    this.add(this.mVoirSatisfactionProximite);

  }

  private class SelectAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      CartagenApplication.getInstance().getFrame().getVisuPanel().selectedObjects = new FT_FeatureCollection<IFeature>();
      for (IUrbanBlock ai : CartAGenDoc.getInstance().getCurrentDataset()
          .getBlocks()) {
        CartagenApplication.getInstance().getFrame().getVisuPanel().selectedObjects
            .add(ai);
      }
    }

    public SelectAction() {
      this.putValue(Action.NAME, "Select all blocks");
    }
  }

}
