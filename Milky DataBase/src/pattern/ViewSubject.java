package pattern;

import javax.swing.SwingUtilities;

/**
 * A peculiar implementation of Subject; it is used in conjunction
 * with ViewObserverAdapters and is a Thread-safe implementation of the pattern
 * that keeps GUI elements from acting crazy. 
 * @author stg
 *
 * @param <T>
 */
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
