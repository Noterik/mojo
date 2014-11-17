package org.springfield.mojo.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.FileChannel;

/**
 * Class that supports certain file operations
 * 
 * @author Jaap Blom <j.blom@noterik.nl>
 * @author Levi Pires <l.pires@noterik.nl>
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2010
 * @package com.noterik.springfield.tools
 * @access private
 *
 */
public class FileHelper {

	public static String VIDEO_FILE = "Video";
	public static String IMAGE_FILE = "Image";
	public static String AUDIO_FILE = "Audio";

	public static boolean isValidFile(String type, String file) {
		if (file == null || file.indexOf(".") == -1) {
			return false;
		}
		String ext = file.substring(file.lastIndexOf(".") + 1).toLowerCase();
		if (type.equals(IMAGE_FILE)) {
			if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("bmp") || ext.equals("gif")) {
				return true;
			}
		} else {
			if (ext.equals("wmv") || ext.equals("mpg") || ext.equals("mpeg") || ext.equals("mp4") || ext.equals("mov")
					|| ext.equals("avi") || ext.equals("flv") || ext.equals("m4v") || ext.equals("f4v") || ext.equals("3gp")) {
				return true;
			}
		}
		return false;
	}

	public static String getFileType(String file) {
		if (file == null || file.indexOf(".") == -1) {
			return null;
		}
		String ext = file.substring(file.lastIndexOf(".") + 1).toLowerCase();
		if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("bmp") || ext.equals("gif")) {
			return IMAGE_FILE;
		}
		if (ext.equals("wmv") || ext.equals("mpg") || ext.equals("mpeg") || ext.equals("mp4") || ext.equals("mov")
				|| ext.equals("avi") || ext.equals("flv") || ext.equals("m4v") || ext.equals("f4v")) {
			return VIDEO_FILE;
		}
		if (ext.equals("mp3") || ext.equals("wma") || ext.equals("flac") || ext.equals("mid") || ext.equals("wav")) {
			return AUDIO_FILE;
		}
		return null;
	}

	public static long getFileSize(String file) {
		File f = new File(file);
		if (f.exists()) {
			return f.length();
		}
		return -1;
	}

	public static boolean saveStringToFile(String fileToSave, String data) {
		Writer fw = null;
		try {
			fw = new FileWriter(fileToSave);
			fw.write(data);
			fw.append('\n');
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fw != null)
				try {
					fw.close();
				} catch (IOException e) {
				}
		}
		return true;
	}

	public static String getFileNameFromPath(String path) {
		if (path == null || (path.indexOf("/") == -1 && path.indexOf("\\") == -1)) {
			return null;
		}
		if (path.indexOf("/") != -1) {
			return path.substring(path.lastIndexOf("/") + 1);
		}
		if (path.indexOf("\\") != -1) {
			return path.substring(path.lastIndexOf("\\") + 1);
		}
		return null;
	}

	public static String getExtension(String fileName) {
		if (fileName != null) {
			if (fileName.lastIndexOf(".") > 0) {
				String ft = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
				return ft.replaceAll(" ", "");
			}
		}
		return null;
	}
	
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        } 
        return dir.delete();
    }
	
	public static boolean moveFile(String fromFile, String toFile) {
		if (fromFile == null || toFile == null) {
			return false;
		}
		return moveFile(new File(fromFile), new File(toFile));
	}

	public static boolean moveFile(File fromFile, File toFile) {
		if (fromFile == null || toFile == null) {
			return false;
		}
		return fromFile.renameTo(toFile);
	}
	
	public static boolean copyFile(String in, String out) {
		File inFile = new File(in);
		File outFile = new File(out);
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			inChannel = new FileInputStream(inFile).getChannel();
			outChannel = new FileOutputStream(outFile).getChannel();
			
			// hack for files larger than 64Mb
	        int maxCount = (64 * 1024 * 1024) - (32 * 1024);
	        long size = inChannel.size();
	        long position = 0;
	        while (position < size) {
	        	position += inChannel.transferTo(position, maxCount, outChannel);
	        }
		} catch (IOException e) {
			return false;
		} finally {
			if (inChannel != null) {
				try {
					inChannel.close();
				} catch(Exception e) {}
			}
			if (outChannel != null) {
				try {
					outChannel.close();
				} catch(Exception e) {}
			}
		}
		return true;
	}
}
