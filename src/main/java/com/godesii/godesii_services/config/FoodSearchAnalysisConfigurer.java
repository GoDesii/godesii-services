package com.godesii.godesii_services.config;

import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

public class FoodSearchAnalysisConfigurer implements LuceneAnalysisConfigurer {

    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {

        // Index-time analyzer: tokenize + lowercase + synonyms + ASCII folding + edge
        // ngram
        context.analyzer("food_analyzer").custom()
                .tokenizer("standard")
                .tokenFilter("lowercase")
                .tokenFilter("synonym")
                .param("synonyms", "food_synonyms.txt")
                .param("ignoreCase", "true")
                .param("expand", "true")
                .tokenFilter("asciifolding")
                .tokenFilter("edgeNGram")
                .param("minGramSize", "2")
                .param("maxGramSize", "15");

        // Query-time analyzer: same pipeline minus edge ngram (exact token matching at
        // query time)
        context.analyzer("food_query_analyzer").custom()
                .tokenizer("standard")
                .tokenFilter("lowercase")
                .tokenFilter("synonym")
                .param("synonyms", "food_synonyms.txt")
                .param("ignoreCase", "true")
                .param("expand", "true")
                .tokenFilter("asciifolding");

        // Simple keyword normalizer for sorting fields
        context.normalizer("sort_normalizer").custom()
                .tokenFilter("lowercase")
                .tokenFilter("asciifolding");
    }
}
