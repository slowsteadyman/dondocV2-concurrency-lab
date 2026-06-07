package com.dondoc.service;

import com.dondoc.dto.FarmMembers;
import com.dondoc.dto.FarmMembers.FarmJoinResponse;
import com.dondoc.dto.Farms;
import com.dondoc.entity.Farm;
import com.dondoc.entity.FarmMember;
import com.dondoc.repository.FarmMemberRepository;
import com.dondoc.repository.FarmRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

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

    public FarmJoinResponse addFarmMember(long userId, long farmId){
        FarmMember farmMember = new FarmMember(
                null, userId, farmId, LocalDateTime.now()
        );
        farmMemberRepository.save(farmMember);

        return new FarmJoinResponse(userId, farmId, farmMember.getJoinedAt());
    }
}
