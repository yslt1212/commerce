package de.sep.server.adapter;

import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import de.sep.domain.dto.*;
import de.sep.domain.entity.ProductEntity;
import de.sep.domain.entity.UserEntity;
import de.sep.server.errors.BadRequestException;
import de.sep.server.errors.ForbiddenException;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.services.GeocodingService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class UserAdapter extends DataAdapter {

    GeocodingService geocodingService;

    public UserAdapter() throws SQLException {
        super("user");
        geocodingService = new GeocodingService();
    }

    String notFoundByNameMessage ="Wir konnten keinen Nutzer mit diesem Namen finden";
    String notFoundByIdMessage = "Wir konnten keine Nutzer mit ideser Id finden";
    String wrongCredentialsMessage="Wrong Password";
    String notEnoughMoney="Leider haben sie nicht genug Geld";
    String internalErrorMessage = "Something went badly wrong";

    public String getCompanynameByID(int id) throws InternalServerErrorException, NotFoundException {
        ResultSet rs = null;

        String company_name = "";
        try {
            rs = getQuery("SELECT company_name FROM `commercial_user` WHERE commercial_user_id = '"+id+"'");

            if(rs.next()) {
                do {
                    company_name = rs.getString("company_name");
                } while (rs.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return company_name;
    }

    public List<MySellsDto> getMySells(int id) throws NotFoundException, InternalServerErrorException {
        ResultSet rs = null;

        List<MySellsDto> list = new ArrayList<>();

        try {
            rs = getQuery("SELECT * FROM orders NATURAL JOIN offer_history JOIN user u ON u.user_id = offer_history.seller natural join product_history WHERE seller ="+id);
            while(rs.next()) {
                MySellsDto p = new MySellsDto();
                p.setOrderId(rs.getInt("order_id"));
                p.setSeller(rs.getString("username"));
                p.setBuyerId(rs.getInt("user_id"));
                p.setDeliveryDate(rs.getTimestamp("delivery_date"));
                p.setDeliveryType(rs.getInt("delivery_type"));
                p.setOffername(rs.getString("offername"));
                p.setPrice(rs.getDouble("price"));
                list.add(p);
            }
            rs = getQuery("SELECT * FROM orders NATURAL JOIN offer_history JOIN user u ON u.user_id = offer_history.seller natural join auction_history WHERE seller ="+id);
            while(rs.next()) {
                MySellsDto p = new MySellsDto();
                p.setOrderId(rs.getInt("order_id"));
                p.setSeller(rs.getString("username"));
                p.setBuyerId(rs.getInt("user_id"));
                p.setDeliveryDate(rs.getTimestamp("delivery_date"));
                p.setDeliveryType(rs.getInt("delivery_type"));
                p.setOffername(rs.getString("offername"));
                p.setPrice(rs.getDouble("start_price"));
                list.add(p);
            }



        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return list;
    }

    public UserEntity getUserById(int id) throws InternalServerErrorException, NotFoundException {
        ResultSet rs = null;

        UserEntity user = null;

        try {
            rs = getById(id);

            if(rs.next()) {

                do {
                    user = UserEntity.builder().id(rs.getInt("user_id"))
                            .username(rs.getString("username"))
                            .password(rs.getString("password"))
                            .email(rs.getString("email"))
                            .street(rs.getString("street"))
                            .postcode(rs.getString("postcode"))
                            .city((rs.getString("city")))
                            .balance(rs.getDouble("balance"))
                            .type(rs.getInt("type"))
                            .wantsTFA(rs.getInt("wantsTFA"))
                            .img(rs.getString("img")).build();
                } while (rs.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return user;
    }

    public UserEntity searchByName(String name) throws InternalServerErrorException, NotFoundException {
        ResultSet rs = null;

        UserEntity user = null;
        try {
            rs = getByName(name, false);

            if(rs.next()) {
                do {
                    user = UserEntity.builder().id(rs.getInt("user_id"))
                            .username(rs.getString("username"))
                            .password(rs.getString("password"))
                            .email(rs.getString("email"))
                            .street(rs.getString("street"))
                            .postcode(rs.getString("postcode"))
                            .city((rs.getString("city")))
                            .balance(rs.getDouble("balance"))
                            .type(rs.getInt("type"))
                            .wantsTFA(rs.getInt("wantsTFA"))
                            .img(rs.getString("img")).build();
                } while (rs.next());
            }else{
                throw new NotFoundException(notFoundByNameMessage);
            }

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return user;
    }

    public UserEntity getByName(String name) throws InternalServerErrorException,NotFoundException {
        ResultSet rs = null;

        UserEntity user = null;
        try {
            rs = getByName(name, true);
            if(rs.next()) {
                do {
                    user = UserEntity.builder().id(rs.getInt("user_id"))
                            .username(rs.getString("username"))
                            .password(rs.getString("password"))
                            .email(rs.getString("email"))
                            .street(rs.getString("street"))
                            .postcode(rs.getString("postcode"))
                            .city((rs.getString("city")))
                            .balance(rs.getDouble("balance"))
                            .type(rs.getInt("type"))
                            .wantsTFA(rs.getInt("wantsTFA"))
                            .img(rs.getString("img")).build();
                } while (rs.next());
            }else{
                throw new NotFoundException(notFoundByNameMessage);
            }

        } catch (SQLException e) {
           throw new InternalServerErrorException(e.getMessage());
        }
        return user;
    }

    public void registerUser(RegisterUserDto rg) throws BadRequestException, InternalServerErrorException {
        try {
            int type = rg.getType();

            switch (type) {
                case 0:
                    addPrivateCustomer(rg);
                    break;
                case 1:
                    addCommercialCustomer(rg);
                    break;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            throw new BadRequestException("You did something wrong: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException("Something went terribly wrong, cause: " + e.getMessage());
        }

    }

    private void addPrivateCustomer(RegisterUserDto rg) throws SQLIntegrityConstraintViolationException, SQLException, BadRequestException {

        String address = rg.getStreet()+", "+rg.getPostcode();
        String [] response = geocodingService.getAddress(address);
        if (response [7] == "false"){
            throw new BadRequestException("Adresse ungültig");
        }
        if (response [7] == "true") {
            LatLng coordinates = new LatLng(Double.parseDouble(response [5]),Double.parseDouble(response [6]));
            String street = response [1] + response [2];
            addQuery(
                    "INSERT INTO user (username, password, email, street, postcode, city, longitude, latitude, balance,type)"
                            + "VALUES ('"
                            + rg.getUsername()
                            + "','"
                            + rg.getPassword()
                            + "','"
                            + rg.getEmail()
                            + "', '"
                            + street
                            + "', '"
                            + response [3]
                            + "', '"
                            + response [4]
                            + "', '"
                            + coordinates.lng
                            + "', '"
                            + coordinates.lat
                            + "', '"
                            + rg.getBalance()
                            + "','"
                            + rg.getType()
                            + "')");
        }
    }


    private void addCommercialCustomer(RegisterUserDto rg) throws SQLException, SQLIntegrityConstraintViolationException, BadRequestException {
        String address = rg.getStreet()+", "+rg.getPostcode();
        String [] response = geocodingService.getAddress(address);
        if (response [7] == "false"){
            throw new BadRequestException("Adresse ungültig");
        }
        if (response [7] == "true") {
            LatLng coordinates = new LatLng(Double.parseDouble(response [5]),Double.parseDouble(response [6]));
            String street = response [1] + response [2];
            addQuery(
                    "INSERT INTO user (username, password, email, street, postcode, city, longitude, latitude, balance,type)"
                            + "VALUES ('"
                            + rg.getUsername()
                            + "','"
                            + rg.getPassword()
                            + "','"
                            + rg.getEmail()
                            + "', '"
                            + street
                            + "', '"
                            + response [3]
                            + "', '"
                            + response [4]
                            + "', '"
                            + coordinates.lng
                            + "', '"
                            + coordinates.lat
                            + "', '"
                            + rg.getBalance()
                            + "','"
                            + rg.getType()
                            + "')");
        }
        ResultSet rs= getByName(rg.getUsername(), true);
        rs.next();
        int userid = rs.getInt("user_id");
        String query = String.format("Insert INTO commercial_user (commercial_user_id, commercial_username,company_name) VALUES ('%d', '%s', '%s')",
                userid, rg.getUsername(), rg.getCompany()
        );
        addQuery(query);
    }


    public void loginUser(String username, String password) throws ForbiddenException, NotFoundException, InternalServerErrorException {
        UserEntity user = this.getByName(username);

        if(!user.getPassword().equals(password)) {
            throw new ForbiddenException(wrongCredentialsMessage);
        }
    }


    public void orderProduct(int userid, int productid) throws InternalServerErrorException, NotFoundException, BadRequestException {
        try {
            ProductAdapter productAdapter = new ProductAdapter();
            ProductEntity product = productAdapter.getProductById(productid);
            Double price = product.getPrice();
            UserEntity user = getUserById(userid);

            int offerId = productAdapter.getOfferIdFromProductId(productid);


            Double balance = user.getBalance();

            if(balance< price) {
                throw new BadRequestException(notEnoughMoney);
            }

            // Insert into offer_history

            String addToOfferHistory = "INSERT INTO offer_history (offer_id, offername, seller, category, description, img, delivery_type) " +
                "VALUES ('%d', '%s', '%d', '%s', '%s', '%s', '%d');";
            addToOfferHistory = String.format(addToOfferHistory, offerId, product.getName(), product.getSellerId(), product.getCategory(), product.getDescription(), "", product.getDeliveryType());


            int offer_history_id = addQuery(addToOfferHistory);

            double oldPrice;

            ResultSet oldPriceSet = getQuery("Select old_price, MAX(time_stamp) from producthistoryview WHERE offer_id="+offerId);
            if(oldPriceSet.next()) {
                double currOldPrice = oldPriceSet.getDouble("old_price");
                if(currOldPrice == 0) {
                    oldPrice = price;
                } else {
                    oldPrice = oldPriceSet.getDouble("old_price");
                }

            } else {
                throw new InternalServerErrorException("Konnte keinen alten Preis finden");

            }

            String addToProductHistory = "Insert into product_history (offer_history_id, price, old_price)" +
                    "VALUES ('%d', '%s', '%s')";

            addToProductHistory = String.format(addToProductHistory, offer_history_id, price, oldPrice);

            addQuery(addToProductHistory);

            Random random = new Random();

            int days = random.nextInt(7)+3;

            long milis = 1000 * 60 * 60 * 24 * days + System.currentTimeMillis();


            Timestamp currentDate = new Timestamp(milis);


            String addToOrder = "Insert into orders (offer_history_id, user_id, delivery_date)" +
                    "VALUES ('%d', '%d', '%s')";

            addToOrder = String.format(addToOrder, offer_history_id, userid, currentDate.toString());


            addQuery(addToOrder);
            balance = balance - price;

            setBalance(userid, balance);

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        } catch (NotFoundException e) {

            StackTraceElement[] trace = e.getStackTrace();
            if(trace[0].getClassName().contains("UserAdapter")) {
                throw new NotFoundException(notFoundByIdMessage);
            } else {
                throw new NotFoundException(e.getMessage());
            }
        }

    }

    public void handleTransaction(double amount,int sourceUserId,int destinationUserId) throws NotFoundException, InternalServerErrorException {
        UserEntity source = getUserById(sourceUserId);
        UserEntity destination = getUserById(destinationUserId);
        //Remove balance from source
        setBalance(source.getId(),source.getBalance()-amount);
        //Add balance to seller
        setBalance(destination.getId(),destination.getBalance()+amount);
    }

    public void updateUser(int id, UpdateUserDto updateUserDto) throws InternalServerErrorException, BadRequestException {
        try {


            String address = updateUserDto.getStreet()+", "+updateUserDto.getPostcode();
            String [] response = geocodingService.getAddress(address);
            if(response != null){
                for(String s : response){
                    System.out.println(s);
                }
            }
            if (response [7] == "false"){
                throw new BadRequestException("Adresse ungültig");
            }
            if (response [7] == "true") {
                LatLng coordinates = new LatLng(Double.parseDouble(response[5]), Double.parseDouble(response[6]));
                String street = response[1] + response[2];

                String query = String.format("UPDATE user SET username='%s', email='%s',street='%s', postcode='%s',city='%s' ,latitude='%s', longitude='%s', img='%s', wantsTFA='%d' WHERE user_id='%s' ",
                        updateUserDto.getUsername(), updateUserDto.getEmail(), updateUserDto.getStreet(),updateUserDto.getPostcode(), updateUserDto.getCity(), coordinates.lat, coordinates.lng, updateUserDto.getImageb64(), updateUserDto.getWantsTFA(), id);

                System.out.println("query");
                updateQuery(query);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BadRequestException("You did something wrong, cause: " + e.getMessage());
        } catch (SQLException e) {
            throw new InternalServerErrorException("Something went terribly wrong, cause: " + e.getMessage());
        }
    }

    public void changePassword(int id, ChangePasswordDto changePasswordDto) throws BadRequestException, InternalServerErrorException, NotFoundException {
        try {

            UserEntity user = getUserById(id);

            if (user.getPassword().equals(changePasswordDto.getOldPassword())) {
                String query = String.format("UPDATE user SET password='%s' WHERE user_id='%s' ",
                        changePasswordDto.getNewPassword(),id);
                updateQuery(query);
            } else {
                throw new BadRequestException(wrongCredentialsMessage);
            }



        } catch (SQLException e) {
            throw new InternalServerErrorException("Something went terribly wrong, cause: " + e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public void setBalance(int id,double balance) throws NotFoundException, InternalServerErrorException {
        // Testing here if user exists
        UserEntity user = getUserById(id);
        String query = String.format(Locale.US,"Update user set balance = '%f' where user_id = '%d'", balance, id);
        try {
            updateQuery(query);
        } catch (SQLException e) {
            throw new InternalServerErrorException("Something went terribly wrong, cause: " + e.getMessage());
        }
    }

    public List<GetProductDto> getLastViewedProducts(int userid, int amount) throws InternalServerErrorException {
        String query=String.format("SELECT offer_id, product_id,offername,seller,price,category,description \n" +
                "FROM lastviewedview a\n" +
                "WHERE user_id='%s'\n" +
                "ORDER BY last_viewed DESC, times_viewed DESC\n" +
                "LIMIT %s",userid, amount);
        try {
            ResultSet rs = getQuery(query);
            List<GetProductDto> products = new ArrayList<>();

            while(rs.next()) {
                GetProductDto p = GetProductDto.builder()
                        .offerId(rs.getInt("offer_id"))
                        .productId(rs.getInt("product_id"))
                        .offername(rs.getString("offername"))
                        .seller(rs.getInt("seller"))
                        .price(rs.getDouble("price"))
                        .category(rs.getString("category"))
                        .description(rs.getString("description"))
                        .build();
                products.add(p);
            }

            return products;

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }

    }

    public void lookAtProduct(int userid, int productid) throws NotFoundException, InternalServerErrorException {
        try {

            UserEntity user = getUserById(userid);
            ProductAdapter productAdapter = new ProductAdapter();
            ProductEntity product = productAdapter.getProductById(productid);


            String query = String.format("SELECT * FROM viewed_product WHERE user_id ='%s' AND product_id ='%s'",userid,productid);
            ResultSet rs = getQuery(query);

            if(rs.next()) {
                int viewProductID=rs.getInt("viewed_product_id");
                int times_viewed=rs.getInt("times_viewed")+1;

                String updateQuery=String.format("UPDATE viewed_product SET times_viewed = '%d', last_viewed =NOW() WHERE viewed_product_id = '%d'",times_viewed,viewProductID);
                addQuery(updateQuery);
            } else {
                String createQuery=String.format("INSERT INTO viewed_product (product_id ,user_id) VALUES ('%d','%s')",productid,userid);
                addQuery(createQuery);
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }


    }



    public void bidOnAuction(int userid,double bidAmount, int auctionid) throws InternalServerErrorException, NotFoundException {
        try {
            AuctionAdapter auctionAdapter = new AuctionAdapter();
            auctionAdapter.getAuctionById(auctionid);

            UserAdapter userAdapter = new UserAdapter();
            userAdapter.getUserById(userid);

            String query = "INSERT INTO bid (auction_id,bid_amount,user_id)"
                    + "VALUES ('"
                    + auctionid
                    + "','"
                    + bidAmount
                    + "','"
                    + userid
                    + "')";
            addQuery(query);

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public List<GetAuctionDto> getMyAuctions(int userid) throws NotFoundException, InternalServerErrorException {
        List<GetAuctionDto> auctions = new ArrayList<>();

        try {

            getUserById(userid);
            ResultSet queryResult = super.getQuery("Select * From auctionview where seller="+userid);
            GetAuctionDto auction = null;
            if(queryResult.next()) {
                do {
                    auction = new GetAuctionDto(
                            queryResult.getInt("auction_id"),
                            queryResult.getString("offername"),
                            queryResult.getInt("seller"),
                            queryResult.getString("category"),
                            queryResult.getString("description"),
                            queryResult.getTimestamp("end_date"),
                            queryResult.getDouble("start_price"),
                            queryResult.getDouble("bid_amount"),
                            queryResult.getInt("delivery_type")
                    );
                    auctions.add(auction);
                } while (queryResult.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }
        } catch (SQLException | InternalServerErrorException exception) {
            throw new InternalServerErrorException(exception.getMessage());
        }

        return auctions;
    }

    public List<GetAuctionDto> getMyBids(int userid) throws NotFoundException, InternalServerErrorException {
        List<GetAuctionDto> auctions = new ArrayList<>();

        try {

            getUserById(userid);
            ResultSet queryResult = super.getQuery("SELECT * FROM auction" +
                    " a NATURAL JOIN bid NATURAL JOIN offer JOIN" +
                    " (SELECT MAX(bid_amount) as max_bid, auction_id From bid GROUP BY auction_id )" +
                    " q ON a.auction_id = q.auction_id WHERE user_id =" +
                    +userid);
            GetAuctionDto auction = null;
            if(queryResult.next()) {
                do {
                    auction = new GetAuctionDto(
                            queryResult.getInt("auction_id"),
                            queryResult.getString("offername"),
                            queryResult.getInt("seller"),
                            queryResult.getString("category"),
                            queryResult.getString("description"),
                            queryResult.getTimestamp("end_date"),
                            queryResult.getDouble("start_price"),
                            queryResult.getDouble("max_bid"),
                            queryResult.getInt("delivery_type")
                    );
                    auctions.add(auction);
                } while (queryResult.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }
        } catch (SQLException | InternalServerErrorException exception) {
            throw new InternalServerErrorException(exception.getMessage());
        }

        return auctions;
    }

    public void noteAuction(int auctionid, int userid) throws NotFoundException, InternalServerErrorException {
        try {
            AuctionAdapter auctionAdapter = new AuctionAdapter();
            auctionAdapter.getAuctionById(auctionid);

            getUserById(userid);

            String query = "INSERT INTO noted_auction (auction_id,user_id)"
                    + "VALUES ('"
                    + auctionid
                    + "','"
                    + userid
                    + "')";
            logger.log(query);
            addQuery(query);

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public List<GetAuctionDto> getNotedAuctions(int userid) throws NotFoundException, InternalServerErrorException {
        List<GetAuctionDto> auctions = new ArrayList<>();

        try {

            getUserById(userid);
            ResultSet queryResult = super.getQuery("SELECT * FROM noted_auction NATURAL JOIN auctionview WHERE user_id ="+userid);
            GetAuctionDto auction = null;
            if(queryResult.next()) {
                do {
                    auction = new GetAuctionDto(
                            queryResult.getInt("auction_id"),
                            queryResult.getString("offername"),
                            queryResult.getInt("seller"),
                            queryResult.getString("category"),
                            queryResult.getString("description"),
                            queryResult.getTimestamp("end_date"),
                            queryResult.getDouble("start_price"),
                            queryResult.getDouble("bid_amount"),
                            queryResult.getInt("delivery_type")
                    );
                    auctions.add(auction);
                } while (queryResult.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }
        } catch (SQLException | InternalServerErrorException exception) {
            throw new InternalServerErrorException(exception.getMessage());
        }

        return auctions;
    }

    /*public List<VoucherDto> getVouchers(int userid) throws NotFoundException, InternalServerErrorException {
        List<VoucherDto> vouchers = new ArrayList<>();

        try {

            getUserById(userid);
            ResultSet queryResult = super.getQuery("SELECT * FROM voucher WHERE user_id ="+userid);
            VoucherDto voucher = null;
            if(queryResult.next()) {
                do {
                    voucher = new VoucherDto(
                            queryResult.getInt("voucher_id"),
                            queryResult.getInt("code"),
                            queryResult.getInt("value"),
                            queryResult.getInt("active"),
                            queryResult.getInt("user_id")
                            );
                    vouchers.add(voucher);
                } while (queryResult.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }
        } catch (SQLException | InternalServerErrorException exception) {
            throw new InternalServerErrorException(exception.getMessage());
        }

        return vouchers;
    }*/

   public void addVoucher(VoucherDto voucher) throws SQLException {
        String intoVoucherBaseQuery = "INSERT INTO VOUCHER (code,value,active,user_id) VALUES";
        StringBuilder queryBuilder = new StringBuilder(intoVoucherBaseQuery);
           queryBuilder.append(String.format(Locale.US,"('%s','%d','%d','%d')",
           voucher.getCode(),
           voucher.getValue(),
           1,
           voucher.getUser_id()));


            super.addQuery(queryBuilder.toString());





   }

   /* public boolean validateCode(String code) throws SQLException{

        int codeint=Integer.parseInt(code);
        ResultSet queryResult= super.getQuery("SELECT voucher.code");
        if(queryResult.next()){
            do{
                if(queryResult.getInt("code")==codeint){

                    return true;
                }
            }while(queryResult.next());
        }

        return false;
    }
    public int getVoucherAmount(String code) throws SQLException{
        if(validateCode(code)==false) return 0;

        int codeint=Integer.parseInt(code);

        ResultSet queryResult= super.getQuery("SELECT value FROM voucher WHERE code="+code);
        return queryResult.getInt("value");



    }

    */
    public GetVoucherDto getVoucher(String code)throws SQLException{

        GetVoucherDto gvdto=null;
        String query = "SELECT * FROM voucher WHERE code LIKE '"+code+"'";
        System.out.println(query);
        ResultSet queryResult = super.getQuery(query);

        if(queryResult.next()) {
            gvdto = new GetVoucherDto(
                    queryResult.getInt("voucher_id"),
                    queryResult.getString("code"),
                    queryResult.getInt("value"),
                    queryResult.getInt("active"),
                    queryResult.getInt("user_id")
            );
        }
        return gvdto;
    }
    public List<GetUserGiftDto> getUserGifts(int userid) throws Exception{
        List<GetUserGiftDto> userGiftList=new ArrayList<>();
        GetUserGiftDto grdto = null;
        ResultSet queryResult = super.getQuery("SELECT * From USER WHERE NOT user_id="+userid+ " AND Type=0");
        System.err.println("SELECT * From USER WHERE NOT user_id="+userid);
        if(queryResult.next()) {
            do {
                grdto = new GetUserGiftDto(
                        queryResult.getInt("user_id"),
                        queryResult.getString("img"),
                        queryResult.getString("username")


                );

                userGiftList.add(grdto);
            } while(queryResult.next());
        }

        return userGiftList;



    }
    public void changeVoucherOwnership(UpdateVoucherDto uvdto) throws SQLException {
        String query = "UPDATE VOUCHER SET user_id="+uvdto.getUserid()+" WHERE code="+uvdto.getCode();
        addQuery(query);
    }

    public void deactivateVoucher(String code) throws SQLException {
        String query = "UPDATE VOUCHER SET active=0 WHERE code="+code;
		addQuery(query);
	}
	
    public List<GetVoucherDto> showVoucher(int user_id)throws SQLException{
        List<GetVoucherDto> list = new ArrayList<>();

        GetVoucherDto gvdto=null;

        ResultSet queryResult = super.getQuery("SELECT * FROM voucher WHERE user_id="+user_id+ " AND active = 1");
        if(queryResult.next()) {
            do {
                gvdto = new GetVoucherDto(
                        queryResult.getInt("voucher_id"),
                        queryResult.getString("code"),
                        queryResult.getInt("value"),
                        queryResult.getInt("active"),
                        queryResult.getInt("user_id"));
                list.add(gvdto);
            } while (queryResult.next());

        }
        return list;

    }
}
