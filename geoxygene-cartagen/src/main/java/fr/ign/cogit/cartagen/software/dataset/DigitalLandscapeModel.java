/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.dataset;

public class DigitalLandscapeModel implements DBType {

	@Override
	public boolean isDLM() {return true;}

	@Override
	public boolean isDCM() {return false;}

}
