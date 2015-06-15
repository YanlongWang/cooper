package jdepend.statistics.action;

import jdepend.framework.exception.JDependException;
import jdepend.framework.ui.action.AsynAction;
import jdepend.framework.ui.component.JDependFrame;
import jdepend.statistics.domain.ScoreCollection;

public abstract class ScoreListAction extends AsynAction {

	protected ScoreCollection scoreCollection;

	public ScoreListAction(JDependFrame frame, String name) {
		super(frame, name);
		this.scoreCollection = ScoreCollection.getInstance();
	}

	@Override
	protected int getProcess() throws JDependException {
		return scoreCollection.getScoreInfos().size();
	}

}
