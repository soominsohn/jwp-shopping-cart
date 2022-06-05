package woowacourse.shoppingcart.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.dto.CustomerPasswordRequest;
import woowacourse.shoppingcart.dto.CustomerRequest;
import woowacourse.shoppingcart.dto.CustomerResponse;
import woowacourse.shoppingcart.dto.CustomerUpdateRequest;
import woowacourse.shoppingcart.exception.InvalidCustomerException;
import woowacourse.shoppingcart.util.HashTool;

@SuppressWarnings("NonAsciiChracters")
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("classpath:schema.sql")
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @DisplayName("addCustomer 메서드는 회원을 가입한다.")
    @Nested
    class AddCustomerTest {

        @Test
        void 중복되지_않은_아이디일_경우_성공() {
            CustomerRequest customerRequest = new CustomerRequest("angie", "angel", "12345678aA!");

            CustomerResponse actual = customerService.addCustomer(customerRequest);

            assertThat(actual).extracting("loginId", "name")
                    .containsExactly("angie", "angel");
        }

        @Test
        void 중복되는_아이디일_경우_예외발생() {
            CustomerRequest customerRequest = new CustomerRequest("angie", "angel", "12345678aA!");

            customerService.addCustomer(customerRequest);

            assertThatThrownBy(() -> customerService.addCustomer(customerRequest))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("updateCustomer 메서드는 회원정보를 수정한다.")
    @Nested
    class UpdateCustomerTest {

        @Test
        void 정상적인_데이터가_들어올_경우_성공() {
            CustomerRequest customerRequest = new CustomerRequest("angie", "angel", "12345678aA!");
            customerService.addCustomer(customerRequest);

            CustomerUpdateRequest updateCustomerRequest = new CustomerUpdateRequest("seungpapang", "12345678aA!");
            Customer customer = new Customer("angie", "angel", HashTool.hashing("12345678aA!"));

            CustomerResponse actual = customerService.updateCustomer(updateCustomerRequest, customer);

            assertThat(actual).extracting("loginId", "name")
                    .containsExactly("angie", "seungpapang");
        }

        @Test
        void 존재하지_않는_회원일_경우_예외발생() {
            CustomerUpdateRequest updateCustomerRequest = new CustomerUpdateRequest("angel", "12345678aA!");
            Customer customer = new Customer("angie", "angel", HashTool.hashing("12345678aA!"));

            assertThatThrownBy(() -> customerService.updateCustomer(updateCustomerRequest, customer))
                    .isInstanceOf(InvalidCustomerException.class);
        }

        @Test
        void 이미_존재하는_유저네임인_경우_예외발생() {
            CustomerRequest customerRequest = new CustomerRequest("angie", "angel", "12345678aA!");
            customerService.addCustomer(customerRequest);

            CustomerRequest updateCustomerRequest = new CustomerRequest("angie", "angel", "12345678aA!");

            assertThatThrownBy(() -> customerService.addCustomer(updateCustomerRequest))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("deletedCustomer 메서드는 회원 탈퇴를 한다.")
    @Nested
    class deletedCustomerTest {

        @Test
        void 존재하지_않는_회원일_경우_예외발생() {
            CustomerRequest customerRequest = new CustomerRequest("angie", "angel", "12345678aA!");
            customerService.addCustomer(customerRequest);
            Customer deletingCustomer = new Customer("seungpapang", "seungpapang", HashTool.hashing("12345678aA!"));
            CustomerPasswordRequest customerPasswordRequest = new CustomerPasswordRequest("123456789aA!");

            assertThatThrownBy(() -> customerService.deleteCustomer(deletingCustomer, customerPasswordRequest))
                    .isInstanceOf(InvalidCustomerException.class);
        }
    }
}
