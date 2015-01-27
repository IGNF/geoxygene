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

import fr.ign.cogit.geoxygene.style.LineInterpolationSymbolizer;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.math.Interpolation;

/**
 * @author Nicolas Mellado
 *
 */
public class LineInterpolationSymbolizerValidator implements
        SymbolizerValidator {

    private static Interpolation.Functor interFun = Interpolation.linearFunctor;

    @Override
    public boolean validate(Symbolizer s) throws InvalidSymbolizerException {
        System.out.println("Validating LineInterpolationSymbolizer");

        if (s == null)
            throw new InvalidSymbolizerException("null Symbolizer ");

        LineInterpolationSymbolizer symbolizer = (LineInterpolationSymbolizer) s;

        boolean updated = false;

        updated = this.updateStroke(symbolizer) || updated;

        return updated;
    }

    private boolean updateStroke(LineInterpolationSymbolizer s) {
        double alpha = s.getAlpha();

        // update stroke
        Stroke subStroke1 = s.getFirstSymbolizer().getStroke();
        Stroke subStroke2 = s.getSecondSymbolizer().getStroke();

        // HACK: need to call getColor and not getStroke to update internal
        // transient fields according to the css properties

        s.getStroke().setStroke(
                Interpolation.interpolateRGB(subStroke1.getColor(),
                        subStroke2.getColor(), alpha, interFun));
        s.getStroke().setStrokeOpacity(
                (float) Interpolation.interpolate(
                        subStroke1.getStrokeOpacity(),
                        subStroke2.getStrokeOpacity(), alpha, interFun));
        s.getStroke().setStrokeWidth(
                (float) Interpolation.interpolate(subStroke1.getStrokeWidth(),
                        subStroke2.getStrokeWidth(), alpha, interFun));

        return true;
    }

}
