package com.frentix.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.olat.restapi.support.vo.CourseVO;
import org.olat.user.restapi.UserVO;

/**
 * Some helper methods for handling comma separated values (CSV) files.
 * 
 * @author jkraehemann, joel.kraehemann@frentix.com, http://www.frentix.com
 *
 */
public class CsvHelper {

	/**
	 * Reads a CSV file into a string array.
	 * 
	 * @param csvFile the file to be read
	 * @param separator the field separator
	 * @return a string array whereas each field has its own entry
	 * @throws IOException
	 */
	public static String[] readCsv(File csvFile, char separator) throws IOException{
		return(IOUtils.toString(csvFile.toURI()).split("[^\\]" + separator));
	}
	
	/**
	 * Writes the user's attributes to CSV ouput stream.
	 * 
	 * @param output the OuputStream
	 * @param separator the field separator
	 * @param user the UserVO
	 * @throws IOException
	 */
	public static void writeUserToCsv(OutputStream output, char separator,
			UserVO user) throws IOException{
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(user.getKey())
		.append(separator)
		.append(user.getLogin())
		.append(separator)
		.append(user.getPassword())
		.append(separator)
		.append('"')
		.append(user.getFirstName())
		.append('"')
		.append(separator)
		.append('"')
		.append(user.getLastName())
		.append('"')
		.append(separator)
		.append(user.getEmail())
		.append(separator)
		.append('"')
		.append(user.getPortrait())
		.append('"')
		.append('\n');
		
		IOUtils.write(stringBuilder, output);
	}
	
	/**
	 * Writes the user's attributes to CSV file you may specify whether
	 * to overwrite or to append the file.
	 * 
	 * @param csvFile the file to write
	 * @param separator the field separator
	 * @param append if false the file will be deleted first else the user will be append to end
	 * @param user the UserVO
	 * @throws IOException
	 */
	public static void writeUserToCsv(File csvFile, char separator,
			boolean append,
			UserVO user) throws IOException{
		if(!append){
			if(csvFile.exists()){
				csvFile.delete();
			}
			
			csvFile.createNewFile();
		}
		
		OutputStream output = new FileOutputStream(csvFile, append);
		
		writeUserToCsv(output, separator,
				user);
	}
	
	/**
	 * Writes the attributes of the users to CSV file you may specify whether
	 * to overwrite or to append the file.
	 * 
	 * @param csvFile the file to write
	 * @param separator the field separator
	 * @param append if false the file will be deleted first else the users will be append to end
	 * @param user the UserVOes
	 * @throws IOException
	 */
	public static void writeUserToCsv(File csvFile, char separator,
			boolean append,
			List<UserVO> user) throws IOException{
		if(!append){
			if(csvFile.exists()){
				csvFile.delete();
			}
			
			csvFile.createNewFile();
		}
		
		OutputStream output = new FileOutputStream(csvFile, append);
		
		for(UserVO currentUser: user){
			writeUserToCsv(output, separator,
					currentUser);
		}
	}
	
	/**
	 * Writes the course's attributes to CSV output stream.
	 * 
	 * @param output the OutputStream
	 * @param separator the field separator
	 * @param course the CourseVO
	 * @throws IOException
	 */
	public static void writeCourseToCsv(OutputStream output, char separator,
			CourseVO course) throws IOException{
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(course.getKey())
		.append(separator)
		.append(course.getSoftKey())
		.append(separator)
		.append(course.getRepoEntryKey())
		.append(separator)
		.append('"')
		.append(course.getTitle())
		.append('"')
		.append(separator)
		.append('"')
		.append(course.getDisplayName())
		.append('"')
		.append('\n');
		
		IOUtils.write(stringBuilder, output);
	}
	
	/**
	 * Writes the course's attributes to CSV file you may specify whether
	 * to overwrite or to append the file.
	 * 
	 * @param csvFile
	 * @param separator
	 * @param append
	 * @param course
	 * @throws IOException
	 */
	public static void writeCourseToCsv(File csvFile, char separator,
			boolean append,
			CourseVO course) throws IOException{
		if(!append){
			if(csvFile.exists()){
				csvFile.delete();
			}
			
			csvFile.createNewFile();
		}
		
		OutputStream output = new FileOutputStream(csvFile, append);
		
		writeCourseToCsv(output, separator, course);
	}
}
