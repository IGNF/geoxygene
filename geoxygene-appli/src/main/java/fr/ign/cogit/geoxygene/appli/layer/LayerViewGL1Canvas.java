/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.layer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2i;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;

/** @author JeT GL drawable canvas inserted into a LayerViewLwjglPanel */
public class LayerViewGL1Canvas extends LayerViewGLCanvas {

    private static final long serialVersionUID = 2813681374260169340L; // serializable

    /** @throws LWJGLException */
    public LayerViewGL1Canvas(final LayerViewGLPanel parentPanel)
            throws LWJGLException {
        super(parentPanel);
    }

    @Override
    protected void initGL() {
        super.initGL();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, this.getWidth(), this.getHeight(), 0, -1000, 1000);

        glMatrixMode(GL_MODELVIEW);
        glViewport(0, 0, this.getWidth(), this.getHeight());

    }

    // private static volatile Object renderingLock = new Object();

    // info found in JVM source code :
    // http://www.docjar.com/html/api/sun/awt/SunToolkit.java.html (:208)
    // But it doesn't change anything...
    //
    // assert !sun.awt.SunToolkit.isAWTLockHeldByCurrentThread();
    // sun.awt.SunToolkit.awtLock();
    // try {
    // } finally {
    // sun.awt.SunToolkit.awtUnlock();
    // }

    // http://comments.gmane.org/gmane.comp.java.openjdk.awt.devel/5073
    // sun.java2d.opengl.OGLRenderQueue rq =
    // sun.java2d.opengl.OGLRenderQueue.getInstance();
    // rq.tryLock();
    // rq.unlock();
    @Override
    protected void paintGL() {
        super.paintGL();
        if (!this.isDisplayable() || this.getContext() == null) {
            return;
        }

        try {
            this.makeCurrent();
        } catch (LWJGLException exception) {
            // if makeCurrent() throws an exception, then the canvas is not
            // ready
            return;
        }

        // synchronized (renderingLock) { // this lock ensure that only one
        // AWTGLCanvas can paint at a time
        try {
            // System.err.println("-------------------------------------------------- paint GL --------------------------------");
            // RenderGLUtil.glDraw(null);
            glClearColor(1f, 1f, 0f, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            glClearColor(0f, 0f, 0f, 1);
            glClear(GL_DEPTH_BUFFER_BIT);
            // this.parentPanel.repaint();
            if (this.getParentPanel() != null
                    && this.getParentPanel().getRenderingManager() != null) {
                this.getParentPanel().getRenderingManager().renderAll();
            }

            // System.err.println("-------------------------------------------------- swap buffers --------------------------------");

            if (this.doPaintOverlay()) {
                this.glPaintOverlays();
            }

            this.swapBuffers();
        } catch (LWJGLException e) {
            logger.error("Error rendering the LwJGL : " + e.getMessage());
            // e.printStackTrace();
        }

    }

    @Override
    public void componentResized(final ComponentEvent e) {
        super.componentResized(e);
        if (this.getParentPanel() == null) {
            return;
        }
        if (this.getSize().equals(this.getParentPanel().getSize())) {
            return;
        }
        // System.err.println("component resize to " + this.getSize() +
        // " in GLCanvas");
        try {
            this.makeCurrent();
        } catch (LWJGLException exception) {
            // if makeCurrent() throws an exception, then the canvas is not
            // ready
            return;
        }
        try {
            if (this.getContext() == null) {
                return;
            }
            this.setSize(this.getParentPanel().getSize());

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, this.getWidth(), this.getHeight(), 0, -1000, 1000);

            glMatrixMode(GL_MODELVIEW);
            glViewport(0, 0, this.getWidth(), this.getHeight());
        } catch (Exception e1) {
            // don't know hot to prevent/check this exception.
            // isDisplayable() and isValid() are both true at this point...
            logger.warn("Error resizing the heavyweight AWTGLCanvas : "
                    + e1.getMessage());
            // e1.printStackTrace();
        }
        this.repaint(); // super.componentResized(e);
    }

    /**
     * paint overlays in GL windows FIXME: No sync is done. We may check if the
     * window size changed during the rendering proces...
     */
    public void glPaintOverlays() {
        // create an image same as the GL window Size
        BufferedImage overlay = new BufferedImage(this.getWidth(),
                this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) overlay.getGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        this.getParentPanel().paintGeometryEdition(g);
        this.getParentPanel().paintOverlays(g);

        // paint the rendered image into GL window
        this.drawImage(overlay, 0, 0, this.getWidth(), this.getHeight());
    }

    // /**
    // * Convert the buffered image to a texture
    // */
    // private ByteBuffer convertImageData(BufferedImage bufferedImage) {
    // ByteBuffer imageBuffer;
    // WritableRaster raster;
    // BufferedImage texImage;
    //
    // ColorModel glAlphaColorModel = new
    // ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]
    // {
    // 8, 8, 8, 8 }, true, false,
    // Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
    //
    // raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
    // bufferedImage.getWidth(), bufferedImage.getHeight(), 4, null);
    // texImage = new BufferedImage(glAlphaColorModel, raster, true, new
    // Hashtable());
    //
    // // copy the source image into the produced image
    // Graphics g = texImage.getGraphics();
    // g.setColor(new Color(0f, 0f, 0f, 0f));
    // g.fillRect(0, 0, 256, 256);
    // g.drawImage(bufferedImage, 0, 0, null);
    //
    // // build a byte buffer from the temporary image
    // // that be used by OpenGL to produce a texture.
    // byte[] data = ((DataBufferByte)
    // texImage.getRaster().getDataBuffer()).getData();
    //
    // imageBuffer = ByteBuffer.allocateDirect(data.length);
    // imageBuffer.order(ByteOrder.nativeOrder());
    // imageBuffer.put(data, 0, data.length);
    // imageBuffer.flip();
    //
    // return imageBuffer;
    // }

    // /**
    // * Checker texture
    // */
    // private ByteBuffer generateCheckerTexture(int width, int height) {
    // byte[] imageBuffer = new byte[width * height * 4];
    // for (int y = 0; y < height; y++) {
    // for (int x = 0; x < width; x++) {
    // boolean cell = x < width / 2 && y < width / 2 || x > width / 2 && y >
    // width
    // / 2;
    // imageBuffer[(x + y * width) * 4 + 0] = (byte) (cell ? 255 : 0);
    // imageBuffer[(x + y * width) * 4 + 1] = (byte) (cell ? 255 : 0);
    // imageBuffer[(x + y * width) * 4 + 2] = (byte) (cell ? 50 : 150);
    // imageBuffer[(x + y * width) * 4 + 3] = (byte) (cell ? 150 : 50);
    // }
    // }
    //
    // return (ByteBuffer)
    // ByteBuffer.allocateDirect(imageBuffer.length).put(imageBuffer).rewind();
    // }

    public static ByteBuffer loadTexture(final BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0,
                image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth()
                * image.getHeight() * 4); // 4 for RGBA, 3 for RGB

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) (pixel >> 16 & 0xFF)); // Red component
                buffer.put((byte) (pixel >> 8 & 0xFF)); // Green component
                buffer.put((byte) (pixel & 0xFF)); // Blue component
                buffer.put((byte) (pixel >> 24 & 0xFF)); // Alpha component.
                                                         // Only for
                                                         // RGBA
            }
        }
        buffer.flip(); // FOR THE LOVE OF GOD DO NOT FORGET THIS

        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want

        return buffer;
    }

    // private static BufferedImage img = null;
    // static {
    // try {
    // img = ImageIO.read(new File("/home/turbet/apple_ex.png"));
    // JOptionPane.showMessageDialog(null, "img", "img",
    // JOptionPane.INFORMATION_MESSAGE, new ImageIcon(img));
    // } catch (IOException e) {
    // }
    // }

    /**
     * Draw an image into a GL window
     * 
     * @param texture
     * @param x
     * @param y
     * @param width
     * @param height
     */
    private void drawImage(final BufferedImage texture, final int x,
            final int y, final int width, final int height) {

        // System.err.println("<vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv>");
        // Thread.dumpStack();
        // System.err.println("<^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^>");
        // try {
        // Thread.sleep(2000);
        // } catch (InterruptedException e) {
        // logger.error(e.getMessage());
        // System.exit(1);
        // }

        // Enable 2D
        glEnable(GL_TEXTURE_2D);
        int texId = GL11.glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);

        // final int texSize = 32;
        // ByteBuffer textureBuffer = generateCheckerTexture(texSize, texSize);
        // System.err.println("texture buffer = " + textureBuffer);
        // glTexImage2D(GL_TEXTURE_2D, 0, 4, texSize, texSize, 0, GL_RGBA,
        // GL_UNSIGNED_BYTE, textureBuffer);

        // ByteBuffer buf = loadTexture(img);
        // buf.rewind();
        // glTexImage2D(GL_TEXTURE_2D, 0, 4, img.getWidth(), img.getHeight(), 0,
        // GL_RGBA, GL_UNSIGNED_BYTE, buf);

        ByteBuffer buf = loadTexture(texture);
        buf.rewind();
        glTexImage2D(GL_TEXTURE_2D, 0, 4, texture.getWidth(),
                texture.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Begin Drawing Quads
        glBegin(GL_QUADS);

        // Top-left vertex (corner)
        glTexCoord2f(0f, 0f);
        glVertex2i(x, y);

        // Bottom-left vertex (corner)
        glTexCoord2f(1f, 0f);
        glVertex2i(x + width, y);

        // Bottom-right vertex (corner)
        glTexCoord2f(1f, 1f);
        glVertex2i(x + width, y + height);

        // Top-right vertex (corner)
        glTexCoord2f(0f, 1f);
        glVertex2i(x, y + height);

        glEnd();
        glDisable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);

    }

}
