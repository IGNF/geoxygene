package fr.ign.cogit.geoxygene.appli.gl;



/**
 * Pixel renderer draws pixels in a texture image
 * 
 * @author JeT
 * 
 */
public interface TexturePixelRenderer {

    /**
     * @param x
     *            point x coordinate
     * @param y
     *            point y coordinate
     * @param image
     *            image to set point
     */
    void renderFirstPixel(int x, int y, GradientTextureImage image);

    /**
     * @param x
     *            point x coordinate
     * @param y
     *            point y coordinate
     * @param image
     *            image to set point
     */
    void renderPixel(int x, int y, GradientTextureImage image);

    /**
     * @param x
     *            point x coordinate
     * @param y
     *            point y coordinate
     * @param image
     *            image to set point
     */
    void renderLastPixel(int x, int y, GradientTextureImage image);

    /**
     * describe the current drawn line
     * 
     * @param xi
     * @param yi
     * @param xf
     * @param yf
     */
    void setCurrentLine(int xi, int yi, int xf, int yf);

}
