package net.eekie.metrics.config.jmx;

import net.eekie.metrics.config.ZooConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.MBeanServerFactoryBean;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import java.io.IOException;

@Configuration
@EnableMBeanExport(server = "mBeanServer", defaultDomain = "observable-zoo")
public class JmxConfig {

	@Value("${jmx.service.url}")
	public String jmxServiceUrl;

	@Autowired
	private ZooConfig zooConfig;

	@Bean
	public MBeanServerFactoryBean mBeanServerFactoryBean() {
		return new MBeanServerFactoryBean();
	}

	@Bean
	public MBeanServer mBeanServer() {
		MBeanServerFactoryBean mBeanServerFactoryBean = new MBeanServerFactoryBean();
		mBeanServerFactoryBean.setLocateExistingServerIfPossible(true);
		mBeanServerFactoryBean.afterPropertiesSet();
		return mBeanServerFactoryBean.getObject();
	}

	@Bean
	public JMXConnectorServer jmxServerConnector() throws IOException, JMException {
		ConnectorServerFactoryBean factoryBean = new ConnectorServerFactoryBean();

		factoryBean.setServer(mBeanServer());
		factoryBean.setServiceUrl(jmxServiceUrl);
		factoryBean.afterPropertiesSet();

		return factoryBean.getObject();
	}

	@Bean
	public ZooResource zooResource() {
		return new ZooResource(zooConfig.zooWithAnimalCountMetricListener());
	}

}
