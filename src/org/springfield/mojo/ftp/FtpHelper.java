package org.springfield.mojo.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;


/**
 * Helper class for basic ftp operations.
 *
 * @author Jaap Blom <j.blom@noterik.nl>
 * @author Levi Pires <l.pires@noterik.nl>
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.springfield.tools.ftp
 * @access private
 * @version $Id: FtpHelper.java,v 1.20 2011-12-20 09:00:35 derk Exp $
 *
 */
public class FtpHelper {

	/** the FtpHelper's log4j logger */
	private static Logger logger = Logger.getLogger(FtpHelper.class);
	
	/**
	 * Send file to ftp server
	 * 
	 * @param input
	 * @param outputDir
	 * @param host
	 * @param user
	 * @param pw
	 * @param deleteInput
	 * @deprecated
	 * 
	 * @return
	 */
	public static boolean sendFileWithFtp(String input, String outputDir, String host, String user, String pw, boolean deleteInput) {
		String lFolder = new File(input).getParent();
		String lFilename =  HttpHelper.getFileNameFromURL(input);
		String rFilename = HttpHelper.getFileNameFromURL(input);
		if(rFilename != null){
			if(rFilename.lastIndexOf(".") != -1){
				rFilename = rFilename.substring(rFilename.lastIndexOf("."), rFilename.length());
				rFilename = "raw" + rFilename;
			}
		}
		boolean isOk = commonsSendFile(host, user, pw, outputDir, lFolder, rFilename, lFilename, true);
		if(isOk && deleteInput){
			new File(input).delete();
		}
		return isOk;
	}
	
	/**
	 * Get a file from an ftp server 
	 * 
	 * @param remoteFile
	 * @param localDir
	 * @param host
	 * @param user
	 * @param pw
	 * @deprecated
	 * 
	 * @return
	 */
	public static boolean getFileWithFtp(String remoteFile, String localDir, String host, String user, String pw) {
		boolean isOk = false;
		
		// check local directory
		if (new File(localDir).exists() && new File(localDir).isDirectory()) {
			String rFolder = new File(remoteFile).getParent();
			String filename = new File(remoteFile).getName();
			
			// transfer
			isOk = commonsGetFile(host, user, pw, rFolder, localDir, filename);
		} else {
			logger.error("The local directory does not exist: " + localDir);
		}
		return isOk;
	}
	
	/**
	 * Get a file from an FTP server, using the commons library
	 * 
	 * @param server	ftp server location
	 * @param username 	user login information
	 * @param password 	user login information
	 * @param rFolder 	remote folder
	 * @param lFolder	local folder
	 * @param filename	name of file to retrieve
	 */
	public static boolean commonsGetFile(String server, String username, String password, String rFolder, String lFolder, String filename) {
		return commonsGetFile(server, username, password, rFolder, lFolder, filename,  filename);
	}
	
	/**
	 * Get a file from an FTP server, using the commons library
	 * 
	 * @param server	ftp server location
	 * @param username 	user login information
	 * @param password 	user login information
	 * @param rFolder 	remote folder
	 * @param lFolder	local folder
	 * @param rFilename	name of remote file to retrieve
	 * @param lFilename	name of local file to retrieve
	 */
	public static boolean commonsGetFile(String server, String username, String password, String rFolder, String lFolder, String rFilename,  String lFilename) {
		FTPClient ftp = null;
		int reply;
		boolean succes;
		
		try {			
			// Connect and logon to FTP Server
			ftp = new FTPClient();
			ftp.connect( server );
			ftp.login( username, password );
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			 
			// check if connected
			reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				logger.info("Could not connect to " + server + ".");
				logger.info("Reply code: "+reply);
				ftp.disconnect();
				return false;
			}

			logger.info("Connected to " + server + ".");
			logger.info(ftp.getReplyString());
			 
			// goto remote folder
			succes = ftp.changeWorkingDirectory( rFolder );			
			if(!succes) {
				reply = ftp.getReplyCode();
				ftp.disconnect();
				logger.info("Could not change working directory to " + rFolder + ".");
				logger.info("Reply code: "+reply);
				return false;
			}
			
			logger.info("Changed working directory to " + rFolder + ".");
			
			// download file
			File file = new File( lFolder + File.separator + lFilename );
			
			// create folder if not exists
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			
			FileOutputStream fos = new FileOutputStream( file ); 
			succes = ftp.retrieveFile( rFilename, fos );
			fos.close();
			if(!succes) {
				reply = ftp.getReplyCode();
				ftp.disconnect();
				logger.info("Downloading failed for file " + rFilename + ".");
				logger.info("Reply code: "+reply);
				return false;
			}
			
			logger.info("Downloading completed for file " + rFilename + ".");
			
			// logout
			ftp.logout();
		} catch(Exception e) {
			logger.error("",e);
			return false;
		} finally {
			// disconnect
			try {
				if(ftp!=null) {
					if(ftp.isConnected()) {
						ftp.disconnect();
					}
				}
			} catch(Exception e) {
				logger.error("",e);
			}
		}
		return true;
	}
	
	private static boolean commonsGetFile(FTPClient client, String lFilename, String rFilename) {
		logger.info("Downloading file " + rFilename + ".");
		
		// check if client is connected
		if(!client.isConnected()) {
			logger.error("ftp client was not connected");
			return false;
		}
		
		try {
			// download file
			FileOutputStream fos = new FileOutputStream( new File(lFilename) ); 
			boolean succes = client.retrieveFile( rFilename, fos );
			fos.close();
			if(!succes) {
				int reply = client.getReplyCode();
				logger.info("Downloading failed for file " + rFilename + ".");
				logger.info("Reply code: "+reply);
				return false;
			}
		} catch(Exception e) {
			logger.error("",e);
			return false;
		}
		
		logger.info("Downloading finished for file " + rFilename + ".");
		return true;
		
	}
	
	public static boolean commonsGetFolder(String server, String username, String password, String rFolder, String lFolder) {
		FTPClient ftp = null;
		int reply;
		boolean success;
		
		// check input folder
		File lFolderFile = new File(lFolder);
		if(!lFolderFile.exists()) {
			logger.error("Local folder does not exist");
			return false;
		}
		if(!lFolderFile.isDirectory()) {
			logger.error("Local folder is not a directory");
			return false;
		}
		
		try {			
			// Connect and logon to FTP Server
			ftp = new FTPClient();
			ftp.connect( server );
			ftp.login( username, password );
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			 
			// check if connected
			reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				logger.info("Could not connect to " + server + ".");
				return false;
			}

			logger.info("Connected to " + server + ".");
			
			// get folder
			success = commonsGetFolder(ftp, rFolder, lFolder);
			
			// logout
			ftp.logout();
		} catch(Exception e) {
			logger.error("",e);
			return false;
		} finally {
			// disconnect
			try {
				if(ftp!=null) {
					if(ftp.isConnected()) {
						ftp.disconnect();
					}
				}
			} catch(Exception e) {
				logger.error("",e);
			}
		}
		return success;
	}
	
	private static boolean commonsGetFolder(FTPClient client, String rFolder, String lFolder) {
		logger.info("Getting folder " + rFolder + ".");
		
		// check if client is connected
		if(!client.isConnected()) {
			logger.error("ftp client was not connected");
			return false;
		}
		
		try {
			// create local folder
			new File(lFolder).mkdir();
			
			// move to current folder
			client.changeWorkingDirectory(rFolder);
			
			// loop through remote folder
			FTPListParseEngine engine = client.initiateListParsing();
			
			// loop through files
			while(engine.hasNext()) {
				FTPFile[] files = engine.getNext(10);
				for(FTPFile file : files) {
					if(file.isDirectory()) {
						String childFolder = lFolder + "/" + file.getName();
						commonsGetFolder(client, childFolder, file.getName());
					} else {
						String lFilename = lFolder + "/" + file.getName();
						commonsGetFile(client, lFilename, file.getName());
					}
				}
			}
			
			// move back to parent folder
			client.changeToParentDirectory();
		} catch(Exception e) {
			logger.error("Could not upload folder",e);
			return false;
		}
		return true;
	}
	
	public static boolean commonsSendFile(String server, String username, String password, String rFolder, String lFolder, String filename){
		return commonsSendFile(server, username, password, rFolder, lFolder, filename, false);
	}
	
	public static boolean commonsSendFile(String server, String username, String password, String rFolder, String lFolder, String filename, boolean recursiveCreate) {
		return commonsSendFile(server, username, password, rFolder, lFolder, filename, filename, recursiveCreate);
	}
	
	/**
	 * Send a file to an FTP server, using the commons library
	 * 
	 * @param server	ftp server location
	 * @param username 	user login information
	 * @param password 	user login information
	 * @param rFolder 	remote folder
	 * @param lFolder	local folder
	 * @param filename	name of file to store
	 */
	public static boolean commonsSendFile(String server, String username, String password, String rFolder, String lFolder, String rFilename, String lFilename, boolean recursiveCreate) {
		FTPClient ftp = null;
		int reply;
		boolean succes;
		
		try {			
			// Connect and logon to FTP Server
			ftp = new FTPClient();
			ftp.connect( server );
			ftp.login( username, password );
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			 
			// check if connected
			reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				logger.info("Could not connect to " + server + ".");
				ftp.disconnect();
				return false;
			}

			logger.info("Connected to " + server + ".");
			
			// remove the slash from the beginning of the uri
			String noSlash = URIParser.removeFirstSlash(rFolder);
			logger.debug("Removed the first slash from uri");
			
			// goto remote folder (create the folder if necessary)
			if( recursiveCreate ){
				String[] parts = noSlash.split("/");
				
				// strip the folder in parts and create them recursively
				for( String part : parts ){
					if( !(ftp.changeWorkingDirectory( part ) || (ftp.makeDirectory( part ) && ftp.changeWorkingDirectory( part ))) ){
						logger.info("Could not create working directory: " + noSlash + ", specifically directory "+part);
						ftp.disconnect();				
						return false;
					}
				}
			}else{				
				succes = ftp.changeWorkingDirectory( noSlash ) || (ftp.makeDirectory( noSlash ) && ftp.changeWorkingDirectory( noSlash ));
				if(!succes) {
					logger.info("Could not change working directory to " + noSlash + ".");
					ftp.disconnect();				
					return false;
				}
			}
			logger.info("Changed working directory to " + rFolder + ".");
			
			// upload file
			File file = new File( lFolder + File.separator + lFilename );
			FileInputStream fis = new FileInputStream( file ); 
			succes = ftp.storeFile( rFilename, fis );
			fis.close();
			if(!succes) {
				reply = ftp.getReplyCode();
				ftp.disconnect();
				logger.info("Uploading failed for file " + lFilename + ".");
				logger.info("Reply code: "+reply);
				return false;
			}
			
			logger.info("Uploading finished for file " + lFilename + ".");
			
			// logout
			ftp.logout();
		} catch(Exception e) {
			logger.error("",e);
			return false;
		} finally {
			// disconnect
			try {
				if(ftp!=null) {
					if(ftp.isConnected()) {
						ftp.disconnect();
					}
				}
			} catch(Exception e) {
				logger.error("",e);
			}
		}
		return true;
	}
	
	/**
	 * Send a complete folder, including subfolders to an FTP server, using the commons library
	 * 
	 * @param server
	 * @param username
	 * @param password
	 * @param rFolder
	 * @param lFolder
	 * @return
	 */
	public static boolean commonsSendFolder(String server, String username, String password, String rFolder, String lFolder) {
		return commonsSendFolder(server, username, password, rFolder, lFolder, false);
	}
	
	/**
	 * Send a complete folder, including subfolders to an FTP server, using the commons library
	 * 
	 * @param server
	 * @param username
	 * @param password
	 * @param rFolder
	 * @param lFolder
	 * @return
	 */
	public static boolean commonsSendFolder(String server, String username, String password, String rFolder, String lFolder, boolean recursiveCreate) {
		FTPClient ftp = null;
		int reply;
		boolean succes;
		
		// check input folder
		File lFolderFile = new File(lFolder);
		if(!lFolderFile.exists()) {
			logger.error("Local folder does not exist");
			return false;
		}
		if(!lFolderFile.isDirectory()) {
			logger.error("Local folder is not a directory");
			return false;
		}
		
		try {			
			// Connect and logon to FTP Server
			ftp = new FTPClient();
			ftp.connect( server );
			ftp.login( username, password );
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			 
			// check if connected
			reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				logger.info("Could not connect to " + server + ".");
				return false;
			}

			logger.info("Connected to " + server + ".");
			
			// remove the slash from the beginning of the uri
			String noSlash = URIParser.removeFirstSlash(rFolder);
			logger.debug("Removed the first slash from uri");
			if( recursiveCreate ) {
				// goto remote folder (create the folder if necessary)
				String[] parts = noSlash.split("/");
				
				// strip the folder in parts and create them recursively
				for( String part : parts ){
					if( !(ftp.changeWorkingDirectory( part ) || (ftp.makeDirectory( part ) && ftp.changeWorkingDirectory( part ))) ){
						logger.info("Could not create working directory: " + noSlash + ", specifically directory "+part);	
						return false;
					}
				}
			} else{				
				succes = ftp.changeWorkingDirectory( noSlash ) || (ftp.makeDirectory( noSlash ) && ftp.changeWorkingDirectory( noSlash ));
				if(!succes) {
					logger.info("Could not change working directory to " + noSlash + ".");
					ftp.disconnect();				
					return false;
				}
			}
			logger.info("Changed working directory to " + rFolder + ".");
			
			// send folder
			commonsSendFolder(ftp, lFolderFile);
			
			// logout
			ftp.logout();
		} catch(Exception e) {
			logger.error("",e);
			return false;
		} finally {
			// disconnect
			try {
				if(ftp!=null) {
					if(ftp.isConnected()) {
						ftp.disconnect();
					}
				}
			} catch(Exception e) {
				logger.error("",e);
			}
		}
		return true;
	}
	
	/**
	 * Send a complete folder, including subfolders to an FTP server, using the commons library
	 * 
	 * @param client
	 * @param lFolderFile
	 * @return
	 */
	private static boolean commonsSendFolder(FTPClient client, File lFolderFile) {
		logger.info("Uploading folder " + lFolderFile.getName() + ".");
		
		// check if client is connected
		if(!client.isConnected()) {
			logger.error("ftp client was not connected");
			return false;
		}
		
		try {
			// create folder
			client.makeDirectory(lFolderFile.getName());
			client.changeWorkingDirectory(lFolderFile.getName());
			
			// loop through local folder
			File[] list = lFolderFile.listFiles();
			for(File file : list) {
				if(file.isDirectory()) {
					commonsSendFolder(client, file);
				} else {
					commonsSendFile(client, file, file.getName());
				}
			}
			
			// return to parent folder
			client.changeToParentDirectory();
		} catch(Exception e) {
			logger.error("Could not upload folder",e);
			return false;
		}
		return true;
	}
	
	/**
	 * Send a file to an FTP server, using the commons library
	 * 
	 * @param client
	 * @param localFile
	 * @param remoteFilename
	 * @return
	 */
	private static boolean commonsSendFile(FTPClient client, File localFile, String remoteFilename) {
		logger.info("Uploading file " + remoteFilename + ".");
		
		// check if client is connected
		if(!client.isConnected()) {
			logger.error("ftp client was not connected");
			return false;
		}
		
		try {
			// upload file
			FileInputStream fis = new FileInputStream( localFile ); 
			boolean succes = client.storeFile( remoteFilename, fis );
			fis.close();
			if(!succes) {
				int reply = client.getReplyCode();
				logger.info("Uploading failed for file " + remoteFilename + ".");
				logger.info("Reply code: "+reply);
				return false;
			}
		} catch(Exception e) {
			logger.error("",e);
			return false;
		}
		
		logger.info("Uploading finished for file " + remoteFilename + ".");
		return true;
	}
}