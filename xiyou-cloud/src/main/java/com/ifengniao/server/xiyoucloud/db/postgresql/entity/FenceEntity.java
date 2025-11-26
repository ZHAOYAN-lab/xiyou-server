package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

@Entity
@Table(name = "xy_fence", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class FenceEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "fence_id")
    private Integer fenceId;
    @Basic
    @Column(name = "fence_name")
    private String fenceName;
    @Basic
    @Column(name = "fence_type")
    private Short fenceType;
    @Basic
    @Column(name = "fence_status")
    private Boolean fenceStatus;
    @Basic
    @Column(name = "fence_content")
    private Polygon fenceContent;
    @ManyToOne
    @JoinColumn(name = "fence_map", referencedColumnName = "map_id", nullable = false)
    private MapEntity fenceMap;

    public boolean fenceContainPoint(double x, double y) {
        return this.getFenceContent().contains(new GeometryFactory().createPoint(new Coordinate(x, y)));
    }
}
