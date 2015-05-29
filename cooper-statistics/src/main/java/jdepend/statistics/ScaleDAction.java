package jdepend.statistics;

import java.awt.event.ActionEvent;
import java.util.Collections;

import jdepend.core.local.config.CommandConfMgr;
import jdepend.core.local.score.ScoreByItemComparator;
import jdepend.core.local.score.ScoreInfo;
import jdepend.framework.exception.JDependException;
import jdepend.framework.ui.graph.GraphData;
import jdepend.framework.ui.graph.GraphDataItem;
import jdepend.framework.ui.graph.GraphUtil;
import jdepend.model.result.AnalysisResult;

public class ScaleDAction extends ScoreListAction {

	public ScaleDAction(StaticsFrame frame) {
		super(frame, "规模抽象程度合理性分析");
	}

	@Override
	protected void analyse(ActionEvent e) throws JDependException {

		Collections.sort(scoreCollection.getScoreInfos(), new ScoreByItemComparator(AnalysisResult.Metrics_LC));

		GraphData graph = new GraphData();
		GraphDataItem item = new GraphDataItem();
		item.setTitle("规模抽象程度合理性折线图");
		item.setLineName("抽象程度合理性折线");
		item.setLineXName("代码行数");
		item.setLineYName("抽象程度合理性");
		item.setType(GraphDataItem.SPLINE);
		String tip;
		String attribute;
		for (ScoreInfo scoreInfo : scoreCollection.getScoreInfos()) {
			item.addData(scoreInfo.lc, scoreInfo.distance);
			attribute = CommandConfMgr.getInstance().getTheGroup(scoreInfo.group).getAttribute();
			if (attribute != null && attribute.length() > 0) {
				tip = attribute + " ";
			}
			tip = scoreInfo.group + " " + scoreInfo.command;
			item.addTip(scoreInfo.lc, tip);
			this.progress();
		}
		graph.addItem(item);

		this.addResult("规模抽象程度合理性折线图", GraphUtil.getInstance().createGraph(graph));

	}

}
