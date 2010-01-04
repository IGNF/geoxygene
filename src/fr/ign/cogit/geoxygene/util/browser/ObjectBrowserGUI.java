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

package fr.ign.cogit.geoxygene.util.browser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Cette classe met en oeuvre l'interface graphique (vue et contrôleur) du Navigateur d'objet de GeOxygene, conformement à
 * l'architecture à Modèle séparable de Sun Microsystems.
 * Les méthodes mises à disposition par cette classe sont appelées depuis la classe ObjectBrowser (Modèle).
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class ObjectBrowserGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Localisation des fichiers d'internationalisation de l'interface. */
	private static final String I18N_LANGUAGE_FILE_LOCATION =
		"fr.ign.cogit.geoxygene.util.browser.ObjectBrowserLanguageFile"; //$NON-NLS-1$
	/** Nombre de lignes visibles dans l'interface pour les attributs de type tableau ou Collection. */
	private static final int NBVISIBLEROW = 3;

	/** Gestionnaire de contraintes de mise en page du navigateur d'objet. */
	public GridBagConstraints gbc = new GridBagConstraints();
	/** Gestionnaire de mise en page du navigateur d'objet. */
	public GridBagLayout gbl = new GridBagLayout();
	/** Insets définissant l'espacement entre les différents composants graphiques de l'interface. */
	public Insets extpad = new Insets(2, 5, 2, 5);
	/** Panel d'affichage dans lequel sont positionnés les éléments. */
	public JPanel panel = new JPanel();
	public GridLayout gl_panel = new GridLayout();
	public int ncc;

	/** L'objet dont l'interface courante du navigateur d'objet de GeOxygene est la représentation. */
	private Object browsedObject;

	/** Flag définissant si l'affichage d'un bandeau contenant le nom du type de l'objet a été demandé par le Modèle (ObjectBrowser). */
	boolean showClassName;
	/** Flag définissant si l'affichage des attributs publics de l'objet a été demandé par le Modèle (ObjectBrowser). */
	boolean showPublicAttributes;
	/** Flag définissant si l'affichage des attributs protected de l'objet a été demandé par le Modèle (ObjectBrowser). */
	boolean showProtectedAttributes;
	/** Flag définissant si l'affichage des méthodes publiques, locales et héritées de l'objet a été demandé par le Modèle (ObjectBrowser). */
	boolean showPublicMethods;
	/** Flag définissant si l'affichage des méthodes protected, locales et héritées de l'objet a été demandé par le Modèle (ObjectBrowser). */
	boolean showProtectedMethods;

	/** Locale courante. */
	private Locale currentLocale;
	/** RessourceBundle lié à la Locale et au fichier d'internationalisation. */
	private ResourceBundle i18nLanguageFile;

	/**
	 * Constructeur par défaut.
	 * 
	 * @param browsedObject l'objet que l'interface du navigateur que l'on cherche à construire doit représenter.
	 * @param showClassName l'affichage d'un bandeau comportant le nom du type de l'objet est-il demandé ?
	 * @param showPublicAttributes l'affichage des attributs publics de l'objet est-il demandé ?
	 * @param showProtectedAttributes l'affichage des attributs protected de l'objet est-il demandé ?
	 * @param showPublicMethods l'affichage des méthodes publiques, locales et héritées de l'objet est-il demandé ?
	 * @param showProtectedMethods l'affichage des méthodes protected, locales et héritées de l'objet est-il demandé ?
	 */
	public ObjectBrowserGUI(
			Object browsedObject,
			boolean showClassName,
			boolean showPublicAttributes,
			boolean showProtectedAttributes,
			boolean showPublicMethods,
			boolean showProtectedMethods) {
		super();
		this.currentLocale = Locale.getDefault();
		this.i18nLanguageFile = ResourceBundle.getBundle(I18N_LANGUAGE_FILE_LOCATION,this.currentLocale);
		//i18nLanguageFile = ResourceBundle.getBundle(I18N_LANGUAGE_FILE_LOCATION,new Locale("en", "US"));

		setTitle(this.i18nLanguageFile.getString("DefaultFrameTitle")); //$NON-NLS-1$
		this.browsedObject = browsedObject;
		this.showClassName = showClassName;
		this.showPublicAttributes = showPublicAttributes;
		this.showProtectedAttributes = showProtectedAttributes;
		this.showPublicMethods = showPublicMethods;
		this.showProtectedMethods = showProtectedMethods;
		initInterface();
	}

	/**
	 * Construit une instance du navigateur d'objet de GeOxygene pour l'objet browsedObject avec un titre de fenêtre faisant référence à className.
	 * 
	 * @param browsedObject l'objet que l'interface du navigateur que l'on cherche à construire doit représenter.
	 * @param showClassName l'affichage d'un bandeau comportant le nom du type de l'objet est-il demandé ?
	 * @param showPublicAttributes l'affichage des attributs publics de l'objet est-il demandé ?
	 * @param showProtectedAttributes l'affichage des attributs protected de l'objet est-il demandé ?
	 * @param showPublicMethods l'affichage des méthodes publiques, locales et héritées de l'objet est-il demandé ?
	 * @param showProtectedMethods l'affichage des méthodes protected, locales et héritées de l'objet est-il demandé ?
	 * @param className le nom du type de l'objet représenté par l'interface du navigateur que l'on cherche à construire.
	 */
	public ObjectBrowserGUI(
			Object browsedObject,
			boolean showClassName,
			boolean showPublicAttributes,
			boolean showProtectedAttributes,
			boolean showPublicMethods,
			boolean showProtectedMethods,
			String className) {
		super();
		this.currentLocale = Locale.getDefault();
		this.i18nLanguageFile = ResourceBundle.getBundle(I18N_LANGUAGE_FILE_LOCATION,this.currentLocale);
		//i18nLanguageFile = ResourceBundle.getBundle(I18N_LANGUAGE_FILE_LOCATION,new Locale("en", "US"));

		setTitle(this.i18nLanguageFile.getString("DefaultFrameTitle")+" "+className); //$NON-NLS-1$ //$NON-NLS-2$
		this.browsedObject = browsedObject;
		this.showClassName = showClassName;
		this.showPublicAttributes = showPublicAttributes;
		this.showProtectedAttributes = showProtectedAttributes;
		this.showPublicMethods = showPublicMethods;
		this.showProtectedMethods = showProtectedMethods;
		initInterface();
	}

	/**
	 * méthode permettant le changement du titre de la fenêtre d'interface représentant l'objet courant.
	 * 
	 * @param className le nom du type de l'objet représenté par l'interface du navigateur d'objet.
	 */
	protected void changeTitle(String className) {
		setTitle(this.i18nLanguageFile.getString("DefaultFrameTitle")+" "+className); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Initialisation de l'interface graphique du navgateur d'objet.
	 */
	private void initInterface() {

		this.ncc = 0;
		this.getContentPane().setLayout(this.gl_panel);
		this.panel.setLayout(this.gbl);

		JScrollPane scrolling_panel = new JScrollPane(this.panel);
		JMenuBar jmb = new JMenuBar();

		// Definition of the "File" menu and of the "Close" attached item.

		JMenu file = new JMenu(this.i18nLanguageFile.getString("MenuFileLabel")); //$NON-NLS-1$
		JMenuItem item;
		file.add(
				item =
					new JMenuItem(
							this.i18nLanguageFile.getString("MenuFileCloseItemLabel"))); //$NON-NLS-1$
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// Definition of the "Exit" item.

		file.addSeparator();
		file.add(
				item =
					new JMenuItem(
							this.i18nLanguageFile.getString("MenuFileExitItemLabel"))); //$NON-NLS-1$
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
		jmb.add(file);

		// Definition of the "Edit" menu and of the "Refresh" attached item.

		JMenu edit = new JMenu(this.i18nLanguageFile.getString("MenuEditLabel")); //$NON-NLS-1$
		edit.add(
				item =
					new JMenuItem(
							this.i18nLanguageFile.getString("MenuEditRefreshItemLabel"))); //$NON-NLS-1$
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ObjectBrowser.refresh(
						getBrowsedObject(),
						ObjectBrowser.HIDE_WHEN_REFRESH,
						ObjectBrowserGUI.this.showClassName,
						ObjectBrowserGUI.this.showPublicAttributes,
						ObjectBrowserGUI.this.showProtectedAttributes,
						ObjectBrowserGUI.this.showPublicMethods,
						ObjectBrowserGUI.this.showProtectedMethods);
			}
		});
		jmb.add(edit);

		this.setJMenuBar(jmb);

		this.getContentPane().add(scrolling_panel);
	}

	/**
	 * Ajoute un bandeau à l'interface contenant l'affichage du type (className) de l'objet représenté
	 * par l'instance courante du navigateur d'objet.
	 * 
	 * @param className le type de l'objet représenté par l'instance courante du navigateur d'objet.
	 */
	public void addClassNameLabel(String className) {
		JLabel label = new JLabel(className, SwingConstants.CENTER);
		label.setOpaque(true);
		label.setBackground(Color.GRAY);

		label.setFont(new Font("SansSerif", Font.BOLD, 16)); //$NON-NLS-1$

		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.gridy = this.ncc;
		this.gbc.gridwidth = 2;
		this.gbc.gridheight = 1;
		this.gbc.insets = this.extpad;

		this.gbc.gridx = 0;
		this.gbc.weightx = 25;
		this.gbl.setConstraints(label, this.gbc);
		this.panel.add(label);

		this.ncc++;
	}

	/**
	 * Ajoute à l'interface du navigateur, la représentation d'un attribut de type primitif ou chaîne de caractères.
	 * 
	 * @param attrib_label le nom de l'attribut.
	 * @param attrib_value la valeur de l'attribut.
	 */
	public void addAttribute(String attrib_label, String attrib_value) {
		JLabel label = new JLabel(attrib_label + " :", SwingConstants.RIGHT); //$NON-NLS-1$
		JTextField txtfld = new JTextField(attrib_value);

		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.gridy = this.ncc;
		this.gbc.gridwidth = 1;
		this.gbc.gridheight = 1;
		this.gbc.insets = this.extpad;

		this.gbc.gridx = 0;
		this.gbc.weightx = 25;
		this.gbl.setConstraints(label, this.gbc);
		this.panel.add(label);

		this.gbc.gridx = 1;
		this.gbc.weightx = 75;
		this.gbl.setConstraints(txtfld, this.gbc);
		this.panel.add(txtfld);

		this.ncc++;
	}

	/**
	 * Ajoute à l'interface du navigateur, la représentation d'un attribut de type objet.
	 * 
	 * @param attrib_label le nom de l'attribut.
	 * @param attrib_type le type de l'attribut.
	 * @param attrib_obj la valeur de l'attribut.
	 */
	public void addObjectAttribute(
			String attrib_label,
			String attrib_type,
			Object attrib_obj) {
		JLabel label = new JLabel(attrib_label + " :", SwingConstants.RIGHT); //$NON-NLS-1$
		JButton object_button = new JButton(attrib_type);
		object_button.setToolTipText(
				"Visualiser l'objet de type " + attrib_type + ".");
		object_button.addActionListener(
				new ObjectBrowserAttributeListener(attrib_obj));

		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.gridy = this.ncc;
		this.gbc.gridwidth = 1;
		this.gbc.gridheight = 1;
		this.gbc.insets = this.extpad;

		this.gbc.gridx = 0;
		this.gbc.weightx = 25;
		this.gbl.setConstraints(label, this.gbc);
		this.panel.add(label);

		this.gbc.gridx = 1;
		this.gbc.weightx = 75;
		this.gbl.setConstraints(object_button, this.gbc);
		this.panel.add(object_button);

		this.ncc++;
	}

	/**
	 * Ajoute à l'interface du navigateur, un composant graphique représentant le contenu d'un tableau ou d'une
	 * collection de type primitif ou chaîne de caractères.
	 * <p>ATTENTION: méthode vouée à disparaêtre, car remplacée par addObjectAttributeList() !</p>
	 * @param attrib_values un vecteur contenant l'ensemble des valeurs de l'objet de type tableau ou collection.
	 */
	public void addAttributeList(Vector<Object> attrib_values) {
		//addAttributeList(i18nLanguageFile.getString("DefaultCollectionClassesContentLabel"),attrib_values);
		addObjectAttributeList(this.i18nLanguageFile.getString("DefaultCollectionClassesContentLabel"),attrib_values); //$NON-NLS-1$
	}

	/**
	 * Ajoute à l'interface du navigateur, un composant graphique représentant le contenu d'un attribut de type tableau ou
	 * collection de type primitif ou chaîne de caractères.
	 * <p>ATTENTION: méthode vouée à disparaêtre, car remplacée par addObjectAttributeList() !</p>
	 * @param attrib_label le nom de l'attribut.
	 * @param attrib_values les valeurs du tableau ou de la collection portés par l'attribut.
	 */
	public void addAttributeList(String attrib_label, Vector<Object> attrib_values) {

		addObjectAttributeList(attrib_label, attrib_values);

		/* JLabel label = new JLabel(attrib_label + " :", JLabel.RIGHT);
		JList attribList = new JList(attrib_values);
		attribList.setVisibleRowCount(NBVISIBLEROW);
		attribList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane attribPane = new JScrollPane(attribList);

		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.gridy = this.ncc;
		this.gbc.gridwidth = 1;
		this.gbc.gridheight = 1;
		this.gbc.insets = this.extpad;

		this.gbc.gridx = 0;
		this.gbc.weightx = 25;
		this.gbl.setConstraints(label, gbc);
		this.panel.add(label);

		this.gbc.gridx = 1;
		this.gbc.weightx = 75;
		this.gbl.setConstraints(attribPane, gbc);
		this.panel.add(attribPane);

		this.ncc++; */
	}

	/**
	 * Ajoute à l'interface du navigateur, un composant graphique représentant le contenu d'un tableau ou d'une
	 * collection.
	 * @param attrib_objects un vecteur contenant l'ensemble des valeurs de l'objet de type tableau ou collection.
	 */
	public void addObjectAttributeList(Vector<Object> attrib_objects) {
		addObjectAttributeList(this.i18nLanguageFile.getString("DefaultCollectionClassesContentLabel"), attrib_objects, 1, ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Ajoute à l'interface du navigateur, un composant graphique représentant le contenu d'un attribut de type tableau ou
	 * collection.
	 * @param attrib_label le nom de l'attribut.
	 * @param attrib_objects les valeurs du tableau ou de la collection portés par l'attribut.
	 */
	public void addObjectAttributeList(
			String attrib_label,
			Vector<Object> attrib_objects) {
		addObjectAttributeList(attrib_label, attrib_objects, 1, ""); //$NON-NLS-1$
	}

	/**
	 * Ajoute à l'interface du navigateur, un composant graphique représentant le contenu d'un attribut de type tableau ou
	 * collection.
	 * @param attrib_label le nom de l'attribut.
	 * @param attrib_objects les valeurs du tableau ou de la collection portés par l'attribut.
	 * @param attrib_level la dimension du tableau ou de la collection.
	 * @param attrib_underlyingType le type du contenu du tableau ou de la collection.
	 */
	public void addObjectAttributeList(
			String attrib_label,
			Vector<Object> attrib_objects,
			int attrib_level,
			String attrib_underlyingType) {
		JLabel label = new JLabel(attrib_label + " :", SwingConstants.RIGHT); //$NON-NLS-1$
		JPanel attribListPanel = new JPanel();
		int NbElemAttribList = attrib_objects.size();
		GridLayout gl_attribListPanel = new GridLayout(NbElemAttribList, 1);
		//GridLayout gl_attribListPanel = new GridLayout(NbElemAttribList,2);
		attribListPanel.setLayout(gl_attribListPanel);
		String attribTypeName;
		Class<?> attribType;
		String attribValue;
		double prefHeight = 0;
		double prefWidth = 0;
		double prefWidthMax = 0;
		boolean isTextPrintableField=false;

		for (int i = 0; i < NbElemAttribList; i++) {
			//JLabel listlabel = new JLabel(i+":",JLabel.RIGHT);
			//attribListPanel.add(listlabel);

			attribType = attrib_objects.get(i).getClass();
			attribValue = attrib_objects.get(i).toString();
			isTextPrintableField=false;

			if (attrib_underlyingType.equals("")) { //$NON-NLS-1$
				attribTypeName = attribType.getName();
			} else {
				attribTypeName = ""; //$NON-NLS-1$
				for (int j = 0; j < attrib_level; j++) {
					attribTypeName += "["; //$NON-NLS-1$
				}
				attribTypeName += attrib_underlyingType;
				for (int j = 0; j < attrib_level; j++) {
					attribTypeName += "]"; //$NON-NLS-1$
				}
			}

			if (attribTypeName.equals("java.lang.String") //$NON-NLS-1$
					|| attribTypeName.equals("java.lang.Boolean") //$NON-NLS-1$
					|| attribTypeName.equals("java.lang.Byte") //$NON-NLS-1$
					|| attribTypeName.equals("java.lang.Character") //$NON-NLS-1$
					|| attribTypeName.equals("java.lang.Double") //$NON-NLS-1$
					|| attribTypeName.equals("java.lang.Float") //$NON-NLS-1$
					|| attribTypeName.equals("java.lang.Integer") //$NON-NLS-1$
					|| attribTypeName.equals("java.lang.Long") //$NON-NLS-1$
					|| attribTypeName.equals("java.lang.Short")) { //$NON-NLS-1$
				isTextPrintableField=true;
			}

			//if (!((attribValue.indexOf("@") > -1) || (attribValue.lastIndexOf("[") > -1))) {
			if (isTextPrintableField) {
				JTextField txtfld = new JTextField(attribValue);
				attribListPanel.add(txtfld);
				if (i < NBVISIBLEROW) {
					//prefsize += 7 + txtfld.getPreferredSize().getHeight();
					prefHeight += txtfld.getPreferredSize().getHeight();
				}
				prefWidth = txtfld.getPreferredSize().getWidth();
				if (prefWidth > prefWidthMax) prefWidthMax = prefWidth;
			} else {
				JButton object_button = new JButton(attribTypeName);

				Object[] msgArguments = { new Integer(i), attribTypeName };
				MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
				formatter.setLocale(this.currentLocale);
				formatter.applyPattern(
						this.i18nLanguageFile.getString("AttributeToolTipLabel")); //$NON-NLS-1$
				object_button.setToolTipText(formatter.format(msgArguments));

				object_button.addActionListener(
						new ObjectBrowserAttributeListener(attrib_objects.get(i)));
				attribListPanel.add(object_button);
				if (i < NBVISIBLEROW) {
					prefHeight += 1 + object_button.getPreferredSize().getHeight();
					//prefsize += object_button.getPreferredSize().getHeight();
				}
				prefWidth = object_button.getPreferredSize().getWidth();
				if (prefWidth > prefWidthMax) prefWidthMax = prefWidth;
			}
		}

		JScrollPane attribPane = new JScrollPane(attribListPanel);

		//attribPane.setPreferredSize(new Dimension(100, (int) prefsize));
		attribPane.setPreferredSize(new Dimension((int) (prefWidthMax+50), (int) (prefHeight+3)));

		ObjectBrowserListRuler lruler =
			new ObjectBrowserListRuler(
					NbElemAttribList,
					(int) attribListPanel.getPreferredSize().getHeight());
		lruler.setPreferredHeight(
				(int) (attribListPanel.getPreferredSize().getHeight()));
		attribPane.setRowHeaderView(lruler);

		//attribPane.setPreferredSize(new Dimension(0, (int) prefsize));
		//attribPane.setPreferredSize(new Dimension((int) (prefwidthmax*1.2), (int) prefsize));

		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.gridy = this.ncc;
		this.gbc.gridwidth = 1;
		this.gbc.gridheight = 1;
		this.gbc.insets = this.extpad;

		this.gbc.gridx = 0;
		this.gbc.weightx = 25;
		this.gbl.setConstraints(label, this.gbc);
		this.panel.add(label);

		this.gbc.gridx = 1;
		this.gbc.weightx = 75;
		this.gbl.setConstraints(attribPane, this.gbc);
		this.panel.add(attribPane);

		this.ncc++;
	}

	/**
	 * Ajoute à l'interface du navigateur, un composant graphique représentant une méthode.
	 * 
	 * @param obj l'objet portant la méthode à représenter au sein de l'interface du navigateur d'objet de GeOxygene.
	 * @param method la méthode qui doit être représenté par ce composant graphique.
	 */
	public void addMethod(Object obj, Method method) {
		JButton method_button = new JButton(method.getName());

		Object[] msgArguments = { method.getName()};
		MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
		formatter.setLocale(this.currentLocale);
		formatter.applyPattern(
				this.i18nLanguageFile.getString("MethodToolTipLabel")); //$NON-NLS-1$
		method_button.setToolTipText(formatter.format(msgArguments));

		method_button.addActionListener(
				new ObjectBrowserMethodListener(obj, method));

		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.gridy = this.ncc;
		this.gbc.gridwidth = 2;
		this.gbc.gridheight = 1;
		this.gbc.insets = this.extpad;

		this.gbc.gridx = 0;
		this.gbc.weightx = 100;
		this.gbl.setConstraints(method_button, this.gbc);
		this.panel.add(method_button);

		this.ncc++;
	}

	/**
	 * @return l'objet dont l'interface courante du navigateur d'objet de GeOxygene est la représentation.
	 */
	public Object getBrowsedObject() {
		return this.browsedObject;
	}

	/**
	 * @return le RessourceBundle lié à la Locale et au fichier d'internationalisation.
	 */
	public ResourceBundle getI18nLanguageFile() {
		return this.i18nLanguageFile;
	}

}
