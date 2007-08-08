/*
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
 *  
 */

package fr.ign.cogit.geoxygene.spatial.coordgeom;

//import SRC.SC_CRS -> non implemente;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
   

/**
 * Point connu par ses coordonnées.
 * Les coordonnées sont connues par un tableau, de longueur la dimension des géométries (2D ou 3D).
 * Dans cette version, tous les DirectPosition sont en 3D.
 * Si on est en 2D, la 3ieme coordonnée vaut NaN.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 * 19.02.2007 : correction de bug méthode move(double offsetX, double offsetY, double offsetZ)
 *
 */


public class DirectPosition {
      
    //////////////////////////////////////////////////////////////////////////////////////////
    // Attribut CRS //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////    
    /** Identifiant du système de coordonnées de référence (CRS en anglais).
      * Lorsque les DirectPosition servent à définir un GM_Object, cet attribut doit être null.
      * En effet, il est alors porté par le GM_Object. */
    // Dans la norme ISO, cet attribut est une relation qui pointe vers la classe SC_CRS (non implémentée)
    protected int CRS;  
    
    /** Renvoie l' identifiant du système de coordonnées de référence. */
    public int getCRS() {return this.CRS;}   
    
    /** Affecte une valeur au système de coordonnées de référence. */
    public void setCRS(int crs) {CRS = crs; }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Attribut coordinate et dimension //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////    
    /** Tableau des coordonnées du point. */
    protected double[] coordinate = new double[3];
        
    /** Dimension des coordonnées (2D ou 3D) - dimension = coordinate.length  */
    protected int dimension = 3;
    
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Constructeurs /////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////        
    /** Constructeur par défaut (3D): crée un tableau de coordonées à 3 dimensions, vide.*/
    public DirectPosition() {
        coordinate[0] = Double.NaN;      
        coordinate[1] = Double.NaN;      
        coordinate[2] = Double.NaN;        
    }
            
    /** Constructeur d'un DirectPosition à n dimensions : crée un tableau de coordonées à n dimensions, vide.*/
   /*public DirectPosition(int n) {
        coordinate = new double[n];
        dimension = n;
    }*/
    
    /** Constructeur à partir d'un tableau de coordonnées. 
      * Si le tableau passé en paramètre est 2D, la 3ième coordonnée du DirectPosition vaudra NaN.
      * Le tableau est recopié et non passé en référence. */
    public DirectPosition(double[] coord) {
        coordinate[0] = coord[0];
        coordinate[1] = coord[1];
        if (coord.length == 3) coordinate[2] = coord[2];
            else coordinate[2] = Double.NaN;
    }
        
    /** Constructeur à partir de 2 coordonnées. */
    public DirectPosition(double X, double Y) {
        coordinate[0] = X;
        coordinate[1] = Y;
        coordinate[2] = Double.NaN;        
    }
        
    /** Constructeur à partir de 3 coordonnées. */
    public DirectPosition(double X, double Y, double Z) {
        coordinate[0] = X;
        coordinate[1] = Y;
        coordinate[2] = Z;
    }
    
    
          
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Méthodes get //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    /** Renvoie le tableau des coordonnées. */
    public double[] getCoordinate() {
        return this.coordinate;
    }
        
    /** Renvoie la dimension (toujours 3). */
    public int getDimension () {
        return this.dimension;
    }
    
    /** Renvoie la i-ème coordonnées (i=0 pour X, i=1 pour Y, i=3 pour Z). */
    public double getCoordinate(int i) {
         return this.coordinate[i];
    }
    
    /** Renvoie X (1ère coordonnee du tableau, indice 0). */
    public double getX() {
        return this.coordinate[0];
    }
        
    /** Renvoie Y (2ième coordonnée du tableau, indice 1). */
    public double getY() {
        return this.coordinate[1];
    }
        
    /** Renvoie Z (3ième coordonnée du tableau, indice 2). */
    public double getZ() {
        return this.coordinate[2];
    }
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Méthodes set //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    /** Affecte les coordonnées d'un tableau des coordonnées (2D ou 3D). 
     * Si le tableau passé en paramètre est 2D, la 3ième coordonnée du DirectPosition vaudra NaN.
     * Le tableau est recopié et non passé en référence. */
    public void setCoordinate(double[] coord)  {
        coordinate[0] = coord[0];
        coordinate[1] = coord[1];
        if (coord.length == 3) coordinate[2] = coord[2];
            else coordinate[2] = Double.NaN;
    }
        
    /** Affecte la position d'un point géométrique. Le point passé en paramètre doit avoir la même dimension que this.*/
    public void setCoordinate(GM_Point thePoint) {
       DirectPosition pt = thePoint.getPosition();
       double[] coord = pt.getCoordinate();
       /*if (dimension == coord.length)
            for (int i=0; i<coord.length; i++) coordinate[i] = coord[i];*/
       setCoordinate(coord);
    }
        
    /** Affecte une valeur à la i-ème coordonnées (i=0 pour X, i=1 pour Y, i=3 pour Z.). */
    public void setCoordinate(int i, double x)  { 
        coordinate[i] = x;
    }
        
    /** Affecte une valeur à X et Y. */
    public void setCoordinate(double x, double y) {
        coordinate[0] = x;
        coordinate[1] = y;
        coordinate[2] = Double.NaN;
    }
        
    /** Affecte une valeur à X, Y et Z. */
    public void setCoordinate(double x, double y, double z)  {
        coordinate[0] = x;
        coordinate[1] = y;
        coordinate[2] = z;
    }
    
    /** Affecte une valeur à X (1ère coordonnée du tableau). */
    public void setX(double x) {
        coordinate[0] = x;
    }
        
    /** Affecte une valeur à Y (2ième coordonnée du tableau). */
    public void setY(double y) {
        coordinate[1] = y;
    }
        
    /** Affecte une valeur à Z (3ième coordonnée du tableau). */
    public void setZ(double z)  {
        coordinate[2] = z;
    }
    
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Méthodes move /////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////    
    /** Déplace le point suivant toutes les dimensions. Le point passé en paramètre doit avoir la même dimension que this.*/
    public void move(DirectPosition offsetPoint)  {
        if (dimension == offsetPoint.getDimension())
            for (int i=0; i<dimension; i++) 
                coordinate[i] += offsetPoint.getCoordinate(i);
    }
        
    /** Déplace le point suivant X et Y. */
    public void move(double offsetX, double offsetY) {
        coordinate[0] += offsetX;
        coordinate[1] += offsetY;
    }
        
    /** Déplace le point suivant X, Y et Z.
	 * coordinate.length<3 -> coordinate.length<4
	 */
    public void move(double offsetX, double offsetY, double offsetZ) {
        if (coordinate.length<4) {
            coordinate[0] += offsetX;
            coordinate[1] += offsetY;
            coordinate[2] += offsetZ;
        }
    }
    
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Méthode equals ////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////    
    /** Indique si self et le point passé en paramètre sont égaux, à une tolérance près.
      * Si les 2 points ont une troisième dimension affectée, on teste cette dimension.
      * Tolérance est un double qui doit être > 0. */
    public boolean equals(DirectPosition pt, double tolerance)  {
        double x1, x2;
        for (int i=0; i<=1; i++) {
            x1 = coordinate[i];
            x2 = pt.getCoordinate(i);
            if ( (x2 > x1+tolerance) || (x2 < x1-tolerance) ) return false;
        }
        if (!Double.isNaN(this.getZ()))
             if (!Double.isNaN(pt.getZ())) {
                x1 = coordinate[2];
                x2 = pt.getCoordinate(2);
                if ( (x2 > x1+tolerance) || (x2 < x1-tolerance) ) return false;      
             }
        return true;
    }    
    
    /** Indique si self et le point passé en paramètre sont égaux EN 2D, à une tolérance près.
      * Tolérance est un double qui doit être > 0. */
    public boolean equals2D(DirectPosition pt, double tolerance)  {
        double x1, x2;
        for (int i=0; i<=1; i++) {
            x1 = coordinate[i];
            x2 = pt.getCoordinate(i);
            if ( (x2 > x1+tolerance) || (x2 < x1-tolerance) ) return false;
        }
        return true;
    }        
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Méthode clone /////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////    
    /** Clone le point. */
    public Object clone() {
        DirectPosition dp = new DirectPosition(this.getCoordinate());
        return dp;
    }
        
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Méthode toGM_Point ////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////      
    /** Créée un GM_Point à partir de this.*/
    public GM_Point toGM_Point() {
        return new GM_Point(this);
    }
    

    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Méthode d'affichage ///////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    /** Affiche les coordonnées du point (2D et 3D). */
    public String toString () {
         if (Double.isNaN(this.getZ()))
             return new String("DirectPosition - X : "+this.getX()+"     Y : "+this.getY());
         else 
             return new String("DirectPosition - X : "+this.getX()+"     Y : "+this.getY()+"     Z : "+this.getZ());
    }
    

}
