/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */

/*
 * Créé le 2 août 2007
 */
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

/**
 * @author JGaffuri
 */
public class GeneralisationLaunchingFrame extends JFrame {
  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger
      .getLogger(GeneralisationLaunchingFrame.class.getName());

  /**
   * @return
   */
  public static Logger getLogger() {
    return GeneralisationLaunchingFrame.logger;
  }

  private static GeneralisationLaunchingFrame geneLaunchingFrame = null;

  public static GeneralisationLaunchingFrame get() {
    if (geneLaunchingFrame == null) {
      geneLaunchingFrame = new GeneralisationLaunchingFrame();
    }
    return geneLaunchingFrame;
  }

  /**
     */
  private JButton bGeneComplete = new JButton("Generalisation complete");
  /**
     */
  private JButton bRetablirBati = new JButton("Retablir Bâti");

  /**
     */
  private JTextArea tConsole;

  public void setTConsole(JTextArea tConsole) {
    this.tConsole = tConsole;
  }

  public JTextArea getTConsole() {
    return this.tConsole;
  }

  /**
     */
  private JScrollPane scrollConsole;

  public GeneralisationLaunchingFrame() {
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setResizable(false);
    this.setSize(new Dimension(400, 300));
    this.setLocation(100, 100);
    this.setTitle("Trigger Generalisation");
    this.setVisible(false);

    this.bGeneComplete.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeneralisationLaunchingFrame.this.getTConsole().setText("aaa");
        GeneralisationLaunchingFrame.this.getTConsole()
            .setText(GeneralisationLaunchingFrame.this.getTConsole().getText()
                + "\nssfse");
      }
    });

    this.setTConsole(new JTextArea(10, 40));
    this.getTConsole().setFont(new Font("Arial", Font.PLAIN, 10));
    this.scrollConsole = new JScrollPane(this.getTConsole());

    this.setLayout(new GridBagLayout());
    GridBagConstraints c;
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.insets = new Insets(5, 5, 5, 5);
    this.add(this.bGeneComplete, c);
    this.add(this.bRetablirBati, c);
    this.add(this.scrollConsole, c);

    this.pack();

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        GeneralisationLaunchingFrame.this.setVisible(false);
      }

      @Override
      public void windowActivated(WindowEvent e) {
      }
    });

  }

}
