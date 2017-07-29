package helpers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PDFHelper {
    public static String getText(File pdfFile) throws IOException {
        PDDocument doc = PDDocument.load(pdfFile);
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setParagraphEnd("\n");
        return stripper.getText(doc);
    }

    public static List<String> getReferences(File pdfFile) throws IOException {
        List<String> references = new LinkedList<String>();
        String text = getText(pdfFile);
        text = text.replaceAll("\\r", "");
        String[] parts = text.split("(References|REFERENCES)[\\n]*");

        for (String s : parts[parts.length-1].split("\\n\\n")) {
            if (s.startsWith("[")) {
                references.add(s.replaceAll("\\n", " "));
            }
            else {
                String lastAddedReference = references.get(references.size()-1);
                lastAddedReference += " " + s.replaceAll("\\n", " ");
                references.set(references.size()-1, lastAddedReference);
            }
        }

        return references;
    }
}
