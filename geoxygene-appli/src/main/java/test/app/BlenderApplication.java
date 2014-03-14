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

package test.app;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import fr.ign.util.graphcut.GraphCut;
import fr.ign.util.graphcut.PixelEdge;
import fr.ign.util.graphcut.Tile;

/**
 * @author JeT
 * 
 */
public class BlenderApplication {

    JFrame frame = null;
    private BlenderPanel main = null;
    private BufferedImage image = null;
    private Tile tileToApply = null;
    private final Preferences prefs = Preferences.userRoot();
    private static final String LAST_DIRECTORY = "blender.app.lastDirectory";
    private static final int TH_WIDTH = 50;
    private static final int TH_HEIGHT = 50;
    private static final File defaultTileFilename = new File("/export/home/kandinsky/turbet/cassini samples/waves big.png");
    private BlendingMode mode = BlendingMode.SrcOver;
    boolean drawEdges = true;
    boolean drawMask = true;
    boolean doubleAction = false;
    boolean selfApply = false;
    String displayMode = "vertices";
    final JTextArea textArea = new JTextArea("no message");
    final GraphCut graphCut = new GraphCut();

    public enum BlendingMode {
        SrcOver, Src, SrcIn, SrcOut, SrcAtop, Dst, DstIn, DstOut, DstOver, DstAtop, MaxL, Seam, GraphCut
    };

    public BlenderApplication() {
        this.frame = new JFrame("Blending test");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.main = new BlenderPanel(this);
        this.main.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
                "blank cursor"));
        this.frame.getContentPane().add(this.main, BorderLayout.CENTER);
        JPanel tool = new JPanel();
        this.frame.getContentPane().add(tool, BorderLayout.SOUTH);

        ButtonGroup grp = new ButtonGroup();
        JRadioButton blackButton = new JRadioButton("black");
        blackButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BlenderApplication.this.main.setBg1(Color.black);
                BlenderApplication.this.main.setBg2(Color.black);
                BlenderApplication.this.main.repaint();

            }
        });
        grp.add(blackButton);
        tool.add(blackButton);
        JRadioButton grayButton = new JRadioButton("gray");
        grayButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BlenderApplication.this.main.setBg1(Color.gray);
                BlenderApplication.this.main.setBg2(Color.gray);
                BlenderApplication.this.main.repaint();

            }
        });
        grp.add(grayButton);
        tool.add(grayButton);
        JRadioButton checkerButton = new JRadioButton("checker");
        checkerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BlenderApplication.this.main.setBg1(Color.white);
                BlenderApplication.this.main.setBg2(Color.gray);
                BlenderApplication.this.main.repaint();

            }
        });
        grp.add(checkerButton);
        tool.add(checkerButton);
        JRadioButton whiteButton = new JRadioButton("white");
        whiteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BlenderApplication.this.main.setBg1(Color.white);
                BlenderApplication.this.main.setBg2(Color.white);
                BlenderApplication.this.main.repaint();

            }
        });
        grp.add(whiteButton);
        tool.add(whiteButton);
        BlenderApplication.this.main.setBg1(Color.white);
        BlenderApplication.this.main.setBg2(Color.white);
        whiteButton.setSelected(true);

        JButton refresh = new JButton("refresh");
        tool.add(refresh);
        refresh.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BlenderApplication.this.refresh();
            }
        });

        JButton fill = new JButton("fill");
        tool.add(fill);
        fill.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (BlenderApplication.this.getTile() == null) {
                    return;
                }
                Graphics2D g2 = BlenderApplication.this.getImage().createGraphics();
                g2.setComposite(AlphaComposite.Src);
                for (int y = 0; y < BlenderApplication.this.getImage().getHeight(); y += BlenderApplication.this.getTile().getHeight()) {
                    for (int x = 0; x < BlenderApplication.this.getImage().getWidth(); x += BlenderApplication.this.getTile().getWidth()) {
                        g2.drawImage(BlenderApplication.this.getTile().getImage(), null, x, y);
                    }
                }
                BlenderApplication.this.frame.repaint();
            }
        });

        JButton fillWhite = new JButton("fill white");
        tool.add(fillWhite);
        fillWhite.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (BlenderApplication.this.getTile() == null) {
                    return;
                }
                Graphics2D g2 = BlenderApplication.this.getImage().createGraphics();
                g2.setColor(Color.white);
                g2.fillRect(0, 0, BlenderApplication.this.getImage().getWidth(), BlenderApplication.this.getImage().getHeight());
                BlenderApplication.this.frame.repaint();
            }
        });

        JButton fillBlack = new JButton("fill black");
        tool.add(fillBlack);
        fillBlack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (BlenderApplication.this.getTile() == null) {
                    return;
                }
                Graphics2D g2 = BlenderApplication.this.getImage().createGraphics();
                g2.setColor(Color.black);
                g2.fillRect(0, 0, BlenderApplication.this.getImage().getWidth(), BlenderApplication.this.getImage().getHeight());
                BlenderApplication.this.frame.repaint();
            }
        });

        final JLabel preview = new JLabel();
        preview.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JButton reset = new JButton("reset");
        tool.add(reset);
        tool.add(preview);
        reset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BlenderApplication.this.initializeImage();
                BlenderApplication.this.frame.repaint();
            }
        });

        JComboBox modeComboBox = new JComboBox();
        for (BlendingMode mode : BlendingMode.values()) {
            modeComboBox.addItem(mode);
        }
        modeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    BlenderApplication.this.mode = (BlendingMode) e.getItem();
                }

            }
        });
        this.mode = BlendingMode.GraphCut;
        modeComboBox.setSelectedItem(this.getMode());
        tool.add(modeComboBox);
        JButton load = new JButton("load");
        tool.add(load);
        tool.add(preview);
        load.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(BlenderApplication.this.prefs.get(LAST_DIRECTORY, "."));
                if (fc.showOpenDialog(BlenderApplication.this.frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selectedFile = fc.getSelectedFile();
                        BlenderApplication.this.tileToApply = new Tile(BlenderApplication.this.readImageABGR(selectedFile));
                        BlenderApplication.this.prefs.put(LAST_DIRECTORY, selectedFile.getAbsolutePath());
                        preview.setIcon(new ImageIcon(BlenderApplication.this.tileToApply.getImage().getScaledInstance(TH_WIDTH, TH_HEIGHT, Image.SCALE_SMOOTH)));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        });

        JRadioButton drawEdgeButton = new JRadioButton("edges");
        drawEdgeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BlenderApplication.this.drawEdges = !BlenderApplication.this.drawEdges;
                BlenderApplication.this.main.repaint();
            }
        });
        drawEdgeButton.setSelected(this.drawEdges);
        tool.add(drawEdgeButton);

        JRadioButton drawMaskButton = new JRadioButton("mask");
        drawMaskButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BlenderApplication.this.drawMask = !BlenderApplication.this.drawMask;
                BlenderApplication.this.main.repaint();
            }
        });
        drawMaskButton.setSelected(this.drawMask);
        tool.add(drawMaskButton);

        JRadioButton doubleButton = new JRadioButton("double");
        doubleButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BlenderApplication.this.doubleAction = !BlenderApplication.this.doubleAction;
            }
        });
        doubleButton.setSelected(this.doubleAction);
        tool.add(doubleButton);
        JRadioButton autoButton = new JRadioButton("self");
        autoButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BlenderApplication.this.selfApply = !BlenderApplication.this.selfApply;
            }
        });
        autoButton.setSelected(this.selfApply);
        tool.add(autoButton);
        tool.add(this.textArea);
        try {
            BlenderApplication.this.tileToApply = new Tile(this.readImageABGR(defaultTileFilename));
            preview.setIcon(new ImageIcon(BlenderApplication.this.tileToApply.getImage().getScaledInstance(TH_WIDTH, TH_HEIGHT, Image.SCALE_SMOOTH)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        this.initializeImage();
        this.frame.pack();
        this.frame.setSize(1500, 1100);
        this.frame.setLocation(10, 10);
    }

    public void message(String s) {
        this.textArea.setText(s);
    }

    /**
     * @param selectedFile
     * @throws IOException
     */
    private BufferedImage readImageABGR(File selectedFile) throws IOException {
        BufferedImage src = ImageIO.read(selectedFile);
        BufferedImage convertedImage = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = convertedImage.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();
        return convertedImage;
    }

    /**
     * @return the mode
     */
    public BlendingMode getMode() {
        return this.mode;
    }

    /**
     * @return the graphcut mask
     */
    public BufferedImage getMask() {
        return this.graphCut.getMask();
    }

    /**
     * 
     */
    private void initializeImage() {
        // generate image
        this.image = new BufferedImage(1600, 1200, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = this.image.createGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, this.image.getWidth(), this.image.getHeight());
        this.graphCut.setImage(this.image);
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * @return the tileToApply
     */
    public Tile getTile() {
        return this.tileToApply;
    }

    private void run() {
        this.frame.setVisible(true);

    }

    private void refresh() {
        this.main.repaint();
    }

    /**
     * 
     */
    public static void main(String[] args) {
        BlenderApplication app = new BlenderApplication();
        app.run();

        //        testMinCut();
    }

    //    /**
    //     * 
    //     */
    //    private static void testMinCut() {
    //        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge>(
    //                DefaultWeightedEdge.class);
    //        Integer v1 = 1;
    //        Integer v2 = 2;
    //        Integer v3 = 3;
    //        Integer v4 = 4;
    //        Integer v5 = 5;
    //        Integer v6 = 6;
    //        Integer v7 = 7;
    //
    //        graph.addVertex(v1);
    //        graph.addVertex(v2);
    //        graph.addVertex(v3);
    //        graph.addVertex(v4);
    //        graph.addVertex(v5);
    //        graph.addVertex(v6);
    //        graph.addVertex(v7);
    //
    //        DefaultWeightedEdge A = graph.addEdge(1, 2);
    //        DefaultWeightedEdge B = graph.addEdge(1, 4);
    //        DefaultWeightedEdge C = graph.addEdge(1, 3);
    //        DefaultWeightedEdge D = graph.addEdge(2, 5);
    //        DefaultWeightedEdge E = graph.addEdge(2, 4);
    //        DefaultWeightedEdge F = graph.addEdge(3, 4);
    //        DefaultWeightedEdge G = graph.addEdge(3, 6);
    //        DefaultWeightedEdge H = graph.addEdge(4, 5);
    //        DefaultWeightedEdge I = graph.addEdge(4, 7);
    //        DefaultWeightedEdge J = graph.addEdge(5, 7);
    //        DefaultWeightedEdge K = graph.addEdge(6, 7);
    //
    //        graph.setEdgeWeight(A, 7);
    //        graph.setEdgeWeight(B, 5);
    //        graph.setEdgeWeight(C, 6);
    //        graph.setEdgeWeight(D, 2);
    //        graph.setEdgeWeight(E, 1);
    //        graph.setEdgeWeight(F, 3);
    //        graph.setEdgeWeight(G, 9);
    //        graph.setEdgeWeight(H, 5);
    //        graph.setEdgeWeight(I, 3);
    //        graph.setEdgeWeight(J, 6);
    //        graph.setEdgeWeight(K, 8);
    //
    //        EdmondsKarpMaximumFlow<Integer, DefaultWeightedEdge> algoMaxFlow = new EdmondsKarpMaximumFlow<Integer, DefaultWeightedEdge>(graph);
    //        algoMaxFlow.calculateMaximumFlow(v1, v7);
    //        for (Map.Entry<DefaultWeightedEdge, Double> entry : algoMaxFlow.getMaximumFlow().entrySet()) {
    //            if (entry.getKey() == A) {
    //                System.err.println("A : " + entry.getValue());
    //            } else if (entry.getKey() == B) {
    //                System.err.println("B : " + entry.getValue());
    //            } else if (entry.getKey() == C) {
    //                System.err.println("C : " + entry.getValue());
    //            } else if (entry.getKey() == D) {
    //                System.err.println("D : " + entry.getValue());
    //            } else if (entry.getKey() == E) {
    //                System.err.println("E : " + entry.getValue());
    //            } else if (entry.getKey() == F) {
    //                System.err.println("F : " + entry.getValue());
    //            } else if (entry.getKey() == G) {
    //                System.err.println("G : " + entry.getValue());
    //            } else if (entry.getKey() == H) {
    //                System.err.println("H : " + entry.getValue());
    //            } else if (entry.getKey() == I) {
    //                System.err.println("I : " + entry.getValue());
    //            } else if (entry.getKey() == J) {
    //                System.err.println("J : " + entry.getValue());
    //            } else if (entry.getKey() == K) {
    //                System.err.println("K : " + entry.getValue());
    //            } else {
    //                System.err.println("??????");
    //            }
    //        }
    //        System.err.println("maximum flow size = " + algoMaxFlow.getMaximumFlowValue());
    //
    //        MinSourceSinkCut<Integer, DefaultWeightedEdge> algoMinCut = new MinSourceSinkCut<Integer, DefaultWeightedEdge>(graph);
    //        algoMinCut.computeMinCut(v1, v7);
    //        System.err.print("cut edge : ");
    //        for (DefaultWeightedEdge edge : algoMinCut.getCutEdges()) {
    //            if (edge == A) {
    //                System.err.print("A");
    //            } else if (edge == B) {
    //                System.err.print("B");
    //            } else if (edge == C) {
    //                System.err.print("C");
    //            } else if (edge == D) {
    //                System.err.print("D");
    //            } else if (edge == E) {
    //                System.err.print("E");
    //            } else if (edge == F) {
    //                System.err.print("F");
    //            } else if (edge == G) {
    //                System.err.print("G");
    //            } else if (edge == H) {
    //                System.err.print("H");
    //            } else if (edge == I) {
    //                System.err.print("I");
    //            } else if (edge == J) {
    //                System.err.print("J");
    //            } else if (edge == K) {
    //                System.err.print("K");
    //            } else {
    //                System.err.print("?");
    //            }
    //        }
    //        System.err.println("");
    //        System.err.print("Sink partition : ");
    //        for (Integer i : algoMinCut.getSinkPartition()) {
    //            System.err.print(i);
    //        }
    //        System.err.println("");
    //        System.err.print("Source partition : ");
    //        for (Integer i : algoMinCut.getSourcePartition()) {
    //            System.err.print(i);
    //        }
    //    }

}

class BlenderPanel extends JPanel implements MouseMotionListener, MouseWheelListener, MouseListener {

    private static final long serialVersionUID = 1L;
    private static final double rotationPrecision = 20;
    private BlenderApplication app = null;
    private double mouseX, mouseY; // mouse position
    private double rotation = 0;
    private AffineTransform at = new AffineTransform();
    private Color bg1 = Color.white;
    private Color bg2 = Color.gray;
    private Set<PixelEdge> edges = null;
    private AffineTransform edgesAt = null;

    /**
     * @param app
     */
    public BlenderPanel(BlenderApplication app) {
        super();
        this.app = app;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }

    /**
     * @return the bg1
     */
    public Color getBg1() {
        return this.bg1;
    }

    /**
     * @param bg1
     *            the bg1 to set
     */
    public void setBg1(Color bg1) {
        this.bg1 = bg1;
    }

    /**
     * @return the bg2
     */
    public Color getBg2() {
        return this.bg2;
    }

    /**
     * @param bg2
     *            the bg2 to set
     */
    public void setBg2(Color bg2) {
        this.bg2 = bg2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        this.drawBackground(g2);

        if (this.app.drawMask) {
            g2.drawImage(this.app.getMask(), null, 0, 0);
        } else {
            g2.drawImage(this.app.getImage(), null, 0, 0);
        }
        g2.setColor(Color.red);
        g2.setTransform(this.at);

        BufferedImage tile = this.app.getTile().getImage();
        if (tile != null) {
            g2.setComposite(AlphaComposite.SrcOver.derive(0.5f));
            g2.drawImage(tile, null, 0, 0);
            g2.setComposite(AlphaComposite.SrcOver);

            //            g2.drawRect(0, 0, tile.getWidth(), tile.getHeight());
        } else {
            g2.drawString("load a tile", 0, 0);
        }

        if (this.edges != null && this.app.drawEdges) {
            g2.setColor(Color.yellow);

            g2.setTransform(this.edgesAt);
            for (PixelEdge e : this.edges) {
                if (e.getSource() == null) {
                    if (e.getTarget() == null) {
                        System.err.println("edge has null source & target");
                    } else {
                        System.err.println("edge has null source");
                    }
                } else if (e.getTarget() == null) {
                    System.err.println("edge has null target");
                } else {
                    g2.drawLine(e.getSource().getX(), e.getSource().getY(), e.getTarget().getX(), e.getTarget().getY());
                }
            }
        }
        if (this.app.selfApply) {
            g2.drawString("SELF is checked !! A tile will be drawn before merge !!", -100, -20);
        }
    }

    /**
     * @param g2
     */
    private void drawBackground(Graphics2D g2) {
        int squareSize = 10;
        for (int y = 0; y <= this.getHeight(); y += squareSize) {
            for (int x = 0; x <= this.getWidth(); x += squareSize) {
                g2.setColor(((x / squareSize) % 2) == ((y / squareSize) % 2) ? this.getBg1() : this.getBg2());
                g2.fillRect(x, y, squareSize, squareSize);
            }
        }
    }

    private void merge() {
        switch (this.app.getMode()) {
        case SrcOver:
            this.mergeSrcOver();
            break;
        case Dst:
            this.mergeDst();
            break;
        case Src:
            this.mergeSrc();
            break;
        case SrcIn:
            this.mergeSrcIn();
            break;
        case SrcOut:
            this.mergeSrcOut();
            break;
        case DstIn:
            this.mergeDstIn();
            break;
        case DstOut:
            this.mergeDstOut();
            break;
        case DstOver:
            this.mergeDstOver();
            break;
        case SrcAtop:
            this.mergeSrcAtop();
            break;
        case DstAtop:
            this.mergeDstAtop();
            break;
        case MaxL:
            this.mergeMaxL();
            break;
        case Seam:
            this.mergeSeam();
            break;
        case GraphCut:
            this.mergeGraphCut();
            break;
        default:
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Unknown selection mode " + this.app.getMode());
        }
        this.repaint();
    }

    private void mergeGraphCut() {

        System.err.println("Start Graph Cut");
        this.app.graphCut.pasteTile(this.app.getTile(), this.at);
        System.err.println("Graph Cut Ended");
        this.setEdges(this.at, this.app.graphCut.getLastCutEdges());

        //        JDialog dialog = new JDialog(this.app.frame);
        //        //        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        //        final JPanel panel = new JPanel(new BorderLayout());
        //        final GraphCutPanel graphCutPanel = new GraphCutPanel(this.app, graph, algoMinCut);
        //        panel.add(graphCutPanel, BorderLayout.CENTER);
        //        JPanel toolPanel = new JPanel();
        //        JComboBox<String> displayComboBox = new JComboBox<String>();
        //        displayComboBox.addItem("vertices");
        //        displayComboBox.addItem("diff");
        //        displayComboBox.addItem("diff incoming");
        //        displayComboBox.addItem("diff outgoing");
        //        displayComboBox.addItem("tile");
        //        displayComboBox.addItem("flow");
        //        displayComboBox.addItemListener(new ItemListener() {
        //
        //            @Override
        //            public void itemStateChanged(ItemEvent e) {
        //                BlenderPanel.this.app.displayMode = (String) e.getItem();
        //                graphCutPanel.update();
        //                graphCutPanel.repaint();
        //            }
        //        });
        //        toolPanel.add(displayComboBox);
        //        panel.add(toolPanel, BorderLayout.SOUTH);
        //        dialog.setContentPane(panel);
        //        dialog.pack();
        //        dialog.setVisible(true);
    }

    /**
     * @param w
     * @param h
     * @param maskPixels
     * @param xImage
     * @param yImage
     * @param lMask
     * @return
     */

    private void mergeMaxL() {

        int w = this.app.getTile().getWidth();
        int h = this.app.getTile().getHeight();

        BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D gTile = src.createGraphics();
        gTile.setComposite(AlphaComposite.Clear);
        gTile.fillRect(0, 0, w, h);

        byte[] srcPixels = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
        byte[] tilePixels = ((DataBufferByte) this.app.getTile().getImage().getRaster().getDataBuffer()).getData();
        byte[] imagePixels = ((DataBufferByte) this.app.getImage().getRaster().getDataBuffer()).getData();
        int nbTile = 0, nbImage = 0;
        for (int yTile = 0; yTile < h; yTile++) {
            for (int xTile = 0; xTile < w; xTile++) {
                Point2D pixel = new Point2D.Double(xTile, yTile);
                Point2D transformedPixel = new Point2D.Double();
                this.at.transform(pixel, transformedPixel);
                int xImage = (int) transformedPixel.getX();
                int yImage = (int) transformedPixel.getY();
                if (xImage < 0 || xImage >= this.app.getImage().getWidth() || yImage < 0 || yImage >= this.app.getImage().getHeight()) {
                    continue;
                }
                int lTile = (xTile + yTile * w) * 4;
                int lImage = (xImage + yImage * this.app.getImage().getWidth()) * 4;
                int tileA = tilePixels[lTile] & 0xFF;
                int tileB = tilePixels[lTile + 1] & 0xFF;
                int tileG = tilePixels[lTile + 2] & 0xFF;
                int tileR = tilePixels[lTile + 3] & 0xFF;
                int imageA = imagePixels[lImage] & 0xFF;
                int imageB = imagePixels[lImage + 1] & 0xFF;
                int imageG = imagePixels[lImage + 2] & 0xFF;
                int imageR = imagePixels[lImage + 3] & 0xFF;
                double luminanceTile = (tileR + tileG + tileB) * (255 - tileA);
                double luminanceImage = (imageR + imageG + imageB) * (255 - imageA);
                if (luminanceTile >= luminanceImage) {
                    srcPixels[lTile] = (byte) tileA;
                    srcPixels[lTile + 1] = (byte) tileB;
                    srcPixels[lTile + 2] = (byte) tileG;
                    srcPixels[lTile + 3] = (byte) tileR;
                    nbTile++;
                } else {
                    srcPixels[lTile] = (byte) imageA;
                    srcPixels[lTile + 1] = (byte) imageB;
                    srcPixels[lTile + 2] = (byte) imageG;
                    srcPixels[lTile + 3] = (byte) imageR;
                    nbImage++;
                }
            }
        }
        System.err.println("tile = " + nbTile + " bg = " + nbImage);

        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.SrcOver);
        g2.drawImage(src, null, 0, 0);

    }

    private void setEdges(AffineTransform at, Set<PixelEdge> cutEdges) {
        this.edgesAt = new AffineTransform(at);
        this.edges = cutEdges;
    }

    private void mergeSeam() {

        int w = this.app.getImage().getWidth();
        int h = this.app.getImage().getHeight();
        double[] energyMap = new double[w * h];
        BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = src.createGraphics();

        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, w, h);
        //
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.SrcOver);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);

        byte[] srcPixels = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
        byte[] dstPixels = ((DataBufferByte) this.app.getImage().getRaster().getDataBuffer()).getData();
        int xMin = 0, yMin = 0;
        double lMin = Double.POSITIVE_INFINITY;
        double lMax = Double.NEGATIVE_INFINITY;
        int nbSrc = 0, nbDst = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int l = x + y * w;
                int lb = l * 4;
                byte srcA = srcPixels[lb];
                byte srcB = srcPixels[lb + 1];
                byte srcG = srcPixels[lb + 2];
                byte srcR = srcPixels[lb + 3];
                byte dstA = dstPixels[lb];
                byte dstB = dstPixels[lb + 1];
                byte dstG = dstPixels[lb + 2];
                byte dstR = dstPixels[lb + 3];
                double lSrc = srcA == 0 ? Double.POSITIVE_INFINITY : (srcR + srcG + srcB);
                double lDst = dstA == 0 ? Double.POSITIVE_INFINITY : (dstR + dstG + dstB);
                //                System.err.println("lsrc = " + lSrc + " ldst = " + lDst);
                if (srcA != 0) {
                    energyMap[l] = Math.abs(srcR - dstR) + Math.abs(srcG - dstG) + Math.abs(srcB - dstB);
                }
                if (energyMap[l] < lMin) {
                    lMin = energyMap[l];
                    xMin = x;
                    yMin = y;
                }
                if (energyMap[l] != Double.POSITIVE_INFINITY && energyMap[l] > lMax) {
                    lMax = energyMap[l];
                }
            }
        }
        System.err.println("lMin = " + lMin + " lMax = " + lMax);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int l = x + y * w;
                int lb = l * 4;

                double v = (energyMap[l] - lMin) / (lMax - lMin);
                byte value = (byte) (v * 255);
                srcPixels[lb] = (byte) 255;
                srcPixels[lb + 1] = value;
                srcPixels[lb + 2] = value;
                srcPixels[lb + 3] = value;
            }
        }

        int l = xMin + yMin * w;
        int lb = l * 4;

        srcPixels[lb] = (byte) 255;
        srcPixels[lb + 1] = 0;
        srcPixels[lb + 2] = 0;
        srcPixels[lb + 3] = (byte) 255;

        System.err.println("src = " + nbSrc + " dst = " + nbDst);
        g2 = this.app.getImage().createGraphics();
        g2.setTransform(new AffineTransform());
        g2.setComposite(AlphaComposite.SrcOver);
        g2.drawImage(src, null, 0, 0);
    }

    private void mergeSrcOver() {
        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.SrcOver);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);
    }

    private void mergeDst() {
        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.Dst);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);
    }

    private void mergeSrc() {
        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.Src);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);
    }

    private void mergeSrcIn() {
        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);
    }

    private void mergeSrcOut() {
        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.SrcOut);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);
    }

    private void mergeDstAtop() {
        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.DstAtop);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);
    }

    private void mergeSrcAtop() {
        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);
    }

    private void mergeDstOver() {
        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.DstOver);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);
    }

    private void mergeDstIn() {
        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.DstIn);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);
    }

    private void mergeDstOut() {
        Graphics2D g2 = this.app.getImage().createGraphics();
        g2.setTransform(this.at);
        g2.setComposite(AlphaComposite.DstOut);
        g2.drawImage(this.app.getTile().getImage(), null, 0, 0);
    }

    private void computeAt() {
        this.at = new AffineTransform();
        this.at.translate(this.mouseX, this.mouseY);
        this.at.rotate(this.rotation);

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.mouseX = e.getX();
        this.mouseY = e.getY();
        this.computeAt();
        this.repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.rotation += e.getWheelRotation() / rotationPrecision;
        this.computeAt();
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (this.app.selfApply) {
            this.mergeSrc();
        }
        this.merge();
        if (this.app.doubleAction) {
            this.merge();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
