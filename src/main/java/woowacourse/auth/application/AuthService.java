package woowacourse.auth.application;

import org.springframework.stereotype.Service;
import woowacourse.auth.dto.TokenRequest;
import woowacourse.auth.dto.TokenResponse;
import woowacourse.auth.support.JwtTokenProvider;
import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.dto.LoginCustomer;

@Service
public class AuthService {

    private final CustomerDao customerDao;
    private JwtTokenProvider jwtTokenProvider;

    public AuthService(CustomerDao customerDao, JwtTokenProvider jwtTokenProvider) {
        this.customerDao = customerDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public TokenResponse createToken(TokenRequest tokenRequest) {
        if (!customerDao.checkValidLogin(tokenRequest.getLoginId(), tokenRequest.getPassword())) {
            throw new IllegalArgumentException("아이디나 패스워드 정보가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(tokenRequest.getLoginId());
        Customer customer = customerDao.findIdByLoginId(tokenRequest.getLoginId());
        return new TokenResponse(token, customer.getName());
    }

    public LoginCustomer findCustomerByToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        String payload = jwtTokenProvider.getPayload(token);

        return new LoginCustomer(customerDao.findIdByLoginId(payload));
    }
}
