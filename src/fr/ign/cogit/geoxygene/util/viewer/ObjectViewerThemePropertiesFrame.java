/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import uk.ac.leeds.ccg.dbffile.Dbf;
import uk.ac.leeds.ccg.geotools.HSVShader;
import uk.ac.leeds.ccg.geotools.MonoShader;
import uk.ac.leeds.ccg.geotools.RandomShader;
import uk.ac.leeds.ccg.geotools.Shader;
import uk.ac.leeds.ccg.geotools.ShapefileReader;
import uk.ac.leeds.ccg.geotools.UniqueShader;

/**
 * This class implements the frame for the defintion of properties of themes to be displayed
 * in the (Geo)Object viewer.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class ObjectViewerThemePropertiesFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3613183796524975458L;

	public static final String FRAME_TITLE =
		"GeOxygene Object Viewer - Theme Properties";

	private ObjectViewerThemeProperties objectViewerThemeProperties;

	public ObjectViewerThemePropertiesFrame(ObjectViewerThemeProperties objectViewerThemeProperties) {

		super(FRAME_TITLE);

		this.objectViewerThemeProperties = objectViewerThemeProperties;


		//Set up the theme properties panel
		JTabbedPane themePropertiesTabbedPanel = new JTabbedPane();

		JPanel themePropertiesPanel = new JPanel(new GridLayout(6, 2, 10, 10));
		themePropertiesPanel.setBorder(
				BorderFactory.createTitledBorder("Theme Properties"));

		JPanel themeShadersPanel = new JPanel(new GridLayout(3, 2, 1, 50));
		themeShadersPanel.setBorder(
				BorderFactory.createTitledBorder("Shader Properties"));

		//Add the components to the themePropertiesPanel
		JLabel fillThemeColorLabel = new JLabel("Fill in Theme Color:");
		Icon fillThemeColorButtonIcon =
			new RectIcon(
					getObjectViewerThemeProperties().getFillInThemeColor());
		final JButton fillThemeColorButton =
			new JButton(fillThemeColorButtonIcon);
		fillThemeColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFrame ccframe =
					new ObjectViewerColorChooser(
							((RectIcon) (fillThemeColorButton.getIcon()))
							.getColor(),
							fillThemeColorButton);
				ccframe.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						ccframe.dispose();
					}
				});
				ccframe.setResizable(false);
				ccframe.pack();
				ccframe.setVisible(true);
			}
		});

		JLabel outlineThemeColorLabel = new JLabel("Outline Theme Color:");
		Icon outlineThemeColorButtonIcon =
			new RectIcon(
					getObjectViewerThemeProperties().getOutlineThemeColor());
		final JButton outlineThemeColorButton =
			new JButton(outlineThemeColorButtonIcon);
		outlineThemeColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFrame ccframe =
					new ObjectViewerColorChooser(
							((RectIcon) (outlineThemeColorButton.getIcon()))
							.getColor(),
							outlineThemeColorButton);
				ccframe.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						ccframe.dispose();
					}
				});
				ccframe.setResizable(false);
				ccframe.pack();
				ccframe.setVisible(true);
			}
		});

		JLabel fillHighlightColorLabel = new JLabel("Fill in Highlight Color:");
		Icon fillHighlightColorButtonIcon =
			new RectIcon(objectViewerThemeProperties.getFillInHighlightColor());
		final JButton fillHighlightColorButton =
			new JButton(fillHighlightColorButtonIcon);
		fillHighlightColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFrame ccframe =
					new ObjectViewerColorChooser(
							((RectIcon) (fillHighlightColorButton.getIcon()))
							.getColor(),
							fillHighlightColorButton);
				ccframe.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						ccframe.dispose();
					}
				});
				ccframe.setResizable(false);
				ccframe.pack();
				ccframe.setVisible(true);
			}
		});

		JLabel outlineHighlightColorLabel =
			new JLabel("Outline Highlight Color:");
		Icon outlineHighlightColorButtonIcon =
			new RectIcon(
					getObjectViewerThemeProperties().getOutlineHighlightColor());
		final JButton outlineHighlightColorButton =
			new JButton(outlineHighlightColorButtonIcon);
		outlineHighlightColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFrame ccframe =
					new ObjectViewerColorChooser(
							((RectIcon) (outlineHighlightColorButton.getIcon()))
							.getColor(),
							outlineHighlightColorButton);
				ccframe.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						ccframe.dispose();
					}
				});
				ccframe.setResizable(false);
				ccframe.pack();
				ccframe.setVisible(true);
			}
		});

		JLabel fillSelectionColorLabel = new JLabel("Fill in Selection Color:");
		Icon fillSelectionColorButtonIcon =
			new RectIcon(
					getObjectViewerThemeProperties().getFillInSelectionColor());
		final JButton fillSelectionColorButton =
			new JButton(fillSelectionColorButtonIcon);
		fillSelectionColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFrame ccframe =
					new ObjectViewerColorChooser(
							((RectIcon) (fillSelectionColorButton.getIcon()))
							.getColor(),
							fillSelectionColorButton);
				ccframe.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						ccframe.dispose();
					}
				});
				ccframe.setResizable(false);
				ccframe.pack();
				ccframe.setVisible(true);
			}
		});

		JLabel outlineSelectionColorLabel =
			new JLabel("Outline Selection Color:");
		Icon outlineSelectionColorButtonIcon =
			new RectIcon(
					getObjectViewerThemeProperties().getOutlineSelectionColor());
		final JButton outlineSelectionColorButton =
			new JButton(outlineSelectionColorButtonIcon);
		outlineSelectionColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFrame ccframe =
					new ObjectViewerColorChooser(
							((RectIcon) (outlineSelectionColorButton.getIcon()))
							.getColor(),
							outlineSelectionColorButton);
				ccframe.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						ccframe.dispose();
					}
				});
				ccframe.setResizable(false);
				ccframe.pack();
				ccframe.setVisible(true);
			}
		});

		themePropertiesPanel.add(fillThemeColorLabel);
		themePropertiesPanel.add(fillThemeColorButton);
		themePropertiesPanel.add(outlineThemeColorLabel);
		themePropertiesPanel.add(outlineThemeColorButton);

		themePropertiesPanel.add(fillHighlightColorLabel);
		themePropertiesPanel.add(fillHighlightColorButton);
		themePropertiesPanel.add(outlineHighlightColorLabel);
		themePropertiesPanel.add(outlineHighlightColorButton);

		themePropertiesPanel.add(fillSelectionColorLabel);
		themePropertiesPanel.add(fillSelectionColorButton);
		themePropertiesPanel.add(outlineSelectionColorLabel);
		themePropertiesPanel.add(outlineSelectionColorButton);


		//Add the components to the themeShadersPanel
		final JComboBox shaderTypeComboBox ;
		final JComboBox attributeDoubleComboBox;
		final JComboBox attributeStringComboBox;

		if (getObjectViewerThemeProperties().getDataSourceType() == Utils.GEOXYGENE) {

			JLabel shaderLabel = new JLabel("Type of shader:");
			shaderTypeComboBox = new JComboBox( new String[] {"Mono", "Random", "Continuous", "Unique"} );
			shaderTypeComboBox.setSelectedItem( getShaderName(getObjectViewerThemeProperties().getShader()) );
			final JLabel attributeDoubleLabel = new JLabel("Attributes (double or int):");
			attributeDoubleComboBox = new JComboBox();
			final JLabel attributeStringLabel = new JLabel("Attributes (String):");
			attributeStringComboBox = new JComboBox();

			String[] doubleFieldsNames = getDoubleOrIntFields();
			if (doubleFieldsNames.length > 0) {
				for (int i=0; i<doubleFieldsNames.length; i++)
					attributeDoubleComboBox.addItem(doubleFieldsNames[i]);
				if (getObjectViewerThemeProperties().getShadedBy() != null
						&& getSelectedFieldClass(getObjectViewerThemeProperties().getShadedBy()).equals(double.class) )
					attributeDoubleComboBox.setSelectedItem( getObjectViewerThemeProperties().getShadedBy() );
				else
					attributeDoubleComboBox.setSelectedItem(doubleFieldsNames[0]);
			}

			String[] stringFieldsNames = getStringFields();
			if (stringFieldsNames.length > 0) {
				for (int i=0; i<stringFieldsNames.length; i++)
					attributeStringComboBox.addItem(stringFieldsNames[i]);
				if (getObjectViewerThemeProperties().getShadedBy() != null
						&& getSelectedFieldClass(getObjectViewerThemeProperties().getShadedBy()).equals(String.class) )
					attributeStringComboBox.setSelectedItem( getObjectViewerThemeProperties().getShadedBy() );
				else
					attributeStringComboBox.setSelectedItem(stringFieldsNames[0]);
			}

			themeShadersPanel.add(shaderLabel);
			themeShadersPanel.add(shaderTypeComboBox);
			themeShadersPanel.add(attributeDoubleLabel);
			themeShadersPanel.add(attributeDoubleComboBox);
			themeShadersPanel.add(attributeStringLabel);
			themeShadersPanel.add(attributeStringComboBox);

			attributeDoubleLabel.setEnabled(false);
			attributeDoubleComboBox.setEnabled(false);
			attributeStringLabel.setEnabled(false);
			attributeStringComboBox.setEnabled(false);

			shaderTypeComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (shaderTypeComboBox.getSelectedItem().equals("Continuous")) {
						attributeDoubleLabel.setEnabled(true);
						attributeDoubleComboBox.setEnabled(true);
						attributeStringLabel.setEnabled(false);
						attributeStringComboBox.setEnabled(false);
					} else if (shaderTypeComboBox.getSelectedItem().equals("Unique")) {
						attributeDoubleLabel.setEnabled(false);
						attributeDoubleComboBox.setEnabled(false);
						attributeStringLabel.setEnabled(true);
						attributeStringComboBox.setEnabled(true);
					} else {
						attributeDoubleLabel.setEnabled(false);
						attributeDoubleComboBox.setEnabled(false);
						attributeStringLabel.setEnabled(false);
						attributeStringComboBox.setEnabled(false);
					}
				}
			});

		} else {
			shaderTypeComboBox = null;
			attributeDoubleComboBox = null;
			attributeStringComboBox = null;
		}


		//Add the control panel at the bottom of the window
		final JPanel controlPanel =
			new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ObjectViewerThemePropertiesFrame.this.dispose();
				getObjectViewerThemeProperties().setFillInThemeColor(
						((RectIcon) (fillThemeColorButton.getIcon())).getColor());
				getObjectViewerThemeProperties().setOutlineThemeColor(
						((RectIcon) (outlineThemeColorButton.getIcon()))
						.getColor());
				getObjectViewerThemeProperties().setFillInHighlightColor(
						((RectIcon) (fillHighlightColorButton.getIcon()))
						.getColor());
				getObjectViewerThemeProperties().setOutlineHighlightColor(
						((RectIcon) (outlineHighlightColorButton.getIcon()))
						.getColor());
				getObjectViewerThemeProperties().setFillInSelectionColor(
						((RectIcon) (fillSelectionColorButton.getIcon()))
						.getColor());
				getObjectViewerThemeProperties().setOutlineSelectionColor(
						((RectIcon) (outlineSelectionColorButton.getIcon()))
						.getColor());
				if ((getObjectViewerThemeProperties().getDataSourceType() == Utils.GEOXYGENE)&&(attributeDoubleComboBox!=null)&&(attributeStringComboBox!=null)&&(shaderTypeComboBox!=null)) {
					if (attributeDoubleComboBox.isEnabled() && !attributeStringComboBox.isEnabled())
						getObjectViewerThemeProperties().setShader( selectShader(( String) shaderTypeComboBox.getSelectedItem()) ,
								(String) attributeDoubleComboBox.getSelectedItem() );
					else if (!attributeDoubleComboBox.isEnabled() && attributeStringComboBox.isEnabled())
						getObjectViewerThemeProperties().setShader( selectShader(( String) shaderTypeComboBox.getSelectedItem()) ,
								(String) attributeStringComboBox.getSelectedItem() );
					else getObjectViewerThemeProperties().setShader( selectShader(( String) shaderTypeComboBox.getSelectedItem()));
				}
				getObjectViewerThemeProperties().setChanged();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ObjectViewerThemePropertiesFrame.this.dispose();
			}
		});
		controlPanel.add(okButton);
		controlPanel.add(cancelButton);


		//Add the components to the colorchooser frame
		Container contentPane = getContentPane();
		themePropertiesTabbedPanel.addTab("Colors", themePropertiesPanel);
		themePropertiesTabbedPanel.setToolTipTextAt(
				0,
		"Set the colors for the current theme.");
		themePropertiesTabbedPanel.setSelectedIndex(0);
		themePropertiesTabbedPanel.addTab("Shaders", themeShadersPanel);
		themePropertiesTabbedPanel.setToolTipTextAt(
				1,
		"Change the shader of the current theme.");

		contentPane.add(themePropertiesTabbedPanel, BorderLayout.CENTER);
		contentPane.add(controlPanel, BorderLayout.SOUTH);

	}


	private Shader selectShader(String type) {
		if (type.equals("Mono")) return new MonoShader();
		else if (type.equals("Random")) return new RandomShader();
		else if (type.equals("Continuous")) return new HSVShader();
		else if (type.equals("Unique")) return new UniqueShader();
		else System.out.println("### PROBLEM selecting shader ...");
		return null;
	}


	private String getShaderName(Shader shader) {
		if (shader instanceof MonoShader) return "Mono";
		else if (shader instanceof RandomShader) return "Random";
		else if (shader instanceof HSVShader) return "Continuous";
		else if (shader instanceof UniqueShader) return "Unique";
		else return "### PROBLEM selecting shader name !!";
	}


	private String[] getFieldNames() {
		String[] fieldsName = new String[0];
		if (getObjectViewerThemeProperties().getDataSourceType().equals(Utils.SHAPEFILE)) {
			ShapefileReader shpRd = (ShapefileReader) getObjectViewerThemeProperties().getDataSource();
			Dbf dbf = shpRd.dbf;
			fieldsName = new String[dbf.getNumFields()];
			for (int i=0; i<fieldsName.length; i++)
				fieldsName[i] = dbf.getFieldName(i).toString();
		}

		else if (getObjectViewerThemeProperties().getDataSourceType().equals(Utils.GEOXYGENE)) {
			GeOxygeneReader geOxyRd = (GeOxygeneReader) getObjectViewerThemeProperties().getDataSource();
			fieldsName = geOxyRd.getFieldsNames();
		}
		return fieldsName;
	}


	private String[] getDoubleOrIntFields() {
		Vector<String> vector = new Vector<String>();
		String[] allFields = getFieldNames();
		for (int i=0; i<allFields.length; i++)
			if (getSelectedFieldClass(allFields[i]).equals(double.class) ||
					getSelectedFieldClass(allFields[i]).equals(int.class))
				vector.add(allFields[i]);
		String[] array = new String[vector.size()];
		for (int i=0; i<vector.size(); i++)
			array[i] = vector.get(i);
		return array;
	}


	private String[] getStringFields() {
		Vector<String> vector = new Vector<String>();
		String[] allFields = getFieldNames();
		for (int i=0; i<allFields.length; i++)
			if (getSelectedFieldClass(allFields[i]).equals(String.class))
				vector.add(allFields[i]);
		String[] array = new String[vector.size()];
		for (int i=0; i<vector.size(); i++)
			array[i] = vector.get(i);
		return array;
	}


	private Class<?> getSelectedFieldClass (String fieldName) {
		Class<?> result = null;
		if (getObjectViewerThemeProperties().getDataSourceType().equals(Utils.GEOXYGENE)) {
			GeOxygeneReader geOxyRd = (GeOxygeneReader) getObjectViewerThemeProperties().getDataSource();
			result = geOxyRd.getFieldType(fieldName);
		}
		return result;
	}


	private ObjectViewerThemeProperties getObjectViewerThemeProperties() {
		return objectViewerThemeProperties;
	}

}