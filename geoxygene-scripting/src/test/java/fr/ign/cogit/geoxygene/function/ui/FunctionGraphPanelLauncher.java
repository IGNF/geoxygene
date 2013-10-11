package fr.ign.cogit.geoxygene.function.ui;

import javax.swing.JFrame;

import fr.ign.cogit.geoxygene.function.ui.FunctionGraphPanel;

public class FunctionGraphPanelLauncher {

  public static void main(final String[] args) {
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.getContentPane().add(new FunctionGraphPanel());
    f.pack();
    f.setVisible(true);

  }
}
