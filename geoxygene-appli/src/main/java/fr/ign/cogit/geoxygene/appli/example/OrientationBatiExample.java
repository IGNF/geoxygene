package fr.ign.cogit.geoxygene.appli.example;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

import cern.colt.Arrays;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.FloatingMainFrame;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.filter.expression.Expression;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.LabelPlacement;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointPlacement;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.style.thematic.DiagramRadius;
import fr.ign.cogit.geoxygene.style.thematic.DiagramSizeElement;
import fr.ign.cogit.geoxygene.style.thematic.DiagramSymbolizer;
import fr.ign.cogit.geoxygene.style.thematic.ThematicClass;
import fr.ign.cogit.geoxygene.style.thematic.ThematicSymbolizer;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientation;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

public class OrientationBatiExample extends AbstractGeOxygeneApplicationPlugin {
  
  /** Logger. */
  private static final Logger LOGGER = Logger.getLogger(OrientationBatiExample.class.getName());
  
  
  private FeatureType featureTypeBati;
  private SchemaDefaultFeature schemaBati;
  private FeatureType featureTypePPRE;
  private SchemaDefaultFeature schemaPPRE;
  private FeatureType featureTypeAxe;
  private SchemaDefaultFeature schemaAxe;
  private FeatureType featureFeuille;
  private SchemaDefaultFeature schemaFeuille;
  
  
  @Override
  public void initialize(final GeOxygeneApplication application) {
    this.application = application;
    
    createSchemas();

    JMenu menu = addMenu("Example", "Orientation");
    application.getMainFrame().getMenuBar()
      .add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);
    
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    LOGGER.info("Start");
    try {
      this.application.getMainFrame().removeAllProjectFrames();
      
      // On charge un wkt
      IGeometry bati01 = WktGeOxygene.makeGeOxygene("POLYGON((468585.59999999997671694 6738092,468583.29999999998835847 6738090.09999999962747097,468579.90000000002328306 6738090.40000000037252903,468572.79999999998835847 6738098,468572.90000000002328306 6738100.79999999981373549,468575.20000000001164153 6738103.09999999962747097,468573.20000000001164153 6738105.29999999981373549,468573.29999999998835847 6738108.09999999962747097,468579.70000000001164153 6738114.09999999962747097,468578.5 6738115.5,468578.59999999997671694 6738119.90000000037252903,468574.29999999998835847 6738120.20000000018626451,468573.29999999998835847 6738121.20000000018626451,468573.59999999997671694 6738127.70000000018626451,468569.59999999997671694 6738128.09999999962747097,468568.40000000002328306 6738129.5,468568.79999999998835847 6738135.79999999981373549,468570.20000000001164153 6738136.79999999981373549,468569 6738138.29999999981373549,468569.09999999997671694 6738144.5,468555.40000000002328306 6738145.20000000018626451,468555.09999999997671694 6738140.90000000037252903,468551.90000000002328306 6738138,468553.79999999998835847 6738136.09999999962747097,468553.5 6738131.70000000018626451,468551.70000000001164153 6738130,468553.40000000002328306 6738127.90000000037252903,468553 6738122.5,468558.79999999998835847 6738122.09999999962747097,468562.59999999997671694 6738117.90000000037252903,468562.29999999998835847 6738111.70000000018626451,468558.20000000001164153 6738108,468552.5 6738108.40000000037252903,468548.79999999998835847 6738112.40000000037252903,468546.29999999998835847 6738110.40000000037252903,468542.09999999997671694 6738110.70000000018626451,468540.20000000001164153 6738112.90000000037252903,468540.40000000002328306 6738117.09999999962747097,468536.20000000001164153 6738117.20000000018626451,468533.90000000002328306 6738119.70000000018626451,468534 6738123.59999999962747097,468530.20000000001164153 6738123.90000000037252903,468528.09999999997671694 6738126,468528.29999999998835847 6738130,468530.59999999997671694 6738132.29999999981373549,468534.59999999997671694 6738132.09999999962747097,468534.90000000002328306 6738136,468537 6738138.20000000018626451,468537.29999999998835847 6738143,468540.5 6738146.29999999981373549,468532.70000000001164153 6738155,468532.90000000002328306 6738158.59999999962747097,468551.70000000001164153 6738176,468554.70000000001164153 6738176,468560.79999999998835847 6738169.20000000018626451,468560.90000000002328306 6738167.70000000018626451,468564.90000000002328306 6738167.40000000037252903,468564.5 6738156.20000000018626451,468568.59999999997671694 6738156,468569.79999999998835847 6738154.5,468573.40000000002328306 6738154.29999999981373549,468573.40000000002328306 6738150.5,468579 6738150.09999999962747097,468581.70000000001164153 6738147.09999999962747097,468581.79999999998835847 6738145.40000000037252903,468583.59999999997671694 6738145.20000000018626451,468584.59999999997671694 6738144,468584.40000000002328306 6738137.79999999981373549,468588.40000000002328306 6738137.59999999962747097,468589.79999999998835847 6738135.90000000037252903,468589.40000000002328306 6738129.90000000037252903,468593.20000000001164153 6738129.70000000018626451,468594.70000000001164153 6738128.09999999962747097,468594.59999999997671694 6738121.79999999981373549,468593 6738120.40000000037252903,468594.29999999998835847 6738118.90000000037252903,468594.20000000001164153 6738114.79999999981373549,468593 6738113.59999999962747097,468599.09999999997671694 6738107,468598.90000000002328306 6738104.29999999981373549,468596.79999999998835847 6738102.29999999981373549,468590.20000000001164153 6738102.5,468586 6738098.70000000018626451,468585.59999999997671694 6738092))");
      createLayer(bati01);
      
      // On charge un wkt
      IGeometry bati02 = WktGeOxygene.makeGeOxygene("POLYGON((470384.09999999997671694 6738158.90000000037252903,470387 6738159,470387.09999999997671694 6738162.09999999962747097,470390 6738162.20000000018626451,470389.90000000002328306 6738163.59999999962747097,470396 6738163.70000000018626451,470396.09999999997671694 6738162.20000000018626451,470399.09999999997671694 6738162.29999999981373549,470399.09999999997671694 6738159.09999999962747097,470401.90000000002328306 6738159.09999999962747097,470401.90000000002328306 6738160.70000000018626451,470405.20000000001164153 6738160.59999999962747097,470405.09999999997671694 6738159.09999999962747097,470407.70000000001164153 6738159,470408.09999999997671694 6738156.29999999981373549,470410.79999999998835847 6738156.29999999981373549,470410.90000000002328306 6738157.79999999981373549,470414.09999999997671694 6738157.79999999981373549,470414.20000000001164153 6738156.29999999981373549,470417.09999999997671694 6738156.29999999981373549,470417.20000000001164153 6738153.40000000037252903,470419.90000000002328306 6738153.40000000037252903,470420.09999999997671694 6738145.59999999962747097,470417 6738145.70000000018626451,470416.90000000002328306 6738147.09999999962747097,470410.90000000002328306 6738147.09999999962747097,470410.90000000002328306 6738148.59999999962747097,470408 6738148.59999999962747097,470407.90000000002328306 6738150,470402.09999999997671694 6738150,470402.09999999997671694 6738151.40000000037252903,470399 6738151.59999999962747097,470399 6738152.90000000037252903,470387.5 6738152.90000000037252903,470387.40000000002328306 6738151.29999999981373549,470384.09999999997671694 6738151.40000000037252903,470384.09999999997671694 6738158.90000000037252903))");
      createLayer(bati02);
      
      IGeometry bati03 = WktGeOxygene.makeGeOxygene("POLYGON((468508.79999999998835847 6739029.5,468517.29999999998835847 6739024.79999999981373549,468514.90000000002328306 6739020.20000000018626451,468521 6739016.59999999962747097,468506 6738988.90000000037252903,468511.79999999998835847 6738985.70000000018626451,468512.40000000002328306 6738986.79999999981373549,468527.29999999998835847 6738978.29999999981373549,468512.29999999998835847 6738950.70000000018626451,468518.70000000001164153 6738946.79999999981373549,468519.29999999998835847 6738947.70000000018626451,468532.90000000002328306 6738940,468533.20000000001164153 6738939,468512.79999999998835847 6738902.40000000037252903,468505.5 6738906.70000000018626451,468508.20000000001164153 6738911.5,468501.20000000001164153 6738915.5,468516.29999999998835847 6738942.90000000037252903,468510 6738946.29999999981373549,468509.40000000002328306 6738945.29999999981373549,468494.40000000002328306 6738953.79999999981373549,468509.59999999997671694 6738981.5,468503.59999999997671694 6738984.90000000037252903,468503 6738983.70000000018626451,468488.40000000002328306 6738992.20000000018626451,468508.79999999998835847 6739029.5))");
      createLayer(bati03);
      
      IGeometry bati04 = WktGeOxygene.makeGeOxygene("POLYGON((467974.70000000001164153 6739019,467980.90000000002328306 6739024.20000000018626451,467984.79999999998835847 6739019.70000000018626451,467990.79999999998835847 6739019.20000000018626451,467990.09999999997671694 6739011,467982.79999999998835847 6739011.59999999962747097,467981.90000000002328306 6739012.79999999981373549,467980.70000000001164153 6739011.90000000037252903,467974.70000000001164153 6739019))");
      createLayer(bati04);
      
      IGeometry bati05 = WktGeOxygene.makeGeOxygene("POLYGON((468498.29999999998835847 6738838.29999999981373549,468497.40000000002328306 6738836.59999999962747097,468494 6738830.70000000018626451,468492.79999999998835847 6738828.59999999962747097,468489.5 6738822.90000000037252903,468488.29999999998835847 6738820.70000000018626451,468484.90000000002328306 6738814.79999999981373549,468483.40000000002328306 6738812.20000000018626451,468480.09999999997671694 6738806.5,468478.70000000001164153 6738803.90000000037252903,468475.40000000002328306 6738798.20000000018626451,468473.90000000002328306 6738795.59999999962747097,468470.59999999997671694 6738789.79999999981373549,468468.79999999998835847 6738786.70000000018626451,468446.29999999998835847 6738799.70000000018626451,468460.90000000002328306 6738825.29999999981373549,468456.29999999998835847 6738828.20000000018626451,468458.40000000002328306 6738831.70000000018626451,468452.79999999998835847 6738835.09999999962747097,468454.90000000002328306 6738838.59999999962747097,468460.40000000002328306 6738835.40000000037252903,468465 6738843.5,468469.59999999997671694 6738840.90000000037252903,468475.70000000001164153 6738851.20000000018626451,468498.29999999998835847 6738838.29999999981373549))");
      createLayer(bati05);
      
      IGeometry bati06 = WktGeOxygene.makeGeOxygene("POLYGON((468529.59999999997671694 6738777.70000000018626451,468530.59999999997671694 6738762.09999999962747097,468531.40000000002328306 6738762.20000000018626451,468532.90000000002328306 6738736.09999999962747097,468534.09999999997671694 6738736.09999999962747097,468534.70000000001164153 6738725.59999999962747097,468533.40000000002328306 6738725.5,468534.5 6738706.59999999962747097,468531.5 6738705.90000000037252903,468529.5 6738696.09999999962747097,468521.90000000002328306 6738697.79999999981373549,468520.20000000001164153 6738690.20000000018626451,468493 6738696,468497.29999999998835847 6738714.90000000037252903,468494.70000000001164153 6738715.59999999962747097,468521.5 6738764.09999999962747097,468522 6738763.90000000037252903,468529.59999999997671694 6738777.70000000018626451))");
      createLayer(bati06);
      
      IGeometry bati07 = WktGeOxygene.makeGeOxygene("POLYGON((470536.59999999997671694 6739316.09999999962747097,470538 6739319.5,470548.59999999997671694 6739315,470546.59999999997671694 6739310,470549.29999999998835847 6739308.79999999981373549,470546.79999999998835847 6739302.90000000037252903,470544.09999999997671694 6739304,470543.90000000002328306 6739303.5,470537.20000000001164153 6739306.20000000018626451,470537.59999999997671694 6739307.29999999981373549,470531.40000000002328306 6739309.79999999981373549,470531.09999999997671694 6739309.29999999981373549,470525 6739311.79999999981373549,470524.70000000001164153 6739312.59999999962747097,470520.59999999997671694 6739322.20000000018626451,470520.79999999998835847 6739322.70000000018626451,470520.40000000002328306 6739322.90000000037252903,470522.70000000001164153 6739328.5,470523.20000000001164153 6739328.40000000037252903,470525.79999999998835847 6739334.90000000037252903,470528.59999999997671694 6739333.79999999981373549,470529.5 6739336.29999999981373549,470534.70000000001164153 6739334.20000000018626451,470533.79999999998835847 6739331.70000000018626451,470535.90000000002328306 6739330.70000000018626451,470533.59999999997671694 6739325,470532.5 6739325.40000000037252903,470531.09999999997671694 6739326,470528.70000000001164153 6739320.40000000037252903,470530.29999999998835847 6739316.79999999981373549,470536 6739314.59999999962747097,470536.59999999997671694 6739316.09999999962747097))");
      createLayer(bati07);
      
      IGeometry bati08 = WktGeOxygene.makeGeOxygene("POLYGON((469656.5 6739126.29999999981373549,469644.40000000002328306 6739122.79999999981373549,469639.29999999998835847 6739138.5,469638.29999999998835847 6739141.79999999981373549,469643.79999999998835847 6739143.59999999962747097,469656.29999999998835847 6739147.59999999962747097,469661.40000000002328306 6739149.20000000018626451,469663.20000000001164153 6739143.5,469646.5 6739138.09999999962747097,469648.79999999998835847 6739129.79999999981373549,469656.5 6739132.29999999981373549,469658.29999999998835847 6739126.79999999981373549,469656.5 6739126.29999999981373549))");
      createLayer(bati08);
      
      IGeometry bati09 = WktGeOxygene.makeGeOxygene("POLYGON((470049.90000000002328306 6738141,470051.5 6738142.09999999962747097,470052.29999999998835847 6738142.70000000018626451,470046.20000000001164153 6738151.20000000018626451,470052.90000000002328306 6738156.29999999981373549,470054.59999999997671694 6738154.09999999962747097,470068.59999999997671694 6738164.29999999981373549,470073.09999999997671694 6738158.20000000018626451,470075.79999999998835847 6738160.20000000018626451,470076.40000000002328306 6738159.79999999981373549,470077.29999999998835847 6738160.59999999962747097,470074.29999999998835847 6738164.5,470101.5 6738185.59999999962747097,470110.90000000002328306 6738173.29999999981373549,470112.20000000001164153 6738174.20000000018626451,470138.09999999997671694 6738193.09999999962747097,470142.90000000002328306 6738187,470116.20000000001164153 6738167.5,470117 6738166.40000000037252903,470125.5 6738154.79999999981373549,470125.79999999998835847 6738154.90000000037252903,470133.59999999997671694 6738144.40000000037252903,470134.70000000001164153 6738145.29999999981373549,470132.29999999998835847 6738148.40000000037252903,470134.70000000001164153 6738150.20000000018626451,470134.40000000002328306 6738150.59999999962747097,470136.79999999998835847 6738152.40000000037252903,470140.90000000002328306 6738147.20000000018626451,470136 6738143.29999999981373549,470135.5 6738143.79999999981373549,470134.70000000001164153 6738143.09999999962747097,470135.40000000002328306 6738142.20000000018626451,470124.79999999998835847 6738134.09999999962747097,470141.79999999998835847 6738111.5,470143.5 6738112.59999999962747097,470147.59999999997671694 6738107.20000000018626451,470152.5 6738110.90000000037252903,470156.90000000002328306 6738104.90000000037252903,470143.09999999997671694 6738094.40000000037252903,470142.59999999997671694 6738094.90000000037252903,470141.90000000002328306 6738095.79999999981373549,470122.79999999998835847 6738081.90000000037252903,470118.29999999998835847 6738088.09999999962747097,470137.5 6738102.5,470138.29999999998835847 6738103.09999999962747097,470113.70000000001164153 6738136.09999999962747097,470083.59999999997671694 6738114,470084.5 6738112.79999999981373549,470094.59999999997671694 6738099.29999999981373549,470094.5 6738094.20000000018626451,470109 6738094.40000000037252903,470109.20000000001164153 6738080.40000000037252903,470091.09999999997671694 6738080,470091.20000000001164153 6738079.29999999981373549,470087.5 6738079,470087.5 6738079.90000000037252903,470084.70000000001164153 6738079.90000000037252903,470072.20000000001164153 6738079.70000000018626451,470071.79999999998835847 6738093.5,470084.70000000001164153 6738093.79999999981373549,470084.79999999998835847 6738090.70000000018626451,470087.5 6738090.70000000018626451,470087.59999999997671694 6738094.09999999962747097,470086.20000000001164153 6738095.59999999962747097,470086.90000000002328306 6738096.20000000018626451,470072.59999999997671694 6738115.29999999981373549,470079.59999999997671694 6738120.59999999962747097,470081.40000000002328306 6738121.79999999981373549,470118.59999999997671694 6738149.40000000037252903,470119.5 6738150.20000000018626451,470105.59999999997671694 6738168.90000000037252903,470104.5 6738168.09999999962747097,470103.5 6738168.90000000037252903,470083.09999999997671694 6738153.29999999981373549,470078.90000000002328306 6738158.70000000018626451,470077.79999999998835847 6738158,470078 6738157.70000000018626451,470075.09999999997671694 6738155.59999999962747097,470083.70000000001164153 6738143.90000000037252903,470076.09999999997671694 6738138.20000000018626451,470076 6738137,470072.29999999998835847 6738134.40000000037252903,470070.59999999997671694 6738133.09999999962747097,470070.29999999998835847 6738132.90000000037252903,470068.40000000002328306 6738133.59999999962747097,470061.5 6738128.70000000018626451,470059.90000000002328306 6738127.5,470053.20000000001164153 6738122.70000000018626451,470052.79999999998835847 6738123.29999999981373549,470051 6738122.09999999962747097,470051 6738120.90000000037252903,470051.70000000001164153 6738120.79999999981373549,470049.59999999997671694 6738107.90000000037252903,470052.70000000001164153 6738107.29999999981373549,470052.29999999998835847 6738106.20000000018626451,470053.09999999997671694 6738104.09999999962747097,470051.70000000001164153 6738102.5,470051.5 6738101.29999999981373549,470048.59999999997671694 6738101.79999999981373549,470048.20000000001164153 6738100.40000000037252903,470045.09999999997671694 6738101,470044.5 6738098.70000000018626451,470037.5 6738099.79999999981373549,470037.59999999997671694 6738101.90000000037252903,470034.09999999997671694 6738102.5,470035.5 6738110,470036.09999999997671694 6738109.90000000037252903,470037 6738116.5,470023.79999999998835847 6738118.79999999981373549,470016 6738112.79999999981373549,470017.70000000001164153 6738110.5,470016.5 6738109.59999999962747097,470015.59999999997671694 6738108,470013.70000000001164153 6738107.29999999981373549,470012.90000000002328306 6738106.70000000018626451,470011 6738109.29999999981373549,470009.79999999998835847 6738108.20000000018626451,470007.59999999997671694 6738110.90000000037252903,470006 6738109.90000000037252903,470001.70000000001164153 6738115.70000000018626451,470003.20000000001164153 6738116.90000000037252903,470001.40000000002328306 6738119.5,470007.5 6738124.20000000018626451,470008.09999999997671694 6738123.5,470011.70000000001164153 6738126.59999999962747097,470012.90000000002328306 6738136.09999999962747097,470011.20000000001164153 6738136.5,470012.20000000001164153 6738143.79999999981373549,470013.79999999998835847 6738143.59999999962747097,470015 6738150.20000000018626451,470020.40000000002328306 6738149.40000000037252903,470021.09999999997671694 6738152.70000000018626451,470029 6738151.40000000037252903,470026.09999999997671694 6738130.5,470039 6738128.59999999962747097,470040.59999999997671694 6738137,470043.90000000002328306 6738136.59999999962747097,470049.90000000002328306 6738141),(470059.5 6738147.20000000018626451,470064.29999999998835847 6738141.20000000018626451,470071.40000000002328306 6738146.70000000018626451,470066.70000000001164153 6738152.5,470059.5 6738147.20000000018626451))");
      createLayer(bati09);
      
      IGeometry bati10 = WktGeOxygene.makeGeOxygene("POLYGON((466521.70000000001164153 6736479.70000000018626451,466508.90000000002328306 6736478.79999999981373549,466507.5 6736492.29999999981373549,466520.40000000002328306 6736493.09999999962747097,466521.70000000001164153 6736479.70000000018626451))");
      createLayer(bati10);
      
      IGeometry bati11 = WktGeOxygene.makeGeOxygene("POLYGON((468534.79999999998835847 6738520.59999999962747097,468547.29999999998835847 6738520.70000000018626451,468547.40000000002328306 6738522.5,468556.29999999998835847 6738522.59999999962747097,468556.20000000001164153 6738521.09999999962747097,468572.5 6738521.29999999981373549,468576.29999999998835847 6738506.90000000037252903,468573 6738493.59999999962747097,468535.20000000001164153 6738492.59999999962747097,468531.20000000001164153 6738506.59999999962747097,468534.79999999998835847 6738520.59999999962747097))");
      createLayer(bati11);
      
      IGeometry bati12 = WktGeOxygene.makeGeOxygene("POLYGON((469452 6737303,469469.70000000001164153 6737301.79999999981373549,469468.20000000001164153 6737284.20000000018626451,469466.79999999998835847 6737284.40000000037252903,469464.90000000002328306 6737284.70000000018626451,469463.29999999998835847 6737285.29999999981373549,469461.70000000001164153 6737285.90000000037252903,469460.09999999997671694 6737286.59999999962747097,469458.79999999998835847 6737287.5,469457.29999999998835847 6737288.40000000037252903,469456.20000000001164153 6737289.40000000037252903,469455.5 6737290.29999999981373549,469454.70000000001164153 6737291.5,469453.79999999998835847 6737293.29999999981373549,469453.20000000001164153 6737294.70000000018626451,469452.70000000001164153 6737296.29999999981373549,469452.40000000002328306 6737298,469452.20000000001164153 6737299.59999999962747097,469452 6737301.20000000018626451,469452 6737303))");
      createLayer(bati12);
      
      ((FloatingMainFrame)this.application.getMainFrame()).organizeCurrentDesktop(3);
      for (int i = 0; i < this.application.getMainFrame().getDesktopProjectFrames().length; i++) {
        ProjectFrame frame = this.application.getMainFrame().getDesktopProjectFrames()[i];
        frame.getLayerLegendPanel().getLayerViewPanel().getViewport().zoomToFullExtent();
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  
  private void createLayer(IGeometry bati01) {
    
    try {
      
      ProjectFrame projectFrame = application.getMainFrame().newProjectFrame();
      URL urlCircle = new URL("file", "", "./img/fleche.png");
      
      // ==============================================================================================================
      //    Layer 1 : PPRE
      Population<DefaultFeature> popPPRE = new Population<DefaultFeature>(false, "PPRE", DefaultFeature.class, true);
      popPPRE.setFeatureType(this.featureTypePPRE);
      
      Geometry bati01Jts = AdapterFactory.toGeometry(new GeometryFactory(), bati01);
      Polygon ppre = JtsUtil.PPRE(bati01Jts);
      IGeometry ppreGeox = AdapterFactory.toGM_Object(ppre);
      DefaultFeature nppre = popPPRE.nouvelElement(ppreGeox);
      nppre.setSchema(this.schemaPPRE);
      
      // Affiche
      Layer layerPPRE = projectFrame.addUserLayer(popPPRE, "PPRE", null);
      PolygonSymbolizer polySymbolR = (PolygonSymbolizer) layerPPRE.getSymbolizer();
      polySymbolR.getFill().setColor(new Color(236, 228, 236));
      polySymbolR.getStroke().setColor(new Color(218, 81, 137));
      polySymbolR.setUnitOfMeasurePixel();
      polySymbolR.getStroke().setStrokeWidth(1.0f);
      
      // ==============================================================================================================
      //    Layer 2 : bati
      Population<DefaultFeature> popBati = new Population<DefaultFeature>(false, "Bati", DefaultFeature.class, true);
      popBati.setFeatureType(this.featureTypeBati);
      
      DefaultFeature n = popBati.nouvelElement(bati01);
      n.setSchema(this.schemaBati);
      
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      MesureOrientation mesure = new MesureOrientation(bati01);
      double orientationGenerale = mesure.getOrientationGenerale();
      mesure.calculerOrientationCote();
      double orientationMur = mesure.getOrientationCotes();
      int nbOrientation = mesure.getNombreOrientation();
      double orientationMur2 = mesure.getContributionSecondaire();
      
      double[] tab = mesure.getContributionsCotesOrientation();
      // System.out.println(Arrays.toString(tab));
      
      Object[] attributes = new Object[] { orientationGenerale, orientationMur, nbOrientation, orientationMur2};
      n.setAttributes(attributes);

      // Affiche
      Layer layerBati = projectFrame.addUserLayer(popBati, "Bati", null);
      projectFrame.getLayerLegendPanel().getLayerViewPanel().getViewport().zoomToFullExtent();
      
      PolygonSymbolizer polySymbol = (PolygonSymbolizer) layerBati.getSymbolizer();
      polySymbol.setUnitOfMeasurePixel();
      polySymbol.getFill().setColor(new Color(32, 112, 177));
      polySymbol.getFill().setFillOpacity(0.8f);
      polySymbol.getStroke().setColor(new Color(111, 85, 66));
      polySymbol.getStroke().setStrokeOpacity(0.8f);
      polySymbol.getStroke().setStrokeWidth(1.0f);
      
      // -----------------------------------------------------------------
      PointSymbolizer symbolizerP1 = new PointSymbolizer();
      symbolizerP1.setUnitOfMeasurePixel();
      Graphic graphic1 = new Graphic();
      Mark mark1 = new Mark();
      mark1.setStroke(new Stroke());
      mark1.setFill(new Fill());
      List<Mark> marks1 = new ArrayList<Mark>();
      marks1.add(mark1);
      graphic1.setMarks(marks1);
      symbolizerP1.setGraphic(graphic1);
      symbolizerP1.getGraphic().setMarks(marks1);
      symbolizerP1.getGraphic().setSize(20);
      symbolizerP1.getGraphic().getMarks().get(0).getStroke().setStrokeWidth(1);
      symbolizerP1.getGraphic().getMarks().get(0).getStroke().setStrokeOpacity(0.0f);
      symbolizerP1.getGraphic().getMarks().get(0).getStroke().setColor(new Color(218, 81, 137));
      symbolizerP1.getGraphic().getMarks().get(0).getFill().setFillOpacity(0.0f);
      symbolizerP1.getGraphic().getMarks().get(0).getFill().setColor(new Color(236, 228, 236));
      symbolizerP1.getGraphic().getMarks().get(0).setWellKnownName("circle");
      
      double angle = 180 * orientationGenerale / Math.PI;
      symbolizerP1.getGraphic().setRotation(new Literal(Double.toString(angle)));
      
      ExternalGraphic externalGraphicCircle1 = new ExternalGraphic();
      externalGraphicCircle1.setHref(urlCircle.toString());
      externalGraphicCircle1.setFormat("png"); //$NON-NLS-1$
      symbolizerP1.getGraphic().getExternalGraphics().add(externalGraphicCircle1);
      
      layerBati.getStyles().get(0).getFeatureTypeStyles().get(0).getRules().get(0).getSymbolizers().add(symbolizerP1);
      
      TextSymbolizer txtSymbolizer = new TextSymbolizer();
      txtSymbolizer.setUnitOfMeasurePixel();
      fr.ign.cogit.geoxygene.style.Font sldFont = new fr.ign.cogit.geoxygene.style.Font(
          new Font("Verdana", Font.PLAIN, 36));
      txtSymbolizer.setFont(sldFont);
      txtSymbolizer.setLabel("nbOrientation");
      Fill txtFill = new Fill();
      txtSymbolizer.setFill(txtFill);
      Stroke txtStroke = new Stroke();
      txtStroke.setColor(Color.BLACK);
      txtStroke.setStrokeWidth(1.5f);
      txtSymbolizer.setStroke(txtStroke);
      txtFill.setColor(new Color(0, 0, 0));
      LabelPlacement placement = new LabelPlacement();
      PointPlacement ptPlacement = new PointPlacement();
      placement.setPlacement(ptPlacement);
      ptPlacement.setRotation(0.0f);
      txtSymbolizer.setLabelPlacement(placement);
      layerBati.getStyles().get(0).getFeatureTypeStyles().get(0).getRules().get(0).getSymbolizers().add(txtSymbolizer);
      
      // ==============================================================================================================
      //    Layer 3 : centre feuille
      Population<DefaultFeature> popFeuille = new Population<DefaultFeature>(false, "Feuille", DefaultFeature.class, true);
      popFeuille.setFeatureType(this.featureFeuille);
      
      Coordinate[] coords = ppre.getCoordinates();
      double xmin = Double.MAX_VALUE;
      for (Coordinate c : coords) {
        if (c.x < xmin) {
          xmin = c.x;
        }
      } 
       
      double lg1 = coords[0].distance(coords[1]);
      double lg2 = coords[1].distance(coords[2]);
      double l = lg1;
      if (lg2 < lg1) {
        l = lg2;
      }
      DirectPosition pos = new DirectPosition(ppreGeox.centroid().getX(), ppreGeox.centroid().getY() + l*2/3);
      
      DefaultFeature featCentre = popFeuille.nouvelElement(new GM_Point(pos));
      featCentre.setSchema(this.schemaFeuille);
      Object[] atts = new  Object[90];
      for (int i = 0; i < 90; i++) {
        atts[i] = tab[i];
      }
      // attributes = new Object[] { nbOrientation};
      featCentre.setAttributes(atts);
      
      Layer layerFeuille = projectFrame.addUserLayer(popFeuille, "Feuilles", null);
      PointSymbolizer symbolizerP2 = (PointSymbolizer) layerFeuille.getSymbolizer();
      symbolizerP2.setUnitOfMeasurePixel();
      Graphic graphic2 = new Graphic();
      Mark mark2 = new Mark();
      mark2.setStroke(new Stroke());
      mark2.setFill(new Fill());
      List<Mark> marks2 = new ArrayList<Mark>();
      marks2.add(mark2);
      graphic2.setMarks(marks2);
      symbolizerP2.setGraphic(graphic2);
      symbolizerP2.getGraphic().setMarks(marks2);
      symbolizerP2.getGraphic().setSize(2);
      symbolizerP2.getGraphic().getMarks().get(0).getStroke().setStrokeWidth(1);
      symbolizerP2.getGraphic().getMarks().get(0).getStroke().setStrokeOpacity(0.8f);
      symbolizerP2.getGraphic().getMarks().get(0).getStroke().setColor(new Color(218, 81, 137));
      symbolizerP2.getGraphic().getMarks().get(0).getFill().setFillOpacity(0.0f);
      symbolizerP2.getGraphic().getMarks().get(0).getFill().setColor(new Color(236, 228, 236));
      symbolizerP2.getGraphic().getMarks().get(0).setWellKnownName("square");
      // layerFeuille.getStyles().get(0).getFeatureTypeStyles().get(0).getRules().get(0).getSymbolizers().add(symbolizerP2);
      
      
      // ==============================================================================================================
      //    Layer 4 : axes
      /*Population<DefaultFeature> popAxes = new Population<DefaultFeature>(false, "Axes", DefaultFeature.class, true);
      popAxes.setFeatureType(this.featureTypeAxe);
      
      
      
      double a1 = Math.tan(orientationMur);
      double b1 = pos.getY() - a1 * pos.getX();
      double x1 = pos.getX() + 2;
      if (orientationMur <= Math.PI / 2) {
        x1 = pos.getX() + 10;
      }
      double y1 = a1 * x1 + b1;
      ILineString axe1 = new GM_LineString (pos, new DirectPosition(x1, y1));
      DefaultFeature featAxe1 = popAxes.nouvelElement(axe1);
      featAxe1.setSchema(this.schemaAxe);
      
      if (nbOrientation > 1) {
        double a2 = Math.tan(orientationMur2); // + Math.PI / 2
        double b2 = pos.getY() - a2 * pos.getX();
        double x2 = pos.getX() + 2;
        if (orientationMur2 <= Math.PI / 2) {
          x2 = pos.getX() + 10;
        }
        double y2 = a2 * x2 + b2;
        ILineString axe2 = new GM_LineString (pos, new DirectPosition(x2, y2));
        DefaultFeature featAxe2 = popAxes.nouvelElement(axe2);
        featAxe2.setSchema(this.schemaAxe);
      }
      
      Layer layerAxe = projectFrame.addUserLayer(popAxes, "Axes", null);
      LineSymbolizer lineSymbol = (LineSymbolizer) layerAxe.getSymbolizer();
      lineSymbol.getStroke().setColor(new Color(0, 0, 0));
      lineSymbol.setUnitOfMeasurePixel();
      lineSymbol.getStroke().setStrokeWidth(1.0f);*/
      
      
      // ==============================================================================================================
      //    Layer 5 : diagram rose
      
      //Layer roseLayerCommune = projectFrame.addUserLayer(commune75Pop, "RoseChart", null);
      
      Map<IFeature, IDirectPosition> points = new HashMap<IFeature, IDirectPosition>();
      Map<IFeature, Double> radius = new HashMap<IFeature, Double>();
      points.put(featCentre, pos);
      radius.put(featCentre, 30.0);
      
      ThematicSymbolizer rosets = new ThematicSymbolizer();
      rosets.setUnitOfMeasureMetre();
      rosets.setRadius(radius);
      rosets.setPoints(points);
      List<DiagramSymbolizer> rosetsymbolizers = new ArrayList<DiagramSymbolizer>();
      
      DiagramSymbolizer symbolRose = new DiagramSymbolizer();
      symbolRose.setDiagramType("rosechart90");
      
      DiagramRadius dr = new DiagramRadius();
      dr.setValue(0.8);
      
      List<ThematicClass> themList = new ArrayList<ThematicClass>();
      
      for (int i = 0; i < 90; i++) {
        ThematicClass bi = new ThematicClass();
        bi.setClassLabel("angle " + i);
        Fill fillBlue = new Fill();
        fillBlue.setColor(Color.ORANGE);
        bi.setFill(fillBlue);
        Expression classValueBI = new PropertyName("angle " + i); 
        bi.setClassValue(classValueBI);
        themList.add(bi);
      }
      
      
      
      List<DiagramSizeElement> rosels = new ArrayList<DiagramSizeElement>();
      rosels.add(dr);
      symbolRose.setDiagramSize(rosels);
      symbolRose.setThematicClass(themList);
      rosetsymbolizers.add(symbolRose);
      
      rosets.setSymbolizers(rosetsymbolizers);
      layerFeuille.getStyles().get(0).getFeatureTypeStyles().get(0).getRules().get(0).getSymbolizers().add(rosets);
      
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void createSchemas() {
    
    // Schema 1
    this.featureTypeBati = new FeatureType();
    this.featureTypeBati.setTypeName("Bati");
    this.featureTypeBati.setGeometryType(IPolygon.class);
    
    AttributeType orientationGeneraleAttribute = new AttributeType("orientationGenerale", "double");
    this.featureTypeBati.addFeatureAttribute(orientationGeneraleAttribute);
    AttributeType orientationMurAttribute = new AttributeType("orientationMur", "double");
    this.featureTypeBati.addFeatureAttribute(orientationMurAttribute);
    AttributeType nbOrientationAttribute = new AttributeType("nbOrientation", "double");
    this.featureTypeBati.addFeatureAttribute(nbOrientationAttribute);
    AttributeType orientationMur2Attribute = new AttributeType("orientationMur2", "double");
    this.featureTypeBati.addFeatureAttribute(orientationMur2Attribute);
    
    this.schemaBati = new SchemaDefaultFeature();
    this.schemaBati.setFeatureType(this.featureTypeBati);
    this.featureTypeBati.setSchema(this.schemaBati);
    Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
    attLookup.put(new Integer(0), new String[] { orientationGeneraleAttribute.getNomField(), orientationGeneraleAttribute.getMemberName() });
    attLookup.put(new Integer(1), new String[] { orientationMurAttribute.getNomField(), orientationMurAttribute.getMemberName() });
    attLookup.put(new Integer(2), new String[] { nbOrientationAttribute.getNomField(), nbOrientationAttribute.getMemberName() });
    attLookup.put(new Integer(3), new String[] { orientationMur2Attribute.getNomField(), orientationMur2Attribute.getMemberName() });
    this.schemaBati.setAttLookup(attLookup);
    
    // Schema 2
    this.featureTypePPRE = new FeatureType();
    this.featureTypePPRE.setTypeName("PPRE");
    this.featureTypePPRE.setGeometryType(IPolygon.class);
    
    this.schemaPPRE = new SchemaDefaultFeature();
    this.schemaPPRE.setFeatureType(this.featureTypePPRE);
    this.featureTypePPRE.setSchema(this.schemaPPRE);
    Map<Integer, String[]> attLookupPPRE = new HashMap<Integer, String[]>(0);
    this.schemaPPRE.setAttLookup(attLookupPPRE);
    
    // Schema 3
    this.featureTypeAxe = new FeatureType();
    this.featureTypeAxe.setTypeName("Axes");
    this.featureTypeAxe.setGeometryType(ILineString.class);
    
    this.schemaAxe = new SchemaDefaultFeature();
    this.schemaAxe.setFeatureType(this.featureTypeAxe);
    this.featureTypeAxe.setSchema(this.schemaAxe);
    Map<Integer, String[]> attLookupAxe = new HashMap<Integer, String[]>(0);
    this.schemaAxe.setAttLookup(attLookupAxe);
    
    // Schema 4
    this.featureFeuille = new FeatureType();
    this.featureFeuille.setTypeName("Feuille");
    this.featureFeuille.setGeometryType(IPoint.class);
    
    // 
    Map<Integer, String[]> attLookupFeuille = new HashMap<Integer, String[]>(0);
    for (int i = 0; i < 90; i++) {
      AttributeType anglei = new AttributeType("angle " + i, "double");
      this.featureFeuille.addFeatureAttribute(anglei);
      attLookupFeuille.put(new Integer(i), new String[] { anglei.getNomField(), anglei.getMemberName() });
    }
    
    this.schemaFeuille = new SchemaDefaultFeature();
    this.schemaFeuille.setFeatureType(this.featureFeuille);
    this.featureFeuille.setSchema(this.schemaFeuille);
    
    
    this.schemaFeuille.setAttLookup(attLookupFeuille);
  }

}

