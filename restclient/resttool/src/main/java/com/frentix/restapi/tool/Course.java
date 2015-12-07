package com.frentix.restapi.tool;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.log4j.Logger;
import org.olat.restapi.support.vo.CourseVO;

import com.frentix.restapi.RestConnection;
import com.frentix.util.CsvHelper;

/**
 * Import or query courses via RESTful web service.
 * 
 * @author jkraehemann, joel.kraehemann@frentix.com, http://www.frentix.com
 *
 */
public class Course {
	Logger logger = Logger.getLogger(Course.class);
	
	/**
	 * Internal usage to communicate with RESTful web services.
	 */
	private RestConnection restConnection;
	
	/**
	 * Creates a new Course instance.
	 * 
	 * @param restConnection the RestConnection
	 */
	public Course(RestConnection restConnection){
		this.restConnection = restConnection;
	}
	
	/**
	 * Imports a course via RESTful web service.
	 * 
	 * @param outputFile the file to safe the course's attributes.
	 * @param inputFile the course as a ZIP file
	 * @throws IOException 
	 * @throws URISyntaxExceptio
	 */
	public void importCourse(File outputFile, File inputFile) throws IOException, URISyntaxException {
		logger.info("importing course file: " + inputFile.getPath());
		
		/* login to openolat RESTful web service */
		restConnection.login();
		
		/* create request */
		URI request = restConnection.getContextURI().path("restapi").path("repo/courses").build();
		HttpPost method = restConnection.createPost(request, MediaType.APPLICATION_JSON, true);
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.addPart("file", new FileBody(inputFile));
		entity.addPart("filename", new StringBody(inputFile.getName()));
		entity.addPart("resourcename", new StringBody(inputFile.getName()));
		entity.addPart("displayname", new StringBody(inputFile.getName()));
		entity.addPart("access", new StringBody("3"));
		String softKey = UUID.randomUUID().toString().replace("-", "").substring(0, 30);
		entity.addPart("softkey", new StringBody(softKey));
		method.setEntity(entity);
		
		/* execute request */
		HttpResponse response = restConnection.execute(method);
		
		if(response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201){
			throw(new IOException());
		}
		
		/* parse response */
		CourseVO course = restConnection.parse(response, CourseVO.class);

		/* write to file */
		CsvHelper.writeCourseToCsv(outputFile, ',', false, course);
	}
	
	/**
	 * Queries courses via RESTful web service.
	 * 
	 * @param outputFile the file to safe the queried courses
	 */
	public void queryCourse(File outputFile){
		//TODO:JK: implement me
	}
	
	public RestConnection getRestConnection() {
		return restConnection;
	}

	public void setRestConnection(RestConnection restConnection) {
		this.restConnection = restConnection;
	}
}
