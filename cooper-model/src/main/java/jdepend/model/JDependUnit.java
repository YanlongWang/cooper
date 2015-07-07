package jdepend.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jdepend.metadata.JavaClass;
import jdepend.metadata.JavaPackage;
import jdepend.model.result.AnalysisResult;

/**
 * 分析单元
 * 
 * @author <b>Abner</b>
 * 
 */
public interface JDependUnit extends Serializable, Measurable, Comparable<JDependUnit> {

	/**
	 * 该分析单元是否是内部对象
	 * 
	 * @return
	 */
	public boolean isInner();

	/**
	 * 得到类型
	 * 
	 * @return
	 */
	public JDependUnitType getType();

	public void setType(JDependUnitType type);

	public Collection<JavaClassUnit> getClasses();

	public boolean containsClass(JavaClassUnit javaClass);
	
	public boolean containsClass(JavaClass javaClass);

	public Collection<JavaPackage> getJavaPackages();

	/**
	 * 得到该分析单元的路径（根包名称）
	 * 
	 * @return
	 */
	public String getPath();

	public Collection<? extends JDependUnit> getAfferents();

	public Collection<? extends JDependUnit> getEfferents();

	/**
	 * 收集循环依赖信息
	 * 
	 * @param list
	 * @param knowledge
	 * @return
	 */
	int collectCycle(List<JDependUnit> list, Map<JDependUnit, Integer> knowledge);

	/**
	 * 收集第一个循环依赖链
	 * 
	 * @return
	 */
	public List<? extends JDependUnit> collectCycle();

	/**
	 * 与特定分析单元的传出耦合信息明细
	 * 
	 * @param dependUnit
	 * @return
	 */
	public RelationDetail ceCouplingDetail(JDependUnit dependUnit);

	/**
	 * 与特定分析单元的传入耦合信息明细
	 * 
	 * @param dependUnit
	 * @return
	 */
	public RelationDetail caCouplingDetail(JDependUnit dependUnit);

	/**
	 * 传入耦合值
	 * 
	 * @return
	 */
	public float caCoupling();

	/**
	 * 传出耦合值
	 * 
	 * @return
	 */
	public float ceCoupling();

	/**
	 * 与特定分析单元的耦合值
	 * 
	 * @param dependUnit
	 * @return
	 */
	public float coupling(JDependUnit dependUnit);
	
	/**
	 * 得到分析结果
	 * 
	 * @return
	 */
	public AnalysisResult getResult();

	/**
	 * 清空缓存
	 */
	public void clear();

	public int hashCode();

	public boolean equals(Object obj);
}
