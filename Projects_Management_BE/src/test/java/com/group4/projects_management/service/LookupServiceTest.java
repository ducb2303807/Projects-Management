package com.group4.projects_management.service;

import com.group4.common.dto.LookupDTO;
import com.group4.common.enums.LookupType;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.entity.Priority;
import com.group4.projects_management.entity.TaskStatus;
import com.group4.projects_management.mapper.LookupMapper;
import com.group4.projects_management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LookupServiceTest {
    @Mock
    private AppRoleRepository appRoleRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private ProjectRoleRepository projectRoleRepository;
    @Mock
    private PriorityRepository priorityRepository;
    @Mock
    private ProjectStatusRepository projectStatusRepository;
    @Mock
    private ProjectMemberStatusRepository projectMemberStatusRepository;
    @Mock
    private TaskStatusRepository taskStatusRepository;
    @Mock
    private LookupMapper lookupMapper;
    @InjectMocks
    private LookupServiceImpl lookupService;

    @BeforeEach
    void setUp() {
        lookupService.init();
    }

    @Nested
    @DisplayName("Tests cho hàm getAll")
    class GetAllTests {

        @Test
        @DisplayName("Nên trả về danh sách DTO khi loại danh mục tồn tại")
        void getAll_Success() {
            Priority priority = new Priority();
            priority.setId(1L);
            priority.setName("HIGH");
            priority.setDescription("Urgent task");
            priority.setSystemCode("PRIORITY_HIGH");

            LookupDTO expectedDto = new LookupDTO("1", "HIGH", "Urgent task", "PRIORITY_HIGH");

            when(priorityRepository.findAll()).thenReturn(List.of(priority));
            when(lookupMapper.toDto(any(Priority.class))).thenReturn(expectedDto);

            List<LookupDTO> result = lookupService.getAll(LookupType.PRIORITY);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("1", result.get(0).getId());
            assertEquals("HIGH", result.get(0).getName());
            assertEquals("PRIORITY_HIGH", result.get(0).getSystemCode());
            verify(priorityRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Nên ném lỗi RuntimeException nếu LookupType chưa được cấu hình registry")
        void getAll_Fail_TypeNotRegistered() {
            assertThrows(RuntimeException.class, () -> lookupService.getAll(null));
        }
    }

    @Nested
    @DisplayName("Tests cho hàm saveOrUpdate")
    class SaveOrUpdateTests {

        @Test
        @DisplayName("CREATE: Nên tạo mới khi ID null (Sử dụng TASK_STATUS)")
        void create_Success() {
            LookupDTO input = new LookupDTO(null, "NEW", "Desc", "TASK_NEW");
            TaskStatus saved = new TaskStatus();
            saved.setId(10L);
            saved.setName("NEW");
            saved.setSystemCode("TASK_NEW");

            LookupDTO expectedDto = new LookupDTO("10", "NEW", "Desc", "TASK_NEW");

            when(taskStatusRepository.save(any(TaskStatus.class))).thenReturn(saved);
            when(lookupMapper.toDto(any(TaskStatus.class))).thenReturn(expectedDto);

            LookupDTO result = lookupService.saveOrUpdate(LookupType.TASK_STATUS, input);

            assertEquals("10", result.getId());
            assertEquals("TASK_NEW", result.getSystemCode());
            verify(taskStatusRepository).save(any());
        }

        @Test
        @DisplayName("UPDATE: Nên cập nhật thành công với loại cho phép sửa (TASK_STATUS)")
        void update_Success() {
            LookupDTO input = new LookupDTO("1", "UPDATED", "Desc", "TASK_UPDATED");
            TaskStatus existing = new TaskStatus();
            existing.setId(1L);
            existing.setSystemCode("OLD_CODE");

            LookupDTO expectedDto = new LookupDTO("1", "UPDATED", "Desc", "TASK_UPDATED");

            when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(taskStatusRepository.save(any(TaskStatus.class))).thenAnswer(i -> i.getArgument(0));
            when(lookupMapper.toDto(any(TaskStatus.class))).thenReturn(expectedDto);

            LookupDTO result = lookupService.saveOrUpdate(LookupType.TASK_STATUS, input);

            assertEquals("UPDATED", result.getName());
            assertEquals("TASK_UPDATED", result.getSystemCode());
            verify(taskStatusRepository).save(any());
        }

        @Test
        @DisplayName("UPDATE_FAIL: Nên ném ResourceNotFoundException khi ID không tồn tại")
        void update_Fail_NotFound() {
            LookupDTO input = new LookupDTO("999", "NAME", "DESC", "SYS_CODE");

            when(taskStatusRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    lookupService.saveOrUpdate(LookupType.TASK_STATUS, input)
            );
        }

        @Test
        @DisplayName("SECURITY: Nên chặn chỉnh sửa APP_ROLE vì editable = false")
        void update_Fail_SystemLocked() {
            LookupDTO input = new LookupDTO("1", "HACK", "Desc", "HACK_CODE");

            assertThrows(RuntimeException.class, () ->
                    lookupService.saveOrUpdate(LookupType.APP_ROLE, input)
            );
        }
    }

    @Test
    @DisplayName("PARSE_ID: Kiểm tra parse ID String sang Long")
    void testParseIdLogic() {
        LookupDTO dtoNumeric = new LookupDTO("123", "Name", "Desc", "SYS_CODE");

        when(projectStatusRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                lookupService.saveOrUpdate(LookupType.PROJECT_STATUS, dtoNumeric)
        );

        verify(projectStatusRepository).findById(123L);
    }
}