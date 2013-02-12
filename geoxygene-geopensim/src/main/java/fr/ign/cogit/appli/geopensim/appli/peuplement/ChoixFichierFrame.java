/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.appli.peuplement;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;



/**
 * @author Florence Curie
 *
 */
public class ChoixFichierFrame extends JFrame implements ActionListener,FocusListener{


	private static final long serialVersionUID = -2687762356288261250L;
	private static final Logger logger = Logger.getLogger(ChoixFichierFrame.class.getName());
	private JButton boutonChoix, boutonCreation, boutonValidation, boutonAnnulation;
	private JTextField nomFichier,nomRepertoire,nomNouveauFichier;
	private JRadioButton boutonRadio1,boutonRadio2;
	private JLabel labelNouveauNom; 
	
	// Constructeur
	public ChoixFichierFrame(){

		// La fenêtre
		this.setTitle("Création de méthodes de peuplement");
		this.setBounds(50, 100, 500, 250);
		this.setResizable(false);
		this.setIconImage(new ImageIcon(GeOpenSimApplication.class.getResource("/geopensim-icon.gif")).getImage());
		Container contenu = this.getContentPane();
		
		// Arrêt du programme sur fermeture de la fenêtre
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);	
				dispose();
			}
		});

		// Les boutons radio
		ButtonGroup groupeBoutons = new ButtonGroup();
		// Le premier bouton
		Box hBoxBoutonRadio1 = Box.createHorizontalBox();
		boutonRadio1 = new JRadioButton("Ajouter des méthodes à un fichier existant",true);
		boutonRadio1.addActionListener(this);
		hBoxBoutonRadio1.add(boutonRadio1);
		hBoxBoutonRadio1.add(Box.createHorizontalGlue());
		groupeBoutons.add(boutonRadio1);
		
		// Choix d'un fichier
		Box hBoxChoixFichier = Box.createHorizontalBox();
		hBoxChoixFichier.add(Box.createHorizontalStrut(20));
		String nomFich = new File("").getAbsolutePath()+"\\ConfigurationMethodesPeuplement.xml";
		nomFichier = new JTextField(50);
		nomFichier.setText(nomFich);
		nomFichier.setMaximumSize(nomFichier.getPreferredSize());
		hBoxChoixFichier.add(nomFichier);
		hBoxChoixFichier.add(Box.createHorizontalStrut(10));
		boutonChoix = new JButton("Choisir un fichier");
		boutonChoix.addActionListener(this);
		hBoxChoixFichier.add(boutonChoix);
		
		// Le second bouton radio
		Box hBoxBoutonRadio2 = Box.createHorizontalBox();
		boutonRadio2 = new JRadioButton("Créer un nouveau fichier",false);
		boutonRadio2.addActionListener(this);
		hBoxBoutonRadio2.add(boutonRadio2);
		hBoxBoutonRadio2.add(Box.createHorizontalGlue());
		groupeBoutons.add(boutonRadio2);

		// Choix d'un répertoire
		Box hBoxCreationFichier = Box.createHorizontalBox();
		hBoxCreationFichier.add(Box.createHorizontalStrut(20));
		nomRepertoire = new JTextField(50);
		nomRepertoire.setMaximumSize(nomRepertoire.getPreferredSize());		
		nomRepertoire.setEnabled(false);
		hBoxCreationFichier.add(nomRepertoire);
		hBoxCreationFichier.add(Box.createHorizontalStrut(10));
		boutonCreation = new JButton("Choisir un répertoire");
		boutonCreation.addActionListener(this);
		boutonCreation.setEnabled(false);
		hBoxCreationFichier.add(boutonCreation);
		
		// Nom du nouveau fichier
		Box hBoxNouveauFichier = Box.createHorizontalBox();
		hBoxNouveauFichier.add(Box.createHorizontalStrut(20));
		labelNouveauNom = new JLabel("Nom du nouveau fichier : ");
		labelNouveauNom.setEnabled(false);
		hBoxNouveauFichier.add(labelNouveauNom);
		hBoxNouveauFichier.add(Box.createHorizontalStrut(10));
		nomNouveauFichier = new JTextField(50);
		nomNouveauFichier.setMaximumSize(nomNouveauFichier.getPreferredSize());		
		nomNouveauFichier.setEnabled(false);
		nomNouveauFichier.addFocusListener(this);
		hBoxNouveauFichier.add(nomNouveauFichier);
		
		// Bouton de validation
		Box hBoxValidation = Box.createHorizontalBox();
		hBoxValidation.add(Box.createHorizontalGlue());
		boutonValidation = new JButton("Valider");
		boutonValidation.addActionListener(this);
		hBoxValidation.add(boutonValidation);
		hBoxValidation.add(Box.createHorizontalStrut(10));
		boutonAnnulation = new JButton("Annuler");
		boutonAnnulation.addActionListener(this);
		hBoxValidation.add(boutonAnnulation);
		
		// L'agencement vertical des boîtes horizontales 
		Box vBox = Box.createVerticalBox();
		vBox.add(hBoxBoutonRadio1);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(hBoxChoixFichier);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(hBoxBoutonRadio2);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(hBoxCreationFichier);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(hBoxNouveauFichier);
		vBox.add(Box.createVerticalStrut(25));
		vBox.add(hBoxValidation);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  
        contenu.add(vBox,BorderLayout.CENTER);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame fenetre1 = new ChoixFichierFrame();
		fenetre1.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==this.boutonChoix){// Choix d'un fichier à compléter
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("fichier XML", "xml");
			chooser.setFileFilter(filter);
			Properties prop = System.getProperties();
			String currentDirectory = prop.getProperty("user.dir");
			File repCourant = new File(currentDirectory);
			chooser.setCurrentDirectory(repCourant);
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                nomFichier.setText(file.toString());
                //On vérifie que le schéma du fichier xml est bien celui de la classe ConfigurationMethodesPeuplement
    			boolean ok = false;
                try {	
    				JAXBContext context = JAXBContext.newInstance(ConfigurationMethodesPeuplement.class);
    				String nomFichierXSD = "schemaMethodePeuplement.xsd";
    				String nomChemin = new File("").getAbsolutePath();
    				context.generateSchema(new MySchemaOutputResolver(nomFichierXSD)); 
    				SchemaFactory schemaFactory =SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
    				Schema schema = schemaFactory.newSchema(new File(nomChemin+"\\"+nomFichierXSD));
    				Validator validator = schema.newValidator();
    				validator.validate(new StreamSource(nomFichier.getText()));
    				ok = true;
    			} catch (SAXException e1) {
    				logger.info("erreur du type : SAXException");
    			} catch (JAXBException e1) {
    				logger.info("erreur du type : JAXBException");
    			} catch (IOException e1) {
    				logger.info("erreur du type : IOException");
    			}
    			// Si le fichier n'est pas ok on efface le fichier et on affiche un message d'erreur
    			if (!ok){
    				nomFichier.setText("");
					JOptionPane.showMessageDialog(null, "Sélectionnez un nouveau fichier", "Ce fichier ne contient pas de méthodes de peuplement", JOptionPane.WARNING_MESSAGE);
    			}
            } 
		}else if(e.getSource()==this.boutonCreation){// Création d'un nouveau fichier
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("fichier XML", "xml");
			chooser.setFileFilter(filter);
			Properties prop = System.getProperties();
			String currentDirectory = prop.getProperty("user.dir");
			File repCourant = new File(currentDirectory);
			chooser.setCurrentDirectory(repCourant);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                nomRepertoire.setText(file.toString());
            } 
		}else if(e.getSource()==this.boutonRadio1){
			if (boutonRadio1.isSelected()){
				// On désactive les composants qui correspondent à l'autre bouton
				boutonCreation.setEnabled(false);
				nomRepertoire.setEnabled(false);
				nomNouveauFichier.setEnabled(false);
				labelNouveauNom.setEnabled(false);
				// On active les composants qui correspondent à ce bouton 
				boutonChoix.setEnabled(true);
				nomFichier.setEnabled(true);
			}
		}else if(e.getSource()==this.boutonRadio2){
			if (boutonRadio2.isSelected()){
				// On désactive les composants qui correspondent à l'autre bouton
				boutonChoix.setEnabled(false);
				nomFichier.setEnabled(false);
				// On active les composants qui correspondent à ce bouton 
				boutonCreation.setEnabled(true);
				nomRepertoire.setEnabled(true);
				nomNouveauFichier.setEnabled(true);
				labelNouveauNom.setEnabled(true);
			}
		}else if(e.getSource()==this.boutonValidation){
			boolean ok = true;
			File dossier=new File(nomRepertoire.getText());
			if ((((!dossier.exists() && !dossier.isDirectory())||(nomNouveauFichier.getText().isEmpty()))
					&& boutonRadio2.isSelected())||((nomFichier.getText().isEmpty())&&boutonRadio1.isSelected())){
				JOptionPane.showMessageDialog(null, "Aucun fichier n'est sélectionné", "Les champs sont mal remplis", JOptionPane.INFORMATION_MESSAGE);
				ok = false;
			}
			if (ok){
				// Récupération du nom complet du fichier
				String nomCheminFichier = "";
				if (boutonRadio1.isSelected()){// Si le fichier existe et va être complété
					nomCheminFichier = nomFichier.getText();
				}else{
					nomCheminFichier = nomRepertoire.getText()+"\\"+nomNouveauFichier.getText();
					// Création du nouveau fichier
					ConfigurationMethodesPeuplement configuration = new ConfigurationMethodesPeuplement();
					configuration.marshall(nomCheminFichier);
				}
				// Appel de la fenêtre de gestion des méthodes de peuplement
				JFrame fenetre2 = new ListeMethodesPeuplementFrame(nomCheminFichier,this.getIconImage());
				fenetre2.setVisible(true);
				// Fermeture de la fenêtre
				setVisible(false);	
				dispose();
			}
		}else if(e.getSource()==this.boutonAnnulation){
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource()==this.nomNouveauFichier){
			if (!nomNouveauFichier.getText().isEmpty()){
				String nom = nomNouveauFichier.getText();
				if (!nom.endsWith(".xml")){
					nomNouveauFichier.setText(nom+".xml");
				}
			}
		}
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
	}
	
	class MySchemaOutputResolver extends SchemaOutputResolver {
		File baseDir = new File(".");
		String nomFichierXSD = "";
		public MySchemaOutputResolver(String nomFich) {nomFichierXSD = nomFich;}
		@Override
		public Result createOutput(String namespaceUri,String suggestedFileName) 
		throws IOException {return new StreamResult(new File(baseDir, nomFichierXSD));}
	}
}
