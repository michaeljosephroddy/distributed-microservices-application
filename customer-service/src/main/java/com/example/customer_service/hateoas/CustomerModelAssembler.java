package com.example.customer_service.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.customer_service.controller.CustomerController;
// import com.example.order_service.controller.OrderController;
import com.example.customer_service.model.Customer;
import com.example.customer_service.model.CustomerDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class CustomerModelAssembler implements RepresentationModelAssembler<Customer, EntityModel<CustomerDTO>> {

        @Override
        public EntityModel<CustomerDTO> toModel(Customer customer) {
                CustomerDTO dto = new CustomerDTO(
                                customer.getId(),
                                customer.getName(),
                                customer.getEmail(),
                                customer.getTotalOrders());

                String orderServiceUrl = "http://order-service/api/orders/" + customer.getId();

                return EntityModel.of(dto,
                                linkTo(methodOn(CustomerController.class).getCustomerById(customer.getId()))
                                                .withSelfRel(),
                                Link.of(orderServiceUrl, "customer-orders"),
                                linkTo(methodOn(CustomerController.class).createCustomer(customer))
                                                .withRel("update-customer"),
                                linkTo(methodOn(CustomerController.class).deleteCustomer(customer.getId()))
                                                .withRel("delete-customer"));
        }
}