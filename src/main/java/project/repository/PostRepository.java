package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.model.Post;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Post findByPostId(UUID id);
}
