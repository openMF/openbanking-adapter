/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zaxxer.hikari.HikariDataSource;
import hu.dpc.ob.config.*;
import hu.dpc.ob.util.TenantAwareRoutingSource;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.sql.DataSource;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication(scanBasePackages = "hu.dpc.ob")
@EnableConfigurationProperties({AccessSettings.class, AdapterSettings.class, ApiSettings.class, PspSettings.class})
@EnableTransactionManagement
public class OpenbankingApplication {

	private Environment environment;
	private AdapterSettings adapterSettings;

	@Autowired
	public OpenbankingApplication(Environment environment, AdapterSettings adapterSettings) {
		this.environment = environment;
		this.adapterSettings = adapterSettings;
	}

	private static Logger log = LoggerFactory.getLogger(OpenbankingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OpenbankingApplication.class, args);
	}

	@Bean
	public DataSource dataSource() {
		TenantAwareRoutingSource dataSource = new TenantAwareRoutingSource();
		List<TenantConfig> tenants = adapterSettings.getTenants();

		HashMap<Object,Object> targetDataSources = new HashMap<>();
		DataSource defaultDs = null;
		for (TenantConfig tenant : tenants) {
			HikariDataSource ds = new HikariDataSource();
			ds.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name", String.class));

			String url = environment.getProperty("spring.datasource.url", String.class);
			ds.setJdbcUrl(url.substring(0, url.lastIndexOf("/") + 1) + tenant.getDbSchema());

			ds.setUsername(environment.getProperty("spring.datasource.username", String.class));
			ds.setPassword(environment.getProperty("spring.datasource.password", String.class));
			ds.setInitializationFailTimeout(0);
			ds.setMaximumPoolSize(5);

			targetDataSources.put(tenant.getName(), ds);

			if (defaultDs == null)
				defaultDs = ds;
		}
		dataSource.setTargetDataSources(targetDataSources);
		dataSource.setDefaultTargetDataSource(defaultDs);

		dataSource.afterPropertiesSet();
		return dataSource;
	}

	@Bean
	public RestTemplate restTemplate() {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectionRequestTimeout(30000);
		requestFactory.setConnectTimeout(30000);
		requestFactory.setReadTimeout(30000);

		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, acceptingTrustStrategy).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
        	log.warn("Trusted SSL context initialization failed", e);
			// nothing
        }

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
				.setSSLSocketFactory(csf)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();

		requestFactory.setHttpClient(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
	}

	@Bean
	public Jackson2ObjectMapperBuilder objectMapperBuilder() {
		return new Jackson2ObjectMapperBuilder()
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.serializationInclusion(JsonInclude.Include.NON_EMPTY)
				.failOnUnknownProperties(false);
	}
}
