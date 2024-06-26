package com.example.kakaoshop.order.item;

import com.example.kakaoshop.order.Order;
import com.example.kakaoshop.product.option.ProductOption;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_item_tb",
        indexes = {
                @Index(name = "order_item_option_id_idx", columnList = "option_id"),
                @Index(name = "order_item_order_id_idx", columnList = "order_id")
        })
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductOption option;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Builder
    public OrderItem(int id, ProductOption option, int quantity, int price, Order order) {
        this.id = id;
        this.option = option;
        this.quantity = quantity;
        this.price = price;
        this.order = order;
    }

}
