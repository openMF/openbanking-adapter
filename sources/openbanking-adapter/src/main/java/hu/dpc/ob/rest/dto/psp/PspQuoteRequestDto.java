/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import hu.dpc.ob.domain.entity.InteropExtension;
import hu.dpc.ob.domain.entity.InteropPayment;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.type.InteropAmountType;
import hu.dpc.ob.domain.type.InteropTransactionRole;
import hu.dpc.ob.rest.dto.ob.api.ExtensionData;
import hu.dpc.ob.rest.dto.ob.api.GeoCodeData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PspQuoteRequestDto {

    @NotNull
    private String transactionCode;
    @NotNull
    private String quoteCode;
    @NotNull
    private String accountId;
    @NotNull
    private PspAmountData amount;
    @NotNull
    private InteropAmountType amountType;
    @NotNull
    private InteropTransactionRole transactionRole;

    private PspInteropTransactionTypeData transactionType;

    private String note;

    private GeoCodeData geoCode;

    private LocalDateTime expiration;

    private List<ExtensionData> extensionList;

    public PspQuoteRequestDto(@NotNull String transactionCode, @NotNull String quoteCode, @NotNull String accountId, @NotNull PspAmountData amount,
                              @NotNull InteropAmountType amountType, @NotNull InteropTransactionRole transactionRole, PspInteropTransactionTypeData transactionType,
                              String note, GeoCodeData geoCode, LocalDateTime expiration, List<ExtensionData> extensionList) {
        this.transactionCode = transactionCode;
        this.quoteCode = quoteCode;
        this.accountId = accountId;
        this.amount = amount;
        this.amountType = amountType;
        this.transactionRole = transactionRole;
        this.transactionType = transactionType;
        this.note = note;
        this.geoCode = geoCode;
        this.expiration = expiration;
        this.extensionList = extensionList;
    }

    @NotNull
    public static PspQuoteRequestDto create(Payment payment, String quoteId, String accountId) {
        InteropPayment interopPayment = payment.getInteropPayment();
        if (interopPayment == null)
            return null;

        List<InteropExtension> extensions = interopPayment.getExtensions();
        List<ExtensionData> extensionList = extensions.isEmpty() ? null : extensions.stream().map(ExtensionData::create).collect(Collectors.toList());

        return new PspQuoteRequestDto(payment.getPaymentId(), quoteId, accountId, PspAmountData.create(payment), interopPayment.getAmountType(),
                interopPayment.getInitiator(), PspInteropTransactionTypeData.create(interopPayment), interopPayment.getNote(),
                GeoCodeData.create(interopPayment), payment.getExpiresOn(), extensionList);
    }
}
