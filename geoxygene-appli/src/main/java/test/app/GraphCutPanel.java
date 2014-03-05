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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import fr.ign.util.graphcut.MinSourceSinkCut;
import fr.ign.util.graphcut.PixelEdge;
import fr.ign.util.graphcut.PixelVertex;

/**
 * @author JeT
 * 
 */
public class GraphCutPanel extends JPanel implements MouseListener, MouseWheelListener, MouseMotionListener {
    private DefaultDirectedWeightedGraph<PixelVertex, PixelEdge> graph = null;
    private MinSourceSinkCut<PixelVertex, PixelEdge> algo = null;
    private final DecimalFormat df = new DecimalFormat("#.##");
    private double zoom = 50.;
    private final Point tr = new Point(0, 0);
    private final Point clickedTr = new Point(0, 0);
    private boolean mouseClicked = false;
    private boolean allEdges = false;
    private Point mousePos;
    private PixelVertex selectedVertex = null;
    private BlenderApplication app = null;
    private double min = 0, max = 0;
    private BufferedImage diff = null;

    /**
     * Constructor
     * 
     * @param graph
     * @param algoMinCut
     */
    public GraphCutPanel(BlenderApplication app, DefaultDirectedWeightedGraph<PixelVertex, PixelEdge> graph, MinSourceSinkCut<PixelVertex, PixelEdge> algoMinCut) {
        this.graph = graph;
        this.algo = algoMinCut;
        this.app = app;
        this.setPreferredSize(new Dimension(1000, 1000));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.min = Double.POSITIVE_INFINITY;
        this.max = Double.NEGATIVE_INFINITY;
        for (PixelEdge e : this.graph.edgeSet()) {
            double weight = this.graph.getEdgeWeight(e);
            if (weight == Double.POSITIVE_INFINITY) {
                continue;
            }
            if (weight < this.min) {
                this.min = weight;
            }
            if (weight > this.max) {
                this.max = weight;
            }
        }

    }

    public void update() {
        this.diff = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (this.app.displayMode.equals("vertices")) {
            this.displayVertices(g2);
        }
        if (this.app.displayMode.equals("diff")) {
            this.displayDifferences(g2);
        }
        if (this.app.displayMode.equals("diff incoming")) {
            this.displayIncomingDifferences(g2);
        }
        if (this.app.displayMode.equals("diff outgoing")) {
            this.displayOutgoingDifferences(g2);
        }
        if (this.app.displayMode.equals("tile")) {
            this.displayTile(g2);
        }
        if (this.app.displayMode.equals("flow")) {
            this.displayFlow(g2);
        }
        if (this.selectedVertex != null) {
            PixelVertex v = this.selectedVertex;
            g2.setColor(Color.black);
            g2.setTransform(new AffineTransform());
            g2.drawString("selected Vertex : " + v, 10, 20);
            g2.drawString("min & max error: " + this.min + " / " + this.max, 10, 40);
            Set<PixelEdge> inEdges = this.getInEdges(this.selectedVertex);
            Set<PixelEdge> outEdges = this.getOutEdges(this.selectedVertex);

            g2.drawString(inEdges.size() + " incoming edges " + outEdges.size() + " out edges", 10, 60);
            int posY = 80;
            for (PixelEdge e : inEdges) {
                double weight = this.graph.getEdgeWeight(e);
                g2.drawString("IN from " + e.getSource().getX() + "x" + e.getSource().getY() + " : " + weight + " / " + this.algo.ekMaxFlow.getFlow(e) + " "
                        + (this.algo.getCutEdges().contains(e) ? "is a cut" : ""), 10, posY);
                posY += 20;
            }
            for (PixelEdge e : outEdges) {
                double weight = this.graph.getEdgeWeight(e);
                g2.drawString("OUT to " + e.getTarget().getX() + "x" + e.getTarget().getY() + " : " + weight + " / " + this.algo.ekMaxFlow.getFlow(e) + " "
                        + (this.algo.getCutEdges().contains(e) ? "is a cut" : ""), 10, posY);
                posY += 20;
            }
        }
    }

    /**
     * @param g2
     */
    private void displayDifferences(Graphics2D g2) {
        if (this.diff == null) {
            this.diff = new BufferedImage(this.app.getTile().getWidth(), this.app.getTile().getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            //            Graphics2D g2Tile = this.diff.createGraphics();
            for (PixelEdge e : this.graph.edgeSet()) {
                double weight = this.graph.getEdgeWeight(e);

                float c = (float) ((weight - this.min) / (this.max - this.min));
                if (c > 1) {
                    c = 1.f;
                }
                if (e.getSource().getX() > 0 && e.getSource().getX() < this.app.getTile().getWidth() && e.getSource().getY() > 0
                        && e.getSource().getY() < this.app.getTile().getHeight()) {
                    this.diff.setRGB(e.getSource().getX(), e.getSource().getY(), new Color(c, c, c).getRGB());
                }
            }
        }
        g2.setColor(Color.yellow);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        AffineTransform transform = new AffineTransform();
        transform.translate(-this.tr.x, -this.tr.y);
        transform.scale(this.zoom, this.zoom);
        g2.setTransform(transform);
        g2.drawImage(this.diff, null, 0, 0);
    }

    /**
     * @param g2
     */
    private void displayIncomingDifferences(Graphics2D g2) {
        if (this.diff == null) {
            this.diff = new BufferedImage(this.app.getTile().getWidth(), this.app.getTile().getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            //            Graphics2D g2Tile = this.diff.createGraphics();
            for (PixelVertex v : this.graph.vertexSet()) {
                this.setPixelRGBValue(v);
            }
        }
        g2.setColor(Color.yellow);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        AffineTransform transform = new AffineTransform();
        transform.translate(-this.tr.x, -this.tr.y);
        transform.scale(this.zoom, this.zoom);
        g2.setTransform(transform);
        g2.drawImage(this.diff, null, 0, 0);
        this.displayVertexInformation(g2);
    }

    /**
     * @param g2
     */
    private void displayOutgoingDifferences(Graphics2D g2) {
        if (this.diff == null) {
            this.diff = new BufferedImage(this.app.getTile().getWidth(), this.app.getTile().getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            //            Graphics2D g2Tile = this.diff.createGraphics();
            for (PixelVertex v : this.graph.vertexSet()) {
                this.setPixelRGBValue(v);
            }
        }
        g2.setColor(Color.yellow);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        AffineTransform transform = new AffineTransform();
        transform.translate(-this.tr.x, -this.tr.y);
        transform.scale(this.zoom, this.zoom);
        g2.setTransform(transform);
        g2.drawImage(this.diff, null, 0, 0);
        this.displayVertexInformation(g2);
    }

    /**
     * @param v
     */
    private void setPixelRGBValue(PixelVertex v) {
        int nb = 0;
        double weight = 0.;
        boolean inf = false;
        for (PixelEdge e : this.getOutEdges(v)) {
            if (this.graph.getEdgeWeight(e) == Double.POSITIVE_INFINITY) {
                inf = true;
            } else {
                weight += this.graph.getEdgeWeight(e);
                nb++;

            }

        }
        if (nb > 0) {
            weight /= nb;
        }
        float c = (float) ((weight - this.min) / (this.max - this.min));
        if (c > 1) {
            c = 1.f;
        }
        if (v.getX() > 0 && v.getX() < this.app.getTile().getWidth() && v.getY() > 0 && v.getY() < this.app.getTile().getHeight()) {
            this.diff.setRGB(v.getX(), v.getY(), new Color(inf ? c : 0, inf ? c : 0, c).getRGB());
        }
    }

    /**
     * @param g2
     */
    private void displayTile(Graphics2D g2) {
        g2.setColor(Color.green);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        for (PixelVertex v : this.graph.vertexSet()) {
            try {
                g2.setColor(new Color(this.app.getTile().getRGB(v.getX(), v.getY())));
            } catch (Exception e) {
            }
            g2.fillRect(this.x(v.getX()), this.y(v.getY()), Math.max(3, (int) this.zoom - 1), Math.max(3, (int) this.zoom - 1));

        }
        this.displayVertexInformation(g2);

    }

    /**
     * @param g2
     */
    private void displayFlow(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        if (this.diff == null) {
            this.diff = new BufferedImage(this.app.getTile().getWidth(), this.app.getTile().getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            //            Graphics2D g2Tile = this.diff.createGraphics();
            for (PixelVertex v : this.graph.vertexSet()) {
                this.setPixelRGBValue(v);
            }
        }
        g2.setColor(Color.yellow);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2.setColor(Color.red);
        g2.drawString("draw " + (this.allEdges ? "forward" : "reverse") + " edges", this.getWidth() / 2, 20);
        AffineTransform transform = new AffineTransform();
        transform.translate(-this.tr.x, -this.tr.y);
        transform.scale(this.zoom, this.zoom);
        g2.setTransform(transform);
        g2.drawImage(this.diff, null, 0, 0);

        //        for (PixelVertex v : this.graph.vertexSet()) {
        //            g2.setColor(this.vertexColor(v));
        //            this.displayVertex(g2, v);
        //        }
        double max = 0;
        g2.setStroke(new BasicStroke(1));
        for (PixelEdge e : this.graph.edgeSet()) {
            if (e.getSource() != this.algo.getCurrentSink() && e.getSource() != this.algo.getCurrentSource() && e.getTarget() != this.algo.getCurrentSource()
                    && e.getTarget() != this.algo.getCurrentSink()) {
                if (this.allEdges == this.isForward(e)) {
                    continue;
                }
                double flow = this.algo.ekMaxFlow.getFlow(e);
                if (flow > max) {
                    max = flow;
                }

            }
        }
        g2.setStroke(new BasicStroke(1));
        g2.setTransform(new AffineTransform());
        for (PixelEdge e : this.graph.edgeSet()) {
            if (e.getSource() != this.algo.getCurrentSink() && e.getSource() != this.algo.getCurrentSource() && e.getTarget() != this.algo.getCurrentSource()
                    && e.getTarget() != this.algo.getCurrentSink()) {
                if (this.allEdges == this.isForward(e)) {
                    continue;
                }
                double flow = this.algo.ekMaxFlow.getFlow(e);
                float c = (float) (flow > max ? 1f : flow / max);
                g2.setColor(new Color(c, c, c));
                this.displayEdge(g2, e, this.isForward(e) ? 1 : -1, this.isForward(e) ? 1 : -1);

            }
        }
        this.displayVertexInformation(g2);
    }

    /**
     * @param g2
     */
    private void displayVertices(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        for (PixelVertex v : this.graph.vertexSet()) {
            g2.setColor(this.vertexColor(v));
            this.displayVertex(g2, v);
        }
        if (this.allEdges) {
            g2.setStroke(new BasicStroke(1));
            for (PixelEdge e : this.graph.edgeSet()) {
                if (e.getSource() != this.algo.getCurrentSink() && e.getSource() != this.algo.getCurrentSource()
                        && e.getTarget() != this.algo.getCurrentSource() && e.getTarget() != this.algo.getCurrentSink()) {

                    //                    if (this.algo.getCutEdges().contains(e) && e.getSource() != this.algo.getCurrentSink() && e.getSource() != this.algo.getCurrentSource()
                    //                            && e.getTarget() != this.algo.getCurrentSource() && e.getTarget() != this.algo.getCurrentSink()) {
                    g2.setColor(this.edgeColor(e));
                    this.displayEdge(g2, e, this.isForward(e) ? 1 : -1, this.isForward(e) ? 1 : -1);
                    //                    }

                }
            }
        }
        this.displayVertexInformation(g2);
    }

    boolean isForward(PixelEdge e) {
        return (e.getSource().getX() == e.getTarget().getX() && e.getSource().getY() > e.getTarget().getY())
                || (e.getSource().getY() == e.getTarget().getY() && e.getSource().getX() > e.getTarget().getX());
    }

    private Color edgeColor(PixelEdge e) {
        float r = 0, g = 0, b = 0;
        if (this.isForward(e)) {
            r = 1f;
        } else {
            b = 1f;
        }
        if (this.algo.getCutEdges().contains(e)) {
            g = 1f;
        }
        return new Color(r, g, b);
    }

    /**
     * @param v
     * @return
     */
    private Color vertexColor(PixelVertex v) {
        Set<PixelVertex> sinkPartition = this.algo.getSinkPartition();
        Set<PixelVertex> sourcePartition = this.algo.getSourcePartition();
        float r = 0, g = 0, b = 0;
        if (sinkPartition.contains(v)) {
            r = 1f;
        }
        if (sourcePartition.contains(v)) {
            g = 1f;
        }
        return new Color(r, g, b);
    }

    /**
     * @param g2
     */
    private void displayVertexInformation(Graphics2D g2) {
        g2.setStroke(new BasicStroke(1));
        g2.setTransform(new AffineTransform());

        if (this.selectedVertex != null) {
            PixelVertex v = this.selectedVertex;

            g2.setColor(Color.blue);
            g2.fillRect(this.x(this.selectedVertex.getX()), this.y(this.selectedVertex.getY()), (int) this.zoom, (int) this.zoom);
            g2.setColor(Color.black);
            for (PixelEdge inEdge : this.getInEdges(this.selectedVertex)) {
                this.displayEdge(g2, inEdge, 1, 1);
            }
            g2.setColor(Color.blue);
            for (PixelEdge outEdge : this.getOutEdges(this.selectedVertex)) {
                this.displayEdge(g2, outEdge, -1, -1);
            }
            //            this.printVertex(this.selectedVertex);
            //            for (PixelEdge inEdge : this.getInEdges(this.selectedVertex)) {
            //                this.printEdge(inEdge);
            //            }
            //            for (PixelEdge outEdge : this.getOutEdges(this.selectedVertex)) {
            //                this.printEdge(outEdge);
            //
            //            }

            //                        // overpass private members
            //                        Field f;
            //                        try {
            //                            f = MinSourceSinkCut.class.getDeclaredField("ekMaxFlow");
            //                            f.setAccessible(true);
            //                            EdmondsKarpMaximumFlow<PixelVertex, PixelEdge> ek = (EdmondsKarpMaximumFlow<PixelVertex, PixelEdge>) f.get(this.algo);
            //                            Class<?> ekClasses[] = EdmondsKarpMaximumFlow.class.getDeclaredClasses();
            //                            for (Class<? > ekClass : ekClasses ) {
            //                                if (ekClass.getSimpleName().equals("Node")) {
            //                                    
            //                                }
            //                            }
            //                            List<EdmondsKarpMaximumFlow.Node> nodex = ek.nodes;
            //                        } catch (Exception e) {
            //                            e.printStackTrace();
            //                        }
        }
    }

    /**
     * @param v
     */
    private void printVertex(PixelVertex v) {
        System.err.println(v + " Tile  = " + v.getTileR() + " " + v.getTileG() + " " + v.getTileB() + " " + v.getTileA());
        System.err.println(v + " Image = " + v.getImageR() + " " + v.getImageG() + " " + v.getImageB() + " " + v.getImageA());

    }

    private void printEdge(PixelEdge e) {
        System.err.println("Edge from " + e.getSource() + " to " + e.getTarget() + " weight = " + this.graph.getEdgeWeight(e));
    }

    private Set<PixelEdge> getInEdges(PixelVertex v) {
        Set<PixelEdge> edges = new HashSet<PixelEdge>();
        for (PixelEdge e : this.graph.edgeSet()) {
            if (e.getTarget() == v) {
                edges.add(e);
            }
        }
        return edges;
    }

    private Set<PixelEdge> getOutEdges(PixelVertex v) {
        Set<PixelEdge> edges = new HashSet<PixelEdge>();
        for (PixelEdge e : this.graph.edgeSet()) {
            if (e.getSource() == v) {
                edges.add(e);
            }
        }
        return edges;
    }

    private int x(double x) {
        return (int) (x * this.zoom - this.tr.x);
    }

    private int y(double y) {
        return (int) (y * this.zoom - this.tr.y);
    }

    private void displayVertex(Graphics2D g2, PixelVertex v) {
        g2.fillRect(this.x(v.getX()), this.y(v.getY()), (int) this.zoom, (int) this.zoom);
    }

    private void displayEdge(Graphics2D g2, PixelEdge e, int dx, int dy) {
        if (this.algo.getCutEdges().contains(e)) {
            g2.setColor(Color.red);
        }
        g2.drawLine(this.x(e.getSource().getX() + 0.5) + dx, this.y(e.getSource().getY() + 0.5) + dy, this.x(e.getTarget().getX() + 0.5) + dx,
                this.y(e.getTarget().getY() + 0.5) + dy);

        //        g2.drawString(this.df.format(this.graph.getEdgeWeight(e)), this.x((e.getSource().getX() + e.getTarget().getX()) / 2.) + 25 * dx,
        //                this.y((e.getSource().getY() + e.getTarget().getY()) / 2.) + 25 * dy);

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.mouseClicked) {
            this.tr.setLocation(this.clickedTr.x + (this.mousePos.x - e.getX()), this.clickedTr.y + (this.mousePos.y - e.getY()));
            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int vertexX = (int) ((e.getX() + this.tr.x) / this.zoom);
        int vertexY = (int) ((e.getY() + this.tr.y) / this.zoom);
        PixelVertex v = this.getVertex(vertexX, vertexY);
        if (v == null) {
            this.app.message("");
            return;
        }
        this.app.message(v.toString());
        this.selectedVertex = v;
        this.repaint();

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.zoom /= 1 + 0.1 * e.getPreciseWheelRotation();
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //        System.err.println("Pixel Vertex " + );
        this.allEdges = !this.allEdges;
        this.repaint();
    }

    private PixelVertex getVertex(int vertexX, int vertexY) {
        for (PixelVertex v : this.graph.vertexSet()) {
            if (v.getX() == vertexX && v.getY() == vertexY) {
                return v;
            }
        }
        return null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.mouseClicked = true;
        this.mousePos = e.getPoint();
        this.clickedTr.setLocation(this.tr);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.mouseClicked = false;
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
