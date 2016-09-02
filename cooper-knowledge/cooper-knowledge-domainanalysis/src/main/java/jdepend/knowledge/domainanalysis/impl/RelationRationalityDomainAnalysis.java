package jdepend.knowledge.domainanalysis.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jdepend.framework.util.BundleUtil;
import jdepend.knowledge.domainanalysis.AbstractDomainAnalysis;
import jdepend.knowledge.domainanalysis.AdviseInfo;
import jdepend.knowledge.domainanalysis.StructureCategory;
import jdepend.model.Relation;
import jdepend.model.result.AnalysisResult;
import jdepend.model.util.RelationByMetricsComparator;

public final class RelationRationalityDomainAnalysis extends AbstractDomainAnalysis {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8403210347174082973L;

	public RelationRationalityDomainAnalysis() {
		super("关系合理性分析器", "用于对最值得关注的关系项目进行建议");
	}

	@Override
	protected AdviseInfo doAdvise(String name, AnalysisResult result) {
		if (result.getAttentionRelationScale() > 0) {

			List<Relation> relations = new ArrayList<Relation>(result.getRelations());
			Collections.sort(relations, new RelationByMetricsComparator(Relation.AttentionLevel, false));

			String advise = BundleUtil.getString(BundleUtil.Advise_Relation_AttentionLevel_Big);
			AdviseInfo info = new RelationAdviseInfo(relations.get(0));
			info.setDesc(advise);

			return info;
		}
		return null;

	}

	@Override
	public StructureCategory getStructureCategory() {
		return StructureCategory.RelationRationalityDomainAnalysis;
	}
}
