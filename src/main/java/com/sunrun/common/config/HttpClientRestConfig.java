package com.sunrun.common.config;

import com.sunrun.utils.HttpClientUtil;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class HttpClientRestConfig {
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        //clientHttpRequestFactory.setHttpClient(HttpsClientPoolThread.builder().createSSLClientDefault());
        try {
            clientHttpRequestFactory.setHttpClient(HttpClientUtil.acceptsUntrustedCertsHttpClient());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        //这里是使用了自定义的一个HttpsClientPoolThread线程池单例 以后有机会会单独写文章展示其配置内容, 大家可以先使用默认的HttpClients.createDefault()进行配置,或自定义线程池;
        clientHttpRequestFactory.setConnectTimeout(10000);
        clientHttpRequestFactory.setReadTimeout(10000);
        clientHttpRequestFactory.setConnectionRequestTimeout(200);
        return clientHttpRequestFactory;
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }
}
