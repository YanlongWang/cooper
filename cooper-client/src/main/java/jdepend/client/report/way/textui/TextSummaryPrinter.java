package jdepend.client.report.way.textui;

import java.util.Iterator;

import jdepend.client.report.util.ReportConstant;
import jdepend.core.domain.WisdomAnalysisResult;
import jdepend.framework.exception.JDependException;
import jdepend.framework.util.MetricsFormat;
import jdepend.metadata.Named;
import jdepend.model.Component;
import jdepend.model.JDependUnit;
import jdepend.model.JavaClassUnit;
import jdepend.model.MetricsMgr;
import jdepend.model.result.AnalysisResult;

public final class TextSummaryPrinter extends SummaryPrinter {

	public TextSummaryPrinter() {
		super();
	}

	@Override
	protected void printHeader(AnalysisResult result) {
		getWriter().println(result);

		// 建议
		try {
			StringBuilder advise = new WisdomAnalysisResult(result).getAdvise();
			if (advise != null && advise.length() > 0) {
				getWriter().println(advise);
			}
		} catch (JDependException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void printNoStats() {
		getWriter().println("No stats available: JDependUnit referenced, but not analyzed.");
	}

	@Override
	protected void printComponentHeader(JDependUnit unit) {
		getWriter().println("\n--------------------------------------------------");
		getWriter().println("# UnitName: " + unit.getName());
		if (unit instanceof Component) {
			Component component = (Component) unit;
			if (component.isDefinedComponentLevel()) {
				getWriter().println("# Layer: " + component.getLayerDesc());
			}
			if (component.getAreaComponent() != null) {
				getWriter().println("# AreaComponent: " + component.getAreaComponent().getName());
			}
		}
		getWriter().println("--------------------------------------------------");
	}

	@Override
	protected void printStatistics(JDependUnit unit) {
		getWriter().println("\nStats:");
		getWriter().println(tab() + "isInner: " + unit.isInner());
		getWriter().println(tab() + "TotalClasses: " + unit.getClassCount());
		getWriter().println(tab() + "ConcreteClasses: " + unit.getConcreteClassCount());
		getWriter().println(tab() + "AbstractClasses: " + unit.getAbstractClassCount());
		getWriter().println(tab() + "TotalLineCount: " + unit.getLineCount());
		getWriter().println("");
		getWriter().println(tab() + "Ca: " + unit.getAfferentCoupling());
		getWriter().println(tab() + "Ce: " + unit.getEfferentCoupling());
		getWriter().println("");
		getWriter().println(tab() + "A: " + MetricsFormat.toFormattedMetrics(unit.getAbstractness()));
		getWriter().println(tab() + "I: " + MetricsFormat.toFormattedMetrics(unit.getStability()));
		getWriter().println(tab() + "D: " + MetricsFormat.toFormattedMetrics(unit.getDistance()));
		getWriter().println(tab() + "Coupling: " + MetricsFormat.toFormattedMetrics(unit.getCoupling()));
		getWriter().println(tab() + "Cohesion: " + MetricsFormat.toFormattedMetrics(unit.getCohesion()));
		getWriter().println(tab() + "Balance: " + MetricsFormat.toFormattedMetrics(unit.getBalance()));
	}

	@Override
	protected void printClassName(JavaClassUnit jClass) {
		getWriter().println(tab() + jClass.getName());
	}

	@Override
	protected void printPackageName(JDependUnit unit) {
		getWriter().println(tab() + unit.getName());
	}

	@Override
	protected void printAbstractClassesHeader() {
		getWriter().println("AbstractClasses:");
	}

	@Override
	protected void printConcreteClassesHeader() {
		getWriter().println("ConcreteClasses:");
	}

	@Override
	protected void printEfferentsHeader() {
		getWriter().println("DependsUpon:");
	}

	@Override
	protected void printEfferentsError() {
		getWriter().println(tab() + "Not dependent on any JDependUnits.");
	}

	@Override
	protected void printAfferentsHeader() {
		getWriter().println("UsedBy:");
	}

	@Override
	protected void printAfferentsError() {
		getWriter().println(tab() + "Not used by any JDependUnits.");
	}

	@Override
	protected void printCyclesHeader() {
		printSectionBreak();
		getWriter().println("\n--------------------------------------------------");
		getWriter().println("# DependencyCycles:");
		getWriter().println("--------------------------------------------------\n");
	}

	@Override
	protected void printCycleHeader(Named unit) {
		getWriter().println(unit.getName());
		getWriter().println(tab() + "|");
	}

	@Override
	protected void printCycleTarget(Named unit) {
		getWriter().println(tab() + "|-> " + unit.getName());
	}

	@Override
	protected void printCycleContributor(Named unit) {
		getWriter().println(tab() + "|   " + unit.getName());
	}

	@Override
	protected void printCycleFooter() {
		printSectionBreak();
	}

	@Override
	protected void printSummary(AnalysisResult inputData) {
		getWriter().println("\n--------------------------------------------------");
		getWriter().println("# Summary:");
		getWriter().println("--------------------------------------------------\n");

		int maxNameLength = 0;
		for (JDependUnit unit : inputData.getComponents()) {
			if (maxNameLength < unit.getName().length()) {
				maxNameLength = unit.getName().length();
			}
		}

		getWriter().print(ReportConstant.Name + this.calBlank(ReportConstant.Name.length(), maxNameLength) + "	");
		getWriter().print(ReportConstant.LC + "	");
		getWriter().print(ReportConstant.CN + "	");
		getWriter().print(ReportConstant.CC + "	");
		getWriter().print(ReportConstant.AC + "	");
		getWriter().print(ReportConstant.Ca + "	");
		getWriter().print(ReportConstant.Ce + "	");
		getWriter().print(ReportConstant.A + "	");
		getWriter().print(ReportConstant.I + "	");
		getWriter().print(ReportConstant.D + "	");
		getWriter().print(ReportConstant.Coupling + "	");
		getWriter().print(ReportConstant.Cohesion + "	");
		getWriter().print(ReportConstant.Balance + "	");
		getWriter().print(ReportConstant.Cycle);

		getWriter().println(":\n");

		Object[][] data = new Object[inputData.getComponents().size()][15];
		Object[] row = new Object[15];
		int rowCount = 0;

		Iterator<Component> i = inputData.getComponents().iterator();
		while (i.hasNext()) {
			JDependUnit unit = i.next();
			row[0] = unit.getName();
			row[1] = unit.getLineCount();
			row[2] = unit.getClassCount();
			row[3] = unit.getAbstractClassCount();
			row[4] = unit.getConcreteClassCount();
			row[5] = unit.getAfferentCoupling();
			row[6] = unit.getEfferentCoupling();
			row[7] = unit.getAbstractness();
			row[8] = unit.getStability();
			row[9] = unit.getDistance();
			row[10] = unit.getCoupling();
			row[11] = unit.getCohesion();
			row[12] = unit.getBalance();
			row[13] = 0F;
			if (unit.getContainsCycle()) {
				row[14] = MetricsMgr.Cyclic;
			} else {
				row[14] = MetricsMgr.NoValue;
			}
			for (int col = 0; col < row.length; col++) {
				data[rowCount][col] = row[col];
				if (col == 0) {
					getWriter().print(row[col] + this.calBlank(((String) row[col]).length(), maxNameLength) + "	");
				} else if (row[col] instanceof Float) {
					getWriter().print(MetricsFormat.toFormattedMetrics((Float) row[col]) + "	");
				} else {
					getWriter().print(row[col] + "	");
				}

			}
			rowCount++;
			getWriter().print("\n");
		}

		getWriter().print(
				inputData.getSummary().getName()
						+ this.calBlank(inputData.getSummary().getName().length(), maxNameLength) + "	");
		getWriter().print(inputData.getSummary().getLineCount() + "	");
		getWriter().print(inputData.getSummary().getClassCount() + "	");
		getWriter().print(inputData.getSummary().getConcreteClassCount() + "	");
		getWriter().print(inputData.getSummary().getAbstractClassCount() + "	");
		getWriter().print(inputData.getSummary().getAbstractClassCount() + "	");
		getWriter().print(inputData.getSummary().getEfferentCoupling() + "	");
		getWriter().print(MetricsFormat.toFormattedMetrics(inputData.getSummary().getAbstractness()) + "	");
		getWriter().print(MetricsFormat.toFormattedMetrics(inputData.getSummary().getStability()) + "	");
		getWriter().print(MetricsFormat.toFormattedMetrics(inputData.getSummary().getDistance()) + "	");
		getWriter().print(MetricsFormat.toFormattedMetrics(inputData.getSummary().getCoupling()) + "	");
		getWriter().print(MetricsFormat.toFormattedMetrics(inputData.getSummary().getCohesion()) + "	");
		getWriter().print(MetricsFormat.toFormattedMetrics(inputData.getSummary().getBalance()) + "	");
		getWriter().print("\n");
	}

	private String calBlank(int currentLength, int maxLength) {
		StringBuilder blank = new StringBuilder();
		for (int i = currentLength; i < maxLength; i++) {
			blank.append(" ");
		}
		return blank.toString();
	}

	protected void printSectionBreak() {
		getWriter().println("");
	}

}
