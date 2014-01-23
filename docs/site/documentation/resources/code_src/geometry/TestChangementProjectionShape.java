// On charge le ShapeFile dans une FeatureCollection, 
IFeatureCollection<IFeature> featCollInitiale = 
        ShapefileReader.read(new File("\\ne_10m_admin_0_countries\\ne_10m_admin_0_countries.shp").getAbsolutePath());
		
// On va stocker le Feature sélectionner dans une nouvelle Population
Population<DefaultFeature> popEtatsUnis = new Population<DefaultFeature>(false, "EtatsUnis0", DefaultFeature.class, true);
		
// On recherche le code ISO sur 2 chiffres du pays dans le ShapeFile
for (IFeature feat : featCollInitiale) {
  // On sélectionne uniquement le polygone des Etats-unis
	if(feat.getAttribute("iso_a2").equals("US")){
		// On stocke le Feature sélectionné dans une nouvelle Population
		popEtatsUnis.setFeatureType(featCollInitiale.getFeatureType());
		popEtatsUnis.add((DefaultFeature) feat);
	}
}		

// On crée une nouvelle population qui sera peuplée par les Feature qui auront été translatés
Population<IFeature> popTranslation = new Population<IFeature>();
popTranslation.setFeatureType(popEtatsUnis.getFeatureType());
				
// On parcourt la population initiale (il n'y en a qu'une seule en fait)
for (int i = 0; i < popEtatsUnis.size(); i++) {
			
	// On caste le Feature de la population (qui est un MultiPolygon en plusieurs polygones simples
	DefaultFeature ancienFeature = popEtatsUnis.get(i);
	GM_MultiSurface multiSurface = (GM_MultiSurface) ancienFeature.getGeom();
						
	// On parcourt tous les polygones
	for (int j = 0; j < multiSurface.size(); j++) {
				
		// On récupère les coordonnées du polygone courant
		IDirectPositionList coordsInitiales = multiSurface.get(j).coord();
				
		// On crée une liste qui contiendra les coordonnées du polygone translaté
		List<IDirectPosition> listeCoordTranslatees = new ArrayList<IDirectPosition>();
				
		// On parcourt toutes les coordonnées du polygone courant
		for(IDirectPosition coordAncienne : coordsInitiales.getList()) {
					
			// On translate l'ancienne coordonnée X de 40
			double ancienneLongitude = coordAncienne.getX();
			double nouvelleLongitude = ancienneLongitude + 40;
										
			// Si l'ancienne longitude est supérieure à la limite Est du WGS84, 
			//    la nouvelle longitude est replacée toute à l'ouest
			if(nouvelleLongitude > 180.0){
			  double diff = nouvelleLongitude - 180.0;
			  nouvelleLongitude = -180 +diff;
			}
					
			// On ajoute la nouvelle paire de coordonnées à la liste de coordonnées
			IDirectPosition coordNouvelle = new DirectPosition(nouvelleLongitude, coordAncienne.getY());
			listeCoordTranslatees.add(coordNouvelle);
		}
			
		// On crée un nouveau Feature qui contiendra le nouveau polygone créé
		DefaultFeature nouveauFeature = new DefaultFeature();
		nouveauFeature.setFeatureType(ancienFeature.getFeatureType());
				
		// On crée un polygone à partir de cette nouvelle liste de coordonnées
		GM_LineString lineString = new GM_LineString(listeCoordTranslatees);
		GM_Polygon nouveauPolygone = new GM_Polygon(lineString);
						
		// On ajoute le polygone au nouveau Feature, puis ce dernier à la nouvelle Population
		nouveauFeature.setGeom(nouveauPolygone);
		popTranslation.add(nouveauFeature);
				
	}

}
		

