/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut G�ographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut G�ographique National
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
 */
package fr.ign.cogit.geoxygene.feature;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.processing.CoverageProcessor;
import org.geotools.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.ImageUtil;

/**
 * 
 * FT_Coverage is used to store image data (i.e. geotiffs). It stores the image
 * itself as a Geotools GridCoverage and the envelope of this image. This class
 * is intended to be a temporary class until the 2dCoverages are fully managed.
 * 
 * @see GridCoverage2D
 * @author Bertrand Dumenieu
 * 
 */
public class FT_Coverage extends AbstractFeature {

  private GridCoverage2D coverage;
  private GM_Envelope box;

  public FT_Coverage(GridCoverage2D coverage) {
    this.setCoverage(coverage);
    Envelope envelope = coverage.getEnvelope();
    this.setBox(new GM_Envelope(
        new DirectPosition(envelope.getUpperCorner().getCoordinate()),
        new DirectPosition(envelope.getLowerCorner().getCoordinate())));
  }

  public GridCoverage2D coverage() {
    return this.coverage;

  }

  public void setCoverage(GridCoverage2D _coverage) {
    this.coverage = _coverage;
    Rectangle2D rec = coverage.getEnvelope2D().getBounds2D();
    this.geom = new GM_Envelope(rec.getMinX(), rec.getMaxX(), rec.getMinY(),
        rec.getMaxY()).getGeom();
  }

  public void setBox(GM_Envelope box) {
    this.box = box;
  }

  public GM_Envelope getBox() {
    return box;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

  public FT_Coverage cropEnvelope(Envelope env) {
    GridCoverage2D coverage = this.coverage();
    final CoverageProcessor processor = new CoverageProcessor();
    final ParameterValueGroup param = processor.getOperation("CoverageCrop")
        .getParameters();

    final GeneralEnvelope cropEnv = new GeneralEnvelope(env);
    param.parameter("Source").setValue(coverage);
    param.parameter("Envelope").setValue(cropEnv);
    GridCoverage2D cropped = (GridCoverage2D) processor.doOperation(param);
    return new FT_Coverage(cropped);

  }

  public FT_Coverage crop(IGeometry geometry) {

    GridCoverage2D coverage = this.coverage();
    Geometry geom = null;
    try {
      geom = AdapterFactory.toGeometry(new GeometryFactory(), geometry);
    } catch (Exception e) {
      e.printStackTrace();
    }

    final CoverageProcessor processor = new CoverageProcessor();
    final ParameterValueGroup param = processor.getOperation("CoverageCrop")
        .getParameters();
    param.parameter("Source").setValue(coverage);
    param.parameter("ROI").setValue(geom);
    GridCoverage2D cropped = (GridCoverage2D) processor.doOperation(param);

    // FIXME PB de thread asynchrone : le fond est rendu transparent avant que
    // l'image ne soit croppée.
    // FIXME On parcourt l'image pour rendre transparent la partie coupée
    // Ce serait mieux de prévoir que l'image soit transparente à la base ...
    BufferedImage img = ImageUtil.toBufferedImage(cropped.getRenderedImage());

    for (int i = 0; i < img.getWidth(); i++) {
      for (int j = 0; j < img.getHeight(); j++) {
        Color c = new Color(img.getRGB(i, j));
        if (c == Color.BLACK) {
          Color color = new Color(0, 0, 0, 0);
          img.setRGB(i, j, color.getRGB());
        }
      }
    }
    GridCoverage2D cropped_transp = ImageUtil.bufferedImageToGridCoverage2D(img,
        cropped.getEnvelope());
    return new FT_Coverage(cropped_transp);

  }
}
