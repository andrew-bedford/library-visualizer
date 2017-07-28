package paperscout;

import helpers.PDFHelper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileName = "C:\\Users\\Andrew Bedford\\OneDrive\\Library\\2016 - TaintART - A practical multi-level information-flow tracking system for android runtime.pdf";
        try {
            String text = PDFHelper.getText(new File(fileName));
            //System.out.println("Text in PDF: " + text);

            System.out.println("-------------------------------------------------------------");
            List<String> references = PDFHelper.getReferences(new File(fileName));
            for (String r : references) {
                System.out.println("Reference : " + r.replace("\n", " "));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
