package pattern;

import javax.swing.SwingUtilities;

public abstract class ViewSubject<T> extends Subject<T> {

	@Override
	public void notifyObservers() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ViewSubject.super.notifyObservers();
			}
		});
	}
}
