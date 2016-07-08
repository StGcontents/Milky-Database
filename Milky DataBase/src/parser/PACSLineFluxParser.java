package parser;

import java.util.Arrays;

import controller.FluxFactory;
import parser.PACSFluxCSVParser;

public class PACSLineFluxParser extends PACSFluxCSVParser {
	
	private final static Integer[] FLAG_COLUMNS = new Integer[] { 1, 4, 7, 10, 13, 16, 19 };
	private final static Integer[] VALUES_COLUMNS = new Integer[] { 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20, 21 };
	
	private static PACSLineFluxParser me;
	private PACSLineFluxParser() {
		initEnums();
	}
	protected static synchronized FluxCSVParser instance() {
		if (me == null) me = new PACSLineFluxParser();
		return me;
	}
	
	@Override
	protected void initValueEnums() {
		FLAG_ENUM.addAll(Arrays.asList(FLAG_COLUMNS));
		DOUBLE_ENUM.addAll(Arrays.asList(VALUES_COLUMNS));
	}
	
	@Override
	protected FluxFactory getFactory() {
		return FluxFactory.getLineFluxFactory();
	}
}
