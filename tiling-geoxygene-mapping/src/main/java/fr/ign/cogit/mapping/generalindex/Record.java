package fr.ign.cogit.mapping.generalindex;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.mapping.datastructure.Node;


/*  @author Dr Tsatcha D.
 * Cette classe permet d'enregistrer un objet
 * On change l'enregistrement dans notre modèle 
 * comme une composante entier et vakeur???
 * @see Node
 */


public class Record<T> {
	/**
	 * Creation d'une nouvelle instance
	 *
	 * @param <T> le type de la valeur à indexer.
	 * @param key la clé
	 * @param value la valeur
	 * @return une nouveau enregistrement.
	 */
	public static <T> Record<T> create(Node key, T value) {
		return new Record<>(key, value);
	}

	/** La clé de cette instance. */
	protected Node key;

	/** La valeur de l'index. */
	protected T value;

	/**
	 * Constructeur du nouveau instance.
	 *
	 * @param key la clé
	 * @param value la valeur
	 */
	protected Record(Node key, T value) {
		this.key = key;
		this.value = value;
	}

        // TODO Auto-generated constructor stub
    

    /**
	 * Obtient la clé.
	 *
	 * @return la clé
	 */
	public Node getKey() {
		return key;
	}

	/**
	 * Obtient la valeur.
	 *
	 * @return la valeur
	 */
	public T getValue() {
		return value;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return String.format("%s: %s", key, value);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Record<T> other = (Record<T>) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}


}
