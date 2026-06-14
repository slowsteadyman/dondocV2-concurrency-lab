package com.dondoc.service;

import com.dondoc.dto.auth.LoginRequest;
import com.dondoc.dto.auth.LoginResponse;
import com.dondoc.dto.auth.SignUpRequest;
import com.dondoc.entity.User;
import com.dondoc.exception.ApiException;
import com.dondoc.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId()).orElse(null);

        if(user == null || !user.getUserPassword().equals(request.getUserPassword())) {
            throw new ApiException(HttpStatus.CONFLICT, "아이디나 비밀번호가 일치하지 않습니다.");
        } else  {
            userRepository.updateLastLoginAt(user.getId());
            return new LoginResponse(
                    user.getId(),
                    user.getName(),
                    user.getAge(),
                    user.getMonthlyIncome(),
                    user.getTargetExpenseRatio(),
                    user.getCurrentPigLevel(),
                    user.getCurrentHouseLevel(),
                    user.getCurrentCharacterLevel()
            );
        }
    }

    @Transactional
    public Long createUser(SignUpRequest request){
        if(userRepository.findByUserId(request.getUserId()).isEmpty()) {
            return userRepository.save(request);
        } else {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }
    }
}
