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
import java.awt.event.ActionListener;
import java.io.File;

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

  public static GeneralisationConfigurationFrame geneConfigFrame;

  public static GeneralisationConfigurationFrame getInstance() {
    if (GeneralisationConfigurationFrame.geneConfigFrame == null) {
      GeneralisationConfigurationFrame.geneConfigFrame = new GeneralisationConfigurationFrame();
      GeneralisationConfigurationFrame.geneConfigFrame.resetValues();
    }
    return GeneralisationConfigurationFrame.geneConfigFrame;
  }

  public static JTabbedPane panneauOnglets = new JTabbedPane();

  // general
  public static JPanel pGeneral = new JPanel(new GridBagLayout());
  public static JLabel lDescription = new JLabel("Description:");
  public static JTextField tDescription = new JTextField(""
      + GeneralisationSpecifications.getDESCRIPTION());
  public static JLabel lEchelleCible = new JLabel("Map scale   1:");
  public static JTextField tEchelleCible = new JTextField(""
      + Legend.getSYMBOLISATI0N_SCALE());
  public static JLabel lResolution = new JLabel("Resolution");
  public static JTextField tResolution = new JTextField(""
      + GeneralisationSpecifications.getRESOLUTION());

  // boutons
  public static JPanel panneauBoutons = new JPanel(new GridBagLayout());
  public static JButton bValider = new JButton("Validate");
  public static JButton bAnnuler = new JButton("Reset values");
  public static JButton bEnregistrer = new JButton("Save in XML");

  private double oldScale;

  public GeneralisationConfigurationFrame() {
    super("CartAGen - Generalisation configuration");
    this.setResizable(false);
    this.setSize(new Dimension(400, 300));
    this.setLocation(100, 100);
    this.setVisible(false);
    this.oldScale = Legend.getSYMBOLISATI0N_SCALE();

    GridBagConstraints c;

    // general

    // description
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    GeneralisationConfigurationFrame.pGeneral.add(
        GeneralisationConfigurationFrame.lDescription, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 3;
    GeneralisationConfigurationFrame.tDescription.setColumns(30);
    GeneralisationConfigurationFrame.pGeneral.add(
        GeneralisationConfigurationFrame.tDescription, c);

    // echelle cible
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    GeneralisationConfigurationFrame.pGeneral.add(
        GeneralisationConfigurationFrame.lEchelleCible, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 1;
    GeneralisationConfigurationFrame.tEchelleCible.setColumns(10);
    GeneralisationConfigurationFrame.pGeneral.add(
        GeneralisationConfigurationFrame.tEchelleCible, c);

    // resolution
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 2;
    GeneralisationConfigurationFrame.pGeneral.add(
        GeneralisationConfigurationFrame.lResolution, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 2;
    GeneralisationConfigurationFrame.tResolution.setColumns(10);
    GeneralisationConfigurationFrame.pGeneral.add(
        GeneralisationConfigurationFrame.tResolution, c);

    GeneralisationConfigurationFrame.panneauOnglets.addTab(
        "General",
        new ImageIcon(GeneralisationConfigurationFrame.class
            .getResource("/images/co.gif").getPath().replaceAll("%20", " ")),
        GeneralisationConfigurationFrame.pGeneral,
        "General parameters of generalisation");

    // panneau des boutons

    // bouton valider
    GeneralisationConfigurationFrame.bValider.setPreferredSize(new Dimension(
        110, 30));
    GeneralisationConfigurationFrame.bValider
        .addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            GeneralisationConfigurationFrame.this.validateValues();
            GeneralisationConfigurationFrame.this.setVisible(false);
          }
        });
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 5, 5);
    GeneralisationConfigurationFrame.panneauBoutons.add(
        GeneralisationConfigurationFrame.bValider, c);

    // bouton annuler
    GeneralisationConfigurationFrame.bAnnuler.setPreferredSize(new Dimension(
        110, 30));
    GeneralisationConfigurationFrame.bAnnuler
        .addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            GeneralisationConfigurationFrame.this.resetValues();
          }
        });
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 5, 5);
    GeneralisationConfigurationFrame.panneauBoutons.add(
        GeneralisationConfigurationFrame.bAnnuler, c);

    // bouton enregistrer
    GeneralisationConfigurationFrame.bEnregistrer
        .setPreferredSize(new Dimension(110, 30));
    GeneralisationConfigurationFrame.bEnregistrer
        .addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
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
            int res = parcourir.showOpenDialog(GeneralisationConfigurationFrame
                .getInstance());
            if (res == JFileChooser.APPROVE_OPTION) {
              GeneralisationSpecifications.enregistrer(parcourir
                  .getSelectedFile());
            } else if (res == JFileChooser.ERROR_OPTION) {
              JOptionPane.showMessageDialog(
                  GeneralisationConfigurationFrame.getInstance(), "Error",
                  "Error", JOptionPane.ERROR_MESSAGE);
            }
          }
        });
    c = new GridBagConstraints();
    c.gridx = 2;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 5, 5);
    GeneralisationConfigurationFrame.panneauBoutons.add(
        GeneralisationConfigurationFrame.bEnregistrer, c);

    this.setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    c.gridheight = 5;
    this.add(GeneralisationConfigurationFrame.panneauOnglets, c);

    c = new GridBagConstraints();
    c.gridheight = 1;
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    this.add(GeneralisationConfigurationFrame.panneauBoutons, c);

    this.pack();

  }

  public void resetValues() {

    // general
    GeneralisationConfigurationFrame.tDescription
        .setText(GeneralisationSpecifications.getDESCRIPTION());
    GeneralisationConfigurationFrame.tEchelleCible.setText(""
        + Legend.getSYMBOLISATI0N_SCALE());
    GeneralisationConfigurationFrame.tResolution.setText(""
        + GeneralisationSpecifications.getRESOLUTION());

  }

  public void validateValues() {

    // general
    GeneralisationSpecifications
        .setDESCRIPTION(GeneralisationConfigurationFrame.tDescription.getText());
    Legend.setSYMBOLISATI0N_SCALE(Double
        .parseDouble(GeneralisationConfigurationFrame.tEchelleCible.getText()));
    GeneralisationSpecifications.setRESOLUTION(Double
        .parseDouble(GeneralisationConfigurationFrame.tResolution.getText()));

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

}
