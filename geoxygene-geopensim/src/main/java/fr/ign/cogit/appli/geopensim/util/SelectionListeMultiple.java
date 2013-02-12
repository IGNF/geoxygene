/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * @author amolteanu
 * 
 */
public class SelectionListeMultiple extends JFrame {
  private static final long serialVersionUID = 1L;
  private JList listeShapes, listeShapesSelectionnes;
  private JButton selectionner, supprimer, lancerAppariement,
      boutonlancerCreationIdGeo, boutonDate;
  private String listeShapesCharges[] = { "bâtiment 2007", "bâtiment 1999",
      "bâtiment 1978", "bâtiment 1957", "bâtiment 1978", "bâtiment 1978",
      "bâtiment 1978", "bâtiment 1978", "bâtiment 1978", "bâtiment 1978",
      "bâtiment 1978", "bâtiment 1978", "bâtiment 1978", "bâtiment 1978" };
  private JPanel panelBouton = new JPanel();

  public SelectionListeMultiple(String listeShapesCharges[]) {
    // par la suite ce constructeur va prendre en paramètre une liste
    // contenant les fichiers shapes chargés
    super("Sélectionner la base de données de référence");
    // lire le panneau de contenu et définir ses paramètres
    Container conteneur = getContentPane();
    conteneur.setLayout(new FlowLayout());
    // définir la liste des shapes chargés de JList
    listeShapes = new JList(listeShapesCharges);
    // le nombre de lignes visible dans la liste des shapes Sélectionnés
    listeShapes.setVisibleRowCount(5);
    // on définit la hauteur de chaque élément de la liste
    listeShapes.setFixedCellHeight(15);
    listeShapes
        .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    // créer le bouton Sélectionner
    selectionner = new JButton("sélectionner >>>");
    selectionner.addActionListener(new ActionListener() {
      // gérer les évènements de bouton
      @Override
      public void actionPerformed(ActionEvent action) {
        // on place les éléments choisi dans la listeShapesSelectionnes
        listeShapesSelectionnes.setListData(listeShapes.getSelectedValues());
      }
    }// fin de
    );// fin de l'appel addActionListener
    // on rajoute le bouton au conteneur
    panelBouton.add(selectionner);
    panelBouton.setLayout(new GridLayout(5, 5, 2, 1));

    // créer le bouton supprimer
    supprimer = new JButton("<<< Supprimer ");
    supprimer.addActionListener(new ActionListener() {
      // gérer les évènements de bouton
      @Override
      public void actionPerformed(ActionEvent action) {
        // on place les éléments choisi dans la listeShapesSelectionnes
        listeShapesSelectionnes.setListData(listeShapesSelectionnes
            .getSelectedValues());
      }
    }// fin de
        );// fin de l'appel addActionListener
    // on rajoute le bouton au panel
    panelBouton.add(supprimer);

    // créer le bouton lancer la création des des identifiants géo
    boutonlancerCreationIdGeo = new JButton("créer les identifiants id_Geo");

    // créer le bouton lancer la création du champ Date
    boutonDate = new JButton("créer le champ DATE");

    // on définit la liste des shapes sélectionnés
    listeShapesSelectionnes = new JList();
    listeShapesSelectionnes.setVisibleRowCount(5);
    listeShapesSelectionnes.setFixedCellWidth(100);
    listeShapesSelectionnes.setFixedCellHeight(15);
    listeShapesSelectionnes
        .setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    // on rajoute les éléments Créés au conteneur

    conteneur.add(new JScrollPane(listeShapes));
    conteneur.add(panelBouton, BorderLayout.CENTER);
    conteneur.add(new JScrollPane(listeShapesSelectionnes));
    conteneur.add(boutonlancerCreationIdGeo, BorderLayout.CENTER);
    conteneur.add(boutonDate, BorderLayout.CENTER);

    setSize(500, 250);
    setVisible(true);
  }

  public SelectionListeMultiple() {
    // par la suite ce constructeur va prendre en paramètre une liste
    // contenant les fichiers shapes chargés
    super("Sélectionner les données à apparier");
    // lire le panneau de contenu et définir ses paramètres
    Container conteneur = getContentPane();
    conteneur.setLayout(new FlowLayout());
    // définir la liste des shapes chargés de JList
    listeShapes = new JList(listeShapesCharges);
    // le nombre de lignes visible dans la liste des shapes Sélectionnés
    listeShapes.setVisibleRowCount(5);
    // on définit la hauteur de chaque élément de la liste
    listeShapes.setFixedCellHeight(15);
    listeShapes
        .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    // on rajoute au conteneur la liste
    conteneur.add(new JScrollPane(listeShapes));

    // créer le bouton Sélectionner
    selectionner = new JButton("sélectionner >>>");
    selectionner.addActionListener(new ActionListener() {
      // gérer les évènements de bouton
      @Override
      public void actionPerformed(ActionEvent action) {
        // on place les éléments choisi dans la listeShapesSelectionnes
        listeShapesSelectionnes.setListData(listeShapes.getSelectedValues());
      }
    }// fin de
        );// fin de l'appel addActionListener
    // on rajoute le bouton au conteneur
    panelBouton.add(selectionner);
    panelBouton.setLayout(new GridLayout(5, 5, 2, 1));

    // créer le bouton supprimer
    supprimer = new JButton("<<< Supprimer ");
    supprimer.addActionListener(new ActionListener() {
      // gérer les évènements de bouton
      @Override
      public void actionPerformed(ActionEvent action) {
        // on place les éléments choisi dans la listeShapesSelectionnes
        listeShapesSelectionnes.setListData(listeShapesSelectionnes
            .getSelectedValues());
      }
    }// fin de
        );// fin de l'appel addActionListener
    // on rajoute le bouton au conteneur
    panelBouton.add(supprimer);

    // créer le bouton lancer l'Appariement
    lancerAppariement = new JButton("Lancer l'appariement de données ");

    conteneur.add(panelBouton, BorderLayout.CENTER);

    // on définit la liste des shapes sélectionnés
    listeShapesSelectionnes = new JList();
    listeShapesSelectionnes.setVisibleRowCount(5);
    listeShapesSelectionnes.setFixedCellWidth(100);
    listeShapesSelectionnes.setFixedCellHeight(15);
    listeShapesSelectionnes
        .setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    // on rajoute au conteneur la liste
    conteneur.add(new JScrollPane(listeShapesSelectionnes));
    conteneur.add(lancerAppariement, BorderLayout.CENTER);
    setSize(500, 250);
    setVisible(true);
  }
}
