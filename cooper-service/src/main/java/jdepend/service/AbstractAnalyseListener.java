package jdepend.service;


public abstract class AbstractAnalyseListener implements AnalyseListener {
	@Override
	public int compareTo(AnalyseListener o) {
		return this.order().compareTo(o.order());
	}
}
