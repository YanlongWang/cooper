package jdepend.knowledge.pattern.impl2.feature.leaf;

import jdepend.knowledge.pattern.impl2.AbstractFeature;
import jdepend.knowledge.pattern.impl2.FeatureCheckContext;
import jdepend.model.Attribute;
import jdepend.model.JavaClassUnit;

public class AbstractAttributeIsSelfSuperFeature extends AbstractFeature {

	@Override
	protected boolean have(FeatureCheckContext context) {
		if (context.getAllSupers() == null || context.getAbstractAttributes() == null) {
			return false;
		} else {
			for (Attribute attribute : context.getAbstractAttributes()) {
				for (JavaClassUnit javaClass : attribute.getTypeClasses()) {
					if (context.getAllSupers().contains(javaClass)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	@Override
	public String getName() {
		return "属性中存在自己的父类类型";
	}

}
