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

    public File getDirectory() { return _directory; }
    public Set<Paper> getPapers() { return _papers; }
    public Integer getSize() { return _papers.size(); }
    public boolean isPresent(Paper paper) { return _papers.contains(paper); }
    public boolean isPresent(String paperTitle) {
        //TODO Format paperTitle before testing
        for(Paper p : _papers) {
            if (p.getTitle().equals(paperTitle)) { return true; }
        }
        return false;
    }

    /**
     * @return Set of papers that are in the library's directory
     */
    private Set<Paper> loadPapers() {
        Set<Paper> papers = new HashSet<Paper>();
        List<File> filesInLibrary = FileHelper.listFiles(_directory, "pdf", true);
        for(File f : filesInLibrary) {
            Paper p = new Paper(f);
            papers.add(p);
            System.out.println(String.format("Loaded '%s' [%d/%d]", p.getTitle(), papers.size(), filesInLibrary.size()));
        }
        return papers;
    }
}
