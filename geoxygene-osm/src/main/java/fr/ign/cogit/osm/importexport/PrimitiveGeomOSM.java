package fr.ign.cogit.osm.importexport;

public abstract class PrimitiveGeomOSM {
	private OSMResource objet;

	public void setObjet(OSMResource objet) {
		this.objet = objet;
	}

	public OSMResource getObjet() {
		return objet;
	}
	
}
