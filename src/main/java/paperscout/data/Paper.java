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
        _file = new File(path);
        _title = extractTitle();
        _text = extractText();
        _abstract = extractAbstract();
        _references = extractReferences();
    }

    public Paper(File file) {
        new Paper(file.getAbsolutePath());
    }

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
        List<String> references = new LinkedList<String>();
        String text = _text.replaceAll("\\r", "");
        String[] parts = text.split("(References|REFERENCES|Bibliography|BIBLIOGRAPHY)[\\n]*");

        for (String s : parts[parts.length-1].split("\\n\\n")) {
            if (s.startsWith("[")) {
                references.add(s.replaceAll("\\n", " "));
            }
            else {
                if (references.size() > 0) {
                    String lastAddedReference = references.get(references.size() - 1);
                    lastAddedReference += " " + s.replaceAll("\\n", " ");
                    references.set(references.size() - 1, lastAddedReference);
                }
            }
        }

        return null;
    }
}
