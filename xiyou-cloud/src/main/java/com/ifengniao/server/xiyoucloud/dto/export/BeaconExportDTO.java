package com.ifengniao.server.xiyoucloud.dto.export;

import cn.hutool.core.annotation.Alias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BeaconExportDTO implements IBeaconExportDTO {
    @Alias("信标MAC地址")
    private String mac;
    @Alias("信标类型（1人员、2物品、3设备、4车辆、5未知）")
    private String type;
    @Alias("信标型号")
    private String product;
    @Alias("备注")
    private String remark;
}
