/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "remittance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"payment_id"}, name = "uk_remittance.payment")})
public final class Remittance extends AbstractEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 140)
    @Column(name = "unstructured", length = 140)
    private String unstructured; // Information supplied to enable the matching/reconciliation of an entry with the items that the payment is intended to settle, such as commercial invoices in an accounts' receivable system, in an unstructured form

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 35)
    @Column(name = "reference", length = 35)
    private String reference; // Unique reference, as assigned by the creditor, to unambiguously refer to the payment. If available, the initiating party should provide this reference, to enable reconciliation by the creditor upon receipt of the amount of money.

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "supplementary", length = 256)
    private String supplementary; // Additional information that can not be captured in the structured fields and/or any other specific block.


    public Remittance(@NotNull Payment payment, @Size(max = 140) String unstructured, @Size(max = 35) String reference, String supplementary) {
        this.payment = payment;
        this.unstructured = unstructured;
        this.reference = reference;
        this.supplementary = supplementary;
    }
}
