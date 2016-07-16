package pattern;

import model.Statistics;
import view.HeavyTaskView;

/**
 * Observer implementation requesting for statistical calculation values.
 * @author stg
 *
 */
public class StatisticsObserverAdapter extends ViewObserverAdapter<Statistics, HeavyTaskView> {
	
	public StatisticsObserverAdapter(HeavyTaskView adaptee) {
		setAdaptee(adaptee);
	}

	@Override
	public void doStuff() {
		adaptee.update(subject.retrieveState());
	}

}
