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

package fr.ign.cogit.appli.geopensim.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;

/**
 * @author Julien Perret
 *
 */
public class RepositoryManager extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private boolean validated = false;
    /**
     * @param args
     */
    public static void main(String[] args) {
        MetadataManager mm = MetadataManager.getInstance();
        ConnectionRepository cr = mm.connectionRepository();
        for (Object c : cr.getAllDescriptor()) {
            JdbcConnectionDescriptor descriptor = (JdbcConnectionDescriptor) c;
            System.out.println(descriptor);
        }
    }
    /**
     * Création du JDialog de sélection des fichiers.
     * @return vrai si le JDialog a été validé (grâce au bouton "Ok"), faux sinon.
     */
    public boolean showDialog() {
        final JDialog dialog = createDialog(this);
        dialog.setVisible(true);
        dialog.dispose();
        return this.validated;
    }
    /**
     * @param repositoryManager
     * @return
     */
    private JDialog createDialog(RepositoryManager parent) {
        final JDialog dialog = new JDialog(parent, "Choisissez une base de données", true);

        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());

        MetadataManager mm = MetadataManager.getInstance();
        ConnectionRepository cr = mm.connectionRepository();
        for (Object c : cr.getAllDescriptor()) {
            JdbcConnectionDescriptor descriptor = (JdbcConnectionDescriptor) c;
            System.out.println(descriptor);
        }

//        contentPane.add(scrollpane,BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        JButton okButton = new JButton("Ok");
        okButton.setActionCommand("Ok");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                validated = true;
                dialog.dispose();
            }
        });

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        contentPane.add(buttonPanel,BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        return dialog;
    }
}
