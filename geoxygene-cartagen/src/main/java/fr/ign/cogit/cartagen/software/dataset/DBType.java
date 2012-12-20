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

public interface DBType {

	/**
	 * true if the type of database is DigitalLandscapeModel.
	 * @return
	 */
	public boolean isDLM();
	
	/**
	 * true if the type of database is DigitalCartographicModel.
	 * @return
	 */
	public boolean isDCM();
}
