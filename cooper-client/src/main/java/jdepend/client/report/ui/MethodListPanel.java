package jdepend.client.report.ui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jdepend.framework.ui.component.TableMouseMotionAdapter;
import jdepend.framework.ui.component.TableSorter;
import jdepend.framework.ui.util.JTableUtil;
import jdepend.framework.util.BundleUtil;
import jdepend.framework.util.MetricsFormat;
import jdepend.framework.util.StringUtil;
import jdepend.metadata.Method;
import jdepend.metadata.util.JavaClassUtil;
import jdepend.model.JDependUnitMgr;

public class MethodListPanel extends JPanel {

	private DefaultTableModel methodListModel;

	private JTable methodListTable;

	private Collection<Method> methods;

	public MethodListPanel(Collection<Method> methods) {
		super();
		this.setLayout(new BorderLayout());

		this.methods = methods;

		this.initMethodList();

		JScrollPane pane = new JScrollPane(methodListTable);
		pane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		this.add(pane);
	}

	public int loadMethodList() {

		methodListModel.setRowCount(0);

		Object[] row;

		for (Method method : this.methods) {
			row = new Object[14];

			row[0] = method.getJavaClass().getId();
			row[1] = method.getInfo();

			row[2] = method.getJavaClass().getName();
			row[3] = method.getAccessFlagName();
			row[4] = method.getName();
			row[5] = method.getArgumentInfo();
			row[6] = method.getReturnTypes().size() == 0 ? "" : method
					.getReturnTypes();
			row[7] = method.getSelfLineCount();
			row[8] = method.getInvokedMethods().size();
			row[9] = method.getCascadeInvokedMethods().size();
			row[10] = method.getInvokeMethods().size();
			row[11] = MetricsFormat.toFormattedMetrics(method.getStability());
			row[12] = method.isRemoteInvokeItem() ? "是" : "否";
			row[13] = method.isRemoteInvokedItem() ? "是" : "否";

			methodListModel.addRow(row);

		}

		return methodListModel.getRowCount();

	}

	protected void initMethodList() {

		methodListModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};
		
		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(this.createSaveAsItem());
		

		TableSorter sorter = new TableSorter(methodListModel);

		methodListTable = new JTable(sorter);
		methodListTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JTable table = (JTable) e.getSource();
				Point p = new Point(e.getX(), e.getY());
				int col = table.columnAtPoint(p);
				int row = table.rowAtPoint(p);
				String classId = (String) table.getValueAt(row, 0);
				String info = (String) table.getValueAt(row, 1);
				String currentCol = (String) table.getColumnModel()
						.getColumn(col).getHeaderValue();

				if (e.getClickCount() == 2) {
					Method currentMethod = JDependUnitMgr.getInstance()
							.getResult().getTheClass(classId).getJavaClass()
							.getTheMethod(info);
					if (currentMethod != null) {
						if (currentCol.equals("名称")) {
							MethodDetailDialog d = new MethodDetailDialog(
									currentMethod);
							d.setModal(true);
							d.setVisible(true);
						} else if (currentCol.equals("传入")) {
							InvokeItemListDialog d = new InvokeItemListDialog(
									currentMethod.getInvokedItems());
							d.setModal(true);
							d.setVisible(true);
						} else if (currentCol.equals("级联传入")) {
							InvokeItemListDialog d = new InvokeItemListDialog(
									currentMethod.getCascadeInvokedItems());
							d.setModal(true);
							d.setVisible(true);
						} else if (currentCol.equals("传出")) {
							InvokeItemListDialog d = new InvokeItemListDialog(
									currentMethod.getInvokeItems());
							d.setModal(true);
							d.setVisible(true);
						}
					}
				}else if (e.getButton() == 3) {
					popupMenu.show(methodListTable, e.getX(), e.getY());
				}
			}
		});

		sorter.setTableHeader(methodListTable.getTableHeader());

		methodListModel.addColumn("类标识");
		methodListModel.addColumn("方法标识");
		methodListModel.addColumn("类名");
		methodListModel.addColumn("访问修饰符");
		methodListModel.addColumn("名称");
		methodListModel.addColumn("参数");
		methodListModel.addColumn("返回值");
		methodListModel.addColumn("行数");
		methodListModel.addColumn("传入");
		methodListModel.addColumn("级联传入");
		methodListModel.addColumn("传出");
		methodListModel.addColumn("稳定性");
		methodListModel.addColumn("是否包含进程间调用");
		methodListModel.addColumn("是否被进程间调用");

		// 增加点击图标
		List<String> colNames = new ArrayList<String>();
		colNames.add("名称");
		colNames.add("传入");
		colNames.add("级联传入");
		colNames.add("传出");

		methodListTable.getColumnModel().getColumn(0).setMinWidth(0);
		methodListTable.getColumnModel().getColumn(0).setMaxWidth(0);
		methodListTable.getColumnModel().getColumn(1).setMinWidth(0);
		methodListTable.getColumnModel().getColumn(1).setMaxWidth(0);

		methodListTable.addMouseMotionListener(new TableMouseMotionAdapter(
				methodListTable, colNames));

	}
	
	protected JMenuItem createSaveAsItem() {
		JMenuItem saveAsItem = new JMenuItem(BundleUtil.getString(BundleUtil.Command_SaveAs));
		saveAsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTableUtil.exportTableToExcel(methodListTable);
			}
		});

		return saveAsItem;
	}

	public int filterMehtodList(String className, String name, String callerName) {
		methodListModel.setRowCount(0);

		this.inFilterMethodList(className, name, callerName);

		return methodListModel.getRowCount();
	}

	private void inFilterMethodList(String className, String name,
			String callerName) {
		Object[] row;
		boolean callerNameMatch;

		for (Method method : this.methods) {
			row = new Object[14];

			if ((className == null || className.length() == 0 || JavaClassUtil
					.match(className, method.getJavaClass()))
					&& (name == null || name.length() == 0 || StringUtil.match(
							name.toUpperCase(), method.getName().toUpperCase()))) {

				callerNameMatch = true;
				if (callerName != null && callerName.length() > 0) {
					callerNameMatch = false;
					for (Method invokedMehthod : method.getInvokedMethods()) {
						if (StringUtil.match(callerName.toUpperCase(),
								invokedMehthod.getPath().toUpperCase())) {
							callerNameMatch = true;
							break;
						}
					}
				}
				if(!callerNameMatch){
					continue;
				}
				row[0] = method.getJavaClass().getId();
				row[1] = method.getInfo();

				row[2] = method.getJavaClass().getName();
				row[3] = method.getAccessFlagName();
				row[4] = method.getName();
				row[5] = method.getArgumentInfo();
				row[6] = method.getReturnTypes().size() == 0 ? "" : method
						.getReturnTypes();
				row[7] = method.getSelfLineCount();
				row[8] = method.getInvokedMethods().size();
				row[9] = method.getCascadeInvokedMethods().size();
				row[10] = method.getInvokeMethods().size();
				row[11] = MetricsFormat.toFormattedMetrics(method
						.getStability());
				row[12] = method.isRemoteInvokeItem() ? "是" : "否";
				row[13] = method.isRemoteInvokedItem() ? "是" : "否";

				methodListModel.addRow(row);
			}
		}
	}
}
