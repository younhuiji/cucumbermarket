package com.sohwakmo.cucumbermarket.repository;

import com.sohwakmo.cucumbermarket.domain.Member;
import com.sohwakmo.cucumbermarket.domain.Product;
import com.sohwakmo.cucumbermarket.domain.ProductOfInterested;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductOfInterestedRepository  extends JpaRepository<ProductOfInterested, Integer> {
    Optional<ProductOfInterested> findByMemberAndProduct(Integer member, Product product);

    // delete table products_of_interested where member_no = ? and product_no = ?
    void deleteByMemberAndProduct(Integer member, Product product);

    // select * from products_of_interested where member_no = ?
    List<ProductOfInterested> findByMember(Integer Member);

    // select * from products_of_interested where product_no = ?
    ProductOfInterested findByProduct(Product product);

    // delete table products_of_interested where product_no = ?
    void deleteByProduct(Product product);

    Page<ProductOfInterested> findByMemberOrderByProductProductNoDesc(Integer memberNo, Pageable pageable);
}
