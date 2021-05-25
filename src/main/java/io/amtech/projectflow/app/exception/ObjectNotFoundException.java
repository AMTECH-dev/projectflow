package io.amtech.projectflow.app.exception;

public class ObjectNotFoundException extends DomainException {

    public ObjectNotFoundException(final String obj, final String id) {
        super(String.format("%s not found by %s", obj, id), 404);
    }

    public ObjectNotFoundException(final String obj, final long id) {
        this(obj, ""+id);
    }
}
