
  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;
    JMenuItem menuItem = addMenu("Curve", "Gaussian Filter");
    menuItem.addActionListener(this);
  }

