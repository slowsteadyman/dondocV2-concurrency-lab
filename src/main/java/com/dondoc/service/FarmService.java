package com.dondoc.service;

import com.dondoc.dto.FarmMembers;
import com.dondoc.dto.Farms;
import com.dondoc.entity.Farm;
import com.dondoc.entity.FarmMember;
import com.dondoc.exception.ApiException;
import com.dondoc.repository.FarmMemberRepository;
import com.dondoc.repository.FarmRepository;
import com.dondoc.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FarmService {
    private final FarmRepository farmRepository;
    private final FarmMemberRepository farmMemberRepository;
    // 정합성 검사를 위한 userRepository 주입이 적절한지 의문
    private final UserRepository userRepository;

    public FarmService(FarmRepository farmRepository, FarmMemberRepository farmMemberRepository, UserRepository userRepository){
        this.farmRepository = farmRepository;
        this.farmMemberRepository = farmMemberRepository;
        this.userRepository = userRepository;
    }

    public List<Farms> getFarms(){
        List<Farm> entities = farmRepository.findAll();
        return entities.stream()
                .map(entity -> new Farms(
                        entity.getId(),
                        entity.getName(),
                        entity.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<FarmMembers> getFarmMembers(){
        List<FarmMember> entities = farmMemberRepository.findAll();
        return entities.stream()
                .map(entity -> new FarmMembers(
                        entity.getId(),
                        entity.getUserId(),
                        entity.getFarmId(),
                        entity.getJoinedAt()
                )).collect(Collectors.toList());
    }

    public void createFarm(Farms dto){
        Farm farm = new Farm(
                null, dto.getName(), dto.getCreatedAt()
        );
        farmRepository.save(farm);
    }

    public void createFarmMember(FarmMembers dto){
        FarmMember farmMember = new FarmMember(
                null, dto.getUserId(), dto.getFarmId(), dto.getJoinedAt()
        );
        farmMemberRepository.save(farmMember);

    }

    // 단순 조회(SELECT) 쿼리라 @Transactional 어노테이션 X
    public List<Farms.FarmGetResponse> getFarmList(Long userId) {
        // 정합성 검사
        userRepository.findById(userId).orElseThrow(()-> new ApiException(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자"));

        // 모든 농장 목록 가져오기
        List<Farm> farms = farmRepository.findAll();
        // <농장 id, 농장 회원 수> map 가져오기
        Map<Long, Integer> memberCountMap = farmMemberRepository.findMemberCount();
        // userId 사용자가 참여 중인 농장 id 리스트 가져오기
        List<Long> joinedFarms = farmMemberRepository.findJoinedFarmIdsByUserId(userId);

        return farms.stream().map(farm -> new Farms.FarmGetResponse(
                farm.getId(),
                farm.getName(),
                memberCountMap.getOrDefault(farm.getId(), 0),
                joinedFarms.contains(farm.getId()),
                farm.getCreatedAt())
        ).collect(Collectors.toList());
    }
}
