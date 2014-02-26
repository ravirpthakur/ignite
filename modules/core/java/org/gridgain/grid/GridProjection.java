// @java.file.header

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid;

import org.gridgain.grid.compute.*;
import org.gridgain.grid.events.*;
import org.gridgain.grid.lang.*;
import org.gridgain.grid.messaging.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Defines grid projection which represents a common functionality over a group of nodes.
 * The {@link Grid} interface itself also extends {@code GridProjection} which makes
 * an instance of {@link Grid} a projection over all grid nodes.
 * <h1 class="header">Clustering</h1>
 * Grid projection allows to group grid nodes into various subgroups to perform distributed
 * operations on them. All {@code 'forXXX(...)'} methods will create a child grid projection
 * from existing projection. If you create a new projection from current one, then the resulting
 * projection will include a subset of nodes from current projection. For example, the following
 * code snippet will create a projection over a random remote node:
 * <pre name="code" class="java">
 * Grid g = GridGain.grid();
 *
 * GridProjection randomRemoteNodeProjection = g.forRemotes().forRandom();
 * </pre>
 * <h1 class="header">Features</h1>
 * Grid projection provides the following functionality over the underlying group of nodes:
 * <ul>
 * <li>Compute ({@link #compute()} - functionality for executing tasks and closures over nodes in this projection.</li>
 * <li>Messaging ({@link #message()} - functionality for topic-based message exchange over nodes in this projection.</li>
 * <li>Events ({@link #events()} - functionality for querying and listening to events on nodes in this projection.</li>
 * </ul>
 *
 * @author @java.author
 * @version @java.version
 */
public interface GridProjection {
    /**
     * Gets instance of grid.
     *
     * @return Grid instance.
     */
    public Grid grid();

    /**
     * Gets {@code compute} functionality over this grid projection. All operations
     * on the returned {@link GridCompute} instance will only include nodes from
     * this projection.
     *
     * @return Compute instance over this grid projection.
     */
    public GridCompute compute();

    /**
     * Gets {@code messaging} functionality over this grid projection. All operations
     * on the returned {@link GridMessaging} instance will only include nodes from
     * this projection.
     *
     * @return Messaging instance over this grid projection.
     */
    public GridMessaging message();

    /**
     * Gets {@code events} functionality over this grid projection. All operations
     * on the returned {@link GridEvents} instance will only include nodes from
     * this projection.
     *
     * @return Events instance over this grid projection.
     */
    public GridEvents events();

    /**
     * Creates a grid projection over a given set of nodes.
     *
     * @param nodes Collection of nodes to create a projection from.
     * @return Projection over provided grid nodes.
     */
    public GridProjection forNodes(Collection<? extends GridNode> nodes);

    /**
     * Creates a grid projection for the given node.
     *
     * @param node Node to get projection for.
     * @param nodes Optional additional nodes to include into projection.
     * @return Grid projection for the given node.
     */
    public GridProjection forNode(GridNode node, GridNode... nodes);

    /**
     * Creates a grid projection for nodes other than given node.
     *
     * @param node Node to exclude from grid projection.
     * @return Projection that will contain all nodes that original projection contained excluding
     *      given node.
     */
    public GridProjection forOthers(GridNode node);

    /**
     * // TODO
     * @param prj
     * @return
     */
    public GridProjection forOthers(GridProjection prj);

    /**
     * Creates a grid projection over nodes with specified node IDs.
     *
     * @param ids Collection of node IDs.
     * @return Projection over nodes with specified node IDs.
     */
    public GridProjection forNodeIds(Collection<UUID> ids);

    /**
     * Creates a grid projection for a node with specified ID.
     *
     * @param id Node ID to get projection for.
     * @param ids Optional additional node IDs to include into projection.
     * @return Projection over node with specified node ID.
     */
    public GridProjection forNodeId(UUID id, UUID... ids);

    /**
     * Creates a grid projection which includes all nodes that pass the given predicate filter.
     *
     * @param p Predicate filter for nodes to include into this projection.
     * @return Grid projection for nodes that passed the predicate filter.
     */
    public GridProjection forPredicate(GridPredicate<GridNode> p);

    /**
     * Creates monadic projection with the nodes from this projection that have given node
     * attribute with optional value. If value is {@code null} than simple attribute presence
     * (with any value) will be used for inclusion of the node.
     *
     * @param n Name of the attribute.
     * @param v Optional attribute value to match.
     * @return Monadic projection.
     */
    public GridProjection forAttribute(String n, @Nullable String v);

    /**
     * Creates monadic projection with the nodes from this projection that have configured
     * all caches with given names. If node does not have at least one of these caches, it will not
     * be included to result projection.
     *
     * @param cacheName Cache name.
     * @param cacheNames Optional additional cache names to include into projection.
     * @return Projection over nodes that have specified cache running.
     */
    public GridProjection forCache(String cacheName, @Nullable String... cacheNames);

    /**
     * Creates monadic projection with the nodes from this projection that have configured
     * streamers with given names. If node does not have at least one of these streamers, it will not
     * be included to result projection.
     *
     * @param streamerName Streamer name.
     * @param streamerNames Optional additional streamer names to include into projection.
     * @return Projection over nodes that have specified streamer running.
     */
    public GridProjection forStreamer(String streamerName, @Nullable String... streamerNames);

    /**
     * Gets monadic projection consisting from the nodes in this projection excluding the local node, if any.
     *
     * @return Monadic projection consisting from the nodes in this projection excluding the local node, if any.
     */
    public GridProjection forRemotes();

    /**
     * Gets monadic projection consisting from the nodes in this projection residing on the
     * same host as given node.
     *
     * @param node Node residing on the host for which projection is created.
     * @return Projection for nodes residing on the same host as passed in node.
     */
    public GridProjection forHost(GridNode node);

    /**
     * Gets monadic projection consisting from the daemon nodes in this projection.
     * <p>
     * Daemon nodes are the usual grid nodes that participate in topology but not
     * visible on the main APIs, i.e. they are not part of any projections. The only
     * way to see daemon nodes is to use this method.
     * <p>
     * Daemon nodes are used primarily for management and monitoring functionality that
     * is build on GridGain and needs to participate in the topology but also needs to be
     * excluded from "normal" topology so that it won't participate in task execution
     * or in-memory data grid storage.
     *
     * @return Monadic projection consisting from the daemon nodes in this projection.
     */
    public GridProjection forDaemons();

    /**
     * Creates monadic projection with one random node from current projection.
     *
     * @return Monadic projection.
     */
    public GridProjection forRandom();

    /**
     * Gets read-only collections of nodes in this projection.
     *
     * @return All nodes in this projection.
     */
    public Collection<GridNode> nodes();

    /**
     * Gets a node for given ID from this optionally filtered projection.
     *
     * @param nid Node ID.
     * @return Node with given ID from this projection or {@code null} if such node does not exist in this
     *      projection.
     */
    @Nullable public GridNode node(UUID nid);

    /**
     * @return // TODO
     */
    @Nullable public GridNode node();

    /**
     * Gets predicate that defines a subset of nodes for this projection.
     *
     * @return Predicate that defines a subset of nodes for this projection.
     */
    public GridPredicate<GridNode> predicate();

    /**
     * Gets a metrics snapshot for this projection.
     *
     * @return Grid project metrics snapshot.
     * @throws GridException If projection is empty.
     * @see GridNode#metrics()
     */
    public GridProjectionMetrics metrics() throws GridException;
}
