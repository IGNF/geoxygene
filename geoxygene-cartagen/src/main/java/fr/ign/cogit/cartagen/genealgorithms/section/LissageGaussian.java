/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.section;

/*
 * Created on 18 août 2005
 */

import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


/**
 * @author Mechouche
 */
public class LissageGaussian {
    
    /*public static void main (String args[]) {
        DirectPositionList dpl = new DirectPositionList();

        //Ligne non lissée
        dpl = new DirectPositionList();
        dpl.add(new DirectPosition(0,0));
        dpl.add(new DirectPosition(2,1));
        dpl.add(new DirectPosition(4,3));
        dpl.add(new DirectPosition(6,6));
        dpl.add(new DirectPosition(8,3));
        dpl.add(new DirectPosition(10,1));
        dpl.add(new DirectPosition(12,0));
        GM_LineString line = new GM_LineString(dpl);

        GM_LineString lineGauss = LissageGauss.AppliquerLissageGaussien(line,4,1);
    }*/
    
    public static GM_LineString AppliquerLissageGaussien(GM_LineString polyLigne, double sigma, double pas, boolean estCeUnPolygone){
        //---------------------------------------------
        int n_pts_dans_polyligneline = polyLigne.numPoints();
        
        double sigma2 = sigma;  
        int k1 = (int)((4.*sigma2 - 1)/pas);    
        GM_LineString polyLigneLissee = new GM_LineString();
        GM_LineString poly = new GM_LineString();
        
        poly = polyLigne;
        if(sigma < 0) return polyLigneLissee; 
        //echantillonage de la polyligne échantillonée
        //--------------------- traiter la cas des trous --------------------------
        DirectPosition d0 = new DirectPosition();       
        d0.setCoordinate(poly.getControlPoint(0).getX(), poly.getControlPoint(0).getY());
        int nb = poly.sizeControlPoint();
//        int nb2 = nb;
       // System.out.println("nombre de points :"+nb);
        boolean val = true;
        while(nb > 0 && val && val){
            nb --;
            if(d0.getX()==poly.getControlPoint(nb).getX() && d0.getY()==poly.getControlPoint(nb).getY()){
                val = false;
            }
        }
        //-------------------------------------------------------------------------
        GM_LineString polyLigneEchantillone = (GM_LineString) Operateurs.echantillone(polyLigne, pas);
        //----- Voir combien de sous-segments contient chaque segment ---------------------------       
        int nombreSegment = polyLigne.sizeControlPoint()-1;
        //System.out.println(nombreSegment);
        int[] segments = new int[nombreSegment];
        for(int i = 0; i< nombreSegment; i++){
        //GM_LineSegment  g =(GM_LineSegment) polyLigne.getSegment(i);      
        double d = polyLigne.getControlPoint(i).distance(polyLigne.getControlPoint(i+1));
        int n;
        if((int)(d%pas) ==0)
             n = (int)(d/pas);
        else n = (int)(d/pas) + 1;
        segments[i] = n;
        //System.out.println(n);
        }
        //---------------------------------------------------------------------------------------
        //liste des direct positions        
        DirectPositionList dpl = (DirectPositionList) polyLigneEchantillone.coord();
        int nbrePoints = dpl.size();
        //récupération du i-ème point de la dpl
//        DirectPosition premierPoint = (DirectPosition) dpl.get(0);       
        //récupération des coordonnées des direct position
//        double x = premierPoint.getX();
//        double y = premierPoint.getY();
        
        //----- Réévaluation de sigma dans le cas où k1 > nbre points dans la ligne -----------
        if(k1 < 1) k1 = 1;
        if(k1 > nbrePoints){
            sigma2 = (float)(n_pts_dans_polyligneline / 4.0);
            k1 = (int)((4.*sigma2 - 1)/pas);
            }
        
        //--- définition des coefficients de Gauss c1 et c2
        float c2 = (float)(-1.0/(2.0 * Math.pow(sigma2, 2)));
        float c1 = 1;
        for(int k = 0; k < k1; k++){
            c1 = (float)(c1 + 2.0 * Math.exp(c2 * Math.pow(k * pas, 2)));
            }
        c1 = 1 / c1;
        
        //------------- Calculer les poids des points voisins en utilisant c1 et c2 -----------------
        float[] poids = new float[k1];
        for( int k = 0; k < k1; k++){
            poids[k] = (float)(c1 * Math.exp(c2 * Math.pow((k * pas), 2)));
            }       
           
        //------------- Extention de la ligne décomposée aux deux extrêmités -----------------------

        double x1 = dpl.get(0).getX();
        double y1 = dpl.get(0).getY();
        //DirectPosition dp = new DirectPosition();
//        int nombreDecomposition = nbrePoints;//9000; // 9000 arbitrairement
        DirectPositionList ligneEtendue = new DirectPositionList();
        DirectPositionList ligneLissee2 = new DirectPositionList();
        DirectPositionList ligneLissee = new DirectPositionList();      
        
        for(int i = k1; i >0; i--){
        //erreur ? for(int i = 0; i < k1; i++){
            DirectPosition dp = new DirectPosition();
//            double xi = dpl.get(i).getX();
//            double yi = dpl.get(i).getY();
            dp.setX(2 * x1 - dpl.get(i +1).getX());
            dp.setY(2 * y1 - dpl.get(i +1).getY());
            //dp.setCoordinate(2 * x1 - dpl.get(i + 1).getX(), 2 * y1 - dpl.get(i + 1).getY());
            ligneEtendue.add(dp);
        }
        
            for(int l = 0; l < nbrePoints; l++){
                //erreur ? ligneEtendue.add(l+k1, dpl.get(l));
                ligneEtendue.add(dpl.get(l));
            }
            
            for(int i = 0; i <= k1; i++){
                DirectPosition dp = new DirectPosition();
//                double xi = dpl.get(i).getX();
//                double yi = dpl.get(i).getY();
                //errreur ? dp.setCoordinate(2 * dpl.get(nbrePoints).getX() - dpl.get(nbrePoints - i).getX(), 2 * dpl.get(nbrePoints).getY() - dpl.get(nbrePoints - i).getY());
                dp.setX(2 * dpl.get(nbrePoints-1).getX() - dpl.get(nbrePoints - 1 - i).getX());
                dp.setY(2 * dpl.get(nbrePoints-1).getY() - dpl.get(nbrePoints - 1 - i).getY());
                //dp.setCoordinate(2 * dpl.get(nbrePoints-1).getX() - dpl.get(nbrePoints - 1 - i).getX(), 2 * dpl.get(nbrePoints-1).getY() - dpl.get(nbrePoints - 1 - i).getY());
                //erreur ? ligneEtendue.add(nbrePoints+ k1 + i, dp);
                ligneEtendue.add(dp);
            }
            
        
        //------------- Calculer les coordonnées lissées de chaque point de la ligne décomposée ------- 
        for(int j = k1; j< k1 + nbrePoints+1; j++){
            DirectPosition dp = new DirectPosition();
            double x0 = poids[0] * ligneEtendue.get(j).getX();
            double y0 = poids[0] * ligneEtendue.get(j).getY();
            for(int k = 0; k < k1; k++){            
                x0 = x0 + poids[k] * (ligneEtendue.get(j - k).getX() + ligneEtendue.get(j + k).getX());
                y0 = y0 + poids[k] * (ligneEtendue.get(j - k).getY() + ligneEtendue.get(j + k).getY());             
            }
            
            if (x0<Math.pow(10,-3) && x0>-Math.pow(10,-3)) x0 = 0;
            if (y0<Math.pow(10,-3) && y0>-Math.pow(10,-3)) y0 = 0;
            
            dp.setX(x0);
            dp.setY(y0);
            ligneLissee2.add(dp);
            }
        //------------ Extraction des points homologues des points originaux ----------------------------
        //int[] segments = new int[nbrePoints];//Nombre d'échantillons dans chaque segment initial
        int l = 0;
        ligneLissee.add(ligneLissee2.get(0));
        for(int i = 0; i< nombreSegment; i++){
            l = l + segments[i];
            ligneLissee.add(ligneLissee2.get(l));
            }
                
        if(estCeUnPolygone){ 
            ligneLissee.add(nb+1, ligneLissee.get(0));
            ligneLissee.add(ligneLissee.get(0));
        }
        
        
        //for(int i = 0; i < nbrePoints; i++)
            //polyLigneLissee.setControlPoint(i, ligneLissee2.get(i));
        
        //mieux
        polyLigneLissee = new GM_LineString(ligneLissee);
        //System.out.println("nb nb nb"+nb);
        //System.out.println(polyLigneLissee.coord());
        return polyLigneLissee;     
    }
}
