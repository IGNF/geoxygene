package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.table.TableColumn;

import fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * Color Chooser providing the colors of COGIT Laboratory color wheels.
 * 
 * @author Charlotte Hoarau
 * 
 */
public class COGITColorChooserPanel extends AbstractColorChooserPanel implements
    MouseListener {
  private static final long serialVersionUID = 1L;

  private JLabel lblCerclesImage;
  private JPanel mainPanel;
  private JTable tCodesCouleur;
  private JPanel tablePanel;
  private BufferedImage cerclesImage;

  private static ColorReferenceSystem crs;

  // Properties of the last selected color.
  private ColorimetricColor lastColor;
  private int lastX;
  private int lastY;

  /**
   * Contructor. It creates a COGITColorChooserPanel with the chromatic wheels
   * and the corresponding information table.
   */
  public COGITColorChooserPanel() {
    this.cerclesImage = new BufferedImage(1100, 450,
        java.awt.image.BufferedImage.TYPE_INT_RGB);
    Graphics2D g = this.cerclesImage.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    COGITColorChooserPanel.createCercleImage(g);

    this.lblCerclesImage = new JLabel(new ImageIcon(this.cerclesImage));
    this.lblCerclesImage.addMouseListener(this);

    this.tCodesCouleur = new JTable(4, 8);
    Font f = this.tCodesCouleur.getFont();
    f = f.deriveFont(Font.BOLD);
    this.tCodesCouleur.setFont(f);
    this.tCodesCouleur.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.tCodesCouleur.setGridColor(new Color(225, 225, 225));// Couleur des
                                                              // bords
    this.tCodesCouleur.setBackground(new Color(200, 200, 200));// Couleur des
                                                               // cases
    this.tCodesCouleur.setForeground(new Color(100, 100, 100));// Couleur du
                                                               // texte

    this.tCodesCouleur.setValueAt("COGIT", 0, 0); //$NON-NLS-1$

    this.tCodesCouleur.setValueAt(
        "Usual Color", 1, 0); //$NON-NLS-1$
    this.tCodesCouleur.setValueAt(
        "Lightness", 2, 0); //$NON-NLS-1$
    this.tCodesCouleur.setValueAt(
        "ColorKey", 3, 0); //$NON-NLS-1$

    this.tCodesCouleur.setValueAt(
        "Others", 0, 2); //$NON-NLS-1$
    this.tCodesCouleur.setValueAt(
        "Hexa", 1, 2); //$NON-NLS-1$
    this.tCodesCouleur.setValueAt("sRGB", 2, 2); //$NON-NLS-1$

    this.tCodesCouleur.setValueAt("RGB", 0, 4); //$NON-NLS-1$
    this.tCodesCouleur.setValueAt(
        "Red", 1, 4); //$NON-NLS-1$
    this.tCodesCouleur.setValueAt(
        "Green", 2, 4); //$NON-NLS-1$
    this.tCodesCouleur.setValueAt(
        "Blue", 3, 4); //$NON-NLS-1$

    this.tCodesCouleur.setValueAt("CIELab", 0, 6); //$NON-NLS-1$
    this.tCodesCouleur.setValueAt("L", 1, 6); //$NON-NLS-1$
    this.tCodesCouleur.setValueAt("a", 2, 6); //$NON-NLS-1$
    this.tCodesCouleur.setValueAt("b", 3, 6); //$NON-NLS-1$

    for (int i = 0; i < 6; i++) {
      TableColumn colonne = this.tCodesCouleur.getColumnModel().getColumn(i);

      if (i == 0) {
        colonne.setPreferredWidth(110);
        colonne.setMinWidth(110);
        colonne.setMaxWidth(110);
      } else if (i == 4 || i == 6) {
        colonne.setPreferredWidth(50);
        colonne.setMinWidth(50);
        colonne.setMaxWidth(50);
      } else if (i == 1) {
        colonne.setPreferredWidth(180);
        colonne.setMinWidth(180);
        colonne.setMaxWidth(180);
      } else {
        colonne.setPreferredWidth(90);
        colonne.setMinWidth(90);
        colonne.setMaxWidth(90);
      }
    }

    // Initialization of the buttons
    this.tablePanel = new JPanel();
    this.tablePanel.setBackground(new Color(225, 225, 225));
    this.tablePanel.add(this.tCodesCouleur);

    this.mainPanel = new JPanel();
    this.mainPanel.setLayout(new BorderLayout());
    this.mainPanel.add(this.lblCerclesImage, BorderLayout.NORTH);
    this.mainPanel.add(this.tablePanel, BorderLayout.SOUTH);

    this.add(this.mainPanel);
  }

  /**
   * Method to draw the three chromatic wheels of the
   * {@link ColorReferenceSystem}.
   * @param g The {@link Graphics2D} to be modified
   */
  public static void createCercleImage(Graphics2D g) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(new Color(225, 225, 225));
    g.fillRect(0, 0, 1100, 450);

    COGITColorChooserPanel.crs = ColorReferenceSystem.defaultColorRS(); //$NON-NLS-1$

    // Creating the image of the main wheel (pure colors)
    // Création de l'image du cercle principal (Couleurs pures)
    for (int j = 0; j < 12; j++) {
      List<ColorimetricColor> listCouleurs = COGITColorChooserPanel.crs
          .getSlice(0, j);
      for (int i = 0; i < listCouleurs.size(); i++) {
        ColorimetricColor couleur = listCouleurs.get(listCouleurs.size() - 1
            - i);
        g.setColor(couleur.toColor());
        g.fillArc(50 + i * 15, 50 + i * 15, 300 - 30 * i, 300 - 30 * i,
            30 * (j + 1), 30);
      }
    }

    // Creating the image of the wheel of grey colors
    // Création de l'image du cercle des Couleurs grisées
    for (int j = 0; j < 7; j++) {
      List<ColorimetricColor> listCouleurs = COGITColorChooserPanel.crs
          .getSlice(1, j);
      for (int i = 0; i < listCouleurs.size(); i++) {
        ColorimetricColor couleur = listCouleurs.get(listCouleurs.size() - 1
            - i);
        g.setColor(couleur.toColor());
        g.fillArc(400 + i * 15, 50 + i * 15, 300 - 30 * i, 300 - 30 * i,
            52 * j, 52);
      }
    }

    // Creating the image of the wheel of coloured greys
    // Création de l'image du cercle des Gris colorés
    for (int j = 0; j < 7; j++) {
      List<ColorimetricColor> listCouleurs = COGITColorChooserPanel.crs
          .getSlice(2, j);
      for (int i = 0; i < listCouleurs.size(); i++) {
        ColorimetricColor couleur = listCouleurs.get(listCouleurs.size() - 1
            - i);
        g.setColor(couleur.toColor());
        g.fillArc(750 + i * 15, 50 + i * 15, 300 - 30 * i, 300 - 30 * i,
            52 * j, 52);
      }
    }

    // Creating the image of the white, grey and black bar
    // Création de l'image de la gamme de Gris, Noir et Blanc
    List<ColorimetricColor> listCouleurs = COGITColorChooserPanel.crs.getSlice(
        3, 0);
    for (int i = 0; i < listCouleurs.size(); i++) {
      ColorimetricColor couleur = listCouleurs.get(listCouleurs.size() - 1 - i);
      g.setColor(couleur.toColor());
      g.fillRect(550 + i * 40, 400, 40, 25);
    }

    // Creating the image of the wheel of brown colors
    // Création de l'image de la gamme de Marrons
    List<ColorimetricColor> listCouleursM = COGITColorChooserPanel.crs
        .getSlice(3, 1);
    for (int i = 0; i < listCouleursM.size(); i++) {
      ColorimetricColor couleur = listCouleursM.get(listCouleursM.size() - 1
          - i);
      g.setColor(couleur.toColor());
      g.fillRect(220 + i * 40, 400, 40, 25);
    }

    // Creating the contours of the bars and the wheel centers
    // Création des bords pour marquer les deux gammes horizontaux et
    // le centre des cercles
    g.setColor(new Color(225, 225, 225));
    g.fillArc(155, 155, 90, 90, 0, 360);
    g.fillArc(460, 110, 180, 180, 0, 360);
    g.fillArc(810, 110, 180, 180, 0, 360);

    g.setColor(new Color(200, 200, 200));
    g.drawRect(220, 400, 40 * 7, 25);
    g.drawRect(550, 400, 40 * 9, 25);
    g.drawArc(155, 155, 90, 90, 0, 360);
    g.drawArc(460, 110, 180, 180, 0, 360);
    g.drawArc(810, 110, 180, 180, 0, 360);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getSource() == this.lblCerclesImage) {
      int xpos = e.getX();
      int ypos = e.getY();

      int rgb = this.cerclesImage.getRGB(xpos, ypos);
      Color color = new Color(rgb);
      ColorimetricColor newColor = ColorReferenceSystem.searchColor(color);

      Color backgroundColor = new Color(225, 225, 225);
      if (rgb != backgroundColor.getRGB()) {
        this.updateTable(newColor);
        this.markColor(newColor);
        this.updateCircleColor(newColor);
        this.lastColor = newColor;
      }
    }
  }

  /**
   * Method to point a color on the chromatic wheels.
   * @param g the {@link Graphics2D} to be modified
   * @param c the {@link ColorimetricColor} to point
   */
  public static void displayColor(Graphics2D g, ColorimetricColor c) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    int x = c.getXScreen();
    int y = c.getYScreen();

    if (c.getLightness() > 5) {
      g.setColor(Color.BLACK);
    } else {
      g.setColor(Color.WHITE);
    }
    g.fillOval(x - 3, y - 3, 6, 6);

    if (c.getLightness() > 5) {
      g.setColor(Color.WHITE);
    } else {
      g.setColor(Color.BLACK);
    }
    g.setStroke(new BasicStroke(1.5f));
    g.drawOval(x - 3, y - 3, 6, 6);
  }

  /**
   * Method to point a color on the chromatic wheels.
   * @param g the {@link Graphics2D} to be modified
   * @param c the {@link ColorimetricColor} to point
   */
  public static void clearDisplayColor(Graphics2D g, ColorimetricColor c) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    int x = c.getXScreen();
    int y = c.getYScreen();

    g.setColor(c.toColor());
    g.fillOval(x - 3, y - 3, 6, 6);
    g.setStroke(new BasicStroke(2f));
    g.drawOval(x - 3, y - 3, 6, 6);
  }

  /**
   * Method to mark the selected color. It erase the last cross and draw a new
   * one.
   * @param c Selected Color.
   */
  public void markColor(ColorimetricColor c) {
    Graphics2D g = (Graphics2D) this.lblCerclesImage.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    int x = c.getXScreen();
    int y = c.getYScreen();
    if (this.lastColor != null) {
      g.setColor(this.lastColor.toColor());
      g.drawLine(this.lastX, this.lastY - 3, this.lastX, this.lastY + 3);
      g.drawLine(this.lastX - 3, this.lastY, this.lastX + 3, this.lastY);
    }

    if (c.getLightness() > 5) {
      g.setColor(Color.WHITE);
    } else {
      g.setColor(Color.BLACK);
    }

    g.drawLine(x, y - 3, x, y + 3);
    g.drawLine(x - 3, y, x + 3, y);
    this.lastX = x;
    this.lastY = y;
  }

  /**
   * Method to update the graphic around the selected color. It erase the last
   * one and draw a new one.
   * @param c Selected color.
   */
  public void updateCircleColor(ColorimetricColor c) {
    if (this.lastColor != null) {
      this.circleColor(this.lastColor, this.lastColor.toColor(), 1.8f);
    }
    if (c.getLightness() > 5) {
      this.circleColor(c, Color.WHITE, 0.8f);
    } else {
      this.circleColor(c, Color.BLACK, 0.8f);
    }
  }

  /**
   * Method which surround the section of the selected color by a graphic.
   * @param c1 Selected color.
   * @param c2 Color of the surrounding graphic.
   * @param stroke Width of the stroke of the graphic.
   */
  public void circleColor(ColorimetricColor c1, Color c2, float stroke) {
    Graphics2D g = (Graphics2D) this.lblCerclesImage.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(c2);
    g.setStroke(new BasicStroke(stroke));

    // If c is in the main wheel
    for (int j = 0; j < 12; j++) {
      List<ColorimetricColor> listCP = COGITColorChooserPanel.crs
          .getSlice(0, j);
      for (int i = 0; i < listCP.size(); i++) {
        ColorimetricColor couleur = listCP.get(listCP.size() - 1 - i);
        if (couleur.equals(c1)) {
          g.drawArc(50 + i * 15, 50 + i * 15, 300 - 30 * i, 300 - 30 * i,
              30 * (j + 1), 30);
          g.drawArc(50 + (i + 1) * 15, 50 + (i + 1) * 15, 300 - 30 * (i + 1),
              300 - 30 * (i + 1), 30 * (j + 1), 30);
        }
      }
    }

    // If c is in the wheel of color with grey
    for (int j = 0; j < 7; j++) {
      List<ColorimetricColor> listCG = COGITColorChooserPanel.crs
          .getSlice(1, j);
      for (int i = 0; i < listCG.size(); i++) {
        ColorimetricColor couleur = listCG.get(listCG.size() - 1 - i);
        if (couleur.equals(c1)) {
          g.drawArc(400 + i * 15, 50 + i * 15, 300 - 30 * i, 300 - 30 * i,
              52 * j, 52);
          g.drawArc(400 + (i + 1) * 15, 50 + (i + 1) * 15, 300 - 30 * (i + 1),
              300 - 30 * (i + 1), 52 * j, 52);
        }
      }
    }

    // If c is in the wheel of grey with color
    for (int j = 0; j < 7; j++) {
      List<ColorimetricColor> listGC = COGITColorChooserPanel.crs
          .getSlice(2, j);
      for (int i = 0; i < listGC.size(); i++) {
        ColorimetricColor couleur = listGC.get(listGC.size() - 1 - i);
        if (couleur.equals(c1)) {
          g.drawArc(750 + i * 15, 50 + i * 15, 300 - 30 * i, 300 - 30 * i,
              52 * j, 52);
          g.drawArc(750 + (i + 1) * 15, 50 + (i + 1) * 15, 300 - 30 * (i + 1),
              300 - 30 * (i + 1), 52 * j, 52);
        }
      }
    }

    // If c is in the black to white bar
    List<ColorimetricColor> listGNB = COGITColorChooserPanel.crs.getSlice(3, 0);
    for (int i = 0; i < listGNB.size(); i++) {
      ColorimetricColor couleur = listGNB.get(listGNB.size() - 1 - i);
      if (couleur.equals(c1)) {
        g.drawRect(550 + i * 40, 400, 40, 25);
      }
    }

    // If c is in the brown bar
    List<ColorimetricColor> listM = COGITColorChooserPanel.crs.getSlice(3, 1);
    for (int i = 0; i < listM.size(); i++) {
      ColorimetricColor couleur = listM.get(listM.size() - 1 - i);
      if (couleur.equals(c1)) {
        g.drawRect(220 + i * 40, 400, 40, 25);
      }
    }
  }

  /**
   * Updating the information table with the new selected color information.
   * @param c The selected {@link ColorimetricColor}
   */
  public void updateTable(ColorimetricColor c) {

    float[] labCodes = new float[3];
    labCodes = c.getLab();

    // Setting the new values on the JTable
    if (c.getRedRGB() == 225 || c.getGreenRGB() == 225 || c.getBlueRGB() == 225) {
    } else {
      this.tCodesCouleur.setValueAt(c.getUsualName(), 1, 1);
      this.tCodesCouleur.setValueAt(c.getLightness(), 2, 1);
      this.tCodesCouleur.setValueAt(c.getCleCoul(), 3, 1);

      this.tCodesCouleur.setValueAt(Integer.toHexString(c.toColor().getRGB()),
          1, 3);
      this.tCodesCouleur.setValueAt(c.toColor().getRGB(), 2, 3);

      this.tCodesCouleur.setValueAt(c.getRedRGB(), 1, 5);
      this.tCodesCouleur.setValueAt(c.getGreenRGB(), 2, 5);
      this.tCodesCouleur.setValueAt(c.getBlueRGB(), 3, 5);

      this.tCodesCouleur.setValueAt(Math.round(labCodes[0]), 1, 7);
      this.tCodesCouleur.setValueAt(Math.round(labCodes[1]), 2, 7);
      this.tCodesCouleur.setValueAt(Math.round(labCodes[2]), 3, 7);
    }

    this.getColorSelectionModel().setSelectedColor(c.toColor());
  }

  @Override
  public void mouseEntered(MouseEvent arg0) {
  }

  @Override
  public void mouseExited(MouseEvent arg0) {
  }

  @Override
  public void mousePressed(MouseEvent arg0) {
  }

  @Override
  public void mouseReleased(MouseEvent arg0) {
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    Color c = COGITColorChooserPanel.showDialog(new JButton(),
        "Pick A Color", Color.BLUE); //$NON-NLS-1$
    System.out.println("Selected Color : " + c); //$NON-NLS-1$
  }

  /**
   * Creates and returns a new dialog containing the specified ColorChooser pane
   * along with "OK", "Cancel", and "Reset" buttons. If the "OK" or "Cancel"
   * buttons are pressed, the dialog is automatically hidden (but not disposed).
   * If the "Reset" button is pressed, the color-chooser's color will be reset
   * to the color which was set the last time show was invoked on the dialog and
   * the dialog will remain showing.
   * @param component
   * @param initialColor
   * @return
   */
  public static Color showDialog(Component component, String title,
      Color initialColor) {
    JColorChooser colorChooser = new JColorChooser(
        initialColor != null ? initialColor : Color.white);

    colorChooser.addChooserPanel(new COGITColorChooserPanel());

    JDialog dialog = JColorChooser.createDialog(component, title, true,
        colorChooser, null, null);
    dialog.setVisible(true);
    Color c = colorChooser.getColor();

    return c;
  }

  /**
   * Creates and returns a new dialog containing the specified ColorChooser pane
   * along with "OK", "Cancel", and "Reset" buttons. If the "OK" or "Cancel"
   * buttons are pressed, the dialog is automatically hidden (but not disposed).
   * If the "Reset" button is pressed, the color-chooser's color will be reset
   * to the color which was set the last time show was invoked on the dialog and
   * the dialog will remain showing. It also points the given list of colors on
   * the chromatic wheels.
   * @param component
   * @param colors The {@link ColorimetricColor}s to be pointed on the wheels.
   * @return
   */
  public static Color show(Component component, String title,
      List<ColorimetricColor> colors) {
    JColorChooser colorChooser = new JColorChooser(colors.get(0).toColor());
    COGITColorChooserPanel cogitChooser = new COGITColorChooserPanel();
    colorChooser.addChooserPanel(cogitChooser);
    for (int i = 0; i < colorChooser.getChooserPanels().length; i++) {
      colorChooser.removeChooserPanel(colorChooser.getChooserPanels()[i]);
    }
    colorChooser.removeChooserPanel(colorChooser.getChooserPanels()[0]);

    JDialog dialog = JColorChooser.createDialog(component, title, true,
        colorChooser, null, null);

    JLabel label = (JLabel) ((JPanel) colorChooser.getChooserPanels()[0]
        .getComponent(0)).getComponent(0);

    for (ColorimetricColor c : colors) {
      COGITColorChooserPanel.displayColor(
          (Graphics2D) ((ImageIcon) label.getIcon()).getImage().getGraphics(),
          c);
      if (c.getLightness() > 5) {
        cogitChooser.circleColor(c, Color.WHITE, 0.8f);
      } else {
        cogitChooser.circleColor(c, Color.BLACK, 0.8f);
      }
    }
    cogitChooser.repaint();
    cogitChooser.validate();
    dialog.setVisible(true);

    Color c = colorChooser.getColor();
    return c;
  }

  @Override
  // We did this work in the constructor so we can skip it here.
  protected void buildChooser() {
  }

  @Override
  public String getDisplayName() {
    return "COGITColorReferenceSystem"; //$NON-NLS-1$
  }

  @Override
  public Icon getLargeDisplayIcon() {
    return null;
  }

  @Override
  public Icon getSmallDisplayIcon() {
    return null;
  }

  @Override
  public void updateChooser() {
    Color c = this.getColorSelectionModel().getSelectedColor();
    ColorimetricColor cRef = new ColorimetricColor(c, true);
    this.updateTable(cRef);
  }
}
