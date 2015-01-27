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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.LineInterpolationSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonInterpolationSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;

public class SymbolizerValidatorFactory {
    private static final Logger logger = Logger
            .getLogger(SymbolizerValidatorFactory.class.getName()); // logger

    /**
     * @brief Describes the link between {@link Symbolizer} and
     *        {@link SymbolizerValidator}.
     */
    private static final Map<String, String> validatorNames;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(PolygonInterpolationSymbolizer.class.getName(),
            PolygonInterpolationSymbolizerValidator.class.getName());
        aMap.put(LineInterpolationSymbolizer.class.getName(),
            LineInterpolationSymbolizerValidator.class.getName());

        validatorNames = Collections.unmodifiableMap(aMap);
    }

    /**
     * @brief Stores {@link SymbolizerValidator} previously instantiated for
     *        later use
     */
    private static Map<String, SymbolizerValidator> validatorCache = new HashMap<String, SymbolizerValidator>();

    /**
     * 
     * @param s
     * @return null if no {@link SymbolizerValidator} can be associated to the
     *         input {@link Symbolizer}
     */
    public static SymbolizerValidator getOrCreateValidator(Symbolizer s) {
        String sname = s.getClass().getName();
        SymbolizerValidator validator = null;

        if (!validatorNames.containsKey(sname))
            return null;

        synchronized (validatorCache) {
            if (validatorCache.containsKey(sname))
                return validatorCache.get(sname);

            try {
                validator = (SymbolizerValidator) Class.forName(
                        validatorNames.get(sname)).newInstance();
                validatorCache.put(sname, validator);
                return validator;
                // do not use multi-catch parameters to be compatible to java
                // versions < 1.7:
            } // catch (InstantiationException | IllegalAccessException
              // | ClassNotFoundException e) {
            catch (InstantiationException e) {
                logger.error(e.getStackTrace().toString());
                return null;
            } catch (IllegalAccessException e) {
                logger.error(e.getStackTrace().toString());
                return null;
            } catch (ClassNotFoundException e) {
                logger.error(e.getStackTrace().toString());
                return null;
            }
        }
    }
}
