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
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Reader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

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
public class StyleEditionExpertFrame extends JDialog implements ActionListener,
ListSelectionListener {

    private static final String FIND_PREV_COMMAND = "FindPrev";

    private static final String FIND_NEXT_COMMAND = "FindNext";

    private static final String NEWLINE = System.getProperty("line.separator");

    private static final long serialVersionUID = 87814921699188942L;

    private static final int borderSize = 20;

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
    private JPanel searchPanel = null;
    private JTextField searchField = null;
    private JCheckBox regexCB = null;
    private JCheckBox matchCaseCB = null;
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
        this.initializeGui(false);

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
     * @param sldToCopy
     *            The initial SLD styles before modifications
     */
    public void copyInitialSLD(StyledLayerDescriptor sldToCopy) {
        if (sldToCopy == null) {
            return;
        }
        CharArrayWriter writer = new CharArrayWriter();
        sldToCopy.marshall(writer);
        Reader reader = new CharArrayReader(writer.toCharArray());
        try {
            this.initialSLD = StyledLayerDescriptor.unmarshall(reader);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        this.getInitialSLD().setDataSet(sldToCopy.getDataSet());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (this.initialSLD != null) {
            this.initialSLD.marshall(baos);
        }
        this.getEditor().setText(baos.toString());
        this.getEditor().setEditable(true);
        this.getEditor().setEnabled(true);

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        // center view on current selected layer when the window is displayed
        this.gotoSelectedLayer();
    }

    /**
     * 
     */
    private void gotoSelectedLayer() {
        if (this.layerLegendPanel.getSelectedLayers().size() == 1) {
            int caret = this
                    .getEditor()
                    .getText()
                    .toLowerCase()
                    .indexOf(
                            "<name>" + this.layer.getName().toLowerCase()
                            + "</name>");
            if (caret >= 0) {
                this.getEditor().setCaretPosition(caret);
                centerLineInScrollPane(this.getEditor());
            }
        }
    }

    private static void centerLineInScrollPane(JTextComponent component) {
        Container container = SwingUtilities.getAncestorOfClass(
                JViewport.class, component);

        if (container == null) {
            return;
        }

        try {
            Rectangle r = component.modelToView(component.getCaretPosition());
            JViewport viewport = (JViewport) container;

            int extentHeight = viewport.getExtentSize().height;
            int viewHeight = viewport.getViewSize().height;

            int y = Math.max(0, r.y - extentHeight / 4);
            y = Math.min(y, viewHeight - extentHeight);

            viewport.setViewPosition(new Point(0, y));
        } catch (BadLocationException ble) {
        }
    }

    public Layer getLayer() {
        return this.layer;
    }

    private void reloadSldContent() {
        int caret = this.getEditor().getCaretPosition();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.layerViewPanel.getProjectFrame().getSld().marshall(baos);
        this.getEditor().setText(baos.toString());

        this.getEditor().setCaretPosition(caret);
    }

    private void initializeGui(boolean forceEdition) {

        this.getContentPane().removeAll();
        this.getContentPane().setLayout(new BorderLayout());
        Layer layer = null;
        if (this.layerLegendPanel.getSelectedLayers().size() == 1) {
            layer = this.layerLegendPanel.getSelectedLayers().iterator().next();
        }
        if (layer instanceof NamedLayer) {
            NamedLayer namedLayer = (NamedLayer) layer;
            this.layer = namedLayer;
            this.copyInitialSLD(this.layer.getSld());
        } else {
            this.getEditor().setEnabled(false);
            this.info("Cannot edit Layer type "
                    + layer.getClass().getSimpleName());
        }

        // listen to layers selection change
        this.layerLegendPanel.addSelectionChangeListener(this);

        if (!forceEdition
                && this.layerLegendPanel.getLayerViewPanel().getProjectFrame()
                .getSldEditionOwners().size() > 0) {
            JPanel panel = new JPanel(new GridBagLayout());
            JLabel label = new JLabel(
                    "<html><center><font color='red' family='bold' size='+2'>"
                            + I18N.getString("EditionFrame.SLDInEditionWarningMessage")
                            + "</font></center></html>");

            label.setBorder(BorderFactory.createEmptyBorder(borderSize,
                    borderSize, borderSize, borderSize));
            label.setOpaque(true);
            label.setBackground(Color.white);
            Insets insets = new Insets(2, 2, 2, 2);
            panel.add(label, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets,
                    borderSize, borderSize));
            JButton buttonOk = new JButton("Close");
            buttonOk.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    StyleEditionExpertFrame.this.dispose();
                }
            });
            panel.add(buttonOk, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, insets,
                    borderSize, borderSize));
            JButton buttonForceEdition = new JButton(
                    "Edit at your own risk anyway");
            buttonForceEdition.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    StyleEditionExpertFrame.this.initializeGui(true);
                }
            });
            panel.add(buttonForceEdition, new GridBagConstraints(0, 1, 1, 1, 0,
                    0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    insets, borderSize, borderSize));
            this.setContentPane(panel);
            this.setModalityType(ModalityType.APPLICATION_MODAL);
            this.pack();
            return;

        } else {
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    this.getEditionPanel(), this.getDisplayPanel());
            splitPane.setDividerSize(3);
            splitPane.setDividerLocation(0.7);
            splitPane.setResizeWeight(1.);
            this.getContentPane().add(splitPane, BorderLayout.CENTER);
            JPanel cmdPanel = new JPanel(new BorderLayout());
            cmdPanel.add(this.getToolsPanel(), BorderLayout.SOUTH);
            this.getContentPane().add(cmdPanel, BorderLayout.SOUTH);

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    StyleEditionExpertFrame.this.layerLegendPanel
                    .getLayerViewPanel()
                    .getProjectFrame()
                    .releaseSldEditionLock(StyleEditionExpertFrame.this);
                }
            });
            this.layerLegendPanel.getLayerViewPanel().getProjectFrame()
            .addSldEditionLock(this);
        }
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ((JDialog) e.getSource()).dispose();
            }
        });

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
            this.editionPanel.add(this.getSearchPanel(), BorderLayout.NORTH);
        }
        return this.editionPanel;
    }

    private JPanel getSearchPanel() {
        if (this.searchPanel == null) {
            this.searchPanel = new JPanel(new BorderLayout());

            // Create a toolbar with searching options.
            JToolBar toolBar = new JToolBar();
            this.searchField = new JTextField(30);
            toolBar.add(this.searchField);
            final JButton nextButton = new JButton("Next");
            nextButton.setActionCommand(FIND_NEXT_COMMAND);
            nextButton.addActionListener(this);
            toolBar.add(nextButton);
            this.searchField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    nextButton.doClick(0);
                }
            });
            JButton prevButton = new JButton("Prev");
            prevButton.setActionCommand(FIND_PREV_COMMAND);
            prevButton.addActionListener(this);
            toolBar.add(prevButton);
            this.regexCB = new JCheckBox("Regex");
            toolBar.add(this.regexCB);
            this.matchCaseCB = new JCheckBox("Case");
            toolBar.add(this.matchCaseCB);
            this.searchPanel.add(toolBar, BorderLayout.NORTH);

        }
        return this.searchPanel;
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
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
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
            this.editor.setAntiAliasingEnabled(true);
            this.editor
            .setText("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            this.editor.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_S
                            && (e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
                        StyleEditionExpertFrame.this.applyButton.doClick();
                    }
                }
            });
        }
        return this.editor;
    }

    public void applySld() {
        try {

            ByteArrayInputStream in = new ByteArrayInputStream(this.getEditor()
                    .getText().getBytes("UTF-8"));
            StyledLayerDescriptor new_sld = StyledLayerDescriptor
                    .unmarshall(in);
            this.layerViewPanel.getProjectFrame().loadSLD(new_sld, true);
        } catch (Exception e) {
            e.printStackTrace();
            this.error(e.getClass().getName(), null);
            this.error("Error found in SLD file", e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // "FindNext" => search forward, "FindPrev" => search backward
        if (e.getActionCommand().equals(FIND_NEXT_COMMAND)
                || e.getActionCommand().equals(FIND_PREV_COMMAND)) {
            String command = e.getActionCommand();
            boolean forward = FIND_NEXT_COMMAND.equals(command);

            // Create an object defining our search parameters.
            SearchContext context = new SearchContext();
            String text = this.searchField.getText();
            if (text.length() == 0) {
                return;
            }
            context.setSearchFor(text);
            context.setMatchCase(this.matchCaseCB.isSelected());
            context.setRegularExpression(this.regexCB.isSelected());
            context.setSearchForward(forward);
            context.setWholeWord(false);

            boolean found = SearchEngine.find(this.editor, context);
            if (!found) {
                JOptionPane.showMessageDialog(this, "Text not found");
            }
        }
        // When the user apply style modifications to the map and the legend
        if (e.getSource() == this.getApplyButton()) {

            this.applySld();

            this.reloadSldContent();
            this.layerLegendPanel.getModel().fireActionPerformed(null);
            this.layerLegendPanel.repaint();
            this.layerViewPanel.repaint();
        }

        // When the user cancel style modifications in the main interface
        if (e.getSource() == this.getCancelButton()) {
            this.layerViewPanel.getProjectFrame().loadSLD(this.getInitialSLD(),
                    true);
            this.layerLegendPanel.repaint();
            this.layerViewPanel.repaint();
            this.layerLegendPanel.removeSelectionChangeListener(this);
            StyleEditionExpertFrame.this.dispose();
        }

        // When the user validate style modifications
        if (e.getSource() == this.getValidButton()) {
            this.applySld();
            this.layerLegendPanel.getModel().fireActionPerformed(null);
            this.layerViewPanel.reset();
            this.layerLegendPanel.repaint();
            this.layerViewPanel.repaint();
            this.layerLegendPanel.removeSelectionChangeListener(this);
            StyleEditionExpertFrame.this.dispose();
        }

    }

    /**
     * Callback called when the selction changed in LayerLegendPanel
     * 
     * @param e
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (this.layerLegendPanel.getSelectedLayers().size() == 1) {
            Layer layer = this.layerLegendPanel.getSelectedLayers().iterator()
                    .next();
            if (layer instanceof NamedLayer) {
                NamedLayer namedLayer = (NamedLayer) layer;
                this.layer = namedLayer;
            }
        }
        this.gotoSelectedLayer();
    }

}
