package com.dondoc.service;

import com.dondoc.dto.*;
import com.dondoc.entity.User;
import com.dondoc.repository.RecordRepository;
import com.dondoc.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;

    public UserService(UserRepository userRepository, RecordRepository recordRepository){
        this.userRepository = userRepository;
        this.recordRepository = recordRepository;
    }

    public List<Users> getUsers(){
        List<User> entities = userRepository.findAll();
        return entities.stream()
                .map(entity -> new Users(
                        entity.getId(),
                        entity.getUserId(),
                        entity.getUserPassword(),
                        entity.getName(),
                        entity.getAge(),
                        entity.getCurrentPigLevel(),
                        entity.getCurrentHouseLevel(),
                        entity.getMonthlyIncome(),
                        entity.getTargetExpenseRatio(),
                        entity.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public void createUser(Users dto){
        User user = new User(
                null,
                dto.getUserId(),
                dto.getUserPassword(),
                dto.getName(),
                dto.getAge(),
                dto.getCurrentPigLevel(),
                dto.getCurrentHouseLevel(),
                3,
                dto.getMonthlyIncome(),
                dto.getTargetExpenseRatio(),
                dto.getCreatedAt()
        );
        userRepository.save(user);
    }

    // monthlyBudget = 월수입 × 목표지출비율 / 100  -> 이번 달에 쓸 수 있는 총 예산
    // 예) 200만원 × 80% = 160만원

    // dailyBudget = 월예산 / 이번달 일수  -> 하루에 쓸 수 있는 예산
    // LocalDate.now().lengthOfMonth() → 이번달이 며칠인지 자동으로 계산해줌
    public ApiResponse<UserMeResponse> getUserMe(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        long monthlyBudget = user.getMonthlyIncome() * user.getTargetExpenseRatio() / 100;
        long dailyBudget = monthlyBudget / LocalDate.now().lengthOfMonth();

        UserMeResponse data = new UserMeResponse(
                user.getName(),
                user.getAge(),
                user.getCurrentPigLevel(),
                user.getCurrentHouseLevel(),
                user.getCurrentCharacterLevel(),
                user.getMonthlyIncome(),
                user.getTargetExpenseRatio(),
                monthlyBudget,
                dailyBudget
        );

        return new ApiResponse<>(true, data, "내 정보 조회 성공");
    }

    public ApiResponse<UserPatchResponse> updateUserMe(Long userId, UserPatchRequest request){
        userRepository.update(userId, request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        long monthlyBudget = user.getMonthlyIncome() * user.getTargetExpenseRatio() / 100;
        long dailyBudget = monthlyBudget / LocalDate.now().lengthOfMonth();

        UserPatchResponse data = new UserPatchResponse(
                user.getId(),
                user.getName(),
                user.getAge(),
                user.getMonthlyIncome(),
                user.getTargetExpenseRatio(),
                monthlyBudget,
                dailyBudget
        );

        return new ApiResponse<>(true, data, "프로필 설정이 완료되었습니다.");

    }



}
