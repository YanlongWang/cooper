package jdepend.client.report.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jdepend.framework.ui.component.JDependFrame;
import jdepend.framework.ui.component.TableMouseMotionAdapter;
import jdepend.framework.ui.component.TableSorter;
import jdepend.framework.ui.dialog.CooperDialog;
import jdepend.framework.ui.util.JTableUtil;
import jdepend.model.JDependUnit;
import jdepend.model.MetricsMgr;
import jdepend.client.report.util.ReportConstant;

public final class JDependUnitCaCeListDialog extends CooperDialog {

	private JDependFrame frame;

	private JDependUnit component;

	private String metrics;

	private JTable listTable;

	private DefaultTableModel listModel;

	private Collection<? extends JDependUnit> listData;

	public JDependUnitCaCeListDialog(JDependFrame frame, JDependUnit component, String metrics) {

		super(component.getName() + " " + metrics + " list");

		this.frame = frame;

		this.component = component;
		this.metrics = metrics;

		initList();

		showList();

		List<String> fitColNames = new ArrayList<String>();
		fitColNames.add(ReportConstant.Name);
		JTableUtil.fitTableColumns(listTable, fitColNames);

		this.add(new JScrollPane(listTable));
	}

	public void showList() {
		listModel.setRowCount(0);
		this.loadList();
	}

	public void loadList() {

		Object[] row;

		listData = new ArrayList<JDependUnit>();

		if (metrics.equals(ReportConstant.Ca)) {
			listData = this.component.getAfferents();
		} else if (metrics.equals(ReportConstant.Ce)) {
			listData = this.component.getEfferents();
		}
		for (JDependUnit unit : listData) {
			row = new Object[12];
			row[0] = unit.getName();
			row[1] = unit.getLineCount();
			row[2] = unit.getClassCount();
			row[3] = unit.getAbstractClassCount();
			row[4] = unit.getAfferentCoupling();
			row[5] = unit.getEfferentCoupling();
			row[6] = unit.getValue(MetricsMgr.A);
			row[7] = unit.getValue(MetricsMgr.I);
			row[8] = unit.getValue(MetricsMgr.D);
			row[9] = unit.getValue(MetricsMgr.Coupling);
			row[10] = unit.getValue(MetricsMgr.Cohesion);
			row[11] = unit.getValue(MetricsMgr.Balance);

			listModel.addRow(row);
		}
	}

	private void initList() {

		listModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};

		TableSorter sorter = new TableSorter(listModel);

		listTable = new JTable(sorter);

		sorter.setTableHeader(listTable.getTableHeader());

		listModel.addColumn(ReportConstant.Name);
		listModel.addColumn(ReportConstant.LC);
		listModel.addColumn(ReportConstant.CC);
		listModel.addColumn(ReportConstant.AC);
		listModel.addColumn(ReportConstant.Ca);
		listModel.addColumn(ReportConstant.Ce);
		listModel.addColumn(ReportConstant.A);
		listModel.addColumn(ReportConstant.I);
		listModel.addColumn(ReportConstant.D);
		listModel.addColumn(ReportConstant.Coupling);
		listModel.addColumn(ReportConstant.Cohesion);
		listModel.addColumn(ReportConstant.Balance);

		listTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable table = (JTable) e.getSource();
					String current = (String) table.getValueAt(table.rowAtPoint(e.getPoint()), 0);
					String currentCol = (String) table.getColumnModel().getColumn(table.columnAtPoint(e.getPoint()))
							.getHeaderValue();
					JDependUnit currentComponent = getTheComponent(current);
					if (currentCol.equals(ReportConstant.Name)) {
						JDependUnit left = null;
						JDependUnit right = null;
						if (metrics.equals(ReportConstant.Ca)) {
							left = currentComponent;
							right = component;
						} else if (metrics.equals(ReportConstant.Ce)) {
							left = component;
							right = currentComponent;
						}
						RelationDetailPanel relationDetailPanel = new RelationDetailPanel(frame, left, right);
						JDependUnitCaCeListDialog.this.getContentPane().removeAll();
						JDependUnitCaCeListDialog.this.getContentPane().add(BorderLayout.CENTER, relationDetailPanel);
						FlowLayout buttonFlowLayout = new FlowLayout();
						buttonFlowLayout.setAlignment(FlowLayout.RIGHT);
						JPanel buttonBar = new JPanel(buttonFlowLayout);
						buttonBar.add(createBackButton());
						JDependUnitCaCeListDialog.this.add(BorderLayout.SOUTH, buttonBar);

						JDependUnitCaCeListDialog.this.setVisible(true);
					}
				}
			}
		});

		List<String> detailColumnNames = new ArrayList<String>();
		detailColumnNames.add(ReportConstant.Name);

		listTable.addMouseMotionListener(new TableMouseMotionAdapter(listTable, detailColumnNames));
	}

	private JDependUnit getTheComponent(String name) {
		for (JDependUnit component : this.listData) {
			if (component.getName().equals(name)) {
				return component;
			}
		}
		return null;
	}

	private JLabel createBackButton() {

		JLabel button = new JLabel("<html><a href='#'>返回</a></html>");
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JDependUnitCaCeListDialog.this.getContentPane().removeAll();
				JDependUnitCaCeListDialog.this.getContentPane().add(BorderLayout.CENTER, new JScrollPane(listTable));
				JDependUnitCaCeListDialog.this.setVisible(true);
			}
		});

		return button;
	}
}
