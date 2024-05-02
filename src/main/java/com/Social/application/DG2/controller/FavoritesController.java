package com.Social.application.DG2.controller;

import com.Social.application.DG2.dto.FavoritesDto;
import com.Social.application.DG2.service.FavoritesService;
import com.Social.application.DG2.util.annotation.CheckLogin;
import com.Social.application.DG2.util.exception.ConflictException;
import com.Social.application.DG2.util.exception.NotFoundException;
import com.Social.application.DG2.util.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    @Autowired
    private FavoritesService favoritesService;
    @CheckLogin
    @PostMapping("/{postId}")
    public ResponseEntity<String> saveFavorite(@RequestParam String postId) {
        try {
            favoritesService.saveFavorite(postId);
            return ResponseEntity.ok("Lưu bài mình thích thành công!");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @CheckLogin
    @GetMapping("/get")
    public ResponseEntity<Page<FavoritesDto>> getFavoritesByToken(
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int pageSize,
                                                            @RequestParam(defaultValue = "createAt") String sortName,
                                                            @RequestParam(defaultValue = "DESC") String sortType) {

        // Tạo một biến Sort.Direction để lưu hướng sắp xếp
        Sort.Direction direction;

        // Kiểm tra giá trị của sortType
        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
        Page<FavoritesDto> favoritesPage = favoritesService.getFavoritesByToken(pageable);
        return ResponseEntity.ok(favoritesPage);
    }

    @CheckLogin
    @DeleteMapping("/delete/{favoritesID}")
    public ResponseEntity<String> deleteFavorite(@RequestParam String posts) {
        try {
            favoritesService.deleteFavorite(posts);
            return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }

    }
}