package fr.ign.cogit.geoxygene.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Le menu de l'interface
 * @author julien Gaffuri
 *
 */
public class MenuGeoxygene extends JMenuBar {
	private static final long serialVersionUID = 5852864862627549895L;

	static Logger logger=Logger.getLogger(MenuGeoxygene.class.getName());

	private InterfaceGeoxygene frameMirage = null;
	/**
	 * @return la fenêtre à laquelle le menu est eventuellement li�
	 */
	InterfaceGeoxygene getFrameMirage() {return frameMirage;}

	//vue
	private JMenu menuVue=new JMenu("Affichage");
	private JMenuItem mVueEch=new JMenuItem("1:"+Legende.getECHELLE_SYMBOLISATION());
	private JMenuItem mVueEch6250=new JMenuItem("1:6250");
	private JMenuItem mVueEch12500=new JMenuItem("1:12500");
	private JMenuItem mVueEch25k=new JMenuItem("1:25k");
	private JMenuItem mVueEch50k=new JMenuItem("1:50k");
	private JMenuItem mVueEch100k=new JMenuItem("1:100k");
	private JMenuItem mImprimer=new JMenuItem("Imprimer");

	//config
	private JMenu menuConfig=new JMenu("Configuration");
	public JCheckBoxMenuItem mAntiAliasing=new JCheckBoxMenuItem("AntiAliasing");
	private JMenuItem mRechargerConfigurationLogger=new JMenuItem("Recharger la configuration du logger");

	//infos
	private JMenu menuInfos=new JMenu("?");
	public JMenuItem mAPropos=new JMenuItem("A propos...");

	public MenuGeoxygene(final InterfaceGeoxygene frameMirage) {
		this.frameMirage = frameMirage;
		
		setFont(new Font("Arial",Font.PLAIN,9));

		//menu vue

		mVueEch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				getFrameMirage().getPanelVisu().setTaillePixel(Legende.getECHELLE_SYMBOLISATION()*PanelVisu.getMETERS_PER_PIXEL());
				getFrameMirage().getPanelVisu().repaint();
			}});
		mVueEch.setFont(getFont());
		menuVue.add(mVueEch);

		menuVue.addSeparator();
		
		mVueEch6250.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				getFrameMirage().getPanelVisu().setTaillePixel(6250*PanelVisu.getMETERS_PER_PIXEL());
				getFrameMirage().getPanelVisu().repaint();
			}});
		mVueEch6250.setFont(getFont());
		menuVue.add(mVueEch6250);

		mVueEch12500.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				getFrameMirage().getPanelVisu().setTaillePixel(12500*PanelVisu.getMETERS_PER_PIXEL());
				getFrameMirage().getPanelVisu().repaint();
			}});
		mVueEch12500.setFont(getFont());
		menuVue.add(mVueEch12500);

		mVueEch25k.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				getFrameMirage().getPanelVisu().setTaillePixel(25000*PanelVisu.getMETERS_PER_PIXEL());
				getFrameMirage().getPanelVisu().repaint();
			}});
		mVueEch25k.setFont(getFont());
		menuVue.add(mVueEch25k);

		mVueEch50k.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				getFrameMirage().getPanelVisu().setTaillePixel(50000*PanelVisu.getMETERS_PER_PIXEL());
				getFrameMirage().getPanelVisu().repaint();
			}});
		mVueEch50k.setFont(getFont());
		menuVue.add(mVueEch50k);

		mVueEch100k.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				getFrameMirage().getPanelVisu().setTaillePixel(100000*PanelVisu.getMETERS_PER_PIXEL());
				getFrameMirage().getPanelVisu().repaint();
			}});
		mVueEch100k.setFont(getFont());
		menuVue.add(mVueEch100k);

		menuVue.addSeparator();

		mImprimer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Thread th=new Thread(new Runnable(){
					public void run() {
				        try {
				            PrinterJob printJob = PrinterJob.getPrinterJob();
				            printJob.setPrintable(getFrameMirage().getPanelVisu());
				            PrintRequestAttributeSet aset=new HashPrintRequestAttributeSet();
				            if (printJob.printDialog(aset)) printJob.print(aset);
				        } catch (java.security.AccessControlException ace) {
				            JOptionPane.showMessageDialog(getFrameMirage().getPanelVisu(), "Impossible d'imprimer; probleme de controle d'acces: "+ace.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				        } catch (Exception ex) {
				            ex.printStackTrace();
				        }
					}
				});
				th.start();
			}});
		mImprimer.setFont(getFont());
		menuVue.add(mImprimer);

		JMenuItem menuItemCentrer = new JMenuItem("Centrer la vue");
		menuItemCentrer.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				getFrameMirage().getPanelVisu().centrer();
			}
		});
		menuItemCentrer.setFont(getFont());
		menuVue.add(menuItemCentrer);

		//menu config

		mAntiAliasing.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				getFrameMirage().getPanelVisu().setAntiAliasing(mAntiAliasing.isSelected());
				getFrameMirage().getPanelVisu().repaint();
			}});
		mAntiAliasing.setSelected(getFrameMirage().getPanelVisu().isAntiAliasing());
		mAntiAliasing.setFont(getFont());
		menuConfig.add(mAntiAliasing);

		mRechargerConfigurationLogger.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				logger.info("Rechargement configuration log");
				PropertyConfigurator.configure("log4j.properties");
			}});
		mRechargerConfigurationLogger.setFont(getFont());
		menuConfig.add(mRechargerConfigurationLogger);

		//menu infos
		mAPropos.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
		 	    String message="<html><b>GeOxygene</b><br />http://oxygene-project.sourceforge.net/<br /><br />Laboratoire COGIT<br />http://recherche.ign.fr/labos/cogit<br /><br />Institut Géographique National<br />http://www.ign.fr</html>";
		 	    String titre="A propos de MiraGe";
		 		JOptionPane.showMessageDialog(null, message, titre, JOptionPane.INFORMATION_MESSAGE, new ImageIcon("images/splash.png")); 
			}});
		mAPropos.setFont(getFont());
		menuInfos.add(mAPropos);

		
		menuVue.setFont(getFont());
		add(menuVue);
		menuConfig.setFont(getFont());
		add(menuConfig);
		menuInfos.setFont(getFont());
		add(menuInfos);
	}

	/**
	 * Renvoie le Menu "Affichage"
	 * @return le Menu "Affichage"
	 */
	public JMenu getMenuAffichage() {return this.menuVue;}
}
