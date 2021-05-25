package io.amtech.projectflow.app.general;

import java.util.List;
import java.util.Map;

public class SearchCriteria {
    public static final int MAX_LIMIT = 100;

    private final int limit;
    private final int offset;
    private final Map<String, String> filters;
    private final List<String> orders;

    public SearchCriteria(int limit, int offset, Map<String, String> filters, List<String> orders) {
        this.limit = limit;
        this.offset = offset;
        this.filters = filters;
        this.orders = orders;
    }

    public int getLimit() {
        if (limit == 0 || limit > MAX_LIMIT) {
            return MAX_LIMIT;
        }

        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public List<String> getOrders() {
        return orders;
    }
}
