package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement;

public class ScaleMasterElementPanel extends JXPanel implements MouseListener {

  /****/
  private static final long serialVersionUID = 2955037889746765340L;
  /**
   * The scale master element to be displayed in this panel.
   */
  private List<ScaleMasterElement> elements;
  private List<JPanel> panels;
  private EditScaleMasterFrame parent;
  private JLabel label;
  private Popup popup;

  public ScaleMasterElementPanel(List<ScaleMasterElement> elements,
      EditScaleMasterFrame parent) {
    super();
    this.parent = parent;
    this.setElements(elements);
    this.panels = new ArrayList<JPanel>();

    for (ScaleMasterElement element : elements) {
      JPanel panel = new JPanel();
      Color color = this.findElementColor(element);
      MattePainter painter = new MattePainter(color);
      this.setBackgroundPainter(painter);
      panel.setBackground(color);
      panel.setOpaque(true);
      label = new JLabel(element.toString());
      panel.add(label);
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      panel.addMouseListener(this);
      this.add(panel);
      this.panels.add(panel);
    }
    this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
  }

  private Color findElementColor(ScaleMasterElement element) {
    Color dbHue = this.parent.getDbHues().get(element.getDbName());
    int generalisationScore = 0;
    if (element.getOgcFilter() != null) {
      generalisationScore++;
    }
    for (@SuppressWarnings("unused")
    String process : element.getProcessesToApply()) {
      generalisationScore++;
      // TODO utilisation d'une ontologie de processus
      /*
       * if (OntologyUtil.isA(this.parent.getOntology(), process, "algorithme"))
       * { generalisationScore++; } else { generalisationScore += 2; }
       */
    }
    // decreases lightness according to the generalisation score
    Color color = dbHue;
    for (int i = 0; i < generalisationScore; i++) {
      color = getBrighterColor(color);
    }
    return color;
  }

  /**
   * Gives a brighter color even if it is Red, Green or Blue.
   * @param color
   * @return
   */
  private Color getBrighterColor(Color color) {
    Color brighter = color.brighter().brighter();
    if (brighter.equals(color)) {
      int red = color.getRed();
      int blue = color.getBlue();
      int green = color.getGreen();
      if (red == 0)
        red = 50;
      if (blue == 0)
        blue = 50;
      if (green == 0)
        green = 50;
      brighter = new Color(red, green, blue);
    }
    return brighter;
  }

  public void setElements(List<ScaleMasterElement> elements) {
    this.elements = elements;
  }

  public List<ScaleMasterElement> getElements() {
    return this.elements;
  }

  public JLabel getLabel() {
    return label;
  }

  public void setLabel(JLabel label) {
    this.label = label;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (SwingUtilities.isRightMouseButton(e)) {
      PopupFactory factory = PopupFactory.getSharedInstance();
      JPanel contents = this.getPopupContent((JPanel) e.getSource());
      this.popup = factory.getPopup(this.parent, contents, e.getX(), e.getY());
      this.popup.show();
    } else {
      if (this.popup != null) {
        this.popup.hide();
        this.popup = null;
      }
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
    if (this.popup != null) {
      this.popup.hide();
      this.popup = null;
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  private JPanel getPopupContent(JPanel source) {
    JPanel panel = new JPanel();
    ScaleMasterElement element = this.elements.get(this.panels.indexOf(source));
    JLabel lblDb = new JLabel(element.getDbName());
    panel.add(lblDb);
    if (element.getOgcFilter() != null) {
      JLabel lblFilter = new JLabel(element.getOgcFilter().toString());
      panel.add(lblFilter);
    }
    if (element.getProcessesToApply().size() != 0) {
      for (int i = 0; i < element.getProcessesToApply().size(); i++) {
        StringBuffer procStr = new StringBuffer();
        procStr.append(element.getProcessesToApply().get(i) + "(");
        Map<String, Object> params = element.getParameters().get(i);
        for (String param : params.keySet()) {
          procStr.append(param + ": " + params.get(param).toString() + "; ");
        }
        procStr.delete(procStr.length() - 2, procStr.length() - 1);
        procStr.append(")");
        JLabel lblProc = new JLabel(procStr.toString());
        panel.add(lblProc);
      }
    }
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    Border insideBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    Border outsideBorder = BorderFactory.createLineBorder(Color.BLUE, 3);
    panel.setBorder(BorderFactory.createCompoundBorder(outsideBorder,
        insideBorder));
    return panel;
  }

}
