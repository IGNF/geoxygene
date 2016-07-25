package fr.ign.cogit.geoxygene.sig3d.util;

import java.awt.GraphicsEnvironment;

import javax.media.j3d.GraphicsConfigTemplate3D;

/**
 * Class to test if Java3D is installated;
 * 
 * @author mickaelbrasebin
 *
 */
public class Java3DInstallated {
	
	
	public static boolean isJava3DInstallated(){
		boolean isJava3DInstallated = false;
		
		try
		{
		   GraphicsConfigTemplate3D gconfigTemplate = new GraphicsConfigTemplate3D();
		   GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(gconfigTemplate);
		   isJava3DInstallated = true;
		
		}
		catch (Error e) // You shouldn't normally catch java.lang.Error... this is an exception
		{
		   System.out.println("Java3D binaries not installed");
		}	catch (Exception e) // You shouldn't normally catch java.lang.Error... this is an exception
		{
			System.out.println("Java3D binaries not installed");
		}
		
		
		return isJava3DInstallated;
	}

}
