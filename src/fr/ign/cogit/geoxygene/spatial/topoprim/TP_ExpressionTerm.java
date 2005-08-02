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

package fr.ign.cogit.geoxygene.spatial.topoprim;

/**
  * non utilisé. Cette classe est cependant présente dans la norme.
  * Terme de l'expression polynomiale TP_Expression : un coefficient (égal à +1, -1 ou 0) 
  * et une variable (TP_DirectedTopo).
  * Il me parait plus simple d'utiliser directement le TP_DirectedTopo comme variable, et 
  * l'orientation comme coefficient.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class TP_ExpressionTerm {
    
    /** Le coefficient. Vaut +1 par défaut.*/
    protected int coefficient = 1;
    
    
    /** Renvoie le coefficient. */
    public int getCoefficient () {return this.coefficient;}
    

    /**La variable.*/
    protected TP_DirectedTopo variable;
    
    
    /** Renvoie la variable. */
    public TP_DirectedTopo getVariable () {return this.variable;}
    
    
    /** Constructeur par défaut. */
    public TP_ExpressionTerm() {
        coefficient = 1;
        variable = null;
    }
    
    
    /** Constructeur à partir d'un TP_DirectedTopo. Affecte +1 au coefficient. */
    public TP_ExpressionTerm (TP_DirectedTopo dt) {
        coefficient = 1;
        variable = dt;
    }
    
    
    /** Constructeur à partir d'un TP_DirectedTopo et d'un coefficient. */
    public TP_ExpressionTerm (int coeff, TP_DirectedTopo dt) {
        if ((coeff == -1) || (coeff == 0) || (coeff == +1)) {
            coefficient = coeff;
            variable = dt;
        } else System.out.println("coeff = +-1");
    }

}
