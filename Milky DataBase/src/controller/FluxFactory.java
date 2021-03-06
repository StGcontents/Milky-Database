package controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Flux;
import model.Ion;
import model.IonPool;

/**
 * Concrete Factory for Flux objects instantiation. It has two subclasses,
 * one for Line Flux objects and the other for Continuous Flux objects.
 * @author stg
 *
 */
public abstract class FluxFactory extends AbstractFactory<Flux> {
	
	public static FluxFactory getFactoryByType(boolean isContinuous) {
		if (isContinuous) return ContinuousFluxFactory.instance();
		else return LineFluxFactory.instance();
	}
	
	public static FluxFactory getLineFluxFactory() {
		return getFactoryByType(false);
	}
	
	public static FluxFactory getContinuousFluxFactory() {
		return getFactoryByType(true);
	}
	
	@Override
	public List<Flux> create(ResultSet set) {
		//TODO
		List<Flux> fluxes = new ArrayList<>();
		
		try {
			while (set.next()) {
				
				try {
					Flux flux = newInstance();
				
					flux.setValue(set.getDouble(1));
					flux.setError(set.getDouble(2));
					flux.setUpperLimit(set.wasNull());
					flux.setAperture(set.getString(3));
					
					Ion ion = IonPool.checkById(set.getInt(4));
					if (ion == null) {
						ion = new Ion(set.getInt(4), set.getString(5), set.getInt(6), set.getDouble(7));
						IonPool.insert(ion);
					}
					flux.setIon(ion);
				
					fluxes.add(flux);
				}
				catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return fluxes;
	}
	
	public Flux create(Ion ion, double value, boolean upperLimit, Double error, String aperture) {
		
		Flux flux = newInstance();
		
		flux.setIon(ion);
		flux.setValue(value);
		flux.setUpperLimit(upperLimit);
		flux.setError(error);
		flux.setAperture(aperture);
		return flux;
	}
	
	protected abstract Flux newInstance() ;
	
	protected static class ContinuousFluxFactory extends FluxFactory {
		private static FluxFactory me;
		private ContinuousFluxFactory() { }
		static synchronized FluxFactory instance() {
			if (me == null) me = new ContinuousFluxFactory();
			return me;
		}
		
		@Override
		protected Flux newInstance() {
			Flux flux = new Flux();
			flux.setContinuous(true);
			return flux;
		}
	}
	
	protected static class LineFluxFactory extends FluxFactory {
		private static FluxFactory me;
		private LineFluxFactory() { }
		static synchronized FluxFactory instance() {
			if (me == null) me = new LineFluxFactory();
			return me;
		}
		
		@Override
		protected Flux newInstance() {
			Flux flux = new Flux();
			flux.setContinuous(false);
			return flux;
		}
	}
}
