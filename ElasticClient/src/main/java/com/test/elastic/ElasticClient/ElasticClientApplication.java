package com.test.elastic.ElasticClient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.elasticsearch.client.RestClient;

import com.fasterxml.uuid.Generators;

@SpringBootApplication
public class ElasticClientApplication {

	public static void main(String[] args) throws KeyManagementException, CertificateException,
			NoSuchAlgorithmException, KeyStoreException, IOException {
		SpringApplication.run(ElasticClientApplication.class, args);
		System.out.println("True");
		UUID scriptId = Generators.timeBasedGenerator().generate();
		ElasticsearchClient Eclient = setClientforfuture();
		System.out.println("ESCLient" + Eclient.toString());

		LocalDateTime time = LocalDateTime.now();
		HashMap<String, Object> displayIndex = new HashMap<String, Object>();
		displayIndex.put("@timestamp", time.toString());
		displayIndex.put("errorTime", "elastictimetest");
		displayIndex.put("userId", "test@vk112652.hvoiptest.dk");
		displayIndex.put("groupId", "vk12345");
		displayIndex.put("serviceProvider", "test");
		displayIndex.put("failureReason", "test");
		displayIndex.put("errorMessage", "elasticTest");
		displayIndex.put("status", "test");
		displayIndex.put("fixAttempt", 2);
		displayIndex.put("source", "ScheduledFixer");

		setElasticIndex(Eclient, displayIndex, scriptId.toString());

	}

	private static void setElasticIndex(ElasticsearchClient client, HashMap<String, Object> indexData, String scriptId)
			throws IOException {
		// indexData.put("scriptId",scriptId);
		String indexName;
		indexName = "hss_fixer";
		IndexResponse indexResponse = client.index(i -> i
				.index("hss_fixer-123")
				.id(scriptId).document(indexData));

		System.out.println("Index Data : " + indexData);
		// System.out.println("Index Request : " + indexRequest);
		System.out.println("Index Data : " + indexData);
		// System.out.println("Index Request : " + indexRequest);
		System.out.println("Index Response : " + indexResponse.result());
		System.out.println("Index Response : " + indexResponse.result());
	}

	public static ElasticsearchClient setClientforfuture() throws CertificateException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, IOException {
		String path = "/opt/vxsp/HssProvCorrector/conf";
		if (System.getProperty("os.name").contains("Windows"))
			path = "C:\\Users\\M95983\\Downloads\\ElasticClient\\ElasticClient\\src\\main\\resources\\elasticsearch-ca.pem";
		System.out.println("Certificate path " + path);
		Path caCertificatePath = Paths.get(
				path);
		System.out.println(caCertificatePath);

		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		X509Certificate trustedCa;
		try (InputStream is = Files.newInputStream(caCertificatePath)) {
			trustedCa = (X509Certificate) factory.generateCertificate(is);
		}
		KeyStore trustStore = KeyStore.getInstance("pkcs12");
		trustStore.load(null, null);
		trustStore.setCertificateEntry("ca", trustedCa);
		SSLContextBuilder sslContextBuilder = SSLContexts.custom()
				.loadTrustMaterial(trustStore, null);
		final SSLContext sslContext = sslContextBuilder.build();
		String apiKeyAuth = "blN5ekE0b0JMbWZQRFFfR0hneHA6em5wLXRpdW5Sb0d5UW9sUXVjcG1CQQ==";
		Header[] defaultHeaders = new Header[] { new BasicHeader("Authorization", "ApiKey " + apiKeyAuth) };
		RestClient restClient = RestClient.builder(
				new HttpHost("simaelastictest.hvoiptest.ip.tdk.dk", 9200, "https"))
				.setHttpClientConfigCallback(new HttpClientConfigCallback() {
					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder.setSSLContext(sslContext);
					}
				}).setDefaultHeaders(defaultHeaders).build();
		ElasticsearchTransport transport = new RestClientTransport(
				restClient, new JacksonJsonpMapper());
		ElasticsearchClient client = new ElasticsearchClient(transport);
		return client;
	}

}
