package de.sep.server.mapper;

import de.sep.domain.dto.AddProductDto;
import de.sep.domain.dto.GetProductDto;
import de.sep.domain.dto.UpdateProductDto;
import de.sep.domain.entity.ProductEntity;

import java.math.BigDecimal;

public class ProductMapper {

    public ProductEntity mapAddProductDto(AddProductDto productDto){
        return ProductEntity.builder()
                .name(productDto.getProductname())
                .sellerId(productDto.getSeller())
                .price(productDto.getPrice())
                .category(productDto.getCategory())
                .description(productDto.getDescription())
                .img(productDto.getImg())
                .deliveryType(productDto.getDeliveryType())
                .build();
    }

    public AddProductDto mapAddProductDto(ProductEntity product){
        AddProductDto addProductDto = new AddProductDto();
        addProductDto.setProductname(product.getName());
        addProductDto.setSeller(product.getSellerId());
        addProductDto.setPrice(product.getPrice());
        addProductDto.setCategory(product.getCategory());
        addProductDto.setDescription(product.getDescription());
        return addProductDto;
    }

    public GetProductDto mapGetProductDto(ProductEntity product) {
        return new GetProductDto(0,product.getId(), product.getName(), product.getSellerId(), product.getPrice(), product.getCategory(),product.getDescription(),product.getDeliveryType());
    }

    public ProductEntity mapGetProductDto(GetProductDto getProductDto) {
        return new ProductEntity(
                getProductDto.getProductId(),
                getProductDto.getOffername(),
                getProductDto.getSeller(),
                getProductDto.getPrice(),
                getProductDto.getCategory(),
                getProductDto.getDescription(),
                "",
                getProductDto.getDeliveryType());
    }

    public UpdateProductDto mapUpdateProductDto(ProductEntity product){
        return new UpdateProductDto(
                product.getName(),
                product.getCategory(),
                product.getDescription(),
                product.getImg(),
                product.getDeliveryType(),
                BigDecimal.valueOf(product.getPrice()));
    }

    public ProductEntity mapUpdateProductDto(UpdateProductDto updateProductDto){
        return new ProductEntity(0, updateProductDto.getName(), 0,updateProductDto.getPrice().doubleValue(), updateProductDto.getCategory(), updateProductDto.getDescription(),updateProductDto.getImage(),updateProductDto.getDeliveryType());
    }
}
