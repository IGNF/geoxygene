package fr.ign.cogit.geoxygene.scripting;

import javax.swing.SwingUtilities;

import fr.ign.cogit.geoxygene.scripting.GroovyConsoleUI;
import groovy.lang.GroovyShell;

public class GroovyConsoleUILauncher {

  public GroovyConsoleUILauncher() {
    // TODO Auto-generated constructor stub
  }

  public static void main(final String[] args) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        GroovyConsoleUI console = new GroovyConsoleUI(null, "my console");
        console.setVisible(true);
        console.addConsoleTab("0", "Tab 1", null, null, new GroovyShell());
        console.addConsoleTab("1", "Tab 2", null, null, new GroovyShell());

      }
    });

  }

}
