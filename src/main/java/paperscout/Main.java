package paperscout;

import helpers.FileHelper;
import helpers.PDFHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(Level.SEVERE);
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

            //Should return 6
            String paperTitle = "What the app is that? deception and countermeasures in the android user interface";
            if (PDFHelper.isReferenced(paperTitle, references)) {
                System.out.println("Identifier: " + PDFHelper.getReferenceIdentifier(paperTitle, references));
            }
            System.out.println("-------------------------------------------------------------");

            paperTitle = "Language-Based Information-Flow Security";
            List<File> filesInLibrary = FileHelper.listFiles(new File("C:\\Users\\Andrew\\OneDrive\\Library"), "pdf", true);
            for (File f : filesInLibrary) {
                System.out.println("-------------------------------------------------------------");
                System.out.println("Analyzing " + f.getName());
                references = PDFHelper.getReferences(f);
                if (references.size() > 0) {
                    boolean paperIsReferenced = PDFHelper.isReferenced(paperTitle, references);
                    if (paperIsReferenced) {
                        String referenceId = PDFHelper.getReferenceIdentifier(paperTitle, references);
                        System.out.println("RESULT: '" + f.getName() + "' references '" + paperTitle + "'");
                        List<String> sentences = PDFHelper.getSentences(f);
                        for (String sentence : sentences) {
                            if (PDFHelper.containsCitationToReference(sentence, referenceId)) {
                                System.out.println("\t" + sentence);
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
