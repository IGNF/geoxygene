/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
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
package fr.ign.cogit.geoxygene.util.loader.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.datatools.GeodatabaseFactory;
import fr.ign.cogit.geoxygene.datatools.GeodatabaseType;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.util.loader.Chargement;

/**
 * Interface de chargement de données de GéOxygène.
 * <p>
 * TODO ajouter une liste de changelistener pour surveiller la fin du chargement
 * et savoir quand récupérer le dataset chargé.
 * @author Julien Perret
 * 
 */
public class GUIChargementDonnees extends JPanel {
  static Logger logger = Logger.getLogger(GUIChargementDonnees.class.getName());
  Border padding = BorderFactory.createEmptyBorder(20, 20, 5, 20);

  /**
   * Panel structuré en arbre. Un panel possède un panel précédent et une liste
   * de panels suivants.
   * <p>
   * s TODO ajouter une méthode update à exécuter à chaque fois qu'on change de
   * panel pour mettre à jour son contenu
   * @author Julien Perret
   * 
   */
  private class GeoPanel extends JPanel {
    private static final long serialVersionUID = 2941427695796724709L;
    GeoPanel previous = null;

    /**
     * Renvoie la valeur de l'attribut precedent.
     * @return la valeur de l'attribut precedent
     */
    @SuppressWarnings("unused")
    public GeoPanel previousPanel() {
      return this.previous;
    }

    List<GeoPanel> next = new ArrayList<GeoPanel>();
    List<JRadioButton> radioButtons = new ArrayList<JRadioButton>();
    ButtonGroup group = new ButtonGroup();
    JPanel box = new JPanel();
    JPanel buttons = new JPanel(new GridBagLayout());
    JButton nextButton = null;
    JButton finishButton = null;
    boolean isFinal = false;

    /**
     * Constructeur affectant un layout de type GridBag.
     * @param titre titre du panel
     */
    public GeoPanel(String titre) {
      super(new BorderLayout());
      this.setName(titre);
      this.initGUI();
    }

    /**
     * Constructeur affectant un layout de type GridBag et un le panel
     * précédent. Un bouton "précédent" est créé pour revenir à celui-ci. Ce
     * Panel n'est pas final.
     * @param titre titre du panel
     * @param precedent le panel précédent
     * @see #GeoPanel(String,GeoPanel,boolean)
     */
    public GeoPanel(String titre, GeoPanel precedent) {
      this(titre, precedent, false);
    }

    /**
     * Constructeur vide affectant un layout de type GridBag et un le panel
     * précédent. Un bouton "précédent" est créé pour revenir à celui-ci. Si ce
     * Panel est final, un bouton "finish" est créer et permet de terminer le
     * chargement.
     * @param titre titre du panel
     * @param precedent le panel précédent
     * @param isFinal vrai si le panel est final, faux sinon
     */
    public GeoPanel(String titre, GeoPanel precedent, boolean isFinal) {
      super(new BorderLayout());
      this.setName(titre);
      this.previous = precedent;
      this.previousPanel().addSuivant(this);
      this.isFinal = isFinal;
      this.initGUI();
    }

    /**
     * Initialise les boutons et le layout du panel.
     */
    private void initGUI() {
      this.box.setLayout(new BoxLayout(this.box, BoxLayout.Y_AXIS));

      this.add(this.box, BorderLayout.CENTER);

      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.anchor = GridBagConstraints.WEST;
      c.weightx = 1;
      if (this.previousPanel() != null) {
        final JButton previousButton = new JButton("Précédent");
        previousButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            GUIChargementDonnees.this.previousDialog();
          }
        });
        this.buttons.add(previousButton, c);
      }
      if (this.isFinal) {
        c.anchor = GridBagConstraints.EAST;
        this.finishButton = new JButton("Finir");
        this.finishButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            GUIChargementDonnees.this.finish();
          }
        });
        this.buttons.add(this.finishButton, c);
      }
      this.add(this.buttons, BorderLayout.SOUTH);
      this.setBorder(GUIChargementDonnees.this.padding);
    }

    /**
     * Ajout d'un panel à la liste des panels suivants. Ajout un radio button et
     * le boutton suivant s'il manque.
     * @param suivant un panel suivant
     * @see #GeoPanel(String,GeoPanel)
     * @see #GeoPanel(String,GeoPanel,boolean)
     */
    public void addSuivant(GeoPanel suivant) {
      JRadioButton button = new JRadioButton(suivant.getName());
      this.next.add(suivant);
      this.radioButtons.add(button);
      this.group.add(button);
      this.box.add(button);
      if (this.radioButtons.size() == 1) {
        button.setSelected(true);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        this.nextButton = new JButton("Suivant");
        this.nextButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            GUIChargementDonnees.this.nextDialog(GeoPanel.this.nextPanel());
          }
        });
        this.buttons.add(this.nextButton, c);
      }
    }

    /**
     * Renvoie le panel suivant sélectionné dans le groupe de radiobuttons.
     * @return le panel suivant sélectionné dans le groupe de radiobuttons.
     */
    public GeoPanel nextPanel() {
      for (int i = 0; i < this.radioButtons.size(); i++) {
        if (this.radioButtons.get(i).isSelected()) {
          return this.next.get(i);
        }
      }
      return null;
    }

    /**
     * Renvoie la valeur de l'attribut box.
     * @return la valeur de l'attribut box
     */
    public JPanel getBox() {
      return this.box;
    }

    /**
     * Renvoie la valeur de l'attribut nextButton.
     * @return la valeur de l'attribut nextButton
     */
    public JButton getNextButton() {
      return this.nextButton;
    }

    /**
     * Renvoie la valeur de l'attribut finishButton.
     * @return la valeur de l'attribut finishButton
     */
    public JButton getFinishButton() {
      return this.finishButton;
    }
  }

  private static final long serialVersionUID = 1L;
  JLabel label;
  JFrame frame;
  String chargementDesc = "Chargement de données à partir d'un fichier";
  String chargementExistantDesc = "Exécute un chargement existant";
  String chargementExistantConfirmationDesc = "Exécuter le chargement suivant";
  String chargementShapefileDesc = "Chargement de shapefiles";
  String chargementAvanceDesc = "Chargement avancé";
  String chargementAvanceChoixJeuDesc = "Choix d'un jeu existant";
  String chargementAvanceNouveauJeuDesc = "Nouveau jeu";
  String chargementAvanceIgnorerJeuDesc = "Ne pas définir de jeu";
  String chargementAvanceSourceDesc = "Choix des fichiers source";
  String chargementAvanceSchemaDesc = "Chargement du schéma";
  String chargementAvanceMappingDesc = "Spécification du mapping";
  String exportDesc = "Export de données";

  Stack<GeoPanel> history = new Stack<GeoPanel>();
  GeoPanel currentPanel = null;

  // Create the components.
  GeoPanel chargementPanel;
  GeoPanel chargementExistantPanel;
  GeoPanel chargementExistantConfirmationPanel;
  GeoPanel chargementShapefilePanel;
  GeoPanel chargementAvancePanel;
  GeoPanel chargementAvanceChoixJeuPanel;
  GeoPanel chargementAvanceNouveauJeuPanel;
  GeoPanel chargementAvanceIgnorerJeuPanel;
  GeoPanel chargementAvanceSourcePanel;
  GeoPanel chargementAvanceSchemaPanel;
  GeoPanel chargementAvanceMappingPanel;
  GeoPanel exportPanel;
  BorderLayout layout = new BorderLayout();
  DefaultListModel listModel = new DefaultListModel();
  private DataSet dataSet = null;
  private List<?> dataSets = null;

  int typeGeodatabase = GeodatabaseType.OJB;

  /** Creates the GUI shown inside the frame's content pane. */
  public GUIChargementDonnees(final JFrame frame) {
    super();
    this.frame = frame;
    frame.setMinimumSize(new Dimension(600, 400));
    this.setLayout(this.layout);

    this.label = new JLabel(
        "Appuyer sur \"suivant\" pour passer à l'étape suivante.",
        SwingConstants.CENTER);

    this.chargementPanel = new GeoPanel(this.chargementDesc);

    this.chargementExistantPanel = this.creerChargementExistant();
    this.chargementExistantConfirmationPanel = this
        .creerChargementExistantConfirmation();

    this.chargementAvancePanel = new GeoPanel(this.chargementAvanceDesc,
        this.chargementPanel, false);

    this.chargementAvanceChoixJeuPanel = this.creerChargementAvanceChoixJeu();
    this.chargementAvanceNouveauJeuPanel = new GeoPanel(
        this.chargementAvanceNouveauJeuDesc, this.chargementAvancePanel, true);
    this.chargementAvanceIgnorerJeuPanel = new GeoPanel(
        this.chargementAvanceIgnorerJeuDesc, this.chargementAvancePanel, false);

    this.chargementShapefilePanel = new GeoPanel(this.chargementShapefileDesc,
        this.chargementAvanceIgnorerJeuPanel, true);
    // 
    final JList liste = new JList(this.listModel);
    final JButton ajouterButton = new JButton("Ajouter des shapefiles",
        new ImageIcon("images/Plus.png"));
    final JButton supprimerButton = new JButton("Supprimer des shapefiles",
        new ImageIcon("images/Moins.png"));
    ajouterButton
        .setToolTipText("Ajouter des fichiers ESRI shapefile à la liste");
    ajouterButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIShapefileChoice chooser = new GUIShapefileChoice(true);
        File[] files = chooser.getSelectedFiles();
        chooser.dispose();
        if (files != null) {
          for (File file : files) {
            GUIChargementDonnees.this.listModel.addElement(file);
          }
        }
        frame.pack();
        frame.repaint();
      }
    });
    supprimerButton
        .setToolTipText("Suprimer des fichiers ESRI shapefile de la liste");
    supprimerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (int index = liste.getSelectedIndices().length - 1; index >= 0; index--) {
          int selectedIndex = liste.getSelectedIndices()[index];
          GUIChargementDonnees.this.listModel.remove(selectedIndex);
        }
      }
    });
    JPanel ajouterEtSupprimer = new JPanel(new BorderLayout());
    ajouterEtSupprimer.add(ajouterButton, BorderLayout.WEST);
    ajouterEtSupprimer.add(supprimerButton, BorderLayout.EAST);
    this.chargementShapefilePanel.add(ajouterEtSupprimer, BorderLayout.NORTH);
    liste.setMinimumSize(new Dimension(200, 100));
    this.chargementShapefilePanel.add(liste, BorderLayout.CENTER);

    this.add(this.label, BorderLayout.SOUTH);
    this.nextDialog(this.chargementPanel);
  }

  /**
   * TODO il faudrait nettoyer le panel chargementExistantConfirmationPanel
   * avant d'y ajouter la liste des fichiers à charger.
   */
  private GeoPanel creerChargementExistant() {
    final GeoPanel panel = new GeoPanel(this.chargementExistantDesc,
        this.chargementPanel);

    final JTextField chargementText = new JTextField("");
    final JButton choixFichierButton = new JButton("Choisir...");
    choixFichierButton.setToolTipText("Choisir un fichier de chargement");
    chargementText.setEnabled(false);
    choixFichierButton.setEnabled(true);
    final JFileChooser choixFichier = new JFileChooser();
    choixFichier.setFileFilter(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return (f.isDirectory() || f.isFile()
            && f.getAbsolutePath().endsWith(".xml"));
      }

      @Override
      public String getDescription() {
        return "fichiers de chargement xml";
      }
    });

    choixFichierButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (choixFichier.showOpenDialog(GUIChargementDonnees.this) == JFileChooser.APPROVE_OPTION) {
          chargementText.setText(choixFichier.getSelectedFile().getName());
          panel.getNextButton().setEnabled(true);
          /**
           * On remplit le panel de confirmation à l'aide du fichier de
           * chargement choisi
           */
          Chargement chargement = Chargement.charge(choixFichier
              .getSelectedFile().getName());
          for (String nomFichier : chargement.getFichiers().values()) {
            JLabel newLabel = new JLabel(nomFichier);
            GUIChargementDonnees.this.chargementExistantConfirmationPanel
                .getBox().add(newLabel, BorderLayout.CENTER);
          }
          GUIChargementDonnees.this.frame.pack();
          GUIChargementDonnees.this.frame.repaint();
        }
      }
    });

    JPanel fichierPanel = new JPanel(new GridBagLayout());
    fichierPanel.setBorder(this.padding);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 4;
    c.gridx = 0;
    c.gridy = 0;
    fichierPanel.add(chargementText, c);
    c.weightx = 1;
    c.gridx = 1;
    fichierPanel.add(choixFichierButton, c);

    panel.getBox().add(
        new JLabel("Choisissez le fichier de chargement à exécuter"),
        BorderLayout.NORTH);
    panel.getBox().add(fichierPanel, BorderLayout.CENTER);
    return panel;
  }

  /**
	 */
  private GeoPanel creerChargementExistantConfirmation() {
    GeoPanel panel = new GeoPanel(this.chargementExistantConfirmationDesc,
        this.chargementExistantPanel, true);
    panel
        .getBox()
        .add(
            new JLabel(
                "Appuyer sur \"Finir\" pour exécuter le chargement des fichiers listés :"),
            BorderLayout.NORTH);
    return panel;
  }

  /**
   * TODO ajouter aussi le choix de la base de donnée !!!
   */
  private GeoPanel creerChargementAvanceChoixJeu() {
    final GeoPanel panel = new GeoPanel(this.chargementAvanceChoixJeuDesc,
        this.chargementAvancePanel, true);

    panel.getFinishButton().setEnabled(false);
    /*
     * final JTextField nomJeu = new JTextField();
     * panel.add(nomJeu,BorderLayout.NORTH);
     * nomJeu.getDocument().addDocumentListener(new DocumentListener() {
     * 
     * @Override public void changedUpdate(DocumentEvent e) { }
     * 
     * @Override public void insertUpdate(DocumentEvent e) {
     * panel.getFinishButton().setEnabled(!nomJeu.getText().isEmpty()); }
     * 
     * @Override public void removeUpdate(DocumentEvent e) {
     * panel.getFinishButton().setEnabled(!nomJeu.getText().isEmpty()); } });
     */
    JPanel dbPanel = new JPanel(new GridBagLayout());
    dbPanel.setBorder(this.padding);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 0;
    c.gridy = 0;

    // ATTENTION : l'indice dans le tableau doit correspondre au type de
    // database pour la méthode GeodatabaseFactory.newInstance(int);
    String[] geodatabaseTypeStrings = { "None", "OJB", "Hibernate" };
    JComboBox geodatabaseTypeCombo = new JComboBox(geodatabaseTypeStrings);
    dbPanel.add(geodatabaseTypeCombo, c);

    final List<String> dataSetStrings = new ArrayList<String>();
    final JComboBox combo = new JComboBox(dataSetStrings.toArray(new String[0]));

    c.weightx = 2;
    c.gridx = 1;
    dbPanel.add(combo, c);

    geodatabaseTypeCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = ((JComboBox) e.getSource()).getSelectedIndex();
        if (index != 0) {
          if (GUIChargementDonnees.logger.isDebugEnabled()) {
            GUIChargementDonnees.logger
                .debug("création d'une instance de base de données de type "
                    + GeodatabaseType.toString(index));
          }
          DataSet.db = GeodatabaseFactory.newInstance(index);
          GUIChargementDonnees.this.dataSets = DataSet.db
              .loadAll(DataSet.class);
          for (Object o : GUIChargementDonnees.this.dataSets) {
            DataSet subDataSet = (DataSet) o;
            dataSetStrings.add(subDataSet.getNom());
            if (GUIChargementDonnees.logger.isDebugEnabled()) {
              GUIChargementDonnees.logger.debug("Ajout du dataset "
                  + subDataSet.getNom());
            }
            /** Si on a trouvé au moins un dataset, on active le bouton finish */
            panel.getFinishButton().setEnabled(true);
          }
          if (GUIChargementDonnees.this.dataSets.isEmpty()) {
            GUIChargementDonnees.logger
                .info("Aucun dataset trouvé dans la base de donnée sélectionnée");
          } else {
            GUIChargementDonnees.this.dataSet = (DataSet) GUIChargementDonnees.this.dataSets
                .get(0);
          }
          combo.setModel(new DefaultComboBoxModel(dataSetStrings
              .toArray(new String[0])));
        }
      }
    });
    combo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = ((JComboBox) e.getSource()).getSelectedIndex();
        GUIChargementDonnees.this.dataSet = (DataSet) GUIChargementDonnees.this.dataSets
            .get(index);
      }
    });
    panel.add(dbPanel, BorderLayout.CENTER);
    return panel;
  }

  /**
	 */
  @SuppressWarnings("unused")
  private GeoPanel creerChargementAvanceNouveauJeu() {
    GeoPanel panel = new GeoPanel(this.chargementAvanceNouveauJeuDesc,
        this.chargementAvancePanel, true);

    final int numButtons = 2;
    JRadioButton[] radioButtons = new JRadioButton[numButtons];
    final ButtonGroup group = new ButtonGroup();

    final String rechargerCommand = "Recharger :";
    final String nouveauFichierCommand = "Nouveau fichier";

    radioButtons[0] = new JRadioButton(rechargerCommand);
    radioButtons[0].setActionCommand(rechargerCommand);

    radioButtons[1] = new JRadioButton(nouveauFichierCommand);
    radioButtons[1].setActionCommand(nouveauFichierCommand);

    for (int i = 0; i < numButtons; i++) {
      group.add(radioButtons[i]);
    }
    radioButtons[1].setSelected(true);

    final JButton nextButton = new JButton("Suite");

    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String command = group.getSelection().getActionCommand();
        if (command == rechargerCommand) {
          GUIChargementDonnees.this
              .nextDialog(GUIChargementDonnees.this.exportPanel);
        } else if (command == nouveauFichierCommand) {
          GUIChargementDonnees.this
              .nextDialog(GUIChargementDonnees.this.exportPanel);
        }
      }
    });

    GeoPanel box = new GeoPanel(this.chargementAvanceSourceDesc);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.LINE_START;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 3;
    box.add(radioButtons[0], c);
    c.gridy = 1;
    box.add(radioButtons[1], c);
    c.gridx = 4;
    c.gridy = 3;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.LINE_START;
    final JButton previousButton = new JButton("Précédent");
    previousButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUIChargementDonnees.this.previousDialog();
      }
    });
    box.add(previousButton, c);
    c.anchor = GridBagConstraints.LINE_END;
    box.add(nextButton, c);
    return panel;
  }

  /**
	 */
  @SuppressWarnings("unused")
  private GeoPanel creerChargementAvanceSchemaDialogBox() {

    JLabel charge = new JLabel("Données chargées :");

    final JButton nextButton = new JButton("Suite");

    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUIChargementDonnees.this
            .nextDialog(GUIChargementDonnees.this.exportPanel);
      }
    });

    GeoPanel box = new GeoPanel(this.chargementAvanceSchemaDesc);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.LINE_START;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 3;
    box.add(charge, c);
    c.gridx = 4;
    c.gridy = 3;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.LINE_START;
    final JButton previousButton = new JButton("Précédent");
    previousButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUIChargementDonnees.this.previousDialog();
      }
    });
    box.add(previousButton, c);
    c.anchor = GridBagConstraints.LINE_END;
    box.add(nextButton, c);
    return box;
  }

  /**
	 */
  @SuppressWarnings("unused")
  private GeoPanel creerChargementAvanceMappingDialogBox() {

    JLabel appliquer = new JLabel("Appliquer un mapping existant :");

    final JTextField chargementText = new JTextField("");
    final JButton choixFichierButton = new JButton("Choisir...");
    choixFichierButton.setToolTipText("Choisir un fichier de mapping");
    chargementText.setEnabled(false);
    choixFichierButton.setEnabled(false);
    final JFileChooser choixFichier = new JFileChooser();
    // choixFichier.setFileSelectionMode(JFileChooser.FILES_ONLY);
    choixFichier.setFileFilter(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.isFile() && f.getAbsolutePath().endsWith(".xml");
      }

      @Override
      public String getDescription() {
        return "fichiers de chargement xml";
      }
    });

    final JButton nextButton = new JButton("Suite");

    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      }
    });
    choixFichierButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (choixFichier.showOpenDialog(GUIChargementDonnees.this) == JFileChooser.APPROVE_OPTION) {
          chargementText.setText(choixFichier.getSelectedFile().getName());
          nextButton.setEnabled(true);
        }
      }
    });

    GeoPanel box = new GeoPanel(this.chargementAvanceMappingDesc);
    JPanel fichierPanel = new JPanel();
    fichierPanel.setLayout(new BoxLayout(fichierPanel, BoxLayout.X_AXIS));

    fichierPanel.add(chargementText);
    fichierPanel.add(choixFichierButton);

    // box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
    // box.setAlignmentX(0);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    // c.anchor=GridBagConstraints.LINE_START;
    c.gridwidth = 2;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    box.add(appliquer, c);
    // c.anchor=GridBagConstraints.CENTER;
    c.gridx = 0;
    c.gridy = 1;
    box.add(fichierPanel, c);
    c.gridx = 1;
    c.gridy = 3;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.LINE_START;
    final JButton previousButton = new JButton("Précédent");
    previousButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUIChargementDonnees.this.previousDialog();
      }
    });
    box.add(previousButton, c);
    c.anchor = GridBagConstraints.LINE_END;
    box.add(nextButton, c);
    return box;
  }

  /**
	 */
  @SuppressWarnings("unused")
  private GeoPanel creerExportDialogBox() {
    GeoPanel box = new GeoPanel(this.exportDesc);
    return box;
  }

  /**
   * @param newPanel
   */
  protected void nextDialog(GeoPanel newPanel) {
    if (this.currentPanel != null) {
      this.remove(this.currentPanel);
    }
    this.currentPanel = newPanel;
    this.history.add(this.currentPanel);
    this.add(this.currentPanel, BorderLayout.CENTER);
    this.frame.setTitle(this.currentPanel.getName() + " :");
    this.frame.pack();
    this.frame.repaint();
  }

  /**
	 */
  protected void previousDialog() {
    if (this.history.size() < 2) {
      return;
    }
    if (this.currentPanel != null) {
      this.remove(this.currentPanel);
    }
    this.history.pop();
    this.currentPanel = this.history.peek();
    this.add(this.currentPanel, BorderLayout.CENTER);
    this.frame.setTitle(this.currentPanel.getName() + " :");
    this.frame.pack();
    this.frame.repaint();
  }

  /**
   * Termine le chargement et dispose de la frame utilisée.
   */
  protected void finish() {
    this.frame.dispose();
  }

  /** Sets the text displayed at the bottom of the frame. */
  void setLabel(String newText) {
    this.label.setText(newText);
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be
   * invoked from the event-dispatching thread.
   */
  static void createAndShowGUI() {
    // Create and set up the window.
    JFrame frame = new JFrame("DialogDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Create and set up the content pane.
    GUIChargementDonnees newContentPane = new GUIChargementDonnees(frame);
    newContentPane.setOpaque(true); // content panes must be opaque
    frame.setLayout(new BorderLayout());
    frame.setContentPane(newContentPane);

    // Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        GUIChargementDonnees.createAndShowGUI();
      }
    });
  }

  /**
   * Renvoie la valeur de l'attribut dataSet.
   * @return la valeur de l'attribut dataSet
   */
  public DataSet getDataSet() {
    return this.dataSet;
  }

  /**
   * Affecte la valeur de l'attribut dataSet.
   * @param dataSet l'attribut dataSet à affecter
   */
  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
  }
}
