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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor;
import fr.ign.util.ui.JRecentFileChooser;
import fr.ign.util.ui.SliderWithSpinner;
import fr.ign.util.ui.SliderWithSpinner.SliderWithSpinnerModel;

/**
 * @author JeT
 * 
 */
public class StrokeTextureExpressiveRenderingUI implements GenericParameterUI {

  private static final int FILE_LENGTH_DISPLAY = 50;
  private JPanel main = null;
  private StrokeTextureExpressiveRenderingDescriptor strtex = null;

  private final Preferences prefs = Preferences.userRoot();
  private ProjectFrame parentProjectFrame = null;
  double paperDensity = 0.7;
  double brushDensity = 1.9;
  double strokePressure = 2.64;
  double sharpness = 0.1;
  public String paperTextureFilename = null;
  public String brushTextureFilename = null;
  public int brushStartLength = 100;
  public int brushEndLength = 200;
  public GenericParameterUI shaderUI = null;
  private JLabel paperFilenameLabel = null;
  private JLabel brushFilenameLabel = null;

  private static final String LAST_DIRECTORY = StrokeTextureExpressiveRenderingUI.class
      .getSimpleName() + ".lastDirectory";
  private static final String PAPER_LAST_DIRECTORY = StrokeTextureExpressiveRenderingUI.class
      .getSimpleName() + ".paperLastDirectory";
  private static final String BRUSH_LAST_DIRECTORY = StrokeTextureExpressiveRenderingUI.class
      .getSimpleName() + ".brushLastDirectory";

  /**
   * Constructor
   */
  public StrokeTextureExpressiveRenderingUI(
      StrokeTextureExpressiveRenderingDescriptor strtex,
      ProjectFrame projectFrame) {
    this.parentProjectFrame = projectFrame;
    this.main = null;
    this.setStrokeTextureExpressiveRendering(strtex);

  }

  /**
   * set the managed stroke texture expressive rendering object
   * 
   * @param strtex
   */
  private void setStrokeTextureExpressiveRendering(
      StrokeTextureExpressiveRenderingDescriptor strtex) {
    this.strtex = strtex;
    this.setValuesFromObject();
  }

  /**
   * set variable values from stroke texture expressive rendering object
   */
  @Override
  public void setValuesFromObject() {
    this.paperDensity = this.strtex.getPaperDensity();
    this.brushDensity = this.strtex.getBrushDensity();
    this.strokePressure = this.strtex.getStrokePressure();
    this.sharpness = this.strtex.getSharpness();
    this.paperTextureFilename = this.strtex.getPaperTextureFilename();
    this.brushTextureFilename = this.strtex.getBrushTextureFilename();
    this.brushStartLength = this.strtex.getBrushStartLength();
    this.brushEndLength = this.strtex.getBrushEndLength();
    this.getShaderUI().setValuesFromObject();
  }

  private GenericParameterUI getShaderUI() {
    if (this.shaderUI == null) {
      this.shaderUI = ShaderUIFactory.getShaderUI(
          this.strtex.getShaderDescriptor(), this.parentProjectFrame);
    }
    return this.shaderUI;
  }

  /**
   * set variable values from stroke texture expressive rendering object
   */
  @Override
  public void setValuesToObject() {
    this.strtex.setPaperDensity(this.paperDensity);
    this.strtex.setBrushDensity(this.brushDensity);
    this.strtex.setStrokePressure(this.strokePressure);
    this.strtex.setSharpness(this.sharpness);
    this.strtex.setPaperTextureFilename(this.paperTextureFilename);
    this.strtex.setBrushTextureFilename(this.brushTextureFilename);
    this.strtex.setBrushStartLength(this.brushStartLength);
    this.strtex.setBrushEndLength(this.brushEndLength);
    this.shaderUI.setValuesToObject();
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

  private JPanel getMainPanel() {
    if (this.main == null) {
      this.main = new JPanel();
      this.main.setLayout(new BoxLayout(this.main, BoxLayout.Y_AXIS));
      this.main.setBorder(BorderFactory
          .createEtchedBorder(EtchedBorder.LOWERED));
      JPanel paperPanel = new JPanel(new BorderLayout());

      JButton paperBrowseButton = new JButton("paper browse...");
      paperBrowseButton.setToolTipText("Load background paper file");
      paperBrowseButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          JFileChooser fc = new JRecentFileChooser();
          fc.setCurrentDirectory(new File(
              StrokeTextureExpressiveRenderingUI.this.prefs.get(
                  PAPER_LAST_DIRECTORY, ".")));
          fc.setFileFilter(new FileFilter() {
            private final String[] okFileExtensions = new String[] { "jpg",
                "png", "gif" };

            @Override
            public String getDescription() {
              // TODO Auto-generated method stub
              return null;
            }

            @Override
            public boolean accept(File f) {
              for (String extension : this.okFileExtensions) {
                if (f.getName().toLowerCase().endsWith(extension)) {
                  return true;
                }
              }
              return false;
            }
          });

          if (fc
              .showOpenDialog(StrokeTextureExpressiveRenderingUI.this.parentProjectFrame
                  .getGui()) == JFileChooser.APPROVE_OPTION) {
            try {
              File selectedFile = fc.getSelectedFile();
              StrokeTextureExpressiveRenderingUI.this.paperTextureFilename = selectedFile
                  .getAbsolutePath();
              StrokeTextureExpressiveRenderingUI.this.paperFilenameLabel
              .setText(StrokeTextureExpressiveRenderingUI.this.paperTextureFilename.substring(Math
                  .max(
                      0,
                      StrokeTextureExpressiveRenderingUI.this.paperTextureFilename
                      .length() - FILE_LENGTH_DISPLAY)));

              StrokeTextureExpressiveRenderingUI.this.prefs.put(
                  PAPER_LAST_DIRECTORY, selectedFile.getAbsolutePath());

              StrokeTextureExpressiveRenderingUI.this.refresh();
            } catch (Exception e1) {
              JOptionPane.showMessageDialog(
                  StrokeTextureExpressiveRenderingUI.this.parentProjectFrame
                  .getGui(), e1.getMessage());
              e1.printStackTrace();
            }
          }
        }

      });

      paperPanel.add(paperBrowseButton, BorderLayout.EAST);
      this.paperFilenameLabel = new JLabel(
          this.paperTextureFilename.substring(Math.max(0,
              this.paperTextureFilename.length() - FILE_LENGTH_DISPLAY)));
      paperPanel.add(this.paperFilenameLabel, BorderLayout.CENTER);

      this.main.add(paperPanel);

      SliderWithSpinnerModel brushStartModel = new SliderWithSpinnerModel(
          this.brushStartLength, 1, 5000, 1);
      final SliderWithSpinner brushStartSpinner = new SliderWithSpinner(
          brushStartModel);
      JSpinner.NumberEditor brushStartEditor = (JSpinner.NumberEditor) brushStartSpinner
          .getEditor();
      DecimalFormat intFormat = brushStartEditor.getFormat();
      intFormat.setMinimumFractionDigits(0);
      brushStartEditor.getTextField().setHorizontalAlignment(
          SwingConstants.CENTER);
      brushStartSpinner.setBorder(BorderFactory
          .createTitledBorder("brush start"));
      brushStartSpinner.setToolTipText("length of the brush start");
      brushStartSpinner.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
          StrokeTextureExpressiveRenderingUI.this.brushStartLength = (int) (double) (brushStartSpinner
              .getValue());
          StrokeTextureExpressiveRenderingUI.this.refresh();

        }
      });

      SliderWithSpinnerModel brushEndModel = new SliderWithSpinnerModel(
          this.brushEndLength, 1, 5000, 1);
      final SliderWithSpinner brushEndSpinner = new SliderWithSpinner(
          brushEndModel);
      JSpinner.NumberEditor brushEndEditor = (JSpinner.NumberEditor) brushEndSpinner
          .getEditor();
      intFormat.setMinimumFractionDigits(0);
      brushEndEditor.getTextField().setHorizontalAlignment(
          SwingConstants.CENTER);
      brushEndSpinner.setBorder(BorderFactory.createTitledBorder("brush end"));
      brushEndSpinner.setToolTipText("length of the brush end");
      brushEndSpinner.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
          StrokeTextureExpressiveRenderingUI.this.brushEndLength = (int) (double) (brushEndSpinner
              .getValue());
          StrokeTextureExpressiveRenderingUI.this.refresh();

        }
      });

      JPanel brushPanel = new JPanel(new BorderLayout());

      JButton brushBrowseButton = new JButton("brush browser...");
      brushBrowseButton.setToolTipText("Load brush file");
      brushBrowseButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          JFileChooser fc = new JRecentFileChooser();
          fc.setCurrentDirectory(new File(
              StrokeTextureExpressiveRenderingUI.this.prefs.get(
                  BRUSH_LAST_DIRECTORY, ".")));
          fc.setFileFilter(new FileFilter() {
            private final String[] okFileExtensions = new String[] { "jpg",
                "png", "gif" };

            @Override
            public String getDescription() {
              // TODO Auto-generated method stub
              return null;
            }

            @Override
            public boolean accept(File f) {
              for (String extension : this.okFileExtensions) {
                if (f.getName().toLowerCase().endsWith(extension)) {
                  return true;
                }
              }
              return false;
            }
          });
          if (fc
              .showOpenDialog(StrokeTextureExpressiveRenderingUI.this.parentProjectFrame
                  .getGui()) == JFileChooser.APPROVE_OPTION) {
            try {
              File selectedFile = fc.getSelectedFile();
              StrokeTextureExpressiveRenderingUI.this.brushTextureFilename = selectedFile
                  .getAbsolutePath();
              StrokeTextureExpressiveRenderingUI.this.brushFilenameLabel
              .setText(StrokeTextureExpressiveRenderingUI.this.brushTextureFilename.substring(Math
                  .max(
                      0,
                      StrokeTextureExpressiveRenderingUI.this.brushTextureFilename
                      .length() - FILE_LENGTH_DISPLAY)));
              Pattern pattern = Pattern.compile("([0-9]+)-([0-9]+)");
              Matcher matcher = pattern
                  .matcher(StrokeTextureExpressiveRenderingUI.this.brushTextureFilename);
              if (matcher.matches()) {
                int start = Integer.valueOf(matcher.group(1));
                int end = Integer.valueOf(matcher.group(2));
                brushStartSpinner.setValue(start);
                brushEndSpinner.setValue(end);
                StrokeTextureExpressiveRenderingUI.this.brushStartLength = (int) (double) (brushStartSpinner
                    .getValue());
                StrokeTextureExpressiveRenderingUI.this.brushEndLength = (int) (double) (brushEndSpinner
                    .getValue());
              }
              StrokeTextureExpressiveRenderingUI.this.prefs.put(
                  BRUSH_LAST_DIRECTORY, selectedFile.getAbsolutePath());
              StrokeTextureExpressiveRenderingUI.this.refresh();
            } catch (Exception e1) {
              JOptionPane.showMessageDialog(
                  StrokeTextureExpressiveRenderingUI.this.parentProjectFrame
                  .getGui(), e1.getMessage());
              e1.printStackTrace();
            }
          }
        }

      });

      this.brushFilenameLabel = new JLabel(
          this.brushTextureFilename.substring(Math.max(0,
              this.brushTextureFilename.length() - FILE_LENGTH_DISPLAY)));
      brushPanel.add(brushBrowseButton, BorderLayout.EAST);
      brushPanel.add(this.brushFilenameLabel, BorderLayout.CENTER);

      this.main.add(brushPanel);

      this.main.add(brushStartSpinner);
      this.main.add(brushEndSpinner);

      SliderWithSpinnerModel brushDensityModel = new SliderWithSpinnerModel(
          this.brushDensity, 0, 10, .1);
      final SliderWithSpinner brushDensitySpinner = new SliderWithSpinner(
          brushDensityModel);
      JSpinner.NumberEditor brushDensityEditor = (JSpinner.NumberEditor) brushDensitySpinner
          .getEditor();
      brushDensityEditor.getTextField().setHorizontalAlignment(
          SwingConstants.CENTER);
      brushDensitySpinner.setBorder(BorderFactory
          .createTitledBorder("brush density"));
      brushDensitySpinner.setToolTipText("brush height scale factor");
      brushDensitySpinner.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
          StrokeTextureExpressiveRenderingUI.this.brushDensity = (brushDensitySpinner
              .getValue());
          StrokeTextureExpressiveRenderingUI.this.refresh();

        }
      });
      this.main.add(brushDensitySpinner);

      SliderWithSpinnerModel paperDensityModel = new SliderWithSpinner.SliderWithSpinnerModel(
          this.paperDensity, 0, 10, 0.1, .001);
      final SliderWithSpinner paperDensitySpinner = new SliderWithSpinner(
          paperDensityModel);
      JSpinner.NumberEditor paperDensityEditor = (JSpinner.NumberEditor) paperDensitySpinner
          .getEditor();
      paperDensityEditor.getTextField().setHorizontalAlignment(
          SwingConstants.CENTER);
      paperDensitySpinner.setBorder(BorderFactory
          .createTitledBorder("paper density"));
      paperDensitySpinner.setToolTipText("scale factor for paper height");
      paperDensitySpinner.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
          StrokeTextureExpressiveRenderingUI.this.paperDensity = (paperDensitySpinner
              .getValue());
          StrokeTextureExpressiveRenderingUI.this.refresh();

        }
      });
      this.main.add(paperDensitySpinner);

      SliderWithSpinnerModel pressureModel = new SliderWithSpinnerModel(
          this.strokePressure, 0.01, 100, .01);
      final SliderWithSpinner pressureSpinner = new SliderWithSpinner(
          pressureModel);
      JSpinner.NumberEditor pressureEditor = (JSpinner.NumberEditor) pressureSpinner
          .getEditor();
      pressureEditor.getTextField().setHorizontalAlignment(
          SwingConstants.CENTER);
      pressureSpinner.setBorder(BorderFactory
          .createTitledBorder("stroke pressure"));
      pressureSpinner.setToolTipText("distance between brush and paper");
      pressureSpinner.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
          StrokeTextureExpressiveRenderingUI.this.strokePressure = (pressureSpinner
              .getValue());
          StrokeTextureExpressiveRenderingUI.this.refresh();

        }
      });
      this.main.add(pressureSpinner);
      SliderWithSpinnerModel sharpnessModel = new SliderWithSpinnerModel(
          this.sharpness, 0.0001, 10, .001);
      final SliderWithSpinner sharpnessSpinner = new SliderWithSpinner(
          sharpnessModel);
      JSpinner.NumberEditor sharpnessEditor = (JSpinner.NumberEditor) sharpnessSpinner
          .getEditor();
      sharpnessEditor.getTextField().setHorizontalAlignment(
          SwingConstants.CENTER);
      sharpnessSpinner.setBorder(BorderFactory
          .createTitledBorder("blending sharpness"));
      sharpnessSpinner
          .setToolTipText("blending contrast between brush and paper");
      sharpnessSpinner.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
          StrokeTextureExpressiveRenderingUI.this.sharpness = (sharpnessSpinner
              .getValue());
          StrokeTextureExpressiveRenderingUI.this.refresh();

        }
      });
      this.main.add(sharpnessSpinner);

      JPanel subShaderPanel = new JPanel(new BorderLayout());
      subShaderPanel.setBorder(BorderFactory
          .createTitledBorder("subshader parameters"));

      subShaderPanel.add(this.getShaderUI().getGui());
      this.main.add(subShaderPanel);
    }
    return this.main;
  }
}
