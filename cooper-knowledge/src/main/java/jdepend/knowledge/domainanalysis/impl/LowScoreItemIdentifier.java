package jdepend.knowledge.domainanalysis.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jdepend.framework.util.BundleUtil;
import jdepend.knowledge.domainanalysis.AbstractDomainAnalysis;
import jdepend.knowledge.domainanalysis.AdviseInfo;
import jdepend.knowledge.domainanalysis.StructureCategory;
import jdepend.model.result.AnalysisResult;

public final class LowScoreItemIdentifier extends AbstractDomainAnalysis {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7594679029292290185L;

	public LowScoreItemIdentifier() {
		super("低分项目识别器", "用于识别分数最低的分项项目");
	}

	@Override
	protected AdviseInfo doAdvise(String name, AnalysisResult result) {

		List<ScoreScaleItem> items = new ArrayList<ScoreScaleItem>();

		if (result.getDistance() != null) {
			items.add(new ScoreScaleItem(AnalysisResult.Metrics_D, result.getDistance() / result.getDistanceScale()));
		}
		if (result.getBalance() != null) {
			items.add(new ScoreScaleItem(AnalysisResult.Metrics_Balance, result.getBalance() / result.getBalanceScale()));
		}
		if (result.getEncapsulation() != null) {
			items.add(new ScoreScaleItem(AnalysisResult.Metrics_Encapsulation, result.getEncapsulation()
					/ result.getEncapsulationScale()));
		}
		if (result.getRelationRationality() != null) {
			items.add(new ScoreScaleItem(AnalysisResult.Metrics_RelationRationality, result.getRelationRationality()
					/ result.getRelationRationalityScale()));
		}

		if (items.size() > 0) {
			Collections.sort(items);
			AdviseInfo info = new AdviseInfo();
			info.setDesc(BundleUtil.getString(BundleUtil.Advise_LessScoreItem));
			info.addComponentName(items.iterator().next().name);
			return info;
		} else {
			return null;
		}
	}

	@Override
	public StructureCategory getStructureCategory() {
		return StructureCategory.LowScoreItemIdentifier;
	}

	class ScoreScaleItem implements Comparable<ScoreScaleItem> {

		public String name;
		public Float scale;

		public ScoreScaleItem(String name, Float scale) {
			super();
			this.name = name;
			this.scale = scale;
		}

		@Override
		public int compareTo(ScoreScaleItem o) {
			return scale.compareTo(o.scale);
		}

	}

}
