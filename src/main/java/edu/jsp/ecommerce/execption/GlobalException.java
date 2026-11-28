package edu.jsp.ecommerce.execption;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import edu.jsp.ecommerce.Response.ApiResponse;

@RestControllerAdvice
public class GlobalException { 
	@ExceptionHandler(ResoruceNotFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleRsoruceNotFoundException(ResoruceNotFoundException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
	}
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse<String>> handleBadRequestException(BadRequestException ex){
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String , String>>> handleMethodArugmentNotValidException(MethodArgumentNotValidException ex){
		HashMap<String , String>map=new HashMap<String, String>();
		List<FieldError>erros=ex.getFieldErrors();
		for(FieldError f:erros) {
			map.put(f.getField(), f.getDefaultMessage());
		}
		
		ApiResponse<Map<String, String>> apiResponse=ApiResponse.<Map<String,String>>builder().data(map).sucess(false).message("Bad Request").build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		
	}

}
