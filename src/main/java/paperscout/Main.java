package paperscout;

import helpers.FileHelper;
import helpers.PDFHelper;
import paperscout.data.Library;
import paperscout.data.Paper;
import paperscout.data.Reference;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(Level.SEVERE);
        java.util.logging.Logger.getLogger("edu.umass.cs.mallet.grmm.inference.TRP").setLevel(Level.SEVERE);

        try {
            HashMap<String, HashSet<String>> relationMap = new HashMap<String, HashSet<String>>();
            HashMap<String, String> resultsMap = new HashMap<String, String>();


            Library library = new Library("C:\\Users\\Andrew\\OneDrive\\Library\\PaperScout");

            int librarySize = library.getSize();
            int fileNumber = 0;
            for (Paper p1 : library.getPapers()) {
                fileNumber++;
                String paperTitle = p1.getTitle();
                System.out.println(String.format("Analyzing '%s' [%d/%d]", paperTitle, fileNumber, librarySize));

                relationMap.put(paperTitle, new HashSet<String>());

                List<Reference> references;
                String results = "";

                for (Paper p2 : library.getPapers()) {

                    references = p2.getReferences();
                    if (references.size() > 0) {
                        boolean paperIsReferenced = p2.containsReferenceTo(p1);
                        if (paperIsReferenced) {
                            relationMap.get(paperTitle).add(p2.getTitle());

                            results += "<div class=\"reference\" data-title=\""+p2.getTitle()+"\">";
                            String referenceId = PDFHelper.getReferenceIdentifier(paperTitle, references);
                            results += "<span class=\"reference-header\">In '" + p2.getTitle() + "' as [" + referenceId + "]</span>";
                            List<String> sentences = PDFHelper.getSentences(p2.getFile());
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
        writeOutputToHTMLFile(output);
    }

    private static void writeOutputToHTMLFile(String output) {
        String templateHTML = FileHelper.readStringFromFile("src/main/html/templates/main.html");
        String templateWithOutputInserted = templateHTML.replace("%%%nodes-and-edges%%%", output);
        FileHelper.writeStringToFile("src/main/html/main.html", templateWithOutputInserted);
    }

    private static String getPaperTitle(File f1) {
        return f1.getName().substring(7, f1.getName().length()-4);
    }


}
