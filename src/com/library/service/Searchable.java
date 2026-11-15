package com.library.service;

import java.util.List;

/**
 * [OOP Concept: Abstraction - Generic Interface]
 * Generic interface defining search capability across various entity types.
 *
 * @param <T> The entity type being searched
 */
public interface Searchable<T> {
    /**
     * Search entities matching the keyword.
     *
     * @param keyword The term to look for
     * @return List of matched entities
     */
    List<T> search(String keyword);
}
