package com.example.kakao.order;

import com.example.kakao._core.util.DummyEntity;
import com.example.kakao.cart.Cart;
import com.example.kakao.cart.CartJPARepository;
import com.example.kakao.order.item.Item;
import com.example.kakao.order.item.ItemJPARepository;
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

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Import(ObjectMapper.class)
@DataJpaTest
public class OrderJPARepositoryTest extends DummyEntity {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserJPARepository userJPARepository;

    @Autowired
    private ProductJPARepository productJPARepository;

    @Autowired
    private OptionJPARepository optionJPARepository;

    @Autowired
    private CartJPARepository cartJPARepository;

    @Autowired
    private OrderJPARepository orderJPARepository;

    @Autowired
    private ItemJPARepository itemJPARepository;

    @BeforeEach
    public void setUp() {
        User user = userJPARepository.save(newUser("ssar"));

        // 옵션 생성
        List<Product> productListPS = productJPARepository.saveAll(productDummyList());
        List<Option> optionListPS = optionJPARepository.saveAll(optionDummyList(productListPS));

        // 카트 생성 - 2개
        List<Cart> cartListPS = cartJPARepository.saveAll(cartDummyList(user, optionListPS, 5));

        // 주문 생성
        Order order = orderJPARepository.save(newOrder(user));

        // 주문 아이템 생성
        itemJPARepository.saveAll(itemDummyList(cartListPS, order));

        em.clear();
    }

    @Test
    @DisplayName("주문 상세 확인")
    void order_item_find_test() throws JsonProcessingException {
        //given
        List<Item> itemList = itemJPARepository.findAllByOrderId(1);

        //when


        //then
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String json = mapper.writeValueAsString(itemList);
        System.out.println(json);
    }

    @Test
    @DisplayName("주문 내역 확인")
    void order_find_test() throws JsonProcessingException {
        List<Order> orderList = orderJPARepository.findAllByUserId(1);

        assertThat(orderList).hasSize(1);
        assertThat(orderList.get(0).getId()).isEqualTo(1);
        assertThat(orderList.get(0).getUser().getId()).isEqualTo(1);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String json = mapper.writeValueAsString(orderList);
        System.out.println(json);
    }
}
