package com.competency.SCMS.service;

import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.exception.UserNotFoundException;
import com.competency.SCMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

//@Service
//@Bean
//@RequiredArgsConstructor
//public class UserService {
//    private final UserRepository userRepository;
//
//    // 전체 학생 수 조회
//    public long getStudentCount() {
//        return userRepository.count();
//    }
//
//    // 학생 목록 페이지네이션/검색/필터
//    public Page<User> getUserList(String search, Boolean locked, Pageable pageable) {
//        if (search != null && !search.isEmpty()) {
//            if (locked != null) {
//                return userRepository.findByNameContainsOrStudentNumContainsAndLocked(
//                        search, search, locked, pageable);
//            }
//            return userRepository.findByNameContainsOrStudentNumContains(
//                    search, search, pageable);
//        } else if (locked != null) {
//            return userRepository.findByLocked(locked, pageable);
//        }
//        return userRepository.findAll(pageable);
//    }
//
//    // 사용자 상세 조회
//    public User getUserById(Integer userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
//    }
//
//    // 사용자 정보 수정/저장
//    public User updateUser(Integer userId, User updateData) {
//        User user = getUserById(userId);
//        user.setName(updateData.getName());
//        user.setEmail(updateData.getEmail());
//        user.setStudentNum(updateData.getStudentNum());
//        user.setLocked(updateData.getLocked());
//        // 더 필요한 필드 추가
//        return userRepository.save(user);
//    }
//
//    // 계정 활성화/비활성화 처리
//    public void updateUserStatus(Integer userId, Boolean locked) {
//        User user = getUserById(userId);
//        user.setLocked(locked);
//        userRepository.save(user);
//    }
//}

