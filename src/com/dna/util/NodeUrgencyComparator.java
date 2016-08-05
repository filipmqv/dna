package com.dna.util;

import java.util.Comparator;

import com.dna.model.Node;

public class NodeUrgencyComparator implements Comparator<Node>{

	@Override
    public int compare(Node o1, Node o2) {
        return o2.getUrgency() - o1.getUrgency();
    }

}
