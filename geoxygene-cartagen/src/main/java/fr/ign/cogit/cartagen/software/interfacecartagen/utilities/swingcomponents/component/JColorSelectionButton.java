/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;

public class JColorSelectionButton extends JButton implements ActionListener {

  /****/
  private static final long serialVersionUID = 6897796103725414261L;
  private Color color;
  private JColorChooser colorChooser;
  String okLbl, cancelLbl;

  public JColorSelectionButton() {
    super();
    internationalisation();
    this.color = Color.RED;
    this.setForeground(color);
    this.setIcon(new ColorIcon(18, this.color));
    this.setContentAreaFilled(true);
    this.setOpaque(true);
    this.addActionListener(this);
    this.colorChooser = new JColorChooser();
    this.setPreferredSize(new Dimension(25, 25));
    this.setPreferredSize(new Dimension(25, 25));
    this.setPreferredSize(new Dimension(25, 25));
  }

  public JColorSelectionButton(Color color) {
    super();
    this.color = color;
    this.setForeground(color);
    this.setIcon(new ColorIcon(18, this.color));
    this.setContentAreaFilled(true);
    this.setOpaque(true);
    this.addActionListener(this);
    this.colorChooser = new JColorChooser(color);
    this.setPreferredSize(new Dimension(25, 25));
    this.setPreferredSize(new Dimension(25, 25));
    this.setPreferredSize(new Dimension(25, 25));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    ColorChooserFrame frame = new ColorChooserFrame(this);
    frame.setVisible(true);
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public JColorChooser getColorChooser() {
    return colorChooser;
  }

  public void setColorChooser(JColorChooser colorChooser) {
    this.colorChooser = colorChooser;
  }

  /**
   * Fix the values of Frame labels according to Language Locale.
   */
  private void internationalisation() {
    okLbl = I18N.getString("MainLabels.lblOk");
    cancelLbl = I18N.getString("MainLabels.lblCancel");
  }

  class ColorChooserFrame extends JFrame implements ActionListener,
      MouseListener {

    /****/
    private static final long serialVersionUID = 1L;
    private JColorSelectionButton btn;

    ColorChooserFrame(JColorSelectionButton btn) {
      super(I18N.getString("MainLabels.color"));
      this.btn = btn;

      JPanel btnPanel = new JPanel();
      JButton okBtn = new JButton(btn.okLbl);
      okBtn.addActionListener(this);
      okBtn.setActionCommand("ok");
      JButton cancelBtn = new JButton(btn.cancelLbl);
      cancelBtn.addActionListener(this);
      cancelBtn.setActionCommand("cancel");
      btnPanel.add(okBtn);
      btnPanel.add(cancelBtn);
      btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

      this.getContentPane().add(btn.getColorChooser());
      this.getContentPane().add(btnPanel);
      this.getContentPane().setLayout(
          new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        colorSelected();
        this.setVisible(false);
      }
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
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("ok")) {
        colorSelected();
        this.setVisible(false);
      } else if (e.getActionCommand().equals("cancel")) {
        this.setVisible(false);
      }
    }

    private void colorSelected() {
      btn.color = btn.getColorChooser().getColor();
      btn.setForeground(btn.getColorChooser().getColor());
      ((ColorIcon) btn.getIcon()).setColor(btn.getColorChooser().getColor());
      btn.repaint();
    }
  }

  private static class ColorIcon implements Icon {

    private int size;
    private Color color;

    public ColorIcon(int size, Color color) {
      this.size = size;
      this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setColor(color);
      g2d.fillRect(x, y, size, size);
    }

    @Override
    public int getIconWidth() {
      return size;
    }

    @Override
    public int getIconHeight() {
      return size;
    }

    public void setColor(Color color) {
      this.color = color;
    }

  }

}
