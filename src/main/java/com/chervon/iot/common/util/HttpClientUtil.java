package com.chervon.iot.common.util;


import com.chervon.iot.common.exception.Bad_RequestException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;


/**
 * Created by Lynn on 2017/4/13.
 */
public class HttpClientUtil {
	private static final ObjectMapper mapper = new ObjectMapper();
	public static CloseableHttpClient createSSLClientDefault(){
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				//信任所有
				public boolean isTrusted(X509Certificate[] chain,
										 String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return  HttpClients.createDefault();
	}

	public static String doGet(String url, Map<String, String> param)throws IOException,Exception {

		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClientUtil.createSSLClientDefault();

		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			System.out.println("BULIDE" + builder);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();
			System.out.println(url + "url");
			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setHeader("secret", "QItvWW2yzjr78GI3RKeyyu");
			httpGet.setHeader("accept", "application/json");
			System.out.println(httpGet.getHeaders("secret"));
			System.out.println(httpGet);
			// 执行请求
			response = httpclient.execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				System.out.println(111);
				resultString = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		}catch (Exception e){
			throw  new Exception();
		}
		finally {
				response.close();
				httpclient.close();
		}
		return resultString;
	}

	public static String doGet(String url)throws IOException,Exception {
		return doGet(url, null);
	}
	public static String doPostJson(String url, String json,String method,String sign)throws IOException,Exception {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("method",method);
			httpPost.addHeader("sign",sign);
			httpPost.addHeader("Content-Type","application/json");
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		}catch (Exception e){
			throw  new Exception();
		}
		finally {
				response.close();
				httpClient.close();
		}
		return resultString;
	}


	public static String doPostJson(String url, File file)throws  IOException,Exception {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClientUtil.createSSLClientDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("secret","QItvWW2yzjr78GI3RKeyyu");
		httpPost.setHeader("accept","application/json");
		try {
			MultipartEntity mutiEntity = new MultipartEntity();
			mutiEntity.addPart("file", new FileBody(file));
			httpPost.setEntity(mutiEntity);
			response = httpClient.execute(httpPost);
			response.setHeader("content-type", "application/json");
			resultString = EntityUtils.toString(response.getEntity(), "utf-8").replace("\\", "");
		}
		catch (Exception e){
			throw  new Exception();
		}
		finally {
				response.close();
				httpClient.close();
	}
		return resultString;
	}
	public static String doPostJson(String url, String requestJson,Map<String,String> headMaps)throws IOException,Exception {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			//httpPost.addHeader("Content-Type","application/x-zc-object");
			for(String key : headMaps.keySet()){
				System.out.println(key+"ss"+headMaps.get(key));
				httpPost.addHeader(key,headMaps.get(key));
			}
			// 创建请求内容
			StringEntity entity = new StringEntity(requestJson, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
			JsonNode result = mapper.readTree(resultString);
			if(result.get("error")!=null){
				throw new Bad_RequestException();
			}
		} catch (Exception e){
			throw e;
		}
		finally {
			response.close();
			httpClient.close();
		}
		return resultString;
	}

}