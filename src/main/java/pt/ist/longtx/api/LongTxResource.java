package pt.ist.longtx.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.TransactionError;
import pt.ist.fenixframework.longtx.LongTransaction;
import pt.ist.fenixframework.longtx.TransactionalContext;

@Path("/longTx")
public class LongTxResource {

    @Context
    HttpServletRequest request;

    @GET
    @Path("/select/{id}")
    public Response select(@PathParam("id") String id) {
        TransactionalContext ctx = FenixFramework.getDomainObject(id);
        request.getSession(true).setAttribute(LongTxFilter.CONTEXT_KEY, ctx);
        return Response.ok().build();
    }

    @GET
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") String id) {
        TransactionalContext ctx = FenixFramework.getDomainObject(id);
        deleteIt(ctx);
        return Response.ok().build();
    }

    @GET
    @Path("/create/{name}")
    public Response create(@PathParam("name") String name) {
        doCreate(name);
        return Response.ok().build();
    }

    @GET
    @Path("/reset")
    public Response reset() {
        SystemBootstrap.createIt();
        request.getSession(true).removeAttribute(LongTxFilter.CONTEXT_KEY);
        return Response.ok().build();
    }

    @GET
    @Path("/rollback")
    public Response rollback() {
        request.getSession(true).removeAttribute(LongTxFilter.CONTEXT_KEY);
        return Response.ok().build();
    }

    @GET
    @Path("/commit")
    public Response commit() {
        System.out.println("Ctx: " + LongTransaction.getContextForThread());
        LongTransaction.removeContextFromThread();
        TransactionalContext ctx = (TransactionalContext) request.getSession(true).getAttribute(LongTxFilter.CONTEXT_KEY);
        return commitContext(ctx);
    }

    @Atomic(mode = TxMode.WRITE)
    private Response commitContext(TransactionalContext ctx) {
        try {
            ctx.commit(false);
            ctx.setDomainRoot(null);
            request.getSession(true).removeAttribute(LongTxFilter.CONTEXT_KEY);
            return Response.ok().build();
        } catch (TransactionError e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @Atomic(mode = TxMode.WRITE)
    public void doCreate(String name) {
        FenixFramework.getDomainRoot().addTransactionalContext(new TransactionalContext(name));
    }

    @Atomic(mode = TxMode.WRITE)
    public void deleteIt(TransactionalContext ctx) {
        ctx.setDomainRoot(null);
    }

}
