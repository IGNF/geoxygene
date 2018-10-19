package fr.ign.cogit.geoxygene.semio.legend.symbol;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Sebastien Mustière  - IGN / Laboratoire COGIT
 * @author Charlotte Hoarau  - IGN / Laboratoire COGIT
 * @author Vianney Dugrain
 * 
 * Graphical Symbol.
 * Cartographical view of a symbol on the Map.
 * Adapted to the tree implantation mode (points, lines, areas)
 *
 */
public class GraphicSymbol {
  @SuppressWarnings("unused")
  private static final Logger logger = Logger
                          .getLogger(GraphicSymbol.class);
  
  /**
   * Constructor based on a SLD Layer.
   * @param layer SLD Layer.
   */
  public GraphicSymbol (Layer layer, double resolution){
    this.setUnitOfMeasure(layer);
    this.setSizeOnMap(layer, resolution);
    this.setWidthOnMap(layer, resolution);
    this.setTypeGeometrie(layer.getFeatureCollection().get(0));
    this.setPointSymbolName(layer);
    this.setColor(layer);
    this.setJoin(layer);
    this.setCap(layer);
  }

  public GraphicSymbol(){}
  
  public final static int UNKNOWN = -1;
	public final static int POINTS = 0;
	public final static int LINES = 1;
	public final static int SURFACES = 2;
	
	
	/**
	 * Main color of the symbol.
	 * FIXME : to be changed when the model will rely on SLD.
	 */
	private ColorimetricColor color;
	
	/**
	 * Get the main color of the symbol.
	 * @return The main color of the symbol.
	 */
	public ColorimetricColor getColor() {
	  return color;
	}
	
	/**
	 * Set the main color of the symbol.
	 * @param color The main color of the symbol.
	 */
	public void setColor(ColorimetricColor color) {
	  this.color = color;
	}
	
	public void setColor(Layer layer){
	  Symbolizer symbolizer = layer.getSymbolizer();
	  if (symbolizer.isLineSymbolizer()) {
        this.color = new ColorimetricColor(
            symbolizer.getStroke().getColor(), true);
      } else if (symbolizer.isPolygonSymbolizer()) {
        this.color = new ColorimetricColor(
            ((PolygonSymbolizer)symbolizer).getFill().getColor(), true);
      } else if (symbolizer.isPointSymbolizer()) {
        this.color = new ColorimetricColor(
            ((PointSymbolizer)symbolizer).getGraphic().getMarks().get(0).
            getFill().getColor(), true);
      }
	}
	
	/**
	 * Geometric type of the symbol.
	 * FIXME : May be changed when the model will rely on SLD.
	 * Possible values: see static attributes
	 */
	private int geometricType = UNKNOWN;
	
	/**
	 * Get the geometric type of the symbol.
	 * Possible values: see static attributes.
	 * @return The geometric type of the symbol.
	 */
	public int getTypeGeometrie() {
	  return geometricType;
	}
	
	/**
	 * Geometric type of the symbol.
	 * Possible values: see static attributes.
	 * @param type The geometric type of the symbol.
	 */
	public void setTypeGeometrie(int type) {
	  this.geometricType = type;
	}
	
	/**
	 * Set the geometric type of the symbol defined through one representative GeOxygene {@link FT_Feature}.
	 * FIXME : May be changed when the model will rely on SLD.
	 * Possible values: see static attributes
	 * @param feature The GeOxygene {@link IFeature} defining the geometric type of the symbol.
	 */
	public void setTypeGeometrie(IFeature feature) {
		if (feature.getGeom() instanceof GM_Point) {
			this.setTypeGeometrie(GraphicSymbol.POINTS);
			return;
		}
		if (feature.getGeom() instanceof GM_Polygon) {
			this.setTypeGeometrie(GraphicSymbol.SURFACES);
			return;
		}
		if (feature.getGeom() instanceof GM_LineString) {
			this.setTypeGeometrie(GraphicSymbol.LINES);
			return;
		}
		if (feature.getGeom() instanceof GM_MultiCurve<?>) {
			this.setTypeGeometrie(GraphicSymbol.LINES);
			return;
		}
		this.setTypeGeometrie(GraphicSymbol.UNKNOWN);
		return;
	}
	
	/**
	 * Size of the GraphicSymbol on the Map.
	 * Just defined for a punctual graphic symbol.
	 * For the other symbols, the covered area  by objects on the map depends
	 * directly on the geometry and not on the graphic symbol.
	 * 
	 * FRENCH:
	 * Taille du symbole graphique représenté sur la carte.
	 * Définit uniquement pour les symboles graphiques ponctuels.
	 * Pour les autres symboles graphiques, la surface couverte sur la carte par les objets
	 * géographiques dépendra directement de leur géométrie, et non du symbol graphique utilisé.
	 * 
	 * Si l'objet est un point : taille = taille du symbol ponctuel définit dans le SLD correspondant.
	 * Si l'objet est une ligne ou un polygone : taille = -1.
	 * 
	 */
	private double sizeOnMap;
	public double getSizeOnMap() {
		return sizeOnMap;
	}
	public void setSizeOnMap(double sizeOnMap) {
		this.sizeOnMap = sizeOnMap;
	}
	
	public void setSizeOnMap(Layer layer, double resolution) {
		if (layer.getSymbolizer().isPointSymbolizer()) {
			double sizeOnMap = ((PointSymbolizer)layer.getSymbolizer())
												.getGraphic().getSize();
			if (this.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.METRE)) {
			  this.setSizeOnMap(sizeOnMap);
			} else if (this.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
			  this.setSizeOnMap(sizeOnMap * resolution);
			}
		} else {
			this.setSizeOnMap(-1);
		};
	}
	
	/**
	 * Width of the Stroke of the GraphicSymbol on the Map.
	 * 
	 * FRENCH:
	 * Epaisseur du contour du symbole graphique représenté sur la carte.
	 * Exprimée en unité terrain.
	 * 
	 */
	private double widthOnMap;
	public double getWidthOnMap() {
		return widthOnMap;
	}
	public void setWidthOnMap(double widthOnMap) {
		this.widthOnMap = widthOnMap;
	}
	
	public void setWidthOnMap(Layer layer, double resolution) {
	  
		double widthOnMap = 0;
		if (layer.getSymbolizer().isPointSymbolizer()) {
		  if (this.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.METRE)) {
            widthOnMap = ((PointSymbolizer)layer.getSymbolizer()).getGraphic()
                  .getMarks().get(0).getStroke().getStrokeWidth();	
          } else if (this.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
            widthOnMap = ((PointSymbolizer)layer.getSymbolizer()).getGraphic()
                  .getMarks().get(0).getStroke().getStrokeWidth() * resolution; 
          }	
		} else {
		  if (this.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.METRE)) {
			widthOnMap = layer.getSymbolizer().getStroke().getStrokeWidth();
		  } else if (this.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
		    widthOnMap = layer.getSymbolizer().getStroke().getStrokeWidth() * resolution;
		  }
		}
		this.setWidthOnMap(widthOnMap);
	}
	
   /**
	* Type of symbol used for points
	* 
	* FRENCH:
	* Type de symbole utilisé pour les points 
	*/
	private String pointSymbolName;
	public String getPointSymbolName() {
		return pointSymbolName;
	}
	
	public void setPointSymbolName(Layer layer) {
		if (layer.getSymbolizer().isPointSymbolizer()) {
			pointSymbolName = ((PointSymbolizer) layer.getSymbolizer()).getGraphic().getMarks().get(0).getWellKnownName();
		} else {
			pointSymbolName = null;
		}
	}
	
   /**
	* Unit of measure of the GraphicSymbol
	* 
	* FRENCH:
	* Unité de mesure du GraphicSymbol
	*/
	private String unitOfMeasure;
	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}
	public void setUnitOfMeasure(Layer layer) {
		unitOfMeasure = layer.getSymbolizer().getUnitOfMeasure();
	}
	
   /**
	* Join symbolization for outlines
	* 
	* FRENCH:
	* Symbolisation des jointures pour les contours
	*/
	private int join;
	public int getJoin() {
		return join;
	}
	public void setJoin(Layer layer) {
		if(layer.getSymbolizer().isPointSymbolizer()) {
			join = 0;
		} else {
			join = layer.getSymbolizer().getStroke().getStrokeLineJoin();
		}
	}
	
   /**
	* Cap used for line extremities
	* 
	* FRENCH:
	* Chapeau utilisé pour les extrémités des lignes
	*/
	private int cap;
	public int getCap() {
		return cap;
	}
	public void setCap(Layer layer) {
		if(layer.getSymbolizer().isPointSymbolizer()) {
		  cap = 0;
		} else {
		  cap = layer.getSymbolizer().getStroke().getStrokeLineCap();
		}
	}
	
	@Override
  public String toString(){
	  return "Graphic Symbol : cap = " + this.getCap() + ", join = " + this.getJoin();
	}
	
}
