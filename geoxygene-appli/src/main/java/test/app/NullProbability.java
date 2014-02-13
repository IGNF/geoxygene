package test.app;

/**
 * null probability function (returns 0)
 * 
 * @author JeT
 * 
 */
public class NullProbability implements TileProbability {

    public NullProbability() {
        // nothing to initialize
    }

    @Override
    public double getProbability(double x, double y) {
        return 0;
    }

}
