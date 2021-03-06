package com.tekklabs.senadores;

import com.tekklabs.model.Json2Model;
import com.tekklabs.model.Model2Json;
import com.tekklabs.model.entities.Candidate;
import com.tekklabs.model.entities.Senator;
import com.tekklabs.util.Csv2JsonParser;
import com.tekklabs.util.FileReader;
import com.tekklabs.util.JSONWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.text.Collator;
import java.util.Arrays;
import java.util.List;

/**
 * Created by taciosd on 8/2/15.
 */
public class SenatorsParser {

    private static final String HELP_TAG = "--help";
    private static final String CSV2JSON_TAG = "--csv2json";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Argumentos insuficientes. Utilize '--help' para menu de ajuda.");
            return;
        }

        String command = args[0];
        boolean result = true;

        if (command.equals(HELP_TAG)) {
            printHelp();
            return;
        }
        else if (command.equals(CSV2JSON_TAG)) {
            result = csv2json(args);
        }
        else {
            System.out.println("Comando inválido. Utilize '--help' para ajuda.");
            result = false;
        }

        if (!result) {
            System.out.println("Operacao terminou com erro.");
        }
        else {
            System.out.println("Operacao terminou com sucesso.");
        }
    }

    private static void printHelp() {
        System.out.println("===== Ajuda do Leitor de dados dos senadores =====");
        System.out.println();
        System.out.println("Modo de uso: ./SenatorsParser " + CSV2JSON_TAG + " <csvFilePath> <tseCandidatesFilePath> <jsonFilePath>");
        System.out.println("======================================");
    }

    private static boolean csv2json(String[] args) {
        if (args.length != 4) {
            System.out.println("Número de argumentos incorreto. Utilize '--help' para ajuda.");
            return false;
        }

        try {
            String csvFilePath = args[1];
            List<String> csvContent = FileReader.readCsv(csvFilePath, "UTF-8");
            JSONArray jsonArray = new Csv2JsonParser(";", true, new SenatorsJsonObjectCreator()).writeJson(csvContent);
            List<Senator> senatorList = Json2Model.json2Senator(jsonArray);

            FileInputStream fis = new FileInputStream(args[2]);
            byte[] buffer = new byte[8000000];
            int size = fis.read(buffer);
            byte[] bufferReduced = Arrays.copyOf(buffer, size);
            String tseFileContent = new String(bufferReduced, 0, size, "UTF-8");
            fis.close();

            JSONArray tseJson = (JSONArray) JSONValue.parse(tseFileContent);
            List<Candidate> tseCandList = Json2Model.json2Candidate(tseJson);

            associateCpfInSenatorList(senatorList, tseCandList);
            Model2Json pol2json = new Model2Json();
            JSONArray senatorJsonArray = pol2json.senatorList2Json(senatorList);

            JSONWriter writer = new JSONWriter();
            senatorJsonArray.writeJSONString(writer);
            String senatorArrayStr = writer.toString();

            String jsonFilePath = args[3];
            PrintWriter printWriter = new PrintWriter(jsonFilePath, "UTF-8");
            printWriter.print(senatorArrayStr);
            printWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static void associateCpfInSenatorList(List<Senator> senatorList, List<Candidate> tseCandList) {
        for (Senator senator : senatorList) {
            try {
                String cpf = findCpfInList(tseCandList, senator.politicianName);
                if (cpf == null) {
                    System.out.println("Candidato não encontrado! Nome: " + senator.politicianName);
                }
                senator.cpf = cpf;
            }
            catch (Exception e) {
                System.out.println("Excecão ao tentar associar um senador a um candidato.");
            }
        }
    }

    private static String findCpfInList(List<Candidate> tseCandList, String polName) {
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY);

        for (Candidate cand : tseCandList) {
            int result = collator.compare(cand.politicianName, polName);
            if (result == 0) {
                return cand.cpf;
            }
        }

        return null;
    }
}
