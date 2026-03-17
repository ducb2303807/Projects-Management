package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.LookupDTO;
import com.group4.common.enums.LookupType;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LookupApi extends AbstractAuthenticatedApi {
    private static final String ENDPOINT = "lookups";

    public LookupApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    public CompletableFuture<List<LookupDTO>> getAll(LookupType type) {
        String endpoint = ENDPOINT + "/" + type.name();
        return this.sendGetRequest(
                endpoint,
                LookupDTO[].class,
                null
        ).thenApply(List::of);
    }

    public CompletableFuture<LookupDTO> createOrUpdate(LookupType type, LookupDTO dto) {
        String endpoint = ENDPOINT + "/" + type.name();

        return this.sendPostRequest(
                endpoint,
                dto,
                LookupDTO.class,
                null
        );
    }
}
