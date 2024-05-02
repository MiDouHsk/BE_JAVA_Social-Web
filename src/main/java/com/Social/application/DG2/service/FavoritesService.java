package com.Social.application.DG2.service;

import com.Social.application.DG2.dto.FavoritesDto;
import com.Social.application.DG2.entity.Favorites;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FavoritesService {
    void saveFavorite(String posts);
    void deleteFavorite(UUID favorites);
    Page<FavoritesDto> getFavoritesByToken(Pageable pageable);

}
