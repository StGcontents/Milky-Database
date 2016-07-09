package pattern;

import model.Statistics;
import view.HeavyTaskView;

public class StatisticsObserverAdapter extends ViewObserverAdapter<Statistics, HeavyTaskView> {
	
	public StatisticsObserverAdapter(HeavyTaskView adaptee) {
		setAdaptee(adaptee);
	}

	@Override
	public void stateChanged() {
		adaptee.update(subject.retrieveState());
	}

}
