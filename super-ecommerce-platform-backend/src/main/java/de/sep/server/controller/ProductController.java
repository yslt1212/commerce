package de.sep.server.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.sep.server.adapter.ProductAdapter;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.GetProductDto;
import de.sep.domain.dto.AddProductDto;
import de.sep.domain.dto.UpdateProductDto;
import de.sep.domain.entity.ProductEntity;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.mapper.ProductMapper;
import de.sep.server.util.ResponseEntity;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;

public class ProductController extends BaseController {

    ProductAdapter productAdapter;
    ProductMapper productMapper;
    UserAdapter userAdapter;

    Gson gson;

    public ProductController(){
        try {
            productAdapter = new ProductAdapter();
            productMapper = new ProductMapper();
            userAdapter = new UserAdapter();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            authenticate(request.getSession());

            if(request.getParameter("productid") != null){
                int productId = Integer.parseInt(request.getParameter("productid"));

                GetProductDto product = productMapper.mapGetProductDto(productAdapter.getProductById(productId));

                responseEntity.setData(product);
            } else {
                List<GetProductDto> products = productAdapter.getAllProducts();
                if(!products.isEmpty()) {
                    responseEntity.setStatus(SC_OK);
                }else{
                    responseEntity.setStatus(SC_NO_CONTENT);
                }
                responseEntity.setData(products);
            }
        } catch (NotFoundException | NumberFormatException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        }catch (UnauthorizedException e){
            responseEntity.setStatus(SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        }catch (Exception e){
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        }finally {
            try {
                sendResponse(response, responseEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response){

        ResponseEntity responseEntity = new ResponseEntity();
//      TODO Implement setting the seller properly when we have a CommercialUserAdapter
        List<AddProductDto> products = new ArrayList<>();

        try {

            authenticateWithType(request.getSession(), 1);

//            User user = userAdapter.getUserById(userId);
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Type requestBodyType;
            if(body.trim().startsWith("[")) {
                requestBodyType = new TypeToken<List<AddProductDto>>() {}.getType();
                products = gson.fromJson(body,requestBodyType);
            }else {
                requestBodyType = new TypeToken<AddProductDto>() {}.getType();
                products.add(gson.fromJson(body,requestBodyType));
            }

            productAdapter.addProducts(products.stream().map(productMapper::mapAddProductDto).collect(Collectors.toList()));
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(SC_UNAUTHORIZED);
            e.printStackTrace();
        } catch (IOException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
            e.printStackTrace();
        }
        try {
            sendResponse(response, responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity responseEntity = new ResponseEntity();

        try {
            authenticateWithType(request.getSession(), 1);
            int productId = Integer.parseInt(request.getParameter("productid"));
            productAdapter.deleteProductById(productId);
            responseEntity.setStatus(SC_OK);
        } catch (SQLException | UnauthorizedException exception) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(exception.getMessage());
            exception.printStackTrace();
        }
        try {
            sendResponse(response,responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response){
        ResponseEntity<UpdateProductDto> responseEntity = new ResponseEntity<>();

        try {
            authenticateWithType(request.getSession(), 1);

            int productId = Integer.parseInt(request.getParameter("productid"));
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            UpdateProductDto updateProductDto = gson.fromJson(body,new TypeToken<UpdateProductDto>() {}.getType());

            ProductEntity product = productAdapter.getProductById(productId);
            double oldPrice = product.getPrice();
            // Change product fields
            product.setName(updateProductDto.getName());
            product.setCategory(updateProductDto.getCategory());
            product.setDescription(updateProductDto.getDescription());
            product.setPrice(updateProductDto.getPrice().doubleValue());
            product.setImg(updateProductDto.getImage());
            product.setDeliveryType(updateProductDto.getDeliveryType());
            // Change the product in db and return the changed product
            product = productAdapter.updateProduct(product,oldPrice);

            responseEntity.setData(productMapper.mapUpdateProductDto(product));
            responseEntity.setStatus(SC_OK);
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
            e.printStackTrace();
        } catch (InternalServerErrorException | NotFoundException | SQLException | IOException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
            e.printStackTrace();
        }

        try {
            sendResponse(response,responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
