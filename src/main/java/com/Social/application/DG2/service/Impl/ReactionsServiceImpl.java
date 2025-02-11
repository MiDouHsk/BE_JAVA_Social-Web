package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.dto.ReactionsDto;
import com.Social.application.DG2.entity.Comments;
import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.Reactions;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.repositories.CommentsRepository;
import com.Social.application.DG2.repositories.PostsRepository;
import com.Social.application.DG2.repositories.ReactionsRepository;
import com.Social.application.DG2.repositories.UsersRepository;
import com.Social.application.DG2.service.ReactionsService;
import com.Social.application.DG2.util.exception.NotFoundException;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReactionsServiceImpl implements ReactionsService {
    private final ReactionsRepository reactionsRepository;
    private final UsersRepository usersRepository;
    private final Cache<String, List<Reactions>> myCache;
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;

    public ReactionsServiceImpl(ReactionsRepository reactionsRepository, UsersRepository usersRepository, Cache<String, List<Reactions>> myCache, PostsRepository postsRepository, CommentsRepository commentsRepository) {
        this.reactionsRepository = reactionsRepository;
        this.usersRepository = usersRepository;
        this.myCache = myCache;
        this.postsRepository = postsRepository;
        this.commentsRepository = commentsRepository;
    }

    public ResponseEntity<String> createReaction(ReactionsDto reactionDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        Optional<Reactions> existingReaction = reactionsRepository.findByCreatedByAndObjectIdAndType(currentUser, reactionDTO.getObjectId(), reactionDTO.getType());

        if (existingReaction.isPresent()) {
            return ResponseEntity.badRequest().body("Bạn đã thả cảm xúc cho đối tượng này trước đó.");
        } else {
            if ("Posts".equals(reactionDTO.getObjectType())) {
                Optional<Posts> post = postsRepository.findById(reactionDTO.getObjectId());
                if (post.isEmpty()) {
                    throw new NotFoundException("Không tìm thấy bài viết!");
                }

                Posts posts = post.get();
                posts.setTotalLike(posts.getTotalLike() + 1);
                postsRepository.save(posts);

            } else if ("Comments".equals(reactionDTO.getObjectType())) {
                Optional<Comments> comments = commentsRepository.findById(reactionDTO.getObjectId());
                if (comments.isEmpty()) {
                    throw new NotFoundException("Không tìm thấy Comment!");
                }

                Comments comment = comments.get();
                comment.setTotalLike(comment.getTotalLike() + 1);
                commentsRepository.save(comment);
            }

            Reactions reaction = new Reactions();
            reaction.setId(UUID.randomUUID().toString());
            reaction.setCreatedBy(currentUser);

            reaction.setObjectType(reactionDTO.getObjectType());
            reaction.setObjectId(reactionDTO.getObjectId());
            reaction.setType(reactionDTO.getType());

            reactionsRepository.save(reaction);
            return ResponseEntity.ok("Tạo thành công cảm xúc.");
        }
    }


    @Override
    public void deleteReaction(String postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        if (currentUser != null) {
            // Xóa các phản ứng dựa trên postId và userId
            reactionsRepository.deleteByObjectIdAndCreatedBy(postId, currentUser.getId());

            // Giảm tổng số lượng like của bài viết đi 1
            Optional<Posts> optionalPost = postsRepository.findById(postId);
            if (optionalPost.isPresent()) {
                Posts post = optionalPost.get();
                post.setTotalLike(post.getTotalLike() - 1);
                postsRepository.save(post);
            } else {
                throw new IllegalArgumentException("Không tìm thấy bài viết cho postId đã cho.");
            }
        } else {
            throw new IllegalStateException("Không thể xác định người dùng hiện tại.");
        }
    }


    @Override
    public void updateReaction(String reactionId, ReactionsDto updatedReactionDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        Optional<Reactions> reactionOptional = reactionsRepository.findById(reactionId);
        if (reactionOptional.isPresent()) {
            Reactions reaction = reactionOptional.get();
            if (reaction.getCreatedBy().equals(currentUser)) {

                reaction.setObjectType(updatedReactionDto.getObjectType());
                reaction.setObjectId(updatedReactionDto.getObjectId());
                reaction.setType(updatedReactionDto.getType());
                reactionsRepository.save(reaction);
                ResponseEntity.ok("Chỉnh sửa cảm xúc thành công.");
            } else {
                throw new IllegalArgumentException("Bạn không thể chỉnh sửa cảm xúc của người khác.");
            }
        } else {
            throw new IllegalArgumentException("Không tìm thấy ID reactions.");
        }
    }

    @Override
    public int getReactionCountByIdPost(String object_id) {
        String cacheKey = "ReactionCountByIdPost_" + object_id;
        List<Reactions> reactions;
        if (myCache.getIfPresent(cacheKey) != null) {
            reactions = myCache.getIfPresent(cacheKey);
        } else {
            reactions = reactionsRepository.findByObjectId(object_id);
            myCache.put(cacheKey, reactions);
        }
        return reactions.size();
    }

    @Override
    public int getReactionCountByTypeAndObjectId(String object_id, String type) {
        String cacheKey = "ReactionCountByTypeAndObjectId_" + object_id + "_" + type;
        List<Reactions> reactions;
        if (myCache.getIfPresent(cacheKey) != null) {
            reactions = myCache.getIfPresent(cacheKey);
        } else {
            reactions = reactionsRepository.findByObjectIdAndType(object_id, type);
            myCache.put(cacheKey, reactions);
        }
        return reactions.size();
    }

    @Override
    public Page<Users> getUserByReaction(String objectId, String type, Pageable pageable) {
        String cacheKey = objectId + "_" + type;
        List<Reactions> reactions;

        if (myCache.getIfPresent(cacheKey) != null) {
            reactions = myCache.getIfPresent(cacheKey);
        } else {
            reactions = reactionsRepository.findAllByObjectIdAndType(objectId, type);
            myCache.put(cacheKey, reactions);
        }

        if (!reactions.isEmpty()) {
            List<Users> usersList = reactions.stream()
                    .map(Reactions::getCreatedBy)
                    .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), usersList.size());
            List<Users> pagedUsersList = usersList.subList(start, end);
            return new PageImpl<>(pagedUsersList, pageable, usersList.size());
        } else {
            throw new IllegalArgumentException("Không tìm thấy cảm xúc cho loại: " + type + " và ID: " + objectId);
        }
    }

    @Override
    public Page<Users> getAllUsersInReactions(Pageable pageable) {
        List<Reactions> reactions;
        String cacheKey = "allUsers";

        if (myCache.getIfPresent(cacheKey) != null) {
            reactions = myCache.getIfPresent(cacheKey);
        } else {
            reactions = reactionsRepository.findAll();
            myCache.put(cacheKey, reactions);
        }

        List<Users> usersList = reactions.stream()
                .map(Reactions::getCreatedBy)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), usersList.size());
        return new PageImpl<>(usersList.subList(start, end), pageable, usersList.size());
    }
}
