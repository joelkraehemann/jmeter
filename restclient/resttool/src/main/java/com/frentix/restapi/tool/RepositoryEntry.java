package com.frentix.restapi.tool;

import java.io.File;

import org.apache.log4j.Logger;

import com.frentix.restapi.RestConnection;

/**
 * Import or query repository entries via RESTful web service.
 * 
 * @author jkraehemann, joel.kraehemann@frentix.com, http://www.frentix.com
 *
 */
public class RepositoryEntry {
	private static Logger logger = Logger.getLogger(RepositoryEntry.class);

	/**
	 * Internal usage to communicate with RESTful web services.
	 */
	private RestConnection restConnection;
	
	/**
	 * Creates a new RepositoryEntry instance.
	 * 
	 * @param restConnection the RestConnection
	 */
	public RepositoryEntry(RestConnection restConnection){
		this.restConnection = restConnection;
	}
	
	/**
	 * Imports a repository entry via RESTful web service.
	 * 
	 * @param outputFile the file to safe the repository entry's attributes.
	 * @param inputFile the repository entry as a ZIP file
	 */
	public void importRepositoryEntry(File outputFile, File inputFile){
		//TODO:JK: implement me
	}
	
	/**
	 * Queries repository entries via RESTful web service.
	 * 
	 * @param outputFile the file to safe the queried repository entries
	 */
	public void queryRepositoryEntry(File outputFile){
		//TODO:JK: implement me
	}
	
	public RestConnection getRestConnection() {
		return restConnection;
	}

	public void setRestConnection(RestConnection restConnection) {
		this.restConnection = restConnection;
	}
}
