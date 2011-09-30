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

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;

/**
 * Cette classe contient des interfaces permettant de générer des attributs (et
 * valeurs) dans des tables selectionnées par un utilisateur.
 * @author Ana-Maria Raimond
 */
public class InterfaceGenereAttributs extends JFrame {

  private static final long serialVersionUID = 1L;
  private boolean[] choixSelectionne = new boolean[2];
  private boolean generationIDsEtDate = false;
  private static int valeurDate, valeurDateSaisie;
  private static boolean generationIdGeo = false, generationDate = false;

  public JDialog interfaceAttributDate(final Frame owner,
      final Geodatabase data, final String table) {
    final JDialog dialogueAttributDate = new JDialog(owner,
        "génération de l'attribut Date ", true);
    Container conteneur = dialogueAttributDate.getContentPane();
    JLabel texte;
    final JTextField date;
    JButton boutonValider, boutonAnnuler;
    Font policeTexte;
    JPanel panelBouton = new JPanel(), panelTexte = new JPanel();

    conteneur.setLayout(new BorderLayout(5, 5));
    // définition du champ texte
    texte = new JLabel(
        "Introduisez la date de votre base de données sélectionnée : " + "' "
            + table + " '");
    policeTexte = new Font("Serif", Font.BOLD, 14);
    texte.setFont(policeTexte);
    // panelTexte.setLayout(new GridLayout(5, 5, 1, 2));
    panelTexte.add(texte, BorderLayout.CENTER);
    // définition du champ correspondant à la date de la BDG sélectionnée avec
    // une taille prédéfinie
    date = new JTextField(4);
    panelTexte.add(date);
    conteneur.add(panelTexte, BorderLayout.NORTH);
    // définition bouton valider
    boutonValider = new JButton("Valider");
    boutonValider.setActionCommand("Valider");
    boutonValider.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          valeurDateSaisie = Integer.parseInt(date.getText());
          if ((valeurDateSaisie < 1000) || (valeurDateSaisie > 9999))
            throw (new DateException());
          else if (valeurDateSaisie < 0)
            throw (new DateNegative());
          else {
            getValeurSaisie();
            // on ferme la fenêtre
            DatabaseAccess accesBase = new DatabaseAccess(data, table, true);
            accesBase.remplissageColonneDate(valeurDate);
            dialogueAttributDate.dispose();
          }
        } catch (NumberFormatException numeroFormatException) {
          JOptionPane.showMessageDialog(owner, "Vous devez saisir un entier",
              "Format de date incorrect", JOptionPane.ERROR_MESSAGE);
        } catch (DateException dateexception) {
          JOptionPane.showMessageDialog(owner,
              "Vous devez saisir un entier de 4 chiffre",
              "Format de date incorrect", JOptionPane.ERROR_MESSAGE);
        } catch (DateNegative dateNegative) {
          JOptionPane.showMessageDialog(owner, "La date est négative",
              "Format de date incorrect", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    panelBouton.add(boutonValider);
    // définition bouton Annuler
    boutonAnnuler = new JButton("Annuler");
    boutonAnnuler.setActionCommand("Annuler");
    boutonAnnuler.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dialogueAttributDate.dispose();
      }
    });
    // panelBouton.add(boutonAnnuler);
    conteneur.add(panelBouton, BorderLayout.CENTER);

    dialogueAttributDate.pack();
    dialogueAttributDate.setLocationRelativeTo(owner);
    return dialogueAttributDate;

  }

  public JDialog interfaceGenereAttributs(Frame parent) {
    ButtonGroup groupeBoutons;
    final JDialog interfaceDialogue = new JDialog(parent,
        "Préparation des données au format du modèle GeOpenSim", true);
    // lire le panneau de contenu et définir ses paramètres
    Container conteneur = interfaceDialogue.getContentPane();
    JPanel panelBoutons = new JPanel(), panelOK = new JPanel();
    JLabel texteQuestion;
    Font policeTexte;
    final JRadioButton boutonCreationIDS, boutonCreationAttributDate;
    JButton boutonValider, boutonAnnuler;

    conteneur.setLayout(new BorderLayout(5, 5));
    texteQuestion = new JLabel("Dans cette étape nous allons... ");
    policeTexte = new Font("Serif", Font.BOLD, 20);
    texteQuestion.setFont(policeTexte);
    conteneur.add(texteQuestion, BorderLayout.PAGE_START);

    // créer le bouton pour la création des identifiants
    boutonCreationIDS = new JRadioButton("générer les identifiants ID_GEO");
    panelBoutons.add(boutonCreationIDS);
    boutonCreationIDS.setSelected(false);
    // créer le bouton pour la création d'un attribut Date
    boutonCreationAttributDate = new JRadioButton("créer l'attribut DATE");
    panelBoutons.add(boutonCreationAttributDate);
    boutonCreationAttributDate.setSelected(false);

    panelBoutons.setLayout(new GridLayout(2, 1));
    // créer la relation logique entre les JBoutons
    groupeBoutons = new ButtonGroup();
    groupeBoutons.add(boutonCreationIDS);
    groupeBoutons.add(boutonCreationAttributDate);
    conteneur.add(panelBoutons, BorderLayout.CENTER);

    boutonValider = new JButton("Valider");
    boutonValider.setActionCommand("Valider");
    boutonAnnuler = new JButton("Annuler");
    boutonAnnuler.setActionCommand("Annuler");
    panelOK.add(boutonValider);
    panelOK.add(boutonAnnuler);
    conteneur.add(panelOK, BorderLayout.SOUTH);
    boutonValider.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        generationIdGeo = boutonCreationIDS.isSelected();
        generationDate = boutonCreationAttributDate.isSelected();
        getChoixSelectionne();
        interfaceDialogue.dispose();
      }
    });
    boutonAnnuler.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        interfaceDialogue.dispose();
      }
    });
    interfaceDialogue.pack();
    interfaceDialogue.setLocationRelativeTo(parent);
    return interfaceDialogue;

  }

  public JDialog interfaceGenereAttributsBaseRef(Frame parent) {
    final JDialog interfaceDialogue = new JDialog(parent,
        "Préparation des données au format du modèle GeOpenSim", true);
    // lire le panneau de contenu et définir ses paramètres
    Container conteneur = interfaceDialogue.getContentPane();
    JPanel panelBoutons = new JPanel(), panelOK = new JPanel();
    JLabel texteQuestion;
    Font policeTexte, policeBouton;
    JCheckBox boutonCreationIDS;
    JCheckBox boutonCreationAttributDate;
    JButton boutonValider, boutonAnnuler;

    conteneur.setLayout(new BorderLayout(5, 5));
    texteQuestion = new JLabel("Dans cette étape nous allons... ");
    policeTexte = new Font("Serif", Font.BOLD, 16);
    policeBouton = new Font("Serif", Font.BOLD, 14);
    texteQuestion.setFont(policeTexte);
    conteneur.add(texteQuestion, BorderLayout.PAGE_START);
    panelBoutons.setLayout(new GridLayout(2, 1));

    // on crée le check boxe pour générer les id.
    boutonCreationIDS = new JCheckBox("générer les identifiants ID_GEO");
    boutonCreationIDS.setMnemonic(KeyEvent.VK_C);
    boutonCreationIDS.setSelected(true);
    boutonCreationIDS.setEnabled(false);
    boutonCreationIDS.setFont(policeBouton);
    panelBoutons.add(boutonCreationIDS);
    // on crée le check boxe pour générer les dates
    conteneur.add(panelBoutons, BorderLayout.CENTER);
    boutonCreationAttributDate = new JCheckBox("créer l'attribut DATE");
    boutonCreationAttributDate.setMnemonic(KeyEvent.VK_G);
    boutonCreationAttributDate.setSelected(true);
    boutonCreationAttributDate.setEnabled(false);
    boutonCreationAttributDate.setFont(policeBouton);
    // panelBoutons.add(boutonCreationAttributDate);
    // on crée les boutons pour valider, annuler
    boutonValider = new JButton("Valider");
    boutonValider.setActionCommand("Valider");
    boutonAnnuler = new JButton("Annuler");
    boutonAnnuler.setActionCommand("Annuler");
    panelOK.add(boutonValider);
    panelOK.add(boutonAnnuler);
    conteneur.add(panelOK, BorderLayout.SOUTH);
    boutonValider.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        generationIDsEtDate = true;
        interfaceDialogue.dispose();
      }
    });
    boutonAnnuler.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        generationIDsEtDate = false;
        interfaceDialogue.dispose();
      }
    });
    interfaceDialogue.pack();
    interfaceDialogue.setLocationRelativeTo(parent);
    return interfaceDialogue;

  }

  // l'utilisateur a le choix de générer les identifiants id_Geo ou de de
  // générer la Date
  private void getChoixSelectionne() {
    choixSelectionne[0] = generationIdGeo;
    choixSelectionne[1] = generationDate;
  }

  private void getValeurSaisie() {
    valeurDate = valeurDateSaisie;
  }

  public boolean[] showDialog() {
    final JDialog dialog = interfaceGenereAttributsBaseRef(this);
    dialog.setSize(400, 200);
    dialog.setVisible(true);
    dialog.dispose();
    return choixSelectionne;
  }

  public boolean showDialogGenereAttributsBaseRef() {
    final JDialog dialog = interfaceGenereAttributsBaseRef(this);
    dialog.setSize(450, 150);
    dialog.setVisible(true);
    dialog.dispose();
    return generationIDsEtDate;
  }

  public int showDialogDate(String table, Geodatabase data) {
    final JDialog dialog = interfaceAttributDate(this, data, table);
    dialog.setSize(500, 150);
    dialog.setVisible(true);
    dialog.dispose();
    return valeurDate;
  }

  public class DateException extends Exception {
    private static final long serialVersionUID = 1L;

    DateException() {
      super("Format de date incorrect");
    }
  }

  public class DateNegative extends Exception {
    private static final long serialVersionUID = 1L;

    DateNegative() {
      super("La date est négative");
    }
  }

}
