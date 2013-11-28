/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/*
 * Created on 3 août 2005
 */
package fr.ign.cogit.geoxygene.appli.plugin.cartagen;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author julien Gaffuri
 * 
 */
public final class CartAGenRightPanel extends JPanel implements MouseListener,
    MouseMotionListener {
  private static final long serialVersionUID = -3719644987901188303L;

  // la fenetre a laquelle le panel est eventuellement lie
  /**
     */
  private MainFrame frame = null;
  private DecimalFormat df;

  /**
   * @return
   */
  MainFrame getFrame() {
    return this.frame;
  }

  /**
     */
  public JPanel pSelection = new JPanel(new GridBagLayout());
  /**
     */
  public JLabel lNbSelection = new JLabel("Nb=0");
  /**
     */
  public JCheckBox cDisplaySelection = new JCheckBox("Display", true);
  /**
     */
  public JButton bSelectionCenterAndZoom = new JButton("Center and zoom");
  /**
   */
  public JButton bSelectionSuppression = new JButton("Eliminate objects");
  /**
     */
  public JButton bEmptySelection = new JButton("Clear selection");

  /**
   */
  public JPanel pMouseTracking = new JPanel(new GridBagLayout());
  private JCheckBox trackMouse;
  private JTextField xField, yField;

  /**
     */
  private GridBagConstraints c_ = null;

  public GridBagConstraints getGBC() {
    if (this.c_ == null) {
      this.c_ = new GridBagConstraints();
      this.c_.gridwidth = GridBagConstraints.REMAINDER;
      this.c_.anchor = GridBagConstraints.NORTHWEST;
      this.c_.insets = new Insets(1, 5, 1, 5);
    }
    return this.c_;
  }

  public CartAGenRightPanel(MainFrame frame) {
    this.frame = frame;
    df = new DecimalFormat();
    df.setMaximumFractionDigits(2);
    df.setMinimumFractionDigits(2);
    df.setDecimalSeparatorAlwaysShown(true);

    this.setPreferredSize(new Dimension(150, 800));
    this.setMinimumSize(new Dimension(150, 200));
    this.setMaximumSize(new Dimension(150, 1600));

    this.setLayout(new GridBagLayout());
    this.setFont(new Font("Arial", Font.PLAIN, 9));

    GridBagConstraints c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.anchor = GridBagConstraints.FIRST_LINE_START;
    c.insets = new Insets(1, 5, 1, 5);

    // mouse tracking
    this.pMouseTracking.setFont(this.getFont());
    this.pMouseTracking.setBorder(BorderFactory
        .createTitledBorder("Mouse Tracking"));
    this.trackMouse = new JCheckBox("Track mouse");
    this.xField = new JTextField();
    xField.setPreferredSize(new Dimension(110, 20));
    xField.setMinimumSize(new Dimension(110, 20));
    xField.setMaximumSize(new Dimension(110, 20));
    this.yField = new JTextField();
    yField.setPreferredSize(new Dimension(110, 20));
    yField.setMinimumSize(new Dimension(110, 20));
    yField.setMaximumSize(new Dimension(110, 20));
    JLabel xLabel = new JLabel("x ");
    xLabel.setFont(this.getFont());
    JLabel yLabel = new JLabel("y ");
    yLabel.setFont(this.getFont());
    JPanel xPanel = new JPanel(new GridBagLayout());
    xPanel.add(xLabel);
    xPanel.add(xField);
    JPanel yPanel = new JPanel(new GridBagLayout());
    yPanel.add(yLabel);
    yPanel.add(yField);
    this.pMouseTracking.add(trackMouse, c);
    this.pMouseTracking.add(xPanel, c);
    this.pMouseTracking.add(yPanel, c);

    // selection
    this.pSelection.setFont(this.getFont());
    this.pSelection.setBorder(BorderFactory.createTitledBorder("Selection"));

    this.lNbSelection.setFont(this.getFont());
    this.pSelection.add(this.lNbSelection, c);

    this.cDisplaySelection.setFont(this.getFont());
    this.pSelection.add(this.cDisplaySelection, c);

    this.bSelectionCenterAndZoom.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        IFeatureCollection<IFeature> fc = new FT_FeatureCollection<IFeature>(
            getFrame().getSelectedProjectFrame().getLayerViewPanel()
                .getSelectedFeatures());
        try {
          CartAGenRightPanel.this.getFrame().getSelectedProjectFrame()
              .getLayerViewPanel().getViewport().zoom(fc.envelope());
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
        CartAGenRightPanel.this.getFrame().getSelectedProjectFrame().repaint();
      }
    });
    this.bSelectionCenterAndZoom.setFont(this.getFont());
    this.pSelection.add(this.bSelectionCenterAndZoom, c);

    this.bSelectionSuppression.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        for (IFeature sel : getFrame().getSelectedProjectFrame()
            .getLayerViewPanel().getSelectedFeatures()) {
          if (!(sel instanceof IGeneObj)) {
            continue;
          }
          ((IGeneObj) sel).eliminate();
        }
      }
    });
    this.bSelectionSuppression.setFont(this.getFont());
    this.pSelection.add(this.bSelectionSuppression, c);

    this.bEmptySelection.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        getFrame().getSelectedProjectFrame().getLayerViewPanel()
            .getSelectedFeatures().clear();
        lNbSelection.setText("Nb=0");
        getFrame().getSelectedProjectFrame().getLayerViewPanel().repaint();
      }
    });
    this.bEmptySelection.setFont(this.getFont());
    this.pSelection.add(this.bEmptySelection, c);

    this.add(this.pMouseTracking, this.getGBC());
    this.add(this.pSelection, this.getGBC());

    // add a listener on the selection mode button
    // TODO à régler quand les plugins seront lancés après l'interface.
    // ((AbstractMainFrame) frame).getCurrentDesktop().addMouseListener(this);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    this.lNbSelection.setText("Nb="
        + String.valueOf(SelectionUtil.getSelectedObjects(
            frame.getApplication()).size()));
    this.validate();
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (this.trackMouse.isSelected()) {

      Point pt = e.getPoint();
      try {
        IDirectPosition pos = getFrame().getSelectedProjectFrame()
            .getLayerViewPanel().getViewport().toModelDirectPosition(pt);
        xField.setText(df.format(pos.getX()));
        yField.setText(df.format(pos.getY()));
      } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
      }
    }
  }
}
