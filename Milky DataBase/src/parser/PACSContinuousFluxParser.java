package parser;

import java.util.Arrays;

import controller.FluxFactory;
import parser.PACSFluxCSVParser;

public class PACSContinuousFluxParser extends PACSFluxCSVParser {

	private final static Integer[] FLAG_COLUMNS = new Integer[] { 5, 8, 11, 14, 17 };
	private final static Integer[] VALUES_COLUMNS = new Integer[] { 1, 2, 3, 4, 6, 7, 9, 10, 12, 13, 15, 16, 18, 19 };
	
	private static PACSContinuousFluxParser me;
	private PACSContinuousFluxParser() {
		initEnums();
		clearValues();
	}
	protected static synchronized FluxCSVParser instance() {
		if (me == null) me = new PACSContinuousFluxParser();
		return me;
	}
	
	@Override
	protected void initValueEnums() {
		FLAG_ENUM.addAll(Arrays.asList(FLAG_COLUMNS));
		DOUBLE_ENUM.addAll(Arrays.asList(VALUES_COLUMNS));
	}
	
	@Override
	protected FluxFactory getFactory() {
		return FluxFactory.getContinuousFluxFactory();
	}
	
	@Override
	protected void clearValues() {
		super.clearValues();
		flags.add(false);
		flags.add(false);
	}
	
}
