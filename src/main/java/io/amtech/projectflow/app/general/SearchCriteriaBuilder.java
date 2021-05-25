package io.amtech.projectflow.app.general;

import java.util.HashMap;
import java.util.Map;

public class SearchCriteriaBuilder {
    private final int maxLimit;
    private Integer limit;
    private Integer offset;
    private Map<String, String> filters = new HashMap<>();
    private String order;

    public SearchCriteriaBuilder() {
        this(SearchCriteria.MAX_LIMIT);
    }

    public SearchCriteriaBuilder(Integer maxLimit) {
        this.maxLimit = SearchCriteria.MAX_LIMIT;
    }

    public SearchCriteriaBuilder limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public SearchCriteriaBuilder offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public SearchCriteriaBuilder filter(String key, String value) {
        if (value != null) {
            this.filters.put(key, value);
        }
        return this;
    }

    public SearchCriteriaBuilder order(String order) {
        if (order != null) {
            this.order = order;
        }
        return this;
    }

    private Integer getLimit() {
        if (limit == null || limit > getMaxLimit() || limit <= 0) {
            return getMaxLimit();
        }

        return limit;
    }

    private Integer getOffset() {
        if (offset == null || offset <= 0) {
            return 0;
        }
        return offset;
    }

    private int getMaxLimit() {
        return maxLimit;
    }

    public SearchCriteria build() {
        return new SearchCriteria(getLimit(), getOffset(), this.filters, this.order);
    }
}
