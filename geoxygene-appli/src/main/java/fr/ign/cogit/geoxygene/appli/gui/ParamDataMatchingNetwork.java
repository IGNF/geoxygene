package fr.ign.cogit.geoxygene.appli.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.filechooser.FileFilter;

import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.plugin.DataMatchingPlugin;


/**
 * 
 * @author MDVan-Damme 
 */
public class ParamDataMatchingNetwork extends JDialog implements ActionListener {

    /** A classic logger. */
    private Logger logger = Logger.getLogger(ParamDataMatchingNetwork.class.getName());

    /** Origin Frame. */
    DataMatchingPlugin dataMatchingPlugin;
    
    /** 2 buttons : launch and cancel. */
    JButton launch = new JButton(I18N.getString("DataMatchingPlugin.Launch"));
    JButton cancel = new JButton(I18N.getString("DataMatchingPlugin.Cancel"));
    
    /** FileUploads Field for uploading Reference shape . */
    JButton buttonRefShape = new JButton(I18N.getString("DataMatchingPlugin.Import"));
    JTextField filenameRefShape = new JTextField(50);
    
    /** FileUploads Field for uploading Comparative shape . */
    JButton buttonCompShape = new JButton(I18N.getString("DataMatchingPlugin.Import"));
    JTextField filenameCompShape = new JTextField(50);
    
    /** FileUploads Field for uploading Parameters data. */
    JButton buttonParamFile = new JButton(I18N.getString("DataMatchingPlugin.Import"));
    JTextField filenameParamFile = new JTextField(50);
    
    /**
     * Constructor.
     * Initialize the JDialog. 
     * @param dmp
     */
    public ParamDataMatchingNetwork(DataMatchingPlugin dmp) {
        
        dataMatchingPlugin = dmp;
        
        Box boite = Box.createVerticalBox();
        setModal(true);
        setTitle(I18N.getString("DataMatchingPlugin.InputDialogTitle"));
        
        
        // First Line
        JPanel line = new JPanel();
        buttonRefShape.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doUpload("1");
            }
          });
        
        line = new JPanel();
        line.add(new JLabel("Réseau de référence : "));
        line.add(filenameRefShape);
        line.add(buttonRefShape); 
        line.add(new JLabel("(.shp)"));
        boite.add(line);

        // Second line
        line = new JPanel();
        buttonCompShape.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doUpload("2");
            }
          });
        line.add(new JLabel("Réseau de comparaison : "));
        line.add(filenameCompShape);
        line.add(buttonCompShape); 
        line.add(new JLabel("(.shp)"));
        boite.add(line);
        
        // Third line
        line = new JPanel();
        buttonParamFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doUpload("3");
            }
          });
        line.add(new JLabel("Paramètres de l'appariement : "));
        line.add(filenameParamFile);
        line.add(buttonParamFile); 
        line.add(new JLabel("(.xml)"));
        boite.add(line);

        // Buttons line
        line = new JPanel();
        line.add(launch);
        line.add(cancel);
        boite.add(line);

        add(boite);

        launch.addActionListener(this);
        cancel.addActionListener(this);
        pack();
        setLocation(400, 200);
        setVisible(true);
    }

    
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == launch) {
            // Set 3 filenames to dataMatching plugins
            dataMatchingPlugin.setRefShapeFilename(filenameRefShape.getText());
            dataMatchingPlugin.setCompShapeFilename(filenameCompShape.getText());
            dispose();
        } else if (source == cancel) {
            // do nothing
            dispose();
        }
    }
    
    
    private void doUpload(String type) {
        JFileChooser choixFichierRefShape = new JFileChooser();
        choixFichierRefShape.setCurrentDirectory(new File("D:\\Data\\Appariement\\MesTests\\T3"));
        
        // Crée un filtre qui n'accepte que les fichier shp ou les répertoires
        choixFichierRefShape.setFileFilter(new FileFilter() {
          @Override
          public boolean accept(File f) {
            return (f.isFile() && (f.getAbsolutePath().endsWith(".shp")
                || f.getAbsolutePath().endsWith(".SHP")) 
            || f.isDirectory());
          }

          @Override
          public String getDescription() {
            return "ShapefileReader.ESRIShapefiles";
          }
        
        });
        
        choixFichierRefShape.setFileSelectionMode(JFileChooser.FILES_ONLY);
        choixFichierRefShape.setMultiSelectionEnabled(false);
        
        int returnVal = choixFichierRefShape.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (type.equals("1")) {
                filenameRefShape.setText(choixFichierRefShape.getSelectedFile()
                        .getAbsolutePath());
            } else if (type.equals("2")) {
                filenameCompShape.setText(choixFichierRefShape.getSelectedFile()
                        .getAbsolutePath());
            } else if (type.equals("3")) {
                filenameParamFile.setText(choixFichierRefShape.getSelectedFile()
                        .getAbsolutePath());
            }
        }
        
    }
}