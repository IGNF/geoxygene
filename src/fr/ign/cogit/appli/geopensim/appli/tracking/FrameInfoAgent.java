
package fr.ign.cogit.appli.geopensim.appli.tracking;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.Etat;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.geoxygene.appli.MainFrame;

/**
 * @author julien Gaffuri
 *
 */
public class FrameInfoAgent extends JFrame {
	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(FrameInfoAgent.class.getName());
	private AgentGeographique agentGeo = null;
	private AgentGeographique getAgentGeo() { return agentGeo; }
	private MainFrame mainFrame = null;
	public FrameInfoAgent(MainFrame mainFrame, final AgentGeographique agentGeo){
		this.agentGeo = agentGeo;
		this.mainFrame = mainFrame;
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setResizable(false);
		setSize(new Dimension(600,300));
		setLocation(100, 100);
		setTitle(" infos selection");
		setIconImage(mainFrame.getIconImage());
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				dispose();
			}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		setLayout(new GridBagLayout());
		GridBagConstraints c;
		c=new GridBagConstraints();
		c.gridx=0; c.gridy=GridBagConstraints.RELATIVE; c.insets=new Insets(5,5,5,5);
		add(getScroll(), c);
		add(getJApercu(), c);
		this.repaint();
		pack();
	}
	private JScrollPane scroll = null;
	public JScrollPane getScroll() {
		if (scroll == null) {
		    if (logger.isDebugEnabled()) logger.debug("construction panneau arbre");
		    scroll = new JScrollPane(getArbre(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		    scroll.setPreferredSize(new Dimension(600,350));
		}
		return scroll;
	}
	private JTree arbre;
	public JTree getArbre() {
		if(arbre==null){
//			if (logger.isDebugEnabled()) logger.debug("construction des représentations associées à chaque état");
//			construireRepresentations();
			if (logger.isDebugEnabled()) logger.debug("construction de l'arbre (appel a fonction recursive sur le premier etat qui est l'etat initial)");
			DefaultMutableTreeNode racine = null;
//			if ( getAgentGeo().getRepresentationRacine()!=null ) racine =  construireArbreRecurssif(getAgentGeo().getRepresentationRacine());
			if ( getAgentGeo().getRepresentationRacine()!=null ) racine =  construireArbreRecurssif(getAgentGeo().getEtatRacine());
			arbre = new JTree(racine);
			arbre.setFont(new Font("Arial", Font.PLAIN, 10));
			arbre.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			arbre.setSelectionRow(0);
			arbre.setToggleClickCount(2);

			arbre.addTreeSelectionListener(new TreeSelectionListener(){
				public void valueChanged(TreeSelectionEvent e) {
					if (logger.isDebugEnabled()) {
					  logger.debug("affichage de l'etat " + ((DefaultMutableTreeNode)getArbre().getLastSelectedPathComponent()).getUserObject());
					  logger.debug("Avant = "+getJApercu().getEtatAAfficher().getAgentGeographique());
					}
					getJApercu().setEtatAAfficher((ElementRepresentation) ((DefaultMutableTreeNode)getArbre().getLastSelectedPathComponent()).getUserObject());
					if (logger.isDebugEnabled()) {
					  logger.debug("rafraichissement...");
					}
					getJApercu().getEtatAAfficher().getAgentGeographique().prendreAttributsRepresentation(getJApercu().getEtatAAfficher());
					if (logger.isDebugEnabled()) {
					  logger.debug("après = "+getJApercu().getEtatAAfficher().getAgentGeographique());
					}
					//getJApercu().setVisible(true);
					//for(Layer layer:jApercu.getRenderingManager().getLayers()) ((UserLayer)layer).getFeatureCollection().clear();
					getJApercu().repaint();
					if (logger.isDebugEnabled()) {
					  logger.debug("fin rafraichissement");
					}
//                    getJApercu().getRenderingManager().renderAll();
				}});

			//deroule l'arbre
			getArbre().expandPath(getArbre().getPathForRow(0));
			int row = 0;
			while (row < getArbre().getRowCount()) {
			  getArbre().expandRow(row++);
			}
		}
		return arbre;
	}
	private PanelVisuInfoAgent jApercu=null;
	public PanelVisuInfoAgent getJApercu() {
		if (jApercu == null) {
			if (logger.isDebugEnabled()) {
			  logger.debug("construction de la fenetre apercu");
			}
			jApercu = new PanelVisuInfoAgent(mainFrame);
			jApercu.setSize(new Dimension(500, 300));
			jApercu.setPreferredSize(new Dimension(500, 300));
			try {
			  jApercu.getViewport().zoom(getAgentGeo().getGeom().envelope());
			} catch (NoninvertibleTransformException e) {
			  e.printStackTrace();
			}
			if (getAgentGeo().getRepresentationRacine() != null) {
				if (logger.isDebugEnabled()) {
				  logger.debug("affectation de l'état racine à l'agent");
				}
				jApercu.setEtatAAfficher(this.agentGeo.getRepresentationRacine());
				this.agentGeo.prendreAttributsRepresentation(this.agentGeo.getRepresentationRacine());
				jApercu.repaint();
			}
			//jApercu.etatAAfficher = (ElementRepresentation) ((DefaultMutableTreeNode)getArbre().getLastSelectedPathComponent()).getUserObject();
		}
		return jApercu;
	}
	/**
	 * Construit recursivement un arbre d'etats a partir d'un etat donne
	 * @param eag
	 * @return
	 */
//	private DefaultMutableTreeNode construireArbreRecurssif(ElementRepresentation eag){
//
//		//cas ou l'etat n'a pas de successeurs: renvoi du noeud seul
//		if(eag.getSuccesseurs()==null || eag.getSuccesseurs().size()==0)
//			return new DefaultMutableTreeNode(eag);
//
//		//cas ou l'etat a des successeurs: renvoit de partie d'arbre avec appel recursif
//		DefaultMutableTreeNode pere = new DefaultMutableTreeNode(eag);
//		if (logger.isTraceEnabled()) logger.trace("appel 	 pour construction de l'arbre d'états");
//		for(ElementRepresentation eagFils : eag.getSuccesseurs()) pere.add( construireArbreRecurssif(eagFils) );
//		return pere;
//	}

	private DefaultMutableTreeNode construireArbreRecurssif(Etat etat){
	    if (etat == null) { return null; }
		//cas ou l'etat n'a pas de successeurs: renvoi du noeud seul
		if (etat.getSuccesseurs()==null || etat.getSuccesseurs().isEmpty()) {
			return new DefaultMutableTreeNode(etat.getRepresentationAssociee());
		}
		//cas ou l'etat a des successeurs: renvoit de partie d'arbre avec appel recursif
		DefaultMutableTreeNode pere = new DefaultMutableTreeNode(etat.getRepresentationAssociee());
		if (logger.isTraceEnabled()) {
		  logger.trace("appel pour construction de l'arbre d'états");
		}
		for (Etat etatFils : etat.getSuccesseurs()) {
		  pere.add( construireArbreRecurssif(etatFils) );
		}
		return pere;
	}
}
