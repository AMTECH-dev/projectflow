package io.amtech.projectflow.app.general;

import lombok.Getter;

import java.util.List;

@Getter
public class PagedData<T> {
    private final List<T> data;
    private final Meta meta;

    public PagedData(List<T> data, int limit, int offset) {
        this.data = data;
        this.meta = new Meta(limit, offset);
    }
}
