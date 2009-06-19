/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.style;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.converter.ExpressionConverter;
import fr.ign.cogit.geoxygene.filter.converter.FilterConverter;
import fr.ign.cogit.geoxygene.filter.converter.PropertyIsEqualToConverter;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.converter.CssParameterConverter;
import fr.ign.cogit.geoxygene.style.converter.ExternalGraphicConverter;
import fr.ign.cogit.geoxygene.style.converter.FillConverter;
import fr.ign.cogit.geoxygene.style.converter.GraphicConverter;
import fr.ign.cogit.geoxygene.style.converter.LineSymbolizerConverter;
import fr.ign.cogit.geoxygene.style.converter.MarkConverter;
import fr.ign.cogit.geoxygene.style.converter.NamedLayerConverter;
import fr.ign.cogit.geoxygene.style.converter.PlacementConverter;
import fr.ign.cogit.geoxygene.style.converter.PointSymbolizerConverter;
import fr.ign.cogit.geoxygene.style.converter.PolygonSymbolizerConverter;
import fr.ign.cogit.geoxygene.style.converter.RuleConverter;
import fr.ign.cogit.geoxygene.style.converter.StrokeConverter;
import fr.ign.cogit.geoxygene.style.converter.StyledLayerDescriptorConverter;
import fr.ign.cogit.geoxygene.style.converter.TextSymbolizerConverter;

/**
 * Descripteur de couches stylisées.
 * Implémente la norme OGC 02-070 sur les StyledLayerDescriptors.
 *  TODO revoir les userLayer
 *  TODO voir les rasterSymbolizers
 *  FIXME passer à la geoapi
 * @author Julien Perret
 *
 */
public class StyledLayerDescriptor {
	protected List<ChangeListener> listenerList = new ArrayList<ChangeListener>();
	/**
	 * Ajout un {@link ChangeListener}.
	 * Adds a {@link ChangeListener}.
	 * @param l the {@link ChangeListener} to be added
	 */
	public void addChangeListener(ChangeListener l) {
		if (listenerList==null) {listenerList = new ArrayList<ChangeListener>();}
		listenerList.add(l);
	}
	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created.
	 */
	public void fireActionPerformed(ChangeEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.toArray();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-1; i>=0; i-=1) {
			((ChangeListener)listeners[i]).stateChanged(e);
		}
	}

	/**
	 * Constructeur vide.
	 */
	public StyledLayerDescriptor() {super();}

	private List<Layer> layers = new ArrayList<Layer>();
	/**
	 * Renvoie la valeur de l'attribut layers.
	 * @return la valeur de l'attribut layers
	 */
	public List<Layer> getLayers() {return this.layers;}
	/**
	 * Affecte la valeur de l'attribut layers.
	 * @param layers l'attribut layers à affecter
	 */
	public void setLayers(List<Layer> layers) {this.layers = layers;}
	
	/**
	 * Charge le SLD décrit dans le fichier XML.
	 * Si le fichier n'existe pas, crée un nouveau SLD vide.
	 * @param nomFichier fichier XML décrivant le SLD à charger
	 * @return le SLD décrit dans le fichier XML ou un SLD vide si le fichier n'existe pas.
	 */
	public static StyledLayerDescriptor charge(String nomFichier) {
        try {return (StyledLayerDescriptor) StyledLayerDescriptor.getXStream().fromXML(new FileInputStream(new File(nomFichier)));}
        catch (FileNotFoundException e) {
			/** Si le fichier n'existe pas, on crée un nouveau SLD vide */
			return new StyledLayerDescriptor();
		}
	}
	/**
	 * Sauve le SLD dans le fichier en paramètre
	 * @param nomFichier fichier dans lequel on sauve le SLD
	 */
	public void toXml(String nomFichier) {
		try {StyledLayerDescriptor.getXStream().toXML(this, new FileOutputStream(new File(nomFichier)));}
		catch (FileNotFoundException e) {e.printStackTrace();}
	}
	/**
	 * Renvoie un objet {@link XStream} servant pour le chargement ou la sauvegarde
	 * d'un objet de type {@link StyledLayerDescriptor}
	 * @return un objet {@link XStream} servant pour le chargement ou la sauvegarde
	 * d'un objet de type {@link StyledLayerDescriptor}
	 */
	private static XStream getXStream() {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("StyledLayerDescriptor", StyledLayerDescriptor.class);
		xstream.alias("NamedLayer", NamedLayer.class);
		xstream.alias("UserStyle", UserStyle.class);
		xstream.alias("FeatureTypeStyle", FeatureTypeStyle.class);
		xstream.alias("Rule", Rule.class);
		xstream.alias("LineSymbolizer", LineSymbolizer.class);
		xstream.alias("PolygonSymbolizer", PolygonSymbolizer.class);
		xstream.alias("PointSymbolizer", PointSymbolizer.class);
		xstream.alias("TextSymbolizer", TextSymbolizer.class);
        xstream.alias("Geometry",String.class);
        xstream.alias("Graphic",Graphic.class);
        xstream.alias("Stroke",Stroke.class);
        xstream.alias("Fill",Fill.class);
        xstream.alias("Filter",Filter.class);
        xstream.alias("Font",Font.class);
        xstream.alias("Mark",Mark.class);
        xstream.alias("ExternalGraphic",ExternalGraphic.class);
        xstream.alias("Label",String.class);
        xstream.alias("CssParameter",CssParameter.class);
        xstream.alias("PointPlacement",PointPlacement.class);
        xstream.alias("LinePlacement",LinePlacement.class);
        xstream.alias("PropertyIsEqualTo",PropertyIsEqualTo.class);
        xstream.alias("PropertyName",PropertyName.class);
        xstream.alias("Literal",Literal.class);
		xstream.addImplicitCollection(StyledLayerDescriptor.class, "layers");
		xstream.addImplicitCollection(FeatureTypeStyle.class, "rules");
		xstream.addImplicitCollection(UserStyle.class, "featureTypeStyles");
		xstream.addImplicitCollection(Stroke.class, "cssParameters");
		xstream.addImplicitCollection(Font.class, "cssParameters");
		xstream.registerConverter(new StyledLayerDescriptorConverter());
		xstream.registerConverter(new NamedLayerConverter());
		xstream.registerConverter(new RuleConverter());
		xstream.registerConverter(new LineSymbolizerConverter());
		xstream.registerConverter(new StrokeConverter());
		xstream.registerConverter(new CssParameterConverter());
		xstream.registerConverter(new PolygonSymbolizerConverter());
		xstream.registerConverter(new FillConverter());
		xstream.registerConverter(new FilterConverter());
		xstream.registerConverter(new GraphicConverter());
		xstream.registerConverter(new MarkConverter());
		xstream.registerConverter(new PointSymbolizerConverter());
		xstream.registerConverter(new PropertyIsEqualToConverter());
		xstream.registerConverter(new TextSymbolizerConverter());
		xstream.registerConverter(new ExternalGraphicConverter());
		xstream.registerConverter(new PlacementConverter());
		xstream.registerConverter(new ExpressionConverter());
		return xstream;
	}
	@Override
	public String toString() {
		String result = "StyledLayerDescriptor\n";
		for (Layer layer:this.getLayers()) {
			result+="   "+layer.getClass().getSimpleName()+" - "+layer.getName()+"\n";
			for(Style style:layer.getStyles()) {
				result+="      "+style.getClass().getSimpleName()+" - "+style.getName()+"\n";
				if (style.isUserStyle()) {
					UserStyle userStyle = (UserStyle) style;
					for(FeatureTypeStyle fts:userStyle.getFeatureTypeStyles()) {
						result+="         "+fts.getClass().getSimpleName()+" - "+fts.getName()+"\n";
						for(Rule rule:fts.getRules()) {
							result+="            "+rule.getClass().getSimpleName()+" - "+rule.getName()+"\n";
							for(Symbolizer symbolizer:rule.getSymbolizers()) {
								result+="               "+symbolizer.getClass().getSimpleName()+"\n";
								result+="               -Geometry = "+symbolizer.getGeometryPropertyName()+"\n";
								result+="               -Stroke Color = "+symbolizer.getStroke().getColor()+"\n";
								result+="               -Stroke width = "+symbolizer.getStroke().getStrokeWidth()+"\n";
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Renvoie le layer portant le nom en paramètre.
	 * @param layerName nom du layer cherché
	 * @return layer portant le nom en paramètre
	 */
	public Layer getLayer(String layerName) {
		for (Layer layer:layers) {
			if (layer.getName().equalsIgnoreCase(layerName.toLowerCase())) {
				//System.out.println("Récupération du layer "+layer);
				return layer;
			}
		}
		return null;
	}
	
	/**
	 * Crée un nouveau layer portant le nom donné en paramètre et un symbolizer
	 * adapté au type de géométrie en paramètre.
	 * <p>
	 * TODO choisir les couleur de la nouvelle couche dans une palette
	 * ou à partir de la couche la plus proche
	 * @param layerName nom du layer cherché 
	 * @param geometryType type de géométrie porté par le layer
	 * @param color couleur de la nouvelle couche
	 * @return layer portant le nom et la géométrie en paramètre
	 */
	public Layer createLayer(String layerName, Class<? extends GM_Object> geometryType, Color color) {
		Layer layer = new NamedLayer(layerName);
		UserStyle style = new UserStyle();
		style.setName("Style créé pour le layer "+layerName);
		FeatureTypeStyle fts = new FeatureTypeStyle();
		Rule rule = new Rule();
		Stroke stroke = new Stroke();
		stroke.setStroke(color);
		Fill fill = new Fill();
		fill.setFill(color.brighter());
		if (geometryType.equals(GM_Polygon.class)||geometryType.equals(GM_MultiSurface.class)) {
			/** Ajoute un polygone symbolizer */
			PolygonSymbolizer polygonSymbolizer = new PolygonSymbolizer();
			polygonSymbolizer.setStroke(stroke);
			polygonSymbolizer.setFill(fill);
			rule.getSymbolizers().add(polygonSymbolizer);
		} else if (geometryType.equals(GM_LineString.class)||geometryType.equals(GM_MultiCurve.class)) {
			/** Ajoute un line symbolizer */
			LineSymbolizer lineSymbolizer = new LineSymbolizer();
			lineSymbolizer.setStroke(stroke);
			rule.getSymbolizers().add(lineSymbolizer);			
		} else if (geometryType.equals(GM_Point.class)||geometryType.equals(GM_MultiPoint.class)) {
			/** Ajoute un point symbolizer */
			PointSymbolizer pointSymbolizer = new PointSymbolizer();
			Graphic graphic = new Graphic();
			Mark mark = new Mark();
			mark.setStroke(stroke);
			mark.setFill(fill);
			graphic.getMarks().add(mark);
			pointSymbolizer.setGraphic(graphic);
			rule.getSymbolizers().add(pointSymbolizer);			
		}
		fts.getRules().add(rule);
		style.getFeatureTypeStyles().add(fts);
		layer.getStyles().add(style);
		return layer;
	}
	/**
	 * Crée un nouveau layer portant le nom donné en paramètre et un symbolizer
	 * adapté au type de géométrie en paramètre.
	 * <p>
	 * Les couleurs associées au symbolizer du layer sont créées aléatoirement.
	 * @param layerName nom du layer cherché 
	 * @param geometryType type de géométrie porté par le layer
	 * @return layer portant le nom et la géométrie en paramètre
	 */
	public Layer createLayer(String layerName, Class<? extends GM_Object> geometryType) {
	    return this.createLayer(layerName, geometryType,new Color((float)Math.random(),(float)Math.random(),(float)Math.random(),0.5f));
	}
}
