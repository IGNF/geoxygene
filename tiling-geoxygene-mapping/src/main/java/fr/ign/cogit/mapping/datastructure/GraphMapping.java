package fr.ign.cogit.mapping.datastructure;
/*
 * @auhor Dtsatcha 
 * Cette classe permet de stocker les contenus à
 * charger dans un fichier ... pas encore utilisé
 * @author Dr Tsatcha D.
 */

public class GraphMapping {
    public String getGraphname() {
        return graphname;
    }
    public void setGraphname(String graphname) {
        this.graphname = graphname;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getGraphpath() {
        return graphpath;
    }
    public void setGraphpath(String graphpath) {
        this.graphpath = graphpath;
    }
    /*
     * graphname 
     *  le nom du graph
     *  content 
     *  le contenu du graph
     */
    protected String graphname;
    protected String content;
    /*
     * graphpath 
     * le chemin de stockage du graphe
     */
    protected String graphpath;
    public GraphMapping(String graphname, String content, String graphpath) {
        super();
        this.graphname = graphname;
        this.content = content;
        this.graphpath = graphpath;
    }
    
    
    

}
