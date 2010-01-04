/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.util.color;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A simple Color Quantizer using an octree.
 * 
 * @author Julien Perret
 *
 */
public class OctreeQuantizer { 
    static Logger logger=Logger.getLogger(OctreeQuantizer.class.getName());
    private static final int MAX_DEPTH = 7;
    private int numNodes = 0;
    private int maxNodes = 0;
    private int size;
    private int currentDepth;
    private int leafDepth;
    private Color colorLookUpTable[];
    private OctreeNode tree = null;
    //the reduceList contains a chain of all trees which contain at least 2 children for each level
    private OctreeNode reduceList[] = new OctreeNode[MAX_DEPTH + 1];
    private int k;
    private BufferedImage image;
    public BufferedImage getImage() {return this.image;}

    private BufferedImage remappedImage;
    public BufferedImage getRemappedImage() {return this.remappedImage;}
    
    // Getters and Setters
    public static int getMAXDEPTH() {return MAX_DEPTH;} 
    public int getNumNodes() {return this.numNodes;}
    public void setNumNodes(int numNodes) {this.numNodes = numNodes;}
    public int getMaxNodes() {return this.maxNodes;}
    public void setMaxNodes(int maxNodes) {this.maxNodes = maxNodes;}
    public int getSize() {return this.size;}
    public void setSize(int size) {this.size = size;}
    public int getLevel() {return this.currentDepth;}
    public void setLevel(int level) {this.currentDepth = level;}
    public int getLeafLevel() {return this.leafDepth;}
    public void setLeafLevel(int leafLevel) {this.leafDepth = leafLevel;}
    public OctreeNode getTree() {return this.tree;}
    public int getK() {return this.k;}
    public Color[] getColorLookUpTable() {return this.colorLookUpTable;}
    //public OctreeNode[] getReduceList() {return reduceList;}

    /**
     * Constructor.
     * @param image image to quantize
     */
    public OctreeQuantizer(BufferedImage image) {
	this.image=image;
	Set<Color> paletteColors = ColorUtil.getColors(image);
	int numberOfColors = paletteColors.size();
	logger.info(numberOfColors+" colors found"); //$NON-NLS-1$
	this.k = numberOfColors;
	quantize();
    }
    /**
     * Constructor.
     * Runs the quantization using parameters ki as the number of colors.
     * @param image image to quantize
     * @param ki number of colors.
     */
    public OctreeQuantizer(BufferedImage image, int ki) {
	this.image=image;
	this.k = ki;
	quantize();
    }

    private void quantize() {
        long t = System.currentTimeMillis();
    	setColors();
    	logger.info("Octree Quantization took "+(System.currentTimeMillis()-t)+" ms"); //$NON-NLS-1$ //$NON-NLS-2$
    	t = System.currentTimeMillis();
    	reMap(this.image);
    	logger.info("Remapping took "+(System.currentTimeMillis()-t)+" ms");    	 //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * Remaps the given image using the quantization.
     * @param imageToReMap image to remap
     */
    public void reMap(BufferedImage imageToReMap) {
	this.remappedImage=new BufferedImage(imageToReMap.getWidth(), imageToReMap.getHeight(),BufferedImage.TYPE_INT_RGB);
	for(int y = 0 ; y < imageToReMap.getHeight() ; y++) {
	    for(int x = 0 ; x < imageToReMap.getWidth() ; x++) {
		Color color = new Color(imageToReMap.getRGB(x, y));
		int id = findColor(this.tree, color); 
		color = this.colorLookUpTable[id];
		this.remappedImage.setRGB(x, y, color.getRGB());
	    }
	}
    }

    Map<Color,Boolean> colorMap = new HashMap<Color,Boolean>();
    
    /**
     * Builds the octree used for the quantization using parameter k as the number of colors.
     */
    public void setColors() {
	// Create the color Look-Up Table
	this.colorLookUpTable = new Color[this.k];
	// Delete the existing tree if there is one
	this.tree = delete(this.tree);
	// Set the number of leaves of the tree to 0
	this.size = 0;
	// Set the current node depth to the maximum depth (maximum image depth is 24 bits)
	this.currentDepth = MAX_DEPTH;
	// Set the leaf level to the current node depth+1 (all leaves are on the same level)
	this.leafDepth = this.currentDepth + 1;
	// For each pixel of the input image, insert the color into the octree
	for(int y = 0 ; y < this.image.getHeight() ; y++) {
	    for(int x = 0 ; x < this.image.getWidth() ; x++) {
		Color color = new Color(this.image.getRGB(x, y));
		Boolean added = this.colorMap.get(color);
		if (added==null) {
			this.colorMap.put(color,Boolean.TRUE);
			this.tree = insertNode(this.tree, color, 0);
			//writeDebugImage();
			// if we have more leaves than the target number of colors, reduce the tree
			if (this.size > this.k) reduceTree();
		}
	    }
	}
	int index[] = new int[1]; 
	index[0] = 0; 
	initColorLookUpTable(this.tree, index);
    }

	int tmpIndex = 0;
    /**
	 * 
	 */
	@SuppressWarnings("unused")
	private void writeDebugImage() {
		BufferedImage debugImage = buildOctreeImage(50);
		ColorUtil.writeImage(debugImage, "/tmp/Octree_"+(this.tmpIndex++)); //$NON-NLS-1$
	}

	/**
     * Builds the octree used for the quantization using the given colors
     */
    public void setColors(Color[] colors) {
	// Create the color Look-Up Table
	this.k=colors.length;
	this.colorLookUpTable = new Color[this.k];
	// Delete the existing tree if there is one
	this.tree = delete(this.tree);
	// Set the number of leaves of the tree to 0
	this.size = 0;
	// Set the current node depth to the maximum depth (maximum image depth is 24 bits)
	this.currentDepth = MAX_DEPTH;
	// Set the leaf level to the current node depth+1 (all leaves are on the same level)
	this.leafDepth = this.currentDepth + 1;
	// For input color, insert the color into the octree
	for(Color color:colors) if(color!=null) this.tree = insertNode(this.tree, color, 0);
	int index[] = new int[1]; 
	index[0] = 0; 
	initColorLookUpTable(this.tree, index);
    }

    /**
     * Compute the branch index for a certain color at a depth
     * It is a 3 bit integer (i.e. from 0 to 7) to determine at which branch to insert the new color.
     * The first (leftmost) bit is taken from the red component, the second (middle) bit is taken from the green and the last from the blue.
     * The bit taken from each component is chosen according to the depth of the depth into which the color is inserted into the octree.
     * The highest bits are taken for the topmost depth.
     * Basically, we reproduce the same ordering as in sRGB...
     * @param color color to build the index from
     * @param level depth to compute the index for
     * @return the branch index for a certain color at a given depth.
     */
    private int branchIndex(Color color, int level) {
	return ((color.getRed() >> (MAX_DEPTH - level)) & 1) << 2 | ((color.getGreen() >> (MAX_DEPTH - level)) & 1) << 1 | (color.getBlue() >> (MAX_DEPTH - level)) & 1;
    }
    /**
     * Find the color index corresponding to a given color
     * @param octreeNode tree node to start the seach from
     * @param color the color to look for
     * @return the color index corresponding to a given color
     */
    public int findColor(OctreeNode octreeNode, Color color) { 
	if (octreeNode.leaf) return octreeNode.colorIndex;
        OctreeNode treeNode = octreeNode.children[branchIndex(color, octreeNode.depth)]; 
        return (treeNode != null)?findColor(treeNode, color):findNearestEntry(color); 
    } 
    /** 
     * Search the color look-up table for the closest color to a given one
     * @param color the color to look for in the look-up table
     * @return an index of the closest entry. 
     */ 
    public int findNearestEntry(Color color) { 
	int n = this.colorLookUpTable.length;
	float distance = Float.MAX_VALUE; 
	int bestIndex = 0; 
	for (int i=0; i < n ; i++) { 
	    Color c = this.colorLookUpTable[i];
	    if (c==null) continue;
	    // compute the distance e between the color we're looking for and the current one
	    float e = ColorUtil.sqDistance(c,color); 
	    if (e < distance) {
		distance = e;
		bestIndex = i;
	    } 
	} 
	return bestIndex; 
    }

    /**
     * Insert a (new) color to the octree
     * @param octreeNode the octree to insert the color into 
     * @param color color to insert
     * @param level depth of the tree
     * @return the modified tree
     */
    public OctreeNode insertNode(OctreeNode octreeNode, Color color, int level) {
	//if (logger.isDebugEnabled()) logger.debug("insertNode "+color+" at depth "+depth);
	// if the octree is empty, create a new node
        OctreeNode node = octreeNode;
	if (node == null) {
	    node = new OctreeNode();
	    //increment the global number of nodes
	    this.numNodes++;
	    // maintain the maximum number of nodes the octree ever contained
	    if (this.numNodes > this.maxNodes) this.maxNodes = this.numNodes;
	    // set the node's depth
	    node.depth = level;
	    node.leaf = (level >= this.leafDepth) ? true : false;
	    if (node.leaf) this.size++; // increment the number of leaves
	}
	// increment the number of colors the node contains
        node.nbColor++;
	// sum up all the red, green and blue values of all contained colors
        node.addComponents(color);

	if (!(node.leaf) && (level < this.leafDepth)) {
	    int branch = branchIndex(color,level); 
	    if (node.children[branch] == null) {
		// add a new child to the node
	        node.nbChildren++;
		if (node.nbChildren == 2) {
		    // replace the tree for the current depth by the current tree
		    // and add the previous one to the current tree as its next reduceable tree
		    // this way, the reduceList contains a chain of all trees which contain at least 2 children for each level
                    node.nextReduceable = this.reduceList[level];
		    this.reduceList[level] = node;
		}
	    }
	    // insert the color as a child of the current node
	    node.children[branch] = insertNode(node.children[branch], color, level + 1); 
	}
	return node; 
    }

    /**
     * @param octreeNode
     * @return
     */
    public OctreeNode delete(OctreeNode octreeNode) { 
	if (octreeNode == null) return null; 
	for (int i = 0; i < 8; i++) octreeNode.children[i] = delete(octreeNode.children[i]);
	this.numNodes--;
	return null;
    }

    /**
     * Reduce the depth of the tree by deleting children of a certain depth
     * The first tree deleted is the last one encounterd when adding colors
     */
    public void reduceTree() {
	int newLevel = this.currentDepth;
	// find the lowest level where we have reduceable trees, i.e. trees with at least 2 children
	while (this.reduceList[newLevel] == null) newLevel--;
	reduceNode(newLevel);
    } 

    public static final int MIN_STANDARD_DEVIATION=0;
    public static final int MIN_DISTANCE=1;
    public static int reductionMethod=MIN_DISTANCE;
    /**
	 * @param octreeNode
	 */
	private void reduceNode(int newLevel) {
		if (reductionMethod==MIN_STANDARD_DEVIATION) reduceNodeWithMinimumStandardDeviation(newLevel);
		if (reductionMethod==MIN_DISTANCE) reduceNodeWithMinimumMaximumDistance(newLevel);
	}

	/**
	 * @param newLevel
	 */
	@SuppressWarnings("unused")
	private void reduceNodeWithMinimumDistance(int newLevel) {
		// find the best octree node to reduce
		OctreeNode octreeNode = this.reduceList[newLevel];
		OctreeNode previousOctreeNode = null;
		float minDistance = Float.MAX_VALUE;
		OctreeNode minOctreeNode = octreeNode;
		OctreeNode minPreviousOctreeNode = null;
		int minChildIndex = 0;
		// go through all the reduceable octree node of the level to find the best choice
		while(octreeNode!=null) {
			Color parentColor = octreeNode.getColor();
			for (int i = 0; i < 8; i++) {
				if ( octreeNode.children[i] != null ) {
					Color childColor = octreeNode.children[i].getColor();
					float distance = ColorUtil.sqDistance(parentColor, childColor);
					if (distance<minDistance) {
						minDistance = distance;
						minOctreeNode = octreeNode;
						minPreviousOctreeNode = previousOctreeNode;
						minChildIndex = i;
					}
				}
			}
			previousOctreeNode = octreeNode;
			octreeNode = octreeNode.nextReduceable;
		}
		octreeNode=minOctreeNode;
		previousOctreeNode = minPreviousOctreeNode;
		// if there were only 2 children, the node is not reduceable any more
		if(octreeNode.nbChildren==2) {
			if(previousOctreeNode!=null) previousOctreeNode.nextReduceable=octreeNode.nextReduceable;
			else this.reduceList[newLevel] = octreeNode.nextReduceable;
			// delete the reduceable octree we chose and replace it by the next reduceable octree of the same level if there is one
			// now, mark it as a leaf and delete its children
			octreeNode.leaf = true;
			this.size = this.size - octreeNode.nbChildren + 1;
			int treeDepth = octreeNode.depth;
			for (int i = 0; i < 8; i++) octreeNode.children[i] = delete(octreeNode.children[i]); 
			if (treeDepth < this.currentDepth) {
			    this.currentDepth = treeDepth; 
			    this.leafDepth = this.currentDepth + 1; 
			}		
		} else {
			// otherwise, juste remove the selected child
			//octreeNode.nbColor-=octreeNode.children[minChildIndex].nbColor;
			//octreeNode.red-=octreeNode.children[minChildIndex].red;
			//octreeNode.green-=octreeNode.children[minChildIndex].green;
			//octreeNode.blue-=octreeNode.children[minChildIndex].blue;
			//octreeNode.nbChildren--;
			delete(octreeNode.children[minChildIndex]);
			octreeNode.children[minChildIndex]=null;
		}
	}

	/**
	 * @param newLevel
	 */
	private void reduceNodeWithMinimumStandardDeviation(int newLevel) {
		// find the best octree node to reduce
		OctreeNode octreeNode = this.reduceList[newLevel];
		OctreeNode previousOctreeNode = null;
		double minSqStandardDeviation = Double.MAX_VALUE;
		OctreeNode minOctreeNode = null;
		OctreeNode minPreviousOctreeNode = null;
		// go through all the reduceable octree node of the level to find the best choice
		while(octreeNode!=null) {
		    double sqStandardDeviation = 0;
		    Color parentColor = octreeNode.getColor();
		    for (int i = 0; i < 8; i++) {
			if ( octreeNode.children[i] != null ) {
				Color childColor = octreeNode.children[i].getColor();
				float deviation = ColorUtil.sqDistance(parentColor, childColor);
				sqStandardDeviation+=deviation;
			}
		    }
		    sqStandardDeviation/=octreeNode.nbChildren;
		    if (sqStandardDeviation<minSqStandardDeviation) {
			minSqStandardDeviation = sqStandardDeviation;
			minOctreeNode = octreeNode;
			minPreviousOctreeNode = previousOctreeNode;
		    }
		    previousOctreeNode = octreeNode;
		    octreeNode = octreeNode.nextReduceable;
		}
		octreeNode=minOctreeNode;
		previousOctreeNode = minPreviousOctreeNode;
		if(previousOctreeNode!=null) previousOctreeNode.nextReduceable=octreeNode.nextReduceable;
		else this.reduceList[newLevel] = octreeNode.nextReduceable;
		// delete the reduceable octree we chose and replace it by the next reduceable octree of the same level if there is one
		// now, mark it as a leaf and delete its children
		octreeNode.leaf = true;
		this.size = this.size - octreeNode.nbChildren + 1;
		int treeDepth = octreeNode.depth;
		for (int i = 0; i < 8; i++) octreeNode.children[i] = delete(octreeNode.children[i]); 
		if (treeDepth < this.currentDepth) {
		    this.currentDepth = treeDepth; 
		    this.leafDepth = this.currentDepth + 1; 
		}
	}

	/**
	 * @param newLevel
	 */
	private void reduceNodeWithMinimumMaximumDistance(int newLevel) {
		// find the best octree node to reduce
		OctreeNode octreeNode = this.reduceList[newLevel];
		OctreeNode previousOctreeNode = null;
		double minMaxDistance = Double.MAX_VALUE;
		OctreeNode minOctreeNode = null;
		OctreeNode minPreviousOctreeNode = null;
		// go through all the reduceable octree node of the level to find the best choice
		while(octreeNode!=null) {
		    double maxDistance = 0;
		    Color parentColor = octreeNode.getColor();
		    for (int i = 0; i < 8; i++) {
			if ( octreeNode.children[i] != null ) {
				Color childColor = octreeNode.children[i].getColor();
				float deviation = ColorUtil.sqDistance(parentColor, childColor);
				maxDistance=Math.max(maxDistance, deviation);
			}
		    }
		    if (maxDistance<minMaxDistance) {
			minMaxDistance = maxDistance;
			minOctreeNode = octreeNode;
			minPreviousOctreeNode = previousOctreeNode;
		    }
		    previousOctreeNode = octreeNode;
		    octreeNode = octreeNode.nextReduceable;
		}
		octreeNode=minOctreeNode;
		previousOctreeNode = minPreviousOctreeNode;
		if(previousOctreeNode!=null) previousOctreeNode.nextReduceable=octreeNode.nextReduceable;
		else this.reduceList[newLevel] = octreeNode.nextReduceable;
		// delete the reduceable octree we chose and replace it by the next reduceable octree of the same level if there is one
		// now, mark it as a leaf and delete its children
		octreeNode.leaf = true;
		this.size = this.size - octreeNode.nbChildren + 1;
		int treeDepth = octreeNode.depth;
		for (int i = 0; i < 8; i++) octreeNode.children[i] = delete(octreeNode.children[i]); 
		if (treeDepth < this.currentDepth) {
		    this.currentDepth = treeDepth; 
		    this.leafDepth = this.currentDepth + 1; 
		}
	}

	/**
     * Initialize the color look-up table.
     * @param octreeNode an octree
     * @param index the current color index in the color look-up table
     */
    public void initColorLookUpTable(OctreeNode octreeNode, int index[]) {
	if (octreeNode != null) {
	    // if tree is a leaf, add its average color to the look-up table and affect it with its color index
	    // otherwise, treat its children
	    if (octreeNode.leaf || octreeNode.depth == this.leafDepth) {
		if(this.colorLookUpTable[index[0]]==null) this.colorLookUpTable[index[0]]=octreeNode.getColor();
		octreeNode.colorIndex = index[0]++;
		octreeNode.leaf = true;
	    } else for (int octant = 0; octant < 8; octant++) initColorLookUpTable(octreeNode.children[octant], index);
	}
    }

    public void writeOctreeImage(int sizeElement, String imageName) {
	BufferedImage octreeImage = buildOctreeImage(sizeElement);
	ColorUtil.writeImage(octreeImage, imageName);
    }

    /**
     * Build an image representing the entire color octree.
     * @param sizeElement the size (radius) of the largest circle to draw
     * @return an image representing the entire color octree
     */
    public BufferedImage buildOctreeImage(int sizeElement) {
	int leafLevel = Math.min(getLeafLevel()+1,3); 
	int radius = (leafLevel)*sizeElement;
	BufferedImage octreeImage = new BufferedImage(2*radius,2*radius,java.awt.image.BufferedImage.TYPE_INT_RGB);
	Graphics2D graphics = octreeImage.createGraphics();
	graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	graphics.setStroke(new BasicStroke(1.0f));
	drawNode(graphics,getTree(),0,radius,leafLevel);
	return octreeImage;
    }
    /**
     * Draw a tree and its children into a graphics.
     * @param graphics graphics in which to draw the tree
     * @param node tree to draw
     * @param startAngle angle to start drawing
     */
    public void drawNode(Graphics2D graphics, OctreeNode node, double startAngle, int radius, int leafLevel) {
	if ((node==null)||(node.depth>leafLevel)) return;
	// shift the the beginning of the current arc
	int x = shift(leafLevel,node.depth,radius);
	// radius of the current arc
	int r = 2*radius(leafLevel,node.depth,radius);
	// angle for each child element
	double angle = 360.0/Math.pow(8.0,node.depth);
	if (logger.isDebugEnabled()) logger.debug(node.depth+"-"+angle); //$NON-NLS-1$
	/*
		graphics.setColor(Color.black);
		graphics.drawLine(x+r/2, x+r/2, (int)(x+(r/2)*(1+Math.cos(Math.toRadians(startAngle)))), (int)(x+(r/2)*(1+Math.sin(Math.toRadians(startAngle)))));
		graphics.drawLine(x+r/2, x+r/2, (int)(x+(r/2)*(1+Math.cos(Math.toRadians(startAngle+angle)))), (int)(x+(r/2)*(1+Math.sin(Math.toRadians(startAngle+angle)))));
		graphics.drawArc(x,x,r,r,(int)startAngle,(int)(angle+0.5));
	 */
	// draw the children
	double angleChildren = angle/8.0;
	for(int index=0;index<8;index++) drawNode(graphics,node.children[index],startAngle+index*angleChildren,radius,leafLevel);
	// build the average color for the children of the node
	Color color = node.getColor();
	graphics.setColor(color);
	graphics.fillArc(x,x,r,r,(int)startAngle,(int)(angle+0.5));
    }

    /**
     * @param leafLevel
     * @param depth
     * @param radius
     * @return
     */
    public static int shift(int leafLevel, int depth, int radius) {return (int)((new Double(leafLevel-depth).doubleValue()*radius)/new Double(leafLevel).doubleValue());}

    /**
     * @param leafLevel
     * @param depth
     * @param radius
     * @return
     */
    public static int radius(int leafLevel, int depth, int radius) {return (int)(new Double(depth*radius).doubleValue()/new Double(leafLevel).doubleValue());}

    /**
     * Class representing a color octree node.
     * @author Julien Perret
     */
    class OctreeNode { 
        boolean leaf = false;
        int depth = 0;
        int colorIndex = 0;
        int nbChildren = 0;
        int nbColor = 0;
        float[] components = new float[]{0f,0f,0f};
        OctreeNode nextReduceable = null;
        public OctreeNode children[] = new OctreeNode[8];
        public OctreeNode() {for (int i = 0; i < 8; i++) this.children[i] = null;}
        
        public void addComponents(Color color) {
    		float[] newComponents = 
    			(ColorUtil.getColorSpace()==ColorUtil.RGB)?color.getColorComponents(null):
    			(ColorUtil.getColorSpace()==ColorUtil.LAB)?ColorUtil.toLab(color):ColorUtil.toXyz(color);
        	for (int i = 0 ; i < 3 ; i++) this.components[i]+=newComponents[i];
        }
        
        public Color getColor() {
        	float[] newComponents = new float[3];
        	for ( int c = 0 ; c < 3 ; c++ ) newComponents[c]=this.components[c]/Math.max(1,this.nbColor);
        	if (logger.isDebugEnabled()) logger.debug(newComponents[0]+" "+newComponents[1]+" "+newComponents[2]); //$NON-NLS-1$ //$NON-NLS-2$
        	return
        		(ColorUtil.getColorSpace()==ColorUtil.RGB)?new Color(ColorSpace.getInstance(ColorSpace.CS_sRGB),newComponents,1f):
    			(ColorUtil.getColorSpace()==ColorUtil.LAB)?ColorUtil.toColor(newComponents):new Color(ColorSpace.getInstance(ColorSpace.CS_CIEXYZ),newComponents,1f);
        }
    }
}
