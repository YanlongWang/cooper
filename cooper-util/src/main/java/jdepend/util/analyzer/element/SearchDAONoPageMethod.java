package jdepend.util.analyzer.element;

import jdepend.framework.exception.JDependException;
import jdepend.metadata.Method;
import jdepend.model.JavaClassUnit;
import jdepend.model.result.AnalysisResult;
import jdepend.util.analyzer.framework.AbstractAnalyzer;
import jdepend.util.analyzer.framework.Analyzer;
import jdepend.util.analyzer.framework.AnalyzerException;

public class SearchDAONoPageMethod extends AbstractAnalyzer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5733130337906328027L;
	private String daoEndClassName;
	private String pageType;

	public SearchDAONoPageMethod() {
		super("搜索DAO上的没有分页信息的方法", Analyzer.Attention, "搜索DAO上的没有分页信息的方法");
	}

	@Override
	protected void doSearch(AnalysisResult result) throws AnalyzerException {
		if (daoEndClassName == null || daoEndClassName.length() == 0) {
			throw new AnalyzerException("没有daoEndClassName参数的定义");
		}
		if (pageType == null || pageType.length() == 0) {
			throw new AnalyzerException("没有pageType参数的定义");
		}
		for (JavaClassUnit javaClass : result.getClasses()) {
			if (javaClass.getName().endsWith(daoEndClassName)) {
				for (Method method : javaClass.getJavaClass().getSelfMethods()) {
					if (method.existReturn()) {
						if (method.getReturnTypes().contains("java.util.List")) {
							if (!method.getArgumentTypes().contains(pageType)) {
								this.print(javaClass.getName() + "." + method.getName());
								this.print("\n");
							}
						}
					}
				}
			}
		}
	}

	public String getDaoEndClassName() {
		return daoEndClassName;
	}

	public void setDaoEndClassName(String daoEndClassName) {
		this.daoEndClassName = daoEndClassName;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

}
