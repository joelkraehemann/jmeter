/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * 12.10.2011 by frentix GmbH, http://www.frentix.com
 * <p>
 */
package com.frentix.restapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.olat.core.util.StringHelper;
import org.olat.restapi.security.RestSecurityHelper;

/**
 * 
 * Description:
 * Manage a connection to the grizzly server used by the unit test
 * with some helpers methods.
 * 
 *
 * Initial Date:  20 déc. 2011
 *
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 * @author jkraehemann, joel.kraehemann@frentix.com, http://www.frentix.com
 */
public class RestConnection {

	private static final Logger logger = Logger.getLogger(RestConnection.class);

	public final static int PORT = 9998;
	public final static String HOST = "localhost";
	public final static String PROTOCOL = "http";
	public final static String CONTEXT_PATH = "openolat";

	public final static String LOGIN = "administrator";
	public final static String PASSWORD = "openolat";

	private int port;
	private String host;
	private String protocol;
	private String contextPath;

	private String login;
	private String password;

	private final DefaultHttpClient httpclient;
	private static final JsonFactory jsonFactory = new JsonFactory();

	private String securityToken;

	/**
	 * Default constructor creates a new RestConnection instance.
	 */
	public RestConnection(){
		this(HOST, PORT, PROTOCOL, CONTEXT_PATH, LOGIN, PASSWORD);
	}

	/**
	 * Creates a new RestConnection instance.
	 * 
	 * @param url URL to remote location.
	 */
	public RestConnection(URL url) {
		this(url.getHost(), url.getPort(), url.getProtocol(), url.getPath(), LOGIN, PASSWORD);
	}

	/**
	 * Creates a new RestConnection instance.
	 * 
	 * @param host remote host
	 * @param port the port to connect
	 * @param protocol the protocol to be used
	 * @param contextPath the default context path
	 * @param login credentials
	 * @param password credentials
	 */
	public RestConnection(String host, int port, String protocol, String contextPath, String login, String password){
		this.port = port;
		this.host = host;
		this.protocol = protocol;
		this.contextPath = contextPath;

		this.login = login;
		this.password = password;

		httpclient = new DefaultHttpClient();
		HttpClientParams.setCookiePolicy(httpclient.getParams(), CookiePolicy.RFC_2109);
	}

	public CookieStore getCookieStore() {
		return httpclient.getCookieStore();
	}

	public String getSecurityToken() {
		return securityToken;
	}

	public String getSecurityToken(HttpResponse response) {
		if(response == null) return null;

		Header header = response.getFirstHeader(RestSecurityHelper.SEC_TOKEN);
		return header == null ? null : header.getValue();
	}

	public void shutdown() {
		httpclient.getConnectionManager().shutdown();
	}

	public void setCredentials(String username, String password) {
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope("localhost", getPort()),
				new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Login to RESTful webservice.
	 * 
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public boolean login() throws IOException, URISyntaxException{
		return(login(getLogin(), getPassword()));
	}

	public boolean login(String username, String password) throws IOException, URISyntaxException {
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope("localhost", getPort()),
				new UsernamePasswordCredentials(username, password));

		URI uri = getContextURI().path("restapi").path("auth").path(username).queryParam("password", password).build();
		HttpGet httpget = new HttpGet(uri);
		HttpResponse response = httpclient.execute(httpget);

		Header header = response.getFirstHeader(RestSecurityHelper.SEC_TOKEN);
		if(header != null) {
			securityToken = header.getValue();
		}

		HttpEntity entity = response.getEntity();
		int code = response.getStatusLine().getStatusCode();
		EntityUtils.consume(entity);
		return code == 200;
	}

	/**
	 * HTTP GET method.
	 * 
	 * @param uri the resource
	 * @param cl the vo to send
	 * @return the retrieved vo
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public <T> T get(URI uri, Class<T> cl) throws IOException, URISyntaxException {
		HttpGet get = createGet(uri, MediaType.APPLICATION_JSON, true);
		HttpResponse response = execute(get);
		if(200 == response.getStatusLine().getStatusCode()) {
			HttpEntity entity = response.getEntity();
			return parse(entity.getContent(), cl);
		}
		logger.error("get return: " + response.getStatusLine().getStatusCode());
		return null;
	}

	/**
	 * Add an object
	 * 
	 * @param put the PUT request
	 * @param pairs the names and corresponding values
	 * @throws UnsupportedEncodingException
	 */
	public void addEntity(HttpEntityEnclosingRequestBase put, NameValuePair... pairs)
			throws UnsupportedEncodingException {
		if(pairs == null || pairs.length == 0) return;

		List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		for(NameValuePair pair:pairs) {
			pairList.add(pair);
		}
		HttpEntity myEntity = new UrlEncodedFormEntity(pairList, "UTF-8");
		put.setEntity(myEntity);
	}

	/**
	 * Add an object (application/json)
	 * 
	 * @param put the PUT request
	 * @param obj the JSON
	 * @throws UnsupportedEncodingException
	 */
	public void addJsonEntity(HttpEntityEnclosingRequestBase put, Object obj)
			throws UnsupportedEncodingException {
		if(obj == null) return;

		String objectStr = stringuified(obj);
		HttpEntity myEntity = new StringEntity(objectStr, MediaType.APPLICATION_JSON, "UTF-8");
		put.setEntity(myEntity);
	}

	/**
	 * Attach file to POST request.
	 * 
	 * @param post the request
	 * @param filename the filename field
	 * @param file the file
	 * @throws UnsupportedEncodingException
	 */
	public void addMultipart(HttpEntityEnclosingRequestBase post, String filename, File file)
			throws UnsupportedEncodingException {

		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.addPart("filename", new StringBody(filename));
		FileBody fileBody = new FileBody(file, "application/octet-stream");
		entity.addPart("file", fileBody);
		post.setEntity(entity);
	}

	/**
	 * HTTP PUT method.
	 * 
	 * @param uri
	 * @param accept
	 * @param cookie
	 * @return
	 */
	public HttpPut createPut(URI uri, String accept, boolean cookie) {
		HttpPut put = new HttpPut(uri);
		decorateHttpMessage(put,accept, "en", cookie);
		return put;
	}

	/**
	 * HTTP PUT method.
	 * 
	 * @param uri
	 * @param accept
	 * @param langage
	 * @param cookie
	 * @return
	 */
	public HttpPut createPut(URI uri, String accept, String langage, boolean cookie) {
		HttpPut put = new HttpPut(uri);
		decorateHttpMessage(put,accept, langage, cookie);
		return put;
	}

	/**
	 * HTTP GET method.
	 * 
	 * @param uri
	 * @param accept
	 * @param cookie
	 * @return
	 */
	public HttpGet createGet(URI uri, String accept, boolean cookie) {
		HttpGet get = new HttpGet(uri);
		decorateHttpMessage(get,accept, "en", cookie);
		return get;
	}

	/**
	 * HTTP POST method.
	 * 
	 * @param uri
	 * @param accept
	 * @param cookie
	 * @return
	 */
	public HttpPost createPost(URI uri, String accept, boolean cookie) {
		HttpPost get = new HttpPost(uri);
		decorateHttpMessage(get,accept, "en", cookie);
		return get;
	}

	/**
	 * HTTP DELETE method.
	 * 
	 * @param uri
	 * @param accept
	 * @param cookie
	 * @return
	 */
	public HttpDelete createDelete(URI uri, String accept, boolean cookie) {
		HttpDelete del = new HttpDelete(uri);
		decorateHttpMessage(del, accept, "en", cookie);
		return del;
	}

	/**
	 * Some HTTP request attributes.
	 * 
	 * @param msg
	 * @param accept
	 * @param langage
	 * @param cookie
	 */
	private void decorateHttpMessage(HttpMessage msg, String accept, String langage, boolean cookie) {
		if(cookie) {
			HttpClientParams.setCookiePolicy(msg.getParams(), CookiePolicy.RFC_2109);
		}
		if(StringHelper.containsNonWhitespace(accept)) {
			msg.addHeader("Accept", accept);
		}
		if(StringHelper.containsNonWhitespace(langage)) {
			msg.addHeader("Accept-Language", langage);
		}
	}

	/**
	 * Execute request.
	 * 
	 * @param request the request
	 * @return the HttpResponse
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public HttpResponse execute(HttpUriRequest request)
			throws IOException, URISyntaxException {
		HttpResponse response = httpclient.execute(request);
		return response;
	}

	/**
	 * @return http://localhost:9998
	 */
	public UriBuilder getBaseURI() throws URISyntaxException  {
		URI uri;

		uri = new URI(getProtocol(), null, getHost(), getPort(), null, null, null);

		return UriBuilder.fromUri(uri);
	}

	/**
	 * @return http://localhost:9998/olat
	 */
	public UriBuilder getContextURI()  throws URISyntaxException {
		return getBaseURI().path(getContextPath());
	}

	/**
	 * Make string representation of a JSON object.
	 * 
	 * @param obj
	 * @return
	 */
	public String stringuified(Object obj) {
		try {
			ObjectMapper mapper = new ObjectMapper(jsonFactory); 
			StringWriter w = new StringWriter();
			mapper.writeValue(w, obj);
			return w.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Parses the response.
	 * 
	 * @param response the HttpResponse
	 * @param cl the VO's class
	 * @return the parsed VO
	 */
	public <U> U parse(HttpResponse response, Class<U> cl) {
		try {
			InputStream body = response.getEntity().getContent();
			ObjectMapper mapper = new ObjectMapper(jsonFactory);
			U obj = mapper.readValue(body, cl);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Parses the response.
	 * 
	 * @param body the response body
	 * @param cl the VO's class
	 * @return the parsed VO
	 */
	public <U> U parse(InputStream body, Class<U> cl) {
		try {
			ObjectMapper mapper = new ObjectMapper(jsonFactory);
			U obj = mapper.readValue(body, cl);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Applies the given properties.
	 * 
	 * @param properties the Properties
	 */
	public void applyProperties(Properties properties){
		/* host */
		if(properties.containsKey("restconnection.host")){
			setHost(properties.getProperty("restconnection.host"));
		}

		/* port */
		if(properties.containsKey("restconnection.port")){
			setPort(Integer.parseInt(properties.getProperty("restconnection.port")));
		}

		/* protocol */
		if(properties.containsKey("restconnection.protocol")){
			setProtocol(properties.getProperty("restconnection.protocol"));
		}

		/* context path */
		if(properties.containsKey("restconnection.contextpath")){
			setContextPath(properties.getProperty("restconnection.contextpath"));
		}

		/* login */
		if(properties.containsKey("restconnection.login")){
			setLogin(properties.getProperty("restconnection.login"));
		}

		/* password */
		if(properties.containsKey("restconnection.password")){
			setPassword(properties.getProperty("restconnection.password"));
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
