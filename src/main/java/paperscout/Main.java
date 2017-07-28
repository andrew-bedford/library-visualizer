package paperscout;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String fileName = "C:\\Users\\Andrew Bedford\\OneDrive\\Library\\2016 - TaintART - A practical multi-level information-flow tracking system for android runtime.pdf";
        try {
            String text = getText(new File(fileName));
            System.out.println("Text in PDF: " + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String getText(File pdfFile) throws IOException {
        PDDocument doc = PDDocument.load(pdfFile);
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setParagraphEnd("\n");
        return stripper.getText(doc);
    }
}
