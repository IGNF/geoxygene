/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli.plugin.semio.contrast;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import fr.ign.cogit.geoxygene.appli.panel.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.semio.legend.symbol.color.Contrast;
import fr.ign.cogit.geoxygene.semio.legend.symbol.color.ContrastCollection;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * @author Charlotte Hoarau
 * 
 * <code>ContrastFrame</code> provides a frame with the COGTI chromatic wheels
 * to allow a user to select two colors and look for COGIT hue and lightness
 * contrasts.
 * It is available by an item menu of the plugin dedicated to semiotics tools.
 *
 */
public class ContrastFrame extends JFrame implements MouseListener{
	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	private JPanel tablePanel;
	private JLabel lblCerclesImage;
	private BufferedImage cerclesImage;
	private JTable colorTable;
	private JTable contrastTable;
	
	private List<Color> selectedColors;
	
	public ContrastFrame(){
		setTitle("Contrasts on Chormatic Wheels");
		cerclesImage =
			new BufferedImage(1100,450,java.awt.image.BufferedImage.TYPE_INT_RGB);
		Graphics2D g1 = cerclesImage.createGraphics();
		g1.setRenderingHint
			(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		COGITColorChooserPanel.createCercleImage(g1);
		
		lblCerclesImage = new JLabel(new ImageIcon(cerclesImage));
		lblCerclesImage.addMouseListener(this);		

		colorTable = new JTable(new ColorTableModel());
		Font f = colorTable.getFont();
	    f = f.deriveFont(Font.BOLD);
	    colorTable.setFont(f);
	    colorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    colorTable.setGridColor(new  Color(225,225,225));//Couleur des bords
	    colorTable.setBackground(new Color(200,200,200));//Couleur des cases
	    colorTable.setForeground(new Color(100,100,100));//Couleur du texte
	    colorTable.setRowHeight(25);
	    colorTable.getColumnModel().setColumnMargin(10);
	    colorTable.setDefaultRenderer(
	    		Color.class,
                new ColorRenderer(true));
	
		contrastTable = new JTable(2,3);
		
	    contrastTable.setFont(f);
		contrastTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		contrastTable.setGridColor(new  Color(225,225,225));//Couleur des bords
		contrastTable.setBackground(new Color(200,200,200));//Couleur des cases
		contrastTable.setForeground(new Color(100,100,100));//Couleur du texte
		contrastTable.setRowHeight(25);
		contrastTable.getColumnModel().setColumnMargin(10);

		contrastTable.getColumnModel().getColumn(0).setMaxWidth(300);
		contrastTable.getColumnModel().getColumn(0).setMinWidth(300);
		contrastTable.setValueAt("COGIT Contrasts",1,0);
		contrastTable.getColumnModel().getColumn(1).setMaxWidth(300);
		contrastTable.getColumnModel().getColumn(1).setMinWidth(300);
		contrastTable.setValueAt("Hue Contrast",0,1);
		contrastTable.getColumnModel().getColumn(2).setMaxWidth(300);
		contrastTable.getColumnModel().getColumn(2).setMinWidth(300);

		contrastTable.setValueAt("Lightness Contrast",0,2);
		
		tablePanel = new JPanel();
		tablePanel.setBackground(new Color(225,225,225));
		tablePanel.add(colorTable);
		tablePanel.add(contrastTable);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(lblCerclesImage, BorderLayout.NORTH);
		mainPanel.add(tablePanel, BorderLayout.SOUTH);
		add(mainPanel);
		pack();
		
		selectedColors = new ArrayList<Color>();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ContrastFrame frame = new ContrastFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == lblCerclesImage){
		    int xpos = e.getX();
		    int ypos = e.getY();
		    
		    int rgb = cerclesImage.getRGB(xpos, ypos);
		    Color color = new Color(rgb);
		    ColorimetricColor newColor = ColorReferenceSystem.searchColor(color);
		    
		    JLabel label = (JLabel)e.getSource();

		    
		    Color backgroundColor = new Color(225, 225, 225);
		    //Update only if the selected color is not the background one
		    if (rgb != backgroundColor.getRGB()) {
		    	
		    	if (selectedColors.size() == 0) {//First Color selected
		    		COGITColorChooserPanel.displayColor(
							(Graphics2D)((ImageIcon)label.getIcon()).getImage().getGraphics(),
							newColor);
					mainPanel.repaint();
					mainPanel.validate();
					
					//stocking selected colors
					selectedColors.add(color);
		    	
		    		((ColorTableModel)colorTable.getModel()).setValueAt(color,0,1);
		    	} else if (selectedColors.size() == 1) {
		    		if (selectedColors.contains(color)){//Same color selected -> Color unselected
			    		selectedColors.remove(color);
						((ColorTableModel)colorTable.getModel()).setValueAt(new Color(200, 200, 200),0,1);
						COGITColorChooserPanel.clearDisplayColor(
			    				(Graphics2D)((ImageIcon)label.getIcon()).getImage().getGraphics(),
			    				newColor);
			    		mainPanel.repaint();
						mainPanel.validate();
			    	} else {//Second Color selected
		    		COGITColorChooserPanel.displayColor(
							(Graphics2D)((ImageIcon)label.getIcon()).getImage().getGraphics(),
							newColor);
					mainPanel.repaint();
					mainPanel.validate();
					
					//stocking selected colors
					selectedColors.add(color);
		    	
		    		((ColorTableModel)colorTable.getModel()).setValueAt(color,1,1);
		    		
		    		updateTable();
			    	}
		    	} else if (selectedColors.size() == 2) {
		    		if (selectedColors.contains(color)){//a color has been unselected
			    		clearTable();
			    		COGITColorChooserPanel.clearDisplayColor(
			    				(Graphics2D)((ImageIcon)label.getIcon()).getImage().getGraphics(),
			    				newColor);
			    		mainPanel.repaint();
						mainPanel.validate();
						
			    		if (color.equals(selectedColors.get(0))) {//First color unselected
			    			selectedColors.remove(color);
							((ColorTableModel)colorTable.getModel()).setValueAt(selectedColors.get(0),0,1);
							((ColorTableModel)colorTable.getModel()).setValueAt(new Color(200, 200, 200),1,1);
			    		} else {//Second color unselected
			    			selectedColors.remove(color);
							((ColorTableModel)colorTable.getModel()).setValueAt(new Color(200, 200, 200),1,1);
			    		}
			    	} else {//Trying to select a third color
			    		JOptionPane.showMessageDialog(this, "Only two colors must be selected simultaneously! Please unselect one color before going on.", "Legent Tree not finished", JOptionPane.ERROR_MESSAGE);
			    	}
		    	}
		    }
		}
	}
	
	public void updateTable(){
		Contrast contrast = ContrastCollection.getCOGITContrast(
				ColorReferenceSystem.searchColor(selectedColors.get(0)),
				ColorReferenceSystem.searchColor(selectedColors.get(1)));
		
		contrastTable.setValueAt(contrast.getContrasteTeinte(),1,1);
		contrastTable.setValueAt(contrast.getContrasteClarte(),1,2);
	}
	
	public void clearTable(){
		contrastTable.setValueAt("",1,1);
		contrastTable.setValueAt("",1,2);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	
	
	/**
	 * Table model to design the table showing the selected colors.
	 * 
	 * @author CHoarau
	 *
	 */
	class ColorTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		private String[] columnNames = {
				"Color Names",
                "Colors"};
		
		private Object[][] data = {
	            {"Color 1", new Color(200, 200, 200)},
	            {"Color 2", new Color(200, 200, 200)}
		};
		
		@Override
		public int getColumnCount() {
            return columnNames.length;
		}

		@Override
		public int getRowCount() {
            return data.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
		}
		
		@Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
        
        @Override
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
	}
}
