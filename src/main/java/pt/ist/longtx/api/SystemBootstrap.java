package pt.ist.longtx.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.longtx.domain.Note;
import pt.ist.longtx.domain.NoteSystem;

@WebListener("/api/notes*")
public class SystemBootstrap implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        createIt();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    @Atomic(mode = TxMode.WRITE)
    public static void createIt() {
        NoteSystem system = new NoteSystem();
        system.setTitle("Dummy Title");
        FenixFramework.getDomainRoot().setNoteSystem(system);
        FenixFramework.getDomainRoot().getTransactionalContextSet().clear();

        system.addNote(new Note("Introduce myself"));
        system.addNote(new Note("What are Long Lived Transactions?"));
        system.addNote(new Note("Why are they hard to implement?"));
        system.addNote(new Note("Related Work"));
        system.addNote(new Note("Solution"));
        system.addNote(new Note("Performance Analysis"));

    }

}
