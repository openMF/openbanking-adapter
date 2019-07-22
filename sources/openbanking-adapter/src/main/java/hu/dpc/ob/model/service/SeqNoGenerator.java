/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.model.service;

import hu.dpc.ob.domain.entity.ConsentEvent;
import hu.dpc.ob.domain.entity.PaymentEvent;
import hu.dpc.ob.domain.repository.ConsentEventRepository;
import hu.dpc.ob.domain.repository.PaymentEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static javax.transaction.Transactional.TxType.MANDATORY;

@Component
public class SeqNoGenerator {

    private final ConsentEventRepository consentEventRepository;
    private final PaymentEventRepository paymentEventRepository;

    private ConcurrentHashMap<Class, AtomicLong> seqNos = new ConcurrentHashMap<>(2);

    @Autowired
    public SeqNoGenerator(ConsentEventRepository consentEventRepository, PaymentEventRepository paymentEventRepository) {
        this.consentEventRepository = consentEventRepository;
        this.paymentEventRepository = paymentEventRepository;
    }

    public long getNextSeqNo(@NotNull Class clazz) {
        if (ConsentEvent.class.isAssignableFrom(clazz))
            return getConsentEventNextSeqNo();
        else if (PaymentEvent.class.isAssignableFrom(clazz))
            return getPaymentEventNextSeqNo();
        throw new UnsupportedOperationException("Can not generate sequence number for type " + clazz);
    }

    @Transactional(MANDATORY)
    public long getConsentEventNextSeqNo() {
        AtomicLong seqNo = seqNos.computeIfAbsent(ConsentEvent.class, k -> {
            ConsentEvent max = consentEventRepository.findTopByOrderBySeqNoDesc();
            return new AtomicLong(max == null ? 0 : max.getSeqNo());
        });
        // TODO: no one else is touching the sequences, otherwise find check before return
        return seqNo.incrementAndGet();
    }

    @Transactional(MANDATORY)
    public long getPaymentEventNextSeqNo() {
        AtomicLong seqNo = seqNos.computeIfAbsent(PaymentEvent.class, k -> {
            PaymentEvent max = paymentEventRepository.findTopByOrderBySeqNoDesc();
            return new AtomicLong(max == null ? 0 : max.getSeqNo());
        });
        // TODO: no one else is touching the sequences, otherwise find check before return
        return seqNo.incrementAndGet();
    }
}
