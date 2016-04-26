package fr.ign.cogit.geoxygene.sig3d.gui.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.filter.OBJFilter;
import fr.ign.cogit.geoxygene.sig3d.io.obj.OBJExport;

public class IOToolBar extends JMenu implements ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private JMenuItem jMIExport;
  private MainWindow mw;

  public IOToolBar(MainWindow mw) {

    super("IO");

    this.mw = mw;

    // Sauvegarder tous les objets en base
    this.jMIExport = new JMenuItem("Export OBJ");

    // Creation d'un listener
    this.jMIExport.addActionListener(this);

    this.add(jMIExport);


  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub

    if (e.getSource().equals(jMIExport)) {

      JFileChooser choixFichierObj = new JFileChooser();
      choixFichierObj.setFileFilter(new OBJFilter());
      choixFichierObj.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      choixFichierObj.setMultiSelectionEnabled(false);
      JFrame frame = new JFrame();
      frame.setVisible(true);
      int returnVal = choixFichierObj.showSaveDialog(frame);
      frame.dispose();
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        String objFileName = choixFichierObj.getSelectedFile()
            .getAbsolutePath();
        if (!objFileName.contains(".obj")) { //$NON-NLS-1$
          objFileName = objFileName + ".obj"; //$NON-NLS-1$
        }

        OBJExport.export(objFileName,  mw.getInterfaceMap3D().getCurrent3DMap());

      }

    }
  }

}
