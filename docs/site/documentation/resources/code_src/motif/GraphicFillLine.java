  /**
   * Le Graphic Fill permet de répéter une forme dans le remplissage d'une
   * ligne. Cette forme peut être une image (ExternalGraphic) ou une forme
   * prédéfinie (Mark).
   */
  public void exampleGraphicFill_Line() {
    Layer layer = projectFrame.getSld().createLayer("GraphicFill_Line",
        GM_LineString.class, Color.green, Color.red, 1f, 4);
    LineSymbolizer symbolizer = (LineSymbolizer) layer.getSymbolizer();
    GraphicFill graphicFill = new GraphicFill();
    Graphic graphic = new Graphic();
    graphic.setSize(5f);

    // --------------- Exemple avec un Graphic de type image ----------------------------------
    // --------------------- Image au format svg
    ExternalGraphic circle = new ExternalGraphic();
    URL url = SLDDemoApplication.class.getResource("/images/circles.svg");
    circle.setHref(url.toString());
    circle.setFormat("svg");
    graphic.getExternalGraphics().add(circle);

    graphicFill.getGraphics().add(graphic);
    symbolizer.getStroke().setGraphicType(graphicFill);

    Population<DefaultFeature> pop = new Population<DefaultFeature>("GraphicFill_Line");
    pop.add(new DefaultFeature(new GM_LineString(new DirectPositionList(
        new DirectPosition(10, 120), new DirectPosition(10, 170),
        new DirectPosition(60, 170), new DirectPosition(60, 220),
        new DirectPosition(110, 220)))));
    projectFrame.getDataSet().addPopulation(pop);
    projectFrame.getSld().add(layer);
  }
