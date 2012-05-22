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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationComparaison;
import fr.ign.cogit.appli.geopensim.ConfigurationComparaison.ParametresComparaison;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.macro.EtatGlobal;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.comparaison.Comparaison;
import fr.ign.cogit.appli.geopensim.feature.meso.GroupeBatiments;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * @author Florence Curie
 *
 */
public class ComparaisonDialog extends JFrame implements ActionListener{

	private static final long serialVersionUID = -8936353827186574005L;
	private static final Logger logger = Logger.getLogger(ComparaisonDialog.class.getName());
	private JButton boutonFermer,boutonCalculer;
	JFrame parent;
	IGeometry zone;
	private JComboBox comboParametrage;
	private JLabel texte;
	private ConfigurationComparaison configuration = null;
	private Object[][] data = null;
	private EtatGlobal etat1, etat2;
	private String[] columnNames = {"","opération","pondération","","","diffNorm"};
	private DecimalFormat f = new DecimalFormat();
	private List<ZoneElementaireUrbaine> listeZEEtat1 = null;
	private List<ZoneElementaireUrbaine> listeZEEtat2 = null;
	private JTable table = null;
	private JScrollPane scrollPane = null;

	// Constructeur
	public ComparaisonDialog(final JFrame parent,EtatGlobal et1,EtatGlobal et2,IGeometry zoneEtude){

		super();
		this.parent = parent;
		this.zone = zoneEtude;
		this.etat1 = et1;
		this.etat2 = et2;
		f.setMaximumFractionDigits(2);

		// La fenêtre
		this.setTitle("Comparaison des états");
		this.setBounds(50, 100, 600, 300);
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

		// On vérifie que chaque ZE est entière sinon on requalifie la zone
		listeZEEtat1 = this.selectionZonesElementaires(etat1);
		listeZEEtat2 = this.selectionZonesElementaires(etat2);

		// On récupère les paramètres de comparaison à appliquer
		configuration = ConfigurationComparaison.getInstance();
		HashMap<String,List<ParametresComparaison>> listeParametrages = configuration.getListType();

		// Création d'une combobox permettant de choisir le paramétrage
		String[] listeParam = new String[listeParametrages.size()];
		int compt=0;
		for (String param : listeParametrages.keySet()){
			listeParam[compt++] = param;
		}
		comboParametrage = new JComboBox(listeParam);
		comboParametrage.setMaximumSize(comboParametrage.getPreferredSize());	
		comboParametrage.setSelectedIndex(0);
		comboParametrage.addActionListener(this);
		Box hBoxParametrage = Box.createHorizontalBox();
		hBoxParametrage.add(Box.createHorizontalStrut(10));
		hBoxParametrage.add(new JLabel("Paramètrage à appliquer pour le calcul de distance : "));
		hBoxParametrage.add(comboParametrage);
		hBoxParametrage.add(Box.createHorizontalStrut(10));
		boutonCalculer = new JButton("Calculer");
		boutonCalculer.addActionListener(this);
		hBoxParametrage.add(boutonCalculer);
		hBoxParametrage.add(Box.createHorizontalGlue());

		// Création des noms de colonnes
		columnNames[3] = etat1.getNom();
		columnNames[4] = etat2.getNom();
		data = new Object[1][columnNames.length];

		// Création de la Jtable
		table = new JTable(data, columnNames);
		scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		table.setAlignmentY(CENTER_ALIGNMENT);
		centrerTable(table);

		// Affichage de la distance
		Box hBoxDistance = Box.createHorizontalBox();
		texte = new JLabel("la distance entre les états : ");
		hBoxDistance.add(texte);//+f.format(distance)));
		hBoxDistance.add(Box.createHorizontalGlue());

		// Bouton de validation
		Box hBoxFermer = Box.createHorizontalBox();
		hBoxFermer.add(Box.createHorizontalGlue());
		boutonFermer = new JButton("Fermer");
		boutonFermer.addActionListener(this);
		hBoxFermer.add(boutonFermer);
		hBoxFermer.add(Box.createHorizontalStrut(10));

		// L'agencement vertical des boîtes horizontales 
		Box vBox = Box.createVerticalBox();
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(hBoxParametrage);
		vBox.add(Box.createVerticalStrut(15));
		vBox.add(scrollPane);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBoxDistance);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBoxFermer);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		contenu.add(vBox,BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.boutonFermer){
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if (e.getSource()==this.boutonCalculer){
			// On récupère la liste des paramètres de configuration
			List<ParametresComparaison> listeParametres = configuration.getParametres((String) comboParametrage.getSelectedItem());
			String[] nomMethodes = new String[listeParametres.size()];
			String[] nomOperations = new String[listeParametres.size()];
			Double[] numPonderation = new Double[listeParametres.size()];
			for (int i=0;i<listeParametres.size();i++){
				nomMethodes[i] = listeParametres.get(i).getMethode();
				nomOperations[i] = listeParametres.get(i).getTypeOperation();
				numPonderation[i] = listeParametres.get(i).getPonderation();
			}

			// Remplissage du tableau de données
			data = new Object[nomMethodes.length][columnNames.length];
			for (int i=0;i<nomMethodes.length;i++){
				data[i][0] = nomMethodes[i].replaceFirst("get", "");
			}
			for (int i=0;i<nomOperations.length;i++){
				data[i][1] = nomOperations[i];
			}
			for (int i=0;i<numPonderation.length;i++){
				data[i][2] = numPonderation[i];
			}

			// Création de la liste des méthodes
			List<Method> listeMethodes = new ArrayList<Method>();
			for (int i=0;i<nomMethodes.length;i++){
				String nom = nomMethodes[i];
				try {
					Method methode = ZoneElementaireUrbaine.class.getMethod(nom,(Class[]) null);
					logger.debug(methode);
					listeMethodes.add(methode);
				} catch (SecurityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NoSuchMethodException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			// Calcul des valeurs agrégées
			List<Double> listeValAgreEtat1 = new ArrayList<Double>();
			List<Double> listeValAgreEtat2 = new ArrayList<Double>();
			List<Double> listeDiffNorm = new ArrayList<Double>();
			for (int i=0;i<listeMethodes.size();i++){
				Method methode=listeMethodes.get(i);
				String operation = nomOperations[i];
				double valeurAgregeeEtat1 = Comparaison.CalculValeurAgregee(methode, operation, listeZEEtat1);
				double valeurAgregeeEtat2 = Comparaison.CalculValeurAgregee(methode, operation, listeZEEtat2);
				// On normalise
				double diff = Math.abs(valeurAgregeeEtat1-valeurAgregeeEtat2);
				double diffNorm = diff/valeurAgregeeEtat1;
				if(diffNorm>1)diffNorm = 1;
				// On remplit la liste
				listeValAgreEtat1.add(valeurAgregeeEtat1);
				listeValAgreEtat2.add(valeurAgregeeEtat2);
				listeDiffNorm.add(diffNorm);
				// On remplit le tableau
				data[i][3] = f.format(valeurAgregeeEtat1);
				data[i][4] = f.format(valeurAgregeeEtat2);
				data[i][5] = f.format(diffNorm);
			}
			table = new JTable(data, columnNames);
			scrollPane.setViewportView(table);

			// Calcul de la distance entre états
			List<Double> listePonderations = new ArrayList<Double>();
			for (int i = 0;i<numPonderation.length;i++){
				listePonderations.add(numPonderation[i]);
			}
//			double distance = Comparaison.CalculDistance(listeValAgreEtat1, listeValAgreEtat2, listePonderations);
			double distance = Comparaison.CalculDistance(listeDiffNorm, listePonderations);
			logger.info("distance entre les états : "+distance);
			texte.setText("la distance entre les états : " +f.format(distance));
		}
	}

	@SuppressWarnings("unchecked")
	public List<ZoneElementaireUrbaine> selectionZonesElementaires(EtatGlobal etat){
		List<ZoneElementaireUrbaine> listeZEEtat = new ArrayList<ZoneElementaireUrbaine>();
		for (AgentGeographique agent:etat.getCollection()){
			if(agent instanceof AgentZoneElementaireBatie){
				ZoneElementaireUrbaine rep = null;
				if(!etat2.isSimule()){
					rep = (ZoneElementaireUrbaine)agent.getRepresentation(etat.getDate());
				}else{
					rep = (ZoneElementaireUrbaine)agent.getRepresentation(etat.getDate(),etat.getId());
				}
				if((rep!=null)&&(rep.getGeom()!=null)){
					if(zone.contains(rep.getGeom())){
						rep.qualifier();
						listeZEEtat.add(rep);
					}
					if(zone.intersectsStrictement(rep.getGeom())){
					    IGeometry surfaceIntersection = zone.intersection(rep.getGeom());
						if(surfaceIntersection instanceof GM_Polygon){
							if(surfaceIntersection.area()>=100){
								ZoneElementaireUrbaine zoneE = creationZoneElementaire((GM_Polygon)surfaceIntersection, rep);
								listeZEEtat.add(zoneE);
							}
						}else if(surfaceIntersection instanceof GM_MultiSurface){
							for (GM_Polygon intersect :((GM_MultiSurface<GM_Polygon>) surfaceIntersection).getList()) {
								if(intersect.area()>=100){
									ZoneElementaireUrbaine zoneE = creationZoneElementaire(intersect, rep);
									listeZEEtat.add(zoneE);
								}
							}
						}else if(surfaceIntersection instanceof GM_Aggregate){
							for (GM_Object intersect:((GM_Aggregate<GM_Object>)surfaceIntersection).getList()){
								if((intersect instanceof GM_Polygon)){
									if(intersect.area()>=100){
										ZoneElementaireUrbaine zoneE = creationZoneElementaire((GM_Polygon)intersect, rep);
										listeZEEtat.add(zoneE);
									}
								}
							}
						} 
					}
				}
			}
		}
		return listeZEEtat;
	}

	private void centrerTable(JTable tab) {     
		DefaultTableCellRenderer custom = new DefaultTableCellRenderer();
		custom.setHorizontalAlignment(JLabel.CENTER);
		for (int i=1 ; i<tab.getColumnCount() ; i++)
			tab.getColumnModel().getColumn(i).setCellRenderer(custom);
	}

	private ZoneElementaireUrbaine creationZoneElementaire(GM_Polygon surfaceIntersection,ZoneElementaireUrbaine rep){
		ZoneElementaireUrbaine zoneE = new ZoneElementaireUrbaine((GM_Polygon)surfaceIntersection);
		for (GroupeBatiments groupe :rep.getGroupesBatiments()){
//			for (Batiment bati:groupe.getBatiments()){
//				if(zoneE.getGeom().contains(bati.getGeom())){
//					zoneE.addBatiment(bati);
//				}
//			}
		  GroupeBatiments groupeE = new GroupeBatiments(zoneE, groupe.getBatiments(),
		      groupe.getGeometrie());
		  zoneE.addGroupeBatiments(groupeE);
		  groupeE.setDateSourceSaisie(rep.getDateSourceSaisie());
		  groupeE.construireAlignements();
		}
		for (Troncon tronc:rep.getTroncons()){
			if(tronc.getGeom().distance(zoneE.getGeom())<1){
				zoneE.addTroncon(tronc);
			}
		}
		zoneE.construireEspacesVides();
//		zoneE.construireGroupes();
		zoneE.qualifier();
		return zoneE;
	}

}
