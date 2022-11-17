package fr.inria.diverse;

import fr.inria.diverse.tools.Configuration;
import fr.inria.diverse.tools.Executor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.softwareheritage.graph.SwhUnidirectionalGraph;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public abstract class GraphExplorer {
    static Logger logger = LogManager.getLogger(GraphExplorer.class);
    protected Configuration config = Configuration.getInstance();
    protected Graph graph;

    public GraphExplorer(Graph graph) {
        this.graph = graph;
    }


    /**
     * Iterate over the graph list of nodes in a parallel way
     *
     * @throws InterruptedException
     */
    public void exploreGraphNode(long size) throws InterruptedException {
        Executor executor = new Executor(this.config.getThreadNumber());
        logger.info("Num of nodes: " + size);
        for (int thread = 0; thread < this.config.getThreadNumber(); thread++) {
            long finalThread = thread;
            SwhUnidirectionalGraph graphCopy = graph.getGraph().copy();
            executor.execute(() -> {
                Instant timestamp = Instant.now();
                for (long currentNodeId = finalThread; currentNodeId < size; currentNodeId = currentNodeId + this.config.getThreadNumber()) {
                    if (Duration.between(timestamp, Instant.now())
                            .toMinutes() > config.getCheckPointIntervalInMinutes()) {
                        timestamp = Instant.now();
                        logger.info("Node " + currentNodeId + " over " + size + " thread " + finalThread);
                    }
                    try {
                        this.exploreGraphNodeAction(currentNodeId, graphCopy.copy());
                    } catch (Throwable e) {
                        logger.error("Error catch for node " + currentNodeId, e);
                    }

                }
            });
        }
        executor.shutdown();
        //Waiting Tasks
        while (!executor.awaitTermination(config.getCheckPointIntervalInMinutes(), TimeUnit.MINUTES)) {
            logger.info("Node traversal completed, waiting for asynchronous tasks. Tasks performed " + executor.getCompletedTaskCount() + " over " + executor.getTaskCount());
            logger.info("Partial checkpoint");
            this.exploreGraphNodeCheckpointAction();
        }
    }

    /**
     * Function call by exploreGraphNode at each node
     *
     * @param currentNodeId the current Node id
     * @param graphCopy     the current graphCopy (thread safe approach)
     */
    void exploreGraphNodeAction(long currentNodeId, SwhUnidirectionalGraph graphCopy) {
    }

    /**
     * Function called peridically by exploreGraphNode to perform partial backups
     */
    void exploreGraphNodeCheckpointAction() {
    }

    void run() throws InterruptedException, IOException {
        try {
            this.exploreGraphNode(graph.getGraph().numNodes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error", e);
        }
    }

}