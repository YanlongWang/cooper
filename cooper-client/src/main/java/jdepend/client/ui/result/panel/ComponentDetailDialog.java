package jdepend.client.ui.result.panel;

import java.awt.BorderLayout;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JScrollPane;

import jdepend.framework.ui.component.TextViewer;
import jdepend.framework.ui.dialog.CooperDialog;
import jdepend.model.Component;
import jdepend.model.JDependUnit;
import jdepend.client.report.way.textui.TextSummaryPrinter;

public final class ComponentDetailDialog extends CooperDialog {

	private String detailText = null;

	public ComponentDetailDialog(Component component) {

		super(component.getName());

		getContentPane().setLayout(new BorderLayout());

		TextViewer classProperty = new TextViewer();

		printUnit(component);

		classProperty.setText(detailText);
		classProperty.setCaretPosition(0);

		this.add(new JScrollPane(classProperty));
	}

	private void printUnit(Component unit) {

		OutputStream info = new ByteArrayOutputStream();

		TextSummaryPrinter printer = new TextSummaryPrinter();

		printer.setStream(info);

		printer.printComponent(unit);

		printer.getWriter().flush();

		detailText = info.toString();

		try {
			info.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
