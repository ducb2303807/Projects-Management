package com.group4.projects_management.mapper;

import com.group4.common.dto.PluginDTO;
import com.group4.common.dto.UserWidgetConfigDTO;
import com.group4.projects_management.entity.Plugin;
import com.group4.projects_management.entity.UserWidgetConfig;
import org.springframework.stereotype.Component;

@Component
public class PluginMapper {

    public PluginDTO toDto(Plugin entity) {
        if (entity == null) return null;
        return new PluginDTO(
                entity.getId(),
                entity.getName(),
                entity.getVersion(),
                entity.getDescription(),
                entity.getMainClass(),
                entity.getPath()
        );
    }

    public UserWidgetConfigDTO toDto(UserWidgetConfig entity) {
        if (entity == null) return null;
        UserWidgetConfigDTO dto = new UserWidgetConfigDTO();
        dto.setWidgetConfigId(entity.getId());
        dto.setPluginId(entity.getPlugin().getId());
        dto.setPluginName(entity.getPlugin().getName());
        dto.setIsVisible(true); // hoặc lấy từ entity nếu có field isVisible
        dto.setPosX(entity.getPosX());
        dto.setPosY(entity.getPosY());
        return dto;
    }

    public UserWidgetConfig toEntity(UserWidgetConfigDTO dto) {
        if (dto == null) return null;
        UserWidgetConfig entity = new UserWidgetConfig();
        entity.setId(dto.getWidgetConfigId());
        entity.setPosX(dto.getPosX());
        entity.setPosY(dto.getPosY());
        // plugin và user sẽ được set trong service khi save
        return entity;
    }
}
