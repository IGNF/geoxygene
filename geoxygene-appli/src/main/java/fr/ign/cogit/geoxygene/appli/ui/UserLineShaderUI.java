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

package fr.ign.cogit.geoxygene.appli.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.style.expressive.ParameterDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.UserLineShaderDescriptor;

/**
 * @author JeT
 * 
 */
public class UserLineShaderUI implements ExpressiveRenderingUI,
        ParameterChangeListener {

    private final List<ParameterUI> editors = new ArrayList<ParameterUI>();
    private final Preferences prefs = Preferences.userRoot();
    private static final String SUBSHADER_LAST_DIRECTORY = BasicTextureExpressiveRenderingUI.class
            .getSimpleName() + ".subshaderLastDirectory";
    private JPanel main = null;
    private ProjectFrame parentProjectFrame = null;
    private UserLineShaderDescriptor strtex = null;
    private String subshaderFilename = null;
    private JLabel subshaderFilenameLabel = null;

    /**
     * Constructor
     */
    public UserLineShaderUI(UserLineShaderDescriptor strtex, ProjectFrame projectFrame) {
        this.parentProjectFrame = projectFrame;
        this.setUserShaderDescriptor(strtex);
    }

    /**
     * @return the strtex
     */
    public UserLineShaderDescriptor getUserShaderDescriptor() {
        return this.strtex;
    }

    /**
     * @param strtex
     *            the strtex to set
     */
    public void setUserShaderDescriptor(UserLineShaderDescriptor strtex) {
        this.dispose();
        this.strtex = strtex;
        for (ParameterDescriptor param : strtex.getParameters()) {
            this.editors.add(ParameterUIFactory.createUI(param));
        }
        this.setValuesFromObject();
    }

    /**
     * 
     */
    private void dispose() {
        for (ParameterUI editor : this.editors) {
            editor.removeParameterChangedListener(this);
        }
        this.editors.clear();
        this.main = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ui.ExpressiveRenderingUI#getGui()
     */
    @Override
    public JComponent getGui() {
        if (this.main == null) {
            this.main = new JPanel();
            this.main.setLayout(new BoxLayout(this.main, BoxLayout.Y_AXIS));
            this.main.setBorder(BorderFactory
                    .createEtchedBorder(EtchedBorder.LOWERED));
            JPanel shaderPanel = new JPanel(new BorderLayout());
            JButton shaderBrowseButton = new JButton("shader browse...");
            shaderBrowseButton.setBorder(BorderFactory.createEmptyBorder(2, 2,
                    2, 2));
            shaderBrowseButton.setToolTipText("Load background paper file");
            shaderBrowseButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser(UserLineShaderUI.this.prefs
                            .get(SUBSHADER_LAST_DIRECTORY, "."));
                    if (fc.showOpenDialog(UserLineShaderUI.this.parentProjectFrame
                            .getGui()) == JFileChooser.APPROVE_OPTION) {
                        try {
                            File selectedFile = fc.getSelectedFile();
                            UserLineShaderUI.this.subshaderFilename = selectedFile
                                    .getAbsolutePath();
                            UserLineShaderUI.this.subshaderFilenameLabel
                                    .setText(UserLineShaderUI.this.subshaderFilename
                                            .substring(UserLineShaderUI.this.subshaderFilename
                                                    .length() - 30));

                            UserLineShaderUI.this.prefs.put(
                                    SUBSHADER_LAST_DIRECTORY,
                                    selectedFile.getAbsolutePath());

                            UserLineShaderUI.this.refresh();
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(
                                    UserLineShaderUI.this.parentProjectFrame
                                            .getGui(), e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                }

            });
            this.subshaderFilenameLabel = new JLabel(
                    this.subshaderFilename.substring(this.subshaderFilename
                            .length() - 30));
            shaderPanel.add(shaderBrowseButton, BorderLayout.WEST);
            shaderPanel.add(this.subshaderFilenameLabel, BorderLayout.CENTER);

            this.main.add(shaderPanel);
            for (ParameterUI editor : this.editors) {
                Component gui = editor.getGui();
                editor.addParameterChangedListener(this);
                this.main.add(gui);
            }
        }
        return this.main;
    }

    /**
     * set variable values from stroke texture expressive rendering object
     */
    @Override
    public void setValuesFromObject() {
        this.subshaderFilename = this.strtex.getFilename();
        for (ParameterUI editor : this.editors) {
            editor.setValuesFromObject();
        }
    }

    /**
     * set variable values from stroke texture expressive rendering object
     */
    @Override
    public void setValuesToObject() {
        this.strtex.setFilename(this.subshaderFilename);
        for (ParameterUI editor : this.editors) {
            editor.setValuesToObject();
        }
    }

    protected void refresh() {
        this.setValuesToObject();
        this.parentProjectFrame.repaint();
    }

    @Override
    public void onParameterChange(ParameterChangeEvent event) {
        this.refresh();
    }

}
