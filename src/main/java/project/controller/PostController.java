package project.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.dto.PostDto;
import project.model.Post;
import project.repository.PostRepository;
import project.repository.UserRepository;
import project.service.PostService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "api/v1")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PostController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> postModelList = postRepository.findAll();
        if (postModelList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            for (Post post : postModelList) {
                UUID id = post.getPostId();
                post.add(linkTo(methodOn(PostController.class).getOnePost(id)).withSelfRel());
            }
        }
        return new ResponseEntity<List<Post>>(postRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getOnePost(@PathVariable(value = "id") UUID id) {
        Optional<Post> postOne = postRepository.findById(id);
        if (postOne.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        postOne.get().add(linkTo(methodOn(PostController.class).getAllPosts()).withRel("Post List"));
        return new ResponseEntity<Post>(postOne.get(), HttpStatus.OK);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable(value = "id") UUID id) {
        Optional<Post> postDelete = postRepository.findById(id);
        if (postDelete.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        postRepository.delete(postDelete.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable(value="id") UUID id,
    @RequestBody @Valid Post post){
        Optional<Post> postUpdate = postRepository.findById(id);
        if (postUpdate.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        post.setPostId(postUpdate.get().getPostId());
        return new ResponseEntity<Post>(postRepository.save(post), HttpStatus.OK);
    }

    @PostMapping("/posts/v1")
    public ResponseEntity<Object> savePostV1(@RequestBody @Valid PostDto postDto) {
        var postModel = new Post();
        BeanUtils.copyProperties(postDto, postModel);
        postModel.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.save(postModel));
    }

    @GetMapping("/post")
    public List<Post> showPosts() {
        return postRepository.findAll();
    }

    @PostMapping("/post/owner")
    public Post savePost(@RequestBody Post post) {
        post.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        return postRepository.save(post);
    }

    @PutMapping("post/{idpost}/owner/{idowner}")
    public Post updatePostWithOwner(@PathVariable UUID idpost, @PathVariable UUID idowner) {
        Post post = postRepository.findById(idpost).get();
//        User user = ownerRepository.findById(idowner).get();
//        post.assignUser(user);
        return postRepository.save(post);
    }
}
