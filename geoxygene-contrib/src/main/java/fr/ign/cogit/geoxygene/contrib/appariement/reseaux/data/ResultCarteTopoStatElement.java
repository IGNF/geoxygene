package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 *
 */
public class ResultCarteTopoStatElement {
    
    private Map<Integer, Integer> nbElement;

    public ResultCarteTopoStatElement() {
        nbElement = new HashMap<Integer, Integer>();
    }
    
    public Map<Integer, Integer> getNbElement() {
        return nbElement;
    }
    
    public void setNbElement(Map<Integer, Integer> stat) {
        nbElement = stat;
    }
    
    public void addCarteTopoStat(int type, int nb) {
        nbElement.put(type,  nb);
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public int getNbElementForType(int type) {
        if (nbElement.size() < 1) {
            return 0;
        } else {
            if (nbElement.get(type) == null) {
                return 0;
            }
            return nbElement.get(type);
        }
    }
    
    /**
     * 
     * @param type
     * @param nb
     */
    public void addNbElementForType(int type, int nb) {
        nbElement.put(type, getNbElementForType(type) + nb);
    }
}

