/**
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for
 * the development and deployment of geographic (GIS) applications. It is a open
 * source
 * contribution of the COGIT laboratory at the Institut Géographique National
 * (the French
 * National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * this library (see file LICENSE if present); if not, write to the Free
 * Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.style;

import java.awt.Color;
import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Descripteur de couches stylisées.
 * Implémente la norme OGC 02-070 sur les StyledLayerDescriptors.
 * TODO revoir les userLayer
 * TODO voir les rasterSymbolizers
 * FIXME passer à la geoapi
 * @author Julien Perret
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "name",
        // "description",
        // "environmentVariables",
        // "useSLDLibrary",
"layers" })
@XmlRootElement(name = "StyledLayerDescriptor")
public class StyledLayerDescriptor {
    static Logger logger = Logger.getLogger(StyledLayerDescriptor.class
            .getName());

    @XmlElement(name = "Name")
    protected String name;
    // @XmlElement(name = "Description")
    // protected Description description;
    // @XmlElement(name = "EnvironmentVariables")
    // protected EnvironmentVariables environmentVariables;
    // @XmlElement(name = "UseSLDLibrary")
    // protected List<UseSLDLibrary> useSLDLibrary;
    @XmlAttribute(required = true)
    protected String version;

    /**
     * Constructeur vide.
     */
    public StyledLayerDescriptor() {
        super();
    }

    @XmlElements( { @XmlElement(name = "NamedLayer", type = NamedLayer.class),
        @XmlElement(name = "UserLayer", type = UserLayer.class) })
        private List<Layer> layers = new ArrayList<Layer>();

    public List<Layer> getLayers() {
        return this.layers;
    }

    /**
     * Affecte la valeur de l'attribut layers.
     * @param layers l'attribut layers à affecter
     */
    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    @Override
    public String toString() {
        CharArrayWriter writer = new CharArrayWriter();
        this.marshall(writer);
        return writer.toString();
    }

    /**
     * Renvoie le layer portant le nom en paramètre.
     * @param layerName nom du layer cherché
     * @return layer portant le nom en paramètre
     */
    public Layer getLayer(String layerName) {
        for (Layer layer : this.layers) {
            if (layer.getName().equalsIgnoreCase(layerName.toLowerCase())) {
                // System.out.println("Recuperation du layer "+layer);
                return layer;
            }
        }
        return null;
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
            Class<? extends GM_Object> geometryType) {
        return this.createLayer(layerName, geometryType, new Color((float) Math
                .random(), (float) Math.random(), (float) Math.random(), 0.5f));
    }

    /**
     * @param layer
     */
    public void add(Layer layer) {
        this.layers.add(layer);
        this.fireActionPerformed(new ChangeEvent(this));
    }

    // Event handling
    @XmlTransient
    protected List<ChangeListener> listenerList = new ArrayList<ChangeListener>();

    /**
     * Ajout un {@link ChangeListener}.
     * Adds a {@link ChangeListener}.
     * @param l the {@link ChangeListener} to be added
     */
    public void addChangeListener(ChangeListener l) {
        if (this.listenerList == null) {
            this.listenerList = new ArrayList<ChangeListener>();
        }
        this.listenerList.add(l);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type. The event instance
     * is lazily created.
     */
    public void fireActionPerformed(ChangeEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = this.listenerList.toArray();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            ((ChangeListener) listeners[i]).stateChanged(e);
        }
    }

    public static void main(String[] args) {
        StyledLayerDescriptor sld = StyledLayerDescriptor
        .unmarshall("geopensimSLD.xml"); //$NON-NLS-1$
        System.out.println(sld);
    }

    public static StyledLayerDescriptor unmarshall(InputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(
                    StyledLayerDescriptor.class, NamedLayer.class,
                    NamedStyle.class);
            Unmarshaller m = context.createUnmarshaller();
            StyledLayerDescriptor sld = (StyledLayerDescriptor) m
            .unmarshal(stream);
            return sld;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new StyledLayerDescriptor();
    }

    /**
     * Charge le SLD decrit dans le fichier XML.
     * Si le fichier n'existe pas, cree un nouveau SLD vide.
     * @param fileName fichier XML decrivant le SLD à charger
     * @return le SLD decrit dans le fichier XML ou un SLD vide si le fichier
     *         n'existe pas.
     */
    public static StyledLayerDescriptor unmarshall(String fileName) {
        try {
            return unmarshall(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            logger.error("File " + fileName + " could not be read"); //$NON-NLS-1$//$NON-NLS-2$
            return new StyledLayerDescriptor();
        }
    }

    public void marshall(Writer writer) {
        try {
            JAXBContext context = JAXBContext.newInstance(
                    StyledLayerDescriptor.class, NamedLayer.class,
                    NamedStyle.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, writer);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void marshall(OutputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(
                    StyledLayerDescriptor.class, NamedLayer.class,
                    NamedStyle.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, stream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sauve le SLD dans le fichier en parametre
     * @param fileName fichier dans lequel on sauve le SLD
     */
    public void marshall(String fileName) {
        try {
            this.marshall(new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            logger.error("File " + fileName + " could not be written to"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    /**
     * Add a symbolizer.
     * <p>
     * Permet d'ajouter un nouveau symbolizer dans une couche avec les couleurs
     * du symbolizer déja existant.
     * @param layerName name of the layer to add a symbolizer to
     * @param geometryType type of symbolizer
     */
    public void addSymbolizer(String layerName,
            Class<? extends GM_Object> geometryType) {
        Layer layer = getLayer(layerName);
        List<Style> styles = layer.getStyles();
        for (Style style : styles) {
            if (style.isUserStyle()) {
                UserStyle userStyle = (UserStyle) style;
                List<FeatureTypeStyle> ftsList = userStyle
                .getFeatureTypeStyles();
                userStyle.setFeatureTypeStyles(ftsList);
                for (FeatureTypeStyle fts : ftsList) {
                    List<Rule> rules = fts.getRules();
                    if (geometryType.equals(GM_Polygon.class)
                            || geometryType.equals(GM_MultiSurface.class)) {
                        for (Rule rule : rules) {
                            Stroke ruleStroke = rule.getSymbolizers().get(0)
                            .getStroke();
                            if (ruleStroke == null) {
                                ruleStroke = new Stroke();
                            }
                            PolygonSymbolizer polygonSymbolizer = new PolygonSymbolizer();
                            polygonSymbolizer.setStroke(ruleStroke);
                            Color color = ruleStroke.getColor();
                            color = color.brighter();
                            Fill fill = new Fill();
                            fill.setColor(color);
                            polygonSymbolizer.setFill(fill);
                            rule.getSymbolizers().add(polygonSymbolizer);
                        }
                    } else if (geometryType.equals(GM_LineString.class)
                            || geometryType.equals(GM_MultiCurve.class)) {
                        for (Rule rule : rules) {
                            Stroke ruleStroke = rule.getSymbolizers().get(0)
                            .getStroke();
                            if (ruleStroke == null) {
                                ruleStroke = new Stroke();
                            }
                            LineSymbolizer lineSymbolizer = new LineSymbolizer();
                            lineSymbolizer.setStroke(ruleStroke);
                            rule.getSymbolizers().add(lineSymbolizer);
                        }
                    } else if (geometryType.equals(GM_Point.class)
                            || geometryType.equals(GM_MultiPoint.class)) {
                        for (Rule rule : rules) {
                            Stroke ruleStroke = rule.getSymbolizers().get(0)
                            .getStroke();
                            if (ruleStroke == null) {
                                ruleStroke = new Stroke();
                            }
                            PointSymbolizer pointSymbolizer = new PointSymbolizer();
                            Graphic graphic = new Graphic();
                            Mark mark = new Mark();
                            mark.setStroke(ruleStroke);
                            Color color = ruleStroke.getColor();
                            color = color.brighter();
                            Fill fill = new Fill();
                            fill.setColor(color);
                            mark.setFill(fill);
                            graphic.getMarks().add(mark);
                            pointSymbolizer.setGraphic(graphic);
                            rule.getSymbolizers().add(pointSymbolizer);
                        }
                    }
                }
            }
        }
    }

    /**
     * Crée un nouveau layer portant le nom donné en paramètre et un symbolizer
     * adapté au type de géométrie en paramètre.
     * <p>
     * Les couleurs associées au symbolizer du layer sont créées aléatoirement.
     * TODO choisir les couleur de la nouvelle couche dans une palette ou à
     * partir de la couche la plus proche
     * @param layerName nom du layer cherché
     * @param geometryType type de géométrie porté par le layer
     * @return layer portant le nom et la géométrie en paramètre
     */
    public Layer createLayerRandomColor(String layerName,
            Class<? extends GM_Object> geometryType) {
        return createLayer(layerName, geometryType, new Color((float) Math
                .random(), (float) Math.random(), (float) Math.random(), 0.5f));
    }

    /**
     * Crée un nouveau layer portant le nom, le type de géométrie, la couleur de
     * trait.
     * <p>
     * @param layerName nom du layer cherché
     * @param geometryType type de géométrie porté par le layer
     * @param strokeColor la couleur du trait
     * @return layer portant le nom et la géométrie en paramètre
     */
    public Layer createLayer(String layerName,
            Class<? extends GM_Object> geometryType, Color strokeColor) {
        return createLayer(layerName, geometryType, strokeColor, strokeColor
                .brighter().brighter());
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
            Class<? extends GM_Object> geometryType, Color strokeColor,
                    Color fillColor) {
        return createLayer(layerName, geometryType, strokeColor, fillColor,
                0.5f);
    }

    /**
     * Crée un nouveau layer portant le nom, le type de géométrie, la couleur de
     * trait, la couleur de remplissage,
     * l'opacité donnés en paramètre.
     * <p>
     * @param layerName nom du layer cherché
     * @param geometryType type de géométrie porté par le layer
     * @param strokeColor la couleur du trait
     * @param fillColor la couleur de remplissage
     * @param opacity l'opacité des objets de la couche
     * @return layer portant le nom et la géométrie en paramètre
     */
    public Layer createLayer(String layerName,
            Class<? extends GM_Object> geometryType, Color strokeColor,
                    Color fillColor, float opacity) {
        return createLayer(layerName, geometryType, strokeColor, fillColor,
                opacity, 1.0f);
    }

    /**
     * Crée un nouveau layer portant le nom, le type de géométrie, la couleur de
     * trait, la couleur de remplissage,
     * l'opacité et la largeur de trait donnés en paramètre.
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
            Class<? extends GM_Object> geometryType, Color strokeColor,
                    Color fillColor, float opacity, float strokeWidth) {
        Layer layer = new NamedLayer(layerName);
        UserStyle style = new UserStyle();
        style.setName("Style créé pour le layer " + layerName);
        FeatureTypeStyle fts = new FeatureTypeStyle();
        fts.getRules().add(
                createRule(geometryType, strokeColor, fillColor, opacity,
                        opacity, strokeWidth));
        style.getFeatureTypeStyles().add(fts);
        layer.getStyles().add(style);
        return layer;
    }

    public Rule createRule(Class<? extends GM_Object> geometryType,
            Color strokeColor, Color fillColor, float strokeOpacity,
            float fillOpacity, float strokeWidth) {
        Rule rule = new Rule();
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
        if (geometryType.equals(GM_Polygon.class)
                || geometryType.equals(GM_MultiSurface.class)) {
            /** Ajoute un polygone symbolizer */
            PolygonSymbolizer polygonSymbolizer = new PolygonSymbolizer();
            polygonSymbolizer.setStroke(stroke);
            polygonSymbolizer.setFill(fill);
            rule.getSymbolizers().add(polygonSymbolizer);
            return rule;
        }
        if (geometryType.equals(GM_LineString.class)
                || geometryType.equals(GM_MultiCurve.class)) {
            /** Ajoute un line symbolizer */
            LineSymbolizer lineSymbolizer = new LineSymbolizer();
            lineSymbolizer.setStroke(stroke);
            rule.getSymbolizers().add(lineSymbolizer);
            return rule;
        }
        if (geometryType.equals(GM_Point.class)
                || geometryType.equals(GM_MultiPoint.class)) {
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

    /**
     * Créer un layer pour représenter une ligne utilisant deux styles: le
     * premier pour la bordure de la ligne, le deuxième pour le trait central
     * @param layerName le nom de la couche
     * @param mainStrokeColor la couleur du trait central
     * @param borderStrokeColor la couleur du trait de bordure
     * @param mainStrokeWidth l'épaisseur du trait central
     * @param borderStrokeWidth l'épaisseur du trait de bordure
     * @return un nouveau layer permettant de représenter des lignes avec
     *         bordure
     */
    public Layer createLayerWithBorder(String layerName, Color mainStrokeColor,
            Color borderStrokeColor, float mainStrokeWidth,
            float borderStrokeWidth) {
        if (mainStrokeWidth > borderStrokeWidth) {
            System.out
            .println("Le layer n'a pas été créé: " +
                    "La largeur du trait central ne peut " +
                    "pas être plus grande que celle du " +
            "trait de bordure");
            return null;
        }
        Layer layer = new NamedLayer(layerName);

        // Creation de la ligne de bord
        FeatureTypeStyle borderFts = new FeatureTypeStyle();
        Rule borderRule = new Rule();
        UserStyle borderStyle = new UserStyle();
        borderStyle.setName("Style de la bordure");
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
        mainStyle.setName("Style de la ligne centrale");
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
     * TODO
     * @param layerName
     * @param wellKnownText
     * @param strokeColor
     * @param fillColor
     */
    public Layer createPointLayer(String layerName, String wellKnownText,
            Color strokeColor, Color fillColor) {
        Layer layer = new NamedLayer(layerName);
        UserStyle style = new UserStyle();
        style.setName("Style créé pour le layer " + layerName);
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
}
