/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * Style Edition Main Frame in expert mode. It displays a layer's sld as an
 * editable XML file
 * 
 * @author JeT
 */
public class StyleEditionExpertFrame extends JDialog implements ActionListener {

    private static final String NEWLINE = System.getProperty("line.separator");

    private static final long serialVersionUID = 87814921699188942L;

    private static Logger logger = Logger
            .getLogger(StyleEditionExpertFrame.class.getName());
    private static Border border = BorderFactory
            .createBevelBorder(BevelBorder.LOWERED);

    // Main GeOxygene application elements
    private LayerLegendPanel layerLegendPanel = null;
    private LayerViewPanel layerViewPanel = null;
    private ProjectFrame projectFrame = null;
    private NamedLayer layer = null;
    // Main Dialog Elements
    private JPanel editionPanel = null;
    private JPanel toolsPanel = null;
    private JPanel displayPanel = null;
    private RSyntaxTextArea editor = null;
    private JTextPane displayTextPane = null;
    private JButton applyButton;
    private JButton validButton;
    private JButton cancelButton;

    private SimpleAttributeSet infoAttr = null;
    private SimpleAttributeSet criticAttr = null;
    private SimpleAttributeSet errorAttr = null;
    private SimpleAttributeSet bindingAttr = null;
    private final Color resultBackgroundColor = new Color(250, 250, 220);
    private final Color defaultBackground = this.resultBackgroundColor;
    private final Color defaultForeground = Color.black;
    private final Color errorBackground = this.resultBackgroundColor;
    private final Color errorForeground = Color.red;
    private final Color criticBackground = Color.red;
    private final Color criticForeground = Color.white;
    private final Color bindingBackground = this.resultBackgroundColor;
    private final Color bindingForeground = new Color(10, 80, 20);

    // The initial SLD styles before modifications
    private StyledLayerDescriptor initialSLD = null;

    /**
     * Constructor of Style Edition Main Frame.
     * 
     * @param projectFrame
     *            the project frame where the layer lies
     * @param layerLegendPanel
     *            the layerLegendPanel of the style to be modified.
     */
    public StyleEditionExpertFrame(ProjectFrame projectFrame,
            LayerLegendPanel layerLegendPanel) {
        super(SwingUtilities.getWindowAncestor(layerLegendPanel));
        this.layerLegendPanel = layerLegendPanel;
        this.projectFrame = projectFrame;
        this.layerViewPanel = this.layerLegendPanel.getLayerViewPanel();
        this.initializeColors();
        this.initializeGui();
        Layer layer = null;
        if (layerLegendPanel.getSelectedLayers().size() == 1) {
            layer = this.layerLegendPanel.getSelectedLayers().iterator().next();
        }
        if (layer instanceof NamedLayer) {
            NamedLayer namedLayer = (NamedLayer) layer;
            this.layer = namedLayer;
            this.setInitialSLD(this.layer.getSld());
        } else {
            this.getEditor().setEnabled(false);
            this.info("Cannot edit Layer type "
                    + layer.getClass().getSimpleName());
        }
    }

    private void initializeColors() {
        this.infoAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(this.infoAttr, this.defaultForeground);
        StyleConstants.setBackground(this.infoAttr, this.defaultBackground);
        StyleConstants.setBold(this.infoAttr, true);
        this.errorAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(this.errorAttr, this.errorForeground);
        StyleConstants.setBackground(this.errorAttr, this.errorBackground);
        StyleConstants.setBold(this.errorAttr, false);
        this.criticAttr = new SimpleAttributeSet();
        StyleConstants.setBackground(this.criticAttr, this.criticBackground);
        StyleConstants.setForeground(this.criticAttr, this.criticForeground);
        StyleConstants.setBold(this.criticAttr, false);
        this.bindingAttr = new SimpleAttributeSet();
        StyleConstants.setBackground(this.bindingAttr, this.bindingBackground);
        StyleConstants.setForeground(this.bindingAttr, this.bindingForeground);
        StyleConstants.setBold(this.bindingAttr, false);
    }

    private void info(String message) {
        StyledDocument doc = this.getResultDocument();
        try {
            doc.insertString(doc.getLength(), message + NEWLINE, this.infoAttr);
        } catch (BadLocationException e) {
            logger.info(message);
        }
    }

    private String formatStackTraceElement(final StackTraceElement ste) {
        return ste.toString();
    }

    private void error(String message, Throwable e) {
        StyledDocument doc = this.getResultDocument();
        if (message != null) {
            try {
                doc.insertString(doc.getLength(), message + NEWLINE,
                        this.criticAttr);
            } catch (BadLocationException e1) {
                logger.error(e1.getMessage());
            }
        }
        if (e != null) {
            try {
                doc.insertString(doc.getLength(), e.getMessage() + NEWLINE,
                        this.criticAttr);
            } catch (BadLocationException e1) {
                logger.error(e.getMessage());
            }
            StackTraceElement[] stackTrace = e.getStackTrace();
            StringBuilder str = new StringBuilder();
            for (StackTraceElement ste : stackTrace) {
                str.append(this.formatStackTraceElement(ste) + NEWLINE);
            }
            try {
                doc.insertString(doc.getLength(), str.toString(),
                        this.errorAttr);
            } catch (BadLocationException e1) {
                logger.error(str.toString());
            }
            if (e.getCause() != null) {
                this.error("CausedBy", e.getCause());
            }
        }
    }

    /**
     * @return The initial SLD styles before modifications
     */
    public StyledLayerDescriptor getInitialSLD() {
        return this.initialSLD;
    }

    /**
     * @param initialSLD
     *            The initial SLD styles before modifications
     */
    public void setInitialSLD(StyledLayerDescriptor initialSLD) {
        this.initialSLD = initialSLD;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (this.initialSLD != null) {
            this.initialSLD.marshall(baos);
        }
        this.getEditor().setText(baos.toString());
        this.getEditor().setEditable(true);
        this.getEditor().setEnabled(true);
    }

    public Layer getLayer() {
        return this.layer;
    }

    private void initializeGui() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                this.getEditionPanel(), this.getDisplayPanel());
        splitPane.setDividerSize(3);
        splitPane.setDividerLocation(0.7);
        splitPane.setResizeWeight(1.);
        this.getContentPane().add(splitPane, BorderLayout.CENTER);
        JPanel cmdPanel = new JPanel(new BorderLayout());
        cmdPanel.add(this.getToolsPanel(), BorderLayout.SOUTH);
        this.getContentPane().add(cmdPanel, BorderLayout.SOUTH);
        this.setTitle(I18N.getString("StyleEditionFrame.StyleEdition")); //$NON-NLS-1$
        this.pack();
        this.setSize(650, 750);
        this.setLocation(200, 200);
        this.setAlwaysOnTop(false);

    }

    private StyledDocument getResultDocument() {
        return this.getDisplayTextPane().getStyledDocument();
    }

    private JPanel getEditionPanel() {
        if (this.editionPanel == null) {
            this.editionPanel = new JPanel(new BorderLayout());
            this.editionPanel.setBorder(border);
            RTextScrollPane sp = new RTextScrollPane(this.getEditor());
            sp.setFoldIndicatorEnabled(true);

            this.editionPanel.add(sp, BorderLayout.CENTER);
        }
        return this.editionPanel;
    }

    public JButton getValidButton() {
        if (this.validButton == null) {
            this.validButton = new JButton(
                    I18N.getString("StyleEditionFrame.Ok")); //$NON-NLS-1$
            this.validButton.addActionListener(this);
            this.validButton.setBounds(50, 50, 100, 20);
        }
        return this.validButton;
    }

    public JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton(
                    I18N.getString("StyleEditionFrame.Cancel")); //$NON-NLS-1$
            this.cancelButton.addActionListener(this);
            this.cancelButton.setBounds(50, 50, 100, 20);
        }
        return this.cancelButton;
    }

    public JButton getApplyButton() {
        if (this.applyButton == null) {
            this.applyButton = new JButton(
                    I18N.getString("StyleEditionFrame.Apply")); //$NON-NLS-1$
            this.applyButton.addActionListener(this);
            this.applyButton.setBounds(50, 50, 100, 20);
        }
        return this.applyButton;
    }

    private JPanel getToolsPanel() {
        if (this.toolsPanel == null) {
            this.toolsPanel = new JPanel(new BorderLayout());
            this.toolsPanel.add(this.getApplyButton(), BorderLayout.CENTER);
            this.toolsPanel.setBorder(border);
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(this.getApplyButton());
            buttonPanel.add(this.getValidButton());
            buttonPanel.add(this.getCancelButton());

            buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
            this.toolsPanel.add(buttonPanel);
        }
        return this.toolsPanel;
    }

    private JPanel getDisplayPanel() {
        if (this.displayPanel == null) {
            this.displayPanel = new JPanel(new BorderLayout());
            this.displayPanel.setBorder(border);
            this.displayPanel.add(new JScrollPane(this.getDisplayTextPane(),
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                    BorderLayout.CENTER);
        }
        return this.displayPanel;
    }

    private JTextPane getDisplayTextPane() {
        if (this.displayTextPane == null) {
            this.displayTextPane = new JTextPane();
            this.displayTextPane.setEditable(false);
            this.displayTextPane.setBackground(this.resultBackgroundColor);
            this.displayTextPane.setForeground(Color.black);
        }
        return this.displayTextPane;
    }

    RSyntaxTextArea getEditor() {
        if (this.editor == null) {
            this.editor = new RSyntaxTextArea(20, 60);
            this.editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
            this.editor.setCodeFoldingEnabled(true);
            this.editor
                    .setText("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        }
        return this.editor;
    }

    public void applySld() {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(this.getEditor()
                    .getText().getBytes("UTF-8"));
            StyledLayerDescriptor new_sld = StyledLayerDescriptor
                    .unmarshall(in);

            // copy/paste from AbstractProjectFrame::loadSld()
            if (new_sld != null) {
                this.layer.getSld().setBackground(new_sld.getBackground());
                this.layerViewPanel.setViewBackground(new_sld.getBackground());
                for (int i = 0; i < this.getInitialSLD().getLayers().size(); i++) {
                    String name = this.layer.getSld().getLayers().get(i)
                            .getName();
                    // logger.debug(name);
                    // vérifier que le layer est décrit dans le SLD
                    if (new_sld.getLayer(name) != null) {
                        if (new_sld.getLayer(name).getStyles() != null) {
                            // logger.debug(new_sld.getLayer(name).getStyles());
                            this.layer
                                    .getSld()
                                    .getLayers()
                                    .get(i)
                                    .setStyles(
                                            new_sld.getLayer(name).getStyles());

                        } else {
                            logger.trace("Le layer " + name
                                    + " n'a pas de style défini dans le SLD");
                        }
                    } else {
                        logger.trace("Le layer " + name
                                + " n'est pas décrit dans le SLD");
                    }
                }

                this.layerLegendPanel.repaint();
                this.layerViewPanel.repaint();

                /**
                 * // loading finished
                 */
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.error(e.getClass().getName(), null);
            this.error("Error found in SLD file", e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // When the user apply style modifications to the map and the legend
        if (e.getSource() == this.getApplyButton()) {
            this.applySld();
            this.layerLegendPanel.getModel().fireActionPerformed(null);
            this.layerLegendPanel.repaint();
            this.layerViewPanel.repaint();
        }

        // When the user cancel style modifications in the main interface
        if (e.getSource() == this.getCancelButton()) {
            this.layer.setSld(this.getInitialSLD());
            this.layerLegendPanel.repaint();
            this.layerViewPanel.repaint();
            StyleEditionExpertFrame.this.dispose();
        }

        // When the user validate style modifications
        if (e.getSource() == this.getValidButton()) {
            this.applySld();
            this.layerLegendPanel.getModel().fireActionPerformed(null);
            this.layerLegendPanel.repaint();
            this.layerViewPanel.repaint();
            this.layerViewPanel.reset();
            StyleEditionExpertFrame.this.dispose();
        }

    }

}
