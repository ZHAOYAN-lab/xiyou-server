package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.LocationObjectEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;
import java.util.Map;

public interface LocationObjectEntityRepository extends JpaRepositoryImplementation<LocationObjectEntity, Integer> {

    LocationObjectEntity findByLocationObjectName(String locationObjectName);

    List<LocationObjectEntity> findAllByLocationObjectTypeAndLocationObjectBeaconIsNullOrderByLocationObjectNameAsc(Short locationObjectType);

    List<LocationObjectEntity> findAllByLocationObjectBeaconIsNotNullOrderByLocationObjectNameAsc();

    @Query(value = """
             select location_object_type as type, beacon_online as online, count(*) as count
             from xy_location_object
             inner join xy_beacon
             on location_object_beacon = beacon_id
             group by type, online
            """, nativeQuery = true)
    List<Map<String, Object>> countGroupByTypeAndOnline();

}