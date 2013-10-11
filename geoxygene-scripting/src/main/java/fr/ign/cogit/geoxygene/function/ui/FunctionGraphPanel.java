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

package fr.ign.cogit.geoxygene.function.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import jsyntaxpane.DefaultSyntaxKit;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.reflections.Reflections;

import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.scripting.GroovyConsoleUI;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * @author JeT
 *
 */
public class FunctionGraphPanel extends JPanel {

  private static final long serialVersionUID = 2201543469755639190L;
  protected static final Color ErrorBackgroundColor = new Color(0.8f, 0.1f, 0.0f);
  protected static final Color OkBackgroundColor = new Color(1f, 1f, 1f);
  private static ImageIcon drawIcon = new ImageIcon(FunctionGraphPanel.class.getResource("/fr/irit/vortex/scripting/refresh.png"));

  private Color axisColor = Color.black;
  private Color graphBgColor = new Color(0.97f, 0.97f, 0.97f);
  private Color graphLineColor = new Color(0.1f, 0.1f, 0.8f);
  private Color smallTickColor = new Color(0.95f, 0.95f, 0.95f);
  private Color wideTickColor = new Color(0.9f, 0.9f, 0.9f);
  private int smallTickSize = 2;
  private int wideTickSize = 4;

  private JPanel graphPanel = null;
  private JPanel groovyPanel = null;
  private JPanel paramPanel = null;
  private JPanel commandPanel = null;
  private JPanel editionPanel = null;
  private JTextField xMinField = null;
  private JTextField xMaxField = null;
  private JTextField yMinField = null;
  private JTextField yMaxField = null;
  private JTextField xSmallTickField = null;
  private JTextField xWideTickField = null;
  private JTextField ySmallTickField = null;
  private JTextField yWideTickField = null;
  private GroovyShell groovyShell = null;
  private Script groovyScript = null;
  private JEditorPane editor = null;
  private JLabel notificationLabel = null;
  private JButton drawButton = null;
  // JAVA7: 
  //  private JComboBox<Class<? extends Function1D>> helpComboBox = null;
  private JComboBox helpComboBox = null;
  private double xMinValue = -1;
  private double xMaxValue = 1;
  private double yMinValue = -1;
  private double yMaxValue = 1;
  private double xSmallTickValue = 0.1;
  private double xWideTickValue = 1;
  private double ySmallTickValue = .1;
  private double yWideTickValue = 1;

  public FunctionGraphPanel() {
    this.setLayout(new BorderLayout());
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.getGraphPanel(), this.getGroovyPanel());
    splitPane.setResizeWeight(0.8);
    splitPane.setDividerSize(3);
    this.add(splitPane, BorderLayout.CENTER);
    JPanel southPanel = new JPanel(new BorderLayout());
    southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
    southPanel.add(this.getParamPanel(), this.getNotificationLabel());
    this.add(southPanel, BorderLayout.SOUTH);
  }

  private JPanel getGroovyPanel() {
    if (this.groovyPanel == null) {
      this.groovyPanel = new JPanel(new BorderLayout());
      this.groovyPanel.add(this.getCommandPanel(), BorderLayout.NORTH);
      this.groovyPanel.add(this.getEditionPanel(), BorderLayout.CENTER);
    }
    return this.groovyPanel;
  }

  private JPanel getCommandPanel() {
    if (this.commandPanel == null) {
      this.commandPanel = new JPanel(new BorderLayout());
      this.commandPanel.add(this.getDrawButton(), BorderLayout.EAST);
      this.commandPanel.add(this.getHelpComboBox(), BorderLayout.CENTER);
    }
    return this.commandPanel;
  }

  //  JAVA7:
  //  private JComboBox<Class<? extends Function1D>> getHelpComboBox() {
  //    if (this.helpComboBox == null) {
  //      this.helpComboBox = new JComboBox<Class<? extends Function1D>>();
  //      Set<Class<? extends Function1D>> functions = this.getFunctionList();
  //      for (Class<? extends Function1D> function : functions) {
  //        this.helpComboBox.addItem(function);
  //        this.helpComboBox.setRenderer(new FunctionClassRenderer());
  //      }
  //    }
  //    return this.helpComboBox;
  //  }

  private JComboBox getHelpComboBox() {
    if (this.helpComboBox == null) {
      this.helpComboBox = new JComboBox();
      Set<Class<? extends Function1D>> functions = this.getFunctionList();
      for (Class<? extends Function1D> function : functions) {
        this.helpComboBox.addItem(function);
        this.helpComboBox.setRenderer(new FunctionClassRenderer());
      }
    }
    return this.helpComboBox;
  }

  private Set<Class<? extends Function1D>> getFunctionList() {
    Reflections reflections = new Reflections("fr.ign.cogit.geoxygene.function");
    return reflections.getSubTypesOf(Function1D.class);
  }

  private JButton getDrawButton() {
    if (this.drawButton == null) {
      this.drawButton = new JButton(drawIcon);
      //      this.drawButton.setBorder(emptyBorder);
      this.drawButton.setToolTipText("Send changes");
      this.drawButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent e) {
          FunctionGraphPanel.this.repaint();

        }
      });
    }
    return this.drawButton;
  }

  private JPanel getParamPanel() {
    if (this.paramPanel == null) {
      this.paramPanel = new JPanel(new GridLayout(0, 4, 10, 10));
      this.paramPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      this.paramPanel.add(new JLabel("x min"));
      this.paramPanel.add(this.getXMinTextField());
      this.paramPanel.add(new JLabel("x max"));
      this.paramPanel.add(this.getXMaxTextField());
      this.paramPanel.add(new JLabel("y min"));
      this.paramPanel.add(this.getYMinTextField());
      this.paramPanel.add(new JLabel("y max"));
      this.paramPanel.add(this.getYMaxTextField());
      this.paramPanel.add(new JLabel("x small tick"));
      this.paramPanel.add(this.getXSmallTickTextField());
      this.paramPanel.add(new JLabel("x wide tick"));
      this.paramPanel.add(this.getXWideTickTextField());
      this.paramPanel.add(new JLabel("y small tick"));
      this.paramPanel.add(this.getYSmallTickTextField());
      this.paramPanel.add(new JLabel("y wide tick"));
      this.paramPanel.add(this.getYWideTickTextField());
    }
    return this.paramPanel;
  }

  public Script getGroovyScript() {
    if (this.groovyScript == null) {
      this.groovyScript = this.getGroovyShell().parse(this.getEditor().getText());
      this.groovyScript.setProperty("config", this.getGroovyShell().getProperty("config"));
    }
    return this.groovyScript;
  }

  public GroovyShell getGroovyShell() {
    if (this.groovyShell == null) {
      Binding binding = new Binding();
      ImportCustomizer defaultImports = new ImportCustomizer();
      defaultImports.addStaticStars("java.lang.Math");
      defaultImports.addStarImports("fr.ign.cogit.geoxygene.function");
      final CompilerConfiguration config = new CompilerConfiguration();
      config.addCompilationCustomizers(defaultImports);
      this.groovyShell = new GroovyShell(binding, config);
    }
    return this.groovyShell;
  }

  /**
   * @param groovyShell the groovyShell to set
   */
  public void setGroovyShell(final GroovyShell groovyShell) {
    this.groovyShell = groovyShell;
  }

  private JPanel getGraphPanel() {
    if (this.graphPanel == null) {
      this.graphPanel = new JPanel() {

        /* (non-Javadoc)
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        @Override
        public void paintComponent(final Graphics g) {
          super.paintComponent(g);
          int width = this.getWidth();
          int height = this.getHeight();
          Graphics2D g2 = (Graphics2D) g;
          FunctionGraphPanel.this.draw(g2, width, height);
        }

      };
      this.graphPanel.setBackground(Color.white);
      this.setPreferredSize(new Dimension(500, 500));
    }
    return this.graphPanel;
  }

  private JTextField getXSmallTickTextField() {
    if (this.xSmallTickField == null) {
      this.xSmallTickField = new JTextField(String.valueOf(this.xSmallTickValue));
      this.xSmallTickField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          try {
            FunctionGraphPanel.this.xSmallTickValue = Double.valueOf(FunctionGraphPanel.this.xSmallTickField.getText());
            FunctionGraphPanel.this.xSmallTickField.setBackground(OkBackgroundColor);
            FunctionGraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            FunctionGraphPanel.this.xSmallTickField.setBackground(ErrorBackgroundColor);
          }
        }
      });
    }
    return this.xSmallTickField;
  }

  private JTextField getXWideTickTextField() {
    if (this.xWideTickField == null) {
      this.xWideTickField = new JTextField(String.valueOf(this.xWideTickValue));
      this.xWideTickField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          try {
            FunctionGraphPanel.this.xWideTickValue = Double.valueOf(FunctionGraphPanel.this.xWideTickField.getText());
            FunctionGraphPanel.this.xWideTickField.setBackground(OkBackgroundColor);
            FunctionGraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            FunctionGraphPanel.this.xWideTickField.setBackground(ErrorBackgroundColor);
          }
        }
      });
    }
    return this.xWideTickField;
  }

  private JTextField getYSmallTickTextField() {
    if (this.ySmallTickField == null) {
      this.ySmallTickField = new JTextField(String.valueOf(this.ySmallTickValue));
      this.ySmallTickField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          try {
            FunctionGraphPanel.this.ySmallTickValue = Double.valueOf(FunctionGraphPanel.this.ySmallTickField.getText());
            FunctionGraphPanel.this.ySmallTickField.setBackground(OkBackgroundColor);
            FunctionGraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            FunctionGraphPanel.this.ySmallTickField.setBackground(ErrorBackgroundColor);
          }
        }
      });
    }
    return this.ySmallTickField;
  }

  private JTextField getYWideTickTextField() {
    if (this.yWideTickField == null) {
      this.yWideTickField = new JTextField(String.valueOf(this.yWideTickValue));
      this.yWideTickField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          try {
            FunctionGraphPanel.this.yWideTickValue = Double.valueOf(FunctionGraphPanel.this.yWideTickField.getText());
            FunctionGraphPanel.this.yWideTickField.setBackground(OkBackgroundColor);
            FunctionGraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            FunctionGraphPanel.this.yWideTickField.setBackground(ErrorBackgroundColor);
          }
        }
      });
    }
    return this.yWideTickField;
  }

  private JTextField getXMinTextField() {
    if (this.xMinField == null) {
      this.xMinField = new JTextField(String.valueOf(this.xMinValue));
      this.xMinField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          try {
            FunctionGraphPanel.this.xMinValue = Double.valueOf(FunctionGraphPanel.this.xMinField.getText());
            FunctionGraphPanel.this.xMinField.setBackground(OkBackgroundColor);
            FunctionGraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            FunctionGraphPanel.this.xMinField.setBackground(ErrorBackgroundColor);
          }
        }
      });
    }
    return this.xMinField;
  }

  private JTextField getXMaxTextField() {
    if (this.xMaxField == null) {
      this.xMaxField = new JTextField(String.valueOf(this.xMaxValue));
      this.xMaxField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          try {
            FunctionGraphPanel.this.xMaxValue = Double.valueOf(FunctionGraphPanel.this.xMaxField.getText());
            FunctionGraphPanel.this.xMaxField.setBackground(OkBackgroundColor);
            FunctionGraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            FunctionGraphPanel.this.xMaxField.setBackground(ErrorBackgroundColor);
          }
        }
      });
    }
    return this.xMaxField;
  }

  private JTextField getYMinTextField() {
    if (this.yMinField == null) {
      this.yMinField = new JTextField(String.valueOf(this.yMinValue));
      this.yMinField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          try {
            FunctionGraphPanel.this.yMinValue = Double.valueOf(FunctionGraphPanel.this.yMinField.getText());
            FunctionGraphPanel.this.yMinField.setBackground(OkBackgroundColor);
            FunctionGraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            FunctionGraphPanel.this.yMinField.setBackground(ErrorBackgroundColor);
          }
        }
      });
    }
    return this.yMinField;
  }

  private JTextField getYMaxTextField() {
    if (this.yMaxField == null) {
      this.yMaxField = new JTextField(String.valueOf(this.yMaxValue));
      this.yMaxField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          try {
            FunctionGraphPanel.this.yMaxValue = Double.valueOf(FunctionGraphPanel.this.yMaxField.getText());
            FunctionGraphPanel.this.yMaxField.setBackground(OkBackgroundColor);
            FunctionGraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            FunctionGraphPanel.this.yMaxField.setBackground(ErrorBackgroundColor);
          }
        }
      });
    }
    return this.yMaxField;
  }

  private JLabel getNotificationLabel() {
    if (this.notificationLabel == null) {
      this.notificationLabel = new JLabel("Welcome in the groovy console");
      this.notificationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    }
    return this.notificationLabel;
  }

  private JPanel getEditionPanel() {
    if (this.editionPanel == null) {
      this.editionPanel = new JPanel(new BorderLayout());
      this.editionPanel.setPreferredSize(new Dimension(400, 300));
      this.editionPanel.add(new JScrollPane(this.getEditor()), BorderLayout.CENTER);

      // HACK due to a bug in JEditorPane highlighting. Content type has to be set AFTER the editor is added to the GUI
      // and text is destroyed when setting the language :(
      DefaultSyntaxKit.initKit();
      String text = this.getEditor().getText();
      this.getEditor().setContentType("text/groovy");
      this.getEditor().setText(text);
    }
    return this.editionPanel;
  }

  JEditorPane getEditor() {
    if (this.editor == null) {
      this.editor = new JEditorPane();

      // hardcoded key mapping
      this.editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "graphAction");
      this.editor.getActionMap().put("graphAction", new AbstractAction() {

        @Override
        public void actionPerformed(final ActionEvent actionevent) {
          FunctionGraphPanel.this.repaint();
        }

      });
      this.editor.setText("//x*x\n//new GaussFunction(1,0,1).evaluate( x )\nsin(x*PI)");
    }
    return this.editor;
  }

  public void displayNotification(final String notification) {
    this.getNotificationLabel().setText(notification);
  }

  private void draw(final Graphics2D g2, final int width, final int height) {
    try {
      g2.setColor(this.graphBgColor);
      g2.fillRect(0, 0, width, height);
      int xZeroPixel = (int) this.xWorldToPixel(0);
      int yZeroPixel = (int) this.yWorldToPixel(0);

      // draw small ticks
      if (true) {
        double xMinWorld = Math.round(this.xPixelToWorld(0) / this.xSmallTickValue) * this.xSmallTickValue;
        double xMaxWorld = Math.round(this.xPixelToWorld(width) / this.xSmallTickValue) * this.xSmallTickValue;
        double yMinWorld = Math.round(this.yPixelToWorld(0) / this.ySmallTickValue) * this.ySmallTickValue;
        double yMaxWorld = Math.round(this.yPixelToWorld(height) / this.ySmallTickValue) * this.ySmallTickValue;
        //        System.err.println("x = " + xMinWorld + "x" + xMaxWorld);
        //        System.err.println("y = " + yMinWorld + "x" + yMaxWorld);
        g2.setColor(this.smallTickColor);
        for (double xWorld = Math.min(xMinWorld, xMaxWorld); xWorld < Math.max(xMinWorld, xMaxWorld); xWorld += this.xSmallTickValue) {
          int xPixel = (int) this.xWorldToPixel(xWorld);
          g2.drawLine(xPixel, 0, xPixel, height);
        }
        for (double yWorld = Math.min(yMinWorld, yMaxWorld); yWorld < Math.max(yMinWorld, yMaxWorld); yWorld += this.ySmallTickValue) {
          int yPixel = (int) this.yWorldToPixel(yWorld);
          g2.drawLine(0, yPixel, width, yPixel);
        }
        g2.setColor(this.axisColor);
        for (double xWorld = Math.min(xMinWorld, xMaxWorld); xWorld < Math.max(xMinWorld, xMaxWorld); xWorld += this.xSmallTickValue) {
          int xPixel = (int) this.xWorldToPixel(xWorld);
          g2.drawLine(xPixel, yZeroPixel - this.smallTickSize, xPixel, yZeroPixel + this.smallTickSize);
        }
        for (double yWorld = Math.min(yMinWorld, yMaxWorld); yWorld < Math.max(yMinWorld, yMaxWorld); yWorld += this.ySmallTickValue) {
          int yPixel = (int) this.yWorldToPixel(yWorld);
          g2.drawLine(xZeroPixel - this.smallTickSize, yPixel, xZeroPixel + this.smallTickSize, yPixel);
        }
      }      // draw wide ticks
      if (true) {
        double xMinWorld = Math.round(this.xPixelToWorld(0) / this.xWideTickValue) * this.xWideTickValue;
        double xMaxWorld = Math.round(this.xPixelToWorld(width) / this.xWideTickValue) * this.xWideTickValue;
        double yMinWorld = Math.round(this.yPixelToWorld(0) / this.yWideTickValue) * this.yWideTickValue;
        double yMaxWorld = Math.round(this.yPixelToWorld(height) / this.yWideTickValue) * this.yWideTickValue;
        g2.setColor(this.wideTickColor);

        for (double xWorld = Math.min(xMinWorld, xMaxWorld); xWorld < Math.max(xMinWorld, xMaxWorld); xWorld += this.xWideTickValue) {
          int xPixel = (int) this.xWorldToPixel(xWorld);
          g2.drawLine(xPixel, 0, xPixel, height);
        }
        for (double yWorld = Math.min(yMinWorld, yMaxWorld); yWorld < Math.max(yMinWorld, yMaxWorld); yWorld += this.yWideTickValue) {
          int yPixel = (int) this.yWorldToPixel(yWorld);
          g2.drawLine(0, yPixel, width, yPixel);
        }
        g2.setColor(this.axisColor);
        for (double xWorld = Math.min(xMinWorld, xMaxWorld); xWorld < Math.max(xMinWorld, xMaxWorld); xWorld += this.xWideTickValue) {
          int xPixel = (int) this.xWorldToPixel(xWorld);
          g2.drawLine(xPixel, yZeroPixel - this.wideTickSize, xPixel, yZeroPixel + this.wideTickSize);
        }
        for (double yWorld = Math.min(yMinWorld, yMaxWorld); yWorld < Math.max(yMinWorld, yMaxWorld); yWorld += this.yWideTickValue) {
          int yPixel = (int) this.yWorldToPixel(yWorld);
          g2.drawLine(xZeroPixel - this.wideTickSize, yPixel, xZeroPixel + this.wideTickSize, yPixel);
        }
      }

      // draw axis
      g2.setColor(this.axisColor);
      if (xZeroPixel >= 0 && xZeroPixel < width) {
        g2.drawLine(xZeroPixel, 0, xZeroPixel, height);
      }
      if (yZeroPixel >= 0 && yZeroPixel < height) {
        g2.drawLine(0, yZeroPixel, width, yZeroPixel);
      }

      // draw graph
      int[] xPoints = new int[width];
      int[] yPoints = new int[width];
      int n = 0;
      for (int xPixel = 0; xPixel < width; xPixel += 10) {
        double xWorld = this.xPixelToWorld(xPixel);
        double yWorld;
        yWorld = this.evaluate(xWorld);
        int yPixel = (int) this.yWorldToPixel(yWorld);
        xPoints[n] = xPixel;
        yPoints[n] = yPixel;
        n++;
      }
      g2.setColor(this.graphLineColor);
      g2.drawPolyline(xPoints, yPoints, n);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private double evaluate(final double x) {
    this.getGroovyShell().setVariable("x", x);
    Object result = this.getGroovyShell().evaluate(this.getEditor().getText());
    return (Double) result;
  }

  private double xWorldToPixel(final double xWorld) {
    double w = this.xMaxValue - this.xMinValue;
    if (Math.abs(w) < 1E-6) {
      return xWorld;
    }
    return (xWorld - this.xMinValue) * (this.getGraphPanel().getWidth() - 1) / w;
  }

  private double yWorldToPixel(final double yWorld) {
    double h = this.yMaxValue - this.yMinValue;
    if (Math.abs(h) < 1E-6) {
      return yWorld;
    }
    return (this.getGraphPanel().getHeight() - 1) * (1 - (yWorld - this.yMinValue) / h);
  }

  private double xPixelToWorld(final double xPixel) {
    double w = this.getGraphPanel().getWidth() - 1;
    if (w < 0.5) {
      return xPixel;
    }
    return this.xMinValue + (this.xMaxValue - this.xMinValue) * xPixel / w;
  }

  private double yPixelToWorld(final double yPixel) {
    double h = this.getGraphPanel().getHeight() - 1;
    if (h < 0.5) {
      return yPixel;
    }
    return this.yMinValue + (this.yMaxValue - this.yMinValue) * (h - yPixel) / h;
  }

}
