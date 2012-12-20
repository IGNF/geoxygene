/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema;

import java.awt.Color;

/**
 * Classe pour stocker tous les attributs d'un symbol
 * 
 * @author KJaara
 * 
 */
public class SymbolShape {

  public int symbolId;
  public String varName = null;
  public String description = null;
  public double ext_width = 0;
  public Color ext_colour = Color.black;
  public String ext_endstyle = "";
  public String ext_joinstyle = "";
  public int ext_priority = -1;
  public boolean ext_isdashed = false;
  public double ext_dash = -1;
  public double ext_dash_space = -1;
  public boolean has_int = false;
  public double int_width = 0;
  public Color int_colour = Color.black;
  public int int_priority = -1;
  public boolean int_isdashed = false;
  public double int_dash = -1;
  public double int_dash_space = -1;
  public String int_endstyle = null;
  public double sep_width = 0;
  public Color sep_colour = Color.black;
  public int sep_priority = -1;

}
