package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.dto.UsersInfoDto;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.repositories.UsersRepository;
import com.Social.application.DG2.service.FollowService;
import com.Social.application.DG2.util.exception.InvalidRequestException;
import com.Social.application.DG2.util.exception.MethodNotAllowedException;
import com.Social.application.DG2.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private UsersRepository usersRepository;
    @Override
    public void followUser(String followingUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Users users = usersRepository.findByUsername(currentUsername);
        Users followingUser = usersRepository.findById(followingUserId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng đang muốn theo dõi."));

        if (users == null || followingUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng hoặc người dùng đang muốn theo dõi.");
        }

        if (users.getId().equals(followingUser.getId())) {
            throw new MethodNotAllowedException("Bạn không thể theo dõi chính mình.");
        }

        List<Users> following = users.getFollowingUser();

        // Kiểm tra xem users đã theo dõi followingUser chưa
        boolean isAlreadyFollowing = false;
        for (Users user : following) {
            if (user.getId().equals(followingUserId)) {
                isAlreadyFollowing = true;
                break;
            }
        }

        if (!isAlreadyFollowing) {
            following.add(followingUser);
            users.setFollowingUser(following);
            usersRepository.save(users);
        } else {
            throw new InvalidRequestException("Bạn đã theo dõi người dùng này trước đó rồi.");
        }
    }

    @Override
    public int getFollowingCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Users users = usersRepository.findByUsername(currentUsername);
        return users.getFollowingUser().size();
    }

    @Override
    public int getFollowerCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        int followerCount = usersRepository.countByFollowingUsersUsername(currentUsername);
        return followerCount;
    }

    @Override
    public void unfollowUser(String followingUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Users currentUser = usersRepository.findByUsername(currentUsername);
        Users followingUser = usersRepository.findById(followingUserId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng đang muốn hủy theo dõi."));

        List<Users> following = currentUser.getFollowingUser();
        if (!following.contains(followingUser)) {
            throw new NotFoundException("Bạn đã hủy theo dõi người này trước đó.");
        }

        following.remove(followingUser);
        usersRepository.save(currentUser);
    }

    @Override
    public Page<UsersInfoDto> getFollowingListUsers(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();
        Page<Users> users = usersRepository.findFollowingUsersByUserId(currentUserId, pageable);
        return users.map(this::usersInfoDto);
    }

    @Override
    public Page<UsersInfoDto> getFollowerListUsers(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();
        Page<Users> users = usersRepository.findFollowerUsersByUserId(currentUserId, pageable);
        return users.map(this::usersInfoDto);
    }

    @Override
    public Page<UsersInfoDto> getNotFollowingListUsers(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();
        Page<Users> users = usersRepository.findUnfollowerUsersByUserId(currentUserId, pageable);

        List<Users> filteredUsers = users.getContent().stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .collect(Collectors.toList());

        Page<Users> filteredPage = new PageImpl<>(filteredUsers, pageable, users.getTotalElements());

        return filteredPage.map(this::usersInfoDto);
    }

    @Override
    public int countFollowingUsersById(String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        return user.getFollowingUser().size();
    }


    @Override
    public int countFollowerUsersById(String userId) {
        int followerCount = usersRepository.countByFollowingUsersId(userId);
        return followerCount;
    }

    @Override
    public Page<UsersInfoDto> getFollowingListUsersById(String userId, Pageable pageable) {
        Page<Users> followingUsersPage = usersRepository.findFollowingUsersByUserId(userId, pageable);
        return followingUsersPage.map(this::usersInfoDto);
    }

    @Override
    public Page<UsersInfoDto> getFollowerListUsersById(String userId, Pageable pageable) {
        Page<Users> followerUsersPage = usersRepository.findFollowerUsersByUserId(userId, pageable);
        return followerUsersPage.map(this::usersInfoDto);
    }


    private UsersInfoDto usersInfoDto(Users users) {
        UsersInfoDto dto = new UsersInfoDto();
        dto.setId(users.getId());
        dto.setUsername(users.getUsername());
        dto.setFirstName(users.getFirstName());
        dto.setLastName(users.getLastName());
        dto.setAvatar(users.getAvatar());
        dto.setGender(users.isGender());
        dto.setPhoneNumber(users.getPhoneNumber());
        dto.setDateOfBirth(users.getDateOfBirth());
        dto.setAddress(users.getAddress());
        dto.setMail(users.getMail());
        return dto;
    }

}
