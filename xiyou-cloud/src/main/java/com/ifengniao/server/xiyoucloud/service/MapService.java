package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.MapEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.BuildingEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.LocalServerEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.MapEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MapService {

    @Autowired
    private BuildingEntityRepository buildingEntityRepository;
    @Autowired
    private LocalServerEntityRepository localServerEntityRepository;
    @Autowired
    private MapEntityRepository mapEntityRepository;

    public List<MapEntity> findAll() {
        return mapEntityRepository.findAll();
    }

    public MapEntity findById(Integer id) {
        if (id == null) {
            return null;
        }
        return mapEntityRepository.findById(id).orElse(null);
    }

    public MapEntity findByCpaIdAndLocalServerMac(String cpaId, String localServerMac) {
        return mapEntityRepository.findByMapCpaIdAndMapLocalServer_LocalServerMac(cpaId, localServerMac);
    }

    public MapEntity save(MapEntity map) {
        return mapEntityRepository.save(map);
    }

    public void delete(MapEntity map) {
        mapEntityRepository.delete(map);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findMapTree() {
        return buildingEntityRepository.findAll().stream().map(building -> Map.of(
                "title", building.getBuildingName(),
                "children", localServerEntityRepository.findAllByLocalServerBuilding(building).stream().map(ls -> Map.of(
                        "title", ls.getLocalServerCpaProject(),
                        "children", ls.getMapList().stream().map(map -> Map.of(
                                "mapId", map.getMapId(),
                                "title", map.getMapCpaName()
                        )).collect(Collectors.toList())
                )).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }

}
