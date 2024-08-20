package com.rodrigo.drawing_contest.dtos.websockets.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class ResultResponseDto {
    private List<UserRatesDto> rates = new ArrayList<UserRatesDto>();
}
