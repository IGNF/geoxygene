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
import fr.ign.cogit.geoxygene.style.InterpolationSymbolizerInterface;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.util.math.Interpolation;

/**
 * @author Nicolas Mellado
 */
public abstract class SymbolizerValidator {
    /**
     * @brief Exception thrown by a SymbolizerValidator when a
     *        {@link Symbolizer} is not valid
     */
    public class InvalidSymbolizerException extends Exception {
        private static final long serialVersionUID = -8288455971644828523L;

        public InvalidSymbolizerException(String message) {
            super(message);
        }
    }

    /**
     * @brief Validate a {@link Symbolizer}, and eventually modify it when
     *        needed.
     * @param s
     *            Input {@link Symbolizer}
     * @return if s has been modified during validation
     * @throws InvalidSymbolizerException
     *             If the {@link Symbolizer} cannot be validated
     */
    public abstract boolean validate(InterpolationSymbolizerInterface s) throws InvalidSymbolizerException;
    
    
    protected Stroke interpolate(Stroke subStroke1, Stroke subStroke2, 
                                   double alpha, Interpolation.Functor interFun) {
      Color outStroke  = null;
      float outOpacity = (float) -1.;
      float outWidth   = (float) -1.;
      
      Stroke out = new Stroke();
      
      if (subStroke1 == null ){
        if (subStroke2 == null)
          return null;
        outStroke  = subStroke2.getColor();
        outOpacity = subStroke2.getStrokeOpacity();
        outWidth   = subStroke2.getStrokeWidth();
      }else if (subStroke2 == null){
        outStroke  = subStroke1.getColor();
        outOpacity = subStroke1.getStrokeOpacity();
        outWidth   = subStroke1.getStrokeWidth();
      }else {

        // HACK: need to call getColor and not getStroke to update internal
        // transient fields according to the css properties
        outStroke =
            Interpolation.interpolateRGB(subStroke1.getColor(),
                subStroke2.getColor(), alpha, interFun);
        outOpacity =
            (float) Interpolation.interpolate(
                subStroke1.getStrokeOpacity(),
                subStroke2.getStrokeOpacity(), alpha, interFun);
        outWidth = 
            (float) Interpolation.interpolate(subStroke1.getStrokeWidth(),
                subStroke2.getStrokeWidth(), alpha, interFun);
      }

      if (outStroke  != null) out.setStroke(outStroke);
      if (outOpacity >= 0)    out.setStrokeOpacity(outOpacity);
      if (outWidth >= 0)      out.setStrokeWidth(outWidth);
      
      // update non-continuous parameters
      if (alpha < 0.5f){
        out.setStrokeLineCap(subStroke1.getStrokeLineCap());
        out.setStrokeLineJoin(subStroke1.getStrokeLineJoin());
      }else {
        out.setStrokeLineCap(subStroke2.getStrokeLineCap());
        out.setStrokeLineJoin(subStroke2.getStrokeLineJoin());
      }
      
      return out;
    }


    
    protected Fill interpolate(Fill subFill1, Fill subFill2, 
        double alpha, Interpolation.Functor interFun) {
      // interpolated parameters
      Color outColor = null;
      float outOpacity = (float) -1.0;
      
      Fill out = new Fill();
      
      if (subFill1 == null){
        if (subFill2 == null)
          return null;
        outColor   = subFill2.getColor();
        outOpacity = subFill2.getFillOpacity();
      }else if (subFill2 == null){
        outColor   = subFill1.getColor();
        outOpacity = subFill1.getFillOpacity();
      }else {
        outColor = Interpolation.interpolateRGB(subFill1.getColor(),
            subFill2.getColor(), alpha, interFun);
        outOpacity = (float) Interpolation.interpolate(subFill1.getFillOpacity(),
            subFill2.getFillOpacity(), alpha, interFun);
      }

      if (outColor  != null) out.setColor(outColor);
      if (outOpacity >= 0)   out.setFillOpacity(outOpacity);
      
      return out;
    }
}
