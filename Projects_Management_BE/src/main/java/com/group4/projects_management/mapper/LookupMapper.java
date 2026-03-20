package com.group4.projects_management.mapper;

import com.group4.common.dto.LookupDTO;
import com.group4.projects_management.entity.BaseLookup;
import org.mapstruct.Mapper;

import java.io.Serializable;

@Mapper(componentModel = "spring")
public abstract class LookupMapper {
    public abstract LookupDTO toDto(BaseLookup<?> entity);

    public String mapId(Serializable id) {
        if (id == null) {
            return null;
        }
        return String.valueOf(id);
    }
}
