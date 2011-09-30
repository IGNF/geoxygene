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

package fr.ign.cogit.appli.geopensim.appli.comparaison;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.macro.EtatGlobal;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.appli.tracking.SimulationTreeFrame;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * @author Florence Curie
 *
 */
public class ChoixEtatComparaison extends JFrame implements ActionListener, FocusListener{
	private static final long serialVersionUID = -2687762356288261250L;
	private static final Logger logger = Logger.getLogger(ChoixEtatComparaison.class.getName());
	private JButton boutonValidation,boutonAnnulation,boutonSelection,boutonChoixEtat1,boutonChoixEtat2;
	JRadioButton partieCommune,partieSelectionnee,sansDelimitation;
	private JTextField nomEtat1,nomEtat2,etatFocus;
	JFrame parent;
	List<AgentZoneElementaireBatie> listeZESelect = new ArrayList<AgentZoneElementaireBatie>();
	JLabel nbIlotsText;
	String texte = "Nb îlots sélectionnés : ";
	ChoixEtatSelection fenetre5 = null;
	EtatGlobal etatSelect;
	List<EtatGlobal> listeEtats;
	EtatGlobal etatVisu;
	EtatGlobal etat1,etat2;

	// Constructeur
	public ChoixEtatComparaison(final JFrame parent,List<EtatGlobal> listeEtats,EtatGlobal etatVisu){

		super();
		this.parent = parent;
		this.listeEtats = listeEtats;
		this.etatVisu = etatVisu;
		// La fenêtre
		this.setTitle("Choix des états à comparer");
		this.setBounds(50, 100, 400, 280);
		this.setResizable(false);
		this.setIconImage(new ImageIcon(GeOpenSimApplication.class.getResource("/geopensim-icon.gif")).getImage());
		Container contenu = this.getContentPane();
		
		// Arrêt du programme sur fermeture de la fenêtre
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				// Fermeture de la fenêtre
				setVisible(false);	
				dispose();
			}
		});

		// Le premier état
		Box hBoxChoix1 = Box.createHorizontalBox();
		hBoxChoix1.add(new JLabel("Etat 1 : "));
		hBoxChoix1.add(Box.createHorizontalStrut(5));
		nomEtat1 = new JTextField(50);
		nomEtat1.setText("");
		nomEtat1.setMaximumSize(nomEtat1.getPreferredSize());
		nomEtat1.addFocusListener(this);
		hBoxChoix1.add(nomEtat1);
		boutonChoixEtat1 = new JButton("choix état 1");
		boutonChoixEtat1.addActionListener(this);
		hBoxChoix1.add(Box.createHorizontalStrut(10));
		hBoxChoix1.add(boutonChoixEtat1);
		hBoxChoix1.add(Box.createHorizontalStrut(10));
		
		// Le deuxième état
		Box hBoxChoix2 = Box.createHorizontalBox();
		hBoxChoix2.add(new JLabel("Etat 2 : "));
		hBoxChoix2.add(Box.createHorizontalStrut(5));
		nomEtat2 = new JTextField(50);
		nomEtat2.setText("");
		nomEtat2.setMaximumSize(nomEtat2.getPreferredSize());
		nomEtat2.addFocusListener(this);
		hBoxChoix2.add(nomEtat2);
		boutonChoixEtat2 = new JButton("choix état 2");
		boutonChoixEtat2.addActionListener(this);
		hBoxChoix2.add(Box.createHorizontalStrut(10));
		hBoxChoix2.add(boutonChoixEtat2);
		hBoxChoix2.add(Box.createHorizontalStrut(10));
		
		// Choix de la zone de comparaison
		JPanel panneau = new JPanel();
		panneau.setLayout(new javax.swing.BoxLayout(panneau, BoxLayout.Y_AXIS));
		Box hBoxTitre = Box.createHorizontalBox();
		hBoxTitre.add(new JLabel(" Choix de la zone de comparaison "));
		hBoxTitre.add(Box.createHorizontalGlue());
		panneau.add(hBoxTitre);
		panneau.add(Box.createVerticalStrut(5));
		// Alternative 1
		Box hBoxAlt1 = Box.createHorizontalBox();
		hBoxAlt1.add(Box.createHorizontalStrut(10));
		partieCommune = new JRadioButton("Partie commune aux deux états");
		partieCommune.addActionListener(this);
		hBoxAlt1.add(partieCommune);
		hBoxAlt1.add(Box.createHorizontalGlue());
		panneau.add(hBoxAlt1);
		// Alternative 2
		Box hBoxAlt2 = Box.createHorizontalBox();
		hBoxAlt2.add(Box.createHorizontalStrut(10));
		partieSelectionnee = new JRadioButton("Définir une zone en sélectionnant des îlots");
		partieSelectionnee.addActionListener(this);
		hBoxAlt2.add(partieSelectionnee);
		boutonSelection = new JButton("Sélection ...");
		boutonSelection.addActionListener(this);
		hBoxAlt2.add(boutonSelection);
		hBoxAlt2.add(Box.createHorizontalGlue());
		panneau.add(hBoxAlt2);
		Box hBoxTexte = Box.createHorizontalBox();
		hBoxTexte.add(Box.createHorizontalStrut(32));
		nbIlotsText = new JLabel(texte);
		hBoxTexte.add(nbIlotsText);
		hBoxTexte.add(Box.createHorizontalGlue());
		panneau.add(hBoxTexte);
		// Alternative 3
		Box hBoxAlt3 = Box.createHorizontalBox();
		hBoxAlt3.add(Box.createHorizontalStrut(10));
		sansDelimitation = new JRadioButton("Tout (zones différentes)");
		sansDelimitation.addActionListener(this);
		hBoxAlt3.add(sansDelimitation);
		hBoxAlt3.add(Box.createHorizontalGlue());
		panneau.add(hBoxAlt3);
		ButtonGroup grouperadio = new ButtonGroup();
		grouperadio.add(partieCommune);
		grouperadio.add(partieSelectionnee);
		grouperadio.add(sansDelimitation);
		partieCommune.setSelected(true);
		boutonSelection.setEnabled(false);
		
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
		vBox.add(hBoxChoix1);
		vBox.add(hBoxChoix2);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(panneau);
		vBox.add(Box.createVerticalStrut(20));
		vBox.add(hBoxValidation);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  
        contenu.add(vBox,BorderLayout.CENTER);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.partieSelectionnee){
			boutonSelection.setEnabled(true);
		}else if(e.getSource()==this.sansDelimitation){
			boutonSelection.setEnabled(false);
		}else if(e.getSource()==this.partieCommune){
			boutonSelection.setEnabled(false);
		}else if(e.getSource()==this.boutonSelection){
			if((etat1!=null)&&(etat2!=null)){
				// On ouvre une fenêtre
				fenetre5 = new ChoixEtatSelection(this, etat1, etat2);
				fenetre5.setVisible(true);
				fenetre5.setAlwaysOnTop(true);
				this.setVisible(false);
			}else{
				logger.info("il faut choisir des états à comparer");
				JOptionPane.showMessageDialog(this, "il faut choisir des états à comparer");
			}
		}else if(e.getSource()==this.boutonValidation){
			if ((etat1!=null)&&(etat2!=null)){
			    IGeometry zoneEtat1 = null;
				IGeometry zoneEtat2 = null;
				IGeometry zoneCommune = null;

				if(this.partieSelectionnee.isSelected()){
					for (AgentZoneElementaireBatie zone:listeZESelect){
						ZoneElementaireUrbaine rep = null;
						if(!etatSelect.isSimule()){
							rep = (ZoneElementaireUrbaine)zone.getRepresentation(etatSelect.getDate());
						}else{
							rep = (ZoneElementaireUrbaine)zone.getRepresentation(etatSelect.getDate(),etatSelect.getId());
						}
						if((rep!=null)&&(rep.getGeom()!=null)){
							if(zoneCommune==null)zoneCommune = rep.getGeom();
							else zoneCommune = zoneCommune.union(rep.getGeom());
						}
					}
				}else{
					for (AgentGeographique agent:etat1.getCollection()){
						if(agent instanceof AgentZoneElementaireBatie){
							ZoneElementaireUrbaine rep = null;
							if(!etat1.isSimule()){
								rep = (ZoneElementaireUrbaine)agent.getRepresentation(etat1.getDate());
							}else{
								rep = (ZoneElementaireUrbaine)agent.getRepresentation(etat1.getDate(),etat1.getId());
							}
							if((rep!=null)&&(rep.getGeom()!=null)&&(!rep.getBordeUniteUrbaine())){
								if(zoneEtat1==null)zoneEtat1 = rep.getGeom();
								else zoneEtat1 = zoneEtat1.union(rep.getGeom());
							}
						}
					}
					for (AgentGeographique agent:etat2.getCollection()){
						if(agent instanceof AgentZoneElementaireBatie){
							ZoneElementaireUrbaine rep = null;
							if(!etat2.isSimule()){
								rep = (ZoneElementaireUrbaine)agent.getRepresentation(etat2.getDate());
							}else{
								rep = (ZoneElementaireUrbaine)agent.getRepresentation(etat2.getDate(),etat2.getId());
							}
							if((rep!=null)&&(rep.getGeom()!=null)&&(!rep.getBordeUniteUrbaine())){
								if(zoneEtat2==null) zoneEtat2 = rep.getGeom();
								else zoneEtat2 = zoneEtat2.union(rep.getGeom());
							}
						}
					}
					if(this.partieCommune.isSelected()){
						if((zoneEtat1!=null)&&(zoneEtat2!=null))zoneCommune = zoneEtat1.intersection(zoneEtat2);
					}else if (this.sansDelimitation.isSelected()){
						if((zoneEtat1!=null)&&(zoneEtat2!=null))zoneCommune = zoneEtat1.union(zoneEtat2);
					}
				}
				logger.info(zoneCommune);
				if (zoneCommune!=null){
					logger.info(zoneCommune.getClass());
					if(zoneCommune instanceof GM_Aggregate){
						GM_Object zoneCommune2 = null;
						for (GM_Object zoneC:((GM_Aggregate<GM_Object>)zoneCommune).getList()){
							if((zoneC instanceof GM_Polygon)){
								if (zoneCommune2==null)zoneCommune2 = zoneC;
								else zoneCommune2.union(zoneC);
							}
						}
						zoneCommune = zoneCommune2;
					}
				}
				if (zoneCommune!=null){
					ComparaisonDialog fenetreStat = new ComparaisonDialog(this,etat1,etat2,zoneCommune);
					fenetreStat.setVisible(true);
					fenetreStat.setAlwaysOnTop(true);
					// Fermeture de la fenêtre
					setVisible(false);	
					dispose();
				}else{
					logger.info("La zone de comparaison est vide");
					JOptionPane.showMessageDialog(this, "La zone de comparaison est vide");
				}
			}else{
				logger.info("il faut choisir des états à comparer");
				JOptionPane.showMessageDialog(this, "il faut choisir des états à comparer");
			}
		 }else if(e.getSource()==this.boutonAnnulation){
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if(e.getSource()==this.boutonChoixEtat1){
			etatFocus = nomEtat1;
			SimulationTreeFrame simulFrame = new SimulationTreeFrame(this,listeEtats,etatVisu);
			simulFrame.setEnabledBouton(false);
			simulFrame.setVisible(true);
			simulFrame.setAlwaysOnTop(true);
		}else if(e.getSource()==this.boutonChoixEtat2){
			etatFocus = nomEtat2;
			SimulationTreeFrame simulFrame = new SimulationTreeFrame(this,listeEtats,etatVisu);
			simulFrame.setEnabledBouton(false);
			simulFrame.setVisible(true);
			simulFrame.setAlwaysOnTop(true);
		}
	}

	/**
	 * @param etat l'état sélectionné
	 */
	public void setEtatSelect(EtatGlobal etat) {
		if (etatFocus==nomEtat1){
			this.nomEtat1.setText(etat.getNom());
			etat1 = etat;
		}else if (etatFocus==nomEtat2){
			this.nomEtat2.setText(etat.getNom());
			etat2 = etat;
		}
	}
	
	/**
	 * @return la fenêtre précédente
	 */
	public JFrame getPreviousFrame() {
		return this.parent;
	}
	
	public void getSelection(){
		this.setVisible(true);
		etatSelect = fenetre5.getEtat();
		logger.info(etatSelect.getNom());
		MainFrame mainF = (MainFrame)this.parent;
		Set<IFeature> selectedFeatures=mainF.getSelectedProjectFrame().getLayerViewPanel().getSelectedFeatures();
		listeZESelect = new ArrayList<AgentZoneElementaireBatie>();
		for (IFeature feat:selectedFeatures){
			if(feat instanceof AgentZoneElementaireBatie){
				listeZESelect.add((AgentZoneElementaireBatie)feat);
			}
		}
		nbIlotsText.setText(texte+listeZESelect.size());
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource()==nomEtat1){
			etatFocus = nomEtat1;
		}else if(e.getSource()==nomEtat2){
			etatFocus = nomEtat2;
		}
	}

	@Override
	public void focusLost(FocusEvent e) {}

}
