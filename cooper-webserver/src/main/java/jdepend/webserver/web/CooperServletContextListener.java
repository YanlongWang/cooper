package jdepend.webserver.web;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import jdepend.framework.config.PropertyConfigurator;
import jdepend.framework.context.JDependContext;
import jdepend.framework.log.BusiLogUtil;
import jdepend.framework.log.LogUtil;
import jdepend.framework.persistent.ConnectionFactory;
import jdepend.webserver.persistent.WebServerConnectionProvider;

import org.apache.log4j.Logger;

public class CooperServletContextListener implements ServletContextListener {

	private Logger logger = Logger.getLogger(CooperServletContextListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		String path = arg0.getServletContext().getRealPath("//");
		JDependContext.setRunningPath(path);

		String workspacePath = path + File.separator + "WEB-INF";
		JDependContext.setWorkspacePath(workspacePath);

		logger.info("WorkspacePath:" + workspacePath);

		// 设置ConnectionProvider
		ConnectionFactory.setProvider(new WebServerConnectionProvider());

		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// 设置日志是否打印
		PropertyConfigurator conf = new PropertyConfigurator();
		BusiLogUtil.BUSINESSLOG = conf.isPrintBusiLog();
		LogUtil.SYSTEMLOG = conf.isPrintSystemLog();
		LogUtil.SYSTEMWARNING = conf.isPrintSystemWarning();

		// String classPath = path + File.separator + "WEB-INF" + File.separator
		// + "classes";
		// SearchUtil search = new SearchUtil();
		// search.addPath(classPath);
		// ClassSearchUtil.getInstance().setClassList(search.getClasses());
	}

}
