package com.openelements.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.PublicKey;
import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents a smart contract on the Hiero network.
 */
public record Contract(
        @NonNull ContractId contractId,
        @Nullable AccountId adminKey,
        @Nullable AccountId autoRenewAccount,
        int autoRenewPeriod,
        @NonNull Instant createdTimestamp,
        @Nullable String fileId,
        @Nullable PublicKey memo,
        @Nullable String proxyAccountId,
        @Nullable PublicKey proxyAccountIdKey,
        @Nullable String runtimeBytecode,
        @Nullable String bytecode,
        @Nullable String evmAddress,
        @Nullable String solidityAddress,
        boolean deleted,
        @NonNull Instant fromTimestamp,
        @NonNull Instant toTimestamp
) {
    public Contract {
        Objects.requireNonNull(contractId, "contractId must not be null");
        Objects.requireNonNull(createdTimestamp, "createdTimestamp must not be null");
        Objects.requireNonNull(fromTimestamp, "fromTimestamp must not be null");
        Objects.requireNonNull(toTimestamp, "toTimestamp must not be null");
    }
}
