package com.csradar.scraper;

import java.util.List;

public interface JobSourceAdapter {
    String sourceName();

    List<ScrapedJob> fetchJobs();
}
