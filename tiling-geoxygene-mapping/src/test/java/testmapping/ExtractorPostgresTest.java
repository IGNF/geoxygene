package testmapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.MathContext;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType;
import fr.ign.cogit.mapping.storage.database.extractor.sql.postgres.PostgresExtractor;
/*
 * @author Dr Tsatcha D.
 */
public class ExtractorPostgresTest {
    
protected String tableName = "ref_patte_d_oie";

    
    
    
@Test
    public void viewInfo(){
    //   PostgresExtractor myextractor = new PostgresExtractor(tableName);
//       IPopulation<IFeature> contents = myextractor.getTableContains();
//
//       System.out.println("NB features = " + contents.size()); // 4054
//       System.out.println(contents.getFeatureType().getGeometryType().getSimpleName()); // GM_MultiSurface  GM_Point
//       
//       for (int i = 0; i < contents.size(); i++) {
 //       DefaultFeature feature = (DefaultFeature) contents.get(i);
//         System.out.println(feature.getSchema().getFeatureType().getGeometryType());
//         if (i < 10) {
//           System.out.println(feature.getGeom().toString());
//         }
//         
//         
////         for (int j=0; j < feature.getSchema().getFeatureType().getFeatureAttributes().size(); j++) {
////           AttributeType attr = feature.getSchema().getFeatureType().getFeatureAttributeI(j);
////           System.out.println(attr.getNomField());
////         }
////         
//         System.out.println(feature.getAttributes().length);
//       }
//       
//   }
       
   
   PostgresExtractor myextractor = new PostgresExtractor();
   Map<Integer, String> entry =  myextractor.readTableGeometry(tableName,"geometrie");
//   for(Integer key :  entry.keySet()) {
//       System.out.println(entry.get(key));
// }
//   
   
  // IPopulation<IFeature> contents = myextractor.readTableGeometry(tableName);
   
//   for (int i = 0; i < contents.size(); i++) {
//              DefaultFeature feature = (DefaultFeature) contents.get(i);
//               System.out.println(feature.getSchema().getFeatureType().getGeometryType());
//              
//               //Geometry g = (Geometry) feature.getAttribute(0) ;
//               
//            //   System.out.println(g.toText());
//
//               
//               if (i < 10) {
//                 System.out.println(feature.getGeom().toString());
//               }
//               
//               
//              for (int j=0; j < feature.getSchema().getFeatureType().getFeatureAttributes().size(); j++) {
//                 fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType attr = feature.getSchema().getFeatureType().getFeatureAttributeI(j);
//               }
//               
//               System.out.println(feature.getAttributes().length);
//             }
   /*
    * Test de parcours des contenus de la base de données postgis
    */
             
//   
//   Map<Integer, Map<String, String>>test= myextractor.readAllTables();
//   
//   for( Integer table :  test.keySet()){
//       
//       Map<String, String> entry =  test.get(table);
//       
//        Map<Integer, String> mcontent=myextractor.readTableGeometry(entry.get("table"),entry.get("geometry_colum"));
//     
//        for(Integer key :  mcontent.keySet()) {
//            System.out.println(mcontent.get(key));
//        }
//   }
//   
   
   // tous ses objets geometriques dans la base qui nous a été fournie marche
  String point = "POINT (711042.185243728 1619000)";

  String poly="POLYGON((729332.355903467 1601027.8820766,729334.327446237 1601045.7510704,729322.026086829 1601047.10830475,729320.05456029 1601029.2393176,729332.355903467 1601027.8820766)(711042.185243728 1619000))";

  String linestring= "LINESTRING(729348.509028449 1601034.66975591,729347.430141434 1601023.29994659,729365.213957044 1601021.61243124,729366.292844059 1601032.98224056,729348.509028449 1601034.66975591)";

  String multi ="MULTIPOLYGON(((706750.362868165 1620484.9791133,706764.818871027 1620490.69599431,706761.214603976 1620499.80995326,706746.75859482 1620494.09309536,706750.362868165 1620484.9791133)))";
   multi ="MULTIPOLYGON(((706725.053210402 1620485.82324342,706718.279264562 1620491.51098956,706720.787823866 1620494.49859465,706717.551029732 1620497.2163665,706710.292354529 1620488.57149205,706720.303085049 1620480.16597895,706725.053210402 1620485.82324342)))";

  //coordinateToString (multi);

  
//  Map<Integer, Coordinate[]> testmap=coordinateToString (multi); 
//
//   for (Integer key : testmap.keySet()  ){
//       
//       Coordinate[] mp =testmap.get(key);
//       System.out.println(key);
//        for (Coordinate c : mp){
//            
//            System.out.println(c.x + " "+ c.y);
//        }
//   }
////   
//  coordinateToString (point).toString();
//   
//   String linestring="LINESTRING (711042.185243728 1619000,711029.999969107 1619007.35999109,711019.99999332 1619023.64950709,711001.770144774 1619044.99968275,710989.999512804 1619055.38980976,710980.000090175 1619072.30988711,710980.000090175 1619084.26025003,710984.500023963 1619089.99997914,710984.800388321 1619120.00016809,710991.680004365 1619145.00032842,710992.530206975 1619155.00040293,710989.240027974 1619165.00046015,710975.000102282 1619182.53998816,710971.119704553 1619189.99961793,710966.160097135 1619219.99980688,710959.640033268 1619244.99996722,710955.000150709 1619252.16049135,710950.000162816 1619255.03034294,710930.000211243 1619253.62978041,710923.439766901 1619255.00002444,710919.4000599 1619260.00007033,710914.970377075 1619285.00023067,710906.950150893 1619300.00033379,710899.999730727 1619308.84030223,710885.000320205 1619317.86017656,710871.889941506 1619329.9995029,710854.999839689 1619339.52967525,710840.000429166 1619345.30982614,710825.080120123 1619359.99970913,710804.999960757 1619367.12991476,710789.999997078 1619380.65970898,710771.400086368 1619389.99988079,710759.379980502 1619399.99997258,710750.000093932 1619410.79024673,710740.000118146 1619416.77049279,710735.000130253 1619423.99970889,710735.000130253 1619425.61955333,710740.000118146 1619425.76001406,710750.000093932 1619422.36973524,710780.000021291 1619416.63003206,710785.000009184 1619417.19996452,710789.499942973 1619420.00008702,710793.629814597 1619430.00014424,710796.380112174 1619450.00029325,710804.999960757 1619474.84988093,710807.819956142 1619499.99961138,710804.999960757 1619507.78966784,710793.660238242 1619524.99975443,710794.350025266 1619534.99981165,710792.209860075 1619549.99993205,710796.16991244 1619570.00004649,710797.270142103 1619585.00016689,710796.419939492 1619590.0001955,710787.829961398 1619605.00028133,710777.910193405 1619630.00042439,710772.519676532 1619659.99962807,710775.000033398 1619668.43038321,710787.839918228 1619679.99974251,710791.179881376 1619694.99982834,710794.999984971 1619698.67006063,710816.230158147 1619704.99988556)";
//   coordinateToString (linestring);
//   
//   String multi ="MULTIPOLYGON ((706750.362868165 1620484.9791133,706764.818871027 1620490.69599431,706761.214603976 1620499.80995326,706746.75859482 1620494.09309536,706750.362868165 1620484.9791133))";
//   coordinateToString (multi);

   }  
   
public Map<Integer, Coordinate[]> coordinateToString(String textDabase) {
    
    // le nom de la geometrie
    String geoname = null;
    int featureOrder = 0;
    double lon, lat;
    int i = 0;
    // liste des coordonnées
    List<Coordinate> coords = new ArrayList<>();
    Map<Integer, Coordinate[]> content = new HashMap<Integer, Coordinate[]>();
    // permet de separer le nom et les composantes géometriques
    // de l'objet dans postgis c'est (... par exemple POINT(10,10)...
    // dans virtuoso c'est l'espace " ".
    String[] twopart = textDabase.split("\\(");
    int begin = twopart[0].length();
    String use = textDabase.substring(begin, textDabase.length());
    // on récupère la prémière composante de la segmentation
    geoname = twopart[0];
    
   // System.out.println(geoname);
    if(geoname.contains("MULTIPOLYGON")){
        // on enlève la première et la dernière parenthèse
        use=use.substring(1, use.length()-1);
    }
    
    

    while (i < use.length()) {

        /*
         * Permet de gerer la sequence des parenthèses ouvrantes des
         * polygones
         */
        int j = i;
        while (use.charAt(i) == '(' || use.charAt(i) == ','
                && i < use.length()) {
            i++;

        }
        // on recherche des points à recupérer
        int ptl = 0;
        String s = "";
        String[] ptc = { "", "" };
        while (use.charAt(i) != ')' && use.charAt(i) != ','
                && i < use.length()) {

            // while (use.charAt(i) != ',') {
            // System.out.println(use.charAt(i));

            while (use.charAt(i) != ' ' && ptl == 0 && i < use.length()) {
                s = s + use.substring(i, i + 1);
                i++;

            }
            if ((use.charAt(i) == ' ')) {
                ptc[0] = s;
             s = "";
                ptl++;
                i++;
            }
            // System.out.println(s);
            s = s + use.substring(i, i + 1);
            i++;
        }
        if (use.charAt(i) == ',') {
            ptc[1] = s;
            s = "";
            lon = Double.parseDouble(ptc[0]);
            lat = Double.parseDouble(ptc[1]);
            MathContext mc = new MathContext(0);
             Coordinate c = new Coordinate(lon, lat);
            coords.add(c);
            ptl = 0;
         
            i++;
        }
      
        if (use.charAt(i) == ')') {
           ptc[1] = s;
            s = "";
            lon = Double.parseDouble(ptc[0]);
            lat = Double.parseDouble(ptc[1]);
           ptl = 0;
            // coordfeature.add(pt);
            Coordinate c = new Coordinate(lon, lat);
            coords.add(c);
            featureOrder++;
            if (coords.size() > 0) {
                content.put(featureOrder,
                        coords.toArray(new Coordinate[] {}));
                coords = new ArrayList<Coordinate>();
                if (i < use.length()) {
                    i++;
                } else {
                    break;
                }

                i++;
            }
          
        }
    }
    return content;
}
   
}