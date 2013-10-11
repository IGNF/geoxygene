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

package fr.ign.cogit.geoxygene.scripting;

/**
 * @author JeT
 * Exception thrown on text transformation error
 */
public class TextTransformException extends Exception {

  private static final long serialVersionUID = 174264499329321826L; // Serializable UID

  /**
   * Constructor
   */
  public TextTransformException() {
  }

  /**
   * Constructor
   * @param message
   */
  public TextTransformException(final String message) {
    super(message);
  }

  /**
   * Constructor
   * @param cause
   */
  public TextTransformException(final Throwable cause) {
    super(cause);
  }

  /**
   * Constructor
   * @param message
   * @param cause
   */
  public TextTransformException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
