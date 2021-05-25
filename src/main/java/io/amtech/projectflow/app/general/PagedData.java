package io.amtech.projectflow.app.general;

import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class PagedData<T> {
    private final List<T> data;
    private final Meta meta;

    public PagedData(final List<T> data, final int limit, final int offset) {
        this.data = data;
        this.meta = new Meta(limit, offset);
    }

    public PagedData(final List<T> data, final SearchCriteria criteria) {
        this(data, criteria.getLimit(), criteria.getOffset());
    }

    public <K> PagedData<K> map(final Function<T, K> mapper) {
        return new PagedData<>(data.stream().map(mapper).collect(Collectors.toList()),
                this.getMeta().getLimit(),
                this.getMeta().getOffset());
    }
}
