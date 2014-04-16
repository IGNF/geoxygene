package fr.ign.cogit.geoxygene.appli.render.texture;

public class ParameterizedSegment {
    public ParameterizedPoint p1;
    public ParameterizedPoint p2;

    /**
     * Default Constructor
     */
    public ParameterizedSegment() {
    }

    /**
     * Quick constructor
     * 
     * @param p1
     * @param p2
     */
    public ParameterizedSegment(ParameterizedPoint p1, ParameterizedPoint p2) {
        super();
        this.p1 = p1;
        this.p2 = p2;
    }

}