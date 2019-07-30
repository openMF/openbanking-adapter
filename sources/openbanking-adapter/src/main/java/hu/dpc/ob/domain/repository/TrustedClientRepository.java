/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.repository;

import hu.dpc.ob.domain.entity.TrustedClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface TrustedClientRepository extends JpaRepository<TrustedClient, Long>, JpaSpecificationExecutor<TrustedClient> {

    TrustedClient findByClientId(@NotNull String clientId);
}
