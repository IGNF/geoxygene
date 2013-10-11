package fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamVarianteGeneralProcess;



public class EditParamVariantePanel extends JPanel implements ActionListener {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 4791806011051504347L;
    
    private ParamVarianteGeneralProcess paramVariante;
    
    private JRadioButton jcYesForceAppariementSimple;
    private JRadioButton jcNoForceAppariementSimple;
    
    private JRadioButton jcYesRedecoupageArcsNonApparies;
    private JRadioButton jcNoRedecoupageArcsNonApparies;
    
    private JRadioButton jcYesChercheRondsPoints;
    private JRadioButton jcNoChercheRondsPoints;
    
    private JRadioButton jcYesFiltrageImpassesParasites;
    private JRadioButton jcNoFiltrageImpassesParasites;
    
    private JRadioButton jcYesRedecoupageNoeudsNonApparies;
    private JRadioButton jcNoRedecoupageNoeudsNonApparies;
    
    private JTextField fieldDistanceNoeudArc = null;
    private JTextField fieldDistanceProjectionNoeud = null;
    
    /**
     * Constructor.
     * @param pn1
     * @param pn2
     */
    public EditParamVariantePanel(ParamVarianteGeneralProcess pv) {
      
        paramVariante = pv;
        
        // Initialize fields
        initField();
        
        // Initialize the panel with all fields
        initPanel();
        
    }
    
    
    public void initField() {
        
        // ForceAppariementSimple
        ButtonGroup gpForceAppariementSimple = new ButtonGroup();
        jcYesForceAppariementSimple = new JRadioButton();
        gpForceAppariementSimple.add(jcYesForceAppariementSimple);
        jcNoForceAppariementSimple = new JRadioButton();
        gpForceAppariementSimple.add(jcNoForceAppariementSimple);
        if (paramVariante.getForceAppariementSimple()) {
            jcYesForceAppariementSimple.setSelected(true);
        } else {
            jcNoForceAppariementSimple.setSelected(true);
        }
        
        // RedecoupageArcsNonApparies
        ButtonGroup gpRedecoupageArcsNonApparies = new ButtonGroup();
        jcYesRedecoupageArcsNonApparies = new JRadioButton();
        gpRedecoupageArcsNonApparies.add(jcYesRedecoupageArcsNonApparies);
        jcNoRedecoupageArcsNonApparies = new JRadioButton();
        gpRedecoupageArcsNonApparies.add(jcNoRedecoupageArcsNonApparies);
        if (paramVariante.getRedecoupageArcsNonApparies()) {
            jcYesRedecoupageArcsNonApparies.setSelected(true);
        } else {
            jcNoRedecoupageArcsNonApparies.setSelected(true);
        }
        
        // ChercheRondsPoints
        ButtonGroup gpChercheRondsPoints = new ButtonGroup();
        jcYesChercheRondsPoints = new JRadioButton();
        gpChercheRondsPoints.add(jcYesChercheRondsPoints);
        jcNoChercheRondsPoints = new JRadioButton();
        gpChercheRondsPoints.add(jcNoChercheRondsPoints);
        if (paramVariante.getChercheRondsPoints()) {
            jcYesChercheRondsPoints.setSelected(true);
        } else {
            jcNoChercheRondsPoints.setSelected(true);
        }
        
        // FiltrageImpassesParasites
        ButtonGroup gpFiltrageImpassesParasites = new ButtonGroup();
        jcYesFiltrageImpassesParasites = new JRadioButton();
        gpFiltrageImpassesParasites.add(jcYesFiltrageImpassesParasites);
        jcNoFiltrageImpassesParasites = new JRadioButton();
        gpFiltrageImpassesParasites.add(jcNoFiltrageImpassesParasites);
        if (paramVariante.getFiltrageImpassesParasites()) {
            jcYesFiltrageImpassesParasites.setSelected(true);
        } else {
            jcNoFiltrageImpassesParasites.setSelected(true);
        }
        
        // RedecoupageNoeudsNonApparies
        ButtonGroup gpRedecoupageNoeudsNonApparies = new ButtonGroup();
        jcYesRedecoupageNoeudsNonApparies = new JRadioButton();
        jcYesRedecoupageNoeudsNonApparies.addActionListener(this);
        gpRedecoupageNoeudsNonApparies.add(jcYesRedecoupageNoeudsNonApparies);
        jcNoRedecoupageNoeudsNonApparies = new JRadioButton();
        gpRedecoupageNoeudsNonApparies.add(jcNoRedecoupageNoeudsNonApparies);
        jcNoRedecoupageNoeudsNonApparies.addActionListener(this);
        
        fieldDistanceNoeudArc = new JTextField(10);
        fieldDistanceNoeudArc.setText(Double.toString(paramVariante.getDistanceNoeudArc()));
        
        fieldDistanceProjectionNoeud = new JTextField(10);
        fieldDistanceProjectionNoeud.setText(Double.toString(paramVariante.getDistanceProjectionNoeud()));
        
        if (paramVariante.getRedecoupageNoeudsNonApparies()) {
            jcYesRedecoupageNoeudsNonApparies.setSelected(true);
            fieldDistanceNoeudArc.setEditable(true);
            fieldDistanceProjectionNoeud.setEditable(true);
        } else {
            jcNoRedecoupageNoeudsNonApparies.setSelected(true);
            fieldDistanceNoeudArc.setEditable(false);
            fieldDistanceProjectionNoeud.setEditable(false);
        }
    }
    
    /**
     * 
     */
    private void initPanel() {
        
        FormLayout layout = new FormLayout(
                "40dlu, 150dlu, pref, 10dlu, pref, pref, pref, pref, pref, 40dlu",  // colonnes
                "20dlu, pref, pref, pref, pref, 5dlu, pref, pref, pref, 40dlu");   // lignes
        setLayout(layout);
        CellConstraints cc = new CellConstraints();
      
        // -------------------------------------------------------------------------
      
        // Ligne 1
        add(new JLabel("Niveau de complexité, recherche ou non de liens 1-n aux noeuds : "), cc.xyw(2, 2, 2));
        add(jcYesForceAppariementSimple, cc.xy(5,  2));
        add(new JLabel("oui"), cc.xy(6,  2));
        add(jcNoForceAppariementSimple, cc.xy(7,  2));
        add(new JLabel("non"), cc.xy(8,  2));
      
        // Ligne 2
        add(new JLabel("Appariement en deux passes qui tente un surdécoupage du réseau pour les arcs non appariés en première passe : "), cc.xyw(2, 3, 2));
        add(jcYesRedecoupageArcsNonApparies, cc.xy(5,  3));
        add(new JLabel("oui"), cc.xy(6,  3));
        add(jcNoRedecoupageArcsNonApparies, cc.xy(7,  3));
        add(new JLabel("non"), cc.xy(8,  3));
      
        // Ligne 3
        JLabel label3 = new JLabel("Recherche des ronds-points (faces circulaires) dans le réseau 2 : ");
        label3.setToolTipText("... pour éviter d'apparier un noeud du réseau 1 à une partie seulement d'un rond-point");
        add(label3, cc.xyw(2, 4, 2));
        add(jcYesChercheRondsPoints, cc.xy(5,  4));
        add(new JLabel("oui"), cc.xy(6,  4));
        add(jcNoChercheRondsPoints, cc.xy(7,  4));
        add(new JLabel("non"), cc.xy(8,  4));
      
        // Ligne 4
        JLabel label4 = new JLabel("Quand un arc est apparié à un ensemble d'arcs, élimine de cet ensemble les petites impasses : ");
        label4.setToolTipText("... qui créent des aller-retour parasites (de longueur inférieure à distanceNoeuds)");
        add(label4, cc.xyw(2, 5, 2));
        add(jcYesFiltrageImpassesParasites, cc.xy(5,  5));
        add(new JLabel("oui"), cc.xy(6,  5));
        add(jcNoFiltrageImpassesParasites, cc.xy(7,  5));
        add(new JLabel("non"), cc.xy(8,  5));
      
        // Ligne 5
        JLabel label5 = new JLabel("Appariement en deux passes qui tente un surdécoupage du réseau pour les noeuds non appariés en première passe : ");
        add(label5, cc.xyw(2, 7, 2));
        add(jcYesRedecoupageNoeudsNonApparies, cc.xy(5,  7));
        add(new JLabel("oui"), cc.xy(6,  7));
        add(jcNoRedecoupageNoeudsNonApparies, cc.xy(7,  7));
        add(new JLabel("non"), cc.xy(8,  7));
      
        // Ligne 6
        add(new JLabel("Distance max de la projection des noeuds du réseau 1 sur le réseau 2 : "), cc.xy(3, 8));
        add(fieldDistanceNoeudArc, cc.xyw(5, 8, 4));
        add(new JLabel(" m"), cc.xy(9, 8));
        
        // Ligne 7
        add(new JLabel("Distance min entre la projection d'un noeud sur un arc : "), cc.xy(3, 9));
        // et les extrémités de cet arc pour créer un nouveau noeud sur le réseau 2
        add(fieldDistanceProjectionNoeud, cc.xyw(5, 9, 4));
        add(new JLabel(" m"), cc.xy(9, 9));
    }
    
    
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == jcYesRedecoupageNoeudsNonApparies) {
            if (jcYesRedecoupageNoeudsNonApparies.isSelected()) {
                fieldDistanceNoeudArc.setEditable(true);
                fieldDistanceProjectionNoeud.setEditable(true);
            } else {
                fieldDistanceNoeudArc.setEditable(false);
                fieldDistanceProjectionNoeud.setEditable(false);
            }
        }
        if (source == jcNoRedecoupageNoeudsNonApparies) {
            if (jcNoRedecoupageNoeudsNonApparies.isSelected()) {
                fieldDistanceNoeudArc.setEditable(false);
                fieldDistanceProjectionNoeud.setEditable(false);
            } else {
                fieldDistanceNoeudArc.setEditable(true);
                fieldDistanceProjectionNoeud.setEditable(true);
            }
        }
    }
    
    
    /**
     * 
     * @return
     */
    public ParamVarianteGeneralProcess valideField() {
        
        ParamVarianteGeneralProcess returnParamVariante = new ParamVarianteGeneralProcess();
        
        // ForceAppariementSimple
        if (jcYesForceAppariementSimple.isSelected()) {
            returnParamVariante.setForceAppariementSimple(true);
        } else {
            returnParamVariante.setForceAppariementSimple(false);
        }
        
        // RedecoupageArcsNonApparies
        if (jcYesRedecoupageArcsNonApparies.isSelected()) {
            returnParamVariante.setRedecoupageArcsNonApparies(true);
        } else {
            returnParamVariante.setRedecoupageArcsNonApparies(false);
        }
        
        // ChercheRondsPoints
        if (jcYesChercheRondsPoints.isSelected()) {
            returnParamVariante.setChercheRondsPoints(true);
        } else {
            returnParamVariante.setChercheRondsPoints(false);
        }
        
        // FiltrageImpassesParasites
        if (jcYesFiltrageImpassesParasites.isSelected()) {
            returnParamVariante.setFiltrageImpassesParasites(true);
        } else {
            returnParamVariante.setFiltrageImpassesParasites(false);
        }
        
        // RedecoupageNoeudsNonApparies
        if (jcYesRedecoupageNoeudsNonApparies.isSelected()) {
            returnParamVariante.setRedecoupageNoeudsNonApparies(true);
            returnParamVariante.setDistanceNoeudArc(Double.parseDouble(fieldDistanceNoeudArc.getText()));
            returnParamVariante.setDistanceNoeudArc(Double.parseDouble(fieldDistanceProjectionNoeud.getText()));
        } else {
            returnParamVariante.setRedecoupageNoeudsNonApparies(true);
            returnParamVariante.setDistanceNoeudArc(0);
            returnParamVariante.setDistanceNoeudArc(0);
        }
        
        
        // return 
        return returnParamVariante;
    }

}
