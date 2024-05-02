package com.Social.application.DG2.mapper;

import com.Social.application.DG2.config.MapperConfig;
import com.Social.application.DG2.dto.FavoritesDto;
import com.Social.application.DG2.entity.Favorites;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FavoritesMapper {
    @Autowired
    private ModelMapper modelMapper;
    public FavoritesDto maptoEntity(Favorites favorites) {
        return modelMapper.map(favorites, FavoritesDto.class);
    }

    public Favorites maptoDto(FavoritesDto favoritesDto) {
        return modelMapper.map(favoritesDto, Favorites.class);
    }
}
