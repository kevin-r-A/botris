package com.csl.cs108ademoapp.Web.Responses;

import java.util.List;

public class PagedResultDto<T> {
    public long TotalCount;
    public List<T> items;
}
