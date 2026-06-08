package com.dondoc.service;

import com.dondoc.dto.CreateFarmRequest;
import com.dondoc.dto.CreateFarmResponse;
import com.dondoc.dto.FarmMembers;
import com.dondoc.dto.Farms;
import com.dondoc.entity.Farm;
import com.dondoc.entity.FarmMember;
import com.dondoc.repository.FarmMemberRepository;
import com.dondoc.repository.FarmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmService {
    private final FarmRepository farmRepository;
    private final FarmMemberRepository farmMemberRepository;

    public FarmService(FarmRepository farmRepository, FarmMemberRepository farmMemberRepository){
        this.farmRepository = farmRepository;
        this.farmMemberRepository = farmMemberRepository;
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

    @Transactional
    public CreateFarmResponse createFarm(Long userId, CreateFarmRequest request) {
        if (userId == null) {
            throw new SecurityException("인증 토큰 없음");
        }

        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("농장 이름은 필수입니다");
        }

        String farmName = request.getName().trim();

        if (farmRepository.existsByName(farmName)) {
            throw new IllegalStateException("중복된 농장 이름");
        }

        LocalDateTime createdAt = LocalDateTime.now();
        Farm farm = new Farm(null, farmName, createdAt);
        Long farmId = farmRepository.saveAndReturnId(farm);

        FarmMember farmMember = new FarmMember(null, userId, farmId, createdAt);
        farmMemberRepository.save(farmMember);

        return new CreateFarmResponse(farmId, farmName, true, createdAt);
    }
}
