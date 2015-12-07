package com.frentix;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;

import com.frentix.restapi.RestConnection;
import com.frentix.restapi.tool.Course;
import com.frentix.restapi.tool.RepositoryEntry;
import com.frentix.restapi.tool.User;

/**
 * You may import or query repository entries with Resttool as well generate, create or query
 * users of an openolat LMS instance.
 * 
 * @author jkraehemann, joel.kraehemann@frentix.com, http://www.frentix.com
 */
public class Resttool 
{
	public final static String RESTTOOL_INVOKE = "java -jar Resttool.jar"; 

	public final static String GENERATE_USER_USAGE = "-g user count -o user.csv";
	public final static String CREATE_USER_USAGE = "-i user user.csv -o user.csv";
	public final static String QUERY_USER_USAGE = "-q user -o user.csv";
	
	public final static String IMPORT_COURSE_USAGE = "-i course course.zip -o course.csv";
	public final static String QUERY_COURSE_USAGE = "-q course -o course.csv";
	
	/**
	 * Shows sample usage on console.
	 * 
	 */
	public static void printUsage(){
		System.out.println("frentix GmbH Resttool usage");
		
		/* generate user */
		System.out.print(RESTTOOL_INVOKE);
		System.out.print(' ');
		System.out.println(GENERATE_USER_USAGE);
		
		//TODO:JK: add missing
		
		/* import repository entry */
		System.out.print(RESTTOOL_INVOKE);
		System.out.print(' ');
		System.out.println(IMPORT_COURSE_USAGE);
		
		//TODO:JK: add missing
	}
	
    public static void main( String[] args ) throws NumberFormatException, IOException, URISyntaxException{
    	
        if(args == null || args.length == 0){
        	printUsage();
        	
        	System.exit(1);
        }
        
        int argOffset = -1;
        int outputOffset = -1;
        
        /* read output file from command line */
        File outputFile = null;
        boolean outputValid = false;
        
        if((argOffset = ArrayUtils.indexOf(args, "-o")) != -1){
        	if(argOffset + 1 < args.length){
        		outputFile = new File(args[argOffset + 1]);
        		
        		outputOffset = argOffset;
        		outputValid = true;
        	}
        }
        
        /* action */
        boolean actionValid = false;
        
        if(outputValid){
        	RestConnection restConnection = new RestConnection();
        	
        	/* load properties */
        	Properties properties = new Properties();
        	
        	try{
        		properties.load(Resttool.class.getResourceAsStream("resttool.properties"));
        	}catch(IOException e){
        		System.err.println("couldn't find resttool.properties");
        	}
        	
        	/* apply properties */
        	restConnection.applyProperties(properties);
        	
        	/* check action, now */
	        if((argOffset = ArrayUtils.indexOf(args, "-g")) != -1){
	        	if(argOffset + 1 < args.length){
	        		if("user".equals(args[argOffset + 1]) && args.length == 5 && outputOffset == 3){
	        			/* create helper */
	        			User user = new User(restConnection);
	        			
	        			/* apply properties */
	                	user.applyProperties(properties);
	                
	        			/* generate the users */
	        			user.generateUser(outputFile, Integer.parseInt(args[2]));
	        			
	        			/* everything is fine */
	        			actionValid = true;
	        		}
	        	}
	        }else if((argOffset = ArrayUtils.indexOf(args, "-i")) != -1){
	        	if(argOffset + 1 < args.length){
	        		if(args[argOffset + 1] != null && args.length == 5 && outputOffset == 3 && "course".equals(args[argOffset + 1]) && args[argOffset + 2].endsWith(".zip")){
	        			/* retrieve input file */
	        			File inputFile = new File(args[argOffset + 2]);

	        			/* import course */
	        			Course course = new Course(restConnection);
	        			course.importCourse(outputFile, inputFile);
	        			
	        			/* everything went fine */
	        			actionValid = true;
	        		}
	        	}
	        }
        }
        
        /* check if argsValid still false */
        if(!outputValid || !actionValid){
        	printUsage();
        	
        	System.exit(1);
        }
        
        /* everything went fine */
        System.exit(0);
    }
}
