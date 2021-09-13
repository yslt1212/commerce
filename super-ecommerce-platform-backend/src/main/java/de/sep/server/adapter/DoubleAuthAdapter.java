package de.sep.server.adapter;

import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class DoubleAuthAdapter extends DataAdapter{
    UserAdapter userAdapter = new UserAdapter();

    public DoubleAuthAdapter() throws SQLException {
        super("double_auth_code");

    }

    public int getCodeById(int double_auth_code_id) throws SQLException, NotFoundException {
        ResultSet rs = super.getById(double_auth_code_id);
        int code;
        if(rs.next()) {
            do {
                code = rs.getInt("code");
            }while (rs.next());
        } else {
            throw new NotFoundException("did not found any code with this id");
        }
        return code;
    }

    public int getCode(String val, int type) throws NotFoundException, SQLException {
        int code = -1;

        String valPointer = "email";

        if(type == 1) {
            valPointer = "tel";
        }

        String query = "Select * From " + this.table +" where "+valPointer+" = '" + val + "' and used = 0 order by time_stamp desc limit 1";
        ResultSet queryResult = super.getQuery(query);
        if(queryResult.next()) {
            do {
                code = queryResult.getInt("code");
            }while (queryResult.next());
        }else{
            throw new NotFoundException("No code created for this" + val);
        }


        return code;

    }

    public int createCode(String val, int type) throws SQLException {
        Random random = new Random();

        int code = random.nextInt(80000) + 10000;

        String valPointer = "email";

        if(type == 1) {
            valPointer = "tel";
        }

        String query = String.format("Insert into %s (%s, code) Values ('%s', %d)", this.table,valPointer, val, code);

        return super.addQuery(query);
    }

    public void deactivateCode(int code, int type) throws SQLException, NotFoundException {

        if(type == 0) {
            String emailQuery = String.format("Select email from double_auth_code where code = %d", code);

            ResultSet rs = super.getQuery(emailQuery);

            String email;

            if(rs.next()) {
                do {
                    email = rs.getString("email");
                }while (rs.next());

                String query = String.format("UPDATE double_auth_code SET used='1' WHERE email LIKE '%s'", email);
                System.out.println(query);
                super.updateQuery(query);
            } else {
                throw new NotFoundException("Could not find that code");
            }

        } else if (type == 1) {
            String query = String.format("UPDATE double_auth_code SET used='1' WHERE code = %d", code);

            super.updateQuery(query);
        }




    }


}
