package com.sparta.goatgam.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageAndIdResponseDto {
    private String message;
    private UUID id;
}
