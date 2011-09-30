/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 *
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut géographique National (the French
 * National Mapping Agency).
 *
 * See: http://oxygene-project.sourceforge.net
 *
 * Copyright (C) 2005 Institut géographique National
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

package fr.ign.cogit.appli.geopensim.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * @author am raimond
 *
 */
public class InterfaceParametrageSeuils extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private static boolean[] choixSelectionne = new boolean[3];
	private static JRadioButton boutonMemeLOD;
	private static JRadioButton boutonDifferentLOD;
	static JRadioButton boutonDonneesCrees;
	private static JButton boutonOK;
	private static JButton boutonAnnuler;
	private static boolean memeLOD = false, differentLOD = false,
			donneesCrees = false;/*, bdgCartes = false, bdgPhotos = false,
			bdgPhotosCartes = false, cartesPhotos = false;*/
	/**
	 * Methode qui renvoie les valeurs des seuils utilisés dans le processus d'appariement
	 * @return
	 * valeursSeuilsSelections[0] = le seuil de Sélection
	 * valeursSeuilsSelections[1] = le seuil pour le critére de positio ou distance surfacique
	 */
	public double[] choixSeuilsAppariementPonctuel() {
		double valeursSeuilsSelections[] = new double [2];
		boolean[] selectedActions = showDialogChoixDeDonnees();
		memeLOD = selectedActions[0];
		differentLOD = selectedActions[1];
		donneesCrees = selectedActions[2];
		// initialisation des seuils en fonction des caractéristiques des données choisies
		if (memeLOD) {
			valeursSeuilsSelections[0] = 30;
			valeursSeuilsSelections[1] = 20;
		} else if (differentLOD) {
			valeursSeuilsSelections[0] = 1000;
			valeursSeuilsSelections[1] = 800;
		} else if (donneesCrees){
			valeursSeuilsSelections[0] = 30;
			valeursSeuilsSelections[1] = 20;
		}
		return valeursSeuilsSelections;
	}
	public double[] choixSeuilsAppariementLineaire() {
		double valeursSeuilsSelections[] = new double [2];
		boolean[] selectedActions = showDialogChoixDeDonnees();
		memeLOD = selectedActions[0];
		differentLOD = selectedActions[1];
		donneesCrees = selectedActions[2];
		// initialisation des seuils en fonction des caractéristiques des données choisies
		if (memeLOD) {
			valeursSeuilsSelections[0] = 50;
			valeursSeuilsSelections[1] = 20;
		} else if (differentLOD) {
			valeursSeuilsSelections[0] = 80;
			valeursSeuilsSelections[1] = 50;
		} else if (donneesCrees){
			valeursSeuilsSelections[0] = 50;
			valeursSeuilsSelections[1] = 20;
		}
		return valeursSeuilsSelections;
	}
	/**
	 * @return tableau contenant le seuil de sélection des candidats et le seuil de distance surfacique
	 */
	public double[] choixSeuilsAppariementSurfacique() {
		double valeursSeuilsSelections[] = new double [2];
		boolean[] selectedActions = showDialogChoixDeDonnees();
		memeLOD = selectedActions[0];
		differentLOD = selectedActions[1];
		donneesCrees = selectedActions[2];
		// initialisation des seuils en fonction des caractéristiques des données choisies
		if (memeLOD) {
			valeursSeuilsSelections[0] = 2;
			valeursSeuilsSelections[1] = 0.5;
		} else if (differentLOD) {
			valeursSeuilsSelections[0] = 10;
			valeursSeuilsSelections[1] = 3;
		} else if (donneesCrees){
			valeursSeuilsSelections[0] = 2;
			valeursSeuilsSelections[1] = 0.5;
		}
		return valeursSeuilsSelections;
	}
	public boolean[] showDialogChoixDeDonnees() {
		final JDialog dialog = interfaceChoixSeuils(this);
		dialog.setSize(550, 250);
		dialog.setVisible(true);
		dialog.dispose();
		return choixSelectionne;
	}
	public static JDialog interfaceChoixSeuils(final Frame parent) {
		final JDialog dialogueChoixDonnees = new JDialog(parent,
				" caractéristiques des données à apparier", true);
		Container panneauChoixDeDonnees = dialogueChoixDonnees.getContentPane();
		JPanel panneauDonnees = new JPanel(), panneauBienvenue = new JPanel(), panneauOK = new JPanel();
		JLabel titreBienvenue;
		Font policeTitreBienvenue;
		policeTitreBienvenue = new Font("Serif", Font.BOLD, 20);
		titreBienvenue = new JLabel(
				"Choisissez la caractéristique des données à apparier");
		titreBienvenue.setFont(policeTitreBienvenue);
		panneauBienvenue.add(titreBienvenue, BorderLayout.CENTER);
		// définition du panel concernant le type de données
		boutonDonneesCrees = new JRadioButton("données Créées à partir de cartes/photos");
		boutonMemeLOD = new JRadioButton("Le même niveau de détail");
		boutonDifferentLOD = new JRadioButton("différents niveaux de détails");
		ButtonGroup groupeBoutons= new ButtonGroup();
		groupeBoutons.add(boutonDonneesCrees);
		groupeBoutons.add(boutonMemeLOD);
		groupeBoutons.add(boutonDifferentLOD);
		panneauDonnees.add(boutonDonneesCrees);
		boutonDonneesCrees.setSelected(false);
		panneauDonnees.setLayout(new GridLayout(5, 5, 3, 1));
		panneauDonnees.add(boutonMemeLOD);
		boutonMemeLOD.setSelected(false);
		panneauDonnees.add(boutonDifferentLOD);
		boutonDifferentLOD.setSelected(true);
		// définition du panel OK
		boutonOK = new JButton("Valider");
		boutonOK.setActionCommand("Valider");
		panneauOK.add(boutonOK);
		boutonAnnuler = new JButton("Annuler");
		boutonAnnuler.setActionCommand("Annuler");
		boutonMemeLOD.setToolTipText("Cocher cette option si les données a apparier\n + ont le même niveau de détail");
		boutonDifferentLOD.setToolTipText("Cocher cette option si les données a apparier\n + ont le même niveau de détail\n ET si les données n'ont pas été Créées à partir de cartes ou photos");
		boutonDonneesCrees.setToolTipText("Cocher cette option si au moins une BD a été Créée à partir de cartes ou photos");
		panneauOK.add(boutonAnnuler);
		// action sur le bouton valider
		boutonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				memeLOD = boutonMemeLOD.isSelected();
				differentLOD = boutonDifferentLOD.isSelected();
				donneesCrees = boutonDonneesCrees.isSelected();
				getChoixSelectionne();
				dialogueChoixDonnees.dispose();
			}
		});
		boutonAnnuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				dialogueChoixDonnees.dispose();
			}
		});
		// panneauChoixDeDonnees.setPreferredSize(new Dimension(400,400));
		panneauChoixDeDonnees.add(panneauBienvenue, BorderLayout.NORTH);
		panneauChoixDeDonnees.add(panneauDonnees, BorderLayout.CENTER);
		panneauChoixDeDonnees.add(panneauOK, BorderLayout.SOUTH);
		dialogueChoixDonnees.pack();
		dialogueChoixDonnees.setLocationRelativeTo(null);
		return dialogueChoixDonnees;
	}
	private static void getChoixSelectionne() {
		choixSelectionne[0] = memeLOD;
		choixSelectionne[1] = differentLOD;
		choixSelectionne[2] = donneesCrees;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
}
