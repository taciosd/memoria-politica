package com.tsd.memoriapolitica.db;

import android.content.Context;

import com.tsd.memoriapolitica.domain.Politician;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by taciosd on 8/2/15.
 */
public abstract class PoliticianDao extends Dao {

    private static List<Politician> politiciansCache = new ArrayList<>();
    private CandidateDao candidateDao;
    private String resourceName;

    public PoliticianDao(Context aContext, CandidateDao candidateDao, String resourceName) {
        super(aContext);
        this.candidateDao = candidateDao;
        this.resourceName = resourceName;
    }

    public List<Politician> getAll() {
        synchronized (politiciansCache) {
            if (politiciansCache.isEmpty()) {
                PoliticianDao.politiciansCache.addAll(parsePoliticiansFromJsonFile());
            }
        }

        return PoliticianDao.politiciansCache;
    }

    public Politician getByCpf(String cpf) {
        List<Politician> allPol = getAll();

        for (Politician pol : allPol) {
            if (pol.getCpf().equals(cpf)) {
                return pol;
            }
        }

        return null;
    }

    private List<Politician> parsePoliticiansFromJsonFile() {

        try {
            JSONArray fedDepArray = ResourceUtil.getJsonArrayFromFile(getContext(), resourceName);
            List<Politician> fedDepList = parseJsonArray(fedDepArray);
            fillPoliticiansWithCandidateInfo(fedDepList);
            return fedDepList;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private void fillPoliticiansWithCandidateInfo(List<Politician> fedDepList) throws IOException, JSONException {
        List<String> cpfList = new ArrayList<>();
        for (Politician pol : fedDepList) {
            cpfList.add(pol.getCpf());
        }
        List<Politician> candidates = candidateDao.findCandidates(cpfList);

        for (Politician pol : fedDepList) {
            int index = candidates.indexOf(pol);
            pol.fillWithCandidateInfo(candidates.get(index));
        }
    }

    protected abstract List<Politician> parseJsonArray(JSONArray jsonArray) throws JSONException, IOException;
}
