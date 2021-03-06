package jdepend.statistics.action;

import java.awt.event.ActionEvent;

import jdepend.core.score.ScoreFacade;
import jdepend.core.score.ScoreInfo;
import jdepend.framework.exception.JDependException;
import jdepend.framework.ui.graph.GraphUtil;
import jdepend.framework.ui.graph.model.GraphData;
import jdepend.framework.ui.graph.model.GraphDataItem;
import jdepend.model.result.AnalysisResult;
import jdepend.statistics.StaticsFrame;

public class ScaleDAction extends ScoreListAction {

	public ScaleDAction(StaticsFrame frame) {
		super(frame, "规模抽象程度合理性分析");
	}

	@Override
	protected void analyse(ActionEvent e) throws JDependException {

		ScoreFacade.sort(scoreCollection.getScoreInfos(), AnalysisResult.Metrics_LC);

		GraphData graph = new GraphData();
		GraphDataItem item = new GraphDataItem();
		item.setTitle("规模抽象程度合理性折线图");
		item.setLineName("抽象程度合理性折线");
		item.setLineXName("代码行数");
		item.setLineYName("抽象程度合理性");
		item.setType(GraphDataItem.SPLINE);
		String tip;
		for (ScoreInfo scoreInfo : scoreCollection.getScoreInfos()) {
			item.addData(scoreInfo.lc, scoreInfo.distance);
			tip = scoreInfo.group + " " + scoreInfo.command;
			item.addTip(scoreInfo.lc, tip);
			this.progress();
		}
		graph.addItem(item);

		this.addResult("规模抽象程度合理性折线图", GraphUtil.createGraph(graph));

	}

}
