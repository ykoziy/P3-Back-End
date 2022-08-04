package com.revature.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revature.annotations.Authorized;
import com.revature.dtos.ProductInfo;
import com.revature.models.Product;
import com.revature.services.ProductService;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:3000" }, allowCredentials = "true")
public class ProductController {
	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@Authorized
	@GetMapping
	public ResponseEntity<List<Product>> getInventory() {
		return ResponseEntity.ok(productService.findAll());
	}

	@Authorized
	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable("id") int id) {
		Optional<Product> optional = productService.findById(id);

		if (!optional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(optional.get());
	}

	@Authorized
	@PutMapping
	public ResponseEntity<Product> upsert(@RequestBody Product product) {
		return ResponseEntity.ok(productService.save(product));
	}

	@Authorized
	@PatchMapping
	public ResponseEntity<List<Product>> purchase(@RequestBody List<ProductInfo> metadata) {
		List<Product> productList = new ArrayList<Product>();

		for (int i = 0; i < metadata.size(); i++) {
			Optional<Product> optional = productService.findById(metadata.get(i).getId());

			if (!optional.isPresent()) {
				return ResponseEntity.notFound().build();
			}

			Product product = optional.get();

			if (product.getQuantity() - metadata.get(i).getQuantity() < 0) {
				return ResponseEntity.badRequest().build();
			}

			product.setQuantity(product.getQuantity() - metadata.get(i).getQuantity());
			productList.add(product);
		}

		productService.saveAll(productList, metadata);

		return ResponseEntity.ok(productList);
	}

	@Authorized
	@DeleteMapping("/{id}")
	public ResponseEntity<Product> deleteProduct(@PathVariable("id") int id) {
		Optional<Product> optional = productService.findById(id);

		if (!optional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		productService.delete(id);

		return ResponseEntity.ok(optional.get());
	}

	@GetMapping("/search/{input}")
	public ResponseEntity<Set<Product>> getBySimilarNameDescription(@PathVariable("input") String input) {

		return ResponseEntity.ok(productService.findBySimilarNameDescription(input));
	}

	@Authorized
	@GetMapping("/search/description/{query}")
	public ResponseEntity<List<Product>> searchByDescription(@PathVariable("query") String query) {
		List<Product> productList = productService.findByDescription(query);
		if (productList.size() == 0) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(productList);
	}

	@GetMapping("search/price")
	public ResponseEntity<List<Product>> searchByPriceRange(@RequestParam double startPrice,
			@RequestParam double endPrice) {
		List<Product> productList = productService.searchByPriceRange(startPrice, endPrice);
		if (productList.size() == 0) {
			return ResponseEntity.status(204).build();
		}
		return ResponseEntity.ok(productList);
	}

	@GetMapping("search/tag/{tagName}")
	public ResponseEntity<List<Product>> searchByTag(@PathVariable("tagName") String tagName) {
		List<Product> productList = productService.searchByTag(tagName);
		if (productList.size() == 0) {
			return ResponseEntity.status(204).build();
		}
		return ResponseEntity.ok(productList);
	}
}
