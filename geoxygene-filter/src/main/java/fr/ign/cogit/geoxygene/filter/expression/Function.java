/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.filter.expression;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import fr.ign.cogit.geoxygene.filter.function.FunctionImpl;

/**
 * @author Julien Perret
 */
@XmlRootElement(name = "Function")
@XmlSeeAlso({ FunctionImpl.class })
public abstract class Function extends Expression {
  /**
   * @return the name of the function
   */
  public abstract String getName();

  /**
   * @return the fallback (default) value returned by the function, especially
   *         used if the function could not be evaluated.
   */
  public abstract String getFallbackValue();

  /**
   * @param fallbackValue
   */
  public abstract void setFallbackValue(String fallbackValue);

  /**
   * @return the list of the function's parameters
   */
  public abstract List<Expression> getParameters();

  /**
   * @param parameters
   */
  public abstract void setParameters(List<Expression> parameters);
}
