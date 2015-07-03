package jdepend.client.ui.profile;

import jdepend.client.ui.JDependCooper;
import jdepend.model.profile.ProfileFacade;
import jdepend.service.profile.scope.ProfileScopeFacade;
import jdepend.service.profile.scope.WorkspaceProfileScope;

public class WorkspaceProfileSettingDialog extends ProfileSettingDialog {

	public WorkspaceProfileSettingDialog(JDependCooper frame) {
		super(frame, ProfileScopeFacade.getInstance().getWorkspaceProfileScope().getProfileFacade());
	}

	@Override
	protected void updateScope(ProfileFacade profileFacade) {
		WorkspaceProfileScope workspaceProfileScope = new WorkspaceProfileScope();
		workspaceProfileScope.setProfileFacade(profileFacade);
		ProfileScopeFacade.getInstance().setWorkspaceProfileScope(workspaceProfileScope);
	}
}
