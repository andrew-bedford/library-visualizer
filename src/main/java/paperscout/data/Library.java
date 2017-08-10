package paperscout.data;

import helpers.FileHelper;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Library {
    private File _directory;
    private Set<Paper> _papers;


    /**
     * @param path Path to the directory containing the library's papers
     */
    public Library(String path) {
        _directory = new File(path);
        _papers = loadPapers();
    }


    /**
     * @return Set of papers that are in the library's directory
     */
    private Set<Paper> loadPapers() {
        Set<Paper> papers = new HashSet<Paper>();
        List<File> filesInLibrary = FileHelper.listFiles(_directory, "pdf", true);
        for(File f : filesInLibrary) {
            papers.add(new Paper(f));
        }
        return papers;
    }
}
