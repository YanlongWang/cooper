package jdepend.util.refactor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdepend.framework.exception.JDependException;
import jdepend.model.JDependUnitMgr;
import jdepend.model.result.AnalysisResult;

public class AdjustHistory {

	private List<Memento> mementos = new ArrayList<Memento>();

	private List<String> actions;

	private AnalysisResult current;// 移动后的当前结果

	private Memento compared = null;

	private static AdjustHistory inst = new AdjustHistory();

	private AdjustHistory() {
	}

	public static AdjustHistory getInstance() {
		return inst;
	}

	public void addMemento() {
		AnalysisResult result = JDependUnitMgr.getInstance().getResult().clone();
		this.addMemento(CreateMemento(result));
	}

	private void addMemento(Memento memento) {
		this.mementos.add(memento);
	}

	private Memento CreateMemento(AnalysisResult result) {
		return new Memento(result, actions);
	}

	public Memento getCompared() {
		if (this.compared != null) {
			return this.compared;
		} else {
			return this.getOriginality();
		}
	}

	public Memento getOriginality() {
		if (this.mementos.size() == 0) {
			return null;
		} else {
			return this.mementos.get(0);
		}
	}

	public void setCompared(Memento memento) {
		this.compared = memento;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

	public Memento getPrior() {
		return this.mementos.get(this.mementos.size() - 1);
	}

	public List<Memento> getMementos() {
		return this.mementos;
	}

	public Memento getTheMemento(Date date) {
		for (Memento memento : this.mementos) {
			if (memento.getCreateDate().equals(date)) {
				return memento;
			}
		}
		return null;
	}

	public void clear() {
		this.mementos = new ArrayList<Memento>();
		this.actions = null;
		this.current = null;
		this.compared = null;
	}

	public boolean empty() {
		return this.mementos.size() == 0;
	}

	public void setCurrent(AnalysisResult current) {
		this.current = current;
	}

	public AnalysisResult getCurrent() {
		return current;
	}

	public CompareInfo compare(CompareObject object) throws JDependException {
		if (this.getCompared() != null) {
			AnalysisResult result = this.getCompared().getResult();
			Object originality = object.getOriginalityValue(result);
			// 获取比较的数值
			CompareInfo info = new CompareInfo(object);
			info.setMetrics(object.getMetrics());
			info.setValue(object.getValue());
			info.setOriginality(originality);
			// 进行比较
			info.calResult();

			return info;
		} else {
			return null;
		}
	}
}
