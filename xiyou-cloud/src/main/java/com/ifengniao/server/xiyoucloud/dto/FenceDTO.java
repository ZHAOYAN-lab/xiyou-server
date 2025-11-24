package com.ifengniao.server.xiyoucloud.dto;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.FenceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class FenceDTO {
    private Integer fenceId;
    private String fenceName;
    private Short fenceType;
    private Boolean fenceStatus;
    private List<Pxy> fenceContent;
    private MapDTO fenceMap;
    
    // ==========================================================
    // 新增字段用于存储商品区域信息 (对应需求文档 1.1.3.2)
    // ==========================================================
    private String objectName;  // 对象名称 (来自前端的文本输入)
    private String belongType;  // 所属类型 (货物种类1/2等)
    private String iconUrl;     // 上传图标的URL或存储路径 (用于移动端显示)


    public static List<FenceDTO> covert(Collection<FenceEntity> list) {
        List<FenceDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new FenceDTO(source));
            }
        }
        return result;
    }

    public FenceDTO(FenceEntity fenceEntity) {
        this.fenceId = fenceEntity.getFenceId();
        this.fenceName = fenceEntity.getFenceName();
        this.fenceType = fenceEntity.getFenceType();
        this.fenceStatus = fenceEntity.getFenceStatus();
        this.fenceContent = Pxy.covert(fenceEntity.getFenceContent().getCoordinates());
        this.fenceMap = new MapDTO(fenceEntity.getFenceMap());
        
        // 映射逻辑已在 FenceService 中处理 (DTO -> Entity)
        // 从 Entity 到 DTO 的映射（如果您需要从数据库读取时填充这些字段）
        // this.objectName = fenceEntity.getObjectName(); 
        // this.belongType = fenceEntity.getBelongType(); 
        // this.iconUrl = fenceEntity.getIconUrl();
    }

    public boolean fenceContainPoint(double x, double y) throws Exception {
        return this.covertXyListToPolygon().contains(new GeometryFactory().createPoint(new Coordinate(x, y)));
    }

    public Polygon covertXyListToPolygon() throws Exception {
        if (getFenceContent().size() < 4) {
            throw new BaseException("围栏需要是一个封闭图形").setStrCode(Constants.ERR_1504);
        }
        Coordinate[] coordinates = Pxy.covertToCoordinateArray(getFenceContent());
//        //必须首尾坐标点相同组成闭合多边形
        if (!(coordinates[0].equals2D(coordinates[coordinates.length - 1]))) {
            throw new BaseException("围栏需要是一个封闭图形").setStrCode(Constants.ERR_1504);
        }
        return new GeometryFactory().createPolygon(coordinates);
    }

    public List<Pxy> getFenceContent() {
        if (fenceContent == null) {
            fenceContent = new ArrayList<>();
        }
        return fenceContent;
    }

}