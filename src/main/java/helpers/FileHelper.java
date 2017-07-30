package helpers;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class FileHelper {
		
	public static String convertFileToString(File file) {
		try {
			byte[] bytes = Files.readAllBytes(file.toPath());
			String contents =  new String(bytes,"UTF-8");
			return contents;
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * Searches for files with a specific extension in a folder
	 * @return List of file found
	 */
	public static List<File> listFiles(File folder, String extension, boolean searchInSubdirectories) {
		List<File> filesFound = new LinkedList<File>();
		Queue<File> foldersToExplore = new LinkedList<File>();
		foldersToExplore.add(folder);
		while (!foldersToExplore.isEmpty()) {
			for (File file : foldersToExplore.poll().listFiles()) {
				if (file.isDirectory() && searchInSubdirectories == true) {
					foldersToExplore.add(file);
				} 
				else if (file.isFile() && file.toString().endsWith(extension)) {
					filesFound.add(file);
				}
			}
		}
		return filesFound;
	}

	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    for (File myFile: files) {
	        if (myFile.isDirectory()) {  
	            deleteFolder(myFile);
	        } 
	        myFile.delete();
	    }
	}
}
