package view;

import java.awt.Container;

public abstract class View {
	
	public abstract Container generateView() ;
	
	public abstract void showError(Exception e) ;
	
	protected abstract void reset() ;
	
	public abstract boolean isCurrentlyShown() ; 
}
