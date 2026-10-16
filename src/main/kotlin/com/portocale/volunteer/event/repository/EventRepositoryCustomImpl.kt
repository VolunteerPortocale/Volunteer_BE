package com.portocale.volunteer.event.repository

import com.portocale.volunteer.event.Event
import com.portocale.volunteer.event.EventFilterApi
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository

@Repository
class EventRepositoryCustomImpl(
    private val mongoTemplate: MongoTemplate
) : EventRepositoryCustom {

    override fun findByFilter(filter: EventFilterApi): EventAggregationResult {
        require(filter.page >= 0) { "Page must be >= 0" }
        require(filter.size > 0) { "Size must be > 0" }

        val operations = mutableListOf<AggregationOperation>()
        val criteria = Criteria()

        if (filter.statuses.isNotEmpty()) {
            criteria.and("status").`in`(filter.statuses.map { it.name })
        }

        if (filter.categories.isNotEmpty()) {
            criteria.and("category").`in`(filter.categories.map { it.name })
        }

        if (filter.statuses.isNotEmpty() || filter.categories.isNotEmpty()) {
            operations += Aggregation.match(criteria)
        }

        operations += Aggregation.facet(
            Aggregation.sort(
                Sort.by(
                    Sort.Order.desc("createdAt"),
                    Sort.Order.desc("_id")
                )
            ),
            Aggregation.skip(filter.page.toLong() * filter.size),
            Aggregation.limit(filter.size.toLong())
        ).`as`("events")
            .and(
                Aggregation.count().`as`("total")
            ).`as`("metadata")

        return mongoTemplate.aggregate(
            Aggregation.newAggregation(operations),
            Event::class.java,
            EventAggregationResult::class.java
        ).uniqueMappedResult ?: EventAggregationResult()
    }

    data class EventAggregationResult(
        val events: List<Event> = emptyList(),
        val metadata: List<EventCount> = emptyList()
    ) {
        val total: Int
            get() = metadata.firstOrNull()?.total ?: 0
    }

    data class EventCount(
        val total: Int = 0
    )
}
