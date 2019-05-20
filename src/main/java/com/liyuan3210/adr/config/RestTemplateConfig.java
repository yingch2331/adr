package com.liyuan3210.adr.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
public class RestTemplateConfig {
	
	// 连接超时时间
	private final static int CONNECT_TIMEOUT = 60000;
	
	// 读取超时时间
	private final static int READ_TIMEOUT = 60000;

	@Bean
	public RestTemplate restTemplate() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		
		// http请求连接池
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(3000);
		// 每个路由最大并发量
		connectionManager.setDefaultMaxPerRoute(3000);
		
		// 失败重试处理
		DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(2, true);
		
		// 请求头设置
		List<BasicHeader> defaultHeaders = new ArrayList<BasicHeader>();
		defaultHeaders.add(new BasicHeader("Content-Type", "text/html;charset=UTF-8"));
		defaultHeaders.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
		defaultHeaders.add(new BasicHeader("Accept-Language", "zh-CN"));
		
		builder.setConnectionManager(connectionManager);
		builder.setRetryHandler(retryHandler);
		builder.setDefaultHeaders(defaultHeaders);
		
		// httpClient对象
		HttpClient httpClient = builder.build();
		// 请求连接工厂类
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
		requestFactory.setReadTimeout(READ_TIMEOUT);
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(requestFactory);
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
		List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
		supportedMediaTypes.add(MediaType.TEXT_HTML);
		supportedMediaTypes.add(MediaType.TEXT_PLAIN);
		supportedMediaTypes.add(MediaType.parseMediaType("text/javascript;charset=utf-8"));
		stringHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
		messageConverters.add(stringHttpMessageConverter);
		messageConverters.add(new FastJsonHttpMessageConverter());
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new AllEncompassingFormHttpMessageConverter());
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
		
		restTemplate.setMessageConverters(messageConverters);
		
		return restTemplate;
	}
	
}
