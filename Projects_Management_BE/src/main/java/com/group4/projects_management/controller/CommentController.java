package com.group4.projects_management.controller; /***********************************************************************
 * Module:  CommentController.java
 * Author:  Lenovo
 * Purpose: Defines the Class CommentController
 ***********************************************************************/

import com.group4.common.dto.CommentCreateRequestDTO;
import com.group4.common.dto.CommentDTO;
import com.group4.projects_management.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @pdOid 98320d4a-632b-4031-ac14-b0d540fb9dfd
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentCreateRequestDTO request) {
        return ResponseEntity.ok(commentService.createComment(request.getTaskId(), request.getProjectMemberId(), request.getContent(), request.getParentId()));
    }

    @PostMapping("/{parentId}/reply")
    public ResponseEntity<CommentDTO> replyComment(
            @PathVariable Long parentId,
            @RequestBody CommentCreateRequestDTO request) {
        return ResponseEntity.ok(commentService.createComment(request.getTaskId(), request.getProjectMemberId(), request.getContent(), request.getParentId()));
    }
}