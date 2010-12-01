package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.table.TableColumn;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * @author Charlotte Hoarau
 *
 */
public class COGITColorChooserPanel extends AbstractColorChooserPanel
									implements MouseListener{
	private static final long serialVersionUID = 1L;

	JLabel lblCerclesImage;
	JLabel lblColorImage;
	BufferedImage sampleImage;
	JPanel panneau;
	JTable tCodesCouleur;
	JScrollPane panneauTab;
	JPanel panneauBtn;
	JButton btnOK;
	JButton btnCancel;
	BufferedImage cerclesImage;

	public COGITColorChooserPanel(){
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setTitle("Cercles Chromatiques Lucil");

		cerclesImage = createCercleImage();
		lblCerclesImage = new JLabel(new ImageIcon(cerclesImage));
		lblCerclesImage.setName(new String("Label contenant l'image des cercles chromatiques"));
		lblCerclesImage.addMouseListener(this);

		tCodesCouleur = new JTable(3,6);
		tCodesCouleur.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tCodesCouleur.setGridColor(new  Color(225,225,225));//Couleur des bords
		tCodesCouleur.setBackground(new Color(200,200,200));//Couleur des cases
		tCodesCouleur.setForeground(new Color(100,100,100));//Couleur du texte

		tCodesCouleur.setValueAt("Couleur Usuelle",0,0);
		tCodesCouleur.setValueAt("Niveau de valeur",1,0);
		tCodesCouleur.setValueAt("Clé Couleur",2,0);

		tCodesCouleur.setValueAt("Rouge",0,2);
		tCodesCouleur.setValueAt("Vert",1,2);
		tCodesCouleur.setValueAt("Bleu",2,2);

		tCodesCouleur.setValueAt("L",0,4);
		tCodesCouleur.setValueAt("a",1,4);
		tCodesCouleur.setValueAt("b",2,4);
		Font f = tCodesCouleur.getFont();
		//f = f.deriveFont((float)20);
	    f = f.deriveFont(Font.BOLD);
	    tCodesCouleur.setFont(f);

		for (int i=0;i<6;i++){
			TableColumn colonne = tCodesCouleur.getColumnModel().getColumn(i);

			if (i==3 || i==5){
				colonne.setPreferredWidth(30);
				colonne.setMinWidth(30);
				colonne.setMaxWidth(30);
			}else if (i==2 || i==4){
				colonne.setPreferredWidth(60);
				colonne.setMinWidth(60);
				colonne.setMaxWidth(60);
				//tCodesCouleur.setFont(new Font());
			}else if (i==1){
				colonne.setPreferredWidth(150);
				colonne.setMinWidth(200);
				colonne.setMaxWidth(200);
			}else{
				colonne.setPreferredWidth(100);
				colonne.setMinWidth(100);
				colonne.setMaxWidth(100);
			}
		}

		//Initialization of the sample image with a gray rectangle.
		sampleImage = new BufferedImage(
				115,
				85,
				java.awt.image.BufferedImage.TYPE_INT_RGB);
		lblColorImage = new JLabel(new ImageIcon(sampleImage));
		createSampleImage(200, 200, 200, lblColorImage);
		lblColorImage.setVisible(false);

		//Initialization of the buttons
		btnOK = new JButton("OK");
		btnCancel = new JButton("Annuler");
		panneauBtn = new JPanel();
		panneauBtn.setBackground(new Color(225,225,225));
		panneauBtn.setName("Panneau contenant les boutons");
		panneauBtn.add(tCodesCouleur);
		panneauBtn.add(lblColorImage);
		panneauBtn.add(btnOK);
		panneauBtn.add(btnCancel);

		panneau = new JPanel();
		panneau.setLayout(new BorderLayout());
		panneau.setName("Panneau principal");
		panneau.add(lblCerclesImage, BorderLayout.NORTH);
		panneau.add(panneauBtn, BorderLayout.SOUTH);

		add(panneau);
	}

	public Graphics createSampleImage(int r, int v, int b, JLabel lbl){

		Graphics g = lbl.getGraphics();

		if(g==null){
			g = sampleImage.createGraphics();
		}

		g.setColor(new Color(130,130,130));
		g.fillRect(0, 0, 115, 85);

		g.setColor(Color.BLACK);
		g.fillRect(75,45,25,25);
		g.fillRect(45,15,25,25);

		g.setColor(Color.WHITE);
		g.fillRect(45,45,25,25);
		g.fillRect(15,15,25,25);
		g.fillRect(75,15,25,25);

		g.setColor(new Color(r,v,b));
		g.fillRect(15, 45, 25, 25);
		g.fillRect(50, 50, 15, 15);
		g.fillRect(80, 50, 15, 15);
		g.fillRect(20, 20, 15, 15);
		g.fillRect(50, 20, 15, 15);
		g.fillRect(80, 20, 15, 15);

		g.setColor(Color.BLACK);
		g.fillRect(55,25,5,5);
		g.fillRect(85,25,5,5);

		g.setColor(Color.WHITE);
		g.fillRect(25,25,5,5);

		return g;
	}

	public BufferedImage createCercleImage(){
		BufferedImage cerclesImage =
			new BufferedImage(1100,450,java.awt.image.BufferedImage.TYPE_INT_RGB);
		Graphics2D g = cerclesImage.createGraphics();
		g.setRenderingHint
			(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(225,225,225));
		g.fillRect(0, 0, 1100, 450);

		ColorReferenceSystem crs = ColorReferenceSystem.unmarshall(
				ColorReferenceSystem.class.getResource("/color/ColorReferenceSystem.xml").getPath()); //$NON-NLS-1$

		//Création de l'image du cercle principal (Couleurs pures)
		for (int j=0;j<12;j++){
			List<ColorimetricColor> listCouleurs = crs.getSlice(0, j);
			for (int i=0;i<listCouleurs.size();i++){
				ColorimetricColor couleur = listCouleurs.get(listCouleurs.size()-1-i);
				g.setColor(
						new Color(
								couleur.getRedRGB(),
								couleur.getGreenRGB(),
								couleur.getBlueRGB()));
				g.fillArc(50+i*15, 50+i*15, 300-30*i, 300-30*i, 30*(j+1), 30);
			}
		}

		//Création de l'image du cercle des Couleurs grisées
		for (int j=0;j<7;j++){
			List<ColorimetricColor> listCouleurs = crs.getSlice(1,j);
			for (int i=0;i<listCouleurs.size();i++){
				ColorimetricColor couleur = listCouleurs.get(listCouleurs.size()-1-i);
				g.setColor(
						new Color(
								couleur.getRedRGB(),
								couleur.getGreenRGB(),
								couleur.getBlueRGB()));
				g.fillArc(400+i*15, 50+i*15, 300-30*i, 300-30*i, 52*j, 52);
			}
		}

		//Création de l'image du cercle des Gris colorés
		for (int j=0;j<7;j++){
			List<ColorimetricColor> listCouleurs = crs.getSlice(2,j);
			for (int i=0;i<listCouleurs.size();i++){
				ColorimetricColor couleur = listCouleurs.get(listCouleurs.size()-1-i);
				g.setColor(
						new Color(
								couleur.getRedRGB(),
								couleur.getGreenRGB(),
								couleur.getBlueRGB()));
				g.fillArc(750+i*15, 50+i*15, 300-30*i, 300-30*i, 52*j, 52);
			}
		}

		//Création de l'image de la gamme de Gris, Noir et Blanc
		List<ColorimetricColor> listCouleurs = crs.getSlice(3,0);
		for (int i=0;i<listCouleurs.size();i++){
			ColorimetricColor couleur = listCouleurs.get(listCouleurs.size()-1-i);
			g.setColor(
					new Color(
							couleur.getRedRGB(),
							couleur.getGreenRGB(),
							couleur.getBlueRGB()));
			g.fillRect(550+i*40, 400, 40, 25);
		}

		//Création de l'image de la gamme de Marrons
		List<ColorimetricColor> listCouleursM = crs.getSlice(3,1);
		for (int i=0;i<listCouleursM.size();i++){
			ColorimetricColor couleur = listCouleursM.get(listCouleursM.size()-1-i);
			g.setColor(
					new Color(
							couleur.getRedRGB(),
							couleur.getGreenRGB(),
							couleur.getBlueRGB()));
			g.fillRect(220+i*40, 400, 40, 25);
		}

		// Création des bords pour marquer les deux gammes horizontaux et
			// le centre des cercles
		g.setColor(new Color(225,225,225));
		g.fillArc(155, 155, 90, 90, 0, 360);
		g.fillArc(460, 110, 180, 180, 0, 360);
		g.fillArc(810, 110, 180, 180, 0, 360);

		g.setColor(new Color(200,200,200));
		g.drawRect(220, 400, 40*7, 25);
		g.drawRect(550, 400, 40*9, 25);
		g.drawArc(155, 155, 90, 90, 0, 360);
		g.drawArc(460, 110, 180, 180, 0, 360);
		g.drawArc(810, 110, 180, 180, 0, 360);

		return cerclesImage;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == lblCerclesImage){
		    int xpos = e.getX();
		    int ypos = e.getY();
		    int rgb = cerclesImage.getRGB(xpos, ypos);
		    Color color = new Color(rgb);
		    ColorimetricColor newColor = ColorReferenceSystem.searchColor(color);
		    updateTable(newColor);
		}
	}

	public void updateTable(ColorimetricColor c){

		float[] labCodes = new float[3];
		labCodes = c.getLab();

		//Setting the new values on the JTable
		if (c.getRedRGB() == 225 ||
				c.getGreenRGB() == 225 ||
				c.getBlueRGB() == 225){
		}else{
			tCodesCouleur.setValueAt(c.getUsualName(),0,1);
			tCodesCouleur.setValueAt(c.getLightness(),1,1);
			tCodesCouleur.setValueAt(c.getCleCoul(),2,1);
			tCodesCouleur.setValueAt(c.getRedRGB(),0,3);
			tCodesCouleur.setValueAt(c.getGreenRGB(), 1,3);
			tCodesCouleur.setValueAt(c.getBlueRGB(), 2,3);
			tCodesCouleur.setValueAt(Math.round(labCodes[0]),0,5);
			tCodesCouleur.setValueAt(Math.round(labCodes[1]), 1,5);
			tCodesCouleur.setValueAt(Math.round(labCodes[2]), 2,5);

			createSampleImage(c.getRedRGB(),
					c.getGreenRGB(),
					c.getBlueRGB(),
					lblColorImage);
		}

		getColorSelectionModel().setSelectedColor(c.toColor());
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        Color c = COGITColorChooserPanel.showDialog(new JButton(),
                    I18N.getString("StyleEditionFrame.PickAColor"), Color.BLUE); //$NON-NLS-1$
		System.out.println(c);
	}

	/**
	 * Creates and returns a new dialog containing the specified ColorChooser pane
	 * along with "OK", "Cancel", and "Reset" buttons.
	 * If the "OK" or "Cancel" buttons are pressed, the dialog is automatically hidden (but not disposed).
	 * If the "Reset" button is pressed, the color-chooser's color will be reset to the color
	 * which was set the last time show was invoked on the dialog and the dialog will remain showing.
	 * @param component
	 * @param initialColor
	 * @return The chosen color
	 */
	public static Color showDialog(Component component, String title, Color initialColor){
		JColorChooser colorChooser = new JColorChooser(initialColor != null?
                initialColor : Color.white);

		colorChooser.addChooserPanel(new COGITColorChooserPanel());

		JDialog dialog = JColorChooser.createDialog(
				component, title, true, colorChooser, null, null);
		dialog.setVisible(true);
		Color c = colorChooser.getColor();

		return c;
	}
	@Override
	// We did this work in the constructor so we can skip it here.
	protected void buildChooser() {}

	@Override
	public String getDisplayName() {
		return "COGIT Color Reference System";
	}

	@Override
	public Icon getLargeDisplayIcon() {
		return null;
	}

	@Override
	public Icon getSmallDisplayIcon() {
		return null;
	}

	@Override
	public void updateChooser() {
		Color c = getColorSelectionModel().getSelectedColor();
		ColorimetricColor cRef = new ColorimetricColor(c, true);
		updateTable(cRef);
	}
}
