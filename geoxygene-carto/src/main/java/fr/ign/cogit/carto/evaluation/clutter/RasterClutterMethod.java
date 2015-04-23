/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.carto.evaluation.clutter;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.segmentation.FelzenszwalbHuttenlocherSegmenter;
import org.openimaj.image.segmentation.SegmentationUtilities;

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
   * 
   * @return
   */
  public double getEdgeDensityClutter() {
    return computeEdgeDensityClutter(image);
  }

  /**
   * Compute the edge density clutter in a grid version of the image, where the
   * number of columns of the grid is given (the size of the cells is computed
   * according to the image size).
   * 
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
    // first compute the RenderedOp that computes both vertical and
    // horizontal
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

  /**
   * Compute the Bravo & Farid (2008) scale invariant clutter, based on the
   * segmentation technique from Felzenszwalb and Huttenlocher (2004). Once the
   * segmentation is computed, the clutter is the number of segmented objects.
   * @param k is the segmentation threshold (default 500)
   * @param minSize is the minimum component size enforced by post-processing
   *          (default 20)
   * @param sigma Used to smooth the input image before segmenting it (default
   *          0.5)
   * @return
   */
  public int getBravoFaridClutter(float k, int minSize, float sigma) {
    FImage fImage = ImageUtilities.createFImage(image.getAsBufferedImage());
    FelzenszwalbHuttenlocherSegmenter<FImage> segmenter = new FelzenszwalbHuttenlocherSegmenter<>(
        sigma, k, minSize);
    List<ConnectedComponent> components = segmenter.segment(fImage);
    return components.size();
  }

  /**
   * Compute the Bravo & Farid (2008) scale invariant clutter (see
   * getBravoFaridClutter()) and exports the segmented image as a file.
   * @param k
   * @param minSize
   * @param sigma
   * @param output the file in which the image is written.
   * @return
   * @throws IOException
   */
  public int getAndExportBravoFaridClutter(float k, int minSize, float sigma,
      File output) throws IOException {
    MBFImage fImage = ImageUtilities.createMBFImage(image.getAsBufferedImage(),
        true);
    FelzenszwalbHuttenlocherSegmenter<MBFImage> segmenter = new FelzenszwalbHuttenlocherSegmenter<>(
        sigma, k, minSize);
    List<ConnectedComponent> components = segmenter.segment(fImage);
    MBFImage segImage = SegmentationUtilities
        .renderSegments(fImage, components);
    ImageUtilities.write(segImage, output);
    return components.size();
  }
}
