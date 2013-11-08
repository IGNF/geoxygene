package fr.ign.cogit.geoxygene.appli.plugin.cartetopo.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultCarteTopoStatElement;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultCarteTopoStatElementInterface;

public class TestPanel extends JToolBar {

  private static final long serialVersionUID = 4791806011051504347L;

  private ProjectFrame projectFrame;
  private JPanel tableauResultat;
  private ResultCarteTopoStatElement resultStatArc;
  private ResultCarteTopoStatElement resultStatNoeud;

  public TestPanel(ProjectFrame p, ResultCarteTopoStatElement resArc,
      ResultCarteTopoStatElement resNoeud) {
    projectFrame = p;
    resultStatArc = resArc;
    resultStatNoeud = resNoeud;

    initPanel();

    projectFrame.getGui().getRootPane()
        .add(tableauResultat, BorderLayout.SOUTH);
    // tableauResultat.setVisible(true);
    projectFrame.getGui().validate();

  }

  private void initPanel() {

    FormLayout layout = new FormLayout(
        "40dlu, pref, 20dlu, pref, 20dlu, pref, 20dlu", // colonnes
        "20dlu, pref, 10dlu, pref, pref, pref, pref, pref, pref, pref, 50dlu"); // lignes
    CellConstraints cc = new CellConstraints();

    tableauResultat = new JPanel();
    tableauResultat.setLayout(layout);

    // Entete
    tableauResultat.add(new JLabel("Arcs"), cc.xy(4, 2));
    tableauResultat.add(new JLabel("Noeuds"), cc.xy(6, 2));

    // Nb bruts
    tableauResultat.add(new JLabel("Nombre bruts : "), cc.xy(2, 4));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatArc
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_BRUTS))),
            cc.xy(4, 4));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatNoeud
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_BRUTS))),
            cc.xy(6, 4));

    // Import + instanciation de la topologie
    tableauResultat.add(new JLabel("Instanciation de la topologie : "),
        cc.xy(2, 5));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatArc
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_IMPORT))),
            cc.xy(4, 5));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatNoeud
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_IMPORT))),
            cc.xy(6, 5));

    // 1. Apres graphe planaire
    tableauResultat.add(new JLabel("Graphe planaire : "), cc.xy(2, 6));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatArc
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_PLANAIRE))),
            cc.xy(4, 6));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatNoeud
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_PLANAIRE))),
            cc.xy(6, 6));

    // 2. Fusion des noeuds proches
    tableauResultat
        .add(new JLabel("Fusion des noeuds proches : "), cc.xy(2, 7));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatArc
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_NOEUDS_PROCHES))),
            cc.xy(4, 7));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatNoeud
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_NOEUDS_PROCHES))),
            cc.xy(6, 7));

    // 3. Suppresssion des noeuds isolés
    tableauResultat.add(new JLabel("Suppression des noeuds isolés : "),
        cc.xy(2, 8));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatArc
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_NOEUDS_ISOLES))),
            cc.xy(4, 8));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatNoeud
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_NOEUDS_ISOLES))),
            cc.xy(6, 8));

    // 4. Filtre des noeuds simples
    tableauResultat
        .add(new JLabel("Filtre des noeuds simples : "), cc.xy(2, 9));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatArc
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_INDEXATION))),
            cc.xy(4, 9));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatNoeud
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_INDEXATION))),
            cc.xy(6, 9));

    // 5. Fusion des arcs en double
    tableauResultat.add(new JLabel("Fusion des arcs en double : "),
        cc.xy(2, 10));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatArc
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_INDEXATION))),
            cc.xy(4, 10));
    tableauResultat
        .add(
            new JLabel(
                Integer.toString(resultStatNoeud
                    .getNbElementForType(ResultCarteTopoStatElementInterface.NB_INDEXATION))),
            cc.xy(6, 10));
  }

}
