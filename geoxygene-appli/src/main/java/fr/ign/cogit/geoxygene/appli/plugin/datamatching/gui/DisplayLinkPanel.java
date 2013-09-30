package fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.LienReseaux;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;

/**
 * 
 *
 */
public class DisplayLinkPanel extends JInternalFrame  {
    
    /** Default serial ID. */
    private static final long serialVersionUID = 1L;
    
    private JPanel tableauPanel;
    private LienAppariementTableModel modele = new LienAppariementTableModel();
    private JTable tableau;
    private DisplayLinkGeomPanel geomLinkPanel;
    
    /**
     * Constructor.
     * @param liens
     */
    public DisplayLinkPanel(DisplayLinkGeomPanel gLP, EnsembleDeLiens liens) {
        
        super("Détail des liens.", true, true, true, true);
        
        geomLinkPanel = gLP;
        
        // Setting the tool tip text to the frame and its sub components
        setToolTipText(this.getTitle());
        getDesktopIcon().setToolTipText(this.getTitle());
        setLocation(0, 400);
        this.setSize(400, 400);
        setFrameIcon(new ImageIcon(DisplayLinkPanel.class.getResource("/images/icons/link.png")));
        
        // Initialize panel
        initTableauPanel(liens);
        
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(tableauPanel, BorderLayout.CENTER);
        
        pack();
        setVisible(true);
        
    }
    
    /**
     * 
     * @param liens
     */
    private void initTableauPanel(EnsembleDeLiens liens) {
        tableauPanel = new JPanel();
        tableauPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)));
        FormLayout layout = new FormLayout(
                "10dlu, pref, 10dlu",  // colonnes
                "10dlu, pref, pref, 10dlu");  // lignes
        tableauPanel.setLayout(layout);
        
        CellConstraints cc = new CellConstraints(); 
        
        tableau = new JTable(modele);
        tableau.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        tableau.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int viewRow = tableau.getSelectedRow();
                        if (viewRow >= 0) {
                            // javax.swing.JOptionPane.showMessageDialog(null, tableau.getRowCount());
                            // int modelRow = tableau.convertRowIndexToModel(viewRow);
                            /*javax.swing.JOptionPane.showMessageDialog(null, String.format("Selected Row in view: %d. " +
                                    "Selected Row in model: %d.", 
                                    viewRow, modelRow));*/
                            String cellValue = String.valueOf( tableau.getValueAt(viewRow, 0) );
                            geomLinkPanel.displayLink(cellValue);
                            // javax.swing.JOptionPane.showMessageDialog(null,cellValue);
                            // String cellValue = String.valueOf( tableau.getValueAt(row, column) );
                        }
                    }
                }
        );
        
        //tableau.getColumnModel().getColumn(4).setPreferredWidth(400);
        tableauPanel.add(tableau.getTableHeader(), cc.xy(2, 2));
        tableauPanel.add(new JScrollPane(tableau), cc.xy(2, 3));
        
        for(Lien l : liens) {
            LienReseaux lienReseau = (LienReseaux)l;
            
            String objectType1 = "-";
            if (lienReseau.getArcs1().size() > 0) {
                objectType1 = "ARC";
            } else if (lienReseau.getGroupes1().size() > 0) {
                objectType1 = "GROUPE";
            } else if (lienReseau.getNoeuds1().size() > 0) {
                objectType1 = "NOEUD";
            }
            String objectType2 = "-";
            if (lienReseau.getArcs2().size() > 0) {
                objectType2 = "ARC";
            } else if (lienReseau.getGroupes2().size() > 0) {
                objectType2 = "GROUPE";
            } else if (lienReseau.getNoeuds2().size() > 0) {
                objectType2 = "NOEUD";
            }
            
            StringBuffer strBuffer = new StringBuffer();
            List<Groupe> groupes1 = lienReseau.getGroupes1();
            if (!groupes1.isEmpty()) {
                strBuffer.append(groupesToString(groupes1));
            } else {
                // Export des arcs
                if (!lienReseau.getArcs1().isEmpty()) {
                    strBuffer.append("[" + arcsToString(lienReseau.getArcs1()) + "]");
                }
                // On exporte les noeuds
                if (!lienReseau.getNoeuds1().isEmpty()) {
                    strBuffer.append("[" + noeudsToString(lienReseau.getNoeuds1()) + "]");
                }
            }
            strBuffer.append("|");
            List<Groupe> groupes2 = lienReseau.getGroupes2();
            if (!groupes2.isEmpty()) {
                strBuffer.append(groupesToString(groupes2));
            } else {
                // on exporte les arcs
                if (!lienReseau.getArcs2().isEmpty()) {
                    strBuffer.append("[" + arcsToString(lienReseau.getArcs2()) + "]");
                }
                
                // on exporte les noeuds
                if (!lienReseau.getNoeuds2().isEmpty()) {
                    strBuffer.append("[" + noeudsToString(lienReseau.getNoeuds2()) + "]");
                }
            }
            
            modele.addLienAppariement(new LienAppariement(
                        Integer.toString(lienReseau.getId()), 
                        Double.toString(lienReseau.getEvaluation()),
                        objectType1,
                        objectType2,
                        strBuffer.toString()
                    ));
        }
    }
    
    /*@Override
    public void actionPerformed(ActionEvent e) {
        
    }*/
    
    
    private String arcsToString (List<Arc> arcs) {
        if (arcs.isEmpty()) {
            return "";
        }
        String res = "";
        boolean isFirst = true;
        // on exporte les arcs
        for (Arc arc : arcs) {
            if (isFirst) {
                isFirst = false;
            } else {
                res += ",";
            }
            res += "arc." + arc.getId();
         }
        return res;
    }
    
    private String noeudsToString (List<Noeud> noeuds) {
        String res = "";
        boolean isFirst = true;
        // On exporte les arcs
        for (Noeud noeud : noeuds) {
            if (isFirst) {
                isFirst = false;
            } else {
                res += ",";
            }
            res += "noeud."+noeud.getId();
        }
        return res;
    }
    
    private String groupesToString (List<Groupe> groupes) {
        String res = "";
        boolean isFirst = true; 
        for (Groupe g : groupes) {
            res += "[";
            
            // Export des arcs
            List<Arc> arcs = g.getListeArcs();
            for (Arc arc : arcs) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    res += ",";
                }
                res += "arc." + arc.getId();
                arc.getCorrespondants();
            }
            
            // Export des noeuds
            List<Noeud> noeuds = g.getListeNoeuds();
            for (Noeud noeud : noeuds) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    res += ",";
                }
                res += "noeud."+noeud.getId();
            }
            res += "]";
            isFirst = true;
        }
        return res;
    }

}

class LienAppariement {
    String id;
    String evaluation;
    String typeObject1;
    String typeObject2;
    String detail;
    
    public LienAppariement(String i, String e, String t1, String t2, String d) {
        id = i;
        evaluation = e;
        typeObject1 = t1;
        typeObject2 = t2;
        detail = d;
    }
    
    public String getId() {
        return id;
    }
    public String getEvaluation() {
        return evaluation;
    }
    public String getTypeObject1() {
        return typeObject1;
    }
    public String getTypeObject2() {
        return typeObject2;
    }
    public String getDetail() {
        return detail;
    }
}


class LienAppariementTableModel extends AbstractTableModel {
    
    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Data. */
    private final List<LienAppariement> data = new ArrayList<LienAppariement>();
    
    private final String[] rowHeader = {"ID", 
            "EVALUATION", "TYPE OBJET 1", "TYPE OBJET 2", "DETAIL"};
 
    public LienAppariementTableModel() {
        super();
    }
 
    public int getRowCount() {
        return data.size();
    }
 
    public int getColumnCount() {
        return rowHeader.length;
    }
 
    public String getColumnName(int columnIndex) {
        return rowHeader[columnIndex];
    }
 
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return data.get(rowIndex).getId();
            case 1:
                return data.get(rowIndex).getEvaluation();
            case 2:
                return data.get(rowIndex).getTypeObject1();
            case 3:
                return data.get(rowIndex).getTypeObject2();
            case 4:
                return data.get(rowIndex).getDetail();
            default:
                return null; // Ne devrait jamais arriver
        }
    }
    
    public void addLienAppariement(LienAppariement lien) {
        data.add(lien);
        fireTableRowsInserted(data.size() -1, data.size() -1);
    }
 
    public void removePgLayer(int rowIndex) {
        data.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
}


