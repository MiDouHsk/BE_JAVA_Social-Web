package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.dto.CommentsDto;
import com.Social.application.DG2.entity.Comments;
import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.repositories.CommentsRepository;
import com.Social.application.DG2.repositories.PostsRepository;
import com.Social.application.DG2.repositories.UsersRepository;
import com.Social.application.DG2.service.CommentsService;
import com.Social.application.DG2.service.PostsService;
import com.Social.application.DG2.util.exception.NotFoundException;
import com.Social.application.DG2.util.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentsServiceImpl implements CommentsService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private PostsService postsService;
    @Override
    public Comments saveComment(CommentsDto commentsDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null ) {
            throw new NotFoundException("bạn cần đăng nhập để thực hiện hành động này !");
        }

        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        Optional<Posts> post = postsRepository.findById(commentsDto.getPostId());
        if (post .isEmpty()) {
            throw new NotFoundException("Không tìm thấy bài viết!");
        }

        Posts posts = post.get();
        posts.setTotalComment(posts.getTotalComment() + 1);

        Comments comments = new Comments();
        comments.setContent(commentsDto.getContent());
        comments.setCreateBy(currentUser);
        comments.setPostId(post.get());
        comments.setCreateAt(new Timestamp(System.currentTimeMillis()));
        postsRepository.save(posts);

        return commentsRepository.save(comments);
    }

    @Override
    public Page<Comments> getCommentsByPostId(String postId, Pageable pageable) {
        return commentsRepository.findByPostId(postId, pageable);
    }

    @Override
    public void deleteComment(UUID commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null ) {
            throw new NotFoundException("bạn cần đăng nhập để thực hiện hành động này !");
        }

        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        Optional<Comments> comments = commentsRepository.findById(commentId.toString());
        if (comments.isEmpty()){
            throw new NotFoundException("không tìm thấy bài viết cần xóa !");
        }
        Comments comment = comments.get();
        if (!comment.getCreateBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Bạn không có quyền xóa bình luận này!");
        }
        Posts posts = comment.getPostId();
        posts.setTotalComment(posts.getTotalComment() - 1);


        postsRepository.save(posts);

        commentsRepository.deleteById(commentId.toString());
    }

    @Override
    public void updateComments(CommentsDto commentsDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null ) {
            throw new NotFoundException("bạn cần đăng nhập để thực hiện hành động này !");
        }

        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }


        Comments comment = commentsRepository.findById(commentsDto.getId()).orElse(null);
        if (comment == null){
            throw new NotFoundException("Không tìm thấy bình luận cần cập nhật !");
        }

        if (!comment.getCreateBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Bạn không có quyền cập nhật bình luận này!");
        }

        comment.setContent(commentsDto.getContent());
        commentsDto.setCreateAt(new Timestamp(System.currentTimeMillis()));
        commentsRepository.save(comment);
    }
}
