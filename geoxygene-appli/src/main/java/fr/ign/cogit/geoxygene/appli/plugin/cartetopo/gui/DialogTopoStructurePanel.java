package fr.ign.cogit.geoxygene.appli.plugin.cartetopo.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;


public class DialogTopoStructurePanel extends JDialog implements ActionListener {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 4791806011051504347L;
    
    /** Actions */
    private JCheckBox cb1;
    private JCheckBox cb2;
    private JCheckBox cb3;
    
    
    public DialogTopoStructurePanel() {
        
        setModal(true);
        setTitle(I18N.getString("DataMatchingPlugin.InputDialogTitle"));
        setIconImage(new ImageIcon(
            GeOxygeneApplication.class.getResource("/images/icons/vector.png")).getImage());
        
        cb1 = new JCheckBox("Ici là");
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(cb1, BorderLayout.CENTER);
        
        pack();
        setLocation(500, 250);
        setVisible(true);
    }
    
    /**
     * Actions : launch and cancel.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
      Object source = evt.getSource();
    }

}
