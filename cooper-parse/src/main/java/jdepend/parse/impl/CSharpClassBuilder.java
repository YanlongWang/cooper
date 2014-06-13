package jdepend.parse.impl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jdepend.model.JavaClass;
import jdepend.parse.ParseConfigurator;
import jdepend.parse.ParseData;
import jdepend.parse.ParseListener;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

public class CSharpClassBuilder extends AbstractClassBuilder {

	private PackageFilter filter;

	private int countClasses = -1;

	private List<JavaClass> classes;

	public CSharpClassBuilder(ParseConfigurator conf) {
		this.setConf(conf);
		this.filter = new PackageFilter(conf.getFilteredPackages(), conf.getNotFilteredPackages());
	}

	@Override
	public List<JavaClass> build(ParseData data) {
		if (this.classes == null || this.getConf().getEveryClassBuild()) {
			classes = new ArrayList<JavaClass>();

			// 调用C#代码
			ActiveXComponent builder = new ActiveXComponent("parse.Builder");
			this.classes = (List<JavaClass>) Dispatch.call(builder, "build", new Object[] { classes }).toJavaObject();

			// (new JavaClassRelationCreator(this.getConf())).create(classes);

			this.onClassBuild(classes);
		}
		return this.classes;
	}

	@Override
	public PackageFilter getFilter() {
		return this.filter;
	}

	@Override
	public void addParseListener(ParseListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWriter(PrintWriter writer) {
		// TODO Auto-generated method stub

	}

}
