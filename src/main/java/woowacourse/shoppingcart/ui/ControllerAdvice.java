package woowacourse.shoppingcart.ui;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import woowacourse.shoppingcart.exception.*;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleUnhandledException() {
        return ResponseEntity.badRequest().body("Unhandled Exception");
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity handle() {
        return ResponseEntity.badRequest().body("존재하지 않는 데이터 요청입니다.");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleInvalidRequest(final BindingResult bindingResult) {
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        final FieldError mainError = fieldErrors.get(0);

        return ResponseEntity.badRequest().body(mainError.getDefaultMessage());
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            ConstraintViolationException.class,
    })
    public ResponseEntity handleInvalidRequest(final RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({
            InvalidOrderException.class,
            IllegalArgumentException.class,
            DuplicatedCartProductException.class,
            InvalidCartProductException.class
    })
    public ResponseEntity handleInvalidAccess(final RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({
        InvalidCustomerLoginException.class,
        InvalidTokenException.class
    })
    public ResponseEntity handleInvalidLogin(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler({
            InvalidCustomerException.class,
            InvalidProductException.class,
            NotInCustomerCartItemException.class,
            InvalidCartItemException.class
    })
    public ResponseEntity handleCustomerNotFound(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
