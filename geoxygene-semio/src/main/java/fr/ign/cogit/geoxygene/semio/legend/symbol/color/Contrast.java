package fr.ign.cogit.geoxygene.semio.legend.symbol.color;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.semio.legend.mapContent.SymbolisedFeatureCollection;
import fr.ign.cogit.geoxygene.semio.legend.metadata.SemanticRelation;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Elodie Buard - IGN / Laboratoire COGIT
 * @author Sébastien Mustière - IGN / Laboratoire COGIT 
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 * 
 * Color Contrasts (hue contrast and lightness contrast) between two colors.
 * These contrasts comes from user's perception tests of the colors 
 * of the COGIT {@link ColorReferenceSystem}.
 * <p>
 * <strong>French:</strong><br />
 * Contraste coloré entre deux couleurs.
 * Le contrate a deux composantes: le contraste de teinte et le contraste de clarté.
 * Ces valeurs de contrastes sont issus de tests utilisateurs concernant la perception 
 * des couleurs du système de référence des couleurs du COGIT.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
	name = "Contrast",
	propOrder = {
		"idC1",
		"idC2",
		"contrasteTeinte",
		"contrasteClarte",
		"qualiteContrasteTeinte",
		"qualiteContrasteClarte"
})
public class Contrast {
	
	@XmlElement(name="idC1")
	private int idC1;
	
	public int getIdC1() {
		return idC1;
	}
	
	public void setIdC1(int idC1) {
		this.idC1 = idC1;
	}
	
		@XmlElement(name="idC2")
	private int idC2;
	
	public int getIdC2() {
		return idC2;
	}

	public void setIdC2(int idC2) {
		this.idC2 = idC2;
	}


	/**
	 * Hue contrast.
	 * (ranging from 0 = not contrasted, to 1=well contrasted)
	 * <p>
	 * <strong>French:</strong><br />
	 * Valeur du contraste de teinte 
	 * (valeur entre 0 et 1, 0: pas contrasté, 1: très contrasté)
	 */
	@XmlElement(name="contrasteTeinte")
	private double contrasteTeinte = -1;
	
	/**
	 * Gets the hue contrast.
	 * <p>
     * <strong>French:</strong><br />
	 * Renvoie la valeur du contraste de teinte.
	 * (valeur entre 0 et 1, 0: pas contrasté, 1: très contrasté)
	 * 
	 * @return The hue contrast
	 */
	public double getContrasteTeinte() {
		return contrasteTeinte;
	}
	
	/**
	 * Sets the hue contrast.
	 * <p>
	 * <strong>French:</strong><br />
	 * Spécifie la valeur du contraste de teinte.
	 * (valeur entre 0 et 1, 0: pas contrasté, 1: très contrasté)
	 * 
	 * @param contrasteTeinte The hue contrast
	 */
	public void setContrasteTeinte(double contrasteTeinte) {
		this.contrasteTeinte = contrasteTeinte;
	}
	
	/**
	 * Lightness contrast.
	 * (ranging from 0 = not contrasted, to 1=well contrasted)
	 * <p>
     * <strong>French:</strong><br />
	 * Valeur du contraste de clarté.
	 * (valeur entre 0 et 1, 0: pas contrasté, 1: très contrasté)
	 */
	@XmlElement(name="contrastesClarte")
	private double contrasteClarte = -1;
	
	

	/**
	 * Gets the lightness contrast.
	 * (ranging from 0 = not contrasted, to 1=well contrasted)
	 * <p>
     * <strong>French:</strong><br />
	 * Renvoie la valeur du contraste de clarté.
	 * (valeur entre 0 et 1, 0: pas contrasté, 1: très contrasté)
	 * 
	 * @return The lightness contrast.
	 */
	public double getContrasteClarte() {
		return contrasteClarte;
	}
	
	/**
	 * Sets the lightness contrast.
	 * (ranging from 0 = not contrasted, to 1=well contrasted)
	 * <p>
     * <strong>French:</strong><br />
	 * Spécifie la valeur du contraste de clarté.
	 * (valeur entre 0 et 1, 0: pas contrasté, 1: très contrasté)
	 * 
	 * @param contrasteClarte The Lightness Contrast.
	 */
	public void setContrasteClarte(double contrasteClarte) {
		this.contrasteClarte = contrasteClarte;
	}
	
	
	/**
	 * Quality of the hue contrast regarding the {@link SemanticRelation}ship 
	 * that colors are supposed to convey.
	 * <p>
     * <strong>French:</strong><br />
	 * Valeur de la qualité de contraste de teinte. Cette qualité dépend de la 
	 * relation sémantique reliant les lignes de légende 
     * symbolisées par les couleurs de ce contrastes
     * 
     * <p>
     * Les notes de qualité de contrastes sont entre 0 et 10.
     * <strong>Warning:</strong> 0 = bonne note, 10 = mauvaise note.
     * <p>
     * Note = 0 si la valeur est dans l'intervalle acceptable
     * Note = écart à borne inf acceptable si la valeur est inférieure à cette borne
     * Note = écart à borne sup acceptable si la valeur est supérieure à cette borne
	 */
	@XmlElement(name="qualiteContrasteTeinte")
	private double qualiteContrasteTeinte = -1;
	
	/**
	 * Gets the quality of the hue contrast.
	 * 
	 * @return The quality of the hue contrast
	 */
	public double getQualiteContrasteTeinte() {
		return qualiteContrasteTeinte;
	}
	
	/**
	 * Sets the quality of the hue contrast.
	 * 
	 * @param qualiteContrasteTeinte The quality of the hue contrast.
	 */
	public void setQualiteContrasteTeinte(double qualiteContrasteTeinte) {
		this.qualiteContrasteTeinte = qualiteContrasteTeinte;
	}

	
	/**
	 * Quality of the lightness contrast regarding the {@link SemanticRelation}ship 
     * that colors are supposed to convey and the area covered by the 
     * corresponding {@link SymbolisedFeatureCollection}.
     * <p>
     * <strong>French:</strong><br />
	 * Valeur de la qualité de contraste de clarte. 
	 * Cette qualité dépend de la relation sémantique reliant les lignes de légende 
	 * symbolisées par les couleurs de ce contrastes ainsi que 
	 * de la surface couverte par les familles cartographiques correspondantes.
	 * <p>
	 * Les notes de qualité de contrastes sont entre 0 et 10.
     * <strong>Warning:</strong> 0 = bonne note, 10 = mauvaise note.
     * <p>
     * Note = 0 si la valeur est dans l'intervalle acceptable
     * Note = écart à borne inf acceptable si la valeur est inférieure à cette borne
     * Note = écart à borne sup acceptable si la valeur est supérieure à cette borne
	 */
	@XmlElement(name="qualiteContrasteClarte")
	private double qualiteContrasteClarte = -1;
	
	/**
	 * Gets the quality of lightness contrast.
	 * 
	 * @return The quality of the lightness contrast
	 */
	public double getQualiteContrasteClarte() {
		return qualiteContrasteClarte;
	}
	
	/**
	 * Sets the quality of lightness contrast.
	 * 
	 * @param qualiteContrasteClarte The quality of the lightness contrast
	 */
	public void setQualiteContrasteClarte(double qualiteContrasteClarte) {
		this.qualiteContrasteClarte = qualiteContrasteClarte;
	}
	
	
	/**
	 * Default Contructor
	 */
	public Contrast (){
		super();
	}
	
	public Contrast(int idC1, int idC2, double contrasteTeinte,
			double contrasteClarte) {
		super();
		this.idC1 = idC1;
		this.idC2 = idC2;
		this.contrasteTeinte = contrasteTeinte;
		this.contrasteClarte = contrasteClarte;
	}
	
	/**
	 * Determine les qualités de teinte acceptables, en fonction du type de relation.
     *   
     * NB: cela dépend à première vue de l'étalonage de la matrice de contraste,
     * ce qui explique que cette méthode est mise ici. Peut être déplacée néanmoins. 
     * FIXME : methode à tester
     * TODO : Commentaire en anglais
     *  
     * @return Liste de deux chiffres (borne sup et inf des valeurs acceptables)
     */
    public static List<Integer> getHueContrastQualityBounds(int type) {
        List<Integer> conditions = new ArrayList<Integer>();

        switch (type) {
            case SemanticRelation.DIFFERENCE:
                //notes acceptables de teinte : de 3 à 6
                conditions.add(new Integer(3));
                conditions.add(new Integer(6));
    
            case SemanticRelation.ASSOCIATION:
                //notes acceptables de teinte :  0 à 2
                conditions.add(new Integer(0));
                conditions.add(new Integer(2));
                break;
                
            case SemanticRelation.ORDER:
                //notes acceptables de teinte :  0 à 1
                conditions.add(new Integer(0));
                conditions.add(new Integer(1));
                break;
                
            default:
                break;
        }
        return conditions;
    }
    
    
    /**
     * Determine les qualités de clarté acceptables, 
     * en fonction du type de relation et du rapport 
     * entre les surfaces respectives couvertes par les objets.  
     *  
     * NB: cela dépend à première vue de l'étalonage de la matrice de contraste, 
     * ce qui explique que cette méthode est mise ici. Peut être déplacée néanmoins.
     * FIXME : methode à tester
     * TODO : Commentaire en anglais
     *  
     * @return Liste de deux chiffres (borne sup et inf des valeurs acceptables)
     */
    public static List<Integer> getLightnessContrastQualityBounds(int type, double rapportSurfaces) {
        List<Integer> conditions = new ArrayList<Integer>();
        
        if (type != SemanticRelation.ORDER) {
            if (rapportSurfaces == 0) {
                //on ne passe jamais ici en fait?
                //note clarte: de 0 à 1
                conditions.add(new Integer(0));
                conditions.add(new Integer(1));
            }
            else if (rapportSurfaces < 2) {
                //note clarte: de 1 à 2
                conditions.add(new Integer(1));
                conditions.add(new Integer(2));
            }
            else if (rapportSurfaces < 4) {
                //note clarte: de 2 à 3
                conditions.add(new Integer(2));
                conditions.add(new Integer(3));
            }
            else if (rapportSurfaces < 6) {
                //note clarte: de 3 à 4
                conditions.add(new Integer(3));
                conditions.add(new Integer(4));
            }
            else if (rapportSurfaces < 8) {
                //note clarte: de 4 à 5
                conditions.add(new Integer(4));
                conditions.add(new Integer(5));
            }
            else {
                //note clarte: de 5 à 6
                conditions.add(new Integer(5));
                conditions.add(new Integer(6));
            }
        }

        if (type == SemanticRelation.ORDER) {
            //ça devrait dépendre de la valeur maximale de l'attribut...
            //FIXME pour l'instant entre 0 et 7 (à affiner)
            conditions.add(new Integer(1));
            conditions.add(new Integer(7));
        }

        return conditions;
    }
    
    @Override
    public String toString(){
      return "Contrast between " + this.idC1 + " et " + this.idC2
      + " : Clarte = " + this.contrasteClarte
      + " : Teinte = " + this.contrasteTeinte;
    }
}
