package com.dna.util;

import java.util.Comparator;

import com.dna.model.Node;

public class NodeComparator implements Comparator<Node>{
	
    @Override
    public int compare(Node o1, Node o2) {
        return o1.getSequence().compareTo(o2.getSequence());
    }
	
}
