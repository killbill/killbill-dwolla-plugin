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
public class DwollaNotifications extends org.jooq.impl.TableImpl<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord> {

	private static final long serialVersionUID = 531450938;

	/**
	 * The reference instance of <code>killbill.dwolla_notifications</code>
	 */
	public static final org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaNotifications DWOLLA_NOTIFICATIONS = new org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaNotifications();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord> getRecordType() {
		return org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord.class;
	}

	/**
	 * The column <code>killbill.dwolla_notifications.record_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, org.jooq.types.UInteger> RECORD_ID = createField("record_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_notifications.self</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, java.lang.String> SELF = createField("self", org.jooq.impl.SQLDataType.CHAR.length(128).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_notifications.id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, java.lang.String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(64).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_notifications.topic</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, java.lang.String> TOPIC = createField("topic", org.jooq.impl.SQLDataType.CHAR.length(32).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_notifications.account_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, java.lang.String> ACCOUNT_ID = createField("account_id", org.jooq.impl.SQLDataType.CHAR.length(64).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_notifications.event_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, java.lang.String> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.CHAR.length(64).nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_notifications.subscription_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, java.lang.String> SUBSCRIPTION_ID = createField("subscription_id", org.jooq.impl.SQLDataType.CHAR.length(64), this, "");

	/**
	 * The column <code>killbill.dwolla_notifications.additional_data</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, java.lang.String> ADDITIONAL_DATA = createField("additional_data", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>killbill.dwolla_notifications.created_date</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, java.sql.Timestamp> CREATED_DATE = createField("created_date", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>killbill.dwolla_notifications.kb_tenant_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, java.lang.String> KB_TENANT_ID = createField("kb_tenant_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * Create a <code>killbill.dwolla_notifications</code> table reference
	 */
	public DwollaNotifications() {
		this("dwolla_notifications", null);
	}

	/**
	 * Create an aliased <code>killbill.dwolla_notifications</code> table reference
	 */
	public DwollaNotifications(java.lang.String alias) {
		this(alias, org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaNotifications.DWOLLA_NOTIFICATIONS);
	}

	private DwollaNotifications(java.lang.String alias, org.jooq.Table<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord> aliased) {
		this(alias, aliased, null);
	}

	private DwollaNotifications(java.lang.String alias, org.jooq.Table<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, org.killbill.billing.plugin.dwolla.dao.gen.Killbill.KILLBILL, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord, org.jooq.types.UInteger> getIdentity() {
		return org.killbill.billing.plugin.dwolla.dao.gen.Keys.IDENTITY_DWOLLA_NOTIFICATIONS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord> getPrimaryKey() {
		return org.killbill.billing.plugin.dwolla.dao.gen.Keys.KEY_DWOLLA_NOTIFICATIONS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.killbill.billing.plugin.dwolla.dao.gen.tables.records.DwollaNotificationsRecord>>asList(org.killbill.billing.plugin.dwolla.dao.gen.Keys.KEY_DWOLLA_NOTIFICATIONS_PRIMARY, org.killbill.billing.plugin.dwolla.dao.gen.Keys.KEY_DWOLLA_NOTIFICATIONS_DWOLLA_NOTIFICATIONS_SELF);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaNotifications as(java.lang.String alias) {
		return new org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaNotifications(alias, this);
	}

	/**
	 * Rename this table
	 */
	public org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaNotifications rename(java.lang.String name) {
		return new org.killbill.billing.plugin.dwolla.dao.gen.tables.DwollaNotifications(name, null);
	}
}
