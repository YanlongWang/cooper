package jdepend.server.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jdepend.framework.exception.JDependException;
import jdepend.knowledge.database.AnalysisResultRepository;
import jdepend.metadata.JavaPackage;
import jdepend.model.result.AnalysisResult;
import jdepend.parse.ParseConfigurator;
import jdepend.parse.impl.PackageFilter;
import jdepend.server.service.AnalyseDataDTO;
import jdepend.server.service.JDependRemoteService;
import jdepend.server.service.session.JDependRequest;
import jdepend.server.service.session.JDependSession;
import jdepend.service.JDependLocalService;
import jdepend.service.config.ServiceConfigurator;
import jdepend.service.impl.JDependLocalServiceImpl;

public class JDependRemoteServiceImpl extends UnicastRemoteObject implements JDependRemoteService {

	private List<ServiceMonitor> monitors = new ArrayList<ServiceMonitor>();

	public JDependRemoteServiceImpl() throws java.rmi.RemoteException {
		super();
	}

	protected void onAnalyse(JDependRequest request) throws JDependException {
		for (ServiceMonitor monitor : monitors) {
			monitor.onAnalyse(request);
		}
	}

	public AnalysisResult analyze(JDependRequest request, AnalyseDataDTO data) throws RemoteException {
		try {
			// 发送分析提醒
			this.onAnalyse(request);

			//生成ServiceConfigurator
			ServiceConfigurator serviceConfigurator = new ServiceConfigurator(data.getServiceConf()
					.getServiceProperties());
			//生成ParseConfigurator
			ParseConfigurator parseConfigurator = new ParseConfigurator(data.getServiceConf().getParseProperties());

			PackageFilter packageFilter = new PackageFilter(data.getServiceConf().getFilteredPackages(), data
					.getServiceConf().getNotFilteredPackages());
			parseConfigurator.setPackageFilter(packageFilter);
			parseConfigurator.setJavaClassRelationTypes(data.getServiceConf().getProfileFacade()
					.getJavaClassRelationItemProfile().getJavaClassRelationTypes());

			// 创建本地服务
			JDependLocalService localService = new JDependLocalServiceImpl(request.getGroupName(),
					request.getCommandName(), serviceConfigurator, parseConfigurator, data.getServiceConf()
							.getProfileFacade());
			// 创建服务上下文
			JDependSession session = JDependSessionMgr.getInstance().getSession(request);
			localService.initServiceContext(false, session.getClient(), session.getUserName());
			// 设置组织包的组件
			if (data.getComponent() != null) {
				localService.setComponent(data.getComponent());
			}
			// 设置分析数据
			localService.setAnalyzeData(data.getAnalyzeData());

			String path = data.getPath();
			if (path != null && path.length() > 0) {
				localService.addDirectory(path);
			}

			// 设置FileterPackages
			localService.addCommandFilteredPackages(data.getServiceConf().getCommandFilteredPackages());
			// 增加解析监听器
			localService.addParseListener(new RemoteParseListener(JDependSessionMgr.getInstance().getSession(request)));
			// 分析服务
			AnalysisResult result = localService.analyze();
			// 保存分析结果
			AnalysisResultRepository.getInstance().save(result);
			// 返回结果
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	public void addMonitor(ServiceMonitor monitor) {
		if (!this.monitors.contains(monitor)) {
			this.monitors.add(monitor);
		}
	}

	@Override
	public int getAnalyzeSchedule(JDependRequest request) throws RemoteException {
		try {
			return JDependSessionMgr.getInstance().getSession(request).getAnalyzeSchedule();
		} catch (JDependException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public Collection<JavaPackage> getPackages(JDependRequest request, AnalyseDataDTO data) throws RemoteException {
		JDependLocalService localService = new JDependLocalServiceImpl(request.getGroupName(), request.getCommandName());
		// 设置分析数据
		localService.setAnalyzeData(data.getAnalyzeData());
		try {
			return localService.getPackages();
		} catch (JDependException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}
}
