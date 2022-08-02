package com.revature.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_item")
public class CartItem
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	private int quantity;
	
	
	@ManyToOne(targetEntity = Product.class)
	@JoinColumn(name = "product_id")
	private int productId;
	
	
	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "user_id")
	private int userId;
}
