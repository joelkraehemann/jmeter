package com.frentix.restapi.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.log4j.Logger;
import org.olat.user.restapi.UserVO;

import com.frentix.restapi.RestConnection;
import com.frentix.util.CsvHelper;

/**
 * Generate, import or query users via RESTful web service. You may set the
 * appropriate properties to configure parameters of user generation.
 * 
 * @author jkraehemann, joel.kraehemann@frentix.com, http://www.frentix.com
 *
 */
public class User {
	private static Logger logger = Logger.getLogger(User.class);
	
	public final static String GENERATE_USER_FIRSTNAME_PREFIX = "John";
	public final static String GENERATE_USER_SURNAME_PREFIX = "JMeter_";
	public final static String GENERATE_USER_LOGIN_PREFIX = "jmeter_";
	
	/**
	 * The prefix to be used for generating the firstname of a user.
	 * 
	 * generate.user.firstname.prefix is the property key of this field. 
	 */
	private String generateUserFirstnamePrefix;
	
	/**
	 * The prefix to be used for generating surname of a user.
	 * 
	 * generate.user.surename.prefix is the property key of this field.
	 */
	private String generateUserSurnamePrefix;
	
	/**
	 * The prefix to be used for generating login name of a user.
	 * 
	 * generate.user.login.prefix is the property key of this field.
	 */
	private String generateUserLoginPrefix;
	
	/**
	 * Internal usage to communicate with RESTful web services.
	 */
	private RestConnection restConnection;
	
	/**
	 * Creates a new User instance.
	 * 
	 * @param restConnection the RestConnection
	 */
	public User(RestConnection restConnection){
		this(restConnection, GENERATE_USER_FIRSTNAME_PREFIX, GENERATE_USER_SURNAME_PREFIX, GENERATE_USER_LOGIN_PREFIX);
	}
	
	/**
	 * Creates a new User instance.
	 * 
	 * @param restConnection the RestConnection
	 * @param generateUserFirstnamePrefix prefix to be used
	 * @param generateUserSurnamePrefix prefix to be used
	 * @param generateUserLoginPrefix prefix to be used
	 */
	public User(RestConnection restConnection, String generateUserFirstnamePrefix, String generateUserSurnamePrefix, String generateUserLoginPrefix){
		/* RestConnection */
		this.restConnection = restConnection;
		
		
		/* firstname */
		this.generateUserFirstnamePrefix = GENERATE_USER_FIRSTNAME_PREFIX;
		
		
		/* surname */
		this.generateUserSurnamePrefix = GENERATE_USER_SURNAME_PREFIX;
		
		
		/* login */
		this.generateUserLoginPrefix = GENERATE_USER_LOGIN_PREFIX;
	}

	/**
	 * Generates users via RESTful web service.
	 * 
	 * @param outputFile the file to safe the generated user attributes
	 * @param count the count of users to be created
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @see #generateUserSurnamePrefix
	 * @see #generateUserFirstnamePrefix
	 * @see #generateUserLoginPrefix
	 */
	public void generateUser(File outputFile, int count) throws IOException, URISyntaxException{

		logger.info("starting to generate " + count + " users");
		
		/* login to openolat RESTful web service */
		if(!restConnection.login()){
			throw new IOException();
		}
		
		List<UserVO> user = new ArrayList<UserVO>();
		
		for(int i = 0; i < count; i++){
			logger.info("generating user number " + i);
			
			/* generate user */
			UserVO vo = new UserVO();
			String username = (getGenerateUserLoginPrefix() + i + "_" + UUID.randomUUID().toString()).substring(0, 24);
			vo.setLogin(username);
			String password = ("passwd_" + i + "_" + UUID.randomUUID().toString()).substring(0, 24);
			vo.setPassword(password);
			vo.setFirstName(getGenerateUserFirstnamePrefix() + i);
			vo.setLastName(getGenerateUserSurnamePrefix() + i);
			vo.setEmail(username + "@frentix.com");
			vo.putProperty("telOffice", "39847592");
			vo.putProperty("telPrivate", "39847592");
			vo.putProperty("telMobile", "39847592");
			vo.putProperty("gender", "Female");//male or female
			vo.putProperty("birthDay", "12/12/2009");

			/* create request */
			URI request = restConnection.getContextURI().path("restapi").path("users").build();
			HttpPut method = restConnection.createPut(request, MediaType.APPLICATION_JSON, true);
			restConnection.addJsonEntity(method, vo);
			method.addHeader("Accept-Language", "en");

			/* execute request */
			HttpResponse response = restConnection.execute(method);
			
			if(response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201){
				throw new IOException();
			}
			
			/* parse response */
			InputStream body = response.getEntity().getContent();
			
			UserVO current = restConnection.parse(body, UserVO.class);
			
			if(current == null){
				throw new IOException();
			}
			
			/* set password what won't be received */
			current.setPassword(vo.getPassword());
			
			/* add to list */
			user.add(current);
		}

		/* close connection */
		restConnection.shutdown();
		
		/* write to file */
		logger.info("writing generated users to file: " + outputFile.getPath());
		
		CsvHelper.writeUserToCsv(outputFile, ',', false, user);
	}
	
	/**
	 * Imports users via RESTful web service.
	 * 
	 * @param inputFile the file containing the user's attributes.
	 * @param outputFile
	 */
	public void importUser(File outputFile, File inputFile){
		//TODO:JK: implement me
	}
	
	/**
	 * Queries existing users via RESTful web service.
	 * 
	 * @param outputFile the file to safe the queried user attributes.
	 */
	public void queryUser(File outputFile){
		//TODO:JK: implement me
	}

	/**
	 * Applies the given properties.
	 * 
	 * @param properties the Properties
	 */
	public void applyProperties(Properties properties){
		/* firstname prefix */
		if(properties.containsKey("generateuser.firstnamePrefix")){
			setGenerateUserFirstnamePrefix(properties.getProperty("generateuser.firstnamePrefix"));
		}
		
		/* surname prefix */
		if(properties.containsKey("generateuser.surnamePrefix")){
			setGenerateUserSurnamePrefix(properties.getProperty("generateuser.surnamePrefix"));
		}
		
		/* login prefix */
		if(properties.containsKey("generateuser.loginPrefix")){
			setGenerateUserLoginPrefix(properties.getProperty("generateuser.loginPrefix"));
		}
	}
	
	public String getGenerateUserFirstnamePrefix() {
		return generateUserFirstnamePrefix;
	}

	public void setGenerateUserFirstnamePrefix(String generateUserFirstnamePrefix) {
		this.generateUserFirstnamePrefix = generateUserFirstnamePrefix;
	}

	public String getGenerateUserSurnamePrefix() {
		return generateUserSurnamePrefix;
	}

	public void setGenerateUserSurnamePrefix(String generateUserSurnamePrefix) {
		this.generateUserSurnamePrefix = generateUserSurnamePrefix;
	}

	public String getGenerateUserLoginPrefix() {
		return generateUserLoginPrefix;
	}

	public void setGenerateUserLoginPrefix(String generateUserLoginPrefix) {
		this.generateUserLoginPrefix = generateUserLoginPrefix;
	}

	public RestConnection getRestConnection() {
		return restConnection;
	}

	public void setRestConnection(RestConnection restConnection) {
		this.restConnection = restConnection;
	}

}
