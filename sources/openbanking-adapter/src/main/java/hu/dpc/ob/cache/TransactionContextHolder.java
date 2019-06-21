/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashSet;

@Component
public class TransactionContextHolder {

    private static Logger log = LoggerFactory.getLogger(TransactionContextHolder.class);

    private CacheManager cacheManager;
    private Cache<String, TransactionContext> transactionContextCache; // transactionId -> transaction context
//    private Cache<Integer, PartyContext> partyCache; // hashCode(party idType, id, subTypeOrId) -> party context
    private Cache<Integer, HashSet> partyTransactionCache; // WAITING transactions only! - hashCode(party idType, id, subTypeOrId) -> list of waiting transactionIds
    private Cache<String, String> channelClientRefCache; // clientRef -> transactionId
    private Cache<String, String> transferCache; // transferId -> transactionId


    @PostConstruct
    public void postConstruct() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
//                .withCache("transactionContext",
//                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, TransactionCacheContext.class, ResourcePoolsBuilder.heap(10000)))
//                .withCache("partyCache",
//                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, PartyContext.class, ResourcePoolsBuilder.heap(10000)))
//                .withCache("partyTransactionCache",
//                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, HashSet.class, ResourcePoolsBuilder.heap(10000)))
//                .withCache("channelClientRefCache",
//                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10000)))
//                .withCache("transferCache",
//                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10000)))
                .build();
        cacheManager.init();
//
//        transactionContextCache = cacheManager.getCache("transactionContext", String.class, TransactionCacheContext.class);
//        partyCache = cacheManager.getCache("partyCache", Integer.class, PartyContext.class);
//        partyTransactionCache = cacheManager.getCache("partyTransactionCache", Integer.class, HashSet.class);
//        channelClientRefCache = cacheManager.getCache("channelClientRefCache", String.class, String.class);
//        transferCache = cacheManager.getCache("transferCache", String.class, String.class);
    }

    @PreDestroy
    public void preDestroy() {
        cacheManager.removeCache("transactionContext");
        cacheManager.removeCache("partyCache");
        cacheManager.removeCache("partyTransactionCache");
        cacheManager.removeCache("channelClientRef");
        cacheManager.removeCache("transferCache");
        cacheManager.close();
    }
}
