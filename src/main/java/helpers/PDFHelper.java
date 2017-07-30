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

    public static void getSentences(File pdfFile) throws IOException {
        String textWithoutRefs = getTextWithoutReferences(pdfFile);

        //region Loading sentence detector model
        InputStream inputStream = new FileInputStream("./lib/english-sentences.zip");
        SentenceModel model = new SentenceModel(inputStream);
        SentenceDetectorME detector = new SentenceDetectorME(model);
        //endregion

        //Detecting the position of the sentences in the raw text
        Span spans[] = detector.sentPosDetect(textWithoutRefs);

        //Printing the spans of the sentences in the paragraph
        for (Span span : spans) {

            System.out.println("Sentence: " + textWithoutRefs.substring(span.getStart(), span.getEnd()).replaceAll("\\n", " ").replaceAll("- ", "").trim());
        }
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
                String lastAddedReference = references.get(references.size()-1);
                lastAddedReference += " " + s.replaceAll("\\n", " ");
                references.set(references.size()-1, lastAddedReference);
            }
        }

        return references;
    }
}
