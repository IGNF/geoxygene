package fr.ign.cogit.geoxygene.api.spatial.toporoot;

public interface ITopology {

    /** Renvoie l'identifiant topologique. */
    public abstract int getId();

    /**
     * Affecte une valeur a l'identifiant topologique. Assure la coherence avec
     * featureID. OBSOLETE
     * @param Id
     */
    public abstract void setId(int Id);

    /**
     * Dimension topologique de l'objet (0 pour node, 1 pour edge, 2 pour face, 3
     * pour solid).
     * @return dimension
     */
    public abstract int dimension();

}