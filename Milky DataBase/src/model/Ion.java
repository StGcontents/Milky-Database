package model;

public class Ion {
	
	private int id;
	private String atom;
	private int chargedState;
	private double line;
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	public String getAtom() { return atom; }
	public void setAtom(String atom) { this.atom = atom; }

	public int getChargedState() { return chargedState; }
	public void setChargedState(int chargedState) { this.chargedState = chargedState; }

	public double getLine() { return line; }
	public void setLine(double line) { this.line = line; }

	public Ion(int id, String atom, int chargedState, double line) {
		setId(id);
		setAtom(atom);
		setChargedState(chargedState);
		setLine(line);
	}
	@Override
	public String toString() {
		double d = Math.floor(getLine());
		d += (d - Math.floor(getLine() * 10) / 10.0);
		return getAtom() + getChargedState() + " " + d;
	}
	
	public boolean matches(String atom, int chargedState, double line) {
		return getAtom().equals(atom) 
				&& getChargedState() == chargedState 
				&& Math.abs(getLine() - line) < 1;
	}
}