package jdepend.report.way.textui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jdepend.framework.log.LogUtil;
import jdepend.framework.util.MetricsFormat;
import jdepend.model.GroupCohesionItem;
import jdepend.model.JDependUnit;
import jdepend.model.JavaClassUnit;
import jdepend.model.JavaClassRelationItem;
import jdepend.model.MetricsMgr;
import jdepend.model.Relation;
import jdepend.model.SubJDependUnit;
import jdepend.model.result.AnalysisResult;
import jdepend.model.util.JDependUnitByMetricsComparator;
import jdepend.report.filter.CouplingReportFilter;
import jdepend.report.filter.RelationFilter;
import jdepend.report.filter.ReportFilter;
import jdepend.report.util.ReportConstant;
import jdepend.report.util.ReportUtil;
import jdepend.report.way.xmlui.XMLSummaryPrinter;

/**
 * 报告输出器
 * 
 * @author <b>Abner</b>
 * 
 */
public final class JDependPrinter extends Printer {

	public final static String Split = "~";

	public final static String Start = "@";

	private ReportFilter filter = new ReportFilter();

	private RelationFilter relationFilter = new RelationFilter();

	private CouplingReportFilter couplingFilter = new CouplingReportFilter();

	protected void printSplit(String name) {
		this.getWriter().print(Split);
		this.getWriter().print(Start);
		this.getWriter().print(name);
		this.getWriter().print(Split);

		getWriter().flush();

	}

	public void print(AnalysisResult result, Set<String> items) {

		LogUtil.getInstance(JDependPrinter.class).systemLog("create text report start!");

		if (items.contains(ReportConstant.SummaryText)) {
			printSplit(ReportConstant.SummaryText);
			printTextBasic(result);
			LogUtil.getInstance(JDependPrinter.class).systemLog("SummaryText report finish!");
		}

		if (items.contains(ReportConstant.SummaryXML)) {
			printSplit(ReportConstant.SummaryXML);
			printXMLBasic(result);
			LogUtil.getInstance(JDependPrinter.class).systemLog("SummaryXML report finish!");
		}

		if (items.contains(ReportConstant.RelationText)) {
			printSplit(ReportConstant.RelationText);
			printRelations(result.getRelations());
			LogUtil.getInstance(JDependPrinter.class).systemLog("RelationText report finish!");
		}

		if (items.contains(ReportConstant.CouplingText)) {
			printSplit(ReportConstant.CouplingText);
			printCouplings(result.getComponents());
			LogUtil.getInstance(JDependPrinter.class).systemLog("CouplingText report finish!");
		}
		if (items.contains(ReportConstant.CohesionText)) {
			printSplit(ReportConstant.CohesionText);
			printCohesions(result.getComponents());
			LogUtil.getInstance(JDependPrinter.class).systemLog("CohesionText report finish!");
		}

		getWriter().flush();

		LogUtil.getInstance(JDependPrinter.class).systemLog("create text report finish!");
	}

	protected void printTextBasic(AnalysisResult result) {

		TextSummaryPrinter textSummaryPrinter = new TextSummaryPrinter();
		textSummaryPrinter.setStream(getStream());

		textSummaryPrinter.printBasic(result);

	}

	protected void printXMLBasic(AnalysisResult result) {

		XMLSummaryPrinter xmlSummaryPrinter = new XMLSummaryPrinter();
		xmlSummaryPrinter.setStream(getStream());

		xmlSummaryPrinter.printBasic(result);
	}

	public void printCouplings(Collection<? extends JDependUnit> units1) {

		getWriter().println("<?xml version=\"1.0\"?>");
		getWriter().println("<Components>");

		List<JDependUnit> units = new ArrayList<JDependUnit>(units1);

		Collections.sort(units, new JDependUnitByMetricsComparator(MetricsMgr.Coupling, false));
		for (JDependUnit unit : units) {
			printCoupling(unit);
		}

		getWriter().println("</Components>");
	}

	private void printCoupling(JDependUnit unit) {

		Float ceCoupling = 0F;
		Float caCoupling = 0F;
		Float coupling = 0F;

		coupling = unit.getCoupling();
		ceCoupling = unit.ceCoupling();
		caCoupling = unit.caCoupling();

		getWriter().println(tab() + "<Component name=\"" + unit.getName() + "\">");
		// 输出摘要信息
		if (couplingFilter.isComponentSummary()) {
			getWriter().println(tab(2) + "<Ce>"

			+ MetricsFormat.toFormattedMetrics(ceCoupling) + "</Ce>");
			getWriter().println(tab(2) + "<Ca>" + MetricsFormat.toFormattedMetrics(caCoupling) + "</Ca>");

			getWriter().println(tab(2) + "<Coupling>" + MetricsFormat.toFormattedMetrics(coupling) + "</Coupling>");
		}
		// 输出类信息
		if (couplingFilter.isClassInfo()) {
			printJavaClassCouplingInfo(unit);
		}

		getWriter().println(tab() + "</Component>");

	}

	private void printJavaClassCouplingInfo(JDependUnit unit) {

		if (unit.getClasses().size() == 0) {
			return;
		}

		List<JavaClassUnit> javaClasses = new ArrayList<JavaClassUnit>(unit.getClasses());
		Collections.sort(javaClasses, new JDependUnitByMetricsComparator(MetricsMgr.Coupling, false));
		Float coupling = 0.0F;

		for (JavaClassUnit javaClass : javaClasses) {
			coupling = javaClass.getCoupling();
			if (couplingFilter.isInnerClass() && coupling.equals(0.0F) || couplingFilter.isExpertClass()
					&& !coupling.equals(0.0F)) {
				getWriter().println(
						tab(3) + "<JavaClass name=\"" + javaClass.getName() + "\" CeCoupling=\""
								+ MetricsFormat.toFormattedMetrics(javaClass.ceCoupling()) + "\" CaCoupling=\""
								+ MetricsFormat.toFormattedMetrics(javaClass.caCoupling()) + "\" Coupling=\""
								+ MetricsFormat.toFormattedMetrics(coupling) + "\">");
			}
			// 打印细节
			if (couplingFilter.isClassDetail()) {
				this.printJavaClassCouplingInfoDetail(javaClass);
			}
			getWriter().println(tab(3) + "</JavaClass>");

		}
	}

	private void printJavaClassCouplingInfoDetail(JavaClassUnit javaClass) {

		getWriter().println(tab(4) + "<Ce>");
		for (JavaClassRelationItem ceItem : javaClass.getJavaClass().getCeItems()) {
			getWriter().println(
					tab(5) + "<CouplingJavaClass name=\"" + ceItem.getTarget().getName() + "\" DependType=\""
							+ ceItem.getType().getName() + "\" Intensity=\""
							+ MetricsFormat.toFormattedMetrics(ReportUtil.calCouplingIntensity(ceItem)) + "\"/>");
		}
		getWriter().println(tab(4) + "</Ce>");
		getWriter().println(tab(4) + "<Ca>");
		for (JavaClassRelationItem caItem : javaClass.getJavaClass().getCaItems()) {
			getWriter().println(
					tab(5) + "<CouplingJavaClass name=\"" + caItem.getSource().getName() + "\" DependType=\""
							+ caItem.getType().getName() + "\" Intensity=\""
							+ MetricsFormat.toFormattedMetrics(ReportUtil.calCouplingIntensity(caItem)) + "\"/>");
		}
		getWriter().println(tab(4) + "</Ca>");

	}

	public void printCohesions(Collection<? extends JDependUnit> units1) {

		getWriter().println("<?xml version=\"1.0\"?>");
		getWriter().println("<Components>");

		List<JDependUnit> units = new ArrayList<JDependUnit>(units1);

		Collections.sort(units, new JDependUnitByMetricsComparator(MetricsMgr.Cohesion));
		for (JDependUnit unit : units) {
			printCohesion(unit);
		}

		getWriter().println("</Components>");
	}

	public float printCohesion(JDependUnit unit) {

		Float cohesion = 0F;
		cohesion = unit.getCohesion();

		getWriter().println(tab() + "<Component name=\"" + unit.getName() + "\">");

		getWriter().println(tab(2) + "<Cohesion>" + MetricsFormat.toFormattedMetrics(cohesion) + "</Cohesion>");

		if (filter.isClassInfo()) {
			printJavaClassCohesionInfo(unit);
		}

		getWriter().println(tab() + "</Component>");

		return cohesion;
	}

	private void printJavaClassCohesionInfo(JDependUnit unit) {

		if (unit.getClasses().size() == 0) {
			return;
		}

		List<JavaClassUnit> javaClasses = new ArrayList<JavaClassUnit>(unit.getClasses());
		Collections.sort(javaClasses, new JDependUnitByMetricsComparator(MetricsMgr.Cohesion));

		for (JavaClassUnit javaClass : javaClasses) {
			getWriter().println(
					tab(3) + "<JavaClass name=\"" + javaClass.getName() + "\" Cohesion=\""
							+ MetricsFormat.toFormattedMetrics(javaClass.getCohesion()) + "\">");
			// 打印细节
			if (filter.isClassDetail())
				this.printJavaClassCohesionInfoDetail(javaClass);

			getWriter().println(tab(3) + "</JavaClass>");

		}
	}

	private void printJavaClassCohesionInfoDetail(JavaClassUnit javaClass) {

		getWriter().println(tab(4) + "<Ce>");
		for (JavaClassRelationItem ceItem : javaClass.getJavaClass().getCeItems()) {
			getWriter().println(
					tab(5) + "<CohesionjavaClass name=\"" + ceItem.getTarget().getName() + "\" DependType=\""
							+ ceItem.getType().getName() + "\" Intensity=\""
							+ MetricsFormat.toFormattedMetrics(ReportUtil.calCohesionIntensity(ceItem)) + "\"/>");
		}
		getWriter().println(tab(4) + "</Ce>");
		getWriter().println(tab(4) + "<Ca>");
		for (JavaClassRelationItem caItem : javaClass.getJavaClass().getCaItems()) {
			getWriter().println(
					tab(5) + "<CohesionjavaClass name=\"" + caItem.getSource().getName() + "\" DependType=\""
							+ caItem.getType().getName() + "\" Intensity=\""
							+ MetricsFormat.toFormattedMetrics(ReportUtil.calCohesionIntensity(caItem)) + "\"/>");
		}
		getWriter().println(tab(4) + "</Ca>");

	}

	public float printSubJDependUnitCohesion(SubJDependUnit unit) {

		Float cohesion = 0F;
		cohesion = unit.getCohesion();

		getWriter().println(tab() + "<SubJDependUnit name=\"" + unit.getName() + "\">");

		getWriter().println(tab(2) + "<Cohesion>" + MetricsFormat.toFormattedMetrics(cohesion) + "</Cohesion>");

		printGroupCohesionItem(unit.getGroupCohesionInfo().getGroupCohesionItems());

		getWriter().println(tab() + "</SubJDependUnit>");

		return cohesion;
	}

	private void printGroupCohesionItem(List<GroupCohesionItem> items) {

		for (GroupCohesionItem item : items) {
			getWriter().println(
					tab(3) + "<SubJDependUnit name=\"" + item.getName() + "\" Cohesion=\""
							+ MetricsFormat.toFormattedMetrics(item.getCohesion()) + "\">");
			// 打印细节
			this.printJavaClassRelationItems(item.getJavaClassRelationItems());

			getWriter().println(tab(3) + "</SubJDependUnit>");

		}
	}

	private void printJavaClassRelationItems(Collection<JavaClassRelationItem> javaClassRelationItems) {

		for (JavaClassRelationItem item : javaClassRelationItems) {
			getWriter().println(
					tab(4) + "<CohesionjavaClass source=\"" + item.getSource().getName() + "\" target=\""
							+ item.getTarget().getName() + "\" DependType=\"" + item.getType().getName()
							+ "\" Intensity=\"" + MetricsFormat.toFormattedMetrics(item.getRelationIntensity())
							+ "\"/>");
		}
	}

	protected void printRelations(Collection<Relation> relations) {

		getWriter().println("Current Element	Depend Element	Relation	Current	Depend	RelationBalance");
		float balance = 0F;
		for (Relation relation : relations) {
			balance += relation.getBalance();
			getWriter().println("\n" + relation);

			if (relationFilter.isRelationDetail()) {
				this.printRelationDetail(relation);
			}
		}
		getWriter().println("Relations Balance : " + balance);

	}

	protected void printRelationDetail(Relation relation) {

		if (!relationFilter.isTheRelationDetail(relation))
			return;

		Collection<JavaClassRelationItem> items = relation.getItems();
		if (items != null && items.size() != 0) {
			for (JavaClassRelationItem item : items) {
				getWriter().println(
						"\n #javaClassName: " + item.getSource().getName() + " #DependJavaClassName: "
								+ item.getTarget().getName() + " #DependType: " + item.getType().getName()
								+ " #Intensity: " + item.getRelationIntensity());
			}
		}
	}

	protected String tab() {
		return "    ";
	}

	protected String tab(int n) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < n; i++) {
			s.append(tab());
		}

		return s.toString();
	}
}
