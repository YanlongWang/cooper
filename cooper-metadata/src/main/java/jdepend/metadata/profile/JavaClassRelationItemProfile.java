package jdepend.metadata.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdepend.metadata.JavaClassRelationType;
import jdepend.metadata.relationtype.FieldRelation;
import jdepend.metadata.relationtype.HttpRelation;
import jdepend.metadata.relationtype.InheritRelation;
import jdepend.metadata.relationtype.JavaClassRelationTypes;
import jdepend.metadata.relationtype.ParamRelation;
import jdepend.metadata.relationtype.TableRelation;
import jdepend.metadata.relationtype.VariableRelation;

public class JavaClassRelationItemProfile implements Serializable {

	private static final long serialVersionUID = 6254286808982233198L;

	private Map<String, Float> types;

	private List<String> ignoreTables;// 实际忽略的表集合

	private transient JavaClassRelationTypes javaClassRelationTypes;

	public static List<String> getAllTypes() {

		List<String> allTypes = new ArrayList<String>();

		allTypes.add(JavaClassRelationTypes.Inherit);
		allTypes.add(JavaClassRelationTypes.Field);
		allTypes.add(JavaClassRelationTypes.Param);
		allTypes.add(JavaClassRelationTypes.Variable);
		allTypes.add(JavaClassRelationTypes.Table);
		allTypes.add(JavaClassRelationTypes.Http);

		return allTypes;
	}

	public Map<String, Float> getTypes() {
		return types;
	}

	public void setTypes(Map<String, Float> types) {
		this.types = types;
	}

	public Float getType(String name) {
		return this.types.get(name);
	}

	public List<String> getIgnoreTables() {
		return ignoreTables;
	}

	public void setIgnoreTables(List<String> ignoreTables) {
		this.ignoreTables = ignoreTables;
	}

	public JavaClassRelationTypes getJavaClassRelationTypes() {

		if (javaClassRelationTypes == null) {
			javaClassRelationTypes = new JavaClassRelationTypes();

			Map<String, JavaClassRelationType> types = new HashMap<String, JavaClassRelationType>();

			types.put(JavaClassRelationTypes.Inherit, new InheritRelation(this.getType(JavaClassRelationTypes.Inherit)));
			types.put(JavaClassRelationTypes.Field, new FieldRelation(this.getType(JavaClassRelationTypes.Field)));
			types.put(JavaClassRelationTypes.Param, new ParamRelation(this.getType(JavaClassRelationTypes.Param)));
			types.put(JavaClassRelationTypes.Variable,
					new VariableRelation(this.getType(JavaClassRelationTypes.Variable)));
			types.put(JavaClassRelationTypes.Table, new TableRelation(this.getType(JavaClassRelationTypes.Table)));
			types.put(JavaClassRelationTypes.Http, new HttpRelation(this.getType(JavaClassRelationTypes.Http)));

			javaClassRelationTypes.setTypes(types);
		}

		return javaClassRelationTypes;
	}

	public String getExplain() {
		StringBuilder info = new StringBuilder();

		info.append("类关系的强度与类型有关，不同的关系类型有不同的强度。类关系的强度是构成组件关系强度的依据。\n\n");
		info.append("当某一类关系类型的强度被设置为0时，系统将不采集该类关系。\n\n");
		info.append("Field包含关系是组合和聚合的关系的统称。在实际代码中很多类间的包含关系并非语义的包含关系，而是调用关系，如采用Spring注解向Service注入的其他Service这种关系。系统会识别这样的关系，并确定为Variable关系。\n\n");
		info.append("Table关系的含义是两个类共同操作了同一张数据库表。这种关系是以数据库表为手段建立的生产者消费者模式。系统能够通过多种信息（类代码或配置文件信息）识别操作数据库的具体类型（Select、Insert、Update、Delete）。\n\n");
		info.append("Http关系的含义是两个类之间存在http方式的远程调用。这种关系是通过url字符串匹配实现的，被调用方使用了RequestMapping注解，调用方采用了指定的调用者（在设置->管理参数->文件解析配置文件中配置）调用该注解配置的url。\n\n");

		return info.toString();
	}
}
