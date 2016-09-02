package jdepend.knowledge.pattern.impl1;

import java.util.ArrayList;
import java.util.Collection;

import jdepend.knowledge.pattern.PatternInfo;
import jdepend.metadata.JavaClass;
import jdepend.metadata.Method;
import jdepend.model.JavaClassUnit;

public final class StateIdentifyer extends AbstractPatternIdentifyer {

	@Override
	public String getExplain() {
		StringBuilder explain = new StringBuilder();
		explain.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>状态模式</strong><br>");
		explain
				.append("&nbsp;&nbsp;&nbsp;&nbsp;1、本身是接口或抽象类；2、存在多个子类；3、子类覆盖了父类方法；4、父类方法是抽象方法；5、该方法返回值是父类；6、父类是有状态的。<br><br>");
		return explain.toString();
	}

	@Override
	public Collection<PatternInfo> identify(Collection<JavaClassUnit> javaClasses) {
		Collection<PatternInfo> rtn = new ArrayList<PatternInfo>();
		for (JavaClassUnit javaClass : javaClasses) {
			if (javaClass.getJavaClass().isAbstract() && javaClass.getJavaClass().isState()) {
				Collection<JavaClass> subClasses = javaClass.getJavaClass().getSubClasses();
				if (subClasses.size() > 1) {
					L: for (JavaClass subClass : subClasses) {
						for (Method method : subClass.getSelfMethods()) {
							if (!method.isConstruction() && method.getReturnTypes().size() == 1
									&& method.getReturnClassTypes().size() == 1
									&& method.getReturnClassTypes().iterator().next().equals(javaClass)) {
								for (Method superMethod : javaClass.getJavaClass().getSelfMethods()) {
									if (superMethod.isAbstract() && method.isOverride(superMethod)) {
										rtn
												.add(new PatternInfo(javaClass.getJavaClass(), javaClass.getName() + "."
														+ method.getName()));
										break L;
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
