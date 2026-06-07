// 매번 API마다 이 구조를 새로 만들면 중복이 생겨셔, <T>로 제네릭하게 만들어서 data 부분만 API마다
// 다르게 넣는다.

package com.dondoc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse <T>{
    private boolean success;
    private T data;
    private String message;

}
