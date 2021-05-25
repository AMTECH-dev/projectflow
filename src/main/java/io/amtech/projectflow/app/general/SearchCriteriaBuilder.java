package io.amtech.projectflow.app.general;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchCriteriaBuilder {
    private final int maxLimit;
    private Integer limit;
    private Integer offset;
    private Map<String, String> filters = new HashMap<>();
    private List<String> orders = new ArrayList<>();

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
            this.orders.add(order);
        }
        return this;
    }

    public SearchCriteriaBuilder orders(String value) {
        if (StringUtils.isEmpty(value) || "".equals(value.trim())) {
            return null;
        }

        List<String> ordersList = Arrays.stream(value.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        this.orders.addAll(ordersList);
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
        return new SearchCriteria(getLimit(), getOffset(), this.filters, this.orders);
    }
}
