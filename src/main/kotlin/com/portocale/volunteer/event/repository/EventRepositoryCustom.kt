package com.portocale.volunteer.event.repository

import com.portocale.volunteer.event.EventFilterApi
import com.portocale.volunteer.event.repository.EventRepositoryCustomImpl.EventAggregationResult

interface EventRepositoryCustom {
    fun findByFilter(filter: EventFilterApi): EventAggregationResult
}
