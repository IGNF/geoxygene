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

package fr.ign.util.graphcut;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import fr.ign.cogit.geoxygene.util.ColorUtil;

/**
 * @author JeT
 *         Algorithm used to paste tiles into an image using the graph-cut
 *         algorithm
 *         It stores a mask image to know where previous tiles have been pasted.
 *         The algorithm computes a connected cut between existing image and
 *         tile which has the minimal perceptible difference
 *         This class alters the given image, it does not copy it !
 */
public class GraphCut {

    private BufferedImage image = null; // ABGR image containing the existing image
    private BufferedImage mask = null; // Grayscale Mask (used as binary).
    private byte[] maskPixels = null;
    private byte[] imagePixels = null;
    private MinSourceSinkCut<PixelVertex, PixelEdge> algoMinCut = null;
    private Shape clippingShape;

    /**
     * Constructor
     */
    public GraphCut() {
        super();
    }

    /**
     * 
     * @param image
     *            ABGR image containing the existing image
     *            This class alters the given image, it does not copy it !
     */
    public GraphCut(BufferedImage image) {
        super();
        this.setImage(image);
    }

    /**
     * Set the image to be filled. Image mask is reset to fully empty
     * This class alters the given image, it does not copy it !
     */
    public void setImage(BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
            throw new IllegalArgumentException("GraphCut images must be of type ABGR");
        }
        this.image = image;
        this.initializeMask();
        this.imagePixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    /**
     * Set a clipping shape to clip pasted tiles
     * 
     * @param clippingShape
     */
    public void setClippingShape(Shape clippingShape) {
        this.clippingShape = clippingShape;
    }

    /**
     * initialize mask to white value (white = empty)
     */
    private void initializeMask() {
        // generate mask
        this.mask = new BufferedImage(1600, 1200, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = this.mask.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setColor(Color.white);
        g2.fillRect(0, 0, this.mask.getWidth(), this.mask.getHeight());
        this.maskPixels = ((DataBufferByte) this.getMask().getRaster().getDataBuffer()).getData();
    }

    /**
     * @return the image mask
     */
    public BufferedImage getMask() {
        return this.mask;
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * Paste a tile in the final image using graph given affine transformation
     * 
     * @param tile
     *            tile to paste
     * @param tileOrientation
     *            tile orientation and position in the final image
     */
    public void pasteTile(BufferedImage tile, AffineTransform tileTransform) {
        DefaultDirectedWeightedGraph<PixelVertex, PixelEdge> graph = new DefaultDirectedWeightedGraph<PixelVertex, PixelEdge>(new PixelEdge.PixelEdgeFactory());
        PixelEdge edge = null;
        double weight = 0;
        int w = tile.getWidth();
        int h = tile.getHeight();
        PixelVertex[][] tileVertices = new PixelVertex[h][w];
        PixelVertex vertex = null;
        PixelVertex A = new PixelVertex(-3, h / 2);
        PixelVertex B = new PixelVertex(w + 3, h / 2);
        graph.addVertex(A);
        graph.addVertex(B);

        // graphCutTile is the final image to draw in image
        BufferedImage graphCutTile = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D gTile = graphCutTile.createGraphics();
        gTile.setComposite(AlphaComposite.Clear);
        gTile.fillRect(0, 0, w, h);

        byte[] graphCutTilePixels = ((DataBufferByte) graphCutTile.getRaster().getDataBuffer()).getData();
        byte[] tilePixels = ((DataBufferByte) tile.getRaster().getDataBuffer()).getData();

        // Add pixels as graph vertices
        for (int yTile = 0; yTile < h; yTile++) {
            for (int xTile = 0; xTile < w; xTile++) {
                Point2D pixel = new Point2D.Double(xTile, yTile);
                Point2D transformedPixel = new Point2D.Double();
                tileTransform.transform(pixel, transformedPixel);
                int xImage = (int) transformedPixel.getX();
                int yImage = (int) transformedPixel.getY();
                if (xImage < 0 || xImage >= this.getImage().getWidth() || yImage < 0 || yImage >= this.getImage().getHeight()) {
                    tileVertices[yTile][xTile] = null;
                    continue;
                }
                int lTile = (xTile + yTile * w) * 4;
                int lImage = (xImage + yImage * this.getImage().getWidth()) * 4;
                int lMask = (xImage + yImage * this.getImage().getWidth());
                byte pixelMask = this.maskPixels[lMask];
                int tileA = tilePixels[lTile] & 0xFF;
                int tileB = tilePixels[lTile + 1] & 0xFF;
                int tileG = tilePixels[lTile + 2] & 0xFF;
                int tileR = tilePixels[lTile + 3] & 0xFF;

                if (pixelMask != 0) { // out of mask
                    // copy Tile
                    graphCutTilePixels[lTile + 0] = (byte) tileA;
                    graphCutTilePixels[lTile + 1] = (byte) tileB;
                    graphCutTilePixels[lTile + 2] = (byte) tileG;
                    graphCutTilePixels[lTile + 3] = (byte) tileR;
                } else {
                    int imageA = this.imagePixels[lImage] & 0xFF;
                    int imageB = this.imagePixels[lImage + 1] & 0xFF;
                    int imageG = this.imagePixels[lImage + 2] & 0xFF;
                    int imageR = this.imagePixels[lImage + 3] & 0xFF;
                    tileVertices[yTile][xTile] = new PixelVertex(xTile, yTile, tileR / 255f, tileG / 255f, tileB / 255f, tileA / 255f, imageR / 255f,
                            imageG / 255f, imageB / 255f, imageA / 255f);
                    graph.addVertex(tileVertices[yTile][xTile]);
                    if (this.isOnTileBorder(xTile, yTile, tile)) {
                        graph.addVertex(tileVertices[yTile][xTile]);
                        edge = graph.addEdge(A, tileVertices[yTile][xTile]);
                        graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);
                    } else if (this.hasNonZeroMaskBorder(xTile, yTile, tileTransform)) {
                        graph.addVertex(tileVertices[yTile][xTile]);
                        edge = graph.addEdge(tileVertices[yTile][xTile], B);
                        graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);
                    }

                }
            }
        }

        //        // add edges from image A (dst) to borders
        //        for (int xTile = 0; xTile < this.app.getTile().getWidth(); xTile++) {
        //            vertex = tileVertices[0][xTile];
        //            if (vertex != null) {
        //                edge = graph.addEdge(A, vertex);
        //                graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);
        //            }
        //            vertex = tileVertices[this.app.getTile().getHeight() - 1][xTile];
        //            if (vertex != null) {
        //                edge = graph.addEdge(A, vertex);
        //                graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);
        //            }
        //        }
        //        for (int yTile = 1; yTile < this.app.getTile().getHeight() - 1; yTile++) {
        //            vertex = tileVertices[yTile][0];
        //            if (vertex != null) {
        //                edge = graph.addEdge(A, vertex);
        //                graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);
        //            }
        //            vertex = tileVertices[yTile][this.app.getTile().getWidth() - 1];
        //            if (vertex != null) {
        //                edge = graph.addEdge(A, vertex);
        //                graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);
        //            }
        //        }

        //        // add edge from center to image B (src)
        //        double keepRatio = 0.2;
        //        for (int yTile = (int) (h / 2 * (1 - keepRatio)); yTile < (int) (h / 2 * (1 + keepRatio)); yTile++) {
        //            for (int xTile = (int) (w / 2 * (1 - keepRatio)); xTile < (int) (w / 2 * (1 + keepRatio)); xTile++) {
        //                edge = graph.addEdge(tileVertices[yTile][xTile], B);
        //                graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);
        //            }
        //        }

        // add edges to A & B from opposite corners
        //        edge = graph.addEdge(A, tileVertices[0][0]);
        //        graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);
        //        edge = graph.addEdge(tileVertices[h - 1][w - 1], B);
        //        graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);

        //        for (int yTile = 1; yTile < this.app.getTile().getHeight() - 1; yTile++) {
        //            vertex = tileVertices[yTile][this.app.getTile().getWidth() - 1];
        //            if (vertex != null) {
        //                edge = graph.addEdge(vertex, B);
        //                graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);
        //            }
        //                        vertex = tileVertices[yTile][this.app.getTile().getWidth() - 1];
        //                        if (vertex != null) {
        //                            edge = graph.addEdge(A, vertex);
        //                            graph.setEdgeWeight(edge, Double.POSITIVE_INFINITY);
        //                        }
        //        }

        // add all edges between pixels (forward and reverse edges)
        for (int yTile = 0; yTile < h; yTile++) {
            for (int xTile = 0; xTile < w; xTile++) {
                vertex = tileVertices[yTile][xTile];
                if (vertex == null) {
                    continue;
                }
                if (xTile != w - 1) {
                    PixelVertex vertexNeighbor = tileVertices[yTile][xTile + 1];
                    if (vertexNeighbor != null) {
                        edge = graph.addEdge(vertex, vertexNeighbor);
                        weight = distanceColor(vertex, vertexNeighbor);
                        //                System.err.println("weight to x+1 = " + weight);
                        graph.setEdgeWeight(edge, weight);
                        edge = graph.addEdge(vertexNeighbor, vertex);
                        graph.setEdgeWeight(edge, weight);
                    }
                }
                if (yTile != h - 1) {
                    PixelVertex vertexNeighbor = tileVertices[yTile + 1][xTile];
                    if (vertexNeighbor != null) {
                        edge = graph.addEdge(vertex, vertexNeighbor);
                        weight = distanceColor(vertex, vertexNeighbor);
                        //                System.err.println("weight to y+1 = " + weight);
                        graph.setEdgeWeight(edge, weight);
                        edge = graph.addEdge(vertexNeighbor, vertex);
                        graph.setEdgeWeight(edge, weight);
                    }
                }
            }
        }

        this.algoMinCut = new MinSourceSinkCut<PixelVertex, PixelEdge>(graph);
        this.algoMinCut.computeMinCut(A, B);
        for (PixelVertex v : this.algoMinCut.getSinkPartition()) {

            int l = (v.getX() + v.getY() * w) * 4;
            if (l >= 0 && l < w * h * 4) {
                graphCutTilePixels[l + 3] = (byte) (v.getTileR() * 255);
                graphCutTilePixels[l + 2] = (byte) (v.getTileG() * 255);
                graphCutTilePixels[l + 1] = (byte) (v.getTileB() * 255);
                graphCutTilePixels[l + 0] = (byte) (v.getTileA() * 255);
                //                System.err.println("resulting pixel value (byte) = " + graphCutTilePixels[l + 3] + " " + graphCutTilePixels[l + 2] + " "
                //                        + graphCutTilePixels[l + 1] + " " + graphCutTilePixels[l]);
                //                System.err.println("            =>       (float) = " + v.getTileR() + " " + v.getTileG() + " " + v.getTileB() + " " + v.getTileA());
            }
        }
        // finally draw the graphcut tile to the resulting image and update mask
        Graphics2D g2 = this.getImage().createGraphics();
        if (this.clippingShape != null) {
            g2.setClip(this.clippingShape);
        }

        g2.setTransform(tileTransform);
        g2.setComposite(AlphaComposite.SrcOver);
        g2.drawImage(graphCutTile, null, 0, 0);
        // draw tile in mask
        g2 = this.getMask().createGraphics();
        g2.setTransform(tileTransform);
        g2.setComposite(AlphaComposite.Clear);
        g2.drawImage(graphCutTile, null, 0, 0);

    }

    private Byte getMaskPixel(int xTile, int yTile, AffineTransform tileTransform) {
        Point2D pixel = new Point2D.Double(xTile, yTile);
        Point2D transformedPixel = new Point2D.Double();
        tileTransform.transform(pixel, transformedPixel);
        int xImage = (int) transformedPixel.getX();
        int yImage = (int) transformedPixel.getY();
        if (xImage < 0 || xImage >= this.getMask().getWidth() || yImage < 0 || yImage >= this.getMask().getHeight()) {
            return null;
        }
        int lMask = xImage + yImage * this.getMask().getWidth();
        return this.maskPixels[lMask];
    }

    private boolean hasNonZeroMaskBorder(int xTile, int yTile, AffineTransform tileTransform) {
        Byte west = this.getMaskPixel(xTile - 1, yTile, tileTransform);
        if (west != null && west != 0) {
            return true;
        }
        Byte east = this.getMaskPixel(xTile + 1, yTile, tileTransform);
        if (east != null && east != 0) {
            return true;
        }
        Byte north = this.getMaskPixel(xTile, yTile - 1, tileTransform);
        if (north != null && north != 0) {
            return true;
        }
        Byte south = this.getMaskPixel(xTile, yTile + 1, tileTransform);
        if (south != null && south != 0) {
            return true;
        }
        return false;
    }

    /**
     * check if a pixel is on the tile border
     * 
     * @param xTile
     * @param yTile
     * @param tile
     * @return
     */
    private boolean isOnTileBorder(int xTile, int yTile, BufferedImage tile) {
        return xTile == 0 || xTile == tile.getWidth() - 1 || yTile == 0 || yTile == tile.getHeight() - 1;
    }

    //    private boolean isInTile(int xTile, int yTile, BufferedImage tile) {
    //        return xTile >= 0 && xTile < tile.getWidth() && yTile > 0 && yTile < tile.getHeight();
    //    }

    private static double distanceColor(PixelVertex p1, PixelVertex p2) {
        return distanceLab(p1, p2);
    }

    /**
     * get all cut edges from last pasted tile
     * (it is recomputed at each call)
     * 
     * @return the graph cut edges
     */
    public Set<PixelEdge> getLastCutEdges() {
        if (this.algoMinCut == null) {
            return null;
        }
        return this.algoMinCut.getCutEdges();
    }

    /**
     * get all vertices kept from final image during last paste
     * (it is recomputed at each call)
     * 
     * @return the source cut partition
     */
    public Set<PixelVertex> getLastSourcePartition() {
        if (this.algoMinCut == null) {
            return null;
        }
        return this.algoMinCut.getSourcePartition();
    }

    /**
     * get all vertices kept from tile image during last paste
     * (it is recomputed at each call)
     * 
     * @return the tile cut partition
     */
    public Set<PixelVertex> getLastTilePartition() {
        if (this.algoMinCut == null) {
            return null;
        }
        return this.algoMinCut.getSinkPartition();
    }

    /**
     * Distance function in LAB color model
     */
    public static double distanceLab(PixelVertex p1, PixelVertex p2) {
        float[] labVertex1Tile = ColorUtil.rgbToLab(p1.getTileR(), p1.getTileG(), p1.getTileB());
        float[] labVertex2Tile = ColorUtil.rgbToLab(p2.getTileR(), p2.getTileG(), p2.getTileB());
        float[] labVertex1Image = ColorUtil.rgbToLab(p1.getImageR(), p1.getImageG(), p1.getImageB());
        float[] labVertex2Image = ColorUtil.rgbToLab(p2.getImageR(), p2.getImageG(), p2.getImageB());
        float dlp = labVertex2Tile[0] - labVertex2Image[0];
        float dap = labVertex2Tile[1] - labVertex2Image[1];
        float dbp = labVertex2Tile[2] - labVertex2Image[2];
        float dln = labVertex1Tile[0] - labVertex1Image[0];
        float dan = labVertex1Tile[1] - labVertex1Image[1];
        float dbn = labVertex1Tile[2] - labVertex1Image[2];

        return Math.sqrt(dlp * dlp + dap * dap + dbp * dbp) + Math.sqrt(dln * dln + dan * dan + dbn * dbn) + 1;
    }

    /**
     * Distance function in RGB color model
     */
    public static double distanceRGB(PixelVertex p1, PixelVertex p2) {
        //        return (Math.abs(p2.getSrcR() - p1.getDstR()) + Math.abs(p2.getSrcG() - p1.getDstG()) + Math.abs(p2.getSrcB() - p1.getDstB())) * p1.getSrcA()
        //                * p2.getDstA();
        float drp = p2.getTileR() - p2.getImageR();
        float dgp = p2.getTileG() - p2.getImageG();
        float dbp = p2.getTileB() - p2.getImageB();
        float drn = p1.getTileR() - p1.getImageR();
        float dgn = p1.getTileG() - p1.getImageG();
        float dbn = p1.getTileB() - p1.getImageB();
        return Math.sqrt(drp * drp + dgp * dgp + dbp * dbp) + Math.sqrt(drn * drn + dgn * dgn + dbn * dbn);
    }

}
