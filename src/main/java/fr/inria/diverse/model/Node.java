package fr.inria.diverse.model;

import fr.inria.diverse.Graph;
import org.softwareheritage.graph.SwhUnidirectionalGraph;

import java.io.Serializable;

public class Node implements Serializable {
    private static final long serialVersionUID = -5583517045537897698L;
    private long nodeId;
    private SwhUnidirectionalGraph graph;


    public Node() {
    }

    public Node(long nodeId, SwhUnidirectionalGraph g) {
        this.graph=g;
        this.nodeId = nodeId;
    }
    public SwhUnidirectionalGraph getGraph() {
        return graph;
    }

    public void setGraph(SwhUnidirectionalGraph g) {
        this.graph = g;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return nodeId == node.nodeId;
    }

    @Override
    public int hashCode() {
        return (int) (nodeId ^ (nodeId >>> 32));
    }
}
