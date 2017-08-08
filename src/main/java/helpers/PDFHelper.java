package helpers;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    public static String getTextWithoutReferences(File pdfFile) throws  IOException {
        List<String> references = new LinkedList<String>();
        String text = getText(pdfFile);
        text = text.replaceAll("\\r", "");
        String[] parts = text.split("(References|REFERENCES|Bibliography|BIBLIOGRAPHY)[\\n]*");
        return parts[0];
    }

    public static List<String> getSentences(File pdfFile) throws IOException {
        List<String> sentences = new ArrayList<String>();
        String textWithoutRefs = getTextWithoutReferences(pdfFile);

        //region Loading sentence detector model
        InputStream inputStream = new FileInputStream("./lib/english-sentences.zip");
        SentenceModel model = new SentenceModel(inputStream);
        SentenceDetectorME detector = new SentenceDetectorME(model);
        //endregion

        //Detecting the position of the sentences in the raw text
        Span spans[] = detector.sentPosDetect(textWithoutRefs);

        //Printing the spans of the sentences
        for (Span span : spans) {
            sentences.add(getSentenceFromSpan(textWithoutRefs, span));
        }

        return sentences;
    }

    public static boolean isReferenced(String paperTitle, List<String> references) {
        boolean isReferenced = false;
        for (String reference : references) {
            if (reference.toLowerCase().replaceAll("[^a-zA-Z0-9]", "").contains(paperTitle.toLowerCase().replaceAll("[^a-zA-Z0-9]", ""))) {
                isReferenced = true;
                break;
            }
        }
        return isReferenced;
    }

    public static String getReferenceIdentifier(String paperTitle, List<String> references) {
        for (String reference : references) {
            if (reference.toLowerCase().replaceAll("[^a-zA-Z0-9]", "").contains(paperTitle.toLowerCase().replaceAll("[^a-zA-Z0-9]", ""))) {
                int start = reference.indexOf("[") + 1;
                int end = reference.indexOf("]");
                return reference.substring(start, end);
            }
        }
        //TODO Throw "not found" exception
        return "";
    }

    public static boolean containsCitation(String sentence) {
        return sentence.matches(".*\\[.*\\].*");
    }

    //TODO Fix: currently [8] matches on [18] because [18] contains 8 and is between brackets
    public static boolean containsCitationToReference(String sentence, String referenceIdentifier) {
        return sentence.matches(".*\\[.*" + referenceIdentifier + "\\].*");
    }

    private static String getSentenceFromSpan(String text, Span span) {
        return text.substring(span.getStart(), span.getEnd()).replaceAll("\\n", " ").replaceAll("- ", "").trim();
    }

    public static List<String> getReferences(File pdfFile) throws IOException {
        List<String> references = new LinkedList<String>();
        String text = getText(pdfFile);
        text = text.replaceAll("\\r", "");
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

        return references;
    }
}
