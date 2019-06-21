/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.config.PspSettings;
import hu.dpc.ob.config.AdapterSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = "hu.dpc.ob")
@EnableConfigurationProperties({AccessSettings.class, AdapterSettings.class, ApiSettings.class, PspSettings.class})
public class OpenbankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenbankingApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setConnectionRequestTimeout(30000);
		httpRequestFactory.setConnectTimeout(30000);
		httpRequestFactory.setReadTimeout(30000);

		return new RestTemplate(httpRequestFactory);
	}

}
