package fr.ign.cogit.carto.evaluation.clutter;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.HashMap;
import java.util.Map;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;

public class RasterClutterMethod {

  private TiledImage image;

  public RasterClutterMethod(TiledImage image) {
    super();
    this.image = image;
  }

  public TiledImage getImage() {
    return image;
  }

  public void setImage(TiledImage image) {
    this.image = image;
  }

  /**
   * According to (Rosenhotlz et al 2007), a simple edge density gives a good
   * global evaluation of an image clutter. The edge detection method is the
   * default JAI one, i.e. Sobel gradient magnitude.
   * @return
   */
  public double getEdgeDensityClutter() {
    return computeEdgeDensityClutter(image);
  }

  /**
   * Compute the edge density clutter in a grid version of the image, where the
   * number of columns of the grid is given (the size of the cells is computed
   * according to the image size).
   * @param nbColumns
   * @return
   */
  public Map<Integer, Map<Integer, Double>> getGridEdgeDensityClutter(
      int nbColumns) {
    // first compute the number of pixels in a grid cell
    int cellSize = image.getWidth() / nbColumns;
    int nbRows = Math.round(image.getHeight() / cellSize);
    Map<Integer, Map<Integer, Double>> clutters = new HashMap<Integer, Map<Integer, Double>>();
    int y = 0;
    for (int i = 0; i < nbRows; i++) {
      int x = 0;
      Map<Integer, Double> columns = new HashMap<Integer, Double>();
      for (int j = 0; j < nbColumns; j++) {
        // compute the sub-image in the i,j cell
        TiledImage subImage = image.getSubImage(x, y, cellSize, cellSize);
        columns.put(j, computeEdgeDensityClutter(subImage));
        x += cellSize;
      }
      clutters.put(i, columns);
      y += cellSize;
    }

    return clutters;
  }

  private double computeEdgeDensityClutter(PlanarImage image) {
    // first compute the RenderedOp that computes both vertical and horizontal
    // edge detection
    KernelJAI sobelVertKernel = KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;
    KernelJAI sobelHorizKernel = KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL;
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(image);
    pb.add(sobelHorizKernel);
    pb.add(sobelVertKernel);
    RenderedOp renderedOp = JAI.create("gradientmagnitude", pb);
    BufferedImage edgeImage = renderedOp.getAsBufferedImage();

    // then compute a density value, i.e. the mean
    int edgeTotal = 0;
    for (int i = 0; i < edgeImage.getWidth(); i++)
      for (int j = 0; j < edgeImage.getHeight(); j++)
        edgeTotal += Math.abs(edgeImage.getRGB(i, j));

    return Math.abs(edgeTotal / (edgeImage.getWidth() * edgeImage.getHeight()));
  }
}
