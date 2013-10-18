package fr.ign.cogit.geoxygene.appli.layer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2i;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * LayerViewGLPanel is the basic implementation of a GL Viewer.
 * It adds a glass Pane over the GL window in order to draw Swing drawings over
 * the GL view.
 * !! Do not add children to this element, use setGLComponent() only !! 
 * @author turbet
 *
 */
public abstract class LayerViewGLPanel extends LayerViewPanel {

  private static final long serialVersionUID = -7181604491025859187L; // serializable UID
  //  private static final int GLASS_LAYER_INDEX = 10; // layer index on which the overlay Swing stuff will be drawn
  //  private static final int GL_LAYER_INDEX = 1; // layer index on which the GL stuff will be rendered
  private static Logger logger = Logger.getLogger(LayerViewGLPanel.class.getName());

  //  private final JLayeredPane layeredPane = null;
  //  private final JPanel glPanel = null;
  //  private final Component glassPanel = null;

  /**
   * 
   * @param frame
   */
  public LayerViewGLPanel(final ProjectFrame frame) {
    super(frame);

    this.setLayout(new BorderLayout());
    //    this.layeredPane = new JLayeredPane();
    //    this.add(this.layeredPane, BorderLayout.CENTER);

    //    this.glassPanel = new Component() {
    //      @Override
    //      public void paint(Graphics g) {
    //        super.paint(g);
    //        //        g.drawLine(10, 10, this.getWidth() - 10, this.getHeight() - 10);
    //        LayerViewGLPanel.this.overlayPaint(g);
    //      }
    //    };
    //    //    this.glassPanel.setOpaque(false);
    //    this.glassPanel.setBackground(new Color(0, 0, 0, 0));

    //    this.glPanel = new JPanel();
    //    this.glPanel.setOpaque(false);
    //    this.glPanel.setLayout(new BorderLayout());
    //    this.glPanel.setBounds(0, 0, 800, 800);
    //    glassCanvas.setOpaque(false);

    //    this.layeredPane.add(this.glPanel, JLayeredPane.DEFAULT_LAYER, 1);
    //    this.layeredPane.add(this.glassPanel, JLayeredPane.DEFAULT_LAYER, 0);

    //    this.addComponentListener(this);
  }

  //  /**
  //   * paint method drawing into an AWT canvas over the GL window
  //   * @param g graphics to draw into
  //   */
  //  public void overlayPaint(Graphics g) {
  //    this.paintGeometryEdition(g);
  //    this.paintOverlays(g);
  //  }

  /**
   * Repaint the panel using the repaint method of the super class {@link JPanel}. Called in order to perform the progressive rendering.
   * 
   * @see #paintComponent(Graphics)
   */
  @Override
  public final void superRepaint() {
    Container parent = this.getParent();
    if (parent != null) {
      parent.repaint();
    }
  }

  /** Dispose of the panel and its rendering manager. */
  @Override
  public void dispose() {
    if (this.getRenderingManager() != null) {
      this.getRenderingManager().dispose();
    }
    this.setViewport(null);
    //    this.glPanel.setVisible(false);
    // TODO: properly close GL stuff
  }

  /** Evenements SLD */
  @Override
  public void actionPerformed(final ActionEvent e) {
    this.repaint();
  }

  /**
   * Set the child Component where GL will be rendered
   * @param glComponent
   */
  public void setGLComponent(final Component glComponent) {
    //    this.add(glComponent, BorderLayout.CENTER);
    this.removeAll();
    this.add(glComponent, BorderLayout.CENTER);
    //    glComponent.setBounds(0, 0, 800, 800);

  }

  @Override
  public synchronized void layerAdded(final Layer l) {
    if (this.getRenderingManager() != null) {
      this.getRenderingManager().addLayer(l);
    }
    try {
      IEnvelope env = l.getFeatureCollection().getEnvelope();
      if (env == null) {
        env = l.getFeatureCollection().envelope();
      }
      this.getViewport().zoom(env);
    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public void layerOrderChanged(final int oldIndex, final int newIndex) {
    this.repaint();
  }

  @Override
  public void layersRemoved(final Collection<Layer> layers) {
    this.repaint();
  }

  @Override
  public int print(final Graphics arg0, final PageFormat arg1, final int arg2) throws PrinterException {
    logger.error("LayerViewGlPanel::print(...) not implemented yet");
    return 0;
  }

  @Override
  public IEnvelope getEnvelope() {
    if (this.getRenderingManager().getLayers().isEmpty()) {
      return null;
    }
    List<Layer> copy = new ArrayList<Layer>(this.getRenderingManager().getLayers());
    Iterator<Layer> layerIterator = copy.iterator();
    IEnvelope envelope = layerIterator.next().getFeatureCollection().envelope();
    while (layerIterator.hasNext()) {
      IFeatureCollection<? extends IFeature> collection = layerIterator.next().getFeatureCollection();
      if (collection != null) {
        IEnvelope env = collection.getEnvelope();
        if (envelope == null) {
          envelope = env;
        } else {
          envelope.expand(env);
        }
      }
    }
    return envelope;
  }

  @Override
  public void saveAsImage(final String fileName) {
    logger.error("LayerViewGLPanel::saveAsImage(...) not implemented yet");

  }

  /****************************************************************
   * 
   * 
   *    less or more GL Stuff (should be reorganized)
   * 
   * 
   ***************************************************************/

  /**
   * paint overlays in GL windows
   * FIXME: No sync is done. We may check if the window size changed during the rendering proces...
   */
  public void glPaintOverlays() {
    // create an image same as the GL window Size
    BufferedImage overlay = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g = (Graphics2D) overlay.getGraphics();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
    g.fillRect(0, 0, this.getWidth(), this.getHeight());
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    this.paintGeometryEdition(g);
    this.paintOverlays(g);

    // paint the rendered image into GL window
    this.drawImage(overlay, 0, 0, this.getWidth(), this.getHeight());
  }

  //  /**
  //   * Convert the buffered image to a texture
  //   */
  //  private ByteBuffer convertImageData(BufferedImage bufferedImage) {
  //    ByteBuffer imageBuffer;
  //    WritableRaster raster;
  //    BufferedImage texImage;
  //
  //    ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 }, true, false,
  //        Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
  //
  //    raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, bufferedImage.getWidth(), bufferedImage.getHeight(), 4, null);
  //    texImage = new BufferedImage(glAlphaColorModel, raster, true, new Hashtable());
  //
  //    // copy the source image into the produced image
  //    Graphics g = texImage.getGraphics();
  //    g.setColor(new Color(0f, 0f, 0f, 0f));
  //    g.fillRect(0, 0, 256, 256);
  //    g.drawImage(bufferedImage, 0, 0, null);
  //
  //    // build a byte buffer from the temporary image
  //    // that be used by OpenGL to produce a texture.
  //    byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();
  //
  //    imageBuffer = ByteBuffer.allocateDirect(data.length);
  //    imageBuffer.order(ByteOrder.nativeOrder());
  //    imageBuffer.put(data, 0, data.length);
  //    imageBuffer.flip();
  //
  //    return imageBuffer;
  //  }

  //  /**
  //   * Checker texture
  //   */
  //  private ByteBuffer generateCheckerTexture(int width, int height) {
  //    byte[] imageBuffer = new byte[width * height * 4];
  //    for (int y = 0; y < height; y++) {
  //      for (int x = 0; x < width; x++) {
  //        boolean cell = x < width / 2 && y < width / 2 || x > width / 2 && y > width / 2;
  //        imageBuffer[(x + y * width) * 4 + 0] = (byte) (cell ? 255 : 0);
  //        imageBuffer[(x + y * width) * 4 + 1] = (byte) (cell ? 255 : 0);
  //        imageBuffer[(x + y * width) * 4 + 2] = (byte) (cell ? 50 : 150);
  //        imageBuffer[(x + y * width) * 4 + 3] = (byte) (cell ? 150 : 50);
  //      }
  //    }
  //
  //    return (ByteBuffer) ByteBuffer.allocateDirect(imageBuffer.length).put(imageBuffer).rewind();
  //  }

  public static ByteBuffer loadTexture(final BufferedImage image) {

    int[] pixels = new int[image.getWidth() * image.getHeight()];
    image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

    ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int pixel = pixels[y * image.getWidth() + x];
        buffer.put((byte) (pixel >> 16 & 0xFF)); // Red component
        buffer.put((byte) (pixel >> 8 & 0xFF)); // Green component
        buffer.put((byte) (pixel & 0xFF)); // Blue component
        buffer.put((byte) (pixel >> 24 & 0xFF)); // Alpha component. Only for RGBA
      }
    }
    buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

    // You now have a ByteBuffer filled with the color data of each pixel.
    // Now just create a texture ID and bind it. Then you can load it using 
    // whatever OpenGL method you want, for example:

    return buffer;
  }

  //  private static BufferedImage img = null;
  //  static {
  //    try {
  //      img = ImageIO.read(new File("/home/turbet/apple_ex.png"));
  //      JOptionPane.showMessageDialog(null, "img", "img", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(img));
  //    } catch (IOException e) {
  //    }
  //  }

  /**
   * Draw an image into a GL window
   * @param texture
   * @param x
   * @param y
   * @param width
   * @param height
   */
  private void drawImage(final BufferedImage texture, final int x, final int y, final int width, final int height) {

    //System.err.println("<vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv>");
    //    Thread.dumpStack();
    //    System.err.println("<^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^>");
    //    try {
    //      Thread.sleep(2000);
    //    } catch (InterruptedException e) {
    //      logger.error(e.getMessage());
    //      System.exit(1);
    //    }

    //Enable 2D
    glEnable(GL_TEXTURE_2D);
    int texId = GL11.glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texId);

    //    final int texSize = 32;
    //    ByteBuffer textureBuffer = generateCheckerTexture(texSize, texSize);
    //    System.err.println("texture buffer = " + textureBuffer);
    //    glTexImage2D(GL_TEXTURE_2D, 0, 4, texSize, texSize, 0, GL_RGBA, GL_UNSIGNED_BYTE, textureBuffer);

    //    ByteBuffer buf = loadTexture(img);
    //    buf.rewind();
    //    glTexImage2D(GL_TEXTURE_2D, 0, 4, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

    ByteBuffer buf = loadTexture(texture);
    buf.rewind();
    glTexImage2D(GL_TEXTURE_2D, 0, 4, texture.getWidth(), texture.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    //Begin Drawing Quads
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