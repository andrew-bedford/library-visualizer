package paperscout;

import helpers.PDFHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileName = "C:\\Users\\Andrew\\OneDrive\\Library\\2016 - TaintART - A practical multi-level information-flow tracking system for android runtime.pdf";
        //String fileName = "C:\\Users\\Andrew\\OneDrive\\Library\\2012 - Precise enforcement of progress-sensitive security.pdf";
        //String fileName = "C:\\Users\\Andrew\\OneDrive\\Library\\2003 - Language-Based Information-Flow Security.pdf";

        try {
            String text = PDFHelper.getText(new File(fileName));
            //System.out.println("Text in PDF: " + text);

            System.out.println("-------------------------------------------------------------");

            List<String> references = PDFHelper.getReferences(new File(fileName));
            for (String r : references) {
                System.out.println("Reference : " + r);
            }
            System.out.println("-------------------------------------------------------------");

            PDFHelper.getSentences(new File(fileName));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
