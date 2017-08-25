package paperscout.data;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Paper {
    File _file;
    String _title;
    String _abstract;
    String _text;
    List<Reference> _references;

    public Paper(String path) {
        try { _file = new File(path); } catch (Exception e) { System.err.println(String.format("File '%s' not found", path)); }
        _title = extractTitle();
        _text = extractText();
        _abstract = extractAbstract();
        _references = extractReferences();
    }

    public Paper(File file) {
        new Paper(file.getAbsolutePath());
    }

    public String getTitle() { return _title; }
    public String getText() { return _text; }
    public String getAbstract() { return _abstract; }
    public List<Reference> getReferences() { return _references; }
    public File getFile() { return _file; }

    /**
     * @return Returns the paper's title
     */
    private String extractTitle() {
        return extractTitleFromFilename();
    }


    /**
     * @return Returns the paper's title from the file's name (assuming that it has the format "year - title.pdf")
     */
    private String extractTitleFromFilename() {
        String fileName = _file.getName();
        int indexAfterYear = 7;
        int indexBeforeFileExtension = fileName.length()-4;
        return fileName.substring(indexAfterYear, indexBeforeFileExtension);
    }

    private String extractText() {
        try {
            PDDocument doc = PDDocument.load(_file);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setParagraphEnd("\n");
            return stripper.getText(doc);
        } catch (IOException e) {
            System.err.println("Unable to load PDF file and extract its text");
        }
        return "";
    }

    private String extractAbstract() {
        return null;
    }

    private List<Reference> extractReferences() {
        List<Reference> references = new LinkedList<>();
        String text = _text.replaceAll("\\r", "");
        //Assumption: Papers only have one "reference" section
        String[] parts = text.split("(References|REFERENCES|Bibliography|BIBLIOGRAPHY)[\\n]*");

        for (String s : parts[parts.length-1].split("\\n\\n")) {
            if (s.startsWith("[")) {
                String r = s.replaceAll("\\n", " ");
                references.add(new Reference(r));
            }
            else {
                if (references.size() > 0) {
                    Reference lastAddedReference = references.get(references.size() - 1);
                    String lastRefAppendedWithNextLine = lastAddedReference.getText() + " " + s.replaceAll("\\n", " ");
                    references.set(references.size() - 1, new Reference(lastRefAppendedWithNextLine));
                }
            }
        }

        return references;
    }

    /**
     * @param p
     * @return Returns true if paper p references this paper
     */
    public boolean isReferencedBy(Paper p) {
        return p.references(this);
    }

    public boolean references(Paper p) {
        for (Reference r : _references) {
            if (r.getTitle() == p.getTitle()) { //TODO Do not use only the title (this may lead to collisions (i.e., two papers with the same name, but different authors)
                return true;
            }
        }
        return false;
    }
}
