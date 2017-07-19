/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * Frame that allows to paste (or write) a geometry in WKT format and display it
 * in the geometry pool with custom color and width.
 * @author Guillaume
 * 
 */
public class ShowWKTFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;
  private JTextArea txtArea;
  private JTextField txtWidth;
  private JColorSelectionButton colorChooser;
  private JButton okBtn, cancelBtn;
  private GeOxygeneApplication application = null;

  // internationalisation values
  private String widthLbl, okLbl, cancelLbl, frameTitle;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("cancel")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("ok")) {
      try {
        displayWkt();
      } catch (ParseException e1) {
        e1.printStackTrace();
      }
      this.setVisible(false);
    }
  }

  public ShowWKTFrame(GeOxygeneApplication application) {
    super();
    this.application = application;
    internationalisation();
    this.setTitle(frameTitle);
    this.setSize(500, 500);
    this.setAlwaysOnTop(true);

    // define the text area
    txtArea = new JTextArea();
    txtArea.setPreferredSize(new Dimension(300, 1500));
    txtArea.setMaximumSize(new Dimension(300, 1500));
    txtArea.setMinimumSize(new Dimension(300, 1500));
    txtArea.setLineWrap(true);

    // define a panel with the colorChooser and the width
    JPanel symbolPanel = new JPanel();
    colorChooser = new JColorSelectionButton();
    txtWidth = new JTextField();
    txtWidth.setPreferredSize(new Dimension(70, 20));
    txtWidth.setMaximumSize(new Dimension(70, 20));
    txtWidth.setMinimumSize(new Dimension(70, 20));
    symbolPanel.add(Box.createHorizontalGlue());
    symbolPanel.add(colorChooser);
    symbolPanel.add(Box.createHorizontalGlue());
    symbolPanel.add(new JLabel(widthLbl + " "));
    symbolPanel.add(txtWidth);
    symbolPanel.add(Box.createHorizontalGlue());
    symbolPanel.setLayout(new BoxLayout(symbolPanel, BoxLayout.X_AXIS));

    // define a panel with the OK and Cancel buttons
    JPanel btnPanel = new JPanel();
    okBtn = new JButton(okLbl);
    okBtn.addActionListener(this);
    okBtn.setActionCommand("ok");
    cancelBtn = new JButton(cancelLbl);
    cancelBtn.addActionListener(this);
    cancelBtn.setActionCommand("cancel");
    btnPanel.add(okBtn);
    btnPanel.add(cancelBtn);
    btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

    this.getContentPane().add(new JScrollPane(txtArea));
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(symbolPanel);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(btnPanel);
    this.getContentPane()
        .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  /**
   * Fix the values of Frame labels according to Language Locale.
   */
  private void internationalisation() {
    widthLbl = I18N.getString("ShowWKTFrame.widthLabel");
    okLbl = I18N.getString("MainLabels.lblOk");
    cancelLbl = I18N.getString("MainLabels.lblCancel");
    frameTitle = I18N.getString("ShowWKTFrame.frameTitle");
  }

  /**
   * The method that actually draws the WKT geometry pasted in the text area.
   * @throws ParseException
   */
  private void displayWkt() throws ParseException {
    String wktGeom = txtArea.getText();
    IGeometry geom = WktGeOxygene.makeGeOxygene(wktGeom);
    Color colour = colorChooser.getColor();
    int widthPixels = Integer.valueOf(txtWidth.getText());
    GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
        .getGeometryPool();
    pool.setSld(application.getMainFrame().getSelectedProjectFrame().getSld());
    pool.addFeatureToGeometryPool(geom, colour, widthPixels);

  }

}
