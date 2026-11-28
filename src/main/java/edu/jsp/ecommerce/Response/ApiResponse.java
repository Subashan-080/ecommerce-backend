
package edu.jsp.ecommerce.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private T data;
    private String message;
    private boolean sucess;

    public static <T> ApiResponse<T> sucess(T data, String message) {
        return ApiResponse.<T>builder()
                .data(data)
                .sucess(true)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .data(null)
                .sucess(false)
                .message(message)
                .build();
    }
}
