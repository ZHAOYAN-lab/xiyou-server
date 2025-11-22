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
public class BeaconExportJPDTO implements IBeaconExportDTO {
    @Alias("タグMACアドレス")
    private String mac;
    @Alias("タグタイプ（1人、2物品、3設備、4車両、5不明）")
    private String type;
    @Alias("タグモデル")
    private String product;
    @Alias("コメント")
    private String remark;
}
