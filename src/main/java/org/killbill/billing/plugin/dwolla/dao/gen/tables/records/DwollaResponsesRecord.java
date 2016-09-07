/**
 * This class is generated by jOOQ
 */
package org.killbill.billing.plugin.dwolla.dao.gen.tables.records;

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
public class DwollaResponsesRecord extends org.jooq.impl.UpdatableRecordImpl<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaResponsesRecord> implements org.jooq.Record11<org.jooq.types.UInteger, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal, java.lang.String, java.lang.String, java.sql.Timestamp, java.lang.String> {

	private static final long serialVersionUID = -1631819991;

	/**
	 * Setter for <code>killbill.dwolla_responses.record_id</code>.
	 */
	public void setRecordId(org.jooq.types.UInteger value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.record_id</code>.
	 */
	public org.jooq.types.UInteger getRecordId() {
		return (org.jooq.types.UInteger) getValue(0);
	}

	/**
	 * Setter for <code>killbill.dwolla_responses.kb_account_id</code>.
	 */
	public void setKbAccountId(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.kb_account_id</code>.
	 */
	public java.lang.String getKbAccountId() {
		return (java.lang.String) getValue(1);
	}

	/**
	 * Setter for <code>killbill.dwolla_responses.kb_payment_id</code>.
	 */
	public void setKbPaymentId(java.lang.String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.kb_payment_id</code>.
	 */
	public java.lang.String getKbPaymentId() {
		return (java.lang.String) getValue(2);
	}

	/**
	 * Setter for <code>killbill.dwolla_responses.kb_payment_method_id</code>.
	 */
	public void setKbPaymentMethodId(java.lang.String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.kb_payment_method_id</code>.
	 */
	public java.lang.String getKbPaymentMethodId() {
		return (java.lang.String) getValue(3);
	}

	/**
	 * Setter for <code>killbill.dwolla_responses.kb_payment_transaction_id</code>.
	 */
	public void setKbPaymentTransactionId(java.lang.String value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.kb_payment_transaction_id</code>.
	 */
	public java.lang.String getKbPaymentTransactionId() {
		return (java.lang.String) getValue(4);
	}

	/**
	 * Setter for <code>killbill.dwolla_responses.transaction_type</code>.
	 */
	public void setTransactionType(java.lang.String value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.transaction_type</code>.
	 */
	public java.lang.String getTransactionType() {
		return (java.lang.String) getValue(5);
	}

	/**
	 * Setter for <code>killbill.dwolla_responses.amount</code>.
	 */
	public void setAmount(java.math.BigDecimal value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.amount</code>.
	 */
	public java.math.BigDecimal getAmount() {
		return (java.math.BigDecimal) getValue(6);
	}

	/**
	 * Setter for <code>killbill.dwolla_responses.currency</code>.
	 */
	public void setCurrency(java.lang.String value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.currency</code>.
	 */
	public java.lang.String getCurrency() {
		return (java.lang.String) getValue(7);
	}

	/**
	 * Setter for <code>killbill.dwolla_responses.additional_data</code>.
	 */
	public void setAdditionalData(java.lang.String value) {
		setValue(8, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.additional_data</code>.
	 */
	public java.lang.String getAdditionalData() {
		return (java.lang.String) getValue(8);
	}

	/**
	 * Setter for <code>killbill.dwolla_responses.created_date</code>.
	 */
	public void setCreatedDate(java.sql.Timestamp value) {
		setValue(9, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.created_date</code>.
	 */
	public java.sql.Timestamp getCreatedDate() {
		return (java.sql.Timestamp) getValue(9);
	}

	/**
	 * Setter for <code>killbill.dwolla_responses.kb_tenant_id</code>.
	 */
	public void setKbTenantId(java.lang.String value) {
		setValue(10, value);
	}

	/**
	 * Getter for <code>killbill.dwolla_responses.kb_tenant_id</code>.
	 */
	public java.lang.String getKbTenantId() {
		return (java.lang.String) getValue(10);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<org.jooq.types.UInteger> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record11 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row11<org.jooq.types.UInteger, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal, java.lang.String, java.lang.String, java.sql.Timestamp, java.lang.String> fieldsRow() {
		return (org.jooq.Row11) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row11<org.jooq.types.UInteger, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal, java.lang.String, java.lang.String, java.sql.Timestamp, java.lang.String> valuesRow() {
		return (org.jooq.Row11) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<org.jooq.types.UInteger> field1() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.RECORD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.KB_ACCOUNT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field3() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.KB_PAYMENT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field4() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.KB_PAYMENT_METHOD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field5() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.KB_PAYMENT_TRANSACTION_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field6() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.TRANSACTION_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.math.BigDecimal> field7() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.AMOUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field8() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.CURRENCY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field9() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.ADDITIONAL_DATA;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field10() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.CREATED_DATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field11() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES.KB_TENANT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.types.UInteger value1() {
		return getRecordId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value2() {
		return getKbAccountId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value3() {
		return getKbPaymentId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value4() {
		return getKbPaymentMethodId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value5() {
		return getKbPaymentTransactionId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value6() {
		return getTransactionType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.math.BigDecimal value7() {
		return getAmount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value8() {
		return getCurrency();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value9() {
		return getAdditionalData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value10() {
		return getCreatedDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value11() {
		return getKbTenantId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value1(org.jooq.types.UInteger value) {
		setRecordId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value2(java.lang.String value) {
		setKbAccountId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value3(java.lang.String value) {
		setKbPaymentId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value4(java.lang.String value) {
		setKbPaymentMethodId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value5(java.lang.String value) {
		setKbPaymentTransactionId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value6(java.lang.String value) {
		setTransactionType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value7(java.math.BigDecimal value) {
		setAmount(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value8(java.lang.String value) {
		setCurrency(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value9(java.lang.String value) {
		setAdditionalData(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value10(java.sql.Timestamp value) {
		setCreatedDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord value11(java.lang.String value) {
		setKbTenantId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DwollaResponsesRecord values(org.jooq.types.UInteger value1, java.lang.String value2, java.lang.String value3, java.lang.String value4, java.lang.String value5, java.lang.String value6, java.math.BigDecimal value7, java.lang.String value8, java.lang.String value9, java.sql.Timestamp value10, java.lang.String value11) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached DwollaResponsesRecord
	 */
	public DwollaResponsesRecord() {
		super(org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES);
	}

	/**
	 * Create a detached, initialised DwollaResponsesRecord
	 */
	public DwollaResponsesRecord(org.jooq.types.UInteger recordId, java.lang.String kbAccountId, java.lang.String kbPaymentId, java.lang.String kbPaymentMethodId, java.lang.String kbPaymentTransactionId, java.lang.String transactionType, java.math.BigDecimal amount, java.lang.String currency, java.lang.String additionalData, java.sql.Timestamp createdDate, java.lang.String kbTenantId) {
		super(org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaResponses.DWOLLA_RESPONSES);

		setValue(0, recordId);
		setValue(1, kbAccountId);
		setValue(2, kbPaymentId);
		setValue(3, kbPaymentMethodId);
		setValue(4, kbPaymentTransactionId);
		setValue(5, transactionType);
		setValue(6, amount);
		setValue(7, currency);
		setValue(8, additionalData);
		setValue(9, createdDate);
		setValue(10, kbTenantId);
	}
}
