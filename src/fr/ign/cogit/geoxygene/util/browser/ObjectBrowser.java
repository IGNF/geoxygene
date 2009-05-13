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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Vector;

/**
 *  Classe mettant en oeuvre le navigateur d'objet graphique de GeOxygene.
 *  <br>Elle instancie le "modèle" du navigateur d'objet de GeOxygene, conformement à
 *  l'architecture à modèle séparable de Sun Microsystems. Elle pilote la construction de
 *  l'interface graphique (vue) représentant l'objet Java (classe ObjectBrowserGUI).
 *  <br><br> Cette classe utilise intensivement le package reflection du J2SDK afin de rendre
 *  possible la représentation graphique et la navigation au sein de n'importe quel schéma de
 *  classes Java.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class ObjectBrowser {

	private static final int NULL = 0;
	private static final int PRIMITIVE = 1;
	private static final int ARRAY = 2;
	private static final int OBJECT = 3;
	private static final int STRING = 4;
	private static final int COLLECTION = 5;

	/** Flag indiquant si un bandeau portant le nom de la classe doit être
	 * affiché dans l'interface (vrai par défaut). */
	private static final boolean SHOW_CLASSNAME = true;
	/** Flag indiquant si les attributs publics de l'objet doivent être
	 * affichés dans l'interface (vrai par défaut). */
	private static final boolean SHOW_PUBLIC_ATTRIBUTES = true;
	/** Flag indiquant si les attributs protected de l'objet doivent être
	 * affichés dans l'interface (vrai par défaut). */
	private static final boolean SHOW_PROTECTED_ATTRIBUTES = true;
	/** Flag indiquant si les méthodes publiques de l'objet doivent être
	 * affichées dans l'interface (vrai par défaut). */
	private static final boolean SHOW_PUBLIC_METHODS = true;
	/** Flag indiquant si les méthodes protected de l'objet doivent être
	 * affichées dans l'interface (vrai par défaut). */
	private static final boolean SHOW_PROTECTED_METHODS = true;
	/** Fixe le comportement par défaut lors d'une demande de rafraichissement de la représentation
	 * graphique d'un objet. Par défaut, l'ancienne représentation de l'objet reste visible et une
	 * nouvelle représentation est affichée. */
	protected static final boolean HIDE_WHEN_REFRESH = false;

	/** Référence vers l'interface graphique du navigateur d'objet. */
	private static ObjectBrowserGUI browserInterface;

	/**
	 * Teste si l'objet passé en paramètre est une instance de Collection (au sens Java du terme).
	 * @param obj l'objet à tester.
	 * @return vrai si l'objet passé en paramètre est une instance de Collection, faux sinon.
	 */
	private static boolean isCollectionClass(Object obj) {
		return (obj.getClass().isInstance(Collection.class));
		/*
		try {
			((Collection) obj).toArray();
			return true;
		} catch (ClassCastException ccex) {
			return false;
		}
		 */
	}

	/**
	 * Méthode statique de conversion d'une Collection en tableau d'Object.
	 * 
	 * @param obj l'objet instance de collection à convertir.
	 * @return le tableau d'object correspondant à la collection passée en paramètre. Si la classe n'est pas de type
	 *  Collection renvoie null.
	 */
	private static Object[] convertCollectionToArray(Object obj) {
		try {
			return ((Collection<?>) obj).toArray();
		} catch (ClassCastException ccex) {
			return null;
		}
	}

	/**
	 * Teste si la classe passée en paramètre est de type Array (tableau).
	 * 
	 * @param classObject la classe à tester.
	 * @return vrai si classObject est de type Array, faux sinon.
	 */
	private static boolean isArrayClass(Class<?> classObject) {
		return classObject.isArray();
	}

	/**
	 * Si classObject est de type Array, cette méthode permet de déterminer le type de contenu de ce "tableau".
	 * 
	 * @param classObject la classe de type tableau dont on cherche à déterminer le type de contenu.
	 * @return ObjectBrowser.PRIMITIVE dans le cas où le contenu du tableau est de type primitif,
	 * ObjectBrowser.OBJECT (i.e. de type objet) dans tous les autres cas.
	 */
	private static int getArrayClassComponentType(Class<?> classObject) {
		// Faudrait peut-être intercepter le fait que ce soit pas un Array !!! ;-))
		Class<?> componentType = classObject.getComponentType();
		if (componentType.isPrimitive()) {
			return PRIMITIVE;
		} else {
			return OBJECT;
		}
	}

	/**
	 * Méthode statique permettant de tester si la classe passée en argument est de type tableau
	 * d'un type primitif (int, short, long, boolean, char, etc.).
	 * 
	 * @param classObject la classe à tester.
	 * @return vrai si la classe passée en argument est de type tableau d'un type primitif, faux sinon.
	 */
	private static boolean isArrayClassComponentTypePrimitive(Class<?> classObject) {
		// Faudrait peut-être intercepter le fait que ce soit pas un Array !!! ;-))
		if (getArrayClassComponentType(classObject) == PRIMITIVE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Méthode statique renvoyant la dimension de l'objet de type tableau passé en argument.
	 * @param obj l'objet de type tableau pour lequel on cherche la dimension.
	 * @return la dimension de l'objet de type tableau passé en argument.
	 */
	private static int getArrayLevel(Object obj) {
		// Faudrait peut-être intercepter le fait que ce soit pas un Array !!! ;-))
		String arrayValueString;

		arrayValueString = obj.toString();
		return (arrayValueString.lastIndexOf("[") + 1);
	}

	/**
	 * Méthode statique retournant la dimension de l'attribut field sur l'objet obj.
	 * 
	 * @param field l'attribut dont on cherche la dimension (tableau).
	 * @param obj l'objet qui porte l'attribut field.
	 * @return la dimension de l'attribut field porté par obj ou 0 s'il ne s'agit pas d'un attribut de type tableau (Array).
	 */
	private static int getArrayLevel(Field field, Object obj) {
		// Faudrait peut-être intercepter le fait que ce soit pas un Array !!! ;-))
		String arrayValueString;

		try {
			arrayValueString = field.get(obj).toString();
			return (arrayValueString.lastIndexOf("[") + 1);
		} catch (IllegalAccessException e) {
			return 0;
		}

	}

	/**
	 * Méthode statique retournant le type du contenu d'un champ de type tableau (Array), passé
	 * en argument de la méthode.
	 * 
	 * @param field le champ de type tableau pour lequel on cherche le type de contenu.
	 * @return ObjectBrowser.STRING s'il s'agit d'un tableau de chaîne de caractères,
	 * ObjectBrowser.PRIMITIVE pour un tableau d'éléments de type primitif, et
	 * ObjectBrowser.OBJECT sinon.
	 */
	private static int getArrayComponentType(Field field) {
		// Faudrait peut-être intercepter le fait que ce soit pas un Array !!! ;-))
		Class<?> fieldType = field.getType();
		String fieldTypeName = fieldType.getName();
		String underlyingFieldTypeName;
		char underlyingType;
		int typeIndex;

		typeIndex = fieldTypeName.lastIndexOf("[") + 1;
		underlyingType = fieldTypeName.charAt(typeIndex);

		switch (underlyingType) {
		case 'L' :
			underlyingFieldTypeName =
				fieldTypeName.substring(
						typeIndex + 1,
						fieldTypeName.length() - 1);
			if (underlyingFieldTypeName.equals("java.lang.String")) {
				return STRING;
			} else {
				return OBJECT;
			}
		default :
			return PRIMITIVE;
		}

	}

	/**
	 * Méthode statique renvoyant le nom du type ou de la classe de l'objet tableau passé en paramètre.
	 * 
	 * @param obj l'objet dont on cherche le type.
	 * @return le nom de la classe ou du type de contenu du tableau.
	 */
	private static String getArrayComponentTypeName(Object obj) {
		// Faudrait peut-être intercepter le fait que ce soit pas un Array !!! ;-))
		String typeName = obj.toString();
		String underlyingTypeName;
		char underlyingType;
		int typeIndex;

		typeIndex = typeName.lastIndexOf("[") + 1;
		underlyingType = typeName.charAt(typeIndex);

		switch (underlyingType) {
		case 'L' :
			underlyingTypeName =
				typeName.substring(typeIndex + 1, typeName.length() - 1);
			return underlyingTypeName;
		case 'B' :
			return "byte";
		case 'C' :
			return "char";
		case 'D' :
			return "double";
		case 'F' :
			return "float";
		case 'I' :
			return "int";
		case 'J' :
			return "long";
		case 'S' :
			return "short";
		case 'Z' :
			return "boolean";
		default :
			return "void";
		}
	}

	/**
	 * Si l'attribut field passé en paramètre est de type tableau, cette méthode statique
	 * renvoie le nom du type ou de la classe qualifiant le contenu du tableau.
	 * 
	 * @param field le champ de type tableau dont on cherche à déterminer le type de contenu.
	 * @return le nom de la classe ou du type de contenu du tableau.
	 */
	private static String getArrayComponentTypeName(Field field) {
		// Faudrait peut-être intercepter le fait que ce soit pas un Array !!! ;-))
		Class<?> fieldType = field.getType();
		String fieldTypeName = fieldType.getName();
		String underlyingFieldTypeName;
		char underlyingType;
		int typeIndex;

		typeIndex = fieldTypeName.lastIndexOf("[") + 1;
		underlyingType = fieldTypeName.charAt(typeIndex);

		switch (underlyingType) {
		case 'L' :
			underlyingFieldTypeName =
				fieldTypeName.substring(
						typeIndex + 1,
						fieldTypeName.length() - 1);
			return underlyingFieldTypeName;
		case 'B' :
			return "byte";
		case 'C' :
			return "char";
		case 'D' :
			return "double";
		case 'F' :
			return "float";
		case 'I' :
			return "int";
		case 'J' :
			return "long";
		case 'S' :
			return "short";
		case 'Z' :
			return "boolean";
		default :
			return "void";
		}
	}

	/**
	 * Pour l'attribut field de type Collection sur l'objet obj, retourne le type
	 * du contenu de la Collection.
	 * 
	 * @param field l'attribut de type Collection.
	 * @param obj l'objet portant l'attribut field.
	 * @return ObjectBrowser.NULL s'il ne s'agit pas d'un champ de type Collection,
	 * ObjectBrowser.PRIMITIVE (resp. ObjectBrowser.OBJECT) s'il s'agit d'une Collection
	 * d'éléments de type primitif (resp. de type objet).
	 */
	private static int getCollectionComponentType(Field field, Object obj) {
		// Faudrait peut-être intercepter le fait que ce soit pas une Collection !!! ;-))

		String fieldValue;

		try {
			fieldValue = field.get(obj).toString();
			if (fieldValue.indexOf("@") > -1) {
				return OBJECT;
			} else if (fieldValue.lastIndexOf("[") > 0) {
				return OBJECT;
			} else {
				return PRIMITIVE;
			}

		} catch (IllegalAccessException e) {
			return NULL;
		}
	}

	/**
	 * Retourne le type de l'attribut field porté par l'objet obj.
	 * 
	 * @param field l'attribut dont on cherche le type.
	 * @param obj l'objet portant l'attribut field.
	 * @return suivant le type, une valeur entière parmi ObjectBrowser.PRIMITIVE,
	 * ObjectBrowser.ARRAY, ObjectBrowser.OBJECT, ObjectBrowser.STRING,
	 * ObjectBrowser.OBJECT ou ObjectBrowser.NULL.
	 */
	private static int getFieldType(Field field, Object obj) {

		Class<?> fieldType = field.getType();
		String fieldTypeName = fieldType.getName();
		Object fieldValue;
		String fieldValueString;
		String fieldClass;
		int index;

		if ((index = fieldTypeName.lastIndexOf(".")) > -1) {
			fieldClass = fieldTypeName.substring(index + 1);
		} else {
			fieldClass = "";
		}

		try {
			fieldValue = field.get(obj);
			fieldValueString = fieldValue.toString();

			if (fieldType.isPrimitive()) {
				return PRIMITIVE;
			} else if (fieldType.isArray()) {
				return ARRAY;
			} else {
				if (fieldValueString.indexOf(fieldClass + "@") > -1) {
					return OBJECT;
				} else {
					if (fieldTypeName.equals("java.lang.String")) {
						return STRING;
					} else {
						if (fieldValue.getClass().isInstance(Collection.class))
							return COLLECTION;
						else
							return OBJECT;
						/*
						try {
							Collection testCollection = (Collection) fieldValue;
							return COLLECTION;
						} catch (ClassCastException cce) {
							return OBJECT;
						}
						 */
					}
				}
			}
		} catch (IllegalAccessException e) {
			return NULL;
		}
	}

	/**
	 * Teste si l'attribut field porté par l'objet obj est de type primitif.
	 * 
	 * @param field l'attribut à tester.
	 * @param obj l'objet portant l'attribut field.
	 * @return vrai si l'attribut est de type primitif, faux sinon.
	 */
	private static boolean isFieldTypePrimitive(Field field, Object obj) {
		if (getFieldType(field, obj) == PRIMITIVE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Teste si l'attribut field porté par l'objet obj est de type tableau (Array).
	 * 
	 * @param field l'attribut à tester.
	 * @param obj l'objet portant l'attribut field.
	 * @return vrai si l'attribut est de type tableau (Array), faux sinon.
	 */
	private static boolean isFieldTypeArray(Field field, Object obj) {
		if (getFieldType(field, obj) == ARRAY) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Teste si l'attribut field porté par l'objet obj est de type objet.
	 * 
	 * @param field l'attribut à tester.
	 * @param obj l'objet portant l'attribut field.
	 * @return vrai si l'attribut est de type objet, faux sinon.
	 */
	private static boolean isFieldTypeObject(Field field, Object obj) {
		if (getFieldType(field, obj) == OBJECT) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Teste si l'attribut field porté par l'objet obj est de type chaîne de caractères (String).
	 * 
	 * @param field l'attribut à tester.
	 * @param obj l'objet portant l'attribut field.
	 * @return vrai si l'attribut est de type chaîne de caractères (String), faux sinon.
	 */
	private static boolean isFieldTypeString(Field field, Object obj) {
		if (getFieldType(field, obj) == STRING) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Teste si l'attribut field porté par l'objet obj est de type Collection d'objets.
	 * 
	 * @param field l'attribut à tester.
	 * @param obj l'objet portant l'attribut field.
	 * @return vrai si l'attribut est de type Collection d'objets, faux sinon.
	 */
	private static boolean isFieldTypeCollection(Field field, Object obj) {
		if (getFieldType(field, obj) == COLLECTION) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * En fonction de la valeur des constantes SHOW_PUBLIC_ATTRIBUTES et SHOW_PROTECTED_ATTRIBUTES, renvoie
	 * l'ensemble des attributs publics et/ou protected accessibles de la classe classObj passée en argument.
	 * 
	 * @param classObj la classe sur laquelle on cherche les attributs publics et/ou protected accessibles.
	 * @return un tableau (Field[]) contenant l'ensemble des attributs accessibles de la classe.
	 */
	@SuppressWarnings("unused")
	private static Field[] getAccessibleFields(Class<?> classObj) {
		return getAccessibleFields(
				classObj,
				SHOW_PUBLIC_ATTRIBUTES,
				SHOW_PROTECTED_ATTRIBUTES);
	}

	/**
	 * En fonction de la valeur des arguments retrievePublicFields et retrieveProtectedFields, renvoie
	 * l'ensemble des attributs publics et/ou protected accessibles de la classe classObj passée en argument.
	 * 
	 * @param classObj la classe sur laquelle on cherche les attributs publics et/ou protected accessibles.
	 * @param retrievePublicFields si vrai, l'ensemble des champs publics de la classe sera retourné par la méthode.
	 * @param retrieveProtectedFields si vrai, l'ensemble des champs protected de la classe sera retourné par la méthode.
	 * @return un tableau (Field[]) contenant l'ensemble des attributs accessibles de la classe.
	 */
	public static Field[] getAccessibleFields(
			Class<?> classObj,
			boolean retrievePublicFields,
			boolean retrieveProtectedFields) {

		//Field[] allAccessibleFields = classObj.getDeclaredFields();
		Field[] publicFields=classObj.getFields();
		int nbPublicFields=publicFields.length;
		Field[] localFields=classObj.getDeclaredFields();
		int nbLocalFields=localFields.length;

		/* TreeSet allFields=new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
			   return ((Field)o1).getName().compareTo(((Field)o2).getName());
			}
		}); */

		Vector<Field> allFields=new Vector<Field>();
		boolean isAlreadyInVector;

		for(int i=0;i<nbLocalFields;i++) {
			allFields.add(localFields[i]);
		}

		for(int i=0;i<nbPublicFields;i++) {
			isAlreadyInVector=false;
			for(int j=0;j<allFields.size();j++) {
				if (publicFields[i].equals(allFields.get(j))) {
					isAlreadyInVector=true;
					break;
				}
			}
			if (!isAlreadyInVector){
				allFields.add(publicFields[i]);
			}
		}


		///////////////////////////////////////////////////////////
		//// debut ajout Arnaud pour recuperer les champs "primitive" protected herites
		//// en fait on fait ceci pour recuper l'id !
		Class<?> superClass = classObj.getSuperclass();
		while (superClass != Object.class) {
			Field[] superAccessibleFields = superClass.getDeclaredFields();
			for (int i=0; i<superAccessibleFields.length; i++)
				if (superAccessibleFields[i].getType().isPrimitive())
					allFields.add(superAccessibleFields[i]);
			superClass = superClass.getSuperclass();
		}
		//// fin ajout Arnaud
		///////////////////////////////////////////////////////////


		Object[] allObjectFields=allFields.toArray();

		Field[] allAccessibleFields = new Field[allObjectFields.length];
		for(int i=0;i<allObjectFields.length;i++) {
			allAccessibleFields[i]=(Field)allObjectFields[i];
		}


		int nbAllAccessibleFields = allAccessibleFields.length;
		Vector<Field> accessibleFields = new Vector<Field>();
		Field[] resultAccessibleFields;
		int nbAccessibleFields = 0;
		int fieldModifier = 0;

		for (int i = 0; i < nbAllAccessibleFields; i++) {
			fieldModifier = allAccessibleFields[i].getModifiers();

			if (Modifier.isPublic(fieldModifier)
					&& (!(Modifier.isStatic(fieldModifier)))
					&& retrievePublicFields) {
				accessibleFields.add(allAccessibleFields[i]);
			} else if (
					Modifier.isProtected(fieldModifier)
					&& (!(Modifier.isStatic(fieldModifier)))
					&& retrieveProtectedFields) {
				accessibleFields.add(allAccessibleFields[i]);
				try {
					if (!(allAccessibleFields[i].isAccessible())) {
						allAccessibleFields[i].setAccessible(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		nbAccessibleFields = accessibleFields.size();
		resultAccessibleFields = new Field[nbAccessibleFields];
		for (int i = 0; i < nbAccessibleFields; i++) {
			resultAccessibleFields[i] = (accessibleFields.get(i));
		}

		return resultAccessibleFields;
	}

	/**
	 * En fonction de la valeur des constantes SHOW_PUBLIC_METHODS et SHOW_PROTECTED_METHODS, renvoie
	 * l'ensemble des méthodes publiques et/ou protected locales et héritées, ne prenant aucun argument et accessibles de la classe
	 * classObj passée en argument.
	 * 
	 * @param classObj la classe sur laquelle on cherche les méthodes locales et héritées, publiques
	 * et/ou protected.
	 * @return un tableau (Method[]) contenant l'ensemble des méthodes publiques et/ou protected, locales et héritées, ne prenant aucun argument et accessibles de la classe.
	 */
	@SuppressWarnings("unused")
	private static Method[] getAccessibleMethods(Class<?> classObj) {
		return getAccessibleMethods(
				classObj,
				SHOW_PUBLIC_METHODS,
				SHOW_PROTECTED_METHODS);
	}

	/**
	 * En fonction de la valeur des arguments retrievePublicMethods et retrieveProtectedMethods, renvoie
	 * l'ensemble des méthodes publiques et/ou protected, locales et héritées, ne prenant aucun argument et accessibles de la classe
	 * classObj passée en argument.
	 * 
	 * @param classObj la classe sur laquelle on cherche les méthodes publiques et/ou protected, locales
	 * et héritées.
	 * @param retrievePublicMethods si vrai, l'ensemble des méthodes publiques, locales et héritées, portées par la classe classObj sera retourné par la méthode.
	 * @param retrieveProtectedMethods si vrai, l'ensemble des méthodes protected, locales et héritées, portées par la classe classObj sera retourné par la méthode.
	 * @return un tableau (Method[]) contenant l'ensemble des méthodes publiques et/ou protected, locales et héritées, ne prenant aucun argument et accessibles de la classe.
	 */
	private static Method[] getAccessibleMethods(
			Class<?> classObj,
			boolean retrievePublicMethods,
			boolean retrieveProtectedMethods) {
		Method[] allAccessibleMethods;
		Vector<Method> allAccessibleMethodsVector = new Vector<Method>();
		Method[] publicMethods = classObj.getMethods();
		Method[] localMethods = classObj.getDeclaredMethods();
		Vector<String> localMethodNames = new Vector<String>();
		int nbLocalMethods = localMethods.length;
		int nbPublicMethods = publicMethods.length;
		boolean isInVector;

		int nbAllAccessibleMethods;
		Vector<Method> accessibleMethods;
		Method[] resultAccessibleMethods;
		int nbAccessibleMethods = 0;
		int methodModifier = 0;
		int nbParameters;
		Class<?> methodReturnType;

		for (int j = 0; j < nbLocalMethods; j++) {
			localMethodNames.add(localMethods[j].getName());
			allAccessibleMethodsVector.add(localMethods[j]);
		}

		for (int i = 0; i < nbPublicMethods; i++) {
			isInVector = false;
			for (int j = 0; j < nbLocalMethods; j++) {
				if (publicMethods[i]
				                  .getName()
				                  .equals(localMethodNames.get(j))) {
					isInVector = true;
					break;
				}
			}
			if (!(isInVector)) {
				allAccessibleMethodsVector.add(publicMethods[i]);
			}
		}

		nbAllAccessibleMethods = allAccessibleMethodsVector.size();
		allAccessibleMethods = new Method[nbAllAccessibleMethods];
		for (int i = 0; i < nbAllAccessibleMethods; i++) {
			allAccessibleMethods[i] =
				(allAccessibleMethodsVector.get(i));
		}
		accessibleMethods = new Vector<Method>();

		for (int i = 0; i < nbAllAccessibleMethods; i++) {
			methodModifier = allAccessibleMethods[i].getModifiers();
			nbParameters = allAccessibleMethods[i].getParameterTypes().length;
			methodReturnType = allAccessibleMethods[i].getReturnType();

			if (Modifier.isPublic(methodModifier)
					&& (!(Modifier.isStatic(methodModifier)))
					&& retrievePublicMethods) {
				if ((nbParameters == 0)
						&& (!(methodReturnType.getName().equals("void")))) {
					accessibleMethods.add(allAccessibleMethods[i]);
				}
			} else if (
					Modifier.isProtected(methodModifier)
					&& (!(Modifier.isStatic(methodModifier)))
					&& retrieveProtectedMethods) {
				if ((nbParameters == 0)
						&& (!(methodReturnType.getName().equals("void")))) {
					accessibleMethods.add(allAccessibleMethods[i]);
					try {
						if (!(allAccessibleMethods[i].isAccessible())) {
							allAccessibleMethods[i].setAccessible(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		nbAccessibleMethods = accessibleMethods.size();
		resultAccessibleMethods = new Method[nbAccessibleMethods];
		for (int i = 0; i < nbAccessibleMethods; i++) {
			resultAccessibleMethods[i] = (accessibleMethods.get(i));
		}

		return resultAccessibleMethods;
	}

	/**
	 * Lance l'affichage par défaut (défini par les constantes SHOW_CLASSNAME, SHOW_PUBLIC_ATTRIBUTES,
	 * SHOW_PROTECTED_ATTRIBUTES, SHOW_PUBLIC_METHODS, SHOW_PROTECTED_METHODS) de la représentation
	 * graphique de l'objet obj passé en argument.
	 * 
	 * @param obj l'objet dont on souhaite obtenir une représentaion par défaut dans le navigateur d'objet de GeOxygene.
	 */
	public static void browse(Object obj) {
		browse(
				obj,
				SHOW_CLASSNAME,
				SHOW_PUBLIC_ATTRIBUTES,
				SHOW_PROTECTED_ATTRIBUTES,
				SHOW_PUBLIC_METHODS,
				SHOW_PROTECTED_METHODS);
	}

	/**
	 * Lance l'affichage de la représentation graphique de l'objet obj passé en argument.
	 * 
	 * @param obj obj l'objet dont on souhaite obtenir une représentaion dans le navigateur d'objet de GeOxygene.
	 * @param showClassName si vrai, affiche dans l'interface un bandeau avec le nom du type de l'objet.
	 * @param showPublicAttributes si vrai, affiche dans l'interface les attributs publics portés par l'objet.
	 * @param showProtectedAttributes si vrai, affiche dans l'interface les attributs protected portés par l'objet.
	 * @param showPublicMethods si vrai, affiche dans l'interface les méthodes publiques, locales et héritées, portées par l'objet.
	 * @param showProtectedMethods si vrai, affiche dans l'interface les méthodes protected, locales et héritées, portées par l'objet.
	 */
	@SuppressWarnings("unchecked")
	public static void browse(
			Object obj,
			boolean showClassName,
			boolean showPublicAttributes,
			boolean showProtectedAttributes,
			boolean showPublicMethods,
			boolean showProtectedMethods) {

		try {
			Class objectClass;
			String objectClassName;
			Field[] objectFields;
			int nbFields = 0;
			Method[] objectMethods;
			int nbMethods = 0;

			Field currentField;
			String currentFieldName;
			Class currentFieldType;
			String currentFieldTypeName;
			Object currentFieldValue;
			int arrayFieldLength;
			int arrayLevel;

			Method currentMethod;
			@SuppressWarnings("unused")
			String currentMethodName;
			Class[] currentMethodParameters;
			@SuppressWarnings("unused")
			int nbCurrentMethodParameters;

			try {
				objectClass = obj.getClass();
				objectClassName = objectClass.getName();
				objectFields =
					getAccessibleFields(
							objectClass,
							showPublicAttributes,
							showProtectedAttributes);
				objectMethods =
					getAccessibleMethods(
							objectClass,
							showPublicMethods,
							showProtectedMethods);
				nbFields = objectFields.length;
				nbMethods = objectMethods.length;

				browserInterface =
					new ObjectBrowserGUI(
							obj,
							showClassName,
							showPublicAttributes,
							showProtectedAttributes,
							showPublicMethods,
							showProtectedMethods,
							objectClassName);

				// Special case of Array classes.
				if (isArrayClass(objectClass)) {
					int nbArrayElements = Array.getLength(obj);
					Vector<Object> arrayElements = new Vector<Object>();
					for (int i = 0; i < nbArrayElements; i++) {
						arrayElements.add(Array.get(obj, i));
					}
					if (showClassName) {
						arrayLevel = getArrayLevel(obj);
						String arrayObjectClassName = "";
						for (int i = 0; i < arrayLevel; i++) {
							arrayObjectClassName += "[";
						}
						arrayObjectClassName += getArrayComponentTypeName(obj);
						for (int i = 0; i < arrayLevel; i++) {
							arrayObjectClassName += "]";
						}
						browserInterface.addClassNameLabel(
								arrayObjectClassName);
						browserInterface.changeTitle(arrayObjectClassName);

					}

					if (isArrayClassComponentTypePrimitive(objectClass)) {
						browserInterface.addAttributeList(arrayElements);
					} else {
						browserInterface.addObjectAttributeList(arrayElements);
					}
				} else if (showClassName) {
					browserInterface.addClassNameLabel(objectClassName);
				}

				// Special Case of Collection classes.
				if (isCollectionClass(obj)) {
					Object[] arrayFromCollectionObject =
						convertCollectionToArray(obj);

					int nbCollectionElements = arrayFromCollectionObject.length;
					Vector<Object> collectionElements = new Vector<Object>();
					for (int i = 0; i < nbCollectionElements; i++) {
						collectionElements.add(arrayFromCollectionObject[i]);
					}

					if (isArrayClassComponentTypePrimitive(arrayFromCollectionObject
							.getClass())) {
						browserInterface.addAttributeList(collectionElements);
					} else {
						browserInterface.addObjectAttributeList(
								collectionElements);
					}

				}

				for (int i = 0; i < nbFields; i++) {
					currentField = objectFields[i];
					currentFieldName = currentField.getName();
					currentFieldType = currentField.getType();
					currentFieldTypeName = currentFieldType.getName();

					try {
						currentFieldValue = currentField.get(obj);

						if (isFieldTypeObject(currentField, obj)) {
							browserInterface.addObjectAttribute(
									currentFieldName,
									currentFieldTypeName,
									currentFieldValue);
						}

						if (isFieldTypePrimitive(currentField, obj)
								|| isFieldTypeString(currentField, obj)) {
							browserInterface.addAttribute(
									currentFieldName,
									currentFieldValue.toString());
						}

						if (isFieldTypeCollection(currentField, obj)) {

							if (getCollectionComponentType(currentField, obj)
									!= OBJECT) {
								browserInterface.addAttributeList(
										currentFieldName,
										new Vector(
												(Collection) (currentFieldValue)));
							} else {
								browserInterface.addObjectAttributeList(
										currentFieldName,
										new Vector(
												(Collection) (currentFieldValue)));
							}
						}

						if (isFieldTypeArray(currentField, obj)) {

							arrayFieldLength =
								Array.getLength(currentFieldValue);
							arrayLevel = getArrayLevel(currentField, obj);
							Vector attribvalarray = new Vector();

							for (int j = 0; j < arrayFieldLength; j++) {
								attribvalarray.add(
										Array.get(currentFieldValue, j));
							}

							if (((getArrayComponentType(currentField)
									== STRING)
									|| (getArrayComponentType(currentField)
											== PRIMITIVE))
											&& (arrayLevel == 1)) {
								browserInterface.addAttributeList(
										currentFieldName,
										attribvalarray);
							} else {
								if (arrayLevel == 1) {
									browserInterface.addObjectAttributeList(
											currentFieldName,
											attribvalarray);
								} else {
									browserInterface.addObjectAttributeList(
											currentFieldName,
											attribvalarray,
											arrayLevel - 1,
											getArrayComponentTypeName(currentField));
								}
							}
						}

					} catch (NullPointerException npex) {

						//browserInterface.add_attribute(currentFieldName,"NULL");
						browserInterface.addObjectAttribute(
								currentFieldName,
								"null",
								null);
					}
				}

				for (int i = 0; i < nbMethods; i++) {
					currentMethod = objectMethods[i];
					currentMethodName = currentMethod.getName();

					currentMethodParameters = currentMethod.getParameterTypes();
					nbCurrentMethodParameters = currentMethodParameters.length;

					browserInterface.addMethod(obj, currentMethod);
				}

				browserInterface.pack();
				//browserInterface.setSize(browserInterface.getPreferredSize());

				//browserInterface.show();
				browserInterface.setVisible(true);

				browserInterface.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						browserInterface.dispose();
					}
				});
			} catch (NullPointerException npex) {
				@SuppressWarnings("unused")
				ObjectBrowserNullPointerFrame nullFrame =
					new ObjectBrowserNullPointerFrame();
			}
		} catch (IllegalAccessException e) {
			@SuppressWarnings("unused")
			ObjectBrowserIllegalAccessFrame illegalAccessFrame =
				new ObjectBrowserIllegalAccessFrame();
			//e.printStackTrace();
		}

	}

	/**
	 * Déclenche de façon programmatique le rafraichissement de l'interface représentant l'objet. Le
	 * comportement du rafraîchissement (l'ancienne représentation de l'objet disparaît-elle ?) est
	 * fixé par la valeur de la constante ObjectBrowser.HIDE_WHEN_REFRESH.
	 * <p>De plus, Le nouvel affichage de l'objet est régi par les valeurs des constantes SHOW_CLASSNAME,
	 * SHOW_PUBLIC_ATTRIBUTES,SHOW_PROTECTED_ATTRIBUTES, SHOW_PUBLIC_METHODS et SHOW_PROTECTED_METHODS.</p>
	 * 
	 * @param obj objet dont on veut rafrîchir la représentation graphique.
	 */
	public static void refresh(Object obj) {
		refresh(obj, HIDE_WHEN_REFRESH);
	}

	/**
	 * Déclenche de façon programmatique le rafraichissement de l'interface représentant l'objet. Le nouvel
	 * affichage de l'objet est régi par les valeurs des constantes SHOW_CLASSNAME, SHOW_PUBLIC_ATTRIBUTES,
	 * SHOW_PROTECTED_ATTRIBUTES, SHOW_PUBLIC_METHODS et SHOW_PROTECTED_METHODS.
	 * 
	 * @param obj objet dont on veut rafrîchir la représentation graphique.
	 * @param dispose si vrai, l'ancienne représentation de l'objet disparaît.
	 */
	public static void refresh(Object obj, boolean dispose) {
		refresh(obj,dispose,SHOW_CLASSNAME,SHOW_PUBLIC_ATTRIBUTES,SHOW_PROTECTED_ATTRIBUTES,SHOW_PUBLIC_METHODS,SHOW_PROTECTED_METHODS);
	}

	/**
	 * Déclenche de façon programmatique le rafraichissement de l'interface représentant l'objet.
	 * 
	 * @param obj objet dont on veut rafrîchir la représentation graphique.
	 * @param dispose si vrai, l'ancienne représentation de l'objet disparaît.
	 * @param showClassName si vrai, affiche dans l'interface un bandeau avec le nom du type de l'objet.
	 * @param showPublicAttributes si vrai, affiche dans l'interface les attributs publics portés par l'objet.
	 * @param showProtectedAttributes si vrai, affiche dans l'interface les attributs protected portés par l'objet.
	 * @param showPublicMethods si vrai, affiche dans l'interface les méthodes publiques, locales et héritées, portées par l'objet.
	 * @param showProtectedMethods si vrai, affiche dans l'interface les méthodes protected, locales et héritées, portées par l'objet.
	 */
	public static void refresh(
			Object obj,
			boolean dispose,
			boolean showClassName,
			boolean showPublicAttributes,
			boolean showProtectedAttributes,
			boolean showPublicMethods,
			boolean showProtectedMethods) {
		browserInterface.setVisible(!dispose);
		browse(
				obj,
				showClassName,
				showPublicAttributes,
				showProtectedAttributes,
				showPublicMethods,
				showProtectedMethods);
	}

}
