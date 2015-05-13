package jdepend.knowledge.pattern.impl1;

import java.util.ArrayList;
import java.util.Collection;

import jdepend.knowledge.pattern.PatternInfo;
import jdepend.metadata.Attribute;
import jdepend.metadata.InvokeItem;
import jdepend.metadata.JavaClass;
import jdepend.metadata.Method;
import jdepend.model.JavaClassUnit;

public final class DecoratorIdentifyer extends AbstractPatternIdentifyer {

	@Override
	public String getExplain() {
		StringBuilder explain = new StringBuilder();
		explain.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>装饰模式</strong><br>");
		explain.append("&nbsp;&nbsp;&nbsp;&nbsp;1、存在父类（可能不止一层）；2、属性有父类类型，并且不是集合类型；3、覆盖了父类方法；4、父类还有其他子类；5、在覆盖的方法中调用了该属性的覆盖方法；6、存在父类类型作为参数的构造函数<br><br>");
		return explain.toString();
	}

	@Override
	public Collection<PatternInfo> identify(Collection<JavaClassUnit> javaClasses) {
		Collection<JavaClass> superClasses;
		Collection<PatternInfo> rtn = new ArrayList<PatternInfo>();
		PatternInfo rtnItem;
		boolean otherSubClass;
		boolean constructionArg;
		for (JavaClassUnit javaClass : javaClasses) {
			// 计算存在父类的JavaClasses
			superClasses = javaClass.getJavaClass().getSupers();
			if (superClasses != null && superClasses.size() > 0) {
				// 存在其他子类
				otherSubClass = false;
				M: for (JavaClass superClass : superClasses) {
					if (!superClass.getSubClasses().contains(javaClass)) {
						otherSubClass = true;
						break M;
					}
				}
				if (otherSubClass) {
					// 父类类型作为参数的构造函数
					constructionArg = false;
					N: for (Method method : javaClass.getJavaClass().getSelfMethods()) {
						if (method.isConstruction()) {
							for (JavaClass argClass : method.getArgClassTypes()) {
								if (superClasses.contains(argClass)) {
									constructionArg = true;
									break N;
								}
							}
						}
					}
					if (constructionArg) {
						L: for (Attribute attribute : javaClass.getJavaClass().getAttributes()) {
							if (!attribute.existCollectionType()) {
								for (JavaClass superClass : superClasses) {
									if (attribute.getTypes().contains(superClass.getName())) {
										for (Method method : javaClass.getJavaClass().getOverrideMethods()) {
											for (Method superMethod : javaClass.getJavaClass().getOverridedMethods(
													method)) {
												for (InvokeItem item : method.getInvokeItems()) {
													if (item.getCallee().equals(superMethod)
															&& method.getReadFields().contains(attribute)) {
														rtnItem = new PatternInfo(javaClass.getJavaClass(), javaClass.getName() + "."
																+ item.getCallee().getName());
														if (!rtn.contains(rtnItem)) {
															rtn.add(rtnItem);
														}
														break L;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return rtn;
	}
}
