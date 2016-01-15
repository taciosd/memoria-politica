package com.tsd.memoriapolitica.gui.notebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsd.memoriapolitica.domain.CitizenNotebook;
import com.tsd.memoriapolitica.domain.Constants;
import com.tsd.memoriapolitica.domain.Politician;
import com.tsd.memoriapolitica.domain.PoliticianClass;
import com.tsd.memoriapolitica.gui.MainActivity;
import com.tsd.memoriapolitica.gui.PoliticianThumbnailAdapter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by PC on 05/07/2015.
 */
public class ReprovedPoliticiansFragment extends PoliticianFragment {

    public static PoliticianFragment newInstance(Bundle args) {
        PoliticianFragment fragment = new ReprovedPoliticiansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ReprovedPoliticiansFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        PoliticianClass polClass = (PoliticianClass) bundle.getSerializable(Constants.POLITICIAN_CLASS);
        CitizenNotebook notebook = (CitizenNotebook) bundle.getSerializable(Constants.NOTEBOOK_OBJ);

        Set<Politician> politicians = notebook.getReprovedPoliticians(polClass);
        mAdapter = new PoliticianThumbnailAdapter((MainActivity) getActivity(), politicians, Approval.REPROVED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }
}
