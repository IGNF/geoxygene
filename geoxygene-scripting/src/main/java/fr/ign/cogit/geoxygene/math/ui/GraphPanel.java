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

package fr.ign.cogit.geoxygene.math.ui;

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
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import fr.ign.cogit.geoxygene.math.MathUtil;
import jsyntaxpane.DefaultSyntaxKit;

/**
 * @author JeT
 *
 */
public class GraphPanel extends JPanel {

  private static final long serialVersionUID = 2201543469755639190L;
  protected static final Color ErrorBackgroundColor = new Color(0.8f, 0.1f, 0.0f);
  protected static final Color OkBackgroundColor = new Color(1f, 1f, 1f);
  private static ImageIcon drawIcon = new ImageIcon(GraphPanel.class.getResource("/fr/irit/vortex/scripting/refresh.png"));

  private Color axisColor = Color.black;
  private Color graphBgColor = new Color(0.97f, 0.97f, 0.97f);
  private Color pointColor = new Color(0.1f, 0.1f, 0.8f);
  private int pointSize = 3;
  private Color vectorColor = new Color(0.8f, 0.1f, 0.1f);
  private Color smallTickColor = new Color(0.95f, 0.95f, 0.95f);
  private Color wideTickColor = new Color(0.9f, 0.9f, 0.9f);
  private int smallTickSize = 2;
  private int wideTickSize = 4;

  private JPanel graphPanel = null;
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
  private JEditorPane editor = null;
  private JLabel notificationLabel = null;
  private JButton drawButton = null;

  // JAVA7: 
  //  private JComboBox<Class<? extends Function1D>> helpComboBox = null;
  private JPanel toolPanel = null;
  private double xMinValue = -1;
  private double xMaxValue = 1;
  private double yMinValue = -1;
  private double yMaxValue = 1;
  private double xSmallTickValue = 0.1;
  private double xWideTickValue = 1;
  private double ySmallTickValue = .1;
  private double yWideTickValue = 1;

  private Map<String, Point2d> pointEntries = new HashMap<String, Point2d>();
  private Map<String, Pair<Point2d, Vector2d>> vectorEntries = new HashMap<String, Pair<Point2d, Vector2d>>();

  public GraphPanel() {
    this.setLayout(new BorderLayout());
    this.add(this.getGraphPanel(), BorderLayout.CENTER);
    JPanel southPanel = new JPanel(new BorderLayout());
    southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
    southPanel.add(this.getCommandPanel());
    southPanel.add(this.getParamPanel());
    this.add(southPanel, BorderLayout.SOUTH);
  }

  public void addPoint(final String name, final Point2d p) {
    this.pointEntries.put(name, new Point2d(p));
    this.centerRange();
    this.repaint();
  }

  public void addPoint(final String name, final double x, final double y) {
    this.pointEntries.put(name, new Point2d(x, y));
    this.centerRange();
    this.repaint();
  }

  public void addVector(final String name, final Point2d p, final Vector2d v) {
    this.vectorEntries.put(name, new Pair<Point2d, Vector2d>(new Point2d(p), new Vector2d(v)));
    this.centerRange();
    this.repaint();
  }

  public void addVector(final String name, final double x1, final double y1, final double x2, final double y2) {
    this.vectorEntries.put(name, new Pair<Point2d, Vector2d>(new Point2d(x1, y1), new Vector2d(x2, y2)));
    this.centerRange();
    this.repaint();
  }

  private JPanel getCommandPanel() {
    if (this.commandPanel == null) {
      this.commandPanel = new JPanel(new BorderLayout());
      this.commandPanel.add(this.getDrawButton(), BorderLayout.EAST);
      this.commandPanel.add(this.getToolPanel(), BorderLayout.CENTER);
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

  private JPanel getToolPanel() {
    if (this.toolPanel == null) {
      this.toolPanel = new JPanel();
    }
    return this.toolPanel;
  }

  private JButton getDrawButton() {
    if (this.drawButton == null) {
      this.drawButton = new JButton(drawIcon);
      //      this.drawButton.setBorder(emptyBorder);
      this.drawButton.setToolTipText("Send changes");
      this.drawButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent e) {
          GraphPanel.this.centerRange();
          GraphPanel.this.repaint();

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
          GraphPanel.this.draw(g2, width, height);
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
            GraphPanel.this.xSmallTickValue = Double.valueOf(GraphPanel.this.xSmallTickField.getText());
            GraphPanel.this.xSmallTickField.setBackground(OkBackgroundColor);
            GraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            GraphPanel.this.xSmallTickField.setBackground(ErrorBackgroundColor);
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
            GraphPanel.this.xWideTickValue = Double.valueOf(GraphPanel.this.xWideTickField.getText());
            GraphPanel.this.xWideTickField.setBackground(OkBackgroundColor);
            GraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            GraphPanel.this.xWideTickField.setBackground(ErrorBackgroundColor);
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
            GraphPanel.this.ySmallTickValue = Double.valueOf(GraphPanel.this.ySmallTickField.getText());
            GraphPanel.this.ySmallTickField.setBackground(OkBackgroundColor);
            GraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            GraphPanel.this.ySmallTickField.setBackground(ErrorBackgroundColor);
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
            GraphPanel.this.yWideTickValue = Double.valueOf(GraphPanel.this.yWideTickField.getText());
            GraphPanel.this.yWideTickField.setBackground(OkBackgroundColor);
            GraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            GraphPanel.this.yWideTickField.setBackground(ErrorBackgroundColor);
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
            GraphPanel.this.xMinValue = Double.valueOf(GraphPanel.this.xMinField.getText());
            GraphPanel.this.xMinField.setBackground(OkBackgroundColor);
            GraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            GraphPanel.this.xMinField.setBackground(ErrorBackgroundColor);
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
            GraphPanel.this.xMaxValue = Double.valueOf(GraphPanel.this.xMaxField.getText());
            GraphPanel.this.xMaxField.setBackground(OkBackgroundColor);
            GraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            GraphPanel.this.xMaxField.setBackground(ErrorBackgroundColor);
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
            GraphPanel.this.yMinValue = Double.valueOf(GraphPanel.this.yMinField.getText());
            GraphPanel.this.yMinField.setBackground(OkBackgroundColor);
            GraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            GraphPanel.this.yMinField.setBackground(ErrorBackgroundColor);
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
            GraphPanel.this.yMaxValue = Double.valueOf(GraphPanel.this.yMaxField.getText());
            GraphPanel.this.yMaxField.setBackground(OkBackgroundColor);
            GraphPanel.this.repaint();
          } catch (NumberFormatException e1) {
            GraphPanel.this.yMaxField.setBackground(ErrorBackgroundColor);
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
          GraphPanel.this.repaint();
        }

      });
      this.editor.setText("//x*x\n//new GaussFunction(1,0,1).evaluate( x )\nsin(x*PI)");
    }
    return this.editor;
  }

  public void displayNotification(final String notification) {
    this.getNotificationLabel().setText(notification);
  }

  private void centerRange() {
    Double xmin = null, ymin = null, xmax = null, ymax = null;

    for (Point2d p : this.pointEntries.values()) {
      if (xmin == null || xmin > p.x) {
        xmin = p.x;
      }
      if (ymin == null || ymin > p.y) {
        ymin = p.y;
      }
      if (xmax == null || xmax < p.x) {
        xmax = p.x;
      }
      if (ymax == null || ymax < p.y) {
        ymax = p.y;
      }
    }
    for (Pair<Point2d, Vector2d> pair : this.vectorEntries.values()) {
      Point2d p = new Point2d(pair.getLeft());
      if (xmin == null || xmin > p.x) {
        xmin = p.x;
      }
      if (ymin == null || ymin > p.y) {
        ymin = p.y;
      }
      if (xmax == null || xmax < p.x) {
        xmax = p.x;
      }
      if (ymax == null || ymax < p.y) {
        ymax = p.y;
      }
      p.x += pair.getRight().x;
      p.y += pair.getRight().y;
      if (xmin == null || xmin > p.x) {
        xmin = p.x;
      }
      if (ymin == null || ymin > p.y) {
        ymin = p.y;
      }
      if (xmax == null || xmax < p.x) {
        xmax = p.x;
      }
      if (ymax == null || ymax < p.y) {
        ymax = p.y;
      }
    }
    double xrange = xmax - xmin;
    double yrange = ymax - ymin;
    double maxrange = xrange > yrange ? xrange : yrange;
    double cx = (xmax + xmin) / 2;
    double cy = (ymax + ymin) / 2;
    double ratio = this.getGraphPanel().getWidth() / this.getGraphPanel().getHeight();
    if (ratio > 1) {
      xmin = cx - maxrange / 2 * 1.05 * ratio;
      ymin = cy - maxrange / 2 * 1.05;
      xmax = cx + maxrange / 2 * 1.05 * ratio;
      ymax = cy + maxrange / 2 * 1.05;
    } else {
      xmin = cx - maxrange / 2 * 1.05 / ratio;
      ymin = cy - maxrange / 2 * 1.05;
      xmax = cx + maxrange / 2 * 1.05 / ratio;
      ymax = cy + maxrange / 2 * 1.05;
    }
    this.getXMinTextField().setText(String.valueOf(xmin));
    this.xMinValue = xmin;
    this.getXMaxTextField().setText(String.valueOf(xmax));
    this.xMaxValue = xmax;
    this.getYMinTextField().setText(String.valueOf(ymin));
    this.yMinValue = ymin;
    this.getYMaxTextField().setText(String.valueOf(ymax));
    this.yMaxValue = ymax;
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
      for (Map.Entry<String, Point2d> pointEntry : this.pointEntries.entrySet()) {
        this.drawPoint(g2, pointEntry.getKey(), pointEntry.getValue());
      }
      for (Map.Entry<String, Pair<Point2d, Vector2d>> vectorEntry : this.vectorEntries.entrySet()) {
        this.drawVector(g2, vectorEntry.getKey(), vectorEntry.getValue().getLeft(), vectorEntry.getValue().getRight());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void drawPoint(final Graphics2D g2, final String name, final Point2d pWorld) {
    int xPixel = (int) this.xWorldToPixel(pWorld.x);
    int yPixel = (int) this.yWorldToPixel(pWorld.y);
    g2.setColor(this.pointColor);
    g2.drawOval(xPixel, yPixel, this.pointSize, this.pointSize);
    g2.drawString(name, xPixel + this.pointSize, yPixel - this.pointSize);
  }

  private void drawVector(final Graphics2D g2, final String name, final Point2d pWorld, final Vector2d vWorld) {
    int x1Pixel = (int) this.xWorldToPixel(pWorld.x);
    int y1Pixel = (int) this.yWorldToPixel(pWorld.y);
    int x2Pixel = (int) this.xWorldToPixel(pWorld.x + vWorld.x);
    int y2Pixel = (int) this.yWorldToPixel(pWorld.y + vWorld.y);
    g2.setColor(this.vectorColor);
    g2.drawLine(x1Pixel, y1Pixel, x2Pixel, y2Pixel);
    g2.drawString(name, x1Pixel + 1, y1Pixel - 1);
    Vector2d nPixel = new Vector2d(y1Pixel - y2Pixel, x2Pixel - x1Pixel);
    nPixel.normalize();
    nPixel.x *= 5;
    nPixel.y *= 5;
    double l = Math.sqrt((y2Pixel - y1Pixel) * (y2Pixel - y1Pixel) + (x2Pixel - x1Pixel) * (x2Pixel - x1Pixel));
    int decX = (int) ((x2Pixel - x1Pixel) / l * 5.);
    int decY = (int) ((y2Pixel - y1Pixel) / l * 5.);

    g2.drawLine((int) (x1Pixel - nPixel.x), (int) (y1Pixel - nPixel.y), (int) (x1Pixel + nPixel.x), (int) (y1Pixel + nPixel.y));
    g2.drawLine(x2Pixel, y2Pixel, (int) (x2Pixel + nPixel.x - decX), (int) (y2Pixel + nPixel.y - decY));
    g2.drawLine(x2Pixel, y2Pixel, (int) (x2Pixel - nPixel.x - decX), (int) (y2Pixel - nPixel.y - decY));

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
