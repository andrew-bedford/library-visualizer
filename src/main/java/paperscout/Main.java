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

            //TODO Verify why '2008 -  Formalizing non-interference for a simple bytecode language in Coq.pdf' returns that the reference id is Pie02 instead of SM03
            //paperTitle = "Language-Based Information-Flow Security";
            //paperTitle = "A Model for Delimited Information Release";
            //paperTitle = "Approximate Non-Interference";
            //paperTitle = "Dynamic intransitive noninterference";
            paperTitle = "RobotDroid: A Lightweight Malware Detection Framework on Smartphones"; //(2012) No results
            //paperTitle = "Malware Obfuscation Techniques: A Brief Survey"; //(2010) 1 result
            paperTitle = "Static Analysis of Implicit Control Flow: Resolving Java Reflection and Android Intents";
            List<File> filesInLibrary = FileHelper.listFiles(new File("C:\\Users\\Andrew\\OneDrive\\Library"), "pdf", true);
            for (File f : filesInLibrary) {
                references = PDFHelper.getReferences(f);
                if (references.size() > 0) {
                    boolean paperIsReferenced = PDFHelper.isReferenced(paperTitle, references);
                    if (paperIsReferenced) {
                        System.out.println("-------------------------------------------------------------");
                        String referenceId = PDFHelper.getReferenceIdentifier(paperTitle, references);
                        System.out.println("Paper Scout: '" + f.getName() + "' references '" + paperTitle + "' as [" + referenceId + "]");
                        List<String> sentences = PDFHelper.getSentences(f);
                        for (String sentence : sentences) {
                            if (PDFHelper.containsCitationToReference(sentence, referenceId)) {
                                System.out.print("\t - " + sentence);

                                int currentSentenceIndex = sentences.indexOf(sentence);
                                int nextSentenceIndex = currentSentenceIndex + 1;
                                if (nextSentenceIndex < sentences.size()) {
                                    String nextSentence = sentences.get(nextSentenceIndex);
                                    if (PDFHelper.containsCitation(nextSentence) == false) {
                                        System.out.println(" " + nextSentence);
                                    }
                                    else {
                                        System.out.println("");
                                    }
                                }

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
