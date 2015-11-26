package fr.ign.cogit.mapping.generalindex;



import java.util.Iterator;

import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.cartagen.graph.Node;


/**
 * Cette interface définit une collection externe
 * d'enregisrement. Cette interface definit des operations necessaires
 * au stockage (add, remove, clear, delete).
 
 * @param <T> Le type de l'index
 *
 * @author Dr Tsatcha D.
 *
 * @see RecordFactory
 * @see IndexFactory
 * @see IndexManager
 * @see IndexListener
 * @see QueryCache
 */
public interface Index<T> {
	/**
	 * Demande si l'index est fermé
	 *
	 * @return <code>true</code> si l'index est fermé; sinon <code>false</code>.
	 */
	public boolean isClosed();

	/**
	 * Fermeture de l'instance.
	 *
	 * @throws IndexException est declenché si une exception s'es produite
	 * lors de la fermeture.
	 */
	public void close() throws IndexException;

	/**
	 * Ouvre une intanciation de l'enregistrement.
	 *
	 * @throws IndexException est declenché si une exception s'est produite 
	 * pendant l'ouverture de l'enregistrement.
	 */
	public void open() throws IndexException;

	/**
	 * Supprime toutes les resources generées par l'enregistrement.
	 *
	 * @throws IndexException est declenché si une exception s'est produite 
	 * pendant la suppression de l'enregistrement.
	 */
	public void delete() throws IndexException;

	/**
	 * Netoye tous les enregistrements.
	 *
	 * @throws IndexException .
	 */
	public void clear() throws IndexException;

	/**
	 * La taille de l'index.
	 *
	 * @return la taille de l'index.
	 * @throws IndexException est declenchée si une erreur s'est produite
	 * pendant la lecture de l'enregistrement.
	 */
	public long size() throws IndexException;

	/**
	 *Ajoute un ensemble d'enregistrements.
	 *
	 * @param records  les enregistrements à  ajouter.
	 * @throws IndexException est declenché si l'erreur s'est produite 
	 * pendant l'ajout des enregistrements
	 */
	public void add(Iterator<Record<T>> records) throws IndexException;

	/**
	 * Ajoute un enregistrement à l'index.
	 *
	 * @param r l'enregistrement à ajouter.
	 * @return <code>true</code>si l'enregistrement est ajouté; otherwise <code>false</code>
	 * @throws IndexException est declenchée si une erreur s'est produire pendant
	 * l'ajout.
	 */
	public boolean add(Record<T> r) throws IndexException;

	/**
	 * Enlève un ensemble d'enregistrement à  l'index.
	 *
	 * @param records les enregistrement à retirer.
	 * @throws IndexException est declenché si une erreur s'est produite
	 * pendant l'ajout de l'enregistrement.
	 */
	public void remove(Iterator<Record<T>> records) throws IndexException;

	/**
	 * Retire un enregistrement à  l'index.
	 *
	 * @param r l'enregistrement à retirer.
	 * @return <code>true</code> si l'enregistrement est retirée; otherwise <code>false</code>.
	 * @throws IndexException si une erreur s'est produite pendant le retrait de l'enregistrement
	 * .
	 */
	public boolean remove(Record<T> r) throws IndexException;
      

        /** Retourne un interateur de l'index. */
	public Iterator<Record<T>> iterator();
	
	/** Get the record factory used to create records. */
        public RecordFactory<T> getRecordFactory();

	/**
	 * Améliore les actions d'enregistrements. Cette action est register
	 * @param graph le graph
	 * @param graphName le nom du graphe
	 */
	public void register(Graph graph, Node graphName);

	/**
	  Améliore les actions d'enregistrement. Cette action est unregister
         * @param graph le graph
         * @param graphName le nom du graphe
         */
	
	  public void unregister(Graph graph, Node graphName);

	/**
	 * Chasse de l'index.
	 * @throws IndexException declenché si une erreur s'es produite pendant
	 * le flush.
	 */
	public void flush() throws IndexException;
}
