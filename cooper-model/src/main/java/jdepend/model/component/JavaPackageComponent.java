package jdepend.model.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jdepend.framework.exception.JDependException;
import jdepend.model.Component;
import jdepend.model.JavaClassUnit;
import jdepend.model.JavaPackage;

/**
 * 将基本JavaPackage视为Component
 * 
 * @author <b>Abner</b>
 * 
 */
public final class JavaPackageComponent extends Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5194861862987562929L;

	public JavaPackageComponent() {
	}

	public JavaPackageComponent(String name) {
		super(name);
	}

	@Override
	protected List<Component> doList(Collection<JavaPackage> javaPackages) throws JDependException {

		List<Component> components = new ArrayList<Component>();
		Component component = null;

		for (JavaPackage javaPackage : javaPackages) {
			component = new JavaPackageComponent(javaPackage.getName());
			component.setTitle(javaPackage.getName());
			for (JavaClassUnit javaClass : javaPackage.getClasses()) {
				component.addJavaClass(javaClass);
			}
			components.add(component);
		}

		return components;
	}
}
