package Tester_Fede;

import org.junit.*;
import model.Galaxy;
import model.Galaxy.Luminosity;
import controller.GalaxyFactory;
import controller.GalaxySearchController;
import view.GalaxyInfoView;

public class SearchNameTest {

	@Before
	public Galaxy initialize(){
		Galaxy Mrk334;
		String name = "Mrk334";
		int h = 0;
		int min = 3;
		double sec = 9.6038;
		boolean sign = true;
		int deg= 21;
		int arcmin= 57;
		double arcsec= 6.8064;
		double redshift= 0.0219450;
		Double distance = 96.600;
		String spectre = "S1.8";
		Luminosity lum1 = new Luminosity (41.16,false);
		Luminosity lum2 = new Luminosity (41.09,false);
		Luminosity lum3 = new Luminosity (41.22,false);
		Luminosity[] luminosities = new Luminosity [3];
		luminosities[0]=lum1;
		luminosities[1]=lum2;
		luminosities[2]=lum3;
		Double metallicity =null;
		Double metallicityError = null;
		return Mrk334 = GalaxyFactory.instance().createGalaxy(name, h, min, sec, sign, deg, arcmin, arcsec, redshift, distance, spectre, luminosities, metallicity, metallicityError);
		
	}
	
	public void isSearchCorrect() {
	}{
		Galaxy result;
		GalaxySearchController.instance().searchNames("Mrk334");
		
	}
}
