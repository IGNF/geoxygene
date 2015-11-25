package fr.ign.cogit.mapping.generalindex;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import fr.ign.cogit.mapping.datastructure.Node;

/**
 * J'ai utilisé plutôt un String un Var que celui
 * definit dans la version initiale.
 */
/** @author Dtsatcha */
public class QueryCache<T> extends LinkedHashMap<Node, T> {
	private static final long serialVersionUID = 1L;

	private int maxSize;
	private Map<String, Node> blankNodeMap;

	public QueryCache(int size) {
		super(size);
		maxSize = size;
		blankNodeMap = new LinkedHashMap<String, Node>(size) {
			private static final long serialVersionUID = 1L;

			/** {@inheritDoc} */
			@Override
			protected boolean removeEldestEntry(Entry<String, Node> eldest) {
				return size() > maxSize;
			}
		};
	}

	/** {@inheritDoc} */
	@Override
	protected boolean removeEldestEntry(Entry<Node, T> eldest) {
		return size() > maxSize;
	}

	public Map<String, Node> getBlankNodeMap() {
		return blankNodeMap;
	}
}
