package jdepend.client.ui.profile.settingpanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jdepend.client.ui.profile.ProfileValidateException;
import jdepend.model.profile.MaintainProfileFacade;
import jdepend.model.profile.model.AnalysisResultProfile;

public class AnalysisResultProfileSettingPanel extends ModelProfileSettingPanel {

	private AnalysisResultProfile analysisResultProfile;

	private JTextField distanceField;
	private JTextField balanceField;
	private JTextField encapsulationField;
	private JTextField relationRationalityField;

	public AnalysisResultProfileSettingPanel(AnalysisResultProfile analysisResultProfile) {
		this.analysisResultProfile = analysisResultProfile;

		this.add(this.leftPanel());
		this.add(this.rightPanel());
	}

	protected Component leftPanel() {

		JPanel left = new JPanel(new BorderLayout());

		JPanel itemPanel = new JPanel(new GridLayout(4, 3));

		itemPanel.add(new JLabel("抽象程度合理性比例："));

		distanceField = new JTextField();
		distanceField.setText(String.valueOf(this.analysisResultProfile.getDistance()));

		itemPanel.add(distanceField);
		itemPanel.add(new JLabel("取值范围：0~100"));

		itemPanel.add(new JLabel("内聚性比例："));

		balanceField = new JTextField();
		balanceField.setText(String.valueOf(this.analysisResultProfile.getBalance()));

		itemPanel.add(balanceField);
		itemPanel.add(new JLabel("取值范围：0~100"));

		itemPanel.add(new JLabel("封装性比例："));

		encapsulationField = new JTextField();
		encapsulationField.setText(String.valueOf(this.analysisResultProfile.getEncapsulation()));

		itemPanel.add(encapsulationField);
		itemPanel.add(new JLabel("取值范围：0~100"));

		itemPanel.add(new JLabel("关系合理性比例："));

		relationRationalityField = new JTextField();
		relationRationalityField.setText(String.valueOf(this.analysisResultProfile.getRelationRationality()));

		itemPanel.add(relationRationalityField);
		itemPanel.add(new JLabel("取值范围：0~100"));

		left.add(BorderLayout.CENTER, itemPanel);
		left.add(BorderLayout.SOUTH, new JLabel("要求：各项目比例和为100"));

		return left;
	}

	@Override
	public void validateData() throws ProfileValidateException {
		float distance = Float.valueOf(distanceField.getText());
		if (distance < 0 || distance > 100) {
			throw new ProfileValidateException("抽象程度合理性比例超出了范围！", 0);
		}

		float balance = Float.valueOf(balanceField.getText());
		if (balance < 0 || balance > 100) {
			throw new ProfileValidateException("内聚性比例超出了范围！", 0);
		}

		float encapsulation = Float.valueOf(encapsulationField.getText());
		if (encapsulation < 0 || encapsulation > 100) {
			throw new ProfileValidateException("封装性比例超出了范围！", 0);
		}

		float relationRationality = Float.valueOf(relationRationalityField.getText());
		if (relationRationality < 0 || relationRationality > 100) {
			throw new ProfileValidateException("关系合理性比例超出了范围！", 0);
		}

		float score = distance + balance + encapsulation + relationRationality;
		if (score != 100) {
			throw new ProfileValidateException("比例之和须等于100！", 0);
		}

	}

	@Override
	public void save(MaintainProfileFacade maintainProfileFacade) {

		float distance = Float.valueOf(distanceField.getText());
		float balance = Float.valueOf(balanceField.getText());
		float encapsulation = Float.valueOf(encapsulationField.getText());
		float relationRationality = Float.valueOf(relationRationalityField.getText());

		AnalysisResultProfile newAnalysisResultProfile = new AnalysisResultProfile();

		newAnalysisResultProfile.setDistance(distance);
		newAnalysisResultProfile.setBalance(balance);
		newAnalysisResultProfile.setEncapsulation(encapsulation);
		newAnalysisResultProfile.setRelationRationality(relationRationality);

		maintainProfileFacade.setAnalysisResultProfile(newAnalysisResultProfile);
	}

	@Override
	protected String getExplain() {
		StringBuilder info = new StringBuilder();
		info.append("判断分析结果的分数由多个部分构成，用户可以用过设置每个部分所占的比例来表达自己关心的特性。\n");
		info.append("");

		return info.toString();
	}
}
