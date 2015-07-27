package jdepend.core.serviceproxy.framework;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import jdepend.framework.exception.JDependException;
import jdepend.framework.file.AnalyzeData;
import jdepend.metadata.JavaPackage;
import jdepend.model.Component;
import jdepend.model.result.AnalysisResult;
import jdepend.parse.BuildListener;
import jdepend.parse.ParseListener;
import jdepend.service.AnalyseListener;

/**
 * 后台服务代理
 * 
 * @author <b>Abner</b>
 * 
 */
public interface JDependServiceProxy {
	
	/**
	 * 得到服务分析目标包含的包集合
	 * 
	 * @return
	 * @throws JDependException 
	 */
	public Collection<JavaPackage> getPackages() throws JDependException;

	/**
	 * 分析过程
	 * 
	 * @return 分析单元集合
	 * @throws JDependException
	 */
	AnalysisResult analyze() throws JDependException;

	/**
	 * 设置组件
	 * 
	 * 该组件主要是判断哪些package集合是一个分析单元
	 * 
	 * @param component
	 */
	void setComponent(Component component);

	/**
	 * 设置log writer
	 * 
	 * @param printWriter
	 */
	void setLogWriter(PrintWriter printWriter);

	/**
	 * 设置分析目标路径
	 * 
	 * @param name
	 * @throws JDependException
	 */
	void addDirectory(String name) throws JDependException;
	
	/**
	 * 设置分析数据
	 * 
	 * @param data
	 * @throws JDependException 
	 */
	void setAnalyseData(AnalyzeData data);

	/**
	 * 计算类总数
	 * 
	 * @return
	 */
	int countClasses();

	/**
	 * 增加解析接口
	 * 
	 * @param listener
	 */
	void addParseListener(ParseListener listener);

	/**
	 * 增加构建接口
	 * 
	 * @param listener
	 */
	void addBuildListener(BuildListener listener);

	/**
	 * 增加分析接口
	 * 
	 * @param listener
	 */
	void addAnalyseListener(AnalyseListener listener);
	/**
	 * 设置filteredPackages
	 * 
	 * @param filteredPackages
	 */
	void addCommandFilteredPackages(List<String> filteredPackages);
}
