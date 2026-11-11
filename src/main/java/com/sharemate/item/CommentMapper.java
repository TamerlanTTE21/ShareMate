package com.sharemate.item;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentDto convertToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}
