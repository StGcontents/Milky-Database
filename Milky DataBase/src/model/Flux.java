package model;

public class Flux {
	
	private boolean isContinuous;
	private Ion ion;
	private double flux;
	private boolean upperLimit;
	private double error;
	private String aperture;
	
	protected class Ion {
		private String atom;
		private int chargedState;
		private int line;
		
		public String getAtom() { return atom; }
		public void setAtom(String atom) { this.atom = atom; }

		public int getChargedState() { return chargedState; }
		public void setChargedState(int chargedState) { this.chargedState = chargedState; }

		public int getLine() { return line; }
		public void setLine(int line) { this.line = line; }

		protected Ion(String atom, int chargedState, int line) {
			setAtom(atom);
			setChargedState(chargedState);
			setLine(line);
		}
	}
}
