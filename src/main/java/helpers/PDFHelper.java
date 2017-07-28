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
        String[] parts = text.split("(References|REFERENCES)[\\n]*\\[");

        return Arrays.asList(parts[parts.length].split("\n\n"));
    }
}
