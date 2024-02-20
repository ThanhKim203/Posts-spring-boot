package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import project.model.Post;
import project.repository.PostRepository;

import javax.transaction.Transactional;
import java.awt.print.Pageable;

@Service
@RequestMapping(path = "api/v1/token")
public class PostService {
    @Autowired
    PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll((org.springframework.data.domain.Pageable) pageable);
    }
}
