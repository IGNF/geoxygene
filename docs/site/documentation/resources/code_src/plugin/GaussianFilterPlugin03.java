
  @SuppressWarnings("unchecked")
  @Override
  public void actionPerformed(final ActionEvent e) {

    // On récupère la couche sélectionnée
    ProjectFrame project = this.application.getMainFrame().getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel().getSelectedLayers();
    if (selectedLayers.size() != 1) {
      javax.swing.JOptionPane.showMessageDialog(null, "You need to select one (and only one) layer.");
      GaussianFilterPlugin.LOGGER.error("You need to select one (and only one) layer.");
      return;
    }
    Layer layer = selectedLayers.iterator().next();
    
    // On propose le champ de saisie du paramètre sigma.
    double sigma = Double.parseDouble(JOptionPane.showInputDialog(GaussianFilterPlugin.this.application.getMainFrame(), "Sigma"));
    
    // On construit une population de DefaultFeature
    Population<DefaultFeature> pop = new Population<DefaultFeature>("GaussianFilter " + layer.getName() + " " + sigma);
    pop.setClasse(DefaultFeature.class);
    pop.setPersistant(false);
    for (IFeature f : layer.getFeatureCollection()) {
      ILineString line = null;
      if (ILineString.class.isAssignableFrom(f.getGeom().getClass())) {
        line = (ILineString) f.getGeom();
      } else {
        if (IMultiCurve.class.isAssignableFrom(f.getGeom().getClass())) {
          line = ((IMultiCurve<ILineString>) f.getGeom()).get(0);
        }
      }
      // On ajoute à la population de 
      pop.nouvelElement(GaussianFilter.gaussianFilter(line, sigma, 1));
    }
    
    // Créer les métadonnées du jeu correspondant 
    FeatureType newFeatureType = new FeatureType();
    newFeatureType.setGeometryType(ILineString.class);
    pop.setFeatureType(newFeatureType);
    
    // On ajoute au ProjectFrame la nouvelle couche créée à partir de la nouvelle population
    project.getDataSet().addPopulation(pop);
    project.addFeatureCollection(pop, pop.getNom(), layer.getCRS());
  }


