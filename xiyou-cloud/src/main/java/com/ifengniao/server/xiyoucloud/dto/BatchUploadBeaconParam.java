package com.ifengniao.server.xiyoucloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BatchUploadBeaconParam {
    private int langType;
    private String saveName;
}
