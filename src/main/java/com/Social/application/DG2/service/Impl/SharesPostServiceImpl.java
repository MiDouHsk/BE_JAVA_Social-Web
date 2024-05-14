package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.dto.SharesPostDto;
import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.SharesPosts;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.repositories.PostsRepository;
import com.Social.application.DG2.repositories.SharesPostsRepository;
import com.Social.application.DG2.repositories.UsersRepository;
import com.Social.application.DG2.service.SharesPostService;

import java.util.List;

import com.Social.application.DG2.util.exception.NotFoundException;
import com.Social.application.DG2.util.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class SharesPostServiceImpl implements SharesPostService {
    private final SharesPostsRepository sharesPostsRepository;
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentsServiceImpl.class);
    @Autowired
    public SharesPostServiceImpl(SharesPostsRepository sharesPostsRepository, UsersRepository usersRepository, PostsRepository postsRepository) {
        this.sharesPostsRepository = sharesPostsRepository;
        this.usersRepository = usersRepository;
        this.postsRepository = postsRepository;
    }

    @Override
    public SharesPosts createdSharePost(SharesPostDto sharesPostDto) {
        String postId = sharesPostDto.getPostDtoId();
        String currentUsername = sharesPostDto.getUserDtoId();

        Optional<Posts> optionalPost = postsRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bài viết!");
        }

        Posts post = optionalPost.get();
        post.setTotalShare(post.getTotalShare() + 1);
        postsRepository.save(post);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        SharesPosts sharesPosts = new SharesPosts();
        sharesPosts.setUserId(currentUser);
        sharesPosts.setPostId(post);
        sharesPosts.setCreateAt(new Timestamp(System.currentTimeMillis()));

        logger.info("Share thành công bài viết với ID: {}", postId);
        return sharesPostsRepository.save(sharesPosts);
    }



    @Override
    public void deleteSharedPost(String sharesPostId, String currentUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !authentication.getName().equals(currentUsername)) {
            throw new UnauthorizedException("Bạn không có quyền xóa !");
        }

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        Optional<SharesPosts> optionalSavedPost = sharesPostsRepository.findById(sharesPostId);
        if (optionalSavedPost.isEmpty()) {
            throw new NotFoundException("Bài viết không được lưu!");
        }

        SharesPosts sharesPosts = optionalSavedPost.get();
        Posts originalPost = sharesPosts.getPostId();
        originalPost.setTotalShare(originalPost.getTotalShare() - 1);
        postsRepository.save(originalPost);

        logger.info("Xóa thành công bài viết đã Chia sẻ.");
        sharesPostsRepository.delete(sharesPosts);
    }

    @Override
    public List<SharesPosts> getSharedPostByCurrentUser(String currentUsername) {
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        logger.info("Lấy thành công bài viết cho user đã đăng nhập.");
        return sharesPostsRepository.findByUserId(currentUser);
    }

    @Override
    public List<SharesPosts> getSharedPostsByUserId(String userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        logger.error("lấy thành công cho user có id: {}", userId);
        return sharesPostsRepository.findByUserId(user);
    }
}