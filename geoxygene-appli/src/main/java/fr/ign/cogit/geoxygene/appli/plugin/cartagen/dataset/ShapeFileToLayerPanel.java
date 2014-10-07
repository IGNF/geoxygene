package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.ShapeFileFilter;

public class ShapeFileToLayerPanel extends JPanel implements ActionListener {

  /****/
  private static final long serialVersionUID = 7418510543377118888L;

  private String layerName;
  private File file;
  private Map<String, JTextField> attrFields;
  private Map<String, String> attributeMapping;
  private JTextField txtFile;
  private JButton browseBtn;

  public ShapeFileToLayerPanel(String layerName, String[] attrList) {
    super();
    this.layerName = layerName;
    attrFields = new HashMap<String, JTextField>();
    attributeMapping = new HashMap<String, String>();

    JPanel pFile = new JPanel();
    txtFile = new JTextField();
    txtFile.setPreferredSize(new Dimension(120, 20));
    txtFile.setMaximumSize(new Dimension(120, 20));
    txtFile.setMinimumSize(new Dimension(120, 20));
    txtFile.setEditable(false);
    browseBtn = new JButton(new ImageIcon(ShapeFileToLayerPanel.class
        .getResource("/images/browse.jpeg").getPath().replaceAll("%20", " ")));
    browseBtn.addActionListener(this);
    browseBtn.setActionCommand("browse");
    pFile.add(txtFile);
    pFile.add(browseBtn);
    pFile.setLayout(new BoxLayout(pFile, BoxLayout.X_AXIS));

    this.add(pFile);
    if (attrList != null) {
      for (String attr : attrList) {
        JPanel pAttr = new JPanel();
        JTextField txtField = new JTextField();
        txtField.setPreferredSize(new Dimension(120, 20));
        txtField.setMaximumSize(new Dimension(120, 20));
        txtField.setMinimumSize(new Dimension(120, 20));
        attrFields.put(attr, txtField);
        pAttr.add(new JLabel(attr + ": "));
        pAttr.add(txtField);
        pAttr.setLayout(new BoxLayout(pAttr, BoxLayout.X_AXIS));
        this.add(pAttr);
      }
    }
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
  }

  public String getLayerName() {
    return layerName;
  }

  public File getFile() {
    return file;
  }

  public Map<String, String> getAttributeMapping() {
    for (String attr : attrFields.keySet()) {
      attributeMapping.put(attr, attrFields.get(attr).getText());
    }
    return attributeMapping;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("browse")) {
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new ShapeFileFilter());
      int returnVal = fc.showOpenDialog(null);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      file = fc.getSelectedFile();
      txtFile.setEditable(true);
      txtFile.setText(file.getPath());
      txtFile.setEditable(false);
    }
  }

}
