package fr.ign.cogit.geoxygene.util.gl;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public interface Tile {

    public static final byte MASK_IN = (byte) 0;
    public static final byte MASK_OUT = (byte) 255;

    /**
     * set the tile image. The image is converted to BYTE_ABGR
     * 
     */
    public abstract void setImage(BufferedImage image);

    /**
     * @return the image
     */
    public abstract BufferedImage getImage();

    /**
     * get tile width
     */
    public abstract int getWidth();

    /**
     * get tile height
     */
    public abstract int getHeight();

    /**
     * @return the borders
     */
    public abstract List<Point> getBorders();

    /**
     * @return the mask
     */
    public abstract BufferedImage getMask();

    /**
     * @return the border
     */
    public abstract BufferedImage getBorder();

    /**
     * @return the number of lighten pixels
     */
    public abstract int getSize();

}