package jdepend.knowledge.domainanalysis.impl;

import jdepend.framework.util.MetricsFormat;
import jdepend.knowledge.domainanalysis.AbstractDomainAnalysis;
import jdepend.knowledge.domainanalysis.AdviseInfo;
import jdepend.knowledge.domainanalysis.StructureCategory;
import jdepend.model.result.AnalysisResult;

public final class SummaryDomainAnalysis extends AbstractDomainAnalysis {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4360237506670726504L;

	public SummaryDomainAnalysis() {
		super("整体分数小结", "用于对分数进行建议");
	}

	@Override
	protected AdviseInfo doAdvise(String name, AnalysisResult result) {

		StringBuilder content = new StringBuilder();
		Float score = result.getScore();
		if (score != null) {
			if (score < 50F) {
				content.append("整体得分（" + score + "）偏低");
			} else if (score < 70F) {
				content.append("整体得分（" + score + "）中等偏低");
			} else if (score < 80F) {
				content.append("整体得分（" + score + "）中等偏高");
			} else {
				content.append("整体得分（" + score + "）较高");
			}
		}
		Float relationRationality = result.getRelationRationality();
		if (relationRationality != null) {
			if (relationRationality < 10F) {
				content.append("，存在问题的关系较多（比例为" + MetricsFormat.toFormattedPercent(result.getAttentionRelationScale())
						+ "）");
			} else if (relationRationality == 40F) {
				content.append("，组件间关系合理，恰当");
			} else if (relationRationality > 30F) {
				content.append("，组件间关系设计得较好（存在问题的关系比例为"
						+ MetricsFormat.toFormattedPercent(result.getAttentionRelationScale()) + "）");
			}
		}
		Float balance = result.getBalance();
		if (balance != null) {
			if (balance < 10F) {
				content.append("，内聚性方面做得欠佳");
			} else if (balance >= 18F) {
				content.append("，内聚性方面做得不错");
			}
		}
		Float distance = result.getDistance();
		if (distance != null) {
			if (distance < 10F) {
				content.append("，抽象程度设计上表现不好");
			} else if (distance >= 18F) {
				content.append("，抽象程度设计上表现良好");
			}
		}
		Float encapsulation = result.getEncapsulation();
		if (encapsulation != null) {
			if (encapsulation < 10F) {
				content.append("，封装性方面做得欠佳");
			} else if (balance >= 18F) {
				content.append("，封装性方面做得不错");
			}
		}
		return new AdviseInfo(content.toString());

	}

	@Override
	public StructureCategory getStructureCategory() {
		return StructureCategory.Summary;
	}

}
