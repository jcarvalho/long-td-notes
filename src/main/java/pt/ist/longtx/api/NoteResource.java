package pt.ist.longtx.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.longtx.LongTransaction;
import pt.ist.fenixframework.longtx.TransactionalContext;
import pt.ist.longtx.domain.Note;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/notes")
@Produces(MediaType.APPLICATION_JSON)
public class NoteResource {

    @GET
    @Atomic(mode = TxMode.READ)
    public String viewNotes() {
        JsonObject finalObject = new JsonObject();
        finalObject.addProperty("title", FenixFramework.getDomainRoot().getNoteSystem().getTitle());
        JsonArray array = new JsonArray();

        for (Note note : FenixFramework.getDomainRoot().getNoteSystem().getNoteSet()) {
            JsonObject json = new JsonObject();
            json.addProperty("id", note.getExternalId());
            json.addProperty("contents", note.getContents());

            array.add(json);
        }

        TransactionalContext ctx = LongTransaction.getContextForThread();
        finalObject.addProperty("context", ctx == null ? null : ctx.getName());

        JsonArray available = new JsonArray();
        for (TransactionalContext ctxs : FenixFramework.getDomainRoot().getTransactionalContextSet()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", ctxs.getExternalId());
            obj.addProperty("name", ctxs.getName());
            available.add(obj);
        }

        finalObject.add("contexts", available);

        finalObject.add("notes", array);
        return finalObject.toString();
    }

    @GET
    @Path("/remove/{id}")
    public Response removeNote(@PathParam("id") String id) {
        Note note = FenixFramework.getDomainObject(id);
        return removeIt(note);
    }

    @GET
    @Path("/edit/{id}/{contents}")
    public Response edit(@PathParam("id") String id, @PathParam("contents") String contents) {
        Note note = FenixFramework.getDomainObject(id);
        return editIt(note, contents);
    }

    @Atomic(mode = TxMode.WRITE)
    private Response editIt(Note note, String contents) {
        if (note.getSystem() == null) {
            return Response.serverError().build();
        }
        note.setContents(contents);
        return Response.ok().build();
    }

    @GET
    @Path("/restart")
    public Response restart() {
        SystemBootstrap.createIt();
        return Response.ok().build();
    }

    @Atomic
    public Response removeIt(Note note) {
        if (note.getSystem() == null) {
            return Response.serverError().build();
        }
        note.setSystem(null);
        return Response.ok().build();
    }
}
