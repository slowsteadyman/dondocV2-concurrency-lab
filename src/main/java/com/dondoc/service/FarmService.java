package com.dondoc.service;

import com.dondoc.dto.ApiResponse;
import com.dondoc.dto.FarmMembers.FarmJoinResponse;
import com.dondoc.dto.Farms;
import com.dondoc.dto.Farms.FarmDetailMemberResponse;
import com.dondoc.dto.Farms.FarmDetailResponse;
import com.dondoc.entity.Farm;
import com.dondoc.entity.FarmMember;
import com.dondoc.exception.ApiException;
import com.dondoc.repository.FarmMemberRepository;
import com.dondoc.repository.FarmRepository;
import com.dondoc.repository.UserRepository;
import com.dondoc.repository.projection.FarmMemberDetail;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FarmService {

    private final FarmRepository farmRepository;
    private final FarmMemberRepository farmMemberRepository;
    private final UserRepository userRepository;

    public FarmService(
            FarmRepository farmRepository,
            FarmMemberRepository farmMemberRepository,
            UserRepository userRepository) {
        this.farmRepository = farmRepository;
        this.farmMemberRepository = farmMemberRepository;
        this.userRepository = userRepository;
    }

    // ── 농장 조회 ─────────────────────────────────────────────────────────────

    public List<Farms.FarmGetResponse> getFarmList(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자"));

        List<Farm> farms = farmRepository.findAll();
        Map<Long, Integer> memberCountMap = farmMemberRepository.findMemberCount();
        List<Long> joinedFarms = farmMemberRepository.findJoinedFarmIdsByUserId(userId);

        return farms.stream().map(farm -> new Farms.FarmGetResponse(
                farm.getId(),
                farm.getName(),
                memberCountMap.getOrDefault(farm.getId(), 0),
                joinedFarms.contains(farm.getId()),
                farm.getCreatedAt())
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FarmDetailResponse getFarmDetail(Long userId, Long farmId) {
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 토큰 없음");
        }

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
                        member.getJoinedAt()))
                .toList();

        return new FarmDetailResponse(
                farm.getId(),
                farm.getName(),
                members.size(),
                joined,
                members);
    }

    // ── 농장 생성 ─────────────────────────────────────────────────────────────

    @Transactional
    public Farms.CreateResponse createFarm(Long userId, Farms.CreateRequest request) {
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 토큰 없음");
        }

        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "농장 이름은 필수입니다");
        }

        String farmName = request.getName().trim();

        if (farmRepository.existsByName(farmName)) {
            throw new ApiException(HttpStatus.CONFLICT, "중복된 농장 이름");
        }

        LocalDateTime createdAt = LocalDateTime.now();
        Farm farm = new Farm(null, farmName, createdAt);
        Farm savedFarm = farmRepository.save(farm);
        Long farmId = savedFarm.getId();

        FarmMember farmMember = new FarmMember(null, userId, farmId, createdAt);
        farmMemberRepository.save(farmMember);

        return new Farms.CreateResponse(farmId, farmName, true, createdAt);
    }

    // ── 농장 가입 / 탈퇴 ──────────────────────────────────────────────────────

    public FarmJoinResponse addFarmMember(long userId, long farmId) {
        farmRepository.findById(farmId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 농장입니다."));

        farmMemberRepository.findByUserIdAndFarmId(userId, farmId)
                .ifPresent(m -> {
                    throw new ApiException(HttpStatus.CONFLICT, "이미 가입한 농장입니다.");
                });

        FarmMember farmMember = new FarmMember(null, userId, farmId, LocalDateTime.now());
        farmMemberRepository.save(farmMember);

        return new FarmJoinResponse(userId, farmId, farmMember.getJoinedAt());
    }

    @Transactional
    public ApiResponse<Farms.LeaveResponse> leaveFarm(Long farmId, Long userId) {
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }

        int deletedCount = farmMemberRepository.deleteByFarmIdAndUserId(farmId, userId);
        if (deletedCount == 0) {
            throw new ApiException(HttpStatus.NOT_FOUND, "농장 멤버를 찾을 수 없습니다.");
        }

        int remainCount = farmMemberRepository.countByFarmId(farmId);
        if (remainCount == 0) {
            farmRepository.deleteById(farmId);
        }

        Farms.LeaveResponse data = new Farms.LeaveResponse(farmId, userId);
        return new ApiResponse<>(true, data, null);
    }

    // ── 내부 / 레거시 ─────────────────────────────────────────────────────────

    public List<Farms.Farm> getFarms() {
        return farmRepository.findAll().stream()
                .map(entity -> new Farms.Farm(
                        entity.getId(),
                        entity.getName(),
                        entity.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<Farms.Member> getFarmMembers() {
        return farmMemberRepository.findAll().stream()
                .map(entity -> new Farms.Member(
                        entity.getId(),
                        entity.getUserId(),
                        entity.getFarmId(),
                        entity.getJoinedAt()))
                .collect(Collectors.toList());
    }

    public void createFarm(Farms.Farm dto) {
        Farm farm = new Farm(null, dto.getName(), dto.getCreatedAt());
        farmRepository.save(farm);
    }

    public void createFarmMember(Farms.Member dto) {
        FarmMember farmMember = new FarmMember(
                null, dto.getUserId(), dto.getFarmId(), dto.getJoinedAt());
        farmMemberRepository.save(farmMember);
    }
}
