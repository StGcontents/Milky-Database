package parser;

import java.util.ArrayList;
import java.util.List;

import controller.FluxFactory;
import model.Galaxy;
import model.Ion;

/**
 * AbstractCSVParser specialization, implemented to manage files collecting Flux data.
 * It is in turn extended by other classes, each one developed for a certain type of
 * Flux data file.
 * @author federico
 *
 */
public abstract class FluxCSVParser extends AbstractCSVParser<Galaxy> {
	
	protected final List<Integer> FLAG_ENUM = new ArrayList<>();
	protected final List<Integer> DOUBLE_ENUM = new ArrayList<>();
	protected final List<Ion> ION_ENUM = new ArrayList<>();
	
	protected List<Double> values = new ArrayList<>();
	protected List<Boolean> flags = new ArrayList<>();
	
	protected void initEnums() {
		initIonEnum();
		initValueEnums();
	}
	
	protected abstract void initIonEnum() ;
	protected abstract void initValueEnums() ;
	protected abstract FluxFactory getFactory() ;
	
	protected void fillGalaxy(Galaxy galaxy, String aperture) {
		int top = ION_ENUM.size();
		for (int i = 0; i < top; ++i) {
			if (values.get(2 * i) != null) {
				galaxy.addFlux(getFactory().create( 
					ION_ENUM.get(i), values.get(2 * i), 
					flags.get(i), values.get(2 * i + 1), aperture));
			}
		}
	}
}
