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

package fr.ign.cogit.appli.geopensim.appli.tracking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultGraphSelectionModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphSelectionModel;

import fr.ign.cogit.appli.geopensim.agent.macro.EtatGlobal;
import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.appli.comparaison.ChoixEtatComparaison;
import fr.ign.cogit.appli.geopensim.appli.comparaison.ChoixEtatVisualisation;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;



/**
 * @author Florence Curie
 *
 */
public class SimulationTreeFrame extends JFrame implements ActionListener,FocusListener{

	private static final long serialVersionUID = -164704492228050289L;
	static Logger logger=Logger.getLogger(SimulationTreeFrame.class.getName());

	private Map<Integer,Double> mapDate = new HashMap<Integer, Double>();
	private Map<EtatGlobal,DefaultGraphCell> mapEtatCell = new HashMap<EtatGlobal,DefaultGraphCell>();
	private EtatGlobal etatVisu = null;
	private List<EtatGlobal> listeE = new ArrayList<EtatGlobal>();
	private JToolBar toolBar = new JToolBar("Barre d'outils");
	private JButton boutonVisu = new JButton("Visualiser");
	private JButton boutonSuppr = new JButton("supprimer");
	private JGraph graph; 
	private JFrame parent;
	String nomEtatSelect;
	ChoixEtatVisualisation fenetre2=null;
	private String choix = "";
	Container contenu;
	JScrollPane myScrollPane;
	boolean test = true;

	public DefaultGraphCell CreationCelluleEtat(EtatGlobal etat){
		// Création de la cellule
		double valeurDefault = 20;
		DefaultGraphCell cell = new DefaultGraphCell("  "+etat.getNom()+"  ");
		double positionY = mapDate.get(etat.getDate());
		if (etat.isSimule()){
			// On calcule la position en X de la cellule 
			double positionX = 0;
			DefaultGraphCell celluleParent = mapEtatCell.get(etat.getEtatPrecedent());
			Rectangle2D rectangleParent = GraphConstants.getBounds(celluleParent.getAttributes());
			positionX = rectangleParent.getMaxX()+45;
			// On crée la cellule
			Rectangle2D rectangle = new Rectangle2D.Double(positionX, positionY, valeurDefault, valeurDefault);
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.RED));
			GraphConstants.setForeground(cell.getAttributes(), Color.RED);
			GraphConstants.setBounds(cell.getAttributes(), rectangle);
			GraphConstants.setAutoSize(cell.getAttributes(), true);
			Rectangle2D rectangleCellule = GraphConstants.getBounds(cell.getAttributes());
			// On modifie la position de la cellule si elle intersecte une autre cellule
			for (EtatGlobal etatG : mapEtatCell.keySet()){
				DefaultGraphCell cellule = mapEtatCell.get(etatG);
				Rectangle2D rectangleExistant = GraphConstants.getBounds(cellule.getAttributes());
				boolean intersect = rectangleExistant.intersects(rectangleCellule);
				if (intersect){
					positionX = rectangleExistant.getMaxX()+45;
					rectangleCellule = new Rectangle2D.Double(positionX, positionY, valeurDefault, valeurDefault);
					GraphConstants.setBounds(cell.getAttributes(), rectangleCellule);
				}
			}
		} else{
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.BLACK));
			GraphConstants.setForeground(cell.getAttributes(), Color.BLACK);
			GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(70, positionY, valeurDefault, valeurDefault));
		}
		// Si l'état est l'état visualisé  
		if (etat.equals(etatVisu)){
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(GraphConstants.getBorderColor(cell.getAttributes()),2));
			GraphConstants.setBackground(cell.getAttributes(), Color.LIGHT_GRAY);
		}
		// Paramètres communs aux cellules
		GraphConstants.setOpaque(cell.getAttributes(), true);
		GraphConstants.setAutoSize(cell.getAttributes(), true);
		GraphConstants.setEditable(cell.getAttributes(), false);
		GraphConstants.setMoveableAxis(cell.getAttributes(), 1);
		GraphConstants.setSizeable(cell.getAttributes(), false);
		// Ajout de deux ports
		DefaultPort port0 = new DefaultPort();
		cell.add(port0);
		port0.setParent(cell);
		Point2D point0 = new Point2D.Double(0,GraphConstants.PERMILLE/2);
		GraphConstants.setOffset(port0.getAttributes(),point0);
		DefaultPort port1 = new DefaultPort();
		cell.add(port1);
		port1.setParent(cell);
		Point2D point1 = new Point2D.Double(GraphConstants.PERMILLE,GraphConstants.PERMILLE/2);
		GraphConstants.setOffset(port1.getAttributes(),point1);
		// Ajout de la cellule crée à la map
		mapEtatCell.put(etat, cell);
		return cell;
	}
	
	public DefaultGraphCell CreationCelluleSansBord(String nom,Rectangle2D rect){
		DefaultGraphCell cell = new DefaultGraphCell(nom);
		GraphConstants.setBounds(cell.getAttributes(), rect);
		GraphConstants.setEditable(cell.getAttributes(), false);
		GraphConstants.setMoveable(cell.getAttributes(), false);
		GraphConstants.setSizeable(cell.getAttributes(), false);
		GraphConstants.setSelectable(cell.getAttributes(), false);
		// Ajout d'un port
		DefaultPort port = new DefaultPort();
		cell.add(port);
		port.setParent(cell);
		return cell;
	}
	
	// Constructeur
	public SimulationTreeFrame(JFrame mainFrame,List<EtatGlobal> listeEtats,EtatGlobal etatVisu){

		super();
		this.parent = mainFrame;
		this.etatVisu = etatVisu;
		this.listeE = listeEtats;
		// Création de la fenêtre principale
		this.setIconImage(mainFrame.getIconImage());
		this.setTitle("Arbre des etats");
		this.setBounds(50, 100, 420, 550);
		this.setResizable(true);
		contenu = this.getContentPane();
		
		// Arrêt du programme sur fermeture de la fenêtre
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);	
				dispose();
			}
		});
		
		// La barre d'outils  
		boutonVisu.addActionListener(this);
		toolBar.add(boutonVisu);
		boutonSuppr.addActionListener(this);
		toolBar.add(boutonSuppr);
		this.add(toolBar,BorderLayout.PAGE_START);
		
		// Création de la liste des dates
		List<Integer> dates = new ArrayList<Integer>();
		for (EtatGlobal etat:listeEtats){dates.add(etat.getDate());}
		Collections.sort(dates);
		
		// Création du graphe 
		GraphModel model = new DefaultGraphModel();
		graph = new JGraph(model);
		myScrollPane = new JScrollPane(graph);
		
		// Calcul des positions en y correspondant aux dates
		mapDate = new HashMap<Integer, Double>();
		int dateMin = dates.get(0);
		int dateMax = dates.get(dates.size()-1);
		double espaceDisponible = this.getHeight()-170;
		double unite = espaceDisponible/(dateMax-dateMin);
		double valInitiale = espaceDisponible;
		for (int date :dates){
			double x = valInitiale - (date-dateMin)*unite;
			mapDate.put(date, x+40);
		}
		
		// Calcul des positions en Y des années le long de l'axe
		int dateMinAxe = (int)Math.round((double)dateMin/10)*10;
		int dateMaxAxe = (int)Math.round((double)dateMax/10)*10;
		int dateAxe = dateMinAxe;
		Map<Integer, Double> mapDateAxe = new HashMap<Integer, Double>();
		while (dateAxe<=dateMaxAxe){
			double x = valInitiale - (dateAxe-dateMin)*unite;
			mapDateAxe.put(dateAxe, x+40);
			dateAxe+=10;
		}
		
		// Création des dates de l'axe
		for (int date : mapDateAxe.keySet()){
			double position = mapDateAxe.get(date);
			DefaultGraphCell cell = CreationCelluleSansBord(((Integer)date).toString(), new Rectangle2D.Double(5, position, 50, 20));
			// Ajout des cellules au graphe
			graph.getGraphLayoutCache().insert(cell);
		}
		
		// Création des cellules de l'extrémité de l'axe
		List<DefaultGraphCell> cells2 = new ArrayList<DefaultGraphCell>();
		cells2.add(CreationCelluleSansBord(" ", new Rectangle2D.Double(40, mapDate.get(dateMax)-35, 40, 10)));
		cells2.add(CreationCelluleSansBord(" ", new Rectangle2D.Double(40, mapDate.get(dateMin)+30, 40, 10)));
		for (DefaultGraphCell cell:cells2){graph.getGraphLayoutCache().insert(cell);}

		// Création des cellules états initiaux et simulés
		List<DefaultGraphCell> cells = new ArrayList<DefaultGraphCell>();
		for (EtatGlobal etat:listeEtats){
			DefaultGraphCell cell = CreationCelluleEtat(etat);
			cells.add(cell);
			// Ajout de la cellule au graphe
			graph.getGraphLayoutCache().insert(cell);
			// Création d'un lien si l'état est simulé
			if (etat.isSimule()){
				// Récupération de son parent
				EtatGlobal etatParent = etat.getEtatPrecedent();
				DefaultGraphCell cellParent = mapEtatCell.get(etatParent);
				// Création du lien
				DefaultEdge lienCellInitCellSimul = new DefaultEdge();
				lienCellInitCellSimul.setSource(cellParent.getChildAt(1));
				lienCellInitCellSimul.setTarget(cell.getChildAt(0));
				GraphConstants.setLineEnd(lienCellInitCellSimul.getAttributes(), GraphConstants.ARROW_TECHNICAL);
				GraphConstants.setEndFill(lienCellInitCellSimul.getAttributes(), true);
				GraphConstants.setEditable(lienCellInitCellSimul.getAttributes(), false);
				GraphConstants.setMoveable(lienCellInitCellSimul.getAttributes(), false);
				GraphConstants.setSizeable(lienCellInitCellSimul.getAttributes(), false);
				GraphConstants.setSelectable(lienCellInitCellSimul.getAttributes(), false);
				GraphConstants.setRouting(lienCellInitCellSimul.getAttributes(), GraphConstants.ROUTING_DEFAULT);
				// Ajout du lien au graphe
				graph.getGraphLayoutCache().insert(lienCellInitCellSimul);
			}
		}
		graph.addFocusListener(this);
		// Possibilité de sélectionner un seul élément à la fois
		DefaultGraphSelectionModel modele = new DefaultGraphSelectionModel(graph);
		modele.setSelectionMode(GraphSelectionModel.SINGLE_GRAPH_SELECTION);
		graph.setSelectionModel(modele);
		
		// Création de l'axe
		DefaultEdge axeTemps = new DefaultEdge();
		axeTemps.setSource(cells2.get(1).getChildAt(0));
		axeTemps.setTarget(cells2.get(0).getChildAt(0));
		GraphConstants.setLineEnd(axeTemps.getAttributes(), GraphConstants.ARROW_CLASSIC);
		GraphConstants.setEndFill(axeTemps.getAttributes(), true);
		GraphConstants.setEditable(axeTemps.getAttributes(), false);
		GraphConstants.setMoveable(axeTemps.getAttributes(), false);
		GraphConstants.setSizeable(axeTemps.getAttributes(), false);
		GraphConstants.setSelectable(axeTemps.getAttributes(), false);
		graph.getGraphLayoutCache().insert(axeTemps);
		graph.getGraphLayoutCache().setAutoSizeOnValueChange(true);

		// Ajout du graphe à la fenêtre	
		contenu.add(myScrollPane);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.boutonVisu) {
			choix = "visual";
			// On récupère les éléments sélectionnés
			Object[] list = graph.getSelectionCells();
			EtatGlobal etatSelection = null;
			for (int i = 0; i < list.length; i++) {
				if (list[i] instanceof DefaultGraphCell) {
					DefaultGraphCell cell = (DefaultGraphCell)list[i];
					for (EtatGlobal etatG:mapEtatCell.keySet()){
						if (mapEtatCell.get(etatG).equals(cell)){
							etatSelection = etatG;
						}
					}
				}
			}
			// Appel de la fenêtre de sélection des états
			if(etatSelection==null){
				this.setEnabledBouton(false);
				fenetre2 = new ChoixEtatVisualisation(this);
				fenetre2.setVisible(true);
				fenetre2.setAlwaysOnTop(true);
//				this.setEnabledBouton(false);
			}else{
				nomEtatSelect = etatSelection.getNom();
				this.affichageCarteEtArbre(nomEtatSelect);
			}
		}else if(e.getSource()==boutonSuppr){
			choix = "suppress";
			// On récupère les éléments sélectionnés
			Object[] list = graph.getSelectionCells();
			EtatGlobal etatSelection = null;
			for (int i = 0; i < list.length; i++) {
				if (list[i] instanceof DefaultGraphCell) {
					DefaultGraphCell cell = (DefaultGraphCell)list[i];
					for (EtatGlobal etatG:mapEtatCell.keySet()){
						if (mapEtatCell.get(etatG).equals(cell)){
							etatSelection = etatG;
						}
					}
				}
			}
			// Appel de la fenêtre de sélection des états
			if(etatSelection==null){
				this.setEnabledBouton(false);
				fenetre2 = new ChoixEtatVisualisation(this);
				fenetre2.setVisible(true);
				fenetre2.setAlwaysOnTop(true);
			}else{
				nomEtatSelect = etatSelection.getNom();
				this.setEtatSelect(nomEtatSelect);
			}
			
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() == this.graph) {
			//
			if((this.parent!=null)&&(parent instanceof ChoixEtatComparaison)){
				if(parent.isVisible()){ 
					// On récupère les éléments sélectionnés
					this.requestFocus();
					EtatGlobal etatSelection = getEtatSelect();
					if(etatSelection!=null){
						((ChoixEtatComparaison)parent).setEtatSelect(etatSelection);
						setVisible(false);	
						dispose();
					}
				}
			}
			
			// La fenêtre de choix de l'état à visualiser
			if (fenetre2!=null){
				if(fenetre2.isVisible()){ 
					// On récupère les éléments sélectionnés
					EtatGlobal etatSelection = getEtatSelect();
					if(etatSelection!=null){
						fenetre2.setEtatSelect(etatSelection.getNom());
						fenetre2.requestFocus();
					}
				}
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
	}
	
	public void affichageCarteEtArbre(String nom){
		// Mise en surbrillance de la cellule sélectionnée
		this.affichageArbre(nom);
		// Appel d'une fonction d'affichage
		((GeOpenSimApplication)((MainFrame)this.parent).getApplication()).affichageCarte(etatVisu);
	}
	
	public void affichageArbre(String nom){
		// Mise en surbrillance de la cellule sélectionnée
		for (EtatGlobal etatG:mapEtatCell.keySet()){
			if (etatG.getNom().equals(nom)){
				DefaultGraphCell celluleSelect = mapEtatCell.get(etatG);
				GraphConstants.setBorder(celluleSelect.getAttributes(), BorderFactory.createLineBorder(GraphConstants.getBorderColor(celluleSelect.getAttributes()),2));
				GraphConstants.setBackground(celluleSelect.getAttributes(), Color.LIGHT_GRAY);
				graph.getGraphLayoutCache().editCell(celluleSelect, celluleSelect.getAttributes());
				etatVisu = etatG;
			}else {
				DefaultGraphCell celluleNonSelect = mapEtatCell.get(etatG); 
				GraphConstants.setBorder(celluleNonSelect.getAttributes(), BorderFactory.createLineBorder(GraphConstants.getBorderColor(celluleNonSelect.getAttributes()),1));
				GraphConstants.setBackground(celluleNonSelect.getAttributes(), Color.WHITE);
				graph.getGraphLayoutCache().editCell(celluleNonSelect, celluleNonSelect.getAttributes());
			}
		}
	}

	public EtatGlobal getEtat(String nomEtat){
		EtatGlobal etatS = null;
		for (EtatGlobal etat:listeE){
			if (etat.getNom().equals(nomEtat)){
				etatS = etat;
			}
		}
		return etatS;
	}
	
	@SuppressWarnings("unchecked")
	public void setEtatSelect(String nomEtat){
		nomEtatSelect = nomEtat;
		if(choix=="visual"){
			this.affichageCarteEtArbre(nomEtatSelect);
			this.setEnabledBouton(true);
		}else if(choix=="suppress"){
			EtatGlobal etatASupprimer = this.getEtat(nomEtatSelect);
			if(!etatASupprimer.isSimule()){
				JOptionPane.showMessageDialog(this, "cet état n'est pas supprimable", "Cet état est réel", JOptionPane.INFORMATION_MESSAGE);
//				logger.info("cet état n'est pas supprimable : état réel");
			}else{
				boolean suppr = true;
				for (EtatGlobal etatC :listeE){
					if(etatC.getEtatPrecedent()==etatASupprimer) suppr = false;
				}
				if(suppr==false){
					JOptionPane.showMessageDialog(this, "cet état n'est pas supprimable", "Cet état est un état parent d'un autre état", JOptionPane.INFORMATION_MESSAGE);
//					logger.info("cet état n'est pas supprimable : état parent d'un autre état");
				}else{
					listeE.remove(this.getEtat(nomEtatSelect));
					((GeOpenSimApplication)((MainFrame)this.parent).getApplication()).suppressionEtatComboBox(etatASupprimer);
					Object[] t = graph.getSelectionCells();
					for (int i=0;i<t.length;i++){
						DefaultGraphCell cellule = ((DefaultGraphCell)t[i]);
						// On supprime les liens de la cellule sélectionnée
						List listePort = cellule.getChildren();
						for (int j=0;j<listePort.size();j++){
							if(listePort.get(j) instanceof DefaultPort){
								DefaultPort port = (DefaultPort)listePort.get(j);
								Object[] listeLien = port.getEdges().toArray();
								graph.getGraphLayoutCache().remove(listeLien);
							}
						}
					}
					// On supprime la cellule sélectionnée
					graph.getGraphLayoutCache().remove(t);
				}
			}
			this.setEnabledBouton(true);
		}
	}
	
	private EtatGlobal getEtatSelect(){
		Object[] list = graph.getSelectionCells();
		EtatGlobal etatSelection = null;
		for (int i = 0; i < list.length; i++) {
			if (list[i] instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell)list[i];
				for (EtatGlobal etatG:mapEtatCell.keySet()){
					if (mapEtatCell.get(etatG).equals(cell)){
						etatSelection = etatG;
					}
				}
			}
		}
		return etatSelection;
	}
	
	public void setEnabledBouton(boolean bool){
		boutonVisu.setEnabled(bool);
		boutonSuppr.setEnabled(bool);
	}
	
}
