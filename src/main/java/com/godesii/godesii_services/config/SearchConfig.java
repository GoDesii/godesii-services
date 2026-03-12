package com.godesii.godesii_services.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class SearchConfig {

    private static final Logger log = LoggerFactory.getLogger(SearchConfig.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Build or rebuild the Lucene index from existing database data on startup.
     * This is a one-time operation that reads all indexed entities.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void buildSearchIndex() {
        try {
            SearchSession searchSession = Search.session(entityManager);
            searchSession.massIndexer()
                    .idFetchSize(150)
                    .batchSizeToLoadObjects(25)
                    .threadsToLoadObjects(4)
                    .startAndWait();
            log.info("✅ Hibernate Search: Mass indexing complete");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("❌ Hibernate Search: Mass indexing interrupted", e);
        }
    }
}
