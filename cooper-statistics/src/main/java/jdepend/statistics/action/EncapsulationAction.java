package jdepend.statistics.action;

import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import jdepend.core.score.ScoreInfo;
import jdepend.framework.exception.JDependException;
import jdepend.framework.ui.graph.GraphUtil;
import jdepend.framework.ui.graph.model.GraphData;
import jdepend.framework.ui.graph.model.GraphDataItem;
import jdepend.model.result.AnalysisResult;
import jdepend.statistics.StaticsFrame;

public final class EncapsulationAction extends ScoreListAction {

	public EncapsulationAction(StaticsFrame frame) {
		super(frame, "组件封装性");
	}

	@Override
	protected void analyse(ActionEvent e) throws JDependException {

		Map<Object, Object> datas = new LinkedHashMap<Object, Object>();
		AnalysisResult result;
		for (ScoreInfo scoreInfo : scoreCollection.getScoreInfos()) {
			result = scoreCollection.getTheResult(scoreInfo);
			datas.put(result.getRunningContext().getGroup() + "|" + result.getRunningContext().getCommand(), result
					.getSummary().getEncapsulation());
			this.progress();
		}

		GraphData data = new GraphData();
		data.setColCount(1);

		GraphDataItem item = new GraphDataItem();

		item.setTitle("组件封装性");
		item.setGroup("Graph");
		item.setType(GraphDataItem.BAR);
		item.setLineXName("项目名称");
		item.setLineYName("组件封装性");
		item.setDatas(datas);

		data.addItem(item);

		this.addResult("组件封装性柱状图", GraphUtil.createGraph(data));

	}
}
