package com.dondoc.service;

import com.dondoc.dto.Farms.FarmDetailMemberResponse;
import com.dondoc.dto.Farms.FarmDetailResponse;
import com.dondoc.entity.Farm;
import com.dondoc.exception.ApiException;
import com.dondoc.repository.FarmMemberRepository;
import com.dondoc.repository.FarmRepository;
import com.dondoc.repository.UserRepository;
import com.dondoc.repository.projection.FarmMemberDetail;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FarmDetailService {

    private final FarmRepository farmRepository;
    private final FarmMemberRepository farmMemberRepository;
    private final UserRepository userRepository;

    public FarmDetailService(
            FarmRepository farmRepository,
            FarmMemberRepository farmMemberRepository,
            UserRepository userRepository
    ) {
        this.farmRepository = farmRepository;
        this.farmMemberRepository = farmMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public FarmDetailResponse getFarmDetail(Long userId, Long farmId) {
        if (userId == null) throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 토큰 없음");
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 농장"));

        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "인증 토큰 없음"));

        boolean joined = farmMemberRepository.existsByFarmIdAndUserId(farmId, userId);

        List<FarmMemberDetail> memberDetails = farmMemberRepository.findMemberDetailsByFarmId(farmId);
        List<FarmDetailMemberResponse> members = memberDetails.stream()
                .map(member -> new FarmDetailMemberResponse(
                        member.getUserId(),
                        member.getName(),
                        member.getCurrentPigLevel(),
                        member.getCurrentHouseLevel(),
                        member.getJoinedAt()
                ))
                .toList();

        return new FarmDetailResponse(
                farm.getId(),
                farm.getName(),
                members.size(),
                joined,
                members
        );
    }

}
