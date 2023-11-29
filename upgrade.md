# 升级文档

## v3.1.0 升级至 v3.1.2

* 执行以下升级SQL（MySQL为例）
```sql
-- since v3.1.0
ALTER TABLE dbt_dictionary ADD COLUMN `app_module`  varchar(50) null comment '应用模块' AFTER tenant_id;
ALTER TABLE dbtlc_model_relation ADD COLUMN bridge_model_storage_key varchar(100) AFTER bridge_model_key;
ALTER TABLE dbtlc_model_relation CHANGE ext_condition extension varchar(200) NULL;
```