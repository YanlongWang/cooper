package jdepend.model.component;

import jdepend.model.GroupInfoCalculator;
import jdepend.model.JavaClass;
import jdepend.model.JavaClassUnit;
import jdepend.model.JavaPackage;
import jdepend.model.util.JavaClassUnitUtil;

/**
 * 以包为单位用于计算组件内聚性的二级元素
 * 
 * @author user
 * 
 */
public class PackageSubJDependUnit extends VirtualComponent {

	public PackageSubJDependUnit(JavaPackage javaPackage) {
		super(javaPackage.getName());

		for (JavaClass javaClass : javaPackage.getClasses()) {
			JavaClassUnit javaClassUnit = JavaClassUnitUtil.getJavaClassUnit(javaClass);
			this.joinJavaClass(javaClassUnit);
			if (this.getResult() == null) {
				this.setResult(javaClassUnit.getResult());
			}
		}
	}

	public JavaPackage getJavaPackage() {
		return this.getJavaPackages().iterator().next();
	}

	@Override
	public float getCohesion() {
		return this.getGroupCohesionInfo().getCohesion();
	}

	@Override
	public float getBalance() {
		return this.getGroupInfoCalculator().getBalance();
	}

	@Override
	protected GroupInfoCalculator createGroupInfoCalculator() {
		return new GroupInfoCalculator(this);
	}
}
