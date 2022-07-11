package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
                    transfer.getType(), transfer.getStatus(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
            if (rowSet.next()){
                transfer.setTransferId(rowSet.getInt("transfer_id"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return transfer;
    }


    public List<Transfer> listTransfers(long userId){
        List<Transfer> transfers = new ArrayList<>();
        int userAccount = getAccountId(userId);

        String sqlSender = "SELECT transfer_id, account_to, account_from, transfer_type_id, transfer_status_id, t1.username as from_user, t2.username to_user, amount FROM transfer " +
                "join account as a1 on account_to = a1.account_id\n" +
                "join account as a2 on account_from = a2.account_id \n" +
                "join tenmo_user as t1 on a1.user_id = t1.user_id \n" +
                "join tenmo_user as t2 on a2.user_id = t2.user_id " +
        "WHERE a1.account_id = ? OR a2.account_id = ?;";

        try{
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlSender, userAccount, userAccount);
            while (rowSet.next()){
                transfers.add(mapRowToTransfer(rowSet));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return transfers;
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
        transfer.setSender(new User());

        transfer.setType(rowSet.getInt("transfer_type_id"));
        transfer.setStatus(rowSet.getInt("transfer_status_id"));
        transfer.setAccountTo(rowSet.getInt("account_to"));
        transfer.setAccountFrom(rowSet.getInt("account_from"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setReceiverUsername(rowSet.getString("to_user"));
        transfer.getSender().setUsername(rowSet.getString("from_user"));

        return transfer;
    }
}
