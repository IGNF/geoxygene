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

package fr.ign.cogit.geoxygene.appli.layer;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.geotiff.GeoTiffReader;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.feature.FT_Coverage;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.style.ColorMap;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.Interpolate;
import fr.ign.cogit.geoxygene.style.InterpolationPoint;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LegendGraphic;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.ShadedRelief;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserStyle;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * Convenience factory for creating GeOxygene layers. In order to manage new
 * layertypes please use LayerTypes.
 * 
 * @see Layer
 * @see GeoTiffReader
 * @see ShapefileReader
 * @see ArcGridReader
 * @author Bertrand Dumenieu
 * 
 */
public class LayerFactory {
  private static Logger logger = Logger.getLogger(LayerFactory.class.getName());

  public enum LayerType {
    SHAPEFILE, GEOTIFF, ASC, TXT;
  };

  private StyledLayerDescriptor model;

  public LayerFactory(StyledLayerDescriptor sld) {
    this.model = sld;
  }

  public void registerReaderListener(ActionListener listener) {
    ShapefileReader.addActionListener(listener);
  }

  public Layer createLayer(String filename, LayerType layertype,
      String styleName) {
    Layer layer = createLayer(filename, layertype);
    layer.setStyles(this.model.getLayer(styleName).getStyles());
    return layer;
  }

  public Layer createLayer(String filename, LayerType layertype) {
    File f = new File(filename);
    try {
      if (f.exists()) {
        switch (layertype) {
          case SHAPEFILE:
            return this.createShapeLayer(f);
          case GEOTIFF:
            return this.createTiffLayer(f);
          case ASC:
            return this.createAscLayer(f);
        }
      }
      return null;
    } catch (IOException e) {
      logger.warning("IO exception, cannot create the layer!"); //$NON-NLS-1$
      e.printStackTrace();
      return null;
    } catch (IllegalArgumentException e1) {
      logger.warning("InvalidArgumentException, cannot create the layer!"); //$NON-NLS-1$
      e1.printStackTrace();
      return null;
    }
  }

  private synchronized Layer createShapeLayer(File file) {
    String populationName = popNameFromFile(file.getPath());
    ShapefileReader shapefileReader = new ShapefileReader(file.getPath(),
        populationName, this.model.getDataSet(), true);
    shapefileReader.read();
    Layer layer = this.createLayer(populationName, shapefileReader
        .getPopulation().getFeatureType().getGeometryType());
    if (layer != null)
      layer.setCRS(shapefileReader.getCRS());
    return layer;
  }

  private Layer createTiffLayer(File file) throws IOException {
    String populationName = popNameFromFile(file.getPath());

    GeoTiffReader reader = new GeoTiffReader(file);
    @SuppressWarnings("cast")
    GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
    Population<FT_Coverage> population = new Population<FT_Coverage>(
        populationName);
    org.opengis.geometry.Envelope envelope = coverage.getEnvelope();
    population.setEnvelope(new GM_Envelope(envelope.getLowerCorner()
        .getCoordinate()[0], envelope.getUpperCorner().getCoordinate()[0],
        envelope.getLowerCorner().getCoordinate()[1], envelope.getUpperCorner()
            .getCoordinate()[1]));
    population.add(new FT_Coverage(coverage));
    this.model.getDataSet().addPopulation(population);
    Layer layer = this.createLayer(populationName);
    return layer;
  }

  private Layer createAscLayer(File file) throws IllegalArgumentException,
      IOException {
    String populationName = popNameFromFile(file.getPath());
    // double[][] range = new double[2][2];
    // BufferedImage grid = ArcGridReader.loadAsc(file.getPath(), range);
    ArcGridReader reader = new ArcGridReader(file);
    @SuppressWarnings("cast")
    GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
    /*
     * DefaultFeature feature = new DefaultFeature(new GM_Envelope( range[0][0],
     * range[0][1], range[1][0], range[1][1]).getGeom());
     * Population<DefaultFeature> population = new Population<DefaultFeature>(
     * populationName); population.add(feature);
     * DataSet.getInstance().addPopulation(population);
     */
    // wat?
    Layer layer = this.createLayer(populationName);
    RasterSymbolizer symbolizer = (RasterSymbolizer) layer.getSymbolizer();
    symbolizer.setShadedRelief(new ShadedRelief());
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;

    Raster raster = coverage.getRenderedImage().getData();
    for (int x = 0; x < coverage.getRenderedImage().getWidth(); x++) {
      for (int y = 0; y < coverage.getRenderedImage().getHeight(); y++) {
        double[] value = raster.getPixel(x, y, new double[1]);
        min = Math.min(min, value[0]);
        max = Math.max(max, value[0]);
      }
    }
    ColorMap colorMap = new ColorMap();
    Interpolate interpolate = new Interpolate();
    double diff = max - min;
    interpolate.getInterpolationPoint().add(
        new InterpolationPoint(min, Color.blue));
    interpolate.getInterpolationPoint().add(
        new InterpolationPoint(min + diff / 3, Color.cyan));
    interpolate.getInterpolationPoint().add(
        new InterpolationPoint(min + 2 * diff / 3, Color.yellow));
    interpolate.getInterpolationPoint().add(
        new InterpolationPoint(max, Color.red));
    colorMap.setInterpolate(interpolate);
    symbolizer.setColorMap(colorMap);
    // layer.setImage(symbolizer, coverage);
    return layer;
  }

  private String popNameFromFile(String file) {
    int lastIndexOfSeparator = file.lastIndexOf(File.separatorChar);
    String name = file.substring(lastIndexOfSeparator + 1,
        file.lastIndexOf(".")); //$NON-NLS-1$
    if (name == null || name.isEmpty()) {
      return "Unnamed population"; //$NON-NLS-1$
    } else
      return name;

  }

  /**
   * Cette méthode génère un nom de couche du type: "Nouvelle couche ('n') où n
   * indique le nombre de couche portant déjà ce nom.
   * @return Le nom de la nouvelle couche
   */
  public String generateNewLayerName() {
    return this.checkLayerName(I18N.getString("LayerFactory.NewLayer")); //$NON-NLS-1$
  }

  public String checkLayerName(String layerName) {
    if (this.model.getLayer(layerName) != null) {
      /** Il existe déjà une population avec ce nom */
      int n = 2;
      while (this.model.getLayer(layerName + " (" + n //$NON-NLS-1$
          + ")") != null) {n++;} //$NON-NLS-1$
      layerName = layerName + " (" + n + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    return layerName;
  }

  /**
   * Créer une nouvelle population en fonction du nom et du type de géométrie
   * envoyés en paramètre.
   * <p>
   * Initialise l'index spatial, ajoute le PanelVisu comme ChangeListener et met
   * à jour le FeatureType de la population.
   * 
   * @param popName Le nom de la population à créer
   * @param geomType Le type de géométrie de la population
   * @return Une nouvelle population à partir du nom et du type de géométrie
   *         envoyés en paramètre.
   */
  public IPopulation<IFeature> generateNewPopulation(String popName,
      Class<? extends IGeometry> geomType) {
    IPopulation<IFeature> newPop = new Population<IFeature>(popName);
    FeatureType type = new FeatureType();
    type.setGeometryType(geomType);
    newPop.setFeatureType(type);
    return newPop;
  }

  public Layer createPopulationAndLayer(String layerName,
      Class<? extends IGeometry> geometryType) {
    if (layerName.isEmpty()) {
      layerName = this.generateNewLayerName();
    } else {
      layerName = this.checkLayerName(layerName);
    }
    // Initialisation de la nouvelle population
    IPopulation<IFeature> newPop = this.generateNewPopulation(layerName,
        geometryType);
    LayerFactory factory = new LayerFactory(this.model);
    Layer newLayer = factory.createLayer(layerName, geometryType);
    FeatureType featureType = new FeatureType();
    featureType.setGeometryType(geometryType);
    this.model.getDataSet().addPopulation(newPop);
    newLayer.getFeatureCollection().setFeatureType(featureType);
    this.model.add(newLayer);
    return newLayer;
  }

  /**
   * Cree un nouveau layer portant le nom donne en parametre et un symbolizer
   * adapte au type de geometrie en parametre.
   * <p>
   * Les couleurs associees au symbolizer du layer sont creees aleatoirement.
   * @param layerName nom du layer cherche
   * @param geometryType type de geometrie porte par le layer
   * @return layer portant le nom et la geometrie en parametre
   */
  public Layer createLayer(String layerName,
      Class<? extends IGeometry> geometryType) {
    return this.createLayer(
        layerName,
        geometryType,
        new Color((float) Math.random(), (float) Math.random(), (float) Math
            .random(), 0.5f));
  }

  /**
   * Pour les rasters, mais c'est nul.
   * @param layerName
   * @return
   */
  public Layer createLayer(String layerName) {
    return this.createLayer(layerName, null, new Color((float) Math.random(),
        (float) Math.random(), (float) Math.random(), 0.5f));
  }

  /**
   * Crée un nouveau layer portant le nom donné en paramètre et un symbolizer
   * adapté au type de géométrie en paramètre.
   * <p>
   * Les couleurs associées au symbolizer du layer sont créées aléatoirement.
   * Elles sont choisies dans le cercle chromatique COGIT et sont différentes
   * des couleurs existantes.
   * @param layerName nom du layer cherché
   * @param geometryType type de géométrie porté par le layer
   * @return layer portant le nom et la géométrie en paramètre
   */
  public Layer createLayerRandomColor(String layerName,
      Class<? extends IGeometry> geometryType,
      Collection<ColorimetricColor> undesirableColors) {
    ColorReferenceSystem crs = ColorReferenceSystem.defaultColorRS();
    ColorimetricColor theColor = null;
    for (ColorimetricColor color : ColorReferenceSystem.getCOGITColors()) {
      if (!undesirableColors.contains(color)) {
        theColor = color;
      }
    }
    // Dans le cas où on a pas trouvé de couleur unique on en prend une au
    // hasard, tant pis si elle existe deja
    if (theColor == null) {
      List<ColorimetricColor> colors = crs.getAllColors();
      theColor = colors.get(new Random().nextInt(colors.size()));
    }
    return this.createLayer(layerName, geometryType, theColor.toColor());
  }

  /**
   * Crée un nouveau layer portant le nom, le type de géométrie, la couleur de
   * trait.
   * <p>
   * @param layerName nom du layer cherché
   * @param geometryType type de géométrie porté par le layer
   * @param fillColor la couleur de l'intérieur
   * @return layer portant le nom et la géométrie en paramètre
   */
  public Layer createLayer(String layerName,
      Class<? extends IGeometry> geometryType, Color fillColor) {
    return this.createLayer(layerName, geometryType, fillColor.darker(),
        fillColor);
  }

  /**
   * Crée un nouveau layer portant le nom, le type de géométrie, la couleur de
   * trait, la couleur de remplissage.
   * <p>
   * @param layerName nom du layer cherché
   * @param geometryType type de géométrie porté par le layer
   * @param strokeColor la couleur du trait
   * @param fillColor la couleur de remplissage
   * @return layer portant le nom et la géométrie en paramètre
   */
  public Layer createLayer(String layerName,
      Class<? extends IGeometry> geometryType, Color strokeColor,
      Color fillColor) {
    return this.createLayer(layerName, geometryType, strokeColor, fillColor,
        0.8f);
  }

  /**
   * Crée un nouveau layer portant le nom, le type de géométrie, la couleur de
   * trait, la couleur de remplissage, l'opacité donnés en paramètre.
   * <p>
   * @param layerName nom du layer cherché
   * @param geometryType type de géométrie porté par le layer
   * @param strokeColor la couleur du trait
   * @param fillColor la couleur de remplissage
   * @param opacity l'opacité des objets de la couche
   * @return layer portant le nom et la géométrie en paramètre
   */
  public Layer createLayer(String layerName,
      Class<? extends IGeometry> geometryType, Color strokeColor,
      Color fillColor, float opacity) {
    return this.createLayer(layerName, geometryType, strokeColor, fillColor,
        opacity, 1.0f);
  }

  /**
   * Crée un nouveau layer portant le nom, le type de géométrie, la couleur de
   * trait, la couleur de remplissage, l'opacité et la largeur de trait donnés
   * en paramètre.
   * <p>
   * @param layerName nom du layer cherché
   * @param geometryType type de géométrie porté par le layer
   * @param strokeColor la couleur du trait
   * @param fillColor la couleur de remplissage
   * @param opacity l'opacité des objets de la couche
   * @param strokeWidth la largeur du trait
   * @return layer portant le nom et la géométrie en paramètre
   */
  public Layer createLayer(String layerName,
      Class<? extends IGeometry> geometryType, Color strokeColor,
      Color fillColor, float opacity, float strokeWidth) {

    LayerFactory.logger.info("create Layer " + layerName + " " + geometryType
        + " " + strokeColor + " " + fillColor + " " + opacity + " "
        + strokeWidth);

    Layer layer = new NamedLayer(this.model, layerName);

    UserStyle style = new UserStyle();
    style.setName("Style créé pour le layer " + layerName);//$NON-NLS-1$

    FeatureTypeStyle fts = new FeatureTypeStyle();
    fts.getRules().add(
        LayerFactory.createRule(geometryType, strokeColor, fillColor, opacity,
            opacity, strokeWidth));
    style.getFeatureTypeStyles().add(fts);
    layer.getStyles().add(style);

    return layer;
  }

  /**
   * Créer un layer pour représenter une ligne utilisant deux styles: le premier
   * pour la bordure de la ligne, le deuxième pour le trait central
   * @param layerName le nom de la couche
   * @param mainStrokeColor la couleur du trait central
   * @param borderStrokeColor la couleur du trait de bordure
   * @param mainStrokeWidth l'épaisseur du trait central
   * @param borderStrokeWidth l'épaisseur du trait de bordure
   * @return un nouveau layer permettant de représenter des lignes avec bordure
   */
  public Layer createLayerWithBorder(String layerName, Color mainStrokeColor,
      Color borderStrokeColor, float mainStrokeWidth, float borderStrokeWidth) {
    if (mainStrokeWidth > borderStrokeWidth) {
      System.out.println("Le layer n'a pas été créé: " //$NON-NLS-1$
          + "La largeur du trait central ne peut " //$NON-NLS-1$
          + "pas être plus grande que celle du " + "trait de bordure"); //$NON-NLS-1$ //$NON-NLS-2$
      return null;
    }
    Layer layer = new NamedLayer(this.model, layerName);
    // Creation de la ligne de bord
    FeatureTypeStyle borderFts = new FeatureTypeStyle();
    Rule borderRule = new Rule();
    UserStyle borderStyle = new UserStyle();
    borderStyle.setName("Style de la bordure"); //$NON-NLS-1$
    Stroke borderStroke = new Stroke();
    borderStroke.setStroke(borderStrokeColor);
    borderStroke.setStrokeWidth(borderStrokeWidth);
    LineSymbolizer borderLineSymbolizer = new LineSymbolizer();
    borderLineSymbolizer.setStroke(borderStroke);
    borderRule.getSymbolizers().add(borderLineSymbolizer);
    borderFts.getRules().add(borderRule);
    borderStyle.getFeatureTypeStyles().add(borderFts);
    layer.getStyles().add(borderStyle);
    // Creation de la ligne centrale
    FeatureTypeStyle mainFts = new FeatureTypeStyle();
    Rule mainRule = new Rule();
    UserStyle mainStyle = new UserStyle();
    mainStyle.setName("Style de la ligne centrale"); //$NON-NLS-1$
    Stroke mainStroke = new Stroke();
    mainStroke.setStroke(mainStrokeColor);
    mainStroke.setStrokeWidth(mainStrokeWidth);
    LineSymbolizer mainLineSymbolizer = new LineSymbolizer();
    mainLineSymbolizer.setStroke(mainStroke);
    mainRule.getSymbolizers().add(mainLineSymbolizer);
    mainFts.getRules().add(mainRule);
    mainStyle.getFeatureTypeStyles().add(mainFts);
    layer.getStyles().add(mainStyle);
    return layer;
  }

  /**
   * @param layerName
   * @param wellKnownText
   * @param strokeColor
   * @param fillColor
   */
  public Layer createPointLayer(String layerName, String wellKnownText,
      Color strokeColor, Color fillColor) {
    Layer layer = new NamedLayer(this.model, layerName);
    UserStyle style = new UserStyle();
    style.setName("Style créé pour le layer " + layerName); //$NON-NLS-1$
    FeatureTypeStyle fts = new FeatureTypeStyle();
    Rule rule = new Rule();
    Stroke stroke = new Stroke();
    stroke.setColor(strokeColor);
    Fill fill = new Fill();
    fill.setFill(fillColor);
    PointSymbolizer pointSymbolizer = new PointSymbolizer();
    Graphic graphic = new Graphic();
    Mark mark = new Mark();
    mark.setWellKnownName(wellKnownText);
    mark.setStroke(stroke);
    mark.setFill(fill);
    graphic.getMarks().add(mark);
    pointSymbolizer.setGraphic(graphic);
    rule.getSymbolizers().add(pointSymbolizer);
    fts.getRules().add(rule);
    style.getFeatureTypeStyles().add(fts);
    layer.getStyles().add(style);
    return layer;
  }

  /**
   * Crée un nouveau layer portant le nom, le type de géométrie, la couleur de
   * trait, la couleur de remplissage, l'opacité et la largeur de trait donnés
   * en paramètre.
   * <p>
   * @param groupName nom du groupe
   * @param geometryType type de géométrie porté par le layer
   * @param strokeColor la couleur du trait
   * @param fillColor la couleur de remplissage
   * @param opacity l'opacité des objets de la couche
   * @param strokeWidth la largeur du trait
   * @return style
   */
  // FIXME Les deux méthodes createStyle et createRule n'ont sans doute pas
  // vraiment leur place ici mais devraient être placées dans un factory et/ou
  // builder propre
  public static Style createStyle(String groupName,
      Class<? extends IGeometry> geometryType, Color strokeColor,
      Color fillColor, float opacity, float strokeWidth) {
    UserStyle style = new UserStyle();
    style.setGroup(groupName);
    FeatureTypeStyle fts = new FeatureTypeStyle();
    fts.getRules().add(
        LayerFactory.createRule(geometryType, strokeColor, fillColor, opacity,
            opacity, strokeWidth));
    style.getFeatureTypeStyles().add(fts);
    return style;
  }

  // FIXME Les deux méthodes createStyle et createRule n'ont sans doute pas
  // vraiment leur place ici mais devraient être placées dans un factory et/ou
  // builder propre
  public static Rule createRule(Class<? extends IGeometry> geometryType,
      Color strokeColor, Color fillColor, float strokeOpacity,
      float fillOpacity, float strokeWidth) {
    Rule rule = new Rule();
    rule.setLegendGraphic(new LegendGraphic());
    Graphic graphicSymbol = new Graphic();
    Mark markSymbol = new Mark();
    Fill fillSymbol = new Fill();
    fillSymbol.setFill(fillColor);
    fillSymbol.setFillOpacity(fillOpacity);
    Stroke strokeSymbol = new Stroke();
    strokeSymbol.setStroke(strokeColor);
    strokeSymbol.setStrokeOpacity(strokeOpacity);
    strokeSymbol.setStrokeWidth(strokeWidth);
    markSymbol.setFill(fillSymbol);
    markSymbol.setStroke(strokeSymbol);
    graphicSymbol.getMarks().add(markSymbol);
    rule.getLegendGraphic().setGraphic(graphicSymbol);

    Stroke stroke = new Stroke();
    stroke.setStroke(strokeColor);
    stroke.setStrokeOpacity(strokeOpacity);
    stroke.setStrokeWidth(strokeWidth);
    Fill fill = new Fill();
    fill.setFill(fillColor);
    fill.setFillOpacity(fillOpacity);
    if (geometryType == null) {
      /** Ajoute un raster symbolizer */
      RasterSymbolizer rasterSymbolizer = new RasterSymbolizer();
      rasterSymbolizer.setStroke(stroke);
      rule.getSymbolizers().add(rasterSymbolizer);
      return rule;
    }
    if (IPolygon.class.isAssignableFrom(geometryType)
        || IMultiSurface.class.isAssignableFrom(geometryType)) {
      /** Ajoute un polygone symbolizer */
      PolygonSymbolizer polygonSymbolizer = new PolygonSymbolizer();
      polygonSymbolizer.setStroke(stroke);
      polygonSymbolizer.setFill(fill);
      rule.getSymbolizers().add(polygonSymbolizer);
      return rule;
    }
    if (ICurve.class.isAssignableFrom(geometryType)
        || IMultiCurve.class.isAssignableFrom(geometryType)) {
      /** Ajoute un line symbolizer */
      LineSymbolizer lineSymbolizer = new LineSymbolizer();
      lineSymbolizer.setStroke(stroke);
      rule.getSymbolizers().add(lineSymbolizer);
      return rule;
    }
    if (IPoint.class.isAssignableFrom(geometryType)
        || IMultiPoint.class.isAssignableFrom(geometryType)) {
      /** Ajoute un point symbolizer */
      PointSymbolizer pointSymbolizer = new PointSymbolizer();
      Graphic graphic = new Graphic();
      Mark mark = new Mark();
      mark.setStroke(stroke);
      mark.setFill(fill);
      graphic.getMarks().add(mark);
      pointSymbolizer.setGraphic(graphic);
      rule.getSymbolizers().add(pointSymbolizer);
      return rule;
    }
    return rule;
  }
}
