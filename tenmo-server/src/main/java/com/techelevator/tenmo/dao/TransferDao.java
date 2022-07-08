package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

List<Transfer> listTransfers(long userId);

Transfer createTransfer(Transfer transfer);

int getAccountId(long userId);

boolean doTransfer(Transfer transfer);

boolean approveTransfer(Transfer transfer);
}
