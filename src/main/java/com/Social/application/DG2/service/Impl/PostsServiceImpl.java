package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.dto.PostsDto;
import com.Social.application.DG2.entity.Medias;
import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.mapper.PostsMapper;
import com.Social.application.DG2.repositories.*;
import com.Social.application.DG2.service.FavoritesService;
import com.Social.application.DG2.service.MediaService;
import com.Social.application.DG2.service.PostsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.Media;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostsServiceImpl implements PostsService{
    @Autowired
    private  PostsRepository postsRepository;
    @Autowired
    private  UsersRepository usersRepository;
    @Autowired
    private  MediaRepository mediaRepository;
    @Autowired
    private PostsMapper postsMapper;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private FavoritesService favoritesService;
    @Autowired
    private MediaService mediaService;

    @Override
    public Posts createPosts(PostsDto postDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }

        Posts post = new Posts();
        post.setId(UUID.randomUUID().toString());
        post.setTitle(postDto.getTitle());
        post.setBody(postDto.getBody());
        post.setStatus(postDto.getStatus());

        post.setTotalLike(0);
        post.setTotalComment(0);

        post.setUserId(currentUser);
        post.setCreateAt(new Timestamp(System.currentTimeMillis()));
        Posts createdPost = postsRepository.save(post);

        List<String> mediasIdStrings = postDto.getMediasId();
        List<Medias> medias = new ArrayList<>();
        for (String mediaId : mediasIdStrings) {
            Optional<Medias> mediaOptional  = mediaRepository.findById(mediaId);
            if (mediaOptional.isPresent()) {
                Medias media = mediaOptional.get();
                media.setPostsId(createdPost);
                medias.add(media);
            }
        }
        createdPost.setMedias(medias);
        postsRepository.save(createdPost);

        return createdPost;
    }

    @Override
    public void updatePost(UUID postId, PostsDto updatedPost) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        if (currentUser == null) {
            throw new EntityNotFoundException("Không tìm thấy người dùng hiện tại");
        }

        Optional<Posts> optionalPost = postsRepository.findById(postId.toString());
        if (!optionalPost.isPresent()) {
            throw new EntityNotFoundException("Không tìm thấy bài đăng có ID: " + postId);
        }

        Posts post = optionalPost.get();

        if (!post.getUserId().equals(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật bài đăng này");
        }

        post.setTitle(updatedPost.getTitle());
        post.setBody(updatedPost.getBody());
        post.setStatus(updatedPost.getStatus());
        post.setCreateAt(new Timestamp(System.currentTimeMillis()));

        List<String> mediasIdStrings = updatedPost.getMediasId();
        List<Medias> medias = new ArrayList<>();
        for (String mediaId : mediasIdStrings) {
            Optional<Medias> mediaOptional  = mediaRepository.findById(mediaId);
            if (mediaOptional.isPresent()) {
                Medias media = mediaOptional.get();
                media.setPostsId(post);
                medias.add(media);
            }
        }
        post.setMedias(medias);


        postsRepository.save(post);
    }

    @Override
    public void deletePost(UUID postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        if (currentUser == null) {
            throw new EntityNotFoundException("Không tìm thấy người dùng hiện tại");
        }

        Optional<Posts> optionalPost = postsRepository.findById(postId.toString());
        if (optionalPost.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy bài đăng có ID: " + postId);
        }

        Posts post = optionalPost.get();
        if (!post.getUserId().equals(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền Xóa bài đăng này");
        }

//        xóa media trên minIO
        List<Medias> medias = post.getMedias();
        for (Medias media : medias) {
            try {
                String objectName = media.getBaseName();
                mediaService.deletePost(objectName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
//        xóa media
        mediaRepository.deleteById(postId.toString());
//        xóa comments
        commentsRepository.deleteByPostId(postId.toString());
//        xóa favorites
//        favoritesService.deleteFavorite(postId.toString());
//        xóa bài posts
        postsRepository.delete(post);
    }

    @Override
    public Page<Posts> getPostsByUserId(Pageable pageable, UUID userId) {
        Optional<Users> userOptional = usersRepository.findById(String.valueOf(userId));
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            return postsRepository.findByUserId(user, pageable);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
    }

    @Override
    public int getNumberOfPostsByUserId(UUID userId) {
        Optional<Users> userOptional = usersRepository.findById(String.valueOf(userId));
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            return postsRepository.countByUserId(user);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
    }

    @Override
    public List<Posts> getAllPosts(Pageable pageable) {
        return postsRepository.findAll();
    }

    @Override
    public int getNumberOfPosts() {
        List<Posts> allPosts = postsRepository.findAll();
        return allPosts.size();
    }


    @Override
    public Page<Posts> getListOfPostsByLoggedInUser(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Users users = usersRepository.findByUsername(currentUsername);
        if (users != null) {
            return postsRepository.findByUserId(users, pageable);
        } else {
            throw new EntityNotFoundException("User not found with username: " + currentUsername);
        }
    }

    @Override
    public int getNumberOfPostsByLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Users users = usersRepository.findByUsername(currentUsername);
        if (users != null) {
            return postsRepository.countByUserId(users);
        } else {
            throw new EntityNotFoundException("User not found with username: " + currentUsername);
        }
    }
}