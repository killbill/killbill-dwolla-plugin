/*! SET storage_engine=INNODB */;

drop table if exists dwolla_responses;
create table dwolla_responses (
    record_id int(11) unsigned not null auto_increment
  , kb_account_id char(36) not null
  , kb_payment_id char(36) not null
  , kb_payment_transaction_id char(36) not null
  , transaction_type varchar(32) not null
  , amount numeric(15,9)
  , currency char(3)
  , transfer_id varchar(256)
  , transfer_status varchar(32)
  , error_code varchar(64)
  , additional_data longtext default null
  , created_date datetime not null
  , kb_tenant_id char(36) not null
  , primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create index dwolla_responses_kb_payment_id on dwolla_responses(kb_payment_id);
create index dwolla_responses_kb_payment_transaction_id on dwolla_responses(kb_payment_transaction_id);

drop table if exists dwolla_payment_methods;
create table dwolla_payment_methods (
    record_id int(11) unsigned not null auto_increment
  , kb_account_id char(36) not null
  , kb_payment_method_id char(36) not null
  , funding_source varchar(64) default null
  , customer_id varchar(64) default null
  , account_id varchar(64) default null
  , is_default boolean not null default false
  , is_deleted boolean not null default false
  , additional_data longtext default null
  , created_date datetime not null
  , updated_date datetime not null
  , kb_tenant_id char(36) not null
  , primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create unique index dwolla_payment_methods_kb_payment_id on dwolla_payment_methods(kb_payment_method_id);

drop table if exists dwolla_tokens;
create table dwolla_tokens (
    record_id int(11) unsigned not null auto_increment
  , access_token char(64) not null
  , refresh_token char(64) not null
  , account_id char(64) not null
  , created_date datetime not null
  , updated_date datetime default null
  , kb_tenant_id char(36) not null
  , primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create unique index dwolla_tokens_account_tenant_id on dwolla_tokens(account_id, kb_tenant_id);

drop table if exists dwolla_notifications;
create table dwolla_notifications (
    record_id int(11) unsigned not null auto_increment
  , self char(128) not null
  , id char(64) not null
  , topic char(32) not null
  , account_id char(64) default null
  , event_id char(64) default null
  , subscription_id char(64) default null
  , additional_data longtext default null
  , created_date datetime not null
  , kb_tenant_id char(36) not null
  , primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create unique index dwolla_notifications_self on dwolla_notifications(self);