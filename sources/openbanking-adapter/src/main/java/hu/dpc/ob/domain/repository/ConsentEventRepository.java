/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.repository;

import hu.dpc.ob.domain.entity.ConsentEvent;
import hu.dpc.ob.domain.entity.User;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.ConsentActionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsentEventRepository extends JpaRepository<ConsentEvent, Long>, JpaSpecificationExecutor<ConsentEvent> {

    String EVENT_LIMIT_CRITERIA = "SELECT e FROM ConsentEvent e " +
            "JOIN e.consent c " +
            "WHERE c.user = :user " +
            "AND c.clientId = :clientId " +
            "AND c.scope = :scope " +
            "AND e.action = :action " +
            "AND e.status = hu.dpc.ob.domain.type.EventStatusCode.ACCEPTED ";

    String RESOURCE_CRITERIA = "AND e.resourceId > :resourceId ";
    String NULL_RESOURCE_CRITERIA = "AND e.resourceId IS NULL ";
    String DATE_CRITERIA = "AND e.createdOn > :fromDate AND e.createdOn <= :toDate ";

    String EVENT_LIMIT_ORDER = "ORDER BY e.seqNo ";


    ConsentEvent findTopByOrderBySeqNoDesc();

    @Query(value = EVENT_LIMIT_CRITERIA + NULL_RESOURCE_CRITERIA + EVENT_LIMIT_ORDER)
    List<ConsentEvent> findLimitEvents(@NotNull @Param("user") User user, @NotNull @Param("clientId") String clientId, @NotNull @Param("scope") ApiScope scope,
                                       @NotNull @Param("action") ConsentActionCode action);

    @Query(value = EVENT_LIMIT_CRITERIA + RESOURCE_CRITERIA + EVENT_LIMIT_ORDER)
    List<ConsentEvent> findLimitEvents(@NotNull @Param("user") User user, @NotNull @Param("clientId") String clientId, @NotNull @Param("scope") ApiScope scope,
                                       @NotNull @Param("action") ConsentActionCode action, @NotNull @Param("resourceId") String resourceId);

    @Query(value = EVENT_LIMIT_CRITERIA + DATE_CRITERIA + NULL_RESOURCE_CRITERIA + EVENT_LIMIT_ORDER)
    List<ConsentEvent> findLimitEvents(@NotNull @Param("user") User user, @NotNull @Param("clientId") String clientId, @NotNull @Param("scope") ApiScope scope,
                                       @NotNull @Param("action") ConsentActionCode action, @NotNull @Param("fromDate") LocalDateTime fromDate,
                                       @NotNull @Param("toDate") LocalDateTime toDate);

    @Query(value = EVENT_LIMIT_CRITERIA + DATE_CRITERIA + RESOURCE_CRITERIA + EVENT_LIMIT_ORDER)
    List<ConsentEvent> findLimitEvents(@NotNull @Param("user") User user, @NotNull @Param("clientId") String clientId, @NotNull @Param("scope") ApiScope scope,
                                       @NotNull @Param("action") ConsentActionCode action, @NotNull @Param("fromDate") LocalDateTime fromDate,
                                       @NotNull @Param("toDate") LocalDateTime toDate, @NotNull @Param("resourceId") String resourceId);

    ConsentEvent findTopByOrderBySeqNoAsc();
}
