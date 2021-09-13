package de.sep.server.adapter;

import de.sep.domain.dto.GetOrderDto;
import de.sep.domain.dto.GetProductDto;
import de.sep.domain.entity.ProductEntity;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ProductAdapter extends DataAdapter{


    String notFoundByNameMessage ="Wir konnten kein Produkt mit diesem Namen finden";
    String notFoundByIdMessage = "Wir konnten kein Produkt mit dieser Id finden";
    String internalErrorMessage = "Something went badly wrong";

    public ProductAdapter() throws SQLException {
        super("productview");
        super.setIdCol("product_id");
        super.setNameCol("offername");

    }

    public void addProducts(List<ProductEntity> products){

        String intoOfferBaseQuery = "INSERT INTO OFFER (offername, seller,category, description, img, delivery_type) VALUES";
        StringBuilder queryBuilder = new StringBuilder(intoOfferBaseQuery);
        try {
            for(ProductEntity product: products) {
                queryBuilder.append(String.format(Locale.US,"('%s','%d','%s','%s','%s','%d')",
                        product.getName(),
                        product.getSellerId(),
                        product.getCategory(),
                        product.getDescription(),
                        product.getImg(),
                        product.getDeliveryType()));
                logger.log(queryBuilder.toString());
                int offerid = addQuery(queryBuilder.toString());
                String query = String.format(Locale.US,"INSERT INTO PRODUCT (offer_id, price) VALUES('%d', '%.2f')",
                        offerid,
                        product.getPrice());
                addQuery(query);
                queryBuilder = new StringBuilder(intoOfferBaseQuery);
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

    }

    public List<GetProductDto> getAllProducts() throws NotFoundException {
        List<GetProductDto> products = new ArrayList<>();

        try {
            ResultSet queryResult = super.getAll();
            GetProductDto product = null;
                if(queryResult.next()) {
                    do {
                        product = GetProductDto.builder()
                                .offerId(queryResult.getInt("offer_id"))
                                .productId(queryResult.getInt("product_id"))
                                .offername(queryResult.getString("offername"))
                                .seller(queryResult.getInt("seller"))
                                .price(queryResult.getDouble("price"))
                                .category(queryResult.getString("category"))
                                .description((queryResult.getString("description")))
                                .deliveryType((queryResult.getInt("delivery_type")))
                                .build();
                        products.add(product);
                    } while (queryResult.next());
                }else{
                    throw new NotFoundException(notFoundByIdMessage);
                }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return products;
    }

    public ProductEntity getProductById(int id) throws InternalServerErrorException, NotFoundException {
        ResultSet rs = null;

        ProductEntity product = null;
        try {

            rs = super.getById(id);

            if(rs.next()) {
                do {
                    product = ProductEntity.builder()
                            .id(rs.getInt("product_id"))
                            .name(rs.getString("offername"))
                            .sellerId(rs.getInt("seller"))
                            .price(rs.getDouble("price"))
                            .category(rs.getString("category"))
                            .description((rs.getString("description")))
                            .img((rs.getString("img")))
                            .deliveryType((rs.getInt("delivery_type")))
                            .build();
                } while (rs.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return product;
    }

    public ProductEntity searchByName(String name) throws InternalServerErrorException, NotFoundException {
        ResultSet rs = null;

        ProductEntity product = null;
        try {
            rs = super.getByName(name, false);

            if(rs.next()) {
                do {
                    product = ProductEntity.builder()
                            .id(rs.getInt("product_id"))
                            .name(rs.getString("offername"))
                            .sellerId(rs.getInt("seller"))
                            .price(rs.getDouble("price"))
                            .category(rs.getString("category"))
                            .description((rs.getString("description")))
                            .build();
                } while (rs.next());
            }else{
                throw new NotFoundException(notFoundByNameMessage);
            }

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return product;
    }

    public ProductEntity getByName(String name) throws InternalServerErrorException,NotFoundException {
        ResultSet rs = null;

        ProductEntity product = null;
        try {
            rs = super.getByName(name, true);

            if(rs.next()) {
                do {
                    product = ProductEntity.builder()
                            .id(rs.getInt("product_id"))
                            .name(rs.getString("offername"))
                            .sellerId(rs.getInt("seller"))
                            .price(rs.getDouble("price"))
                            .category(rs.getString("category"))
                            .description((rs.getString("description")))
                            .build();
                } while (rs.next());
            }else{
                throw new NotFoundException(notFoundByNameMessage);
            }

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return product;
    }

    public List<GetProductDto> getMyProducts(int userid) throws NotFoundException, InternalServerErrorException {
        List<GetProductDto> products = new ArrayList<>();

        try {
            UserAdapter userAdapter = new UserAdapter();

            userAdapter.getUserById(userid);
            ResultSet queryResult = super.getQuery("Select * From productview where seller="+userid);
            GetProductDto product = null;
            if(queryResult.next()) {
                do {
                    product = new GetProductDto(
                            queryResult.getInt("offer_id"),
                            queryResult.getInt("product_id"),
                            queryResult.getString("offername"),
                            queryResult.getInt("seller"),
                            queryResult.getDouble("price"),
                            queryResult.getString("category"),
                            queryResult.getString("description"),
                            queryResult.getInt("delivery_type")
                    );
                    products.add(product);
                } while (queryResult.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }
        } catch (SQLException | InternalServerErrorException exception) {
            throw new InternalServerErrorException(exception.getMessage());
        }

        return products;
    }

    public void deleteOrder(int order_id) throws SQLException{


        String deleteOrderQuery = String.format("DELETE FROM orders WHERE order_id='%d'", order_id );
        PreparedStatement preparedStatement = connection.prepareStatement(deleteOrderQuery);

        preparedStatement.executeUpdate();
    }

    public List<GetOrderDto> getOrders(int user_id) throws SQLException, InternalServerErrorException {

        List<GetOrderDto> list = new ArrayList<>();


        ResultSet rs = null;

        GetOrderDto order = null;
        try {

             rs = super.getQuery("SELECT orders.order_id, u.username AS seller, product_history.price AS price, offer_history.offername " +
                     "AS offername, orders.delivery_date, offer_history.delivery_type FROM orders NATURAL JOIN offer_history " +
                     "NATURAL JOIN product_history JOIN user u ON u.user_id = offer_history.seller WHERE orders.user_id ="+ user_id);

            if(rs.next()) {
                do {
                    order = GetOrderDto.builder()
                            .orderId(rs.getInt("order_id"))
                            .deliveryDate(rs.getTimestamp("delivery_date"))
                            .seller(rs.getString("seller"))
                            .offername(rs.getString("offername"))
                            .price((rs.getDouble("price")))
                            .deliveryType(rs.getInt("delivery_type"))

                            .build();
                    list.add(order);
                } while (rs.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }

        } catch (SQLException | NotFoundException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return list;


    }


    public int getOfferIdFromProductId(int productId) throws SQLException, NotFoundException {
        String productViewQuery = String.format("SELECT (offer_id) FROM productview where product_id=%d",productId);
        ResultSet productViewResult = super.getQuery(productViewQuery);
        if(productViewResult.next()){
            return productViewResult.getInt("offer_id");
        }else{
            throw new NotFoundException(String.format("Produkt mit product_id=%d konnte nicht gefunden werden!",productId));
        }
    }

    public ProductEntity updateProduct(ProductEntity product, double oldPrice) throws SQLException, NotFoundException, InternalServerErrorException {

        int offer_id = getOfferIdFromProductId(product.getId());

        String updateOfferQuery = String.format("UPDATE offer SET offername='%s', category='%s', description='%s', img='%s', delivery_type='%d' WHERE offer_id = '%d'",
                product.getName(),
                product.getCategory(),
                product.getDescription(),
                product.getImg(),
                product.getDeliveryType(),
                offer_id);
        String updateProductQuery = String.format(Locale.US,"UPDATE product SET price='%f' WHERE product_id='%d'",product.getPrice(),product.getId());
        String insertOfferHistoryQuery = String.format(Locale.US,"INSERT INTO offer_history (offer_id, offername, seller, category, description, img, delivery_type) VALUES ('%d', '%s', '%d', '%s', '%s', '%s', '%d')",
                offer_id,product.getName(),product.getSellerId(),product.getCategory(),product.getDescription(),product.getImg(),product.getDeliveryType());


        int offerHistoryId = addQuery(insertOfferHistoryQuery);
        String insertProductHistoryQuery = String.format(Locale.US,"INSERT INTO product_history (offer_history_id, price, old_price) VALUES ('%d', '%.2f', '%.2f')"
                ,offerHistoryId,product.getPrice(),oldPrice);
        addQuery(insertProductHistoryQuery);

        updateQuery(updateProductQuery);
        updateQuery(updateOfferQuery);

        return getProductById(product.getId());
    }

    public void deleteProductById(int productId) throws SQLException {
        String deleteProductQuery = String.format("DELETE FROM product WHERE product_id='%d'",productId );

        PreparedStatement preparedStatement = connection.prepareStatement(deleteProductQuery);

        preparedStatement.executeUpdate();
    }

    public List<GetProductDto> getSimiliarProducts(int productId) throws NotFoundException, InternalServerErrorException {
        List<GetProductDto> products = new ArrayList<>();
        try {
            /*UserAdapter userAdapter = new UserAdapter();

            userAdapter.getUserById(userid);*/
            ResultSet queryResult = super.getQuery(
                    "Select distinct b.offer_id,c.product_id,b.offername,b.seller,c.price,b.category,b.description,b.delivery_type " +
                            "FROM orders a LEFT JOIN offer_history b ON a.offer_history_id = b.offer_history_id LEFT JOIN product c ON b.offer_id = c.offer_id " +
                            "WHERE c.product_id!=1 AND a.user_id IN (SELECT a.user_id as user_a FROM orders a LEFT JOIN offer_history b ON a.offer_history_id = b.offer_history_id LEFT JOIN product c ON b.offer_id = c.offer_id WHERE c.product_id="+productId+") LIMIT 4");
            GetProductDto product = null;
            if(queryResult.next()) {
                do {
                    product = new GetProductDto(
                            queryResult.getInt("b.offer_id"),
                            queryResult.getInt("c.product_id"),
                            queryResult.getString("b.offername"),
                            queryResult.getInt("b.seller"),
                            queryResult.getDouble("c.price"),
                            queryResult.getString("b.category"),
                            queryResult.getString("b.description"),
                            queryResult.getInt("b.delivery_type")
                    );
                    products.add(product);
                } while (queryResult.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }
        } catch (SQLException exception) {
            throw new InternalServerErrorException(exception.getMessage());
        }

        return products;
    }
}
