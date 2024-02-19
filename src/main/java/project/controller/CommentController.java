package project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.model.Comment;
import project.model.Post;
import project.repository.CommentRepository;
import project.repository.PostRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "api/v1")
public class CommentController {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @GetMapping("/comment")
    public Page<Comment> showComments(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> sortBy
    ) {
        List<Comment> commentModelList = commentRepository.findAll();
        if (commentModelList.isEmpty()) {
            return commentRepository.findAll(
                    PageRequest.of(
                            page.orElse(0),
                            2,
                            Sort.Direction.ASC, sortBy.orElse("idcomment")
                    )
            );
        } else {
            for (Comment comment : commentModelList) {
                UUID id = comment.getCommentId();
                comment.add(linkTo(methodOn(CommentController.class).getOneComment(id)).withSelfRel());
            }
        }
        return commentRepository.findAll(
                PageRequest.of(
                        page.orElse(0),
                        2,
                        Sort.Direction.ASC, sortBy.orElse("idcomment")
                )
        );
    }

    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> commentModelList = commentRepository.findAll();
        if (commentModelList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            for (Comment comment : commentModelList) {
                UUID id = comment.getCommentId();
                comment.add(linkTo(methodOn(CommentController.class).getOneComment(id)).withSelfRel());
            }
        }
        return new ResponseEntity<List<Comment>>(commentRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/comment/{id}")
    public ResponseEntity<Comment> getOneComment(@PathVariable(value = "id") UUID id) {
        Optional<Comment> commentModel = commentRepository.findById(id);
        if (commentModel.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        commentModel.get().add(linkTo(methodOn(CommentController.class).getAllComments()).withRel("Comment List"));
        return new ResponseEntity<Comment>(commentModel.get(), HttpStatus.OK);
    }

    @PostMapping("/comment")
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        comment.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        return new ResponseEntity<Comment>(commentRepository.save(comment), HttpStatus.OK);
    }

    @PutMapping("comment/{idcomment}/post/{idpost}")
    public Comment updateCommentWithPost(@PathVariable UUID idcomment, @PathVariable UUID idpost) {
        Comment comment = commentRepository.findById(idcomment).get();
        Post post = postRepository.findById(idpost).get();
        //comment.assignPost(post);
        return commentRepository.save(comment);
    }
}
