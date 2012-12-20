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
package fr.ign.cogit.cartagen.software.interfacecartagen.annexes;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;

/**
 * @author JGaffuri
 * 
 */
public class DataFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  static Logger logger = Logger.getLogger(DataFrame.class.getName());

  public static DataFrame get() {
    return CartagenApplication.getInstance().getFrameDonnees();
  }

  private JTabbedPane panneauOnglets = new JTabbedPane();
  private JPanel pChargement = new JPanel();
  private JPanel pEnrichissement = new JPanel();
  private JPanel pBati = new JPanel();
  private JButton bEnrichissementBati = new JButton("Enrichissement bâti");
  private JPanel pRoutier = new JPanel();
  private JButton bEnrichissementRoutier = new JButton("Enrichissement routier");
  private JPanel pHydro = new JPanel();
  private JButton bEnrichissementHydro = new JButton(
      "Enrichissement hydrographie");

  // private JPanel pRelief = new JPanel();
  // private JButton bStructurationRelief = new JButton("Structuration relief");
  // private JPanel pOccSol = new JPanel();
  // private JButton bStructurationOccSol = new
  // JButton("Structuration occ. sol");

  public DataFrame() {
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setResizable(false);
    this.setSize(new Dimension(400, 300));
    this.setLocation(100, 100);
    this.setTitle(CartagenApplication.getInstance().getFrame().getTitle()
        + " - données");
    this.setIconImage(CartagenApplication.getInstance().getFrame().getIcon());
    this.setVisible(false);

    GridBagConstraints c;

    this.pChargement.setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    CartagenApplication.getInstance();
    this.pChargement.add(new JLabel("Voir dans fichier "
        + CartagenApplication.getInstance()
            .getCheminFichierConfigurationChargementDonnees()
        + " pour spécifier les données à charger"), c);

    this.pEnrichissement.setLayout(new GridBagLayout());

    // enrichissement bati
    this.pBati.setBorder(BorderFactory.createTitledBorder("Bâti"));
    this.bEnrichissementBati.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        Thread th = new Thread(new Runnable() {
          @Override
          public void run() {
            if (CartAGenDoc.getInstance().getCurrentDataset().getTowns().size() == 0) {
              DataFrame.logger.info("Début enrichissement bati...");
              UrbanEnrichment.buildTowns(CartAGenDoc.getInstance()
                  .getCurrentDataset());
              DataFrame.logger.info("Fin enrichissement bati: "
                  + CartAGenDoc.getInstance().getCurrentDataset().getTowns()
                      .size()
                  + " villes et "
                  + CartAGenDoc.getInstance().getCurrentDataset().getBlocks()
                      .size() + " ilots crees.");
            } else {
              DataFrame.logger.info("Enrichissement bâti deja effectue: "
                  + CartAGenDoc.getInstance().getCurrentDataset().getTowns()
                      .size()
                  + " villes et "
                  + CartAGenDoc.getInstance().getCurrentDataset().getBlocks()
                      .size() + " ilots crees.");
            }
          }
        });
        th.start();
      }
    });
    this.bEnrichissementBati.setToolTipText("Création des villes, ilots...");
    this.pBati.add(this.bEnrichissementBati);
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    this.pEnrichissement.add(this.pBati, c);

    // enrichissement routier
    this.pRoutier.setBorder(BorderFactory.createTitledBorder("Routier"));
    this.bEnrichissementRoutier.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        Thread th = new Thread(new Runnable() {
          @Override
          public void run() {
            if (CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()
                .getNodes().size() == 0) {
              DataFrame.logger.info("Début enrichissement routier...");
              NetworkEnrichment.enrichNetwork(CartAGenDoc.getInstance()
                  .getCurrentDataset().getRoadNetwork());
              DataFrame.logger.info("fin enrichissement routier");
            } else {
              DataFrame.logger.info("Routier déjà enrichi");
            }
          }
        });
        th.start();
      }
    });
    this.pRoutier.add(this.bEnrichissementRoutier);
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    this.pEnrichissement.add(this.pRoutier, c);

    // enrichissement hydro
    this.pHydro.setBorder(BorderFactory.createTitledBorder("Hydrographie"));
    this.bEnrichissementHydro.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        Thread th = new Thread(new Runnable() {
          @Override
          public void run() {
            if (CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork()
                .getNodes().size() == 0) {
              DataFrame.logger.info("Début enrichissement hydro...");
              NetworkEnrichment.enrichNetwork(CartAGenDoc.getInstance()
                  .getCurrentDataset().getHydroNetwork());
              DataFrame.logger.info("fin enrichissement hydro");
            } else {
              DataFrame.logger.info("Hydro déjà enrichie");
            }
          }
        });
        th.start();
      }
    });
    this.pHydro.add(this.bEnrichissementHydro);
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    this.pEnrichissement.add(this.pHydro, c);

    // enrichissement relief
    // this.pRelief.setBorder(BorderFactory.createTitledBorder("Relief"));
    // this.bStructurationRelief.addActionListener(new ActionListener() {
    // @Override
    // public void actionPerformed(ActionEvent arg0) {
    // Thread th = new Thread(new Runnable() {
    // @Override
    // public void run() {
    // if (((ReliefFieldAgent) AgentUtil
    // .getAgentAgentFromGeneObj(CartAGenDoc.getInstance()
    // .getCurrentDataset().getReliefField())).getPointAgents()
    // .size() == 0) {
    // DataFrame.logger.info("Début enrichissement relief...");
    // ((ReliefFieldAgent) AgentUtil
    // .getAgentAgentFromGeneObj(CartAGenDoc.getInstance()
    // .getCurrentDataset().getReliefField())).decompose();
    // DataFrame.logger.info("Fin enrichissement relief");
    // } else {
    // DataFrame.logger.info("Relief déjà enrichi");
    // }
    // }
    // });
    // th.start();
    // }
    // });
    // this.bStructurationRelief
    // .setToolTipText("Structuration du relief: décomposition des courbes de niveau et trianglulation");
    // this.pRelief.add(this.bStructurationRelief);
    // c = new GridBagConstraints();
    // c.gridx = 0;
    // c.gridy = GridBagConstraints.RELATIVE;
    // c.fill = GridBagConstraints.HORIZONTAL;
    // this.pEnrichissement.add(this.pRelief, c);

    // occ. sol
    // this.pOccSol.setBorder(BorderFactory.createTitledBorder("Occ. sol"));
    // this.bStructurationOccSol.addActionListener(new ActionListener() {
    // @Override
    // public void actionPerformed(ActionEvent arg0) {
    // Thread th = new Thread(new Runnable() {
    // @Override
    // public void run() {
    // if (((LandUseFieldAgent) AgentUtil
    // .getAgentAgentFromGeneObj(CartAGenDoc.getInstance()
    // .getCurrentDataset().getLandUseField())).getPointAgents()
    // .size() == 0) {
    // DataFrame.logger.info("Début enrichissement occ. sol...");
    // ((LandUseFieldAgent) AgentUtil
    // .getAgentAgentFromGeneObj(CartAGenDoc.getInstance()
    // .getCurrentDataset().getLandUseField())).decompose();
    // DataFrame.logger.info("fin enrichissement occ. sol");
    // } else {
    // DataFrame.logger.info("Occ. sol déjà enrichie");
    // }
    // }
    // });
    // th.start();
    // }
    // });
    // this.bStructurationOccSol
    // .setToolTipText("Structuration de l'occ. sol: décomposition des parcelles et trianglulation");
    // this.pOccSol.add(this.bStructurationOccSol);
    // c = new GridBagConstraints();
    // c.gridx = 0;
    // c.gridy = GridBagConstraints.RELATIVE;
    // c.fill = GridBagConstraints.HORIZONTAL;
    // this.pEnrichissement.add(this.pOccSol, c);

    this.panneauOnglets.addTab("Chargement", new ImageIcon(DataFrame.class
        .getResource("/images/co.gif").getPath().replaceAll("%20", " ")),
        this.pChargement, "Chargement des données brutes");
    this.panneauOnglets.addTab("Enrichissement", new ImageIcon(DataFrame.class
        .getResource("/images/co.gif").getPath().replaceAll("%20", " ")),
        this.pEnrichissement, "Enrichir les données charger");
    this.add(this.panneauOnglets);

    this.pack();

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        DataFrame.this.setVisible(false);
      }

      @Override
      public void windowActivated(WindowEvent e) {
      }
    });

  }
}
