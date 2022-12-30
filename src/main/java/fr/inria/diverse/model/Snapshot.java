package fr.inria.diverse.model;

import it.unimi.dsi.big.webgraph.labelling.ArcLabelledNodeIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.softwareheritage.graph.SwhType;
import org.softwareheritage.graph.SwhUnidirectionalGraph;
import org.softwareheritage.graph.labels.DirEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Snapshot extends Node implements Serializable {
    private static final long serialVersionUID = 2166967946176031738L;
    static Logger logger = LogManager.getLogger(Snapshot.class);
    private List<SnapshotBranch> branches;

    public Snapshot() {
    }

    public Snapshot(long nodeId, SwhUnidirectionalGraph g) {
        super(nodeId, g);
    }

    public List<SnapshotBranch> getBranches() {
        if(this.branches == null) {
            this.branches = new ArrayList<>();
            ArcLabelledNodeIterator.LabelledArcIterator it = this.getGraph().copy()
                    .labelledSuccessors(this.getNodeId());
            for (long snapChildId; (snapChildId = it.nextLong()) != -1; ) {
                final DirEntry[] labels = (DirEntry[]) it.label().get();
                DirEntry label = labels[0];
                String branchName = new String(this.getGraph().getLabelName(label.filenameId));
                //String branchName = url.replace("refs/heads/", "");
                ISnapshotChild snapChild=null;
                switch (this.getGraph().getNodeType(snapChildId)){
                    case REV:{
                        snapChild=new Revision(snapChildId,this.getGraph());
                    }
                    case REL: {
                        snapChild=new Release(snapChildId,this.getGraph());
                    }
                }
                if (snapChild!=null){
                    this.branches.add(new SnapshotBranch(branchName, snapChild));
                }else{
                    logger.warn("Branch skipped since child cannot be init for child id "+snapChildId);
                }

            }
        }
        return branches;
    }

    public void setBranches(List<SnapshotBranch> branches) {
        this.branches = branches;
    }
}
