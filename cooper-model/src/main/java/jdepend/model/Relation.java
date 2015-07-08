package jdepend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jdepend.framework.util.MathUtil;
import jdepend.framework.util.MetricsFormat;
import jdepend.metadata.JavaClassRelationItem;
import jdepend.model.profile.model.RelationProfile;
import jdepend.model.result.AnalysisResult;

/**
 * 两个Element间的关系对象
 * 
 * @author <b>Abner</b>
 * 
 */
public final class Relation implements Comparable<Relation>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6078087861336926293L;

	private Element current = null;

	private Element depend = null;

	private RelationDetail detail;

	private transient boolean isAttention = true;// 是否值得注意，可以通过外部设置为不关注

	private transient String attentionType;// 缓存
	private transient Float attentionLevel;// 缓存

	private transient boolean normality = true;

	public static final String DefaultAttentionType = "DefaultAttentionType";
	public static final String CycleDependAttentionType = "CycleDependAttentionType";// 循环依赖的关系
	public static final String SDPAttentionType = "SDPAttentionType";// 违反稳定依赖原则的关系
	public static final String ComponentLayerAttentionType = "ComponentLayerAttentionType";// 下层组件依赖了上层组件的关系
	public static final String MutualDependAttentionType = "MutualDependAttentionType";// 彼此依赖的关系

	public static final Map<String, String> AttentionTypeList = new HashMap<String, String>();
	static {
		AttentionTypeList.put(DefaultAttentionType, "");
		AttentionTypeList.put(CycleDependAttentionType, "循环依赖");
		AttentionTypeList.put(SDPAttentionType, "违反稳定依赖原则");
		AttentionTypeList.put(ComponentLayerAttentionType, "下层组件依赖了上层组件");
		AttentionTypeList.put(MutualDependAttentionType, "彼此依赖");
	}

	public static final String CurrentName = "Relation_CurrentName";
	public static final String DependName = "Relation_DependName";
	public static final String CurrentCohesion = "Relation_CurrentCohesion";
	public static final String DependCohesion = "Relation_DependCohesion";
	public static final String Intensity = "Relation_Intensity";
	public static final String AttentionType = "Relation_AttentionType";
	public static final String AttentionLevel = "Relation_AttentionLevel";
	public static final String Balance = "Relation_Balance";
	public static final String isProblem = "Relation_isProblem";

	public Relation() {
		super();
	}

	public void setCurrent(Element current) {
		this.current = current;
	}

	public void setDepend(Element depend) {
		this.depend = depend;
	}

	public float getIntensity() {
		return detail.getIntensity();
	}

	public Collection<JavaClassRelationItem> getItems() {
		return this.detail.getItems();
	}

	public void setDetail(RelationDetail detail) {
		this.detail = detail;
	}

	public RelationDetail getDetail() {
		return detail;
	}

	/**
	 * 关系平衡指数
	 * 
	 * @return
	 */
	public float getBalance() {
		return this.current.getIntensity() + this.depend.getIntensity() - detail.getIntensity();
	}

	public int size() {
		return this.getItems().size();
	}

	public int compareTo(Relation obj) {
		return new Float(this.getBalance()).compareTo(obj.getBalance());
	}

	public Element getCurrent() {
		return current;
	}

	public Element getDepend() {
		return depend;
	}

	public Component getOpposite(Component component) {
		if (this.current.getComponent().equals(component)) {
			return this.depend.getComponent();
		} else if (this.depend.getComponent().equals(component)) {
			return this.current.getComponent();
		} else {
			return null;
		}
	}

	public String getAttentionType() {
		if (!this.isAttention) {
			return DefaultAttentionType;
		}
		if (this.attentionType == null) {
			this.attentionType = this.calAttentionType();
		}
		return this.attentionType;
	}

	/**
	 * 得到关注程度
	 * 
	 * @param relations
	 * @return
	 */
	public Float getAttentionLevel() {
		if (!this.isAttention) {
			return new Float(DefaultAttentionType);
		}
		if (this.attentionLevel == null) {
			this.attentionLevel = this.calAttentionLevel();
		}
		return this.attentionLevel;
	}

	public String getAttentionTypeName() {
		return AttentionTypeList.get(this.getAttentionType());
	}

	public boolean isNormality() {
		return normality;
	}

	public boolean isAttention() {
		return this.isAttention && !this.getAttentionType().equals(DefaultAttentionType);
	}

	public void setAttention(boolean isAttention) {
		this.isAttention = isAttention;
	}

	public Object getValue(String metrics) {
		if (metrics.equals(CurrentName)) {
			return this.current.getName();
		} else if (metrics.equals(DependName)) {
			return this.depend.getName();
		} else if (metrics.equals(CurrentCohesion)) {
			return MetricsFormat.toFormattedMetrics(this.current.getIntensity());
		} else if (metrics.equals(DependCohesion)) {
			return MetricsFormat.toFormattedMetrics(this.depend.getIntensity());
		} else if (metrics.equals(Balance)) {
			return MetricsFormat.toFormattedMetrics(this.getBalance());
		} else if (metrics.equals(Intensity)) {
			return MetricsFormat.toFormattedMetrics(this.getIntensity());
		} else if (metrics.equals(AttentionType)) {
			return this.getAttentionType();
		} else if (metrics.equals(AttentionLevel)) {
			return this.getAttentionLevel();
		} else if (metrics.equals(isProblem)) {
			return this.isNormality() ? "" : "是";
		} else {
			return null;
		}
	}

	public void init() {
		this.attentionType = this.calAttentionType();
		this.attentionLevel = this.calAttentionLevel();
	}

	/**
	 * 得到彼此依赖的对方关系
	 * 
	 * @param relation
	 * @return
	 */
	private Relation getReverseRelation() {
		for (Relation relation : this.depend.getComponent().getRelations()) {
			if (this.current.equals(relation.getDepend()) && this.depend.equals(relation.getCurrent())) {
				return relation;
			}
		}
		return null;
	}

	private Float calAttentionLevel() {
		Float attentionLevel = 0F;
		String attentiontype = getAttentionType();
		if (attentiontype.equals(MutualDependAttentionType)) {// 彼此依赖
			Relation relation = this.getReverseRelation();
			if (MathUtil.isEquals(relation.getIntensity(), this.getIntensity())) {
				attentionLevel = this.getAttentionWeight(attentiontype);
			} else {
				Float attention = 1 - this.getIntensity() / (this.getIntensity() + relation.getIntensity());
				attentionLevel = this.getAttentionWeight(attentiontype) + attention;
				if (attention > 0.5) {
					this.normality = false;
				}
			}
		} else if (attentiontype.equals(SDPAttentionType)) {// 稳定性差的组件依赖稳定性高的组件
			Float attention = this.getDepend().getComponent().getStability()
					- this.getCurrent().getComponent().getStability();
			if (attention > 0) {
				attentionLevel = this.getAttentionWeight(attentiontype) + attention;// 按自动计算的稳定性计算attentionLevel
			}
			this.normality = false;
		} else if (attentiontype.equals(ComponentLayerAttentionType)) {// 下层组件依赖了上层组件
			Float attention = 0F;
			if (this.current.getComponent().getAreaComponent() != null
					&& this.depend.getComponent().getAreaComponent() != null) {
				attention = this.getDepend().getComponent().getAreaComponent().instability()
						- this.getCurrent().getComponent().getAreaComponent().instability();
			} else {
				attention = new Float(this.getDepend().getComponent().getLayer()
						- this.getCurrent().getComponent().getLayer());
			}
			attentionLevel = this.getAttentionWeight(attentiontype) + attention;
			this.normality = false;
		} else {
			attentionLevel = 0F;
		}
		return attentionLevel;
	}

	private String calAttentionType() {
		if (this.depend.getComponent().getEfferents().contains(this.current.getComponent())) {// 检测彼此依赖
			return MutualDependAttentionType;
		} else if (this.current.getComponent().isDefinedComponentLevel()
				&& this.depend.getComponent().isDefinedComponentLevel()
				&& this.current.getComponent().getLayer() < this.depend.getComponent().getLayer()) {// 检测组件层依赖(人工指定)
			return ComponentLayerAttentionType;
		} else if (this.current.getComponent().getAreaComponent() != null
				&& this.depend.getComponent().getAreaComponent() != null
				&& this.current.getComponent().getAreaComponent().instability() < this.depend.getComponent()
						.getAreaComponent().instability()) {// 检测组件层依赖（按着AreaComponent）
			return ComponentLayerAttentionType;
		} else if (this.current.getComponent().stability(this.depend.getComponent())) {// 检测稳定依赖（按着自动计算的稳定性）
			return SDPAttentionType;
		} else if (this.current.getComponent().collectCycle().contains(this.depend.getComponent())) {// 检测循环依赖
			return CycleDependAttentionType;
		} else {
			return DefaultAttentionType;
		}
	}

	/**
	 * 将问题类型转化成问题权值
	 * 
	 * @param attentiontype
	 * @return
	 */
	public float getAttentionWeight(String attentiontype) {
		RelationProfile relationProfile = this.getResult().getRunningContext().getProfileFacade().getRelationProfile();
		return relationProfile.getProblemRelations().get(attentiontype);
	}

	public AnalysisResult getResult() {
		return this.current.getComponent().getResult();
	}

	public static Collection<Element> calElements(Collection<Relation> relations) {
		Collection<Element> elements = new ArrayList<Element>();
		for (Relation relation : relations) {
			if (!elements.contains(relation.getCurrent())) {
				elements.add(relation.getCurrent());
			}
			if (!elements.contains(relation.getDepend())) {
				elements.add(relation.getDepend());
			}
		}
		return elements;
	}

	public void clear() {
		this.attentionLevel = null;
		this.attentionType = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((current == null) ? 0 : current.hashCode());
		result = prime * result + ((depend == null) ? 0 : depend.hashCode());
		return result;
	}

	public boolean equals(String current, String depend) {
		return this.current.getName().equals(current) && this.depend.getName().equals(depend);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Relation other = (Relation) obj;
		if (current == null) {
			if (other.current != null)
				return false;
		} else if (!current.equals(other.current))
			return false;
		if (depend == null) {
			if (other.depend != null)
				return false;
		} else if (!depend.equals(other.depend))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "关系耦合值：" + MetricsFormat.toFormattedMetrics(this.getIntensity()) + " " + this.getAttentionTypeName();
	}
}
