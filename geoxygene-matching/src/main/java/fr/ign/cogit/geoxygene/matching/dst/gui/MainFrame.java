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

package fr.ign.cogit.geoxygene.matching.dst.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;

/**
 * @author Julien Perret
 *
 */
public class MainFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 418, 461);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(this.contentPane);
        this.contentPane.setLayout(new GridLayout(1, 0, 0, 0));
         
        JPanel panel = new JPanel();
        panel.setToolTipText("Données d'appariement");
        this.contentPane.add(panel);
        panel.setLayout(null);
        
        JLabel lblSlectionnerLesDonnes = new JLabel("Sélectionner les données à apparier");
        lblSlectionnerLesDonnes.setHorizontalAlignment(SwingConstants.CENTER);
        lblSlectionnerLesDonnes.setLabelFor(panel);
        lblSlectionnerLesDonnes.setBounds(0, 0, 398, 49);
        panel.add(lblSlectionnerLesDonnes);
        
        JButton btnNewButton = new JButton("Référence");
        btnNewButton.setBounds(10, 61, 117, 25);
        panel.add(btnNewButton);
        
        JButton btnCandidats = new JButton("Candidats");
        btnCandidats.setBounds(270, 61, 117, 25);
        panel.add(btnCandidats);
        
        JCheckBox chckbxNewCheckBox = new JCheckBox("Aperçu");
        chckbxNewCheckBox.setBounds(0, 349, 129, 23);
        panel.add(chckbxNewCheckBox);
        
        JButton btnNewButton_1 = new JButton("Ok");
        btnNewButton_1.setBounds(270, 384, 117, 25);
        panel.add(btnNewButton_1);
        
        JInternalFrame internalFrame = new JInternalFrame("New JInternalFrame");
        internalFrame.setBounds(20, 98, 349, 243);
        panel.add(internalFrame);
        internalFrame.setVisible(true);
       // internalFrame.add(new LayerViewer());
    }
}
