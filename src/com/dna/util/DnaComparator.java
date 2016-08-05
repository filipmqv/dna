package com.dna.util;

import java.util.Comparator;

import com.dna.model.Dna;

public class DnaComparator implements Comparator<Dna>{

	@Override
    public int compare(Dna o1, Dna o2) {
        return o1.getNumberOfPositiveErrors() - o2.getNumberOfPositiveErrors();
    }
}
