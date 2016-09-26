/**
 * This class is generated by jOOQ
 */
package org.killbill.billing.plugin.dwolla.dao.gen.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.5.0"
	},
	comments = "This class is generated by jOOQ"
)
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DwollaResponses extends org.jooq.impl.TableImpl<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord> {

	private static final long serialVersionUID = -183608111;

	/**
	 * The reference instance of <code>killbill.dwolla_responses</code>
	 */
	public static final org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses DWOLLA_RESPONSES = new org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord> getRecordType() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord.class;
	}

	/**
	 * The column <code>killbill.dwolla_responses.record_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, org.jooq.types.UInteger> RECORD_ID = createField("record_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.kb_account_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.lang.String> KB_ACCOUNT_ID = createField("kb_account_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.kb_payment_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.lang.String> KB_PAYMENT_ID = createField("kb_payment_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.kb_payment_transaction_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.lang.String> KB_PAYMENT_TRANSACTION_ID = createField("kb_payment_transaction_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.transaction_type</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.lang.String> TRANSACTION_TYPE = createField("transaction_type", org.jooq.impl.SQLDataType.VARCHAR.length(32).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.amount</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.math.BigDecimal> AMOUNT = createField("amount", org.jooq.impl.SQLDataType.DECIMAL.precision(15, 9), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.currency</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.lang.String> CURRENCY = createField("currency", org.jooq.impl.SQLDataType.CHAR.length(3), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.transfer_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.lang.String> TRANSFER_ID = createField("transfer_id", org.jooq.impl.SQLDataType.VARCHAR.length(256), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.transfer_status</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.lang.String> TRANSFER_STATUS = createField("transfer_status", org.jooq.impl.SQLDataType.VARCHAR.length(32), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.error_code</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.lang.String> ERROR_CODE = createField("error_code", org.jooq.impl.SQLDataType.VARCHAR.length(64), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.additional_data</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.lang.String> ADDITIONAL_DATA = createField("additional_data", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>killbill.dwolla_responses.created_date</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.sql.Timestamp> CREATED_DATE = createField("created_date", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_responses.kb_tenant_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, java.lang.String> KB_TENANT_ID = createField("kb_tenant_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * Create a <code>killbill.dwolla_responses</code> table reference
	 */
	public DwollaResponses() {
		this("dwolla_responses", null);
	}

	/**
	 * Create an aliased <code>killbill.dwolla_responses</code> table reference
	 */
	public DwollaResponses(java.lang.String alias) {
		this(alias, org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES);
	}

	private DwollaResponses(java.lang.String alias, org.jooq.Table<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord> aliased) {
		this(alias, aliased, null);
	}

	private DwollaResponses(java.lang.String alias, org.jooq.Table<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, org.killbill.billing.plugin.dwolla.dao.gen.Killbill.KILLBILL, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord, org.jooq.types.UInteger> getIdentity() {
		return org.killbill.billing.plugin.dwolla.dao.gen.Keys.IDENTITY_DWOLLA_RESPONSES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord> getPrimaryKey() {
		return org.killbill.billing.plugin.dwolla.dao.gen.Keys.KEY_DWOLLA_RESPONSES_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord>>asList(org.killbill.billing.plugin.dwolla.dao.gen.Keys.KEY_DWOLLA_RESPONSES_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses as(java.lang.String alias) {
		return new org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses(alias, this);
	}

	/**
	 * Rename this table
	 */
	public org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses rename(java.lang.String name) {
		return new org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses(name, null);
	}
}
