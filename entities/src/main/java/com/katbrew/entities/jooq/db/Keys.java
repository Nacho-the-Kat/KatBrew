/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db;


import com.katbrew.entities.jooq.db.tables.Advertisements;
import com.katbrew.entities.jooq.db.tables.Announcements;
import com.katbrew.entities.jooq.db.tables.Balance;
import com.katbrew.entities.jooq.db.tables.FetchData;
import com.katbrew.entities.jooq.db.tables.Holder;
import com.katbrew.entities.jooq.db.tables.LastUpdate;
import com.katbrew.entities.jooq.db.tables.NftBalance;
import com.katbrew.entities.jooq.db.tables.NftCollection;
import com.katbrew.entities.jooq.db.tables.NftCollectionEntry;
import com.katbrew.entities.jooq.db.tables.NftCollectionInfo;
import com.katbrew.entities.jooq.db.tables.NftTransaction;
import com.katbrew.entities.jooq.db.tables.PriceData;
import com.katbrew.entities.jooq.db.tables.Token;
import com.katbrew.entities.jooq.db.tables.TopHolder;
import com.katbrew.entities.jooq.db.tables.Transaction;
import com.katbrew.entities.jooq.db.tables.Users;
import com.katbrew.entities.jooq.db.tables.Whitelist;
import com.katbrew.entities.jooq.db.tables.records.AdvertisementsRecord;
import com.katbrew.entities.jooq.db.tables.records.AnnouncementsRecord;
import com.katbrew.entities.jooq.db.tables.records.BalanceRecord;
import com.katbrew.entities.jooq.db.tables.records.FetchDataRecord;
import com.katbrew.entities.jooq.db.tables.records.HolderRecord;
import com.katbrew.entities.jooq.db.tables.records.LastUpdateRecord;
import com.katbrew.entities.jooq.db.tables.records.NftBalanceRecord;
import com.katbrew.entities.jooq.db.tables.records.NftCollectionEntryRecord;
import com.katbrew.entities.jooq.db.tables.records.NftCollectionInfoRecord;
import com.katbrew.entities.jooq.db.tables.records.NftCollectionRecord;
import com.katbrew.entities.jooq.db.tables.records.NftTransactionRecord;
import com.katbrew.entities.jooq.db.tables.records.PriceDataRecord;
import com.katbrew.entities.jooq.db.tables.records.TokenRecord;
import com.katbrew.entities.jooq.db.tables.records.TopHolderRecord;
import com.katbrew.entities.jooq.db.tables.records.TransactionRecord;
import com.katbrew.entities.jooq.db.tables.records.UsersRecord;
import com.katbrew.entities.jooq.db.tables.records.WhitelistRecord;

import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AdvertisementsRecord> PK_ADVERTISEMENTS = Internal.createUniqueKey(Advertisements.ADVERTISEMENTS, DSL.name("pk_advertisements"), new TableField[] { Advertisements.ADVERTISEMENTS.ID }, true);
    public static final UniqueKey<AnnouncementsRecord> PK_ANNOUNCEMENTS = Internal.createUniqueKey(Announcements.ANNOUNCEMENTS, DSL.name("pk_announcements"), new TableField[] { Announcements.ANNOUNCEMENTS.ID }, true);
    public static final UniqueKey<BalanceRecord> PK_BALANCE = Internal.createUniqueKey(Balance.BALANCE, DSL.name("pk_balance"), new TableField[] { Balance.BALANCE.ID }, true);
    public static final UniqueKey<FetchDataRecord> FETCH_DATA_IDENTIFIER_KEY = Internal.createUniqueKey(FetchData.FETCH_DATA, DSL.name("fetch_data_identifier_key"), new TableField[] { FetchData.FETCH_DATA.IDENTIFIER }, true);
    public static final UniqueKey<FetchDataRecord> PK_LAST_UPDATE_DATA = Internal.createUniqueKey(FetchData.FETCH_DATA, DSL.name("pk_last_update_data"), new TableField[] { FetchData.FETCH_DATA.ID }, true);
    public static final UniqueKey<HolderRecord> HOLDER_ADDRESS_KEY = Internal.createUniqueKey(Holder.HOLDER, DSL.name("Holder_address_key"), new TableField[] { Holder.HOLDER.ADDRESS }, true);
    public static final UniqueKey<HolderRecord> PK_HOLDER = Internal.createUniqueKey(Holder.HOLDER, DSL.name("pk_holder"), new TableField[] { Holder.HOLDER.ID }, true);
    public static final UniqueKey<LastUpdateRecord> LAST_UPDATE_IDENTIFIER_KEY = Internal.createUniqueKey(LastUpdate.LAST_UPDATE, DSL.name("Last_Update_identifier_key"), new TableField[] { LastUpdate.LAST_UPDATE.IDENTIFIER }, true);
    public static final UniqueKey<LastUpdateRecord> PK_LAST_UPDATE = Internal.createUniqueKey(LastUpdate.LAST_UPDATE, DSL.name("pk_last_update"), new TableField[] { LastUpdate.LAST_UPDATE.ID }, true);
    public static final UniqueKey<NftBalanceRecord> PK_NFT_BALANCE = Internal.createUniqueKey(NftBalance.NFT_BALANCE, DSL.name("pk_nft_balance"), new TableField[] { NftBalance.NFT_BALANCE.ID }, true);
    public static final UniqueKey<NftCollectionRecord> NFT_COLLECTION_TICK_KEY = Internal.createUniqueKey(NftCollection.NFT_COLLECTION, DSL.name("nft_collection_tick_key"), new TableField[] { NftCollection.NFT_COLLECTION.TICK }, true);
    public static final UniqueKey<NftCollectionRecord> NFT_COLLECTION_TX_ID_REV_KEY = Internal.createUniqueKey(NftCollection.NFT_COLLECTION, DSL.name("nft_collection_tx_id_rev_key"), new TableField[] { NftCollection.NFT_COLLECTION.TX_ID_REV }, true);
    public static final UniqueKey<NftCollectionRecord> PK_NFT_COLLECTION = Internal.createUniqueKey(NftCollection.NFT_COLLECTION, DSL.name("pk_nft_collection"), new TableField[] { NftCollection.NFT_COLLECTION.ID }, true);
    public static final UniqueKey<NftCollectionEntryRecord> PK_NFT_COLLECTION_ENTRY = Internal.createUniqueKey(NftCollectionEntry.NFT_COLLECTION_ENTRY, DSL.name("pk_nft_collection_entry"), new TableField[] { NftCollectionEntry.NFT_COLLECTION_ENTRY.ID }, true);
    public static final UniqueKey<NftCollectionInfoRecord> NFT_COLLECTION_INFO_FK_COLLECTION_KEY = Internal.createUniqueKey(NftCollectionInfo.NFT_COLLECTION_INFO, DSL.name("nft_collection_info_fk_collection_key"), new TableField[] { NftCollectionInfo.NFT_COLLECTION_INFO.FK_COLLECTION }, true);
    public static final UniqueKey<NftCollectionInfoRecord> PK_NFT_COLLECTION_INFO = Internal.createUniqueKey(NftCollectionInfo.NFT_COLLECTION_INFO, DSL.name("pk_nft_collection_info"), new TableField[] { NftCollectionInfo.NFT_COLLECTION_INFO.ID }, true);
    public static final UniqueKey<NftTransactionRecord> NFT_TRANSACTION_TX_ID_REV_KEY = Internal.createUniqueKey(NftTransaction.NFT_TRANSACTION, DSL.name("nft_transaction_tx_id_rev_key"), new TableField[] { NftTransaction.NFT_TRANSACTION.TX_ID_REV }, true);
    public static final UniqueKey<NftTransactionRecord> PK_NFT_TRANSACTION = Internal.createUniqueKey(NftTransaction.NFT_TRANSACTION, DSL.name("pk_nft_transaction"), new TableField[] { NftTransaction.NFT_TRANSACTION.ID }, true);
    public static final UniqueKey<PriceDataRecord> PK_PRICE_DATA = Internal.createUniqueKey(PriceData.PRICE_DATA, DSL.name("PK_Price_Data"), new TableField[] { PriceData.PRICE_DATA.ID }, true);
    public static final UniqueKey<TokenRecord> PK_TOKEN = Internal.createUniqueKey(Token.TOKEN, DSL.name("pk_token"), new TableField[] { Token.TOKEN.ID }, true);
    public static final UniqueKey<TokenRecord> TOKEN_TICK_KEY = Internal.createUniqueKey(Token.TOKEN, DSL.name("Token_tick_key"), new TableField[] { Token.TOKEN.TICK }, true);
    public static final UniqueKey<TopHolderRecord> PK_TOP_HOLDER = Internal.createUniqueKey(TopHolder.TOP_HOLDER, DSL.name("pk_top_holder"), new TableField[] { TopHolder.TOP_HOLDER.ID }, true);
    public static final UniqueKey<TransactionRecord> PK_TRANSACTION = Internal.createUniqueKey(Transaction.TRANSACTION, DSL.name("pk_transaction"), new TableField[] { Transaction.TRANSACTION.ID }, true);
    public static final UniqueKey<TransactionRecord> TRANSACTION_HASH_REV_KEY = Internal.createUniqueKey(Transaction.TRANSACTION, DSL.name("Transaction_hash_rev_key"), new TableField[] { Transaction.TRANSACTION.HASH_REV }, true);
    public static final UniqueKey<UsersRecord> PK_USERS = Internal.createUniqueKey(Users.USERS, DSL.name("pk_users"), new TableField[] { Users.USERS.ID }, true);
    public static final UniqueKey<WhitelistRecord> PK_WHITELIST = Internal.createUniqueKey(Whitelist.WHITELIST, DSL.name("pk_whitelist"), new TableField[] { Whitelist.WHITELIST.ID }, true);
    public static final UniqueKey<WhitelistRecord> WHITELIST_ADDRESS_KEY = Internal.createUniqueKey(Whitelist.WHITELIST, DSL.name("Whitelist_address_key"), new TableField[] { Whitelist.WHITELIST.ADDRESS }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<BalanceRecord, HolderRecord> BALANCE__FK_BALANCE_HOLDER = Internal.createForeignKey(Balance.BALANCE, DSL.name("fk_balance_holder"), new TableField[] { Balance.BALANCE.HOLDER_ID }, Keys.PK_HOLDER, new TableField[] { Holder.HOLDER.ID }, true);
    public static final ForeignKey<BalanceRecord, TokenRecord> BALANCE__FK_BALANCE_TOKEN = Internal.createForeignKey(Balance.BALANCE, DSL.name("fk_balance_token"), new TableField[] { Balance.BALANCE.FK_TOKEN }, Keys.PK_TOKEN, new TableField[] { Token.TOKEN.ID }, true);
    public static final ForeignKey<NftBalanceRecord, NftCollectionEntryRecord> NFT_BALANCE__FK_NFT_BALANCE_ENTRY = Internal.createForeignKey(NftBalance.NFT_BALANCE, DSL.name("fk_nft_balance_entry"), new TableField[] { NftBalance.NFT_BALANCE.FK_NFT_ENTRY }, Keys.PK_NFT_COLLECTION_ENTRY, new TableField[] { NftCollectionEntry.NFT_COLLECTION_ENTRY.ID }, true);
    public static final ForeignKey<NftBalanceRecord, HolderRecord> NFT_BALANCE__FK_NFT_BALANCE_HOLDER = Internal.createForeignKey(NftBalance.NFT_BALANCE, DSL.name("fk_nft_balance_holder"), new TableField[] { NftBalance.NFT_BALANCE.HOLDER_ID }, Keys.PK_HOLDER, new TableField[] { Holder.HOLDER.ID }, true);
    public static final ForeignKey<NftCollectionEntryRecord, NftCollectionRecord> NFT_COLLECTION_ENTRY__FK_COLLECTION_ENTRY_COLLECTION = Internal.createForeignKey(NftCollectionEntry.NFT_COLLECTION_ENTRY, DSL.name("fk_collection_entry_collection"), new TableField[] { NftCollectionEntry.NFT_COLLECTION_ENTRY.FK_COLLECTION }, Keys.PK_NFT_COLLECTION, new TableField[] { NftCollection.NFT_COLLECTION.ID }, true);
    public static final ForeignKey<NftCollectionInfoRecord, NftCollectionRecord> NFT_COLLECTION_INFO__FK_COLLECTION_INFO_COLLECTION = Internal.createForeignKey(NftCollectionInfo.NFT_COLLECTION_INFO, DSL.name("fk_collection_info_collection"), new TableField[] { NftCollectionInfo.NFT_COLLECTION_INFO.FK_COLLECTION }, Keys.PK_NFT_COLLECTION, new TableField[] { NftCollection.NFT_COLLECTION.ID }, true);
    public static final ForeignKey<NftTransactionRecord, HolderRecord> NFT_TRANSACTION__FK_NFT_TRANSACTION_HOLDER_DEPLOYER = Internal.createForeignKey(NftTransaction.NFT_TRANSACTION, DSL.name("fk_nft_transaction_holder_deployer"), new TableField[] { NftTransaction.NFT_TRANSACTION.DEPLOYER }, Keys.PK_HOLDER, new TableField[] { Holder.HOLDER.ID }, true);
    public static final ForeignKey<NftTransactionRecord, HolderRecord> NFT_TRANSACTION__FK_NFT_TRANSACTION_HOLDER_TO = Internal.createForeignKey(NftTransaction.NFT_TRANSACTION, DSL.name("fk_nft_transaction_holder_to"), new TableField[] { NftTransaction.NFT_TRANSACTION.TO_ADDRESS }, Keys.PK_HOLDER, new TableField[] { Holder.HOLDER.ID }, true);
    public static final ForeignKey<NftTransactionRecord, NftCollectionRecord> NFT_TRANSACTION__FK_NFT_TRANSACTION_NFT_COLLECTION = Internal.createForeignKey(NftTransaction.NFT_TRANSACTION, DSL.name("fk_nft_transaction_nft_collection"), new TableField[] { NftTransaction.NFT_TRANSACTION.FK_NFT_COLLECTION }, Keys.PK_NFT_COLLECTION, new TableField[] { NftCollection.NFT_COLLECTION.ID }, true);
    public static final ForeignKey<PriceDataRecord, TokenRecord> PRICE_DATA__FK_PRICE_DATA_TOKEN = Internal.createForeignKey(PriceData.PRICE_DATA, DSL.name("fk_price_data_token"), new TableField[] { PriceData.PRICE_DATA.FK_TOKEN }, Keys.PK_TOKEN, new TableField[] { Token.TOKEN.ID }, true);
    public static final ForeignKey<TransactionRecord, HolderRecord> TRANSACTION__FK_TRANSACTION_HOLDER_FROM = Internal.createForeignKey(Transaction.TRANSACTION, DSL.name("fk_transaction_holder_from"), new TableField[] { Transaction.TRANSACTION.FROM_ADDRESS }, Keys.PK_HOLDER, new TableField[] { Holder.HOLDER.ID }, true);
    public static final ForeignKey<TransactionRecord, HolderRecord> TRANSACTION__FK_TRANSACTION_HOLDER_TO = Internal.createForeignKey(Transaction.TRANSACTION, DSL.name("fk_transaction_holder_to"), new TableField[] { Transaction.TRANSACTION.TO_ADDRESS }, Keys.PK_HOLDER, new TableField[] { Holder.HOLDER.ID }, true);
    public static final ForeignKey<TransactionRecord, TokenRecord> TRANSACTION__FK_TRANSACTION_TOKEN = Internal.createForeignKey(Transaction.TRANSACTION, DSL.name("fk_transaction_token"), new TableField[] { Transaction.TRANSACTION.FK_TOKEN }, Keys.PK_TOKEN, new TableField[] { Token.TOKEN.ID }, true);
}
