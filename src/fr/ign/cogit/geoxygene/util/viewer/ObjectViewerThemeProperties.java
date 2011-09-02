/*
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
 */

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.Color;
import java.util.Random;

import uk.ac.leeds.ccg.geotools.DataSource;
import uk.ac.leeds.ccg.geotools.HSVShader;
import uk.ac.leeds.ccg.geotools.HighlightManager;
import uk.ac.leeds.ccg.geotools.LineLayer;
import uk.ac.leeds.ccg.geotools.MonoShader;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import uk.ac.leeds.ccg.geotools.RandomShader;
import uk.ac.leeds.ccg.geotools.SelectionManager;
import uk.ac.leeds.ccg.geotools.ShadeStyle;
import uk.ac.leeds.ccg.geotools.Shader;
import uk.ac.leeds.ccg.geotools.Theme;
import uk.ac.leeds.ccg.geotools.UniqueShader;
import uk.ac.leeds.ccg.geotools.satShader;

/**
 * This class allow to handle properties of themes to be displayed in the
 * (Geo)Object viewer.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class ObjectViewerThemeProperties {

  // Default
  public static final Color DEFAULT_OUTLINE_THEME_COLOR = Color.BLACK;
  public static final Color DEFAULT_SELECTION_COLOR = Color.YELLOW;
  public static final Color DEFAULT_HIGHLIGHT_COLOR = Color.GREEN;
  public static final Color DEFAULT_MISSING_VALUE_SHADER_COLOR = Color.WHITE;
  public static final int DEFAULT_MISSING_VALUE_SHADER = -999999999;

  private ObjectViewerInterface objectViewerInterface;
  private Theme objectViewerTheme;

  private String dataSourceType;
  private DataSource dataSource;

  private Shader shader;
  private String shadedBy = null;

  private Color fillInThemeColor;
  private Color outlineThemeColor;
  private Color fillInHighlightColor;
  private Color outlineHighlightColor;
  private Color fillInSelectionColor;
  private Color outlineSelectionColor;

  private SelectionManager themeSelectionManager;
  private HighlightManager themeHighlightManager;

  private boolean active;
  private boolean visible;

  private ObjectViewerThemeProperties() {

    // random color for filling it
    Random randomColor = new Random();
    float randomRed = randomColor.nextFloat();
    float randomGreen = randomColor.nextFloat();
    float randomBlue = randomColor.nextFloat();
    System.out.println("Theme Color (R: " + randomRed * 255 + ";G: "
        + randomGreen * 255 + ";B: " + randomBlue * 255 + ")");
    Color randomShaderColor = new Color(randomRed, randomGreen, randomBlue);
    this.setFillInThemeColor(randomShaderColor);

    // default color
    this.setOutlineThemeColor(ObjectViewerThemeProperties.DEFAULT_OUTLINE_THEME_COLOR);
    this.setFillInHighlightColor(ObjectViewerThemeProperties.DEFAULT_HIGHLIGHT_COLOR);
    this.setOutlineHighlightColor(ObjectViewerThemeProperties.DEFAULT_HIGHLIGHT_COLOR);
    this.setFillInSelectionColor(ObjectViewerThemeProperties.DEFAULT_SELECTION_COLOR);
    this.setOutlineSelectionColor(ObjectViewerThemeProperties.DEFAULT_SELECTION_COLOR);

  }

  public ObjectViewerThemeProperties(
      ObjectViewerInterface objectViewerInterface, Theme objectViewerTheme,
      String dataSourceType, DataSource datasource, boolean IsActive,
      boolean IsVisible) {
    this();
    this.setObjectViewerInterface(objectViewerInterface);
    this.setObjectViewerTheme(objectViewerTheme);
    this.setDataSource(datasource);
    this.setDataSourceType(dataSourceType);
    this.setActive(IsActive);
    this.setVisible(IsVisible);
  }

  public void setChanged() {
    Theme t = this.getObjectViewerTheme();

    if ((t.getLayer() instanceof PolygonLayer)
        && (!(t.getLayer() instanceof LineLayer))) {
      ShadeStyle updatedThemeShadeStyle = new ShadeStyle();
      updatedThemeShadeStyle.setLineColor(this.getOutlineThemeColor());
      t.setStyle(updatedThemeShadeStyle);
    }

    ShadeStyle updatedHighlightShadeStyle = new ShadeStyle();
    updatedHighlightShadeStyle.setFillColor(this.getFillInHighlightColor());
    updatedHighlightShadeStyle.setLineColor(this.getOutlineHighlightColor());
    t.setHighlightStyle(updatedHighlightShadeStyle);

    ShadeStyle updatedSelectionShadeStyle = new ShadeStyle();
    updatedSelectionShadeStyle.setFillColor(this.getFillInSelectionColor());
    updatedSelectionShadeStyle.setLineColor(this.getOutlineSelectionColor());
    t.setSelectionStyle(updatedSelectionShadeStyle);

    if (this.shader instanceof MonoShader) {
      MonoShader updatedShader = new MonoShader(this.getFillInThemeColor());
      t.setShader(updatedShader);

    } else if (this.shader instanceof RandomShader) {
      RandomShader updatedShader = new RandomShader();
      t.setShader(updatedShader);

    } else if (this.shader instanceof HSVShader) {
      // HSVShader updatedShader = new HSVShader(DEFAULT_LOW_HSVSHADER_COLOR,
      // DEFAULT_HIGH_HSVSHADER_COLOR); marche pas
      satShader updatedShader = new satShader(this.getFillInThemeColor());
      updatedShader
          .setMissingValueCode(ObjectViewerThemeProperties.DEFAULT_MISSING_VALUE_SHADER);
      updatedShader
          .setMissingValueColor(ObjectViewerThemeProperties.DEFAULT_MISSING_VALUE_SHADER_COLOR);
      /*
       * if (dataSourceType.equals(Utils.SHAPEFILE)) {
       * System.out.println("Shader HSV with field = "+shadedBy);
       * ShapefileReader shpRd = (ShapefileReader) dataSource; t =
       * shpRd.getTheme(updatedShader,shadedBy); } a faire
       * 
       * else
       */if (this.dataSourceType.equals(Utils.GEOXYGENE)) {
        GeOxygeneReader geOxyRd = (GeOxygeneReader) this.dataSource;
        t = geOxyRd.getTheme(updatedShader, this.shadedBy);
      }

    } else if (this.shader instanceof UniqueShader) {
      UniqueShader updatedShader = new UniqueShader();
      updatedShader
          .setMissingValueCode(ObjectViewerThemeProperties.DEFAULT_MISSING_VALUE_SHADER);
      updatedShader
          .setMissingValueColor(ObjectViewerThemeProperties.DEFAULT_MISSING_VALUE_SHADER_COLOR);
      /*
       * if (dataSourceType.equals(Utils.SHAPEFILE)) { a faire }
       * 
       * else
       */if (this.dataSourceType.equals(Utils.GEOXYGENE)) {
        GeOxygeneReader geOxyRd = (GeOxygeneReader) this.dataSource;
        t = geOxyRd.getTheme(updatedShader, this.shadedBy);
      }
    }

    this.getObjectViewerInterface().view.setThemeIsVisible(t, true, true);

  }

  public void setObjectViewerInterface(
      ObjectViewerInterface objectViewerInterface) {
    this.objectViewerInterface = objectViewerInterface;
  }

  public ObjectViewerInterface getObjectViewerInterface() {
    return this.objectViewerInterface;
  }

  public void setFillInThemeColor(Color color) {
    this.fillInThemeColor = color;
  }

  public Color getFillInThemeColor() {
    return this.fillInThemeColor;
  }

  public void setOutlineThemeColor(Color color) {
    this.outlineThemeColor = color;
  }

  public Color getOutlineThemeColor() {
    return this.outlineThemeColor;
  }

  public void setFillInHighlightColor(Color color) {
    this.fillInHighlightColor = color;
  }

  public Color getFillInHighlightColor() {
    return this.fillInHighlightColor;
  }

  public void setOutlineHighlightColor(Color color) {
    this.outlineHighlightColor = color;
  }

  public Color getOutlineHighlightColor() {
    return this.outlineHighlightColor;
  }

  public void setFillInSelectionColor(Color color) {
    this.fillInSelectionColor = color;
  }

  public Color getFillInSelectionColor() {
    return this.fillInSelectionColor;
  }

  public void setOutlineSelectionColor(Color color) {
    this.outlineSelectionColor = color;
  }

  public Color getOutlineSelectionColor() {
    return this.outlineSelectionColor;
  }

  public void setObjectViewerTheme(Theme t) {
    this.objectViewerTheme = t;
  }

  public Theme getObjectViewerTheme() {
    return this.objectViewerTheme;
  }

  public String getDataSourceType() {
    return this.dataSourceType;
  }

  public void setDataSourceType(String string) {
    this.dataSourceType = string;
  }

  public DataSource getDataSource() {
    return this.dataSource;
  }

  public void setDataSource(DataSource source) {
    this.dataSource = source;
  }

  public void setShader(Shader sh) {
    this.shader = sh;
  }

  public void setShader(Shader sh, String field) {
    this.shader = sh;
    if (field != null) {
      this.shadedBy = field;
    }
  }

  public Shader getShader() {
    return this.shader;
  }

  public String getShadedBy() {
    return this.shadedBy;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(boolean b) {
    this.active = b;
  }

  public HighlightManager getThemeHighlightManager() {
    return this.themeHighlightManager;
  }

  public void setThemeHighlightManager(HighlightManager manager) {
    this.themeHighlightManager = manager;
  }

  public SelectionManager getThemeSelectionManager() {
    return this.themeSelectionManager;
  }

  public void setThemeSelectionManager(SelectionManager manager) {
    this.themeSelectionManager = manager;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void setVisible(boolean b) {
    this.visible = b;
  }

}
