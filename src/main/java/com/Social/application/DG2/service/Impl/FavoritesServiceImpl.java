package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.dto.FavoritesDto;
import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.repositories.PostsRepository;
import com.Social.application.DG2.repositories.UsersRepository;
import com.Social.application.DG2.service.FavoritesService;
import com.Social.application.DG2.util.exception.ConflictException;
import com.Social.application.DG2.util.exception.NotFoundException;
import com.Social.application.DG2.util.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FavoritesServiceImpl implements FavoritesService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PostsRepository postsRepository;
    @Override
    public void saveFavorite(String postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Bạn cần đăng nhập để thực hiện hành động này!");
        }
        String currentUsername = auth.getName();
        Users user = usersRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        Optional<Posts> optionalPost = postsRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bài đăng có ID: " + postId);
        }

        Posts post = optionalPost.get();

        boolean isPostFavorited = usersRepository.isPostFavoritedByUser(user.getId(), postId);
        if (isPostFavorited) {
            throw new ConflictException("Bài đăng đã được yêu thích trước đó!");
        }

        user.getFavoritesPost().add(post);

        usersRepository.save(user);
    }

    @Override
    public Page<FavoritesDto> getFavoritesByToken(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();

        Page<Posts> favoritePostsPage = postsRepository.findFavoritesByUserId(currentUserId, pageable);

        return favoritePostsPage.map(this::convertToDto);
    }

    private FavoritesDto convertToDto(Posts post) {
        FavoritesDto favoritesDto = new FavoritesDto();
        favoritesDto.setPostsID(post.getId());
        favoritesDto.setTitle(post.getTitle());
        favoritesDto.setBody(post.getBody());
        favoritesDto.setStatus(post.getStatus());
        if (post.getMedias() != null && !post.getMedias().isEmpty()) {
            favoritesDto.setMediasId(Arrays.asList(post.getMedias().get(0).getId()));
        }
        favoritesDto.setTotalLike(String.valueOf(post.getTotalLike()));
        favoritesDto.setTotalComment(String.valueOf(post.getTotalComment()));
        favoritesDto.setUserID(post.getUserId().getId());
        favoritesDto.setCreateAt(post.getCreateAt());
        return favoritesDto;
    }

    @Override
    public void deleteFavorite(String posts) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("Bạn phải đăng nhập mới được thực hiện các hành động tiếp theo!");
        }

        String currentUsername = authentication.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();

        if (currentUserId == null) {
            throw new NotFoundException("Không tìm thấy tài khoản người dùng.");
        }

        int count = postsRepository.countFavoritesByUserIdAndPostId(currentUserId, posts);
        if (count == 0) {
            throw new NotFoundException("Không tìm thấy mục yêu thích!");
        }

        postsRepository.deleteFavoriteByUserIdAndPostId(currentUserId, posts);
    }
}
