package jdepend.report.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jdepend.framework.log.BusiLogUtil;
import jdepend.framework.log.Operation;
import jdepend.framework.ui.component.JDependFrame;
import jdepend.framework.ui.component.TableSorter;
import jdepend.framework.util.BundleUtil;
import jdepend.model.JDependUnitMgr;
import jdepend.model.JavaClassUnit;
import jdepend.util.refactor.RefactorToolFactory;

public class JavaClassMoveToDialog extends JDialog {

	private JDependFrame frame;

	private JTable componentTable;

	private Collection<JavaClassUnit> javaClasses;

	private JPanel content;

	private JavaClassMoveToDialogListener listener;

	public JavaClassMoveToDialog(JDependFrame frame, Collection<JavaClassUnit> javaClasses) {
		this(frame);
		this.javaClasses = javaClasses;
	}

	private JavaClassMoveToDialog(JDependFrame frame) {
		this.frame = frame;

		getContentPane().setLayout(new BorderLayout());
		setSize(650, 380);
		this.setLocationRelativeTo(null);// 窗口在屏幕中间显示

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		content = new JPanel(new BorderLayout());
		content.add(BorderLayout.CENTER, createComponentTable());

		JPanel buttonBar = new JPanel(new FlowLayout());
		buttonBar.add(createOKButton());
		buttonBar.add(createCloseButton());

		panel.add(BorderLayout.CENTER, content);

		panel.add(BorderLayout.SOUTH, buttonBar);

		this.add(panel);
	}

	private JComponent createComponentTable() {

		DefaultTableModel model = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};

		TableSorter sorter = new TableSorter(model);
		componentTable = new JTable(sorter);

		model.addColumn("组件名称");

		Object[] row;
		for (jdepend.model.Component unit : JDependUnitMgr.getInstance().getComponents()) {
			row = new Object[1];
			row[0] = unit.getName();
			model.addRow(row);
		}

		return new JScrollPane(componentTable);
	}

	private JButton createCloseButton() {

		JButton button = new JButton(BundleUtil.getString(BundleUtil.Command_Close));
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		return button;
	}

	private JButton createOKButton() {

		JButton button = new JButton(BundleUtil.getString(BundleUtil.Command_OK));
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				moveTo(e);
			}
		});

		return button;
	}

	private void moveTo(ActionEvent e) {

		if (this.componentTable.getSelectedRows().length != 1) {
			JOptionPane.showMessageDialog(this, "请选择一个組件.", "alert", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			String target = (String) this.componentTable.getValueAt(this.componentTable.getSelectedRows()[0], 0);
			jdepend.model.Component targetComponent = JDependUnitMgr.getInstance().getResult().getTheComponent(target);

			boolean adjust = false;
			L: for (JavaClassUnit javaClass : javaClasses) {
				if (!adjust && !targetComponent.containsClass(javaClass)) {
					adjust = true;
					break L;
				}
			}

			if (!adjust) {
				JOptionPane.showMessageDialog(this, "JavaClass所在的组件与目标组件相同，请选择其它组件.", "alert",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			RefactorToolFactory.createTool().moveClass(javaClasses, targetComponent);

			frame.onRefactoring();

			if (this.listener != null) {
				this.listener.onFinish();
			}
			// 记录日志
			BusiLogUtil.getInstance().businessLog(Operation.moveToClass);

			dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
			Component source = (Component) e.getSource();
			JOptionPane.showMessageDialog(source, ex.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void setListener(JavaClassMoveToDialogListener listener) {
		this.listener = listener;
	}
}
