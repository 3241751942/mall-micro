package com.zzl.commonapi.dto.userservicedto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 批量用户ID请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUserIdsRequest {
    private List<Long> userIds;
}