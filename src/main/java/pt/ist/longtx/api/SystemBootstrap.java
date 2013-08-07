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

        for (int i = 0; i < 5; i++) {
            Note note = new Note();
            note.setContents("Note " + (i + 1));
            system.addNote(note);
        }
    }

}
