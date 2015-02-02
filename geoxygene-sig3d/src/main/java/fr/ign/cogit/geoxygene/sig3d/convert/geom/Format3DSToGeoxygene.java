package fr.ign.cogit.geoxygene.sig3d.convert.geom;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Geometry;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.TriangleStripArray;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.convert.java3d.ConversionJava3DGeOxygene;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

/**
 * Classe permettant de convertir les géométrie d'une scène 3DS en géométrie
 * Géoxygene
 * 
 * @author MBrasebin
 * 
 */
public class Format3DSToGeoxygene {

  public static IFeatureCollection<IFeature> conversionToFeature(Group g) {
    List<IMultiSurface<IOrientableSurface>> iOS = conversion(g);

    int nbElem = iOS.size();

    IFeatureCollection<IFeature> iFColl = new FT_FeatureCollection<IFeature>();

    for (int i = 0; i < nbElem; i++) {

      iFColl.add(new DefaultFeature(iOS.get(i)));
    }

    return iFColl;

  }

  /**
   * Conversion en prenant en compte une matrice m (3 * 4). On attend une
   * rotation + une translation
   * @param g
   * @param m
   * @return
   */
  public static IFeatureCollection<IFeature> conversionToFeature(Group g,
      Matrix m) {
    List<IMultiSurface<IOrientableSurface>> iOS = conversion(g);

    IFeatureCollection<IFeature> iFColl = new FT_FeatureCollection<IFeature>();

    for (IMultiSurface<IOrientableSurface> iMS : iOS) {

      iFColl.add(new DefaultFeature(transformGeom(iMS, m)));
    }

    return iFColl;

  }
  
  
  public static IFeatureCollection<IFeature> conversionToFeatureBidon(Group g,
      Matrix m) {
    List<IMultiSurface<IOrientableSurface>> iOS = conversion(g);

    IFeatureCollection<IFeature> iFColl = new FT_FeatureCollection<IFeature>();

    for (IMultiSurface<IOrientableSurface> iMS : iOS) {

      iFColl.add(new DefaultFeature(transformBidon(iMS, m)));
    }

    return iFColl;

  }
  
  
  private static IMultiSurface<IOrientableSurface> transformBidon(
      IMultiSurface<IOrientableSurface> iMS, Matrix m) {

    
    IMultiSurface<IOrientableSurface> iMSTrans = new GM_MultiSurface<IOrientableSurface>();
    
    for (IOrientableSurface iO : iMS) {

      IDirectPositionList dpl = iO.coord();
      IDirectPositionList dplTrans = new DirectPositionList();

      for (IDirectPosition dp : dpl) {
        dplTrans.add(transformBidon((IDirectPosition)dp.clone()));
      }
      
      
      iMSTrans.add(new GM_Polygon(new GM_LineString(dplTrans)));

    }

    
    return iMSTrans;
  }

  private static IMultiSurface<IOrientableSurface> transformGeom(
      IMultiSurface<IOrientableSurface> iMS, Matrix m) {

    
    IMultiSurface<IOrientableSurface> iMSTrans = new GM_MultiSurface<IOrientableSurface>();
    
    for (IOrientableSurface iO : iMS) {

      IDirectPositionList dpl = iO.coord();
      IDirectPositionList dplTrans = new DirectPositionList();

      for (IDirectPosition dp : dpl) {
        dplTrans.add(transformPoint((IDirectPosition)dp.clone(), m));
      }
      
      
      iMSTrans.add(new GM_Polygon(new GM_LineString(dplTrans)));

    }

    
    return iMSTrans;
  }
  
  
  private static IDirectPosition transformBidon(IDirectPosition dp) {
    
IDirectPosition dpOut =new DirectPosition(dp.getX() * 4970.125 ,
    
   dp.getY() * 4970.125, dp.getZ() * 4970.125);
    
    return dpOut;
    
    
  }

  private static IDirectPosition transformPoint(IDirectPosition dp, Matrix m) {

    double[][] matTemp = new double[4][1];
    matTemp[0][0] = dp.getX();
    matTemp[1][0] = dp.getY();
    matTemp[2][0] = dp.getZ();
    matTemp[3][0] = 1;

    Matrix m2 = new Matrix(matTemp);

    m2 = m.times(m2);

    return new DirectPosition(m2.get(0, 0), m2.get(1, 0), m2.get(2, 0));

  }

  public static List<IMultiSurface<IOrientableSurface>> conversion(Group g) {

    List<IMultiSurface<IOrientableSurface>> lOS = new ArrayList<IMultiSurface<IOrientableSurface>>();

    int nbC = g.numChildren();

    for (int i = 0; i < nbC; i++) {
      Node nTemp = g.getChild(i);

      if (nTemp instanceof Shape3D) {

        Shape3D s = (Shape3D) nTemp;

        int nbGeom = s.numGeometries();

        for (int j = 0; j < nbGeom; j++) {

          Geometry geom = s.getGeometry(j);

          if (geom instanceof TriangleArray) {

            lOS.add(new GM_MultiSurface<IOrientableSurface>(
                ConversionJava3DGeOxygene
                    .fromTriangleArrayToFacettes((TriangleArray) geom)));

          } else if (geom instanceof TriangleStripArray) {

            lOS.add(new GM_MultiSurface<IOrientableSurface>(
                ConversionJava3DGeOxygene
                    .fromTriangleStripArrayToFacettes((TriangleStripArray) geom)));
          } else {

            if (geom != null) {
              System.out.println("Classe non reconnue : "
                  + geom.getClass().getName());

            }

          }
        }
      } else if (nTemp instanceof Group) {

        lOS.addAll(Format3DSToGeoxygene.conversion((Group) nTemp));

      }

    }
    // Format3DSToGeoxygene.applyRotationX(lOS);
    return lOS;

  }

  private static void applyRotationX(List<IOrientableSurface> lOS) {
    int nbEl = lOS.size();

    for (int i = 0; i < nbEl; i++) {

      IDirectPositionList dpl = lOS.get(i).coord();

      int dplS = dpl.size();

      if (dpl.get(0) == dpl.get(dplS - 1)) {

        dplS--;
      }

      for (int j = 0; j < dplS; j++) {

        IDirectPosition dp = dpl.get(j);
        double y = dp.getY();
        double z = dp.getZ();

        dp.setZ(y);
        dp.setY(-z);

      }

    }

  }

}
