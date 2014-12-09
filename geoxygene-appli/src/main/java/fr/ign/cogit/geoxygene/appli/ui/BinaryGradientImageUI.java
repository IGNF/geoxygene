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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.panel.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.style.texture.BinaryGradientImageDescriptor;
import fr.ign.util.ui.SliderWithSpinner;
import fr.ign.util.ui.SliderWithSpinner.SliderWithSpinnerModel;

/**
 * @author JeT, Nicolas
 * 
 */
public class BinaryGradientImageUI extends JDialog implements
        ExpressiveRenderingUI, MouseListener, ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 5511611542508589518L;
    private JPanel main = null;
    private BinaryGradientImageDescriptor strtex = null;

    private ProjectFrame parentProjectFrame = null;

    private Color color1, color2, borderColor;
    private JLabel color1Label, color2Label, borderColorLabel;
    private JDialog color1Dialog, color2Dialog, borderColorDialog;
    private JColorChooser colorChooser;
    private double mapScale;
    private double maxCoastLineLength;
    private double resolution;
    private int blurSize;

    /**
     * Constructor
     */
    public BinaryGradientImageUI(BinaryGradientImageDescriptor strtex,
            ProjectFrame projectFrame) {
        this.parentProjectFrame = projectFrame;
        this.main = null;
        this.setGradientExpressiveRendering(strtex);

    }

    /**
     * set the managed stroke texture expressive rendering object
     * 
     * @param strtex
     */
    private void setGradientExpressiveRendering(
            BinaryGradientImageDescriptor strtex) {
        this.strtex = strtex;
        this.setValuesFromObject();
    }

    @Override
    public void setValuesFromObject() {
        this.mapScale = this.strtex.getMapScale();
        this.maxCoastLineLength = this.strtex.getMaxCoastlineLength();
        this.resolution = this.strtex.getTextureResolution();
        this.blurSize = this.strtex.getBlurSize();
        this.color1 = this.strtex.getColor1();
        this.color2 = this.strtex.getColor2();
        this.borderColor = this.strtex.getBorderColor();

    }

    @Override
    public void setValuesToObject() {
        this.strtex.setMapScale(this.mapScale);
        this.strtex.setMaxCoastlineLength(this.maxCoastLineLength);
        this.strtex.setTextureResolution(this.resolution);
        this.strtex.setBlurSize(this.blurSize);
        this.strtex.setColor1(this.color1);
        this.strtex.setColor2(this.color2);
        this.strtex.setBorderColor(this.borderColor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ui.ExpressiveRenderingUI#getGui()
     */
    @Override
    public JComponent getGui() {
        return new JScrollPane(this.getMainPanel());
    }

    protected void refresh() {
        this.setValuesToObject();
        this.parentProjectFrame.repaint();
    }

    public ImageIcon createColorIcon(Color c) {
        BufferedImage buffImColor = new BufferedImage(100, 30,
                java.awt.image.BufferedImage.TYPE_INT_RGB);

        Graphics2D g = buffImColor.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 100, 30);
        g.setColor(c);
        g.fillRect(0, 0, 100, 30);

        return new ImageIcon(buffImColor);
    }

    /**
     * This method creates and return the panel of the raw color. It form a part
     * of the color preview panel.
     * 
     * @param c
     *            the raw color of the style to be modified.
     * @return the panel of the raw color.
     */
    public JPanel createColorPanel(JLabel clabel, String labelId) {
        clabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JLabel lblColor = new JLabel(I18N.getString(labelId)); //$NON-NLS-1$
        JPanel colorPanel = new JPanel();
        colorPanel.add(lblColor);
        colorPanel.add(clabel);

        return colorPanel;
    }

    private JPanel getMainPanel() {
        if (this.main == null) {
            this.main = new JPanel();
            this.main.setLayout(new BoxLayout(this.main, BoxLayout.Y_AXIS));
            this.main.setBorder(BorderFactory
                    .createEtchedBorder(EtchedBorder.LOWERED));

            JPanel colorPanel = new JPanel();
            colorPanel.setBorder(BorderFactory
                    .createTitledBorder("Gradient colors"));

            this.color1Label = new JLabel(this.createColorIcon(this.color1));
            this.color1Label.addMouseListener(this);
            colorPanel.add(this.createColorPanel(this.color1Label,
                    "StyleEditionFrame.GradientBeginColor"));

            this.color2Label = new JLabel(this.createColorIcon(this.color2));
            this.color2Label.addMouseListener(this);
            colorPanel.add(this.createColorPanel(this.color2Label,
                    "StyleEditionFrame.GradientEndColor"));

            this.borderColorLabel = new JLabel(
                    this.createColorIcon(this.borderColor));
            this.borderColorLabel.addMouseListener(this);
            colorPanel.add(this.createColorPanel(this.borderColorLabel,
                    "StyleEditionFrame.GradientBorderColor"));

            this.main.add(colorPanel);

            SliderWithSpinnerModel mapScaleModel = new SliderWithSpinnerModel(
                    this.mapScale, 1, 10000000, 1000);
            final SliderWithSpinner mapScaleSpinner = new SliderWithSpinner(
                    mapScaleModel);
            JSpinner.NumberEditor mapScaleEditor = (JSpinner.NumberEditor) mapScaleSpinner
                    .getEditor();
            DecimalFormat intFormat = mapScaleEditor.getFormat();
            intFormat.setMinimumFractionDigits(0);
            mapScaleEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            mapScaleSpinner.setBorder(BorderFactory
                    .createTitledBorder("map scale"));
            mapScaleSpinner
            .setToolTipText("inverse map scale value: enter 25000 for 1:25000 real scale");
            mapScaleSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BinaryGradientImageUI.this.mapScale = (mapScaleSpinner
                            .getValue());
                    BinaryGradientImageUI.this.refresh();

                }
            });
            this.main.add(mapScaleSpinner);

            SliderWithSpinnerModel maxCoastLineLengthModel = new SliderWithSpinnerModel(
                    this.maxCoastLineLength, 0, 100000, 100);
            final SliderWithSpinner maxCoastLineLengthSpinner = new SliderWithSpinner(
                    maxCoastLineLengthModel);
            JSpinner.NumberEditor maxCoastLineLengthEditor = (JSpinner.NumberEditor) maxCoastLineLengthSpinner
                    .getEditor();
            maxCoastLineLengthEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            maxCoastLineLengthSpinner.setBorder(BorderFactory
                    .createTitledBorder("max coast line length"));
            maxCoastLineLengthSpinner
            .setToolTipText("max length (in meter) for edges to be considered as coast line. If greater they are considered as map border");
            maxCoastLineLengthSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BinaryGradientImageUI.this.maxCoastLineLength = (maxCoastLineLengthSpinner
                            .getValue());
                    BinaryGradientImageUI.this.refresh();

                }
            });
            this.main.add(maxCoastLineLengthSpinner);

            SliderWithSpinnerModel resolutionModel = new SliderWithSpinnerModel(
                    this.resolution, 16, 24800, 100);
            final SliderWithSpinner resolutionSpinner = new SliderWithSpinner(
                    resolutionModel);
            JSpinner.NumberEditor resolutionEditor = (JSpinner.NumberEditor) resolutionSpinner
                    .getEditor();
            resolutionEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            resolutionSpinner.setBorder(BorderFactory
                    .createTitledBorder("resolution"));
            resolutionSpinner
            .setToolTipText("resolution in DPI of the generated texture. greater than 300 may cause memory issues...");
            resolutionSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BinaryGradientImageUI.this.resolution = (resolutionSpinner
                            .getValue());
                    BinaryGradientImageUI.this.refresh();

                }
            });
            this.main.add(resolutionSpinner);

            SliderWithSpinnerModel blurSizeModel = new SliderWithSpinnerModel(
                    this.blurSize, 0, 100, 1);
            final SliderWithSpinner blurSizeSpinner = new SliderWithSpinner(
                    blurSizeModel);
            JSpinner.NumberEditor blurSizeEditor = (JSpinner.NumberEditor) blurSizeSpinner
                    .getEditor();
            intFormat = blurSizeEditor.getFormat();
            intFormat.setMinimumFractionDigits(0);
            blurSizeEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            blurSizeSpinner.setBorder(BorderFactory
                    .createTitledBorder("blur size"));
            blurSizeSpinner
            .setToolTipText("size of the blurring window applied on UV texture map after generation");
            blurSizeSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BinaryGradientImageUI.this.blurSize = (int) (double) (blurSizeSpinner
                            .getValue());
                    BinaryGradientImageUI.this.refresh();

                }
            });
            this.main.add(blurSizeSpinner);
            //
            // JPanel subShaderPanel = new JPanel(new BorderLayout());
            // subShaderPanel.setBorder(BorderFactory
            // .createTitledBorder("subshader parameters"));
            //
            // subShaderPanel.add(this.getShaderUI().getGui());
            // this.main.add(subShaderPanel);
        }
        return this.main;
    }

    private JDialog generateColorPickerUI() {
        this.colorChooser = new JColorChooser();
        this.colorChooser.addChooserPanel(new COGITColorChooserPanel());
        return JColorChooser.createDialog(this,
                I18N.getString("StyleEditionFrame.PickAColor"), //$NON-NLS-1$
                true, this.colorChooser, this, null);
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (arg0.getSource() == this.color1Label) {
            this.color1Dialog = this.generateColorPickerUI();
            this.color1Dialog.setVisible(true);
        } else if (arg0.getSource() == this.color2Label) {
            this.color2Dialog = this.generateColorPickerUI();
            this.color2Dialog.setVisible(true);
        } else if (arg0.getSource() == this.borderColorLabel) {
            this.borderColorDialog = this.generateColorPickerUI();
            this.borderColorDialog.setVisible(true);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean needUpdate = false;
        // When the user validate a color in the Color Chooser interface
        if (e.getSource().getClass() != JComboBox.class
                && e.getSource().getClass() != JRadioButton.class
                && e.getSource().getClass() != JCheckBox.class) {
            if (((JButton) e.getSource()).getActionCommand() == "OK") { //$NON-NLS-1$

                JDialog dialog = (JDialog) ((JButton) e.getSource())
                        .getParent().getParent().getParent().getParent()
                        .getParent();
                if (dialog == this.color1Dialog) {
                    // Getting the color of the dialog
                    this.color1 = this.colorChooser.getColor();
                    this.color1Label.setIcon(this.createColorIcon(this.color1));
                    needUpdate = true;
                } else if (dialog == this.color2Dialog) {
                    // Getting the color of the dialog
                    this.color2 = this.colorChooser.getColor();
                    this.color2Label.setIcon(this.createColorIcon(this.color2));
                    needUpdate = true;
                } else if (dialog == this.borderColorDialog) {
                    // Getting the color of the dialog
                    this.borderColor = this.colorChooser.getColor();
                    this.borderColorLabel.setIcon(this
                            .createColorIcon(this.borderColor));
                    needUpdate = true;
                }
            }
        }

        if (needUpdate)
            this.refresh();
    }
}
