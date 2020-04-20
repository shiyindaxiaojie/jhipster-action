/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ylzl.eden.uaa.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.xnio.OptionMap;
import org.ylzl.eden.spring.boot.framework.core.FrameworkProperties;
import org.ylzl.eden.spring.boot.framework.core.ProfileConstants;
import org.ylzl.eden.spring.boot.framework.web.filter.CachingHttpHeadersFilter;

import javax.servlet.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web 自动配置测试
 *
 * @author gyl
 * @since 1.0.0
 */
public class WebAutoConfigurationTest {

    private MockServletContext mockServletContext;

    private MockEnvironment mockEnvironment;

	private FrameworkProperties frameworkProperties;

	private ManagementServerProperties managementServerProperties;

    private WebAutoConfiguration webAutoConfiguration;

	private MetricRegistry metricRegistry;

    @Before
    public void setup() {
        mockServletContext = spy(new MockServletContext());
        doReturn(mock(FilterRegistration.Dynamic.class)).when(mockServletContext).addFilter(anyString(), any(Filter.class));
        doReturn(mock(ServletRegistration.Dynamic.class)).when(mockServletContext).addServlet(anyString(), any(Servlet.class));

        mockEnvironment = new MockEnvironment();
		frameworkProperties = new FrameworkProperties();
		managementServerProperties = new ManagementServerProperties();
		managementServerProperties.setContextPath("/management");
        webAutoConfiguration = new WebAutoConfiguration(frameworkProperties, mockEnvironment);

        metricRegistry = new MetricRegistry();
        webAutoConfiguration.setMetricRegistry(metricRegistry);
    }

    /**
     * 测试启动生产环境 Servlet 上下文
     *
     * @throws ServletException
     */
    @Test
    public void testStartUpProdServletContext() throws ServletException {
        mockEnvironment.setActiveProfiles(ProfileConstants.SPRING_PROFILE_PRODUCTION);
        webAutoConfiguration.onStartup(mockServletContext);

        assertThat(mockServletContext.getAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE)).isEqualTo(metricRegistry);
        assertThat(mockServletContext.getAttribute(MetricsServlet.METRICS_REGISTRY)).isEqualTo(metricRegistry);
        verify(mockServletContext).addFilter(eq("webappMetricsFilter"), any(InstrumentedFilter.class));
        verify(mockServletContext).addServlet(eq("metricsServlet"), any(MetricsServlet.class));
        verify(mockServletContext).addFilter(eq("cachingHttpHeadersFilter"), any(CachingHttpHeadersFilter.class));
    }

    /**
     * 测试启动开发环境 Servlet 上下文
     *
     * @throws ServletException
     */
    @Test
    public void testStartUpDevServletContext() throws ServletException {
        mockEnvironment.setActiveProfiles(ProfileConstants.SPRING_PROFILE_DEVELOPMENT);
        webAutoConfiguration.onStartup(mockServletContext);

        assertThat(mockServletContext.getAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE)).isEqualTo(metricRegistry);
        assertThat(mockServletContext.getAttribute(MetricsServlet.METRICS_REGISTRY)).isEqualTo(metricRegistry);
        verify(mockServletContext).addFilter(eq("webappMetricsFilter"), any(InstrumentedFilter.class));
        verify(mockServletContext).addServlet(eq("metricsServlet"), any(MetricsServlet.class));
        verify(mockServletContext, never()).addFilter(eq("cachingHttpHeadersFilter"), any(CachingHttpHeadersFilter.class));
    }

    /**
     * 测试自定义的 Servlet 容器
     */
    @Test
    public void testCustomizeServletContainer() {
        mockEnvironment.setActiveProfiles(ProfileConstants.SPRING_PROFILE_PRODUCTION);
        UndertowEmbeddedServletContainerFactory container = new UndertowEmbeddedServletContainerFactory();
        webAutoConfiguration.customize(container);

        assertThat(container.getMimeMappings().get("abs")).isEqualTo("audio/x-mpeg");
        assertThat(container.getMimeMappings().get("html")).isEqualTo("text/html;charset=utf-8");
        assertThat(container.getMimeMappings().get("json")).isEqualTo("text/html;charset=utf-8");
        if (container.getDocumentRoot() != null) {
            assertThat(container.getDocumentRoot().getPath()).isEqualTo(FilenameUtils.separatorsToSystem("target/classes/static"));
        }

        Builder builder = Undertow.builder();
        for (UndertowBuilderCustomizer customizer: container.getBuilderCustomizers()) {
            customizer.customize(builder);
        }
        OptionMap.Builder serverOptions = (OptionMap.Builder) ReflectionTestUtils.getField(builder, "serverOptions");
        assertThat(serverOptions.getMap().get(UndertowOptions.ENABLE_HTTP2)).isNull();
    }

    /**
     * 测试 Undertow 开启 HTTP2
     */
    @Test
    public void testUndertowHttp2Enabled() {
        frameworkProperties.getHttp().setVersion(FrameworkProperties.Http.Version.V_2_0);
        UndertowEmbeddedServletContainerFactory container = new UndertowEmbeddedServletContainerFactory();
        webAutoConfiguration.customize(container);

        Builder builder = Undertow.builder();
        for (UndertowBuilderCustomizer customizer: container.getBuilderCustomizers()) {
            customizer.customize(builder);
        }
        OptionMap.Builder serverOptions = (OptionMap.Builder) ReflectionTestUtils.getField(builder, "serverOptions");
        assertThat(serverOptions.getMap().get(UndertowOptions.ENABLE_HTTP2)).isTrue();
    }

    /**
     * 测试跨域过滤器是否正常处理 API 接口
     *
     * @throws Exception
     */
    @Test
    public void testCorsFilterOnApiPath() throws Exception {
		frameworkProperties.getCors().setAllowedOrigins(Collections.singletonList("*"));
		frameworkProperties.getCors().setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		frameworkProperties.getCors().setAllowedHeaders(Collections.singletonList("*"));
		frameworkProperties.getCors().setMaxAge(1800L);
		frameworkProperties.getCors().setAllowCredentials(true);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebAutoConfigurationTestController())
            .addFilters(webAutoConfiguration.corsFilter(frameworkProperties, managementServerProperties))
            .build();

        mockMvc.perform(options("/api/test-cors")
            .header(HttpHeaders.ORIGIN, "other.domain.com")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "other.domain.com"))
            .andExpect(header().string(HttpHeaders.VARY, "Origin"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "1800"));

        mockMvc.perform(get("/api/test-cors")
            .header(HttpHeaders.ORIGIN, "other.domain.com"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "other.domain.com"));
    }

    /**
     * 测试跨域过滤器是否正常处理其他接口
     *
     * @throws Exception
     */
    @Test
    public void testCorsFilterOnOtherPath() throws Exception {
		frameworkProperties.getCors().setAllowedOrigins(Collections.singletonList("*"));
		frameworkProperties.getCors().setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		frameworkProperties.getCors().setAllowedHeaders(Collections.singletonList("*"));
		frameworkProperties.getCors().setMaxAge(1800L);
		frameworkProperties.getCors().setAllowCredentials(true);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebAutoConfigurationTestController())
            .addFilters(webAutoConfiguration.corsFilter(frameworkProperties, managementServerProperties))
            .build();

        mockMvc.perform(get("/other/test-cors")
            .header(HttpHeaders.ORIGIN, "other.domain.com"))
            .andExpect(status().isOk())
            .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    /**
     * 测试跨域过滤器配置 Allowed Origins 为 null
     *
     * @throws Exception
     */
    @Test
    public void testCorsFilterDeactivated() throws Exception {
		frameworkProperties.getCors().setAllowedOrigins(null);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebAutoConfigurationTestController())
            .addFilters(webAutoConfiguration.corsFilter(frameworkProperties, managementServerProperties))
            .build();

        mockMvc.perform(get("/api/test-cors")
            .header(HttpHeaders.ORIGIN, "other.domain.com"))
            .andExpect(status().isOk())
            .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    /**
     * 测试跨域过滤器配置 Allowed Origins 为空的列表
     *
     * @throws Exception
     */
    @Test
    public void testCorsFilterDeactivated2() throws Exception {
		frameworkProperties.getCors().setAllowedOrigins(new ArrayList<String>());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebAutoConfigurationTestController())
            .addFilters(webAutoConfiguration.corsFilter(frameworkProperties, managementServerProperties))
            .build();

        mockMvc.perform(get("/api/test-cors")
            .header(HttpHeaders.ORIGIN, "other.domain.com"))
            .andExpect(status().isOk())
            .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
