package com.example.order_service.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// import com.example.order_service.controller.CustomerController;
import com.example.order_service.controller.OrderController;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<OrderDTO>> {

    @Override
    public EntityModel<OrderDTO> toModel(Order order) {
        OrderDTO dto = new OrderDTO(order.getId(), order.getCreatedAt(), order.getQuantity());

        // Manually construct the customer service URL
        String customerServiceUrl = "http://customer-service/api/customers/" + order.getCustomerId();

        return EntityModel.of(dto,
                linkTo(methodOn(OrderController.class).getOrder(order.getId())).withSelfRel(),
                org.springframework.hateoas.Link.of(customerServiceUrl).withRel("customer"), // Replace
                linkTo(methodOn(OrderController.class).updateOrder(order)).withRel("update-order"));
    }
}
