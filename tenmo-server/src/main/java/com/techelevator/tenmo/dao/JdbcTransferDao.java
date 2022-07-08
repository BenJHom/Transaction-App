package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcTransferDao implements TransferDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getAccountId(long userId){
        int accountId = 0;
        String sql = "SELECT account_id FROM account where user_id = ?;";
        try{
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
            if(rowSet.next()){
                accountId = rowSet.getInt("account_id");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return accountId;
    }

    public Transfer createTransfer(Transfer transfer){

        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) "+
                "VALUES (?,?,?,?,?) RETURNING transfer_id;";
        try{
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,
                    transfer.getType(), transfer.getStatus(), transfer.getAccountTo(), transfer.getAccountFrom(), transfer.getAmount());
            if (rowSet.next()){
                transfer = mapRowToTransfer(rowSet);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return transfer;
    }



    // NEEEDS IMPLEMENT
    public Transfer[] getTransfers(){
        return new Transfer[0];
    }

    public boolean doTransfer(Transfer transfer){
        BigDecimal amount = transfer.getAmount();
        if (amount.compareTo(new BigDecimal("0.00"))<=0){
            transfer.setStatus(3);
            return false;
        }

        try {
            String sqlSenderBalance = "SELECT balance FROM account WHERE account_id = ?";
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlSenderBalance, transfer.getAccountFrom());
            BigDecimal senderBalance = new BigDecimal("0.00");
            if (rowSet.next()) {
                senderBalance = rowSet.getBigDecimal("balance");
            }
            if (senderBalance.subtract(amount).compareTo(new BigDecimal("0.00")) < 0) {
                transfer.setStatus(3);
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        String sqlSenderUpdate = "UPDATE account SET balance = balance - ? WHERE account_id = ?;";
        String sqlReceiverUpdate = "UPDATE account SET balance = balance + ? WHERE account_id = ?;";

        try{
            jdbcTemplate.update(sqlSenderUpdate, amount, transfer.getAccountFrom());
            jdbcTemplate.update(sqlReceiverUpdate, amount, transfer.getAccountTo());
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }


        return false;
    }

    public boolean approveTransfer(Transfer transfer){
        return false;
    }

    private Transfer mapRowToTransfer(SqlRowSet rowSet){
        Transfer transfer = new Transfer();

        //Causes invalid column name
        transfer.setType(rowSet.getInt("transfer_type_id"));
        transfer.setStatus(rowSet.getInt("transfer_status_id"));
        transfer.setAccountTo(rowSet.getInt("account_to"));
        transfer.setAccountFrom(rowSet.getInt("account_from"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        transfer.setTransferId(rowSet.getInt("transfer_id"));

        return transfer;
    }
}
