package helpers;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FileHelper {


		/**
         * @param file The file to read from
         * @return A string containing the contents of the file
         */
	public static String readStringFromFile(File file) {
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

	public static String readStringFromFile(String filePath) {
		File f = new File(filePath);
		return readStringFromFile(f);
	}


	/**
	 * @param filePath The path of the file where the contents of the string will be written. The file does not need to exist beforehand
	 * @param data The string that we want to write
	 */
	public static void writeStringToFile(String filePath, String data) {
		try {PrintStream ps = new PrintStream(filePath); ps.println(data);}
		catch (Exception e) { System.err.println(e); }
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
