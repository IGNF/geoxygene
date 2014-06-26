/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;

/**
 * @author JGaffuri
 * 
 */
public class GeneralisationConfigurationFrame extends JFrame {
  private static final long serialVersionUID = 1L;

  private static GeneralisationConfigurationFrame geneConfigFrame;

  public static GeneralisationConfigurationFrame getInstance() {
    if (GeneralisationConfigurationFrame.geneConfigFrame == null) {
      GeneralisationConfigurationFrame.geneConfigFrame = new GeneralisationConfigurationFrame();
      GeneralisationConfigurationFrame.geneConfigFrame.resetValues();
    }
    return geneConfigFrame;
  }

  public JTabbedPane panneauOnglets = new JTabbedPane();

  // general
  public JPanel pGeneral = new JPanel(new GridBagLayout());
  public JLabel lDescription = new JLabel("Description:");
  public JTextField tDescription = new JTextField(""
      + GeneralisationSpecifications.getDESCRIPTION());
  public JLabel lEchelleCible = new JLabel("Map scale   1:");
  public JTextField tEchelleCible = new JTextField(""
      + Legend.getSYMBOLISATI0N_SCALE());
  public JLabel lResolution = new JLabel("Resolution");
  public JTextField tResolution = new JTextField(""
      + GeneralisationSpecifications.getRESOLUTION());

  // boutons
  public JPanel panneauBoutons = new JPanel(new GridBagLayout());
  public JButton bValider = new JButton("Validate");
  public JButton bAnnuler = new JButton("Reset values");
  public JButton bEnregistrer = new JButton("Save in XML");

  private double oldScale;

  private GeneralisationConfigurationFrame() {
    super("CartAGen - Generalisation configuration");
    this.setResizable(false);
    this.setSize(new Dimension(400, 300));
    this.setLocation(100, 100);
    this.setVisible(false);
    this.oldScale = Legend.getSYMBOLISATI0N_SCALE();
    this.setAlwaysOnTop(true);

    GridBagConstraints c;

    // general

    // description
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    pGeneral.add(lDescription, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 3;
    tDescription.setColumns(30);
    pGeneral.add(tDescription, c);

    // echelle cible
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    pGeneral.add(lEchelleCible, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 1;
    tEchelleCible.setColumns(10);
    pGeneral.add(tEchelleCible, c);

    // resolution
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 2;
    pGeneral.add(lResolution, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 2;
    tResolution.setColumns(10);
    pGeneral.add(tResolution, c);

    panneauOnglets.addTab("General", new ImageIcon(
        GeneralisationConfigurationFrame.class.getResource("/images/co.gif")
            .getPath().replaceAll("%20", " ")), pGeneral,
        "General parameters of generalisation");

    // panneau des boutons

    // bouton valider
    bValider.setPreferredSize(new Dimension(110, 30));
    bValider.addActionListener(new ValidateAction(this));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 5, 5);
    panneauBoutons.add(bValider, c);

    // bouton annuler
    bAnnuler.setPreferredSize(new Dimension(110, 30));
    bAnnuler.addActionListener(new ResetAction(this));
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 5, 5);
    panneauBoutons.add(bAnnuler, c);

    // bouton enregistrer
    bEnregistrer.setPreferredSize(new Dimension(110, 30));
    bEnregistrer.addActionListener(new SaveAction(this));
    c = new GridBagConstraints();
    c.gridx = 2;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 5, 5);
    panneauBoutons.add(bEnregistrer, c);

    this.setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    c.gridheight = 5;
    this.add(panneauOnglets, c);

    c = new GridBagConstraints();
    c.gridheight = 1;
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    this.add(panneauBoutons, c);

    this.pack();

  }

  public void resetValues() {

    // general
    tDescription.setText(GeneralisationSpecifications.getDESCRIPTION());
    tEchelleCible.setText("" + Legend.getSYMBOLISATI0N_SCALE());
    tResolution.setText("" + GeneralisationSpecifications.getRESOLUTION());

  }

  public void validateValues() {
    this.oldScale = Legend.getSYMBOLISATI0N_SCALE();
    // general
    GeneralisationSpecifications.setDESCRIPTION(tDescription.getText());
    Legend.setSYMBOLISATI0N_SCALE(Double.parseDouble(tEchelleCible.getText()));
    GeneralisationSpecifications.setRESOLUTION(Double.parseDouble(tResolution
        .getText()));

    // update the SLD width values with the new scale value
    double scaleRatio = Legend.getSYMBOLISATI0N_SCALE() / oldScale;
    for (Layer layer : CartAGenDoc.getInstance().getCurrentDataset().getSld()
        .getLayers()) {
      for (Style style : layer.getStyles()) {
        for (FeatureTypeStyle ftStyle : style.getFeatureTypeStyles()) {
          for (Rule rule : ftStyle.getRules()) {
            for (Symbolizer symbolizer : rule.getSymbolizers()) {
              if (symbolizer.getStroke() != null)
                symbolizer
                    .getStroke()
                    .setStrokeWidth(
                        (float) (symbolizer.getStroke().getStrokeWidth() * scaleRatio));
            }
          }
        }
      }
    }
    CartAGenPlugin.getInstance().getApplication().getMainFrame()
        .getSelectedProjectFrame().getLayerViewPanel().repaint();
  }

  class ValidateAction extends AbstractAction {
    /****/
    private static final long serialVersionUID = 1L;
    private GeneralisationConfigurationFrame frame;

    public ValidateAction(GeneralisationConfigurationFrame frame) {
      super();
      this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      this.frame.validateValues();
      this.frame.setVisible(false);
    }
  }

  class ResetAction extends AbstractAction {
    /****/
    private static final long serialVersionUID = 1L;
    private GeneralisationConfigurationFrame frame;

    public ResetAction(GeneralisationConfigurationFrame frame) {
      super();
      this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      this.frame.resetValues();
    }
  }

  class SaveAction extends AbstractAction {
    /****/
    private static final long serialVersionUID = 1L;
    private GeneralisationConfigurationFrame frame;

    public SaveAction(GeneralisationConfigurationFrame frame) {
      super();
      this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JFileChooser parcourir = new JFileChooser(new File(CartAGenPlugin
          .getInstance().getCheminFichierConfigurationGene()));
      parcourir.setDialogType(JFileChooser.SAVE_DIALOG);
      parcourir.setApproveButtonText("Save");
      parcourir.setDialogTitle("Save");
      FileNameExtensionFilter filter = new FileNameExtensionFilter(
          "XML MiraGe", "xml");
      parcourir.setFileFilter(filter);
      parcourir.setSelectedFile(new File(CartAGenPlugin.getInstance()
          .getCheminFichierConfigurationGene()));
      int res = parcourir.showOpenDialog(this.frame);
      if (res == JFileChooser.APPROVE_OPTION) {
        GeneralisationSpecifications.enregistrer(parcourir.getSelectedFile());
      } else if (res == JFileChooser.ERROR_OPTION) {
        JOptionPane.showMessageDialog(this.frame, "Error", "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
