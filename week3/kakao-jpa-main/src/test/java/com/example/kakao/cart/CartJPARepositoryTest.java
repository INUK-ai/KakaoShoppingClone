package com.example.kakao.cart;

import com.example.kakao._core.util.DummyEntity;
import com.example.kakao.product.Product;
import com.example.kakao.product.ProductJPARepository;
import com.example.kakao.product.option.Option;
import com.example.kakao.product.option.OptionJPARepository;
import com.example.kakao.user.User;
import com.example.kakao.user.UserJPARepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Import(ObjectMapper.class)
@DataJpaTest
public class CartJPARepositoryTest extends DummyEntity {

    @Autowired
    private EntityManager em;

    // 최대한 다른 Repository는 쓰지 않는 것이 좋은가?
    @Autowired
    private UserJPARepository userJPARepository;

    @Autowired
    private ProductJPARepository productJPARepository;

    @Autowired
    private OptionJPARepository optionJPARepository;

    @Autowired
    private CartJPARepository cartJPARepository;

    @Autowired
    private ObjectMapper om;

    @BeforeEach
    @DisplayName("테스트 데이터 생성 + 장바구니 담기")
    public void setUp() {
        // 유저 생성
        User user = userJPARepository.save(newUser("ssar"));

        // 옵션 생성
        List<Product> productListPS = productJPARepository.saveAll(productDummyList());
        List<Option> optionListPS = optionJPARepository.saveAll(optionDummyList(productListPS));

        // 카트 생성 - 2개
        cartJPARepository.saveAll(cartDummyList(user, optionListPS, 5));

        em.clear();
    }

    @Test
    @DisplayName("장바구니 조회")
    public void cart_find_test() throws JsonProcessingException {
        // given
        User user = userJPARepository.findById(1).orElseThrow(
                () -> new RuntimeException("해당 고객을 찾을 수 없습니다.")
        );

        // when
        List<Cart> cartList = cartJPARepository.findAllByUserId(user.getId());

        // then
        assertThat(cartList.get(0).getId()).isEqualTo(1);
        assertThat(cartList.get(0).getUser()).isEqualTo(user);
        assertThat(cartList.get(0).getOption().getId()).isEqualTo(1);
        assertThat(cartList.get(0).getQuantity()).isEqualTo(5);
        assertThat(cartList.get(0).getPrice()).isEqualTo(50000);

        assertThat(cartList.get(1).getId()).isEqualTo(2);
        assertThat(cartList.get(1).getUser()).isEqualTo(user);
        assertThat(cartList.get(1).getOption().getId()).isEqualTo(2);
        assertThat(cartList.get(1).getQuantity()).isEqualTo(5);
        assertThat(cartList.get(1).getPrice()).isEqualTo(54500);

        /* option을 LAZY로 하고 하는 방법은?
        List<CartDTO> cartDTOList = cartList.stream()
                        .map(cart -> new CartDTO(
                                cart.getId(),
                                cart.getUser(),
                                cart.getQuantity(),
                                cart.getPrice()
                        ))
                                .collect(Collectors.toList());

        1. 새로운 DTO를 만든다
        2. Object Mapper를 고친다
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String json = mapper.writeValueAsString(newCart);
        System.out.println(json);

        3. 즉시 로딩 전략으로 수정한다
        4. 프록시 객체를 강제로 초기화한다.
        Hibernate.initialize(cart.getOption());

        System.out.println(om.writeValueAsString(cartList));
         */
    };

    @Test
    @DisplayName("장바구니 변경 - quantity 수정")
    @Transactional
    public void cart_update_test() throws JsonProcessingException {
        // given
        Cart cart = cartJPARepository.findById(1).orElseThrow(
                () -> new RuntimeException("해당 상품을 찾을 수 없습니다.")
        );

        // when
        int quantity = 10;
        cart.update(quantity, cart.getOption().getPrice() * quantity);
        cartJPARepository.save(cart);

        em.flush();

        Cart newCart = cartJPARepository.findById(1).orElseThrow(
                () -> new RuntimeException("해당 상품을 찾을 수 없습니다.")
        );

        // then
        assertThat(newCart.getId()).isEqualTo(1);
        assertThat(newCart.getOption().getId()).isEqualTo(1);
        assertThat(newCart.getQuantity()).isEqualTo(quantity);
        assertThat(newCart.getPrice()).isEqualTo(100000);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String json = mapper.writeValueAsString(newCart);
        System.out.println(json);
    }

    @Test
    @DisplayName("장바구니 전체 삭제")
    public void cart_delete_all_test() {
        // given

        //when
        cartJPARepository.deleteAll();

        //then
        List<Cart> cartList = cartJPARepository.findAll();

        assertThat(cartList).isEmpty();
    }
}
