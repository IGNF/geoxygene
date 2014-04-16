package fr.ign.cogit.geoxygene.appli.render.texture;

public class ParameterizedPoint {
    public double x;
    public double y;
    public double u;
    public double v;

    /**
     * Default constructor
     */
    public ParameterizedPoint() {
    }

    /**
     * Quick constructor
     * 
     * @param x
     * @param y
     * @param u
     * @param v
     */
    public ParameterizedPoint(double x, double y, double u, double v) {
        super();
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
    }

}