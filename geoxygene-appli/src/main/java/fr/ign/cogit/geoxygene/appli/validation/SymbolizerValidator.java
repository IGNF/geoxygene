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
    
    
    protected boolean updateStroke(Stroke subStroke1, Stroke subStroke2, 
                                   double alpha, Interpolation.Functor interFun,
                                   Stroke out) {
      

      // HACK: need to call getColor and not getStroke to update internal
      // transient fields according to the css properties
      
      out.setStroke(
              Interpolation.interpolateRGB(subStroke1.getColor(),
                      subStroke2.getColor(), alpha, interFun));
      out.setStrokeOpacity(
              (float) Interpolation.interpolate(
                      subStroke1.getStrokeOpacity(),
                      subStroke2.getStrokeOpacity(), alpha, interFun));
      out.setStrokeWidth(
              (float) Interpolation.interpolate(subStroke1.getStrokeWidth(),
                      subStroke2.getStrokeWidth(), alpha, interFun));

      return true;
  }
    

    
    protected boolean updateFill(Fill subFill1, Fill subFill2, 
        double alpha, Interpolation.Functor interFun,
        Fill out) {
      out.setColor(
          Interpolation.interpolateRGB(subFill1.getColor(),
              subFill2.getColor(), alpha, interFun));
      out.setFillOpacity(
          (float) Interpolation.interpolate(subFill1.getFillOpacity(),
              subFill2.getFillOpacity(), alpha, interFun));

      return true;
    }
}
