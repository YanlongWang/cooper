package jdepend.statistics.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdepend.core.score.ScoreFacade;
import jdepend.core.score.ScoreInfo;
import jdepend.framework.exception.JDependException;
import jdepend.model.result.AnalysisResult;

public class ScoreCollection {

	private static ScoreCollection inst = new ScoreCollection();

	private List<ScoreInfo> scoreInfos;

	private Map<String, AnalysisResult> results = new HashMap<String, AnalysisResult>();

	private ScoreCollection() {

	}

	public static ScoreCollection getInstance() {
		return inst;
	}

	public synchronized List<ScoreInfo> getScoreInfos() throws JDependException {
		if (scoreInfos == null) {
			scoreInfos = ScoreFacade.getScoreList();
		}
		return scoreInfos;
	}

	public synchronized AnalysisResult getTheResult(ScoreInfo scoreInfo) throws JDependException {
		if (!results.containsKey(scoreInfo.id)) {
			results.put(scoreInfo.id, ScoreFacade.getTheResult(scoreInfo));
		}
		return results.get(scoreInfo.id);

	}

	public synchronized void clear() {
		scoreInfos = null;
		results = new HashMap<String, AnalysisResult>();
	}

}
