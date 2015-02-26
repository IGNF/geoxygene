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
package fr.ign.cogit.geoxygene.appli.validation;

import java.awt.Color;

import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.StrokeExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.interpolation.InterpolationSymbolizerInterface;
import fr.ign.cogit.geoxygene.util.math.Interpolation;

/**
 * @author Nicolas Mellado
 */
public abstract class SymbolizerValidator {
  /**
   * @brief Exception thrown by a SymbolizerValidator when a {@link Symbolizer}
   *        is not valid
   */
  public class InvalidSymbolizerException extends Exception {
    private static final long serialVersionUID = -8288455971644828523L;

    public InvalidSymbolizerException(String message) {
      super(message);
    }
  }

  /**
   * @brief Validate a {@link Symbolizer}, and eventually modify it when needed.
   * @param s Input {@link Symbolizer}
   * @return if s has been modified during validation
   * @throws InvalidSymbolizerException If the {@link Symbolizer} cannot be
   *           validated
   */
  public abstract boolean validate(InterpolationSymbolizerInterface s)
      throws InvalidSymbolizerException;

  protected Stroke interpolate(Stroke subStroke1, Stroke subStroke2,
      double alpha, Interpolation.Functor interFun) {
    Color outStroke = null;
    float outOpacity = (float) -1.;
    float outWidth = (float) -1.;
    StrokeExpressiveRenderingDescriptor outExprRendDescr = null;

    Stroke out = new Stroke();

    if (subStroke1 == null) {
      if (subStroke2 == null)
        return null;
      outStroke = subStroke2.getColor();
      outOpacity = (float) (subStroke2.getStrokeOpacity() * (1.0 - alpha));
      outWidth = subStroke2.getStrokeWidth();
      outExprRendDescr = subStroke2.getExpressiveRendering();
    } else if (subStroke2 == null) {
      outStroke = subStroke1.getColor();
      outOpacity = (float) (subStroke1.getStrokeOpacity() * alpha);
      outWidth = subStroke1.getStrokeWidth();
      outExprRendDescr = subStroke1.getExpressiveRendering();
    } else {

      // HACK: need to call getColor and not getStroke to update internal
      // transient fields according to the css properties
      outStroke = Interpolation.interpolateRGB(subStroke1.getColor(),
          subStroke2.getColor(), alpha, interFun);
      outOpacity = (float) Interpolation.interpolate(
          subStroke1.getStrokeOpacity(), subStroke2.getStrokeOpacity(), alpha,
          interFun);
      outWidth = (float) Interpolation.interpolate(subStroke1.getStrokeWidth(),
          subStroke2.getStrokeWidth(), alpha, interFun);
      outExprRendDescr = this.interpolate(subStroke1.getExpressiveRendering(),
          subStroke2.getExpressiveRendering(), alpha, interFun);
    }

    if (outStroke != null)
      out.setStroke(outStroke);
    if (outOpacity >= 0)
      out.setStrokeOpacity(outOpacity);
    if (outWidth >= 0)
      out.setStrokeWidth(outWidth);
    if (outExprRendDescr != null)
      out.setExpressiveRendering(outExprRendDescr);

    // update non-continuous parameters
    if (alpha < 0.5f) {
      if (subStroke1 != null) {
        out.setStrokeLineCap(subStroke1.getStrokeLineCap());
        out.setStrokeLineJoin(subStroke1.getStrokeLineJoin());
      }
    } else {
      if (subStroke2 != null) {
        out.setStrokeLineCap(subStroke2.getStrokeLineCap());
        out.setStrokeLineJoin(subStroke2.getStrokeLineJoin());
      }
    }

    return out;
  }

  protected StrokeExpressiveRenderingDescriptor interpolate(
      StrokeExpressiveRenderingDescriptor descr1,
      StrokeExpressiveRenderingDescriptor descr2, double alpha,
      Interpolation.Functor interFun) {
    if (descr1 == null || descr2 == null)
      return null;

    Class<?> c1 = descr1.getClass();
    Class<?> c2 = descr2.getClass();

    if (c1 != c2)
      return null;
    if (c1 == BasicTextureExpressiveRenderingDescriptor.class)
      return this.interpolateBasicTextureExpressive(
          (BasicTextureExpressiveRenderingDescriptor) descr1,
          (BasicTextureExpressiveRenderingDescriptor) descr2, alpha, interFun);

    return null;
  }

  private BasicTextureExpressiveRenderingDescriptor interpolateBasicTextureExpressive(
      BasicTextureExpressiveRenderingDescriptor descr1,
      BasicTextureExpressiveRenderingDescriptor descr2, double alpha,
      Interpolation.Functor interFun) {
    if (descr1.getShaderDescriptor().getClass() != descr2.getShaderDescriptor()
        .getClass())
      return null;

    // TODO Add texture interpolation here
    if (descr1.getPaperTextureFilename().compareTo(
        descr2.getPaperTextureFilename()) != 0)
      return null;
    if (descr1.getBrushTextureFilename().compareTo(
        descr2.getBrushTextureFilename()) != 0)
      return null;

    BasicTextureExpressiveRenderingDescriptor out = new BasicTextureExpressiveRenderingDescriptor();

    out.setTransitionSize(Interpolation.interpolate(descr1.getTransitionSize(),
        descr2.getTransitionSize(), alpha, interFun));
    out.setBrushStartLength((int) Interpolation.interpolate(
        descr1.getBrushStartLength(), descr2.getBrushStartLength(), alpha,
        interFun));
    out.setBrushEndLength((int) Interpolation.interpolate(
        descr1.getBrushEndLength(), descr2.getBrushEndLength(), alpha, interFun));
    out.setBrushAspectRatio(Interpolation.interpolate(
        descr1.getBrushAspectRatio(), descr2.getBrushAspectRatio(), alpha,
        interFun));
    out.setPaperSizeInCm(Interpolation.interpolate(descr1.getPaperSizeInCm(),
        descr2.getPaperSizeInCm(), alpha, interFun));
    out.setPaperReferenceMapScale(Interpolation.interpolate(
        descr1.getPaperReferenceMapScale(), descr2.getPaperReferenceMapScale(),
        alpha, interFun));
    out.setPaperDensity(Interpolation.interpolate(descr1.getPaperDensity(),
        descr2.getPaperDensity(), alpha, interFun));
    out.setBrushDensity(Interpolation.interpolate(descr1.getBrushDensity(),
        descr2.getBrushDensity(), alpha, interFun));
    out.setStrokePressure(Interpolation.interpolate(descr1.getStrokePressure(),
        descr2.getStrokePressure(), alpha, interFun));
    out.setSharpness(Interpolation.interpolate(descr1.getSharpness(),
        descr2.getSharpness(), alpha, interFun));

    return out;
  }

  protected Fill interpolate(Fill subFill1, Fill subFill2, double alpha,
      Interpolation.Functor interFun) {
    // interpolated parameters
    Color outColor = null;
    float outOpacity = (float) -1.0;

    Fill out = new Fill();

    if (subFill1 == null) {
      if (subFill2 == null)
        return null;
      outColor = subFill2.getColor();
      outOpacity = (float) (subFill2.getFillOpacity() * (1.0 - alpha));
    } else if (subFill2 == null) {
      outColor = subFill1.getColor();
      outOpacity = (float) (subFill1.getFillOpacity() * alpha);
    } else {
      outColor = Interpolation.interpolateRGB(subFill1.getColor(),
          subFill2.getColor(), alpha, interFun);
      outOpacity = (float) Interpolation.interpolate(subFill1.getFillOpacity(),
          subFill2.getFillOpacity(), alpha, interFun);
    }

    if (outColor != null)
      out.setColor(outColor);
    if (outOpacity >= 0)
      out.setFillOpacity(outOpacity);

    return out;
  }
}
