package fr.ign.cogit.process.geoxygene.cartetopo.ppio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "popArc",
    "popNoeud"
})
@XmlRootElement(name = "CarteTopoResult")
public class CarteTopoResult {
    
    @XmlElement(name = "PopArc")
    private IPopulation<Arc> popArc;
    
    @XmlElement(name = "PopNoeud")
    private IPopulation<Noeud> popNoeud;

    /**
     * Default constructor.
     */
    public CarteTopoResult() {
        popArc = null;
        popNoeud = null;
    }
    
    public IPopulation<Arc> getPopArc() {
        return popArc;
    }
    
    public void setPopArc(IPopulation<Arc> edges) {
        popArc = edges;
    }
    
    public IPopulation<Noeud> getPopNoeud() {
        return popNoeud;
    }
    
    public void setPopNoeud(IPopulation<Noeud> nodes) {
        popNoeud = nodes;
    }
}
