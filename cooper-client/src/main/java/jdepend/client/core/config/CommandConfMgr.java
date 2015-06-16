package jdepend.client.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdepend.framework.log.BusiLogUtil;
import jdepend.framework.log.Operation;
import jdepend.model.component.modelconf.ComponentModelConf;

/**
 * 命令管理器
 * 
 * 管理CommandAdapter集合
 * 
 * @author <b>Abner</b>
 * 
 */
public final class CommandConfMgr {

	private static CommandConfMgr mgr;

	private List<GroupConf> groups;

	private transient List<GroupConfChangeListener> listeners = new ArrayList<GroupConfChangeListener>();

	private CommandConfMgr() throws CommandConfException {
		this.refresh();
	}

	public synchronized void refresh() throws CommandConfException {
		groups = GroupConf.init();

		for (GroupConfChangeListener listener : this.listeners) {
			listener.onRefresh();
		}
	}

	public static CommandConfMgr getInstance() throws CommandConfException {
		if (mgr == null) {
			mgr = new CommandConfMgr();
		}
		return mgr;
	}

	public synchronized Collection<String> getGroupNames() {
		List<String> names = new ArrayList<String>();
		for (GroupConf group : groups) {
			names.add(group.getName());
		}
		return names;
	}

	public synchronized GroupConf getTheGroup(String group) {

		for (GroupConf info : groups) {
			if (info.getName().equals(group)) {
				return info;
			}
		}
		return null;
	}

	public synchronized void createGroup(GroupConf group) throws CommandConfException {
		group.insertAll();
		groups.add(group);

		for (GroupConfChangeListener listener : this.listeners) {
			listener.onCreate(group.getName());
		}

		BusiLogUtil.getInstance().businessLog(Operation.createGroup);
	}

	public synchronized List<GroupConf> getGroups() {
		return groups;
	}

	public synchronized void createGroup(String name, String path, String srcPath, List<String> filteredPackages,
			String attribute, List<CommandConf> commandInfos, Map<String, ComponentModelConf> componentModels)
			throws CommandConfException {
		GroupConf group = new GroupConf(name);
		group.setPath(path);
		group.setSrcPath(srcPath);
		group.setFilteredPackages(filteredPackages);
		group.setAttribute(attribute);
		group.setCommands(commandInfos);
		group.setComponentModelConfs(componentModels);

		this.createGroup(group);
	}

	public synchronized void createGroup(String name, String path, String srcPath, List<String> filteredPackages,
			String attribute) throws CommandConfException {
		GroupConf group = new GroupConf(name, path, srcPath, filteredPackages, attribute);
		this.createGroup(group);
	}

	public synchronized void updateGroup(GroupConf group) throws CommandConfException {
		group.update();

		for (GroupConfChangeListener listener : this.listeners) {
			try {
				listener.onUpdate(group.getName());
			} catch (CommandConfException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void updateGroups() throws CommandConfException {
		for (GroupConf obj : groups) {
			obj.update();
		}
	}

	public synchronized void deleteGroup(String group) throws CommandConfException {

		for (GroupConfChangeListener listener : this.listeners) {
			listener.onDelete(group);
		}
		// 删除命令组
		GroupConf info = this.getTheGroup(group);
		info.delete();
		groups.remove(info);

		BusiLogUtil.getInstance().businessLog(Operation.deleteGroup);
	}

	public synchronized void deleteCommand(String group, String command) throws CommandConfException {
		this.getTheGroup(group).deleteCommand(command);
		BusiLogUtil.getInstance().businessLog(Operation.deleteCommand);
	}

	public synchronized void deleteComponentGroup(String group, String componentGroup) throws CommandConfException {
		this.getTheGroup(group).deleteComponentModel(componentGroup);
	}

	public synchronized void createCommand(String group, CommandConf info) throws CommandConfException {
		this.getTheGroup(group).insertCommand(info);
		BusiLogUtil.getInstance().businessLog(Operation.createCommand);
	}

	public synchronized void updateCommand(String group, String oldlabel, CommandConf info) throws CommandConfException {
		this.getTheGroup(group).updateCommand(oldlabel, info);
	}

	public synchronized CommandConf findCommand(String group, String label) throws CommandConfException {
		return this.getTheGroup(group).getCommandInfo(label);
	}

	public synchronized void addGroupListener(GroupConfChangeListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}

	}

	public synchronized Map<String, CommandConf> getCommands(String group) {
		Map<String, CommandConf> confs = new LinkedHashMap<String, CommandConf>();
		for (CommandConf conf : this.getTheGroup(group).getCommands()) {
			confs.put(conf.label, conf);
		}
		return confs;
	}
}
