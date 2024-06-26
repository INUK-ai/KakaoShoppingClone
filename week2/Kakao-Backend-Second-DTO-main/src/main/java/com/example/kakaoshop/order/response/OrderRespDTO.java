package com.example.kakaoshop.order.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class OrderRespDTO {
    private int id;
    private List<ProductDTO> products;
    private int totalPrice;

    @Builder
    public OrderRespDTO(int id, List<ProductDTO> products, int totalPrice) {
        this.id = id;
        this.products = products;
        this.totalPrice = totalPrice;
    }
}
