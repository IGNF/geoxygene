package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromGeomToSurface;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

public class SkeletonCalculation {

  public static void main(String[] args) {

    String f = "E:/mbrasebin/Donnees/Strasbourg/ZoneTest/ExpBDH/RetrieveDatabase/destroyedBuildings.shp";

    String fout = "E:/mbrasebin/Donnees/Strasbourg/ZoneTest/ExpBDH/RetrieveDatabase/destroyedBuildingsOut2.shp";
    String foutProb = "E:/mbrasebin/Donnees/Strasbourg/ZoneTest/ExpBDH/RetrieveDatabase/destroyedBuildingsProb.shp";
    
    
    
 

    IFeatureCollection<IFeature> iFC = ShapefileReader.read(f);

    IFeatureCollection<IFeature> iFCOut = new FT_FeatureCollection<IFeature>();
    IFeatureCollection<IFeature> iFCOutProb = new FT_FeatureCollection<IFeature>();

    
   
    
    int count = 0;

    double[] angles = new double[8];

    for (int i = 0; i < angles.length; i++) {
      angles[i] = Math.PI / 3 * Math.random();
    }

    for (IFeature feat : iFC) {

      System.out.println(count++);

      List<IOrientableSurface> lOS = FromGeomToSurface.convertGeom(feat
          .getGeom());

      for (IOrientableSurface oS : lOS) {
        
        
      //  IPolygon poly = Filtering.DouglasPeuckerPoly((IPolygon) oS, 2);

   //     filteredFeat.add(new DefaultFeature((IPolygon) oS));

        CampSkeleton sC = new CampSkeleton((IPolygon) oS);
        // iFCOut.addAll(sC.ct.getPopFaces());
        
        
        List<Arc> lArcsOut = sC.getIncludedArcs();
        
        if(lArcsOut == null || lArcsOut.size() == 0){
          iFCOutProb.add(feat);
        }else{

          
          
          
          
          
          iFCOut.addAll(lArcsOut);
  
        }
        
      }

    }

    ShapefileWriter.write(iFCOut, fout);
    ShapefileWriter.write(iFCOutProb, foutProb);
//  ShapefileWriter.write(filteredFeat, ffiltered);


  }

}
