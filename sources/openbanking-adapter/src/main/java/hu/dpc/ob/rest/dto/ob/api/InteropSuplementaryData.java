/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.InteropExtension;
import hu.dpc.ob.domain.entity.InteropPayment;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.type.InteropAmountType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class InteropSuplementaryData {

    @JsonProperty(required = true)
    @NotNull
    private InteropAmountType amountType;
    @JsonProperty(required = true)
    @NotNull
    private InteropTransactionTypeData transactionType;
    @Size(max = 128)
    private String note;
    @Valid
    private GeoCodeData geoCode;
    @Size(max = 16)
    private List<ExtensionData> extensionList;

    InteropSuplementaryData(@NotNull InteropAmountType amountType, @NotNull InteropTransactionTypeData transactionType,
                                   @Size(max = 128) String note, @Valid GeoCodeData geoCode, @Size(max = 16) List<ExtensionData> extensionList) {
        this.amountType = amountType;
        this.transactionType = transactionType;
        this.note = note;
        this.geoCode = geoCode;
        this.extensionList = extensionList;
    }

    public static InteropSuplementaryData create(InteropPayment interopPayment) {
        if (interopPayment == null)
            return null;

        List<InteropExtension> extensions = interopPayment.getExtensions();
        List<ExtensionData> extensionList = extensions.isEmpty() ? null : extensions.stream().map(ExtensionData::create).collect(Collectors.toList());

        return new InteropSuplementaryData(interopPayment.getAmountType(), InteropTransactionTypeData.create(interopPayment),
                interopPayment.getNote(), GeoCodeData.create(interopPayment), extensionList);
    }

    @NotNull
    public InteropPayment mapToEntity(@NotNull Payment payment) {
        @Valid InteropRefundData refundInfo = transactionType.getRefundInfo();
        @Size(max = 36) @NotNull String refundTransactionId = refundInfo == null ? null : refundInfo.getOriginalTransactionId();
        @Size(max = 128) String refundReason = refundInfo == null ? null : refundInfo.getRefundReason();

        @NotNull String longitude = getGeoCode() == null ? null : geoCode.getLongitude();
        @NotNull String latitude = getGeoCode() == null ? null : geoCode.getLatitude();

        InteropPayment interopPayment = new InteropPayment(payment, amountType, transactionType.getScenario(), transactionType.getSubScenario(),
                transactionType.getInitiator(), transactionType.getInitiatorType(), refundTransactionId, refundReason, transactionType.getBalanceOfPayments(),
                longitude, latitude, note);

        if (getExtensionList() != null) {
            for (ExtensionData extension : extensionList) {
                interopPayment.addExtension(extension.getKey(), extension.getValue());
            }
        }
        return interopPayment;
    }

    String updateEntity(@NotNull Payment payment) {
        InteropPayment interopPayment = payment.getInteropPayment();
        if (interopPayment == null) {
            return "Interoperation details were not specified";
        }
        if (!amountType.equals(interopPayment.getAmountType()))
            return "Consent amountType " + interopPayment.getAmountType() + " does not match requested amountType " + amountType;

        String failureReason = transactionType.updateEntity(interopPayment);
        if (failureReason != null)
            return failureReason;
        if (note != null)
            interopPayment.setNote(note);
        if (geoCode != null) {
            failureReason = geoCode.updateEntity(interopPayment);
            if (failureReason != null)
                return failureReason;
        }
        if (extensionList != null) {
            List<InteropExtension> extensions = interopPayment.getExtensions();
            int size = extensions.size();
            for (int i = 0; i < extensionList.size(); i++) {
                ExtensionData extension = extensionList.get(i);
                if (size > i) {
                    InteropExtension ext = extensions.get(i);
                    ext.setKey(extension.getKey());
                    ext.setValue(extension.getValue());
                }
                else {
                    interopPayment.addExtension(extension.getKey(), extension.getValue());
                }
            }
            if (size > extensionList.size()) {
                for (int i = size; --i >= extensionList.size();)
                    interopPayment.removeExtension(extensions.get(i));
            }
        }

        return null;
    }
}
