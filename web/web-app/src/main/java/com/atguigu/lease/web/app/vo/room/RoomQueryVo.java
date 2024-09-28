package com.atguigu.lease.web.app.vo.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "房间查询实体")
public class RoomQueryVo {


    @Schema(description = "省份Id")
    private Long provinceId;

    @Schema(description = "城市Id")
    private Long cityId;

    @Schema(description = "区域Id")
    private Long districtId;

    @Schema(description = "最小租金")
    private BigDecimal minRent;

    @Schema(description = "最大租金")
    private BigDecimal maxRent;

    @Schema(description = "支付方式")
    private Long paymentTypeId;

    @Schema(description = "价格排序方式", allowableValues = {"desc", "asc"}) //允许的值，只允许前端传入指定值，体现在接口文档中。
    private String orderType;

}
