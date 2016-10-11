package fr.ign.cogit.geoxygene.sig3d.util;

import java.awt.Color;



/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.7
 * 
 * 
 * */
public class ColorLocalRandom {
  
  
  
  public static Color getRandomColor(Color c, double r1,double r2,double r3){
   
    
    int r =  c.getRed() + (int)  ( r1  * (0.5 - Math.random()) ); 
    int g =  c.getGreen() + (int)  (  r2  * (0.5 - Math.random())); 
    int b =  c.getBlue() + (int)  ( r3  * (0.5 - Math.random())); 
    
    
    
    r = Math.min(Math.max(r, 0), 255);
    g = Math.min(Math.max(g, 0), 255);
    b = Math.min(Math.max(b, 0), 255);
    
    return new Color(r,g,b);
    
    
    
  }

}
