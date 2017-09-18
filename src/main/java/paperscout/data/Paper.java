package paperscout.data;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import pl.edu.icm.cermine.ContentExtractor;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.model.BibEntryFieldType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class Paper {
    File _file;
    String _title;
    String _abstract;
    String _text;
    List<Reference> _references;

    public Paper(File file) {
        this(file.getAbsolutePath());
    }

    public Paper(String path) {
        try { _file = new File(path); } catch (Exception e) { System.err.println(String.format("File '%s' not found", path)); }
        _title = extractTitle();
        _text = extractText();
        _abstract = extractAbstract();
        _references = extractReferences();
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
        //return extractTitleFromFilename();
        return extractTitleUsingCermine();
    }


    private String extractTitleUsingCermine() {
        try {
            ContentExtractor extractor = new ContentExtractor();
            InputStream inputStream = new FileInputStream(_file.getAbsolutePath());
            extractor.setPDF(inputStream);
            return extractor.getMetadata().getTitle();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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

    //TODO Implement extractAbstract function (extract the text between the keywords Abstract and Introduction?)
    private String extractAbstract() {
        return "";
    }

    //TODO Use Cermine instead of manually extracting containsReferenceTo. I expect Cermine to be more accurate.
    /*
    private List<Reference> extractReferences() {
        List<Reference> references = new LinkedList<>();
        String text = _text.replaceAll("\\r", "");
        //Assumption: Papers only have one "reference" section
        String[] parts = text.split("(References|REFERENCES|Bibliography|BIBLIOGRAPHY)[\\n]*");

        for (String s : parts[parts.length-1].split("\\n\\n")) {
            if (s.startsWith("[")) { //We assume that every containsReferenceTo have the following form "[...] ..."
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
    }*/

    private List<Reference> extractReferences() {
        List<Reference> references = new LinkedList<>();
        try {
            ContentExtractor extractor = new ContentExtractor();
            InputStream inputStream = new FileInputStream(_file.getAbsolutePath());
            extractor.setPDF(inputStream);
            List<BibEntry> cermineReferences = extractor.getReferences();
            for (BibEntry cermineReference : cermineReferences) {
                references.add(new Reference(cermineReference));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return references;
    }

    /**
     * @param p
     * @return Returns true if paper p containsReferenceTo this paper
     */
    public boolean isReferencedBy(Paper p) {
        return p.containsReferenceTo(this);
    }

    public boolean containsReferenceTo(Paper p) {
        for (Reference r : _references) {
            String normalizedPaperTitle = p.getTitle().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
            String normalizedReferenceText = r.getText().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");

            if (normalizedReferenceText.contains(normalizedPaperTitle)) { //TODO Do not use only the title (this may lead to collisions (i.e., two papers with the same name, but different authors)
                return true;
            }
        }
        return false;
    }
}
