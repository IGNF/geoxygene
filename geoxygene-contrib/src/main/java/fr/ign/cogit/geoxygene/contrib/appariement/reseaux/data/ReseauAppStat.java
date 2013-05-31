package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;

/**
 * 
 * 
 *
 */
public class ReseauAppStat {
    
    private ReseauApp reseauApp;
    private ResultCarteTopoStatElement resultCarteTopoEdge;
    private ResultCarteTopoStatElement resultCarteTopoNode;
    
    /**
     * 
     */
    public ReseauAppStat() {
        reseauApp = new ReseauApp();
        resultCarteTopoEdge = new ResultCarteTopoStatElement();
        resultCarteTopoNode = new ResultCarteTopoStatElement();
    }

    /**
     * 
     * @return
     */
    public ReseauApp getReseauApp() {
        return reseauApp;
    }
    
    /**
     * 
     * @param reseau
     */
    public void setReseauApp(ReseauApp reseau) {
        reseauApp = reseau;
    }
    
    /**
     * 
     * @return
     */
    public ResultCarteTopoStatElement getResultCarteTopoEdge() {
        return resultCarteTopoEdge;
    }
    
    /**
     * 
     * @param stat
     */
    public void setResultCarteTopoEdge(ResultCarteTopoStatElement statEdge) {
        resultCarteTopoEdge = statEdge;
    }
    
    /**
     * 
     * @return
     */
    public ResultCarteTopoStatElement getResultCarteTopoNode() {
        return resultCarteTopoNode;
    }
    
    /**
     * 
     * @param stat
     */
    public void setResultCarteTopoNode(ResultCarteTopoStatElement statNode) {
        resultCarteTopoNode = statNode;
    }
    
}