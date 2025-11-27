package com.sky.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderPaymentOtherVO implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd HH:ss:mm")
    private LocalDateTime estimatedDeliveryTime;
}
