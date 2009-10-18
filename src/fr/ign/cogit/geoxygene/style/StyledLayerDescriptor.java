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
 *  TODO revoir les userLayer
 *  TODO voir les rasterSymbolizers
 *  FIXME passer à la geoapi
 * @author Julien Perret
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    //"description",
    //"environmentVariables",
    //"useSLDLibrary",
    "layers"
})
@XmlRootElement(name = "StyledLayerDescriptor")
public class StyledLayerDescriptor {
	static Logger logger=Logger.getLogger(StyledLayerDescriptor.class.getName());
	
    @XmlElement(name = "Name")
    protected String name;
    //@XmlElement(name = "Description")
    //protected Description description;
    //@XmlElement(name = "EnvironmentVariables")
    //protected EnvironmentVariables environmentVariables;
    //@XmlElement(name = "UseSLDLibrary")
    //protected List<UseSLDLibrary> useSLDLibrary;
    @XmlAttribute(required = true)
    protected String version;

	/**
	 * Constructeur vide.
	 */
	public StyledLayerDescriptor() {super();}

    @XmlElements({
        @XmlElement(name = "NamedLayer", type = NamedLayer.class),
        @XmlElement(name = "UserLayer", type = UserLayer.class)
    })
	private List<Layer> layers = new ArrayList<Layer>();
	public List<Layer> getLayers() {return this.layers;}
	/**
	 * Affecte la valeur de l'attribut layers.
	 * @param layers l'attribut layers à affecter
	 */
	public void setLayers(List<Layer> layers) {this.layers = layers;}
	
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
		for (int i = listeners.length-1; i>=0; i-=1) {((ChangeListener)listeners[i]).stateChanged(e);}
	}
	
	
	public static void main(String[] args) {
		StyledLayerDescriptor sld = StyledLayerDescriptor.unmarshall("geopensimSLD.xml");
		System.out.println(sld);
	}

	public static StyledLayerDescriptor unmarshall(InputStream stream) {
		try {
			JAXBContext context = JAXBContext.newInstance(
					StyledLayerDescriptor.class,
					NamedLayer.class,
					NamedStyle.class);
			Unmarshaller m = context.createUnmarshaller();
			StyledLayerDescriptor sld = (StyledLayerDescriptor) m.unmarshal(stream);
			return sld;
		} catch (JAXBException e) {e.printStackTrace();}
		return new StyledLayerDescriptor();		
	}
	/**
	 * Charge le SLD décrit dans le fichier XML.
	 * Si le fichier n'existe pas, crée un nouveau SLD vide.
	 * @param fileName fichier XML décrivant le SLD à charger
	 * @return le SLD décrit dans le fichier XML ou un SLD vide si le fichier n'existe pas.
	 */
	public static StyledLayerDescriptor unmarshall(String fileName) {
		try {
			return unmarshall(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			logger.error("File "+fileName+" could not be read");
			return new StyledLayerDescriptor();
		}
	}

	public void marshall(Writer writer) {
		try {
			JAXBContext context = JAXBContext.newInstance(
					StyledLayerDescriptor.class,
					NamedLayer.class,
					NamedStyle.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(this, writer);
		} catch (JAXBException e) {e.printStackTrace();}		
	}

	public void marshall(OutputStream stream) {
		try {
			JAXBContext context = JAXBContext.newInstance(
					StyledLayerDescriptor.class,
					NamedLayer.class,
					NamedStyle.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(this, stream);
		} catch (JAXBException e) {e.printStackTrace();}		
	}
	/**
	 * Sauve le SLD dans le fichier en paramètre
	 * @param fileName fichier dans lequel on sauve le SLD
	 */
	public void marshall(String fileName) {
		try {
			this.marshall(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			logger.error("File "+fileName+" could not be written to");
		}
	}
}
