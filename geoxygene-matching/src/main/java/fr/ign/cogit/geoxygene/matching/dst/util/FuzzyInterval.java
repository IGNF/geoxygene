/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.util;

import org.apache.log4j.Logger;

/**
 * Expression basique d'un intervalle flou. (continu)
 * 
 * ex :
 *1 |........
 *  |        . 
 *  |         . 
 *  |          . 
 *0 |___________...........____ 0 +inf 
 * Attention
 * les valeurs d'abscisses doivent être triées ou l'intervalle sera incohérent
 * et source d'erreurs.
 * 
 * @author Julien Perret
 * 
 */
public class FuzzyInterval {
    Logger logger = Logger.getLogger(FuzzyInterval.class);

    // Poinst de changements de signe de la dérivée.
    float[][] derivativezeros = null;


    public FuzzyInterval(float[] xs, float[] ys) throws Exception {
        if (xs.length != ys.length) {
            logger.error("Trying to create a fuzzy interval with incomatible xs and ys lenghts : "
                    + xs.length + ", " + ys.length);
            throw new Exception("Trying to create a fuzzy interval with incomatible xs and ys lenghts : "
                    + xs.length + ", " + ys.length);

        }
        float old = xs[0];
        for(float f : xs){
            if(old>f){
            logger.error("FuzzyInterval error : Fuzzy interval unsorted");
            throw new Exception("FuzzyInterval error : Fuzzy interval unsorted"); 
            }
            old=f;
        }
        for(float f : ys){
            if(f <0.0f || f >1.0f){
            logger.error("FuzzyInterval error : ys out of bounds. Ys values must be in [0:1]");
            throw new Exception("FuzzyInterval error : ys out of bounds. Ys values must be in [0:1]"); 
            }
        }
        
        this.derivativezeros = new float[xs.length][2];
        for (int i = 0; i < xs.length; i++) {
            this.derivativezeros[i][0] = xs[i];
            this.derivativezeros[i][1] = ys[i];
        }

    }

    /**
     * 
     */
    public FuzzyInterval() {
        float[] x = {0,1};
        float[] y = {0,0};
        this.derivativezeros = new float[x.length][2];
        for (int i = 0; i < x.length; i++) {
            this.derivativezeros[i][0] = x[i];
            this.derivativezeros[i][1] = y[i];
        }
    }

    /**
     * 
     * @param x
     * @return y
     */
    public double getValue(double x) {
        if (x > this.derivativezeros[this.derivativezeros.length - 1][0]) {
            logger.error("FuzzyInterval out of bounds : trying to get f(" + x + ") in "
                    + "["+this.derivativezeros[0][0]+":"+this.derivativezeros[this.derivativezeros.length - 1][0]+"]");
            return -1;
        }
        if (x < this.derivativezeros[0][0]) {
            logger.error("FuzzyInterval out of bounds :" + x + " < "
                    + this.derivativezeros[0][0]);
            return -1;
        }

        int ppbs =this.derivativezeros.length-1;
        for (int i = 0; i < this.derivativezeros.length; i++) {
            if (this.derivativezeros[i][0] < this.derivativezeros[ppbs][0]
                    && this.derivativezeros[i][0] > x) {
                ppbs = i;
            }
        }
        if (ppbs == 0) {
            logger.error("Incorrect value : " + x);
            return -1;
        }
        float a = (this.derivativezeros[ppbs][1] - this.derivativezeros[ppbs - 1][1])
                / (this.derivativezeros[ppbs][0] - this.derivativezeros[ppbs-1][0]);
        float b = this.derivativezeros[ppbs - 1][1];
        return a * (x-this.derivativezeros[ppbs-1][0]) + b;
    }
    
    @Override
    public String toString(){
         String str = "Fuzzy interval values :";
         for(float[] f: this.derivativezeros){
             str +="\n ["+f[0]+"->"+f[1]+"]";
         }
         return str;         
    }

}
