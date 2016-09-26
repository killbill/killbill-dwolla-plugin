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
public class DwollaPaymentMethods extends org.jooq.impl.TableImpl<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord> {

	private static final long serialVersionUID = -996041958;

	/**
	 * The reference instance of <code>killbill.dwolla_payment_methods</code>
	 */
	public static final org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods DWOLLA_PAYMENT_METHODS = new org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord> getRecordType() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord.class;
	}

	/**
	 * The column <code>killbill.dwolla_payment_methods.record_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, org.jooq.types.UInteger> RECORD_ID = createField("record_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_payment_methods.kb_account_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, java.lang.String> KB_ACCOUNT_ID = createField("kb_account_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_payment_methods.kb_payment_method_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, java.lang.String> KB_PAYMENT_METHOD_ID = createField("kb_payment_method_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_payment_methods.funding_source</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, java.lang.String> FUNDING_SOURCE = createField("funding_source", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.dwolla_payment_methods.customer_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, java.lang.String> CUSTOMER_ID = createField("customer_id", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.dwolla_payment_methods.is_default</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, java.lang.Byte> IS_DEFAULT = createField("is_default", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>killbill.dwolla_payment_methods.is_deleted</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, java.lang.Byte> IS_DELETED = createField("is_deleted", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>killbill.dwolla_payment_methods.additional_data</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, java.lang.String> ADDITIONAL_DATA = createField("additional_data", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>killbill.dwolla_payment_methods.created_date</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, java.sql.Timestamp> CREATED_DATE = createField("created_date", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_payment_methods.updated_date</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, java.sql.Timestamp> UPDATED_DATE = createField("updated_date", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_payment_methods.kb_tenant_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, java.lang.String> KB_TENANT_ID = createField("kb_tenant_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * Create a <code>killbill.dwolla_payment_methods</code> table reference
	 */
	public DwollaPaymentMethods() {
		this("dwolla_payment_methods", null);
	}

	/**
	 * Create an aliased <code>killbill.dwolla_payment_methods</code> table reference
	 */
	public DwollaPaymentMethods(java.lang.String alias) {
		this(alias, org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods.DWOLLA_PAYMENT_METHODS);
	}

	private DwollaPaymentMethods(java.lang.String alias, org.jooq.Table<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord> aliased) {
		this(alias, aliased, null);
	}

	private DwollaPaymentMethods(java.lang.String alias, org.jooq.Table<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, org.killbill.billing.plugin.dwolla.dao.gen.Killbill.KILLBILL, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord, org.jooq.types.UInteger> getIdentity() {
		return org.killbill.billing.plugin.dwolla.dao.gen.Keys.IDENTITY_DWOLLA_PAYMENT_METHODS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord> getPrimaryKey() {
		return org.killbill.billing.plugin.dwolla.dao.gen.Keys.KEY_DWOLLA_PAYMENT_METHODS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaPaymentMethodsRecord>>asList(org.killbill.billing.plugin.dwolla.dao.gen.Keys.KEY_DWOLLA_PAYMENT_METHODS_PRIMARY, org.killbill.billing.plugin.dwolla.dao.gen.Keys.KEY_DWOLLA_PAYMENT_METHODS_DWOLLA_PAYMENT_METHODS_KB_PAYMENT_ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods as(java.lang.String alias) {
		return new org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods(alias, this);
	}

	/**
	 * Rename this table
	 */
	public org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods rename(java.lang.String name) {
		return new org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaPaymentMethods(name, null);
	}
}