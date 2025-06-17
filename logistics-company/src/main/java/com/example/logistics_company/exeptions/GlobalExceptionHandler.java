package com.example.logistics_company.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобален обработчик на изключения (Exception Handler) за REST контролерите.
 * Използва @ControllerAdvice, за да прихваща изключения от целия контролер слой
 * и да връща консистентни HTTP отговори с подходящи статуси и съобщения.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработва валидиращи грешки при @Valid или @Validated анотации в аргументите на методите.
     * Събира всички грешки по полета и ги връща в JSON формат.
     *
     * @param ex изключението, хвърлено при неуспешна валидация на аргументите
     * @return ResponseEntity със статус 400 Bad Request и тяло Map<име на поле, съобщение за грешка>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err->errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Обработва опити за достъп до защитени ресурси без необходимите права.
     *
     * @param ex изключението AccessDeniedException, хвърлено при отказан достъп
     * @return ResponseEntity със статус 403 Forbidden и тялото съдържа съобщението на изключението
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleForbidden(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    /**
     * Обработва всички останали непредвидени изключения.
     * Връща генерично съобщение за грешка с HTTP статус 500 Internal Server Error.
     *
     * @param ex изключението, което не е било по-специално обработено
     * @return ResponseEntity със статус 500 Internal Server Error и съобщението на изключението
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}
