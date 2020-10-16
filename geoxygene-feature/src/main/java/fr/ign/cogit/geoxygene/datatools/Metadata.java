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

package fr.ign.cogit.geoxygene.datatools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;

/**
 * Classe pour décrire les métadonnées des classes java persistantes. S'il
 * s'agit de classes Géographiques, des métadonnées sur la géométrie sont
 * renseignées. Cette classe est instanciée à l'initialisation de Geodatabase.
 * (on crée une liste de métadata, une valeur par classe persistante).
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0 16/09/2009 : ajout SRID, dimension et envelope (Julien Gaffuri)
 * 
 */
public class Metadata {
  static Logger logger = LogManager.getLogger(Metadata.class.getName());

  // ///////////////////////////////////////////////////////////////
  // / attributs ///////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////
  /** Le nom de la classe persistante. */
  protected String _className;

  /** La table du SGBD. */
  protected String _tableName;

  /** La colonne où est stockée la géométrie (mono-représentation !). */
  protected String _geomColumnName;

  /**
   * La colonne où est stocké la clef (ne gère pas les clefs complexes !).
   */
  protected String _idColumnName;

  /**
   * Le nom de l'attribut qui "mappe" la clef (ne gère pas les clefs complexes
   * !).
   */
  protected String _idFieldName;

  /** L'identifant du système de coordonnées. */
  protected int _SRID;

  /** L'enveloppe de la couche. */
  protected IEnvelope _envelope; //

  /**
   * La tolerance sur les coordonnées. _tolerance[0] = tolerance sur X, etc.
   */
  protected double[] _tolerance;

  /** Dimension des coordonnées (2D ou 3D). */
  protected int _dimension;

  // ///////////////////////////////////////////////////////////////
  // / get /////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////
  /** Le nom de la classe persistante. */
  public String getClassName() {
    return this._className;
  }

  /** La classe java persistante; */
  public Class<?> getJavaClass() {
    try {
      return Class.forName(this._className);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /** La table du SGBD. */
  public String getTableName() {
    return this._tableName;
  }

  /** La colonne où est stockée la géométrie (mono-représentation !). */
  public String getGeomColumnName() {
    return this._geomColumnName;
  }

  /**
   * La colonne où est stocké la clef (ne gère pas les clefs complexes !).
   */
  public String getIdColumnName() {
    return this._idColumnName;
  }

  /**
   * Le nom de l'attribut qui "mappe" la clef (ne gère pas les clefs complexes
   * !).
   */
  public String getIdFieldName() {
    return this._idFieldName;
  }

  /**
   * L'identifant du système de coordonnées. Vaut 0 s'il n'est pas affecté, ou
   * si la classe n'est pas Géographique.
   */
  public int getSRID() {
    return this._SRID;
  }

  /**
   * L'enveloppe de la couche. Vaut null si la classe n'est pas Géographique.
   */
  public IEnvelope getEnvelope() {
    return this._envelope;
  }

  /**
   * La tolerance sur les coordonnées. _tolerance[0] = tolerance sur X, etc.
   * Vaut null si la classe n'est pas Géographique.
   */
  public double[] getTolerance() {
    return this._tolerance;
  }

  /**
   * La tolerance sur les coordonnées. getTolerance(i) = tolerance sur X, etc.
   * Vaut null si la classe n'est pas Géographique.
   */
  public double getTolerance(int i) {
    return this._tolerance[i];
  }

  /**
   * Dimension des coordonnées (2D ou 3D). Vaut null si la classe n'est pas
   * Géographique.
   */
  public int getDimension() {
    return this._dimension;
  }

  // ///////////////////////////////////////////////////////////////
  // / set /////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////
  public void setClassName(String ClassName) {
    this._className = ClassName;
  }

  public void setTableName(String TableName) {
    this._tableName = TableName;
  }

  public void setGeomColumnName(String GeomColumnName) {
    this._geomColumnName = GeomColumnName;
  }

  public void setIdColumnName(String IdColumnName) {
    this._idColumnName = IdColumnName;
  }

  public void setIdFieldName(String IdFieldName) {
    this._idFieldName = IdFieldName;
  }

  public void setSRID(int SRID) {
    this._SRID = SRID;
  }

  public void setEnvelope(IEnvelope Envelope) {
    this._envelope = Envelope;
  }

  public void setTolerance(double[] Tolerance) {
    this._tolerance = Tolerance;
  }

  public void setTolerance(int i, double Tolerance) {
    this._tolerance[i] = Tolerance;
  }

  public void setDimension(int Dimension) {
    this._dimension = Dimension;
  }
}
