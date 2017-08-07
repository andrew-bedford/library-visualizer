package paperscout;

import helpers.FileHelper;
import helpers.PDFHelper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(Level.SEVERE);
        //String fileName = "C:\\Users\\Andrew Bedford\\OneDrive\\Library\\2016 - TaintART - A practical multi-level information-flow tracking system for android runtime.pdf";
        //String fileName = "C:\\Users\\Andrew\\OneDrive\\Library\\2012 - Precise enforcement of progress-sensitive security.pdf";
        //String fileName = "C:\\Users\\Andrew\\OneDrive\\Library\\2003 - Language-Based Information-Flow Security.pdf";

        try {
            /*
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
            */
            //TODO Verify why '2008 -  Formalizing non-interference for a simple bytecode language in Coq.pdf' returns that the reference id is Pie02 instead of SM03
            //paperTitle = "Language-Based Information-Flow Security";
            //paperTitle = "A Model for Delimited Information Release";
            //paperTitle = "Approximate Non-Interference";
            //paperTitle = "Dynamic intransitive noninterference";
            //paperTitle = "RobotDroid: A Lightweight Malware Detection Framework on Smartphones"; //(2012) No results
            //paperTitle = "Malware Obfuscation Techniques: A Brief Survey"; //(2010) 1 result
            //paperTitle = "Static Analysis of Implicit Control Flow: Resolving Java Reflection and Android Intents";

            HashMap<String, HashSet<String>> relationMap = new HashMap<String, HashSet<String>>();
            HashMap<String, String> resultsMap = new HashMap<String, String>();

            List<File> filesInLibrary = FileHelper.listFiles(new File("C:\\Users\\Andrew Bedford\\OneDrive\\Library\\PaperScout"), "pdf", true);
            int fileNumber = 1;
            for (File f1 : filesInLibrary) {
                String paperTitle = getPaperTitle(f1);
                System.out.println(String.format("Analyzing '%s' [%d/%d]", paperTitle, fileNumber, filesInLibrary.size()));
                fileNumber++;

                relationMap.put(paperTitle, new HashSet<String>());

                List<String> references;
                String results = "";

                for (File f : filesInLibrary) {
                    references = PDFHelper.getReferences(f);
                    if (references.size() > 0) {
                        boolean paperIsReferenced = PDFHelper.isReferenced(paperTitle, references);
                        if (paperIsReferenced) {
                            relationMap.get(paperTitle).add(getPaperTitle(f));

                            results += "<div class=\"reference\" data-title=\""+getPaperTitle(f)+"\">";
                            String referenceId = PDFHelper.getReferenceIdentifier(paperTitle, references);
                            results += "<span class=\"reference-header\">In '" + getPaperTitle(f) + "' as [" + referenceId + "]</span>";
                            List<String> sentences = PDFHelper.getSentences(f);
                            results += "<ul>";
                            for (String sentence : sentences) {
                                if (PDFHelper.containsCitationToReference(sentence, referenceId)) {
                                    results += "<li>" + sentence;

                                    int currentSentenceIndex = sentences.indexOf(sentence);
                                    int nextSentenceIndex = currentSentenceIndex + 1;
                                    if (nextSentenceIndex < sentences.size()) {
                                        String nextSentence = sentences.get(nextSentenceIndex);
                                        if (PDFHelper.containsCitation(nextSentence) == false) {
                                            results += " " + nextSentence + "</li>";
                                        }
                                        else {
                                            results += "</li>";
                                        }
                                    }
                                }
                            }
                            results += "</ul>";
                            results += "</div>"; //Closes div.reference
                        }
                    }
                }
                resultsMap.put(paperTitle, results);
            }

            generateVisJsGraph(relationMap, resultsMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void generateVisJsGraph(HashMap<String, HashSet<String>> relationMap, HashMap<String, String> resultsMap) {
        //Associate IDs to each paper
        HashMap<String, Integer> idMap = new HashMap<String, Integer>();
        int id = 1;
        for (String paper : relationMap.keySet()) {
            idMap.put(paper, id);
            id++;
        }

        String output = "";

        //Generate nodes
        output = "nodes = new vis.DataSet([\n";
        for (String paperTitle : relationMap.keySet()) {
            int paperID = idMap.get(paperTitle);
            int numberOfReferences = relationMap.get(paperTitle).size(); //Number of papers that reference "paper"
            String paperResults = resultsMap.get(paperTitle).replaceAll("'", "\\\\'").replaceAll("\n", "&#13;");
            output += String.format("{ id: %d, value: %d, label: '%s', title:'%s', results: '%s' },\n", paperID, numberOfReferences, paperTitle.replaceAll("'", "\\\\'"), paperTitle.replaceAll("'", "\\\\'"), paperResults);
        }
        output = output.substring(0,output.length()-2);
        output += "]);\n";

        output += "edges = new vis.DataSet([\n";
        for (String paperTitle : relationMap.keySet()) {
            for (String paperThatReferences : relationMap.get(paperTitle)) {
                int paperID = idMap.get(paperTitle);
                int paperThatReferencesID = idMap.get(paperThatReferences);
                output += String.format("{ from: %d, to: %d },\n", paperID, paperThatReferencesID);

            }
        }
        output = output.substring(0,output.length()-2);
        output += "]);\n";

        System.out.println(output);
        try(PrintWriter out = new PrintWriter("filename.txt")){
            out.println(output);
        } catch (Exception e) {
          System.err.println("Unable to write results to file");
        }
    }

    private static String getPaperTitle(File f1) {
        return f1.getName().substring(7, f1.getName().length()-4);
    }


}
