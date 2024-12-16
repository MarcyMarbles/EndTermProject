package kz.marcy.endtermproject.Entity;

public class Comments extends AbstractSuperClass {
    private String content; // Changeable -> user can edit the content
    private Users author; // Isn't changeable
    private News news; // Isn't changeable
}
