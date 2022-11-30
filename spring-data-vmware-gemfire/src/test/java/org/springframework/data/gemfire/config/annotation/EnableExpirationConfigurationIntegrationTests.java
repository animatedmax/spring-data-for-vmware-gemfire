/*
 * Copyright 2018-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.expiration.AnnotationBasedExpiration;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.stereotype.Service;

/**
 * Integration Tests for {@link EnableExpiration} and {@link ExpirationConfiguration}.
 *
 * @author John Blum
 * @see Test
 * @see Region
 * @see ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see EnableExpiration
 * @see ExpirationConfiguration
 * @see AnnotationBasedExpiration
 * @see SpringApplicationContextIntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class EnableExpirationConfigurationIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private void assertRegionExpirationConfiguration(ConfigurableApplicationContext applicationContext,
			String regionBeanName) {

		Region<?, ?> region = getRegion(applicationContext, regionBeanName);

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(regionBeanName);
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(region.getAttributes().getStatisticsEnabled()).isTrue();
		assertThat(region.getAttributes().getCustomEntryIdleTimeout())
			.isInstanceOf(AnnotationBasedExpiration.class);
		assertThat(region.getAttributes().getCustomEntryTimeToLive())
			.isInstanceOf(AnnotationBasedExpiration.class);
	}

	@SuppressWarnings("unchecked")
	private <K, V> Region<K, V> getRegion(ConfigurableApplicationContext applicationContext, String beanName) {
		return applicationContext.getBean(beanName, Region.class);
	}

	@Test
	public void assertApplicationCachingDefinedRegionsExpirationPoliciesAreCorrect() {

		ConfigurableApplicationContext applicationContext = newApplicationContext(ApplicationConfiguration.class);

		assertThat(applicationContext).isNotNull();
		assertRegionExpirationConfiguration(applicationContext, "CacheOne");
		assertRegionExpirationConfiguration(applicationContext, "CacheTwo");
	}

	@Test
	public void assertClientCacheRegionExpirationPoliciesAreCorrect() {
		assertRegionExpirationConfiguration(newApplicationContext(ClientCacheRegionExpirationConfiguration.class),
			"People");
	}

	@Test
	public void assertPeerCacheRegionExpirationPoliciesAreCorrect() {
		assertRegionExpirationConfiguration(newApplicationContext(PeerCacheRegionExpirationConfiguration.class),
			"People");
	}

	@ClientCacheApplication(name = "EnableExpirationConfigurationIntegrationTests")
	@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
	@EnableExpiration
	@EnableGemFireMockObjects
	static class ApplicationConfiguration {

		@Bean
		ApplicationService applicationService() {
			return new ApplicationService();
		}
	}

	@Service
	static class ApplicationService {

		@Cacheable("CacheOne")
		public Object someMethod(Object key) {
			return null;
		}

		@Cacheable("CacheTwo")
		public Object someOtherMethod(Object key) {
			return null;
		}
	}

	@ClientCacheApplication(name = "EnableExpirationConfigurationIntegrationTests")
	@EnableEntityDefinedRegions(basePackageClasses = Person.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
	@EnableExpiration
	@EnableGemFireMockObjects
	static class ClientCacheRegionExpirationConfiguration { }

	@PeerCacheApplication(name = "EnableExpirationConfigurationIntegrationTests")
	@EnableEntityDefinedRegions(basePackageClasses = Person.class, serverRegionShortcut = RegionShortcut.LOCAL)
	@EnableExpiration
	@EnableGemFireMockObjects
	static class PeerCacheRegionExpirationConfiguration { }

}
