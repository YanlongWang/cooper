package jdepend.service.profile.scope;

import java.util.ArrayList;
import java.util.List;

import jdepend.framework.config.PropertyConfigurator;
import jdepend.framework.domain.PersistentBean;
import jdepend.model.profile.ProfileFacade;
import jdepend.model.profile.model.defaultvalue.DefaultProfileFacadeImpl;

public class ProfileScopeFacade extends PersistentBean implements ProfileScope, ProfileScopeMgr {

	private static final long serialVersionUID = -3898728864369810803L;

	private static ProfileScopeFacade facade;

	private WorkspaceProfileScope workspaceScope;

	private List<GroupProfileScope> groupScopes = new ArrayList<GroupProfileScope>();

	private List<CommandProfileScope> commandScopes = new ArrayList<CommandProfileScope>();

	private ProfileScopeFacade() {
		super("ProfileScopeFacade", "ProfileScopeFacade", PropertyConfigurator.DEFAULT_PROPERTY_DIR);

		if (!this.containSetting()) {
			this.initDefaultScopes();
		}
	}

	private void initDefaultScopes() {
		workspaceScope = new WorkspaceProfileScope();
		workspaceScope.setProfileFacade(new DefaultProfileFacadeImpl());
	}

	private boolean containSetting() {
		return workspaceScope != null || groupScopes != null && groupScopes.size() > 0 || commandScopes != null
				&& commandScopes.size() > 0;
	}

	public static ProfileScopeFacade getInstance() {
		if (facade == null) {
			facade = new ProfileScopeFacade();
		}
		return facade;
	}

	@Override
	public ProfileFacade getProfileFacade(String group, String command) {
		ProfileFacade profileFacade;

		for (ProfileScope scope : this.getProfileScope()) {
			profileFacade = scope.getProfileFacade(group, command);
			if (profileFacade != null) {
				return profileFacade;
			}
		}

		return null;
	}

	private List<ProfileScope> getProfileScope() {

		List<ProfileScope> scopes = new ArrayList<ProfileScope>();
		scopes.addAll(commandScopes);
		scopes.addAll(groupScopes);
		scopes.add(workspaceScope);

		return scopes;
	}

	@Override
	public WorkspaceProfileScope getWorkspaceProfileScope() {
		return workspaceScope;
	}

	@Override
	public void setWorkspaceProfileScope(WorkspaceProfileScope workspaceProfileScope) {
		this.workspaceScope = workspaceProfileScope;
	}

	@Override
	public GroupProfileScope getGroupProfileScope(String group) {
		for (GroupProfileScope groupProfileScope : this.groupScopes) {
			if (groupProfileScope.getGroup().equals(group)) {
				return groupProfileScope;
			}
		}
		return null;
	}

	@Override
	public void setGroupProfileScope(GroupProfileScope groupProfileScope) {

		if (this.groupScopes.contains(groupProfileScope)) {
			this.groupScopes.remove(groupProfileScope);
		}
		this.groupScopes.add(groupProfileScope);
	}

	@Override
	public CommandProfileScope getCommandProfileScope(String group, String command) {
		for (CommandProfileScope commandProfileScope : this.commandScopes) {
			if (commandProfileScope.getGroup().equals(group) && commandProfileScope.getCommand().equals(command)) {
				return commandProfileScope;
			}
		}
		return null;
	}

	@Override
	public void setCommandProfileScope(CommandProfileScope commandProfileScope) {

		if (this.commandScopes.contains(commandProfileScope)) {
			this.commandScopes.remove(commandProfileScope);
		}
		this.commandScopes.add(commandProfileScope);
	}
}
