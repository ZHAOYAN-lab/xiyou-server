package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.FenceEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.MapEntity;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface FenceEntityRepository extends JpaRepositoryImplementation<FenceEntity, Integer> {

    List<FenceEntity> findAllByFenceIdInAndFenceType(Collection<Integer> fenceId, Short fenceType);

    List<FenceEntity> findAllByFenceMapAndFenceStatusIsTrue(MapEntity fenceMap);

    //    select * from xy_fence where point(2, 5.47) <@ polygon(fence_content);
//    select * from xy_fence where ST_Contains(fence_content, ST_GeomFromText('POINT(2 5.47)', 0))
    @Query(value = "select * from xy_fence where fence_map = :mapId and ST_Contains(fence_content, :point)", nativeQuery = true)
    List<FenceEntity> findAllByMapIdAndContainsPoint(@Param(value = "mapId") int mapId, @Param(value = "point") Point point);


}