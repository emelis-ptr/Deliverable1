package deliverable_one;

import utils.LogFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class WriteFile {

    private WriteFile() {
    }

    /**
     * Metodo per scrivere i risultati ottenuti in un file .csv
     *
     * @param allProject: key: anno-mese; value: conteggio fixed tickets.
     */
    public static void writeFile(Map<String, Integer> allProject, Map<String, Integer> allCommits) {

        try (FileWriter fileWriter = new FileWriter("results/TicketsAnalysis.csv")) {

            fileWriter.append("Anno-Mese");
            fileWriter.append(";");
            fileWriter.append("Bug fixed");
            fileWriter.append(";");
            fileWriter.append("Commit totali");
            fileWriter.append("\n");

            for (Map.Entry<String, Integer> entry : allProject.entrySet()) {

                ArrayList<String> rowData = new ArrayList<>();

                rowData.add(entry.getKey());
                rowData.add(Integer.toString(entry.getValue()));
                if (allCommits.containsKey(entry.getKey())) {
                    if(allCommits.get(entry.getKey()) < entry.getValue()) {
                        rowData.add((Integer.toString(allCommits.get(entry.getKey())+1)));
                    }
                    else{
                        rowData.add((Integer.toString(allCommits.get(entry.getKey()))));
                    }
                }

                fileWriter.append(String.join(";", rowData));
                fileWriter.append("\n");
            }

        } catch (IOException e) {
            LogFile.errorLog("Errore nella scrittura del file!");
        }
    }

}
