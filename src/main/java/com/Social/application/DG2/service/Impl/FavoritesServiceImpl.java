package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.dto.FavoritesDto;
import com.Social.application.DG2.entity.Favorites;
import com.Social.application.DG2.entity.Posts;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.repositories.FavoritesRepository;
import com.Social.application.DG2.repositories.PostsRepository;
import com.Social.application.DG2.repositories.UsersRepository;
import com.Social.application.DG2.service.FavoritesService;
import com.Social.application.DG2.util.exception.ConflictException;
import com.Social.application.DG2.util.exception.NotFoundException;
import com.Social.application.DG2.util.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
public class FavoritesServiceImpl implements FavoritesService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private FavoritesRepository favoritesRepository;
    @Autowired
    private PostsRepository postsRepository;
    @Override
    public void saveFavorite(String posts) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Bạn cần đăng nhập để thực hiện hành động này!");
        }
        String currentUsername = auth.getName();
        Users users = usersRepository.findByUsername(currentUsername);
        if (users == null ) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

//        Optional<Favorites> optionalFavorite = favoritesRepository.findById(posts);
//        if (optionalFavorite.isEmpty()){
//            throw new NotFoundException("không tìm thấy bài cần lưu vào mục yêu thích!");
//        }

        Favorites favorites = new Favorites();
        Posts post = postsRepository.findById(posts).orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết!"));

        Optional<Favorites> optionalFavorite2 = favoritesRepository.findByUserIDAndPostsID(users, post);
        if (optionalFavorite2.isPresent()) {
            throw new ConflictException("Bài viết đã tồn tại trong mục yêu thích!");
        }

        favorites.setId(post.getId());
        favorites.setPostsID(post);
        favorites.setUserID(users);
        favorites.setCreateAt(new Timestamp(System.currentTimeMillis()));

        favoritesRepository.save(favorites);

    }

    @Override
    public Page<FavoritesDto> getFavoritesByToken(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();
        Page<Favorites> favorites = favoritesRepository.findAll(pageable);
        return favorites.map(this::favoritesDto);
    }

    private FavoritesDto favoritesDto(Favorites favorites) {
        FavoritesDto dto = new FavoritesDto();
        dto.setPostsID(favorites.getPostsID());
        dto.setUserID(favorites.getUserID());
        dto.setCreateAt(favorites.getCreateAt());
        return dto;
    }

    @Override
    @Transactional
    public void deleteFavorite(UUID favorites) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null ) {
            throw new NotFoundException("Bạn cần đăng nhập để thực hiện hành động này!");
        }

        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        Optional<Favorites> optionalFavorite = favoritesRepository.findById(favorites.toString());
        if (optionalFavorite.isEmpty()){
            throw new NotFoundException("không tìm thấy bài viết cần xóa !");
        }

        favoritesRepository.deleteByPostId(favorites.toString());
    }
}
