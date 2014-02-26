// @java.file.header

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid.kernal.processors.cache.query;

import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;
import org.gridgain.grid.cache.query.*;
import org.gridgain.grid.kernal.processors.cache.*;
import org.gridgain.grid.lang.*;
import org.gridgain.grid.logger.*;
import org.gridgain.grid.util.typedef.*;
import org.gridgain.grid.util.typedef.internal.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * TODO
 *
 * @author @java.author
 * @version @java.version
 */
public class GridCacheQueryAdapter<T> implements GridCacheQuery<T> {
    /** */
    private final GridCacheContext<?, ?> cctx;

    /** */
    private final GridPredicate<GridCacheEntry<Object, Object>> prjPred;

    /** */
    private final GridCacheQueryType type;

    /** */
    private final GridLogger log;

    /** */
    private final Class<?> cls;

    /** */
    private final String clause;

    /** */
    private int pageSize;

    /** */
    private long timeout;

    /** */
    private boolean keepAll;

    /** */
    private boolean incBackups;

    /** */
    private boolean dedup;

    /** */
    private GridProjection prj;

    /** */
    private GridBiPredicate<Object, Object> filter;

    /**
     * @param cctx Context.
     * @param type Query type.
     * @param cls Class.
     * @param clause Clause.
     * @param prjPred Cache projection filter.
     */
    protected GridCacheQueryAdapter(
        GridCacheContext<?, ?> cctx, GridCacheQueryType type,
        @Nullable GridPredicate<GridCacheEntry<Object, Object>> prjPred,
        @Nullable Class<?> cls, @Nullable String clause) {
        assert cctx != null;
        assert type != null;

        this.cctx = cctx;
        this.type = type;
        this.cls = cls;
        this.clause = clause;
        this.prjPred = prjPred;

        log = cctx.logger(getClass());

        pageSize = DFLT_PAGE_SIZE;
        timeout = 0;
        keepAll = true;
        incBackups = false;
        dedup = false;
        prj = null;
        filter = null;
    }

    /**
     * @param cctx Context.
     * @param prjPred Cache projection filter.
     * @param type Query type.
     * @param log Logger.
     * @param pageSize Page size.
     * @param timeout Timeout.
     * @param keepAll Keep all flag.
     * @param incBackups Include backups flag.
     * @param dedup Enable dedup flag.
     * @param prj Grid projection.
     * @param filter Key-value filter.
     * @param cls Class.
     * @param clause Clause.
     */
    public GridCacheQueryAdapter(GridCacheContext<?, ?> cctx, GridPredicate<GridCacheEntry<Object, Object>> prjPred,
        GridCacheQueryType type, GridLogger log, int pageSize, long timeout, boolean keepAll, boolean incBackups,
        boolean dedup, GridProjection prj, GridBiPredicate<Object, Object> filter, Class<?> cls, String clause) {
        this.cctx = cctx;
        this.prjPred = prjPred;
        this.type = type;
        this.log = log;
        this.pageSize = pageSize;
        this.timeout = timeout;
        this.keepAll = keepAll;
        this.incBackups = incBackups;
        this.dedup = dedup;
        this.prj = prj;
        this.filter = filter;
        this.cls = cls;
        this.clause = clause;
    }

    /**
     * @return cache projection filter.
     */
    public GridPredicate<GridCacheEntry<Object, Object>> projectionFilter() {
        return prjPred;
    }

    /**
     * @return Type.
     */
    public GridCacheQueryType type() {
        return type;
    }

    /**
     * @return Class.
     */
    @Nullable public Class<?> queryClass() {
        return cls;
    }

    /**
     * @return Clause.
     */
    @Nullable public String clause() {
        return clause;
    }

    /** {@inheritDoc} */
    @Override public GridCacheQuery<T> pageSize(int pageSize) {
        this.pageSize = pageSize;

        return this;
    }

    /**
     * @return Page size.
     */
    public int pageSize() {
        return pageSize;
    }

    /** {@inheritDoc} */
    @Override public GridCacheQuery<T> timeout(long timeout) {
        this.timeout = timeout;

        return this;
    }

    /**
     * @return Timeout.
     */
    public long timeout() {
        return timeout;
    }

    /** {@inheritDoc} */
    @Override public GridCacheQuery<T> keepAll(boolean keepAll) {
        this.keepAll = keepAll;

        return this;
    }

    /**
     * @return Keep all flag.
     */
    public boolean keepAll() {
        return keepAll;
    }

    /** {@inheritDoc} */
    @Override public GridCacheQuery<T> includeBackups(boolean incBackups) {
        this.incBackups = incBackups;

        return this;
    }

    /**
     * @return Include backups.
     */
    public boolean includeBackups() {
        return incBackups;
    }

    /** {@inheritDoc} */
    @Override public GridCacheQuery<T> enableDedup(boolean dedup) {
        this.dedup = dedup;

        return this;
    }

    /**
     * @return Enable dedup flag.
     */
    public boolean enableDedup() {
        return dedup;
    }

    /** {@inheritDoc} */
    @Override public GridCacheQuery<T> projection(GridProjection prj) {
        this.prj = prj;

        return this;
    }

    /**
     * @return Grid projection.
     */
    public GridProjection projection() {
        return prj;
    }

    /** {@inheritDoc} */
    @Override public <K, V> GridCacheQuery<T> remoteFilter(GridBiPredicate<K, V> filter) {
        this.filter = (GridBiPredicate<Object, Object>)filter;

        return this;
    }

    /**
     * @return Key-value filter.
     */
    public <K, V> GridBiPredicate<K, V> remoteFilter() {
        return (GridBiPredicate<K, V>)filter;
    }

    /**
     * @throws GridException If query is invalid.
     */
    public void validate() throws GridException {
        // TODO: gg-7625
    }

    /** {@inheritDoc} */
    @Override public GridCacheQueryFuture<T> execute(@Nullable Object... args) {
        return execute(null, null, args);
    }

    /** {@inheritDoc} */
    @Override public <R> GridCacheQueryFuture<R> execute(GridReducer <T, R> rmtReducer, @Nullable Object... args) {
        return execute(rmtReducer, null, args);
    }

    /** {@inheritDoc} */
    @Override public <R> GridCacheQueryFuture<R> execute(GridClosure<T, R> rmtTransform, @Nullable Object... args) {
        return execute(null, rmtTransform, args);
    }

    /**
     * @param rmtReducer Optional reducer.
     * @param rmtTransform Optional transformer.
     * @param args Arguments.
     * @return Future.
     */
    private <R> GridCacheQueryFuture<R> execute(@Nullable GridReducer<T, R> rmtReducer,
        @Nullable GridClosure<T, R> rmtTransform, @Nullable Object... args) {
        Collection<GridNode> nodes = nodes();

        if (log.isDebugEnabled())
            log.debug("Executing query [query=" + this + ", nodes=" + nodes + ']');

        if (cctx.deploymentEnabled()) {
            try {
                cctx.deploy().registerClasses(cls, filter, rmtReducer, rmtTransform);
                cctx.deploy().registerClasses(args);
            }
            catch (GridException e) {
                return new GridCacheQueryErrorFuture<>(cctx.kernalContext(), e);
            }
        }

        GridCacheQueryBean bean = new GridCacheQueryBean(this, (GridReducer<Object, Object>)rmtReducer,
            (GridClosure<Object, Object>)rmtTransform, args);

        GridCacheQueryManager qryMgr = cctx.queries();

        return (GridCacheQueryFuture<R>)(nodes.size() == 1 && F.first(nodes).id().equals(cctx.localNodeId()) ?
            qryMgr.queryLocal(bean) : qryMgr.queryDistributed(bean, nodes));
    }

    /**
     * @return Nodes to execute on.
     */
    private Collection<GridNode> nodes() {
        Collection<GridNode> nodes = CU.allNodes(cctx);

        if (prj == null) {
            if (cctx.isReplicated())
                return Collections.singletonList(cctx.localNode());

            return nodes;
        }

        return F.view(nodes, new P1<GridNode>() {
            @Override public boolean apply(GridNode e) {
                return prj.node(e.id()) != null;
            }
        });
    }
}
