package woowacourse.shoppingcart.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static woowacourse.auth.acceptance.AuthAcceptanceTest.내_정보_조회_요청;
import static woowacourse.auth.acceptance.AuthAcceptanceTest.로그인_요청;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import woowacourse.auth.dto.TokenResponse;
import woowacourse.shoppingcart.dto.CustomerResponse;
import woowacourse.shoppingcart.dto.LoginRequest;

@DisplayName("회원 관련 기능")
public class CustomerAcceptanceTest extends AcceptanceTest {

    @DisplayName("회원가입")
    @Test
    void addCustomer() {
        ExtractableResponse<Response> response = 사용자_생성_요청("loginId", "seungpapang", "12345678aA");

        사용자_추가됨(response);
    }

    @DisplayName("내 정보 조회")
    @Test
    void getMe() {
        사용자_생성_요청("loginId", "seungpapang", "12345678aA!");
        LoginRequest loginRequest = new LoginRequest("loginId", "12345678aA!");
        ExtractableResponse<Response> response = 로그인_요청(loginRequest);
        TokenResponse tokenResponse = response.as(TokenResponse.class);

        ExtractableResponse<Response> getMeResponse = 내_정보_조회_요청(tokenResponse);
        CustomerResponse customerResponse = getMeResponse.as(CustomerResponse.class);

        assertAll(() -> {
            assertThat(getMeResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(customerResponse).extracting("loginId", "username")
                .containsExactly("loginId", "seungpapang");
        });
    }


    @DisplayName("내 정보 수정")
    @Test
    void updateMe() {
    }

    @DisplayName("회원탈퇴")
    @Test
    void deleteMe() {
    }

    public static ExtractableResponse<Response> 사용자_생성_요청(String loginId, String username, String password) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("loginId", loginId);
        requestBody.put("username", username);
        requestBody.put("password", password);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().post("/customers")
                .then().log().all()
                .extract();
    }

    public static void 사용자_추가됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }
}
