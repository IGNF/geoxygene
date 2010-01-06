/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.datatools.conversion;

import java.sql.SQLException;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.postgis.PGgeometry;

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * Classe de test. Elle n'est pas utilisée pour l'instant.
 * @author Julien Perret
 *
 */
public class GeOxygene2PostgisFieldConversion implements FieldConversion {
    /**
     * serial uid.
     */
    private static final long serialVersionUID = 1L;

    public Object javaToSql(Object source) throws ConversionException {
        if (source instanceof GM_Object) {
            try {
                PGgeometry pgGeom
                = new PGgeometry(((GM_Object) source).toString());
                return pgGeom;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Object sqlToJava(Object source) throws ConversionException {
        if (source instanceof PGgeometry) {
            PGgeometry geom = (PGgeometry) source;
            try {
                return WktGeOxygene.makeGeOxygene(geom.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
